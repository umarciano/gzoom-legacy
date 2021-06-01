BudgetProposalTable = {

    tdId: null,
    
    load: function(newContentToExplore, useResponder) {
        //Gestione pannellino
        BudgetProposalTable.registerPanel();
        
        if (!useResponder) {
            UpdateAreaResponder.Responders.register(BudgetProposalTable.responder, "BudgetProposalTable");
        }
    },
    
    responder: {
        onLoad : function(newContent) {
            var newContent = $(newContent);
            var budgetProposalTable = newContent.up().down("table#BudgetProposalTable"); 
            if (Object.isElement(budgetProposalTable) && BudgetProposalTable.tdId != null) {
                var td = budgetProposalTable.down("td#" + BudgetProposalTable.tdId);
                if (Object.isElement(td)) {
                    td.addClassName("selected-cell");
                }
            }
        },
        unLoad : function() {
            return typeof "BudgetProposalTable" === "undefined";
        },
        unLoadCondition : function() {
            return !Object.isElement($("BudgetProposalTable"));
        }
    },
    
    registerPanel: function(panel) {
        var table = Object.isElement(panel) && panel.identify() == "BudgetProposalTable" ? $(panel) : $("BudgetProposalTable");
        if (Object.isElement(table)) {
            table.select("td").each(function(td) {
                if (Object.isElement(td)) {
                    td.observe("click", BudgetProposalTable.onPanelSelectManagement);
                }
            });
        }
    },
    
    onPanelSelectManagement: function(e) {
        var saveButton = $$("li.save")[0];
        if (Object.isElement(saveButton)) {
            saveButton.fire("dom:click", {callback: BudgetProposalTable.reloadPortlet.curry(e)});
        } else {
            BudgetProposalTable.reloadPortlet(e);
        }
    },
    
    reloadPortlet: function(e) {
        var td = Event.element(e);
        var tdId = td.identify();
        BudgetProposalTable.tdId = tdId;
        var table = td.up("table");
        var weTransId = td.down("input[name='weTransId']");
        if(Object.isElement(weTransId)) {
            var weTransEntryId = td.down("input[name='weTransEntryId']");
            var weTransMeasureId = td.down("input[name='weTransMeasureId']");
            var weTransWeId = td.down("input[name='weTransWeId']");
            var weTransTypeValueId = td.down("input[name='weTransTypeValueId']");
            var isReadOnly = td.down("input[name='isReadOnly']");
            var customTimePeriodId = td.down("input[name='customTimePeriodId']");
            var groupStatusId = td.down("input[name='groupStatusId']").getValue();
            var glFiscalTypeEnumId = td.down("input[name='glFiscalTypeEnumId']");
            var glFiscalTypeEnumIdValue = Object.isElement(glFiscalTypeEnumId) ? glFiscalTypeEnumId.getValue() : "";
            var parentWorkEffortTypeId = td.down("input[name='parentWorkEffortTypeId']");
            var parentWorkEffortTypeIdValue = Object.isElement(parentWorkEffortTypeId) ? parentWorkEffortTypeId.getValue() : "";            
            
            var operation = "UPDATE";
            if (weTransId.getValue() == "" || weTransId.getValue() == null) {
                operation = "CREATE";
                <#if firstStatus?has_content>
                    groupStatusId = "${firstStatus.statusId?if_exists}";
                </#if>
            }
            var prevSelectedCell = table.down("td.selected-cell");
            if (Object.isElement(prevSelectedCell)) {
                prevSelectedCell.removeClassName("selected-cell");
            }
            td.addClassName("selected-cell");
            ajaxUpdateArea("WorkEffortMeasureIndicatorModelPortletContainer", "<@ofbizUrl>reloadTransactionPortlet</@ofbizUrl>", $H({"weTransId" : weTransId.getValue(), "weTransEntryId" : weTransEntryId.getValue(),
                "weTransMeasureId" : weTransMeasureId.getValue(), "weTransWeId" : weTransWeId.getValue(), "weTransTypeValueId" : weTransTypeValueId.getValue(), "operation" : operation, "isReadOnly" : isReadOnly.getValue(),
                "customTimePeriodId" : customTimePeriodId.getValue(), "reloadRequestType" : "${parameters.reloadRequestType}", "saveView" : "N", "titleFromWorkEffort" : "Y",
                "extraParam1Name" : "partyId", "extraParam1Value" : "${parameters.partyId}", "extraParam2Name" : "workEffortTypeId", "extraParam2Value" : "${parameters.workEffortTypeId}", "extraParam3Name" : "finalBudget", "extraParam3Value" : "${parameters.finalBudget}",
                "extraParam4Name" : "cdcResponsibleId", "extraParam4Value" : "${parameters.cdcResponsibleId}", "extraParam5Name" : "debitCreditFlag", "extraParam5Value" : "${parameters.debitCreditFlag}", "groupStatusId" : groupStatusId,
                "parentWorkEffortTypeId" : parentWorkEffortTypeIdValue, "glFiscalTypeEnumId" : glFiscalTypeEnumIdValue, "specialized" : "Y"}));
        }
    }
}

BudgetProposalTable.load();