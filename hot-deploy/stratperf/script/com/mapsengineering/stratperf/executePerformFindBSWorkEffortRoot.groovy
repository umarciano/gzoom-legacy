import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

context.permission = "BSCPERF";
parameters.weContextId = "CTX_BS";

boolean isValutazione = "WorkEffortRootExecViewSearchFormScreen".equals(parameters.searchFormScreenName);

// Stati visibili — uguali per tutti i ruoli; l'unica differenza tra ruoli è l'orgUnitId (scoping UO).
// Valutazione: ciclo consuntivazione (TOACC_INT..REVIEWED).
// Definizione: stati pre-consuntivazione (INIT..VALIDATED).
String allStati;
if (isValutazione) {
	allStati = "WEORCARD_TOACC_INT,WEORCARD_ACC_INT,WEORCARD_TOACCOUNT,WEORCARD_ACCOUNTED,WEORCARD_REVIEWED";
} else {
	allStati = "WEORCARD_INIT,WEORCARD_TOVALIDATE,WEORCARD_TOCLRFY_DUO,WEORCARD_VALPART,WEORCARD_TOCLRFY_DSA,WEORCARD_VALIDATED";
}
if (UtilValidate.isEmpty(parameters.currentStatusId)) {
	parameters.currentStatusId_op = "contains";
	parameters.currentStatusId_value = allStati;
	parameters.currentStatusContains = allStati;
	parameters.remove("currentStatusId");
}

GroovyUtil.runScriptAtLocation("com/mapsengineering/stratperf/applyScopingBSFilter.groovy", context);

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRoot.groovy", context);
return res;
