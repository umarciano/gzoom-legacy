<#include   "component://workeffortext/config/sql/cardsComparingVersions/common/commonUtils.sql.ftl" />

-- LISTA RELAZIONI MODIFICATE	
													
SELECT  WRFN.COMMENTS AS DESCRIPTION, 
		WEFN.ETCH, 
		WEFN.WORK_EFFORT_NAME, 
		WAFN.COMMENTS, 
		WAFN.ASSOC_WEIGHT, 
		WAFN.SEQUENCE_NUM, 
		WAFN.FROM_DATE, 
		WAFN.THRU_DATE,
		WAFO.COMMENTS AS OLD_COMMENTS, 
		WAFO.ASSOC_WEIGHT AS OLD_ASSOC_WEIGHT, 
		WAFO.SEQUENCE_NUM AS OLD_SEQUENCE_NUM, 
		WAFO.FROM_DATE AS OLD_FROM_DATE, 
		WAFO.THRU_DATE AS OLD_THRU_DATE
																						
FROM WORK_EFFORT_ASSOC WAFN																																			
	INNER JOIN WORK_EFFORT WEFN ON WEFN.WORK_EFFORT_ID = WAFN.WORK_EFFORT_ID_TO																																		
	INNER JOIN WORK_EFFORT_TYPE_ASSOC WRFN ON WRFN.WEFROM_WETO_ENUM_ID = 'WETAFROM'																																		
		AND WRFN.WORK_EFFORT_ASSOC_TYPE_ID = WAFN.WORK_EFFORT_ASSOC_TYPE_ID																																	
		AND WRFN.WORK_EFFORT_TYPE_ID_REF = WEFN.WORK_EFFORT_TYPE_ID																																	
	INNER JOIN WORK_EFFORT_ASSOC WAFO ON WAFO.WORK_EFFORT_ASSOC_TYPE_ID = WAFN.WORK_EFFORT_ASSOC_TYPE_ID																																		
	INNER JOIN WORK_EFFORT WEFO ON WEFO.WORK_EFFORT_ID = WAFO.WORK_EFFORT_ID_TO
		AND  ( WEFO.WORK_EFFORT_ID = WEFN.WORK_EFFORT_ID OR 
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		  WEFO.WORK_EFFORT_SNAPSHOT_ID = WEFN.WORK_EFFORT_SNAPSHOT_ID		-- SE MAPPA.VERSIONE IS NOT NULL	
	<#else>
		  WEFO.WORK_EFFORT_SNAPSHOT_ID = WEFN.WORK_EFFORT_ID					-- SE MAPPA.VERSIONE IS NULL					
	</#if>																																			
		)												
WHERE WAFN.WORK_EFFORT_ID_FROM = <@param workEffortId />																																			
	AND WRFN.WORK_EFFORT_TYPE_ID = <@param workEffortTypeId />																																	
	 AND WAFO.WORK_EFFORT_ID_FROM = <@param oldWorkEffortId /> 																																		
	AND (
		<@compareNotEqual "WAFN.COMMENTS" "WAFO.COMMENTS" /> 
		OR WAFN.ASSOC_WEIGHT <> WAFO.ASSOC_WEIGHT																																		
	  	OR WAFN.FROM_DATE <> WAFO.FROM_DATE 
	  	OR WAFN.THRU_DATE <> WAFO.THRU_DATE
	  	)
	  																																		
UNION ALL
																																			
SELECT  WRTN.COMMENTS AS DESCRIPTION, 
		WETN.ETCH, 
		WETN.WORK_EFFORT_NAME, 
		WATN.COMMENTS, 
		WATN.ASSOC_WEIGHT, 
		WATN.SEQUENCE_NUM, 
		WATN.FROM_DATE, 
		WATN.THRU_DATE,																																			
		WATO.COMMENTS, 
		WATO.ASSOC_WEIGHT, 
		WATO.SEQUENCE_NUM, 
		WATO.FROM_DATE, 
		WATO.THRU_DATE	
																						
FROM WORK_EFFORT_ASSOC WATN																																			
	INNER JOIN WORK_EFFORT WETN ON WETN.WORK_EFFORT_ID = WATN.WORK_EFFORT_ID_FROM																																		
	INNER JOIN WORK_EFFORT_TYPE_ASSOC WRTN ON WRTN.WEFROM_WETO_ENUM_ID = 'WETATO'																																		
		AND WRTN.WORK_EFFORT_ASSOC_TYPE_ID = WATN.WORK_EFFORT_ASSOC_TYPE_ID																																	
		AND WRTN.WORK_EFFORT_TYPE_ID_REF = WETN.WORK_EFFORT_TYPE_ID																																	
	INNER JOIN WORK_EFFORT_ASSOC WATO ON WATO.WORK_EFFORT_ASSOC_TYPE_ID = WATN.WORK_EFFORT_ASSOC_TYPE_ID
	INNER JOIN WORK_EFFORT 	WETO ON WETO.WORK_EFFORT_ID = WATN.WORK_EFFORT_ID_FROM
		AND  ( WETO.WORK_EFFORT_ID = WETN.WORK_EFFORT_ID OR 
	<#if parameters.workEffortRevisionId?if_exists?has_content>
		  WETN.WORK_EFFORT_SNAPSHOT_ID = WETO.WORK_EFFORT_SNAPSHOT_ID		-- SE MAPPA.VERSIONE IS NOT NULL	
	<#else>
		  WETN.WORK_EFFORT_ID = WETO.WORK_EFFORT_SNAPSHOT_ID		-- SE MAPPA.VERSIONE IS NULL			
	</#if>	
		)																																
WHERE WATN.WORK_EFFORT_ID_TO = <@param workEffortId />																																			
	AND WRTN.WORK_EFFORT_TYPE_ID = <@param workEffortTypeId />																																		
	 AND WATO.WORK_EFFORT_ID_TO = <@param oldWorkEffortId /> 																																		
	AND (
		<@compareNotEqual "WATN.COMMENTS" "WATO.COMMENTS" /> 
		OR WATN.ASSOC_WEIGHT <> WATO.ASSOC_WEIGHT																																		
	  	OR WATN.FROM_DATE <> WATO.FROM_DATE 
	  	OR WATN.THRU_DATE <> WATO.THRU_DATE
	  	)																																		
																					