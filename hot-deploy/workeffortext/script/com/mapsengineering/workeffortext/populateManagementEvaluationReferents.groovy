import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import com.mapsengineering.base.birt.util.Utils;

Debug.log(" Run sync service executePerformFindPartyRelationshipEvaluated ENTRATA ");


def partyRelationshipTypeId = UtilProperties.getPropertyValue("BaseConfig.properties", "EmplPerfInsertFromTemplate.partyRelationshipTypeId", "ORG_EMPLOYMENT");
def fromDate = ObjectType.simpleTypeConvert(parameters.fromDate, "Timestamp", null, locale);
def thruDate = ObjectType.simpleTypeConvert(parameters.thruDate, "Timestamp", null, locale);
def isOrgMgr = security.hasPermission("EMPLPERFORG_ADMIN", userLogin);

res = "success";
Debug.log(" Run sync service executePerformFindPartyRelationshipEvaluated ENTRATA " + parameters.insertMode + " - " + context.insertMode);

if ("Y".equals(parameters.insertMode)) {
	return res;
}

serviceMap = ["orgUnitId": parameters.orgUnitId,
		              "partyIdFrom": parameters.partyIdFrom,
		              "partyIdTo": parameters.partyIdTo,
		              "fromDate": fromDate,
		              "thruDate": thruDate,
					  "isOrgMgr": isOrgMgr,
		              "onlyCurrentEvalRef": parameters.onlyCurrentEvalRef,
					  "partyRelationshipTypeId": partyRelationshipTypeId,
					  "timeZone": context.timeZone,
					  "userLogin": context.userLogin];
				  
Debug.log(" Run sync service executePerformFindPartyRelationshipEvaluated with "+ serviceMap + ", userLoginId =" + context.userLogin.userLoginId);
				  
def serviceRes = dispatcher.runSync("executePerformFindPartyRelationshipEvaluated", serviceMap);

request.setAttribute('listIt',serviceRes.rowList);

return res;