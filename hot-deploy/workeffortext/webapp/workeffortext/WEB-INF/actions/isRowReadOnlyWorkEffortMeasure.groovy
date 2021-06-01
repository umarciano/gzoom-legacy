import org.ofbiz.base.util.*;

def isRowReadOnlyWorkEffortMeasure = false;

//Controllo dell'isPosted
def isPosted = UtilValidate.isEmpty(context.isPosted) ? parameters.isPosted : context.isPosted;
//Debug.log("**************************** isRowReadOnlyWorkEffortMeasure.groovy  -> isPosted="+isPosted);

//metto tutte le condizioni in OR
isRowReadOnlyWorkEffortMeasure = isPosted == "Y" ;

//GN-1265
//solo nel caso di indicatori
if (context.accountTypeEnumId == "INDICATOR" && context.insertMode != "Y") {
    GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkWorkEffortMeasureIndicatorResponsible.groovy", context);
    isRowReadOnlyWorkEffortMeasure = isRowReadOnlyWorkEffortMeasure || context.isReadOnlyIndicatorResponsable;
}



context.isRowReadOnlyWorkEffortMeasure = isRowReadOnlyWorkEffortMeasure;
//Debug.log("**************************** isRowReadOnlyWorkEffortMeasure.groovy  -> isRowReadOnlyWorkEffortMeasure="+isRowReadOnlyWorkEffortMeasure);
