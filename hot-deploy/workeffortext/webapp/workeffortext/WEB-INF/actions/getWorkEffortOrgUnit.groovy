import org.ofbiz.service.ServiceUtil;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.client.*;
import javolution.util.*;

context.treeLevelList = "";
context.entityNameExtended = "";
if("Y".equals(context.onlyChild) || "Y".equals(context.onlyGrandchild)) {
    context.entityNameExtended = "Rollup";
	def workEffort = null;
	def workEffortId = UtilValidate.isNotEmpty(parameters.workEffortId) ? parameters.workEffortId : context.workEffortId;
	if(UtilValidate.isNotEmpty(workEffortId)) {
		 workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
	 }
	 if(UtilValidate.isNotEmpty(workEffort)) {
		 context.parentOrgUnitId = workEffort.orgUnitId;
		 context.parentOrgUnitRoleTypeId = workEffort.orgUnitRoleTypeId;
	 }
	 if("Y".equals(context.onlyChild)) {
	     context.treeLevelList = "1";
	 }
	 if("Y".equals(context.onlyGrandchild)) {
	     if(UtilValidate.isNotEmpty(context.onlyGrandchild)) {
	         context.treeLevelList = context.treeLevelList + ",";
	     }
	     context.treeLevelList = context.treeLevelList + "2";
	 }
}

// Debug.log(" context.entityNameExtended " + context.entityNameExtended);
// Debug.log(" context.parentOrgUnitId " + context.parentOrgUnitId);
// Debug.log(" context.treeLevelList " + context.treeLevelList);
