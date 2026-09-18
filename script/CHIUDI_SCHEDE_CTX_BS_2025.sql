-- =============================================================================
-- CHIUDI_SCHEDE_CTX_BS_2025.sql
-- Porta in stato WEORCARD_CLOSED ("Chiusa") tutte le schede CTX_BS
-- del ciclo 2025 che non lo siano già.
--
-- Criteri di selezione:
--   - work_effort_type_id = 'CTX_BS'         (schede radice performance strategica)
--   - estimated_start_date nel range 2025     (ciclo annuale 2025)
--   - current_status_id <> 'WEORCARD_CLOSED' (idempotente: non ritocca già-chiuse)
--
-- Nota sul range date: le date sono memorizzate come timestamp UTC.
-- La mezzanotte italiana del 01/01/2025 = 2024-12-31 23:00:00 UTC, quindi
-- il limite inferiore parte da '2024-12-31' per catturare correttamente le schede.
--
-- Azioni eseguite:
--   1. Verifica (SELECT): mostra le schede che saranno chiuse
--   2. UPDATE  work_effort.current_status_id  -> WEORCARD_CLOSED
--   3. INSERT  work_effort_status             -> riga storico
--
-- Esecuzione:
--   psql -h <host> -U postgres -d cardarelli -v ON_ERROR_STOP=1 \
--        -f CHIUDI_SCHEDE_CTX_BS_2025.sql
-- =============================================================================


-- =============================================================================
-- 0. VERIFICA PREVENTIVA — schede che saranno chiuse
--    (esegui separatamente prima del blocco BEGIN/COMMIT per conferma)
-- =============================================================================

SELECT
    we.work_effort_id         AS scheda_id,
    we.source_reference_id    AS codice_scheda,
    we.work_effort_name       AS nome_scheda,
    we.current_status_id      AS stato_attuale,
    si.description            AS stato_descrizione,
    we.estimated_start_date   AS data_inizio,
    we.estimated_completion_date AS data_fine,
    p.party_name              AS uo_nome
FROM work_effort we
LEFT JOIN status_item si ON si.status_id = we.current_status_id
LEFT JOIN party p         ON p.party_id  = we.org_unit_id
WHERE we.work_effort_type_id = 'CTX_BS'
  AND we.estimated_start_date >= '2024-12-31'
  AND we.estimated_start_date  < '2026-01-01'
  AND we.current_status_id <> 'WEORCARD_CLOSED'
ORDER BY we.source_reference_id;


-- =============================================================================
-- 1. CHIUSURA — aggiorna stato e registra nel log storico
-- =============================================================================

BEGIN;

-- 1a. Aggiorna current_status_id su work_effort
UPDATE work_effort
SET current_status_id    = 'WEORCARD_CLOSED',
    last_status_update   = NOW(),
    last_updated_stamp   = NOW(),
    last_updated_tx_stamp = NOW()
WHERE work_effort_type_id = 'CTX_BS'
  AND estimated_start_date >= '2024-12-31'
  AND estimated_start_date  < '2026-01-01'
  AND current_status_id <> 'WEORCARD_CLOSED';

-- 1b. Inserisce riga storico per ogni scheda appena chiusa
INSERT INTO work_effort_status (
    work_effort_id, status_id, status_datetime,
    created_stamp, created_tx_stamp, last_updated_stamp, last_updated_tx_stamp
)
SELECT
    we.work_effort_id,
    'WEORCARD_CLOSED',
    NOW(),
    NOW(), NOW(), NOW(), NOW()
FROM work_effort we
WHERE we.work_effort_type_id = 'CTX_BS'
  AND we.estimated_start_date >= '2024-12-31'
  AND we.estimated_start_date  < '2026-01-01'
  AND we.current_status_id = 'WEORCARD_CLOSED'
ON CONFLICT DO NOTHING;

COMMIT;


-- =============================================================================
-- 2. VERIFICA FINALE — schede ora in stato Chiusa
-- =============================================================================

SELECT
    we.work_effort_id         AS scheda_id,
    we.source_reference_id    AS codice_scheda,
    we.work_effort_name       AS nome_scheda,
    we.current_status_id      AS stato_attuale,
    we.last_status_update     AS aggiornato_il,
    p.party_name              AS uo_nome
FROM work_effort we
LEFT JOIN party p ON p.party_id = we.org_unit_id
WHERE we.work_effort_type_id = 'CTX_BS'
  AND we.estimated_start_date >= '2024-12-31'
  AND we.estimated_start_date  < '2026-01-01'
  AND we.current_status_id = 'WEORCARD_CLOSED'
ORDER BY we.source_reference_id;
