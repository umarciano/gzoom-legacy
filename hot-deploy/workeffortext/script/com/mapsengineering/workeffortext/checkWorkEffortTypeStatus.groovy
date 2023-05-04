import org.ofbiz.base.util.*;
import java.text.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ServiceUtil;
import com.mapsengineering.base.util.*;
import javolution.util.FastMap;

result = ServiceUtil.returnSuccess();
def statusId = "";
def security = dctx.getSecurity();

/** Recupero params del workEffortTypeStatus.params 
 * il campo e' utilizzato per contenere i possibili valori del duplicateAdmit default = "N", possibili valori = COPY, CLONE, SNAPSHOT
 * e il nextLevelAtOpen
 * 
 * duplicateAdmit = "N"; // COPY, CLONE, SNAPSHOT
 * nextLevelAtOpen = "N"; // Y, gestito in checkWorkEffortTypeStatus.groovy
 * */
def partyId = userLogin.partyId;
def workEffortAndTypeStatusViewList = delegator.findList("WorkEffortAndTypeStatusView", EntityCondition.makeCondition("workEffortId", parameters.workEffortId), null, null, null, false);
def workEffortAndTypeStatusView = EntityUtil.getFirst(workEffortAndTypeStatusViewList);
if (UtilValidate.isNotEmpty(workEffortAndTypeStatusView)) {
    if ("ROLE".equals(workEffortAndTypeStatusView.managWeStatusEnumId)) {
        condList = [];
        condList.add(EntityCondition.makeCondition("workEffortId", parameters.workEffortId));
        condList.add(EntityCondition.makeCondition("roleTypeId", workEffortAndTypeStatusView.managementRoleTypeId));
        condList.add(EntityCondition.makeCondition("partyId", partyId));
        def wepaList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition(condList), null, null, null, false);
        def wepa = EntityUtil.getFirst(wepaList);
        Debug.log(" Found " + wepaList.size() + " wepa with condList " + condList);
    	if (UtilValidate.isNotEmpty(wepa) && UtilValidate.isNotEmpty(workEffortAndTypeStatusView.workEffortId) && workEffortAndTypeStatusView.workEffortId.equals(workEffortAndTypeStatusView.workEffortParentId) && "Y".equals(workEffortAndTypeStatusView.isRoot)) {
			def paramsMap = FastMap.newInstance();
			BshUtil.eval(workEffortAndTypeStatusView.params, paramsMap);
			if ("Y".equals(paramsMap.nextLevelAtOpen)) {			
				def orderBy = ["sequenceId"];
				def statusList = delegator.findList("StatusItemAndValidChangeStatusTo", EntityCondition.makeCondition("statusId", workEffortAndTypeStatusView.currentStatusId), null, orderBy, null, false);						
				def statusItem = EntityUtil.getFirst(statusList);
				if (UtilValidate.isNotEmpty(statusItem)) {
					statusId = statusItem.statusIdTo;
				}
			}			
		}
	}
}

result.put("statusId", statusId);
return result;
