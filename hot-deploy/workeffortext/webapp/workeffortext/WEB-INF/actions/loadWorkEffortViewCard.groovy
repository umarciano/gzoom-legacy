/*
 * Carica il campo data_view_card per la visualizzazione della presa visione scheda
 */

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;

Debug.logInfo("=============================================", "loadWorkEffortViewCard");
Debug.logInfo("=== SCRIPT EXECUTION STARTED ===", "loadWorkEffortViewCard");
Debug.logInfo("=============================================", "loadWorkEffortViewCard");

workEffortId = parameters.workEffortId;

Debug.logInfo("*** loadWorkEffortViewCard.groovy - START - workEffortId: " + workEffortId + " ***", "loadWorkEffortViewCard");
Debug.logInfo("parameters.contentId: " + parameters.contentId, "loadWorkEffortViewCard");
Debug.logInfo("context.folderIndex: " + context.folderIndex, "loadWorkEffortViewCard");

if (workEffortId) {
    try {
        workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
        if (workEffort) {
            dataViewCard = workEffort.get("dataViewCard");
            if (dataViewCard != null) {
                context.dataViewCard = dataViewCard;
                Debug.logInfo("dataViewCard caricato: " + dataViewCard, "loadWorkEffortViewCard");
            } else {
                Debug.logInfo("dataViewCard è NULL", "loadWorkEffortViewCard");
            }
            
            // Carica lo stato corrente per determinare se la valutazione è stata condivisa
            currentStatusId = workEffort.get("currentStatusId");
            if (currentStatusId != null) {
                context.currentStatusId = currentStatusId;
                Debug.logInfo("currentStatusId: " + currentStatusId, "loadWorkEffortViewCard");
            }
        } else {
            Debug.logInfo("WorkEffort non trovato", "loadWorkEffortViewCard");
        }
    } catch (Exception e) {
        // Log dell'errore ma non blocchiamo la visualizzazione
        Debug.logError("Errore nel caricamento di dataViewCard: " + e.getMessage(), "loadWorkEffortViewCard");
    }
} else {
    Debug.logInfo("workEffortId è NULL", "loadWorkEffortViewCard");
}

Debug.logInfo("=============================================", "loadWorkEffortViewCard");
Debug.logInfo("=== SCRIPT EXECUTION COMPLETED ===", "loadWorkEffortViewCard");
Debug.logInfo("context.currentStatusId final value: " + context.currentStatusId, "loadWorkEffortViewCard");
Debug.logInfo("=============================================", "loadWorkEffortViewCard");

Debug.logInfo("*** loadWorkEffortViewCard.groovy - END ***", "loadWorkEffortViewCard");
