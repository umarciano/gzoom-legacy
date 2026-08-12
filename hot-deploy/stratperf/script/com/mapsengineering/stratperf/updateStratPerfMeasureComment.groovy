import org.ofbiz.base.util.UtilValidate;

def currentUserLoginId = userLogin?.getString("userLoginId");
def userGroups = currentUserLoginId ? delegator.findByAnd("UserLoginSecurityGroup", [userLoginId: currentUserLoginId])*.getString("groupId") : [];
def allowedUser = "admin" == currentUserLoginId || userGroups.contains("STRATPERF_DIR_SAN") || userGroups.contains("STRATPERF_DIR_AMM");

if (!allowedUser || UtilValidate.isEmpty(parameters.workEffortMeasureId)) {
    request.setAttribute("_ERROR_MESSAGE_", "Utente non autorizzato o misura non specificata.");
    return "error";
}

def measure = delegator.findOne("WorkEffortMeasure", [workEffortMeasureId: parameters.workEffortMeasureId], false);
def workEffort = measure ? delegator.findOne("WorkEffort", [workEffortId: measure.workEffortId], false) : null;
if (!measure || !workEffort || workEffort.workEffortTypeId != "CTX_BS") {
    request.setAttribute("_ERROR_MESSAGE_", "La misura non appartiene a una scheda di Performance Strategica.");
    return "error";
}

measure.comments = parameters.comments ?: "";
delegator.store(measure);
return "success";