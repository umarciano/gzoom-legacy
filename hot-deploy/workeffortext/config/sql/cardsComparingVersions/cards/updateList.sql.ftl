<#include   "component://workeffortext/config/sql/cardsComparingVersions/cards/cardsCommon.sql.ftl" />

-- LISTA SCHEDE MODIFICATE	
<@selectCards />
	
	,WEE.WORK_EFFORT_ID AS OLD_WORK_EFFORT_ID, 
	WEE.WORK_EFFORT_SNAPSHOT_ID AS OLD_WORK_EFFORT_SNAPSHOT_ID
	    
<@fromCards />

	INNER JOIN WORK_EFFORT WEE
	  <#if parameters.workEffortRevisionId?if_exists?has_content>
	  	ON WEE.WORK_EFFORT_SNAPSHOT_ID = WE.WORK_EFFORT_SNAPSHOT_ID			-- SE MAPPA.VERSIONE IS NOT NULL
	  <#else>
	  	ON WEE.WORK_EFFORT_SNAPSHOT_ID = WE.WORK_EFFORT_ID						-- SE MAPPA.VERSIONE IS NULL	
	  </#if>									
		  									

<@whereCards />

	<#if parameters.workEffortRevisionId?if_exists?has_content>
    	AND WE.WORK_EFFORT_REVISION_ID = <@param parameters.workEffortRevisionId jdbcType.CHAR />	   -- SE MAPPA.VERSIONE IS NOT NULL
    <#else>
    	AND WE.WORK_EFFORT_REVISION_ID IS NULL								                           -- SE MAPPA.VERSIONE IS NULL										
	    AND WE.ESTIMATED_START_DATE <= <@param parameters.monitoringDate jdbcType.TIMESTAMP />						   -- SE MAPPA.VERSIONE IS NULL										
	    AND WE.ESTIMATED_COMPLETION_DATE >= <@param parameters.monitoringDate jdbcType.TIMESTAMP />					   -- SE MAPPA.VERSIONE IS NULL	
    </#if>							
	AND WEE.WORK_EFFORT_REVISION_ID = <@param parameters.workEffortRevisionIdComp jdbcType.CHAR />																												
																														
-- GESTIONE PERMESSI	
<@conditionPermissionCards />	

-- ORDINAMENTO
<@orderCards />						