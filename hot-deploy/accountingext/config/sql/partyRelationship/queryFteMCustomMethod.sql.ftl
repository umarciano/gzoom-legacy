<#include "classpath://sql/FtlQuery-vendor-lib.ftl" />
<#include "classpath://sql/partyRelationship/common/partyRelationshipCommon.sql.ftl" />


SELECT A.GL_ACCOUNT_ID, A.PARTY_ID, A.ROLE_TYPE_ID 

, 
	<#if isPartTime?if_exists?has_content>
		<#if partyRelationTypeId == "ORG_EMPLOYMENT">
			SUM(
				ROUND(EMPLOYMENT_AMOUNT/100*
					<@dateDiffMese "MIN_THRU_DATE", "MAX_FROM_DATE" />
					, DECIMAL_SCALE
				)
			) AS AMOUNT
	
		<#else>
			SUM(ROUND(EMPLOYMENT_AMOUNT/100*RELATIONSHIP_VALUE/100*
					<@dateDiffMese "MIN_THRU_DATE", "MAX_FROM_DATE" />
					, DECIMAL_SCALE
				)
			) AS AMOUNT
			
		</#if>
	<#else>
		<#if partyRelationTypeId == "ORG_EMPLOYMENT">
			SUM(
				ROUND(
					<@dateDiffMese "MIN_THRU_DATE", "MAX_FROM_DATE" />
					, DECIMAL_SCALE
				)
			) AS AMOUNT

	<#else>
			SUM(
				ROUND(RELATIONSHIP_VALUE/100*
					<@dateDiffMese "MIN_THRU_DATE", "MAX_FROM_DATE" />
					, DECIMAL_SCALE
				)
			) AS AMOUNT

	</#if>
	</#if>

FROM (
			
<@selectPartyRelationshipMinMax />
		
<@fromPartyRelationship />

<@wherePartyRelationship />

) A
<@groupByPartyRelationshipMinMax />
