import java.sql.Timestamp;
import java.util.Date;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.text.SimpleDateFormat;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;
import javolution.util.FastSet;
import javolution.util.FastList;

// servizio richiamato ANCHE in caso di inserimento massivo  di risorse ne lfolder Risorse Umane dell'obiettivo
parameters.roleTypeId = UtilValidate.isEmpty(parameters.roleTypeId) ? context.roleTypeId : parameters.roleTypeId;

parameters.entityName = "PartyRoleView";

/** Whether if partyIdAss or roleTypeIdAss is true*/
def isAss = false;
/** Whether if partyIdApp or roleTypeIdApp is true*/
def isApp = false;

if("Y".equals(parameters.fromHumanResFolder)) {
	parameters.entityName = "PartyRoleViewAndEmpl";
}

keys = parameters.keySet();

for (key in keys) {
	if ((key.startsWith("partyIdApp") || key.startsWith("roleTypeIdApp"))
	 && UtilValidate.isNotEmpty(parameters[key])) {
		parameters.entityName = "PartyRoleViewRelationshipRoleAssAppView";
		parameters.partyRelationshipTypeIdApp = "ORG_EMPLOYMENT";
		isApp = true;
	}
	if ((key.startsWith("partyIdAss") || key.startsWith("roleTypeIdAss"))
	 && UtilValidate.isNotEmpty(parameters[key])) {
		parameters.entityName = "PartyRoleViewRelationshipRoleAssAppView";
		parameters.partyRelationshipTypeIdAss = "ORG_ALLOCATION";
		isAss = true;
	}
}


context.executePerformFindScriptName = null;

//5479
if ("PartyRoleViewRelationshipRoleAssAppView".equals(parameters.entityName)) {
    parameters.fieldList = "[parentRoleTypeId|parentRoleCode|roleTypeId|partyId|statusId|partyName|profile|category|employmentAmount|fromDateApp|thruDateApp|fromDateAss|thruDateAss|category|profile|employmentAmount|categoryLang]"; 
} else if("PartyRoleView".equals(parameters.entityName)) {
    parameters.fieldList = "[parentRoleTypeId|parentRoleCode|roleTypeId|partyId|statusId|partyName]";
} else if("PartyRoleViewAndEmpl".equals(parameters.entityName)) {
	parameters.fieldList = "[parentRoleTypeId|parentRoleCode|roleTypeId|partyId|statusId|partyName|category|categoryLang]";
}


res = GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeLookupPerformFind.groovy", context);


//5479
if ("PartyRoleViewRelationshipRoleAssAppView".equals(parameters.entityName) && UtilValidate.isNotEmpty(context.listIt) && UtilValidate.isNotEmpty(parameters.workEffortId)) {
    def workEffort = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortId], false);
    def condDate = [];
    
    if(isApp) {
    	condDate.add(EntityCondition.makeCondition("fromDateApp", EntityOperator.LESS_THAN_EQUAL_TO, workEffort.estimatedCompletionDate));
        condDate.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDateApp", EntityOperator.GREATER_THAN_EQUAL_TO, workEffort.estimatedStartDate),
            EntityOperator.OR, EntityCondition.makeCondition("thruDateApp", null))); 
    }
    if(isAss) {
    	condDate.add(EntityCondition.makeCondition("fromDateAss", EntityOperator.LESS_THAN_EQUAL_TO, workEffort.estimatedCompletionDate));
        condDate.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDateAss", EntityOperator.GREATER_THAN_EQUAL_TO, workEffort.estimatedStartDate),
            EntityOperator.OR, EntityCondition.makeCondition("thruDateAss", null)));         
    }

    context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition(condDate));
}
	 
	 
//GN-875 Aggiunto filtro per searchDate

if("PartyRoleViewRelationshipRoleAssAppView".equals(parameters.entityName) && UtilValidate.isNotEmpty(context.listIt)){
	FromAndThruDatesProviderFromParams fromAndThruDatesProvider = new FromAndThruDatesProviderFromParams(context, parameters, delegator, false);
	fromAndThruDatesProvider.run();
	
	def condDate = [];
	if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getFromDate())) {
		if(isApp) {
			condDate.add(EntityCondition.makeCondition("fromDateApp", EntityOperator.LESS_THAN_EQUAL_TO, fromAndThruDatesProvider.getFromDate()));
		}
		if(isAss) {
			condDate.add(EntityCondition.makeCondition("fromDateAss", EntityOperator.LESS_THAN_EQUAL_TO, fromAndThruDatesProvider.getFromDate()));
		}
	}
	if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getThruDate())) {
		if(isApp) {
			condDate.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDateApp", EntityOperator.GREATER_THAN_EQUAL_TO, 
					fromAndThruDatesProvider.getThruDate()),
					EntityOperator.OR, EntityCondition.makeCondition("thruDateApp", null)));
		}
		if(isAss) {
			condDate.add(EntityCondition.makeCondition(EntityCondition.makeCondition("thruDateAss", EntityOperator.GREATER_THAN_EQUAL_TO, 
					fromAndThruDatesProvider.getThruDate()),
					EntityOperator.OR, EntityCondition.makeCondition("thruDateAss", null)));
		}
	}
	
	if(UtilValidate.isNotEmpty(condDate)) {
		context.listIt = EntityUtil.filterByCondition(context.listIt, EntityCondition.makeCondition(condDate));
	}	
}

//GN-2135 raggruppo i risultati per partyId per evitare record multipli
if (UtilValidate.isNotEmpty(context.listIt)) {
	def partyList = context.listIt;
	def toReturnList = FastList.newInstance();
	def distinctPartyIdSet = FastSet.newInstance();
	
	partyList.each { partyItem ->
    	if (! distinctPartyIdSet.contains(partyItem.partyId)) {
    		toReturnList.add(partyItem);
    		distinctPartyIdSet.add(partyItem.partyId);
    	}	
	}
	context.listIt = toReturnList;
}

 	 
