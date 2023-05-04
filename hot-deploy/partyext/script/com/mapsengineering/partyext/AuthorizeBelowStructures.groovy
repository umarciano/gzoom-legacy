import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;

/**
 * Servizio ricorsivo per autorizzare una persona sull'albero delle strutture figlie
 */
def result = ServiceUtil.returnSuccess();
def uiLabelMap = UtilProperties.getResourceBundleMap("PartyExtUiLabels", locale);
def message = uiLabelMap.AuthorizeExecuted;

// iterazione per recuperare l'albero delle strutture figlie
def checkRelationshipList(partyIdFrom, roleTypeIdFrom, partyIdTo, roleTypeIdTo, fromDate, thruDate, ctxEnabled) {
    def condList = [];
    condList.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom)); //uo padre
    condList.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
    condList.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
    def partyRelationshipList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(condList), null, null, null, false);
    if (UtilValidate.isNotEmpty(partyRelationshipList)) {
        partyRelationshipList.each { partyRelationshipItem ->
            createOrUpdatePartyRelationship(partyRelationshipItem.partyIdTo, partyRelationshipItem.roleTypeIdTo, partyIdTo, roleTypeIdTo, fromDate, thruDate, ctxEnabled);
            checkRelationshipList(partyRelationshipItem.partyIdTo, partyRelationshipItem.roleTypeIdTo, partyIdTo, roleTypeIdTo, fromDate, thruDate, ctxEnabled);
        }
    }
    return null;
}

// cerca se esiste gia' una relazione di tipo responsabile o autorizzata
def createOrUpdatePartyRelationship(partyIdFrom, roleTypeIdFrom, partyIdTo, roleTypeIdTo, fromDate, thruDate, ctxEnabled) {
    def authCond = [];
    authCond.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom)); // uo
    authCond.add(EntityCondition.makeCondition("roleTypeIdFrom", roleTypeIdFrom));
    authCond.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, ["ORG_RESPONSIBLE", "ORG_DELEGATE"]));
    authCond.add(EntityCondition.makeCondition("partyIdTo", partyIdTo)); // dipendente
    authCond.add(EntityCondition.makeCondition("roleTypeIdTo", roleTypeIdTo));
    def authList = delegator.findList("PartyRelationship", EntityCondition.makeCondition(authCond), null, null, null, false);
    if (UtilValidate.isNotEmpty(authList)) {
        authList.each { authItem ->
            // aggiorna solo il campo ctxEnabled, quindi si aggiornano solo le relazioni di tipo ORG_DELEGATE, per cui il campo ha una funzione
            if("ORG_DELEGATE".equals(authItem.partyRelationshipTypeId)) {
                updatePartyRelationship(ctxEnabled, authItem);
            }
        }
    } else {
        // crea na nuova relazioni di tipo ORG_DELEGATE
        createPartyRelationship(partyIdFrom, partyIdTo, roleTypeIdFrom, roleTypeIdTo, fromDate, thruDate, ctxEnabled, context.userLogin);
    }
}

//crea na nuova relazioni di tipo ORG_DELEGATE
def createPartyRelationship(partyIdFrom, partyIdTo, roleTypeIdFrom, roleTypeIdTo, fromDate, thruDate, ctxEnabled, userLogin) {
    def localParameters = [:];
    localParameters.partyIdFrom = partyIdFrom; // uo
    localParameters.partyIdTo = partyIdTo; // dipendente
    localParameters._AUTOMATIC_PK_ = "Y";
    localParameters.partyRelationshipTypeId = "ORG_DELEGATE"; // crea solo delegato
    localParameters.roleTypeIdFrom = roleTypeIdFrom;
    localParameters.roleTypeIdTo = roleTypeIdTo;
    localParameters.fromDate = fromDate;
    if (UtilValidate.isNotEmpty(ctxEnabled)) {
        localParameters.ctxEnabled = ctxEnabled;
    }
    if (UtilValidate.isNotEmpty(thruDate)) {
        localParameters.thruDate = thruDate;
    }
    dctx.getDispatcher().runSync("crudServiceDefaultOrchestration_PartyRelationship", ["parameters": localParameters, "userLogin": userLogin, "operation": "CREATE", "entityName": "PartyRelationship"]);
}

// Aggiorna solo il campo ctxEnabled
def updatePartyRelationship(ctxEnabled, paramsMap) {
    def localParameters = [:];
    localParameters.partyRelationshipTypeId = paramsMap.partyRelationshipTypeId;
    localParameters.partyIdFrom = paramsMap.partyIdFrom;
    localParameters.roleTypeIdFrom = paramsMap.roleTypeIdFrom;
    localParameters.partyIdTo = paramsMap.partyIdTo;
    localParameters.roleTypeIdTo = paramsMap.roleTypeIdTo;
    localParameters.parentRoleTypeId = paramsMap.parentRoleTypeId;
    localParameters.fromDate = paramsMap.fromDate;
    localParameters.comments = paramsMap.comments;
    localParameters.relationshipName = paramsMap.relationshipName;
    if (UtilValidate.isNotEmpty(ctxEnabled)) {
        def str = paramsMap.ctxEnabled;
        if (str.indexOf(ctxEnabled) < 0) {
            localParameters.ctxEnabled = paramsMap.ctxEnabled + ctxEnabled;
        }
    }
    if (UtilValidate.isNotEmpty(paramsMap.thruDate)) {
        localParameters.thruDate = paramsMap.thruDate;
    }
    dctx.getDispatcher().runSync("crudServiceDefaultOrchestration_PartyRelationship", ["parameters": localParameters, "userLogin": userLogin, "operation": "UPDATE", "entityName": "PartyRelationship"]);
}

try {
    // Debug.log("authorizeBelowStructures ");
    
	def partyIdFrom = parameters.partyIdFrom; //uo padre
	def partyIdTo = parameters.partyIdTo; // dipendente
	def roleTypeIdFrom = parameters.roleTypeIdFrom;
	def roleTypeIdTo = parameters.roleTypeIdTo;
	def fromDate = parameters.fromDate;
	def thruDate = parameters.thruDate;
	def ctxEnabled = parameters.ctxEnabled;
	
	checkRelationshipList(partyIdFrom, roleTypeIdFrom, partyIdTo, roleTypeIdTo, fromDate, thruDate, ctxEnabled);
	
} catch (Exception e) {
    Debug.logError(e, "");
	message = uiLabelMap.AuthorizeNotExecuted + ": " +e.getMessage();
}

result.put("message", message);
return result;