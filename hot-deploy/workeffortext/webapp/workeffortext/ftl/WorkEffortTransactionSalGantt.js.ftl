WorkEffortTransactionSalGantt = {

    load: function(newContentToExplore, withoutResponder) {
        
        //Gestione pannellino
        WorkEffortTransactionSalGantt.registerPanel();
        
        //Gestisco ricaricamento dopo salvataggio transaction
        <#if parameters.weTransId?has_content>
            WorkEffortMeasureProjectTransactionPanel.reloadPanelAfterSave();
        </#if>
        
        if (!withoutResponder) {
            UpdateAreaResponder.Responders.register(WorkEffortTransactionSalGantt.responder, "WorkEffortTransactionSalGantt");
        }
    },
    
    /**
    * Responder functions
    **/
    responder : {
        onLoad : function(newContent) {
            if(newContent.identify() == "WorkEffortMeasureIndicatorSalGanttTransactionPanel_SAL") {
                WorkEffortTransactionSalGantt.registerPanel(newContent);
            }
        },
        unLoad : function() {
            return typeof "WorkEffortTransactionSalGantt" === "undefined";
        }
    },
    
    registerPanel: function(panel) {
        var table = Object.isElement(panel) && panel.identify() == "IndicatorTransactionGanttTable" ? $(panel) : $("IndicatorTransactionGanttTable");
        if (Object.isElement(table)) {
            table.select("td").each(function(td) {
                if (Object.isElement(td)) {
                	var glAccountId = '';
                	var glAccountIdField = td.down("input[name='glAccountId']");
                	if (Object.isElement(glAccountIdField)) {
                		glAccountId = glAccountIdField.getValue();
                	}
                	
                	if (glAccountId == 'SAL01' || glAccountId == 'SAL03') {
                		td.observe("dblclick", WorkEffortTransactionSalGantt.onPanelDblClick);
                	} else {
                		td.observe("click", WorkEffortTransactionSalGantt.onPanelSelectManagement);
                	}                    
                }
            });
        }
    },
    
    onPanelSelectManagement: function(e) {
        var td = Event.element(e);
        if(td.tagName != "TD") {
            td = td.up("td");
        }
        var table = td.up("table");
        var weTransId = td.down("input[name='weTransId']");
        var weTransEntryId = td.down("input[name='weTransEntryId']");
        var weTransMeasureId = td.down("input[name='weTransMeasureId']");
        var weTransWeId = td.down("input[name='weTransWeId']");
        var weTransTypeValueId = td.down("input[name='weTransTypeValueId']");
        var isReadOnly = td.down("input[name='isReadOnly']");
        var customTimePeriodId = td.down("input[name='customTimePeriodId']");
        var glFiscalTypeEnumId = td.down("input[name='glFiscalTypeEnumId']");
        var glFiscalTypeEnumIdValue = Object.isElement(glFiscalTypeEnumId) ? glFiscalTypeEnumId.getValue() : "";
        var parentWorkEffortTypeId = td.down("input[name='parentWorkEffortTypeId']");
        var parentWorkEffortTypeIdValue = Object.isElement(parentWorkEffortTypeId) ? parentWorkEffortTypeId.getValue() : "";
        
        var operation = "UPDATE";
        if (weTransId.getValue() == "" || weTransId.getValue() == null) {
            operation = "CREATE";
        }
        var prevSelectedCell = table.down("td.selected-cell");
        if (Object.isElement(prevSelectedCell)) {
            prevSelectedCell.removeClassName("selected-cell");
        }
        td.addClassName("selected-cell");
        
    	var specialized = WorkEffortTransactionSalGantt.getSpecialized();
        ajaxUpdateArea("WorkEffortMeasureIndicatorModelPortletContainerSal_SAL", "<@ofbizUrl>reloadTransactionPortlet</@ofbizUrl>", $H({"weTransId" : weTransId.getValue(), "weTransEntryId" : weTransEntryId.getValue(),
            "weTransMeasureId" : weTransMeasureId.getValue(), "weTransWeId" : weTransWeId.getValue(), "weTransTypeValueId" : weTransTypeValueId.getValue(), "operation" : operation, "isReadOnly" : isReadOnly.getValue(),
            "customTimePeriodId" : customTimePeriodId.getValue(), "titleFromWorkEffort" : "Y", "reloadRequestType" : "IndicatorSalGantt", "layoutType" : "${layoutType!}", "searchDate" : "${parameters.searchDate?if_exists?replace("&#47;", "/")}", "contentIdInd" : "SAL",
            "parentWorkEffortTypeId" : parentWorkEffortTypeIdValue, "glFiscalTypeEnumId" : glFiscalTypeEnumIdValue, "specialized" : specialized}));   	
    },
    
    onPanelDblClick : function(e) {
    	var workEffortId = '${parameters.workEffortId?if_exists}';
		new Ajax.Request("<@ofbizUrl>getCrudEnumId</@ofbizUrl>", {
			parameters: {"workEffortId": workEffortId, "folder": "WEFLD_SAL"},
			onSuccess: function(response) {
				var data = response.responseText.evalJSON(true);
				if (data) {
					if (data.crudEnumId == "" || data.crudEnumId == "ALL" || data.crudEnumId == "INSERT_UPDATE" || data.crudEnumId == "UPDATE") {
						WorkEffortTransactionSalGantt.saveWeTrans(e);
					}
				}
			},
			onFailure: function() {
				WorkEffortTransactionSalGantt.saveWeTrans(e);
			}
		});
    },
    
    saveWeTrans : function(e) {
        var td = Event.element(e);
        if(td.tagName != "TD") {
            td = td.up("td");
        }
        var isAdmin = "N";
        var isAdminField = td.down("input[name='isAdmin']");
        if (isAdminField) {
        	isAdmin = isAdminField.getValue();
        }

        if (isAdmin == "Y" || ! td.hasClassName("transaction-data-cell-readonly")) {
        	var weTransValue = 0;
        	var div1 = td.down('div');
        	if (Object.isElement(div1)) {
        		var div2 = div1.down('div');
        		if (Object.isElement(div2)) {
        			var html = div2.innerHTML;        			
        			if (html && html.trim() == "X") {
        				weTransValue = 100;
        			}
        		}
        	}
        	
        	var weTransMeasureId = td.down("input[name='weTransMeasureId']").getValue();
        	var weTransId = td.down("input[name='weTransId']").getValue(); 
        	var weTransEntryId = td.down("input[name='weTransEntryId']").getValue(); 
        	var weTransTypeValueId = td.down("input[name='weTransTypeValueId']").getValue();
        	var weTransWeId = td.down("input[name='weTransWeId']").getValue();
            var customTimePeriodId = td.down("input[name='customTimePeriodId']").getValue();
            var glFiscalTypeEnumId = td.down("input[name='glFiscalTypeEnumId']");
            var glFiscalTypeEnumIdValue = Object.isElement(glFiscalTypeEnumId) ? glFiscalTypeEnumId.getValue() : "";
            var glAccountId = td.down("input[name='glAccountId']");
            var glAccountIdValue = Object.isElement(glAccountId) ? glAccountId.getValue() : "";
            var weTransDate = td.down("input[name='weTransDate']");
            var weTransDateValue = Object.isElement(weTransDate) ? weTransDate.getValue() : "";           
            var weTransCurrencyUomId = td.down("input[name='weTransCurrencyUomId']");
            var weTransCurrencyUomIdValue = Object.isElement(weTransCurrencyUomId) ? weTransCurrencyUomId.getValue() : "";
        	
            var operation = "UPDATE";
            if (weTransId == "" || weTransId == null) {
                operation = "CREATE";
            }
        	
        	var newFormId = "panelSalForm";
        	var form = new Element("form", {"id" : newFormId, "name" : newFormId});
        	form.action = "elaborateFormForUpdateAjax";
        	form.insert(new Element("input", { "type": "hidden", "name": "weTransValue", "value": weTransValue })); 
        	form.insert(new Element("input", { "type": "hidden", "name": "weTransMeasureId", "value": weTransMeasureId})); 
        	form.insert(new Element("input", { "type": "hidden", "name": "weTransId", "value": weTransId}));
        	form.insert(new Element("input", { "type": "hidden", "name": "weTransEntryId", "value": weTransEntryId}));
        	form.insert(new Element("input", { "type": "hidden", "name": "weTransTypeValueId", "value": weTransTypeValueId}));
        	form.insert(new Element("input", { "type": "hidden", "name": "weTransWeId", "value": weTransWeId}));
        	form.insert(new Element("input", { "type": "hidden", "name": "operation", "value": operation })); 
        	form.insert(new Element("input", { "type": "hidden", "name": "weTransAccountId", "value": glAccountIdValue })); 
        	form.insert(new Element("input", { "type": "hidden", "name": "workEffortId", "value": weTransWeId })); 
        	form.insert(new Element("input", { "type": "hidden", "name": "customTimePeriodId", "value": customTimePeriodId })); 
        	form.insert(new Element("input", { "type": "hidden", "name": "weTransDate", "value": weTransDateValue }));        	        	
        	form.insert(new Element("input", { "type": "hidden", "name": "weTransCurrencyUomId", "value": weTransCurrencyUomIdValue }));        	        	
        	form.insert(new Element("input", { "type": "hidden", "name": "saveView", "value": "N" }));
        	form.insert(new Element("input", { "type": "hidden", "name": "_AUTOMATIC_PK_", "value": "Y" }));
        	form.insert(new Element("input", { "type": "hidden", "name": "defaultOrganizationPartyId", "value": "${defaultOrganizationPartyId?if_exists}" }));
        	form.insert(new Element("input", { "type": "hidden", "name": "crudService", "value": "crudServiceDefaultOrchestration_WorkEffortTransactionView" }));
        	form.insert(new Element("input", { "type": "hidden", "name": "entityName", "value": "WorkEffortTransactionView" }));
        	form.insert(new Element("input", { "type": "hidden", "name": "ajaxCall", "value": "Y" }));        	
        	form.insert(new Element("input", { "type": "hidden", "name": "isPortletFormDisabled", "value": "N" })); 
        	form.insert(new Element("input", { "type": "hidden", "name": "newInstanceMenu", "value": "N" })); 
        	form.insert(new Element("input", { "type": "hidden", "name": "ignoreToolbar", "value": "Y" }));

        	var callback = (e.memo && e.memo.callback) || Prototype.K;     	
            var options = {onComplete : function(response) {
                var data = response.responseText.evalJSON(true);
                    
                if (data["_ERROR_MESSAGE_"] != null || data["_ERROR_MESSAGE_LIST_"] != null) {
                    modal_box_messages.onAjaxLoad(data, Prototype.K);
                    return false;
                }
                if(data["failMessage"] != null) {
                    modal_box_messages.onAjaxLoad(data, Prototype.K);
                }
                             
                ajaxUpdateArea("WorkEffortMeasureIndicatorSalGanttTransactionPanel_${parameters.contentIdInd?if_exists}", "<@ofbizUrl>reloadIndicatorSalGanttTransactionPanel</@ofbizUrl>",
                        {"workEffortMeasureId" : weTransMeasureId, "reloadPanel" : "Y", "weTransId" : weTransWeId, "weTransEntryId" : weTransEntryId, "layoutType" : "${parameters.layoutType!}",
                        "searchDate" : "${parameters.searchDate?if_exists?replace("&#47;", "/")}",
                        "saveView" : "N", "${parameters.extraParam1Name?if_exists}" : "${parameters.extraParam1Value?if_exists}", "${parameters.extraParam2Name?if_exists}" : "${parameters.extraParam2Value?if_exists}",
                        "${parameters.extraParam3Name?if_exists}" : "${parameters.extraParam3Value?if_exists}", "${parameters.extraParam4Name?if_exists}" : "${parameters.extraParam4Value?if_exists}",
                        "${parameters.extraParam5Name?if_exists}" : "${parameters.extraParam5Value?if_exists}", "contentIdInd" : "${parameters.contentIdInd?if_exists}", "contentIdSecondary" : "${parameters.contentIdSecondary?if_exists}" }, 
                        {onComplete: callback});
            }};
            document.body.insert(form);
            WorkEffortTransactionSalGantt.ajaxSubmitFormUpdateAreas(form, options);  
            form.remove();
        }   	
    },
    
    ajaxSubmitFormUpdateAreas : function(form, options) {
        var params = $H($(form).serialize(true));
        if (!params.get("ajaxCall")) {
            params.set("ajaxCall", "Y");
        }
    
        options = Object.extend({
            parameters: params.toObject()}, options || {});
        new Ajax.Request($(form).action, options);
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

<#if !parameters.justRegisters?has_content>
WorkEffortTransactionSalGantt.load();
</#if>