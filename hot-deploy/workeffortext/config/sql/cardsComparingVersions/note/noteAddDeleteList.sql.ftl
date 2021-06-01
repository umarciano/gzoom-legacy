
-- LISTA NOTE AGGIUNTE
-- param1 = workEffortId
-- param2 = oldWorkEffortId

-- LISTA NOTE RIMOSSE
-- param1 = oldWorkEffortId
-- param2 = workEffortId
																										
SELECT  NDN.NOTE_DATE_TIME, 
		NDN.NOTE_NAME, 
		NDN.NOTE_INFO, 
		WNN.INTERNAL_NOTE, 
		WNN.IS_HTML																						
FROM WORK_EFFORT_NOTE WNN																						
	INNER JOIN NOTE_DATA NDN ON NDN.NOTE_ID = WNN.NOTE_ID																					
WHERE WNN.WORK_EFFORT_ID = <@param param1/>																						
	AND NOT EXISTS																					
	(SELECT 1 FROM WORK_EFFORT_NOTE WNO																					
		INNER JOIN NOTE_DATA NDO ON NDO.NOTE_ID = WNO.NOTE_ID																				
	 WHERE WNO.WORK_EFFORT_ID = <@param param2/> 																					
		AND NDO.NOTE_DATE_TIME = NDN.NOTE_DATE_TIME AND NDO.NOTE_NAME = NDN.NOTE_NAME)																				
																										