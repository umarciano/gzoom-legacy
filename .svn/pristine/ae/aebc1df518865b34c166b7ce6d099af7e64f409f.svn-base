WorkEffortTransactionStandard = {

    table: $("table_TRANSSTD_WorkEffortTransactionIndicatorView-${context.relationTitle?if_exists}"),    
        
    load: function(newContentToExplore, withoutResponder) {
        WorkEffortTransactionStandard.registerForm();
        
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(WorkEffortTransactionStandard.responder, "WorkEffortTransactionStandard");
        }
    },
    
    /**
    * Responder functions
    **/
    responder : {
        onLoad : function(newContent) {
            // del tipo  WETVST003INDICATOR_WorkEffortTransactionView-Indicator
            if(newContent.identify() == "WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}") {
                WorkEffortTransactionStandard.registerForm();
            }
        },
        unLoad : function() {
            return typeof "WorkEffortTransactionStandard" === "undefined";
        }
    },
    
    registerForm: function() {
        if(Object.isElement(WorkEffortTransactionStandard.table)) {
            <#if context.showDetail?default("N") == "ONE">
            	var first = true;
          		if ("${parameters.workEffortMeasureId?if_exists}" != "" || "${parameters.workEffortMeasureId?if_exists}" != null) {
              
              		var firstAttr = WorkEffortTransactionStandard.table.select('input').find(function(element) {
                  		return element.readAttribute('name').startsWith('workEffortMeasureId_o_') && "${parameters.workEffortMeasureId?if_exists}" == element.getValue();
              		});
              
              		var first = firstAttr.up("td");
              		WorkEffortTransactionStandard.cellSelect(first);
          		}
            
            
            	WorkEffortTransactionStandard.table.select("td.refresh-form-td").each(function(td) {
                	if (Object.isElement(td)) {
                    	Event.stopObserving(td, 'click');
                    	Event.observe(td, 'click', WorkEffortTransactionStandard.handleSelectManagement);
                	}
            	});
            </#if>
            
            if(!TableKit.isRegistered(WorkEffortTransactionStandard.table, "onSelectEnd", "WorkEffort_selectEnd")){
                TableKit.registerObserver(WorkEffortTransactionStandard.table, "onSelectEnd", "WorkEffortAssocExtViewExtension_selectEnd", WorkEffortTransactionStandard.onPanelSelectManagement);
                WorkEffortTransactionStandard.onPanelSelectManagement();
            }
            
            TableKit.registerObserver(WorkEffortTransactionStandard.table, 'onDblClickSelectEnd', "WorkEffort_dblclick-select-table", function(table, e) {
                // apre il dettaglio dell'indicatore oppure il popup del movimento
                WorkEffortTransactionStandard.openManagement(e);
            });
        }
        
        var dropListId = "WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}_glAccountId";
        var accountDropList = DropListMgr.getDropList(dropListId);
        if(accountDropList) {
            accountDropList.registerOnChangeListener(WorkEffortTransactionStandard.accountDropListHandler.curry(accountDropList._field), 'WorkEffortMeasureList_accountDropListHandler');
        }
        
        
        // TODO per ora qui
        if ("${parameters.errorLoadTrans?if_exists}" != "" &&  "${parameters.errorLoadTrans?if_exists}" != null) {
            var data = $H({});
            data["_ERROR_MESSAGE_"] = "${parameters.errorLoadTransDescr?if_exists}"; 
            modal_box_messages.onAjaxLoad(data, null);
            modal_box_messages.alert("${parameters.errorLoadTrans?if_exists}");
        }
        
        if ("${parameters.insertMode?if_exists}") {
            if(Object.isElement(WorkEffortTransactionStandard.table)) {
                WorkEffortTransactionStandard.table.select("td.widget-area-style").each(function(td) {
                    if (!Object.isElement(td.up("tr.new-row"))) {
                        td.writeAttribute("readonly", "readonly");
                    }
                });
                WorkEffortTransactionStandard.table.select("input[type='text']").each(function(input) {
                    if (!Object.isElement(input.up("tr.new-row"))) {
                        input.writeAttribute("readonly", "readonly");
                    }
                }); 
                WorkEffortTransactionStandard.table.select('input[name="classNames"]').each(function(input) {
                    if (!Object.isElement(input.up("tr.new-row"))) {
                        input.writeAttribute("readonly", "readonly");
                    }
                });
            }
        }
        
        if (Object.isElement(WorkEffortTransactionStandard.table)) {
            var ths = WorkEffortTransactionStandard.table.select("th.master-th");
            if (Object.isElement(ths[0])) {
                var width = $(ths[0]).getWidth();
                <#if context.manageWidth?default("Y") == "Y">
                    $(ths[0]).setStyle({width:  width + "px"});
                </#if>
                var tdLasts = WorkEffortTransactionStandard.table.select("td.slave-th");
                tdLasts.each(function(element){
                    $(element).setStyle({width:  width + "px"});
                });
            }
            
            <#if context.manageHeight?default("Y") == "Y">
                var tds = WorkEffortTransactionStandard.table.select("td.master-td");
                var height = 25;
                if (Object.isElement(tds[0])) {
                    height = $(tds[0]).getHeight();
                }
                var tdSecondRows = WorkEffortTransactionStandard.table.select("td.slave-td");
                tdSecondRows.each(function(element){
                    var tr = element.up("tr");
                    var cc = $w(tr.className);
                    var previoustr = tr.previous("tr");
                    var previouscc = $w(previoustr.className);
                    if(cc[0] == previouscc[0]) {
                        var previoustd = previoustr.down("td.master-td");
                        if (Object.isElement(previoustd)) {
                            height = $(previoustd).getHeight();
                        }
                    }
                    $(element).setStyle({height: height + "px"});
                });
            </#if>
        }
    },
    
    onPanelSelectManagement: function() {
        var selectRow = TableKit.Selectable.getSelectedRows(WorkEffortTransactionStandard.table)[0];
        var selectedTd = selectRow.down("td.widget-area-style");
        // da il colore di selezionato all'indicatore oppure al movimento
        WorkEffortTransactionStandard.cellSelect($(selectedTd));
    },
    
    handleSelectManagement: function(e) {
        var cellSelected = Event.element(e);
        if(cellSelected.tagName != "TD") {
            cellSelected = cellSelected.up('td');
        }
        WorkEffortTransactionStandard.refreshForm($(cellSelected));
    },
    
    refreshForm: function (workEffortMeasure, isNext) {
        var form = $("WETVST003${accountTypeEnumId?if_exists}_WorkEffortTransactionView-${context.relationTitle?if_exists}");
        var onclickStr = form.readAttribute("onSubmit");
        var attributes =onclickStr.split(","); 
        var request = attributes[3];
        var container = attributes[2].substring(attributes[2].indexOf('\'')+1);
        
        var parametersMap = $H(attributes[4].substring(0, attributes[4].lastIndexOf('\'')).toQueryParams());
        
        if (Object.isElement(workEffortMeasure)) {
            var inputField = workEffortMeasure.down('input');
            var name = inputField.getAttribute('name');
            var rowIndex = name.substring(name.lastIndexOf("_o_") + 3, name.lenght)
            
            var workEffortMeasureId = $(workEffortMeasure.down("input[name='workEffortMeasureId_o_" + rowIndex + "']"));
            
            if ("${parameters.workEffortMeasureId?if_exists}" == "" || "${parameters.workEffortMeasureId?if_exists}" == null || workEffortMeasureId.getValue() != "${parameters.workEffortMeasureId?if_exists}" ) {
                parametersMap.set("workEffortMeasureId", workEffortMeasureId.getValue());
                parametersMap.set("justRegisters", "Y");
                ajaxUpdateAreas(container+',' + request + ',' + parametersMap.toQueryString());
            }
            
        } else {
            var scrollInt = parametersMap.get("scrollInt");
            if (isNext) {
                parametersMap.set("scrollInt", new Number(scrollInt) + 1);
            } else {
                parametersMap.set("scrollInt", new Number(scrollInt) - 1);
            }
            parametersMap.set("justRegisters", "Y");
            
            ajaxUpdateAreas(container+',' + request + ',' + parametersMap.toQueryString());
        }
       
    },
    
    accountDropListHandler: function(accountDropList) {
        if(Object.isElement(accountDropList)) {
            var selectedRow = accountDropList.up("tr");
            
            var inputField = selectedRow.down('input');
            var name = inputField.getAttribute('name');
            var rowIndex = name.substring(name.lastIndexOf("_o_") + 3, name.lenght)
            
            var abbreviation = selectedRow.down("input[name='abbreviation_o_" + rowIndex + "']");
            var weTransUomAbb = selectedRow.down("div.abbreviation");
            if (Object.isElement(weTransUomAbb)) {
                weTransUomAbb.innerHTML = abbreviation.getValue();
            }
            
            var abbreviationLang = selectedRow.down("input[name='abbreviationLang_o_" + rowIndex + "']");
            var weTransUomAbbLang = selectedRow.down("div.abbreviationLang");
            if (Object.isElement(weTransUomAbbLang)) {
                weTransUomAbbLang.innerHTML = abbreviationLang.getValue();
            }
            
            var inputEnumId = selectedRow.down("input[name='inputEnumId_o_" + rowIndex + "']");
            if(Object.isElement(inputEnumId)) {
                var weOtherGoalEnumId = selectedRow.down("input[name='weOtherGoalEnumId_o_" + rowIndex + "']");
                if (Object.isElement(weOtherGoalEnumId)) {
                    if("ACCINP_OBJ" != inputEnumId.getValue()) {
                        weOtherGoalEnumId.setValue("WEMOMG_NONE");
                    }
                    else {
                        weOtherGoalEnumId.setValue("WEMOMG_WEFF");
                    }
                }
                
                var detailEnumId = selectedRow.down("input[name='detailEnumId_o_" + rowIndex + "']");
                var uomDescr = selectedRow.down("input[name='uomDescr_o_" + rowIndex + "']");
                var uomDescrLang = selectedRow.down("input[name='uomDescrLang_o_" + rowIndex + "']");
                if (Object.isElement(uomDescr)) {
                    WorkEffortTransactionStandard.manageUomDescrField(uomDescr, detailEnumId, inputEnumId);
                }
                if (Object.isElement(uomDescrLang)) {
                    WorkEffortTransactionStandard.manageUomDescrField(uomDescrLang, detailEnumId, inputEnumId);
                }
            }
        }
    },
    
    manageUomDescrField: function(field, detailEnumId, inputEnumId) {
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
    
    cellSelect: function(td) {
        if (!Object.isElement(td)) {
            return;
        }
        var tr = td.up('tr');
        var rowspan = 0;
        if (!$(td).hasClassName('selected-cell')) {
           if (Object.isElement(WorkEffortTransactionStandard.table)) {
                WorkEffortTransactionStandard.table.select('td').each(function(cell) {
                    cell.removeClassName("selected-cell");
                });
                WorkEffortTransactionStandard.table.select('tr').each(function(cell) {
                    cell.removeClassName("selected-row");
                });
            }
            td.addClassName("selected-cell");
        }
    },
    
    openManagement: function(e) {
        var selectedRow = TableKit.Selectable.getSelectedRows(WorkEffortTransactionStandard.table).first();
        if (Object.isElement(selectedRow)) {
            var td = selectedRow.down("td.widget-area-style")
            if (td.hasClassName('open-portlet')) {
                WorkEffortTransactionStandard.openPanel(td);
            } else {
                var operationField = selectedRow.select('input').find(function(element) {
                    return element.readAttribute('name').startsWith('operation');
                });
                if (!Object.isElement(operationField) || (Object.isElement(operationField) && operationField.getValue() !== 'CREATE')) {
                    var content = WorkEffortTransactionStandard.table.up("div.management");
                    if (!Object.isElement(content))
                        content = WorkEffortTransactionStandard.table.up("div#searchListContainer");      
                    var item = Toolbar.getInstance(content.identify()).getItem(".management-selected-element");
                    if (!Object.isElement(item))
                        item = Toolbar.getInstance(content.identify()).getItem(".search-selected-element");
                    if (Object.isElement(item)) 
                        item.fire('dom:click');
                }
            }
        }
    },
    
    openPanel: function(td) {
            var firstAttr = td.select('input').find(function(element) {
                return element.readAttribute('name').startsWith('weTransMeasureId_o_');
            });
            var name = firstAttr.getAttribute('name');
            var rowIndex = name.substring(name.lastIndexOf("_o_") + 3, name.lenght)
            var isReadOnly = $(td.down("input[name='isReadOnly_o_" + rowIndex + "']"));
            var isReadOnlyValue = Object.isElement(isReadOnly) ? isReadOnly.getValue() : "";
            var weTransId = $(td.down("input[name='weTransId_o_" + rowIndex + "']"));
            var weTransIdValue = Object.isElement(weTransId) ? weTransId.getValue() : "";
            var weTransEntryId = $(td.down("input[name='weTransEntryId_o_" + rowIndex + "']"));
            var weTransEntryIdValue = Object.isElement(weTransEntryId) ? weTransEntryId.getValue() : "";
            var weTransMeasureId = $(td.down("input[name='weTransMeasureId_o_" + rowIndex + "']"));
            var weTransWeId = $(td.down("input[name='weTransWeId_o_" + rowIndex + "']"));
            var weTransTypeValueId = $(td.down("input[name='weTransTypeValueId_o_" + rowIndex + "']"));
            var customTimePeriodId = $(td.down("input[name='customTimePeriodId_o_" + rowIndex + "']"));
            var customTimePeriodIdValue = Object.isElement(customTimePeriodId) ? customTimePeriodId.getValue() : "";
            var entryPartyId = $(td.down("input[name='entryPartyId_o_" + rowIndex + "']"));
            var entryPartyIdValue = Object.isElement(entryPartyId) ? entryPartyId.getValue() : "";
            var entryRoleTypeId = $(td.down("input[name='entryRoleTypeId_o_" + rowIndex + "']"));
            var entryRoleTypeIdValue = Object.isElement(entryRoleTypeId) ? entryRoleTypeId.getValue() : "";
            var crudEnumId = $(td.down("input[name='crudEnumId_o_" + rowIndex + "']"));
            var crudEnumIdValue = Object.isElement(crudEnumId) ? crudEnumId.getValue() : "";
            var valModId = $(td.down("input[name='valModId_o_" + rowIndex + "']"));
            var valModIdValue = Object.isElement(valModId) ? valModId.getValue() : "";
            var glFiscalTypeEnumId = $(td.down("input[name='glFiscalTypeEnumId_o_" + rowIndex + "']"));
            var glFiscalTypeEnumIdValue = Object.isElement(glFiscalTypeEnumId) ? glFiscalTypeEnumId.getValue() : "";
            var parentWorkEffortTypeId = $(td.down("input[name='parentWorkEffortTypeId_o_" + rowIndex + "']"));
            var parentWorkEffortTypeIdValue = Object.isElement(parentWorkEffortTypeId) ? parentWorkEffortTypeId.getValue() : "";
            var workEffortTypeId = $(td.down("input[name='workEffortTypeId_o_" + rowIndex + "']"));
            var workEffortTypeIdValue = Object.isElement(workEffortTypeId) ? workEffortTypeId.getValue() : "";
            var searchDateCalculate = '${context.searchDateCalculate?if_exists?default("")}';
            
            var weTransProductId = $(td.down("input[name='weTransProductId_o_" + rowIndex + "']"));
            var productIdValue = Object.isElement(weTransProductId) ? weTransProductId.getValue() : "";
            
            var operation = "UPDATE";
            if (weTransIdValue == "" || weTransIdValue == null) {
                operation = "CREATE";
            }
            var h = $H({"isReadOnly" : isReadOnlyValue, "weTransId" : weTransIdValue, "weTransEntryId" : weTransEntryIdValue,
                "weTransMeasureId" : weTransMeasureId.getValue(), "weTransWeId" : weTransWeId.getValue(), "weTransProductId" : productIdValue,  "weTransTypeValueId" : weTransTypeValueId.getValue(), "operation" : operation,
                "customTimePeriodId" : customTimePeriodIdValue, "entryPartyId" : entryPartyIdValue, "entryRoleTypeId" : entryRoleTypeIdValue, "reloadRequestType" : "${parameters.reloadRequestType?if_exists}", "saveView" : "N", "rootInqyTree" : "${parameters.rootInqyTree?if_exists?default('N')}", 
                "searchDate" : "${parameters.searchDate?if_exists?replace("&#47;", "/")}", "contentIdInd" : "${parameters.contentIdInd?if_exists}", "contentIdSecondary" : "${parameters.contentIdSecondary?if_exists}", "crudEnumId" : crudEnumIdValue, "valModId" : valModIdValue,
                "parentWorkEffortTypeId" : parentWorkEffortTypeIdValue, "glFiscalTypeEnumId" : glFiscalTypeEnumIdValue, "specialized" : '${parameters.specialized?if_exists}', "showValuesPanel" : "${showValuesPanel?if_exists}", "onlyWithBudget" : "${onlyWithBudget?if_exists}", 
                "accountFilter" : "${parameters.accountFilter?if_exists}", "fromValoriIndicatori" : "Y", "workEffortTypeId" : workEffortTypeIdValue, "searchDateCalculate" : searchDateCalculate})
            var href = "<@ofbizUrl>reloadTransactionPanel</@ofbizUrl>?" + h.toQueryString();
            
            Utils.showModalBox(href, {'title' : ' ', afterLoadModal: LookupProperties.afterLoadModal, beforeHideModal: LookupProperties.beforeHideModal, afterHideModal: LookupProperties.afterHideModal, width: document.viewport.getWidth() - (document.viewport.getWidth() * 0.1), height: document.viewport.getHeight() - (document.viewport.getHeight() * 0.3)});
    }
}

WorkEffortTransactionStandard.load();