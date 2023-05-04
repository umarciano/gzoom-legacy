import org.ofbiz.base.util.*;

def commentTitle = ! "Y".equals(parameters.detail) && UtilValidate.isNotEmpty(parameters.weTypeSubId) ? uiLabelMap.WeAssAssCommentSubIdTitle : uiLabelMap.FormFieldTitle_comments;
context.commentTitle = commentTitle;

def multiTypeLang = context.multiTypeLang;

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.weassCommentsTitle = getFieldTitle(context.commentTitle, primaryLangFlagPath, primaryLangTooltip);
	context.weassCommentsLangTitle = getFieldTitle("", secondaryLangFlagPath, secondaryLangTooltip);
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
