import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


//GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);

//Debug.log("********************** context.inputFields = " + context.inputFields);

def condition = EntityCondition.makeCondition(context.inputFields);
//condition = EntityCondition.makeCondition([condition, EntityCondition.makeCondition("thruDate", null)]);

context.foundList = delegator.findList(parameters.treeViewEntityName, condition, null, context.orderByFields, null, true);

if (UtilValidate.isNotEmpty(context.foundList) && UtilValidate.isNotEmpty(parameters.respPartyRelationshipTypeId)) {
	def returnList = [];
	context.foundList.each { value ->
		value = value.getAllFields();
		returnList.add(value);
		
		def partyIdTo = value.partyIdTo;
		def roleTypeIdTo = value.roleTypeIdTo;
		
		def responsibleCondition = EntityCondition.makeCondition([EntityCondition.makeCondition("partyIdFrom", partyIdTo), EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdTo), 
															EntityCondition.makeCondition("partyRelationshipTypeId", parameters.respPartyRelationshipTypeId)]);
		
		def responsible = EntityUtil.getFirst(delegator.findList("PartyResponsible", responsibleCondition, null, ["-fromDate"], null, true));
		if (UtilValidate.isNotEmpty(responsible)) {
			value.respPartyName = responsible.respPartyName;
			value.contentId = responsible.contentId;
		}
		
		if (UtilValidate.isNotEmpty(parameters.delegatePartyRelationshipTypeId)) {
			def delegateCondition = EntityCondition.makeCondition(["partyIdFrom": partyIdTo, "roleTypeIdFrom": roleTypeIdTo, 
																   "partyRelationshipTypeId": parameters.delegatePartyRelationshipTypeId, "thruDate": null]);
			def countDelegate = delegator.findCountByCondition("PartyRelationship", delegateCondition, null, null);
			
			if (countDelegate > 0)
				value.numDel = countDelegate;
		}
	}
	context.foundList = returnList;
}

//returnList = [];
//for(GenericValue v : foundList){
//	Debug.log("************************* v = " + v)
//	if(v.respPartyRelationshipTypeId == parameters.responsabile  && v.thruDate == null ){
//		returnList.add(v);
//	}
//}
//
//context.foundList = returnList;