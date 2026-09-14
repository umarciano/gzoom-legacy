-- =====================================================================
-- APPLY una-tantum: voce menu "Catalogo indicatori" (download PDF statico) su CTX_BS
-- =====================================================================
-- Estratto di SETUP_PERF_STRATEGICA.sql V015 per applicare SOLO questa feature senza
-- rieseguire tutto il SETUP. Idempotente. La fonte durevole resta il SETUP.
--
-- Implementazione LEGACY: il PDF e' un file statico servito dalla webapp pubblica 'resources'
-- (hot-deploy/base/webapp/resources/catalogo/CatalogoIndicatori.pdf -> URL /resources/catalogo/...).
-- La foglia di menu NON ha mappatura REFURBISHED nel FE -> viene aperta nell'iframe legacy
-- (dispatcherRequest.groovy redirige al 'link' della foglia).
--
-- GATING (getValidMenu): la foglia e' visibile solo se la 'link' inizia con la chiave di un permesso
-- VIEW/ADMIN dell'utente. Poiche' la 'link' deve essere l'URL reale del PDF (/resources/...), il
-- permesso di gating deve avere chiave 'RESOURCES' -> permesso RESOURCES_VIEW, concesso a TUTTI gli
-- attori. Nessun'altra foglia punta a /resources, quindi mostra SOLO questa voce.
-- =====================================================================
-- Lanciare con:  $env:PGCLIENTENCODING="UTF8"; psql ... -v ON_ERROR_STOP=1 -f apply_menu_catalogo.sql
-- =====================================================================

BEGIN;

INSERT INTO security_permission (permission_id, description, enabled, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('RESOURCES_VIEW', 'Token gating menu: download risorse statiche pubbliche (/resources) - usato dal Catalogo indicatori CTX_BS', 'Y', 'admin',
        now(), now(), now(), now())
ON CONFLICT (permission_id) DO NOTHING;

INSERT INTO security_group_permission (group_id, permission_id,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('AORNADMIN',          'RESOURCES_VIEW', now(), now(), now(), now()),
       ('STRATPERF_DIR_UO',   'RESOURCES_VIEW', now(), now(), now(), now()),
       ('STRATPERF_DIR_SAN',  'RESOURCES_VIEW', now(), now(), now(), now()),
       ('STRATPERF_DIR_AMM',  'RESOURCES_VIEW', now(), now(), now(), now()),
       ('STRATPERF_REFERENTE','RESOURCES_VIEW', now(), now(), now(), now())
ON CONFLICT (group_id, permission_id) DO NOTHING;

DELETE FROM content_assoc     WHERE content_id_to = 'GP_MENU_00572';
DELETE FROM content_attribute WHERE content_id    = 'GP_MENU_00572';
DELETE FROM content           WHERE content_id    = 'GP_MENU_00572';

INSERT INTO content (content_id, content_type_id, status_id, mime_type_id, description, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('GP_MENU_00572', 'GPLUS_MENU_ITEM', 'CTNT_IN_PROGRESS', 'text/plain',
        'Catalogo indicatori (CTX_BS) - download PDF statico', 'admin', now(), now(), now(), now());

INSERT INTO content_attribute (content_id, attr_name, attr_value,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('GP_MENU_00572', 'title', 'MenuUiLabels.Catalogo indicatori', now(), now(), now(), now()),
       -- Link = paginetta HTML (non il PDF grezzo): body misurabile -> iframe legacy a tutta altezza.
       ('GP_MENU_00572', 'link',  '/resources/catalogo/catalogo.html', now(), now(), now(), now());

INSERT INTO content_assoc (content_id, content_id_to, content_assoc_type_id, from_date, sequence_num, created_by_user_login,
       last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES ('GP_MENU_00402', 'GP_MENU_00572', 'TREE_CHILD', TIMESTAMP '2026-01-01 00:00:00', 4, 'admin', now(), now(), now(), now());

DO $$
DECLARE n int;
BEGIN
    SELECT count(*) INTO n FROM security_group_permission
     WHERE permission_id='RESOURCES_VIEW'
       AND group_id IN ('AORNADMIN','STRATPERF_DIR_UO','STRATPERF_DIR_SAN','STRATPERF_DIR_AMM','STRATPERF_REFERENTE');
    IF n <> 5 THEN
        RAISE EXCEPTION 'RESOURCES_VIEW concesso a % gruppi su 5 (attori mancanti -> catalogo non visibile a tutti)', n;
    END IF;
END;
$$;

COMMIT;

-- ---------- Verifica ----------
SELECT 'foglia' AS oggetto, count(*) AS n FROM content WHERE content_id='GP_MENU_00572'
UNION ALL SELECT 'link',   count(*) FROM content_attribute WHERE content_id='GP_MENU_00572' AND attr_name='link' AND attr_value='/resources/catalogo/catalogo.html'
UNION ALL SELECT 'assoc',  count(*) FROM content_assoc WHERE content_id='GP_MENU_00402' AND content_id_to='GP_MENU_00572'
UNION ALL SELECT 'grant attori (att.5)', count(*) FROM security_group_permission
   WHERE permission_id='RESOURCES_VIEW'
     AND group_id IN ('AORNADMIN','STRATPERF_DIR_UO','STRATPERF_DIR_SAN','STRATPERF_DIR_AMM','STRATPERF_REFERENTE');
