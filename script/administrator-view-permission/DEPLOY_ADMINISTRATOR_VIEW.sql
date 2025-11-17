-- ============================================================================
-- DEPLOY: Permesso ADMINISTRATOR_VIEW
-- ============================================================================
-- Questo script APPLICA il permesso ADMINISTRATOR_VIEW al database
-- Usalo per portare l'ambiente dei tuoi colleghi al tuo stato
-- (dove l'amministratore NON PUÒ modificare le valutazioni, solo visualizzare)
--
-- Data: 17 Novembre 2025
-- Autore: Script generato per deploy modifica GZOOM
-- Riferimento: CUSTOMIZATION_GZOOM.md - Sezione "ADMINISTRATOR_VIEW"
-- ============================================================================

-- STEP 1: Verifica stato PRIMA del deploy
SELECT '=== STATO PRIMA DEL DEPLOY ===' as info;

SELECT 'Permesso ADMINISTRATOR_VIEW esistente (dovrebbe essere vuoto):' as check_permesso;
SELECT * FROM security_permission WHERE permission_id = 'ADMINISTRATOR_VIEW';

SELECT 'Assegnazione al gruppo FULLADMIN (dovrebbe essere vuoto):' as check_assegnazione;
SELECT * FROM security_group_permission 
WHERE group_id = 'FULLADMIN' AND permission_id = 'ADMINISTRATOR_VIEW';

-- STEP 2: DEPLOY - Crea il permesso ADMINISTRATOR_VIEW
SELECT '=== ESECUZIONE DEPLOY ===' as info;

INSERT INTO security_permission (permission_id, description) 
VALUES ('ADMINISTRATOR_VIEW', 'Permesso di visualizzazione read-only per amministratori');

SELECT 'Permesso ADMINISTRATOR_VIEW creato' as step1_completato;

-- STEP 3: DEPLOY - Assegna il permesso al gruppo FULLADMIN
INSERT INTO security_group_permission (group_id, permission_id) 
VALUES ('FULLADMIN', 'ADMINISTRATOR_VIEW');

SELECT 'Permesso assegnato al gruppo FULLADMIN' as step2_completato;

-- STEP 4: Verifica stato DOPO il deploy
SELECT '=== STATO DOPO IL DEPLOY ===' as info;

SELECT 'Permesso ADMINISTRATOR_VIEW creato:' as verifica_permesso;
SELECT * FROM security_permission WHERE permission_id = 'ADMINISTRATOR_VIEW';

SELECT 'Assegnazione al gruppo FULLADMIN:' as verifica_assegnazione;
SELECT * FROM security_group_permission 
WHERE group_id = 'FULLADMIN' AND permission_id = 'ADMINISTRATOR_VIEW';

-- STEP 5: Verifica utenti interessati
SELECT '=== UTENTI INTERESSATI ===' as info_utenti;
SELECT 'Utenti del gruppo FULLADMIN (che avranno il permesso ADMINISTRATOR_VIEW):' as check_utenti;
SELECT ulsg.user_login_id, ulsg.group_id, ul.party_id
FROM user_login_security_group ulsg
JOIN user_login ul ON ul.user_login_id = ulsg.user_login_id
WHERE ulsg.group_id = 'FULLADMIN'
AND (ulsg.thru_date IS NULL OR ulsg.thru_date > NOW());

-- STEP 6: Istruzioni finali
SELECT '=== DEPLOY COMPLETATO ===' as risultato;
SELECT 'IMPORTANTE: Esegui LOGOUT e LOGIN per ricaricare i permessi!' as istruzioni;
SELECT 'Ora l''amministratore NON DOVREBBE poter modificare le valutazioni (solo visualizzare)' as test_atteso;
SELECT 'Gli utenti FULLADMIN vedranno i form in modalità read-only' as comportamento;
