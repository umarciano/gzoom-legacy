import org.ofbiz.base.util.*;

if ("Y".equals(parameters.get("insertMode"))) {

    if (UtilValidate.isEmpty(context.get("workEffortView")))
        context.workEffortView = delegator.findOne("WorkEffortView", true, "workEffortId", context.get("workEffortId"));

    if (UtilValidate.isNotEmpty(context.get("workEffortView"))) {
        context.effortUomId = context.workEffortView.effortUomId
        context.estimatedTotalEffort = context.workEffortView.estimatedTotalEffort;
    }
}

if (UtilValidate.isNotEmpty(context.get("effortActual")) && UtilValidate.isNotEmpty(context.get("effortReview")))
    context.forecastEffort = context.effortActual + context.effortReview;

if (UtilValidate.isEmpty(context.get("percentComplete"))) {
    if (
        UtilValidate.isNotEmpty(context.get("effortActual"))
        && UtilValidate.isNotEmpty(context.get("forecastEffort"))
        && context.forecastEffort != 0
    )
        context.percentComplete = (100 * context.effortActual / context.forecastEffort);
}
