import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;
import java.text.*;

result = ServiceUtil.returnSuccess();

def fromDateFormatted = "";
def thruDateFormatted = "";

def workEfforTypeId = parameters.workEffortTypeId;

def workEffortTypePeriodViewConditions = [];
workEffortTypePeriodViewConditions.add(EntityCondition.makeCondition("workEffortTypeId", workEfforTypeId));
workEffortTypePeriodViewConditions.add(EntityCondition.makeCondition("statusEnumId", "OPEN"));
def orderBy = ["-periodThruDate"];

def workEffortTypePeriodViewList = delegator.findList("WorkEffortTypePeriodView", EntityCondition.makeCondition(workEffortTypePeriodViewConditions), null, orderBy, null, false);
def workEffortTypePeriodViewItem = EntityUtil.getFirst(workEffortTypePeriodViewList);

if (UtilValidate.isNotEmpty(workEffortTypePeriodViewItem)) {
	def fromDate = workEffortTypePeriodViewItem.periodFromDate;
	def thruDate = workEffortTypePeriodViewItem.periodThruDate;
	
	DateFormat df = new SimpleDateFormat(UtilDateTime.getDateFormat(locale));	
	fromDateFormatted = df.format(fromDate);
	thruDateFormatted = df.format(thruDate);
}

result.put("fromDateFormatted", fromDateFormatted);
result.put("thruDateFormatted", thruDateFormatted);
return result;
