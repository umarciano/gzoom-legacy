WorkEffortMeasureList = {
    load: function(withoutResponder) {
        WorkEffortMeasureList.loadMeasureTable($('table_WEMFPMMF${accountTypeEnumId?if_exists}_${contentIdInd?if_exists}_WorkEffortMeasure'));
    },
    
    loadMeasureTable : function(measureTable) {  
        if(measureTable) {
            if (typeof TableKit != 'undefined') {
                if (TableKit.isSelectable(measureTable)) {
                    WorkEffortMeasureList.managContextLink(measureTable, '.workEffortExtMenu${accountTypeEnumId?if_exists}', 'uomTypeId');
                    TableKit.registerObserver(measureTable, 'onSelectEnd', 'workeffortmeasure-list-selection', function(measureTable, e) {
                        WorkEffortMeasureList.managContextLink(measureTable, '.workEffortExtMenu${accountTypeEnumId?if_exists}', 'uomTypeId');
                        WorkEffortMeasureList.managAccountDropList(measureTable, "INDICATOR" == "${accountTypeEnumId?if_exists}");
                        WorkEffortMeasureList.managAccountLookup(measureTable, "INDICATOR" == "${accountTypeEnumId?if_exists}");
                    });
                    
                    /**
                    * Calcolo somma dei kpiScoreWeight solo nel caso di indicatore
                    */                    
                    if ("INDICATOR" == "${accountTypeEnumId?if_exists}" && $('${accountTypeEnumId?if_exists}_${contentIdInd?if_exists}_kpiScoreWeightTot') != null) {
	            		WorkEffortMeasureList.kpiScoreWeightTot(measureTable);
                   }
                   
                }
            }
         }
    },  
    
    
    kpiScoreWeightTot : function(table) {
    
    	/**
        * Utilizzato per calcolare la somma kpiScoreWeightTot
        */
        var kpiScoreWeightLists = table.select('input').findAll(function(element) {
            if(element.readAttribute('id')) {
                return element.readAttribute('id').indexOf("WorkEffortMeasureLayoutMultiForm_kpiScoreWeight") > -1 || 
                element.readAttribute('id').indexOf("WEMFromPanelisObiettivoManagementMultiForm_kpiScoreWeight") > -1 ||
                element.readAttribute('id').indexOf("WEMFromPanelManagementMultiForm_kpiScoreWeight") > -1;
            }
        });

        if(kpiScoreWeightLists) {
        	var newElementAdd = "";
    		var kpiScoreWeightTotInput = $('${accountTypeEnumId?if_exists}_${contentIdInd?if_exists}_kpiScoreWeightTot');
    		var kpiScoreWeightTotInputHidden = $('${accountTypeEnumId?if_exists}_${contentIdInd?if_exists}_kpiScoreWeightTotHidden');
    	
            kpiScoreWeightLists.each(function(element) {
            	
            	//controllo per ogni elemento se è quello selezionato se si setto la variabile kpiScoreWeightOld
                tr = element.up('tr');
                if (tr.hasClassName('selected-row')) {
                	element.kpiScoreWeightOld = element.getValue();
                }
                
                //controllo se è un elemento appena inserito, se si aggiungo alla somma il valore di kpiScoreWeightOld
				var rowIndex = TableKit.getRowIndex(tr);       
                insertMode = tr.select("input[name=insertMode_o_"+ rowIndex +"]").first();
                if (typeof insertMode !== 'undefined'  && insertMode.getValue() == 'Y' ) {                	
                	WorkEffortMeasureList.setValue(kpiScoreWeightTotInput, "");
                	
                	//controllo se è un elemento appena inserito, se si aggiungo alla somma il valore di kpiScoreWeightOld
	                var glAccountId = tr.select("input[name=glAccountId_o_"+ rowIndex +"]").first();
                	if ( typeof glAccountId !== 'undefined'  && glAccountId.getValue() !== '') {
	                	if (newElementAdd == "") {
	                		newElementAdd = "[";
	                	} else {
	                		newElementAdd += ";";
	                	}
	                	newElementAdd += element.getValue();
	                }
                } 
                element.observe('change',function(event) {
            	
            		
	            		var kpiScoreWeightTotInput = $('${accountTypeEnumId?if_exists}_${contentIdInd?if_exists}_kpiScoreWeightTot');
	            		var kpiScoreWeightTotInputHidden =  $('${accountTypeEnumId?if_exists}_${contentIdInd?if_exists}_kpiScoreWeightTotHidden');
	            		
	            		WorkEffortMeasureList.setValue(kpiScoreWeightTotInput, "");
	            		
	            		new Ajax.Request("<@ofbizUrl>calculateAssocWeightOrKpiScoreWeight</@ofbizUrl>", {
			                    parameters: {
			                        "amountTotValue": WorkEffortMeasureList.getValue(kpiScoreWeightTotInputHidden),
			                        "amountOld": this.kpiScoreWeightOld,
			                        "amountNew": this.getValue()		                        
			                    }, 
				            	onSuccess: function(response){
				               		var data = response.responseText.evalJSON(true);
				               		WorkEffortMeasureList.setValue(kpiScoreWeightTotInput, data.amountTotValueNew);
				               		WorkEffortMeasureList.setValue(kpiScoreWeightTotInputHidden, data.amountTotValueNew);				                    
				                }
				            });
				  
					
				});
					
				element.observe('focus',function(event) {
					  this.kpiScoreWeightOld = this.getValue();
					});
					
				
            });
            
            /**
   			* Calcolo la somma delle nuove righe che sono state insrite e aggiorno il totale
   			*/
   			if (newElementAdd != "") {
   				newElementAdd += "]";
   				new Ajax.Request("<@ofbizUrl>calculateAssocWeightOrKpiScoreWeightAddNew</@ofbizUrl>", {
                    parameters: {
                        "amountTotValue": WorkEffortMeasureList.getValue(kpiScoreWeightTotInputHidden),
                        "amountElementList": newElementAdd		                     
                    }, 
	            	onSuccess: function(response){
	               		var data = response.responseText.evalJSON(true);
	               		WorkEffortMeasureList.setValue(kpiScoreWeightTotInput, data.amountElementTotValueNew);
	               		WorkEffortMeasureList.setValue(kpiScoreWeightTotInputHidden, data.amountElementTotValueNew);							                    
	                }
	            }); 
   			
   			}
            
        }
		                               
    },   
    
    
    managContextLink : function(table, contextLink, condition) {
    	var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
    	if (selectedRow == null){
    		var context_link = $$(contextLink).first();
    		if (Object.isElement(context_link)) {
    			context_link.addClassName('hidden');
    		}
    	}
        if(selectedRow) {        
            var value = $A(selectedRow.select('input')).find(function(s) {
                return s.readAttribute('name').indexOf(condition) > -1;
            });      
                  
            if(value.readAttribute('value') == 'RATING_SCALE') {            
                var context_links = $$(contextLink);                
                context_links.each(function(context_link){
                	if(context_link.hasClassName('hidden')){
                		context_link.removeClassName('hidden');
                	}
                });	    		
            }
            else {
                var context_links = $$(contextLink);                
                context_links.each(function(context_link){
                	if(!context_link.hasClassName('hidden')){
                		context_link.addClassName('hidden');
                	}
                });
            }
        }
    },
    
    managAccountLookup : function(table, isIndicator) {
    	var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
    	if(selectedRow) {
    		var rowIndex = TableKit.getRowIndex(selectedRow);
    		var formName = selectedRow.up("form").readAttribute("name");
			var accountLookup = selectedRow.down("div#" + formName + "_glAccountId_o_" + rowIndex);
    		if(Object.isElement(accountLookup)) {
    			var lookup = LookupMgr.getLookup(accountLookup.identify());
                if (lookup) {
                	lookup.registerOnSetInputFieldValue(WorkEffortMeasureList.accountLookupHandler.curry(selectedRow, accountLookup, isIndicator), 'WorkEffortMeasureList_accountLookupHandler');
                }
    		}
    	}
    },
    
    accountLookupHandler : function(selectedRow, lookup, isIndicator) {
    	if(Object.isElement(selectedRow)) {
    		var rowIndex = TableKit.getRowIndex(selectedRow);
        	var inputEnumId = $(lookup.down("input[name='inputEnumId_glAccountId_o_" + rowIndex + "']"));
        	if(Object.isElement(inputEnumId)) {
        	
        	    if (isIndicator) {
        	        var weOtherGoalEnumId = selectedRow.down("input[name='weOtherGoalEnumId_o_" + rowIndex + "']");
            	    if (Object.isElement(weOtherGoalEnumId)) {
                		if("ACCINP_OBJ" != inputEnumId.getValue()) {
                			weOtherGoalEnumId.setValue("WEMOMG_NONE");
                		}
                		else {
                			weOtherGoalEnumId.setValue("WEMOMG_WEFF");
                		}
            		}
        		}
        		
        	    var detailEnumId = $(lookup.down("input[name='detailEnumId_glAccountId_o_" + rowIndex + "']"));
    		    var uomDescr = selectedRow.down("input[name='uomDescr_o_" + rowIndex + "']");
    		    var uomDescrLang = selectedRow.down("input[name='uomDescrLang_o_" + rowIndex + "']");
    		    if (Object.isElement(uomDescr)) {
    		    	WorkEffortMeasureList.manageUomDescrField(uomDescr, detailEnumId, inputEnumId);
        		}
    		    if (Object.isElement(uomDescrLang)) {
    		    	WorkEffortMeasureList.manageUomDescrField(uomDescrLang, detailEnumId, inputEnumId);
        		}    		    
        	}
        }
        
        //Bug 4591
        var weMeasureTypeEnumIdField = $(lookup.down("input[name='weMeasureTypeEnumId_glAccountId_o_" + rowIndex + "']"));
        var kpiScoreWeightField = selectedRow.down("input[name='kpiScoreWeight_o_" + rowIndex + "']");
        var kpiOtherWeightField = selectedRow.down("input[name='kpiOtherWeight_o_" + rowIndex + "']");        
        if (Object.isElement(kpiScoreWeightField)) {
        	WorkEffortMeasureList.manageKpiWeightField(kpiScoreWeightField, weMeasureTypeEnumIdField);
        }
        if (Object.isElement(kpiOtherWeightField)) {
        	WorkEffortMeasureList.manageKpiWeightField(kpiOtherWeightField, weMeasureTypeEnumIdField);
        }        
    },
    
    managAccountDropList : function(table, isIndicator) {
    	var selectedRow = $A(TableKit.Selectable.getSelectedRows(table)).first();
    	if(selectedRow) {
    		var rowIndex = TableKit.getRowIndex(selectedRow);
    		var formName = selectedRow.up("form").readAttribute("name");
    		var dropListId = formName + "_glAccountId_o_" + rowIndex;
            
    		var accountDropList = DropListMgr.getDropList(dropListId);
    		if(accountDropList) {
    		    accountDropList.registerOnChangeListener(WorkEffortMeasureList.accountDropListHandler.curry(isIndicator, selectedRow, accountDropList._field), 'WorkEffortMeasureList_accountDropListHandler' + rowIndex);
    		}
    	}
    },
    
    accountDropListHandler : function(isIndicator, selectedRow, accountDropList) {
    	if(Object.isElement(selectedRow)) {
    		var rowIndex = TableKit.getRowIndex(selectedRow);
    		var inputEnumId = selectedRow.down("input[name='inputEnumId_o_" + rowIndex + "']");
            if(Object.isElement(inputEnumId)) {
        	
        	    if (isIndicator) {
        	        var weOtherGoalEnumId = selectedRow.down("input[name='weOtherGoalEnumId_o_" + rowIndex + "']");
            	    if (Object.isElement(weOtherGoalEnumId)) {
                		if("ACCINP_OBJ" != inputEnumId.getValue()) {
                			weOtherGoalEnumId.setValue("WEMOMG_NONE");
                		}
                		else {
                			weOtherGoalEnumId.setValue("WEMOMG_WEFF");
                		}
            		}
        		}
        		
        	    var detailEnumId = selectedRow.down("input[name='detailEnumId_o_" + rowIndex + "']");
    		    var uomDescr = selectedRow.down("input[name='uomDescr_o_" + rowIndex + "']");
    		    var uomDescrLang = selectedRow.down("input[name='uomDescrLang_o_" + rowIndex + "']");
    		    if (Object.isElement(uomDescr)) {
    		    	WorkEffortMeasureList.manageUomDescrField(uomDescr, detailEnumId, inputEnumId);
        		}
    		    if (Object.isElement(uomDescrLang)) {
    		    	WorkEffortMeasureList.manageUomDescrField(uomDescrLang, detailEnumId, inputEnumId);
        		}    		    
        	}
        }
        var weMeasureTypeEnumIdField = selectedRow.down("input[name='weMeasureTypeEnumId_o_" + rowIndex + "']");
        var kpiScoreWeightField = selectedRow.down("input[name='kpiScoreWeight_o_" + rowIndex + "']");
        var kpiOtherWeightField = selectedRow.down("input[name='kpiOtherWeight_o_" + rowIndex + "']");        
        if (Object.isElement(kpiScoreWeightField)) {
        	WorkEffortMeasureList.manageKpiWeightField(kpiScoreWeightField, weMeasureTypeEnumIdField);
        }
        if (Object.isElement(kpiOtherWeightField)) {
        	WorkEffortMeasureList.manageKpiWeightField(kpiOtherWeightField, weMeasureTypeEnumIdField);
        }        
    },
    
    manageUomDescrField : function(field, detailEnumId, inputEnumId) {
        if (Object.isElement(detailEnumId)) {
  		    if ("ACCINP_UO" === inputEnumId.getValue() && "ACCDET_NULL" === detailEnumId.getValue()) {
  		    	field.setAttribute("readonly", "readonly");
  		    	field.setValue('');
  		    	field.removeAttribute("value");
  		    } else {
  		    	field.removeAttribute("readonly");
  		    }
	    }    	
    },
    
    manageKpiWeightField : function(field, weMeasureTypeEnumIdField) {
        if (Object.isElement(weMeasureTypeEnumIdField)) {
            if (weMeasureTypeEnumIdField.getValue() != "WEMT_PERF") {
            	field.setValue("0");
            } else {
            	field.setValue("100");
            }
        }
    },    
    
    getValue : function(inputField) {
	    var valueStr = "";
		if(Object.isElement(inputField)) {
			valueStr = inputField.getValue();
		}
		if(valueStr.indexOf("/") > 0) {
			valueStr = valueStr.substring(0, valueStr.indexOf("/"));
		}
		return valueStr;
	},
	
	setValue : function(inputField, valueFiled) {
		valueStr = "";
		if(Object.isElement(inputField)) {
			valueStr = inputField.getValue();
		}
		if(valueStr.indexOf("/") > 0) {
			valueStr = valueStr.substring(valueStr.indexOf("/"));
		}
		inputField.setValue(valueFiled + valueStr);
	}
}

WorkEffortMeasureList.load();
