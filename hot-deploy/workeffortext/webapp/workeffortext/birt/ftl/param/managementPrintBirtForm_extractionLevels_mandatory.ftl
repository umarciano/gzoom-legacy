<tr>
    <td class="label">${uiLabelMap.ExtractionLevels}</td>
    <td class="widget-area-style">
    	<select name="extractionLevels" id="${printBirtFormId?default("ManagementPrintBirtForm")}_extractionLevels" size="1" class="mandatory">
    		<option value="ONLYUNREA">${uiLabelMap.OnlyUnreachable}</option>
    		<option value="JUSTLATE">${uiLabelMap.JustLate}</option>
    		<option selected="selected" value="ALL">${uiLabelMap.AllLevels}</option>
    	</select>
    </td>
</tr>