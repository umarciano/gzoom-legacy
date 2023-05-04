import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;
import org.ofbiz.common.FindServices;

def res = "success";
def permissionPrefix = "";
def permissionSuffix = "";
if ("CTX_AC".equals(parameters.queryCtx)) {
	permissionPrefix = "ACCOUNTINGEXT";
	permissionSuffix = "_ADMIN";
} else if ("CTX_PY".equals(parameters.queryCtx)) {
	permissionPrefix = "PARTY";
	permissionSuffix = "MGR_ADMIN";
} else {
	permissionPrefix = ContextPermissionPrefixEnum.getPermissionPrefix(parameters.queryCtx);
	permissionSuffix = "MGR_ADMIN";
}
def condList = [];
condList.add(EntityCondition.makeCondition("queryCtx", parameters.queryCtx));
condList.add(EntityCondition.makeCondition("queryType", parameters.queryType));
condList.add(EntityCondition.makeCondition("queryActive", "Y"));
if (! security.hasPermission(permissionPrefix + permissionSuffix, userLogin)) {
	condList.add(EntityCondition.makeCondition("queryPublic", "Y"));
}
def queryCode = parameters.queryCode;
if (UtilValidate.isNotEmpty(queryCode)) {
	queryCode = queryCode.toUpperCase();
	condList.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("queryCode")),
            EntityOperator.LIKE, "%" + queryCode + "%"));
}
def queryName = parameters.queryName;
if (UtilValidate.isNotEmpty(queryName)) {
	queryName = queryName.toUpperCase();
	condList.add(EntityCondition.makeCondition(EntityFunction.UPPER(EntityFieldValue.makeFieldValue("queryName")),
            EntityOperator.LIKE, "%" + queryName + "%"));
}
def orderBy = ["queryCode"];
context.listIt = delegator.findList("QueryConfigView", EntityCondition.makeCondition(condList), null, orderBy, null, false);

request.setAttribute("listIt", context.listIt);

//ricerca automatica per utenti non admin
parameters.noConditionFind = 'Y'; 
context.entityName = "QueryConfigView";
context.inputFields = parameters;
 
def prepareResult = FindServices.prepareFind(dispatcher.getDispatchContext(), context);
request.setAttribute("queryString", prepareResult.get("queryString"));
request.setAttribute("queryStringMap", prepareResult.get("queryStringMap"));

if (res == "success") {
	// check if this is massive-print-search or export-search 
    res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/checkExportSearchResult.groovy", context);
}

return res;