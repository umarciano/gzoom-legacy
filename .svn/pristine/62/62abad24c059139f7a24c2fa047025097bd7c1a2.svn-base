<#include   "component://workeffortext/config/sql/cardsComparingVersions/common/commonUtils.sql.ftl" />
-- LISTA VALORI MODIFICATI

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
		TEN.TT_DESCRIPTION,																																				
		TEO.TM_AMOUNT AS OLD_TM_AMOUNT, 
		TEO.R_UOM_CODE AS OLD_R_UOM_CODE, 
		TEO.TM_DESCRIPTION AS OLD_TM_DESCRIPTION, 
		TEO.TT_DESCRIPTION AS OLD_TT_DESCRIPTION	
																																					
FROM WORK_EFFORT_TRANS_INDIC_VIEW TEN																																						
	INNER JOIN WORK_EFFORT_TRANS_INDIC_VIEW TEO ON TEO.TM_GL_ACCOUNT_ID = TEN.TM_GL_ACCOUNT_ID																								-- NOTA: NELLA ON NON SONO STATE GESTITE LE CONDIZIONI SU WEMOMG_WEFF													
		AND ((TEN.G_INPUT_ENUM_ID = 'ACCINP_OBJ'  AND TEO.M_UOM_DESCR = TEN.M_UOM_DESCR) OR (TEN.G_INPUT_ENUM_ID <> 'ACCINP_OBJ'																																				
			AND ((TEO.M_PRODUCT_ID IS NULL AND TEN.M_PRODUCT_ID IS NULL) OR TEO.M_PRODUCT_ID = TEN.M_PRODUCT_ID)																																			
			AND ((TEO.M_EMPL_POSITION_TYPE_ID IS NULL AND TEN.M_EMPL_POSITION_TYPE_ID IS NULL) OR TEO.M_EMPL_POSITION_TYPE_ID = TEN.M_EMPL_POSITION_TYPE_ID)																																			
			AND ((TEO.M_PARTY_ID IS NULL AND TEN.M_PARTY_ID IS NULL) OR TEO.M_ROLE_TYPE_ID = TEN.M_ROLE_TYPE_ID AND TEO.M_PARTY_ID = TEN.M_PARTY_ID)																																			
			AND ((TEO.M_ORG_UNIT_ROLE_TYPE_ID = TEN.M_ORG_UNIT_ROLE_TYPE_ID AND TEO.M_ORG_UNIT_ID = TEN.M_ORG_UNIT_ID) OR TEN.M_WE_OTHER_GOAL_ENUM_ID <> 'WEMOMG_ORG')))																																			
		AND TEO.TT_TRANSACTION_DATE = TEN.TT_TRANSACTION_DATE																																				
		AND TEO.TM_GL_FISCAL_TYPE_ID = TEN.TM_GL_FISCAL_TYPE_ID	
		
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		 AND TEO.G_WORK_EFFORT_SNAPSHOT_ID = TEN.G_WORK_EFFORT_SNAPSHOT_ID	-- SE MAPPA.VERSIONE IS NOT NULL	
	<#else>
		 AND TEO.G_WORK_EFFORT_SNAPSHOT_ID = <@param workEffortId />					-- SE MAPPA.VERSIONE IS NULL					
	</#if>																																				
																																					
WHERE TEN.M_WORK_EFFORT_ID = <@param workEffortId /> 
	AND TEN.G_GL_ACCOUNT_ID NOT LIKE 'SCORE%'																																						
	 AND TEO.M_WORK_EFFORT_ID = <@param oldWorkEffortId /> 																																					
	AND (TEN.TM_AMOUNT <> TEO.TM_AMOUNT 
		OR <@compareNotEqual "TEN.G_UOM_CODE" "TEO.G_UOM_CODE" />
		OR <@compareNotEqual "TEN.TM_DESCRIPTION" "TEO.TM_DESCRIPTION" />   
	  	OR <@compareNotEqual "TEN.TT_DESCRIPTION" "TEO.TT_DESCRIPTION" />
	  	)																																					
