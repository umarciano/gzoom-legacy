<#include "component://base/widget/ftl/htm/lib.ftl">
<#macro baseMainDecorator body>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
    <#assign langDir = "rtl">
</#if>
<#if !applicationTitle?has_content>
    <#assign applicationTitle = parameters.get("applicationTitle")?if_exists>
</#if>
<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <#if metaTags?has_content>

        <#list metaTags as metaTag>
            <#lt>    ${metaTag}
        </#list>
    </#if>

    <title>${applicationTitle?if_exists}<#if applicationTitle?has_content> - </#if>${applicationName?if_exists}<#if applicationName?has_content> - </#if>${companyName?if_exists}</title>

    <link rel="shortcut icon" href="/theme_gplus/ofbiz.ico" />
    <#if stylesheets?has_content>

        <#list stylesheets as stylesheet>
            <#lt>    <link rel="stylesheet" href="${StringUtil.wrapString(stylesheet)}" type="text/css" />
        </#list>
    </#if>
    <#if javascripts?has_content>

        <#list javascripts as javascript>
            <#lt>    <script src="${StringUtil.wrapString(javascript)}" type="text/javascript"></script>
        </#list>
    </#if>
</head>
<body>
${body?if_exists}
</body>
</html>
<#-- -->
</#macro>
<#-- -->
<#macro addMetaTag metaTag>
    <#if !metaTags?has_content>
        <#assign metaTags = []>
    </#if>
    <#assign metaTags = metaTags + [ metaTag ]>
</#macro>
<#-- -->
<#macro addStylesheet stylesheet>
    <#if !stylesheets?has_content>
        <#assign stylesheets = []>
    </#if>
    <#assign stylesheets = stylesheets + [ stylesheet ]>
</#macro>
<#-- -->
<#macro addJavascript javascript>
    <#if !javascripts?has_content>
        <#assign javascripts = []>
    </#if>
    <#assign javascripts = javascripts + [ javascript ]>
</#macro>
