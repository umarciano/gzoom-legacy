import java.util.Map;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;

Debug.log("******************** STARTED SERVICE writeTransactionToImport ******************");

def dispatcher = dctx.getDispatcher();

def accountCode = context.accountCode;
def workEffortMeasureId = context.workEffortMeasureId;
def refDate = context.refDate;
def actualValue = UtilValidate.isNotEmpty(context.actualValue) ? new Double(context.actualValue) : null;
def budgetValue = UtilValidate.isNotEmpty(context.budgetValue) ? new Double(context.budgetValue) : null;
def actualPyValue = UtilValidate.isNotEmpty(context.actualPyValue) ? new Double(context.actualPyValue) : null;
def scoreKpi = UtilValidate.isNotEmpty(context.scoreKpi) ? new Double(context.scoreKpi) : null;

/*Debug.log("*** accountCode = " + accountCode);
Debug.log("*** refDate = " + refDate);
Debug.log("*** actualValue = " + actualValue);
Debug.log("*** budgetValue = " + budgetValue);
Debug.log("*** actualPyValue = " + actualPyValue);
Debug.log("*** scoreKpi = " + scoreKpi);
Debug.log("*** workEffortMeasureId = " + workEffortMeasureId); */

def uiLabelMap = UtilProperties.getResourceBundleMap("WorkeffortExtUiLabels", locale)

def result = uiLabelMap.UpdateTransactionFromImport_ok;

def wetList = [];

//Aggiunto io
if (UtilValidate.isEmpty(actualValue) && UtilValidate.isEmpty(budgetValue) && UtilValidate.isEmpty(actualPyValue) && UtilValidate.isEmpty(scoreKpi)) {
	def res = ServiceUtil.returnFailure();
	res.result = uiLabelMap.UpdateTransactionFromImport_missingValues;
	Debug.log("Error: " + res.result);
	return res;
}

if (UtilValidate.isEmpty(workEffortMeasureId)) {
	def res = ServiceUtil.returnFailure();
	res.result = uiLabelMap.UpdateTransactionFromImport_missingWorkEffortMeasure;
	Debug.log("Error: " + res.result);
	return res;
}

def workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : workEffortMeasureId], false);
if (UtilValidate.isEmpty(workEffortMeasure)) {
	def res = ServiceUtil.returnFailure();
	res.result = uiLabelMap.UpdateTransactionFromImport_workEffortMeasureNotPresent;
	Debug.log("Error: " + res.result);
	return res;
}

if (UtilValidate.isEmpty(accountCode)) {
	def res = ServiceUtil.returnFailure();
	res.result = uiLabelMap.UpdateTransactionFromImport_missingAccountCode;
	Debug.log("Error: " + res.result);
	return res;
}

def glAccountList = delegator.findList("GlAccount", EntityCondition.makeCondition("accountCode", accountCode), null, null, null, false);
def glAccount = EntityUtil.getFirst(glAccountList);
if (UtilValidate.isEmpty(glAccount) || !glAccount.glAccountId.equals(workEffortMeasure.glAccountId)) {
	def res = ServiceUtil.returnFailure();
	res.result = uiLabelMap.UpdateTransactionFromImport_glAccountIdIncongruent;
	Debug.log("Error: " + res.result);
	return res;
}

def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortMeasure.workEffortId], false);

def isResource = false;

