-- =====================================================================
-- CONFIGURAZIONE SECURITY PROFILE AORNADMIN
-- =====================================================================
-- Script per configurare il profilo di sicurezza AORNADMIN con:
-- - 95 permessi amministrativi (security_group_permission)
-- - 41 esclusioni menu (security_group_content)
--
-- CONTESTI ABILITATI:
--   - Performance Strategica (CTX_BS)
--   - Employee Performance (CTX_EP)
--   - Base/Tools (CTX_BA)
--   - Accounting (CTX_AC)
--   - Payroll (CTX_PY - solo permessi)
--   - Party Management, Human Resources, Work Effort
--
-- CONTESTI ESCLUSI:
--   - GOVERNANCE: CTX_GD (GDPR), CTX_PR (Processi), CTX_CO (Anticorruzione)
--   - ACCOUNTABILITY: CTX_CG, CTX_TR, CTX_DI, CTX_PA, CTX_RE
--   - PERFORMANCE OPERATIVA: CTX_OR
-- =====================================================================

-- =====================================================================
-- SEZIONE 0: CONFIGURAZIONE UTENTE ADMIN (PREREQUISITI)
-- =====================================================================
-- Crea l'utente amministratore 'admin' se non esiste già.
-- Questo utente è necessario per:
-- - Essere assegnato al security group AORNADMIN
-- - Avere accesso completo al sistema
-- - Essere utilizzato come utente di sistema per operazioni automatiche
--
-- NOTA: Le INSERT utilizzano ON CONFLICT DO NOTHING per evitare errori
--       se i record esistono già nel database.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 0.1 CREAZIONE PARTY ADMIN
-- ---------------------------------------------------------------------
-- Crea il party per l'utente admin (tipo PERSON)

