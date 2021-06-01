import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


def workEffortTypePeriodId = UtilValidate.isNotEmpty(parameters.workEffortTypePeriodId) ? parameters.workEffortTypePeriodId : context.workEffortTypePeriodId;
if(UtilValidate.isNotEmpty(workEffortTypePeriodId)) {
	context.inputFields.workEffortRevisionId_fld0_op = "empty";

	def period = delegator.findOne("WorkEffortTypePeriodAndFromThruDate", ["workEffortTypePeriodId": workEffortTypePeriodId], false);
	if(UtilValidate.isNotEmpty(period)) {
		context.inputFields.estimatedStartDate_op = 'lessThanEqualTo';
		context.inputFields.estimatedStartDate_value = period.thruDate;
		context.inputFields.estimatedCompletionDate_op = 'greaterThanEqualTo';
		context.inputFields.estimatedCompletionDate_value = period.fromDate;		
	}
}

if(UtilValidate.isEmpty(parameters.snapshot) || !"Y".equals(parameters.snapshot)) {
	context.inputFields.workEffortSnapshotId_fld0_op = "empty";
}

GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
