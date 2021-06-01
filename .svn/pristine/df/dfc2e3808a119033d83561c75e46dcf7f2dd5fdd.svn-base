<#include   "component://accountingext/config/sql/partyRelationship/common/partyRelationshipCommon.sql.ftl" />

<@selectPartyRelationship />
, 
	<#if isPartTime?if_exists?has_content>
		<#if partyRelationTypeId == "ORG_EMPLOYMENT">
			SUM(ROUND(PE.EMPLOYMENT_AMOUNT/100, UM.DECIMAL_SCALE)) AS AMOUNT
		<#else>
			SUM(ROUND(PE.EMPLOYMENT_AMOUNT/100*PR.RELATIONSHIP_VALUE/100, UM.DECIMAL_SCALE)) AS AMOUNT
		</#if>
	<#else>
		<#if partyRelationTypeId == "ORG_EMPLOYMENT">
			COUNT(*) AS AMOUNT
		<#else>
			SUM(ROUND(PR.RELATIONSHIP_VALUE/100, UM.DECIMAL_SCALE)) AS AMOUNT
		</#if>
	</#if>
	
<@fromPartyRelationship />

<@wherePartyRelationship />

<@groupByPartyRelationship />
