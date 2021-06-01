import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

orgUnitId = null;
if (UtilValidate.isNotEmpty(context.orgUnitId)) {
    orgUnitId = context.orgUnitId;
} else if (UtilValidate.isNotEmpty(parameters.orgUnitId)) {
    orgUnitId = parameters.orgUnitId;
}

condition = EntityCondition.makeCondition("partyResponsibleId", EntityOperator.EQUALS, userLogin.partyId);
if (UtilValidate.isNotEmpty(orgUnitId)) {
    condition = EntityCondition.makeCondition([condition, EntityCondition.makeCondition("orgUnitId", EntityOperator.EQUALS, orgUnitId)]);
}

if (UtilValidate.isNotEmpty(entityName)) {
    context.listIt = delegator.findList(entityName, condition, null, ["orgUnitId", "emplPositionTypeId", "partyName"], null, true);

//    Debug.log("************************************************* context.listIt = " + context.listIt);
}
