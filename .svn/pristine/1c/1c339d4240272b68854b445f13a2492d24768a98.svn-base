// Attenzione, la scelta del workEffortTypeId comporta diverse modifiche alla pagina, 
// in quanto alcune droplist dipendano dalle configurazioni del workEffortType.
// Inoltre alcuni campi vengono visualizzati o nascosti in base a dei params presenti nel folder principale (Obiettivo)
WorkEffortViewManagement = {
	
	load: function() {
	    var insertMode = '${insertMode?if_exists}';
	    var form = WorkEffortViewManagement.loadManagementForm();
	    if(form) {
	    	var formName = form.readAttribute('name');
	    	// set whether the form is obiettivo and set WorkEffortViewManagement.isObiettivo
	    	// for manage dates
	    	WorkEffortViewManagement.checkIsObiettivo(form);
		    if(insertMode == 'Y') {
            	// only for insertMode, because only in insertMode the workEffortTypeId change
            	WorkEffortViewManagement.registerFieldsCols(form);
                WorkEffortViewManagement.registerWorkEffortTypeDropList(form);
                WorkEffortViewManagement.registerWETATOWorkEffortIdFromDropList(form);
	        } else if (WorkEffortViewManagement.isObiettivo == false) {
	        	WorkEffortViewManagement.closeLeftMenu();
	    	}

			// orgUnitRoleTypeId can change always
	        var orgUnitRoleTypeId = $(formName + "_orgUnitRoleTypeId");
	        var drop = DropListMgr.getDropList(formName + "_orgUnitRoleTypeId");
	        if (drop) {
	            drop.registerOnChangeListener(WorkEffortViewManagement.handlerOrgUniField.curry(formName), "handlerOrgUniField");
	            drop.registerOnChangeListener(WorkEffortViewManagement.clearOrgUnitIdField.curry(formName), "clearOrgUnitIdField")
	            WorkEffortViewManagement.handlerOrgUniField(formName);
	            WorkEffortViewManagement.handlerOrgUniRoleTypeIdField(formName);
	        }
	        
	        // orgUnitRoleTypeId can change always
	        // WETATOWorkEffortIdFrom, workEffortIdFrom and workEffortIdTo depends from orgUnitId
	        var orgUnitId = $(formName + "_orgUnitId");
	        var dropOrgUnitId = DropListMgr.getDropList(formName + "_orgUnitId");
	        if (dropOrgUnitId) {
	            dropOrgUnitId.registerOnChangeListener(WorkEffortViewManagement.handlerWETATOWorkEffortIdFromField.curry(formName, dropOrgUnitId), "handlerWETATOWorkEffortIdFromField");
	            dropOrgUnitId.registerOnChangeListener(WorkEffortViewManagement.handlerWorkEffortIdFromField.curry(formName, dropOrgUnitId), "handlerWorkEffortIdFromField");
	            dropOrgUnitId.registerOnChangeListener(WorkEffortViewManagement.handlerWorkEffortIdToField.curry(formName, dropOrgUnitId), "handlerWorkEffortIdToField");
	            WorkEffortViewManagement.handlerWETATOWorkEffortIdFromField(formName, orgUnitId);
	            WorkEffortViewManagement.handlerWorkEffortIdFromField(formName, orgUnitId);
	            WorkEffortViewManagement.handlerWorkEffortIdToField(formName, orgUnitId);
	        }
	
	        WorkEffortViewManagement.trimWorkEffortName(form);
	        
	        var rootInqyTree = '${parameters.rootInqyTree?if_exists}';
	        var loadTreeView = '${parameters.loadTreeView?if_exists}';
	        var id = '${parameters.id?if_exists}';
	        var rootTree = '${parameters.rootTree?if_exists}';
	        if (insertMode != 'Y' && rootInqyTree != 'Y' && loadTreeView == 'Y' && (id == null || id == '') && rootTree != 'Y') {
	        	WorkEffortViewManagement.setNextStatus();
	        }
	        if (insertMode != 'Y' && rootTree != 'Y') {
	        	WorkEffortViewManagement.checkMainParams(form);
	        }
        }
	},
	
	closeLeftMenu : function() {
        var mainContainer = $j("div.right-column");
        var leftCol = $j("div#left-bar-container");
        leftCol.hide();
        mainContainer.addClass("right-column-fullopen");
        $j("li.open").addClass('displayNone');
        $j("li.close").removeClass('displayNone');
	},
		
	loadManagementForm : function() {
	    var form = null;
	
     	var myTabs = Control.Tabs.instances[0];
     	var containerSelected = null;
     	if(!myTabs) {
     		containerSelected = $('main-container')
     	} else {
     		containerSelected = $(myTabs.getActiveContainer());
     	}
     	
    	if (containerSelected) {	     	
	     	form = $(containerSelected).down('form.basic-form');
	    }
	    return form;	
	},
	
	checkIsObiettivo : function(form) {
		var isObiettivo = WorkEffortViewManagement.getFieldValue(form, 'isObiettivo');
		WorkEffortViewManagement.isObiettivo = (isObiettivo === 'Y');
	},
	
	registerFieldsCols: function(form) {
		WorkEffortViewManagement.registerSourceRefFieldCols(form);
		WorkEffortViewManagement.registerEtchFieldCols(form);
		WorkEffortViewManagement.registerWetatoFromFieldCols(form);
		WorkEffortViewManagement.registerPurposeFieldCols(form);
		
		if(WorkEffortViewManagement.isObiettivo == false) {
			WorkEffortViewManagement.registerScheduledDatesFieldCols(form);
			WorkEffortViewManagement.registerActualDatesFieldCols(form);
			WorkEffortViewManagement.registerEstimatedDatesFieldCols(form);
		}
	},
	
	registerSourceRefFieldCols: function(form) {
	    var sourceRefField = $(form.down("input[name='sourceReferenceId']"));
		WorkEffortViewManagement.sourceRefFieldLabelCol = WorkEffortViewManagement.getFieldLabelCol(sourceRefField, false);
		WorkEffortViewManagement.sourceRefFieldCol = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.sourceRefFieldLabelCol);	
	},
	
	registerEtchFieldCols: function(form) {
	    var etchField = $(form.down("input[name='etch']"));
		WorkEffortViewManagement.etchFieldLabelCol = WorkEffortViewManagement.getFieldLabelCol(etchField, false);
		WorkEffortViewManagement.etchFieldCol = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.etchFieldLabelCol);		
	},
	
	registerWetatoFromFieldCols: function(form) {
		var formName = form.readAttribute('name');
	    var WETATOWorkEffortIdFromField = $(formName + "_WETATOWorkEffortIdFromShowEtchY");
	    WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchY = WorkEffortViewManagement.getFieldLabelCol(WETATOWorkEffortIdFromField, false);
		WorkEffortViewManagement.wetatoFromFieldColShowEtchY = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchY);
		
		WETATOWorkEffortIdFromField = $(formName + "_WETATOWorkEffortIdFromShowEtchC");
		WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchC = WorkEffortViewManagement.getFieldLabelCol(WETATOWorkEffortIdFromField, false);
		WorkEffortViewManagement.wetatoFromFieldColShowEtchC = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchC);
	},
		
	registerPurposeFieldCols: function(form) {
	    var workEffortPurposeTypeIdField = $(form.down("input[name='workEffortPurposeTypeId']"));
		WorkEffortViewManagement.workEffortPurposeTypeFieldLabelCol = WorkEffortViewManagement.getFieldLabelCol(workEffortPurposeTypeIdField, false);
		WorkEffortViewManagement.workEffortPurposeTypeFieldCol = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.workEffortPurposeTypeFieldLabelCol);		
	},
		
	registerScheduledDatesFieldCols: function(form) {
	    var scheduledStartDateField = $(form.down("input[name='scheduledStartDate']"));
	    var scheduledCompletionDateField = $(form.down("input[name='scheduledCompletionDate']"));
	    
		WorkEffortViewManagement.scheduledStartDateFieldLabelCol = WorkEffortViewManagement.getFieldLabelCol(scheduledStartDateField, false);
		WorkEffortViewManagement.scheduledStartDateFieldCol = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.scheduledStartDateFieldLabelCol);
		WorkEffortViewManagement.scheduledCompletionDateFieldLabelCol = WorkEffortViewManagement.getFieldLabelCol(scheduledCompletionDateField, false);
		WorkEffortViewManagement.scheduledCompletionDateFieldCol = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.scheduledCompletionDateFieldLabelCol);
	},
	
	registerEstimatedDatesFieldCols: function(form) {
		var estimatedStartDateField = $(form.down("input[name='estimatedStartDate']"));
	    WorkEffortViewManagement.estimatedStartDateFieldLabelCol = WorkEffortViewManagement.getFieldLabelCol(estimatedStartDateField, false);
		WorkEffortViewManagement.estimatedStartDateFieldCol = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.estimatedStartDateFieldLabelCol);
	    var estimatedCompletionDateField = $(form.down("input[name='estimatedCompletionDate']"));
	    WorkEffortViewManagement.estimatedCompletionDateFieldLabelCol = WorkEffortViewManagement.getFieldLabelCol(estimatedCompletionDateField, false);
		WorkEffortViewManagement.estimatedCompletionDateFieldCol = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.estimatedCompletionDateFieldLabelCol);
	},
	
	registerActualDatesFieldCols: function(form) {
	    var actualStartDateField = $(form.down("input[name='actualStartDate']"));
	    var actualCompletionDateField = $(form.down("input[name='actualCompletionDate']"));
	    
		WorkEffortViewManagement.actualStartDateFieldLabelCol = WorkEffortViewManagement.getFieldLabelCol(actualStartDateField, false);
		WorkEffortViewManagement.actualStartDateFieldCol = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.actualStartDateFieldLabelCol);
		WorkEffortViewManagement.actualCompletionDateFieldLabelCol = WorkEffortViewManagement.getFieldLabelCol(actualCompletionDateField, false);
		WorkEffortViewManagement.actualCompletionDateFieldCol = WorkEffortViewManagement.getFieldCol(WorkEffortViewManagement.actualCompletionDateFieldLabelCol);
	},
	
	registerWorkEffortTypeDropList: function(form) {
		var formName = form.readAttribute('name');           
        var dropList = DropListMgr.getDropList(formName + '_workEffortTypeId');
	    if (dropList) {
	    	WorkEffortViewManagement.clearFields(form);
	    	WorkEffortViewManagement.handleWetatoFromField(form, 'Y');
	    	WorkEffortViewManagement.handlePurposeTypeField(form);
	    	WorkEffortViewManagement.handleWorkEffortTypeInfo(form);
	    	
			dropList.registerOnChangeListener(WorkEffortViewManagement.clearFields.curry(form), "clearFields");
			dropList.registerOnChangeListener(WorkEffortViewManagement.handleWetatoFromField.curry(form, 'Y'), "handleWetatoFromField");
			dropList.registerOnChangeListener(WorkEffortViewManagement.handlePurposeTypeField.curry(form), "handlePurposeTypeField");
			dropList.registerOnChangeListener(WorkEffortViewManagement.handleWorkEffortTypeInfo.curry(form), "handleWorkEffortTypeInfo");
            
			var dropOrgUniRoleTypeId = DropListMgr.getDropList(formName + "_orgUnitRoleTypeId");
            if (dropOrgUniRoleTypeId) {
                dropList.registerOnChangeListener(WorkEffortViewManagement.handlerOrgUniField.curry(formName), "handlerOrgUniField");
                dropList.registerOnChangeListener(WorkEffortViewManagement.handlerOrgUniRoleTypeIdField.curry(formName), "handlerOrgUniRoleTypeIdField");
            }
            
            var dropOrgUnitId = DropListMgr.getDropList(formName + "_orgUnitId");
            if (dropOrgUnitId) {
                dropList.registerOnChangeListener(WorkEffortViewManagement.handlerWETATOWorkEffortIdFromField.curry(formName, dropOrgUnitId), "handlerWETATOWorkEffortIdFromField");
                dropList.registerOnChangeListener(WorkEffortViewManagement.handlerWorkEffortIdFromField.curry(formName, dropOrgUnitId), "handlerWorkEffortIdFromField");
                dropList.registerOnChangeListener(WorkEffortViewManagement.handlerWorkEffortIdToField.curry(formName, dropOrgUnitId), "handlerWorkEffortIdToField");
            }
	    }
	},
	
	handleSourceAndEtchFields: function(form, showCodeField, showEtchField, showEtch) {
		var wetFrameEnumId = WorkEffortViewManagement.getFieldValue(form, 'wetFrameEnumId');
		var showCode = "Y";
		if (showCodeField) {
			showCode = showCodeField.getValue();
		}
		if (wetFrameEnumId == 'MANUAL' && showCode == 'Y') {
			WorkEffortViewManagement.showSourceFieldRows();
		} else {
			WorkEffortViewManagement.hideSourceFieldRows();
		}
		if (showEtchField == 'Y' && (wetFrameEnumId == 'MANUAL' || wetFrameEnumId == 'PFAAUOET')) {
			WorkEffortViewManagement.showEtchFieldRows();
		} else {
			WorkEffortViewManagement.hideEtchFieldRows();
		}
		WorkEffortViewManagement.handleWetatoFromField(form, showEtch);
	},
	
	handleWetatoFromField: function(form, showEtch) {
		var wetatoAssocTypeId = WorkEffortViewManagement.getFieldValue(form, 'WETATOWorkEffortAssocTypeId');

	    if(wetatoAssocTypeId.empty()) {
	    	// hide All WETATO fields
	    	WorkEffortViewManagement.hideWetatoFromFieldRow(form, 'Y');
	    	WorkEffortViewManagement.hideWetatoFromFieldRow(form, 'C');
	    } else {
	    	// show only the correct field, hide the other
	    	if (showEtch == "Y") {
				WorkEffortViewManagement.showWetatoFromFieldRow(form, 'Y');
				WorkEffortViewManagement.hideWetatoFromFieldRow(form, 'C');
			} else {
				WorkEffortViewManagement.showWetatoFromFieldRow(form, 'C');
				WorkEffortViewManagement.hideWetatoFromFieldRow(form, 'Y');
			}
	    }
	},	
		
	handlePurposeTypeField: function(form) {
		var wetWePurposeTypeIdWe = WorkEffortViewManagement.getFieldValue(form, 'wetWePurposeTypeIdWe');
	    if(wetWePurposeTypeIdWe.empty()) {
	    	WorkEffortViewManagement.hidePurposeTypeFieldRow(form);
	    } else {
	        WorkEffortViewManagement.showPurposeTypeFieldRow(form);
	    }
	},
	
	handlerWorkEffortIdFromField: function(formName, drop) {
	    if(Object.isElement($(formName +"_orgUnitId"))) {
	        var workEffortIdFromFieldList =  $(formName).select(".droplist_field").findAll(function(element) {
	            return element.id.indexOf("workEffortIdFrom") > 0;
	        });
	        for(var i = 0; i < workEffortIdFromFieldList.size() ; i++) {
	            var workEffortIdFromField = workEffortIdFromFieldList[i];
	            var ident = workEffortIdFromField.id;
	            var index = ident.substring(ident.length - 1, ident.length);
	            var workEffortIdFrom = workEffortIdFromField.down("input[name='description_workEffortIdFrom" + index + "']");
	            if(Object.isElement(workEffortIdFrom)) {    
	                var dropworkEffortIdFrom = DropListMgr.getDropList(formName + "_workEffortIdFrom" + index);
	                var orgUnitIdValue = $(formName + "_orgUnitId").down("input[name='orgUnitId']").getValue();
	                var orgUnitRoleTypeIdValue = $(formName + "_orgUnitRoleTypeId").down("input[name='orgUnitRoleTypeId']").getValue();
	                var workEffortTypeIdValue = $(formName + "_workEffortTypeId").down("input[name='workEffortTypeId']").getValue();
	                var workEffortAssocTypeIdValue = $(formName).down("input[name='workEffortAssocTypeId" + index + "']").getValue();
	                var wefromWetoEnumIdValue = $(formName).down("input[name='wefromWetoEnumId" + index + "']").getValue();
                    if (orgUnitIdValue != null && orgUnitIdValue != '') {  
	                
	                   workEffortIdFrom.removeAttribute("readonly");
	                   var organizationId = '${defaultOrganizationPartyId?if_exists}';
	                   
	                   new Ajax.Request("<@ofbizUrl>getFilterPartyRoleWorkEffort</@ofbizUrl>", {
	                       parameters: {
	                           "wefromWetoEnumId": wefromWetoEnumIdValue, 
                               "workEffortTypeId": workEffortTypeIdValue,
	                           "workEffortAssocTypeId": workEffortAssocTypeIdValue,
	                           "orgUnitId": orgUnitIdValue,
	                           "orgUnitRoleTypeId": orgUnitRoleTypeIdValue,
	                           "organizationId": organizationId,
	                           "index": index
	                       },
	                       onSuccess: function(transport) {
	                           var data = transport.responseText.evalJSON(true); 
	                           var orgUnitIdListAssocField = $(formName).down("input[name='orgUnitIdListAssoc" + data.index + "']");
	                           if (orgUnitIdListAssocField) {
	                               orgUnitIdListAssocField.setValue(data.orgUnitIdList);
	                           }
	                        }
	                  });    
	                } else {
	                    workEffortIdFrom.setAttribute("readonly", "readonly");
	                    dropworkEffortIdFrom._editField.clear();
	                    $(formName + "_workEffortIdFrom").down("input[name='workEffortIdFrom" + index + "']").setValue("");           
	                }
	            }
	        }
        }
    },
    
    handlerWorkEffortIdToField: function(formName, drop) {
	    if(Object.isElement($(formName + "_orgUnitId"))) {
            var workEffortIdToFieldList =  $(formName).select(".droplist_field").findAll(function(element) {
                return element.id.indexOf("workEffortIdTo") > 0;
            });
            for(var i = 0; i < workEffortIdToFieldList.size() ; i++) {
                var workEffortIdToField = workEffortIdToFieldList[i];
                var ident = workEffortIdToField.id;
                var index = ident.substring(ident.length -1, ident.length);
                var workEffortIdTo = workEffortIdToField.down("input[name='description_workEffortIdTo" + i + "']");
                if(Object.isElement(workEffortIdTo)) {    
                    var dropworkEffortIdTo = DropListMgr.getDropList(formName + "_workEffortIdTo" + i);
                    var orgUnitIdValue = $(formName + "_orgUnitId").down("input[name='orgUnitId']").getValue();
                    var orgUnitRoleTypeIdValue = $(formName + "_orgUnitRoleTypeId").down("input[name='orgUnitRoleTypeId']").getValue();
                    var workEffortTypeIdValue = $(formName + "_workEffortTypeId").down("input[name='workEffortTypeId']").getValue();
                    var workEffortAssocTypeIdValue = $(formName).down("input[name='workEffortAssocTypeId" + i + "']").getValue();
                    var wefromWetoEnumIdValue = $(formName).down("input[name='wefromWetoEnumId" + index + "']").getValue();
                    if (orgUnitIdValue != null && orgUnitIdValue != '') {  
                    
                       workEffortIdTo.removeAttribute("readonly");
                       var organizationId = '${defaultOrganizationPartyId?if_exists}';
                       
                       new Ajax.Request("<@ofbizUrl>getFilterPartyRoleWorkEffort</@ofbizUrl>", {
                           parameters: {
                               "wefromWetoEnumId": wefromWetoEnumIdValue, 
                               "workEffortTypeId": workEffortTypeIdValue,
                               "workEffortAssocTypeId": workEffortAssocTypeIdValue,
                               "orgUnitId": orgUnitIdValue,
                               "orgUnitRoleTypeId": orgUnitRoleTypeIdValue,
                               "organizationId": organizationId,
                               "index": i
                           },
                           onSuccess: function(transport) {
                               var data = transport.responseText.evalJSON(true); 
                               var orgUnitIdListAssocField = $(formName).down("input[name='orgUnitIdListAssoc" + data.index + "']");
                               if (orgUnitIdListAssocField) {
                                   orgUnitIdListAssocField.setValue(data.orgUnitIdList);
                               }
                            }
                      });    
                    } else {
                        workEffortIdTo.setAttribute("readonly", "readonly");
                        dropworkEffortIdFrom._editField.clear();
                        $(formName + "_workEffortIdTo").down("input[name='workEffortIdTo']").setValue("");           
                    }
                }
            }
        }
    },
    
    handlerWETATOWorkEffortIdFromField: function(formName, drop) {
	    var WETATOWorkEffortIdFromField = $(formName + "_WETATOWorkEffortIdFromShowEtchY");
	    if(!Object.isElement(WETATOWorkEffortIdFromField)) {
	    	WETATOWorkEffortIdFromField = $(formName + "_WETATOWorkEffortIdFromShowEtchC");
	    }
	    if(!Object.isElement(WETATOWorkEffortIdFromField)) {
	    	WETATOWorkEffortIdFromField = $(formName + "_WETATOWorkEffortIdFrom");
	    }
        if(Object.isElement($(formName + "_orgUnitId"))) {
            
            if(Object.isElement(WETATOWorkEffortIdFromField)) {
                var WETATOWorkEffortIdFrom = WETATOWorkEffortIdFromField.down("input[name='description_WETATOWorkEffortIdFrom']");
                var dropWETATOWorkEffortIdFrom = DropListMgr.getDropList(WETATOWorkEffortIdFromField.identify());
                              
                orgUnitIdValue = $(formName + "_orgUnitId").down("input[name='orgUnitId']").getValue();
                orgUnitRoleTypeIdValue = $(formName + "_orgUnitRoleTypeId").down("input[name='orgUnitRoleTypeId']").getValue();
                workEffortTypeIdValue = $(formName + "_workEffortTypeId").down("input[name='workEffortTypeId']").getValue();
                workEffortAssocTypeIdValue = $(formName).down("input[name='WETATOWorkEffortAssocTypeId']").getValue();
                if (orgUnitIdValue != null && orgUnitIdValue != '') {  
                
                   WETATOWorkEffortIdFrom.removeAttribute("readonly");
                   var organizationId = '${defaultOrganizationPartyId?if_exists}';
                   
                   new Ajax.Request("<@ofbizUrl>getFilterPartyRoleWorkEffort</@ofbizUrl>", {
                        parameters: {
                            "wefromWetoEnumId": "WETATO", 
                            "workEffortTypeId": workEffortTypeIdValue,
                            "workEffortAssocTypeId": workEffortAssocTypeIdValue,
                            "orgUnitId": orgUnitIdValue,
                            "orgUnitRoleTypeId": orgUnitRoleTypeIdValue,
                            "organizationId": organizationId
                        },
                        onSuccess: function(transport) {
                            var data = transport.responseText.evalJSON(true); 
    						var orgUnitIdListAssocField = $(formName).down("input[name='orgUnitIdListAssocWETATO']");
                            if (orgUnitIdListAssocField) {
                                 orgUnitIdListAssocField.setValue(data.orgUnitIdList);
                            }
                        }
                  });    
                } else {
                    WETATOWorkEffortIdFrom.setAttribute("readonly", "readonly");
                    dropWETATOWorkEffortIdFrom._editField.clear();
                    $(WETATOWorkEffortIdFromField.identify()).down("input[name='WETATOWorkEffortIdFrom']").setValue("");           
                }
            }
        }
    },
    
    // manage field orgUnitId with value of field orgUnitRoleTypeId
	handlerOrgUniField: function(formName) {
	    if(Object.isElement($(formName + "_orgUnitId"))) {
	        var orgUnitRoleId = $(formName + "_orgUnitId").down("input[name='description_orgUnitId']");
	        var drop2 = DropListMgr.getDropList(formName + "_orgUnitId");
	                      
	        orgUnitRoleTypeIdValue = $(formName + "_orgUnitRoleTypeId").down("input[name='orgUnitRoleTypeId']").getValue();
	        orgUnitIdValue = $(formName + "_orgUnitId").down("input[name='orgUnitId']").getValue();
	        weContextIdValue = $(formName).down("input[name='weContextId']").getValue();
	        
	        if (orgUnitRoleTypeIdValue != null && orgUnitRoleTypeIdValue != '') {  
	        
	           orgUnitRoleId.removeAttribute("readonly");
	           var organizationId = '${defaultOrganizationPartyId?if_exists}';
	           
	           // populate orgUnitIdList with possible list
	           new Ajax.Request("<@ofbizUrl>getFilterPartyRoleOrgUnit</@ofbizUrl>", {
	                parameters: {
	                    "orgUnitRoleTypeId": orgUnitRoleTypeIdValue,
	                    "orgUnitId": orgUnitIdValue,
	                    "weContextId": weContextIdValue,
	                    "organizationId": organizationId
	                },
	                onSuccess: function(transport) {
	                   var data = transport.responseText.evalJSON(true); 
	                   var localData =  $A({});
	                   var localDataArray = data.result;
	                   localDataArray.each(function(localDataElement) {
	                       localData.push($H(localDataElement));
	                   });
	                   drop2._localData = $A(localData);
	                   drop2._editField.clear();
	                   if (orgUnitIdValue != null && orgUnitIdValue != "") {
	                        drop2.setValue(data.orgUnitDesc);
	                   }
	                }
	          });
	        } else {
	            orgUnitRoleId.setAttribute("readonly", "readonly");
	            drop2._editField.clear();
	            $(formName + "_orgUnitId").down("input[name='orgUnitId']").setValue("");           
	        }
	    }
    },
    
    handlerOrgUniRoleTypeIdField: function(formName) {
        var insertMode = '${insertMode?if_exists}';
        if(Object.isElement($(formName + "_orgUnitRoleTypeId")) && insertMode == 'Y') {
            var drop2 = DropListMgr.getDropList(formName + "_orgUnitRoleTypeId");
            var orgUnitRoleTypeDescriptionField = $(formName + "_orgUnitRoleTypeId").down("input[name='description_orgUnitRoleTypeId']");
            var wetOrgUnitRoleTypeIdValue = $(formName).down("input[name='wetOrgUnitRoleTypeId']").getValue();
            var wetOrgUnitRoleTypeIdValue2 = $(formName).down("input[name='wetOrgUnitRoleTypeId2']").getValue();
            var wetOrgUnitRoleTypeIdValue3 = $(formName).down("input[name='wetOrgUnitRoleTypeId3']").getValue();
            var orgUnitRoleTypeIdField = $(formName).down("input[name='orgUnitRoleTypeId']");
            new Ajax.Request("<@ofbizUrl>getRoleTypeList</@ofbizUrl>", {
	            parameters: {
	                "roleTypeId": wetOrgUnitRoleTypeIdValue,
	                "roleTypeId2": wetOrgUnitRoleTypeIdValue2,
	                "roleTypeId3": wetOrgUnitRoleTypeIdValue3
	            },
	            onSuccess: function(transport) {
	                var data = transport.responseText.evalJSON(true); 
	                var localData =  $A({});
	                var localDataArray = data.roleTypeList;
	                localDataArray.each(function(localDataElement) {
	                    localData.push($H(localDataElement));
	                });
	                drop2._localData = $A(localData);
	                var orgUnitRoleTypeIdParam = '${parameters.orgUnitRoleTypeId?if_exists}';
	                if (orgUnitRoleTypeIdParam == null || orgUnitRoleTypeIdParam == '') {
	                    drop2._editField.clear();
	                }
	                if (wetOrgUnitRoleTypeIdValue != null && wetOrgUnitRoleTypeIdValue != '' && (wetOrgUnitRoleTypeIdValue2 == null || wetOrgUnitRoleTypeIdValue2 == '')) {
	                    if (Object.isElement(orgUnitRoleTypeIdField) && Object.isElement(orgUnitRoleTypeDescriptionField)) {
	                        if (orgUnitRoleTypeIdParam == null || orgUnitRoleTypeIdParam == '') {
	                            orgUnitRoleTypeIdField.setValue(data.roleTypeIdOut);
	                            orgUnitRoleTypeDescriptionField.setValue(data.roleTypeDescOut);
	                        }
	                        orgUnitRoleTypeDescriptionField.writeAttribute("readonly", "readonly");
	                    }
	                } else {
	                    if (Object.isElement(orgUnitRoleTypeIdField) && Object.isElement(orgUnitRoleTypeDescriptionField)) {
	                        if (orgUnitRoleTypeIdParam == null || orgUnitRoleTypeIdParam == '') {
	                            orgUnitRoleTypeIdField.setValue("");
	                        }
	                        orgUnitRoleTypeDescriptionField.removeAttribute("readonly");
	                    }
	                }                   
	            }
	        });
        }
    },
    
    clearOrgUnitIdField: function(formName) {
        var orgUnitIdField = $(formName).down("input[name='orgUnitId']");
        if (orgUnitIdField) {
            orgUnitIdField.setValue("");
        }
    },
    
    handleWorkEffortTypeInfo: function(form) {
    	var workEffortTypeIdField = $(form.down("input[name='workEffortTypeId']"));
    	var showCodeField = $(form.down("input[name='showCode']"));
    	var showEtchField = "Y";
    	var showEtch = "Y";
    	var startDate = "";
    	var onlyRefDate = "";
    	var showRoleType = "";
    	if (workEffortTypeIdField) {
    		var workEffortTypeId = workEffortTypeIdField.getValue();
    		var workEffortId = '${parameters.workEffortIdFrom?if_exists}';
    		if (workEffortTypeId && workEffortTypeId != "") {
    			new Ajax.Request("<@ofbizUrl>getWorkEffortTypeInfo</@ofbizUrl>", {
    				parameters: {"workEffortTypeId": workEffortTypeId, "workEffortId": workEffortId},
    				onSuccess: function(response) {
    					var data = response.responseText.evalJSON(true);
    					if (data) {						
    						// manage showEtch
    						if (showCodeField) {
    							showCodeField.setValue(data.showCode);
    						}
    						showEtchField = data.showEtchField;
    						showEtch = data.showEtch;
    						
    						// manage dates
    						startDate = data.startDate;
    						onlyRefDate = data.onlyRefDate;
    						var estimatedStartDateField = $(form.down("input[name='estimatedStartDate']"));
    						WorkEffortViewManagement.handleSourceAndEtchFields(form, showCodeField, showEtchField, showEtch);
    						if (onlyRefDate == "Y") {
    							if (estimatedStartDateField) {
    						        estimatedStartDateField.removeClassName("mandatory");
    							}
    						    WorkEffortViewManagement.hideFieldRow(WorkEffortViewManagement.estimatedStartDateFieldLabelCol, WorkEffortViewManagement.estimatedStartDateFieldCol);
    						    WorkEffortViewManagement.updateCol(WorkEffortViewManagement.estimatedCompletionDateFieldLabelCol, '${uiLabelMap.WorkeffortReferenceDateTitle?if_exists}');
    						} else {
    							if (estimatedStartDateField) {
    						        estimatedStartDateField.addClassName("mandatory");
    							}    						
    						    WorkEffortViewManagement.showFieldRow(WorkEffortViewManagement.estimatedStartDateFieldLabelCol, WorkEffortViewManagement.estimatedStartDateFieldCol);
    						    WorkEffortViewManagement.updateCol(WorkEffortViewManagement.estimatedCompletionDateFieldLabelCol, '${uiLabelMap.WorkeffortEstimatedCompletionDateTitle?if_exists}');
    						}
    						WorkEffortViewManagement.setEstimatedDates(form, startDate, onlyRefDate, data.fromDateFormatted, data.thruDateFormatted);
    						
    						showRoleType = data.showRoleType;
    						var orgUnitRoleTypeIdField = $(form.down("input[name='orgUnitRoleTypeId']"));
	                        var orgUnitRoleTypeIdLabelCol = WorkEffortViewManagement.getFieldLabelCol(orgUnitRoleTypeIdField, false);
		                    var orgUnitRoleTypeIdFieldCol = WorkEffortViewManagement.getFieldCol(orgUnitRoleTypeIdLabelCol);
    						if (showRoleType == "N") {
    						    WorkEffortViewManagement.hideFieldRow(orgUnitRoleTypeIdLabelCol, orgUnitRoleTypeIdFieldCol);  						    
    						} else {
    						    WorkEffortViewManagement.showFieldRow(orgUnitRoleTypeIdLabelCol, orgUnitRoleTypeIdFieldCol);
    						}
    						
    						// manage orgUnitRoleTypeId
    						var orgUnitRoleTypeId = data.orgUnitRoleTypeId;
    						var formName = form.readAttribute('name');
						    var orgUnitRoleTypeDrop = DropListMgr.getDropList(formName + "_orgUnitRoleTypeId");
						    var orgUnitRoleTypeIdParam = '${parameters.orgUnitRoleTypeId?if_exists}';
						    if (orgUnitRoleTypeIdParam == null || orgUnitRoleTypeIdParam == '') {
					        	if (orgUnitRoleTypeId != null && orgUnitRoleTypeId != "") {
					        		orgUnitRoleTypeDrop.setValue(data.orgUnitRoleTypeDesc, data.orgUnitRoleTypeId);
					        	}
					        }
	    					WorkEffortViewManagement.handlerOrgUniField(formName);
	    					WorkEffortViewManagement.handlerOrgUniRoleTypeIdField(formName);
    					}
    				},
    				onFailure: function() {
    					WorkEffortViewManagement.handleSourceAndEtchFields(form, showCodeField, showEtchField, showEtch);
    					WorkEffortViewManagement.setEstimatedDates(form, "", "", "", "");
    				}
    			});
    		}
    	}
    },
    
    setEstimatedDates: function(form, startDate, onlyRefDate, fromDateFormatted, thruDateFormatted) {
    	var estimatedStartDateField = $(form.down("input[name='estimatedStartDate']"));
    	if (estimatedStartDateField) {
    		var startDateParam = '${parameters.estimatedStartDate?if_exists}';
    		if (!startDateParam || startDateParam == '' || startDate == "OPEN") {
    			if (fromDateFormatted && fromDateFormatted != "") {
    				estimatedStartDateField.setValue(fromDateFormatted);
    			} else {
    				estimatedStartDateField.setValue("");
    			}
    		}
    	}
    	
    	var estimatedCompletionDateField = $(form.down("input[name='estimatedCompletionDate']"));
    	if (estimatedCompletionDateField) {
    		var completionDateParam = '${parameters.estimatedCompletionDate?if_exists}';
    		if (!completionDateParam || completionDateParam == '' || onlyRefDate == "Y") {
    			if (thruDateFormatted && thruDateFormatted != "") {
    				estimatedCompletionDateField.setValue(thruDateFormatted);
    			} else {
    			    estimatedCompletionDateField.setValue("");
    			}
    		}
    	}
    },
    
    registerWETATOWorkEffortIdFromDropList: function(form) {			
	    var formName = form.readAttribute('name');           
        var dropList = DropListMgr.getDropList(formName + '_WETATOWorkEffortIdFromShowEtchY');
	    if (dropList) {
	    	dropList.registerOnChangeListener(WorkEffortViewManagement.setSourceReferenceIdByWorkEffortIdFrom.curry(form), "setSourceReferenceIdByWorkEffortIdFrom");
	    }
	    dropList = DropListMgr.getDropList(formName + '_WETATOWorkEffortIdFromShowEtchC');
	    if (dropList) {
	    	dropList.registerOnChangeListener(WorkEffortViewManagement.setSourceReferenceIdByWorkEffortIdFrom.curry(form), "setSourceReferenceIdByWorkEffortIdFrom");
	    }
	},
	
	setSourceReferenceIdByWorkEffortIdFrom: function(form) {
		var sourceRefField = $(form.down("input[name='sourceReferenceId']"));
		var wetFrameEnumId = WorkEffortViewManagement.getFieldValue(form, 'wetFrameEnumId');
		if (wetFrameEnumId != 'MANUAL') {
			if (sourceRefField) {
				sourceRefField.setValue("");
			}
		}
	},
			
	hideSourceFieldRows: function() {		
		WorkEffortViewManagement.hideFieldRow(WorkEffortViewManagement.sourceRefFieldLabelCol, WorkEffortViewManagement.sourceRefFieldCol);	    
	},
	
	showSourceFieldRows: function() {
		WorkEffortViewManagement.showFieldRow(WorkEffortViewManagement.sourceRefFieldLabelCol, WorkEffortViewManagement.sourceRefFieldCol);	    
	},
	
	hideEtchFieldRows: function() {		
		WorkEffortViewManagement.hideFieldRow(WorkEffortViewManagement.etchFieldLabelCol, WorkEffortViewManagement.etchFieldCol);	    
	},
	
	showEtchFieldRows: function() {
		WorkEffortViewManagement.showFieldRow(WorkEffortViewManagement.etchFieldLabelCol, WorkEffortViewManagement.etchFieldCol);		    
	},
	
	hideWetatoFromFieldRow: function(form, showEtch) {
		if (showEtch == "Y") {
	    	WorkEffortViewManagement.hideFieldRow(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchY, WorkEffortViewManagement.wetatoFromFieldColShowEtchY);
	    	WorkEffortViewManagement.updateCol(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchY, '');
		} else {
			WorkEffortViewManagement.hideFieldRow(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchC, WorkEffortViewManagement.wetatoFromFieldColShowEtchC);
			WorkEffortViewManagement.updateCol(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchC, '');
		}
		
		
		var formName = form.readAttribute('name');
	    WETATOWorkEffortIdFromField = $(formName + "_WETATOWorkEffortIdFromShowEtch" + showEtch);
		if(Object.isElement(WETATOWorkEffortIdFromField)) {
            WETATOWorkEffortIdFromField.removeClassName("mandatory");
        	var mandatoryFields = (WETATOWorkEffortIdFromField).select("input.mandatory");
            for(var i = 0; i < mandatoryFields.size() ; i++) {
                mandatoryFields[i].removeClassName("mandatory");
            }
        }
	},	
	
	showWetatoFromFieldRow: function(form, showEtch) {
		var parentRelTypeDesc = WorkEffortViewManagement.getFieldValue(form, 'wetParentRelTypeDesc');
		if (showEtch == "Y") {
			WorkEffortViewManagement.showFieldRow(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchY, WorkEffortViewManagement.wetatoFromFieldColShowEtchY);
	    	WorkEffortViewManagement.updateCol(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchY, parentRelTypeDesc);
		} else {
			WorkEffortViewManagement.showFieldRow(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchC, WorkEffortViewManagement.wetatoFromFieldColShowEtchC);
	    	WorkEffortViewManagement.updateCol(WorkEffortViewManagement.wetatoFromFieldLabelColShowEtchC, parentRelTypeDesc);
		}
		
		var formName = form.readAttribute('name');
	    WETATOWorkEffortIdFromField = $(formName + "_WETATOWorkEffortIdFromShowEtch" + showEtch);
		WETATOWorkEffortIdFromField.addClassName("mandatory");
        if(Object.isElement(WETATOWorkEffortIdFromField)) {
            var mandatoryFields = (WETATOWorkEffortIdFromField).select("input");
            for(var i = 0; i < mandatoryFields.size() ; i++) {
                mandatoryFields[i].addClassName("mandatory");
            }
        }
	},	
	
	hidePurposeTypeFieldRow: function(form) {
		WorkEffortViewManagement.hideFieldRow(WorkEffortViewManagement.workEffortPurposeTypeFieldLabelCol, WorkEffortViewManagement.workEffortPurposeTypeFieldCol);
		var purposeDescField = $(form.down("input[name='description_workEffortPurposeTypeId']"));
	    if (Object.isElement(purposeDescField)) {
	    	purposeDescField.removeClassName("mandatory");
	    }
		
		WorkEffortViewManagement.updateCol(WorkEffortViewManagement.workEffortPurposeTypeFieldLabelCol, '');		
	},	
	
	showPurposeTypeFieldRow: function(form) {
		WorkEffortViewManagement.showFieldRow(WorkEffortViewManagement.workEffortPurposeTypeFieldLabelCol, WorkEffortViewManagement.workEffortPurposeTypeFieldCol);	
		var purposeDescField = $(form.down("input[name='description_workEffortPurposeTypeId']"));
	    if (Object.isElement(purposeDescField)) {
	    	purposeDescField.addClassName("mandatory");
	    }
				
		var wetPurposeEtch = WorkEffortViewManagement.getFieldValue(form, 'wetPurposeEtch');
		if (wetPurposeEtch != "") {
			WorkEffortViewManagement.updateCol(WorkEffortViewManagement.workEffortPurposeTypeFieldLabelCol, wetPurposeEtch);
		} else {
			WorkEffortViewManagement.updateCol(WorkEffortViewManagement.workEffortPurposeTypeFieldLabelCol, '${uiLabelMap.WorkEffortPurposeType?if_exists}');
		}
	},
	
	trimWorkEffortName: function(form) {
		var workEffortNameField = $(form.down("input[name='workEffortName']"));
		if (workEffortNameField) {
            Event.observe(workEffortNameField, 'blur', function(e) {
            	var workEffortName = workEffortNameField.getValue();
            	if (workEffortName) {
            		workEffortNameField.setValue(workEffortName.trim());
            	}           	
            });
		}
		
		var workEffortNameLangField = $(form.down("input[name='workEffortNameLang']"));
		if (workEffortNameLangField) {
            Event.observe(workEffortNameLangField, 'blur', function(e) {
            	var workEffortNameLang = workEffortNameLangField.getValue();
            	if (workEffortNameLang) {
            		workEffortNameLangField.setValue(workEffortNameLang.trim());
            	}           	
            });
		}		
	},
		
	hideFieldRow: function(fieldLabelCol, fieldCol) {
	    var hiddenClassName = 'hidden';	    
	    if(fieldLabelCol) {
	        fieldLabelCol.addClassName(hiddenClassName);
	    }
	    if(fieldCol) {
	        fieldCol.addClassName(hiddenClassName);
	    }		
	},
	
	showFieldRow: function(fieldLabelCol, fieldCol) {
	    var hiddenClassName = 'hidden';	    
	    if(fieldLabelCol) {
	        fieldLabelCol.removeClassName(hiddenClassName);
	    }
	    if(fieldCol) {
	        fieldCol.removeClassName(hiddenClassName);
	    }		
	},
	
	getFieldValue: function(form, fieldName) {
	    var field = $(form.down("input[name='" + fieldName + "']"));
	    if (Object.isElement(field)) {
	    	return field.getValue();
	    }
		return '';
	},
	
	clearFields: function(form)	{
		var formName = form.readAttribute('name');
		WorkEffortViewManagement.clearField(form, 'sourceReferenceId');
		WorkEffortViewManagement.clearField(form, 'etch');
		
		WorkEffortViewManagement.clearField(form, 'WETATOWorkEffortIdFrom');
		WorkEffortViewManagement.clearField(form, 'sourceReferenceId_WETATOWorkEffortIdFrom');
		WorkEffortViewManagement.clearField(form, 'workEffortName_WETATOWorkEffortIdFrom');

		var wetOrgUnitRoleTypeId = WorkEffortViewManagement.getFieldValue(form, 'wetOrgUnitRoleTypeId');
		var wetOrgUnitRoleTypeDesc = WorkEffortViewManagement.getFieldValue(form, 'wetOrgUnitRoleTypeDesc');
		var wrToOuRoleTypeId = WorkEffortViewManagement.getFieldValue(form, 'wrToOuRoleTypeId');
		var wrToOuRoleTypeDesc = WorkEffortViewManagement.getFieldValue(form, 'wrToOuRoleTypeDesc');
		var dropList = DropListMgr.getDropList(formName + "_orgUnitRoleTypeId");
		var orgUnitRoleTypeIdParam = '${parameters.orgUnitRoleTypeId?if_exists}';
		if (orgUnitRoleTypeIdParam == null || orgUnitRoleTypeIdParam == '') {
        	if(dropList && wetOrgUnitRoleTypeDesc) {
        		dropList.setValue(wetOrgUnitRoleTypeDesc, wetOrgUnitRoleTypeId);
        		dropList._editField.setAttribute("readonly", "readonly");
        		dropList._editField.addClassName("readonly");
        	} else {
        		if (wrToOuRoleTypeId && wrToOuRoleTypeDesc) {
        			if (dropList) {
        				dropList.setValue(wrToOuRoleTypeDesc, wrToOuRoleTypeId);
        			}        		
        		} else {
        			if (dropList) {
        				dropList._editField.clear();
        				dropList._codeField.clear();
        			}
        		}
        		if (dropList) {
        			dropList._editField.removeAttribute("readonly");
        			dropList._editField.removeClassName("readonly");
        		}
        	}
        }
	},	
	
	clearField: function(form, fieldName) {
	    var field = $(form.down("input[name='" + fieldName + "']"));
	    if (Object.isElement(field)) {
	    	field.clear();
	    }
	},
	
    getFieldLabelCol: function(formField, isLookUpField) {
		if(formField) {
			var formFieldTr = null;
			if(isLookUpField == true) {
				var lookupFieldDiv = formField.up('div.lookup_field');
				if(lookupFieldDiv) {
					formFieldTr = lookupFieldDiv.up('tr');
				}
			} else {
				formFieldTr = formField.up('tr');
			}
			if(formFieldTr) {
				return formFieldTr.down('td');
			}
		}		
	    return null;
	},
	
	getFieldCol: function(fieldLabelCol) {
		if(fieldLabelCol) {
	    	return fieldLabelCol.next('td');
	    }
		return null;
	},	
	
	updateCol: function(col, content) {
		if(col) {
			col.update(content);
		}
	},
	
	setNextStatus: function() {
		var modulo = "";
		var form = "";
		var screenName = "";
		
	    <#if parameters.weContextId?if_exists == "CTX_BS">
	    	modulo = "stratperf";
	    	form = "StratPerfRootViewForms";
	    	screenName = "StratPerfScreens.xml";
	    <#elseif  parameters.weContextId?if_exists == "CTX_OR">
	    	modulo = "orgperf";
	    	form = "OrgPerfRootViewForms";
	    	screenName = "OrgPerfScreens.xml";
	    <#elseif  parameters.weContextId?if_exists == "CTX_CO">
	    	modulo = "corperf";
	    	form = "CorPerfRootViewForms";
	    	screenName = "CorPerfScreens.xml";
	    <#elseif  parameters.weContextId?if_exists == "CTX_PR">
	    	modulo = "procperf";
	    	form = "ProcPerfRootViewForms";
	    	screenName = "ProcPerfScreens.xml";
	    <#elseif  parameters.weContextId?if_exists == "CTX_CG">
	    	modulo = "cdgperf";
	    	form = "CdgPerfRootViewForms";
	    	screenName = "CdgPerfScreens.xml";
	    <#elseif  parameters.weContextId?if_exists == "CTX_TR">
	    	modulo = "trasperf";
	    	form = "TrasPerfRootViewForms";
	    	screenName = "TrasPerfScreens.xml";
	    <#elseif  parameters.weContextId?if_exists == "CTX_RE">
	    	modulo = "rendperf";
	    	form = "RendPerfRootViewForms";
	    	screenName = "RendPerfScreens.xml";
	    <#elseif  parameters.weContextId?if_exists == "CTX_GD">
	    	modulo = "gdprperf";
	    	form = "GdprPerfRootViewForms";
	    	screenName = "GdprPerfScreens.xml";
	    <#elseif  parameters.weContextId?if_exists == "CTX_PA">
	    	modulo = "partperf";
	    	form = "PartPerfRootViewForms";
	    	screenName = "PartPerfScreens.xml";
	    <#elseif  parameters.weContextId?if_exists == "CTX_DI">
	    	modulo = "dirigperf";
	    	form = "DirigPerfRootViewForms";
	    	screenName = "DirigPerfScreens.xml";	    	
	    <#else>
	        modulo = "emplperf";
	        form = "EmplPerfRootViewForms";
	        screenName = "EmplPerfScreens.xml";
	    </#if>
	    
		new Ajax.Request("<@ofbizUrl>checkWorkEffortTypeStatus</@ofbizUrl>", {
		    parameters: {"workEffortId": '${parameters.workEffortId?if_exists}'},
		    onSuccess: function(response) {
			    var data = response.responseText.evalJSON(true);
			    if (data) {
				    if (data.statusId && data.statusId != '') {
				        var externalLoginKey = '${requestAttributes.externalLoginKey?if_exists}';
				        var rootInqyTree = '${parameters.rootInqyTree?if_exists}';
				        var specialized = '${parameters.specialized?if_exists}';
				        var workEffortId = '${parameters.workEffortId?if_exists}';
				        var newForm = new Element('form', {id : 'changeStatusForm', name : 'changeStatusForm'});
				        newForm.writeAttribute('action', '<@ofbizUrl>elaborateFormForUpdateAjax</@ofbizUrl>');
				        			        
			            newForm.insert(new Element('input', {name : 'workEffortId', type : 'hidden', value : '${parameters.workEffortId?if_exists}'}));
			            newForm.insert(new Element('input', {name : 'entityName', type : 'hidden', value : 'WorkEffortStatus'}));
			            newForm.insert(new Element('input', {name : 'crudService', type : 'hidden', value : 'crudServiceDefaultOrchestration_WorkEffortRootStatus'}));
			            newForm.insert(new Element('input', {name : 'operation', type : 'hidden', value : 'CREATE'}));
			            newForm.insert(new Element('input', {name : 'saveView', type : 'hidden', value : 'N'}));
			            newForm.insert(new Element('input', {name : 'ignoreAuxiliaryParameters', type : 'hidden', value : 'Y'}));
			            <#assign defaultDateTime = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()>
			            newForm.insert(new Element('input', {name : 'statusDatetime', type : 'hidden', value : '${defaultDateTime}'}));
			            newForm.insert(new Element('input', {name : 'statusId', type : 'hidden', value : data.statusId}));
			            newForm.insert(new Element('input', {name : 'reason', type : 'text', id: 'pippo_reason', value : ''}));
			            newForm.insert(new Element('input', {name : 'messageContext', type : 'hidden', value : 'BaseMessageSaveData'}));
				        			        
				        document.body.insert(newForm);
				        ajaxSubmitFormUpdateAreas('changeStatusForm','','common-container,<@ofbizUrl>managementContainerOnly</@ofbizUrl>,externalLoginKey=' + externalLoginKey + '&entityName=WorkEffortView&rootInqyTree=' +rootInqyTree +'&specialized=' + specialized + '&rootTree=Y&loadTreeView=Y&workEffortIdRoot=' + workEffortId + '&workEffortId=' + workEffortId + '&successCode=management&saveView=N&searchFormLocation=component://' + modulo + '/widget/forms/' + form +'.xml&searchFormResultLocation=component://' + modulo +'/widget/forms/' + form +'.xml&advancedSearchFormLocation=component://' + modulo +'/widget/forms/' + form + '.xml&searchFormScreenName=WorkEffortRootViewSearchFormScreen&searchFormScreenLocation=component://' + modulo + '/widget/screens/' + screenName + '&searchResultContextFormName=WorkEffortRootViewSearchResultContextForm&searchResultContextFormLocation=component://' + modulo + '/widget/forms/' + form + '.xml',
				            {onComplete: function(transport) {UpdateAreaResponder._updateElement(transport, 'common-container,<@ofbizUrl>managementContainerOnly</@ofbizUrl>,externalLoginKey=' + externalLoginKey + '&entityName=WorkEffortView&rootInqyTree=' +rootInqyTree +'&specialized=' + specialized + '&rootTree=Y&loadTreeView=Y&workEffortIdRoot=' + workEffortId + '&workEffortId=' + workEffortId + '&successCode=management&saveView=N&searchFormLocation=component://' + modulo + '/widget/forms/' + form +'.xml&searchFormResultLocation=component://' + modulo +'/widget/forms/' + form +'.xml&advancedSearchFormLocation=component://' + modulo +'/widget/forms/' + form + '.xml&searchFormScreenName=WorkEffortRootViewSearchFormScreen&searchFormScreenLocation=component://' + modulo + '/widget/screens/' + screenName + '&searchResultContextFormName=WorkEffortRootViewSearchResultContextForm&searchResultContextFormLocation=component://' + modulo + '/widget/forms/' + form + '.xml');}});
				        
				        newForm.remove();
				    }
			    }
		    },
		    onFailure: function() {
		    }
	    });
	},
	
	checkMainParams: function(form) {
	    var workEffortTypeId = '${parameters.workEffortTypeId?if_exists}';
	    if (! workEffortTypeId || workEffortTypeId == '') {
	        workEffortTypeId = '${workEffortTypeId?if_exists}';
	    }
		new Ajax.Request("<@ofbizUrl>getWefldMainParams</@ofbizUrl>", {
		    parameters: {"workEffortTypeId": workEffortTypeId},
		    onSuccess: function(response) {
			    var data = response.responseText.evalJSON(true);
			    if (data) {
                    if (data.hideTreeView == "Y") {
                        TreeviewMgr._narrow = "Y";
                        $j("div.treeview-header-toolbar-hide").click();
                    }
                    WorkEffortViewManagement.manageResponsibleField(form, data.hideResponsible);
			    }
		    },
		    onFailure: function() {
		    }
	    });	
	},
	
	manageResponsibleField: function(form, hideResponsible) {
	    var responsibleField = $(form.down("input[name='responsible']"));
	    if (responsibleField) {
	        var trResponsibleField = responsibleField.up('tr');
	        if (trResponsibleField) {
	            if (hideResponsible == "Y") {
	                trResponsibleField.hide();
	            } else {
	                trResponsibleField.show();
	            }
	        }
	    }
	}
}

WorkEffortViewManagement.load();	
