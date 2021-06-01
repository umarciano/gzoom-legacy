<!--
    Accordion menu
 -->

<div id="vertical_container">
    <#assign nodeTrail = ["GP_MENU"]/>
    <@renderMenu contentId="GP_MENU" indentIndex=1 nodeTrail=nodeTrail/>
</div>

<#macro renderMenu contentId="" indentIndex=0 nodeTrail=[] viewSz=9999 viewIdx=0 originalBreadcrumbs="">
    <!-- start of renderCategoryBrowse for contentId=${contentId} -->

    <#local contentIdx = contentId?if_exists />
    <#if (!contentIdx?exists || contentIdx?length == 0)>
        <#local contentIdx = page.contentIdx?if_exists />
        <#if (!contentIdx?exists || contentIdx?length == 0)>
        </#if>
    </#if>

    <#local localBreadcrumbs = originalBreadcrumbs?if_exists/>

    <!-- Look for sub-topics -->
    <@loopSubContent contentId=contentIdx viewIndex=viewIdx viewSize=viewSz contentAssocTypeId="TREE_CHILD" returnAfterPickWhen="1==1" orderBy="[caSequenceNum]">
        <#local contentAttributes = delegator.findByAnd("ContentAttribute", {"contentId": content.contentId})/>

        <#local permission=true/>
        <#local permissionList=Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(contentAttributes, {"attrName":"permission"})/>
        <#if permissionList?has_content>
            <#local permission=Static["com.mapsengineering.base.menu.MenuHelper"].hasPermission(permissionList[0].attrValue?if_exists, userLogin, context.security)/>
        </#if>

        <#if permission>
            <#local breadcrumbs=""/>
            <#local title=""/>
            <#local titleLabel=""/>
            <#local defaultLabel=""/>
            <#local titleList=Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(contentAttributes, {"attrName":"title"})/>
            <#if titleList?has_content>
                <#local defaultLabel=uiLabelMap[StringUtil.wrapString(titleList[0].attrValue?if_exists)]/>
                <#local title=StringUtil.wrapString(Static["com.mapsengineering.base.util.MessageUtil"].getMessage(titleList[0].attrValue, defaultLabel, locale))/>

                <#if localBreadcrumbs?length==0>
                    <#local breadcrumbs = "["/>
                <#else>
                    <#local breadcrumbs = localBreadcrumbs + "|"/>
                </#if>
                <#local breadcrumbs = breadcrumbs + Static["org.ofbiz.base.util.StringUtil"].htmlEncoder.encode(Static["com.mapsengineering.base.util.MessageUtil"].getMessage(titleList[0].attrValue, defaultLabel, locale))/>
            </#if>
            <#if content.contentTypeId=="GPLUS_MENU_ITEM">
                <#local link=""/>
                <#local parameterList=[]/>
                <#local linkList=Static["org.ofbiz.entity.util.EntityUtil"].filterByCondition(contentAttributes, Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("attrName", Static["org.ofbiz.entity.condition.EntityOperator"].LIKE,  "link%"))/>
                <#local linkList=Static["org.ofbiz.entity.util.EntityUtil"].orderBy(linkList, ['attrName'])/>
                <#if linkList?has_content>
                    <#local linkValue = StringUtil.wrapString(linkList[0].attrValue)/>
                    <#if linkValue?index_of("?") != -1>
                        <#local link=linkValue?if_exists?substring(0, linkValue?index_of("?"))/>
                    <#else>
                        <#local link=linkValue/>
                    </#if>
                    <#local parameterList=Static["com.mapsengineering.base.menu.MenuUtil"].createParameterList(linkList, "attrValue")/>
                </#if>
                <#local targetType=""/>
                <#local targetTypeList=Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(contentAttributes, {"attrName":"target-type"})/>
                <#if targetTypeList?has_content>
                    <#local targetType=targetTypeList[0].attrValue/>
                </#if>
                <form action="${link?if_exists}" id="${content.contentId}" method="post" <#--onsubmit="javascript: ajaxSubmitFormUpdateAreas('${content.contentId}','common-main-container'); return false;"-->>
                    <#if targetType=="inter-app">
                        <input type="hidden" value="${requestAttributes.externalLoginKey}" name="externalLoginKey">
                    </#if>
                    <#if parameterList?has_content>
                        <#list parameterList as params>
                            <#local params=StringUtil.wrapString(params)/>
                            <input type="hidden" value="${params?split("=")[1]}" name="${params?split("=")[0]}">
                        </#list>
                    </#if>
                    <#if breadcrumbs?has_content>
                        <#local breadcrumbs = breadcrumbs + "]"/>
                        <input type="hidden" name="breadcrumbs" value="${breadcrumbs}"/>
                    </#if>
                    <input type="submit" value="${title}" class="accordion_link"/>
                </form>
            <#else>
                <#if indentIndex &gt; 1><div id="vertical_nested_container" ></#if>
                <h${indentIndex} class="<#if indentIndex == 1>accordion_toggle<#else>vertical_accordion_toggle</#if>">${title}</h${indentIndex}>
                <#if content.contentTypeId!="GPLUS_MENU_ITEM">
                    <#assign catTrail = nodeTrail + [subContentId]/>
                    <div class="<#if indentIndex == 1>accordion_content<#else>vertical_accordion_content</#if>"><@renderMenu contentId=content.contentId indentIndex=(indentIndex + 1) nodeTrail=catTrail viewSz=viewSz viewIdx=viewIdx originalBreadcrumbs=breadcrumbs/></div>
                </#if>
                <#if indentIndex &gt; 1></div></#if>
            </#if>
        </#if>
    </@loopSubContent>
</#macro>
