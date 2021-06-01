import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

Debug.log("******************************* getPrintBirtList.groovy -> parameters.repContextContentId = " + parameters.repContextContentId);
Debug.log("******************************* getPrintBirtList.groovy -> context.justLoadList = " + context.justLoadList);
if (!"Y".equals(context.justLoadList)) {
	if (UtilValidate.isNotEmpty(parameters.repContextContentId)) {		
		if (UtilValidate.isEmpty(context.listIt) && (UtilValidate.isEmpty(context.listIsEmpty) || context.listIsEmpty != 'Y')) {
		
			def conditionList = [EntityCondition.makeCondition("contentIdStart", parameters.repContextContentId),
				                 EntityCondition.makeCondition("contentTypeId", "REPORT"),
								 EntityCondition.makeCondition("caContentAssocTypeId", "REP_PERM")];
							 
			context.listIt = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition(conditionList), null, ["caSequenceNum"], null, true);
		} else {
			def currentList = [];
			
			context.listIt.each  { content ->
				def conditionList = [EntityCondition.makeCondition("contentIdTo", content.contentId),
					EntityCondition.makeCondition("contentId", parameters.repContextContentId),
					EntityCondition.makeCondition("contentAssocTypeId", "REP_PERM")];
				
				def assocList = delegator.findList("ContentAssoc", EntityCondition.makeCondition(conditionList), null, ["sequenceNum"], null, true);
				if (UtilValidate.isNotEmpty(assocList)) {
					if (UtilValidate.isEmpty(EntityUtil.filterByCondition(currentList, EntityCondition.makeCondition("contentId", content.contentId)))) {
						currentList.add(content);
					}
				}
			}
			
			context.listIt = currentList;
		}
	}
	context.justLoadList = "Y";
} else {
	context.justLoadList = "N";
}

Debug.log("******************************* getPrintBirtList.groovy -> context.listIt = " + context.listIt);