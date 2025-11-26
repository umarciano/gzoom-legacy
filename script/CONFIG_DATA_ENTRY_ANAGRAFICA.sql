/* Query Configurazione Data Entry Risorse Umane  */
/* Definire una nuova tipologia di Data Source */
INSERT INTO public.data_source_type
(	data_source_type_id, 
	description, 
	last_updated_stamp, 
	last_updated_tx_stamp, 
	created_stamp, 
	created_tx_stamp, 
	last_modified_by_user_login, 
	created_by_user_login)
VALUES(	'IMPORT_HR', 
		'Data Source for Human Resources', 
		CURRENT_TIMESTAMP, 
		CURRENT_TIMESTAMP, 
		CURRENT_TIMESTAMP, 
		CURRENT_TIMESTAMP, 
		NULL, 
		NULL);

/* Definire un nuovo Data Source per Risorse Umane */
INSERT INTO data_source (
    data_source_id,
    data_source_type_id,
    description
) VALUES (
    'IMPORT_HR',
    'IMPORT_HR',
    'Import Massivo Risorse Umane'
)
ON CONFLICT (data_source_id) DO NOTHING;

/* Mapping campi per l'interfaccia PERSON_INTERFACE */
-- Prima eliminiamo eventuali configurazioni esistenti per IMPORT_HR
DELETE FROM standard_import_field_config 
WHERE data_source_id = 'IMPORT_HR' 
  AND standard_interface = 'PERSON_INTERFACE';

-- Successivamente eseguo le INSERT
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'personCode', 'Person Code', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'firstName', 'First Name', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'lastName', 'Last Name', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'fiscalCode', 'Fiscal Code', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'personRoleTypeId', 'Person Role Type', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'emplPositionTypeId', 'Employment Position Type', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'qualifCode', 'Qualification Code', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'qualifFromDate', 'Qualification From Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'employmentAmount', 'Employment Amount', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'fromDate', 'Employment Start Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'thruDate', 'Employment End Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'employmentOrgCode', 'Employment Org Code', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'employmentRoleTypeId', 'Employment Org Role Type', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'employmentOrgDescription', 'Employment Org Description', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'employmentOrgComments', 'Employment Org Comments', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'employmentOrgFromDate', 'Employment Org From Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'employmentOrgThruDate', 'Employment Org End Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'allocationOrgCode', 'Allocation Org Code', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'allocationRoleTypeId', 'Allocation Org Role Type', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'allocationOrgDescription', 'Allocation Org Description', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'allocationOrgComments', 'Allocation Org Comments', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'allocationOrgFromDate', 'Allocation Org From Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'allocationOrgThruDate', 'Allocation Org End Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'evaluatorCode', 'Evaluator Code', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'evaluatorFromDate', 'Evaluator From Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'isEvalManager', 'Is Evaluation Manager', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'approverCode', 'Approver Code', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'contactMail', 'Email', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'contactMobile', 'Mobile Phone', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'userLoginId', 'User Login ID', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'groupId', 'Group Profile ID', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'workEffortAssignmentCode', 'Work Effort Assignment Code', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'workEffortDate', 'Work Effort Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'emplPositionTypeDate', 'Employment Position Type Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'description', 'Description', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'comments', 'Comments', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'refDate', 'Reference Date', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_HR', 'PERSON_INTERFACE', 'dataSource', 'Data Source', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);

