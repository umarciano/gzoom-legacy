-- =====================================================================
-- ROLLBACK SECURITY PROFILE ORGPERF_DIR_UO
-- =====================================================================
-- Rimuove completamente il profilo ORGPERF_DIR_UO dal database.
-- Eseguire PRIMA di re-applicare setup_orgperf_dir_uo_profile.sql
-- oppure per annullare la configurazione senza restore del DB.
-- =====================================================================

-- Rimuove associazioni utente-gruppo (se già assegnati utenti)
DELETE FROM public.user_login_security_group
WHERE group_id = 'ORGPERF_DIR_UO';

-- Rimuove esclusioni menu
DELETE FROM public.security_group_content
WHERE group_id = 'ORGPERF_DIR_UO';

-- Rimuove permessi del gruppo
DELETE FROM public.security_group_permission
WHERE group_id = 'ORGPERF_DIR_UO';

-- Rimuove il security group
DELETE FROM public.security_group
WHERE group_id = 'ORGPERF_DIR_UO';

-- Rimuove il permesso custom
DELETE FROM public.security_permission
WHERE permission_id = 'ORGPERFDIR_VIEW';

-- Verifica: devono tornare 0 righe per tutte
SELECT 'user_login_security_group' as tabella, COUNT(*) as righe_residue FROM public.user_login_security_group WHERE group_id = 'ORGPERF_DIR_UO'
UNION ALL
SELECT 'security_group_content',    COUNT(*) FROM public.security_group_content    WHERE group_id = 'ORGPERF_DIR_UO'
UNION ALL
SELECT 'security_group_permission', COUNT(*) FROM public.security_group_permission WHERE group_id = 'ORGPERF_DIR_UO'
UNION ALL
SELECT 'security_group',            COUNT(*) FROM public.security_group            WHERE group_id = 'ORGPERF_DIR_UO'
UNION ALL
SELECT 'security_permission',       COUNT(*) FROM public.security_permission       WHERE permission_id = 'ORGPERFDIR_VIEW';
