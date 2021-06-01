import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId": parameters.workEffortAnalysisId], false);
def permission = "";
if (UtilValidate.isNotEmpty(workEffortAnalysis)) {
	def workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : workEffortAnalysis.workEffortTypeId], false);
	if (UtilValidate.isNotEmpty(workEffortType)) {
		permission = ContextPermissionPrefixEnum.getPermissionPrefix(workEffortType.parentTypeId);
	}
}
if (UtilValidate.isEmpty(permission)) {
	permission = "WORKEFFORT";
}
def isOrgMgr = security.hasPermission(permission + "ORG_ADMIN", userLogin);
def isSup = security.hasPermission(permission + "SUP_ADMIN", userLogin);
def isRole = security.hasPermission(permission + "ROLE_ADMIN", userLogin);
def isTop = security.hasPermission(permission + "TOP_ADMIN", userLogin);

// la prima volta parameters.entityName = "WorkEffortAchieveView", quindi bisogna recuperare solo le schede di cui si e' supManager, orgManager, topManager o roleManager
// le volte successive l'entityName e' "WorkEffortAchieveViewExt", quindi l'estrazione dei workEffort e' demandata ad altri file
if("WorkEffortAchieveView".equals(parameters.entityName) && UtilValidate.isNotEmpty(workEffortAnalysis) && UtilValidate.isEmpty(workEffortAnalysis.workEffortId)) {
    if (isOrgMgr || isSup || isRole || isTop) {
    	def serviceMap = ["isOrgMgr": isOrgMgr,
    	                  "isRole": isRole,
    	                  "isSup": isSup,
    	                  "isTop": isTop,
    	                  "workEffortAnalysisId": parameters.workEffortAnalysisId,
    	                  "timeZone": context.timeZone,
    	                  "userLogin": context.userLogin];
    	def serviceName = "executeChildPerformFindWorkEffortAnalysis"
    	def serviceRes = dispatcher.runSync(serviceName, serviceMap);
        if(UtilValidate.isNotEmpty(serviceRes) && UtilValidate.isNotEmpty(serviceRes.rowList)) {    	
        	def listIt = [];
        	serviceRes.rowList.each { rowItem ->
        		if(UtilValidate.isNotEmpty(rowItem)) {   			
        			def gv = delegator.makeValue("WorkEffortAchieveView");
        			gv.putAll(rowItem);
        			listIt.add(gv);
        		}
        	}
        	context.listIt = listIt;
        } else {
        	context.listIt = [];
        }
    } else {
		changeUserLogin = false;
		if(UtilValidate.isNotEmpty(parameters.userLoginId) && parameters.userLoginId == '_NA_'){
			parameters.userLoginId = "anonymous"; //Bug 4036
			changeUserLogin = true;
		}
		
		GroovyUtil.runScriptAtLocation("com/mapsengineering/base/populateManagement.groovy", context);
		
		context.listIt = request.getAttribute("listIt");
		
		if(changeUserLogin){
			parameters.userLoginId = '_NA_';
		}
	}
}

//Debug.log(".................. workEffortAnalysis.excludeValidity " + workEffortAnalysis.excludeValidity);
//Debug.log(".................. PRIMA " + context.listIt.size());

//Bug 5051 - aggiunto filtro della data in base al canmpo excludeValidity
if(workEffortAnalysis.excludeValidity == "N") {
	if (org.ofbiz.base.util.UtilValidate.isNotEmpty(context.listIt)) {		
		def startDate = org.ofbiz.base.util.UtilDateTime.getYearStart(workEffortAnalysis.referenceDate, timeZone, locale);		
		def endDate = org.ofbiz.base.util.UtilDateTime.getYearEnd(workEffortAnalysis.referenceDate, timeZone, locale);
		
		def conditionList = [];
		conditionList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, endDate));
		conditionList.add(EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.GREATER_THAN_EQUAL_TO, startDate));		
		context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition(conditionList));		
	}
} else if (workEffortAnalysis.excludeValidity == "A") {
	if (org.ofbiz.base.util.UtilValidate.isNotEmpty(context.listIt)) {	
		def conditionList = [];
		conditionList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, workEffortAnalysis.referenceDate));
		conditionList.add(EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.GREATER_THAN_EQUAL_TO, workEffortAnalysis.referenceDate));		
		context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition(conditionList));	
	}
}

if (org.ofbiz.base.util.UtilValidate.isNotEmpty(context.listIt)) {
	def statusConditionList = [];
	statusConditionList.add(EntityCondition.makeCondition("actStEnumId", EntityOperator.NOT_EQUAL, "ACTSTATUS_REPLACED"));
	context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition(statusConditionList));
}