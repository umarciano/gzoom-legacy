import org.ofbiz.base.util.*;

def glAccountIdTitleValue = uiLabelMap["FormFieldTitle_glAccountId"];
if (UtilValidate.isEmpty(context.etchDescr)) {
	def layoutType = context.layoutType;
	if(UtilValidate.isNotEmpty(layoutType) && UtilValidate.isNotEmpty(context[layoutType + "_title"])) {
		glAccountIdTitleValue = context[layoutType + "_title"];
	}
}
if ("indicType".equals(context.etchDescr)) {
	glAccountIdTitleValue = uiLabelMap["WemTypeIndicator"];
}
if ("calcRule".equals(context.etchDescr)) {
	glAccountIdTitleValue = uiLabelMap["WemRuleCalculation"];
}
context.glAccountIdTitleValue = glAccountIdTitleValue;
