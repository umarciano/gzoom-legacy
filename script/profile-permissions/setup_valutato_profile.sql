-- =====================================================================
-- CONFIGURAZIONE SECURITY PROFILE EMPLPERF_VALUTATO
-- =====================================================================
-- Script per configurare il profilo di sicurezza VALUTATO con:
-- - 1 permesso custom (EMPLVALUTATO_VIEW)
-- - 8 permessi operativi (security_group_permission)
--
-- FUNZIONALITÀ ABILITATE:
--   - Visualizzazione Performance Individuale (Employee Performance)
--   - Visualizzazione e creazione contenuti (Content Manager)
--   - Visualizzazione schede di valutazione (Performance Card)
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
-- Crea il permesso specifico per il profilo Valutato

INSERT INTO public.security_permission
(permission_id, description, dynamic_access, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, enabled, last_modified_by_user_login, created_by_user_login)
VALUES('EMPLVALUTATO_VIEW', 'Valutato permission', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Y', NULL, NULL);


-- =====================================================================
-- SEZIONE 2: CREAZIONE SECURITY GROUP
-- =====================================================================
-- Crea il security group EMPLPERF_VALUTATO con portale predefinito GP_WE_PORTAL_3

INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('EMPLPERF_VALUTATO', 'Performance Individuale - Soggetto Valutato', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'GP_WE_PORTAL_3', 'admin', NULL);


-- =====================================================================
-- SEZIONE 3: PERMESSI SECURITY_GROUP_PERMISSION (8 permessi totali)
-- =====================================================================

-- ---------------------------------------------------------------------
-- 3.1 PERMESSI EMPLOYEE PERFORMANCE (2 permessi)
-- ---------------------------------------------------------------------
-- Visualizzazione modulo Employee Performance Manager
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'EMPLPERFMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Visualizzazione scheda di valutazione
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'EMPLPERFCARD_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Permesso specifico Valutato
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'EMPLVALUTATO_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.2 PERMESSI WORK EFFORT (1 permesso)
-- ---------------------------------------------------------------------
-- Creazione work effort
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'WORKEFFORTMGR_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.3 PERMESSI CONTENT MANAGER (3 permessi)
-- ---------------------------------------------------------------------
-- Visualizzazione contenuti
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'CONTENTMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Visualizzazione ruoli contenuti
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'CONTENTMGR_ROLE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Creazione ruoli contenuti
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'CONTENTMGR_ROLE_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.4 PERMESSI TOOLS (1 permesso)
-- ---------------------------------------------------------------------
-- Visualizzazione strumenti OFBiz
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'OFBTOOLS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- =====================================================================
-- SEZIONE 4: VERIFICA CONFIGURAZIONE
-- =====================================================================

-- Conteggio permessi assegnati a EMPLPERF_VALUTATO (deve essere 8)
SELECT COUNT(*) as totale_permessi
FROM security_group_permission
WHERE group_id = 'EMPLPERF_VALUTATO';


-- =====================================================================
-- RIEPILOGO CONFIGURAZIONE
-- =====================================================================
-- TOTALE: 8 permessi
--
-- PERMESSI SUDDIVISI IN:
-- - 3 Employee Performance (EMPLPERFMGR_VIEW, EMPLPERFCARD_VIEW, EMPLVALUTATO_VIEW)
-- - 1 Work Effort (WORKEFFORTMGR_CREATE)
-- - 3 Content Manager (VIEW, ROLE_VIEW, ROLE_CREATE)
-- - 1 Tools (OFBTOOLS_VIEW)
--
-- PROFILO DESTINATO A:
-- - Dipendenti soggetti a valutazione delle performance
-- - Visualizzazione della propria scheda di valutazione
-- - Consultazione contenuti relativi alla performance
-- =====================================================================

--- Esclusioni
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00222', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00267', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00127', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00243', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00281', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00537', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00484', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00519', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00195', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00189', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00139', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00134', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00130', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00520', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00252', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'NOPORTAL_EVAL', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00255', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('EMPLPERF_VALUTATO', 'GP_MENU_00142', '2017-01-01 00:00:00.000', NULL, '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856', '2025-07-04 17:48:26.876', '2025-07-04 17:48:26.856');