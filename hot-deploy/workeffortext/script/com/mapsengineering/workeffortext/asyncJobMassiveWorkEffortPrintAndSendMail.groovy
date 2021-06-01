import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.*;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.security.*;

import com.mapsengineering.base.services.async.AsyncJobOfbizService;
import com.mapsengineering.base.services.async.AsyncJobManager;

result = ServiceUtil.returnSuccess();
/**
 * I filtri applicati i nquesta ricerca vanno applicati anche nel groovy executePerformFindEPWorkEffortRootInqy.groovy
 * perche altrimenti il cambio stato fa una query diversa...
 */
Debug.log("***asyncJobMassiveWorkEffortPrintAndSendMail.groovy");
Debug.log("***parameters.evalManagerPartyId " + parameters.evalManagerPartyId);
Debug.log("***parameters.evalPartyId " + parameters.evalPartyId);

nowStamp = UtilDateTime.nowAsString();
sessionId = HashCrypt.getDigestHash(nowStamp);
sessionId = sessionId.substring(37);
Debug.log("***sessionId " + sessionId);

workEffortRootList = parameters.workEffortRootList;

if (UtilValidate.isNotEmpty(parameters.workEffortRootList) && (UtilValidate.isNotEmpty(parameters.evalManagerPartyId) || UtilValidate.isNotEmpty(parameters.evalPartyId))) {
	conditionList = null;
	if (UtilValidate.isNotEmpty(parameters.evalManagerPartyId)) {
		def condList = [];
		condList.add(EntityCondition.makeCondition("partyId", parameters.evalManagerPartyId));
		condList.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_MANAGER"));	//valutatore	
		def evalManagerList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(condList), null, null, null, false);
		def evalManagerWorkEffortId = EntityUtil.getFieldListFromEntityList(evalManagerList, "workEffortId", false);		
		workEffortRootList = EntityUtil.filterByCondition(parameters.workEffortRootList, EntityCondition.makeCondition("workEffortId", EntityOperator.IN, evalManagerWorkEffortId));
	}
	
	if (UtilValidate.isNotEmpty(parameters.evalPartyId)) {
		def condList = [];
		condList.add(EntityCondition.makeCondition("partyId", parameters.evalPartyId));
		condList.add(EntityCondition.makeCondition("roleTypeId", "WEM_EVAL_IN_CHARGE"))	//valutato
		def evalList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(condList), null, null, null, false);
		def evalWorkEffortId = EntityUtil.getFieldListFromEntityList(evalList, "workEffortId", false);
		workEffortRootList = EntityUtil.filterByCondition(parameters.workEffortRootList, EntityCondition.makeCondition("workEffortId", EntityOperator.IN, evalWorkEffortId));
	}
}

serviceName = "massivePrintAndSendMail";
def serviceContext = dctx.makeValidContext(serviceName, ModelService.IN_PARAM, parameters);
def workEffortRootIdList = EntityUtil.getFieldListFromEntityList(workEffortRootList, "workEffortId", true)
Debug.log("***workEffortRootIdList " + workEffortRootIdList);

serviceContext.put("workEffortRootIdList", workEffortRootIdList);
serviceContext.put("userLogin", userLogin);
serviceContext.put("sessionId", sessionId);
AsyncJobOfbizService job = new AsyncJobOfbizService(dctx.getDispatcher(), serviceName, serviceContext);
job.getConfig().setKeepAliveTimeout(0L);
AsyncJobManager.submit(job);

result.put("sessionId", sessionId);

Debug.log("***result " + result);

return result;