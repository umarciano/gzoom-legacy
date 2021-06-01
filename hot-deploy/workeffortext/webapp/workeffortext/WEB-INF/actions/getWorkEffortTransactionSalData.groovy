import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;
import javolution.util.FastMap;

def transactionPanelMap = [:];
def assocWeightSum = 0.0d;
def glFiscalTypeList = [];

//Non e il parent della gerarchia che si vede nell'albero!!!!
def parentWorkEffortId = UtilValidate.isNotEmpty(context.workEffortId) ? context.workEffortId : parameters.workEffortId;
//Non e valorizzato solo per il layout di default, per quello annuale ci pensa getAncestorWorkEffortType.groovy
if(UtilValidate.isEmpty(parentWorkEffortId) && UtilValidate.isNotEmpty(parameters.workEffortMeasureId)) {
	//serve per quando salvo la portlet
	def meas = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : parameters.workEffortMeasureId], false);
	def childWork = delegator.findOne("WorkEffort", ["workEffortId" : meas.workEffortId], false);
	parentWorkEffortId = childWork.workEffortParentId;
}

def workEffort = delegator.findOne("WorkEffort", ["workEffortId": parentWorkEffortId], false);
def workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId": workEffort.workEffortTypeId], false);
def hierarchyAssocTypeId = workEffortType.hierarchyAssocTypeId;
def parentWorkEffortTypeId = workEffortType.workEffortTypeId;
def calculateFooterValues = UtilValidate.isNotEmpty(workEffortType) && workEffortType.weightSons != 0 ? "Y" : "N"; 
def periodTypeId = periodTypeId = workEffortType.periodTypeId;

if(UtilValidate.isEmpty(hierarchyAssocTypeId) && UtilValidate.isNotEmpty(workEffort.workEffortParentId)) {
	def rootWorkEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffort.workEffortParentId], false);
	def rootWorkEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId": rootWorkEffort.workEffortTypeId], false);
	hierarchyAssocTypeId = rootWorkEffortType.hierarchyAssocTypeId;
	parentWorkEffortTypeId = rootWorkEffortType.workEffortTypeId;
	calculateFooterValues = UtilValidate.isNotEmpty(rootWorkEffortType) && rootWorkEffortType.weightSons != 0 ? "Y" : "N";
}

def periodConditionList = [];
def assocConditionList = [];


if(!"Y".equals(context.annualSal)) {
	periodConditionList.add(EntityCondition.makeCondition("periodTypeId", workEffortType.periodTypeId));
}
else {
	//Prendo l'indicatore SAL di uno dei FIGLI
	def childs = delegator.findList("WorkEffortAssocView", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortIdFrom", parentWorkEffortId), EntityCondition.makeCondition("workEffortAssocTypeId", hierarchyAssocTypeId)), null, null, null, true);
	def firstChild = EntityUtil.getFirst(childs);
	def workEffortMeasure = EntityUtil.getFirst(delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", firstChild.workEffortIdTo), EntityCondition.makeCondition("glAccountId", EntityOperator.LIKE, "SAL%")), null, ["workEffortMeasureId"], null, false));
	if(UtilValidate.isNotEmpty(workEffortMeasure)) {
		def glAccount = delegator.findOne("GlAccount", ["glAccountId": workEffortMeasure.glAccountId], false);
		periodConditionList.add(EntityCondition.makeCondition("periodTypeId", glAccount.periodTypeId));
	}
	else {
		periodConditionList.add(EntityCondition.makeCondition("periodTypeId", workEffortType.periodTypeId));
	}
	
}
if(UtilValidate.isNotEmpty(parameters.searchDate)) {
	FromAndThruDatesProviderFromParams fromAndThruDatesProvider = new FromAndThruDatesProviderFromParams(context, parameters, delegator, false);
	fromAndThruDatesProvider.run();
	
	if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getFromDate())) {
		periodConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromAndThruDatesProvider.getFromDate()));
		assocConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromAndThruDatesProvider.getFromDate()));
	}
	if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getThruDate())) {
		periodConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromAndThruDatesProvider.getThruDate()));
		assocConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromAndThruDatesProvider.getThruDate()));
	}
}
else {
	periodConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, workEffort.estimatedCompletionDate));
	periodConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, workEffort.estimatedStartDate));
}
Debug.log(" - Search CustomTimePeriod with condition " + EntityCondition.makeCondition(periodConditionList));
def periodList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(periodConditionList), null, ["fromDate"], null, true);

