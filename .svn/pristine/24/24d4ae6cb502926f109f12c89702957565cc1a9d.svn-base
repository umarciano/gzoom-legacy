import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.birt.util.UtilDateTime;
import com.mapsengineering.base.util.FolderLayuotTypeExtractor;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

insertMode = UtilValidate.isEmpty(context.insertMode) ? (UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode) : context.insertMode;

def accountTypeEnumId = UtilValidate.isEmpty(context.accountTypeEnumId) ? "RESOURCE" : context.accountTypeEnumId;
context.accountTypeEnumId = accountTypeEnumId;

/** Recupero layout e contentIdInd e contentIdSecondary */
def layoutType = new FolderLayuotTypeExtractor(context, parameters).getLayoutTypeFromContext();
Debug.log(" - executeChildPerformFindWEMResource.groovy layoutType " + layoutType + " context.accountTypeEnumId " + context.accountTypeEnumId + " - insertMode = " + insertMode);

/** Recupero workEffort, workEffortType, workEffortRoot, workEffortTypePeriodId */
def workEffortView = delegator.findOne("WorkEffortAndTypePeriodAndCustomTime", ["workEffortId" : parameters.workEffortId], false);

/** Recupero params */
context.accountFilter = UtilValidate.isNotEmpty(context.accountFilter) ? context.accountFilter : "ALL"; //OBJ, NOOBJ, ALL

if(UtilValidate.isEmpty(parameters.isObiettivo) || !"Y".equals(parameters.isObiettivo)) {
    WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
    paramsEvaluator.evaluateParams(context.workEffortTypeId, layoutType, false);
}


if (!"N".equals(insertMode)) {
	genericValueInsert = [:];
	// create only one row for Insert
    genericValueInsert.put("workEffortId", parameters.workEffortId);
    genericValueInsert.put("isPosted", "N");
    
    context.listIt = [genericValueInsert];
} else {
	if("INDICATOR".equals(accountTypeEnumId)) {
		/** Recupero Misure e Movimenti */
		def searchDate = ObjectType.simpleTypeConvert(parameters.searchDate, "Timestamp", null, locale);
		def workEffortIdRoot = UtilValidate.isNotEmpty(context.workEffortIdRoot) ? context.workEffortIdRoot : parameters.workEffortIdRoot;
		
		/**
		 * siccome il servizio di ricerca indicatori e usato ovunque,
		 * viene piu semplice recuperare il workEffortIdRoot dal
		 * parentId del WorkEffort, anziche aggiungerlo in tutti i punti
		 * in cui il servizio viene richiamato
		 */
		if (UtilValidate.isEmpty(workEffortIdRoot)) {
			if (UtilValidate.isNotEmpty(parameters.workEffortId)) {
				def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
				if (UtilValidate.isNotEmpty(workEffort)) {
					workEffortIdRoot = workEffort.workEffortParentId;
				}
			}
		}
		
		serviceMap = ["workEffortIdRoot": workEffortIdRoot,
		              "workEffortId": parameters.workEffortId,
		              "searchDate" : searchDate, 
		              "contentId": layoutType, 
		              "timeZone": context.timeZone];
		
		if("Y".equals(context.isObiettivo) || "Y".equals(context.showScorekpi)) {
			serviceMap.put("showScorekpi", "Y"); // TODO testare
		}
	
		Debug.log(" Run sync service executeChildPerformFindIndicator with "+ serviceMap + ", userLogin=" + context.userLogin.userLoginId);
		serviceMap.put("userLogin", context.userLogin);

		def res = dispatcher.runSync("executeChildPerformFindIndicator", serviceMap);
		if(UtilValidate.isNotEmpty(res.rowList)) {
			context.listIt = res.rowList;
		} else {
			context.listIt = [];
		}
		
	} else {
		// RESOURCE		
		FromAndThruDatesProviderFromParams fromAndThruDatesProvider = new FromAndThruDatesProviderFromParams(context, parameters, delegator, false);
		fromAndThruDatesProvider.run();
		
		def conditionList = [];
		conditionList.add(EntityCondition.makeCondition("workEffortId", parameters.workEffortId));
		if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getFromDate())) {
			conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromAndThruDatesProvider.getFromDate()));
		}
		if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getThruDate())) {
			conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromAndThruDatesProvider.getThruDate()));
		}
		
		def condition = UtilValidate.isEmpty(context.condition) ? EntityCondition.makeCondition(conditionList) : context.condition;

		def accountConditionList = [];
		accountConditionList.add(EntityCondition.makeCondition("workEffortTypeIdRes", workEffortView.workEffortTypeId));
		accountConditionList.add(EntityCondition.makeCondition("organizationPartyId", context.defaultOrganizationPartyId));
		def glAccountList = delegator.findList("GlAccountWithWorkEffortPurposeType", EntityCondition.makeCondition(accountConditionList), null, ["glAccountTypeDescription", "accountCode"], null, false);
		Debug.log("Search GlAccountWithWorkEffortPurposeType with condition " + EntityCondition.makeCondition(accountConditionList) + ", found " + glAccountList.size());
		
		if (UtilValidate.isNotEmpty(glAccountList)) {
		    def glAccountIdList = EntityUtil.getFieldListFromEntityList(glAccountList, "glAccountId", true);
		    def orderByWemList = null;
		    orderByWemList = UtilValidate.isEmpty(context.orderByWemList) ? orderByWemList : context.orderByWemList;

		    def entityNameFind = UtilValidate.isEmpty(context.entityNameFind) ? "WorkEffortMeasureGlAccountView" : context.entityNameFind;

		    def wemList = delegator.findList(entityNameFind, condition, null, orderByWemList, null, false);
			Debug.log(" Search " + entityNameFind + " with condition "+ condition + " and orderBy " + orderByWemList + ", Found " + wemList.size());
			
			def glAccountField = UtilValidate.isEmpty(context.glAccountField) ? "glAccountId" : context.glAccountField;

		    wemList = EntityUtil.filterByCondition(wemList, EntityCondition.makeCondition(glAccountField, EntityOperator.IN, glAccountIdList));
			Debug.log(" Filtered with weTransAccountId in " + glAccountIdList);
			
			context.listIt = wemList;
		} else {
			context.listIt = [];
		}
	}
}

