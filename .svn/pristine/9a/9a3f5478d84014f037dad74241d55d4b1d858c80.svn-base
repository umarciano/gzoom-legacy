<!-- TODO importare sempre anceh il roleTypeId-->

<tr>
   <td class="label">${uiLabelMap.previewFormRespName}</td>
   <td class="widget-area-style"><div  class="droplist_field" id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyId">
   <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
   <input  class="lookup_parameter" type="hidden" name="saveView" value="N"/>
   <input  class="autocompleter_parameter" type="hidden" name="distincts" value="[Y]"/>
   <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[PartyRoleView]"/>
   <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[partyId, partyName, parentRoleCode]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[partyName]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[partyName, parentRoleCode]]"/>
   <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[roleTypeId| equals| field:roleTypeId]]]"/>   
   <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="partyId"/>
   <div class="droplist_container">
   <input type="text" size="100" maxlength="255" value="" class="droplist_edit_field"  name="partyId_edit_value" id="${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_value"  />   
   <input type="hidden" class="droplist_code_field" name="partyId"/>
   <span class="droplist-anchor"><a style="cursor: pointer;" class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
</tr>



<script>
    RoleTypeIdPartyIdExtPrintBirtExtraParameter = {
        load : function() {
            $('${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_value').writeAttribute("readonly", "readonly");
            $('${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_value').next().writeAttribute("style", "display: none;");
            	            
            var roleTypeDropList = DropListMgr.getDropList('${printBirtFormId?default("ManagementPrintBirtForm")}_roleTypeId');
            if (roleTypeDropList) {
                roleTypeDropList.registerOnChangeListener(RoleTypeIdPartyIdExtPrintBirtExtraParameter.changeRadioButtons, 'RoleTypeIdPartyIdExtPrintBirtExtraParameterChangeRadioButtons');
            }
        },
        changeRadioButtons : function() {
            var roleTypeId = this._codeField.getValue();
            if (roleTypeId) {
                $('${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_value').removeAttribute("readonly");
                $('${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_value').next().removeAttribute("style");
            } else {
            	$$('input:[name="partyId"]')[0].setValue("");
            	$('${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_value').setValue("");
            	$('${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_value').writeAttribute("readonly", "readonly");
            	$('${printBirtFormId?default("ManagementPrintBirtForm")}_partyId_edit_value').next().writeAttribute("style", "display: none;");
            }
        }
    }
    
    RoleTypeIdPartyIdExtPrintBirtExtraParameter.load();
</script>