/* 	QUERY CONFIGURAZIONE DATA ENTRY GZOOM
	DIPARTIMENTI E UOC
*/

/* <!-- CREARE CARTELLA tmp in gzoom-legacy/runtime --!> */

/* Definire una nuova tipologia di Data Source */
INSERT INTO public.data_source_type
(data_source_type_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, last_modified_by_user_login, created_by_user_login)
VALUES('IMPORT_DIP_UOC', 'Data Source for Department and UOC', '2025-07-04 17:48:24.921', '2025-07-04 17:48:24.877', '2025-07-04 17:48:24.921', '2025-07-04 17:48:24.877', NULL, NULL);

/* Definire un nuovo data Source per Dipartimenti e UOC */
INSERT INTO data_source (
    data_source_id,
    data_source_type_id,
    description
) VALUES (
    'IMPORT_DIP_UOC',
    'IMPORT_DIP_UOC',
    'Massive Import for Department and UOC'
)
ON CONFLICT (data_source_id) DO NOTHING;

/* Mapping campi */
INSERT INTO standard_import_field_config (
    data_source_id, 
    standard_interface, 
    internal_field_name, 
    external_field_name,
    interface_seq
) VALUES
('IMPORT_DIP_UOC', 'ORGANIZATION_INTERFACE', 'orgCode', 'UOC Code', 1),
('IMPORT_DIP_UOC', 'ORGANIZATION_INTERFACE', 'description', 'Description', 1),
('IMPORT_DIP_UOC', 'ORGANIZATION_INTERFACE', 'orgRoleTypeId', 'Unit Type', 1),
('IMPORT_DIP_UOC', 'ORGANIZATION_INTERFACE', 'parentOrgCode', 'Parent UOC Code', 1),
('IMPORT_DIP_UOC', 'ORGANIZATION_INTERFACE', 'parentRoleTypeId', 'Parent Unit Type', 1),
('IMPORT_DIP_UOC', 'ORGANIZATION_INTERFACE', 'responsibleCode', 'Responsible Code', 1),
('IMPORT_DIP_UOC', 'ORGANIZATION_INTERFACE', 'refDate', 'Reference Date', 1),
('IMPORT_DIP_UOC', 'ORGANIZATION_INTERFACE', 'thruDate', 'End Date', 1);