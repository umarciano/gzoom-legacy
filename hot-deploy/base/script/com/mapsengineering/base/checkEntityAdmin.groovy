import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

res = "success";
listaEntityNoStartSearch = ["WorkEffortType", "WorkEffortTypePeriodView", "WorkEffortRootInqyView", "WorkEffortAssignmentContentView"];

/**
 * Elimino i casi dove nn voglio la ricerca automanica
 */

if (!listaEntityNoStartSearch.contains(parameters.entityName)){
	
	/**
	 * Controllo se ho il gruppo di FULLADMIN, o i permessi di webappMGR_ADMIN  se non ho questo gruppo allora faccio partire la ricerca automatica
	 */
	
	if (UtilValidate.isEmpty(context.permission)) {
		context.permission = "WORKEFFORT";
	}

	hasPermission = security.hasPermission(context.permission + "MGR_ADMIN", userLogin);
	
	
	listSecurityGroup = EntityUtil.filterByDate(delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition(
																							 EntityCondition.makeCondition("userLoginId", userLogin.userLoginId), 
																							 EntityCondition.makeCondition("groupId", "FULLADMIN")), null, null, null, true));
																						 
	if(UtilValidate.isEmpty(listSecurityGroup) && !hasPermission){
		res = "succesStartSearch"
	}
	
}


//Debug.log("##### res= "+ res);
return res;