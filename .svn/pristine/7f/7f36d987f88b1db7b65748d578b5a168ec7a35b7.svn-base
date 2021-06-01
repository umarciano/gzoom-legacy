import org.ofbiz.service.ServiceUtil;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.client.*;
import javolution.util.*;

if("Y".equals(context.onlyChild)) {
	def workEffort = null;
	def workEffortId = UtilValidate.isNotEmpty(parameters.workEffortId) ? parameters.workEffortId : context.workEffortId;
	 if(UtilValidate.isNotEmpty(workEffortId)) {
		 workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
	 }
	 if(UtilValidate.isNotEmpty(workEffort)) {
		 context.parentOrgUnitId = workEffort.orgUnitId;
		 context.parentOrgUnitRoleTypeId = workEffort.orgUnitRoleTypeId;
	 }
}
