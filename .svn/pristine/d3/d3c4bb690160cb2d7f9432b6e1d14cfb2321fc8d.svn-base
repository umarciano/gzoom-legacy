<#macro query1>
	SELECT  WRFO.COMMENTS AS DESCRIPTION, 
		WEFO.ETCH, 
		WEFO.WORK_EFFORT_NAME, 
		WAFO.COMMENTS, 
		WAFO.ASSOC_WEIGHT, 
		WAFO.SEQUENCE_NUM, 
		WAFO.FROM_DATE, 
		WAFO.THRU_DATE	
																																						
	FROM WORK_EFFORT_ASSOC WAFO																																					
		INNER JOIN WORK_EFFORT WEFO ON WEFO.WORK_EFFORT_ID = WAFO.WORK_EFFORT_ID_TO																																				
		INNER JOIN WORK_EFFORT_TYPE_ASSOC WRFO ON WRFO.WEFROM_WETO_ENUM_ID = 'WETAFROM'																																				
			AND WRFO.WORK_EFFORT_ASSOC_TYPE_ID = WAFO.WORK_EFFORT_ASSOC_TYPE_ID																																			
			AND WRFO.WORK_EFFORT_TYPE_ID_REF = WEFO.WORK_EFFORT_TYPE_ID	
																																					
	WHERE WAFO.WORK_EFFORT_ID_FROM = <@param param1 />																																					
		AND WRFO.WORK_EFFORT_TYPE_ID = <@param param2 />																																				
		AND NOT EXISTS																																				
		(SELECT 1 FROM WORK_EFFORT_ASSOC WAFN																																				
			INNER JOIN WORK_EFFORT WEFN ON WEFN.WORK_EFFORT_ID = WAFN.WORK_EFFORT_ID_TO																																			
		 WHERE WAFN.WORK_EFFORT_ID_FROM = <@param param3 />
			AND WAFN.WORK_EFFORT_ASSOC_TYPE_ID = WAFO.WORK_EFFORT_ASSOC_TYPE_ID	
			AND (WAFO.WORK_EFFORT_ID_TO = WAFN.WORK_EFFORT_ID_TO OR		
	    																																	
</#macro>

<#macro query2>
	SELECT  WRTO.COMMENTS AS DESCRIPTION,  
		WETO.ETCH, 
		WETO.WORK_EFFORT_NAME, 
		WATO.COMMENTS, 
		WATO.ASSOC_WEIGHT, 
		WATO.SEQUENCE_NUM, 
		WATO.FROM_DATE, 
		WATO.THRU_DATE		
																																					
	FROM WORK_EFFORT_ASSOC WATO																																					
		INNER JOIN WORK_EFFORT WETO ON WETO.WORK_EFFORT_ID = WATO.WORK_EFFORT_ID_FROM																																				
		INNER JOIN WORK_EFFORT_TYPE_ASSOC WRTO ON WRTO.WEFROM_WETO_ENUM_ID = 'WETATO'																																				
			AND WRTO.WORK_EFFORT_ASSOC_TYPE_ID = WATO.WORK_EFFORT_ASSOC_TYPE_ID																																			
			AND WRTO.WORK_EFFORT_TYPE_ID_REF = WETO.WORK_EFFORT_TYPE_ID																																			
	WHERE WATO.WORK_EFFORT_ID_TO = <@param param1 />																																					
		AND WRTO.WORK_EFFORT_TYPE_ID = <@param param2 />																																				
		AND NOT EXISTS																																				
		(SELECT 1 FROM WORK_EFFORT_ASSOC WATN																																			
			INNER JOIN WORK_EFFORT WETN ON WETN.WORK_EFFORT_ID = WATN.WORK_EFFORT_ID_FROM																																			
		 WHERE WATN.WORK_EFFORT_ID_TO = <@param param3 />
			AND WATN.WORK_EFFORT_ASSOC_TYPE_ID = WATO.WORK_EFFORT_ASSOC_TYPE_ID
		    AND (WATO.WORK_EFFORT_ID_FROM = WATN.WORK_EFFORT_ID_FROM OR																																	
</#macro>