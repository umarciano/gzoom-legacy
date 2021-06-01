<#include   "component://workeffortext/config/sql/cardsComparingVersions/common/commonUtils.sql.ftl" />
-- LISTA MISURE MODIFICATE
																												
SELECT  ACN.ACCOUNT_NAME, 
		PRN.UOM_DESCR AS PR_UOM_DESCR, 
		WMN.UOM_DESCR AS WM_UOM_DESCR, 
		WMN.COMMENTS, 
		WMN.KPI_SCORE_WEIGHT, 
		WMN.SEQUENCE_ID, 
		WMN.FROM_DATE, 
		WMN.THRU_DATE,																																				
		WMO.UOM_DESCR AS OLD_UOM_DESCR, 
		WMO.COMMENTS AS OLD_COMMENTS, 
		WMO.KPI_SCORE_WEIGHT AS OLD_KPI_SCORE_WEIGHT, 
		WMO.SEQUENCE_ID AS OLD_SEQUENCE_ID, 
		WMO.FROM_DATE AS OLD_FROM_DATE, 
		WMO.THRU_DATE AS OLD_THRU_DATE
																												
FROM WORK_EFFORT_MEASURE WMN																																				
	INNER JOIN WORK_EFFORT_MEASURE WMO ON WMO.GL_ACCOUNT_ID = WMN.GL_ACCOUNT_ID																																			
		AND ((WMO.PRODUCT_ID IS NULL AND WMN.PRODUCT_ID IS NULL) OR WMO.PRODUCT_ID = WMN.PRODUCT_ID)																																		
	INNER JOIN GL_ACCOUNT ACN ON ACN.GL_ACCOUNT_ID = WMN.GL_ACCOUNT_ID	
		AND (ACN.INPUT_ENUM_ID <> 'ACCINP_OBJ' OR ((WMO.UOM_DESCR IS NULL AND WMN.UOM_DESCR IS NULL) OR WMO.UOM_DESCR = WMN.UOM_DESCR))																																		
	LEFT JOIN WORK_EFFORT_MEASURE PRN ON PRN.WORK_EFFORT_ID = NULL AND PRN.PRODUCT_ID <> NULL																																			
		AND PRN.GL_ACCOUNT_ID = WMN.GL_ACCOUNT_ID																																		
		AND PRN.PRODUCT_ID = WMN.PRODUCT_ID																																		
WHERE WMN.WORK_EFFORT_ID = <@param workEffortId />
	 AND WMN.GL_ACCOUNT_ID NOT LIKE 'SCORE%'																																				
	 AND WMO.WORK_EFFORT_ID = <@param oldWorkEffortId />																																			
	AND (
		<@compareNotEqual "WMN.UOM_DESCR" "WMO.UOM_DESCR" />
		OR <@compareNotEqual "WMN.COMMENTS" "WMO.COMMENTS" />																											
	  	OR WMN.KPI_SCORE_WEIGHT <> WMO.KPI_SCORE_WEIGHT 
	  	OR WMN.SEQUENCE_ID <> WMO.SEQUENCE_ID																																			
	    OR WMN.FROM_DATE <> WMO.FROM_DATE 
	    OR WMN.THRU_DATE <> WMO.THRU_DATE)																																			
																																				
