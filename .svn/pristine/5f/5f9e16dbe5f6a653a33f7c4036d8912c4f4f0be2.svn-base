<#include   "component://workeffortext/config/sql/cardsComparingVersions/common/commonUtils.sql.ftl" />
-- LISTA ASSEGNAZIONI MODIFICATE	
																					
SELECT  WPNR.PARTY_NAME AS PARTY_NAME, 
		WPN.COMMENTS AS COMMENTS, 
		WPN.ROLE_TYPE_WEIGHT AS ROLE_TYPE_WEIGHT, 
		WPN.FROM_DATE AS FROM_DATE, 
		WPN.THRU_DATE AS THRU_DATE,																						
		WPO.COMMENTS AS OLD_COMMENTS, 
		WPO.ROLE_TYPE_WEIGHT AS OLD_ROLE_TYPE_WEIGHT, 
		WPO.FROM_DATE AS OLD_FROM_DATE, 
		WPO.THRU_DATE AS OLD_THRU_DATE
																						
FROM WORK_EFFORT_PARTY_ASSIGNMENT WPN																						
INNER JOIN PARTY WPNR ON WPNR.PARTY_ID = WPN.PARTY_ID							
INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WPO ON WPO.PARTY_ID = WPN.PARTY_ID	
																					
WHERE WPN.WORK_EFFORT_ID = <@param workEffortId />	 
	AND WPN.ROLE_TYPE_ID = 'WE_ASSIGNMENT'																						
	AND WPO.WORK_EFFORT_ID = <@param oldWorkEffortId />	 
	AND WPO.ROLE_TYPE_ID = 'WE_ASSIGNMENT'
	AND WPN.FROM_DATE = WPO.FROM_DATE																					
	AND (
		<@compareNotEqual "WPN.COMMENTS" "WPO.COMMENTS" />
		OR WPN.ROLE_TYPE_WEIGHT <> WPO.ROLE_TYPE_WEIGHT 
	  	OR WPN.THRU_DATE <> WPO.THRU_DATE)																					
																						
