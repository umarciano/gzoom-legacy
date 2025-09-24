import org.ofbiz.base.util.*;

def isRowReadOnlyWorkEffortMeasure = false;

// Check global portal read-only mode first
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkPortalReadOnlyMode.groovy", context);

//Controllo dell'isPosted
def isPosted = UtilValidate.isEmpty(context.isPosted) ? parameters.isPosted : context.isPosted;
//Controllo del forceReadOnly (per NOPORTAL_MY)
def forceReadOnly = UtilValidate.isNotEmpty(context.forceReadOnly) ? context.forceReadOnly : parameters.forceReadOnly;
Debug.log("**************************** isRowReadOnlyWorkEffortMeasure.groovy  -> isPosted="+isPosted);
Debug.log("**************************** isRowReadOnlyWorkEffortMeasure.groovy  -> forceReadOnly="+forceReadOnly);

//metto tutte le condizioni in OR
isRowReadOnlyWorkEffortMeasure = isPosted == "Y" || "Y".equals(forceReadOnly);

//GN-1265
//solo nel caso di indicatori
if (context.accountTypeEnumId == "INDICATOR" && context.insertMode != "Y") {
    GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkWorkEffortMeasureIndicatorResponsible.groovy", context);
    isRowReadOnlyWorkEffortMeasure = isRowReadOnlyWorkEffortMeasure || context.isReadOnlyIndicatorResponsable;
}



context.isRowReadOnlyWorkEffortMeasure = isRowReadOnlyWorkEffortMeasure;
Debug.log("**************************** isRowReadOnlyWorkEffortMeasure.groovy  -> isRowReadOnlyWorkEffortMeasure="+isRowReadOnlyWorkEffortMeasure);
