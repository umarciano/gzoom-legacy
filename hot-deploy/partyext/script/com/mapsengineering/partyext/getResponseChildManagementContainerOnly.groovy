import org.ofbiz.base.util.*;

res = "success";

def entityList = ["EmplPositionType", "RoleType", "PartyClassificationType", "PartyRelationshipType", "ContactMechPurposeType", "PartyAndContentDataResource", "PartyContentType", "TrainingClassType", "PartyQualType", "SkillType", "WorkEffortAnalysis", "WorkEffortTypePeriodView", "PartyRoleViewRelationshipRoleView", "EvaluationReferentsView"];
if ("Y".equals(parameters.fromDelete) && UtilValidate.isNotEmpty(parameters.entityName) && "PartyAndContentDataResource".equals(parameters.entityName)) {
    // nel caso di cancellazione di un record di allegati, si deve ritornare a mostrare all'utente la lista degli allegati
    request.setAttribute("managementFormType", parameters.oldManagementFormType);
    request.setAttribute("detail", "");
}
if ("Y".equals(parameters.fromDelete) && UtilValidate.isNotEmpty(parameters.entityName) && !entityList.contains(parameters.entityName)) {
	res = "search";
}

return res;