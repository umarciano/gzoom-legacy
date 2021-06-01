import org.ofbiz.base.util.*;
import java.text.*;
import org.ofbiz.service.ServiceUtil;


result = ServiceUtil.returnSuccess();

def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
if (UtilValidate.isNotEmpty(workEffort)) {
	DateFormat df = new SimpleDateFormat(UtilDateTime.getDateFormat(locale));	
	def estimatedStartDateFormatted = df.format(workEffort.estimatedStartDate);
	def estimatedCompletionDateFormatted = df.format(workEffort.estimatedCompletionDate);
	
	result.put("estimatedStartDateFormatted", estimatedStartDateFormatted);
	result.put("estimatedCompletionDateFormatted", estimatedCompletionDateFormatted);
}

return result;