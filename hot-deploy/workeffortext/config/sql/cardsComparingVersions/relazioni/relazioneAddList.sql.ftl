<#include   "component://workeffortext/config/sql/cardsComparingVersions/relazioni/relazioniCommon.sql.ftl" />


-- LISTA RELAZIONI AGGIUNTE
-- param1 = workEffortId
-- param2 = workEffortTypeId
-- param3 = OldWorkEffortId


	<@query1 />	
																																
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		  WEFN.WORK_EFFORT_SNAPSHOT_ID = WEFO.WORK_EFFORT_SNAPSHOT_ID))		-- SE MAPPA.VERSIONE IS NOT NULL	
	<#else>
		  WEFN.WORK_EFFORT_SNAPSHOT_ID = WEFO.WORK_EFFORT_ID))			-- SE MAPPA.VERSIONE IS NUL										
	</#if>	
																			
UNION ALL
																																				
	<@query2 />	
	
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		  WETN.WORK_EFFORT_SNAPSHOT_ID = WETO.WORK_EFFORT_SNAPSHOT_ID))		-- SE MAPPA.VERSIONE IS NOT NULL	
	<#else>
		  WETN.WORK_EFFORT_SNAPSHOT_ID = WETO.WORK_EFFORT_ID))		-- SE MAPPA.VERSIONE IS NUL										
	</#if>	
	
	