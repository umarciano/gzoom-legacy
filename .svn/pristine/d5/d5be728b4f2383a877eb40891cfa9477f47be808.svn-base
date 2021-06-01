import org.ofbiz.base.util.*;

if ("Y".equals(parameters.insertMode)) {
	if (UtilValidate.isNotEmpty(parameters.workEffortId)) {
		def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
		if (UtilValidate.isNotEmpty(workEffort)) {
			context.fromDate = workEffort.estimatedStartDate;
		}
	}
}