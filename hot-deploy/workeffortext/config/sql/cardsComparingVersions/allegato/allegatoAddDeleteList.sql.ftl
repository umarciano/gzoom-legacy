
-- LISTA ALLEGATO AGGIUNTE
-- param1 = workEffortId
-- param2 = oldWorkEffortId

-- LISTA ALLEGATO RIMOSSE
-- param1 = oldWorkEffortId
-- param2 = workEffortId
																										
	SELECT  DRN.DATA_RESOURCE_NAME, 
			WTN.DESCRIPTION AS WEC_DESCRITPION, 
			CEN.DESCRIPTION AS C_DESCRIPTION, 
			WCN.FROM_DATE, 
			WCN.THRU_DATE																					
	FROM WORK_EFFORT_CONTENT WCN																					
		INNER JOIN WORK_EFFORT_CONTENT_TYPE WTN ON WTN.WORK_EFFORT_CONTENT_TYPE_ID = WCN.WORK_EFFORT_CONTENT_TYPE_ID																				
		INNER JOIN CONTENT CEN ON CEN.CONTENT_ID = WCN.CONTENT_ID																				
		INNER JOIN DATA_RESOURCE DRN ON DRN.DATA_RESOURCE_ID = CEN.DATA_RESOURCE_ID																				
	WHERE WCN.WORK_EFFORT_ID = <@param param1/>																					
		AND NOT EXISTS																				
		(SELECT 1 FROM WORK_EFFORT_CONTENT WCO																				
			INNER JOIN CONTENT CEO ON CEO.CONTENT_ID = WCO.CONTENT_ID																			
			INNER JOIN DATA_RESOURCE DRO ON DRO.DATA_RESOURCE_ID = CEO.DATA_RESOURCE_ID																			
		 WHERE WCO.WORK_EFFORT_ID = <@param param2/>																					
			AND DRO.DATA_RESOURCE_NAME = DRN.DATA_RESOURCE_NAME)																			
		