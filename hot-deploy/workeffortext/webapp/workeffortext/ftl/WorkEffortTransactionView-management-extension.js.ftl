
WorkEffortTransactionViewManagement = {
	// TODO parameters.justRegisters qui e' utilizzato?
	// rimosso ascolto evento onchange lookup Obiettivo, e lasciato sulla droplist, che forse non esiste piu
    // TODO formToExplore potrebbe essere una form qualunque, e' da sistemare
    /**
	* Load form
	**/
    load: function(newContentToExplore, withoutResponder) {
        // console.log("WorkEffortTransactionViewManagement : load " + newContentToExplore);
        // console.log("WorkEffortTransactionViewManagement : parameters.justRegisters ${parameters.justRegisters?if_exists}")
        var formToExplore = Object.isElement(newContentToExplore) ? newContentToExplore.up("form") : null;
        if(!Object.isElement(formToExplore)) {
        	formToExplore = $$("form#WETVMF001${accountTypeEnumId?if_exists}_WorkEffortTransactionViewManagementForm")[0];
        }
        if (Object.isElement(formToExplore)) {
        	WorkEffortTransactionViewManagement.loadForm(formToExplore);
        } 
        
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(WorkEffortTransactionViewManagement.responder, "WorkEffortTransactionViewManagement");
        }
        
    },

	/**
	* Responder functions
	**/
    responder : {
                onLoad : function(newContent) {
                    WorkEffortTransactionViewManagement.load(newContent, true);
                },
                unLoad : function() {
                    //TODO Prevedere eventuali deregistrazioni per liberare memoria
                    return typeof "WorkEffortTransactionViewManagement" === "undefined";
                }
    },

	/**
	* Carica event observers 
	**/
    loadForm : function(form) {
        // console.log("WorkEffortTransactionViewManagement : loadForm ", form);
        if (form) {
            var formName = form.readAttribute("name");
            // rimosso ascolto evento onchange lookup Obiettivo, e lasciato sulla droplist, che forse non esiste piu
            var dropListId = formName + "_weTransMeasureId";
			var measureDropList = DropListMgr.getDropList(dropListId);
			// console.log("WorkEffortTransactionViewManagement : measureDropList ", measureDropList);
	        if(measureDropList) {
				measureDropList.registerOnChangeListener(WorkEffortTransactionViewManagement.measureDropListHandler.curry(measureDropList._field), "measureDropListHandlerManagement");
			}
            
            //in asciolto eventuale drop-list valore
            var weTransValueDropList = DropListMgr.getDropList(formName + "_weTransValue", 0);
            var weTransValueDescInput = form.down("input#" + formName + "_weTransValueDesc");
            if (weTransValueDropList && Object.isElement(weTransValueDescInput)) {
            	weTransValueDropList.registerOnChangeListener(WorkEffortTransactionViewManagement.weTransValueDropListListener.curry(form, weTransValueDescInput), "weTransValue");
            }
            
            //in ascolto drop-down valore
            var weTransValueDropDown = form.down("select#" + formName + "_weTransValue");
            var weTransValueDescInput = form.down("input#" + formName + "_weTransValueDesc");
            if (Object.isElement(weTransValueDropDown) && Object.isElement(weTransValueDescInput)) {
            	Event.observe(weTransValueDropDown, "change", WorkEffortTransactionViewManagement.weTransValueDropDownListener.curry(weTransValueDescInput));
            }
            
            this.setValueUomIdDecimalDigits(form);
        }
    },
    
    measureDropListHandler : function(droplistDiv, span1, span2, span3, span4, span5, span6, span7, span8, span9, span10, span11, span12, span13) {
		// console.log("WorkEffortTransactionViewManagement : measureDropListHandler");
        var array = new Array(span1, span2, span3, span4, span5, span6, span7, span8, span9, span10, span11, span12, span13);
		var weMeasureUomType;
		var weMeasureId;
		var weMeasureDefaultUomId;
		var weMeasureTypeValueId;
		var weMeasureDefaultUomDesc;
		var weMeasureTypeValueDesc;
		var weMeasureUomDecimalScale;
		var weMeasurePeriodTypeId;
		array.each(function(span) {
			if (Object.isElement(span)) {
				var infoList = span.innerHTML.split(":");
				if (Object.isArray(infoList)) {
					for (var i = 0; i < infoList.size(); i++) {
						if (infoList[i] === "weMeasureUomType") {
							weMeasureUomType = infoList[i + 1];
						}
						if (infoList[i] === "weMeasureId") {
							weMeasureId = infoList[i + 1];
						}
						if (infoList[i] === "weMeasureDefaultUomId") {
							weMeasureDefaultUomId = infoList[i + 1];
						}
						if (infoList[i] === "weMeasureTypeValueId") {
							weMeasureTypeValueId = infoList[i + 1];
						}
						if (infoList[i] === "weMeasureDefaultUomDesc") {
							weMeasureDefaultUomDesc = infoList[i + 1];
						}
						if (infoList[i] === "weMeasureTypeValueDesc") {
							weMeasureTypeValueDesc = infoList[i + 1];
						}
						if (infoList[i] === "weMeasureUomDecimalScale") {
							weMeasureUomDecimalScale = infoList[i + 1];
						}
						if (infoList[i] === "weMeasurePeriodTypeId") {
							weMeasurePeriodTypeId = infoList[i + 1];
						}
					}
				}
			}
		});
		var form = droplistDiv.up("form");
    	var formName = form.readAttribute("name");
    	// console.log("WorkEffortTransactionViewManagement : weMeasureId ", weMeasureId);
    	// console.log("WorkEffortTransactionViewManagement : weMeasureUomType ", weMeasureUomType);
        if (!Object.isUndefined(weMeasureId) && !Object.isUndefined(weMeasureUomType)) {
        	if (weMeasureUomType === "RATING_SCALE") {
        		var weTransValueInput = form.down("input#" + formName + "_weTransValue");
        		if (Object.isElement(weTransValueInput) && (weTransValueInput.readAttribute("type") === "text")) {
		        	WorkEffortTransactionViewManagement.refreshForm(form, weMeasureUomType, weMeasureId, weMeasureDefaultUomId, weMeasureTypeValueId);
		        } else {
		            WorkEffortTransactionViewManagement.populateFieldFromDropList(form, weMeasureDefaultUomId, weMeasureDefaultUomDesc, weMeasureTypeValueId, weMeasureTypeValueDesc, weMeasurePeriodTypeId);
		        }
	        }
	        else {
	        	var weTransValueDropList = DropListMgr.getDropList(formName + "_weTransValue", 0);
		        if (!Object.isElement(weTransValueDropList)) {
		            weTransValueDropList = form.down("select#" + formName + "_weTransValue");
		        }
		        if (Object.isElement(weTransValueDropList)) {
		        	WorkEffortTransactionViewManagement.refreshForm(form, weMeasureUomType, weMeasureId, weMeasureDefaultUomId, weMeasureTypeValueId, weMeasureUomDecimalScale, weMeasurePeriodTypeId);
		        } else {
                    WorkEffortTransactionViewManagement.populateFieldFromDropList(form, weMeasureDefaultUomId, weMeasureDefaultUomDesc, weMeasureTypeValueId, weMeasureTypeValueDesc, weMeasurePeriodTypeId);
                    
                    // refresh per uom e typeValue
                    WorkEffortTransactionViewManagement.refreshForm(form, weMeasureUomType, weMeasureId, weMeasureDefaultUomId, weMeasureTypeValueId, weMeasureUomDecimalScale, weMeasurePeriodTypeId);
    		        
                }
	        }
        }
	},
    
    populateField : function(form, lookup, weMeasureUomType) {
        form = $(form);
        var formName = form.readAttribute("name");
		
        var weMeasureIdFields = $H({'weTransAccountId' : $(lookup.down("input#weMeasureAccountCode_weTransMeasureId")),
                                    'weTransProductId' : $(lookup.down("input#weMeasureProductId_weTransMeasureId")),
                                    'weTransEmplPosTypeId' : $(lookup.down("input#weMeasureEmpPosTypeId_weTransMeasureId")),
                                    'weTransPartyId' : $(lookup.down("input#weMeasurePartyCode_weTransMeasureId")),
                                    'weTransOtherWorkEffortId' : $(lookup.down("input#weMeasureOtherWorkEffortCode_weTransMeasureId"))});
        
        weMeasureIdFields.each(function(pair) {
            if (Object.isElement(pair.value)) {
                var target = form.down("input#" + formName + "_" + pair.key + "_edit_field");
                if (Object.isElement(target)) {
                    var targLookup = LookupMgr.getLookup(formName + "_" + pair.key);
                    targLookup.setValue(pair.value.getValue());
                }
            }
        });
        
        var weMeasureOtherFields = $H({'weTransMeasureTypeDesc' : $(lookup.down("input#weMeasureMeasureTypeDesc_weTransMeasureId")),
                                       'weTransRoleDesc' : $(lookup.down("input#weMeasureRoleTypeDesc_weTransMeasureId")),
                                       'weTransOtherMeasGoalDesc' : $(lookup.down("input#weMeasureOtherGoalDesc_weTransMeasureId")),
                                       'weMeasureUomType' : weMeasureUomType,
                                       'weTransUomDesc' : $(lookup.down("input#weMeasureUomDescr_weTransMeasureId")),
                                       'weTransTypeValueDesc' : $(lookup.down("input#weMeasureTypeValueDesc_weTransMeasureId")),
                                       'weTransPeriodTypeDesc' : $(lookup.down("input#weMeasurePeriodTypeDesc_weTransMeasureId"))});
                                       
                                       
    	weMeasureOtherFields.each(function(pair) {
            if (Object.isElement(pair.value)) {
                var target = form.down("input#" + formName + "_" + pair.key);
                if (Object.isElement(target)) {
                    if (pair.key === 'weMeasureUomDescr') {
                        if (Object.isElement(weMeasureUomType) && (weMeasureUomType.readAttribute("value") !== "RATING_SCALE")) {
                            target.writeAttribute("value", pair.value.getValue());
                        }
                    } else {
                        target.writeAttribute("value", pair.value.getValue());
                    }
                }
            }
        });
    
    },
    
    populateFieldFromDropList : function(form, weMeasureDefaultUomId, weMeasureDefaultUomDesc, weMeasureTypeValueId, weMeasureTypeValueDesc) {
        var form = $(form);
        var formName = form.readAttribute("name");
		
         var weMeasureDefaultUomIdDropListId = formName + "_weTransCurrencyUomId";
		 var weMeasureDefaultUomIdDropList = DropListMgr.getDropList(weMeasureDefaultUomIdDropListId);
		 if(weMeasureDefaultUomIdDropList) {
		 	weMeasureDefaultUomIdDropList.setValue(weMeasureDefaultUomDesc, weMeasureDefaultUomId);
		 }
		 
		 var weTransTypeValueIdDropListId = formName + "_weTransTypeValueId";
		 var weTransTypeValueIdDropList = DropListMgr.getDropList(weTransTypeValueIdDropListId);
		 if(weTransTypeValueIdDropList) {
		 	weTransTypeValueIdDropList.setValue(weMeasureTypeValueDesc, weMeasureTypeValueId);
		 }
    },
    
    weTransValueDropListListener : function(form, weTransValueDescInput, span1, span2, span3, span4) {
    	var array = new Array(span1, span2, span3, span4);
		array.each(function(span) {
			if (Object.isElement(span)) {
				var infoList = span.innerHTML.split(":");
				if (Object.isArray(infoList)) {
					for (var i = 0; i < infoList.size(); i++) {
						if (infoList[i] === "uomDescr") {
							weTransValueDescInput.value = infoList[i + 1];
						}
					}
				}
			} 
		});  
    },
    
    weTransValueDropDownListener : function(weTransValueDescInput, event) {
    	var weTransValueDropDown = Event.element(event);
    	weTransValueDropDown.options[weTransValueDropDown.selectedIndex - 1];
    },
    
    refreshForm : function(form, weMeasureUomTypeValue, weTransMeasureId, weMeasureDefaultUomIdValue, weMeasureTypeValueIdValue, weMeasureUomDecimalScale) {
    	var onclickStr = form.readAttribute("onSubmit");
        var attributes =onclickStr.split(","); 
        var request = attributes[3];
        var container = attributes[2].substring(attributes[2].indexOf('\'')+1);
        
        var parameters = $H(attributes[4].substring(0, attributes[4].lastIndexOf('\'')).toQueryParams());
        
        //recupero attributi dalla form weTransEntryId e weTransId
        var weTransEntryId = form.down('input[name=weTransEntryId]');
        var weTransId = form.down('input[name=weTransId]');
        
        if(weTransEntryId && weTransId){
        	parameters.set('weTransEntryId', weTransEntryId.getValue());
        	parameters.set('weTransId', weTransId.getValue());
        }
        
        parameters.set('insertMode', 'W'); // il cambio della droplist è quasi un insert perchè alcuni input della form non vengono inviati 
        if (Object.isUndefined(parameters.get('saveView'))) {
            parameters.set('saveView', 'N');
        }
        if (Object.isUndefined(parameters.get('ajaxReloadingField'))) {
            parameters.set('ajaxReloadingField', 'Y');
        }
        if (Object.isUndefined(parameters.get('weMeasureUomType'))) {
            parameters.set('weMeasureUomType', weMeasureUomTypeValue);
        }
        if (Object.isUndefined(parameters.get('weTransMeasureId'))) {
            parameters.set('weTransMeasureId', weTransMeasureId);
        }
        if (Object.isUndefined(parameters.get('workEffortMeasureId'))) {
            parameters.set('workEffortMeasureId', weTransMeasureId);
        }
        if (Object.isUndefined(parameters.get('weMeasureDefaultUomId'))) {
            parameters.set('weMeasureDefaultUomId', weMeasureDefaultUomIdValue);
        }        
        if (Object.isUndefined(parameters.get('justRegisters'))) {
            parameters.set('justRegisters', 'Y');
        }
        if (Object.isUndefined(parameters.get('fromLookup'))) {
            parameters.set('fromLookup', 'Y');
        }
        // refresh per uom e typeValue
        if (Object.isUndefined(parameters.get('weMeasureDefaultUomId'))) {
            parameters.set('weMeasureDefaultUomId', weMeasureDefaultUomIdValue);
        }
        /*if (Object.isUndefined(parameters.get('weMeasureTypeValueId'))) {
            parameters.set('weMeasureTypeValueId', weMeasureTypeValueIdValue);
        }*/
        if (!Object.isUndefined(weMeasureUomDecimalScale)) {
            parameters.set('weMeasureUomDecimalScale', weMeasureUomDecimalScale);
        }
        
		//var container = form.up("div.child-management-container");
		ajaxUpdateAreas(container+',' + request + ',' + parameters.toQueryString());
    },
    
    setValueUomIdDecimalDigits : function(form){
    	
    	var decimalScaleField = $(form.down("input.weMeasureUomDecimalScale"));
        var valueUomIdField = $(form.down("input.mask_field_weMeasureUomDecimalScale"));
        //alert(decimalScaleField.getValue() + " - " +valueUomIdField.getValue());
        if(decimalScaleField && valueUomIdField){
        	valueUomIdField.setDecimalDigits(decimalScaleField);
        	valueUomIdField._input.setValue(valueUomIdField.getValue());
        }
    }
}

<#if !parameters.justRegisters?has_content>
	WorkEffortTransactionViewManagement.load();
</#if>
