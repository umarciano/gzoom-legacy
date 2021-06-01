import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

def res = "success";

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/executePerformFind.groovy", context);

if (UtilValidate.isNotEmpty(context.listIt)) {
	context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition("workEffortSnapshotId", null));
}

/*
 *  Adesso posso cercare sia per responsibile che per tipo
 *  Ricerca per responsabile, per ogni scheda controllo se è responsabile quello inserito e se appartiene alla data della scheda
 * */
 if ( (UtilValidate.isNotEmpty(parameters.responsibleRoleTypeId) || UtilValidate.isNotEmpty(parameters.responsiblePartyId)) && UtilValidate.isNotEmpty(context.listIt)) {
     
     Debug.log("********** ricerca per responsabile parameters.responsibleRoleTypeId="+parameters.responsibleRoleTypeId);
     Debug.log("********** ricerca per responsabile parameters.responsiblePartyId="+parameters.responsiblePartyId);
     def list = [];
     
     for (int i=0; i < context.listIt.size(); i++ ) {
         def element = context.listIt.get(i);
         
         def prCond = [];
         prCond.add(EntityCondition.makeCondition("partyRelationshipTypeId", "ORG_RESPONSIBLE"));
         prCond.add(EntityCondition.makeCondition("partyIdFrom", element.orgUnitId));
         prCond.add(EntityCondition.makeCondition("roleTypeIdFrom", element.orgUnitRoleTypeId));
         prCond.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, element.estimatedCompletionDate));
         
         if (UtilValidate.isNotEmpty(parameters.responsibleRoleTypeId)) {
             prCond.add(EntityCondition.makeCondition("roleTypeIdTo", parameters.responsibleRoleTypeId));
         }
         if (UtilValidate.isNotEmpty(parameters.responsiblePartyId)) {
             prCond.add(EntityCondition.makeCondition("partyIdTo", parameters.responsiblePartyId));
         }
         
         prCond.add(EntityCondition.makeCondition(
             EntityCondition.makeCondition("thruDate", null),
             EntityOperator.OR,
             EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, element.estimatedCompletionDate)
             ));
         
         def prList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(prCond), null, null, null, false);
         if (UtilValidate.isNotEmpty(prList)) {
             list.add(element);
         }
     }
     // rimmetto la lista filtrata!!
     context.listIt = list;
 }
 

/*
 * GN-758 gestione filtri ruolo su obiettivo
 */
if (UtilValidate.isNotEmpty(context.listIt)) {
	def weAssRoleConditionList = [];
	//filtro tipo ruolo
	if (UtilValidate.isNotEmpty(parameters.weResponsibleRoleTypeId)) {
	    weAssRoleConditionList.add(EntityCondition.makeCondition("roleTypeId", parameters.weResponsibleRoleTypeId));
	}
	//filtro tipo ruolo
	if (UtilValidate.isNotEmpty(parameters.weResponsiblePartyId)) {
	    weAssRoleConditionList.add(EntityCondition.makeCondition("partyId", parameters.weResponsiblePartyId));
	}
	
	if (UtilValidate.isNotEmpty(weAssRoleConditionList)) {
		def tmpList = context.listIt;
		
	    def weAssRoleList = delegator.findList("WorkEffortAssignmentRoleView", EntityCondition.makeCondition(weAssRoleConditionList), null, null, null, false);		    
	    def workEffortIdFromWeAssRoleList = EntityUtil.getFieldListFromEntityList(weAssRoleList, "workEffortId", true);
	    context.listIt = EntityUtil.filterByCondition(tmpList, EntityCondition.makeCondition("workEffortId", EntityOperator.IN, workEffortIdFromWeAssRoleList));		
	}
}

request.setAttribute("listIt", context.listIt);

if (res == "success") {
	// check if this is massive-print-search or export-search 
    res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/checkExportSearchResult.groovy", context);
}

return res;