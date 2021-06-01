import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.wetassCommentsTitle = uiLabelMap.FormFieldTitle_comments + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.wetassCommentsLangTitle = uiLabelMap.FormFieldTitle_comments + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";
}
