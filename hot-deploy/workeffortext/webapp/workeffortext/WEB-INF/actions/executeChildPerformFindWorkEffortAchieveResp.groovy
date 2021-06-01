import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


context.listIt = delegator.findList(parameters.entityName, EntityCondition.makeCondition("workEffortId", parameters.workEffortId), null, null, null, true);

context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition("partyName", EntityOperator.NOT_EQUAL, parameters.partyName));
