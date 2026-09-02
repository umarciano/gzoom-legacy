import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ServiceUtil

// NB: in questo motore groovy gli helper success()/error() di GroovyBaseScript NON sono disponibili
// (crash "No signature of method: ...success()"). Si usa ServiceUtil.returnSuccess()/returnError(),
// come nel resto del componente. Senza questo fix OGNI create/update di scheda CTX_BS falliva.

def workEffortTypeId = parameters.workEffortTypeId
def orgUnitId = parameters.orgUnitId
def startDate = parameters.estimatedStartDate
def endDate = parameters.estimatedCompletionDate
def operation = parameters.operation
def currentId = parameters.workEffortId

if ("CTX_BS" != workEffortTypeId || !orgUnitId || !startDate || !endDate ||
        !("CREATE" == operation || "UPDATE" == operation)) {
    return ServiceUtil.returnSuccess()
}

def existing = delegator.findList("WorkEffort", EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "CTX_BS"),
        EntityCondition.makeCondition("orgUnitId", EntityOperator.EQUALS, orgUnitId)
], EntityOperator.AND), null, null, null, false)

def conflict = existing.find { candidate ->
    candidate.getString("workEffortId") != currentId &&
            candidate.getTimestamp("estimatedStartDate") != null &&
            candidate.getTimestamp("estimatedCompletionDate") != null &&
            !candidate.getTimestamp("estimatedStartDate").after(endDate) &&
            !candidate.getTimestamp("estimatedCompletionDate").before(startDate)
}

if (conflict != null) {
    return ServiceUtil.returnError("Esiste gia' una scheda CTX_BS per la UO ${orgUnitId} con periodo sovrapposto.")
}

return ServiceUtil.returnSuccess()
