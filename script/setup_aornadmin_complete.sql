-- =====================================================================
-- CONFIGURAZIONE COMPLETA SECURITY GROUP AORNADMIN
-- =====================================================================
-- Questo script crea e configura il security group AORNADMIN con tutti
-- i permessi necessari per accedere alle funzionalità di:
-- - PERFORMANCE MANAGEMENT (BSC e Employee Performance)
-- - DATI DI BASE (Party Management, Organizzazioni, Risorse Umane)
-- - ACCOUNTING (Unità Contabili ed Extracontabili)
-- - WORK EFFORT MANAGEMENT
-- 
-- Include anche le esclusioni (security_group_content) per nascondere
-- menu non rilevanti per questo profilo.
-- =====================================================================

\echo ''
\echo '=========================================='
\echo 'SETUP SECURITY GROUP AORNADMIN'
\echo '=========================================='
\echo ''

-- =====================================================================
-- SEZIONE 1: CREAZIONE SECURITY GROUP
-- =====================================================================
\echo '1. Creazione Security Group AORNADMIN...'

INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('AORNADMIN', 'Amministratore di Sistema AORN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'GP_WE_PORTAL_2', 'admin', NULL);

\echo '   ✓ Security Group AORNADMIN creato'
\echo ''

-- =====================================================================
-- SEZIONE 2: PERMESSI BASE E TOOLS
-- =====================================================================
\echo '2. Aggiunta permessi base e tools...'

-- Accesso agli strumenti OFBiz
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'OFBTOOLS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Gestione periodi
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PERIOD_MAINT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Gestione catalogo prezzi
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'CATALOG_PRICE_MAINT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Gestione pagamenti manuali
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'MANUAL_PAYMENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\echo '   ✓ 4 permessi base aggiunti'
\echo ''

-- =====================================================================
-- SEZIONE 3: PERMESSI ACCOUNTING
-- =====================================================================
\echo '3. Aggiunta permessi Accounting...'

-- Visualizzazione comunicazioni accounting
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_COMM_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Stampa assegni
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_PRINT_CHECKS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi ACCTG_ATX (Accounting Transactions Extended)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCTG_ATX_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCTG_ATX_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCTG_ATX_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCTG_ATX_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi ACCOUNTING base (VIEW, ADMIN, CREATE, UPDATE, DELETE)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\echo '   ✓ 11 permessi Accounting aggiunti'
\echo ''

-- =====================================================================
-- SEZIONE 4: PERMESSI ORGANIZATIONAL PERFORMANCE E COMMON
-- =====================================================================
\echo '4. Aggiunta permessi Organizational Performance e Common...'

-- Visualizzazione performance organizzativa
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ORGPERF_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Accesso a funzionalità comuni estese
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'COMMONEXT_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\echo '   ✓ 2 permessi Organizational Performance aggiunti'
\echo ''

-- =====================================================================
-- SEZIONE 5: PERMESSI BSC PERFORMANCE MANAGEMENT
-- =====================================================================
\echo '5. Aggiunta permessi BSC Performance Management...'

-- Permessi base BSC Performance Manager
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFMGR_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFMGR_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFMGR_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFMGR_ROOT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi amministrativi BSC
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFORG_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFVIEW_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFSUP_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFTOP_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi BSC specifici (Report, Query, Analysis)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSC_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'REPORT_PRINT_CTX_BS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_BS_E_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_BS_U_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ANALYSIS_CTX_BS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\echo '   ✓ 16 permessi BSC Performance aggiunti'
\echo ''

-- =====================================================================
-- SEZIONE 6: PERMESSI EMPLOYEE PERFORMANCE MANAGEMENT
-- =====================================================================
\echo '6. Aggiunta permessi Employee Performance Management...'

-- Permessi visualizzazione schede performance
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFCARD_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLVALUTATO_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLVALUTATORE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi ruoli performance (Responsabile e Valutato)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERF_RESP', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERF_VAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi base Employee Performance Manager
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFMGR_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFMGR_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFMGR_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PERMESSO ROOT: consente visibilità GLOBALE su tutte le schede performance
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFMGR_ROOT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi amministrativi Employee Performance
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFORG_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFVIEW_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFSUP_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFTOP_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi Employee Performance specifici (Report, Query, Analysis)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'REPORT_PRINT_CTX_EP_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_EP_E_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_EP_U_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ANALYSIS_CTX_EP_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Accesso portale dipendenti
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'MYPORTAL_EMPLOYEE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\echo '   ✓ 21 permessi Employee Performance aggiunti'
\echo ''

-- =====================================================================
-- SEZIONE 7: PERMESSI WORK EFFORT MANAGEMENT
-- =====================================================================
\echo '7. Aggiunta permessi Work Effort Management...'

-- Permessi base Work Effort Manager
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTMGR_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTMGR_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTMGR_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permesso import accounting
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACC_IMPORT_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\echo '   ✓ 6 permessi Work Effort aggiunti'
\echo ''

-- =====================================================================
-- SEZIONE 8: PERMESSI PARTY MANAGEMENT (DATI DI BASE)
-- =====================================================================
\echo '8. Aggiunta permessi Party Management (Dati di Base)...'

-- Permessi base Party Manager
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_NOTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi Party Manager Base
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGRBASE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGRBASE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi gestione Ruoli (PARTYMGRROLE_*)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGRROLE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGRROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGRROLE_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGRROLE_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGRROLE_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi gestione Relazioni (PARTYMGR_REL_*)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_REL_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_REL_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_REL_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_REL_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_REL_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi Party Content Manager (PARTYMGR_PCM_*)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_PCM_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_PCM_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_PCM_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_PCM_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_PCM_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi Communication Manager (PARTYMGR_CME_*)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_CME_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_CME_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_CME_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_CME_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_CME_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi Qualifications (PARTYMGR_QAL_*)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_QAL_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_QAL_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_QAL_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_QAL_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_QAL_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permessi funzionali (PARTYMGR_F_*)
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_F_EMPL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_F_GEN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_F_GOAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_F_ORG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permesso allocation
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_ALLOCATION', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\echo '   ✓ 39 permessi Party Management aggiunti'
\echo ''

-- =====================================================================
-- SEZIONE 9: PERMESSI HUMAN RESOURCES EXTENDED
-- =====================================================================
\echo '9. Aggiunta permessi Human Resources Extended...'

-- Permessi accesso componente humanresext
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'HUMANRESEXT_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'HUMANRESEXT_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'HUMANRESEXT_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'HUMANRESEXT_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'HUMANRESEXT_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\echo '   ✓ 5 permessi Human Resources Extended aggiunti'
\echo ''

-- =====================================================================
-- SEZIONE 10: ESCLUSIONI MENU (security_group_content)
-- =====================================================================
\echo '10. Configurazione esclusioni menu...'
\echo '    (Menu NON visibili per AORNADMIN)'
\echo ''

-- Esclusione Report Print CTX_BS
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00445', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Report Print CTX_EP
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00447', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Query Config CTX_BS Entità
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00488', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Query Config CTX_EP Entità
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00490', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Analysis CTX_BS
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00550', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Analysis CTX_EP
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00552', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione menu GP_MENU_00139
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00139', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Query Config CTX_EP Aggregata
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00496', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Query Config CTX_BS Aggregata
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00494', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione portale MY
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'NOPORTAL_MY', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione menu GP_MENU_00195
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00195', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione menu GP_MENU_00193
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00193', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\echo '   ✓ 11 esclusioni menu configurate'
\echo ''

-- =====================================================================
-- VERIFICA FINALE
-- =====================================================================
\echo ''
\echo '=========================================='
\echo 'VERIFICA CONFIGURAZIONE'
\echo '=========================================='
\echo ''

\echo 'Totale permessi assegnati ad AORNADMIN:'
SELECT COUNT(*) as totale_permessi
FROM security_group_permission
WHERE group_id = 'AORNADMIN';

\echo ''
\echo 'Totale esclusioni menu configurate:'
SELECT COUNT(*) as totale_esclusioni
FROM security_group_content
WHERE group_id = 'AORNADMIN'
  AND (thru_date IS NULL OR thru_date > CURRENT_TIMESTAMP);

\echo ''
\echo '=========================================='
\echo 'RIEPILOGO PERMESSI PER CATEGORIA'
\echo '=========================================='
\echo ''

SELECT 
    CASE 
        WHEN permission_id LIKE 'PARTYMGR%' THEN 'PARTY MANAGEMENT'
        WHEN permission_id LIKE 'EMPLPERF%' THEN 'EMPLOYEE PERFORMANCE'
        WHEN permission_id LIKE 'BSCPERF%' THEN 'BSC PERFORMANCE'
        WHEN permission_id LIKE 'WORKEFFORT%' THEN 'WORK EFFORT'
        WHEN permission_id LIKE 'ACCOUNTING%' THEN 'ACCOUNTING'
        WHEN permission_id LIKE 'ACCTG_%' THEN 'ACCOUNTING'
        WHEN permission_id LIKE 'HUMANRES%' THEN 'HUMAN RESOURCES'
        WHEN permission_id LIKE 'QUERY_CONFIG%' THEN 'QUERY CONFIG'
        WHEN permission_id LIKE 'REPORT_PRINT%' THEN 'REPORT PRINT'
        WHEN permission_id LIKE 'ANALYSIS%' THEN 'ANALYSIS'
        ELSE 'ALTRI'
    END as categoria,
    COUNT(*) as numero_permessi
FROM security_group_permission
WHERE group_id = 'AORNADMIN'
GROUP BY categoria
ORDER BY numero_permessi DESC;

\echo ''
\echo '=========================================='
\echo 'CONFIGURAZIONE COMPLETATA CON SUCCESSO!'
\echo '=========================================='
\echo ''
\echo 'PROSSIMI PASSI:'
\echo '1. Assegna il security group AORNADMIN agli utenti desiderati:'
\echo '   INSERT INTO user_login_security_group (user_login_id, group_id, from_date)'
\echo '   VALUES (''nome_utente'', ''AORNADMIN'', CURRENT_TIMESTAMP);'
\echo ''
\echo '2. Logout e login con utente AORNADMIN per verificare'
\echo ''
\echo '3. Verifica accesso alle sezioni:'
\echo '   - PERFORMANCE MANAGEMENT'
\echo '   - DATI DI BASE'
\echo '   - Menu specifici: Organizzazioni, Stakeholders, Anagrafiche RU, ecc.'
\echo ''
