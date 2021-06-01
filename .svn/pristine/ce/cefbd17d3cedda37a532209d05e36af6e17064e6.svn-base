import org.ofbiz.base.util.*;

parameters.statusId = "PARTY_ENABLED";
//se sono in dettaglio Misure GlAccount non filtro per parentRoleTypeId
if (! "Y".equals(parameters.isGlAccMeasure) && ! "Y".equals(parameters.noConditionParentRoleType)) {
	parameters.parentRoleTypeId = "ORGANIZATION_UNIT";
}

if (UtilValidate.isNotEmpty(parameters.roleTypeId)) {
	def roleType = delegator.findOne("RoleType", ["roleTypeId" : parameters.roleTypeId], false);
	if (UtilValidate.isNotEmpty(roleType)) {
		def rtParentTypeId = roleType.parentTypeId;
		if ("ORGANIZATION_UNIT".equals(rtParentTypeId)) {
			parameters.parentRoleTypeId = "ORGANIZATION_UNIT";
		}
	}
}


// Se accesso per ruolo, filtra per organizazzioni abilitate.
if ("PARTYMGRROLE_VIEW".equals(parameters.get("userLoginPermission")) || "PARTYMGRROLE_ADMIN".equals(parameters.get("userLoginPermission"))) {
    parameters.entityName = "PartyRoleOrgUnitManagerView";
} 
else {
    parameters.entityName = "PartyRoleView";
}

if (! "ORGANIZATION_UNIT".equals(parameters.roleTypeId) && ! "ORGANIZATION_UNIT".equals(parameters.parentRoleTypeId)) {
	parameters.organizationId = null;
}

if ("PartyRoleView".equals(parameters.entityName) || "Party".equals(parameters.entityName)) {
	if (!(UtilValidate.isNotEmpty(parameters.roleTypeId) || UtilValidate.isNotEmpty(parameters.roleTypeId_fld0_value))) {
		parameters.entityName = "PartyRoleWithoutRoleTypeView";
		parameters.fieldList = "[parentRoleTypeId|parentDescription|parentRoleCode|partyId|partyTypeId|partyName|statusId]";
		parameters.orderBy = "partyName|parentDescription|parentRoleCode|partyId|statusId";
	}
}


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

/*GN-402: se sto filtrando per email cerco solo i contatti di tipo email*/
if (parameters.entityName != null && parameters.entityName.indexOf("ContactMechUserLogin") >= 0) {
    for (key in keys) {
        if (key.startsWith("partyEmail") && key.endsWith("value") && UtilValidate.isNotEmpty(parameters[key])) {
            parameters.contactMechTypeId = "EMAIL_ADDRESS";
		    break;
        }
    }
}

/*gestione filtro userLogin.partyId per le viste di tipo OrgUnitManager*/
if (parameters.entityName != null && parameters.entityName.indexOf("OrgUnitManager") >= 0) {
	if (parameters.entityName.indexOf("RelationshipRoleView") >= 0) {
		parameters.userLoginPartyId = context.userLogin.partyId;
	} else {
		parameters.partyIdTo = context.userLogin.partyId;
	}
}

context.executePerformFindScriptName = null;
res = GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeLookupPerformFind.groovy", context);

