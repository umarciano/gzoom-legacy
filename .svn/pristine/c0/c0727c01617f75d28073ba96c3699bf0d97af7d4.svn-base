import org.ofbiz.base.util.*;

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
    workEffortType = delegator.findOne("WorkEffortType", [ workEffortTypeId : workEffortTypeId ], false);
    if("Y".equals(workEffortType.isTemplate) && "CTX_EP".equals(workEffortType.parentTypeId)) {
        context.statusTypeId = "WE_STATUS_EVALTEMPL";
    }
    if("Y".equals(workEffortType.isTemplate) && "CTX_OR".equals(workEffortType.parentTypeId)) {
        context.statusTypeId = "WE_STATUS_ORGTEMPL";
    }
    if("Y".equals(workEffortType.isTemplate) && "CTX_BS".equals(workEffortType.parentTypeId)) {
        context.statusTypeId = "WE_STATUS_PERFTEMPL";
    }
    context.roleTypeId = "WEM%";
    context.isTemplate = workEffortType.isTemplate;
    //Debug.log("****** context.workEffortTypeRootId" +context.workEffortTypeRootId);
    //Debug.log("****** context.workEffortTypeId" +context.workEffortTypeId);
    //Debug.log("****** workEffortType.isTemplate" +workEffortType.isTemplate);
    //Debug.log("****** workEffortType.parentTypeId" +workEffortType.parentTypeId);
    //Debug.log("****** context.statusTypeId" +context.statusTypeId);
    //Debug.log("****** context.roleTypeId" +context.roleTypeId);
}

