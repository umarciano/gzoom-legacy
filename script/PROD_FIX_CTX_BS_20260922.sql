-- =====================================================================
-- PROD FIX schede CTX_BS - 2026-09-22
-- =====================================================================
-- Changelog (tutto in UNA transazione idempotente, con guardie):
--
--  CONFIG import (per i FUTURI import):
--   * IMPORT_SCHEDE_BS.orgTypeCode = 'UOC' (valore fisso) -> "Tipo Unita' Resp." corretta
--
--  DATI schede CTX_BS (DB popolato):
--   * [2026] org_unit_role_type_id  -> 'UOC'            (tendina "Tipo Unita' Resp." non piu' vuota)
--   * [2026] source_reference_id    -> 2026_STG_<UO>    (omologazione codici: via 'OB_' e 'PF_')
--   * [2026] work_effort_name       -> "Scheda ..."     (era "Obiettivo ...")
--   * [2026] etch (Etichetta)       -> 'PF_STG_2026'    (era 'OB_PF_STG_2026' / NULL; via 'OB_')
--   * [2025] etch (Etichetta)       -> 'PF_STG_2025'    (era 'OB_PF_STG'; via 'OB_' + anno)
--   * [2025] is_posted              -> 'Y'              (congela il ciclo chiuso in sola lettura)
--
--  VISUALIZZAZIONE:
--   * work_effort_type_content (WEFLD_MAIN) params hideTreeView='Y' su CTX_BS e CTX_EP
--     (pannello albero laterale collassato di default nel dettaglio scheda)
--
-- Sostituisce i precedenti PROD_FIX_CTX_BS.sql + CONFIG_FIX_ORGTYPE_IMPORT_SCHEDE_BS.sql.
-- Rieseguibile senza danni (ogni sezione ha WHERE/guardia che la rende idempotente).
-- =====================================================================
BEGIN;

-- ---------------------------------------------------------------------
-- CONFIG import: orgTypeCode='UOC' fisso su IMPORT_SCHEDE_BS
-- ---------------------------------------------------------------------
INSERT INTO public.standard_import_field_config
    (data_source_id, standard_interface, internal_field_name, external_field_name, default_value,
     interface_seq, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','orgTypeCode', NULL, 'UOC',
       1, NOW(), NOW(), NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM public.standard_import_field_config
    WHERE data_source_id='IMPORT_SCHEDE_BS' AND standard_interface='WE_ROOT_INTERFACE'
      AND internal_field_name='orgTypeCode');

-- ---------------------------------------------------------------------
-- [2026] Tipo Unita' Responsabile -> UOC
-- ---------------------------------------------------------------------
DO $$
DECLARE n int;
BEGIN
  SELECT count(*) INTO n FROM work_effort we
  WHERE we.work_effort_type_id='CTX_BS' AND we.source_reference_id LIKE '2026\_%'
    AND we.org_unit_role_type_id='ORGANIZATION_UNIT' AND we.org_unit_id IS NOT NULL
    AND NOT EXISTS (SELECT 1 FROM party_role pr WHERE pr.party_id=we.org_unit_id AND pr.role_type_id='UOC');
  IF n>0 THEN RAISE EXCEPTION 'STOP (tipo unita''): % schede 2026 con UO senza PartyRole UOC', n; END IF;
END $$;

UPDATE work_effort
SET org_unit_role_type_id='UOC', last_updated_stamp=now(), last_updated_tx_stamp=now()
WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_%'
  AND org_unit_role_type_id='ORGANIZATION_UNIT';

-- ---------------------------------------------------------------------
-- [2026] Codici scheda -> 2026_STG_<UO> (rimozione 'OB_' e 'PF_')
-- ---------------------------------------------------------------------
DO $$
DECLARE n int;
BEGIN
  SELECT count(*) INTO n FROM work_effort w
  JOIN work_effort x ON x.source_reference_id = regexp_replace(w.source_reference_id,'^2026_OB_(PF_)?STG_','2026_STG_')
                    AND x.work_effort_id <> w.work_effort_id
  WHERE w.work_effort_type_id='CTX_BS' AND w.source_reference_id LIKE '2026\_OB\_%';
  IF n>0 THEN RAISE EXCEPTION 'STOP (rename codici): % collisioni con codici esistenti', n; END IF;
END $$;

UPDATE work_effort
SET source_reference_id = regexp_replace(source_reference_id,'^2026_OB_(PF_)?STG_','2026_STG_'),
    last_updated_stamp=now(), last_updated_tx_stamp=now()
WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_OB\_%';

-- ---------------------------------------------------------------------
-- [2026] Titolo: "Obiettivo ..." -> "Scheda ..." (solo work_effort_name; _lang e' NULL)
-- ---------------------------------------------------------------------
UPDATE work_effort
SET work_effort_name = regexp_replace(work_effort_name,'^Obiettivo ','Scheda '),
    last_updated_stamp=now(), last_updated_tx_stamp=now()
WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_%'
  AND work_effort_name LIKE 'Obiettivo %';

-- ---------------------------------------------------------------------
-- [2026] Etichetta -> 'PF_STG_2026'
-- ---------------------------------------------------------------------
UPDATE work_effort
SET etch='PF_STG_2026', last_updated_stamp=now(), last_updated_tx_stamp=now()
WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_%'
  AND COALESCE(etch,'') <> 'PF_STG_2026';

-- ---------------------------------------------------------------------
-- [2025] Etichetta -> 'PF_STG_2025'
-- ---------------------------------------------------------------------
UPDATE work_effort
SET etch='PF_STG_2025', last_updated_stamp=now(), last_updated_tx_stamp=now()
WHERE work_effort_type_id='CTX_BS' AND source_reference_id NOT LIKE '2026\_%'
  AND COALESCE(etch,'') <> 'PF_STG_2025';

-- ---------------------------------------------------------------------
-- [2025] is_posted -> 'Y' (congela il ciclo chiuso in sola lettura)
-- ---------------------------------------------------------------------
UPDATE work_effort
SET is_posted='Y', last_updated_stamp=now(), last_updated_tx_stamp=now()
WHERE work_effort_type_id='CTX_BS' AND source_reference_id NOT LIKE '2026\_%'
  AND COALESCE(is_posted,'') <> 'Y';

-- ---------------------------------------------------------------------
-- Pannello albero laterale collassato di default: CTX_BS + CTX_EP
-- ---------------------------------------------------------------------
UPDATE work_effort_type_content
SET params='hideTreeView = "Y";', last_updated_stamp=NOW(), last_updated_tx_stamp=NOW()
WHERE content_id='WEFLD_MAIN' AND work_effort_type_id IN ('CTX_BS','CTX_EP')
  AND (params IS NULL OR params NOT LIKE '%hideTreeView%');

-- ---------------------------------------------------------------------
-- Verifica finale
-- ---------------------------------------------------------------------
SELECT 'tipo_unita_2026' AS check, org_unit_role_type_id AS valore, count(*) AS n
FROM work_effort WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_%'
GROUP BY org_unit_role_type_id
UNION ALL SELECT 'codici_OB_residui_2026','-',count(*)
FROM work_effort WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_OB\_%'
UNION ALL SELECT 'titolo_Obiettivo_residui_2026','-',count(*)
FROM work_effort WHERE work_effort_type_id='CTX_BS' AND source_reference_id LIKE '2026\_%' AND work_effort_name LIKE 'Obiettivo %'
UNION ALL SELECT 'etch',COALESCE(etch,'(NULL)'),count(*)
FROM work_effort WHERE work_effort_type_id='CTX_BS' GROUP BY etch
UNION ALL SELECT 'is_posted_2025',COALESCE(is_posted,'(NULL)'),count(*)
FROM work_effort WHERE work_effort_type_id='CTX_BS' AND source_reference_id NOT LIKE '2026\_%' GROUP BY is_posted
UNION ALL SELECT 'hideTreeView',work_effort_type_id||': '||COALESCE(params,'(NULL)'),count(*)
FROM work_effort_type_content WHERE content_id='WEFLD_MAIN' AND work_effort_type_id IN ('CTX_BS','CTX_EP') GROUP BY work_effort_type_id,params
ORDER BY 1,2;

COMMIT;
