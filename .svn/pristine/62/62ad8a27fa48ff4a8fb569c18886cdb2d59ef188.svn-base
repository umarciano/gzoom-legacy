import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

res = "success";

parameters.uvUserLoginId = userLogin.userLoginId;
context.executePerformFindScriptName = null;
context.executePerformFind = "Y";

res = GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeLookupPerformFind.groovy", context);

lista = context.listIt;

/* GN-GN-890
conditionNotAllOrg = EntityCondition.makeCondition(
        EntityCondition.makeCondition("allOrgAssigned", EntityOperator.NOT_EQUAL, 'Y'),
        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameters.afterOrgUnitId),
        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, parameters.afterRoleTypeId),
        EntityCondition.makeCondition("partyAssWorkEffortId", EntityOperator.NOT_EQUAL, 'NULL')
    );
conditionOrg = EntityCondition.makeCondition(
        EntityCondition.makeCondition("allOrgAssigned", EntityOperator.EQUALS, 'Y'),
        EntityOperator.OR,
        conditionNotAllOrg
    );

conditionNotAllRoles = EntityCondition.makeCondition(
        EntityCondition.makeCondition("allRolesAssigned", EntityOperator.NOT_EQUAL, 'Y'),
        EntityCondition.makeCondition("evalPartyId", EntityOperator.EQUALS, parameters.afterEvalPartyId),
        EntityCondition.makeCondition("roleAssWorkEffortId", EntityOperator.NOT_EQUAL, 'NULL')
    );
conditionRoles = EntityCondition.makeCondition(
        EntityCondition.makeCondition("allRolesAssigned", EntityOperator.EQUALS, 'Y'),
        EntityOperator.OR,
        conditionNotAllRoles
    );

condition = EntityCondition.makeCondition(
        conditionOrg,
        EntityOperator.AND,
        conditionRoles
    );
    
// potrei avere lo stesso workEffortId piu volte quindi applico distinzione
lista = EntityUtil.filterByCondition(lista, condition);
    *
    */


lista = EntityUtil.getFieldListFromEntityList(lista, 'workEffortId', true);
lista = delegator.findList("WorkEffortView", EntityCondition.makeCondition("workEffortId", EntityOperator.IN, lista), null, null, null, true);
context.listIt = lista;

return res;