import org.ofbiz.base.util.*;

if(UtilValidate.isNotEmpty(parameters.snapshot) && parameters.snapshot == "Y"){
    request.setAttribute("workEffortId", parameters.workEffortIdSnapShot);
}else{
    request.setAttribute("workEffortId", parameters.worEffortParentIdFrom);
}
request.setAttribute("selectedId", "");

if ("Y".equals(parameters.ignoreFolderIndex)) {
    request.setAttribute("folderIndex", "");  
}

return "success";