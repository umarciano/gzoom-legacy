import org.ofbiz.base.util.*;
import java.text.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ServiceUtil;


result = ServiceUtil.returnSuccess();

def isFolderVisible = "N";

def workEffortRoot = delegator.findOne("WorkEffort", ["workEffortId" : parameters.workEffortRootId], false);
if (UtilValidate.isNotEmpty(workEffortRoot)) {
	def workEffortTypeContentConditions = [];
	workEffortTypeContentConditions.add(EntityCondition.makeCondition("workEffortTypeId", workEffortRoot.workEffortTypeId));
	workEffortTypeContentConditions.add(EntityCondition.makeCondition("weTypeContentTypeId", "FOLDER"));
	workEffortTypeContentConditions.add(EntityCondition.makeCondition("contentId", parameters.folder));
	def workEffortTypeContentList = delegator.findList("WorkEffortTypeContent", EntityCondition.makeCondition(workEffortTypeContentConditions), null, null, null, false);
	def workEffortTypeContent = EntityUtil.getFirst(workEffortTypeContentList);
	
	if (UtilValidate.isNotEmpty(workEffortTypeContent)) {
		if ("Y".equals(workEffortTypeContent.isVisible)) {
			isFolderVisible = "Y";
		}
	}
}

result.put("isFolderVisible", isFolderVisible);
return result;