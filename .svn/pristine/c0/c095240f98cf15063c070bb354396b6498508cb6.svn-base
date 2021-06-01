import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;



//context.childFilter = "partyIdFrom=${parameters.partyIdTo}|roleTypeIdFrom=${parameters.roleTypeIdTo}|partyRelationshipTypeId=${parameters.currentPartyRelationshipTypeId}";

parameters.sortField = "roleTypeIdTo|parentRoleCode";	

context.inputFields.partyIdFrom = parameters.partyIdTo;
context.inputFields.roleTypeIdFrom = parameters.roleTypeIdTo;
context.inputFields.partyRelationshipTypeId = parameters.currentPartyRelationshipTypeId;

context.managementChildExtraParams = "partyIdTo=${parameters.partyIdTo}|roleTypeIdTo=${parameters.roleTypeIdTo}|partyIdFrom=${parameters.partyIdFrom}|roleTypeIdFrom=${parameters.roleTypeIdFrom}";


GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);

