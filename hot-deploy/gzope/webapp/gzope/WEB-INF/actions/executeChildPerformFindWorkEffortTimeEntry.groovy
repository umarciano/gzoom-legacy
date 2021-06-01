import java.util.Comparator;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

import org.ofbiz.base.util.*;
import com.mapsengineering.base.birt.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

/** Recupero workEffort, workEffortType, workEffortRoot, workEffortTypePeriodId */
def workEffortView = delegator.findOne("WorkEffortAndTypePeriodAndCustomTime", ["workEffortId" : parameters.workEffortId], false);
if(UtilValidate.isEmpty(workEffortView)) {
	workEffortView = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortId], false);
}


/** verranno sovrascritti con altri layout */
context.showParty = UtilValidate.isNotEmpty(context.showParty) ? context.showParty : "N";

/** Recupero params */
WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
paramsEvaluator.evaluateParams(workEffortView.workEffortTypeId, false);

serviceMap = ["workEffortId": parameters.workEffortId, 
              "showParty" : context.showParty, 
              "userLogin": context.userLogin];
def res = dispatcher.runSync("executeChildPerformFindTimeEntry", serviceMap);
hoursTotal = 0;

timeEntryList = [];

if(UtilValidate.isNotEmpty(res.rowList)) {
	timeEntryList.addAll(res.rowList);
	
	hoursTotal = res.hoursTotal;
	
	// add total row
	// create only one row for TOTAL
	org.ofbiz.entity.GenericValue genericValueTotali = delegator.makeValue("WorkEffortTimeEntry");
	genericValueTotali.put("workEffortId", parameters.workEffortId);
	genericValueTotali.put("projectName", "TOTALE");
	genericValueTotali.put("hours", hoursTotal);
	
	timeEntryList.add(genericValueTotali);
	context.timeEntryList = timeEntryList;
}