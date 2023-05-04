import org.ofbiz.base.util.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;

context.showUoCode = "MAIN"; //MAIN, EXT, NONE
context.orderUoBy = "MAINCODE";  //MAINCODE, EXTCODE, UONAME
context.orderRootBy = ""; //TIP-CODE-TIT, TIT-TIP-STA, DATE-EXT-TIP-ETCH

def weContextId = UtilValidate.isNotEmpty(context.weContextId) ? context.weContextId : parameters.weContextId;
def parentTypeId = UtilValidate.isNotEmpty(context.parentTypeId) ? context.parentTypeId : parameters.parentTypeId;
if (UtilValidate.isEmpty(weContextId)) {
	weContextId = parentTypeId;
}
if (UtilValidate.isEmpty(weContextId)) {
	weContextId = parameters.weContextId_value;
}
if (weContextId == null) {
    weContextId = "";
}
if (UtilValidate.isNotEmpty(parameters.workEffortId) && !"Y".equals(context.fromAssOrgUnit) && !"Y".equals(parameters.specialized)) {
	weContextId = "";
}

if (UtilValidate.isNotEmpty(weContextId)) {
	WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(context, parameters, delegator);
	paramsEvaluator.evaluateParams(weContextId, "WEFLD_MAIN", false);
}