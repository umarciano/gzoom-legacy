
<form class="basic-form" id="searchFormJobLogLog" name="searchFormJobLogLog" >
	<input name="entityName" id="SearchJobLogLog_jobLogId" value="JobLogLog" type="hidden">
	<input name="parentEntityName" id="SearchJobLogLog_parentEntityName" value="JobLog" type="hidden">
	<input name="childManagementFormListScreenName" id="SearchJobLogLog_childManagementFormListScreenName" value="JobLogLogChildManagementFormListScreen" type="hidden">
	<input name="childManagementFormListScreenLocation" id="SearchJobLogLog_childManagementFormListScreenLocation" value="component://commondataext/widget/JobLogScreens.xml" type="hidden">
	<input name="childManagementFormListLocation" id="SearchJobLogLog_childManagementFormListLocation" value="component://commondataext/widget/JobLogForms.xml" type="hidden">
	<input name="jobLogId" id="SearchJobLogLog_jobLogId" value="${parameters.jobLogId}" type="hidden">
	<input name="orderBy" id="SearchJobLogLog_orderBy" value="jobLogLogId" type="hidden">
		
	<input name="viewIndex" id="SearchJobLogLog_VIEW_INDEX_" value="0" type="hidden">
					
	<div class="fieldgroup">
		<div class="fieldgroup-body">
			<table id="table_JobLogLogSearch" class="hover-bar resizable draggable toggleable selectable customizable headerFixable" cellspacing="0">
				<tbody>	
				
					<#-- jobLogLogId -->
					<tr>
					  	<td class="label">${uiLabelMap.FormFieldTitle_jobLogLogId}</td>
					    <td colspan="10">
					    <div class="text-find" id="SearchJobLogLog_jobLogLogId">
					    <div class="droplist_container"> 
					    	<select name="jobLogLogId_op" class="selectBox filter-field-selection">
					    		<option value="between">${uiLabelMap.between}</option>
                                <option value="notBetween">${uiLabelMap.not_between}</option>
                                <option value="like">${uiLabelMap.like}</option>
                                <option value="equals">${uiLabelMap.equal}</option>
                                <option value="notEqual">${uiLabelMap.not_equal}</option>
                                <option value="empty">${uiLabelMap.is_empty}</option>
                                <option value="notEmpty">${uiLabelMap.is_not_empty}</option>
                                <option value="contains" selected="selected">${uiLabelMap.contains}</option>
					    	</select>
					    	<input size="30" autocomplete="off" class="text-find-element" name="jobLogLogId" id="jobLogLogId" type="text">
					    </div></div>
					</tr>				
				   	<#-- logTypeEnumId -->
					<tr>
					    <td class="label">${uiLabelMap.FormFieldTitle_logTypeEnumId}</td>
					    <td class="widget-area-style"><div  class="droplist_field" id="SearchJobLogLog_logTypeEnumId">
					    <input  class="autocompleter_option" type="hidden" name="target" value="<@ofbizUrl>ajaxAutocompleteOptions</@ofbizUrl>"/>
					    <input  class="autocompleter_parameter" type="hidden" name="entityName" value="[Enumeration]"/>
					    <input  class="autocompleter_parameter" type="hidden" name="selectFields" value="[[description, enumId]]"/>
					    <input  class="autocompleter_parameter" type="hidden" name="sortByFields" value="[[description]]"/>
					    <input  class="autocompleter_parameter" type="hidden" name="displayFields" value="[[description]]"/>
					    <input  class="autocompleter_parameter" type="hidden" name="constraintFields" value="[[[enumTypeId| equals| JOBLOGTYPE]]]"/>
					    <input  class="autocompleter_parameter" type="hidden" name="saveView" value="N"/>
					    <input  class="autocompleter_parameter" type="hidden" name="entityKeyField" value="enumId"/>
					    <input  class="autocompleter_parameter" type="hidden" name="entityDescriptionField" value="description"/>
					    <div class="droplist_container">
					    <input type="hidden" name="logTypeEnumId" value=""  class="droplist_code_field"/>
					    <input type="text"id="SearchJobLogLog__logTypeEnumId_edit_field" name="description_logTypeEnumId" size="50" maxlength="255"value=""  class="droplist_edit_field"/>
					    <span class="droplist-anchor"><a class="droplist_submit_field fa fa-2x" href="#"></a></span></div></div></td>
					</tr>
					
					<#-- descrizione -->
					<tr>
					  	<td class="label">${uiLabelMap.FormFieldTitle_logMessage}</td>
					    <td colspan="10">
					    <div class="text-find" id="SearchJobLogLog_logMessage">
					    <div class="droplist_container"> 
					    	<select name="logMessage_op" class="selectBox filter-field-selection">
					    		<option value="between">${uiLabelMap.between}</option>
                                <option value="notBetween">${uiLabelMap.not_between}</option>
                                <option value="like">${uiLabelMap.like}</option>
                                <option value="equals">${uiLabelMap.equal}</option>
                                <option value="notEqual">${uiLabelMap.not_equal}</option>
                                <option value="empty">${uiLabelMap.is_empty}</option>
                                <option value="notEmpty">${uiLabelMap.is_not_empty}</option>
                                <option value="contains" selected="selected">${uiLabelMap.contains}</option>
					    	</select>
					    	<input size="30" autocomplete="off" class="text-find-element" name="logMessage" id="logMessage" type="text">
					    </div></div>
					</tr>
					
					<#-- valueRef1 -->
					<tr>
					  	<td class="label">${uiLabelMap.FormFieldTitle_valueRef1}</td>
					    <td colspan="10">
					    <div class="text-find" id="SearchJobLogLog_valueRef1">
					    <div class="droplist_container"> 
					    	<select name="valueRef1_op" class="selectBox filter-field-selection">
					    		<option value="between">${uiLabelMap.between}</option>
                                <option value="notBetween">${uiLabelMap.not_between}</option>
                                <option value="like">${uiLabelMap.like}</option>
                                <option value="equals">${uiLabelMap.equal}</option>
                                <option value="notEqual">${uiLabelMap.not_equal}</option>
                                <option value="empty">${uiLabelMap.is_empty}</option>
                                <option value="notEmpty">${uiLabelMap.is_not_empty}</option>
                                <option value="contains" selected="selected">${uiLabelMap.contains}</option>
					    	</select>
					    	<input size="30" autocomplete="off" class="text-find-element" name="valueRef1" id="valueRef1" type="text">
					    </div></div>
					</tr>	
					
					<#-- valueRef2 -->
					<tr>
					  	<td class="label">${uiLabelMap.FormFieldTitle_valueRef2}</td>
					    <td colspan="10">
					    <div class="text-find" id="SearchJobLogLog_valueRef2">
					    <div class="droplist_container"> 
					    	<select name="valueRef2_op" class="selectBox filter-field-selection">
					    		<option value="between">${uiLabelMap.between}</option>
                                <option value="notBetween">${uiLabelMap.not_between}</option>
                                <option value="like">${uiLabelMap.like}</option>
                                <option value="equals">${uiLabelMap.equal}</option>
                                <option value="notEqual">${uiLabelMap.not_equal}</option>
                                <option value="empty">${uiLabelMap.is_empty}</option>
                                <option value="notEmpty">${uiLabelMap.is_not_empty}</option>
                                <option value="contains" selected="selected">${uiLabelMap.contains}</option>
					    	</select>
					    	<input size="30" autocomplete="off" class="text-find-element" name="valueRef2" id="valueRef2" type="text">
					    </div></div>
					</tr>
					
					<#-- valueRef3 -->
					<tr>
					  	<td class="label">${uiLabelMap.FormFieldTitle_valueRef3}</td>
					    <td colspan="10">
					    <div class="text-find" id="SearchJobLogLog_valueRef3">
					    <div class="droplist_container"> 
					    	<select name="valueRef3_op" class="selectBox filter-field-selection">
					    		<option value="between">${uiLabelMap.between}</option>
                                <option value="notBetween">${uiLabelMap.not_between}</option>
                                <option value="like">${uiLabelMap.like}</option>
                                <option value="equals">${uiLabelMap.equal}</option>
                                <option value="notEqual">${uiLabelMap.not_equal}</option>
                                <option value="empty">${uiLabelMap.is_empty}</option>
                                <option value="notEmpty">${uiLabelMap.is_not_empty}</option>
                                <option value="contains" selected="selected">${uiLabelMap.contains}</option>
					    	</select>
					    	<input size="30" autocomplete="off" class="text-find-element" name="valueRef3" id="valueRef3" type="text">
					    </div></div>
					</tr>	
					<!--tr>
					    <td class="label">${uiLabelMap.FormFieldTitle_VIEW_SIZE_}</td>
					    <td class="widget-area-style">
					    	<input type="text" class="numericInList" name="viewSize" size="15" id="SearchJobLogLog_VIEW_SIZE_" maxlength="3" value="50"/>
					    </td>
					</tr-->
				</tbody>
			 </table>
			 <br></br>
			 <div align="left">        
	            <a href="#" id="button-ok" class="smallSubmit button-ok" onclick="javascript: SearchFormJobLogLog.submitForm($('searchFormJobLogLog')); return false;">${uiLabelMap.BaseButtonSearch}</a>
	        </div>
	        <br></br><br></br>
		</div>
	</div>
 </form>
 
 <script type="text/javascript">
    SearchFormJobLogLog = {
        submitForm : function(form) {
            if (Object.isElement(form)) {
               
	            var parametersMap = $H(Form.serialize(form, true));
	            ajaxUpdateArea($('JLLMM001_JobLogLog').identify(), "<@ofbizUrl>searchJobLogLog</@ofbizUrl>", parametersMap.toObject());
            }
            
            
        },
        
        hideButtons: function(screenlet) {
	        if(Object.isElement(screenlet)) {
	            var resetButton = screenlet.down("input.management-reset-button");
	            var saveButton = screenlet.down("input.save-button");
	            var deleteButton = screenlet.down("input.management-delete-button");
	            
	            if(Object.isElement(resetButton) ){
	        		resetButton.setStyle({display: "none"});
	        	}
	            if(Object.isElement(saveButton) ) {
	                saveButton.setStyle({display: "none"});
	            }
	            if(Object.isElement(deleteButton)) {
	                deleteButton.setStyle({display: "none"});
	            }
	            
	        }
	    }
        
    }
    SearchFormJobLogLog.hideButtons($('child-management-container-JobLogLog'));
</script>