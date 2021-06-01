import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

def parentWe = null;
def workEffortTypeId = "";
// parentWe e' il workeffort attuale
if(UtilValidate.isNotEmpty(context.parentWe)) {
	workEffortTypeId = context.parentWe.workEffortTypeId;
}

def wefromWetoEnumId = UtilValidate.isNotEmpty(context.wefromWetoEnumId) ? context.wefromWetoEnumId : parameters.wefromWetoEnumId;

def workEffortTypeListAssoc = [];

def condList = [];
condList.add(EntityCondition.makeCondition("wefromWetoEnumId", wefromWetoEnumId));
condList.add(EntityCondition.makeCondition("workEffortTypeId", workEffortTypeId));
condList.add(EntityCondition.makeCondition("contentId", parameters.contentId));
condList.add(EntityCondition.makeCondition("isUnique", "N"));

def workEffortTypeAssocAndAssocTypeList = delegator.findList("WorkEffortTypeAssocAndAssocType", EntityCondition.makeCondition(condList), null, null, null, false);
if (UtilValidate.isNotEmpty(workEffortTypeAssocAndAssocTypeList)) {
	workEffortTypeListAssoc = EntityUtil.getFieldListFromEntityList(workEffortTypeAssocAndAssocTypeList, "workEffortTypeIdRef", true);
} else {
	workEffortTypeListAssoc = ["![null-field],"];
}

context.workEffortTypeListAssoc = StringUtil.join(workEffortTypeListAssoc, ",");

if(! "Y".equals(context.showRelationship)) {
	if (workEffortTypeAssocAndAssocTypeList.size() == 1) {
		def workEffortAssocType = EntityUtil.getFirst(workEffortTypeAssocAndAssocTypeList);
	    if(UtilValidate.isNotEmpty(workEffortAssocType)) {
	    	context.workEffortAssocTypeId = workEffortAssocType.workEffortAssocTypeId;
	    	if(wefromWetoEnumId == "WETAFROM") {
	    		context.workEffortTypeIdTo = workEffortAssocType.workEffortTypeIdRef;
	    	}
	    	if(wefromWetoEnumId == "WETATO") {
	    		context.workEffortTypeIdFrom = workEffortAssocType.workEffortTypeIdRef;
	    	}    	
	    }
	}
}
