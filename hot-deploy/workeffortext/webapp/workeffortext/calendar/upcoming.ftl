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

  <#if days?has_content>
  	<script type="text/javascript">
        CalendarExtension = {
            load : function(){
            	CalendarExtension.responderOnLoad = Object.extend({name: 'CalendarExtension', onBeforeLoad : CalendarExtension.onBeforeLoadListener}, LookupProperties.responderOnLoad);
            },
            
            afterLoadModal : function() {
            },
            
            responderOnLoad : false,

		    onBeforeLoadListener : function(contentToUpdate, options) {
		    	if (Object.isString(contentToUpdate) && !Object.isElement($(contentToUpdate))) {
		            var data = options['data'];
		            Utils.hideModalBox({
		                afterHide: LookupProperties.afterHideModal.wrap(function(original, options) {
		                    original(options);
		                    UpdateAreaResponder.executeAjaxUpdateAreas(contentToUpdate, data);
		                })
		            });
		        } else {
		            return true;
		        }
		
		        return false;
		    },
            
        }
        CalendarExtension.load();
    </script>
    <table class="basic-table list-table padded-row-table hover-bar resizable draggable toggleable selectable customizable headerFixable" cellspacing="0">
        <thead>
      <tr class="header-row-2">
        <th>${uiLabelMap.CommonStartDateTime}</th>
        <th>${uiLabelMap.CommonEndDateTime}</th>
        <th>${uiLabelMap.CommonType}</th>
        <th>${uiLabelMap.WorkEffortName}</th>
      </tr>
      </thead>
      <tbody>
      <#assign dateTimeFormat = Static["org.ofbiz.base.util.UtilDateTime"].getDateTimeFormat(locale)>
      <#assign index = 0>
      <#list days as day>
        <#assign workEfforts = day.calendarEntries>
        <#if workEfforts?has_content>
          <#--<tr class="header-row"><th colspan="4"><hr/></th></tr>-->
          <#list workEfforts as calendarEntry>
            <#assign goodCalEntry = true>
            <#if workEffortAssocEnabled?default("N") =="Y">
                <#assign goodCalEntry = validEventWorkEffortIdList?has_content && validEventWorkEffortIdList.contains(calendarEntry.workEffort.workEffortId)>
            </#if>
            <#if goodCalEntry == true>
                <#assign workEffort = calendarEntry.workEffort>
                <tr<#if (index %2 != 0)> class="alternate-row"</#if>>
                  <td><#if workEffort.actualStartDate?exists>${workEffort.actualStartDate?string(dateTimeFormat)}<#else>${workEffort.estimatedStartDate?string(dateTimeFormat)}</#if></td>
                  <td><#if workEffort.actualCompletionDate?exists>${workEffort.actualCompletionDate?string(dateTimeFormat)}<#else>${workEffort.estimatedCompletionDate?string(dateTimeFormat)}</#if></td>
                  <td>${workEffort.getRelatedOne("WorkEffortType").get("description",locale)}</td>
                  <td><a href="#" onclick="Utils.showModalBox('<@ofbizUrl>managementContainerOnly</@ofbizUrl>?entityName=${updateEntityName}&workEffortId=${calendarEntry.workEffort.workEffortId?if_exists}&fromDate=${calendarEntry.workEffort.fromDate?if_exists}&roleTypeId=${calendarEntry.workEffort.roleTypeId?if_exists}&parentEntityName=Party&period=upcoming&form=edit&start=${parameters.start?if_exists}&parentTypeId=${parentTypeId?if_exists}&${addlParam?if_exists}&${urlParam?if_exists}&${extraParams?if_exists}&managementScreenName=${managementScreenName?if_exists}&managementScreenLocation=${managementScreenLocation?if_exists}&managementFormName=${managementFormName}&managementFormLocation=${managementFormLocation?if_exists}&actionMenuName=${actionMenuName}&actionMenuLocation=${actionMenuLocation}&managementPaginationAreaId=MB_content&extraAreaTarget=${parameters._LAST_VIEW_NAME_}&extraAreaId=${managementPaginationAreaId}&extraTargetParameters=${StringUtil.wrapString(urlParam)?replace("&", "*")}*${StringUtil.wrapString(extraParams)?replace("&", "*")}*period=week*start=${parameters.start?if_exists}', {title : '${uiLabelMap.WorkEffortAddCalendarEvent}', afterLoadModal: LookupProperties.afterLoadModal.curry({'responderOnLoad' : CalendarExtension.responderOnLoad}), afterHideModal: LookupProperties.afterHideModal.curry({'responderOnLoad' : CalendarExtension.responderOnLoad}), width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.40), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.40)}); return false;" class="event">${calendarEntry.workEffort.workEffortName?default("Undefined")}</a>&nbsp;</td>
                </tr>
                <#assign index = index+1>
            </#if>
          </#list>
        </#if>
      </#list>
      </tbody>
    </table>
  <#else>
    <div class="screenlet-body">${uiLabelMap.WorkEffortNoEventsFound}.</div>
  </#if>
</div>