<#include   "component://workeffortext/config/sql/cardsComparingVersions/common/commonUtils.sql.ftl" />
-- LISTA RUOLI MODIFICATI
																												
SELECT  WRNT.DESCRIPTION AS DESCRIPTION, 
		WRNR.PARTY_NAME AS PARTY_NAME, 
		WRN.COMMENTS AS COMMENTS, 
		WRN.ROLE_TYPE_WEIGHT AS ROLE_TYPE_WEIGHT, 
		WRN.FROM_DATE AS FROM_DATE, 
		WRN.THRU_DATE AS THRU_DATE,																												
		WRO.COMMENTS AS OLD_COMMENTS, 
		WRO.ROLE_TYPE_WEIGHT AS OLD_ROLE_TYPE_WEIGHT, 
		WRO.FROM_DATE AS OLD_FROM_DATE, 
		WRO.THRU_DATE AS OLD_THRU_DATE	 																	
FROM WORK_EFFORT_PARTY_ASSIGNMENT WRN																												
	INNER JOIN PARTY WRNR ON WRNR.PARTY_ID = WRN.PARTY_ID																											
	INNER JOIN ROLE_TYPE WRNT ON WRNT.ROLE_TYPE_ID = WRN.ROLE_TYPE_ID																											
	INNER JOIN WORK_EFFORT_PARTY_ASSIGNMENT WRO ON WRO.PARTY_ID = WRN.PARTY_ID AND WRO.ROLE_TYPE_ID = WRN.ROLE_TYPE_ID																											
WHERE WRN.WORK_EFFORT_ID = <@param workEffortId /> 
	  AND WRN.ROLE_TYPE_ID <> 'WE_ASSIGNMENT'																												
	  AND WRO.WORK_EFFORT_ID = <@param oldWorkEffortId />	
	  AND WRN.FROM_DATE = WRO.FROM_DATE 																											
	  AND (
	  	<@compareNotEqual "WRN.COMMENTS" "WRO.COMMENTS" /> 
	  	OR WRN.ROLE_TYPE_WEIGHT <> WRO.ROLE_TYPE_WEIGHT	
	  	OR WRN.THRU_DATE <> WRO.THRU_DATE
	  	)																											
																						