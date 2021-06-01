import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import javolution.util.FastList;

def isInsertModeField = context.isInsertMode;
def isInsertMode = false;

if (isInsertModeField instanceof Boolean) {
	isInsertMode = isInsertModeField;
} else if (isInsertModeField instanceof String) {
	isInsertMode = "true".equalsIgnoreCase(isInsertModeField);
}

if (isInsertMode == true) {
	def searchDate = parameters.searchDate;
	if (UtilValidate.isNotEmpty(searchDate)) {
		context.noteDateTime = searchDate;
	}
	
	def workEffortTypeId = "";
	def workEffortId = UtilValidate.isNotEmpty(context.workEffortId) ? context.workEffortId : parameters.workEffortId;
	def noteContentId = UtilValidate.isNotEmpty(context.noteContentId) ? context.noteContentId : parameters.noteContentId;
	
	if (UtilValidate.isNotEmpty(workEffortId)) {
		def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
		if (UtilValidate.isNotEmpty(workEffort)) {
			workEffortTypeId = workEffort.workEffortTypeId;
			if (UtilValidate.isEmpty(searchDate)) {
				context.noteDateTime = workEffort.estimatedStartDate;
			}
		}
	}
	if (UtilValidate.isNotEmpty(workEffortTypeId)) {
		def condList = FastList.newInstance();
		condList.add(EntityCondition.makeCondition("workEffortTypeId", workEffortTypeId));
		condList.add(EntityCondition.makeCondition("contentId", noteContentId));
		condList.add(EntityCondition.makeCondition("isMain", "N"));
		
		def typeAttrList = delegator.findList("WorkEffortTypeAttrAndNoteData", EntityCondition.makeCondition(condList), null, ["sequenceId"], null, false);
		def typeAttr = EntityUtil.getFirst(typeAttrList);				
		if (UtilValidate.isNotEmpty(typeAttr)) {
			def multiTypeLang = context.multiTypeLang;
			def localeSecondarySet = context.localeSecondarySet;
			
			context.noteName = typeAttr.attrName;
			if (UtilValidate.isNotEmpty(multiTypeLang) && ! "NONE".equals(multiTypeLang)) {
				context.noteNameLang = typeAttr.attrNameLang;
			}
			if (UtilValidate.isNotEmpty(typeAttr.noteInfo)) {
				context.noteInfo = typeAttr.noteInfo;
			}
			if (UtilValidate.isNotEmpty(typeAttr.noteInfoLang) && UtilValidate.isNotEmpty(multiTypeLang) && ! "NONE".equals(multiTypeLang)) {
				context.noteInfoLang = typeAttr.noteInfoLang;
			}
			
			context.isHtml = typeAttr.isHtml;
			context.isMain = typeAttr.isMain;
			context.sequenceId = typeAttr.sequenceId;
			context.internalNote = typeAttr.internalNote;
		}
	}	
}