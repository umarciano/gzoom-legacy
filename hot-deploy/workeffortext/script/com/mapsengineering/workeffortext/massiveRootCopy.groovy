import org.ofbiz.base.util.*;
import java.text.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ServiceUtil;
import com.mapsengineering.base.util.*;
import com.mapsengineering.base.services.*;
import org.ofbiz.base.crypto.HashCrypt;
import java.util.*;
import org.ofbiz.service.ModelService;

result = ServiceUtil.returnSuccess();
def nowAsString = UtilDateTime.nowAsString();
def sessionId = HashCrypt.getDigestHash(nowAsString, "SHA");
sessionId = sessionId.substring(37);

def security = dctx.getSecurity();
def dispatcher = dctx.getDispatcher();
def messages = new ArrayList<Map<String, Object>>();

GroovyUtil.runScriptAtLocation("component://base/webapp/base/WEB-INF/actions/setLocaleSecondary.groovy", context);
def startService = new JobLogLog().initLogCode("WorkeffortExtUiLabels", "MassiveRootCopyStartService", null, locale);
messages.add(ServiceLogger.makeLogInfo(startService.getLogMessage(), "INFO_GENERIC"));

def workEffortRootId = parameters.workEffortRootId;
def partyIdList = parameters.partyIdList;
def parties = StringUtil.split(partyIdList, ";#");
def workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId": workEffortRootId], false);
def nowTimestamp = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp(), timeZone, locale);
if (UtilValidate.isNotEmpty(workEffortRoot)) {
	def evalOrigConditionList = [];
	evalOrigConditionList.add(EntityCondition.makeCondition("workEffortId", workEffortRootId));
	evalOrigConditionList.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_IN_CHARGE"));
	def evalOrigList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(evalOrigConditionList), null, null, null, false);
	def evalOrig = EntityUtil.getFirst(evalOrigList);
	
	def partyEval = null;
	if (UtilValidate.isNotEmpty(evalOrig)) {
		def partyEvalConditionList = [];
		partyEvalConditionList.add(EntityCondition.makeCondition("partyId", evalOrig.partyId));
		partyEvalConditionList.add(EntityCondition.makeCondition("roleTypeId", "EMPLOYEE"));
	    def partyEvalList = delegator.findList("PartyAndPartyParentRole", EntityCondition.makeCondition(partyEvalConditionList), null, null, null, false);
		partyEval = EntityUtil.getFirst(partyEvalList);
	}
	
	parties.each { party ->
	    def partyGvConditionList = [];
	    partyGvConditionList.add(EntityCondition.makeCondition("partyId", party));
	    partyGvConditionList.add(EntityCondition.makeCondition("roleTypeId", "EMPLOYEE"));
	    def partyGvList = delegator.findList("PartyAndPartyParentRole", EntityCondition.makeCondition(partyGvConditionList), null, null, null, false);
		def partyGv = EntityUtil.getFirst(partyGvList);
		def	rootSubjectMap = [:];
		if ("Y".equals(context.localeSecondarySet)) {
			rootSubjectMap.workEffortName = workEffortRoot.workEffortNameLang;
		} else {
			rootSubjectMap.workEffortName = workEffortRoot.workEffortName;
		}
		if (UtilValidate.isNotEmpty(partyGv)) {
			rootSubjectMap.partyName = partyGv.partyName;
		}
		def rootSubject = new JobLogLog().initLogCode("WorkeffortExtUiLabels", "MassiveRootCopyRootSubject", rootSubjectMap, locale);
		messages.add(ServiceLogger.makeLogInfo(rootSubject.getLogMessage(), "INFO_GENERIC"));
		
		def serviceName = "workEffortRootCopyService";
		def serviceInMap = dispatcher.getDispatchContext().makeValidContext(serviceName, ModelService.IN_PARAM, parameters);
		serviceInMap.put("workEffortId", workEffortRootId);
		serviceInMap.put("organizationPartyId", workEffortRoot.organizationId);
		serviceInMap.put("deleteOldRoots", "N");
		serviceInMap.put("glAccountCreation", "Y");
		serviceInMap.put("checkExisting", "N");
		serviceInMap.put("workEffortTypeIdFrom", workEffortRoot.workEffortTypeId);
		serviceInMap.put("workEffortTypeIdTo", workEffortRoot.workEffortTypeId);
		serviceInMap.put("duplicateAdmit", "CLONE");
		serviceInMap.put("estimatedStartDateFrom", workEffortRoot.estimatedStartDate);
		serviceInMap.put("estimatedCompletionDateFrom", workEffortRoot.estimatedCompletionDate);
		serviceInMap.put("estimatedStartDateTo", workEffortRoot.estimatedStartDate);
		serviceInMap.put("estimatedCompletionDateTo", workEffortRoot.estimatedCompletionDate);
		serviceInMap.put("copyWorkEffortAssocCopy", "Y");
	    def res = dispatcher.runSync(serviceName, serviceInMap);
	    if (! ServiceUtil.isSuccess(res)) {
			def	rootSubjectErrorMap = [:];
			if ("Y".equals(context.localeSecondarySet)) {
				rootSubjectErrorMap.workEffortName = workEffortRoot.workEffortNameLang;
			} else {
				rootSubjectErrorMap.workEffortName = workEffortRoot.workEffortName;
			}
			if (UtilValidate.isNotEmpty(partyGv)) {
				rootSubjectErrorMap.partyName = partyGv.partyName;
			}
	    	def serviceError = new JobLogLog().initLogCode("WorkeffortExtUiLabels", "MassiveRootCopyRootSubjectError", rootSubjectErrorMap, locale);
	    	messages.add(ServiceLogger.makeLogError(startService.getLogMessage() + " : " + res.get("errorMessage"), "ERROR_BLOCKING"));
	    } else {
	    	def workEffortId = "";
	    	def workEffortRootIdNewList = res.get("workEffortRootIdNewList");
	    	if (UtilValidate.isNotEmpty(workEffortRootIdNewList)) {
	    		workEffortId = workEffortRootIdNewList[0];
	    	}
	    	if (UtilValidate.isNotEmpty(workEffortId)) {
    			def partyRelEvalManagerConditionList = [];
    			partyRelEvalManagerConditionList.add(EntityCondition.makeCondition("partyIdFrom", party));
    			partyRelEvalManagerConditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "WEF_EVALUATED_BY"));
    			partyRelEvalManagerConditionList.add(EntityCondition.makeCondition("roleTypeIdTo", "WEM_EVAL_MANAGER"));
    			partyRelEvalManagerConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
    			def partyRelEvalManagerConditionOr = [];
    			partyRelEvalManagerConditionOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp));
    			partyRelEvalManagerConditionOr.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
    			partyRelEvalManagerConditionList.add(EntityCondition.makeCondition(partyRelEvalManagerConditionOr, EntityJoinOperator.OR));
    			def partyRelEvalManagerList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(partyRelEvalManagerConditionList), null, null, null, false);
    			def partyRelEvalManager = EntityUtil.getFirst(partyRelEvalManagerList);
    			
    			def partyRelUoConditionList = [];
    			partyRelUoConditionList.add(EntityCondition.makeCondition("partyIdTo", party));
    			partyRelUoConditionList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "ORG_EMPLOYMENT"));
    			partyRelUoConditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
    			def partyRelUoConditionOr = [];
    			partyRelUoConditionOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, nowTimestamp));
    			partyRelUoConditionOr.add(EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD));
    			partyRelUoConditionList.add(EntityCondition.makeCondition(partyRelUoConditionOr, EntityJoinOperator.OR));
    			def partyRelUoList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(partyRelUoConditionList), null, null, null, false);
    			def partyRelUo = EntityUtil.getFirst(partyRelUoList);
    			
    			def workEffortList = delegator.findList("WorkEffort", EntityCondition.makeCondition("workEffortParentId", workEffortId), null, null, null, false);
    			if (UtilValidate.isNotEmpty(workEffortList)) {
    				workEffortList.each {workEffort ->
    				    //sostituzione nome
    				    if (workEffortId.equals(workEffort.workEffortId)) {
    				    	if (UtilValidate.isNotEmpty(partyEval) && UtilValidate.isNotEmpty(partyGv) && UtilValidate.isNotEmpty(workEffort.workEffortName)) {
    				    		def newWorkEffortName = workEffort.workEffortName;
    				    		def idx1 = workEffort.workEffortName.indexOf(partyEval.partyName);
    				    		def idx2 = workEffort.workEffortName.indexOf(partyEval.parentRoleCode);
    				    		if (idx1 >= 0) {
    				    			newWorkEffortName = newWorkEffortName.replace(partyEval.partyName, partyGv.partyName);
    				    		}
    				    		if (idx2 >= 0) {
    				    			newWorkEffortName = newWorkEffortName.replace(partyEval.parentRoleCode, partyGv.parentRoleCode);	
    				    		}
    				    		if (idx1 >= 0 || idx2 >= 0) {
    				    			workEffort.workEffortName = newWorkEffortName;
        				    		delegator.store(workEffort);	
    				    		}
    				    	}
    				    	if (UtilValidate.isNotEmpty(partyEval) && UtilValidate.isNotEmpty(partyGv) && UtilValidate.isNotEmpty(workEffort.workEffortNameLang)) {
    				    		def newWorkEffortNameLang = workEffort.workEffortNameLang;
    				    		def idx3 = workEffort.workEffortNameLang.indexOf(partyEval.partyName);
    				    		def idx4 = workEffort.workEffortNameLang.indexOf(partyEval.parentRoleCode);
    				    		if (idx3 >= 0) {
    				    			newWorkEffortNameLang = newWorkEffortNameLang.replace(partyEval.partyName, partyGv.partyName);	
    				    		}
    				    		if (idx4 >= 0) {
    				    			newWorkEffortNameLang = newWorkEffortNameLang.replace(partyEval.parentRoleCode, partyGv.parentRoleCode);	
    				    		}
    				    		if (idx3 >= 0 || idx4 >= 0) {
    				    			workEffort.workEffortNameLang = newWorkEffortNameLang;
        				    		delegator.store(workEffort);
    				    		}
    				    	}    				    	
    				    }
    				
	    				//sostituzione valutato
	    				def evalConidtionList = [];
	    				evalConidtionList.add(EntityCondition.makeCondition("workEffortId", workEffort.workEffortId));
	    				evalConidtionList.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_IN_CHARGE"));
	    				def evalList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(evalConidtionList), null, null, null, false);
	    				if (UtilValidate.isNotEmpty(evalList)) {
	    					evalList.each {evalItem ->
	    						delegator.removeValue(evalItem);
	    					}
	    				}
	    				def evalWepa = delegator.makeValue("WorkEffortPartyAssignment");
	    				evalWepa.workEffortId = workEffort.workEffortId;
	    				evalWepa.partyId = party;
	    				evalWepa.roleTypeId = "WEM_EVAL_IN_CHARGE";
	    				evalWepa.fromDate = workEffort.estimatedStartDate;
	    				evalWepa.thruDate = workEffort.estimatedCompletionDate;
	    				delegator.create(evalWepa);
	    				
	    				//sostituzione WE_ASSIGNMENT
			    		def evalAssConidtionList = [];
			    		evalAssConidtionList.add(EntityCondition.makeCondition("workEffortId", workEffort.workEffortId));
		    			if (UtilValidate.isNotEmpty(evalOrig)) {
		    				evalAssConidtionList.add(EntityCondition.makeCondition("partyId", evalOrig.partyId));
		    			}
			    		evalAssConidtionList.add(EntityCondition.makeCondition("roleTypeId", "WE_ASSIGNMENT"));
			    		def evalAssList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(evalAssConidtionList), null, null, null, false);
			    		if (UtilValidate.isNotEmpty(evalAssList)) {
			    			evalAssList.each {evalAssItem ->
			    				delegator.removeValue(evalAssItem);
			    			}
			    		}
			    		def evalAssNew = delegator.makeValue("WorkEffortPartyAssignment");
			    		evalAssNew.workEffortId = workEffort.workEffortId;
			    		evalAssNew.partyId = party;
			    		evalAssNew.roleTypeId = "WE_ASSIGNMENT";
			    		evalAssNew.fromDate = workEffort.estimatedStartDate;
			    		evalAssNew.thruDate = workEffort.estimatedCompletionDate;
			    		delegator.create(evalAssNew);
	    					    				
		    			//sostituzione valutatore
		    			def evalManagerConidtionList = [];
		    			evalManagerConidtionList.add(EntityCondition.makeCondition("workEffortId", workEffort.workEffortId));
		    			evalManagerConidtionList.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_MANAGER"));
		    			def evalManagerList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(evalManagerConidtionList), null, null, null, false);
		    			if (UtilValidate.isNotEmpty(evalManagerList)) {
		    				evalManagerList.each {evalManagerItem ->
		    					delegator.removeValue(evalManagerItem);
		    				}
		    			}
		    			if (UtilValidate.isNotEmpty(partyRelEvalManager)) {
			    			def evalManagerWepa = delegator.makeValue("WorkEffortPartyAssignment");
			    			evalManagerWepa.workEffortId = workEffort.workEffortId;
			    			evalManagerWepa.partyId = partyRelEvalManager.partyIdTo;
			    			evalManagerWepa.roleTypeId = "WEM_EVAL_MANAGER";
			    			evalManagerWepa.fromDate = workEffort.estimatedStartDate;
			    			evalManagerWepa.thruDate = workEffort.estimatedCompletionDate;
			    			delegator.create(evalManagerWepa);
		    			}
		    			
		    			//sostituzione uorg
		    			if (UtilValidate.isNotEmpty(partyRelUo)) {
		    				workEffort.orgUnitId = partyRelUo.partyIdFrom;
		    				workEffort.orgUnitRoleTypeId = partyRelUo.roleTypeIdFrom;
		    				delegator.store(workEffort);
		    			}
    				}
    				def	rootSubjectSuccessMap = [:];
	    			if ("Y".equals(context.localeSecondarySet)) {
	    				rootSubjectSuccessMap.workEffortName = workEffortRoot.workEffortNameLang;
	    			} else {
	    				rootSubjectSuccessMap.workEffortName = workEffortRoot.workEffortName;
	    			}
	    			if (UtilValidate.isNotEmpty(partyGv)) {
	    				rootSubjectSuccessMap.partyName = partyGv.partyName;
	    			}
	    			def rootSubjectSuccess = new JobLogLog().initLogCode("WorkeffortExtUiLabels", "MassiveRootCopyRootSubjectSuccess", rootSubjectSuccessMap, locale);
	    			messages.add(ServiceLogger.makeLogInfo(rootSubjectSuccess.getLogMessage(), "INFO_GENERIC"));
    			}
	    	} else {
	    		def	rootSubjectNotCopiedMap = [:];
    			if ("Y".equals(context.localeSecondarySet)) {
    				rootSubjectNotCopiedMap.workEffortName = workEffortRoot.workEffortNameLang;
    			} else {
    				rootSubjectNotCopiedMap.workEffortName = workEffortRoot.workEffortName;
    			}
    			if (UtilValidate.isNotEmpty(partyGv)) {
    				rootSubjectNotCopiedMap.partyName = partyGv.partyName;
    			}
    			def rootNotCopied = new JobLogLog().initLogCode("WorkeffortExtUiLabels", "MassiveRootCopyRootSubjectNotCopied", rootSubjectNotCopiedMap, locale);
    			messages.add(ServiceLogger.makeLogInfo(rootNotCopied.getLogMessage(), "INFO_GENERIC"));
	    	}
	    }
    }
}

def endService = new JobLogLog().initLogCode("WorkeffortExtUiLabels", "MassiveRootCopyEndService", null, locale);
messages.add(ServiceLogger.makeLogInfo(endService.getLogMessage(), "INFO_GENERIC"));

writeLogs(dispatcher, sessionId, messages);
result.put("sessionId", sessionId);
return result;

def writeLogs(dispatcher, sessionId, messages) {
	def startTimestamp = UtilDateTime.nowTimestamp();
	Map<String, Object> logParameters = new HashMap<String, Object>();
	logParameters.put(ServiceLogger.SESSION_ID, sessionId);
	logParameters.put(ServiceLogger.SERVICE_NAME, "massiveRootCopy");
	logParameters.put(ServiceLogger.USER_LOGIN, userLogin);
	logParameters.put(ServiceLogger.LOG_DATE, startTimestamp);
	logParameters.put(ServiceLogger.MESSAGES, messages);
	ServiceLogger.writeLogs(dispatcher.getDispatchContext(), logParameters);
}