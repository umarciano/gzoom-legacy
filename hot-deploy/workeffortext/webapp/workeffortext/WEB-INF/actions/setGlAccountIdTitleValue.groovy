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

// Espone consuntivabileParzialmente dalla GlAccount per il campo display nel form dettaglio indicatore (BS).
def glAccountId = context.glAccountId ?: parameters.glAccountId;
if (UtilValidate.isNotEmpty(glAccountId)) {
    try {
        def gl = delegator.findOne("GlAccount", [glAccountId: glAccountId], false);
        context.consuntivabileParzialmente = (gl != null && "Y".equals(gl.consuntivabileParzialmente)) ? "Y" : "N";
    } catch (Exception e) {
        context.consuntivabileParzialmente = "N";
    }
} else {
    context.consuntivabileParzialmente = "N";
}

// Espone parentWorkEffortTypeId nel context (usato da use-when per i campi BS-only nel form dettaglio).
// Il parametro HTTP non viene passato al form di dettaglio: si ricava da wt (WorkEffortAndTypeView)
// caricato dallo screen WorkEffortMeasureViewIndicatorManagementScreen.
if (UtilValidate.isEmpty(context.parentWorkEffortTypeId)) {
    def wt = context.wt;
    if (UtilValidate.isNotEmpty(wt)) {
        context.parentWorkEffortTypeId = wt.workEffortTypeId ?: "";
    }
}
