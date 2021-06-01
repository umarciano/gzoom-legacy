import org.ofbiz.base.util.*;

def isWorkEffortAssocReadOnly(workEffortIdFieldName, workEffortViewFieldName, activationFieldName) {

    if (UtilValidate.isEmpty(context.get(activationFieldName))) {
        if (UtilValidate.isEmpty(context.get(workEffortViewFieldName)))
            context.put(workEffortViewFieldName, delegator.findOne("WorkEffortView", [ workEffortId : context.get(workEffortIdFieldName) ], false));
        workEffortView = context.get(workEffortViewFieldName);
        if (UtilValidate.isNotEmpty(workEffortView))
            context.put(activationFieldName, workEffortView.weActivation);
    }

    weActivation = context.get(activationFieldName);
    return (
        "ACTSTATUS_CLOSED".equals(weActivation)
        || "ACTSTATUS_REPLACED".equals(weActivation)
    );
}

context.isActStReadOnly = false;

if (!"Y".equals(context.get("insertMode"))) {

    context.isActStReadOnly = (
        isWorkEffortAssocReadOnly("workEffortIdFrom", "workEffortViewFrom", "wrFromActivation")
        || isWorkEffortAssocReadOnly("workEffortIdTo", "workEffortViewTo", "wrToActivation")
    ) || (UtilValidate.isNotEmpty(context.isReadOnly) && context.isReadOnly);
}
