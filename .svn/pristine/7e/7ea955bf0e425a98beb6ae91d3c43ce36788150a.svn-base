import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	def localeSecondarySet = context.localeSecondarySet;
	
	if (UtilValidate.isNotEmpty(context.noteId1)) {
		def noteName1 = "Y".equals(localeSecondarySet) ? context.noteName1Lang : context.noteName1;
		context.noteInfo1Title = getFieldTitle(noteName1, primaryLangFlagPath, primaryLangTooltip);
		context.noteInfo1LangTitle = getFieldTitle("", secondaryLangFlagPath, secondaryLangTooltip);
	}
	if (UtilValidate.isNotEmpty(context.noteId2)) {
		def noteName2 = "Y".equals(localeSecondarySet) ? context.noteName2Lang : context.noteName2;
		context.noteInfo2Title = getFieldTitle(noteName2, primaryLangFlagPath, primaryLangTooltip);
		context.noteInfo2LangTitle = getFieldTitle("", secondaryLangFlagPath, secondaryLangTooltip);
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
