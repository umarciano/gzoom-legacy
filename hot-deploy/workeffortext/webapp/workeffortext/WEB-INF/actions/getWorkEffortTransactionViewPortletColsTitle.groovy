import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.weTransCommentTitle = uiLabelMap.FormFieldTitle_weTransReferences + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.weTransCommentLangTitle = "<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";
	
    context.weTransCommentsTitle = uiLabelMap.FormFieldTitle_weTransComments + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.weTransCommentsLangTitle = "<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";
}
