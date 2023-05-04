import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

context.showDates = "N";
context.showRoleType = "Y";
context.showRoleTypeWeight = "Y";
context.showComment = "N";
context.detailEnabled = "Y";
context.showSequence = "N";
context.arrayNumRows = "0";

def insertMode = UtilValidate.isNotEmpty(context.insertMode) ? context.insertMode : parameters.insertMode;

if (!"Y".equals(insertMode)) {
	
	if (UtilValidate.isEmpty(parameters.workEffortTypeId)) {
		 workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
		 if (UtilValidate.isNotEmpty(workEffort)) {
			 parameters.workEffortTypeId = workEffort.workEffortTypeId;
		 }
	}
	
    def	conditionList = new ArrayList();
    conditionList.add(EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, parameters.workEffortTypeId));
    //conditionList.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.LIKE, "WEM%"));
	conditionList.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "EMPLOYEE"));
	
    def fieldsToSelect = UtilMisc.toSet("workEffortTypeId", "roleTypeId");
    def roles = delegator.findList("WorkEffortTypeAndRole", EntityCondition.makeCondition(conditionList), fieldsToSelect, null, null, false);	

    def rolesToFilter = [];
    if (UtilValidate.isNotEmpty(roles)) {
        Iterator<GenericValue> iter = roles.iterator();
        while (iter.hasNext()) {
            GenericValue role = iter.next();
            if (UtilValidate.isNotEmpty(role)) {
        	    rolesToFilter.add(role.roleTypeId);	
            }
        }
    }

    if (UtilValidate.isNotEmpty(rolesToFilter)) {
        context.inputFields.roleTypeId_fld0_op = 'in';
        context.inputFields.roleTypeId_fld0_value = rolesToFilter;
    }
	
    GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWorkEffortAssignmentView.groovy", context);
    
    if (UtilValidate.isEmpty(rolesToFilter)) {
        context.listIt = [];	
    }
} else {
	Debug.log(" First search " + context.entityName + " with condition " + context.inputFields);
	GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWorkEffortAssignmentView.groovy", context);	
}