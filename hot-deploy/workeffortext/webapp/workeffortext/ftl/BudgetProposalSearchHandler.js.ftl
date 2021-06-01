BudgetProposalSearchHandler = {
    load: function() {
        var searchButton = $$("li.search")[0];
        if(Object.isElement(searchButton)) {
            searchButton.observe("click", BudgetProposalSearchHandler.clickListener);
        }
        var importStdButton = $$("li.import-std")[0];
        if (Object.isElement(importStdButton)) {
        	importStdButton.observe("click", BudgetProposalSearchHandler.clickImportStdListener);
        }
        var printButton = $$("li.print")[0];
        if (Object.isElement(printButton)) {
        	printButton.observe("click", BudgetProposalSearchHandler.clickPrintListener);
        }
    },
    
    clickListener: function(event) {
        var element = Event.element(event);
        var workEffortTypeIdDrop = $("BudgetProposalSearchForm_workEffortTypeId");
        var cdcHandler = $("BudgetProposalSearchForm_cdcHandler");
        var debitCreditFlag = $("BudgetProposalSearchForm_debitCreditFlag");
        if(Object.isElement(workEffortTypeIdDrop) && Object.isElement(cdcHandler) && Object.isElement(debitCreditFlag)) {
            var workEffortTypeId = workEffortTypeIdDrop.down("input.droplist_code_field").getValue();
            var partyId = cdcHandler.down("input.droplist_code_field").getValue();
            var debitCreditFlagId = debitCreditFlag.options[debitCreditFlag.selectedIndex].value;
            if(workEffortTypeId == "" || partyId == "" || debitCreditFlagId == "") {
                modal_box_messages.alert("${uiLabelMap.BaseMessageSearchDataMandatoryField}");
            }
            else {
                ajaxUpdateArea("WorkEffortMeasureBudgetProposalTransactionPanel_", "<@ofbizUrl>reloadBudgetProposalTransactionPanel</@ofbizUrl>", $H({"partyId" : partyId, "workEffortTypeId" : workEffortTypeId,
                    "finalBudget" : "${parameters.finalBudget?if_exists}", "cdcResponsibleId" : "${parameters.cdcResponsibleId?if_exists}", "debitCreditFlag" : debitCreditFlagId}));
                $("WorkEffortMeasureIndicatorModelPortletContainer").update()
            }
        }
    },

    clickImportStdListener: function(event) {
        var element = Event.element(event);
        var workEffortTypeIdDrop = $("BudgetProposalSearchForm_workEffortTypeId");
        var cdcHandler = $("BudgetProposalSearchForm_cdcHandler");
        var debitCreditFlag = $("BudgetProposalSearchForm_debitCreditFlag");
        if (Object.isElement(workEffortTypeIdDrop) && Object.isElement(cdcHandler) && Object.isElement(debitCreditFlag)) {
            var workEffortTypeId = workEffortTypeIdDrop.down("input.droplist_code_field").getValue();
            var partyId = cdcHandler.down("input.droplist_code_field").getValue();
            var debitCreditFlagId = debitCreditFlag.options[debitCreditFlag.selectedIndex].value;
            if (workEffortTypeId == "" || partyId == "" || debitCreditFlagId == "") {
                modal_box_messages.alert("${uiLabelMap.BaseMessageSearchDataMandatoryField}");
            } else {
                modal_box_messages.confirm("${uiLabelMap.BaseMessageConfirm}", null, function() {
                    BudgetProposalSearchHandler._callImportStd();
                });
            }
        }
    },

	clickPrintListener: function(event) {
        var element = Event.element(event);
        var workEffortTypeIdDrop = $("BudgetProposalSearchForm_workEffortTypeId");
        var cdcHandler = $("BudgetProposalSearchForm_cdcHandler");
        var debitCreditFlag = $("BudgetProposalSearchForm_debitCreditFlag");
        if(Object.isElement(workEffortTypeIdDrop) && Object.isElement(cdcHandler) && Object.isElement(debitCreditFlag)) {
            var workEffortTypeId = workEffortTypeIdDrop.down("input.droplist_code_field").getValue();
            var partyId = cdcHandler.down("input.droplist_code_field").getValue();
            if(workEffortTypeId == "" || partyId == "" ) {
                modal_box_messages.alert("${uiLabelMap.BaseMessagePrintDataMandatoryField}");
            }
            else {
            	var modulo = 'workeffortext';
            	if('${parameters.weContextId}' == 'CTX_OR'){
            		 modulo = 'orgperf';
            	} else if ('${parameters.weContextId}' == 'CTX_BS'){
            		modulo = 'stratperf';
            	}
                          
                Utils.showModalBox('/'+modulo+'/control/popupWorkEffortLoadPrintBirt?repContextContentId=WE_PRINT_BILANCIO&saveView=N', {title: '${uiLabelMap.BaseSelectPrint}', width: 450, height: 250, 'afterHideModal': function(parametersToSubmit, validateFunction) {
                    parametersToSubmit = $H(parametersToSubmit);
                    if (Object.isHash(parametersToSubmit) && $A(parametersToSubmit.keys()).size() > 0) {
                        this.parametersToSubmit = parametersToSubmit;
                        if (Object.isFunction(validateFunction)) {
                            var params = $H(parametersToSubmit);
                            params.set('workEffortTypeId', workEffortTypeId);
                            params.set('saveView', 'N');
                            params.set('partyId', partyId);
                            params.set('finalBudget', '${parameters.finalBudget?if_exists}');
                            
                            PrintPopupMgr.validate(params, function(params) {
                                var queryString = params.toQueryString();
                                window.open('/'+modulo+'/control/managementPrintBirt?' + queryString, '_blank');
                            }.curry(params));
                        }
                    }
                }});
            }
        }
    },

    _callImportStd: function() {
        new Ajax.Request('<@ofbizUrl>executeStandardImportGlAccount</@ofbizUrl>', {
            parameters: $("BPSF001_BudgetProposal").serialize(true),
            onSuccess: function(transport) {
                var data = transport.responseText.evalJSON(true);
                var jobLogId = data.jobLogId;
                if(jobLogId) {
                    modal_box_messages.alert("${uiLabelMap.StandardImport_finished}<br><br>${uiLabelMap.StandardImport_recordElaborated}" + data.recordElaborated + "<br><br>${uiLabelMap.StandardImport_blockingErrors}" + data.blockingErrors + "<br><br>${uiLabelMap.StandardImport_jobLogId}" + " " + jobLogId);
                    
                    ajaxUpdateArea("WorkEffortMeasureBudgetProposalTransactionPanel_", "<@ofbizUrl>reloadBudgetProposalTransactionPanel</@ofbizUrl>", $H({"partyId" : partyId, "workEffortTypeId" : workEffortTypeId,
                        "finalBudget" : "${parameters.finalBudget?if_exists}", "cdcResponsibleId" : "${parameters.cdcResponsibleId?if_exists}", "debitCreditFlag" : debitCreditFlagId}));
                    $("WorkEffortMeasureIndicatorModelPortletContainer").update()
                }
            }
        });
    },
}

BudgetProposalSearchHandler.load();
