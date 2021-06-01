
<!--
	 Costruisce il parametro da passare per gestire i breadcrumnbs
	 Parametri
	 	breadcrumbItemsList: lista csv delle label da mostrare nella riga breadcrumb  
	 	backAreaId:  id dell'area sulla quale fare l'upload ajax 
 -->
<#macro breadcrumb breadcrumbItemsList backAreaId>
	
	<#if breadcrumbItemsList?has_content>
	
		<#assign itemList = Static["org.ofbiz.base.util.StringUtil"].split(breadcrumbItemsList, "_**_") />
		
		<#assign size = itemList.size()/>
		<#assign index = 1/>
		
		<#if Static["org.ofbiz.base.util.UtilValidate"].isEmpty(parameters.userLoginId) || "_NA_" != parameters.userLoginId>
			<#assign breadcrumbRequest = "breadcrumbRequest"/>
			<#else>
				<#assign breadcrumbRequest = "breadcrumbRequestNotAuth"/>
		</#if>
		
		<#list itemList as item>
			<#if (index > 1)>
				<span class="active-breadcrumbs-separator">&rsaquo;</span>
			</#if>
			<span class="active-breadcrumbs">
				<a href="#" onclick="ajaxUpdateAreas('${backAreaId},<@ofbizUrl>${breadcrumbRequest}?breadcrumbSize=${size}&breadcrumbIndex=${index}</@ofbizUrl>,'); return false;">${item}</a>
			</span>
			<#assign index = index + 1/>
		</#list>
				
	</#if>	
	
</#macro>