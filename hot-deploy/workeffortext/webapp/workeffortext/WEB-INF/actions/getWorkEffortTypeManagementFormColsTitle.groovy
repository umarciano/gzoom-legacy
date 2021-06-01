import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.wetDescriptionTitle = getFieldTitle(uiLabelMap.FormFieldTitle_description, primaryLangFlagPath, primaryLangTooltip);
	context.wetDescriptionLangTitle = getFieldTitle("", secondaryLangFlagPath, secondaryLangTooltip);
	
	context.wetEtchTitle = getFieldTitle(uiLabelMap.FormFieldTitle_etch, primaryLangFlagPath, primaryLangTooltip);
	context.wetEtchLangTitle = getFieldTitle("", secondaryLangFlagPath, secondaryLangTooltip);
	
	context.wetNoteTitle = getFieldTitle(uiLabelMap.FormFieldTitle_note, primaryLangFlagPath, primaryLangTooltip);
	context.wetNoteLangTitle = getFieldTitle("", secondaryLangFlagPath, secondaryLangTooltip);
	
	context.wetPurposeEtchTitle = getFieldTitle(uiLabelMap.FormFieldTitle_purposeEtch, primaryLangFlagPath, primaryLangTooltip);
	context.wetPurposeEtchLangTitle = getFieldTitle("", secondaryLangFlagPath, secondaryLangTooltip);
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
