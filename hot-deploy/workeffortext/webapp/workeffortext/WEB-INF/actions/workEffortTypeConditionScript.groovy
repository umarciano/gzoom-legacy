import org.ofbiz.base.util.*;

screenNameListIndex = "";
workEffortTypeId = parameters.workEffortTypeId;
if(UtilValidate.isEmpty(workEffortTypeId)) {
    workEffortTypeId = context.workEffortTypeId;
}
if(UtilValidate.isEmpty(workEffortTypeId)) {
    workEffortTypeId = parameters.workEffortTypeRootId;
}
if(UtilValidate.isEmpty(workEffortTypeId)) {
    workEffortTypeId = context.workEffortTypeRootId;
}
if(UtilValidate.isNotEmpty(workEffortTypeId)) {
    workEffortType = delegator.findOne("WorkEffortType", [ workEffortTypeId : workEffortTypeId ], true);
    if(UtilValidate.isNotEmpty(workEffortType) && UtilValidate.isNotEmpty(workEffortType.parentTypeId)) {
        if(workEffortType.parentTypeId.startsWith("CTX") && "Y".equals(workEffortType.isRoot)) {
            screenNameListIndex = "1";
        }
    }
}
return screenNameListIndex;