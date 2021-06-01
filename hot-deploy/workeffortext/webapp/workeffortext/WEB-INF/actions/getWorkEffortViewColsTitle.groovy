import org.ofbiz.base.util.*;

if ("shortDescription".equals(context.etchDescr)) {
	context.descriptionFieldLabel = uiLabelMap.WorkEffortShortDescription;
}
if ("printTitle".equals(context.etchDescr)) {
	context.descriptionFieldLabel = uiLabelMap.WorkEffortReportTitle;
}
if ("longDescription".equals(context.etchDescr)) {
	context.descriptionFieldLabel = uiLabelMap.WorkEffortLongDescription;
}
if ("longTitle".equals(context.etchDescr)) {
	context.descriptionFieldLabel = uiLabelMap.WorkEffortLongTitle;
}

def multiTypeLang = context.multiTypeLang;

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.workEffortNameTitle = getFieldTitle(uiLabelMap.FormFieldTitle_workEffortName, primaryLangFlagPath, primaryLangTooltip);
	context.workEffortNameLangTitle = getFieldTitle("", secondaryLangFlagPath, secondaryLangTooltip);
	
	if (UtilValidate.isNotEmpty(context.etchDescr)) {
		context.descriptionTitle = getFieldTitle(context.descriptionFieldLabel, primaryLangFlagPath, primaryLangTooltip);
		context.descriptionLangTitle = getFieldTitle("", secondaryLangFlagPath, secondaryLangTooltip);
	}
}

def getFieldTitle(fieldLabel, langFlagPath, langTooltip) {
	def fieldTitle = "<table cellspacing='0' cellpadding='0'><tr><td class='base-align-left'>";
	if (UtilValidate.isNotEmpty(fieldLabel)) {
		fieldTitle += fieldLabel;
	} else {
		fieldTitle += "&nbsp;";
	}
	fieldTitle += "</td><td class='base-align-right'>";
	fieldTitle += "&nbsp;&nbsp;<img src='" + langFlagPath + "' title='" +langTooltip + "'/>";
	fieldTitle += "</td></tr></table>";
	
	return fieldTitle;
}
