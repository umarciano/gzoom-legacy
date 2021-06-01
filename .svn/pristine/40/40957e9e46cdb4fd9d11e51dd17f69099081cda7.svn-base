import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.security.*;

if (UtilValidate.isEmpty(parameters.period)) {
    parameters.period = "week";
}

startParam = parameters.start;
start = null;
if (UtilValidate.isNotEmpty(startParam)) {
    start = new Timestamp(Long.parseLong(startParam));
}
format = null;
prev = null;
next = null;

switch (parameters.period) {
case "day" :
        if (start == null) {
            start = UtilDateTime.getDayStart(nowTimestamp, timeZone, locale);
        } else {
            start = UtilDateTime.getDayStart(start, timeZone, locale);
        }
        prev = UtilDateTime.getDayStart(start, -1, timeZone, locale);
        next = UtilDateTime.getDayStart(start, 1, timeZone, locale);
        format = UtilDateTime.getTimestampFormat(locale);

        break;
case "week" :
        if (start == null) {
            start = UtilDateTime.getWeekStart(nowTimestamp, timeZone, locale);
        } else {
            start = UtilDateTime.getWeekStart(start, timeZone, locale);
        }
        prev = UtilDateTime.getDayStart(start, -7, timeZone, locale);
        next = UtilDateTime.getDayStart(start, 7, timeZone, locale);
        format = "w";

        break;
case "month" :
        if (start == null) {
            start = UtilDateTime.getMonthStart(nowTimestamp, timeZone, locale);
        } else {
            start = UtilDateTime.getMonthStart(start, timeZone, locale);
        }
        tempCal = UtilDateTime.toCalendar(start, timeZone, locale);
        numDays = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

        prev = UtilDateTime.getMonthStart(start, -1, timeZone, locale);
        next = UtilDateTime.getDayStart(start, numDays+1, timeZone, locale);
        format = "MMMM yyyy";

        break;
default:
        break;
}

if (UtilValidate.isNotEmpty(prev)) {
    context.prevMillis = new Long(prev.getTime()).toString();
    context.prev = prev;
}
if (UtilValidate.isNotEmpty(next)) {
    context.nextMillis = new Long(next.getTime()).toString();
    context.next = next;
}

//workEffortTypeList = delegator.findList("WorkEffortType", EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, "EVENT"), null, null, null, true);
//if (UtilValidate.isNotEmpty(workEffortTypeList)) {
//    workEffortTypeIdList = EntityUtil.getFieldListFromEntityList(workEffortTypeList, "workEffortTypeId", true);
//    if (UtilValidate.isNotEmpty(workEffortTypeList)) {
//        parameters.workEffortTypeId = "[" + StringUtil.join(workEffortTypeIdList, ",") + "]";
//    }
//}

context.put("start",start);
context.put("format",format);