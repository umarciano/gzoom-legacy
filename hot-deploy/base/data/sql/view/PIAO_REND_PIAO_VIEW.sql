create or replace view REND_PIAO_VIEW AS 
select
	val.work_effort_id as ID_VALORE,
	val.work_effort_name as NOME_VALORE,
	val.work_effort_name_lang as NOME_VALORE_LANG,
	val.source_reference_id as CODICE_VALORE,
	val.etch as ETCH_VALORE,
	val.estimated_start_date as DATA_INIZIO_VALORE,
	val.estimated_completion_date as DATA_FINE_VALORE,
	dim.work_effort_id as ID_DIMENSIONE,
	dim.work_effort_name as NOME_DIMENSIONE,
	dim.work_effort_name_lang as NOME_DIMENSIONE_LANG,
	dim.source_reference_id as CODICE_DIMENSIONE,
	dim.etch as ETCH_DIMENSIONE,
	dim.estimated_start_date as DATA_INIZIO_DIMENSIONE,
	dim.estimated_completion_date as DATA_FINE_DIMENSIONE,
	sub.work_effort_id as ID_SOTTODIMENSIONE,
	sub.work_effort_name as NOME_SOTTODIMENSIONE,
	sub.work_effort_name_lang as NOME_SOTTODIMENSIONE_LANG,
	sub.source_reference_id as CODICE_SOTTODIMENSIONE,
	sub.etch as ETCH_SOTTODIMENSIONE,
	sub.estimated_start_date as DATA_INIZIO_SOTTODIMENSIONE,
	sub.estimated_completion_date as DATA_FINE_SOTTODIMENSIONE,
	art.work_effort_id as ID_ARTICOLAZIONE,
	art.work_effort_name as NOME_ARTICOLAZIONE,
	art.work_effort_name_lang as NOME_ARTICOLAZIONE_LANG,
	art.source_reference_id as CODICE_ARTICOLAZIONE,
	art.etch as ETCH_ARTICOLAZIONE,
	art.estimated_start_date as DATA_INIZIO_ARTICOLAZIONE,
	art.estimated_completion_date as DATA_FINE_ARTICOLAZIONE,
	subsez.work_effort_id as ID_SOTTOSEZIONE,
	subsez.work_effort_name as NOME_SOTTOSEZIONE,
	subsez.work_effort_name_lang as NOME_SOTTOSEZIONE_LANG,
	subsez.source_reference_id as CODICE_SOTTOSEZIONE,
	subsez.etch as ETCH_SOTTOSEZIONE,
	subsez.estimated_start_date as DATA_INIZIO_SOTTOSEZIONE,
	subsez.estimated_completion_date as DATA_FINE_SOTTOSEZIONE,
	sez.work_effort_id as ID_SEZIONE,
	sez.work_effort_name as NOME_SEZIONE,
	sez.work_effort_name_lang as NOME_SEZIONE_LANG, 
	sez.source_reference_id as CODICE_SEZIONE,
	sez.etch as ETCH_SEZIONE,
	sez.estimated_start_date as DATA_INIZIO_SEZIONE,
	sez.estimated_completion_date as DATA_FINE_SEZIONE,
	obo.work_effort_id as ID_OBIETTIVO_OPERATIVO,
	obo.work_effort_name as NOME_OBIETTIVO_OPERATIVO,
	obo.work_effort_name_lang as NOME_OBIETTIVO_OPERATIVO_LANG,
	obo.source_reference_id as CODICE_OBIETTIVO_OPERATIVO,
	obo.etch as ETCH_OBIETTIVO_OPERATIVO,
	obo.estimated_start_date as DATA_INIZIO_OBIETTIVO_OPERATIVO,
	obo.estimated_completion_date as DATA_FINE_OBIETTIVO_OPERATIVO,
	obo.org_unit_id as ID_STRUTTURA,
	obs.work_effort_id as ID_OBIETTIVO_STRATEGICO,
	obs.work_effort_name as NOME_OBIETTIVO_STRATEGICO,
	obs.work_effort_name_lang as NOME_OBIETTIVO_STRATEGICO_LANG,
	obs.source_reference_id as CODICE_OBIETTIVO_STRATEGICO,
	obs.etch as ETCH_OBIETTIVO_STRATEGICO,
	obs.estimated_start_date as DATA_INIZIO_OBIETTIVO_STRATEGICO,
	obs.estimated_completion_date as DATA_FINE_OBIETTIVO_STRATEGICO,
	ares.work_effort_id as ID_AREA_STRATEGICA,
	ares.work_effort_name as NOME_AREA_STRATEGICA,
	ares.work_effort_name_lang as NOME_AREA_STRATEGICA_LANG,
	ares.source_reference_id as CODICE_AREA_STRATEGICA,
	ares.etch as ETCH_AREA_STRATEGICA,
	ares.estimated_start_date as DATA_INIZIO_AREA_STRATEGICA,
	ares.estimated_completion_date as DATA_FINE_AREA_STRATEGICA,
	prog.work_effort_id as ID_PROGRAMMA,
	prog.work_effort_name as NOME_PROGRAMMA,
	prog.work_effort_name_lang as NOME_PROGRAMMA_LANG, 
	prog.source_reference_id as CODICE_PROGRAMMA,
	prog.etch as ETCH_PROGRAMMA,
	prog.estimated_start_date as DATA_INIZIO_PROGRAMMA,
	prog.estimated_completion_date as DATA_FINE_PROGRAMMA,
	miss.work_effort_id as ID_MISSIONE,
	miss.work_effort_name as NOME_MISSIONE,
	miss.work_effort_name_lang as NOME_MISSIONE_LANG,
	miss.source_reference_id as CODICE_MISSIONE,
	miss.etch as ETCH_MISSIONE,
	miss.estimated_start_date as DATA_INIZIO_MISSIONE,
	miss.estimated_completion_date as DATA_FINE_MISSIONE,
	ppr.parent_role_code as CODICE_STRUTTURA,
	p.party_name as NOME_STRUTTURA,
	p.party_name_lang as NOME_STRUTTURA_LANG,
	nd.note_name as NOME_NOTA,
	nd.note_info as DESCRIZIONE
