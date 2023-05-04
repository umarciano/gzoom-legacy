create or replace view PERF_INDIC_VIEW as 
select 
	wetav.m_work_effort_id as ID_SCHEDA,
	wetav.m_gl_account_id as ID_INDICATORE_M,
	wetav.tm_gl_account_id as ID_INDICATORE_A,
	wetav.g_account_name as NOME_INDICATORE,
	gl.account_name_lang as NOME_INDICATORE_LANG,
	wetav.g_account_code as CODICE_INDICATORE,
	wetav.g_uom_abbreviation as UDM,
	u.abbreviation_lang as UDM_LANG,
	wetav.tm_amount as VALORE_NUMERICO,
	wetav.r_uom_descr as VALORE_STRINGA,
	wetav.g_uom_type_id as TIPO_VALORE,
	wetav.tt_transaction_date as DATA_VALORE,
	wetav.tm_gl_fiscal_type_id as PERIODO_RILEVAZIONE,
	ate.gl_account_id,
	ate.amount as PUNTEGGIO,
	act.transaction_date as DATA_PUNTEGGIO,
	ate.gl_fiscal_type_id as PERIODO_RILEVAZIONE_PUNTEGGIO,
	urv.comments as COMMENTO,
	urv.comments_lang as COMMENTO_LANG,
	dr.object_info as EMOTICON
from work_effort_trans_all_view wetav 
/* INDICATORE */
left join gl_account gl on gl.gl_account_id = wetav.g_gl_account_id 
left join uom u on u.uom_id = gl.default_uom_id 
/* SCOREKPI */
left join acctg_trans act on act.voucher_ref = wetav.m_work_effort_measure_id and act.transaction_date = wetav.tt_transaction_date 
left join acctg_trans_entry ate on ate.acctg_trans_id = act.acctg_trans_id and ate.gl_account_id = 'SCOREKPI'
/* EMOTICON */
left join uom_range ur on ur.uom_range_id = 'PERF'
left join uom_range_values urv on urv.uom_range_id = ur.uom_range_id and urv.from_value <= ate.amount and urv.thru_value >= ate.amount
left join content c on c.content_id = urv.icon_content_id 
left join data_resource dr on dr.data_resource_id = c.data_resource_id
where wetav.g_gl_account_id <> 'SCORE' and wetav.m_we_measure_type_enum_id = 'WEMT_PERF';