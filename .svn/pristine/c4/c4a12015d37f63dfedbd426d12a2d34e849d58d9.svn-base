<tr>
    <td class="label">${uiLabelMap.TypeNotes}</td>
    <td class="widget-area-style">
    	<select name="typeNotes" id="${printBirtFormId?default("ManagementPrintBirtForm")}_typeNotes" size="1" class="">
    		<option value="Y">${uiLabelMap.CommonInternal}</option>
    		<option <#if parameters.parentTypeId?if_exists == "CTX_EP">selected="selected" </#if> value="N">${uiLabelMap.CommonExternal}</option>
    		<option <#if parameters.parentTypeId?if_exists != "CTX_EP">selected="selected" </#if> value="ALL">${uiLabelMap.CommonAll}</option>
    	</select>
    </td>
</tr>