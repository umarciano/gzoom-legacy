import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastList;

def isInsertModeField = context.isInsertMode;
def isInsertMode = false;

if (isInsertModeField instanceof Boolean) {
	isInsertMode = isInsertModeField;
} else if (isInsertModeField instanceof String) {
	isInsertMode = "true".equalsIgnoreCase(isInsertModeField);
}

if (isInsertMode == true) {
	def localeSecondarySet = context.localeSecondarySet;
	
	def workEffortIdRoot = UtilValidate.isNotEmpty(context.workEffortIdRoot) ? context.workEffortIdRoot : parameters.workEffortIdRoot;
	def parentTypeId = UtilValidate.isNotEmpty(context.parentTypeId) ? context.parentTypeId : parameters.parentTypeId;
	def checkForAllUsers = context.checkForAllUsers;
	
	def workEffortIdFrom = parameters.workEffortIdFrom;
	def weIsTemplate = parameters.weIsTemplate;
	def weIsRoot = context.weIsRoot;
	def loadAlsoIfNotRoot = context.loadAlsoIfNotRoot;
	def gpMenuEnumId = parameters.gpMenuEnumId;
	
	def entityName = getEntityName(parentTypeId, loadAlsoIfNotRoot, workEffortIdFrom, weIsTemplate, weIsRoot);
	def condList = getCondList(workEffortIdFrom, weIsTemplate, weIsRoot, parentTypeId, workEffortTypeIdRoot, workEffortTypeIdFrom, gpMenuEnumId);
	if (condList != null) {
		condList.add(EntityCondition.makeCondition("isRootActive", "Y"));
		if (checkForAllUsers) {
			condList.add(EntityCondition.makeCondition("forAllUsers", "Y"));
		}
	}
	//def orderBy = UtilValidate.isNotEmpty(workEffortIdRoot) ? ["sequenceNum"] : ["-workEffortTypeId"];
	def orderBy = ["-seqEsp"];
	
	def workEffortTypeList = delegator.findList(entityName, EntityCondition.makeCondition(condList), null, orderBy, null, false);
	def workEffortType = EntityUtil.getFirst(workEffortTypeList);
	if (UtilValidate.isNotEmpty(workEffortType)) {
		context.workEffortTypeId = workEffortType.workEffortTypeId;		
		
		context.wetOrgUnitRoleTypeId = workEffortType.orgUnitRoleTypeId;
		context.wetOrgUnitRoleTypeId2 = workEffortType.orgUnitRoleTypeId2;
		context.wetOrgUnitRoleTypeId3 = workEffortType.orgUnitRoleTypeId3;
		if ("Y".equals(localeSecondarySet)) {
			context.wetOrgUnitRoleTypeDesc = workEffortType.orgUnitRoleTypeDescLang;
			context.wetParentRelTypeDesc = workEffortType.parentRelTypeDescLang;
		} else {
			context.wetOrgUnitRoleTypeDesc = workEffortType.orgUnitRoleTypeDesc;
			context.wetParentRelTypeDesc = workEffortType.parentRelTypeDesc;
		}
		context.wetFrameEnumId = workEffortType.frameEnumId;
		context.WETATOWorkEffortAssocTypeId = workEffortType.parentRelTypeId;
		context.wetWePurposeTypeIdWe = workEffortType.wePurposeTypeIdWe;
		context.wetPurposeEtch = workEffortType.purposeEtch;
		context.wetPurposeEtchLang = workEffortType.purposeEtchLang;
		
		def workEffortTypeParentRelList = delegator.findList("WorkEffortTypeParentRelView", EntityCondition.makeCondition("workEffortTypeId", workEffortType.workEffortTypeId), null, null, null, false);
		def workEffortTypeParentRel = EntityUtil.getFirst(workEffortTypeParentRelList);
		if (UtilValidate.isNotEmpty(workEffortTypeParentRel)) {
			context.WETATOWorkEffortTypeIdFrom = workEffortTypeParentRel.parentRelTypeIdRef;
		}
	}
}



def getEntityName(parentTypeId, loadAlsoIfNotRoot, workEffortIdFrom, weIsTemplate, weIsRoot) {
	if (UtilValidate.isEmpty(parentTypeId)) {
		if (! "Y".equals(weIsRoot) || "Y".equals(loadAlsoIfNotRoot)) {
			return "WorkEffortTypeTypeParentRelView";
		}
		return "WorkEffortTypeParentRelView";
	}
	
	if (! "Y".equals(weIsRoot)) {
		return "WorkEffortTypeTypeParentRelView";
	}
	if (UtilValidate.isEmpty(workEffortIdFrom) && UtilValidate.isNotEmpty(weIsTemplate)) {
		return "WorkEffortTypeParentRelView";
	}
	return "WorkEffortTypeParentRelView";
}

def getCondList(workEffortIdFrom, weIsTemplate, weIsRoot, parentTypeId, workEffortTypeIdRoot, workEffortTypeIdFrom, gpMenuEnumId) {
	def condList = FastList.newInstance();
	if (UtilValidate.isEmpty(workEffortIdFrom) && UtilValidate.isNotEmpty(gpMenuEnumId)) {
		condList.add(EntityCondition.makeCondition("gpMenuEnumId", gpMenuEnumId));
	}
	
	if (UtilValidate.isEmpty(parentTypeId)) {
		if (! "Y".equals(weIsRoot) || "Y".equals(loadAlsoIfNotRoot)) {
			condList.add(EntityCondition.makeCondition("workEffortTypeIdRoot", workEffortTypeIdRoot));
			condList.add(EntityCondition.makeCondition("workEffortTypeIdFrom", workEffortTypeIdFrom));
			return condList;
		}
	    condList.add(EntityCondition.makeCondition("isRoot", "Y"));
	    condList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.LIKE, "CTX%"));
	    return condList;
	}
	
	if (! "Y".equals(weIsRoot)) {
	    condList.add(EntityCondition.makeCondition("workEffortTypeIdRoot", workEffortTypeIdRoot));
	    condList.add(EntityCondition.makeCondition("workEffortTypeIdFrom", workEffortTypeIdFrom));		
		return condList;
	}
	if (UtilValidate.isEmpty(workEffortIdFrom) && UtilValidate.isNotEmpty(weIsTemplate)) {		
        condList.add(EntityCondition.makeCondition("isTemplate", weIsTemplate));
        condList.add(EntityCondition.makeCondition("isRoot", "Y"));
        condList.add(EntityCondition.makeCondition("parentTypeId", parentTypeId));
		
		return condList;
	}
	
    condList.add(EntityCondition.makeCondition("isRoot", "Y"));
    condList.add(EntityCondition.makeCondition("parentTypeId", parentTypeId));
	return condList;
}
