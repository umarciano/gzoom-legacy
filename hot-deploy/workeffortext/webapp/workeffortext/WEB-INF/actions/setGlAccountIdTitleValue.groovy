import org.ofbiz.base.util.*;

Debug.log("setGlAccountIdTitleValue.groovy context.etchDescr " + context.etchDescr);
def glAccountIdTitleValue = uiLabelMap["FormFieldTitle_glAccountId"];
if (UtilValidate.isEmpty(context.etchDescr)) {
	def layoutType = context.layoutType;
	if(UtilValidate.isNotEmpty(layoutType) && UtilValidate.isNotEmpty(context[layoutType + "_title"])) {
		glAccountIdTitleValue = context[layoutType + "_title"];
	}
}
if ("indicType".equals(context.etchDescr)) {
	glAccountIdTitleValue = uiLabelMap["WemTypeIndicator"];
} else if ("calcRule".equals(context.etchDescr)) {
	glAccountIdTitleValue = uiLabelMap["WemRuleCalculation"];
} else if ("formula".equals(context.etchDescr)) {
    glAccountIdTitleValue = uiLabelMap["WemFormulaCalculation"];
}
context.glAccountIdTitleValue = glAccountIdTitleValue;
