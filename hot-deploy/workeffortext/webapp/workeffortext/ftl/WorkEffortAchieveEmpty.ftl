	<#import "/base/webapp/common/ftl/gzoomMacro.ftl"  as gzoom/>

	<script type="text/javascript">
        var selectors = ["div.mblShowDescription"];
        populateTooltip(selectors);
    </script>
	
	<#macro formatDate date=null>
		<#if date?is_date>
			${date?string("dd/MM/yyyy")}
		</#if>
	</#macro>
	
	<#assign pattern = "##"/>
	<div class="header-breadcrumbs">
        <div id="header-breadcrumbs-th"><@gzoom.breadcrumb parameters.breadcrumbsCurrentItem?if_exists parameters.backAreaId?if_exists/></div>
        <#assign activeTabIndex = null>
        <#if activeTabIndex?has_content>
            <#if hideMainFolder?has_content>
        	    <#assign activeTabIndex = 1>
                <input type="hidden" id="activeTabIndex" name="activeTabIndex" value="management_${activeTabIndex}" />
            </#if>
        </#if>
    </div>
