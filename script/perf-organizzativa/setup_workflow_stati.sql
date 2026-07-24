-- =============================================================================
-- setup_workflow_stati.sql
-- Workflow 8 stati per CTX_BS (Performance Strategica Cardarelli)
--
-- Crea un nuovo StatusType isolato WE_STATUS_OR_CARD con 8 stati dedicati,
-- le transizioni permesse (StatusValidChange) e il collegamento a CTX_BS
-- tramite WorkEffortTypeStatus + WorkEffortTypeStatusCnt (editabilità folder).
--
-- Flusso:
--   INIT → TOVALIDATE → VALPART ─┐
--                    └──────────→ VALIDATED → TOACCOUNT → ACCOUNTED → REVIEWED → CLOSED
--
-- Pre-requisiti:
--   - WorkEffortType CTX_BS esiste (da WorkEffortTypeData.xml)
--   - ACTSTATUS_PENDING, ACTSTATUS_ACTIVE, ACTSTATUS_CLOSED esistono in status_type
--   - ACTUAL esiste in gl_fiscal_type
--
-- IMPORTANTE — stato iniziale automatico:
--   Il service crudServiceOperation_WorkEffort (services.xml riga ~664) imposta lo stato
--   iniziale di una nuova scheda prendendo il StatusItem con sequenceId più basso del
--   statusTypeId del WorkEffortTypePeriod di CTX_BS con date compatibili e stato OPEN/REOPEN.
--   Affinché una nuova scheda parta da WEORCARD_INIT, ogni WorkEffortTypePeriod di CTX_BS
--   deve avere statusTypeId = 'WE_STATUS_OR_CARD'.
--   VERIFICARE a DB prima dell'esecuzione:
--     SELECT * FROM work_effort_type_period WHERE work_effort_type_id = 'CTX_BS';
--   e aggiornare statusTypeId se necessario (aggiungere UPDATE qui sotto oppure in V003).
--
-- Esecuzione:
--   psql -U postgres -d cardarelli -f V002__perf_organizzativa_workflow_stati.sql
-- =============================================================================

BEGIN;

-- ---------------------------------------------------------------------------
-- 1. StatusType — nuovo tipo workflow specifico Cardarelli per CTX_BS
-- ---------------------------------------------------------------------------

