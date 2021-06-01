<#include   "component://workeffortext/config/sql/cardsComparingVersions/cards/cardsCommon.sql.ftl" />
-- LISTA OBIETTIVI RIMOSSI	
<@selectCards />
																																
FROM WORK_EFFORT WE																																				
		INNER JOIN PARTY P ON P.PARTY_ID = WE.ORG_UNIT_ID
																																	
WHERE WE.WORK_EFFORT_PARENT_ID = <@param oldWorkEffortId />																																	
	AND NOT EXISTS																																	
	(SELECT 1 FROM WORK_EFFORT OBO																																				
	 WHERE OBO.WORK_EFFORT_PARENT_ID = <@param workEffortId />
	 	<#if parameters.workEffortRevisionId?if_exists?has_content>
			AND OBO.WORK_EFFORT_SNAPSHOT_ID = WE.WORK_EFFORT_SNAPSHOT_ID)		-- SE MAPPA.VERSIONE IS NOT NULL	
		<#else>
			AND OBO.WORK_EFFORT_ID = WE.WORK_EFFORT_SNAPSHOT_ID)				-- SE MAPPA.VERSIONE IS NULL					
		</#if>																																			
-- ORDINAMENTO
<@orderCards />
																																		