import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.uomCodeTitle = uiLabelMap.FormFieldTitle_uomCode + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.uomCodeLangTitle = uiLabelMap.FormFieldTitle_uomCode + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";

	context.uomDescrTitle = uiLabelMap.UomRatingScale_uomDescr + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.uomDescrLangTitle = uiLabelMap.UomRatingScale_uomDescr + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";
}
