<#macro query>
	
	-- LISTA VALORI AGGIUNTE
	-- param1 = workEffortId
	-- param2 = oldWorkEffortId
	
	-- LISTA VALORI RIMOSSE
	-- param1 = oldWorkEffortId
	-- param2 = workEffortId

	
	SELECT  TEN.G_ACCOUNT_NAME, 
			TEN.M_PRODUCT_DESCRIPTION, 
			TEN.M_UOM_DESCR, 
			TEN.M_PARTY_NAME, 
			TEN.M_PERIOD_NAME, 
			TEN.TT_FISCAL_TYPE_DESCRIPTION, 
			TEN.R_UOM_CODE,																																						
			TEN.G_UOM_DECIMAL_SCALE, 
			TEN.G_UOM_TYPE_ID,																																				
			TEN.TM_AMOUNT, 
			TEN.G_UOM_CODE, 
			TEN.TM_DESCRIPTION, 
			TEN.TT_DESCRIPTION	
																																						
	FROM WORK_EFFORT_TRANS_INDIC_VIEW TEN
	INNER JOIN WORK_EFFORT_REVISION RV ON RV.WORK_EFFORT_REVISION_ID = <@param parameters.workEffortRevisionIdComp /> 	 
		AND (RV.FROM_DATE IS NULL OR RV.FROM_DATE <= TEN.TT_TRANSACTION_DATE)
		AND (RV.TO_DATE IS NULL OR RV.TO_DATE >= TEN.TT_TRANSACTION_DATE)	
																																				
	WHERE TEN.M_WORK_EFFORT_ID = <@param param1 /> 
		AND TEN.TM_GL_ACCOUNT_ID NOT LIKE 'SCORE%'																																								
		AND NOT EXISTS																																					
		(SELECT 1 FROM WORK_EFFORT_TRANS_INDIC_VIEW TEO		-- NOTA: NELLA WHERE NON SONO STATE GESTITE LE CONDIZIONI SU WEMOMG_WEFF																							
		 WHERE TEO.M_WORK_EFFORT_ID = <@param param2 />																																					
			AND TEO.TM_GL_ACCOUNT_ID = TEN.TM_GL_ACCOUNT_ID																																				
			AND ((TEN.G_INPUT_ENUM_ID = 'ACCINP_OBJ'  AND (TEO.M_UOM_DESCR = TEN.M_UOM_DESCR OR (TEO.M_UOM_DESCR IS NULL AND TEN.M_UOM_DESCR IS NULL))) OR (TEN.G_INPUT_ENUM_ID <> 'ACCINP_OBJ'																																				
				AND ((TEO.M_PRODUCT_ID IS NULL AND TEN.M_PRODUCT_ID IS NULL) OR TEO.M_PRODUCT_ID = TEN.M_PRODUCT_ID)																																			
				AND ((TEO.M_EMPL_POSITION_TYPE_ID  IS NULL AND TEN.M_EMPL_POSITION_TYPE_ID IS NULL) OR TEO.M_EMPL_POSITION_TYPE_ID = TEN.M_EMPL_POSITION_TYPE_ID)																																			
				AND ((TEO.M_PARTY_ID IS NULL AND TEN.M_PARTY_ID IS NULL) OR TEO.M_ROLE_TYPE_ID = TEN.M_ROLE_TYPE_ID AND TEO.M_PARTY_ID = TEN.M_PARTY_ID)																																			
				AND ((TEO.M_ORG_UNIT_ROLE_TYPE_ID = TEN.M_ORG_UNIT_ROLE_TYPE_ID AND TEO.M_ORG_UNIT_ID = TEN.M_ORG_UNIT_ID) OR TEN.M_WE_OTHER_GOAL_ENUM_ID <> 'WEMOMG_ORG')))																																			
			AND TEO.TT_TRANSACTION_DATE = TEN.TT_TRANSACTION_DATE																																				
			AND TEO.TM_GL_FISCAL_TYPE_ID = TEN.TM_GL_FISCAL_TYPE_ID	
</#macro>