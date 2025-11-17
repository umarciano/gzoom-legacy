/**
 * GN-CUSTOM: Validazione server-side del campo weTransValue per Performance Strategica (CTX_BS)
 * 
 * Questo script valida che il valore inserito per la Performance Strategica sia compreso tra 1 e 60.
 * Viene invocato prima del salvataggio tramite SECA (Service ECA).
 */

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

// Parametri in input
String weTransValue = parameters.weTransValue;
String weTransWeId = parameters.weTransWeId;
String weTransMeasureId = parameters.weTransMeasureId;

// Se il campo è vuoto, non validiamo (può essere un campo opzionale o gestito altrove)
if (UtilValidate.isEmpty(weTransValue)) {
    return;
}

// Verifica se siamo in contesto Performance Strategica (CTX_BS)
boolean isStrategicPerformance = false;

if (UtilValidate.isNotEmpty(weTransWeId)) {
    // Recupera il WorkEffort per determinare il tipo
    def workEffort = delegator.findOne("WorkEffort", [workEffortId: weTransWeId], false);
    
    if (workEffort != null) {
        String workEffortTypeId = workEffort.workEffortTypeId;
        
        // Determina il contesto tramite la relazione con WorkEffortPurposeType
        def purposeType = EntityUtil.getFirst(
            delegator.findList("WorkEffortPurposeType",
                EntityCondition.makeCondition("workEffortId", weTransWeId),
                null, null, null, false)
        );
        
        if (purposeType != null) {
            String weContextId = purposeType.workEffortPurposeTypeId;
            
            // CTX_BS = Performance Strategica
            if ("CTX_BS".equals(weContextId)) {
                isStrategicPerformance = true;
            }
        }
    }
}

// Se NON è Performance Strategica, non validare (usa logica standard 1-5)
if (!isStrategicPerformance) {
    return;
}

// VALIDAZIONE RANGE 1-60 per Performance Strategica
try {
    BigDecimal value = new BigDecimal(weTransValue);
    
    if (value.compareTo(BigDecimal.ONE) < 0 || value.compareTo(new BigDecimal("60")) > 0) {
        // Valore fuori range
        String errorMessage = "Il valore della Performance Strategica deve essere compreso tra 1 e 60. Valore inserito: " + weTransValue;
        
        Debug.logError("[GN-CUSTOM] " + errorMessage, "ValidateWeTransValue");
        
        // Interrompi il salvataggio con errore
        return error(errorMessage);
    }
    
    Debug.logInfo("[GN-CUSTOM] Validazione Performance Strategica OK - valore: " + value, "ValidateWeTransValue");
    
} catch (NumberFormatException e) {
    String errorMessage = "Il valore della Performance Strategica deve essere numerico. Valore inserito: " + weTransValue;
    Debug.logError("[GN-CUSTOM] " + errorMessage, "ValidateWeTransValue");
    return error(errorMessage);
}

return success();
