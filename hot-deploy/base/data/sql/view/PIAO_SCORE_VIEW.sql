create or replace view SCORE_VIEW as
select 
	wem.work_effort_id as ID_SCHEDA,
	ate.amount as PUNTEGGIO,
	act.transaction_date as DATA_PUNTEGGIO,
	urv.comments as COMMENTO,
	urv.comments_lang as COMMENTO_LANG,
	dr.object_info as EMOTICON,
	act.description as NOTA,
	act.description_lang as NOTA_LANG
from work_effort_measure wem
join acctg_trans act on act.voucher_ref = wem.work_effort_measure_id 
join acctg_trans_entry ate on ate.acctg_trans_id = act.acctg_trans_id 
left join uom_range ur on ur.uom_range_id = 'PERF'
left join uom_range_values urv on urv.uom_range_id = ur.uom_range_id and urv.from_value <= ate.amount and urv.thru_value >= ate.amount 
left join content c on c.content_id = urv.icon_content_id 
left join data_resource dr on dr.data_resource_id = c.data_resource_id
where wem.gl_account_id = 'SCORE';