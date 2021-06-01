
<#if layoutSettings?has_content && layoutSettings.javaScriptBlocks?has_content>
    <#--layoutSettings.javaScriptBlocks is a list of java scripts. -->
    <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
    <#assign javaScriptsBlockSet = Static["org.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScriptBlocks)/>
    <#list layoutSettings.javaScriptBlocks as javaScriptBlock>
    <script type="text/javascript">
        <#if javaScriptsBlockSet.contains(javaScriptBlock)>
            <#assign nothing = javaScriptsBlockSet.remove(javaScriptBlock)/>
            <#include StringUtil.wrapString(javaScriptBlock) />
        </#if>
    </script>
    </#list>
</#if>
