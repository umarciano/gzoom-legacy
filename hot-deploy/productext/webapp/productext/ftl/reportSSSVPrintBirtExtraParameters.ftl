<#-- Calcola il 31 dicembre dell'anno corrente come default -->
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
<#assign currentYear = nowTimestamp?string("yyyy")>
<#assign defaultYearIndicator = currentYear + "-12-31">
<tr>
   <td class="label-for-print-popup">${uiLabelMap.PrintServiceIndicatorDate} <span class="required">*</span></td>
   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("yearIndicator_datePanel")>
   <td class="widget-area-style">
       <div class="datePanel calendarSingleForm mandatory" id="${id}">
           <input type="hidden" class="dateParams" name="paramName" value="yearIndicator"/>
           <input type="hidden" class="dateParams" name="time" value="false"/>
           <input type="hidden" class="dateParams" name="shortDateInput" value="true"/>
           <input type="hidden" class="dateParams" name="dateTimeValue" value="${defaultYearIndicator}"/>
           <input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/>
           <input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/>
           <input type="hidden" class="dateParams" name="localizedValue" value="${parameters.yearIndicator?default(defaultYearIndicator)}"/>
           <input type="hidden" class="dateParams" name="yearRange" value=""/>
           <input type="hidden" class="dateParams" name="size" value="10"/>
           <input type="hidden" class="dateParams" name="maxlength" value="10"/>
           <input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/>
           <input type="hidden" class="dateParams" name="classNames" value="print-parameters"/>
       </div>
   </td>
</tr>