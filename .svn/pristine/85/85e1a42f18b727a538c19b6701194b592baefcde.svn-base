import org.ofbiz.base.util.*;

//Debug.log("************************************** getNextWizardPartyMap.groocy -> " + context.entityName);

if (UtilValidate.isNotEmpty(parameters.roleTypeId)) {
    context.entityName = "PartyRoleView";
} else {
    context.entityName = "Party";
}

parameters.wizardDataMap.each{ key, value ->
    if (!context.entityName.equals(value.entityName)) {
        value.entityName = context.entityName;
    }
}

wizardPrevMapName = "";
if (UtilValidate.isNotEmpty(context.wizardPrevMapName)) {
	wizardPrevMapName = context.wizardPrevMapName;
}


if ("PartyCreateByTypeForm".equals(wizardPrevMapName)) {
    context.wizardNextMapName = "PartyCreateForm_" + parameters.partyTypeId;
} else if (wizardPrevMapName.indexOf("PartyCreateForm") != -1) {
    wizardMap = parameters.wizardDataMap["PartyCreateByTypeForm"];

    if ("IMPERSONAL_PARTY".equals(wizardMap.partyTypeId)) {
        if (UtilValidate.isNotEmpty(parameters.roleTypeId)) {
            roleType = delegator.findOne('RoleType', true, 'roleTypeId', parameters.roleTypeId);

            if ("EMPLOYEE".equals(roleType.parentTypeId)) {
                context.roleTypeId = roleType.roleTypeId;
                context.wizardNextMapName = "PartyRoleSpecificCreateForm_"+roleType.parentTypeId;
            }
            else {
                context.wizardNextMapName = "Summary";
            }
        } else {
            context.wizardNextMapName = "Summary";
        }

    } else {
        context.wizardNextMapName = "PartyContactMechCreateForm";
    }
} else if (wizardPrevMapName.indexOf("PartyContactMechCreateForm") != -1) {
    wizardMap = parameters.wizardDataMap["PartyCreateByTypeForm"];
    partyTypeId = wizardMap.partyTypeId;
    wizardMap = parameters.wizardDataMap["PartyCreateForm_" + partyTypeId];
    if (UtilValidate.isNotEmpty(wizardMap.roleTypeId)) {
        roleType = delegator.findOne('RoleType', true, 'roleTypeId', wizardMap.roleTypeId);

        if ("EMPLOYEE".equals(roleType.parentTypeId)) {
            context.roleTypeId = roleType.roleTypeId;
            context.wizardNextMapName = "PartyRoleSpecificCreateForm_" + roleType.parentTypeId;
        }
        else {
            context.wizardNextMapName = "Summary";
        }
    } else {
        context.wizardNextMapName = "Summary";
    }
} else if (UtilValidate.isEmpty(parameters.wizardIndex) && UtilValidate.isEmpty(parameters.insertMode)) {
    context.wizardNextMapName = "Summary";
}

//Debug.log("************************************** getNextWizardPartyMap.groocy -> " + context.entityName);
