-- =============================================================================
-- MIGRAZIONE una-tantum: rinomina gruppi di sicurezza ORGPERF_* -> STRATPERF_*
-- =============================================================================
-- Contesto: il "rifacimento gruppi" (disambiguazione organizzativa/strategica) ha
-- rinominato i gruppi SOLO nel SETUP (che gira su restore da zero) e nel codice groovy
-- (executePerformFindBSWorkEffortRoot* ora cercano STRATPERF_*). Un DB gia' configurato
-- resta invece coi vecchi ORGPERF_* -> disallineamento DB<->codice (scoping direttore rotto).
-- Questo script allinea IN-PLACE un DB esistente, senza drop/reimport.
--
-- Cosa fa:
--   1. duplica i 4 gruppi (security_group) col nome nuovo, copiando TUTTE le colonne
--      (via temp table -> robusto a variazioni di schema) e aggiornando la description;
--   2. riaggancia i figli (security_group_permission / _content / user_login_security_group);
--   3. elimina i gruppi vecchi;
--   4. rimuove l'esclusione della voce Stampa (GP_MENU_00209 = /stratperf/control/workEffortPrintBirt)
--      cosi' da renderla visibile al direttore, coerente col SETUP target (non la esclude per nessuno).
--
-- FK verso security_group sono NO ACTION (non cascade) -> serve il metodo insert/repoint/delete.
-- party_relationship / portal_page / protected_view referenziano security_group ma NON hanno
-- righe sui gruppi perf (verificato) -> non toccate.
--
-- IDEMPOTENTE: se ORGPERF_* non esistono piu', gli step 1-3 non fanno nulla e lo step 4 e' un
-- DELETE su 0 righe. Ri-eseguibile in sicurezza.
--
-- Uso: psql -U postgres -h localhost -d <db> -v ON_ERROR_STOP=1 -f MIGRAZIONE_RENAME_GRUPPI_STRATPERF.sql
-- Dopo: RIAVVIARE il legacy (OFBiz cache i gruppi/menu) e verificare con un login direttore.
-- =============================================================================

BEGIN;

-- 1) Duplica i 4 gruppi con nome nuovo (copia tutte le colonne via temp).
CREATE TEMP TABLE tmp_sg ON COMMIT DROP AS
  SELECT * FROM security_group WHERE group_id LIKE 'ORGPERF\_%';
UPDATE tmp_sg
   SET group_id    = replace(group_id, 'ORGPERF_', 'STRATPERF_'),
       description = regexp_replace(description, '[Oo]rganizzativa', 'Strategica', 'g');
INSERT INTO security_group SELECT * FROM tmp_sg;

-- 2) Riaggancia i figli ai gruppi nuovi.
UPDATE security_group_permission SET group_id = replace(group_id, 'ORGPERF_', 'STRATPERF_') WHERE group_id LIKE 'ORGPERF\_%';
UPDATE security_group_content    SET group_id = replace(group_id, 'ORGPERF_', 'STRATPERF_') WHERE group_id LIKE 'ORGPERF\_%';
UPDATE user_login_security_group SET group_id = replace(group_id, 'ORGPERF_', 'STRATPERF_') WHERE group_id LIKE 'ORGPERF\_%';

-- 3) Elimina i gruppi vecchi (ora senza figli).
DELETE FROM security_group WHERE group_id LIKE 'ORGPERF\_%';

-- 4) Stampa visibile al direttore: rimuovi l'esclusione GP_MENU_00209.
DELETE FROM security_group_content WHERE group_id LIKE 'STRATPERF\_%' AND content_id = 'GP_MENU_00209';

COMMIT;
