WorkEffortMeasureManagementExtension = {
	/**
	* Load form
	**/
    load: function(newContentToExplore, withoutResponder) {
        var formToExplore = Object.isElement(newContentToExplore) ? newContentToExplore.up('form') : null;
        if(!formToExplore) {
            var formsToExplore = $$('form');
            formsToExplore.each(function(form) {
                if ((form.identify() == 'WEMMF001' + '${accountTypeEnumId?if_exists}_${contentIdInd?if_exists}' + '_WorkEffortMeasure')
                || (form.identify() == 'WEMMF003_WorkEffortMeasureAndPurposeAccountRes')
                || (form.identify() == 'WEMMF002_WorkEffortMeasureAndPurposeAccountInd')) {
            		WorkEffortMeasureManagementExtension.loadForm(form);
                }
            });
        } else {
            WorkEffortMeasureManagementExtension.loadForm(formToExplore);
        }
        
        <#--if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(WorkEffortMeasureManagementExtension.responder, 'WorkEffortMeasureManagementExtension');
        }-->
        
    },

	<#--/**
	* Responder functions
	**/
    responder : {
                onLoad : function(newContent) {
                    WorkEffortMeasureManagementExtension.load(newContent, true);
                },
                unLoad : function() {
                    //TODO Prevedere eventuali deregistrazioni per liberare memoria
                    return typeof 'WorkEffortMeasureManagementExtension' === 'undefined';
                }
    },-->

	/**
	* Carica event observers 
	**/
    loadForm : function(form) {
        if (form) {
            var formName = form.readAttribute('name');
            // in ascolto evento onchange lookup Obiettivo
            var targLookup = form.down('div#' + formName + '_workEffortId');
            if(targLookup) {
                var lookup = LookupMgr.getLookup(targLookup.identify());
                if (lookup) {
                    lookup.registerOnSetInputFieldValue(WorkEffortMeasureManagementExtension.targetLookupHandler.curry(form, targLookup), 'WorkEffortMeasureManagementExtension_targetLookupHandler');
                    var operationField = form.down("input[name='operation']");
                    if (Object.isElement(operationField) && operationField.readAttribute('value') === 'CREATE') {
                    	WorkEffortMeasureManagementExtension.targetLookupHandler(form, lookup);
                    }
                }
            }
            
            // in ascolto evento onchange lookup Risorsa
            var accountLookup = form.down('div#' + formName + '_glAccountId');
            if(accountLookup) {
                var lookup = LookupMgr.getLookup(accountLookup.identify());
                if (lookup) {
                    lookup.registerOnSetInputFieldValue(WorkEffortMeasureManagementExtension.accountLookupHandler.curry(form, accountLookup), 'WorkEffortMeasureManagementExtension_accountLookupHandler');
                }
            }
            
            // in ascolto evento onchange droplist Indicatore
            var dropListId = formName + "_glAccountId";
            var accountDropList = DropListMgr.getDropList(dropListId);
            if(accountDropList) {
                accountDropList.registerOnChangeListener(WorkEffortMeasureManagementExtension.accountDropListHandler.curry(form, accountDropList), 'WorkEffortMeasureManagementExtension_accountLookupHandler');
            }        
        }
    },
    
    /**
    * Handler evento onchange lookup obiettivo
    **/
    targetLookupHandler : function(form, lookup) {
    	var fromDate = $(form.down("input#estimatedStartDate_workEffortId"));
        var thruDate = $(form.down("input#estimatedCompletionDate_workEffortId"));
        var periodTypeId = $(form.down("input#weTypePeriodId_workEffortId"));
        var periodDesc = $(form.down("input#weTypePeriodDesc_workEffortId"));
        
        var fromDateValue = Object.isElement(fromDate) ? fromDate.value : "";
        var thruDateValue = Object.isElement(thruDate) ? thruDate.value : "";
        var periodTypeIdValue = Object.isElement(periodTypeId) ? periodTypeId.readAttribute("value") : "";
        var periodDescValue = Object.isElement(periodDesc) ? periodDesc.readAttribute("value") : "";
        
        if (fromDateValue && !fromDateValue.empty()) {
        	var value = fromDate.value;
        	date = new Date(Date.getDateFromFormat(fromDateValue,"yyyy-MM-dd HH:mm:ss.S"));
        	fromDateValue = date.strftime(Date.DATE_FORMAT)
        	var target = $(form).down("input#fromDate");
        	if (target) {
        		target.value = fromDateValue;
        	}
        } 
        
           
        if (thruDateValue && !thruDateValue.empty()) {
        	var value = thruDate.value;
        	date = new Date(Date.getDateFromFormat(thruDateValue,"yyyy-MM-dd HH:mm:ss.S"));
        	thruDateValue = date.strftime(Date.DATE_FORMAT)
        	var target = $(form).down("input#thruDate");
        	if (target) {
        		target.value = thruDateValue;
        	}
        } 
        
        if (periodTypeIdValue && !periodTypeIdValue.empty() && periodDescValue && !periodDescValue.empty()) {
        	var formName = form.readAttribute("name");
         	var target = DropListMgr.getDropList(formName + "_periodTypeId", 0);
         	if (target) {
         		// se nella droplist è presente il valore della lookup glAccount lasciare quello
         		var resp = WorkEffortMeasureManagementExtension.setDroplistValueAndDescr(form, "periodTypeId", "periodTypeDescription", "glAccountId", formName + "_periodTypeId");
                if(!resp){
                	target.setValue(periodDescValue, periodTypeIdValue);
                }
         	}
        } 
    },
    
    /**
    * Handler evento onchange lookup account
    **/
    accountLookupHandler : function(form, lookup) {
       //var txt = $(lookup.down("input[name='accountName_glAccountId']"));
    	var txt = $(lookup.down("input[name='uomDescr_glAccountId']"));
        if (Object.isElement(form) && Object.isElement(txt)) {
        	var target = $(form).down("input[name='uomDescr']");
        	if (Object.isElement(target)) {
        		target.value = txt.value;
        	}
        }
        
        
        if (Object.isElement(form)) {
            var inputEnumId = $(lookup.down("input[name='inputEnumId_glAccountId']"));
            if (Object.isElement(inputEnumId)) {
                if("INDICATOR" == "${accountTypeEnumId?if_exists}") {
                	var weOtherGoalEnumId = form.down("select[name='weOtherGoalEnumId']");
                	if(Object.isElement(weOtherGoalEnumId)) {
                		if("ACCINP_OBJ" != inputEnumId.getValue()) {
                			weOtherGoalEnumId.setValue("WEMOMG_NONE");
                		}
                		else {
                			weOtherGoalEnumId.setValue("WEMOMG_WEFF");
                		}
                	}
                	
                	// bug 4901 settare dalla lookup di glAccount la lookup productId 
                	var INDproductId = $(lookup.down("input[name='INDProductId_glAccountId']")).getValue();
                	var INDInternalName = $(lookup.down("input[name='INDInternalName_glAccountId']")).getValue();
                	var INDProductMainCode = $(lookup.down("input[name='INDProductMainCode_glAccountId']")).getValue();
                	
                	var formName = form.readAttribute('name');
                	var productLookup = form.down('div#' + formName + '_productId');
                	
                	if(Object.isElement(productLookup)) {  
                	
                		$(productLookup.down("input[name='productMainCode_productId']")).setValue(INDProductMainCode);
                		$(productLookup.down("input[name='productId']")).setValue(INDproductId);
                		$(productLookup.down("input[name='internalName_productId']")).setValue(INDInternalName);
                	}               	
                	
                }
                
                var uomDescr = form.down("input[name='uomDescr']");
                if(Object.isElement(uomDescr)) {
                    var detailEnumId = $(lookup.down("input[name='detailEnumId_glAccountId']"));
                      if (Object.isElement(detailEnumId)) {
                          if("ACCINP_UO" === inputEnumId.getValue() && "ACCDET_NULL" === detailEnumId.getValue() ) {
                              uomDescr.setAttribute("readonly", "readonly");
                              uomDescr.setValue('');
                              uomDescr.removeAttribute("value");
                          } else {
                              uomDescr.removeAttribute("readonly");
                          }
                      }
                }
            }
            
            //Bug 4591
            var weMeasureTypeEnumIdField = $(lookup.down("input[name='weMeasureTypeEnumId_glAccountId']"));
            var kpiScoreWeightField = form.down("input[name='kpiScoreWeight']");
            if (Object.isElement(weMeasureTypeEnumIdField) && Object.isElement(kpiScoreWeightField)) {
                if (weMeasureTypeEnumIdField.getValue() != "WEMT_PERF") {
                    kpiScoreWeightField.setValue("0");
                }
                else {
                    kpiScoreWeightField.setValue("100");
                }
            } 
            
        }
        var formName = $(form).readAttribute("name");
    	//inizializza uomRangeId dalla lookup del glAccount
        var dropListId = formName + "_uomRangeId";
        WorkEffortMeasureManagementExtension.setDroplistValueAndDescr(form, "uomRangeId", "uomRangeDescription", "glAccountId", dropListId);
        //inizializza uomRangeId dalla lookup del glAccount
		dropListId = formName + "_periodTypeId";
		var resp = WorkEffortMeasureManagementExtension.setDroplistValueAndDescr(form, "periodTypeId", "periodTypeDescription", "glAccountId", dropListId);
        if(!resp){
        	//weTypePeriodId_workEffortId
        	WorkEffortMeasureManagementExtension.setDroplistValueAndDescr(form, "weTypePeriodId", "weTypePeriodDesc", "workEffortId", dropListId);
        }
    },
    
    /**
    * Handler evento onchange droplist account
    **/
    accountDropListHandler : function(form, droplist) {
        if (Object.isElement(form)) {
            var formName = $(form).readAttribute("name");
            var inputEnumId = $(form.down("input[name='inputEnumId']"));
            if (Object.isElement(inputEnumId)) {
                if("INDICATOR" == "${accountTypeEnumId?if_exists}") {
                	var weOtherGoalEnumId = form.down("select[name='weOtherGoalEnumId']");
                	if(Object.isElement(weOtherGoalEnumId)) {
                		if("ACCINP_OBJ" != inputEnumId.getValue()) {
                			weOtherGoalEnumId.setValue("WEMOMG_NONE");
                		}
                		else {
                			weOtherGoalEnumId.setValue("WEMOMG_WEFF");
                		}
                	}
                	
                	// bug 4901 settare dalla droplist di glAccount la lookup productId 
                	var productLookup = form.down('div#' + formName + '_productId');
                	WorkEffortMeasureManagementExtension.setLookupValueAndDescrFromDroplist(formName, productLookup, ["indProductId", "indInternalName", "indProductMainCode"], ["productId", "internalName_productId", "productMainCode_productId"]);
                }
                
                var uomDescr = form.down("input[name='uomDescr']");
                if(Object.isElement(uomDescr)) {
                    var detailEnumId = $(form.down("input[name='detailEnumId']"));
                    if (Object.isElement(detailEnumId)) {
                        if("ACCINP_UO" === inputEnumId.getValue() && "ACCDET_NULL" === detailEnumId.getValue() ) {
                            uomDescr.setAttribute("readonly", "readonly");
                            uomDescr.setValue('');
                            uomDescr.removeAttribute("value");
                        } else {
                            uomDescr.removeAttribute("readonly");
                        }
                    }
                }
            }
            
            //Bug 4591
            var weMeasureTypeEnumIdField = $(form.down("input[name='weMeasureTypeEnumId']"));
            var kpiScoreWeightField = form.down("input[name='kpiScoreWeight']");
            if (Object.isElement(weMeasureTypeEnumIdField) && Object.isElement(kpiScoreWeightField)) {
                if (weMeasureTypeEnumIdField.getValue() != "WEMT_PERF") {
                    kpiScoreWeightField.setValue("0");
                }
                else {
                    kpiScoreWeightField.setValue("100");
                }
            }
            //inizializza uomRangeId dalla droplist del glAccount
            var dropListId = formName + "_uomRangeId";
            WorkEffortMeasureManagementExtension.setDroplistValueAndDescrFromDroplist(formName, "uomRangeId", "uomRangeDescription", dropListId);
            //inizializza periodTypeId dalla droplist del glAccount
            dropListId = formName + "_periodTypeId";
            WorkEffortMeasureManagementExtension.setDroplistValueAndDescrFromDroplist(formName, "periodTypeId", "periodTypeDescription", dropListId);
        }
    },
    
    setDroplistValueAndDescr : function(form, idName, descName, lookupId, dropListId){
    	var resp;
    	
    	var formName = $(form).readAttribute("name");
    	var targLookup = form.down('div#' + formName + '_' + lookupId);
	    if(targLookup) {
		    var lookup = LookupMgr.getLookup(targLookup.identify());
		    if (lookup) {
				targLookup = $(targLookup);
			   	var idLookupValue = $(targLookup.down("input#" + idName + "_" + lookupId));
			   	var descLookupValue = $(targLookup.down("input#" + descName + "_" + lookupId));
			    if(idLookupValue && descLookupValue){
			   		var idValue = idLookupValue.getValue();
			   		var descValue = descLookupValue.getValue();
			   		if(idValue && descValue){
			    		var dropList = DropListMgr.getDropList(dropListId);
			            if(dropList) {
			            	dropList.setValue(descValue, idValue);
			            	resp = true;
						}
			        }
			   	}
		    }
	    }
        return resp;
    },
    
    setDroplistValueAndDescrFromDroplist : function(formName, idName, descName, dropListId){
        // WorkEffortMeasureManagementExtensionFormAdmin_glAccountId_autoCompleterOptions
        var elements = $$("#" + formName + "_glAccountId_autoCompleterOptions");
        if(Object.isArray(elements)) {
            var element = elements[0];
            var li = element.down("li.selected");
            var children = $A($(li).children);
            var idEl = children.find(function(el) {
                return el.innerHTML.indexOf(idName) >= 0;
            }.bind(this));
            var descriptionEl = children.find(function(el) {
                return el.innerHTML.indexOf(descName) >= 0;
            }.bind(this));
            var idArray = idEl.innerHTML.split("_:_");
            var descriptionArray = descriptionEl.innerHTML.split("_:_");
            var dropList = DropListMgr.getDropList(dropListId);
            if(dropList) {
                dropList.setValue(descriptionArray[1], idArray[1]);
            }
        }
    },
    
    setLookupValueAndDescrFromDroplist : function(formName, lookup, fieldNameArray, fieldNameLookupArray){
        // WorkEffortMeasureManagementExtensionFormAdmin_glAccountId_autoCompleterOptions
        var elements = $$("#" + formName + "_glAccountId_autoCompleterOptions");
        if(Object.isArray(elements)) {
            var element = elements[0];
            var li = element.down("li.selected");
            var children = $A($(li).children);
            fieldNameArray.each(function(name, index) {
                var nameEl = children.find(function(el) {
                    return el.innerHTML.indexOf(name) >= 0;
                }.bind(this));
                var nameArray = nameEl.innerHTML.split("_:_");
                if(Object.isElement(lookup) && Object.isElement($(lookup.down("input[name='" + fieldNameLookupArray[index] + "']")))) {  
                    $(lookup.down("input[name='" + fieldNameLookupArray[index] + "']")).setValue(nameArray[1]);
                }
            })
        }
    }
    
}

WorkEffortMeasureManagementExtension.load();
