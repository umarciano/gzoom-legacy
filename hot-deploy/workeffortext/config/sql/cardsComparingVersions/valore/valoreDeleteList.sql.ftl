<#include   "component://workeffortext/config/sql/cardsComparingVersions/valore/valoreCommon.sql.ftl" />

	<@query />
		
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		 AND TEO.G_WORK_EFFORT_SNAPSHOT_ID = TEN.G_WORK_EFFORT_SNAPSHOT_ID)		-- SE MAPPA.VERSIONE IS NOT NULL	
	<#else>
		 AND TEO.G_WORK_EFFORT_SNAPSHOT_ID IS NULL)					-- SE MAPPA.VERSIONE IS NULL					
	</#if>																																				
