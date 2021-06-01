
-- LISTA MISURE AGGIUNTE
-- param1 = workEffortId
-- param2 = oldWorkEffortId

-- LISTA MISURE RIMOSSE
-- param1 = oldWorkEffortId
-- param2 = workEffortId
																										
SELECT  ACN.ACCOUNT_NAME, 
		PRN.UOM_DESCR AS PR_UOM_DESCR, 
		WMN.UOM_DESCR AS WM_UOM_DESCR, 
		WMN.COMMENTS, 
		WMN.KPI_SCORE_WEIGHT, 
		WMN.SEQUENCE_ID, 
		WMN.FROM_DATE, 
		WMN.THRU_DATE	
																																				
FROM WORK_EFFORT_MEASURE WMN																																			
	INNER JOIN GL_ACCOUNT ACN ON ACN.GL_ACCOUNT_ID = WMN.GL_ACCOUNT_ID																																		
	LEFT JOIN WORK_EFFORT_MEASURE PRN ON PRN.WORK_EFFORT_ID = NULL AND PRN.PRODUCT_ID <> NULL																																		
		AND PRN.GL_ACCOUNT_ID = WMN.GL_ACCOUNT_ID																																	
		AND PRN.PRODUCT_ID = WMN.PRODUCT_ID
																																			
WHERE WMN.WORK_EFFORT_ID = <@param param1 /> 
	AND WMN.GL_ACCOUNT_ID NOT LIKE 'SCORE%'																																			
	AND NOT EXISTS																																		
	(SELECT 1 FROM WORK_EFFORT_MEASURE WMO																																		
	 WHERE WMO.WORK_EFFORT_ID = <@param param2 />																																		
		AND WMO.GL_ACCOUNT_ID = WMN.GL_ACCOUNT_ID																																	
		AND ((WMO.PRODUCT_ID IS NULL AND WMN.PRODUCT_ID IS NULL) OR WMO.PRODUCT_ID = WMN.PRODUCT_ID)
		AND (ACN.INPUT_ENUM_ID <> 'ACCINP_OBJ' OR ((WMO.UOM_DESCR IS NULL AND WMN.UOM_DESCR IS NULL) OR WMO.UOM_DESCR = WMN.UOM_DESCR)))																															
	