INSERT INTO status_type (
    status_type_id, parent_type_id, has_table, description,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES (
    'WE_STATUS_OR_CARD', 'PERFORMANCE_STATUS', 'N',
    'Scheda Performance Organizzativa Cardarelli',
    NOW(), NOW(), NOW(), NOW()
)
ON CONFLICT (status_type_id) DO NOTHING;


-- ---------------------------------------------------------------------------
-- 2. StatusItem — 8 stati del ciclo di vita
-- ---------------------------------------------------------------------------

INSERT INTO status_item (
    status_id, status_type_id, status_code, sequence_id, description,
    act_st_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('WEORCARD_INIT',       'WE_STATUS_OR_CARD', 'INIT',       '01', 'Inizializzata',         'ACTSTATUS_PENDING', NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_TOVALIDATE', 'WE_STATUS_OR_CARD', 'TOVALIDATE', '02', 'Da validare',            'ACTSTATUS_PENDING', NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_VALPART',    'WE_STATUS_OR_CARD', 'VALPART',    '03', 'Validata parzialmente',  'ACTSTATUS_PENDING', NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_VALIDATED',  'WE_STATUS_OR_CARD', 'VALIDATED',  '04', 'Validata',               'ACTSTATUS_ACTIVE',  NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_TOACCOUNT',  'WE_STATUS_OR_CARD', 'TOACCOUNT',  '05', 'Da consuntivare',        'ACTSTATUS_ACTIVE',  NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_ACCOUNTED',  'WE_STATUS_OR_CARD', 'ACCOUNTED',  '06', 'Consuntivata',           'ACTSTATUS_ACTIVE',  NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_REVIEWED',   'WE_STATUS_OR_CARD', 'REVIEWED',   '07', 'Visionata',              'ACTSTATUS_ACTIVE',  NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_CLOSED',     'WE_STATUS_OR_CARD', 'CLOSED',     '08', 'Chiusa',                 'ACTSTATUS_CLOSED',  NOW(), NOW(), NOW(), NOW())
ON CONFLICT (status_id) DO NOTHING;


-- ---------------------------------------------------------------------------
-- 3. StatusValidChange — transizioni permesse
--    INIT → TOVALIDATE (obiettivi assegnati)
--    TOVALIDATE → VALPART (validazione Direttore UO)
--    TOVALIDATE → VALIDATED (validazione diretta, senza passare da VALPART)
--    VALPART → VALIDATED (validazione Direttore Sanitario/Amm.)
--    VALIDATED → TOACCOUNT (apertura consuntivazione)
--    TOACCOUNT → ACCOUNTED (conferma amministratore)
--    ACCOUNTED → REVIEWED (presa visione Direttore UO)
--    REVIEWED → CLOSED (firma amministratore)
-- ---------------------------------------------------------------------------

INSERT INTO status_valid_change (
    status_id, status_id_to, transition_name,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('WEORCARD_INIT',       'WEORCARD_TOVALIDATE', 'Proponi per validazione',   NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_TOVALIDATE', 'WEORCARD_VALPART',    'Valida parzialmente',       NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_TOVALIDATE', 'WEORCARD_VALIDATED',  'Valida',                    NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_VALPART',    'WEORCARD_VALIDATED',  'Valida',                    NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_VALIDATED',  'WEORCARD_TOACCOUNT',  'Apri consuntivazione',      NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_TOACCOUNT',  'WEORCARD_ACCOUNTED',  'Consuntiva',                NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_ACCOUNTED',  'WEORCARD_REVIEWED',   'Prendi visione',            NOW(), NOW(), NOW(), NOW()),
    ('WEORCARD_REVIEWED',   'WEORCARD_CLOSED',     'Chiudi',                    NOW(), NOW(), NOW(), NOW())
ON CONFLICT (status_id, status_id_to) DO NOTHING;


-- ---------------------------------------------------------------------------
-- 4. WorkEffortTypeStatus — collega CTX_BS a ciascuno stato
--    (CTX_BS = Performance Strategica, accessibile via menu per test immediato)
--    glFiscalTypeId = ACTUAL per tutti (il tipo rilevazione standard)
--    nextStatusId = transizione principale di avanzamento
-- ---------------------------------------------------------------------------

INSERT INTO work_effort_type_status (
    work_effort_type_root_id, current_status_id,
    gl_fiscal_type_id, next_status_id,
    ctrl_score_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('CTX_BS', 'WEORCARD_INIT',       'ACTUAL', 'WEORCARD_TOVALIDATE', 'CTRL_SCORE_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_TOVALIDATE', 'ACTUAL', 'WEORCARD_VALIDATED',  'CTRL_SCORE_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_VALPART',    'ACTUAL', 'WEORCARD_VALIDATED',  'CTRL_SCORE_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_VALIDATED',  'ACTUAL', 'WEORCARD_TOACCOUNT',  'CTRL_SCORE_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_TOACCOUNT',  'ACTUAL', 'WEORCARD_ACCOUNTED',  'CTRL_SCORE_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_ACCOUNTED',  'ACTUAL', 'WEORCARD_REVIEWED',   'CTRL_SCORE_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_REVIEWED',   'ACTUAL', 'WEORCARD_CLOSED',     'CTRL_SCORE_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_CLOSED',     'ACTUAL', NULL,                  'CTRL_SCORE_NONE', NOW(), NOW(), NOW(), NOW())
ON CONFLICT (current_status_id, work_effort_type_root_id) DO NOTHING;


-- ---------------------------------------------------------------------------
-- 5. WorkEffortTypeStatusCnt — editabilità folder per stato su CTX_BS
--
--    Folder CTX_BS (Performance Strategica, verificati da orgperf/workeffortext):
--      WEFLD_MAIN    = tab Dettaglio (dati scheda)
--      WEFLD_ORGUNIT = tab UO
--      WEFLD_WROLE   = tab Ruoli
--      WEFLD_WEFROM  = tab Obiettivi collegati (associazioni da)
--      WEFLD_NOTE    = tab Note
--      WEFLD_REVIEW  = tab Revisioni
--      WEFLD_ELAB    = tab Indicatori KPI (WorkEffortMeasureKpiFolderScreen)
--      WEFLD_AIND    = tab Transazioni (inserimento valori consuntivi)
--
--    ctrlAmountEnumId:
--      ONLY_OPEN   = editabile
--      AMOUNT_NONE = read-only
--
--    Regola:
--      INIT, TOVALIDATE, VALPART → tutto editabile (ONLY_OPEN)
--      VALIDATED in poi          → read-only (AMOUNT_NONE), tranne:
--      TOACCOUNT                 → solo WEFLD_ELAB e WEFLD_AIND editabili
-- ---------------------------------------------------------------------------

-- Fasi editabili (INIT, TOVALIDATE, VALPART)
INSERT INTO work_effort_type_status_cnt (
    work_effort_type_id, status_id, content_id, to_post, ctrl_amount_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT 'CTX_BS', s.status_id, f.content_id, 'Y', 'ONLY_OPEN',
       NOW(), NOW(), NOW(), NOW()
FROM (VALUES
    ('WEORCARD_INIT'),
    ('WEORCARD_TOVALIDATE'),
    ('WEORCARD_VALPART')
) AS s(status_id)
CROSS JOIN (VALUES
    ('WEFLD_MAIN'),
    ('WEFLD_ORGUNIT'),
    ('WEFLD_WROLE'),
    ('WEFLD_WEFROM'),
    ('WEFLD_NOTE'),
    ('WEFLD_REVIEW'),
    ('WEFLD_ELAB'),
    ('WEFLD_AIND')
) AS f(content_id)
ON CONFLICT (work_effort_type_id, status_id, content_id) DO NOTHING;

-- VALIDATED: tutto read-only
INSERT INTO work_effort_type_status_cnt (
    work_effort_type_id, status_id, content_id, to_post, ctrl_amount_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT 'CTX_BS', 'WEORCARD_VALIDATED', f.content_id, 'Y', 'AMOUNT_NONE',
       NOW(), NOW(), NOW(), NOW()
FROM (VALUES
    ('WEFLD_MAIN'),
    ('WEFLD_ORGUNIT'),
    ('WEFLD_WROLE'),
    ('WEFLD_WEFROM'),
    ('WEFLD_NOTE'),
    ('WEFLD_REVIEW'),
    ('WEFLD_ELAB'),
    ('WEFLD_AIND')
) AS f(content_id)
ON CONFLICT (work_effort_type_id, status_id, content_id) DO NOTHING;

-- TOACCOUNT: solo indicatori e transazioni editabili, il resto read-only
INSERT INTO work_effort_type_status_cnt (
    work_effort_type_id, status_id, content_id, to_post, ctrl_amount_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
VALUES
    ('CTX_BS', 'WEORCARD_TOACCOUNT', 'WEFLD_MAIN',    'Y', 'AMOUNT_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_TOACCOUNT', 'WEFLD_ORGUNIT', 'Y', 'AMOUNT_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_TOACCOUNT', 'WEFLD_WROLE',   'Y', 'AMOUNT_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_TOACCOUNT', 'WEFLD_WEFROM',  'Y', 'AMOUNT_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_TOACCOUNT', 'WEFLD_NOTE',    'Y', 'AMOUNT_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_TOACCOUNT', 'WEFLD_REVIEW',  'Y', 'AMOUNT_NONE', NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_TOACCOUNT', 'WEFLD_ELAB',    'Y', 'ONLY_OPEN',   NOW(), NOW(), NOW(), NOW()),
    ('CTX_BS', 'WEORCARD_TOACCOUNT', 'WEFLD_AIND',    'Y', 'ONLY_OPEN',   NOW(), NOW(), NOW(), NOW())
ON CONFLICT (work_effort_type_id, status_id, content_id) DO NOTHING;

-- ACCOUNTED, REVIEWED, CLOSED: tutto read-only
INSERT INTO work_effort_type_status_cnt (
    work_effort_type_id, status_id, content_id, to_post, ctrl_amount_enum_id,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT 'CTX_BS', s.status_id, f.content_id, 'Y', 'AMOUNT_NONE',
       NOW(), NOW(), NOW(), NOW()
FROM (VALUES
    ('WEORCARD_ACCOUNTED'),
    ('WEORCARD_REVIEWED'),
    ('WEORCARD_CLOSED')
) AS s(status_id)
CROSS JOIN (VALUES
    ('WEFLD_MAIN'),
    ('WEFLD_ORGUNIT'),
    ('WEFLD_WROLE'),
    ('WEFLD_WEFROM'),
    ('WEFLD_NOTE'),
    ('WEFLD_REVIEW'),
    ('WEFLD_ELAB'),
    ('WEFLD_AIND')
) AS f(content_id)
ON CONFLICT (work_effort_type_id, status_id, content_id) DO NOTHING;


-- ---------------------------------------------------------------------------
-- 6. Bonifica schede esistenti CTX_BS — migrazione al nuovo workflow
--
--    Le 139 schede create prima di V002 hanno current_status_id = WEPERFST_EXECPEND
--    (stato del vecchio workflow generico). Le migriamo a WEORCARD_TOVALIDATE
--    perché risultano già configurate con obiettivi e pronte per la validazione.
--
--    Logica di migrazione:
--      WEPERFST_EXECPEND → WEORCARD_TOVALIDATE  (schede attive con obiettivi)
--
--    Aggiorna anche work_effort_status per tracciabilità della transizione.
-- ---------------------------------------------------------------------------

-- 6a. Aggiorna lo stato corrente delle schede
UPDATE work_effort
SET current_status_id       = 'WEORCARD_TOVALIDATE',
    last_status_update      = NOW(),
    last_updated_stamp      = NOW(),
    last_updated_tx_stamp   = NOW()
WHERE work_effort_type_id   = 'CTX_BS'
  AND current_status_id     = 'WEPERFST_EXECPEND';

-- 6b. Registra la transizione nella storia degli stati
INSERT INTO work_effort_status (
    work_effort_id, status_id, status_datetime,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT
    we.work_effort_id,
    'WEORCARD_TOVALIDATE',
    NOW(),
    NOW(), NOW(), NOW(), NOW()
FROM work_effort we
WHERE we.work_effort_type_id = 'CTX_BS'
  AND we.current_status_id   = 'WEORCARD_TOVALIDATE'
ON CONFLICT DO NOTHING;


COMMIT;


-- =============================================================================
-- ROLLBACK (commentato — eseguire solo in caso di ripristino)
-- =============================================================================
--
-- BEGIN;
--
-- DELETE FROM work_effort_type_status_cnt
-- WHERE work_effort_type_id = 'CTX_BS'
--   AND status_id LIKE 'WEORCARD_%';
--
-- DELETE FROM work_effort_type_status
-- WHERE work_effort_type_root_id = 'CTX_BS'
--   AND current_status_id LIKE 'WEORCARD_%';
--
-- DELETE FROM status_valid_change
-- WHERE status_id LIKE 'WEORCARD_%';
--
-- DELETE FROM status_item
-- WHERE status_type_id = 'WE_STATUS_OR_CARD';
--
-- DELETE FROM status_type
-- WHERE status_type_id = 'WE_STATUS_OR_CARD';
--
-- -- Ripristina stato originale schede migrate (sezione 6)
-- UPDATE work_effort
-- SET current_status_id = 'WEPERFST_EXECPEND',
--     last_updated_stamp = NOW(), last_updated_tx_stamp = NOW()
-- WHERE work_effort_type_id = 'CTX_BS'
--   AND current_status_id   = 'WEORCARD_TOVALIDATE';
--
-- DELETE FROM work_effort_status
-- WHERE status_id = 'WEORCARD_TOVALIDATE';
--
-- COMMIT;
