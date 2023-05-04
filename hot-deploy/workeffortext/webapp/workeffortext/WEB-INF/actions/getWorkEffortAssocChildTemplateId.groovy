import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;

if (UtilValidate.isEmpty(context.get("childTemplateIdFrom"))) {

    def workEffortViewToList = delegator.findList("WorkEffortView", EntityCondition.makeCondition("workEffortId", parameters.toFromWeId),null, null, null, false);
        
    if (UtilValidate.isNotEmpty(workEffortViewToList)) {
        workEffortViewTo = workEffortViewToList.get(0);
        context.childTemplateIdFrom = workEffortViewTo.childTemplateId;
    }
	
	//Debug.log(" ********* parameters.entityNameExtended " + parameters.entityNameExtended);
	
	if ("Ref".equals(parameters.entityNameExtended)) {
		context["workEffortIdRef"] = context.workEffortId;
	}
}
