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
-- Post-import (dopo caricamento dati UI): SETUP_POST_IMPORT.sql (wrapper: FASCE_COMPLETO + PARAMETRI_INDICATORI)
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
    -- NB: NIENTE transizione diretta TOVALIDATE->VALIDATED: dal "Da validare" il direttore UO
    -- può solo "Valida parzialmente". VALIDATED si raggiunge da VALPART (futuro direttore san/amm).
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
    ('CTX_BS','WEORCARD_TOVALIDATE', 'ACTUAL','WEORCARD_VALPART',   'CTRL_SCORE_NONE', NOW(),NOW(),NOW(),NOW()),
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


-- =============================================================================
-- V007 — PROFILO SICUREZZA DIRETTORE UO (ORGPERF_DIR_UO)
-- =============================================================================
-- Consolidato da profile-permissions/setup_orgperf_dir_uo_profile.sql (NON idempotente:
-- 164 INSERT senza ON CONFLICT). Qui e' avvolto da un guard psql \if: il blocco gira
-- SOLO se il gruppo non esiste ancora => SETUP resta ri-eseguibile e NON cancella le
-- assegnazioni utente-gruppo (user_login_security_group) aggiunte a mano.
-- Per ricreare da zero il profilo: prima profile-permissions/rollback_orgperf_dir_uo_profile.sql
-- =============================================================================

SELECT NOT EXISTS(SELECT 1 FROM security_group WHERE group_id='ORGPERF_DIR_UO') AS need_dir_uo \gset
\if :need_dir_uo

