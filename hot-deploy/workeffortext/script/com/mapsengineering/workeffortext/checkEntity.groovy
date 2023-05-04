import org.ofbiz.base.util.*;

res = "success";

if ("WorkEffortRootInqyView".equals(parameters.entityName)) {
    res = "workEffortRootInqy";
	request.setAttribute("entityNamePrefix", "WorkEffortRootInqy");
} else if ("WorkEffortRootView".equals(parameters.entityName)) {
    res = "workEffortRoot";
    request.setAttribute("entityNamePrefix", "WorkEffortRoot");
} else if ("WorkEffortRootTemplateView".equals(parameters.entityName)) {
	res = "workEffortRoot";
	request.setAttribute("entityNamePrefix", "WorkEffortRootTemplate");
}  else if ("WorkEffortView".equals(parameters.entityName)) {
    res = "workEffortView";
}  else if("QueryConfigView".equals(parameters.entityName) && UtilValidate.isNotEmpty(parameters.queryCtx)) {
	res = "queryConfigView";
}

return res;