assocConditionList.add(EntityCondition.makeCondition("workEffortIdFrom", parentWorkEffortId));
assocConditionList.add(EntityCondition.makeCondition("workEffortAssocTypeId", hierarchyAssocTypeId));
Debug.log(" - Search WorkEffortAssocExtView with condition " + EntityCondition.makeCondition(assocConditionList));

def workEffortChildList = delegator.findList("WorkEffortAssocExtAndWeTypeCntAndPeriodToView", EntityCondition.makeCondition(assocConditionList), null, ["sequenceNum", "wrToCode", "workEffortIdTo"], null, false);

for(workEffortChild in workEffortChildList) {
	transactionPanelMap[workEffortChild.workEffortIdTo] = [];
	
	//5214 punto 3
	def workEffortMeasure = EntityUtil.getFirst(delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", workEffortChild.workEffortIdTo), EntityCondition.makeCondition("glAccountId", EntityOperator.LIKE, "SAL%")), null, ["workEffortMeasureId"], null, false));
	if(UtilValidate.isNotEmpty(workEffortMeasure) && glFiscalTypeList.size() == 0) {
		def glAcc = delegator.getRelatedOne("GlAccount", workEffortMeasure);
		glFiscalTypeList = delegator.findList("GlAccountTypeGlFiscalTypeView", EntityCondition.makeCondition("glAccountTypeId", glAcc.glAccountTypeId), null, ["sequenceId"], null, true);
	}
	
	for(period in periodList) {
		for(glFiscalType in glFiscalTypeList) {
			def periodMap = period.getAllFields();
			periodMap.glFiscalTypeId = glFiscalType.glFiscalTypeId;
			periodMap.glFiscalTypeEnumId = glFiscalType.glFiscalTypeEnumId;
			periodMap.glFiscalTypeDescription = glFiscalType.description;
			periodMap.weTransWeId = workEffortChild.workEffortIdTo;
			periodMap.isReadOnly = "N";
			periodMap.weTransTypeValueId = glFiscalType.glFiscalTypeId;
			def hasShowActualDatesParam = getHasShowActualDatesParam(workEffortChild);
			periodMap.hasShowActualDates = getHasShowActualDates(workEffortChild, hasShowActualDatesParam);
			periodMap.hasPeriodDates = getHasPeriodDates(workEffortChild, hasShowActualDatesParam);
			if ("Y".equals(periodMap.hasPeriodDates)) {
				periodMap.actualThruDate = getActualThruDate(workEffortChild);
			}
			
			if(UtilValidate.isNotEmpty(workEffortMeasure)) {
				periodMap.weTransMeasureId = workEffortMeasure.workEffortMeasureId;
				periodMap.glAccountId = workEffortMeasure.glAccountId;
				
				def glAccMeasure = delegator.getRelatedOne("GlAccount", workEffortMeasure);
				if(UtilValidate.isNotEmpty(glAccMeasure)) {
					periodMap.weTransCurrencyUomId = glAccMeasure.defaultUomId;
				}
			}
			transactionPanelMap[workEffortChild.workEffortIdTo].add(periodMap);
		}
	}
	assocWeightSum += workEffortChild.assocWeight;
}

def cumulator = [:];
def workEffortIdIterator =  transactionPanelMap.keySet().iterator();

def isAdmin = "N";
def specialized = parameters.specialized;
def adminPermission = getAdminPermission(parentWorkEffortTypeId, specialized);
if (security.hasPermission(adminPermission, context.userLogin)) {
	isAdmin = "Y";
} else {
	isAdmin = "N";
}

while(workEffortIdIterator.hasNext()) {
	def workEffortId = workEffortIdIterator.next();
	
	def workEffortChild = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false);
	
	def workEffortMeasure = EntityUtil.getFirst(delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", workEffortId), EntityCondition.makeCondition("glAccountId", EntityOperator.LIKE, "SAL%")), null, ["workEffortMeasureId"], null, false));
	if(UtilValidate.isNotEmpty(workEffortMeasure)) {
		Debug.log("workEffortId" + workEffortId);
		def valueIndicList = delegator.findList("WorkEffortTransactionViewGantt", EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", workEffortId), EntityCondition.makeCondition("workEffortMeasureId", workEffortMeasure.workEffortMeasureId)), null, ["transactionDate"], null, false);
		def lastPeriod = null;
		cumulator[workEffortId] = [:];
		for(cell in transactionPanelMap[workEffortId]) {
			
			def workEffortTypeCnt = delegator.findOne("WorkEffortTypeStatusCnt", ["workEffortTypeId": workEffortChild.workEffortTypeId, "statusId": workEffortChild.currentStatusId, "contentId": "WEFLD_AIND"], false);
			
			for(valueIndic in valueIndicList) {
				if(valueIndic.glFiscalTypeId.equals(cell.glFiscalTypeId) && valueIndic.transactionDate.equals(cell.thruDate)) {
					if(!"RATING_SCALE".equals(valueIndic.uomTypeId)) {
						cell.weTransValue = valueIndic.amount;
					}
					else {
						cell.weTransValue = valueIndic.uomCode;
					}
					cell.isReadOnly = "Y".equals(valueIndic.isPosted) ? "Y" : "N";
					cell.weTransId = valueIndic.acctgTransId;
					cell.weTransEntryId = valueIndic.acctgTransEntrySeqId;
					cell.hasComments = ((UtilValidate.isNotEmpty(valueIndic.weTransComments) && UtilValidate.isNotEmpty(valueIndic.weTransComments.trim()))
						|| (UtilValidate.isNotEmpty(valueIndic.weTransComment) && UtilValidate.isNotEmpty(valueIndic.weTransComment.trim()))
						|| (UtilValidate.isNotEmpty(valueIndic.weTransCommentsLang) && UtilValidate.isNotEmpty(valueIndic.weTransCommentsLang.trim()))
						|| (UtilValidate.isNotEmpty(valueIndic.weTransCommentLang) && UtilValidate.isNotEmpty(valueIndic.weTransCommentLang.trim()))) ? "Y" : "N";
					
					if("Y".equals(calculateFooterValues) && !"RATING_SCALE".equals(valueIndic.uomTypeId)) {
						def assconCondList = [];
						assconCondList.add(EntityCondition.makeCondition("workEffortIdFrom", parentWorkEffortId));
						assconCondList.add(EntityCondition.makeCondition("workEffortIdTo", cell.weTransWeId));
						assconCondList.add(EntityCondition.makeCondition("workEffortAssocTypeId", hierarchyAssocTypeId));
						def assocList = EntityUtil.filterByCondition(workEffortChildList, EntityCondition.makeCondition(assconCondList));
						
						cumulator[workEffortId][cell.glFiscalTypeId + cell.customTimePeriodId] = EntityUtil.getFirst(assocList).assocWeight * cell.weTransValue;
					}
				}
			}
			
			def cellPeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId": cell.customTimePeriodId], false);
			def timeConditionList = [];
			timeConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, cellPeriod.fromDate));
			timeConditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, cellPeriod.thruDate));
			timeConditionList.add(EntityCondition.makeCondition("periodTypeId", periodTypeId));
			def cellPeriodList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(timeConditionList), null, null, null, true);
			// Debug.log("Found " + cellPeriodList.size() + " CustomTimePeriod with condition = " + EntityCondition.makeCondition(timeConditionList));
			
			def prilConditionList = [];
			prilConditionList.add(EntityCondition.makeCondition("workEffortTypeId", parentWorkEffortTypeId));
			prilConditionList.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, EntityUtil.getFieldListFromEntityList(cellPeriodList, "customTimePeriodId", false)));
			prilConditionList.add(EntityCondition.makeCondition("glFiscalTypeEnumId", cell.glFiscalTypeEnumId));
			prilConditionList.add(EntityCondition.makeCondition("organizationId", context.defaultOrganizationPartyId));
			def prilList = delegator.findList("WorkEffortTypePeriod", EntityCondition.makeCondition(prilConditionList), null, null, null, false);
			// Debug.log(" - Search WorkEffortTypePeriod with condition " + EntityCondition.makeCondition(prilConditionList) + ", found " + prilList.size());
			
			def prilStatusSet = UtilMisc.toSet("OPEN", "REOPEN", "DETECTABLE");
			def pril = EntityUtil.getFirst(prilList);
			if(UtilValidate.isNotEmpty(pril)) {
				def isRil = prilStatusSet.contains(pril.statusEnumId) ? "Y" : "N";
				cell.isReadOnly = "Y".equals(cell.isReadOnly) || "N".equals(isRil) ? "Y" : "N";
			}
			
			if("Y".equals(calculateFooterValues)) {
				def currentPeriod = EntityUtil.getFirst(EntityUtil.filterByCondition(periodList, EntityCondition.makeCondition("customTimePeriodId", cell.customTimePeriodId)));
				lastPeriod = periodList[periodList.indexOf(currentPeriod) - 1].customTimePeriodId;
				if(!cumulator[workEffortId].containsKey(cell.glFiscalTypeId + cell.customTimePeriodId) && lastPeriod != null) {
					cumulator[workEffortId][cell.glFiscalTypeId + cell.customTimePeriodId] = cumulator[workEffortId][cell.glFiscalTypeId + lastPeriod];
				}
			}
			
			if(UtilValidate.isNotEmpty(workEffortTypeCnt)) {
				if("NONE".equals(workEffortTypeCnt.crudEnumId) || ("UPDATE".equals(workEffortTypeCnt.crudEnumId) && UtilValidate.isEmpty(cell.weTransId))) {
					cell.isReadOnly = "Y";
				}
			}
			if(context.isReadOnly == true || "Y".equals(context.isReadOnly) || "true".equals(parameters.isReadOnly) || "Y".equals(parameters.isReadOnly)) {
		        cell.isReadOnly = "Y";
		    }
			
			cell.isAdmin = isAdmin;
		}
	}
}

