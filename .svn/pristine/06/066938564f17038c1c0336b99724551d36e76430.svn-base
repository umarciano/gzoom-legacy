WorkEffortMeasureExtension = {
    // Manage onSelectEnd for open portlet
    // no dblclick for this form
    load: function(newContentToExplore, withoutResponder) {
        // newContentToExplore is null only the first time,
        // then newContentToExplore is a tab or a div, even if with other entityName
        
        // div in piu' rispetto alle altre gestioni
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.identify().indexOf("WorkEffortMeasure") > -1) {
        // if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.identify().indexOf("IndicatorDetailTransactionTable") > -1) {
            var table = newContentToExplore.down("table.basic-table");
            if (Object.isElement(table) && table.tagName == "TABLE") {
                //Gestione pannellino
                WorkEffortMeasureExtension.registerPanel(table);
            }
        }
        
        // Special Case
        // If the user add o remove a record from list, the event "onSelectEnd" is on a table that not in HTML,
        // so, the screenletName, in method manageSelected, is null,
        // the method load is recall when the table is replaced in HTML, so
        // the newContentToExplore is a smaller div, with class 'child-management', and it call manageSelected
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.hasClassName("child-management") 
                && newContentToExplore.identify().indexOf("WorkEffortMeasure") > -1) {
            measureTable = newContentToExplore.down("table.selectable");
            console.log("WorkEffortMeasureExtension : load measureTable ", measureTable);
            if(Object.isElement(measureTable)) {
                console.log("WorkEffortMeasureExtension : load measureTable " + TableKit.isSelectable(measureTable));
                WorkEffortMeasureExtension.onFormSelectManagement(measureTable);
            }
        }
        
        // If newContentToExplore is a tab, find the div with class 'child-management-container'
        if(Object.isElement(newContentToExplore)  && !newContentToExplore.hasClassName("child-management-container")) {
            newContentToExplore = newContentToExplore.down("div.child-management-container");
        }
        
        // the tab is one of all tab, so find only tab for entityName = WorkEffortMeasure
        if(Object.isElement(newContentToExplore) && newContentToExplore.identify() && newContentToExplore.hasClassName("child-management-container") 
            && newContentToExplore.identify().indexOf("WorkEffortMeasure") > -1) {
            //Gestione form
            WorkEffortMeasureExtension.registerForm(newContentToExplore);
        }

        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(WorkEffortMeasureExtension.responder, "WorkEffortMeasureExtension");
        }
    },
    
    /**
    * Responder functions
    **/
    responder : {
        onAfterLoad : function(newContent) {
            WorkEffortMeasureExtension.load(newContent, true);
        },
        unLoad : function() {
            return typeof "WorkEffortMeasureExtension" === "undefined";
        }
    },
    
    registerForm: function(panel) {
        if (!Object.isElement(panel)) {
            return;
        }
        var form = panel.down("form.cachable")
        if (Object.isElement(form)) {
            var table = form.down("table.selectable");
            if (Object.isElement(table)) {
                // TODO proviamo se cosi va, forse non serve...
                table.select("td").each(function(td) {
                    if (Object.isElement(td)) {
                        Event.stopObserving(td, 'dblclick');
                        Event.observe(td, 'dblclick', WorkEffortMeasureExtension.handleSelectManagement);
                    }
                });
                if(!TableKit.isRegistered(table, 'onSelectEnd',  "WorkEffortMeasureExtension_selectEnd")){
                    TableKit.registerObserver(table, "onSelectEnd", "WorkEffortMeasureExtension_selectEnd", WorkEffortMeasureExtension.onFormSelectManagement);
                    WorkEffortMeasureExtension.onFormSelectManagement(table);
                }
            }
        }
    },
    
    handleSelectManagement: function(e) {
        var cellSelected = Event.element(e);
        if(cellSelected.tagName != "TD") {
            cellSelected = cellSelected.up('td');
        }
        
        // get first cell of the row
        if(Object.isElement(cellSelected)) {
        	var rowSelected = cellSelected.up('tr');
        	if(Object.isElement(rowSelected)) {
        		cellSelected = rowSelected.down('td');
        	}
        }
        var table = cellSelected.up('table');
        var managementSelectedElementItem = Toolbar.getInstance()._getItemBySelector(".management-selected-element");
        if (managementSelectedElementItem) {
            populateElementCollection = managementSelectedElementItem.populateElementCollection;
            managementSelectedElementItem.populateElementCollection = WorkEffortMeasureExtension.selectedElementPopulateCollection.curry(cellSelected);
            
            checkExecutability = managementSelectedElementItem.checkExecutability;
            managementSelectedElementItem.checkExecutability = WorkEffortMeasureExtension.checkExecutability.curry(cellSelected);
        }
        
        var operationField = cellSelected.select('input').find(function(element) {
            return element.readAttribute('name').startsWith('operation');
        });
        if (!Object.isElement(operationField) || (Object.isElement(operationField) && operationField.getValue() !== 'CREATE')) {
            var content = table.up("div.data-management-container");
            if (!Object.isElement(content))
                content = table.up("div#searchListContainer");      
            var item = Toolbar.getInstance(content.identify()).getItem(".management-selected-element");
            if (!Object.isElement(item))
                item = Toolbar.getInstance(content.identify()).getItem(".search-selected-element");
            if (Object.isElement(item)) 
                item.fire('dom:click');
        }
        
        if (managementSelectedElementItem) {
            managementSelectedElementItem.populateElementCollection = populateElementCollection;
            managementSelectedElementItem.checkExecutability = checkExecutability;
        }
    },
    
    registerPanel: function(panel) {
        // var table = Object.isElement(panel) && panel.identify() == "${parameters.reloadRequestType?if_exists}TransactionTable_${parameters.contentIdInd?if_exists}" ? $(panel) : $("${parameters.reloadRequestType?if_exists}TransactionTable_${parameters.contentIdInd?if_exists}");
        var table = panel;
        if (Object.isElement(table)) {
            table.select("td").each(function(td) {
                if (Object.isElement(td)) {
                    td.observe("click", WorkEffortMeasureExtension.onPanelSelectManagement);
                }
            });
        }
    },
    onFormSelectManagement: function(e) {
        //Gestire errore load
        WorkEffortMeasureExtension.error();
        
    
        var table = e;
        if(table.tagName != "TABLE") {
            var element = Event.element(e);
            table = element.up("table");
        }
        var selectedRow = TableKit.Selectable.getSelectedRows(table)[0];
        var rowIndex = TableKit.getRowIndex(selectedRow);
        var operation = selectedRow.down("input[name='operation_o_" + rowIndex + "']");
        
        
        var screenletName = $(table).up("div.child-management-container");
        if (!Object.isElement(screenletName)) {
            // Special Case
            // If the user add o remove a record from list, the event "onSelectEnd" is on a table that not in HTML,
            // so, the screenletName is null and return.
            // the method load is recall when the table is replaced in HTML, so
            // the newContentToExplore is a smaller div, with class 'child-management', and it call manageSelected
            return;
        }
        var transactionPanel = $(screenletName).down("div.transactionPanel");
        var transactionPanelId = transactionPanel.identify()
        
        if (Object.isElement(operation) && "CREATE" != operation.getValue()) {
            var workEffortMeasureField = selectedRow.down("input[name='workEffortMeasureId_o_" + rowIndex + "']");
            var workEffortMeasureIsReadOnly = selectedRow.down("input[name='isReadOnly_o_" + rowIndex + "']");
            
            var reloadRequestType = selectedRow.down("input[name='reloadRequestType_o_" + rowIndex + "']");
            var onlyWithBudget = selectedRow.down("input[name='onlyWithBudget_o_" + rowIndex + "']");
            var accountFilter = selectedRow.down("input[name='accountFilter_o_" + rowIndex + "']");
            var contentIdInd = selectedRow.down("input[name='contentIdInd_o_" + rowIndex + "']");
            var contentIdSecondary = selectedRow.down("input[name='contentIdSecondary_o_" + rowIndex + "']");
            
            
            if (Object.isElement(workEffortMeasureField) && Object.isElement(transactionPanel)) {
                var specialized = WorkEffortMeasureExtension.getSpecialized();
                ajaxUpdateArea(transactionPanel.identify(), "<@ofbizUrl>reload" + reloadRequestType.getValue() + "TransactionPanel</@ofbizUrl>", 
                        $H({"onlyWithBudget" : onlyWithBudget.getValue(), "accountFilter" : accountFilter.getValue(), "reloadRequestType" : reloadRequestType.getValue(),
                            "workEffortMeasureId" : workEffortMeasureField.getValue(), "saveView" : "N", "isReadOnly" : workEffortMeasureIsReadOnly.getValue(), 
                            "rootInqyTree" : "${parameters.rootInqyTree?if_exists?default('N')}", "searchDate" : "${parameters.searchDate?if_exists?replace("&#47;", "/")}", 
                            "contentIdInd" : contentIdInd.getValue(), "contentIdSecondary" : contentIdSecondary.getValue(), 
                            "specialized" : specialized}));
            }
        }
        else {
            if(Object.isElement(transactionPanel) && Object.isElement(transactionPanel.down())) {
                transactionPanel.down().remove();
            }
        }
        
        //Elimino la portlet
        var portlets = $$("div.transactionPortlet");
        portlets.each(function(portlet) {
            if (Object.isElement(portlet) && Object.isElement(portlet.down())) {
                portlet.down().remove();
            }
        });
    },
    
    onPanelSelectManagement: function(e) {
        var td = Event.element(e);
        var table = td.up("table");
        var weTransId = td.down("input[name='weTransId']");
        var weTransEntryId = td.down("input[name='weTransEntryId']");
        var weTransMeasureId = td.down("input[name='weTransMeasureId']");
        var voucherRef = td.down("input[name='voucherRef']");
        var weTransWeId = td.down("input[name='weTransWeId']");
        var weTransTypeValueId = td.down("input[name='weTransTypeValueId']");
        var isReadOnly = td.down("input[name='isReadOnly']");
        var customTimePeriodId = td.down("input[name='customTimePeriodId']");
        var entryPartyId = td.down("input[name='entryPartyId']");
        var entryPartyIdValue = Object.isElement(entryPartyId) ? entryPartyId.getValue() : "";
        var entryRoleTypeId = td.down("input[name='entryRoleTypeId']");
        var entryRoleTypeIdValue = Object.isElement(entryRoleTypeId) ? entryRoleTypeId.getValue() : "";
        var crudEnumId = td.down("input[name='crudEnumId']");
        var valModId = td.down("input[name='valModId']");
        var glFiscalTypeEnumId = td.down("input[name='glFiscalTypeEnumId']");
        var glFiscalTypeEnumIdValue = Object.isElement(glFiscalTypeEnumId) ? glFiscalTypeEnumId.getValue() : "";
        var parentWorkEffortTypeId = td.down("input[name='parentWorkEffortTypeId']");
        var parentWorkEffortTypeIdValue = Object.isElement(parentWorkEffortTypeId) ? parentWorkEffortTypeId.getValue() : "";
        
        var weTransProductId = td.down("input[name='weTransProductId']");
        var productIdValue = Object.isElement(weTransProductId) ? weTransProductId.getValue() : "";
        var voucherRefValue = Object.isElement(voucherRef) ? voucherRef.getValue() : "";
        
        var operation = "UPDATE";
        if (weTransId.getValue() == "" || weTransId.getValue() == null) {
            operation = "CREATE";
        }
        var prevSelectedCell = table.down("td.selected-cell");
        if (Object.isElement(prevSelectedCell)) {
            prevSelectedCell.removeClassName("selected-cell");
        }
        td.addClassName("selected-cell");
        
    	var specialized = WorkEffortMeasureExtension.getSpecialized();
        
    	var reloadRequestType = td.down("input[name='reloadRequestType']");
        var onlyWithBudget = td.down("input[name='onlyWithBudget']");
        var accountFilter = td.down("input[name='accountFilter']");
        var contentIdInd = td.down("input[name='contentIdInd']");
        var contentIdSecondary = td.down("input[name='contentIdSecondary']");
        
        ajaxUpdateArea("WorkEffortMeasure_PanelPortletContainer_" + contentIdInd.getValue(), 
    	    "<@ofbizUrl>reloadTransactionPanel</@ofbizUrl>", $H({"weTransId" : weTransId.getValue(), "weTransEntryId" : weTransEntryId.getValue(),
            "weTransMeasureId" : weTransMeasureId.getValue(), "voucherRef" :  voucherRefValue, "weTransWeId" : weTransWeId.getValue(), "weTransProductId" : productIdValue,  "weTransTypeValueId" : 
            weTransTypeValueId.getValue(), "operation" : operation, "isReadOnly" : isReadOnly.getValue(), 
            "reloadRequestType" : reloadRequestType.getValue(), 
            "onlyWithBudget" : onlyWithBudget.getValue(), 
            "accountFilter" : accountFilter.getValue(), "rootInqyTree" : "${parameters.rootInqyTree?if_exists}", 
            "customTimePeriodId" : customTimePeriodId.getValue(), "entryPartyId" : entryPartyIdValue, "entryRoleTypeId" : entryRoleTypeIdValue, 
            "reloadRequestType" : reloadRequestType.getValue(), "saveView" : "N",
            "searchDate" : "${parameters.searchDate?if_exists?replace("&#47;", "/")}", "contentIdInd" : contentIdInd.getValue(), 
            "contentIdSecondary" : contentIdSecondary.getValue(), "crudEnumId" : crudEnumId.getValue(), "valModId" : valModId.getValue(),
            "parentWorkEffortTypeId" : parentWorkEffortTypeIdValue, "glFiscalTypeEnumId" : glFiscalTypeEnumIdValue, "specialized" : specialized}));
    },
    
    selectedElementPopulateCollection : function(cellSelected, form, container) {
        if (cellSelected) {
            $A(cellSelected.select('input', 'select')).each(function(element) {
                var elementName = element.readAttribute("name");
                if(elementName.indexOf('_o_') > -1) {
                    elementName = elementName.substring(0,elementName.indexOf('_o_'));
                }
                var currentValue = element.getValue();
                if (currentValue) {
                    var findElement = $A(form.getElements()).find(function(elm) {
                        return elementName === elm.readAttribute("name");
                    });
                    if (findElement && !findElement.hasClassName('url-params')) {
                        var valueHidden = findElement.readAttribute('value');
                        if (valueHidden) {
                            if (valueHidden.indexOf(currentValue) == -1) {
                                if (valueHidden.indexOf(']') != -1) {
                                    valueHidden = valueHidden.replace(']', '|]');
                                } else {
                                    valueHidden = valueHidden.concat('|]');
                                }

                                valueHidden = valueHidden.replace(']', currentValue+ ']');
                                if (valueHidden.indexOf('[') == -1)
                                    valueHidden = '['.concat(valueHidden);
                            }
                        } else {
                            valueHidden = currentValue;
                        }

                        findElement.writeAttribute('value', valueHidden);
                    } else if (!findElement || (findElement && !findElement.hasClassName('url-params'))) {


                        var inputHidden = new Element('input', {'type' : 'hidden', 'value' : currentValue, 'name' : elementName, 'id' : elementName});
                        form.insert(inputHidden);
                    }
                }
            });
        }
    },
    
    checkExecutability : function(cell, callBack, container) {
        var res = Object.isElement(cell);

        if (!res) {
            modal_box_messages.alert(['BaseMessageNoSelection']);
        } else {
            callBack();
            throw $break;
        }

        return res;
    },
    
    error : function() { 
    	if ("${parameters.errorLoadMeasure?if_exists}" != "" &&  "${parameters.errorLoadMeasure?if_exists}" != null) {
    		var data = $H({});
    		data["_ERROR_MESSAGE_"] = "${parameters.errorLoadMeasureDescr?if_exists}"; 
    		modal_box_messages.onAjaxLoad(data, null);
    		modal_box_messages.alert("${parameters.errorLoadMeasure?if_exists}");
    	} 
    },   
    
    getSpecialized : function() {
    	var specialized = '${specialized?if_exists}';
    	if (! specialized || specialized.empty()) {
    		specialized = '${parameters.specialized?if_exists}'
    	}
    	if (! specialized || specialized.empty()) {
    		return '';
    	}
    	return specialized;
    }    
}

WorkEffortMeasureExtension.load();