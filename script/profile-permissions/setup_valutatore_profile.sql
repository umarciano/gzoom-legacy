-- =====================================================================
-- CONFIGURAZIONE SECURITY PROFILE EMPLPERF_VALUTATORE
-- =====================================================================
-- Script per configurare il profilo di sicurezza VALUTATORE con:
-- - 1 permesso custom (EMPLVALUTATORE_VIEW)
-- - 9 permessi operativi (security_group_permission)
--
-- FUNZIONALITÀ ABILITATE:
--   - Visualizzazione Performance Individuale (Employee Performance)
--   - Gestione completa contenuti (Content Manager)
--   - Gestione transazioni accounting (create/delete)
--   - Creazione work effort
--   - Strumenti base OFBiz
--
-- CONTESTO:
--   - CTX_EP: Employee Performance (Performance Individuale)
--
-- PORTALE PREDEFINITO: GP_WE_PORTAL_3 (Portale Performance Individuale)
-- =====================================================================

-- =====================================================================
-- SEZIONE 1: CREAZIONE PERMESSO CUSTOM
-- =====================================================================
-- Crea il permesso specifico per il profilo Valutatore

INSERT INTO public.security_permission
(permission_id, description, dynamic_access, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, enabled, last_modified_by_user_login, created_by_user_login)
VALUES('EMPLVALUTATORE_VIEW', 'Valutatore permission', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Y', NULL, NULL);


-- =====================================================================
-- SEZIONE 2: CREAZIONE SECURITY GROUP
-- =====================================================================
-- Crea il security group EMPLPERF_VALUTATORE con portale predefinito GP_WE_PORTAL_3

INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('EMPLPERF_VALUTATORE', 'Performance Individuale - Soggetto Valutatore', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'GP_WE_PORTAL_3', 'admin', NULL);


-- =====================================================================
-- SEZIONE 3: PERMESSI SECURITY_GROUP_PERMISSION (9 permessi totali)
-- =====================================================================

-- ---------------------------------------------------------------------
-- 3.1 PERMESSI EMPLOYEE PERFORMANCE (2 permessi)
-- ---------------------------------------------------------------------
-- Visualizzazione modulo Employee Performance Manager
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'EMPLPERFMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permesso specifico Valutatore
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'EMPLVALUTATORE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.2 PERMESSI WORK EFFORT (1 permesso)
-- ---------------------------------------------------------------------
-- Creazione work effort
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'WORKEFFORTMGR_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.3 PERMESSI ACCOUNTING (2 permessi)
-- ---------------------------------------------------------------------
-- Creazione transazioni accounting
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'ACCTG_ATX_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Eliminazione transazioni accounting
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'ACCTG_ATX_DELETE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.4 PERMESSI CONTENT MANAGER (3 permessi)
-- ---------------------------------------------------------------------
-- Visualizzazione contenuti
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'CONTENTMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Visualizzazione ruoli contenuti
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'CONTENTMGR_ROLE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Creazione ruoli contenuti
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'CONTENTMGR_ROLE_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.5 PERMESSI TOOLS (1 permesso)
-- ---------------------------------------------------------------------
-- Visualizzazione strumenti OFBiz
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'OFBTOOLS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- =====================================================================
-- SEZIONE 4: VERIFICA CONFIGURAZIONE
-- =====================================================================

-- Conteggio permessi assegnati a EMPLPERF_VALUTATORE (deve essere 9)
SELECT COUNT(*) as totale_permessi
FROM security_group_permission
WHERE group_id = 'EMPLPERF_VALUTATORE';


-- =====================================================================
-- RIEPILOGO CONFIGURAZIONE
-- =====================================================================
-- TOTALE: 9 permessi
--
-- PERMESSI SUDDIVISI IN:
-- - 2 Employee Performance (EMPLPERFMGR_VIEW, EMPLVALUTATORE_VIEW)
-- - 1 Work Effort (WORKEFFORTMGR_CREATE)
-- - 2 Accounting (ACCTG_ATX_CREATE, ACCTG_ATX_DELETE)
-- - 3 Content Manager (VIEW, ROLE_VIEW, ROLE_CREATE)
-- - 1 Tools (OFBTOOLS_VIEW)
--
-- PROFILO DESTINATO A:
-- - Responsabili che valutano le performance dei dipendenti
-- - Gestione schede di valutazione
-- - Creazione e gestione contenuti di valutazione
-- - Gestione transazioni contabili relative alle performance
-- =====================================================================

--- Esclusioni
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00189', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00222', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00267', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00127', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00243', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00281', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00537', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00484', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00519', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00195', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00130', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00134', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00252', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00520', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00198', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00030', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00031', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00219', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00032', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00264', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00037', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00215', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00038', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00039', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00040', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00045', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00218', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00041', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00042', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00245', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00207', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00249', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00485', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00549', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00514', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00217', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00192', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00211', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00223', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_N0001', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_N0002', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00234', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_N0003', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00242', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'NOPORTAL_EVAL', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATORE', 'GP_MENU_00255', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');