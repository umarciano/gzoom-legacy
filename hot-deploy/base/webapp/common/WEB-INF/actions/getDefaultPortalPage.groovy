import java.awt.List;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.GenericEntity;

Debug.log("******************************* getDefaultPortalPage.groovy -> userLoginId = " + userLogin.userLoginId);
	
if(UtilValidate.isEmpty(userLogin.userLoginId)) {
	return;
}
def conditionList = [EntityCondition.makeCondition("userLoginId", userLogin.userLoginId),
	EntityCondition.makeCondition("thruDate", GenericEntity.NULL_FIELD),
	EntityCondition.makeCondition("defaultPortalPageId", EntityOperator.NOT_EQUAL, GenericEntity.NULL_FIELD)];

list = delegator.findList("UserLoginSecurityGroupView", EntityCondition.makeCondition(conditionList), null, ["fromDate"], null, true);


if (UtilValidate.isNotEmpty(list) && list.size() > 0) {
	first = list[0];
	parameters.portalPageId = first.defaultPortalPageId;
				
}	

