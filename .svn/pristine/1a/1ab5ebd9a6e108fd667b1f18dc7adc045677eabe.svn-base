import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def detailLayout = false;
def ctx = [:];
def destinationList = [];
def glAccountDescr = "";

def localeSecondarySet = context.localeSecondarySet;

def workEffortMeasureId = UtilValidate.isNotEmpty(parameters.workEffortMeasureId) ? parameters.workEffortMeasureId : UtilValidate.isNotEmpty(context.listIt) ? context.listIt[0].workEffortMeasureId : null;
if(UtilValidate.isNotEmpty(workEffortMeasureId)) {
	def workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId": workEffortMeasureId], false);
	def glAccount = delegator.findOne("GlAccount", ["glAccountId": workEffortMeasure.glAccountId], false);
	glAccountDescr = "Y".equals(localeSecondarySet) ? glAccount.descriptionLang : glAccount.description;
	def roleTypeList = delegator.findList("RoleType", EntityCondition.makeCondition("parentTypeId", EntityOperator.LIKE, "GOAL%"), null, null, null, true);
	def roleTypeIdList = EntityUtil.getFieldListFromEntityList(roleTypeList, "roleTypeId", true);
	
	if("ACCINP_UO".equals(glAccount.inputEnumId) && !"ACCDET_NULL".equals(glAccount.detailEnumId)) {
		if(UtilValidate.isNotEmpty(roleTypeIdList)) {
			def glAccountRoleConditionList = [];
			glAccountRoleConditionList.add(EntityCondition.makeCondition("glAccountId", glAccount.glAccountId));
			glAccountRoleConditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIdList));
			if(UtilValidate.isNotEmpty(workEffortMeasure.partyId)) {
				glAccountRoleConditionList.add(EntityCondition.makeCondition("partyId", workEffortMeasure.partyId));
			}
			glAccountRoleList = delegator.findList("GlAccountRoleAndParty", EntityCondition.makeCondition(glAccountRoleConditionList), null, null, null, true);
			if(UtilValidate.isNotEmpty(glAccountRoleList)) {
				if(UtilValidate.isEmpty(workEffortMeasure.partyId)) {
					detailLayout = true;
					destinationList = glAccountRoleList;
					
					ctx = context;
					ctx.workEffortMeasureId = workEffortMeasure.workEffortMeasureId;
					ctx.entityNameDetail = "GlAccountRoleAndParty";
					ctx.destinationList = destinationList;
					GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getWorkEffortMeasureIndicatorDetailTransactionPanelData.groovy", ctx);
					
				}
			}
		}
	}
	
	if("ACCINP_PRD".equals(glAccount.inputEnumId)) {
		context.productId = workEffortMeasure.productId;
		if(UtilValidate.isNotEmpty(workEffortMeasure.productId)) {
			def productConditionList = [];
			productConditionList.add(EntityCondition.makeCondition("productId", workEffortMeasure.productId));
			productConditionList.add(EntityCondition.makeCondition("glAccountId", workEffortMeasure.glAccountId));
			productConditionList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", ""), EntityOperator.OR, EntityCondition.makeCondition("workEffortId", null)));
			def productMeasureList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(productConditionList), null, null, null, false);
			def productMeasure = EntityUtil.getFirst(productMeasureList);
			if(UtilValidate.isNotEmpty(productMeasure) && !"ACCDET_NULL".equals(productMeasure.detailEnumId)) {
				def workEffortMeasureRoleConditionList = [];
				workEffortMeasureRoleConditionList.add(EntityCondition.makeCondition("workEffortMeasureId", productMeasure.workEffortMeasureId));
				workEffortMeasureRoleConditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, roleTypeIdList));
				if(UtilValidate.isNotEmpty(workEffortMeasure.partyId)) {
					workEffortMeasureRoleConditionList.add(EntityCondition.makeCondition("partyId", workEffortMeasure.partyId));
				}
				def workEffortMeasureRoleList = delegator.findList("WorkEffortMeasureRoleAndParty", EntityCondition.makeCondition(workEffortMeasureRoleConditionList), null, null, null, false);
				if(UtilValidate.isNotEmpty(workEffortMeasureRoleList)) {
					detailLayout = true;
					destinationList = workEffortMeasureRoleList;
					
					ctx = context;
					ctx.workEffortMeasureId = workEffortMeasure.workEffortMeasureId;
					ctx.entityNameDetail = "WorkEffortMeasureRoleAndParty";
					ctx.productMeasureId = productMeasure.workEffortMeasureId;
					ctx.destinationList = destinationList;
					GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getWorkEffortMeasureIndicatorDetailTransactionPanelData.groovy", ctx);
				}
			}
		}
	}
}

context.detailLayout = detailLayout;
context.transactionPanelMap = ctx.transactionPanelMap;
context.glFiscalTypeList = ctx.glFiscalTypeList;
context.periodList = ctx.periodList;
context.destinationList = destinationList;
context.destinationColumnDescription = ctx.destinationColumnDescription;
context.showTotal = ctx.showTotal;
context.glAccountDescr = glAccountDescr;