INSERT INTO public.party
(party_id, party_type_id, external_id, preferred_currency_uom_id, description, status_id, created_date, created_by_user_login, last_modified_date, last_modified_by_user_login, data_source_id, is_unread, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, fiscal_code, vat_code, party_name, description_lang, end_date, party_name_lang)
VALUES('admin', 'PERSON', NULL, NULL, NULL, 'PARTY_ENABLED', NULL, NULL, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, NULL, NULL, NULL, NULL)
ON CONFLICT (party_id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 0.2 CREAZIONE PERSON ADMIN
-- ---------------------------------------------------------------------
-- Crea i dati anagrafici della persona admin

INSERT INTO public.person
(party_id, salutation, first_name, middle_name, last_name, personal_title, suffix, nickname, first_name_local, middle_name_local, last_name_local, other_local, member_id, gender, birth_date, deceased_date, height, weight, mothers_maiden_name, marital_status, social_security_number, passport_number, passport_expire_date, total_years_work_experience, "comments", employment_status_enum_id, residence_status_enum_id, occupation, years_with_employer, months_with_employer, existing_customer, card_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, birth_place, birth_country, number_of_child, empl_position_type_id, empl_position_type_date, employment_amount, last_modified_by_user_login, created_by_user_login, person_position)
VALUES('admin', NULL, 'AMMINISTRATORE', 'DI', 'SISTEMA', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)
ON CONFLICT (party_id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 0.3 CONFIGURAZIONE USER_LOGIN ADMIN
-- ---------------------------------------------------------------------
-- Aggiorna le credenziali dell'utente admin
-- Password: "ofbiz" (hash SHA: 47ca69ebb4bdc9ae0adec130880165d2cc05db1a)
-- ATTENZIONE: Cambiare la password dopo il primo accesso!

UPDATE public.user_login
SET 
    current_password = '{SHA}47ca69ebb4bdc9ae0adec130880165d2cc05db1a',
    password_hint = NULL,
    is_system = NULL,
    enabled = 'Y',
    has_logged_out = 'N',
    require_password_change = 'N',
    last_currency_uom = NULL,
    last_locale = 'it_IT',
    last_time_zone = NULL,
    disabled_date_time = NULL,
    successive_failed_logins = NULL,
    external_auth_id = NULL,
    user_ldap_dn = NULL,
    last_updated_stamp = CURRENT_TIMESTAMP,
    last_updated_tx_stamp = CURRENT_TIMESTAMP,
    created_stamp = CURRENT_TIMESTAMP,
    created_tx_stamp = CURRENT_TIMESTAMP,
    party_id = 'admin',
    external_system = NULL,
    description = NULL
WHERE user_login_id = 'admin';


-- =====================================================================
-- SEZIONE 1: CREAZIONE SECURITY GROUP
-- =====================================================================
-- Crea il security group AORNADMIN con portale predefinito GP_WE_PORTAL_2

INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('AORNADMIN', 'Amministratore di Sistema AORN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'GP_WE_PORTAL_2', 'admin', NULL);

INSERT INTO public.user_login_security_group
(user_login_id, group_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('admin', 'AORNADMIN', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (user_login_id, group_id, from_date) DO NOTHING;

-- =====================================================================
-- SEZIONE 2: PERMESSI SECURITY_GROUP_PERMISSION (95 permessi totali)
-- =====================================================================

-- ---------------------------------------------------------------------
-- 2.1 PERMESSI CORE (6 permessi fondamentali)
-- ---------------------------------------------------------------------
-- Permesso critico di visualizzazione amministratore
INSERT INTO public.security_permission
(permission_id, description, dynamic_access, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, enabled, last_modified_by_user_login, created_by_user_login)
VALUES('ADMINISTRATOR_VIEW', 'Permesso di visualizzazione read-only per amministratori', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'Y', NULL, NULL);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('FULLADMIN', 'ADMINISTRATOR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ADMINISTRATOR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Work Effort Management (5 permessi)
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTMGR_CREATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTMGR_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTORG_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WORKEFFORTMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.2 BASE & TOOLS (4 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'TIMESHEET_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'UOM_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'UOMTYPE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'VISITORS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.3 REPORT PRINT (2 permessi - CTX_BS e CTX_EP)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'REPORT_PRINT_CTX_BS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'REPORT_PRINT_CTX_EP_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.4 QUERY CONFIG (10 permessi - BA, BS, EP, AC, PY con E e U)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERYCONFIG_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_BA_E_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_BS_E_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_EP_E_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_AC_E_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_PY_E_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_BS_U_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_EP_U_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_CA_U_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'QUERY_CONFIG_CTX_PY_U_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.5 ANALYSIS (2 permessi - CTX_BS e CTX_EP)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ANALYSIS_CTX_BS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ANALYSIS_CTX_EP_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.6 CTX_BA TOOLS (6 permessi - Interfacciamento dati e tipologie)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'INTERFACCIAMENTODATI_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'TIPOLOGIE_PERIODI_CTX_BA_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'TYPOLOGY_RELATIONSHIPS_OBJECTIVES_CTX_BA_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'OBJECTIVE_CODES_CTX_BA_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'SUBSYSTEM_TYPES_CTX_BA_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'DETECTION_TYPE_CTX_BA_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.7 SYSTEM ADMIN & TOOLS (15 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'CUSTOM_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'OFBTOOLS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'WEBTOOLS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'SERVER_STATS_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ARTIFACT_INFO_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'LABEL_MANAGER_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'DATAFILE_MAINT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'SERVICE_MAINT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PERIOD_MAINT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PORTALPAGE_MAINT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ENUM_STATUS_MAINT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ENTITY_MAINT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ENTITY_DATA_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'UTIL_CACHE_EDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'UTIL_CACHE_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'UTIL_DEBUG_EDIT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'UTIL_DEBUG_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.8 COMMON & VISUAL THEME (6 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'VISUALTHEME_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'COMMON_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'USERPREF_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'TEMPEXPR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ENTITY_SYNC_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'SERVICE_INVOKE_ANY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.9 ACCOUNTING (9 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PAYPROC_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PAY_INFO_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'MANUAL_PAYMENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_COMM_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTING_PRINT_CHECKS', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCTG_PREF_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCTG_FX_UPDATE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCTG_ATX_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.10 CONTENT & COMMON EXTENSIONS (3 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'COMMONEXT_PUBLMSG', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'CONTENTMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'SEND_CONTROL_APPLET', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.11 HUMAN RESOURCES (1 permesso)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'HUMANRES_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.12 PARTY MANAGEMENT (2 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PARTYMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'SECURITY_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.13 ORDER & CATALOG (7 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ORDERMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'CATALOG_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'CATALOG_PRICE_MAINT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'CATALOG_VIEW_ALLOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'CATALOG_PURCHASE_ALLOW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'SHIPRATE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'FACILITY_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.14 BASE EXTENSIONS (5 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BASE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'MARKETING_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BASE_ASYNCJOB_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'MANUFACTURING_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'SFA_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'JBPM_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.15 APPLICATION EXTENSIONS (4 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'COMMONEXT_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'HUMANRESEXT_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'ACCOUNTINGEXT_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'UPDMEASUREEXT_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.16 PERFORMANCE MANAGERS (3 permessi - BSC, Employee, Management)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'EMPLPERFMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'BSCPERFMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'MANAGACCMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.17 PRODUCT & GZOPE (2 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PRODUCTEXT_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GZOPE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.18 PORTAL MANAGEMENT (4 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'MYPORTALBASE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PROJECTMGR_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'PROJECTMGREXT_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'MYPORTALEXT_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 2.19 GZOPE VIEW PERMISSIONS (7 permessi)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GZOPE_MYPORTALEXT_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GZOPE_PROJECTMGREXT_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GZOPE_CONTENTMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GZOPE_HUMANRES_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GZOPE_MARKETING_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GZOPE_ORDERMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_permission
(group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GZOPE_PARTYMGR_VIEW', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- =====================================================================
-- SEZIONE 3: ESCLUSIONI MENU - SECURITY_GROUP_CONTENT (41 esclusioni)
-- =====================================================================
-- Menu NON visibili per il profilo AORNADMIN

-- ---------------------------------------------------------------------
-- 3.1 ESCLUSIONI BASE (12 esclusioni)
-- ---------------------------------------------------------------------
-- Esclusione Report Print CTX_BS
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00445', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Report Print CTX_EP
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00447', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Query Config CTX_BS Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00488', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Query Config CTX_EP Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00490', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Analysis CTX_BS
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00550', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Analysis CTX_EP
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00552', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione GP_MENU_00139
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00139', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Query Config CTX_EP Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00496', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione Query Config CTX_BS Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00494', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione portale MY
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'NOPORTAL_MY', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione GP_MENU_00195
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00195', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Esclusione GP_MENU_00193
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00193', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.2 GOVERNANCE - ANTICORRUZIONE (2 esclusioni)
-- ---------------------------------------------------------------------
-- Query Config CTX_CO Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00487', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_CO Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00493', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.3 GOVERNANCE - PROCESSI (3 esclusioni)
-- ---------------------------------------------------------------------
-- Query Config CTX_PR Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00508', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_PR Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00513', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Timesheet CTX_PR
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00563', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.4 GOVERNANCE - BASE & GDPR (3 esclusioni)
-- ---------------------------------------------------------------------
-- Base Timesheet
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00334', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_GD Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00491', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_GD Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00497', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.5 ACCOUNTABILITY - CONTROLLO GESTIONE (2 esclusioni)
-- ---------------------------------------------------------------------
-- Query Config CTX_CG Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00507', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_CG Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00512', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.6 ACCOUNTABILITY - TRASPARENZA (2 esclusioni)
-- ---------------------------------------------------------------------
-- Query Config CTX_TR Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00498', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_TR Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00492', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.7 ACCOUNTABILITY - RENDICONTAZIONE (2 esclusioni)
-- ---------------------------------------------------------------------
-- Query Config CTX_RE Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00506', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_RE Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00511', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.8 ACCOUNTABILITY - DIRIGENTI (2 esclusioni)
-- ---------------------------------------------------------------------
-- Query Config CTX_DI Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00510', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_DI Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00505', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.9 ACCOUNTABILITY - PARTECIPATE (2 esclusioni)
-- ---------------------------------------------------------------------
-- Query Config CTX_PA Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00509', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_PA Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00504', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.10 PERFORMANCE OPERATIVA (3 esclusioni)
-- ---------------------------------------------------------------------
-- Query Config CTX_OR Entità
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00489', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Query Config CTX_OR Aggregata
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00495', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Timesheet CTX_OR
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00562', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- ---------------------------------------------------------------------
-- 3.11 CTX_BA TOOLS (5 esclusioni)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00570', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00567', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00568', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00486', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00569', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ---------------------------------------------------------------------
-- 3.12 CTX_PY PAYROLL (2 esclusioni)
-- ---------------------------------------------------------------------
INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00546', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00545', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00566', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00081', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00347', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00332', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00348', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00561', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00543', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO public.security_group_content
(group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('AORNADMIN', 'GP_MENU_00544', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


-- =====================================================================
-- SEZIONE 4: VERIFICA CONFIGURAZIONE
-- =====================================================================

-- Conteggio permessi assegnati ad AORNADMIN (deve essere 95)
SELECT COUNT(*) as totale_permessi
FROM security_group_permission
WHERE group_id = 'AORNADMIN';

-- Conteggio esclusioni menu configurate (deve essere 41)
SELECT COUNT(*) as totale_esclusioni
FROM security_group_content
WHERE group_id = 'AORNADMIN'
  AND (thru_date IS NULL OR thru_date > CURRENT_TIMESTAMP);


-- =====================================================================
-- RIEPILOGO CONFIGURAZIONE
-- =====================================================================
-- TOTALE: 95 permessi + 41 esclusioni
--
-- PERMESSI SUDDIVISI IN:
-- - 6 Core (ADMINISTRATOR_VIEW + WORKEFFORT)
-- - 4 Base & Tools
-- - 2 Report Print (CTX_BS, CTX_EP)
-- - 10 Query Config (BA, BS, EP, AC, PY)
-- - 2 Analysis (CTX_BS, CTX_EP)
-- - 6 CTX_BA Tools
-- - 15 System Admin & Tools
-- - 6 Common & Visual Theme
-- - 9 Accounting
-- - 3 Content & Common Extensions
-- - 1 Human Resources
-- - 2 Party Management
-- - 7 Order & Catalog
-- - 5 Base Extensions
-- - 4 Application Extensions
-- - 3 Performance Managers
-- - 2 Product & GZOPE
-- - 4 Portal Management
-- - 7 GZOPE View Permissions
--
-- ESCLUSIONI SUDDIVISE IN:
-- - 12 Base
-- - 8 GOVERNANCE (Anticorruzione, Processi, GDPR)
-- - 12 ACCOUNTABILITY (Controllo Gestione, Trasparenza, Rendicontazione, Dirigenti, Partecipate)
-- - 3 Performance Operativa
-- - 5 CTX_BA Tools
-- - 2 CTX_PY Payroll
-- =====================================================================
