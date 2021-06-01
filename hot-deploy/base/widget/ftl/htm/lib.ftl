<#-- -->
<#assign parameters = Static["org.ofbiz.base.util.UtilHttp"].getCombinedMap(request)>
<#assign locale = request.getParameter("locale")?if_exists>
<#if locale?has_content>
    <#assign dummy = debugLog("*** set locale from request: " + locale)>
    <#assign dummy = Static["org.ofbiz.base.util.UtilHttp"].setLocale(request, locale)?if_exists>
</#if>
<#assign locale = Static["org.ofbiz.base.util.UtilHttp"].getLocale(request)>
<#assign dummy = parameters.put("locale", locale)?default("")>
<#assign dctx = dispatcher.getDispatchContext()>
<#-- -->
<#-- Debug -->
<#-- -->
<#function debugLog msg>
    <#local dummyLog = Static["org.ofbiz.base.util.Debug"].logInfo(msg, "lib.ftl")>
    <#return "">
</#function>
<#-- -->
<#-- Properties -->
<#-- -->
<#function getMessage resource, name, params = {}>
    <#return Static["org.ofbiz.base.util.UtilProperties"].getMessage(resource, name, params, locale)>
</#function>
<#function loadPropertyMap map = "", resource = "">
    <#if map?has_content>
        <#return Static["com.mapsengineering.base.util.MessageUtil"].loadPropertyMap(resource, map, locale)>
    <#else>
        <#return Static["com.mapsengineering.base.util.MessageUtil"].loadPropertyMap(resource, locale)>
    </#if>
</#function>
<#-- -->
<#-- Services -->
<#-- -->
<#function makeValidContext serviceName>
    <#return dctx.makeValidContext(serviceName, "IN", parameters)>
</#function>
<#function runSync serviceName serviceContext = {}>
    <#if !serviceContext?has_content>
        <#local serviceContext = makeValidContext(serviceName)>
    </#if>
    <#return dispatcher.runSync(serviceName, serviceContext)>
</#function>
<#-- -->
<#-- Formatting -->
<#-- -->
<#function formatDate d>
    <#return Static["org.ofbiz.base.util.UtilDateTime"].toDateString(d, locale)>
</#function>
