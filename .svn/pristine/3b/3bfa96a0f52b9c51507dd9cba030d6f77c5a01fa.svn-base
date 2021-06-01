<#include   "component://workeffortext/config/sql/cardsComparingVersions/relazioni/relazioniCommon.sql.ftl" />


-- LISTA RELAZIONI RIMOSSE
-- param1 = oldWorkEffortId
-- param2 = oldWorkEffortTypeId
-- param3 = workEffortId


	<@query1 />	
																																
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		  WEFN.WORK_EFFORT_SNAPSHOT_ID = WEFO.WORK_EFFORT_SNAPSHOT_ID))		-- SE MAPPA.VERSIONE IS NOT NULL	
	<#else>
		  WEFN.WORK_EFFORT_ID = WEFO.WORK_EFFORT_SNAPSHOT_ID))					-- SE MAPPA.VERSIONE IS NULL
	</#if>	
																			
UNION ALL	
																																				
	<@query2 />	
	
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		  WETN.WORK_EFFORT_SNAPSHOT_ID = WETO.WORK_EFFORT_SNAPSHOT_ID))		-- SE MAPPA.VERSIONE IS NOT NULL	
	<#else>
		  WETN.WORK_EFFORT_ID = WETO.WORK_EFFORT_SNAPSHOT_ID))				-- SE MAPPA.VERSIONE IS NULL
	</#if>	
	
	