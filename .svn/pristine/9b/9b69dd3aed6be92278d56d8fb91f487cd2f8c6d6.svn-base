<#if layoutSettings.headerImageUrl?exists>
    <#assign headerImageUrl = layoutSettings.headerImageUrl>
<#elseif layoutSettings.commonHeaderImageUrl?exists>
    <#assign headerImageUrl = layoutSettings.commonHeaderImageUrl>
    <#elseif layoutSettings.VT_HDR_IMAGE_URL?exists>
    <#assign headerImageUrl = layoutSettings.VT_HDR_IMAGE_URL.get(0)>
</#if>
<center>
    <img alt="${StringUtil.wrapString(layoutSettings.companyName)}" src="<@ofbizContentUrl>${StringUtil.wrapString(headerImageUrl)}</@ofbizContentUrl>"/>
</center>
<br>
<#assign copyrightLabel = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("BaseConfig", "info.copyrightLabel", "")>
<#if copyrightLabel?has_content>
    <#assign copyrightValue = Static["com.mapsengineering.base.util.MessageUtil"].getMessage(copyrightLabel, "", locale)>
</#if>
${copyrightValue?default("&nbsp;")}
<br>
<a href="<@ofbizUrl>licence</@ofbizUrl>" target="_blank">${uiLabelMap.BaseLicence}</a>
<br>
<#include "component://base/webapp/common/ftl/version.ftl"/>
<a href="#" class="smallSubmit button-close" onclick="modal_box_messages.buttonCancel()">${uiLabelMap.BaseButtonClose}</a>
