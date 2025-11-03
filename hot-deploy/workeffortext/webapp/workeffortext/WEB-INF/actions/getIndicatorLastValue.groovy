import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
 * Script per recuperare l'ultimo valore di un indicatore dalla tabella AcctgTransEntry
 * Questo script viene eseguito nelle row-actions del form WorkEffortMeasure
 * per popolare il campo indicatorValue da mostrare nella tabella degli indicatori.
 */

def glAccountId = context.glAccountId;
def workEffortId = context.workEffortId;
def workEffortMeasureId = context.workEffortMeasureId;

// Inizializzo il valore dell'indicatore a null
context.indicatorValue = null;

// Verifico che ci siano i parametri necessari
if (UtilValidate.isEmpty(glAccountId)) {
    Debug.logWarning("getIndicatorLastValue.groovy: glAccountId mancante per workEffortMeasureId=${workEffortMeasureId}", "getIndicatorLastValue");
    return;
}

try {
    // DEBUG: Log dei parametri di ricerca
    Debug.logInfo("getIndicatorLastValue.groovy: Cerco transazioni per glAccountId=${glAccountId}, workEffortId=${workEffortId}, workEffortMeasureId=${workEffortMeasureId}", "getIndicatorLastValue");
    
    // Cerco le transazioni collegate al glAccountId dell'indicatore
    // Join tra AcctgTransEntry e AcctgTrans
    // NOTA: NON filtriamo per isPosted per vedere anche le transazioni non ancora postate (come fa il detail panel)
    def condList = [
        EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId)
    ];
    
    // Se c'è un workEffortId, filtro anche per quello
    if (UtilValidate.isNotEmpty(workEffortId)) {
        condList.add(EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId));
    }
    
    def conditions = EntityCondition.makeCondition(condList, EntityOperator.AND);
    
    // Ordino per data transazione decrescente per ottenere l'ultimo valore
    def orderBy = ["transactionDate DESC"];
    
    // Cerco nella view AcctgTransAndEntries (che collega AcctgTrans con AcctgTransEntry)
    def transactionList = delegator.findList("AcctgTransAndEntries", conditions, null, orderBy, null, false);
    
    Debug.logInfo("getIndicatorLastValue.groovy: Trovate ${transactionList?.size() ?: 0} transazioni", "getIndicatorLastValue");
    
    if (UtilValidate.isNotEmpty(transactionList)) {
        // Prendo il primo record (il più recente)
        def lastTransaction = transactionList.get(0);
        def amount = lastTransaction.amount;
        
        Debug.logInfo("getIndicatorLastValue.groovy: Prima transazione trovata - acctgTransId=${lastTransaction.acctgTransId}, amount=${amount}, date=${lastTransaction.transactionDate}", "getIndicatorLastValue");
        
        if (UtilValidate.isNotEmpty(amount)) {
            // Converto il valore in intero (senza decimali)
            context.indicatorValue = amount.intValue();
            
            Debug.logInfo("getIndicatorLastValue.groovy: Valore recuperato per workEffortMeasureId=${workEffortMeasureId}, glAccountId=${glAccountId}: ${context.indicatorValue}", "getIndicatorLastValue");
        }
    } else {
        Debug.logInfo("getIndicatorLastValue.groovy: Nessuna transazione trovata per workEffortMeasureId=${workEffortMeasureId}, glAccountId=${glAccountId}", "getIndicatorLastValue");
    }
    
} catch (Exception e) {
    Debug.logError(e, "getIndicatorLastValue.groovy: Errore nel recupero del valore dell'indicatore per workEffortMeasureId=${workEffortMeasureId}: " + e.getMessage(), "getIndicatorLastValue");
}
