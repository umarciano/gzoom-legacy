<#macro compareNotEqual a b>
	( ${a} is not null and (${b} is null or ${b} <> ${a}) ) or ( ${b} is not null and (${a} is null or ${a} <> ${b}) )
</#macro>
