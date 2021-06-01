<tr>
	<td class="label">${uiLabelMap.CommonFrom}</td>
	   <#assign id=Static["com.mapsengineering.base.util.FreemarkerWorker"].getFieldIdWithTimeStamp("fromDate_datePanel")>
	   <td class="widget-area-style"><div class="datePanel calendarSingleForm" id="${id}">
	   <input type="hidden" class="dateParams" name="paramName" value="fromDate"/>
	   <input type="hidden" class="dateParams" name="time" value="false"/>
	   <input type="hidden" class="dateParams" name="shortDateInput" value="true"/>
	   <input type="hidden" class="dateParams" name="dateTimeValue" value=""/>
	   <input type="hidden" class="dateParams" name="localizedInputTitle" value="${uiLabelMap.CommonFormatDate}"/>
	   <input type="hidden" class="dateParams" name="localizedIconTitle" value="${uiLabelMap.ShowedCommonFormatDate}"/>
	   <input type="hidden" class="dateParams" name="yearRange" value=""/>
	   <input type="hidden" class="dateParams" name="localizedValue" value=""/>
	   <input type="hidden" class="dateParams" name="size" value="10"/>
	   <input type="hidden" class="dateParams" name="maxlength" value="10"/>
	   <input type="hidden" class="dateParams" name="locale" value="${locale.getLanguage()}"/>
	   <input type="hidden" class="dateParams" name="classNames" value="mandatory"/></div></td>
</tr>