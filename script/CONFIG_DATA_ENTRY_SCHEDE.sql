/* Query Configurazione Data Entry Schede valutazione  */
/* Definire una nuova tipologia di Data Source */
INSERT INTO public.data_source_type
(data_source_type_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, last_modified_by_user_login, created_by_user_login)
VALUES('IMPORT_CARDS', 'Data Source for Performance Cards', '2025-11-12 11:49:28.751', '2025-11-12 11:49:28.751', '2025-11-12 11:49:28.751', '2025-11-12 11:49:28.751', NULL, NULL);

/* Definire un nuovo Data Source per le schede di Valutazione */    
INSERT INTO public.data_source
(data_source_id, data_source_type_id, description, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, val_mod_id, last_modified_by_user_login, created_by_user_login)
VALUES('IMPORT_CARDS', 'IMPORT_CARDS', 'Massive Import for Performance Cards', '2025-07-04 17:48:24.918', '2025-07-04 17:48:24.918', '2025-07-04 17:48:24.918', '2025-07-04 17:48:24.918', NULL, NULL, NULL);

/* Mapping campi per l'interfaccia WE_SCHEDA_INTERFACE e WE_PARTY_INTERFACE */
-- Prima eliminiamo eventuali configurazioni esistenti per IMPORT_CARDS
DELETE FROM standard_import_field_config 
WHERE data_source_id = 'IMPORT_CARDS' 
  AND standard_interface = 'WE_SCHEDA_INTERFACE';
  
DELETE FROM standard_import_field_config 
WHERE data_source_id = 'IMPORT_CARDS' 
  AND standard_interface = 'WE_PARTY_INTERFACE';
  
-- Successivamente eseguo le INSERT
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_PARTY_INTERFACE', 'partyCode', 'Matricola Ruolo', NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_PARTY_INTERFACE', 'roleTypeId', 'Tipo Ruolo', NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_PARTY_INTERFACE', 'fromDate', 'Data Inizio Ruolo', NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_PARTY_INTERFACE', 'thruDate', 'Data Fine Ruolo', NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_PARTY_INTERFACE', 'comments', 'Note Ruolo', NULL, 2, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'weContext', 'Contesto', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'workEffortCode', 'Codice Scheda', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'sourceReferenceRootId', 'Codice Scheda', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'workEffortName', 'Nome Scheda', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'partyCode', 'Matricola Valutato', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'evaluatorCode', 'Matricola Valutatore', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'templateCode', 'Codice Template', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'estimatedStartDate', 'Data Inizio', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'estimatedCompletionDate', 'Data Fine', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'currentStatusId', 'Stato', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'description', 'Descrizione', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'operationType', NULL, 'I', 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'orgCode', 'Codice UOC', NULL, 1, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO public.standard_import_field_config
(data_source_id, standard_interface, internal_field_name, external_field_name, default_value, interface_seq, last_modified_by_user_login, created_by_user_login, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp)
VALUES('IMPORT_CARDS', 'WE_SCHEDA_INTERFACE', 'workEffortTypeId', NULL, 'CTX_EP', 1, NULL, NULL, NULL, NULL, NULL, NULL);



--- AMNEX ---
select * from party_role where party_role.party_id ='10295'; --inserire il party_id della propria UOC
INSERT INTO public.party_role
(party_id, role_type_id, last_updated_stamp, last_updated_tx_stamp, created_stamp, created_tx_stamp, parent_role_type_id, last_modified_by_user_login, created_by_user_login)
VALUES('10295', 'UOC', '2025-11-05 11:54:32.331', '2025-11-05 11:54:32.267', '2025-11-05 11:54:32.331', '2025-11-05 11:54:32.267', 'ORGANIZATION_UNIT', NULL, 'admin');


-- SEQUENCE --
SELECT * FROM sequence_value_item WHERE seq_name = 'WeSchedaInterface';
INSERT INTO sequence_value_item (seq_name, seq_id) VALUES ('WeSchedaInterface', 10000);

SELECT * FROM sequence_value_item WHERE seq_name = 'WeSchedaInterfaceExt';
INSERT INTO sequence_value_item (seq_name, seq_id) VALUES ('WeSchedaInterfaceExt', 10000);