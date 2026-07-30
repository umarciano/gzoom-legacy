-- =============================================================================
-- SETUP_PERF_ORGANIZZATIVA.sql
-- Script unico — Configurazione Performance Organizzativa CTX_BS (Cardarelli)
--
-- Script CANONICO unico: consolida (e SOSTITUISCE, tutti RIMOSSI) i vecchi script separati
--   V001  setup_4fasce_scoring.sql      → scoring 4 fasce (PERF_4FASCE, soglie)
--   V002  setup_workflow_stati.sql      → workflow 8 stati WEORCARD_*
--   V003  CONFIG_IMPORT_SCHEDE_BS.sql   → datasource import (schede/obiettivi/misure/ruoli/catalogo)
-- + nature/aree, referente-UOC, Fonte, seed party_role (V005/V006), formato card.
--
-- Tutti i blocchi sono idempotenti (ON CONFLICT DO NOTHING / DO UPDATE / DELETE preventivi).
-- Esecuzione:
--   psql -h <host> -U postgres -d cardarelli -f SETUP_PERF_ORGANIZZATIVA.sql
-- Post-import (dopo caricamento dati UI): POST_IMPORT_FASCE_INDICATORI.sql
-- =============================================================================


-- =============================================================================
-- V001 — SCORING 4 FASCE
-- =============================================================================
-- Crea tipi soglia (SOGLIA_50, SOGLIA_100), scala PERF_4FASCE, 4 bande di punteggio
-- e parametri BeanShell su CTX_BS (e CTX_OR) per il converter WECONVER_4PERCLIMITS.
-- =============================================================================

BEGIN;

