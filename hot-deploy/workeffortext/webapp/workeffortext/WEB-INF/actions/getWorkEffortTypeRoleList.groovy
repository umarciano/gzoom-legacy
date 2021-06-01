import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;


def weTypeRoleWeTypeId = UtilValidate.isNotEmpty(context.weTypeRoleWeTypeId) ? context.weTypeRoleWeTypeId : parameters.weTypeRoleWeTypeId;
def weTypeRoleParentTypeId = UtilValidate.isNotEmpty(context.weTypeRoleParentTypeId) ? context.weTypeRoleParentTypeId : parameters.weTypeRoleParentTypeId;

def workEffortTypeRoleList = [];

def condList = [];
condList.add(EntityCondition.makeCondition("workEffortTypeId", weTypeRoleWeTypeId));
if (UtilValidate.isNotEmpty(weTypeRoleParentTypeId)) {
	if(weTypeRoleParentTypeId.indexOf('%') > -1) {
		condList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.LIKE, weTypeRoleParentTypeId));
	} else {
		condList.add(EntityCondition.makeCondition("parentTypeId", weTypeRoleParentTypeId));
	}
}

def fieldsToSelect = UtilMisc.toSet("roleTypeId", "description", "descriptionLang");
def orderBy = UtilMisc.toList("parentTypeId", "roleTypeId");

def workEffortTypeAndRoleList = delegator.findList("WorkEffortTypeAndRole", EntityCondition.makeCondition(condList), fieldsToSelect, orderBy, null, false);
if (UtilValidate.isNotEmpty(workEffortTypeAndRoleList)) {
	workEffortTypeRoleList = EntityUtil.getFieldListFromEntityList(workEffortTypeAndRoleList, "roleTypeId", true);
} else {
	workEffortTypeRoleList = ["![null-field]"];
}

context.workEffortTypeAndRoleList = workEffortTypeAndRoleList;
context.workEffortTypeRoleList = workEffortTypeRoleList;
