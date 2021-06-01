<#include   "component://workeffortext/config/sql/cardsComparingVersions/cards/cardsCommon.sql.ftl" />

-- LISTA SCHEDE RIMOSSE	
<@selectCards />

<@fromCards />

<@whereCards />
		
-- CONDIZIONE PERSONALIZZATE PER LE SOLE SCHEDE ELIMINATE

		AND WE.WORK_EFFORT_REVISION_ID = <@param parameters.workEffortRevisionIdComp jdbcType.CHAR />
																							
		AND NOT EXISTS																									
			(SELECT 1 FROM WORK_EFFORT WEE
			 WHERE 
		 	<#if parameters.workEffortRevisionId?if_exists?has_content>
		 		-- SE MAPPA.VERSIONE IS NOT NULL
				WEE.WORK_EFFORT_REVISION_ID = <@param parameters.workEffortRevisionId jdbcType.CHAR /> 
				AND WEE.WORK_EFFORT_SNAPSHOT_ID = WE.WORK_EFFORT_SNAPSHOT_ID) 
			<#else>
				-- SE MAPPA.VERSIONE IS NULL
				WEE.WORK_EFFORT_REVISION_ID IS NULL                               	
				AND WEE.ESTIMATED_START_DATE <= <@param parameters.monitoringDate jdbcType.TIMESTAMP />
				AND WEE.ESTIMATED_COMPLETION_DATE >= <@param parameters.monitoringDate jdbcType.TIMESTAMP />
				AND WEE.WORK_EFFORT_ID = WE.WORK_EFFORT_SNAPSHOT_ID)
			</#if>		
				
-- GESTIONE PERMESSI	
<@conditionPermissionCards />	

-- ORDINAMENTO
<@orderCards />
	