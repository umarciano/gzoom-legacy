<#if !breadcrumbs?has_content && parameters.breadcrumbs?has_content>
    <#assign breadcrumbs = parameters.breadcrumbs/>
</#if>

<#if breadcrumbs?has_content>
    <#assign breadcrumbList = StringUtil.toList(breadcrumbs,"\\|")/>
    <#assign count=0>
    <#list breadcrumbList as breadcrumb>
        <#assign b = StringUtil.wrapString(breadcrumb)/>
        <p class="breadcrumbs">${b}</p>
        <#if count &lt; breadcrumbList?size-1>
        <p class="breadcrumbs-separator">&gt;</p>
        </#if>
        <#assign count=count+1>
    </#list>
</#if>
