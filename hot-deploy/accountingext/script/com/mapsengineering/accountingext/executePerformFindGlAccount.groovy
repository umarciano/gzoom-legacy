import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
 * Funzione usata per filtrare la lista 
 */
def filterListIt(listAllKey){
	
	def listIt = request.getAttribute("listIt");
	def listKey = [];
	if (UtilValidate.isNotEmpty(listAllKey)) {
		for (GenericValue value: listAllKey) {
			listKey.add(value.glAccountId);
		}
	}
	def listReturn = [];
	if (UtilValidate.isNotEmpty(listIt) && listKey.size() > 0) {
		for (GenericValue value: listIt) {
			def map = [:];
			map.putAll(value);
			if (UtilValidate.isNotEmpty(value.glAccountId)) {
				if (listKey.contains(value.glAccountId)) {
					listReturn.add(map);
				}
			}
		}
	}
	request.setAttribute("listIt", listReturn);
}

def filterListItByRefId(listAllKey){
	
	def listIt = request.getAttribute("listIt");
	def listKey = [];
	if (UtilValidate.isNotEmpty(listAllKey)) {
		for (GenericValue value: listAllKey) {
			listKey.add(value.glAccountIdRef);
		}
	}
	def listReturn = [];
	if (UtilValidate.isNotEmpty(listIt) && listKey.size() > 0) {
		for (GenericValue value: listIt) {
			def map = [:];
			map.putAll(value);
			if (UtilValidate.isNotEmpty(value.glAccountId)) {
				if (listKey.contains(value.glAccountId)) {
					listReturn.add(map);
				}
			}
		}
	}
	request.setAttribute("listIt", listReturn);
}


def unionListAndfilterList(list1, list2, list3, list4){
	
	def listIt = request.getAttribute("listIt");
	def listKey1 = [];
	if (UtilValidate.isNotEmpty(list1)) {
		for (GenericValue value: list1) {
			listKey1.add(value.glAccountId);
		}
	}
	def listKey2 = [];
	if (UtilValidate.isNotEmpty(list2)) {
		for (GenericValue value: list2) {
			listKey2.add(value.glAccountId);
		}
	}
	def listKey3 = [];
	if (UtilValidate.isNotEmpty(list3)) {
		for (GenericValue value: list3) {
			listKey3.add(value.glAccountId);
		}
	}
	def listKey4 = [];
	if (UtilValidate.isNotEmpty(list4)) {
		for (GenericValue value: list4) {
			listKey4.add(value.glAccountId);
		}
	}
	
	def listReturn = [];
	if (UtilValidate.isNotEmpty(listIt) && (listKey1.size() > 0 || listKey2.size() > 0 || listKey3.size() > 0 || listKey4.size() > 0)) {
		for (GenericValue value: listIt) {
			def map = [:];
			map.putAll(value);
			if (UtilValidate.isNotEmpty(value.glAccountId)) {
				if (listKey1.contains(value.glAccountId) || listKey2.contains(value.glAccountId) || listKey3.contains(value.glAccountId) || listKey4.contains(value.glAccountId)) {
					listReturn.add(map);
				}
			}
		}
	}	
	request.setAttribute("listIt", listReturn);	
}

/**
 * Il product contenuto in glAccount viene sbiancato perchè deve essere messo in or con workEffortMeasure
 */
productId = null;
if(UtilValidate.isNotEmpty(parameters.productId)){
	productId = parameters.productId;
	parameters.productId = null;
}


/**
 * GN-208 - filtro per la riservatezza
 */
if(parameters.accountTypeEnumId == 'INDICATOR'){
	parameters.entityName = 'GlAccountReservedOrganizationView';
}

parameters.orderBy = "accountCode";

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/executePerformFind.groovy", context);

//Caso di ricerca per isGlAccountIdRef = Y filtra solo se il glAccount trovati sono parametri di calcolo per qualcuno
if(UtilValidate.isNotEmpty(parameters.isGlAccountIdRef)){
	glAccountInputCalcList = delegator.findList("GlAccountInputCalc", null, null, null, null, false);
	filterListItByRefId(glAccountInputCalcList);
}

// GN-101
//La ricerca per ruolo o soggetto su GlAccount(respCenterId, respRoleTypeId), GlAccountRole, WorkEffortMeasureRole, WorkEffortMeasure in OR
if(UtilValidate.isNotEmpty(parameters.partyId) || UtilValidate.isNotEmpty(parameters.roleTypeId)){
	
	def mapResp = [:];
	def map = [:];
	if(UtilValidate.isNotEmpty(parameters.partyId)){
		mapResp.put("respCenterId", parameters.partyId);
		map.put("partyId", parameters.partyId);
		}
	if(UtilValidate.isNotEmpty(parameters.roleTypeId)){
		mapResp.put("respCenterRoleTypeId", parameters.roleTypeId);
		map.put("roleTypeId", parameters.roleTypeId);
	}
	
	glAccountRoleList = delegator.findByAnd("GlAccountRole", map);
	
	glAccountList = delegator.findByAnd("GlAccount", mapResp);
	
	workEffortMeasureRoleList = delegator.findByAnd("WorkEffortMeasureAndRole", map);
	
	workEffortMeasureRespList = delegator.findByAnd("WorkEffortMeasure", map);
	
	unionListAndfilterList(workEffortMeasureRoleList, glAccountRoleList, glAccountList, workEffortMeasureRespList);
}


//Caso di ricerca per workEffortPurposeTypeId
if(UtilValidate.isNotEmpty(parameters.workEffortPurposeTypeId)){
	workEffortPurposeAccountList = delegator.findByAnd("WorkEffortPurposeAccount", [workEffortPurposeTypeId : parameters.workEffortPurposeTypeId]);
	filterListIt(workEffortPurposeAccountList);	
}

/*Caso di ricerca per productId ci sono 2 casi
* 1. glAccount.productId
* 2. workEffortMeasure.productId && workEffort = null
**/
if(UtilValidate.isNotEmpty(productId)){
	
	glAccountList = delegator.findByAnd("GlAccount", [productId : productId]);
	workEffortMeasureList = delegator.findByAnd("WorkEffortMeasure", [productId : productId]);
	
	unionListAndfilterList(glAccountList, workEffortMeasureList, null, null);
}

/***
 * Caso di ricera per finalita
 */
if(UtilValidate.isNotEmpty(parameters.workEffortPurposeTypeId)){
	workEffortPurposeAccountList = delegator.findByAnd("WorkEffortPurposeAccount", [workEffortPurposeTypeId : parameters.workEffortPurposeTypeId]);
	filterListIt(workEffortPurposeAccountList);	
}

request.setAttribute("preLoadListIt", "Y");

return res;