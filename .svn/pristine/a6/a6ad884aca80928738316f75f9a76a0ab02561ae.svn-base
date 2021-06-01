-- LISTA NOTE MODIFICATE
																												
SELECT  NDN.NOTE_DATE_TIME, 
		NDN.NOTE_NAME, 
		NDN.NOTE_INFO,
		WNN.INTERNAL_NOTE, 
		WNN.IS_HTML,																												
		NDO.NOTE_INFO AS OLD_NOTE_INFO, 
		WNO.INTERNAL_NOTE AS OLD_INTERNAL_NOTE, 
		WNO.IS_HTML	AS  OLD_IS_HTML																	
FROM WORK_EFFORT_NOTE WNN																												
	INNER JOIN NOTE_DATA NDN ON NDN.NOTE_ID = WNN.NOTE_ID																											
	INNER JOIN NOTE_DATA NDO ON NDO.NOTE_DATE_TIME = NDN.NOTE_DATE_TIME 
		AND NDO.NOTE_NAME = NDN.NOTE_NAME																											
	INNER JOIN WORK_EFFORT_NOTE WNO ON WNO.NOTE_ID = NDO.NOTE_ID																											
WHERE WNN.WORK_EFFORT_ID = <@param workEffortId /> 																										
	 AND WNO.WORK_EFFORT_ID = <@param oldWorkEffortId/> 																											
	 -- AND (NDN.NOTE_INFO <> NDO.NOTE_INFO OR WNN.INTERNAL_NOTE <> WNO.INTERNAL_NOTE)																											
