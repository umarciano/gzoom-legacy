import org.ofbiz.base.util.*;

def multiTypeLang = context.multiTypeLang;

def glFisTypeIsUsedAreaStyle = "";

if (UtilValidate.isNotEmpty(multiTypeLang) && !"NONE".equals(multiTypeLang)) {
	def primaryLangFlagPath = context.primaryLangFlagPath;
	def secondaryLangFlagPath = context.secondaryLangFlagPath;
	def primaryLangTooltip = context.primaryLangTooltip;
	def secondaryLangTooltip = context.secondaryLangTooltip;
	
	context.glFisTypeDescriptionTitle = uiLabelMap.FormFieldTitle_description + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + primaryLangFlagPath + "' title='" +primaryLangTooltip + "'/>";
	context.glFisTypeDescriptionLangTitle = uiLabelMap.FormFieldTitle_description + "&nbsp;&nbsp;&nbsp;&nbsp;<img src='" + secondaryLangFlagPath + "' title='" +secondaryLangTooltip + "'/>";
	
	glFisTypeIsUsedAreaStyle = "width70 break-word";
}

context.glFisTypeIsUsedAreaStyle = glFisTypeIsUsedAreaStyle;
