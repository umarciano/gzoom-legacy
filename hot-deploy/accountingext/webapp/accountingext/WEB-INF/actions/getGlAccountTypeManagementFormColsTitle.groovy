import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.glAccTypeDescriptionTitle = uiLabelMap.FormFieldTitle_description + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.glAccTypeDescriptionLangTitle = uiLabelMap.FormFieldTitle_description + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";
}
