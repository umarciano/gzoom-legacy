-- =============================================================================
-- check_schede_ctx_bs_vs_uoc.sql
-- Verifica: esiste una scheda CTX_BS per ogni UOC presente in DB?
--
-- Esecuzione:
--   psql -U postgres -d cardarelli -f check_schede_ctx_bs_vs_uoc.sql
-- =============================================================================

-- ---------------------------------------------------------------------------
-- 1. Schede CTX_BS esistenti con la loro UO associata
-- ---------------------------------------------------------------------------
SELECT
    we.work_effort_id         AS scheda_id,
    we.work_effort_name       AS scheda_nome,
    we.current_status_id      AS stato,
    we.org_unit_id            AS uo_party_id,
    p.party_name              AS uo_nome,
    ppr.role_code             AS uo_codice_cdc
FROM work_effort we
LEFT JOIN party p          ON p.party_id = we.org_unit_id
LEFT JOIN party_parent_role ppr
    ON ppr.party_id   = we.org_unit_id
    AND ppr.role_type_id = 'ORGANIZATION_UNIT'
WHERE we.we_context_id = 'CTX_BS'
  AND we.work_effort_type_id IS NOT NULL
  AND we.work_effort_type_id NOT IN ('CTX_BS')   -- escludi root di tipo, tieni solo schede reali
ORDER BY uo_codice_cdc;


-- ---------------------------------------------------------------------------
-- 2. UOC presenti in DB (Party con ruolo UOC o ORGANIZATION_UNIT)
--    confrontate con le schede esistenti
-- ---------------------------------------------------------------------------
SELECT
    ppr.role_code             AS codice_cdc,
    p.party_name              AS nome_uo,
    rt.description            AS tipo_uo,
    CASE WHEN we.org_unit_id IS NOT NULL THEN 'SI' ELSE 'NO' END AS ha_scheda_ctx_bs
FROM party p
JOIN party_role pr          ON pr.party_id = p.party_id
JOIN role_type rt            ON rt.role_type_id = pr.role_type_id
                            AND rt.parent_type_id = 'ORGANIZATION_UNIT'
LEFT JOIN party_parent_role ppr
    ON ppr.party_id = p.party_id
    AND ppr.role_type_id = 'ORGANIZATION_UNIT'
LEFT JOIN (
    SELECT DISTINCT org_unit_id
    FROM work_effort
    WHERE we_context_id = 'CTX_BS'
) we ON we.org_unit_id = p.party_id
WHERE p.status_id = 'PARTY_ENABLED'
ORDER BY ha_scheda_ctx_bs, codice_cdc;
