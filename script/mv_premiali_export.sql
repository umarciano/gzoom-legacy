
-- =============================================================================
-- STEP 1 - CREAZIONE CAMPI TABELLA WORK_EFFORT
-- =============================================================================

-- Script DDL: aggiunta colonne di punteggio sulla tabella WORK_EFFORT
-- =============================================================================
-- Le colonne valgono per le sole schede individuali (work_effort_type_id = 'CTX_EP').
-- Sulle schede organizzative (CTX_BS) e altri tipi resteranno NULL.
--
-- Riparametrazione (basata su work_effort.etch della scheda individuale):
--   - SCHEDA 4 / SCHEDA 5  : individuale  (score_ep / 30) * 60       -> 0..60
--                            organizzativa (score_bs / 60) * 40      -> 0..40
--   - altre schede         : individuale  (score_ep / 30) * 40       -> 0..40
--                            organizzativa  score_bs                 -> 0..60
--   In ogni caso overall_ep_bs_score = adjusted_score_ep + adjusted_score_bs
--   (max 100).
--
-- Idempotente: usa IF NOT EXISTS, può essere rilanciato senza effetti.
-- =============================================================================

ALTER TABLE work_effort
    ADD COLUMN IF NOT EXISTS score_ep            NUMERIC(10,2),
    ADD COLUMN IF NOT EXISTS score_bs            NUMERIC(10,2),
    ADD COLUMN IF NOT EXISTS adjusted_score_ep   NUMERIC(10,2),
    ADD COLUMN IF NOT EXISTS adjusted_score_bs   NUMERIC(10,2),
    ADD COLUMN IF NOT EXISTS overall_ep_bs_score NUMERIC(10,2);

COMMENT ON COLUMN work_effort.score_ep
    IS 'Performance individuale grezza (0..30): SUM(acctg_trans_entry.amount) sugli indicatori della scheda CTX_EP (join via work_effort_measure.work_effort_measure_id = acctg_trans_entry.voucher_ref, gl_account.input_enum_id = ''ACCINP_OBJ'', escluso gl_account_id = ''SCOREKPI'').';

COMMENT ON COLUMN work_effort.score_bs
    IS 'Performance organizzativa grezza (0..60): SUM(acctg_trans_entry.amount) sugli indicatori della scheda CTX_BS associata alla stessa org_unit_id. NULL se per quella org_unit_id esistono 0 oppure piu'' di una scheda CTX_BS.';

COMMENT ON COLUMN work_effort.adjusted_score_ep
    IS 'Performance individuale riparametrata: SCHEDA 4/5 -> (score_ep/30)*60; altre schede -> (score_ep/30)*40. NULL se score_ep e'' NULL o 0.';

COMMENT ON COLUMN work_effort.adjusted_score_bs
    IS 'Performance organizzativa riparametrata: SCHEDA 4/5 -> (score_bs/60)*40; altre schede -> score_bs. NULL se score_bs e'' NULL.';

COMMENT ON COLUMN work_effort.overall_ep_bs_score
    IS 'Punteggio complessivo = somma di adjusted_score_ep e adjusted_score_bs (max 100). Se uno dei due e'' NULL viene riportato solo l''altro; e'' NULL solo se entrambi sono NULL.';



