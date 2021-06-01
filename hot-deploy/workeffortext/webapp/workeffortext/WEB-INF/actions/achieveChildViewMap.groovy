import org.ofbiz.base.util.*;

childViewMap = [:];
childMaxSize = 0;

//Nota bene : childList é settato da WorkEffortAchieveMatrixManagementScreen 
def idFieldToCheck = UtilValidate.isNotEmpty(context.idFieldToCheck) ? context.idFieldToCheck : "workEffortId";

if (UtilValidate.isNotEmpty(context.childList)) {
	context.childList.each { childElement ->
		if (childElement.containsKey(idFieldToCheck)) {
			def itemList = [];
			
			def fieldToCheck = childElement[idFieldToCheck];
			if (!childViewMap.containsKey(fieldToCheck)) {
				context.listIt.each { gv ->
					if (gv.workEffortIdFrom.equals(fieldToCheck)) {
						itemList.add(gv);
					}
				}
			}
			
			if (itemList.size() > 0) {
				childViewMap.put(fieldToCheck, itemList);
			}
			if(itemList.size() > childMaxSize) {
				childMaxSize = itemList.size();
			}
		}
	}
}

context.put("childViewMap", childViewMap);
context.put("childMaxSize", childMaxSize); 

