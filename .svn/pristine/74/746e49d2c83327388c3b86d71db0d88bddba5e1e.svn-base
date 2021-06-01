import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def checkPermission(permission) {
    if (security.hasPermission(permission, userLogin))
        return permission;
    return null;
}

res = "success";
entityNamePrefix = "WorkEffortAchieve";

if (UtilValidate.isNotEmpty(userLogin)) {
	
	parameters.uvUserLoginId = userLogin.userLoginId;
	if (UtilValidate.isEmpty(context.permission)) {
		context.permission = "EMPLPERF";
	}
	
	parameters.entityName = entityNamePrefix + "View";
	
    userLoginPermissionOrg = checkPermission(context.permission + "ORG_ADMIN");
	
    if(UtilValidate.isNotEmpty(userLoginPermissionOrg)){
    // solo permessi per vedere i workEffort tali che:
    // si ha una relazione di tipo ORG_RESPONSIBLE oppure ORG_DELEGATE con orgUnitId del workEffort
        parameters.entityName = entityNamePrefix + "OrgMgrView";
    }
    
}

context.entityName = parameters.entityName;

return;
