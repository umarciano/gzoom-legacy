
-- LISTA ASSEGNAZIONI AGGIUNTE
-- param1 = workEffortId
-- param2 = oldWorkEffortId

-- LISTA ASSEGNAZIONI RIMOSSE
-- param1 = oldWorkEffortId
-- param2 = workEffortId

SELECT P.PARTY_NAME AS PARTY_NAME, 
	   WP1.COMMENTS AS COMMENTS, 
	   WP1.ROLE_TYPE_WEIGHT AS ROLE_TYPE_WEIGHT, 
	   WP1.FROM_DATE AS FROM_DATE, 
	   WP1.THRU_DATE AS THRU_DATE
	   	 																					
	FROM WORK_EFFORT_PARTY_ASSIGNMENT WP1																						
	INNER JOIN PARTY P ON P.PARTY_ID = WP1.PARTY_ID
																						
	WHERE WP1.ROLE_TYPE_ID = 'WE_ASSIGNMENT'
	
	AND WP1.WORK_EFFORT_ID = <@param param1 /> 																						
	AND NOT EXISTS																					
	(SELECT 1 FROM WORK_EFFORT_PARTY_ASSIGNMENT WP2																				
	 WHERE WP2.WORK_EFFORT_ID = <@param param2 /> 
	    AND WP2.ROLE_TYPE_ID = 'WE_ASSIGNMENT'																					
		AND WP2.PARTY_ID = WP1.PARTY_ID
		AND WP2.FROM_DATE = WP1.FROM_DATE)		