<#macro table tableName groupName = "">
    ${freeMarkerQuery.getTableName(tableName, groupName)}<#t>
</#macro>
<#macro param value type = 0>
    <#local void=freeMarkerQuery.addParam(value, type)?if_exists>
    ?<#t>
</#macro>
