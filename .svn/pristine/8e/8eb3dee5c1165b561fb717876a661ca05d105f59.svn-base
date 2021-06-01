<#include   "component://workeffortext/config/sql/cardsComparingVersions/cards/cardsCommon.sql.ftl" />


-- LISTA OBIETTIVI MODIFICATI																																			
<@selectCards />	
																																				
	   ,OBO.ETCH AS OLD_ETCH, 
	   OBO.WORK_EFFORT_NAME AS OLD_WORK_EFFORT_NAME, 
	   OBO.DESCRIPTION AS OLD_DESCRIPTION, 
	   OBOR.PARTY_NAME AS OLD_PARTY_NAME, 
	   OBO.ESTIMATED_START_DATE AS OLD_ESTIMATED_START_DATE, 
	   OBO.ESTIMATED_COMPLETION_DATE AS OLD_ESTIMATED_COMPLETION_DATE,
	   OBO.WORK_EFFORT_ID AS OLD_WORK_EFFORT_ID, 
	   OBO.WORK_EFFORT_SNAPSHOT_ID AS OLD_WORK_EFFORT_SNAPSHO_ID,
	   OBO.WORK_EFFORT_TYPE_ID AS OLD_WORK_EFFORT_TYPE_ID
	   																																		
FROM WORK_EFFORT WE																																				
		INNER JOIN PARTY P ON P.PARTY_ID = WE.ORG_UNIT_ID
																																		
	INNER JOIN WORK_EFFORT OBO
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		 ON OBO.WORK_EFFORT_SNAPSHOT_ID = WE.WORK_EFFORT_SNAPSHOT_ID		-- SE MAPPA.VERSIONE IS NOT NULL	
	<#else>
		 ON OBO.WORK_EFFORT_SNAPSHOT_ID = WE.WORK_EFFORT_ID					-- SE MAPPA.VERSIONE IS NULL					
	</#if>	
	INNER JOIN PARTY OBOR ON OBOR.PARTY_ID = OBO.ORG_UNIT_ID																										
WHERE WE.WORK_EFFORT_PARENT_ID = <@param workEffortId />  																																			
	AND OBO.WORK_EFFORT_PARENT_ID = <@param oldWorkEffortId /> 																																		
																																			
-- ORDINAMENTO
<@orderCards />