//Provo nelle Risorse
def glAccontConditionList = [];
glAccontConditionList.add(EntityCondition.makeCondition("workEffortTypeIdRes", workEffort.workEffortTypeId));
glAccontConditionList.add(EntityCondition.makeCondition("glAccountId", glAccount.glAccountId));
glAccontConditionList.add(EntityCondition.makeCondition("organizationPartyId", context.defaultOrganizationPartyId));
def glAccountPurposeTypeList = delegator.findList("GlAccountWithWorkEffortPurposeType", EntityCondition.makeCondition(glAccontConditionList), null, null, null, false);
if (UtilValidate.isNotEmpty(glAccountPurposeTypeList)) {
	isResource = true;
	def transConditionList = [];
	transConditionList.add(EntityCondition.makeCondition("weTransMeasureId", workEffortMeasureId));
	transConditionList.add(EntityCondition.makeCondition("weTransAccountId", glAccount.glAccountId));
	transConditionList.add(EntityCondition.makeCondition("weTransDate", refDate));
	wetList = delegator.findList("WorkEffortTransactionSimplifiedView", EntityCondition.makeCondition(transConditionList), null, null, null, false);
	
	//Debug.log("*** Trovati Valore Risorsa per Misura: " + workEffortMeasureId + " -> " + wetList.size());
}
else {
	//Provo Indicatori
	glAccontConditionList = [];
	glAccontConditionList.add(EntityCondition.makeCondition("workEffortTypeIdInd", workEffort.workEffortTypeId));
	glAccontConditionList.add(EntityCondition.makeCondition("glAccountId", glAccount.glAccountId));
	glAccontConditionList.add(EntityCondition.makeCondition("organizationPartyId", context.defaultOrganizationPartyId));
	glAccountPurposeTypeList = delegator.findList("GlAccountWithWorkEffortPurposeType", EntityCondition.makeCondition(glAccontConditionList), null, null, null, false);
	if (UtilValidate.isNotEmpty(glAccountPurposeTypeList)) {
		def transConditionList = [];
		transConditionList.add(EntityCondition.makeCondition("weTransMeasureId", workEffortMeasureId));
		transConditionList.add(EntityCondition.makeCondition("weTransAccountId", glAccount.glAccountId));
		transConditionList.add(EntityCondition.makeCondition("weTransDate", refDate));
		//transConditionList.add(EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.EQUALS_FIELD, "weTransAccountId"));
		wetList = delegator.findList("WorkEffortTransactionIndicatorView", EntityCondition.makeCondition(transConditionList), null, null, null, false);
		
		//Debug.log("*** Trovati Valore Indicatore per Misura: " + workEffortMeasureId + " -> " + wetList.size());
	}
}

//Aggiorno i valori gia esistenti
def it = wetList.iterator();
while(it.hasNext()) {
	def wet = it.next();
	def updateData = false;
	
/*	if ("SCOREKPI".equals(wet.weAcctgTransAccountId)) {
		if ("WESCORE_NOCALC".equals(glAccount.weScoreRangeEnumId)) {
			Debug.log("**** Aggiorno SCOREKPI nuovo valore: " + scoreKpi);
			wet.weTransValue = scoreKpi;
		}
		
	} */
	if (!"SCOREKPI".equals(wet.weAcctgTransAccountId) && !"SCORE".equals(wet.weAcctgTransAccountId)) {
		if ("ACTUAL".equals(wet.weTransTypeValueId) && UtilValidate.isNotEmpty(actualValue) && !actualValue.equals(wet.weTransValue)) {
			//Debug.log("**** Aggiorno ACTUAL nuovo valore: " + actualValue);
			wet.weTransValue = actualValue;
			updateData = true;
		}
		if ("BUDGET".equals(wet.weTransTypeValueId) && UtilValidate.isNotEmpty(budgetValue) && !budgetValue.equals(wet.weTransValue)) {
			//Debug.log("**** Aggiorno BUDGET nuovo valore: " + budgetValue);
			wet.weTransValue = budgetValue;
			updateData = true;
		}
		if ("ACTUAL_PY".equals(wet.weTransTypeValueId) && UtilValidate.isNotEmpty(actualPyValue) && !actualPyValue.equals(wet.weTransValue)) {
			//Debug.log("**** Aggiorno ACTUAL_PY nuovo valore: " + actualPyValue);
			wet.weTransValue = actualPyValue;
			updateData = true;
		}
	}
	
	if (updateData) {
		//Debug.log("*** Aggiorno indicatore " +  wet);
		def updateMap = [:];
		updateMap.entityName = "WorkEffortTransactionView";
		updateMap.userLogin = userLogin;
		updateMap.operation = "UPDATE";
		updateMap.parameters = wet.getAllFields();
		updateMap.parameters.defaultOrganizationPartyId = context.defaultOrganizationPartyId;
		updateMap.parameters.operation = "UPDATE";
		
		def updateRes = dispatcher.runSync("crudServiceOperation_WorkEffortTransactionView", updateMap);
		if(UtilValidate.isNotEmpty(updateRes.errorMessageList)) {
			result = updateRes.errorMessageList[0];
		}
	}
}

