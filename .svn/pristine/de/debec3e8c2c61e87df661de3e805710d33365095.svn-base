import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.widgets.*;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

def workEffortAnalysis = delegator.findOne("WorkEffortAnalysis", ["workEffortAnalysisId" : parameters.workEffortAnalysisId], false);

def weContext = "";
if (UtilValidate.isNotEmpty(workEffortAnalysis)) {
	def workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId": workEffortAnalysis.workEffortTypeId], false);
	if (UtilValidate.isNotEmpty(workEffortType)) {
		weContext = workEffortType.parentTypeId;
	}
}
context.weContext = weContext;

def permission = getPermission();
def orgUser = false;
def roleUser = false;
def supUser = false;
def topUser = false;

def userLoginPermissionViewAdmin = checkPermission(permission + "VIEW_ADMIN");
if (UtilValidate.isEmpty(userLoginPermissionViewAdmin)) {	
	orgUser = isOrgUser(permission);
	roleUser = isRoleUser(permission);
	supUser = isSupUser(permission);
	topUser = isTopUser(permission);
}

//
//Creo le immagini dei tachimetri e le associo ai relativi workEffortId nella lista
//
workEffortAchieveList = [];
if (UtilValidate.isNotEmpty(context.listIt)) {

	/** Prendo il campo typeBalanceScoreTarId e typeBalanceScoreConId per calcolo dell'immagine */
	def valueItemMap = [:];
	def valueItemList = [];
	for (GenericValue item: context.listIt) {
		def workEffortId = item.workEffortId;
		
		def cumulator = valueItemMap[workEffortId];
		if (UtilValidate.isEmpty(cumulator)) {
			cumulator = item.getAllFields();
			cumulator.canUpdateRoot = canUpdateRoot(orgUser, roleUser, supUser, topUser, item.workEffortParentId);
			valueItemMap[workEffortId] = cumulator;
			valueItemList.add(cumulator);
		}
		
		if (UtilValidate.isNotEmpty(item.amount)) {
			if (UtilValidate.isNotEmpty(workEffortAnalysis) && workEffortAnalysis.typeBalanceScoreTarId.equals(item.glFiscalTypeId)) {
				if (UtilValidate.isEmpty(cumulator.targetAmount) || item.amount.compareTo(cumulator.targetAmount) > 0) {
					cumulator.targetAmount = item.amount;
				}
			} else if (UtilValidate.isNotEmpty(workEffortAnalysis) && workEffortAnalysis.typeBalanceScoreConId.equals(item.glFiscalTypeId)) {
				if (UtilValidate.isEmpty(cumulator.actualAmount) || item.amount.compareTo(cumulator.actualAmount) > 0) {
					cumulator.actualAmount = item.amount;
				}
			}
		}
		
		if (UtilValidate.isNotEmpty(item.hasScoreAlert) && UtilValidate.isNotEmpty(workEffortAnalysis) && workEffortAnalysis.typeBalanceScoreConId.equals(item.glFiscalTypeId)) {
			if (UtilValidate.isEmpty(cumulator.hasAlert) || item.hasScoreAlert.compareTo(cumulator.hasAlert) > 0) {
				cumulator.hasAlert = item.hasScoreAlert;
			}
		}
	}
	
	//
	//Url per caricare immagini
	//
	def srvRootUrl = request.getAttribute("_SERVER_ROOT_URL_");
	
	//
	//Path immagini locale al modulo
	//
	def contextImagesPath = "images/tmp";
	
	def budget = UtilValidate.isNotEmpty(workEffortAnalysis) && workEffortAnalysis.typeBalanceScoreTarId ? workEffortAnalysis.typeBalanceScoreTarId : "BUDGET";
	def actual = UtilValidate.isNotEmpty(workEffortAnalysis) && workEffortAnalysis.typeBalanceScoreConId ? workEffortAnalysis.typeBalanceScoreConId : "ACTUAL";
	
	def series = [budget: UtilProperties.getMessage("WorkeffortExtUiLabels", "WorkEffortAchieveViewProgrammed", locale), actual : UtilProperties.getMessage("WorkeffortExtUiLabels", "WorkEffortAchieveViewRealized", locale)];
	def titles = [:];
	def values = [:];
	
	//Serve per il radar per inserire nel titolo etch al posto di workEffortName
	def titleEtch = UtilValidate.isNotEmpty(parameters.titleEtch) ? parameters.titleEtch : false;
	
	valueItemList.each{ fieldsMap ->
		titles[fieldsMap.workEffortId] = titleEtch ?  fieldsMap.etch : fieldsMap.workEffortName;
		
		def targetAmount = fieldsMap.targetAmount;
		if (UtilValidate.isEmpty(targetAmount)) {
			targetAmount = Double.valueOf(100.0d);
		}
		
		values[fieldsMap.workEffortId] = [budget : targetAmount, actual : fieldsMap.actualAmount];
		
		fieldsMap.imageValue = fieldsMap.actualAmount;
		fieldsMap.budgetValue = fieldsMap.targetAmount;
	}
	workEffortAchieveList = valueItemList;
	
	def src = ValutationWidgets.buildAndSaveSpider(request, contextImagesPath, series, titles, values, 550, 550);
	
	if (UtilValidate.isNotEmpty(src)) {
		//
		//Aggiungo alla mappa dei campi i campi con il sorgente dell'immagine ed il valore
		//
		context.put("imageSrc", src);
	}
}

