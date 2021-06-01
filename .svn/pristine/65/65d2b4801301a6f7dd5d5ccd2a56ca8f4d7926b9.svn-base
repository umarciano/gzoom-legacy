<#import "/base/webapp/common/ftl/gzoomMacro.ftl"  as gzoom/>

<#if !weChildren?has_content>
    <script type="text/javascript">
        var jar = new CookieJar({path : "/"});
        jar.put('activeLinkWorkEffortAchieveExtTabMenu', 'management_0');
    </script>
</#if>
     
<div class="header-breadcrumbs">
    <div id="header-breadcrumbs-th"><@gzoom.breadcrumb parameters.breadcrumbsCurrentItem?if_exists "common-container"/></div>
    <#assign activeTabIndex = null>
    <#if activeTabIndex?has_content>
        <#if !weChildren?has_content && (parameters.workEffortTypeId?has_content || parameters.weTypeId?has_content)>
            <#assign activeTabIndex = 1>
            <input type="hidden" id="activeTabIndex" name="activeTabIndex" value="management_${activeTabIndex}" />
        </#if>
    </#if>
</div>