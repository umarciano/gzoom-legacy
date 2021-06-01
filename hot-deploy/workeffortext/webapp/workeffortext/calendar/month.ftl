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
<style type="text/css">
.calendar tr td {
height: 8em;
width: 10em;
vertical-align: top;
padding: 0.5em;
}
.calendar .header-row td {
height: auto;
}
</style>

<#if periods?has_content>
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
<table class="basic-table calendar padded-row-table hover-bar resizable selectable headerFixable" cellspacing="0">
  <thead>
  <tr class="header-row-2">
    <th width="1%">&nbsp;</th>
    <#list periods as day>
      <th>${day.start?date?string("EEEE")?cap_first}</th>
      <#if (day_index > 5)><#break></#if>
    </#list>
  </tr>
  </thead>
  <tbody>
  <#assign index = 0>
  <#list periods as period>
    <#assign currentPeriod = false/>
    <#if (nowTimestamp >= period.start) && (nowTimestamp <= period.end)><#assign currentPeriod = true/></#if>
    <#assign indexMod7 = period_index % 7>
    <#if indexMod7 = 0>
      <tr>
        <td class="label">
          <a href="#" onclick="ajaxUpdateAreas('${managementPaginationAreaId},<@ofbizUrl>${parameters._LAST_VIEW_NAME_}</@ofbizUrl>,period=week&start=${period.start.time?string("#")}&${urlParam?if_exists}&${addlParam?if_exists}&${extraParams?if_exists}'); return false;">${uiLabelMap.CommonWeek} ${period.start?date?string("w")}</a>
        </td>
    </#if>
    <td<#if currentPeriod> class="current-period"<#else><#if (index %2 != 0)> class="alternate-row"</#if></#if>>
      <span class="h1"><a href="#" onclick="ajaxUpdateAreas('${managementPaginationAreaId},<@ofbizUrl>${parameters._LAST_VIEW_NAME_}</@ofbizUrl>,period=day&start=${period.start.time?string("#")}&${urlParam?if_exists}&${addlParam?if_exists}&${extraParams?if_exists}'); return false;">${period.start?date?string("d")?cap_first}</a></span>

      <#if StringUtil.wrapString(extraParams)?index_of("&") == 0>
        <#assign extraParams = StringUtil.wrapString(extraParams)?substring(1)>
      </#if>
      <#if StringUtil.wrapString(urlParam)?index_of("&") == 0>
        <#assign urlParam = StringUtil.wrapString(urlParam)?substring(1)>
      </#if>

      <a href="#" onclick="javascript: Utils.showModalBox('<@ofbizUrl>managementContainerOnly</@ofbizUrl>?entityName=${entityName}&parentEntityName=Party&saveView=N&insertMode=Y&period=month&form=edit&start=${parameters.start?if_exists}&parentTypeId=${parentTypeId?if_exists}&currentStatusId=CAL_TENTATIVE&estimatedStartDate=${period.start?string(Static["org.ofbiz.base.util.UtilDateTime"].DATE_TIME_FORMAT)}&workEffortIdFrom=${parameters.workEffortIdRoot?if_exists}&estimatedCompletionDate=${period.end?string(Static["org.ofbiz.base.util.UtilDateTime"].DATE_TIME_FORMAT)}&${addlParam?if_exists}&${urlParam?if_exists}&${extraParams?if_exists}&managementScreenName=${managementScreenName?if_exists}&managementScreenLocation=${managementScreenLocation?if_exists}&managementFormName=${managementFormName}&managementFormLocation=${managementFormLocation?if_exists}&actionMenuName=${actionMenuName?if_exists}&actionMenuLocation=${actionMenuLocation?if_exists}&managementPaginationAreaId=MB_content&extraAreaTarget=${parameters._LAST_VIEW_NAME_}&extraAreaId=${managementPaginationAreaId}&extraTargetParameters=${StringUtil.wrapString(urlParam)?replace("&", "*")}*${StringUtil.wrapString(extraParams)?replace("&", "*")}*period=month*start=${parameters.start?if_exists}', {title : '${uiLabelMap.WorkEffortAddCalendarEvent}', afterLoadModal: LookupProperties.afterLoadModal.curry({'responderOnLoad' : CalendarExtension.responderOnLoad}), afterHideModal: LookupProperties.afterHideModal.curry({'responderOnLoad' : CalendarExtension.responderOnLoad}), width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.40), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.40)}); return false;">${uiLabelMap.CommonAddNew}</a>
      <br class="clear"/>

      <#list period.calendarEntries as calEntry>
        <#assign goodCalEntry = true>
        <#if workEffortAssocEnabled?default("N") =="Y">
            <#assign goodCalEntry = validEventWorkEffortIdList?has_content && validEventWorkEffortIdList.contains(calEntry.workEffort.workEffortId)>
        </#if>

        <#if goodCalEntry == true>
            <#if calEntry.workEffort.actualStartDate?exists>
                <#assign startDate = calEntry.workEffort.actualStartDate>
              <#else>
                <#assign startDate = calEntry.workEffort.estimatedStartDate?if_exists>
            </#if>

            <#if calEntry.workEffort.actualCompletionDate?exists>
                <#assign completionDate = calEntry.workEffort.actualCompletionDate>
              <#else>
                <#assign completionDate = calEntry.workEffort.estimatedCompletionDate?if_exists>
            </#if>

            <#if !completionDate?has_content && calEntry.workEffort.actualMilliSeconds?has_content && calEntry.workEffort.actualStartDate?has_content>
                <#assign completionDate =  calEntry.workEffort.actualStartDate + calEntry.workEffort.actualMilliSeconds>
            </#if>
            <#if !completionDate?has_content && calEntry.workEffort.estimatedMilliSeconds?has_content && calEntry.workEffort.estimatedStartDate?has_content>
                <#assign completionDate =  calEntry.workEffort.estimatedStartDate + calEntry.workEffort.estimatedMilliSeconds>
            </#if>
            <hr/>
            <#if (startDate.compareTo(period.start) <= 0 && completionDate?has_content && completionDate.compareTo(period.end) >= 0)>
              ${uiLabelMap.CommonAllDay}
            <#elseif startDate.before(period.start) && completionDate?has_content>
              ${uiLabelMap.CommonUntil} ${completionDate?time?string.short}
            <#elseif !completionDate?has_content>
              ${uiLabelMap.CommonFrom} ${startDate?time?string.short} - ?
            <#elseif completionDate.after(period.end)>
              ${uiLabelMap.CommonFrom} ${startDate?time?string.short}
            <#else>
              ${startDate?time?string.short}-${completionDate?time?string.short}
            </#if>
            <br/>
            <a href="#" onclick="Utils.showModalBox('<@ofbizUrl>managementContainerOnly</@ofbizUrl>?entityName=${updateEntityName}&workEffortId=${calEntry.workEffort.workEffortId?if_exists}&fromDate=${calEntry.workEffort.fromDate?if_exists}&roleTypeId=${calEntry.workEffort.roleTypeId?if_exists}&parentEntityName=Party&period=month&form=edit&start=${parameters.start?if_exists}&parentTypeId=${parentTypeId?if_exists}&${addlParam?if_exists}&${urlParam?if_exists}&${extraParams?if_exists}&managementScreenName=${managementScreenName?if_exists}&managementScreenLocation=${managementScreenLocation?if_exists}&managementFormName=${managementFormName}&managementFormLocation=${managementFormLocation?if_exists}&actionMenuName=${actionMenuName}&actionMenuLocation=${actionMenuLocation}&managementPaginationAreaId=MB_content&extraAreaTarget=${parameters._LAST_VIEW_NAME_}&extraAreaId=${managementPaginationAreaId}&extraTargetParameters=${StringUtil.wrapString(urlParam)?replace("&", "*")}*${StringUtil.wrapString(extraParams)?replace("&", "*")}*period=month*start=${parameters.start?if_exists}', {title : '${uiLabelMap.WorkEffortAddCalendarEvent}', afterLoadModal: LookupProperties.afterLoadModal.curry({'responderOnLoad' : CalendarExtension.responderOnLoad}), afterHideModal: LookupProperties.afterHideModal.curry({'responderOnLoad' : CalendarExtension.responderOnLoad}), width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.40), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.40)}); return false;" class="event">${calEntry.workEffort.workEffortName?default("Undefined")}</a>
            <br/>
        </#if>
      </#list>
    </td>

