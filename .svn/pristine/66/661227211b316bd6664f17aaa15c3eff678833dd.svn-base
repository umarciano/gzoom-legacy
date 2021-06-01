BudgetProposalNextStatusHandler = {
    load: function() {
        var select = $("budgetNextStatus");
        if(Object.isElement(select)) {
            Event.stopObserving(select, "change");
            select.observe("change", BudgetProposalNextStatusHandler.changeHandler);
        }
    },

    changeHandler: function(e) {
        var element = Event.element(e);
        var selectedIndex = element.selectedIndex;

        <#if (denyStatusChange?has_content && denyStatusChange == true)>

        modal_box_messages.alert("${denyStatusChangeMsg?if_exists?j_string}".replace(/\n/g, "<br/>"));
        element.selectedIndex = 0;
        return false;

        <#else>

        var denystatuschangemsg = element.options[selectedIndex].readAttribute("denystatuschangemsg");
        if (denystatuschangemsg && denystatuschangemsg.length > 0) {
            modal_box_messages.alert(denystatuschangemsg.replace(/\n/g, "<br/>"));
            element.selectedIndex = 0;
            return false;
        }

        var form = $("BPSF001_BudgetProposal");
        var workEffortTypeId = form.down("div#" + form.readAttribute("name") + "_workEffortTypeId").down("input.droplist_code_field").getValue();
        var groupStatusId = element.options[selectedIndex].value;
        var defaultOrganizationPartyId = '${defaultOrganizationPartyId?if_exists}';

            modal_box_messages.confirm("${uiLabelMap.BudgetStatusConfirm}", null, function() {
                new Ajax.Request("<@ofbizUrl>changeBudgetStatus</@ofbizUrl>", {
                    parameters: {
                        "groupStatusId": groupStatusId,
                        "workEffortTypeId": workEffortTypeId,
                        "finalBudget": "${parameters.finalBudget}",
                        "cdcResponsibleId": "${parameters.cdcResponsibleId}",
                        "partyId": "${parameters.partyId}",
                        "debitCreditFlag": "${parameters.debitCreditFlag}",
                        "defaultOrganizationPartyId": defaultOrganizationPartyId
                    },
                    onSuccess: function(transport) {
                        var data = transport.responseText.evalJSON(true);
                        
                        var transactionPortlets = $$("div.transactionPortlet");
                        for(var i = 0; i < transactionPortlets.size(); i++) {
                            transactionPortlets[i].update();
                        }

                        var reloadFunc = function() {
                            ajaxUpdateArea("WorkEffortMeasureBudgetProposalTransactionPanel_", "<@ofbizUrl>reloadBudgetProposalTransactionPanel</@ofbizUrl>", {
                                "workEffortTypeId": workEffortTypeId,
                                "finalBudget": "${parameters.finalBudget}",
                                "cdcResponsibleId": "${parameters.cdcResponsibleId}",
                                "partyId": "${parameters.partyId}",
                                "debitCreditFlag": "${parameters.debitCreditFlag}"
                            });
                        };

                        if (data._ERROR_MESSAGE_LIST_ !== undefined || data._ERROR_MESSAGE_ !== undefined) {
                            modal_box_messages.onAjaxLoad(data, reloadFunc);
                        } else {
                            reloadFunc();
                        }
                    }
                });
            });
            
        </#if>
    }
}

BudgetProposalNextStatusHandler.load();
