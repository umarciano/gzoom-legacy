<#function fieldTypeName groupName>
	<#return freeMarkerQuery.getFieldTypeName(groupName)>
</#function>

<#macro dateDiffAnno a, b, groupName = "">
<#local fieldTypeName = fieldTypeName(groupName)>
<#if fieldTypeName == "mysql">
	(DATEDIFF(${a}, ${b})+1)/365.000
<#elseif fieldTypeName == "mssql">
	(DATEDIFF(d, ${b}, ${a})+1)/365.000
<#else>
	(${a} - ${b}+1)/365.000
</#if>
</#macro>

<#macro dateDiffMese a, b, groupName = "">
<#local fieldTypeName = fieldTypeName(groupName)>
<#if fieldTypeName == "mysql">
	(DATEDIFF(${a}, ${b})+1)/30.420
<#elseif fieldTypeName == "mssql">
	(DATEDIFF(d, ${b}, ${a})+1)/30.420
<#else>
	(${a} - ${b}+1)/30.420
</#if>
</#macro>

<#macro dateAdd a, b, day, groupName = "">
<#local fieldTypeName = fieldTypeName(groupName)>
<#if fieldTypeName == "mysql">
	ADDDATE(${a}, INTERVAL ${b} ${day})
<#elseif fieldTypeName == "mssql">
	DATEADD(${day}, ${b}, ${a})
<#elseif fieldTypeName == "h2">
	DATEADD(${day}, ${b}, ${a})
<#elseif fieldTypeName == "postgres">
	(${a} + (interval '1 ${day}' * ${b}))
<#else>
	(${a} + ${b})
</#if>
</#macro>

<#macro dateAddMonitoringDate b, day, groupName = "">
<#local fieldTypeName = fieldTypeName(groupName)>
<#if fieldTypeName == "mysql">
	ADDDATE(<@param monitoringDate jdbcType.TIMESTAMP />, INTERVAL ${b} ${day})
<#elseif fieldTypeName == "mssql">
	DATEADD(${day}, ${b}, <@param monitoringDate jdbcType.TIMESTAMP />)
<#elseif fieldTypeName == "h2">
	DATEADD(${day}, ${b}, <@param monitoringDate jdbcType.TIMESTAMP />)
<#else>
	(<@param monitoringDate jdbcType.TIMESTAMP /> + ${b})
</#if>
</#macro>

<#macro convertFieldToVarcharMax field, alias, groupName="">
<#local fieldTypeName = fieldTypeName(groupName)>
<#if fieldTypeName == "mssql">
    CONVERT(VARCHAR(MAX), ${field}) AS ${alias}
<#else>
    ${field} AS ${alias}
</#if>
</#macro>

<#macro inizioAnno a groupName = "">
<#local fieldTypeName = fieldTypeName(groupName)>
<#if fieldTypeName == "mysql">
	DATE_FORMAT(${a} ,'%Y-01-01')
<#elseif fieldTypeName == "mssql">
	DATEADD(yy, DATEDIFF(yy, 0, ${a}), 0)
<#else>
	TO_DATE(TO_CHAR(${a}, 'YY-01-01'), 'YY-MM-DD')
</#if>
</#macro>

<#macro fineAnno a groupName = "">
<#local fieldTypeName = fieldTypeName(groupName)>
<#if fieldTypeName == "mysql">
	DATE_FORMAT(${a} ,'%Y-12-31')
<#elseif fieldTypeName == "mssql">
	DATEADD(yy, DATEDIFF(yy, 0, ${a}) + 1, -1) 
<#else>
	TO_DATE(TO_CHAR(${a}, 'YY-12-31'), 'YY-MM-DD')
</#if>
</#macro>