import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

if ("EvaluationReferentsView".equals(parameters.entityName)){
	hasPermissionMgr = security.hasPermission("EMPLPERFMGR_ADMIN", userLogin);
	hasPermissionOrg = security.hasPermission("EMPLPERFORG_ADMIN", userLogin);
	if(!hasPermissionOrg && !hasPermissionMgr){
		res = "succesStartSearch";
	}
} else{
	res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/checkEntityAdmin.groovy", context);
}
return res;
