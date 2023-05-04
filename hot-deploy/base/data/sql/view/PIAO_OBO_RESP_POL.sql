create or replace view OBO_RESP_POL as
select 
	wepa.work_effort_id as ID_SCHEDA,
	wepa.party_id as ID_SOGGETTO,
	p.party_name as NOME_SOGGETTO,
	p.party_name_lang as NOME_SOGGETTO_LANG,
	rt.description as RUOLO_SOGGETTO,
	rt.description_lang as RUOLO_SOGGETTO_LANG,
	wepa.from_date as DATA_INIZIO_RESPONSABILITA,
	wepa.thru_date as DATA_FINE_RESPONSABILITA
from work_effort_party_assignment wepa 
join role_type rt on rt.role_type_id = wepa.role_type_id 
join party p on p.party_id = wepa.party_id 
join person pe on pe.party_id = p.party_id;