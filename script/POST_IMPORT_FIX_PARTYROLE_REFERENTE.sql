-- =====================================================================
-- POST_IMPORT_FIX_PARTYROLE_REFERENTE.sql
-- =====================================================================
-- Sana la lookup UI di assegnazione del REFERENTE indicatore (ruolo WEM_IND_IN_CHARGE, party = UOC).
--
-- Problema: l'import del catalogo, creando i gl_account_role WEM_IND_IN_CHARGE, genera come EFFETTO
-- COLLATERALE le party_role corrispondenti sulle UOC SENZA valorizzare parent_role_type_id (resta NULL).
-- La lookup della UI (request lookupPartyRoleOrgUnitView -> vista PartyRoleOrgUnitView) unisce
-- PartyParentRole.roleTypeId (='ORGANIZATION_UNIT') con PartyRole.parentRoleTypeId: se quest'ultimo e'
-- NULL il join salta e la tendina "Soggetto" risulta VUOTA ("Non e' stato trovato alcun dato") ->
-- impossibile assegnare il referente da UI.
--
-- Fix: completare parent_role_type_id = 'ORGANIZATION_UNIT' su tutte le party_role WEM_IND_IN_CHARGE.
-- Idempotente; da rieseguire dopo ogni (re)import del catalogo indicatori. Verificato 2026-08-18 (152 righe).
-- =====================================================================

UPDATE party_role
   SET parent_role_type_id = 'ORGANIZATION_UNIT',
       last_updated_stamp = now(), last_updated_tx_stamp = now()
 WHERE role_type_id = 'WEM_IND_IN_CHARGE'
   AND parent_role_type_id IS DISTINCT FROM 'ORGANIZATION_UNIT';

-- Verifica: deve risultare un'unica riga con parent_role_type_id = 'ORGANIZATION_UNIT'.
SELECT parent_role_type_id, COUNT(*) AS n
FROM party_role WHERE role_type_id = 'WEM_IND_IN_CHARGE'
GROUP BY parent_role_type_id;
