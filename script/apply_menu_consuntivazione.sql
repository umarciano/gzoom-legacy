-- =====================================================================
-- APPLY una-tantum: menu Consuntivazione (Portale Referente) su CTX_BS
-- =====================================================================
-- Estratto di SETUP_PERF_ORGANIZZATIVA.sql V012+V013 per applicare SOLO questa feature
-- senza rieseguire tutto il SETUP. Idempotente. La fonte durevole resta il SETUP.
--   V012: profilo ORGPERF_REFERENTE + permesso CONSUNT_CTX_BS_VIEW + membership 44 referenti + grant admin (AORNADMIN)
--   V013: foglia menu GP_MENU_00571 sotto GP_MENU_00402 (Consultazione), link /consuntCtxBs
-- =====================================================================

-- ---------- V012 ----------
BEGIN;

INSERT INTO security_permission (permission_id, description, enabled, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('CONSUNT_CTX_BS_VIEW', 'Referente indicatore - Consuntivazione Performance Strategica (CTX_BS)', 'Y', 'admin',
        now(), now(), now(), now())
ON CONFLICT (permission_id) DO NOTHING;

INSERT INTO security_group (group_id, description, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('ORGPERF_REFERENTE', 'Performance Strategica - Referente Indicatore (consuntivazione)', 'admin',
        now(), now(), now(), now())
ON CONFLICT (group_id) DO NOTHING;

INSERT INTO security_group_permission (group_id, permission_id,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('ORGPERF_REFERENTE', 'CONSUNT_CTX_BS_VIEW', now(), now(), now(), now())
ON CONFLICT (group_id, permission_id) DO NOTHING;

-- Admin (AORNADMIN) vede SEMPRE la consuntivazione
INSERT INTO security_group_permission (group_id, permission_id,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('AORNADMIN', 'CONSUNT_CTX_BS_VIEW', now(), now(), now(), now())
ON CONFLICT (group_id, permission_id) DO NOTHING;

-- NB: la membership referenti sta in POST_IMPORT_ASSEGNA_PROFILI.sql (non qui).
-- L'admin (AORNADMIN) vede la voce grazie al grant permesso qui sopra.

COMMIT;

-- ---------- V013 ----------
BEGIN;

DELETE FROM content_assoc     WHERE content_id_to = 'GP_MENU_00571';
DELETE FROM content_attribute WHERE content_id    = 'GP_MENU_00571';
DELETE FROM content           WHERE content_id    = 'GP_MENU_00571';

INSERT INTO content (content_id, content_type_id, status_id, mime_type_id, description, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('GP_MENU_00571', 'GPLUS_MENU_ITEM', 'CTNT_IN_PROGRESS', 'text/plain',
        'Portale Referente - Consuntivazione indicatori (CTX_BS)', 'admin', now(), now(), now(), now());

INSERT INTO content_attribute (content_id, attr_name, attr_value,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('GP_MENU_00571', 'title', 'MenuUiLabels.Consuntivazione indicatori', now(), now(), now(), now()),
       ('GP_MENU_00571', 'link',  '/consuntCtxBs', now(), now(), now(), now());

INSERT INTO content_assoc (content_id, content_id_to, content_assoc_type_id, from_date, sequence_num, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('GP_MENU_00402', 'GP_MENU_00571', 'TREE_CHILD', TIMESTAMP '2026-01-01 00:00:00', 3, 'admin', now(), now(), now(), now());

COMMIT;

-- ---------- Verifica ----------
SELECT 'foglia' AS oggetto, count(*) AS n FROM content WHERE content_id='GP_MENU_00571'
UNION ALL SELECT 'grant AORNADMIN', count(*) FROM security_group_permission WHERE group_id='AORNADMIN' AND permission_id='CONSUNT_CTX_BS_VIEW';
-- (la membership referenti/direttori si applica con POST_IMPORT_ASSEGNA_PROFILI.sql)
