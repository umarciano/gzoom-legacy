import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

def fromDate = null;
if (UtilValidate.isNotEmpty(context.fromDate)) {
	fromDate = context.fromDate;
} else {
	if (UtilValidate.isNotEmpty(context.we)) {
		fromDate = context.we.estimatedStartDate;
	}
}

def thruDate = null;
if (UtilValidate.isNotEmpty(context.thruDate)) {
	thruDate = context.thruDate;
} else {
	if (UtilValidate.isNotEmpty(context.we)) {
		thruDate = context.we.estimatedCompletionDate;
	}
}

def periodFromDate = "";
if (UtilValidate.isNotEmpty(context.periodFromDate)) {
	periodFromDate = context.periodFromDate;
} else {
	if (UtilValidate.isNotEmpty(context.usePeriod)) {
		def customTimePeriodFromCondList = [];
		customTimePeriodFromCondList.add(EntityCondition.makeCondition("fromDate", fromDate));
		customTimePeriodFromCondList.add(EntityCondition.makeCondition("periodTypeId", context.usePeriod));
		def customTimePeriodFromList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodFromCondList), null, null, null, false);
		def customTimePeriodFromItem = EntityUtil.getFirst(customTimePeriodFromList);
		if (UtilValidate.isNotEmpty(customTimePeriodFromItem)) {
			periodFromDate = customTimePeriodFromItem.customTimePeriodId;
		}
	}
}

def periodThruDate = "";
if (UtilValidate.isNotEmpty(context.periodThruDate)) {
	periodThruDate = context.periodThruDate;
} else {
	if (UtilValidate.isNotEmpty(context.usePeriod)) {
		def customTimePeriodThruCondList = [];
		customTimePeriodThruCondList.add(EntityCondition.makeCondition("thruDate", thruDate));
		customTimePeriodThruCondList.add(EntityCondition.makeCondition("periodTypeId", context.usePeriod));
		def customTimePeriodThruList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(customTimePeriodThruCondList), null, null, null, false);
		def customTimePeriodThruItem = EntityUtil.getFirst(customTimePeriodThruList);
		if (UtilValidate.isNotEmpty(customTimePeriodThruItem)) {
			periodThruDate = customTimePeriodThruItem.customTimePeriodId;
		}
	}
}

context.periodFromDate = periodFromDate;
context.periodThruDate = periodThruDate;