-- =====================================================================
-- Profili DIRETTORE SANITARIO (ORGPERF_DIR_SAN) e AMMINISTRATIVO (ORGPERF_DIR_AMM)
-- Performance Strategica (CTX_BS) - validazione COMPLETA (VALPART -> VALIDATED)
-- =====================================================================
-- Requisiti (confermati dall'utente):
--   - Due profili distinti.
--   - Vedono TUTTE le schede CTX_BS in QUALSIASI stato, in SOLA CONSULTAZIONE.
--   - Validano con il BOTTONE "Valida" (VALPART -> VALIDATED); la UI mostra il bottone
--     automaticamente perche' l'utente appartiene a uno di questi gruppi (isDirSanAmm).
--   - Menu: Gestione > Definizione (per validare) + Consultazione > Interrogazione Schede Strategiche.
--
-- STRATEGIA: si CLONA il profilo funzionante ORGPERF_DIR_UO (10 permessi + 152 esclusioni menu),
-- MA senza copiare le esclusioni di GP_MENU_00402 (Consultazione) e GP_MENU_00104 (Interrogazione
-- Schede Strategiche), cosi' questi due profili VEDONO anche l'Interrogazione. Definizione (GP_MENU_00092)
-- e Valutazione (GP_MENU_00101) restano visibili come per DIR_UO (non erano escluse).
--
-- SCOPING: NON essendo nel gruppo ORGPERF_DIR_UO, il groovy executePerformFindBSWorkEffortRoot NON
-- applica lo scoping per UO -> questi profili vedono TUTTE le schede (ramo "vedi tutto"). Sola lettura
-- garantita dall'assenza dei permessi BSCPERF*_ADMIN (come per DIR_UO, gia' verificato read-only).
--
-- UTENTI: mariomassimo.mensorio (SAN, matr. 53265) e marcella.abbate (AMM, matr. 53228).
-- Idempotente: guardato con \if sull'esistenza del gruppo. Prerequisito: ORGPERF_DIR_UO deve esistere.
-- =====================================================================

-- ---------------------------------------------------------------------
-- ORGPERF_DIR_SAN (Direttore Sanitario)
-- ---------------------------------------------------------------------
SELECT NOT EXISTS(SELECT 1 FROM security_group WHERE group_id='ORGPERF_DIR_SAN') AS need_dir_san \gset
\if :need_dir_san

INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('ORGPERF_DIR_SAN', 'Performance Strategica - Direttore Sanitario', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'admin', NULL);

-- Permessi: clone integrale da ORGPERF_DIR_UO
INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'ORGPERF_DIR_SAN', permission_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM public.security_group_permission WHERE group_id='ORGPERF_DIR_UO';

-- Esclusioni menu: clone da ORGPERF_DIR_UO tranne Consultazione (00402) e Interrogazione (00104)
INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'ORGPERF_DIR_SAN', content_id, from_date, thru_date, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM public.security_group_content
WHERE group_id='ORGPERF_DIR_UO' AND content_id NOT IN ('GP_MENU_00402','GP_MENU_00104');

-- Utente: Mario Massimo Mensorio
INSERT INTO public.user_login_security_group (user_login_id, group_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('mariomassimo.mensorio', 'ORGPERF_DIR_SAN', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\endif

-- ---------------------------------------------------------------------
-- ORGPERF_DIR_AMM (Direttore Amministrativo)
-- ---------------------------------------------------------------------
SELECT NOT EXISTS(SELECT 1 FROM security_group WHERE group_id='ORGPERF_DIR_AMM') AS need_dir_amm \gset
\if :need_dir_amm

INSERT INTO public.security_group
(group_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, default_portal_page_id, last_modified_by_user_login, created_by_user_login)
VALUES('ORGPERF_DIR_AMM', 'Performance Strategica - Direttore Amministrativo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL, 'admin', NULL);

INSERT INTO public.security_group_permission (group_id, permission_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'ORGPERF_DIR_AMM', permission_id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM public.security_group_permission WHERE group_id='ORGPERF_DIR_UO';

INSERT INTO public.security_group_content (group_id, content_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'ORGPERF_DIR_AMM', content_id, from_date, thru_date, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM public.security_group_content
WHERE group_id='ORGPERF_DIR_UO' AND content_id NOT IN ('GP_MENU_00402','GP_MENU_00104');

-- Utente: Marcella Abbate
INSERT INTO public.user_login_security_group (user_login_id, group_id, from_date, thru_date, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('marcella.abbate', 'ORGPERF_DIR_AMM', CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

\endif

-- Verifica finale
SELECT group_id, (SELECT COUNT(*) FROM security_group_permission p WHERE p.group_id=g.group_id) AS permessi,
       (SELECT COUNT(*) FROM security_group_content c WHERE c.group_id=g.group_id) AS esclusioni_menu,
       (SELECT string_agg(user_login_id,', ') FROM user_login_security_group u WHERE u.group_id=g.group_id) AS utenti
FROM security_group g WHERE g.group_id IN ('ORGPERF_DIR_UO','ORGPERF_DIR_SAN','ORGPERF_DIR_AMM') ORDER BY group_id;
