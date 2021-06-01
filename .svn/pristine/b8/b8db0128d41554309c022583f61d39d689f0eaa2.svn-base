<#macro selectPartyRelationship>
	SELECT 
		AC.GL_ACCOUNT_ID AS GL_ACCOUNT_ID, 
		PR.PARTY_ID_FROM AS PARTY_ID, 
		PR.ROLE_TYPE_ID_FROM AS ROLE_TYPE_ID
	    																																	
</#macro>

<#macro selectPartyRelationshipMinMax>
	SELECT 
		AC.GL_ACCOUNT_ID AS GL_ACCOUNT_ID, 
		PR.PARTY_ID_FROM AS PARTY_ID, 
		PR.ROLE_TYPE_ID_FROM AS ROLE_TYPE_ID,
		UM.DECIMAL_SCALE AS DECIMAL_SCALE,
		PR.RELATIONSHIP_VALUE AS RELATIONSHIP_VALUE,
		PE.EMPLOYMENT_AMOUNT AS EMPLOYMENT_AMOUNT,
		CASE WHEN COALESCE(PR.THRU_DATE, PD.THRU_DATE) < PD.THRU_DATE THEN PR.THRU_DATE ELSE PD.THRU_DATE END AS MIN_THRU_DATE,
		CASE WHEN PR.FROM_DATE >= PD.FROM_DATE THEN PR.FROM_DATE ELSE PD.FROM_DATE END AS MAX_FROM_DATE
	    																																	
</#macro>
	
<#macro fromPartyRelationship>
	FROM 
	<@table "GL_ACCOUNT"/> AC
		INNER JOIN <@table "UOM"/> UM ON UM.UOM_ID = AC.DEFAULT_UOM_ID
		INNER JOIN <@table "CUSTOM_TIME_PERIOD"/> PD ON PD.PERIOD_TYPE_ID = AC.PERIOD_TYPE_ID
		INNER JOIN <@table "EMPL_POSITION_TYPE"/> EP ON EP.PARENT_TYPE_ID = AC.EMPL_POSITION_TYPE_ID
		INNER JOIN <@table "PERSON"/> PE ON PE.EMPL_POSITION_TYPE_ID = EP.EMPL_POSITION_TYPE_ID
		INNER JOIN <@table "PARTY_RELATIONSHIP"/> PR ON PR.PARTY_ID_TO = PE.PARTY_ID 
			AND PR.PARTY_RELATIONSHIP_TYPE_ID = <@param partyRelationTypeId />
			AND PR.FROM_DATE <= PD.THRU_DATE 
			AND (
				PR.THRU_DATE IS NULL 
				OR
			    <#if isFinalPeriod >
			    	PR.THRU_DATE >= PD.THRU_DATE -- CM.CUSTOM_METHOD_NAME LIKE '%F' 
				<#else>
			    	PR.THRU_DATE >= PD.FROM_DATE -- CM.CUSTOM_METHOD_NAME LIKE '%M'
				</#if>																											
			)
</#macro>


<#macro wherePartyRelationship>
	WHERE PD.THRU_DATE = <@param refDate jdbcType.TIMESTAMP /> -- <LANCIO_DATA>
	<#if orgUnitId?if_exists?has_content>
	 AND PR.PARTY_ID_FROM = <@param orgUnitId /> AND PR.ROLE_TYPE_ID_FROM = <@param orgUnitRoleTypeId /> -- <LANCIO_UORG> (DA FOLDER CALCOLO INDICATORI)
	</#if>
	AND AC.GL_ACCOUNT_ID = <@param glAccountId /> -- <LANCIO_INDICATORE> SE VALORIZZATO (DA MAPPA DI LANCIO)

</#macro>

<#macro groupByPartyRelationship>
	GROUP BY AC.GL_ACCOUNT_ID, PR.PARTY_ID_FROM, PR.ROLE_TYPE_ID_FROM
</#macro>
	
<#macro groupByPartyRelationshipMinMax>
	GROUP BY A.GL_ACCOUNT_ID, A.DECIMAL_SCALE, A.PARTY_ID, A.ROLE_TYPE_ID
</#macro>
