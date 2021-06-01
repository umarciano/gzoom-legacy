import org.ofbiz.base.util.*;

childViewList = [];

def idFieldToCheck = UtilValidate.isNotEmpty(context.idFieldToCheck) ? context.idFieldToCheck : "workEffortId";

if (UtilValidate.isNotEmpty(context.childList)) {
	
	context.childList.each { item ->
		if (item.containsKey(idFieldToCheck)) {
			def fieldToCheck = item[idFieldToCheck];
			if (!childViewList.contains(fieldToCheck))
				childViewList.add(fieldToCheck);
		}
	}
}

context.put("childViewList", childViewList);