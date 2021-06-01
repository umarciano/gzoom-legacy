import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

entityName = null;
orderBy = ["orgUnitId", "emplPositionTypeId", "partyName"];
orgUnitId = null;
if (UtilValidate.isNotEmpty(context.orgUnitId)) {
    orgUnitId = context.orgUnitId;
} else if (UtilValidate.isNotEmpty(parameters.orgUnitId)) {
    orgUnitId = parameters.orgUnitId;
}

workEffortPartyPerformanceSummaryList = null;
condition  = [];

if (UtilValidate.isNotEmpty(context.weContextId)) {
    if ("CTX_EP".equals(context.weContextId)) {
        entityName = "PartyAllocationEvaluationPlanNotOpenSummary";
		orderBy = ["emplPositionTypeDescription", "partyName"];
		def defaultPartyRelationshipTypeId = UtilProperties.getPropertyValue("BaseConfig", "EmplPerfInsertFromTemplate.partyRelationshipTypeId", "ORG_EMPLOYMENT");
		condition.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, defaultPartyRelationshipTypeId))
    }
}

if (UtilValidate.isNotEmpty(orgUnitId)) {
    condition.add(EntityCondition.makeCondition("orgUnitId", EntityOperator.EQUALS, orgUnitId));
}
condition.add(EntityCondition.makeCondition("partyResponsibleId", EntityOperator.EQUALS, userLogin.partyId));
condition.add(EntityCondition.makeCondition("partyId", EntityOperator.NOT_EQUAL, userLogin.partyId));

if (UtilValidate.isNotEmpty(entityName)) {
	Debug.log(" - Search Risorse senza Scheda " + entityName + " with condition " + condition);
    context.listIt = delegator.findList(entityName, EntityCondition.makeCondition(condition, EntityOperator.AND), null, orderBy, null, true);
	list = context.listIt;
	newList = [];
	if(UtilValidate.isNotEmpty(list)){
		for(i=0; i< list.size(); i++){
			map = [:];
			map.putAll(list[i]);
			contidtion = EntityCondition.makeCondition(
					EntityCondition.makeCondition("estimated", EntityOperator.EQUALS, list[i].partyId), EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_MANAGER"));
			
			partyEvalRolePermissionViewList = delegator.findList("PartyEvalManagerView", contidtion, null, null, null, true);
			
			if(UtilValidate.isNotEmpty(partyEvalRolePermissionViewList) && UtilValidate.isNotEmpty(partyEvalRolePermissionViewList[0])){
				map.evalManagerPartyId = partyEvalRolePermissionViewList[0].partyId;
			}
			newList.add(map);
		}
	}
	context.listIt = newList;
}
