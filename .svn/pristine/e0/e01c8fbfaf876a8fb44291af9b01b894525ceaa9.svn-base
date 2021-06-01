import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;
def showList = context.showList;

if ("Y".equals(showList) && UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.wenoteNoteInfoTitle = uiLabelMap.FormFieldTitle_noteInfo + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.wenoteNoteInfoLangTitle = uiLabelMap.FormFieldTitle_noteInfo + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";
}