context.workEffortAchieveList = workEffortAchieveList;

//NOTA BENE: inserisco la lista in sessione per poterla utilizzare negli script del report
session.setAttribute("workEffortAchieveList", workEffortAchieveList);

def checkPermission(permission) {
    if (security.hasPermission(permission, context.userLogin))
        return permission;
    return null;
}

def getPermission() {
	return ContextPermissionPrefixEnum.getPermissionPrefix(context.weContext);
}

def isOrgUser(permission) {
	def userLoginPermissionOrg = checkPermission(permission + "ORG_ADMIN");
	return UtilValidate.isNotEmpty(userLoginPermissionOrg);
}

def isRoleUser(permission) {
	def userLoginPermissionRole = checkPermission(permission + "ROLE_ADMIN");
	return UtilValidate.isNotEmpty(userLoginPermissionRole);
}

def isSupUser(permission) {
	def userLoginPermissionSup = checkPermission(permission + "SUP_ADMIN");
	return UtilValidate.isNotEmpty(userLoginPermissionSup);
}

def isTopUser(permission) {
	def userLoginPermissionTop = checkPermission(permission + "TOP_ADMIN");
	return UtilValidate.isNotEmpty(userLoginPermissionTop);
}

def canUpdateRoot(orgUser, roleUser, supUser, topUser, workEffortId) {
	def canUpdateRoot = "N";
	if (UtilValidate.isNotEmpty(context.userLogin)) {
		def rootViewList = [];
		if (orgUser || roleUser || supUser || topUser) {
			rootViewList = getRootViewList(orgUser, roleUser, supUser, topUser, workEffortId);
		} else {		
			def rootViewConditions = [];
			def rootViewFieldsToSelect = UtilMisc.toSet("workEffortId", "uvUserLoginId");
        
			rootViewConditions.add(EntityCondition.makeCondition("workEffortId", workEffortId));
			rootViewConditions.add(EntityCondition.makeCondition("uvUserLoginId", context.userLogin.userLoginId));
			rootViewList = delegator.findList("WorkEffortRootView", EntityCondition.makeCondition(rootViewConditions), rootViewFieldsToSelect, null, null, false);
		}
        
        if (UtilValidate.isNotEmpty(rootViewList)) {
        	canUpdateRoot = "Y";
        }
	}
	
	return canUpdateRoot;
}

//se sono org, role o sup cerco la scheda con il servizio di ricerca apposito
def getRootViewList(orgUser, roleUser, supUser, topUser, workEffortId) {
	def rootViewList = [];	
	def rootSearchServiceMap = ["isOrgMgr": orgUser,
	              "isRole": roleUser,
	              "isSup": supUser,
	              "isTop": topUser,
	              "workEffortId": workEffortId,
	              "weContextId": parameters.weContextId_value,
	              "timeZone": context.timeZone,
	              "userLogin": context.userLogin];
	
    def rootSearchServiceRes = dispatcher.runSync("executeChildPerformFindWorkEffortRoot", rootSearchServiceMap);
    if(UtilValidate.isNotEmpty(rootSearchServiceRes) && UtilValidate.isNotEmpty(rootSearchServiceRes.rowList)) {   	
    	rootSearchServiceRes.rowList.each { rowItem ->
    		if(UtilValidate.isNotEmpty(rowItem)) {   			
    			def gv = delegator.makeValue("WorkEffortRootOrgMgrRoleView");
    			gv.putAll(rowItem);
    			rootViewList.add(gv);
    		}
    	}
    }
    return rootViewList;
}
