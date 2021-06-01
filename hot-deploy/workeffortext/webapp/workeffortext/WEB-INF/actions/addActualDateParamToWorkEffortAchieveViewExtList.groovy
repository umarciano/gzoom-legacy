import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastMap;



/**
 * gn2018 aggiungo ad ogni elemento della lista il flag hasShowActualDates che
 * stabilisce se le date actual del workEffort sono significative
 */

if (UtilValidate.isNotEmpty(context.workEffortAchieveList)) {
	def list = [];
	for (Object item: context.workEffortAchieveList) {
		def fieldsMap = null;		
		if (item instanceof GenericValue) {
			fieldsMap = [:];
			fieldsMap.putAll(item.getAllFields());
		} else {
			fieldsMap = item;
		}
			
		if (UtilValidate.isNotEmpty(fieldsMap)) {
			fieldsMap.hasShowActualDates = getHasShowActualDates(item);
			list.add(fieldsMap);
		}		
	}

	context.workEffortAchieveList = list;	
}


def getHasShowActualDates(workEffort) {
	def hasShowActualDates = "N";
	
	def weParamsMap = FastMap.newInstance();
	BshUtil.eval(workEffort.weParams, weParamsMap);
	if("Y".equals(weParamsMap.showActualDates)) {
		hasShowActualDates = UtilValidate.isNotEmpty(workEffort.actualStartDate) && UtilValidate.isNotEmpty(workEffort.actualCompletionDate) ? "Y" : "N";
	}
	
	return hasShowActualDates;
}