-- =============================================================================
-- STEP 2 - BACKFILL COLONNE TABELLA WORK_EFFORT
-- =============================================================================
-- =============================================================================
-- Backfill colonne score_ep / score_bs / adjusted_* / overall_ep_bs_score
-- sulle schede individuali (work_effort_type_id = 'CTX_EP').
-- =============================================================================
-- Prerequisito: aver eseguito alter_work_effort_add_score_columns.sql.
--
-- Sorgente unica: vista WORK_EFFORT_TRANS_ALL_VIEW (la stessa usata internamente
-- da GZOOM/BIRT). Gestisce nativamente i due meccanismi di legame:
--   * CTX_EP: ate.voucher_ref = work_effort_measure_id (ACCINP_OBJ)
--   * CTX_BS: ate.gl_account_id = wm.gl_account_id su stessa org_unit (ACCINP_UO)
-- Esclude gia' SCOREKPI ed e' vincolata a g.b_organization_party_id = b.organization_id.
--
-- Logica:
--   score_ep         : SUM(tm_amount) sulle transazioni della scheda CTX_EP
--                      con g_input_enum_id = 'ACCINP_OBJ'.
--   score_bs         : SUM(tm_amount) sulle transazioni della CTX_BS associata
--                      alla stessa org_unit_id, con g_input_enum_id = 'ACCINP_UO'.
--                      Se per quell'' org_unit_id esistono 0 oppure piu'' di una
--                      CTX_BS, score_bs (e adjusted_score_bs) restano NULL.
--   adjusted_score_ep: SCHEDA 4/5 -> (score_ep/30)*60; altre -> (score_ep/30)*40
--   adjusted_score_bs: SCHEDA 4/5 -> (score_bs/60)*40; altre -> score_bs
--   overall_ep_bs_score: somma di adjusted_score_ep e adjusted_score_bs (con
--                      COALESCE: se uno e'' NULL viene riportato solo l''altro;
--                      e'' NULL solo se entrambi sono NULL).
--
-- Lo script e'' rieseguibile.
-- =============================================================================

-- 1) score_ep grezzo per ciascuna scheda individuale (CTX_EP)
WITH ep_score AS (
    SELECT we.work_effort_id,
           SUM(v.tm_amount)::numeric(10,2) AS score_ep
    FROM   work_effort we
    LEFT   JOIN work_effort_trans_all_view v
                ON v.m_work_effort_id = we.work_effort_id
               AND v.g_input_enum_id  = 'ACCINP_OBJ'
    WHERE  we.work_effort_type_id = 'CTX_EP'
    GROUP  BY we.work_effort_id
),
-- 2) per ogni org_unit_id: quante CTX_BS esistono e (se esattamente 1) il suo id
bs_per_org AS (
    SELECT org_unit_id,
           COUNT(*)            AS n_bs,
           MIN(work_effort_id) AS bs_work_effort_id
    FROM   work_effort
    WHERE  work_effort_type_id = 'CTX_BS'
      AND  org_unit_id IS NOT NULL
    GROUP  BY org_unit_id
),
-- 3) score_bs grezzo: solo dove n_bs = 1
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
-- 4) calcoli finali per ogni scheda individuale
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


-- Query di verifica (eseguibili a mano, NON modificano dati)

-- Singola scheda nota:
--   SELECT work_effort_id, etch, org_unit_id,
--          score_ep, score_bs,
--          adjusted_score_ep, adjusted_score_bs, overall_ep_bs_score
--   FROM   work_effort WHERE work_effort_id = 'E14511';
--
-- Distribuzione popolamento:
--   SELECT etch,
--          COUNT(*)                    AS totali,
--          COUNT(score_ep)             AS con_score_ep,
--          COUNT(score_bs)             AS con_score_bs,
--          COUNT(overall_ep_bs_score)  AS con_overall
--   FROM   work_effort
--   WHERE  work_effort_type_id = 'CTX_EP'
--   GROUP  BY etch
--   ORDER  BY etch;
--
-- Schede con CTX_BS ambigua (score_bs lasciato a NULL per via di n_bs <> 1):
--   SELECT we.work_effort_id, we.org_unit_id, bp.n_bs
--   FROM   work_effort we
--   LEFT   JOIN (
--           SELECT org_unit_id, COUNT(*) AS n_bs
--           FROM   work_effort
--           WHERE  work_effort_type_id = 'CTX_BS' AND org_unit_id IS NOT NULL
--           GROUP  BY org_unit_id
--   ) bp ON bp.org_unit_id = we.org_unit_id
--   WHERE  we.work_effort_type_id = 'CTX_EP'
--     AND  (bp.n_bs IS NULL OR bp.n_bs > 1)
--   LIMIT  50;


