-- =====================================================================
-- POST-IMPORT: assegnazione automatica profili (membership) derivata dai dati
-- =====================================================================
-- Da eseguire DOPO l'import (servono soggetti + relazioni ORG_RESPONSIBLE) e DOPO
-- SETUP_PERF_ORGANIZZATIVA.sql (che crea gruppi/permessi: strutture).
-- Qui stanno SOLO le assegnazioni utente->gruppo, auto-derivate dalla responsabilita':
--   A) STRATPERF_REFERENTE  -> responsabili (ORG_RESPONSIBLE) delle UOC-referente (indicatori WEM_IND_IN_CHARGE)
--   B) STRATPERF_DIR_UO     -> responsabili (ORG_RESPONSIBLE) delle UO con scheda CTX_BS (per validare la propria scheda)
-- Idempotente (WHERE NOT EXISTS). Rieseguibile a ogni re-import.
-- =====================================================================

-- ---------- A) REFERENTI (44) ----------
INSERT INTO user_login_security_group (user_login_id, group_id, from_date,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT DISTINCT ul.user_login_id, 'STRATPERF_REFERENTE', TIMESTAMP '2026-01-01 00:00:00',
       now(), now(), now(), now()
FROM gl_account_role gar
JOIN party_relationship pr ON pr.party_id_from = gar.party_id
   AND pr.party_relationship_type_id = 'ORG_RESPONSIBLE'
   AND (pr.thru_date IS NULL OR pr.thru_date > now())
JOIN user_login ul ON ul.party_id = pr.party_id_to
WHERE gar.role_type_id = 'WEM_IND_IN_CHARGE'
  AND (gar.thru_date IS NULL OR gar.thru_date > now())
  AND NOT EXISTS (SELECT 1 FROM user_login_security_group x
                  WHERE x.user_login_id = ul.user_login_id AND x.group_id = 'STRATPERF_REFERENTE' AND x.thru_date IS NULL);

-- ---------- B) DIRETTORI UO (85) ----------
INSERT INTO user_login_security_group (user_login_id, group_id, from_date,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT DISTINCT ul.user_login_id, 'STRATPERF_DIR_UO', TIMESTAMP '2026-01-01 00:00:00',
       now(), now(), now(), now()
FROM work_effort we
JOIN party_relationship pr ON pr.party_id_from = we.org_unit_id
   AND pr.party_relationship_type_id = 'ORG_RESPONSIBLE'
   AND (pr.thru_date IS NULL OR pr.thru_date > now())
JOIN user_login ul ON ul.party_id = pr.party_id_to
WHERE we.work_effort_type_id = 'CTX_BS' AND we.org_unit_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM user_login_security_group x
                  WHERE x.user_login_id = ul.user_login_id AND x.group_id = 'STRATPERF_DIR_UO' AND x.thru_date IS NULL);

-- ---------- Verifica ----------
SELECT group_id, count(*) AS membri
FROM user_login_security_group
WHERE group_id IN ('STRATPERF_REFERENTE','STRATPERF_DIR_UO') AND thru_date IS NULL
GROUP BY group_id ORDER BY group_id;