//Aggiorno SCOREKPI
def scoreKpiConditionList = [];
scoreKpiConditionList.add(EntityCondition.makeCondition("transactionDate", refDate));
scoreKpiConditionList.add(EntityCondition.makeCondition("workEffortMeasureId", workEffortMeasureId));
def scoreKpiList = delegator.findList("WorkEffortMeasureScoreKpi", EntityCondition.makeCondition(scoreKpiConditionList), null, null, null, false);
//if (UtilValidate.isNotEmpty(scoreKpiList) && UtilValidate.isNotEmpty(scoreKpi) && "WESCORE_NOCALC".equals(glAccount.weScoreRangeEnumId)) {
if (UtilValidate.isNotEmpty(scoreKpiList) && UtilValidate.isNotEmpty(scoreKpi)) {
	//Debug.log("*** Aggiorno SCOREKPI per misura " + workEffortMeasureId);
	def scoreKpiItem = EntityUtil.getFirst(scoreKpiList);
	def updateScoreKpiMap = [:];
	updateScoreKpiMap.acctgTransId = scoreKpiItem.acctgTransId;
	updateScoreKpiMap.acctgTransEntrySeqId = scoreKpiItem.acctgTransEntrySeqId;
	updateScoreKpiMap.amount = scoreKpi;
	updateScoreKpiMap.userLogin = userLogin;
	
	def updateRes = dispatcher.runSync("updateWeTrans_fixedGlAccount", updateScoreKpiMap);
	if(UtilValidate.isNotEmpty(updateRes.errorMessageList)) {
		result = updateRes.errorMessageList[0];
	}

}

def createActual = false;
def createActualPy = false;
def createBudget = false;
//CREATE
if (UtilValidate.isNotEmpty(actualValue)) {
	def actualConditionList = [];
	actualConditionList.add(EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.NOT_EQUAL, "SCOREKPI"));
	actualConditionList.add(EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.NOT_EQUAL, "SCORE"));
	actualConditionList.add(EntityCondition.makeCondition("weTransTypeValueId", "ACTUAL"));
	def actualList = EntityUtil.filterByCondition(wetList, EntityCondition.makeCondition(actualConditionList));
	if (UtilValidate.isEmpty(actualList)) {
		createActual = true;
	}
}

if (UtilValidate.isNotEmpty(budgetValue)) {
	def budgetConditionList = [];
	budgetConditionList.add(EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.NOT_EQUAL, "SCOREKPI"));
	budgetConditionList.add(EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.NOT_EQUAL, "SCORE"));
	budgetConditionList.add(EntityCondition.makeCondition("weTransTypeValueId", "BUDGET"));
	def budgetList = EntityUtil.filterByCondition(wetList, EntityCondition.makeCondition(budgetConditionList));
	if (UtilValidate.isEmpty(budgetList)) {
		createBudget = true ;
	}
}

if (UtilValidate.isNotEmpty(actualPyValue)) {
	def actualPyConditionList = [];
	actualPyConditionList.add(EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.NOT_EQUAL, "SCOREKPI"));
	actualPyConditionList.add(EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.NOT_EQUAL, "SCORE"));
	actualPyConditionList.add(EntityCondition.makeCondition("weTransTypeValueId", "ACTUAL_PY"));
	def actualPyList = EntityUtil.filterByCondition(wetList, EntityCondition.makeCondition(actualPyConditionList));
	if (UtilValidate.isEmpty(actualPyList)) {
		createActualPy = true ;
	}
}

