import org.ofbiz.entity.GenericEntity;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;


import com.mapsengineering.base.find.PartyEmailFindServices;

PartyEmailFindServices partyEmailFindServices = new PartyEmailFindServices(delegator);
context.emailAddressesList = partyEmailFindServices.getEmailAddress(userLogin.partyId);

if (UtilValidate.isNotEmpty(context.emailAddressesList) && "Y".equals(context.insertMode)) {
	defaultContactMechFrom = EntityUtil.getFirst(context.emailAddressesList);
	if (UtilValidate.isNotEmpty(defaultContactMechFrom)) {
		context.defaultContactMechIdFrom = defaultContactMechFrom.contactMechId;
		Debug.log("********************************************** context.defaultContactMechIdFrom: " + context.defaultContactMechIdFrom);
	}
}

if("Y".equals(context.insertMode)) {
	context.contactMechIdToList = partyEmailFindServices.getEmailAddress(null);
	
	// Default value responsibleId
	if (UtilValidate.isNotEmpty(context.responsibleId) ) {
		contactMechIdToList1 = partyEmailFindServices.getEmailAddress(context.responsibleId);
		
		contactMechId1 = EntityUtil.getFirst(contactMechIdToList1);
		if (UtilValidate.isNotEmpty(contactMechId1) && (UtilValidate.isEmpty(context.defaultContactMechIdFrom) || context.defaultContactMechIdFrom != contactMechId1.contactMechId)) {
			context.defaultContactMechIdTo1 = contactMechId1.contactMechId;
			Debug.log("********************************************** context.responsibleId = " + context.responsibleId + ", context.defaultContactMechIdTo1: " + context.defaultContactMechIdTo1);
		}
	}
	
	// Default value responsible co CTX_CO
	workEffort = delegator.findOne("WorkEffortView", ["workEffortId" : parameters.workEffortId], true);
	if(UtilValidate.isNotEmpty(workEffort.weContextId) && "CTX_CO".equals(workEffort.weContextId)) {
		
		partyList = delegator.findList("PartyRole", EntityCondition.makeCondition("roleTypeId", "WEM_CORR_MANAGER"), null, ["partyId"], null, true);
		def partyIdList = EntityUtil.getFieldListFromEntityList(partyList, "partyId", true);
		
		if (UtilValidate.isNotEmpty(partyIdList)) {
    		contactMechIdToListCo = partyEmailFindServices.getEmailAddresses(partyIdList);
    		contactMechIdToCo = EntityUtil.getFirst(contactMechIdToListCo);
    		
    		if (UtilValidate.isNotEmpty(contactMechIdToCo)) {
    			if (UtilValidate.isEmpty(context.defaultContactMechIdTo1) && (UtilValidate.isEmpty(context.defaultContactMechIdFrom) || context.defaultContactMechIdFrom != contactMechIdToCo.contactMechId)) {
    				context.defaultContactMechIdTo1 = contactMechIdToCo.contactMechId;
    				Debug.log("********************************************** context.defaultContactMechIdTo1: " + context.defaultContactMechIdTo1);
    			} else if(UtilValidate.isNotEmpty(context.defaultContactMechIdTo1) && (UtilValidate.isEmpty(context.defaultContactMechIdFrom) || context.defaultContactMechIdFrom != contactMechIdToCo.contactMechId)) {
    				context.defaultContactMechIdTo2 = contactMechIdToCo.contactMechId;
    				Debug.log("********************************************** context.defaultContactMechIdTo2: " + context.defaultContactMechIdTo2);
    			}
    		}
		}
	}
} else {
	// populate one or many dest
	if(UtilValidate.isNotEmpty(context.communicationEventId)) {
	    
	    if(UtilValidate.isNotEmpty(context.contactMechIdFrom)) {
	        // context.defaultContactMechIdFrom = context.contactMechIdFrom;
	        def contactMech = delegator.findOne("ContactMech", ["contactMechId" : context.contactMechIdFrom], true);
	        context.infoStringFrom = contactMech.infoString;
			Debug.log("********************************************** context.infoStringFrom: " + context.infoStringFrom);
	    }
	    
		if(UtilValidate.isNotEmpty(context.partyIdTo)) {
			context.contactMechIdTo1 = context.contactMechIdTo;
			context.partyIdTo1 = context.partyIdTo;
			Debug.log("********************************************** context.partyIdTo1 " + context.partyIdTo1 + ", contactMechIdTo1: " + context.contactMechIdTo);
		}
		conditionList = [EntityCondition.makeCondition("communicationEventId", context.communicationEventId), EntityCondition.makeCondition("roleTypeId", "CC")];
		contactListParty = delegator.findList("CommunicationEventRole", EntityCondition.makeCondition(conditionList), null, null, null, true);
		if(UtilValidate.isNotEmpty(contactListParty) && contactListParty.size() > 0) {
			context.contactMechIdTo2 = contactListParty[0].contactMechId;
			context.partyIdTo2 = contactListParty[0].partyId;
			Debug.log("********************************************** context.partyIdTo2 " + context.partyIdTo2 + ", contactMechIdTo2: " + contactListParty[0].contactMechId);
		}
		if(UtilValidate.isNotEmpty(contactListParty) && contactListParty.size() > 1) {
			context.contactMechIdTo3 = contactListParty[1].contactMechId;
			context.partyIdTo3 = contactListParty[1].partyId;
			Debug.log("********************************************** context.partyIdTo3 " + context.partyIdTo3 + ", contactMechIdTo3: " + contactListParty[1].contactMechId);
		}
	}
}

