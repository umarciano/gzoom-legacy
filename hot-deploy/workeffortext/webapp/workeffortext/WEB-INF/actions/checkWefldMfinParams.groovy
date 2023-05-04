import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;
import com.mapsengineering.workeffortext.util.LevelUoPartyExtractor;

Debug.log("*** checkWefldMfinParams.groovy ***" );
def workEffortTypeId = parameters.workEffortTypeId != null ? parameters.workEffortTypeId : null;
def workEffortView = delegator.findOne("WorkEffortAndTypePeriodAndCustomTime", ["workEffortId" : parameters.workEffortId], false);
if(UtilValidate.isEmpty(workEffortTypeId)){
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
	if(UtilValidate.isNotEmpty(workEffort)){
		workEffortTypeId = workEffort.workEffortTypeId;
	}
}
parameters.contentId = UtilValidate.isNotEmpty(parameters.contentId) ? parameters.contentId : "WEFLD_MFIN";
context.showKpiWeight = "Y";
context.accountSameUO = "N";
context.accountParentUO = "N";
context.accountChildUO = "N";
if(UtilValidate.isEmpty(parameters.isObiettivo) || !"Y".equals(parameters.isObiettivo)) {
	/** Recupero params */
	WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	paramsEvaluator.evaluateParams(workEffortTypeId, parameters.contentId, false);
}
//Debug.log("*** checkWefldMfinParams accountSameUO " + context.accountSameUO);
//Debug.log("*** checkWefldMfinParams accountParentUO " + context.accountParentUO);
//Debug.log("*** checkWefldMfinParams accountChildUO " + context.accountChildUO);
def levelAccountUo = "N";
if (UtilValidate.isNotEmpty(workEffortView)) {
	LevelUoPartyExtractor levelUoPartyExtractor = new LevelUoPartyExtractor(delegator, workEffortView.orgUnitId, workEffortView.orgUnitRoleTypeId);
	levelUoPartyExtractor.initLevelSameUO(context.get("accountSameUO"), "N");
	levelUoPartyExtractor.initLevelParentUO(context.get("accountParentUO"), "N");
	levelUoPartyExtractor.initLevelChildUO(context.get("accountChildUO"), "N");
	//levelUoPartyExtractor.initLevelSisterUO(context.get("accountSisterUO"), "N");
	//levelUoPartyExtractor.initLevelTopUO(context.get("accountTopUO"), "N");
	levelUoPartyExtractor.run();
	if(levelUoPartyExtractor.isLevelUO()) {
		levelAccountUo = "Y";
		def orgUnitIdList = levelUoPartyExtractor.getOrgUnitIdList();
		context.orgUnitIdListAccount = StringUtil.join(orgUnitIdList, ",").replace(",![null-field]", "").replace("![null-field]", "");
	}
}
context.levelAccountUo = levelAccountUo;
//Debug.log("*** checkWefldMfinParams levelAccountUo " + levelAccountUo);
//Debug.log("*** checkWefldMfinParams orgUnitIdList " + context.orgUnitIdListAccount);
