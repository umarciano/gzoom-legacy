WorkEffortRootView = {
    load: function(newContentToExplore, withoutResponder) {
        var form = Object.isElement(newContentToExplore) ? (newContentToExplore.tagName == 'FORM' ? $(newContentToExplore) : newContentToExplore.down('form')) : $('WorkEffortRootViewCreateFromTemplateManagementForm');
        if (form) {
	        var divDropListEvalPartyIdField = form.down("div#WorkEffortRootViewCreateFromTemplateManagementForm_evalPartyId");
            if(divDropListEvalPartyIdField) {
            	var dropList = DropListMgr.getDropList(divDropListEvalPartyIdField.identify());
                if (dropList) {
                	dropList.registerOnChangeListener(WorkEffortRootView.setAllParameters.curry(form), 'WorkEffortRootView_setAllParameters');
                }
            }
	    }    
    },

    setAllParameters : function(form) {
    	var paTemplateIdField = $(form.down("input[name=paTemplateId]"));
    	var templateNameField = $(form.down("input[name=templateName]"));    	    	
        var divDropListTemplateField = form.down("div#WorkEffortRootViewCreateFromTemplateManagementForm_templateId");
        if(divDropListTemplateField && Object.isElement(paTemplateIdField) && Object.isElement(templateNameField)) {
        	var dropListTemplate = DropListMgr.getDropList(divDropListTemplateField.identify());
            if (dropListTemplate) {	
            	dropListTemplate.setValue(templateNameField.getValue(), paTemplateIdField.getValue());
            }
        }
    
    	var descriptionField = $(form.down("input[name=description]"));
    	var evalPartyNameField = $(form.down("input#WorkEffortRootViewCreateFromTemplateManagementForm_evalPartyId_edit_field"));
    	if (Object.isElement(descriptionField) && Object.isElement(evalPartyNameField) && Object.isElement(templateNameField)) {
    		descriptionField.setValue(evalPartyNameField.getValue() + ' - ' + templateNameField.getValue());   		
    	}
    	
    	var paTemplateId = "";
    	if (Object.isElement(paTemplateIdField)) {
    		paTemplateId = paTemplateIdField.getValue();
    	}
    	
    	var evalPartyIdField = $(form.down("input[name=evalPartyId]"));
    	var divOrgUnitIdField = form.down("div#WorkEffortRootViewCreateFromTemplateManagementForm_orgUnitId");
    	var evalPartyId = "";
    	if (Object.isElement(evalPartyIdField)) {
    		evalPartyId = evalPartyIdField.getValue();
    	}
    	var uoRoleTypeIdField = $(form.down("input[name=uoRoleTypeId]"));
    	
    	var tempEstimatedStartDateField = $(form.down("input[name=tempEstimatedStartDate]")); 
    	var tempEstimatedCompletionDateField = $(form.down("input[name=tempEstimatedCompletionDate]")); 
		new Ajax.Request("<@ofbizUrl>getCreateFromTemplateInfo</@ofbizUrl>", {
			parameters: {"workEffortId": paTemplateId, "evalPartyId": evalPartyIdField.getValue()},
			onSuccess: function(response) {
				var data = response.responseText.evalJSON(true);
				if (data) {
					if (data.estimatedStartDateFormatted) {
						tempEstimatedStartDateField.setValue(data.estimatedStartDateFormatted);
					} else {
						tempEstimatedStartDateField.setValue('');
					}
					if (data.estimatedCompletionDateFormatted) {
						tempEstimatedCompletionDateField.setValue(data.estimatedCompletionDateFormatted);
					} else {
						tempEstimatedCompletionDateField.setValue('');
					}
					
		        	var dropListOrgUnitId = DropListMgr.getDropList(divOrgUnitIdField.identify());
		            if (dropListOrgUnitId) {
		            	var dataPartyId = "";
		            	var dataPartyName = "";
		            	var dataRoleDescription = "";
		            	var dataRoleTypeId = "";
		            	var dataParentRoleCode = "";
		            	if (data.partyId) {
		            		dataPartyId = data.partyId;
		            	}
		            	if (data.partyName) {
		            		dataPartyName = data.partyName;
		            	}
		            	if (data.roleDescription) {
		            		dataRoleDescription = data.roleDescription;
		            	}
		            	if (data.roleTypeId) {
		            		dataRoleTypeId = data.roleTypeId;
		            	}
		            	if (data.parentRoleCode) {
		            		dataParentRoleCode = data.parentRoleCode;
		            	}		            	
		            	dropListOrgUnitId.setValue(dataParentRoleCode  + " - " + dataPartyName + " - " + dataRoleDescription, dataPartyId);
		            	if (Object.isElement(uoRoleTypeIdField)) {
		            	    uoRoleTypeIdField.setValue(dataRoleTypeId);
		            	}
		            }
				}
			}
		});
    }   
}

WorkEffortRootView.load();