def footerMap = [:];

if("Y".equals(calculateFooterValues)) {
	for(glFiscalType in glFiscalTypeList) {
		for(i = 0; i < periodList.size(); i++) {
			def period = periodList[i];
			footerMap[glFiscalType.glFiscalTypeId + period.customTimePeriodId] = 0.0d;
			for(workEffortId in cumulator.keySet()) {
				if(UtilValidate.isNotEmpty(cumulator[workEffortId][glFiscalType.glFiscalTypeId + period.customTimePeriodId])) {
					footerMap[glFiscalType.glFiscalTypeId + period.customTimePeriodId] += cumulator[workEffortId][glFiscalType.glFiscalTypeId + period.customTimePeriodId] / assocWeightSum;
				}
			}
		}
	}
}

def getHasShowActualDatesParam(workEffortChild) {
	def wrToWeParamsMap = FastMap.newInstance();
	BshUtil.eval(workEffortChild.wrToWeParams, wrToWeParamsMap);
	return "Y".equals(wrToWeParamsMap.showActualDates);
}


def getHasShowActualDates(workEffortChild, hasShowActualDatesParam) {
	def hasShowActualDates = "N";
	
	if (hasShowActualDatesParam) {
		hasShowActualDates = UtilValidate.isNotEmpty(workEffortChild.wrToActualFromDate) && UtilValidate.isNotEmpty(workEffortChild.wrToActualThruDate) ? "Y" : "N";
	}	
	return hasShowActualDates;
}

