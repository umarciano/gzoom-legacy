-- =====================================================================
-- POST-IMPORT: assegnazione automatica profili (membership) derivata dai dati
-- =====================================================================
-- Da eseguire DOPO l'import (servono soggetti + relazioni ORG_RESPONSIBLE) e DOPO
-- SETUP_PERF_STRATEGICA.sql (che crea gruppi/permessi: strutture).
-- Qui stanno SOLO le assegnazioni utente->gruppo, auto-derivate dalla responsabilita':
--   A) STRATPERF_REFERENTE  -> le PERSONE referente-indicatore (party del ruolo WEM_IND_IN_CHARGE). Modello
--      persona (2026-09-02): il referente e' una singola persona impostata da import, non piu' la UOC.
--   B) STRATPERF_DIR_UO     -> responsabili (ORG_RESPONSIBLE) delle UO con scheda CTX_BS (per validare la propria scheda)
-- Idempotente (WHERE NOT EXISTS). Rieseguibile a ogni re-import.
-- =====================================================================

-- ---------- A) REFERENTI (persone con ruolo WEM_IND_IN_CHARGE) ----------
INSERT INTO user_login_security_group (user_login_id, group_id, from_date,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT DISTINCT ul.user_login_id, 'STRATPERF_REFERENTE', TIMESTAMP '2026-01-01 00:00:00',
       now(), now(), now(), now()
FROM gl_account_role gar
JOIN user_login ul ON ul.party_id = gar.party_id   -- modello persona: il referente E' la persona (party del ruolo)
WHERE gar.role_type_id = 'WEM_IND_IN_CHARGE'
  AND (gar.thru_date IS NULL OR gar.thru_date > now())
  AND NOT EXISTS (SELECT 1 FROM user_login_security_group x
                  WHERE x.user_login_id = ul.user_login_id AND x.group_id = 'STRATPERF_REFERENTE' AND x.thru_date IS NULL);

-- ---------- B) RESPONSABILI UO -> STRATPERF_DIR_UO (per validare la propria scheda) ----------
-- Ogni responsabile (ORG_RESPONSIBLE) di una UO con scheda CTX_BS, QUALUNQUE sia il ruolo di
-- responsabilita' (DIRETTORE_UOC, DIRETTORE_UOSD, DIRIG_MEDICO, DIRETTORE_DIP, PROF_SAN_PREVENZIONE, ...).
-- Il filtro solo 'DIRETTORE_UOC' lasciava fuori chi dirige la propria UO con un ruolo diverso: non entrando
-- in DIR_UO non vedeva/validava la propria scheda in Definizione/Valutazione/Interrogazione.
INSERT INTO user_login_security_group (user_login_id, group_id, from_date,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT DISTINCT ul.user_login_id, 'STRATPERF_DIR_UO', TIMESTAMP '2026-01-01 00:00:00',
       now(), now(), now(), now()
FROM work_effort we
JOIN party_relationship pr ON pr.party_id_from = we.org_unit_id
   AND pr.party_relationship_type_id = 'ORG_RESPONSIBLE'          -- QUALUNQUE role_type_id_to
   AND (pr.thru_date IS NULL OR pr.thru_date > now())
JOIN user_login ul ON ul.party_id = pr.party_id_to
WHERE we.work_effort_type_id = 'CTX_BS' AND we.org_unit_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM user_login_security_group x
                  WHERE x.user_login_id = ul.user_login_id AND x.group_id = 'STRATPERF_DIR_UO' AND x.thru_date IS NULL);

-- Cleanup: i direttori San/Amm NON devono stare in STRATPERF_DIR_UO. Il DIR_UO li renderebbe 'isRole' nella
-- perform-find -> vedrebbero solo le schede a loro assegnate e non potrebbero firmare "Valida" le VALPART
-- altrui. NB: con l'INSERT allargato sopra (qualunque ruolo), un Dir San/Amm che sia anche ORG_RESPONSIBLE
-- di una UO con scheda verrebbe aggiunto: questa DELETE lo rimuove. Idempotente, identica al criterio del §B.
DELETE FROM user_login_security_group
WHERE group_id = 'STRATPERF_DIR_UO' AND thru_date IS NULL
  AND user_login_id IN (
      SELECT user_login_id FROM user_login_security_group
      WHERE group_id IN ('STRATPERF_DIR_SAN','STRATPERF_DIR_AMM') AND thru_date IS NULL);