INSERT INTO gl_fiscal_type (
    gl_fiscal_type_id, description, gl_fiscal_type_enum_id,
    is_financial_used, is_account_used, is_indicator_used,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('SOGLIA_50',  'Soglia 50% (banda inferiore)',   'GLFISCTYPE_TARGET', 'N','N','Y', NOW(),NOW(),NOW(),NOW()),
    ('SOGLIA_100', 'Soglia 100% (obiettivo pieno)',  'GLFISCTYPE_TARGET', 'N','N','Y', NOW(),NOW(),NOW(),NOW())
ON CONFLICT (gl_fiscal_type_id) DO NOTHING;

INSERT INTO uom_range (uom_range_id, uom_id, description, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
VALUES ('PERF_4FASCE', 'OTH_SCO', 'Performance 4 Fasce (0/50/75/100%)', NOW(),NOW(),NOW(),NOW())
ON CONFLICT (uom_range_id) DO NOTHING;

INSERT INTO uom_range_values (
    uom_range_values_id, uom_range_id, from_value, thru_value,
    range_values_factor, range_values_factor_min, is_positive, color_enum_id, alert, prorate_range,
    comments, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('P4F_000','PERF_4FASCE', -1.0,   -1.0,     0.0,  0.0, 'Y','RED',   'N','N', 'Sotto soglia minima (SOGLIA_50) - punteggio 0%',      NOW(),NOW(),NOW(),NOW()),
    ('P4F_050','PERF_4FASCE',  0.0,   99.99,   50.0, 50.0, 'Y','RED',   'N','N', 'Banda 50%: actual tra SOGLIA_50 e BUDGET',            NOW(),NOW(),NOW(),NOW()),
    ('P4F_075','PERF_4FASCE',100.0,  199.99,   75.0, 75.0, 'Y','YELLOW','N','N', 'Banda 75%: actual tra BUDGET e SOGLIA_100',           NOW(),NOW(),NOW(),NOW()),
    ('P4F_100','PERF_4FASCE',200.0,  301.0,   100.0,100.0, 'Y','GREEN', 'N','N', 'Obiettivo raggiunto (>= SOGLIA_100) - punteggio 100%',NOW(),NOW(),NOW(),NOW())
ON CONFLICT (uom_range_values_id) DO NOTHING;

INSERT INTO work_effort_type_content (
    work_effort_type_id, content_id, we_type_content_type_id, params,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('CTX_OR','WEFLD_ELAB','WEFLD_ELAB',
     'glFiscalTypeIdExcellentLimit = "SOGLIA_100"; glFiscalTypeIdUpperLimit = "SOGLIA_100"; glFiscalTypeIdLowerLimit = "SOGLIA_50";',
     NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEFLD_ELAB','WEFLD_ELAB',
     'glFiscalTypeIdExcellentLimit = "SOGLIA_100"; glFiscalTypeIdUpperLimit = "SOGLIA_100"; glFiscalTypeIdLowerLimit = "SOGLIA_50";',
     NOW(),NOW(),NOW(),NOW())
ON CONFLICT (work_effort_type_id, content_id) DO UPDATE
    SET params = EXCLUDED.params, last_updated_stamp = NOW(), last_updated_tx_stamp = NOW();

COMMIT;

-- =============================================================================
-- V001b — PERIODO FISCALE 2026 (prerequisito scoring)
-- =============================================================================
-- Il reader di scoring (KpiReader) lavora per custom_time_period: senza il periodo
-- FISCAL_YEAR dell'anno, nessun KPI di quell'anno viene valutato (soglie e consuntivi).
-- In produzione esiste solo fino a PER_2025 → si crea il gemello 2026 (stesse date +1 anno).
-- Le transazioni soglia (transaction_date 2026-12-31) cadono in questo periodo.
-- =============================================================================

BEGIN;

INSERT INTO custom_time_period (custom_time_period_id, period_type_id, period_num, period_name,
    from_date, thru_date, is_closed, custom_time_period_code,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp, created_by_user_login, last_modified_by_user_login)
VALUES ('PER_2026','FISCAL_YEAR',1,'Anno 2026','2025-12-31 23:00:00+00','2026-12-30 23:00:00+00','N','ANNO2026',
    NOW(),NOW(),NOW(),NOW(),'admin','admin')
ON CONFLICT (custom_time_period_id) DO NOTHING;

COMMIT;


-- =============================================================================
-- V002 — WORKFLOW 8 STATI WEORCARD_* PER CTX_BS
-- =============================================================================
-- Crea StatusType WE_STATUS_OR_CARD, 8 StatusItem (INIT→CLOSED), transizioni,
-- collegamento CTX_BS e regole di editabilità folder per stato.
-- Migra le schede CTX_BS esistenti da WEPERFST_EXECPEND a WEORCARD_TOVALIDATE.
-- =============================================================================

BEGIN;

INSERT INTO status_type (
    status_type_id, parent_type_id, has_table, description,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES ('WE_STATUS_OR_CARD','PERFORMANCE_STATUS','N','Scheda Performance Organizzativa Cardarelli', NOW(),NOW(),NOW(),NOW())
ON CONFLICT (status_type_id) DO NOTHING;

INSERT INTO status_item (
    status_id, status_type_id, status_code, sequence_id, description, act_st_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('WEORCARD_INIT',       'WE_STATUS_OR_CARD','INIT',       '01','Inizializzata',        'ACTSTATUS_PENDING', NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_TOVALIDATE', 'WE_STATUS_OR_CARD','TOVALIDATE', '02','Da validare',           'ACTSTATUS_PENDING', NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_VALPART',    'WE_STATUS_OR_CARD','VALPART',    '03','Validata parzialmente', 'ACTSTATUS_PENDING', NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_VALIDATED',  'WE_STATUS_OR_CARD','VALIDATED',  '04','Validata',              'ACTSTATUS_ACTIVE',  NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_TOACCOUNT',  'WE_STATUS_OR_CARD','TOACCOUNT',  '05','Da consuntivare',       'ACTSTATUS_ACTIVE',  NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_ACCOUNTED',  'WE_STATUS_OR_CARD','ACCOUNTED',  '06','Consuntivata',          'ACTSTATUS_ACTIVE',  NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_REVIEWED',   'WE_STATUS_OR_CARD','REVIEWED',   '07','Visionata',             'ACTSTATUS_ACTIVE',  NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_CLOSED',     'WE_STATUS_OR_CARD','CLOSED',     '08','Chiusa',                'ACTSTATUS_CLOSED',  NOW(),NOW(),NOW(),NOW())
ON CONFLICT (status_id) DO NOTHING;

INSERT INTO status_valid_change (
    status_id, status_id_to, transition_name,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('WEORCARD_INIT',       'WEORCARD_TOVALIDATE','Proponi per validazione', NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_TOVALIDATE', 'WEORCARD_VALPART',   'Valida parzialmente',    NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_TOVALIDATE', 'WEORCARD_VALIDATED', 'Valida',                 NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_VALPART',    'WEORCARD_VALIDATED', 'Valida',                 NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_VALIDATED',  'WEORCARD_TOACCOUNT', 'Apri consuntivazione',   NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_TOACCOUNT',  'WEORCARD_ACCOUNTED', 'Consuntiva',             NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_ACCOUNTED',  'WEORCARD_REVIEWED',  'Prendi visione',         NOW(),NOW(),NOW(),NOW()),
    ('WEORCARD_REVIEWED',   'WEORCARD_CLOSED',    'Chiudi',                 NOW(),NOW(),NOW(),NOW())
ON CONFLICT (status_id, status_id_to) DO NOTHING;

INSERT INTO work_effort_type_status (
    work_effort_type_root_id, current_status_id,
    gl_fiscal_type_id, next_status_id, ctrl_score_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('CTX_BS','WEORCARD_INIT',       'ACTUAL','WEORCARD_TOVALIDATE','CTRL_SCORE_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_TOVALIDATE', 'ACTUAL','WEORCARD_VALIDATED', 'CTRL_SCORE_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_VALPART',    'ACTUAL','WEORCARD_VALIDATED', 'CTRL_SCORE_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_VALIDATED',  'ACTUAL','WEORCARD_TOACCOUNT', 'CTRL_SCORE_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_TOACCOUNT',  'ACTUAL','WEORCARD_ACCOUNTED', 'CTRL_SCORE_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_ACCOUNTED',  'ACTUAL','WEORCARD_REVIEWED',  'CTRL_SCORE_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_REVIEWED',   'ACTUAL','WEORCARD_CLOSED',    'CTRL_SCORE_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_CLOSED',     'ACTUAL', NULL,                'CTRL_SCORE_NONE', NOW(),NOW(),NOW(),NOW())
ON CONFLICT (current_status_id, work_effort_type_root_id) DO NOTHING;

-- Editabilità folder: INIT/TOVALIDATE/VALPART → tutto ONLY_OPEN
INSERT INTO work_effort_type_status_cnt (
    work_effort_type_id, status_id, content_id, to_post, ctrl_amount_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT 'CTX_BS', s.status_id, f.content_id, 'Y', 'ONLY_OPEN', NOW(),NOW(),NOW(),NOW()
FROM (VALUES ('WEORCARD_INIT'),('WEORCARD_TOVALIDATE'),('WEORCARD_VALPART')) AS s(status_id)
CROSS JOIN (VALUES ('WEFLD_MAIN'),('WEFLD_ORGUNIT'),('WEFLD_WROLE'),('WEFLD_WEFROM'),('WEFLD_NOTE'),('WEFLD_REVIEW'),('WEFLD_ELAB'),('WEFLD_AIND')) AS f(content_id)
ON CONFLICT (work_effort_type_id, status_id, content_id) DO NOTHING;

-- VALIDATED → tutto AMOUNT_NONE
INSERT INTO work_effort_type_status_cnt (
    work_effort_type_id, status_id, content_id, to_post, ctrl_amount_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT 'CTX_BS', 'WEORCARD_VALIDATED', f.content_id, 'Y', 'AMOUNT_NONE', NOW(),NOW(),NOW(),NOW()
FROM (VALUES ('WEFLD_MAIN'),('WEFLD_ORGUNIT'),('WEFLD_WROLE'),('WEFLD_WEFROM'),('WEFLD_NOTE'),('WEFLD_REVIEW'),('WEFLD_ELAB'),('WEFLD_AIND')) AS f(content_id)
ON CONFLICT (work_effort_type_id, status_id, content_id) DO NOTHING;

-- TOACCOUNT → ELAB e AIND editabili, resto read-only
INSERT INTO work_effort_type_status_cnt (
    work_effort_type_id, status_id, content_id, to_post, ctrl_amount_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('CTX_BS','WEORCARD_TOACCOUNT','WEFLD_MAIN',   'Y','AMOUNT_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_TOACCOUNT','WEFLD_ORGUNIT','Y','AMOUNT_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_TOACCOUNT','WEFLD_WROLE',  'Y','AMOUNT_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_TOACCOUNT','WEFLD_WEFROM', 'Y','AMOUNT_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_TOACCOUNT','WEFLD_NOTE',   'Y','AMOUNT_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_TOACCOUNT','WEFLD_REVIEW', 'Y','AMOUNT_NONE', NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_TOACCOUNT','WEFLD_ELAB',   'Y','ONLY_OPEN',   NOW(),NOW(),NOW(),NOW()),
    ('CTX_BS','WEORCARD_TOACCOUNT','WEFLD_AIND',   'Y','ONLY_OPEN',   NOW(),NOW(),NOW(),NOW())
ON CONFLICT (work_effort_type_id, status_id, content_id) DO NOTHING;

-- ACCOUNTED/REVIEWED/CLOSED → tutto AMOUNT_NONE
INSERT INTO work_effort_type_status_cnt (
    work_effort_type_id, status_id, content_id, to_post, ctrl_amount_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT 'CTX_BS', s.status_id, f.content_id, 'Y', 'AMOUNT_NONE', NOW(),NOW(),NOW(),NOW()
FROM (VALUES ('WEORCARD_ACCOUNTED'),('WEORCARD_REVIEWED'),('WEORCARD_CLOSED')) AS s(status_id)
CROSS JOIN (VALUES ('WEFLD_MAIN'),('WEFLD_ORGUNIT'),('WEFLD_WROLE'),('WEFLD_WEFROM'),('WEFLD_NOTE'),('WEFLD_REVIEW'),('WEFLD_ELAB'),('WEFLD_AIND')) AS f(content_id)
ON CONFLICT (work_effort_type_id, status_id, content_id) DO NOTHING;

-- Migrazione schede esistenti da stato generico a workflow Cardarelli
UPDATE work_effort
SET current_status_id = 'WEORCARD_TOVALIDATE', last_status_update = NOW(),
    last_updated_stamp = NOW(), last_updated_tx_stamp = NOW()
WHERE work_effort_type_id = 'CTX_BS' AND current_status_id = 'WEPERFST_EXECPEND';

INSERT INTO work_effort_status (
    work_effort_id, status_id, status_datetime,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT we.work_effort_id, 'WEORCARD_TOVALIDATE', NOW(), NOW(),NOW(),NOW(),NOW()
FROM work_effort we
WHERE we.work_effort_type_id = 'CTX_BS' AND we.current_status_id = 'WEORCARD_TOVALIDATE'
ON CONFLICT DO NOTHING;

COMMIT;


-- =============================================================================
-- V003 — DATASOURCE IMPORT (CONFIG_IMPORT_SCHEDE_BS)
-- =============================================================================
-- Crea i 5 datasource dedicati per l'import a cascata delle schede CTX_BS:
--   IMPORT_INDICATORI_BS  → GL_ACCOUNT_INTERFACE   (catalogo indicatori)
--   IMPORT_SCHEDE_BS      → WE_ROOT_INTERFACE       (schede root)
--   IMPORT_MISURE_BS      → WE_MEASURE_INTERFACE    (misure/KPI sulla root)
--   IMPORT_RUOLI_BS       → WE_PARTY_INTERFACE      (referenti sulla root)
--   IMPORT_OBIETTIVI_BS   → WE_INTERFACE            (non usato nel modello nativo)
-- Rimuove il vecchio datasource multi-tracciato IMPORT_IND_BS se presente.
-- =============================================================================

DELETE FROM standard_import_field_config WHERE data_source_id = 'IMPORT_IND_BS';
DELETE FROM data_source                  WHERE data_source_id = 'IMPORT_IND_BS';
DELETE FROM data_source_type             WHERE data_source_type_id = 'IMPORT_IND_BS';

INSERT INTO public.data_source_type (data_source_type_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES
    ('IMPORT_SCHEDE_BS',    'Import Schede Performance Strategica',                      NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS', 'Import Obiettivi Performance Strategica',                   NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS',    'Import Misure Performance Strategica',                      NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_RUOLI_BS',     'Import Ruoli Performance Strategica',                       NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','Import Catalogo Indicatori Performance Strategica',         NOW(),NOW(),NOW(),NOW())
ON CONFLICT (data_source_type_id) DO UPDATE SET description = EXCLUDED.description;

INSERT INTO public.data_source (data_source_id, data_source_type_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES
    ('IMPORT_SCHEDE_BS',    'IMPORT_SCHEDE_BS',    'Caricamento Massivo Schede Performance Strategica CTX_BS',             NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS', 'IMPORT_OBIETTIVI_BS', 'Caricamento Massivo Obiettivi Performance Strategica CTX_BS',          NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS',    'IMPORT_MISURE_BS',    'Caricamento Massivo Misure Performance Strategica CTX_BS',             NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_RUOLI_BS',     'IMPORT_RUOLI_BS',     'Caricamento Massivo Ruoli Performance Strategica CTX_BS',              NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','IMPORT_INDICATORI_BS','Caricamento Massivo Indicatori (catalogo) Performance Strategica CTX_BS', NOW(),NOW(),NOW(),NOW())
ON CONFLICT (data_source_id) DO UPDATE SET description = EXCLUDED.description, last_updated_stamp = NOW(), last_updated_tx_stamp = NOW();

DELETE FROM standard_import_field_config WHERE data_source_id IN ('IMPORT_SCHEDE_BS','IMPORT_OBIETTIVI_BS','IMPORT_MISURE_BS','IMPORT_RUOLI_BS','IMPORT_INDICATORI_BS');

-- IMPORT_SCHEDE_BS → WE_ROOT_INTERFACE
INSERT INTO public.standard_import_field_config
    (data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES
    ('IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','sourceReferenceRootId',   'Codice Scheda',NULL,  1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','workEffortName',          'Nome Scheda',  NULL,  1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','orgCode',                 'Codice UOC',   NULL,  1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','estimatedStartDate',      'Data Inizio',  NULL,  1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','estimatedCompletionDate', 'Data Fine',    NULL,  1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','weContext',               NULL,'STR',           1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','workEffortTypeId',        NULL,'CTX_BS',        1,NOW(),NOW(),NOW(),NOW()),
    -- 'Inizializzata' = WEORCARD_INIT: risolto per descrizione su work_effort_type_status di CTX_BS.
    -- Senza questo le schede nascono con stato generico WEGS_CREATED (fuori dal workflow WEORCARD_*).
    ('IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','statusItemDesc',          NULL,'Inizializzata', 1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','operationType',           NULL,'O',             1,NOW(),NOW(),NOW(),NOW());

-- IMPORT_OBIETTIVI_BS → WE_INTERFACE (non usato nel modello nativo)
INSERT INTO public.standard_import_field_config
    (data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES
    ('IMPORT_OBIETTIVI_BS','WE_INTERFACE','sourceReferenceRootId',  'Codice Scheda',    NULL,       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS','WE_INTERFACE','sourceReferenceId',      'Codice Obiettivo', NULL,       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS','WE_INTERFACE','workEffortName',         'Nome Obiettivo',   NULL,       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS','WE_INTERFACE','description',            'Descrizione',      NULL,       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS','WE_INTERFACE','customText01',           'Area',             NULL,       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS','WE_INTERFACE','specialTerms',           'Fonte Dati',       NULL,       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS','WE_INTERFACE','estimatedStartDate',     'Data Inizio',      NULL,       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS','WE_INTERFACE','estimatedCompletionDate','Data Fine',        NULL,       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_OBIETTIVI_BS','WE_INTERFACE','workEffortTypeId',       NULL,'CTX_OB_BS',             1,NOW(),NOW(),NOW(),NOW());

-- IMPORT_MISURE_BS → WE_MEASURE_INTERFACE (indicatori sulla ROOT, modello nativo)
-- sourceReferenceId = Codice Scheda: la root è auto-parentata (parent_id = id), quindi
-- passando sourceReferenceId = codice scheda + workEffortTypeId = CTX_BS la misura
-- si aggancia direttamente alla ROOT. N righe = N indicatori sulla stessa scheda root.
INSERT INTO public.standard_import_field_config
    (data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','sourceReferenceRootId','Codice Scheda',    NULL,                                       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','sourceReferenceId',    'Codice Scheda',    NULL,                                       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','accountCode',          'Codice Indicatore',NULL,                                       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','kpiScoreWeight',       'Peso',             NULL,                                       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','fromDate',             'Data Inizio',      NULL,                                       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','thruDate',             'Data Fine',        NULL,                                       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','workEffortTypeId',     NULL,'CTX_BS',                                                 1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','weMeasureTypeDesc',    NULL,'Prestazione (KPI)',                                      1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','weScoreConvEnumId',    NULL,'Avanzamento in percentuale con 4 soglie',                1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','weScoreRangeEnumId',   NULL,'Valore di Fascia',                                      1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_MISURE_BS','WE_MEASURE_INTERFACE','uomRangeDesc',         NULL,'Performance 4 Fasce (0/50/75/100%)',                    1,NOW(),NOW(),NOW(),NOW());

-- IMPORT_RUOLI_BS → WE_PARTY_INTERFACE (referenti sulla ROOT, modello nativo)
INSERT INTO public.standard_import_field_config
    (data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES
    ('IMPORT_RUOLI_BS','WE_PARTY_INTERFACE','sourceReferenceRootId','Codice Scheda',       NULL,                1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_RUOLI_BS','WE_PARTY_INTERFACE','sourceReferenceId',    'Codice Scheda',       NULL,                1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_RUOLI_BS','WE_PARTY_INTERFACE','partyCode',            'Matricola Referente', NULL,                1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_RUOLI_BS','WE_PARTY_INTERFACE','fromDate',             'Data Inizio',         NULL,                1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_RUOLI_BS','WE_PARTY_INTERFACE','thruDate',             'Data Fine',           NULL,                1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_RUOLI_BS','WE_PARTY_INTERFACE','workEffortTypeId',     NULL,'CTX_BS',                             1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_RUOLI_BS','WE_PARTY_INTERFACE','roleTypeId',           NULL,'WEM_PERF_IN_CHARGE',                 1,NOW(),NOW(),NOW(),NOW());

-- =============================================================================
-- NATURA / AREA INDICATORI (GlResourceType + GlAccountResource)
-- =============================================================================
-- L'"Area" dell'obiettivo è mappata sul campo nativo "Natura" dell'indicatore
-- (gl_account.gl_resource_type_id). Le 4 Aree sono valori di GlResourceType e
-- vanno collegate al tipo conto degli indicatori (WECAL) via GlAccountResource:
-- senza il collegamento non compaiono nel dropdown "Natura" e l'import le scarta
-- (validazione checkValidGlAccountResource in GlAccountTypeHelper).
-- Equivalente UI: DATI DI BASE > Modello di Governance > Unità Contabili ed
-- Extracontabili > "Natura unità cont/extr" (valori) e tab "Natura" della
-- Tipologia Unità Extracontabili (collegamenti). Qui in seed per riproducibilità.
-- =============================================================================

BEGIN;

INSERT INTO gl_resource_type (gl_resource_type_id, description, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
VALUES
    ('AREA_APPR',     'Appropriatezza sanitaria', NOW(),NOW(),NOW(),NOW()),
    ('AREA_ESITI',    'Esiti',                    NOW(),NOW(),NOW(),NOW()),
    ('AREA_SERV_STR', 'Servizi strategici',       NOW(),NOW(),NOW(),NOW()),
    ('AREA_SERV_SAN', 'Servizi sanitari',         NOW(),NOW(),NOW(),NOW())
ON CONFLICT (gl_resource_type_id) DO UPDATE
    SET description = EXCLUDED.description, last_updated_stamp = NOW(), last_updated_tx_stamp = NOW();

INSERT INTO gl_account_resource (gl_account_type_id, gl_resource_type_id, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
VALUES
    ('WECAL','AREA_APPR',     NOW(),NOW(),NOW(),NOW()),
    ('WECAL','AREA_ESITI',    NOW(),NOW(),NOW(),NOW()),
    ('WECAL','AREA_SERV_STR', NOW(),NOW(),NOW(),NOW()),
    ('WECAL','AREA_SERV_SAN', NOW(),NOW(),NOW(),NOW())
ON CONFLICT (gl_account_type_id, gl_resource_type_id) DO NOTHING;

COMMIT;


-- IMPORT_INDICATORI_BS → GL_ACCOUNT_INTERFACE (catalogo indicatori)
-- Da eseguire PRIMA dell'import misure: crea gl_account completi con default_uom_id=OTH_SCO
-- (via defaultUomCode='Punt.'), account_type_enum_id=INDICATOR, purposeTypeId=FIN_VAL.
-- Senza questo passo l'import misure crea stub incompleti e gli indicatori non appaiono
-- nella tab "Indicatori di valutazione" (INNER JOIN UOM in queryIndicator.sql.ftl).

INSERT INTO public.custom_method (custom_method_id, custom_method_type_id, custom_method_name, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('SI_NO','GL_ACC','SI_NO','Indicatore Si/No (100%/0%)', NOW(),NOW(),NOW(),NOW())
ON CONFLICT (custom_method_id) DO NOTHING;

INSERT INTO public.standard_import_field_config
    (data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','accountCode',        'Codice Indicatore',      NULL,     1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','accountName',        'Indicatore',             NULL,     1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','description',        'Descrizione sintetica',  NULL,     1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','calcCustomMethodId', 'Tipologia',              NULL,     1,NOW(),NOW(),NOW(),NOW()),
    -- Area → "Natura" nativa dell'indicatore (gl_account.gl_resource_type_id).
    -- La colonna "Area" del file catalogo deve contenere il CODICE (AREA_APPR, AREA_ESITI,
    -- AREA_SERV_STR, AREA_SERV_SAN), non la descrizione: l'interfaccia ha solo glResourceTypeId
    -- (tipo id), nessun glResourceTypeDesc. I codici sono creati nel blocco Natura sopra.
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','glResourceTypeId',   'Area',                   NULL,     1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','accountTypeId',      NULL,'WECAL',                       1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','accountTypeEnumId',  NULL,'INDICATOR',                   1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','weMeasureTypeEnumId',NULL,'WEMT_PERF',                   1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','inputEnumId',        NULL,'ACCINP_UO',                   1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','purposeTypeId',      NULL,'FIN_VAL',                     1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','periodTypeDesc',     NULL,'Annuale',                     1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','defaultUomCode',     NULL,'Punt.',                       1,NOW(),NOW(),NOW(),NOW()),
    -- Fonte dati → gl_account.source (colonna "Fonte" del catalogo, es. "Software Wirgilio").
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','source',             'Fonte',                  NULL,     1,NOW(),NOW(),NOW(),NOW()),
    -- Referente indicatore → crea gl_account_role (party_id + role_type_id sull'indicatore).
    -- IL REFERENTE E' UNA UOC (unita' organizzativa), NON una persona: nel master Obiettivi
    -- la colonna "Referente" contiene una UOC (es. "UOC GSI"). Nel file catalogo mettiamo il
    -- CODICE UOC ("Codice UOC Referente", es. BAA9909); partyIdCdc lo risolve a party_id via
    -- party_parent_role.parent_role_code (role ORGANIZATION_UNIT). roleTypeIdCdc = WEM_IND_IN_CHARGE.
    -- Un solo referente per indicatore (condiviso su tutte le schede) → una sola gl_account_role.
    -- Righe senza codice referente → nessun gl_account_role (l'import salta).
    -- PREREQUISITO: party_role(party_UOC, WEM_IND_IN_CHARGE) deve esistere (seed V006 sotto);
    -- importPartyRole() la VERIFICA ma non la crea.
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','partyIdCdc',         'Codice UOC Referente',   NULL,     1,NOW(),NOW(),NOW(),NOW()),
    ('IMPORT_INDICATORI_BS','GL_ACCOUNT_INTERFACE','roleTypeIdCdc',      NULL,'WEM_IND_IN_CHARGE',           1,NOW(),NOW(),NOW(),NOW());

-- Pulizia modello obsoleto (folder CTX_OB_BS inerti della vecchia versione)
DELETE FROM work_effort_type_content WHERE work_effort_type_id = 'CTX_OB_BS';


-- =============================================================================
-- V004 — TAB RUOLI/REFERENTI VISIBILE SU CTX_BS
-- =============================================================================
-- Il tab WEFLD_WROLE (Ruoli/Referenti) richiede una riga in work_effort_type_content
-- con weTypeContentTypeId='FOLDER' e isVisible='Y'. Senza questa riga il groovy
-- getWorkEffortViewExcludedContent.groovy nasconde il tab per qualsiasi stato.
-- La configurazione in work_effort_type_status_cnt (V002) controlla la sola
-- editabilità per stato, ma non è sufficiente per rendere il tab visibile.
-- =============================================================================

BEGIN;

INSERT INTO work_effort_type_content (
    work_effort_type_id, content_id, we_type_content_type_id, is_visible,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('CTX_BS', 'WEFLD_WROLE', 'FOLDER', 'Y', NOW(), NOW(), NOW(), NOW())
ON CONFLICT (work_effort_type_id, content_id) DO UPDATE
    SET we_type_content_type_id = EXCLUDED.we_type_content_type_id,
        is_visible              = EXCLUDED.is_visible,
        last_updated_stamp      = NOW(),
        last_updated_tx_stamp   = NOW();

COMMIT;

-- =============================================================================
-- V004b — TAB STORICO STATI VISIBILE SU CTX_BS
-- =============================================================================
-- Il tab WEFLD_STATUS (Storico Stati) richiede una riga in work_effort_type_content
-- con weTypeContentTypeId='FOLDER' e isVisible='Y'. Stessa meccanica di V004.
-- Senza questa riga getWorkEffortViewExcludedContent.groovy nasconde il tab.
-- =============================================================================

BEGIN;

INSERT INTO work_effort_type_content (
    work_effort_type_id, content_id, we_type_content_type_id, is_visible,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('CTX_BS', 'WEFLD_STATUS', 'FOLDER', 'Y', NOW(), NOW(), NOW(), NOW())
ON CONFLICT (work_effort_type_id, content_id) DO UPDATE
    SET we_type_content_type_id = EXCLUDED.we_type_content_type_id,
        is_visible              = EXCLUDED.is_visible,
        last_updated_stamp      = NOW(),
        last_updated_tx_stamp   = NOW();

COMMIT;

-- =============================================================================
-- V005 — PREREQUISITO RESPONSABILE SCHEDA (WEM_PERF_IN_CHARGE)
-- =============================================================================
-- Il responsabile della scheda = DIRETTORE della UO (PERSONA). Il ruolo
-- WEM_PERF_IN_CHARGE è parented su EMPLOYEE: l'import ruoli risolve la MATRICOLA
-- del direttore via party_parent_role.parent_role_code (role EMPLOYEE) → party_id.
-- => Il file ruoli deve contenere le MATRICOLE dei direttori
--    (WePartyInterface_BS.xlsx = versione con matricole, ex "_corretto";
--     mappatura in analisi/mappatura-uo-direttore.md), NON i codici UO.
--
-- checkValidityPartyRole (WePartyInterfaceHelper) VERIFICA ma non crea la
-- party_role(direttore, WEM_PERF_IN_CHARGE) → seed preventivo per gli EMPLOYEE.
--
-- NB: revert del vecchio fix errato che portava WEM_PERF_IN_CHARGE a
-- ORGANIZATION_UNIT (avrebbe assegnato la UO invece del direttore-persona).
-- =============================================================================

BEGIN;

-- parent_type_id = EMPLOYEE (persona). Idempotente: forza il valore corretto
-- anche se una vecchia esecuzione l'aveva messo a ORGANIZATION_UNIT.
UPDATE role_type
SET    parent_type_id        = 'EMPLOYEE',
       last_updated_stamp    = NOW(),
       last_updated_tx_stamp = NOW()
WHERE  role_type_id = 'WEM_PERF_IN_CHARGE';

-- party_role preventivo per tutti i direttori/EMPLOYEE (idempotente)
INSERT INTO party_role (party_id, role_type_id, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
SELECT DISTINCT
    ppr.party_id,
    'WEM_PERF_IN_CHARGE',
    NOW(), NOW(), NOW(), NOW()
FROM   party_parent_role ppr
WHERE  ppr.role_type_id = 'EMPLOYEE'
ON CONFLICT (party_id, role_type_id) DO NOTHING;

COMMIT;

-- =============================================================================
-- V006 — PREREQUISITO REFERENTE INDICATORE (WEM_IND_IN_CHARGE)
-- =============================================================================
-- Il referente di un indicatore = UNITA' ORGANIZZATIVA (UOC), NON una persona:
-- nel master Obiettivi la colonna "Referente" e' una UOC (es. "UOC GSI"). E' unico
-- per indicatore e vale su tutte le schede che lo usano.
-- L'import indicatori risolve il CODICE UOC referente via party_parent_role.
-- parent_role_code → party_id (unita' org.) e crea gl_account_role, MA
-- importPartyRole() (GlAccountPurposeInterfaceHelper) VERIFICA e non crea la
-- party_role(party_UOC, WEM_IND_IN_CHARGE): senza, l'import fallisce "IS NOT VALID".
-- Stessa meccanica del Bug 2 di V005 (WEM_PERF_IN_CHARGE), su unita' organizzativa.
--
-- Fix: (1) role_type WEM_IND_IN_CHARGE con parent ORGANIZATION_UNIT (NON EMPLOYEE);
--      (2) seed party_role preventivo per tutte le UOC (role ORGANIZATION_UNIT).
-- =============================================================================

BEGIN;

-- (1) role_type referente indicatore = unita' organizzativa
INSERT INTO role_type (role_type_id, parent_type_id, has_table, description, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
VALUES ('WEM_IND_IN_CHARGE', 'ORGANIZATION_UNIT', 'N', 'Referente Indicatore Performance Strategica', NOW(), NOW(), NOW(), NOW())
ON CONFLICT (role_type_id) DO UPDATE
    SET parent_type_id = 'ORGANIZATION_UNIT', last_updated_stamp = NOW(), last_updated_tx_stamp = NOW();

-- (2) party_role preventivo per tutte le unita' organizzative (idempotente)
INSERT INTO party_role (party_id, role_type_id, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
SELECT DISTINCT
    ppr.party_id,
    'WEM_IND_IN_CHARGE',
    NOW(), NOW(), NOW(), NOW()
FROM   party_parent_role ppr
WHERE  ppr.role_type_id = 'ORGANIZATION_UNIT'
ON CONFLICT (party_id, role_type_id) DO NOTHING;

COMMIT;


-- =============================================================================
-- PARAMETRI INDICATORI CON FORMULA — un solo indicatore, N parametri
-- =============================================================================
-- Un indicatore con formula (NUM/DEN) resta UNO SOLO. I parametri sono
-- gl_fiscal_type dedicati (description = etichetta per il referente); la loro
-- definizione (quanti/quali/ruoli) sta in gl_account_input_calc con
-- gl_account_id_ref = NULL (nessun secondo indicatore). I valori si inseriscono
-- come transazioni su QUESTO indicatore col rispettivo gl_fiscal_type; il valore
-- finale (calcolo esterno) = ACTUAL. gl_account_input_calc non ha import nativo.
--
-- ORDER-SAFE: le righe input_calc si inseriscono solo se l'indicatore esiste
-- (arriva dall'import catalogo). Rieseguire SETUP dopo l'import le popola.
--
-- Caso a 3 parametri: FA34 (A34) "Percentuale di pazienti trattati con setting
-- assistenziale appropriato" = (day surgery + ricovero ordinario) / week surgery
-- Nota: i gl_fiscal_type sotto sono globali → compaiono nel dropdown "Tipo
-- Rilevazione" anche di altri indicatori (limite noto del vincolo "un indicatore").
-- =============================================================================

BEGIN;

-- Parametri come tipi rilevazione (description = etichetta mostrata dalla modale)
INSERT INTO gl_fiscal_type (gl_fiscal_type_id, description, gl_fiscal_type_enum_id, is_financial_used, is_account_used, is_indicator_used, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
VALUES
    ('PAR_A34_DAY',  'Pazienti trattati in day surgery', 'GLFISCTYPE_ACTUAL','N','N','Y', NOW(),NOW(),NOW(),NOW()),
    ('PAR_A34_ORD',  'Pazienti in ricovero ordinario',   'GLFISCTYPE_ACTUAL','N','N','Y', NOW(),NOW(),NOW(),NOW()),
    ('PAR_A34_WEEK', 'Pazienti in week surgery',         'GLFISCTYPE_ACTUAL','N','N','Y', NOW(),NOW(),NOW(),NOW())
ON CONFLICT (gl_fiscal_type_id) DO UPDATE
    SET description = EXCLUDED.description, last_updated_stamp = NOW(), last_updated_tx_stamp = NOW();

-- Definizione parametri di FA34: 3 righe, (A + A) / B ; gl_account_id_ref = NULL
INSERT INTO gl_account_input_calc (gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
SELECT v.* FROM (VALUES
    ('AIC_FA34_1','FA34','1', NULL::varchar, 'A', 'PAR_A34_DAY',  NOW(),NOW(),NOW(),NOW()),
    ('AIC_FA34_2','FA34','2', NULL::varchar, 'A', 'PAR_A34_ORD',  NOW(),NOW(),NOW(),NOW()),
    ('AIC_FA34_3','FA34','3', NULL::varchar, 'B', 'PAR_A34_WEEK', NOW(),NOW(),NOW(),NOW())
) AS v(gl_account_input_calc_id, gl_account_id, input_sequence_num, gl_account_id_ref, factor_calculator, gl_fiscal_type_id, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
WHERE EXISTS (SELECT 1 FROM gl_account g WHERE g.gl_account_id = v.gl_account_id)
ON CONFLICT (gl_account_input_calc_id) DO NOTHING;

COMMIT;


-- =============================================================================
-- FORMATO INDICATORI "CARDARELLI" (card anagrafica indicatore)
-- -----------------------------------------------------------------------------
-- Registra il nuovo formato di visualizzazione del folder "Indicatori" (WEFLD_IND)
-- e lo rende attivo per la tipologia scheda CTX_BS (Performance Strategica).
-- Requisiti: screen WorkEffortMeasureIndicatorCardarelliLayout + FTL + controller
-- (gia' presenti nel codice) -> serve deploy/restart dell'applicativo.
-- La card mostra: Area(Natura), Codice, Indicatore, Descrizione sintetica,
-- Formula, Fonte dati, Peso 60esimi, Referente, Valore atteso, Range/fasce.
-- =============================================================================

BEGIN;

-- Risorsa dati -> screen di layout
INSERT INTO data_resource (data_resource_id, data_resource_type_id, data_template_type_id, data_resource_name, mime_type_id, object_info, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
VALUES ('IND_CARD_LAY','URL_RESOURCE','SCREEN_COMBINED','IndicatoriCardarelli','text/html',
        'component://workeffortext/widget/screens/LayoutWorkEffortRootScreens.xml#WorkEffortMeasureIndicatorCardarelliLayout',
        NOW(),NOW(),NOW(),NOW())
ON CONFLICT (data_resource_id) DO UPDATE
    SET object_info = EXCLUDED.object_info, last_updated_stamp = NOW(), last_updated_tx_stamp = NOW();

-- Content = opzione di formato nel folder Indicatori (contentTypeId = WEFLD_IND)
INSERT INTO content (content_id, content_type_id, data_resource_id, status_id, content_name, description, mime_type_id, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
VALUES ('IND_CARD_LAY','WEFLD_IND','IND_CARD_LAY','CTNT_IN_PROGRESS','IndicatoriCardarelli','Indicatori - Scheda Cardarelli','text/plain',
        NOW(),NOW(),NOW(),NOW())
ON CONFLICT (content_id) DO UPDATE
    SET content_type_id = EXCLUDED.content_type_id, data_resource_id = EXCLUDED.data_resource_id,
        description = EXCLUDED.description, last_updated_stamp = NOW(), last_updated_tx_stamp = NOW();

-- Attivazione del formato per CTX_BS: assegnazione WorkEffortTypeContent.
-- getRootLayout usa il primo per sequence_num -> il nuovo formato ha sequence 1;
-- l'eventuale formato precedente (IND_PROJ_LAY) viene retrocesso a sequence 10.
INSERT INTO work_effort_type_content (work_effort_type_id, we_type_content_type_id, content_id, sequence_num, is_visible, created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp)
VALUES ('CTX_BS','WEFLD_IND','IND_CARD_LAY', 1, 'Y', NOW(),NOW(),NOW(),NOW())
ON CONFLICT (work_effort_type_id, content_id) DO UPDATE
    SET we_type_content_type_id = EXCLUDED.we_type_content_type_id, sequence_num = EXCLUDED.sequence_num,
        is_visible = EXCLUDED.is_visible, last_updated_stamp = NOW(), last_updated_tx_stamp = NOW();

UPDATE work_effort_type_content
    SET sequence_num = 10, last_updated_stamp = NOW(), last_updated_tx_stamp = NOW()
WHERE work_effort_type_id='CTX_BS' AND we_type_content_type_id='WEFLD_IND' AND content_id <> 'IND_CARD_LAY';

COMMIT;
