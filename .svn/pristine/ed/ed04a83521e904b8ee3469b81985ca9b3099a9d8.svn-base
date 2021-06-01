import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

def roleTypeId = "";
def roleTypeId2 = "";
def roleTypeId3 = "";
def workEffortType = context.workEffortType;
if (UtilValidate.isNotEmpty(workEffortType)) {
	roleTypeId = workEffortType.orgUnitRoleTypeId;
	roleTypeId2 = workEffortType.orgUnitRoleTypeId2;
	roleTypeId3 = workEffortType.orgUnitRoleTypeId3;
} else {
	roleTypeId = context.roleTypeId;
	roleTypeId2 = context.roleTypeId2;
	roleTypeId3 = context.roleTypeId3;
}

def conditionList = [];
conditionList.add(EntityCondition.makeCondition("parentTypeId", "ORGANIZATION_UNIT"));
if (UtilValidate.isNotEmpty(roleTypeId)) {
	if (UtilValidate.isEmpty(roleTypeId2)) {
		conditionList.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
	} else {
		def conditionOrs = [];
		conditionOrs.add(EntityCondition.makeCondition("roleTypeId", roleTypeId));
		conditionOrs.add(EntityCondition.makeCondition("roleTypeId", roleTypeId2));
		if (UtilValidate.isNotEmpty(roleTypeId3)) {
			conditionOrs.add(EntityCondition.makeCondition("roleTypeId", roleTypeId3));
		}
		conditionList.add(EntityCondition.makeCondition(conditionOrs, EntityJoinOperator.OR));
	}
}
def orderBy = ["roleTypeId"];
def orgUnitRoleTypeList = delegator.findList("RoleType", EntityCondition.makeCondition(conditionList), null, orderBy, null, false);
context.orgUnitRoleTypeList = orgUnitRoleTypeList;
context.orgUnitRoleTypeDisplayField = "Y".equals(context.localeSecondarySet) ? "descriptionLang" : "description";
