SELECT DISTINCT 
	PR.PARTY_RELATIONSHIP_TYPE_ID,
	P.PARTY_ID AS PARTY_ID_TO,
	P2.PARTY_ID AS PARTY_ID_FROM,
	PR.ROLE_TYPE_ID_TO,
	PR.ROLE_TYPE_ID_FROM,
	PR.FROM_DATE,
	PR.THRU_DATE	
FROM
	<@table "PARTY"/> P
INNER JOIN <@table "PARTY_RELATIONSHIP"/> PR ON
	P.PARTY_ID = PR.PARTY_ID_TO AND PR.ROLE_TYPE_ID_TO = 'WEM_EVAL_MANAGER'
INNER JOIN <@table "PARTY"/> P2 ON
	P2.PARTY_ID = PR.PARTY_ID_FROM AND PR.ROLE_TYPE_ID_FROM = 'WEM_EVAL_IN_CHARGE'
	
<#if orgUnitId?has_content && partyRelationshipTypeId?has_content> 
INNER JOIN <@table "PARTY_RELATIONSHIP"/> UOEC ON
	UOEC.PARTY_ID_TO = PR.PARTY_ID_FROM
	AND UOEC.PARTY_ID_FROM = <@param orgUnitId />
	AND UOEC.PARTY_RELATIONSHIP_TYPE_ID = <@param partyRelationshipTypeId />
	AND UOEC.THRU_DATE IS NULL
INNER JOIN <@table "PARTY_RELATIONSHIP"/> E ON 
	(E.PARTY_ID_FROM = UOEC.PARTY_ID_FROM
	AND E.ROLE_TYPE_ID_FROM = UOEC.ROLE_TYPE_ID_FROM
  	AND E.PARTY_RELATIONSHIP_TYPE_ID IN ('ORG_RESPONSIBLE', 'ORG_DELEGATE') 
  	AND E.THRU_DATE IS NULL)
</#if>

WHERE
	PR.PARTY_RELATIONSHIP_TYPE_ID = 'WEF_EVALUATED_BY'
	<#if partyIdFrom?has_content>
      AND PR.PARTY_ID_FROM = <@param partyIdFrom />
    </#if>
    <#if partyIdTo?has_content>
      AND PR.PARTY_ID_TO = <@param partyIdTo />
    </#if>
    <#if fromDate?has_content>
      AND PR.FROM_DATE = <@param fromDate jdbcType.TIMESTAMP/>
    </#if>
    <#if thruDate?has_content>
      AND PR.THRU_DATE = <@param thruDate jdbcType.TIMESTAMP />
    </#if>
    <#if onlyCurrentEvalRef?if_exists == 'Y'>
      AND PR.THRU_DATE IS NULL
    </#if>
    <#if isOrgMgr?default(false) == true>
		AND P2.PARTY_ID IN (
		SELECT
			DISTINCT EC.PARTY_ID
		FROM
			<@table "PARTY_ROLE"/> EC
		INNER JOIN <@table "PARTY_RELATIONSHIP"/> UOEC ON
			UOEC.PARTY_ID_TO = EC.PARTY_ID
			AND UOEC.PARTY_RELATIONSHIP_TYPE_ID = <@param partyRelationshipTypeId />
			AND UOEC.THRU_DATE IS NULL
		INNER JOIN <@table "PARTY_RELATIONSHIP"/> UOEM ON
			UOEM.PARTY_ID_FROM = UOEC.PARTY_ID_FROM
			AND UOEM.ROLE_TYPE_ID_FROM = UOEC.ROLE_TYPE_ID_FROM
			AND UOEM.PARTY_RELATIONSHIP_TYPE_ID IN(
				'ORG_RESPONSIBLE',
				'ORG_DELEGATE'
			)
			AND UOEM.THRU_DATE IS NULL
		INNER JOIN <@table "PARTY_ROLE"/> EM ON
			EM.PARTY_ID = UOEM.PARTY_ID_TO
			AND EM.ROLE_TYPE_ID = 'WEM_EVAL_MANAGER'
		WHERE
			UOEM.PARTY_ID_TO = '${userLogin.partyId}'
		)
	</#if>