<#include   "component://workeffortext/config/sql/cardsComparingVersions/cards/cardsCommon.sql.ftl" />
-- LISTA SCHEDE AGGIUNTE	
<@selectCards />

<@fromCards />

<@whereCards />
		
-- CONDIZIONE PERSONALIZZATE PER LE SOLE SCHEDE AGGIUNTE
	
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		AND WE.WORK_EFFORT_REVISION_ID = <@param parameters.workEffortRevisionId />   -- SE MAPPA.VERSIONE IS NOT NULL
	<#else>
		AND WE.WORK_EFFORT_REVISION_ID IS NULL		                                   -- SE MAPPA.VERSIONE IS NULL
		AND WE.ESTIMATED_START_DATE <= <@param parameters.monitoringDate jdbcType.TIMESTAMP />          -- SE MAPPA.VERSIONE IS NULL																	
		AND WE.ESTIMATED_COMPLETION_DATE >= <@param parameters.monitoringDate jdbcType.TIMESTAMP />     -- SE MAPPA.VERSIONE IS NULL						
	</#if>																											
		AND NOT EXISTS																																			
		(SELECT 1 FROM WORK_EFFORT WEE																																			
		 WHERE WEE.WORK_EFFORT_REVISION_ID = <@param parameters.workEffortRevisionIdComp jdbcType.CHAR/>
	 		<#if parameters.workEffortRevisionId?if_exists?has_content>
				AND WEE.WORK_EFFORT_SNAPSHOT_ID = WE.WORK_EFFORT_SNAPSHOT_ID)  -- SE MAPPA.VERSIONE IS NOT NULL
			<#else>
				AND WEE.WORK_EFFORT_SNAPSHOT_ID = WE.WORK_EFFORT_ID)           -- SE MAPPA.VERSIONE IS NULL			
			</#if>	
																																					
-- GESTIONE PERMESSI	
<@conditionPermissionCards />	
					
-- ORDINAMENTO
<@orderCards />
