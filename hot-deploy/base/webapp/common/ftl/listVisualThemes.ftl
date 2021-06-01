<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#--<style type="text/css">
.screenlet {
margin: 1em;
}
</style>-->

<div class="screenlet screenlet-lookup">
  <div class="screenlet-title-bar">
    <ul>
      <li class="h3">${uiLabelMap.CommonVisualThemes}</li>
      <#--<li><a href="<@ofbizUrl>main</@ofbizUrl>">${uiLabelMap.CommonCancel}</a></li>-->
    </ul>
    <br class="clear"/>
  </div>
  <#if visualThemes?has_content>
    <#assign orderByList = Static["org.ofbiz.base.util.UtilMisc"].toList("sequenceId")/>
    <table class="basic-table padded-row-table hover-bar resizable selectable headerFixable" cellspacing="0">
        <thead>
            <tr></tr>
        </thead>
      <tbody>
      <#assign index = 0>

      <#assign oldResourceList = delegator.findByAndCache("VisualThemeResource",Static["org.ofbiz.base.util.UtilMisc"].toMap("visualThemeId", visualThemeId, "resourceTypeEnumId", "VT_STYLESHEET"))>
      <#assign oldResourceList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(oldResourceList, orderByList)>

      <#list visualThemes as visualTheme>
        <#assign newResourceList = delegator.findByAndCache("VisualThemeResource",Static["org.ofbiz.base.util.UtilMisc"].toMap("visualThemeId", visualTheme.visualThemeId))>
        <#assign newResourceList = Static["org.ofbiz.entity.util.EntityUtil"].orderBy(newResourceList, orderByList)>

        <#assign screenshots = Static["org.ofbiz.entity.util.EntityUtil"].filterByAnd(newResourceList, Static["org.ofbiz.base.util.UtilMisc"].toMap("resourceTypeEnumId", "VT_SCREENSHOT"))>
        <#assign newResourceList = Static["org.ofbiz.entity.util.EntityUtil"].filterByCondition(newResourceList, Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(
                                                                                                                    Static["org.ofbiz.base.util.UtilMisc"].toList(
                                                                                                                        Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("resourceTypeEnumId", "VT_STYLESHEET"),
                                                                                                                        Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("resourceTypeEnumId", "VT_HDR_JAVASCRIPT")),
                                                                                                                        Static["org.ofbiz.entity.condition.EntityJoinOperator"].OR))>

        <tr<#if visualTheme.visualThemeId == visualThemeId> class="selected"<#else><#if (index %2 != 0)> class="alternate-row"</#if></#if>>
          <td>
            <input type="hidden" class="parameter-to-save" name="userPrefGroupTypeId" value="GLOBAL_PREFERENCES"/>
            <input type="hidden" class="parameter-to-save" name="userPrefTypeId" value="VISUAL_THEME"/>
            <input type="hidden" class="parameter-to-save" name="userPrefValue" value="${visualTheme.visualThemeId}"/>
            <input type="hidden" id="url" name="url" value="<@ofbizUrl>setUserPreference</@ofbizUrl>"/>

            <#assign newResourceLocationList = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(newResourceList, "resourceValue", true)>
            <#assign newResourceLocationList = Static["org.ofbiz.base.util.StringUtil"].join(newResourceLocationList, ' ')>

            <#assign oldResourceLocationList = Static["org.ofbiz.entity.util.EntityUtil"].getFieldListFromEntityList(oldResourceList, "resourceValue", true)>
            <#assign oldResourceLocationList = Static["org.ofbiz.base.util.StringUtil"].join(oldResourceLocationList, ' ')>

            <input type="hidden" name="oldResourceList" class="old-resources" value="${StringUtil.wrapString(oldResourceLocationList)}"/>
            <input type="hidden" name="newResourceList" class="new-resources" value="${StringUtil.wrapString(newResourceLocationList)}"/>
            ${visualTheme.description}
          </td>
          <td>
            <#if visualTheme.visualThemeId == visualThemeId>${uiLabelMap.CommonVisualThemeSelected}<#else>&nbsp;</#if>
          </td>
          <td>
            <#if screenshots?has_content>
              <#list screenshots as screenshot>
                <a href="#" onclick="javascript: window.open('<@ofbizContentUrl>${screenshot.resourceValue}</@ofbizContentUrl>','', 'width=800, height=600, status=no, menubar=no, toolbat=no'); return false;"><img src="<@ofbizContentUrl>${screenshot.resourceValue}</@ofbizContentUrl>" width="150"/></a>
              </#list>
           <#else>
              ${uiLabelMap.CommonVisualThemeNoScreenshots}
            </#if>
          </td>
        </tr>
        <#assign index = index+1>
      </#list>
      </tbody>
    </table>
  </#if>
</div>
