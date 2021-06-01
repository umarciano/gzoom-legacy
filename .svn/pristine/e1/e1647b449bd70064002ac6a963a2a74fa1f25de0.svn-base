import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.security.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityUtil;

def result = ServiceUtil.returnSuccess();
def dispatcher = dctx.getDispatcher();

if (UtilValidate.isEmpty(context.security)) {
	context.security = SecurityFactory.getInstance(delegator);
}

// Replico condizioni utilizzate nella form e nello screen che legano userLoginId e cdcHandler
def cdrHandlerCode = "";
def cdcHandlerCode = "";

Debug.log(" - parameters.cdcHandler = " + parameters.cdcHandler);
if (UtilValidate.isNotEmpty(parameters.cdcHandler)) {

    // Ottiene il codice del Centro di Responsabilita'

    def condList = EntityCondition.makeCondition(
        EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLogin.userLoginId),
        EntityCondition.makeCondition("respCenterId", EntityOperator.EQUALS, parameters.cdcResponsibleId)
    );
	def cdcResponsible = EntityUtil.getFirst(delegator.findList("CdcResponsibleView", condList, null, null, null, false));
	// Debug.log(" - cdcResponsible = " + cdcResponsible);
	if (UtilValidate.isNotEmpty(cdcResponsible)) {
	    def partyParentRole;

	    condList = EntityCondition.makeCondition(
        	EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.cdcResponsibleId),
        	EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ORGANIZATION_UNIT")
    	);
		partyParentRole = EntityUtil.getFirst(delegator.findList("PartyParentRole", condList, null, null, null, false));
		// Debug.log(" - CDR partyParentRole = " + partyParentRole);
		if (UtilValidate.isNotEmpty(partyParentRole)) {
		    cdrHandlerCode = partyParentRole.parentRoleCode;
		}

	    // Ottiene il codice del Centro di Costo

	    condList = EntityCondition.makeCondition(
        	EntityCondition.makeCondition("respCenterId", EntityOperator.EQUALS, parameters.cdcResponsibleId),
        	EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.cdcHandler)
    	);
		def cdcHandler = EntityUtil.getFirst(delegator.findList("CdcHandlerView", condList, null, null, null, false));
		// Debug.log(" - cdcHandler = " + cdcHandler);
		if (UtilValidate.isNotEmpty(cdcHandler)) {

		    condList = EntityCondition.makeCondition(
            	EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, cdcHandler.partyId),
            	EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ORGANIZATION_UNIT")
        	);
			partyParentRole = EntityUtil.getFirst(delegator.findList("PartyParentRole", condList, null, null, null, false));
			// Debug.log(" - CDC partyParentRole = " + partyParentRole);
			if (UtilValidate.isNotEmpty(partyParentRole)) {
				cdcHandlerCode = partyParentRole.parentRoleCode;
			}
		}
	}

	def workEffortPeriodTypeCond = [];
	workEffortPeriodTypeCond.add(EntityCondition.makeCondition("workEffortTypeId", parameters.workEffortTypeId));
	workEffortPeriodTypeCond.add(EntityCondition.makeCondition("glFiscalTypeEnumId", "GLFISCTYPE_TARGETFIN"));
	workEffortPeriodTypeCond.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition("statusEnumId", "DETECTABLE"),
	        EntityCondition.makeCondition("statusEnumId", "OPEN"), EntityCondition.makeCondition("statusEnumId", "REOPEN"), EntityCondition.makeCondition("statusEnumId", "CLOSE")));
	def workEffortTypePeriod = EntityUtil.getFirst(delegator.findList("WorkEffortTypePeriod", EntityCondition.makeCondition(workEffortPeriodTypeCond), null, null, null, false));
	def customTimePeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId": workEffortTypePeriod.customTimePeriodId], false);

	if (UtilValidate.isNotEmpty(cdcHandlerCode)) {
		def standardImportContext = [:];

		standardImportContext.userLogin = userLogin;
		standardImportContext.entityListToImport = "AcctgTransInterface|GlAccountInterface";
		standardImportContext.fromDate = customTimePeriod.fromDate;

		def conditionAcctgTrans = EntityCondition.makeCondition(
    		EntityCondition.makeCondition("uorgCode", EntityOperator.EQUALS, cdrHandlerCode),
    		EntityCondition.makeCondition("partyCode", EntityOperator.EQUALS, cdcHandlerCode),
    		EntityCondition.makeCondition("refDate", EntityOperator.LESS_THAN_EQUAL_TO, customTimePeriod.thruDate)
		);
		def conditionGlAccount = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdCdc", EntityOperator.EQUALS, cdcHandlerCode));
		standardImportContext.filterConditions = [ conditionAcctgTrans, conditionGlAccount ];
		Debug.log("#### standardImportBudget standardImportContext.filterConditions " + standardImportContext.filterConditions);

		// Debug.log(" - context.finalBudget " + context.finalBudget);
		if ("Y".equals(context.finalBudget)) {
		    standardImportContext.finalBudget = "Y";
		}

		serviceResult = dispatcher.runSync("standardImportBudget", standardImportContext);
		Debug.log(" - serviceResult " + serviceResult);

		def jobLogId = "";
		def recordElaborated = 0L;
		def blockingErrors = 0L;
		if (UtilValidate.isNotEmpty(serviceResult.resultETLList)) {
			// Debug.log("#### serviceResult.resultETLList " + serviceResult.resultETLList);
			serviceResult.resultETLList.each { resultMap ->
				jobLogId += resultMap.jobLogId + "; ";
				recordElaborated += resultMap.recordElaborated;
				blockingErrors += resultMap.blockingErrors;
			}
		}
		if (UtilValidate.isNotEmpty(serviceResult.resultList)) {
			// Debug.log("#### serviceResult.resultList " + serviceResult.resultList);
			serviceResult.resultList.each { resultMap ->
				// Debug.log("#### resultMap " + resultMap);
				jobLogId += resultMap.jobLogId + "; ";
				recordElaborated += resultMap.recordElaborated;
				blockingErrors += resultMap.blockingErrors;
			}
		}
		result.put("jobLogId", jobLogId);
		result.put("recordElaborated", recordElaborated);
		result.put("blockingErrors", blockingErrors);
	}
}

return result;
