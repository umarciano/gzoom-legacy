import org.ofbiz.base.util.UtilValidate

// Blocco <actions> di WEMFromPanelManagementMultiForm.
// Imposta showIntermediateColumn='Y' SOLO per le schede CTX_BS, indipendentemente
// dal mix di indicatori annuali/parziali. Il valore viene ereditato dal contesto
// per-riga senza bisogno di ri-settarlo in getIndicatorScoreKpi.groovy.
// Per CTX_EP e altri contesti la variabile resta null -> colonna nascosta.

def workEffortId = parameters.workEffortId
if (UtilValidate.isEmpty(workEffortId)) return

def we = delegator.findOne("WorkEffort", [workEffortId: workEffortId], false)
if ("CTX_BS".equals(we?.workEffortTypeId)) {
    context.showIntermediateColumn = "Y"
}
