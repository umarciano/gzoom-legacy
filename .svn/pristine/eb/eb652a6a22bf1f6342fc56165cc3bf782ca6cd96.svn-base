
-- LISTA RUOLI AGGIUNTE
-- param1 = workEffortId
-- param2 = oldWorkEffortId

-- LISTA RUOLI RIMOSSE
-- param1 = oldWorkEffortId
-- param2 = workEffortId
																										
	SELECT  WRNT.DESCRIPTION AS DESCRIPTION, 
			WRNR.PARTY_NAME AS PARTY_NAME, 
			WRN.COMMENTS AS COMMENTS, 
			WRN.ROLE_TYPE_WEIGHT AS ROLE_TYPE_WEIGHT, 
			WRN.FROM_DATE AS FROM_DATE, 
			WRN.THRU_DATE AS THRU_DATE																										
	FROM WORK_EFFORT_PARTY_ASSIGNMENT WRN																										
		INNER JOIN PARTY WRNR ON WRNR.PARTY_ID = WRN.PARTY_ID																									
		INNER JOIN ROLE_TYPE WRNT ON WRNT.ROLE_TYPE_ID = WRN.ROLE_TYPE_ID	
																										
	WHERE WRN.WORK_EFFORT_ID = <@param param1 /> 
		AND WRN.ROLE_TYPE_ID <> 'WE_ASSIGNMENT'																										
		AND NOT EXISTS																									
		(SELECT 1 FROM WORK_EFFORT_PARTY_ASSIGNMENT WRO																									
		 WHERE WRO.WORK_EFFORT_ID = <@param param2 /> 																									
			AND WRO.PARTY_ID = WRN.PARTY_ID 
			AND WRO.ROLE_TYPE_ID = WRN.ROLE_TYPE_ID
			AND WRO.FROM_DATE = WRN.FROM_DATE)																								
				