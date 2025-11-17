-- ============================================================================
-- ROLLBACK: Permesso ADMINISTRATOR_VIEW
-- ============================================================================
-- Questo script RIMUOVE il permesso ADMINISTRATOR_VIEW dal database
-- Usalo per riportare il tuo ambiente allo stato dei tuoi colleghi
-- (dove l'amministratore PUÒ modificare le valutazioni)
--
-- Data: 17 Novembre 2025
-- Autore: Script generato per test ambiente GZOOM
-- ============================================================================

-- STEP 1: Verifica stato PRIMA del rollback
SELECT '=== STATO PRIMA DEL ROLLBACK ===' as info;

SELECT 'Permesso ADMINISTRATOR_VIEW esistente:' as check_permesso;
SELECT * FROM security_permission WHERE permission_id = 'ADMINISTRATOR_VIEW';

SELECT 'Assegnazione al gruppo FULLADMIN:' as check_assegnazione;
SELECT * FROM security_group_permission 
WHERE group_id = 'FULLADMIN' AND permission_id = 'ADMINISTRATOR_VIEW';

-- STEP 2: ROLLBACK - Rimuovi l'assegnazione del permesso al gruppo
SELECT '=== ESECUZIONE ROLLBACK ===' as info;

DELETE FROM security_group_permission 
WHERE group_id = 'FULLADMIN' AND permission_id = 'ADMINISTRATOR_VIEW';

SELECT 'Record eliminati da security_group_permission' as step1_completato;

-- STEP 3: ROLLBACK - Rimuovi il permesso stesso
DELETE FROM security_permission WHERE permission_id = 'ADMINISTRATOR_VIEW';

SELECT 'Record eliminati da security_permission' as step2_completato;

-- STEP 4: Verifica stato DOPO il rollback
SELECT '=== STATO DOPO IL ROLLBACK ===' as info;

SELECT 'Permesso ADMINISTRATOR_VIEW esistente (dovrebbe essere vuoto):' as verifica_permesso;
SELECT * FROM security_permission WHERE permission_id = 'ADMINISTRATOR_VIEW';

SELECT 'Assegnazione al gruppo FULLADMIN (dovrebbe essere vuoto):' as verifica_assegnazione;
SELECT * FROM security_group_permission 
WHERE group_id = 'FULLADMIN' AND permission_id = 'ADMINISTRATOR_VIEW';

-- STEP 5: Istruzioni finali
SELECT '=== ROLLBACK COMPLETATO ===' as risultato;
SELECT 'IMPORTANTE: Esegui LOGOUT e LOGIN per ricaricare i permessi!' as istruzioni;
SELECT 'Ora l''amministratore DOVREBBE poter modificare le valutazioni' as test_atteso;
