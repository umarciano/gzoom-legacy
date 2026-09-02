-- =====================================================================
-- POST_IMPORT_FIX_PARTYROLE_REFERENTE.sql
-- =====================================================================
-- Sana la lookup UI di assegnazione del REFERENTE indicatore (ruolo WEM_IND_IN_CHARGE).
-- MODELLO PERSONA (2026-09-02): il referente e' una PERSONA (parent EMPLOYEE), non piu' una UOC.
--
-- Problema: le party_role WEM_IND_IN_CHARGE (seed V006 + effetto collaterale import) possono avere
-- parent_role_type_id NULL; la lookup UI (PartyRoleView) associa il ruolo al suo parent: se NULL il
-- join salta e la tendina "Soggetto" risulta VUOTA -> impossibile assegnare il referente da UI.
--
-- Fix: completare parent_role_type_id = 'EMPLOYEE' su tutte le party_role WEM_IND_IN_CHARGE (persone).
-- Idempotente; da rieseguire dopo ogni (re)import del catalogo indicatori.
-- =====================================================================

-- Solo per le PERSONE: il FK party_role(parent_role_type_id, party_id) -> party_parent_role richiede
-- che la persona abbia party_parent_role role EMPLOYEE (che ha). Le eventuali party_role residue su
-- UOC (vecchio modello) restano invariate/obsolete.
UPDATE party_role pr
   SET parent_role_type_id = 'EMPLOYEE',
       last_updated_stamp = now(), last_updated_tx_stamp = now()
 WHERE pr.role_type_id = 'WEM_IND_IN_CHARGE'
   AND pr.parent_role_type_id IS DISTINCT FROM 'EMPLOYEE'
   AND EXISTS (SELECT 1 FROM party_parent_role ppr
               WHERE ppr.party_id = pr.party_id AND ppr.role_type_id = 'EMPLOYEE');

-- Verifica: deve risultare un'unica riga con parent_role_type_id = 'EMPLOYEE'.
SELECT parent_role_type_id, COUNT(*) AS n
FROM party_role WHERE role_type_id = 'WEM_IND_IN_CHARGE'
GROUP BY parent_role_type_id;
