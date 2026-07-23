-- =============================================================================
-- AUTO-REFRESH: score columns su work_effort + mv_premiali_export
-- =============================================================================
-- Questo script installa i meccanismi per tenere sempre aggiornati:
--   1. Le colonne score_ep / score_bs / adjusted_* / overall_ep_bs_score
--      sulla tabella work_effort.
--   2. La vista materializzata mv_premiali_export.
--
-- Prerequisiti:
--   - mv_premiali_export.sql già eseguito (colonne DDL + MV creata)
--   - Estensione pg_cron disponibile (ALTER SYSTEM SET shared_preload_libraries)
--
-- Meccanismo: pg_cron (schedulato)
--   Aggiorna i punteggi e fa il refresh della MV ogni ora.
-- =============================================================================


-- =============================================================================
-- FUNZIONE COMUNE: ricalcola i punteggi su work_effort
-- =============================================================================
-- Riesegue la stessa logica dello STEP 2 di mv_premiali_export.sql.
-- Può essere chiamata manualmente o da pg_cron.
--
-- Parametro p_work_effort_ids (opzionale):
--   - NULL  → ricalcola TUTTE le schede CTX_EP (full-refresh)
--   - array → ricalcola solo le schede elencate (partial-refresh)
-- =============================================================================

CREATE OR REPLACE FUNCTION fn_refresh_premiali_scores(
    p_work_effort_ids TEXT[] DEFAULT NULL
)
RETURNS void
LANGUAGE plpgsql
AS $$
BEGIN
    WITH ep_score AS (
        SELECT we.work_effort_id,
               SUM(v.tm_amount)::numeric(10,2) AS score_ep
        FROM   work_effort we
        LEFT   JOIN work_effort_trans_all_view v
                    ON v.m_work_effort_id = we.work_effort_id
                   AND v.g_input_enum_id  = 'ACCINP_OBJ'
        WHERE  we.work_effort_type_id = 'CTX_EP'
          -- Se viene passata una lista di id, filtra solo quelli
          AND  (p_work_effort_ids IS NULL OR we.work_effort_id = ANY(p_work_effort_ids))
        GROUP  BY we.work_effort_id
    ),
    bs_per_org AS (
        SELECT org_unit_id,
               COUNT(*)            AS n_bs,
               MIN(work_effort_id) AS bs_work_effort_id
        FROM   work_effort
        WHERE  work_effort_type_id = 'CTX_BS'
          AND  org_unit_id IS NOT NULL
        GROUP  BY org_unit_id
    ),
    bs_score AS (
        SELECT bp.org_unit_id,
               SUM(v.tm_amount)::numeric(10,2) AS score_bs
        FROM   bs_per_org bp
        LEFT   JOIN work_effort_trans_all_view v
                    ON v.m_work_effort_id = bp.bs_work_effort_id
                   AND v.g_input_enum_id  = 'ACCINP_UO'
        WHERE  bp.n_bs = 1
        GROUP  BY bp.org_unit_id
    ),
    calc AS (
        SELECT we.work_effort_id,
               we.etch,
               ep.score_ep,
               bs.score_bs,
               CASE
                   WHEN ep.score_ep IS NULL OR ep.score_ep = 0 THEN NULL
                   WHEN we.etch IN ('SCHEDA 4','SCHEDA 5')
                        THEN ROUND((ep.score_ep / 30.0) * 60, 2)
                   ELSE ROUND((ep.score_ep / 30.0) * 40, 2)
               END AS adjusted_score_ep,
               CASE
                   WHEN bs.score_bs IS NULL THEN NULL
                   WHEN we.etch IN ('SCHEDA 4','SCHEDA 5')
                        THEN ROUND((bs.score_bs / 60.0) * 40, 2)
                   ELSE ROUND(bs.score_bs, 2)
               END AS adjusted_score_bs
        FROM   work_effort we
        JOIN   ep_score ep ON ep.work_effort_id = we.work_effort_id
        LEFT   JOIN bs_score bs ON bs.org_unit_id = we.org_unit_id
        WHERE  we.work_effort_type_id = 'CTX_EP'
    )
    UPDATE work_effort we
    SET    score_ep            = c.score_ep,
           score_bs            = c.score_bs,
           adjusted_score_ep   = c.adjusted_score_ep,
           adjusted_score_bs   = c.adjusted_score_bs,
           overall_ep_bs_score = CASE
                                     WHEN c.adjusted_score_ep IS NULL
                                      AND c.adjusted_score_bs IS NULL THEN NULL
                                     ELSE ROUND(
                                         COALESCE(c.adjusted_score_ep, 0)
                                       + COALESCE(c.adjusted_score_bs, 0), 2)
                                 END
    FROM   calc c
    WHERE  c.work_effort_id = we.work_effort_id;
END;
$$;

COMMENT ON FUNCTION fn_refresh_premiali_scores IS
    'Ricalcola score_ep/score_bs/adjusted_*/overall_ep_bs_score sulle schede CTX_EP. '
    'Passare p_work_effort_ids per aggiornare solo alcune schede. '
    'NULL = full-refresh su tutte le schede CTX_EP.';


-- =============================================================================
-- pg_cron: refresh schedulato
-- =============================================================================
-- Installa due job cron:
--   1. Ogni ora al minuto :00 → ricalcola i punteggi su work_effort
--   2. Ogni ora al minuto :05 → refresha la vista materializzata
--
-- Prerequisiti:
--   CREATE EXTENSION IF NOT EXISTS pg_cron;
--   GRANT USAGE ON SCHEMA cron TO <utente_app>;
--
-- Personalizzare la schedule secondo le esigenze (es. '*/30 * * * *' = ogni 30 minuti).
-- =============================================================================

-- Abilita pg_cron sul database corrente (eseguire una sola volta come superuser)
-- CREATE EXTENSION IF NOT EXISTS pg_cron;

-- Job 1: ricalcola i punteggi sulle colonne di work_effort
SELECT cron.schedule(
    'refresh_premiali_scores',          -- nome del job (univoco)
    '0 * * * *',                        -- ogni ora (al minuto 0)
    $$ SELECT fn_refresh_premiali_scores(); $$
);

-- Job 2: refresha la vista materializzata (5 minuti dopo il job 1)
SELECT cron.schedule(
    'refresh_mv_premiali_export',       -- nome del job (univoco)
    '5 * * * *',                        -- ogni ora (al minuto 5)
    $$ REFRESH MATERIALIZED VIEW CONCURRENTLY mv_premiali_export; $$
);

-- Verifica i job installati:
-- SELECT jobid, jobname, schedule, command, active FROM cron.job;

-- Per rimuovere i job (se si vuole cambiare schedule):
-- SELECT cron.unschedule('refresh_premiali_scores');
-- SELECT cron.unschedule('refresh_mv_premiali_export');



-- =============================================================================
-- REFRESH MANUALE (eseguire a mano quando necessario)
-- =============================================================================

-- Ricalcola tutti i punteggi (full-refresh):
-- SELECT fn_refresh_premiali_scores();

-- Poi refresha la MV:
-- REFRESH MATERIALIZED VIEW CONCURRENTLY mv_premiali_export;

-- Ricalcola solo una singola scheda (debug/test):
-- SELECT fn_refresh_premiali_scores(ARRAY['E14511']);
