import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.util.*;


uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtUiLabels", locale)
res = "success";

changeUserLogin = false;

def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId": parameters.workEffortAnalysisId], false);

if(UtilValidate.isNotEmpty(parameters.userLoginId) && parameters.userLoginId == '_NA_'){
	parameters.userLoginId = "anonymous"; //Bug 4036
	changeUserLogin = true;
}
if("WorkEffortAchieveViewExt".equals(parameters.entityName) || UtilValidate.isNotEmpty(workEffortAnalysis) && UtilValidate.isNotEmpty(workEffortAnalysis.workEffortId)) {
	res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/populateManagement.groovy", context);
}

if(changeUserLogin){
	parameters.userLoginId = '_NA_';
	//userLogin = delegator.findOne("UserLogin", [userLoginId : parameters.userLoginId], false);
	//Debug.log("........................ userLogin ${userLogin}");
	
	/*Devo mettere in sessione i paramtri che vengo utilizzati*/
	session = request.getSession();
	javaScriptEnabled = null;
	
	result = dispatcher.runSync("getUserPreference", UtilMisc.toMap("userPrefTypeId", "javaScriptEnabled", "userPrefGroupTypeId", "GLOBAL_PREFERENCES", "userLogin", userLogin));
	if(UtilValidate.isNotEmpty(result)){
		javaScriptEnabled = result.get("userPrefValue");
	}
	session.setAttribute("javaScriptEnabled", Boolean.valueOf("Y".equals(javaScriptEnabled)));
	//session.setAttribute("userLogin", userLogin);
	session.setAttribute("userLoginId", parameters.userLoginId );
}

if("search".equals(res)) {
	request.setAttribute("_ERROR_MESSAGE_", uiLabelMap.noWorkEffortAchieveFound);
}

//Bug 4080
if(UtilValidate.isNotEmpty(workEffortAnalysis.workEffortId)) {
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId": workEffortAnalysis.workEffortId], false);
	request.setAttribute("entityName", "WorkEffortAchieveViewExt");
	request.setAttribute("workEffortId", workEffortAnalysis.workEffortId);
	request.setAttribute("workEffortTypeId",  workEffortAnalysis.workEffortTypeId);
	request.setAttribute("contextManagement", "Y");
	request.setAttribute("parentFormNotAllowed", "Y");
	request.setAttribute("useFolder", "Y");
	request.setAttribute("headerEntityName", "WorkEffortAchieveView");
	request.setAttribute("operationalEntityName", "WorkEffortAchieveView");
	request.setAttribute("parentEntityName", "WorkEffortAchieveView");
	request.setAttribute("workEffortIdFrom", workEffortAnalysis.workEffortId);
	request.setAttribute("workEffortAnalysisId", workEffortAnalysis.workEffortAnalysisId);
	request.setAttribute("transactionDate", workEffortAnalysis.referenceDate);
	request.setAttribute("userLoginId", parameters.userLoginId);
	request.setAttribute("breadcrumbsCurrentItem", workEffort.workEffortName);
	
	if(!changeUserLogin) {
		res = "workEffortAchieveExtManagementContainerOnly";
	}
	else {
		res ="managementContainerOnlyNotAuthRedirect"
	}
}


return res;