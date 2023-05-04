import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

if (! isInsertMode) {
	def workEffort = context.we;
	if (UtilValidate.isNotEmpty(workEffort)) {
		if (UtilValidate.isNotEmpty(context.usePeriod)) {
			if ("Y".equals(context.onlyRefDate)) {
				def customTimePeriodThruCondList = [];
				customTimePeriodThruCondList.add(EntityCondition.makeCondition("thruDate", we.estimatedCompletionDate));
				customTimePeriodThruCondList.add(EntityCondition.makeCondition("periodTypeId", context.usePeriod));
				def customTimePeriodThruList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodThruCondList), null, null, null, false);
				def customTimePeriodThruItem = EntityUtil.getFirst(customTimePeriodThruList);
				if (UtilValidate.isNotEmpty(customTimePeriodThruItem)) {
					context.periodThruDate2 = customTimePeriodThruItem.customTimePeriodId;
				}				
			} else {
				def customTimePeriodFromCondList = [];
				customTimePeriodFromCondList.add(EntityCondition.makeCondition("fromDate", we.estimatedStartDate));
				customTimePeriodFromCondList.add(EntityCondition.makeCondition("periodTypeId", context.usePeriod));
				def customTimePeriodFromList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodFromCondList), null, null, null, false);
				def customTimePeriodFromItem = EntityUtil.getFirst(customTimePeriodFromList);
				if (UtilValidate.isNotEmpty(customTimePeriodFromItem)) {
					context.periodFromDate = customTimePeriodFromItem.customTimePeriodId;
				}
				def customTimePeriodThruCondList = [];
				customTimePeriodThruCondList.add(EntityCondition.makeCondition("thruDate", we.estimatedCompletionDate));
				customTimePeriodThruCondList.add(EntityCondition.makeCondition("periodTypeId", context.usePeriod));
				def customTimePeriodThruList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodThruCondList), null, null, null, false);
				def customTimePeriodThruItem = EntityUtil.getFirst(customTimePeriodThruList);
				if (UtilValidate.isNotEmpty(customTimePeriodThruItem)) {
					context.periodThruDate = customTimePeriodThruItem.customTimePeriodId;
				}
			}
		} else {
			if ("Y".equals(context.onlyRefDate)) {
				context.estimatedCompletionDate2 = we.estimatedCompletionDate;
			}
		}
	}
}