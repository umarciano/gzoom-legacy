import org.ofbiz.base.util.*;

if (UtilValidate.isNotEmpty(context.workEffortType)) {
	if ("Y".equals(context.localeSecondarySet)) {
	    context.workEffortPurposeTypeTitle = UtilValidate.isNotEmpty(context.workEffortType.purposeEtchLang) ? context.workEffortType.purposeEtchLang : uiLabelMap.WorkEffortPurposeType;
	} else {
	    context.workEffortPurposeTypeTitle = UtilValidate.isNotEmpty(context.workEffortType.purposeEtch) ? context.workEffortType.purposeEtch : uiLabelMap.WorkEffortPurposeType;
	}
}
