/*
 * Carica il campo data_view_card per la visualizzazione della presa visione scheda
 */

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.base.util.UtilMisc;

workEffortId = parameters.workEffortId;

if (workEffortId) {
    try {
        workEffort = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
        if (workEffort) {
            dataViewCard = workEffort.get("dataViewCard");
            if (dataViewCard != null) {
                context.dataViewCard = dataViewCard;
            }
        }
    } catch (Exception e) {
        // Log dell'errore ma non blocchiamo la visualizzazione
        Debug.logError("Errore nel caricamento di dataViewCard: " + e.getMessage(), "loadWorkEffortViewCard");
    }
}