-- =============================================================================
-- STEP 3 - CREAZIONE VISTA MATERIALIZZATA
-- =============================================================================
-- Vista materializzata: mv_premiali_export
-- Contiene i dati di tutte le schede CTX_EP con anagrafica dipendente,
-- unità organizzativa e punteggi di performance.
-- Stessa logica della API GET /rest/api/premiali/export.
--
-- Prerequisiti:
--   - Estensione pg_cron installata (per il refresh automatico giornaliero)
--     CREATE EXTENSION IF NOT EXISTS pg_cron;
--   - Utente postgres (o l'utente dell'app) deve avere accesso a cron.job
--
-- Refresh manuale:
--   REFRESH MATERIALIZED VIEW CONCURRENTLY mv_premiali_export;
-- =============================================================================
 
-- 1) Crea la vista materializzata
DROP MATERIALIZED VIEW IF EXISTS mv_premiali_export;

CREATE MATERIALIZED VIEW mv_premiali_export AS
SELECT
    we.work_effort_id,
    we.work_effort_name,
    we.work_effort_type_id,
    we.etch                                      AS tipologia_scheda,
    EXTRACT(YEAR FROM we.estimated_start_date)::int::text AS anno_valutazione,
    p.party_id,
    emp.parent_role_code                         AS matricola,
    p.fiscal_code                                AS codice_fiscale,
    per.first_name                               AS nome,
    per.last_name                                AS cognome,
    we.org_unit_id,
    uo.parent_role_code                          AS codice_unita_organizzativa,
    org.party_name                               AS descrizione_unita_organizzativa,
    ruolo.descrizione_ruolo                      AS ruolo_scheda,
    we.scheduled_start_date                      AS data_inizio_valutazione,
    we.scheduled_completion_date                 AS data_fine_valutazione,
    we.score_ep 								 AS performance_individuale_orig,
    we.score_bs 								 AS performance_organizzativa_orig,
    we.adjusted_score_ep 						 AS performance_individuale,
    we.adjusted_score_bs 						 AS performance_organizzativa,
    we.overall_ep_bs_score 						 AS performance_complessiva,
	(select description from status_item WHERE status_id = we.current_status_id) AS stato_scheda
FROM work_effort we
JOIN work_effort_party_assignment wpa
     ON wpa.work_effort_id = we.work_effort_id
    AND wpa.role_type_id   = 'WEM_EVAL_IN_CHARGE'
JOIN party p
     ON p.party_id = wpa.party_id
LEFT JOIN person per
     ON per.party_id = p.party_id
LEFT JOIN LATERAL (
    SELECT ppr.parent_role_code
    FROM   party_parent_role ppr
    WHERE  ppr.party_id          = p.party_id
      AND  ppr.role_type_id      = 'EMPLOYEE'
      AND  ppr.parent_role_code IS NOT NULL
    ORDER  BY ppr.parent_role_code
    LIMIT  1
) emp ON TRUE
LEFT JOIN party org
     ON org.party_id = we.org_unit_id
LEFT JOIN LATERAL (
    SELECT ppr.parent_role_code
    FROM   party_parent_role ppr
    WHERE  ppr.party_id          = we.org_unit_id
      AND  ppr.role_type_id      = 'ORGANIZATION_UNIT'
      AND  ppr.parent_role_code IS NOT NULL
    LIMIT  1
) uo ON TRUE
-- Campo "Profilo professionale / Incarico" (ruolo_scheda): stessa logica
-- della scheda di valutazione BIRT (SchedaObiettiviOrganizzativi.rptdesign).
-- COALESCE a cascata sulla persona valutata (p.party_id = WEM_EVAL_IN_CHARGE):
--   1) ruolo STORICO da party_history valido nel periodo della scheda
--      (empl_position_type.description);
--   2) fallback: ruolo ATTUALE da party_role (role_type.description),
--      escludendo i ruoli di valutazione WEM_EVAL_*.
LEFT JOIN LATERAL (
    SELECT COALESCE(
        (SELECT ept.description
         FROM   party_history ph
         JOIN   empl_position_type ept ON ept.empl_position_type_id = ph.empl_position_type_id
         WHERE  ph.party_id  = p.party_id
           AND  ph.from_date <= we.estimated_completion_date
           AND  (ph.thru_date IS NULL OR ph.thru_date >= we.estimated_start_date)
         ORDER  BY ph.from_date DESC
         LIMIT  1),
        (SELECT rt.description
         FROM   party_role pr
         JOIN   role_type rt ON rt.role_type_id = pr.role_type_id
         WHERE  pr.party_id = p.party_id
           AND  pr.role_type_id NOT LIKE 'WEM_EVAL_%'
         LIMIT  1)
    ) AS descrizione_ruolo
) ruolo ON TRUE
WHERE we.work_effort_type_id      = 'CTX_EP'
    AND we.work_effort_revision_id IS NULL
	AND we.current_status_id in ('WEEVALST_EXECFINAL', 'WEEVALST_NOTEVAL')
    -- Allineamento semantica legacy:
    -- la query storica includeva solo schede completamente comprese nell'anno richiesto
    -- (estimated_start_date >= YYYY-01-01 AND estimated_completion_date <= YYYY-12-31).
    -- In vista ciò equivale a mantenere solo righe con start/end nello stesso anno.
    AND we.estimated_start_date IS NOT NULL
    AND we.estimated_completion_date IS NOT NULL
    AND EXTRACT(YEAR FROM we.estimated_start_date) = EXTRACT(YEAR FROM we.estimated_completion_date)
