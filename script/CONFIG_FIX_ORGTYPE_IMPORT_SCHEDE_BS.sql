-- =====================================================================
-- CONFIG FIX: orgTypeCode='UOC' fisso per il datasource IMPORT_SCHEDE_BS
-- =====================================================================
-- Correzione ALLA FONTE del disallineamento "Tipo Unita' Resp." (org_unit_role_type_id):
-- l'import delle schede CTX_BS non forniva orgTypeCode -> la UO veniva risolta al ruolo
-- generico 'ORGANIZATION_UNIT' invece di 'UOC'. Aggiungiamo la mappatura a VALORE FISSO
-- 'UOC' (come weContext='STR' e workEffortTypeId='CTX_BS'), cosi' ogni futura
-- (re)importazione crea le schede gia' con org_unit_role_type_id='UOC'.
--
-- Gia' inserita in SETUP_PERF_STRATEGICA.sql (V003) per i setup nuovi; questo script la
-- applica al DB di PRODUZIONE dove la config esiste gia' senza questa riga. Idempotente.
-- NB: NON sistema le schede gia' importate -> per quelle usare
--     POST_IMPORT_FIX_ORG_UNIT_ROLE_TYPE_2026.sql.
-- =====================================================================
BEGIN;

INSERT INTO public.standard_import_field_config
    (data_source_id, standard_interface, internal_field_name, external_field_name, default_value,
     interface_seq, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
SELECT 'IMPORT_SCHEDE_BS','WE_ROOT_INTERFACE','orgTypeCode', NULL, 'UOC',
       1, NOW(), NOW(), NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM public.standard_import_field_config
    WHERE data_source_id='IMPORT_SCHEDE_BS'
      AND standard_interface='WE_ROOT_INTERFACE'
      AND internal_field_name='orgTypeCode');

-- Verifica: deve comparire la riga orgTypeCode -> 'UOC'
SELECT internal_field_name, external_field_name, default_value
FROM public.standard_import_field_config
WHERE data_source_id='IMPORT_SCHEDE_BS' AND standard_interface='WE_ROOT_INTERFACE'
ORDER BY internal_field_name;

COMMIT;
