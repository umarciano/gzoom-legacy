import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***************************************************** executeLookupPerformFindPartyRole.groovy -> context.executePerformFindScriptName = " +  context.executePerformFindScriptName);

lookup = context.lookup;
if (UtilValidate.isEmpty(lookup)) {
    lookup = parameters.lookup;
}

if (! "ORGANIZATION_UNIT".equals(parameters.parentRoleTypeId)) {
	parameters.organizationId = null;
}

if ("Y".equals(lookup)) {
    keys = parameters.keySet();

	for (key in keys) {
		if ((key.startsWith("partyRelationshipTypeIdFrom") || key.startsWith("roleTypeIdFrom") || key.startsWith("partyIdFrom")
			 || key.startsWith("partyRelationshipTypeIdTo") || key.startsWith("roleTypeIdTo") || key.startsWith("partyIdTo"))
		 && UtilValidate.isNotEmpty(parameters[key])) {
			parameters.entityName = parameters.entityName + "RelationshipRoleView";
			break;
		}
	}
	
    for (key in keys) {
        if ((key.startsWith("partyEmail") || key.startsWith("userLoginId")) && key.endsWith("value") && UtilValidate.isNotEmpty(parameters[key])) {
            parameters.entityName = parameters.entityName + "ContactMechUserLogin";
			break;
        }
    }
    context.executePerformFindScriptName = null;
    context.executePerformFind = "Y";

    res = GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeLookupPerformFind.groovy", context);

//    Debug.log("***************************************************** executeLookupPerformFindPartyRole.groovy -> listIt = " + context.listIt);
}

return res;