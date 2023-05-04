import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def contentCondList = [];
contentCondList.add(EntityCondition.makeCondition("contentTypeId", "FOLDER"));
def contentList = delegator.findList("Content", EntityCondition.makeCondition(contentCondList), null, null, null, false);
def folders = EntityUtil.getFieldListFromEntityList(contentList, "contentId", true);

if ("Y".equals(context.rootInqyTree)) {
	if (UtilValidate.isNotEmpty(folders)) {
		folders.each {folder ->
		    context["titleStyle_" + folder] = "label-gray";
		}
	}
} else {
	def workEffortTypeStatusCondList = [];
	workEffortTypeStatusCondList.add(EntityCondition.makeCondition("workEffortTypeId", context.workEffortTypeId));
	workEffortTypeStatusCondList.add(EntityCondition.makeCondition("statusId", context.currentStatusId));
	def workEffortTypeStatusCntList = delegator.findList("WorkEffortTypeStatusCnt", EntityCondition.makeCondition(workEffortTypeStatusCondList), null, null, null, false);
	if (UtilValidate.isNotEmpty(folders)) {
		folders.each {folder ->
		   if (UtilValidate.isNotEmpty(folder)) {
			   def titleStyle = "";
			   if (UtilValidate.isNotEmpty(workEffortTypeStatusCntList)) {
				   workEffortTypeStatusCntList.each {workEffortTypeStatusCntItem ->
					   if (UtilValidate.isNotEmpty(workEffortTypeStatusCntItem) && folder.equals(workEffortTypeStatusCntItem.contentId)) {
						   if("NONE".equals(workEffortTypeStatusCntItem.crudEnumId)) {
							   titleStyle = "label-gray";
						   }
					   }
				   }
			   }
			   context["titleStyle_" + folder] = titleStyle;
		   }
		}	
	}	
}
