import org.ofbiz.base.util.*;

res = "success";

GroovyUtil.runScriptAtLocation("component://base/script/com/mapsengineering/base/populateManagement.groovy", context);

def entityList = ["EmplPositionType", "RoleType", "PartyClassificationType", "PartyRelationshipType", "ContactMechPurposeType", "PartyContentType", "TrainingClassType", "PartyQualType", "SkillType", "WorkEffortAnalysis", "WorkEffortTypePeriodView", "PartyRoleViewRelationshipRoleView", "EvaluationReferentsView"];
if ("Y".equals(parameters.fromDelete) && UtilValidate.isNotEmpty(parameters.entityName) && !entityList.contains(parameters.entityName)) {
	res = "search";
}
if ("Y".equals(parameters.wizard)) {
    res = "wizard";
}

return res;