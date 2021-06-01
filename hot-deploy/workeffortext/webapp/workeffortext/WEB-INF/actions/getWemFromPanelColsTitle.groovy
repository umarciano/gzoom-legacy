import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.wemUomDescrTitle = uiLabelMap.Indicator_uomDescr + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.wemUomDescrLangTitle = uiLabelMap.Indicator_uomDescr + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";
}

def showDescr = context.showDescr;
if ("Y".equals(showDescr)) {
	if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
		context.showUomDescr = "N";
		context.showUomDescrMultiTypeLang = "Y";
	} else {
		context.showUomDescr = "Y";
		context.showUomDescrMultiTypeLang = "N";
	}
} else {
	context.showUomDescr = "N";
	context.showUomDescrMultiTypeLang = "N";
}
