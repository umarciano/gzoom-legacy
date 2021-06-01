<tr>
    <td class="label">${uiLabelMap.FilterAssessors}</td>
    <td class="widget-area-style"><select name="assessors" id="${printBirtFormId?default("ManagementPrintBirtForm")}_assessors" size="1" class="">
    <option selected="selected" value="NOT_CONTROLLED">${uiLabelMap.FilterAssessorsNotControlled}</option>
    <option value="NOT_COMPILED">${uiLabelMap.FilterAssessorsNotCompiled}</option>
    <option value="ONLY_VERIFIED">${uiLabelMap.FilterAssessorsOnlyVerified}</option>
    <option value="ONLY_NOT_VERIFIED">${uiLabelMap.FilterAssessorsOnlyNotVerified}</option>
    </select></td>
</tr>