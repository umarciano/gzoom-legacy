import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

res = "success";

keys = parameters.keySet();

// se permessi per ROLE filtro solo i ruoli abilitati
if ("PARTYMGRROLE_VIEW".equals(parameters.userLoginPermission) || "PARTYMGRROLE_ADMIN".equals(parameters.userLoginPermission)) {
    if(!"PartyRoleView".equals(parameters.entityName)) {
        parameters.entityName = "PartyRoleView";
        parameters.fieldList = "[parentRoleTypeId|parentDescription|parentRoleCode|roleTypeId|roleDescription|partyId|partyName|partyNameLang|statusId|organizationId]";
        parameters.orderBy = "partyName|parentDescription|parentRoleCode|roleDescription|partyId|statusId";
    }
}

if ("PartyRoleView".equals(parameters.entityName) || "Party".equals(parameters.entityName)) {
	if (UtilValidate.isEmpty(parameters.roleTypeId)) {
		parameters.entityName = "PartyRoleWithoutRoleTypeView";
		parameters.fieldList = "[parentRoleTypeId|parentDescription|parentRoleCode|partyId|partyTypeId|partyName|partyNameLang|statusId|organizationId]";
		parameters.orderBy = "partyName|parentDescription|parentRoleCode|partyId|statusId";
	}
}

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

/*GN-402: se sto filtrando per email cerco solo i contatti di tipo email*/
if (parameters.entityName != null && parameters.entityName.indexOf("ContactMechUserLogin") >= 0) {
    for (key in keys) {
        if (key.startsWith("partyEmail") && key.endsWith("value") && UtilValidate.isNotEmpty(parameters[key])) {
            parameters.contactMechTypeId = "EMAIL_ADDRESS";
		    break;
        }
    }
}


res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/executePerformFind.groovy", context);
//Debug.log("request.getAttribute(listIt); " + request.getAttribute("listIt"));

/*
 * Caso quando ho solo il parentRoleTypeId e non il roleTypeId, devo passare come entity Il PARTY non il
 * PartyRoleView perchè da l'errore, visto che la chiave è partyId e roleTypeId e io non ho il roleTypeId
 * **/
if("PartyRoleWithoutRoleTypeView".equals(parameters.entityName)){
	parameters.entityName= "Party";
	request.setAttribute("entityName", parameters.entityName);
}



if ("PARTYMGRROLE_VIEW".equals(parameters.userLoginPermission) || "PARTYMGRROLE_ADMIN".equals(parameters.userLoginPermission)) {
    if (UtilValidate.isNotEmpty(parameters.userLoginParentRoleTypeList)) {
        // listaAllPartyRoleView = lista di tutti i partyRole
        listaAllPartyRoleView = request.getAttribute("listIt");
        
        // context.userLoginChildRoleTypeList = lista di tutti i roleTypeId abilitati
        GroovyUtil.runScriptAtLocation("component://partyext/webapp/partyext/WEB-INF/actions/getUserLoginChildRoleTypeList.groovy", context);
        
        //filtro su tutti i PartyRoleView per cui sei abilitato il parentTypeId o il roleTypeId tramite lista_abilitata
        childRoleTypeList = EntityUtil.getFieldListFromEntityList(context.userLoginChildRoleTypeList, "roleTypeId", true);
        listaPartyRoleView = EntityUtil.filterByCondition(listaAllPartyRoleView, EntityCondition.makeCondition("roleTypeId", EntityOperator.IN, childRoleTypeList));
        
        request.setAttribute("listIt", listaPartyRoleView);
        request.setAttribute("entityName", "PartyRoleView");
    }
    else {
        request.setAttribute("listIt", []);
    }
}    

//Debug.log("--------------------- executePerformFindPartyRole entity=" + parameters.entityName);
return res;