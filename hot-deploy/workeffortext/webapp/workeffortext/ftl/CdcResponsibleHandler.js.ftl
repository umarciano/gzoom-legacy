CdcResponsibleHandler = {
    load: function () {
        var cdcResponsible = $("BudgetProposalSearchForm_cdcResponsibleId");
        
        var cdcResponsibleDrop = DropListMgr.getDropList("BudgetProposalSearchForm_cdcResponsibleId");
        if (cdcResponsibleDrop) {
            cdcResponsibleDrop.registerOnChangeListener(CdcResponsibleHandler.cdcResponsibleChangeHandler.curry(cdcResponsible), "CdcResponsibleHandler.cdcResponsibleChangeHandler");
        }
    },
    
    cdcResponsibleChangeHandler: function(cdcResponsible) {
        var workEffortType = $("BudgetProposalSearchForm_workEffortTypeId");
        var debitCreditFlag = $("BudgetProposalSearchForm_debitCreditFlag");
        if (Object.isElement(cdcResponsible)) {
            var cdcResponsibleId = cdcResponsible.down("input.droplist_code_field").getValue();
            var workEffortTypeId = "";
            if (Object.isElement(workEffortType)) {
                workEffortTypeId = workEffortType.down("input.droplist_code_field").getValue();
            }
            var debitCreditFlagId = "";
            if (Object.isElement(debitCreditFlag)) {
                debitCreditFlagId = debitCreditFlag.options[debitCreditFlag.selectedIndex].value;
            }
            ajaxUpdateArea("content-main-section", "<@ofbizUrl>budgetProposal</@ofbizUrl>", $H({"workEffortTypeId": workEffortTypeId, "cdcResponsibleId": cdcResponsibleId,
                "weContextId": "${parameters.weContextId?if_exists}", "finalBudget": "${parameters.finalBudget?if_exists}", "debitCreditFlag": debitCreditFlagId, 
                "actionMenuLocation": "${parameters.actionMenuLocation}".unescapeHTML(), "breadcrumbs": "${parameters.breadcrumbs?if_exists}".unescapeHTML()}));
        }
    }
}

CdcResponsibleHandler.load();