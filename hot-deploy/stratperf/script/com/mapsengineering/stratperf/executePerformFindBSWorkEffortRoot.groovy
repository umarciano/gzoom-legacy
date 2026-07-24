import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***BS parameters.menuItem" + parameters.menuItem);
context.permission = "BSCPERF";
parameters.weContextId = "CTX_BS";

// ORGPERF_DIR_UO: filtro stato per menu (pre-condizione: V002 seed data WEORCARD_*)
//   Definizione (WorkEffortRootViewSearchFormScreen)     -> WEORCARD_TOVALIDATE
//   Valutazione (WorkEffortRootExecViewSearchFormScreen) -> WEORCARD_ACCOUNTED
// TODO (post-WePartyInterface import): aggiungere scoping UO via context.permission="ORGPERF"
//   e WorkEffortPartyAssignment (roleTypeId=ORGDIR_UO).
String userLoginId = userLogin?.getString("userLoginId");
boolean isDirUO = false;
if (userLoginId) {
	def groups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
	isDirUO = groups?.any { it.getString("groupId") == "ORGPERF_DIR_UO" };
}
if (isDirUO) {
	String stato;
	if ("WorkEffortRootExecViewSearchFormScreen".equals(parameters.searchFormScreenName)) {
		stato = "WEORCARD_ACCOUNTED";
	} else {
		stato = "WEORCARD_TOVALIDATE";
	}
	parameters.currentStatusId_op = "contains";
	parameters.currentStatusId_value = stato;
	parameters.currentStatusContains = stato;
	parameters.remove("currentStatusId");
} else if (UtilValidate.isEmpty(parameters.currentStatusId)) {
	parameters.currentStatusId_op = "contains";
	parameters.currentStatusId_value = parameters.currentStatusContains;
	parameters.remove("currentStatusId");
}

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRoot.groovy", context);
return res;