-- ---------- C) RESPONSABILE-SCHEDA: direttore UO come WEM_PERF_IN_CHARGE sulla propria scheda CTX_BS ----------
-- Necessaria per la VISIBILITA' role-based del direttore (vedi SETUP_PERF_STRATEGICA.sql: sugli stati WEORCARD_*
-- e' impostato manag_we_status_enum_id='ROLE' + management_role_type_id='WEM_PERF_IN_CHARGE').
-- La perform-find gira in isRole=true e mostra la scheda SOLO se l'utente ha questo ruolo assegnato sulla scheda,
-- con thru_date = data fine scheda (join F: A.ESTIMATED_COMPLETION_DATE = F.THRU_DATE). Senza, il direttore non
-- vede nulla in Definizione/Valutazione/Interrogazione. Idempotente. Deriva il direttore dalla relazione
-- ORG_RESPONSIBLE della UO della scheda, QUALUNQUE sia il ruolo (DIRETTORE_UOC, DIRETTORE_UOSD,
-- DIRETTORE_SANITARIO, DIR_AMMINISTRATIVO, DIRETTORE_DIP, PROF_SAN_PREVENZIONE, ecc.). Il filtro solo
-- 'DIRETTORE_UOC' lasciava senza WEM_PERF_IN_CHARGE ~15 schede il cui responsabile ha un ruolo diverso
-- (Direzioni, FORMAZIONE=PROF_SAN_PREVENZIONE, ecc.): il responsabile in gruppo DIR_UO (isRole) non
-- vedeva la propria scheda. Ora ogni scheda con un responsabile lo aggancia come referente-visibilita'.
INSERT INTO work_effort_party_assignment (work_effort_id, party_id, role_type_id, from_date, thru_date,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT we.work_effort_id, pr.party_id_to, 'WEM_PERF_IN_CHARGE', we.estimated_start_date, we.estimated_completion_date,
       now(), now(), now(), now()
FROM work_effort we
JOIN party_relationship pr ON pr.party_id_from = we.org_unit_id
   AND pr.party_relationship_type_id = 'ORG_RESPONSIBLE'
   AND (pr.thru_date IS NULL OR pr.thru_date > now())
WHERE we.work_effort_type_id = 'CTX_BS'
  AND we.org_unit_id IS NOT NULL
  AND we.estimated_start_date IS NOT NULL
  AND we.estimated_completion_date IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM work_effort_party_assignment x
                  WHERE x.work_effort_id = we.work_effort_id AND x.party_id = pr.party_id_to
                    AND x.role_type_id = 'WEM_PERF_IN_CHARGE' AND x.from_date = we.estimated_start_date);

-- ---------- Verifica ----------
SELECT group_id, count(*) AS membri
FROM user_login_security_group
WHERE group_id IN ('STRATPERF_REFERENTE','STRATPERF_DIR_UO') AND thru_date IS NULL
GROUP BY group_id ORDER BY group_id;

SELECT 'WEM_PERF_IN_CHARGE su schede CTX_BS' AS check, count(*) AS assegnazioni
FROM work_effort_party_assignment wepa
JOIN work_effort we ON we.work_effort_id = wepa.work_effort_id AND we.work_effort_type_id = 'CTX_BS'
WHERE wepa.role_type_id = 'WEM_PERF_IN_CHARGE';

-- Responsabili di scheda CTX_BS ancora FUORI da STRATPERF_DIR_UO (atteso: solo i Dir San/Amm):
SELECT DISTINCT pr.role_type_id_to AS ruolo, ul.user_login_id
FROM work_effort we
JOIN party_relationship pr ON pr.party_id_from = we.org_unit_id AND pr.party_relationship_type_id='ORG_RESPONSIBLE'
   AND (pr.thru_date IS NULL OR pr.thru_date > now())
JOIN user_login ul ON ul.party_id = pr.party_id_to
WHERE we.work_effort_type_id='CTX_BS' AND we.org_unit_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM user_login_security_group x
                  WHERE x.user_login_id = ul.user_login_id AND x.group_id='STRATPERF_DIR_UO' AND x.thru_date IS NULL)
ORDER BY ruolo;