<#--
    <td valign="top">
      <table width="100%" cellspacing="0" cellpadding="0" border="0">
        <tr>
          <td nowrap class="monthdaynumber"><a href='<@ofbizUrl>day?start=${period.start.time?string("#")}<#if eventsParam?has_content>&${eventsParam}</#if>${addlParam?if_exists}</@ofbizUrl>' class="monthdaynumber">${period.start?date?string("d")?cap_first}</a></td>
          <td align="right"><a href='<@ofbizUrl>EditWorkEffort?workEffortTypeId=EVENT&currentStatusId=CAL_TENTATIVE&estimatedStartDate=${period.start?string("yyyy-MM-dd HH:mm:ss")}&estimatedCompletionDate=${period.end?string("yyyy-MM-dd HH:mm:ss")}${addlParam?if_exists}</@ofbizUrl>' class="add">${uiLabelMap.CommonAddNew}</a>&nbsp;&nbsp;</td>
        </tr>
      </table>
      <#list period.calendarEntries as calEntry>
      <table width="100%" cellspacing="0" cellpadding="0" border="0">
        <tr width="100%">
          <td class='monthcalendarentry' width="100%" valign='top'>
            <#if (calEntry.workEffort.estimatedStartDate.compareTo(period.start)  <= 0 && calEntry.workEffort.estimatedCompletionDate.compareTo(period.end) >= 0)>
              ${uiLabelMap.CommonAllDay}
            <#elseif calEntry.workEffort.estimatedStartDate.before(period.start)>
              ${uiLabelMap.CommonUntil} ${calEntry.workEffort.estimatedCompletionDate?time?string.short}
            <#elseif calEntry.workEffort.estimatedCompletionDate.after(period.end)>
              ${uiLabelMap.CommonFrom} ${calEntry.workEffort.estimatedStartDate?time?string.short}
            <#else>
              ${calEntry.workEffort.estimatedStartDate?time?string.short}-${calEntry.workEffort.estimatedCompletionDate?time?string.short}
            </#if>
            <br/>
            <a href="<@ofbizUrl>WorkEffortSummary?workEffortId=${calEntry.workEffort.workEffortId}${addlParam?if_exists}</@ofbizUrl>" class="event">${calEntry.workEffort.workEffortName?default("Undefined")}</a>&nbsp;
          </td>
        </tr>
      </table>
      </#list>
    </td>
-->
    <#if !period_has_next && indexMod7 != 6>
    <td colspan='${6 - (indexMod7)}'>&nbsp;</td>
    </#if>
  <#if indexMod7 = 6 || !period_has_next>
  </tr>
  </#if>
  </#list>
  </tbody>
</table>

<#else>
  <div class="screenlet-body">${uiLabelMap.WorkEffortFailedCalendarEntries}!</div>
</#if>
