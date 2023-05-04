import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

statusTypeId = parameters.statusTypeId;
if (UtilValidate.isEmpty(statusTypeId)) {
    statusTypeId = context.statusTypeId;
}

context.insertMode = UtilValidate.isEmpty(context.insertMode) ? UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode : context.insertMode;

if ("Y".equals(context.insertMode)) {
	GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
	context.statusTypeId = statusTypeId;
} else {
	if (UtilValidate.isNotEmpty(statusTypeId)) {
		def condition1 = EntityCondition.makeCondition("statusTypeId", statusTypeId);
		def condition2 = EntityCondition.makeCondition("statusToTypeId", statusTypeId);
		def condition = EntityCondition.makeCondition(condition1, EntityOperator.OR, condition2);		
		// GN-5047
		//def orderBy = ["statusTypeId", "statusSeq", "statusToSeq"];
		def orderBy = ["transitionName"];
		context.listIt = delegator.findList("StatusItemValidChangeStatusType", condition, null, orderBy, null, false);
	
	    if(!("Y".equals(context.contextManagement))) {
	    	context.managementChildExtraParams = "statusTypeId=" + statusTypeId;
	    }
	    //Debug.log("********************************** statusValidChangeList.groovy --> context.listIt=" + context.listIt);
	    //Debug.log("********************************** statusValidChangeList.groovy --> context.managementChildExtraParams=" + context.managementChildExtraParams);
	}
	else {
	    //Debug.log("errore");
	}
}