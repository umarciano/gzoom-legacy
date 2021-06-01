import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;



context.operation = parameters.operation;

context.workEffortId = UtilValidate.isEmpty(parameters.workEffortId) ? parameters.weTransWeId : parameters.workEffortId;

long startTime = System.currentTimeMillis();
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkStatusCrudEnum.groovy", context);
long endTime = System.currentTimeMillis();
Debug.log("Run script in " + (endTime - startTime) + " milliseconds at location = component://workeffortext/webapp/workeffortext/WEB-INF/actions/checkStatusCrudEnum.groovy");


context.periodTypeId = UtilValidate.isEmpty(context.periodTypeId) ? parameters.weMeasurePeriodTypeId : context.periodTypeId;

context.customTimePeriodId = UtilValidate.isEmpty(context.customTimePeriodId) ? parameters.customTimePeriodId : context.customTimePeriodId;


context.glFiscalTypeIdList = delegator.findList("GlFiscalType", EntityCondition.makeCondition( 
									EntityCondition.makeCondition("isFinancialUsed", EntityOperator.EQUALS, "Y"),
									EntityOperator.OR,
									EntityCondition.makeCondition("isAccountUsed", EntityOperator.EQUALS, "Y"))						
									, null, null, null, true);

context.rootInqyTree = parameters.rootInqyTree;


/*inizio GN-2727*/
def valuesDetails = "N";
def hideReference = "N";
def elabScoreIndic = "N";  //N, SCORE, INDIC
def contentIdInd = UtilValidate.isNotEmpty(parameters.contentIdInd) ? parameters.contentIdInd : context.contentIdInd;
def contentId = contentIdInd;
def fromValoriIndicatori = parameters.fromValoriIndicatori;
if ("Y".equals(fromValoriIndicatori)) {
	if ("WEFLD_IND".equals(contentIdInd)) {
		contentId = "WEFLD_AIND";
	}
	if ("WEFLD_IND2".equals(contentIdInd)) {
		contentId = "WEFLD_AIND2";
	}
	if ("WEFLD_IND3".equals(contentIdInd)) {
		contentId = "WEFLD_AIND3";
	}
	if ("WEFLD_IND4".equals(contentIdInd)) {
		contentId = "WEFLD_AIND4";
	}
	if ("WEFLD_IND5".equals(contentIdInd)) {
		contentId = "WEFLD_AIND5";
	}	
}

if (UtilValidate.isNotEmpty(contentId)) {
	def workEffortTypeId = "";
	def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : context.workEffortId], false);
	if (UtilValidate.isNotEmpty(workEffort)) {
		workEffortTypeId = workEffort.workEffortTypeId;
	}
	if (UtilValidate.isNotEmpty(workEffortTypeId)) {
		WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(null, null, delegator);
		def mapParams = paramsEvaluator.getParams(workEffortTypeId, contentId, false);
		if (UtilValidate.isNotEmpty(mapParams)) {
			valuesDetails = mapParams.valuesDetails;
			hideReference = mapParams.hideReference;
			elabScoreIndic = mapParams.elabScoreIndic;
		}
	}
}
context.onlyWithBudget = UtilValidate.isNotEmpty(context.onlyWithBudget) ? context.onlyWithBudget : "N";
context.accountFilter = UtilValidate.isNotEmpty(context.accountFilter) ? context.accountFilter : "ALL"; //OBJ, NOOBJ, ALL


context.valuesDetails = valuesDetails;
context.hideReference = hideReference;
context.elabScoreIndic = elabScoreIndic;
/*fine GN-2727*/