-- =====================================================================
-- CONFIGURAZIONE SECURITY PROFILE ORGPERF_DIR_UO
-- =====================================================================
-- Script per configurare il profilo di sicurezza ORGPERF_DIR_UO con:
-- - 1 permesso custom (ORGPERFDIR_VIEW)
-- - 9 permessi operativi (security_group_permission)
--
-- FUNZIONALITÀ ABILITATE:
--   - Visualizzazione Performance Strategica (OrgPerf) in sola lettura
--   - Validazione parziale schede con obiettivi (su GP_MENU_00092 Definizione)
--   - Presa visione schede con risultati finali (su GP_MENU_00101 Valutazione)
--
-- CONTESTO:
--   - CTX_BS: Performance Strategica
--
-- PORTALE PREDEFINITO: NULL (lascia decidere al gruppo EMPLPERF_VALUTATORE se presente)
--
-- MENU VISIBILI (in sidebar):
--   Performance Strategica > Gestione > Definizione  (GP_MENU_00092)
--   Performance Strategica > Gestione > Valutazione  (GP_MENU_00101)
--   Performance Individuale > ...                     (se l'utente ha anche EMPLPERF_VALUTATORE)
--
-- MENU ESCLUSI (tutto il resto tranne GP_MENU_00124 e discendenti):
--   - Altre sezioni Performance Management (GP_MENU_00105, 00450, 00466)
--   - PS > Amministrazione (GP_MENU_00400)
--   - PS > Gestione > Monitoraggio (GP_MENU_00096)
--   - PS > Gestione > altri (GP_MENU_00494, 00515)
--   - PS > Consultazione foglie (tranne NOPORTAL_BSC che serve al portale)
--   - Dati di Base non accessibili
--   - Contesti esclusi per AORN: CTX_OR, governance, altri moduli
--
-- NOTA: GP_MENU_00124 (Performance Individuale) e discendenti NON sono esclusi,
--       per permettere la combinazione con EMPLPERF_VALUTATORE senza conflitti.
--       Un utente con solo ORGPERF_DIR_UO non vede comunque le foglie /emplperf/
--       nel sidebar (manca il permesso EMPLPERFMGR_VIEW).
-- =====================================================================

-- =====================================================================
-- SEZIONE 1: CREAZIONE PERMESSO CUSTOM
-- =====================================================================

INSERT INTO public.security_permission
(permission_id, description, dynamic_access, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, enabled, last_modified_by_user_login, created_by_user_login)
VALUES('ORGPERFDIR_VIEW', 'Direttore UO - Performance Strategica permission', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Y', NULL, NULL);


-- =====================================================================
-- SEZIONE 2: CREAZIONE SECURITY GROUP
-- =====================================================================

INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('ORGPERF_DIR_UO', 'Performance Strategica - Direttore Unita Operativa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'admin', NULL);
-- NOTA: default_portal_page_id = NULL intenzionale.
-- Se l'utente ha anche EMPLPERF_VALUTATORE (GP_WE_PORTAL_3), quel portale viene usato.
-- Avere due portali diversi (GP_WE_PORTAL_3 + GP_WE_PORTAL_4) causa crash al login.


-- =====================================================================
-- SEZIONE 3: PERMESSI SECURITY_GROUP_PERMISSION (10 permessi totali)
-- =====================================================================

-- Permesso specifico Direttore UO
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'ORGPERFDIR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Visualizzazione modulo OrgPerf (Performance Strategica)
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'ORGPERFMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Accesso role-based alle schede (il Direttore UO è referenziato via work_effort_party_assignment)
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'ORGPERFROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Work Effort: creazione e aggiornamento (necessario per transizioni di stato)
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'WORKEFFORTMGR_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'WORKEFFORTMGR_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Content Manager
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'CONTENTMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'CONTENTMGR_ROLE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'CONTENTMGR_ROLE_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Balanced Scorecard Performance (BSCPERF): necessario per far apparire le foglie
-- di Performance Strategica nel sidebar (link /stratperf/... → chiave BSCPERF)
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'BSCPERFMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- OFBiz Tools base
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'OFBTOOLS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- =====================================================================
-- SEZIONE 4: VERIFICA CONFIGURAZIONE
-- =====================================================================

SELECT COUNT(*) as totale_permessi
FROM security_group_permission
WHERE group_id = 'ORGPERF_DIR_UO';


-- =====================================================================
-- SEZIONE 5: ESCLUSIONI MENU (security_group_content)
-- =====================================================================
-- Logica: escludi tutto tranne:
--   - GP_MENU_00086/00401/00092/00101 (Performance Strategica > Gestione > Definizione/Valutazione)
--   - GP_MENU_00124 e discendenti (Performance Individuale — gestiti da EMPLPERF_VALUTATORE)
--   - NOPORTAL_BSC: NON escluso — è portlet di GP_WE_PORTAL_4, escluderlo causa logout immediato
--   - NOPORTAL_MY: NON escluso — è portlet di GP_WE_PORTAL_3
--
-- Lista generata dal DB corrente (2026-07-23).

INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00030', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00031', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00032', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00037', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00038', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00039', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00040', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00041', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00042', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00045', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00081', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00089', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00096', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00104', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00105', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00108', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00111', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00115', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00120', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00123', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00187', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00188', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00193', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00194', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00198', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00207', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00209', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00210', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00211', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00215', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00216', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00217', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00218', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00219', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00220', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00221', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00223', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00225', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00227', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00234', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00242', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00245', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00249', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00250', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00251', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00253', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00254', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00264', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00265', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00266', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00282', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00332', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00334', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00347', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00348', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00400', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00402', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00403', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00404', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00405', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00445', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00446', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00450', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00451', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00452', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00453', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00454', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00455', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00456', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00457', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00458', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00459', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00460', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00461', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00462', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00463', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00464', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00465', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00466', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00467', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00468', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00469', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00470', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00471', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00472', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00474', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00475', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00476', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00477', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00478', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00479', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00480', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00481', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00482', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00483', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00485', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00486', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00487', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00488', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00489', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00491', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00492', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00493', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00494', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00495', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00497', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00498', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00499', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00500', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00504', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00505', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00506', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00507', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00508', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00509', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00510', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00511', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00512', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00513', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00514', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00515', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00516', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00517', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00518', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00521', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00522', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00523', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00524', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00538', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00543', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00544', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00545', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00546', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00549', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00550', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00551', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00553', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00554', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00561', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00562', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00563', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00566', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00567', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00568', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00569', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00570', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_N0001', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_N0002', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_N0003', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- NOTA: NOPORTAL_BSC NON escluso — portlet landing di GP_WE_PORTAL_4; escluderlo causa logout immediato
-- NOTA: NOPORTAL_MY NON escluso — portlet landing di GP_WE_PORTAL_3
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'NOPORTAL_DIR', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'NOPORTAL_ORG', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'NOPORTAL_PART', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- =====================================================================
-- SEZIONE 6: VERIFICA ESCLUSIONI
-- =====================================================================

SELECT COUNT(*) as totale_esclusioni
FROM security_group_content
WHERE group_id = 'ORGPERF_DIR_UO';


-- =====================================================================
-- RIEPILOGO CONFIGURAZIONE
-- =====================================================================
-- SECURITY GROUP: ORGPERF_DIR_UO
-- PORTALE: NULL (usa il portale del gruppo EMPLPERF_VALUTATORE se presente)
--
-- PERMESSI (10):
--   - ORGPERFDIR_VIEW (custom)
--   - ORGPERFMGR_VIEW (accesso modulo Performance Strategica)
--   - ORGPERFROLE_ADMIN (accesso role-based schede)
--   - WORKEFFORTMGR_CREATE, WORKEFFORTMGR_UPDATE (transizioni di stato)
--   - CONTENTMGR_VIEW, CONTENTMGR_ROLE_VIEW, CONTENTMGR_ROLE_CREATE
--   - BSCPERFMGR_VIEW (necessario per sidebar: chiave BSCPERF → link /stratperf/...)
--   - OFBTOOLS_VIEW
--
-- MENU ACCESSIBILI (sidebar):
--   GP_MENU_00086 Performance Strategica
--   GP_MENU_00401   > Gestione
--   GP_MENU_00092       > Definizione   (validazione parziale schede con obiettivi)
--   GP_MENU_00101       > Valutazione   (presa visione schede con risultati finali)
--   GP_MENU_00124 Performance Individuale (visibile se combinato con EMPLPERF_VALUTATORE)
--
-- ESCLUSIONI: 152 menu (contesti inattivi + PS sub-sezioni non accessibili)
-- GP_MENU_00124 e discendenti NON esclusi (gestiti da EMPLPERF_VALUTATORE)
-- =====================================================================

\endif


-- =====================================================================
-- V008 / V009 — PROFILI DIRETTORE SANITARIO (ORGPERF_DIR_SAN) e AMMINISTRATIVO (ORGPERF_DIR_AMM)
-- =====================================================================
-- Direttori strategici: validazione COMPLETA (VALPART -> VALIDATED) col bottone "Valida"; vedono
-- TUTTE le schede CTX_BS in qualsiasi stato in SOLA CONSULTAZIONE. Costruiti CLONANDO ORGPERF_DIR_UO
-- (deve gia' esistere, vedi V007) e lasciando visibile Consultazione>Interrogazione (GP_MENU_00402/00104).
-- Scoping: non essendo in ORGPERF_DIR_UO, il groovy NON li scopa per UO -> vedono tutto.
-- Utenti: mariomassimo.mensorio (SAN, matr.53265), marcella.abbate (AMM, matr.53228).
-- Sorgente unica: profile-permissions/setup_orgperf_dir_san_amm_profile.sql
-- =====================================================================

-- ORGPERF_DIR_SAN
SELECT NOT EXISTS(SELECT 1 FROM security_group WHERE group_id='ORGPERF_DIR_SAN') AS need_dir_san \gset
\if :need_dir_san
INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('ORGPERF_DIR_SAN', 'Performance Strategica - Direttore Sanitario', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'admin', NULL);
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'ORGPERF_DIR_SAN', permission_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM public.security_group_permission WHERE group_id='ORGPERF_DIR_UO';
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'ORGPERF_DIR_SAN', content_id, from_date, thru_date, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM public.security_group_content WHERE group_id='ORGPERF_DIR_UO' AND content_id NOT IN ('GP_MENU_00402','GP_MENU_00104');
INSERT INTO public.user_login_security_group (user_login_id, group_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('mariomassimo.mensorio', 'ORGPERF_DIR_SAN', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
\endif

-- ORGPERF_DIR_AMM
SELECT NOT EXISTS(SELECT 1 FROM security_group WHERE group_id='ORGPERF_DIR_AMM') AS need_dir_amm \gset
\if :need_dir_amm
INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('ORGPERF_DIR_AMM', 'Performance Strategica - Direttore Amministrativo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'admin', NULL);
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'ORGPERF_DIR_AMM', permission_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM public.security_group_permission WHERE group_id='ORGPERF_DIR_UO';
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'ORGPERF_DIR_AMM', content_id, from_date, thru_date, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM public.security_group_content WHERE group_id='ORGPERF_DIR_UO' AND content_id NOT IN ('GP_MENU_00402','GP_MENU_00104');
INSERT INTO public.user_login_security_group (user_login_id, group_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('marcella.abbate', 'ORGPERF_DIR_AMM', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
\endif


-- =====================================================================
-- V007b — Abilita "Consultazione > Interrogazione Schede Strategiche" al Direttore UO
-- =====================================================================
-- security_group_content = lista ESCLUSIONI: rimuovendo le esclusioni di GP_MENU_00402
-- (Consultazione) e GP_MENU_00104 (Interrogazione Schede Strategiche) il Direttore UO VEDE
-- l'Interrogazione (scoping sulle proprie UO gestito dal groovy Inqy). Idempotente.
DELETE FROM public.security_group_content
 WHERE group_id='ORGPERF_DIR_UO' AND content_id IN ('GP_MENU_00402','GP_MENU_00104');


-- =====================================================================
-- V010 — Registrazione stampa "Assegnazione Obiettivi" (Raccolta_Requisiti 8.1) su CTX_BS
-- =====================================================================
-- Rende il report SchedaAssegnazioneObiettiviBS.rptdesign selezionabile in
-- Consultazione > Stampe e nel lookup "Stampa attiva" (Tipologie > CTX_BS > Stampe abilitate).
-- Serve: DataResource+Content (REPORT), content_assoc verso WE_PRINT (REP_PERM) per comparire
-- nel lookup, content_assoc verso TYPE_PRINT_PDF (TYPE_PRINT) per il formato, e la riga
-- work_effort_type_content (REPORT) sul tipo CTX_BS. Idempotente.
BEGIN;

DELETE FROM work_effort_type_content WHERE work_effort_type_id='CTX_BS' AND content_id='REPORT_BS_ASS';
DELETE FROM content_assoc WHERE content_id_to='REPORT_BS_ASS';
DELETE FROM content WHERE content_id='REPORT_BS_ASS';
DELETE FROM data_resource WHERE data_resource_id='REPORT_BS_ASS';

INSERT INTO data_resource (data_resource_id, data_resource_type_id, object_info, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('REPORT_BS_ASS', 'LOCAL_FILE',
        'component://workeffortext/webapp/workeffortext/birt/report/SchedaAssegnazioneObiettiviBS.rptdesign',
        'admin', now(), now(), now(), now());

INSERT INTO content (content_id, content_type_id, content_name, description, data_resource_id, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('REPORT_BS_ASS', 'REPORT', 'SchedaAssegnazioneObiettiviBS', 'Scheda Assegnazione Obiettivi (Perf. Strategica)',
        'REPORT_BS_ASS', 'admin', now(), now(), now(), now());

INSERT INTO content_assoc (content_id, content_id_to, content_assoc_type_id, from_date, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('WE_PRINT', 'REPORT_BS_ASS', 'REP_PERM', now(), 'admin', now(), now(), now(), now());

INSERT INTO content_assoc (content_id, content_id_to, content_assoc_type_id, from_date, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('TYPE_PRINT_PDF', 'REPORT_BS_ASS', 'TYPE_PRINT', now(), 'admin', now(), now(), now(), now());

INSERT INTO work_effort_type_content (work_effort_type_id, we_type_content_type_id, content_id,
       etch, is_visible, use_filter, only_admin, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('CTX_BS', 'REPORT', 'REPORT_BS_ASS',
        'Stampa Assegnazione Obiettivi', 'Y', 'Y', 'N', 'admin',
        now(), now(), now(), now());

COMMIT;


-- =====================================================================
-- V011 — Stampa CONSUNTIVAZIONE (Scheda 3) su CTX_BS — slot REPORT_BS_DETT
-- =====================================================================
-- La Scheda 2 (Descrizione/razionali) e' stata UNITA nella Scheda 1 (REPORT_BS_ASS = sintesi+dettaglio).
-- Lo slot REPORT_BS_DETT e' ora la stampa CONSUNTIVAZIONE (Scheda 3): report
-- SchedaConsuntivazioneObiettiviBS.rptdesign, radio "STAMPA CONSUNTIVAZIONE SCHEDE".
-- NB: le colonne consuntivo del report (Num/Den/Risultato/Punti) sono placeholder finche' la modale
-- referente non salvera' i movimenti (doc 13 §3.1); il layout/registrazione sono comunque pronti. Idempotente.
BEGIN;

DELETE FROM work_effort_type_content WHERE work_effort_type_id='CTX_BS' AND content_id='REPORT_BS_DETT';
DELETE FROM content_assoc WHERE content_id_to='REPORT_BS_DETT';
DELETE FROM content WHERE content_id='REPORT_BS_DETT';
DELETE FROM data_resource WHERE data_resource_id='REPORT_BS_DETT';

INSERT INTO data_resource (data_resource_id, data_resource_type_id, object_info, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('REPORT_BS_DETT', 'LOCAL_FILE',
        'component://workeffortext/webapp/workeffortext/birt/report/SchedaConsuntivazioneObiettiviBS.rptdesign',
        'admin', now(), now(), now(), now());

INSERT INTO content (content_id, content_type_id, content_name, description, data_resource_id, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('REPORT_BS_DETT', 'REPORT', 'SchedaConsuntivazioneObiettiviBS', 'Stampa Consuntivazione Obiettivi (Perf. Strategica)',
        'REPORT_BS_DETT', 'admin', now(), now(), now(), now());

INSERT INTO content_assoc (content_id, content_id_to, content_assoc_type_id, from_date, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('WE_PRINT', 'REPORT_BS_DETT', 'REP_PERM', now(), 'admin', now(), now(), now(), now());

INSERT INTO content_assoc (content_id, content_id_to, content_assoc_type_id, from_date, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('TYPE_PRINT_PDF', 'REPORT_BS_DETT', 'TYPE_PRINT', now(), 'admin', now(), now(), now(), now());

INSERT INTO work_effort_type_content (work_effort_type_id, we_type_content_type_id, content_id,
       etch, is_visible, use_filter, only_admin, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('CTX_BS', 'REPORT', 'REPORT_BS_DETT',
        'STAMPA CONSUNTIVAZIONE SCHEDE', 'Y', 'Y', 'N', 'admin',
        now(), now(), now(), now());

COMMIT;


-- =====================================================================
-- V012 — Profilo REFERENTE (consuntivazione CTX_BS): ORGPERF_REFERENTE
-- =====================================================================
-- Permesso dedicato CONSUNT_CTX_BS_VIEW (chiave 'CONSUNTCTXBS' -> gata la foglia con link
-- '/consuntCtxBs', stessa convenzione di ANALYSIS_CTX_BS_VIEW/'/analysisCtxBs'). Gruppo ADDITIVO
-- ORGPERF_REFERENTE (portale NULL, ZERO esclusioni). Membership auto-derivata: i responsabili
-- ORG_RESPONSIBLE delle UOC-referente (gl_account_role WEM_IND_IN_CHARGE) -> 44 login (0 senza login).
-- L'admin (gruppo AORNADMIN) riceve il permesso -> vede SEMPRE la voce di consuntivazione. Idempotente.
-- Dettagli: doc 5 (ORGPERF_REFERENTE), doc 11 §6.2, doc 13 §4.
BEGIN;

INSERT INTO security_permission (permission_id, description, enabled, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('CONSUNT_CTX_BS_VIEW', 'Referente indicatore - Consuntivazione Performance Strategica (CTX_BS)', 'Y', 'admin',
        now(), now(), now(), now())
ON CONFLICT (permission_id) DO NOTHING;

INSERT INTO security_group (group_id, description, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('ORGPERF_REFERENTE', 'Performance Strategica - Referente Indicatore (consuntivazione)', 'admin',
        now(), now(), now(), now())
ON CONFLICT (group_id) DO NOTHING;

INSERT INTO security_group_permission (group_id, permission_id,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('ORGPERF_REFERENTE', 'CONSUNT_CTX_BS_VIEW', now(), now(), now(), now())
ON CONFLICT (group_id, permission_id) DO NOTHING;

-- Admin (AORNADMIN) vede SEMPRE la consuntivazione
INSERT INTO security_group_permission (group_id, permission_id,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('AORNADMIN', 'CONSUNT_CTX_BS_VIEW', now(), now(), now(), now())
ON CONFLICT (group_id, permission_id) DO NOTHING;

-- NB: la MEMBERSHIP (assegnazione utenti->gruppo) NON sta qui: dipende dai dati importati
-- (soggetti + ORG_RESPONSIBLE) ed e' in POST_IMPORT_ASSEGNA_PROFILI.sql. Qui solo struttura.
-- L'admin (AORNADMIN) vede comunque la voce grazie al grant permesso qui sopra.

COMMIT;


-- =====================================================================
-- V013 — Foglia menu "Consuntivazione indicatori" (Portale Referente) su CTX_BS
-- =====================================================================
-- Voce nativa GP_MENU_00571 sotto GP_MENU_00402 (Consultazione, Performance Strategica). Link
-- '/consuntCtxBs' = TOKEN DI GATING (permesso CONSUNT_CTX_BS_VIEW, V012); il FE naviga alla route
-- nativa via REFURBISHED_PAGES (GP_MENU_00571 -> CTX_BS/consuntivazione), NON dal link. Il label
-- deriva dalla substring dopo l'ultimo '.' del title ('MenuUiLabels.Consuntivazione indicatori').
-- Visibile ai referenti e all'admin (V012). Idempotente (DELETE-then-INSERT).
BEGIN;

DELETE FROM content_assoc     WHERE content_id_to = 'GP_MENU_00571';
DELETE FROM content_attribute WHERE content_id    = 'GP_MENU_00571';
DELETE FROM content           WHERE content_id    = 'GP_MENU_00571';

INSERT INTO content (content_id, content_type_id, status_id, mime_type_id, description, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('GP_MENU_00571', 'GPLUS_MENU_ITEM', 'CTNT_IN_PROGRESS', 'text/plain',
        'Portale Referente - Consuntivazione indicatori (CTX_BS)', 'admin', now(), now(), now(), now());

INSERT INTO content_attribute (content_id, attr_name, attr_value,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('GP_MENU_00571', 'title', 'MenuUiLabels.Consuntivazione indicatori', now(), now(), now(), now()),
       ('GP_MENU_00571', 'link',  '/consuntCtxBs', now(), now(), now(), now());

INSERT INTO content_assoc (content_id, content_id_to, content_assoc_type_id, from_date, sequence_num, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('GP_MENU_00402', 'GP_MENU_00571', 'TREE_CHILD', TIMESTAMP '2026-01-01 00:00:00', 3, 'admin', now(), now(), now(), now());

COMMIT;
