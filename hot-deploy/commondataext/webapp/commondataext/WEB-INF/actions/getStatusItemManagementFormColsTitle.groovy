import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;

def statusItemStatusIdAreaStyle = "";
def statusItemActStEnumIdAreaStyle = "";

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.statusItemDescriptionTitle = uiLabelMap.FormFieldTitle_description + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.statusItemDescriptionLangTitle = uiLabelMap.FormFieldTitle_description + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";
	
	statusItemStatusIdAreaStyle = "width70 break-word";
	statusItemActStEnumIdAreaStyle = "width100 break-word";
}

context.statusItemStatusIdAreaStyle = statusItemStatusIdAreaStyle;
context.statusItemActStEnumIdAreaStyle = statusItemActStEnumIdAreaStyle;