//Debug.log("*** createActual=" + createActual + " createBudget=" + createBudget + " createActualPy=" + createActualPy);
while (createActual || createBudget || createActualPy) {
	def createMap = [:];
	createMap.entityName = "WorkEffortTransactionView";
	createMap.userLogin = userLogin;
	createMap.operation = "CREATE";
	createMap.parameters = [:];
	createMap.parameters.weTransWeId = workEffort.workEffortId;
	createMap.parameters.weTransMeasureId = workEffortMeasureId;
	createMap.parameters.weTransAccountId = glAccount.glAccountId;
	def cList = [];
	cList.add(EntityCondition.makeCondition("weMeasureWeId", workEffort.workEffortId));
	cList.add(EntityCondition.makeCondition("weMeasureId", workEffortMeasureId));
	if (isResource) {
		cList.add(EntityCondition.makeCondition("workEffortTypeIdRes", workEffort.workEffortTypeId));
	}
	else {
		cList.add(EntityCondition.makeCondition("workEffortTypeIdInd", workEffort.workEffortTypeId));
	}
	def measurePurposeList = delegator.findList("WorkEffortMeasureViewWithWorkEffortPurposeType", EntityCondition.makeCondition(cList), null, null, null, false);
	if (UtilValidate.isNotEmpty(measurePurposeList)) {
		def mp = EntityUtil.getFirst(measurePurposeList);
		createMap.parameters.weTransCurrencyUomId = mp.weMeasureDefaultUomId;
	}
	def weTransTypeValueId = "";
	def value = 0d;
	if (createActual) {
		//Debug.log("*** CREAZIONE ACTUAL misura: " + workEffortMeasureId + " valore: " + actualValue);
		weTransTypeValueId = "ACTUAL";
		value = actualValue;
		createActual = false
	}
	else {
		if (createBudget) {
			//Debug.log("*** CREAZIONE BUDGET misura: " + workEffortMeasureId + " valore: " + budgetValue);
			weTransTypeValueId = "BUDGET";
			value = budgetValue;
			createBudget = false;
		}
		else {
			if (createActualPy) {
				//Debug.log("*** CREAZIONE ACTUAL_PY misura: " + workEffortMeasureId + " valore: " + actualPyValue);
				weTransTypeValueId = "ACTUAL_PY";
				value = actualPyValue;
				createActualPy = false;
			}
		}
	}
	createMap.parameters.weTransTypeValueId = weTransTypeValueId;
	createMap.parameters.weTransDate = refDate;
	createMap.parameters.isPosted = "N";
	createMap.parameters.weTransValue = value;
	createMap.parameters.defaultOrganizationPartyId = context.defaultOrganizationPartyId;
	createMap.parameters.operation = "CREATE";
	def createRes = dispatcher.runSync("crudServiceOperation_WorkEffortTransactionView", createMap);
	if(UtilValidate.isNotEmpty(createRes.errorMessageList)) {
		result = createRes.errorMessageList[0];
	}
}

//SCOREKPI
//if (UtilValidate.isEmpty(scoreKpiList) && UtilValidate.isNotEmpty(scoreKpi) && "WESCORE_NOCALC".equals(glAccount.weScoreRangeEnumId)) {
if (UtilValidate.isEmpty(scoreKpiList) && UtilValidate.isNotEmpty(scoreKpi)) {
	
	//Debug.log("*** CREAZIONE SCOREKPI misura: " + workEffortMeasureId + " valore: " + scoreKpi);
	def kpiMap = [:];
	kpiMap.workEffortMeasureId = workEffortMeasureId;
	kpiMap.glAccountId = glAccount.glAccountId;
	kpiMap.transValue = scoreKpi;
	kpiMap.transDate = refDate;
	kpiMap.acctgTransTypeId = "SCOREKPI";
	kpiMap.userLogin = userLogin;
	def createRes = dispatcher.runSync("createWeTrans_fixedGlAccount", kpiMap);
	if(UtilValidate.isNotEmpty(createRes.errorMessageList)) {
		result = createRes.errorMessageList[0];
	}
}

def res = ServiceUtil.returnSuccess();
res.result = result;

Debug.log("******************** END SERVICE writeTransactionToImport ******************");
return res;