ORDER BY CASE WHEN emp.parent_role_code ~ '^\d+$' THEN emp.parent_role_code::bigint END NULLS LAST,
         CASE WHEN p.party_id ~ '^\d+$' THEN p.party_id::bigint END NULLS LAST,
         we.work_effort_id;
 
-- 2) Indice univoco necessario per REFRESH CONCURRENTLY
--    (permette il refresh senza bloccare le letture)
CREATE UNIQUE INDEX IF NOT EXISTS mv_premiali_export_pk
    ON mv_premiali_export (work_effort_id);
 
-- 3) Indici aggiuntivi per le query più comuni
CREATE INDEX IF NOT EXISTS mv_premiali_export_anno
    ON mv_premiali_export (anno_valutazione);
 
CREATE INDEX IF NOT EXISTS mv_premiali_export_matricola
    ON mv_premiali_export (matricola);
 
CREATE INDEX IF NOT EXISTS mv_premiali_export_org_unit
    ON mv_premiali_export (org_unit_id);
    
   
   
 -- =============================================================================
-- STEP 4 (FACOLTATIVO) - QUERY MOCK E VERIFICA DATI
-- =============================================================================  
   
SELECT *
FROM mv_premiali_export ;


-- Query per dati Mock performance
update work_effort set score_bs = 60 where score_bs is null;

UPDATE public.work_effort
SET 
    adjusted_score_bs = CASE
        WHEN etch IN ('SCHEDA 1', 'SCHEDA 1.1', 'SCHEDA 2', 'SCHEDA 3') 
            THEN score_bs  -- già in base 60, invariato
        WHEN etch IN ('SCHEDA 4', 'SCHEDA 5') 
            THEN ROUND((score_bs * 40.0 / 60.0)::numeric, 2)  -- riparametrato a base 40
    END,
    adjusted_score_ep = CASE
        WHEN etch IN ('SCHEDA 1', 'SCHEDA 1.1', 'SCHEDA 2', 'SCHEDA 3') 
            THEN ROUND((score_ep * 40.0 / 30.0)::numeric, 2)  -- riparametrato a base 40
        WHEN etch IN ('SCHEDA 4', 'SCHEDA 5') 
            THEN ROUND((score_ep * 60.0 / 30.0)::numeric, 2)  -- riparametrato a base 60
    END,
    overall_ep_bs_score = CASE
        WHEN etch IN ('SCHEDA 1', 'SCHEDA 1.1', 'SCHEDA 2', 'SCHEDA 3') 
            THEN ROUND((score_bs + ROUND((score_ep * 40.0 / 30.0)::numeric, 2))::numeric, 2)
        WHEN etch IN ('SCHEDA 4', 'SCHEDA 5') 
            THEN ROUND((ROUND((score_bs * 40.0 / 60.0)::numeric, 2) + ROUND((score_ep * 60.0 / 30.0)::numeric, 2))::numeric, 2)
    END
WHERE etch IN ('SCHEDA 1', 'SCHEDA 1.1', 'SCHEDA 2', 'SCHEDA 3', 'SCHEDA 4', 'SCHEDA 5')
  AND score_bs IS NOT NULL;