/* VALORE PUBBLICO */
from work_effort val
/* DIMENSIONE VALORE PUBBLICO */
join work_effort_assoc wea on wea.work_effort_id_from = val.work_effort_id and wea.work_effort_assoc_type_id <> '20R6'
join work_effort dim on dim.work_effort_id = wea.work_effort_id_to and dim.work_effort_type_id = '20R62DIM' and dim.work_effort_revision_id is null
/* SOTTODIMENSIONE VALORE PUBBLICO */
join work_effort_assoc wea2 on wea2.work_effort_id_from = dim.work_effort_id and wea2.work_effort_assoc_type_id = '20R6'
join work_effort sub on sub.work_effort_id = wea2.work_effort_id_to and sub.work_effort_type_id = '20R64SUB' and sub.work_effort_revision_id is null
/* ARTICOLAZIONE VALORE PUBBLICO */
join work_effort_assoc wea3 on wea3.work_effort_id_from = sub.work_effort_id and wea3.work_effort_assoc_type_id = '20R6'
join work_effort art on art.work_effort_id = wea3.work_effort_id_to and art.work_effort_type_id = '20R66ART' and art.work_effort_revision_id is null
/* SOTTOSEZIONE PIAO */
left join work_effort_assoc wea4 on wea4.work_effort_id_to = art.work_effort_id and wea4.work_effort_assoc_type_id = '20R6R'
left join work_effort subsez on subsez.work_effort_id = wea4.work_effort_id_from and subsez.work_effort_type_id = '20R44SUB' and subsez.work_effort_revision_id is null
/* SEZIONE PIAO */
left join work_effort_assoc wea5 on wea5.work_effort_id_to = subsez.work_effort_id and wea5.work_effort_assoc_type_id = '20R4'
left join work_effort sez on sez.work_effort_id = wea5.work_effort_id_from and sez.work_effort_type_id = '20R42SEZ' and sez.work_effort_revision_id is null
/* OBIETTIVO OPERATIVO */
join work_effort_assoc weaobo on weaobo.work_effort_id_from = art.work_effort_id and weaobo.work_effort_assoc_type_id = '20R6C'
join work_effort obo on obo.work_effort_id = weaobo.work_effort_id_to and obo.work_effort_type_id = '20D66OOP' and obo.work_effort_revision_id is null
/* STRUTTURA RIFERIMENTO OBIETTIVO OPERATIVO */
join party p on p.party_id = obo.org_unit_id 
join party_parent_role ppr on ppr.party_id = p.party_id and ppr.role_type_id = 'ORGANIZATION_UNIT'
/* DESCRIZIONE OBIETTIVO OPERATIVO */
left join work_effort_note wen on wen.work_effort_id = obo.work_effort_id and wen.is_main = 'Y'
left join note_data nd on nd.note_id = wen.note_id
/* OBIETTIVO STRATEGICO */
left join work_effort_assoc wea6 on wea6.work_effort_id_to = obo.work_effort_id and wea6.work_effort_assoc_type_id = '20D6C'
left join work_effort obs on obs.work_effort_id = wea6.work_effort_id_from and obs.work_effort_type_id = '20D64OST' and obs.work_effort_revision_id is null
/* AREA STRATEGICA */
left join work_effort_assoc wea7 on wea7.work_effort_id_to = obs.work_effort_id and wea7.work_effort_assoc_type_id = '20D6'
left join work_effort ares on ares.work_effort_id = wea7.work_effort_id_from and ares.work_effort_type_id = '20D62LIN' and ares.work_effort_revision_id is null
/* PROGRAMMA DI BILANCIO */
left join work_effort_assoc wea8 on wea8.work_effort_id_to = obo.work_effort_id and wea8.work_effort_assoc_type_id = '20D2C'
left join work_effort prog on prog.work_effort_id = wea8.work_effort_id_from and prog.work_effort_type_id = '20D24PRO' and prog.work_effort_revision_id is null
/* MISSIONE DI BILANCIO */
left join work_effort_assoc wea9 on wea9.work_effort_id_to = prog.work_effort_id and wea9.work_effort_assoc_type_id = '20D2'
left join work_effort miss on miss.work_effort_id = wea9.work_effort_id_from and miss.work_effort_type_id = '20D22MIS' and miss.work_effort_revision_id is null
where val.work_effort_type_id = '20R60VAL' and val.work_effort_revision_id is null;