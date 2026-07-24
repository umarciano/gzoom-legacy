-- =====================================================================
-- CONFIGURAZIONE SECURITY PROFILE ORGPERF_DIR_UO
-- =====================================================================
-- Script per configurare il profilo di sicurezza ORGPERF_DIR_UO con:
-- - 1 permesso custom (ORGPERFDIR_VIEW)
-- - 9 permessi operativi (security_group_permission)
--
-- FUNZIONALITÀ ABILITATE:
--   - Visualizzazione Performance Strategica (OrgPerf) in sola lettura
--   - Validazione parziale schede con obiettivi (su GP_MENU_00092 Definizione)
--   - Presa visione schede con risultati finali (su GP_MENU_00101 Valutazione)
--
-- CONTESTO:
--   - CTX_BS: Performance Strategica
--
-- PORTALE PREDEFINITO: NULL (lascia decidere al gruppo EMPLPERF_VALUTATORE se presente)
--
-- MENU VISIBILI (in sidebar):
--   Performance Strategica > Gestione > Definizione  (GP_MENU_00092)
--   Performance Strategica > Gestione > Valutazione  (GP_MENU_00101)
--   Performance Individuale > ...                     (se l'utente ha anche EMPLPERF_VALUTATORE)
--
-- MENU ESCLUSI (tutto il resto tranne GP_MENU_00124 e discendenti):
--   - Altre sezioni Performance Management (GP_MENU_00105, 00450, 00466)
--   - PS > Amministrazione (GP_MENU_00400)
--   - PS > Gestione > Monitoraggio (GP_MENU_00096)
--   - PS > Gestione > altri (GP_MENU_00494, 00515)
--   - PS > Consultazione foglie (tranne NOPORTAL_BSC che serve al portale)
--   - Dati di Base non accessibili
--   - Contesti esclusi per AORN: CTX_OR, governance, altri moduli
--
-- NOTA: GP_MENU_00124 (Performance Individuale) e discendenti NON sono esclusi,
--       per permettere la combinazione con EMPLPERF_VALUTATORE senza conflitti.
--       Un utente con solo ORGPERF_DIR_UO non vede comunque le foglie /emplperf/
--       nel sidebar (manca il permesso EMPLPERFMGR_VIEW).
-- =====================================================================

-- =====================================================================
-- SEZIONE 1: CREAZIONE PERMESSO CUSTOM
-- =====================================================================

INSERT INTO public.security_permission
(permission_id, description, dynamic_access, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, enabled, last_modified_by_user_login, created_by_user_login)
VALUES('ORGPERFDIR_VIEW', 'Direttore UO - Performance Strategica permission', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Y', NULL, NULL);


-- =====================================================================
-- SEZIONE 2: CREAZIONE SECURITY GROUP
-- =====================================================================

INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('ORGPERF_DIR_UO', 'Performance Strategica - Direttore Unita Operativa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'admin', NULL);
-- NOTA: default_portal_page_id = NULL intenzionale.
-- Se l'utente ha anche EMPLPERF_VALUTATORE (GP_WE_PORTAL_3), quel portale viene usato.
-- Avere due portali diversi (GP_WE_PORTAL_3 + GP_WE_PORTAL_4) causa crash al login.


-- =====================================================================
-- SEZIONE 3: PERMESSI SECURITY_GROUP_PERMISSION (10 permessi totali)
-- =====================================================================

-- Permesso specifico Direttore UO
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'ORGPERFDIR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Visualizzazione modulo OrgPerf (Performance Strategica)
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'ORGPERFMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Accesso role-based alle schede (il Direttore UO è referenziato via work_effort_party_assignment)
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'ORGPERFROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Work Effort: creazione e aggiornamento (necessario per transizioni di stato)
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'WORKEFFORTMGR_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'WORKEFFORTMGR_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Content Manager
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'CONTENTMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'CONTENTMGR_ROLE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'CONTENTMGR_ROLE_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Balanced Scorecard Performance (BSCPERF): necessario per far apparire le foglie
-- di Performance Strategica nel sidebar (link /stratperf/... → chiave BSCPERF)
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'BSCPERFMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- OFBiz Tools base
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('ORGPERF_DIR_UO', 'OFBTOOLS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- =====================================================================
-- SEZIONE 4: VERIFICA CONFIGURAZIONE
-- =====================================================================

SELECT COUNT(*) as totale_permessi
FROM security_group_permission
WHERE group_id = 'ORGPERF_DIR_UO';


-- =====================================================================
-- SEZIONE 5: ESCLUSIONI MENU (security_group_content)
-- =====================================================================
-- Logica: escludi tutto tranne:
--   - GP_MENU_00086/00401/00092/00101 (Performance Strategica > Gestione > Definizione/Valutazione)
--   - GP_MENU_00124 e discendenti (Performance Individuale — gestiti da EMPLPERF_VALUTATORE)
--   - NOPORTAL_BSC: NON escluso — è portlet di GP_WE_PORTAL_4, escluderlo causa logout immediato
--   - NOPORTAL_MY: NON escluso — è portlet di GP_WE_PORTAL_3
--
-- Lista generata dal DB corrente (2026-07-23).

INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00030', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00031', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00032', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00037', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00038', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00039', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00040', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00041', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00042', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00045', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00081', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00089', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00096', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00104', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00105', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00108', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00111', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00115', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00120', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00123', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00187', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00188', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00193', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00194', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00198', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00207', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00209', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00210', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00211', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00215', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00216', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00217', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00218', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00219', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00220', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00221', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00223', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00225', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00227', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00234', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00242', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00245', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00249', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00250', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00251', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00253', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00254', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00264', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00265', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00266', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00282', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00332', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00334', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00347', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00348', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00400', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00402', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00403', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00404', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00405', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00445', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00446', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00450', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00451', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00452', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00453', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00454', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00455', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00456', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00457', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00458', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00459', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00460', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00461', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00462', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00463', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00464', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00465', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00466', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00467', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00468', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00469', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00470', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00471', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00472', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00474', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00475', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00476', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00477', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00478', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00479', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00480', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00481', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00482', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00483', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00485', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00486', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00487', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00488', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00489', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00491', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00492', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00493', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00494', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00495', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00497', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00498', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00499', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00500', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00504', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00505', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00506', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00507', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00508', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00509', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00510', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00511', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00512', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00513', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00514', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00515', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00516', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00517', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00518', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00521', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00522', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00523', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00524', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00538', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00543', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00544', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00545', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00546', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00549', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00550', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00551', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00553', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00554', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00561', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00562', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00563', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00566', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00567', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00568', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00569', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_00570', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_N0001', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_N0002', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'GP_MENU_N0003', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
-- NOTA: NOPORTAL_BSC NON escluso — portlet landing di GP_WE_PORTAL_4; escluderlo causa logout immediato
-- NOTA: NOPORTAL_MY NON escluso — portlet landing di GP_WE_PORTAL_3
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'NOPORTAL_DIR', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'NOPORTAL_ORG', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp) VALUES('ORGPERF_DIR_UO', 'NOPORTAL_PART', '2017-01-01 00:00:00.000', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- =====================================================================
-- SEZIONE 6: VERIFICA ESCLUSIONI
-- =====================================================================

SELECT COUNT(*) as totale_esclusioni
FROM security_group_content
WHERE group_id = 'ORGPERF_DIR_UO';


-- =====================================================================
-- RIEPILOGO CONFIGURAZIONE
-- =====================================================================
-- SECURITY GROUP: ORGPERF_DIR_UO
-- PORTALE: NULL (usa il portale del gruppo EMPLPERF_VALUTATORE se presente)
--
-- PERMESSI (10):
--   - ORGPERFDIR_VIEW (custom)
--   - ORGPERFMGR_VIEW (accesso modulo Performance Strategica)
--   - ORGPERFROLE_ADMIN (accesso role-based schede)
--   - WORKEFFORTMGR_CREATE, WORKEFFORTMGR_UPDATE (transizioni di stato)
--   - CONTENTMGR_VIEW, CONTENTMGR_ROLE_VIEW, CONTENTMGR_ROLE_CREATE
--   - BSCPERFMGR_VIEW (necessario per sidebar: chiave BSCPERF → link /stratperf/...)
--   - OFBTOOLS_VIEW
--
-- MENU ACCESSIBILI (sidebar):
--   GP_MENU_00086 Performance Strategica
--   GP_MENU_00401   > Gestione
--   GP_MENU_00092       > Definizione   (validazione parziale schede con obiettivi)
--   GP_MENU_00101       > Valutazione   (presa visione schede con risultati finali)
--   GP_MENU_00124 Performance Individuale (visibile se combinato con EMPLPERF_VALUTATORE)
--
-- ESCLUSIONI: 152 menu (contesti inattivi + PS sub-sezioni non accessibili)
-- GP_MENU_00124 e discendenti NON esclusi (gestiti da EMPLPERF_VALUTATORE)
-- =====================================================================