def getHasPeriodDates(workEffortChild, hasShowActualDatesParam) {
	def hasPeriodDates = "N";
	
	if (hasShowActualDatesParam) {
		if (UtilValidate.isNotEmpty(workEffortChild.wrToActualFromDate) && UtilValidate.isEmpty(workEffortChild.wrToActualThruDate)) {
			if (UtilValidate.isNotEmpty(workEffortChild.workEffortTypePeriodId) && UtilValidate.isNotEmpty(workEffortChild.customTimePeriodId)) {
				hasPeriodDates = "GLFISCTYPE_ACTUAL".equals(workEffortChild.glFiscalTypeEnumId) || "GLFISCTYPE_TARGET".equals(workEffortChild.glFiscalTypeEnumId) ? "Y" : "N";
			}
		}
	}
	
	return hasPeriodDates;
}

def getActualThruDate(workEffortChild) {
	return "GLFISCTYPE_ACTUAL".equals(workEffortChild.glFiscalTypeEnumId) ? workEffortChild.perThruDate : UtilDateTime.addDaysToTimestamp(workEffortChild.perFromDate, -1);
}

def getAdminPermission(parentWorkEffortTypeId, specialized) {
	if ("Y".equals(specialized) && UtilValidate.isNotEmpty(parentWorkEffortTypeId)) {
		def parentWorkEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : parentWorkEffortTypeId], false);
		if (UtilValidate.isNotEmpty(parentWorkEffortType)) {
			def weContextId = parentWorkEffortType.parentTypeId;
			return ContextPermissionPrefixEnum.getPermissionPrefix(weContextId) + "MGR_ADMIN";
		}
	}
	return "WORKEFFORTMGR_ADMIN";
}

context.periodList = periodList;
context.glFiscalTypeList = glFiscalTypeList;
context.transactionPanelMap = transactionPanelMap;
context.workEffortChildList = workEffortChildList;
context.calculateFooterValues = calculateFooterValues;
context.assocWeightSum = assocWeightSum;
context.footerMap = footerMap;
context.parentWorkEffortTypeId = parentWorkEffortTypeId;
