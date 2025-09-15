import org.ofbiz.base.util.*;
import org.ofbiz.base.util.Debug;

// Imposta la variabile isInsertMode basata sui parametri
def insertModeParam = UtilValidate.isNotEmpty(context.insertMode) ? context.insertMode : parameters.insertMode;
def isInsertMode = false;

Debug.logInfo("DEBUG setInsertModeFlag: insertModeParam = " + insertModeParam, "setInsertModeFlag.groovy");
Debug.logInfo("DEBUG setInsertModeFlag: context.insertMode = " + context.insertMode, "setInsertModeFlag.groovy");
Debug.logInfo("DEBUG setInsertModeFlag: parameters.insertMode = " + parameters.insertMode, "setInsertModeFlag.groovy");

// Debug sui form correnti
Debug.logInfo("DEBUG setInsertModeFlag: context.keys = " + context.keySet(), "setInsertModeFlag.groovy");
Debug.logInfo("DEBUG setInsertModeFlag: parameters.keys = " + parameters.keySet(), "setInsertModeFlag.groovy");

if (insertModeParam instanceof Boolean) {
    isInsertMode = insertModeParam;
} else if (insertModeParam instanceof String) {
    isInsertMode = "Y".equalsIgnoreCase(insertModeParam) || "true".equalsIgnoreCase(insertModeParam);
}

// Se non è esplicitamente definito, controlla se è una nuova entità
if (!isInsertMode) {
    def workEffortId = UtilValidate.isNotEmpty(context.workEffortId) ? context.workEffortId : parameters.workEffortId;
    if (UtilValidate.isEmpty(workEffortId)) {
        isInsertMode = true;
    }
}

// Per sicurezza, se la variabile è ancora null, impostala a false
if (isInsertMode == null) {
    isInsertMode = false;
}

Debug.logInfo("DEBUG setInsertModeFlag: final isInsertMode = " + isInsertMode, "setInsertModeFlag.groovy");

context.isInsertMode = isInsertMode;
