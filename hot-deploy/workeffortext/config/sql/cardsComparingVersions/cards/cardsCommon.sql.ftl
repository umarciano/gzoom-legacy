<#macro selectCards>
	SELECT  WE.ETCH, 
		WE.WORK_EFFORT_NAME, 
		WE.DESCRIPTION, 
		P.PARTY_NAME, 
		WE.ESTIMATED_START_DATE, 
		WE.ESTIMATED_COMPLETION_DATE,
		WE.WORK_EFFORT_TYPE_ID,																																				
	    WE.WORK_EFFORT_ID,
	    WE.SEQUENCE_NUM,
	    WE.SOURCE_REFERENCE_ID
	    																																	
</#macro>


<#macro fromCards>
	FROM WORK_EFFORT WE																																				
		INNER JOIN PARTY P ON P.PARTY_ID = WE.ORG_UNIT_ID																																			
		INNER JOIN WORK_EFFORT_TYPE WET ON WET.WORK_EFFORT_TYPE_ID = WE.WORK_EFFORT_TYPE_ID
			AND WET.PARENT_TYPE_ID = <@param parameters.parentTypeId />
		
</#macro>


<#macro whereCards>
	WHERE WET.IS_ROOT = 'Y' AND WET.IS_TEMPLATE = 'N' 	
	<#if parameters.orgUnitId?if_exists?has_content>
		AND WE.ORG_UNIT_ID = <@param parameters.orgUnitId />                -- SE MAPPA.ORG_UNIT_ID IS NOT NULL																
	</#if>
</#macro>

<#macro conditionPermissionCards>
	AND( <@param parameters.userLoginId /> = 'admin' 
	 OR (<@param parameters.userProfile /> = 'MGR_ADMIN' OR (   
	    ((<@param parameters.userProfile /> = 'ORG_ROLE_ADMIN' 
	    	AND (EXISTS (SELECT 1 
				FROM PARTY_ROLE PT
				INNER JOIN PARTY_RELATIONSHIP PR ON PR.PARTY_ID_FROM = PT.PARTY_ID AND PR.ROLE_TYPE_ID_FROM = PT.ROLE_TYPE_ID
				INNER JOIN USER_LOGIN UL ON UL.PARTY_ID = PR.PARTY_ID_TO
				WHERE PT.PARTY_ID = WE.ORG_UNIT_ID AND PT.ROLE_TYPE_ID = WE.ORG_UNIT_ROLE_TYPE_ID
				AND (PR.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_RESPONSIBLE' OR 
					(PR.PARTY_RELATIONSHIP_TYPE_ID = 'ORG_DELEGATE' AND 
						(PR.CTX_ENABLED IS NULL 
							<#if weContextId?has_content>
								OR PR.CTX_ENABLED LIKE '%${weContextId?if_exists}%'
							</#if>
						)
					)
				)
				AND (PR.THRU_DATE IS NULL OR PR.THRU_DATE >= WE.ESTIMATED_COMPLETION_DATE)
				AND UL.USER_LOGIN_ID = <@param parameters.userLoginId />
				) OR EXISTS (SELECT 1      
	              FROM WORK_EFFORT_PARTY_ASSIGNMENT WA
					INNER JOIN USER_LOGIN UL ON UL.PARTY_ID = WA.PARTY_ID
				  WHERE WA.WORK_EFFORT_ID = WE.WORK_EFFORT_ID
					AND WA.ROLE_TYPE_ID LIKE 'WEM%'
					AND UL.USER_LOGIN_ID = <@param parameters.userLoginId />
			)))
	     OR
	    (<@param parameters.userProfile /> = 'ORG_ADMIN' 
	    	AND EXISTS (SELECT 1 
				FROM PARTY_ROLE PT
				INNER JOIN PARTY_RELATIONSHIP PR ON PR.PARTY_ID_FROM = PT.PARTY_ID AND PR.ROLE_TYPE_ID_FROM = PT.ROLE_TYPE_ID
				INNER JOIN USER_LOGIN UL ON UL.PARTY_ID = PR.PARTY_ID_TO
				WHERE PT.PARTY_ID = WE.ORG_UNIT_ID AND PT.ROLE_TYPE_ID = WE.ORG_UNIT_ROLE_TYPE_ID
				AND PR.PARTY_RELATIONSHIP_TYPE_ID IN ('ORG_RESPONSIBLE', 'ORG_DELEGATE')
				AND (PR.THRU_DATE IS NULL OR PR.THRU_DATE >= WE.ESTIMATED_COMPLETION_DATE)
				AND UL.USER_LOGIN_ID = <@param parameters.userLoginId />
				)
	   )) -- chiude primo blocco OR
	   
	   OR( (
			<@param parameters.userProfile /> = 'ROLE_ADMIN' 
			AND EXISTS (SELECT 1      -- filtro role
	          FROM WORK_EFFORT_PARTY_ASSIGNMENT WA
				INNER JOIN USER_LOGIN UL ON UL.PARTY_ID = WA.PARTY_ID
			  WHERE WA.WORK_EFFORT_ID = WE.WORK_EFFORT_ID
				AND WA.ROLE_TYPE_ID LIKE 'WEM%'
					AND UL.USER_LOGIN_ID = <@param parameters.userLoginId />
					)
		   ) OR ( 1=2 ))-- chiude secondo blocco or
	  ) -- chiude or iniziale 
	  )) -- chiude l'AND iniziale del controllo utente 	
</#macro>


<#macro orderCards>
	ORDER BY WE.SEQUENCE_NUM, WE.SOURCE_REFERENCE_ID, WE.WORK_EFFORT_ID
</#macro>