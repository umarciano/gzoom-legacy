import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.*;

def showHierarchyField = "N";
def showHierarchyIcon = "N";

def workEffortType = context.workEffortType;
if(UtilValidate.isEmpty(workEffortType)) {
	def workEffortTypeId = UtilValidate.isNotEmpty(context.workEffortTypeId) ? context.workEffortTypeId : parameters.workEffortTypeId;
	if(UtilValidate.isNotEmpty(workEffortTypeId)) {
		workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : workEffortTypeId], false);
	}
}

if(UtilValidate.isNotEmpty(workEffortType) && UtilValidate.isNotEmpty(workEffortType.workEffortTypeHierarchyId)) {
	if("Y".equals(workEffortType.showHierarchy)) {
		showHierarchyField = "Y";
	} else {
		showHierarchyIcon = "Y";
	}
}

context.showHierarchyField = showHierarchyField;
context.showHierarchyIcon = showHierarchyIcon;

//se workEffortType.showHierarchy = Y metto nel context il conenuto del campo filiera, da mostrare poi nel form
if("Y".equals(context.showHierarchyField)) {
	def workEffortTypeHierarchyViewList = [];
	
	def workEffortId = UtilValidate.isNotEmpty(parameters.workEffortId) ? parameters.workEffortId : context.workEffortId;
	if (UtilValidate.isNotEmpty(workEffortId)) {
		def condList = [];
		condList.add(EntityCondition.makeCondition("workEffortId", workEffortId));
		condList.add(EntityCondition.makeCondition("workEffortRevisionId", GenericEntity.NULL_FIELD));
		workEffortTypeHierarchyViewList = delegator.findList("WorkEffortTypeHierarchyView", EntityCondition.makeCondition(condList), null, null, null, false);
	}

	context.typeHierarchyField = getTypeHierarchyField(workEffortTypeHierarchyViewList);
}



def getTypeHierarchyField(workEffortTypeHierarchyViewList) {
	def typeHierarchyField = "";
	
	if (UtilValidate.isNotEmpty(workEffortTypeHierarchyViewList)) {
		for (typeHierarchyItem in workEffortTypeHierarchyViewList) {
			if (UtilValidate.isNotEmpty(typeHierarchyItem)) {
				typeHierarchyField += getTypeHierarchyItemStr(typeHierarchyItem);
			}
		}
	}
	
	return typeHierarchyField;
}

def getTypeHierarchyItemStr(typeHierarchyItem) {
	def typeHierarchyItemStr = "";
	for(i = 7; i >= 1; i--) {
		def weFromName = typeHierarchyItem['weFromName' + i];
		def weFromTypeEtch = typeHierarchyItem['weFromTypeEtch' + i];
		def weFromEtch = typeHierarchyItem['weFromEtch' + i];
		
		if (UtilValidate.isNotEmpty(weFromName)) {
			if (UtilValidate.isNotEmpty(weFromTypeEtch)) {
				typeHierarchyItemStr += weFromTypeEtch + ": ";
			}
			if (UtilValidate.isNotEmpty(weFromEtch)) {
				typeHierarchyItemStr += weFromEtch + " - ";
			}
			typeHierarchyItemStr += weFromName + "\n";
		}
	}
	return typeHierarchyItemStr;
}
