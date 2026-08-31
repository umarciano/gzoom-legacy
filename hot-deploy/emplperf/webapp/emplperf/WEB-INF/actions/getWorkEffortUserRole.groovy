// Action: getWorkEffortUserRole.groovy
// Returns in context whether the current user is valutatore (evaluator) or valutato (evaluatee)
// for the given workEffortId. Sets:
//  - context.isValutatore (Boolean)
//  - context.isValutato (Boolean)
//  - context.userAssignedRoleTypeIds (List)
//  - context.userAssignments (List of WorkEffortPartyAssignment GenericValue)

import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilValidate

// initialize defaults
context.isValutatore = false
context.isValutato = false
context.userAssignedRoleTypeIds = []
// keep a safe, serializable list of assignments (ids + minimal fields) to avoid strange toMap conversions
context.userAssignments = []

    Debug.logInfo("getWorkEffortUserRole: INIZIO", "getWorkEffortUserRole")

// Se userLogin è nullo
if (userLogin == null) {
    Debug.logInfo("getWorkEffortUserRole: no userLogin, aborting", "getWorkEffortUserRole")
    return
}

def workEffortId = parameters.workEffortId ?: parameters.workEffortIdFrom ?: parameters.id
Debug.logInfo("getWorkEffortUserRole: workEffortId param resolved to: " + workEffortId, "getWorkEffortUserRole")
Debug.logInfo("getWorkEffortUserRole: userLogin.partyId: " + userLogin?.partyId, "getWorkEffortUserRole")

if (!workEffortId) {
    Debug.logInfo("getWorkEffortUserRole: no workEffortId provided", "getWorkEffortUserRole")
    return
}

try {
    // First, prefer precise per-WorkEffort checks for the current user. These are authoritative
    // for the displayed workEffort and must be mutually exclusive: if the user is the
    // evaluated party (WEM_EVAL_IN_CHARGE) for THIS workEffort -> valutato only.
    // If the user is the eval manager (WEM_EVAL_MANAGER) for THIS workEffort -> valutatore only.
    def assignmentsFound = false
    try {
        Debug.logInfo("getWorkEffortUserRole: checking WorkEffortPartyAssignment for current user and workEffort " + workEffortId, "getWorkEffortUserRole")
    // check if current user is the evaluated party for THIS workEffort
    def myEvalCond = [:]
    myEvalCond.workEffortId = workEffortId
    myEvalCond.partyId = userLogin.partyId
    myEvalCond.roleTypeId = "WEM_EVAL_IN_CHARGE"
    def myEval = delegator.findByAnd("WorkEffortPartyAssignment", myEvalCond, null, false)
        if (myEval && myEval.size() > 0) {
            assignmentsFound = true
            context.isValutato = true
            context.isValutatore = false
                myEval.each { a ->
                    def r = a.getString("roleTypeId")
                    if (r && !context.userAssignedRoleTypeIds.contains(r)) context.userAssignedRoleTypeIds.add(r)
                    context.userAssignments.add([workEffortId: a.getString("workEffortId"), partyId: a.getString("partyId"), roleTypeId: r])
                }
            Debug.logInfo("getWorkEffortUserRole: user is WEM_EVAL_IN_CHARGE for workEffort " + workEffortId + ", set isValutato=true", "getWorkEffortUserRole")
        } else {
            // check if current user is the manager for THIS workEffort
            def myMgrCond = [:]
            myMgrCond.workEffortId = workEffortId
            myMgrCond.partyId = userLogin.partyId
            myMgrCond.roleTypeId = "WEM_EVAL_MANAGER"
            def myMgr = delegator.findByAnd("WorkEffortPartyAssignment", myMgrCond, null, false)
            if (myMgr && myMgr.size() > 0) {
                assignmentsFound = true
                context.isValutatore = true
                context.isValutato = false
                myMgr.each { a ->
                    def r = a.getString("roleTypeId")
                    if (r && !context.userAssignedRoleTypeIds.contains(r)) context.userAssignedRoleTypeIds.add(r)
                    context.userAssignments.add([workEffortId: a.getString("workEffortId"), partyId: a.getString("partyId"), roleTypeId: r])
                }
                Debug.logInfo("getWorkEffortUserRole: user is WEM_EVAL_MANAGER for workEffort " + workEffortId + ", set isValutatore=true", "getWorkEffortUserRole")
            }
        }
    } catch (Throwable t) {
        Debug.logError(t, "getWorkEffortUserRole")
    }

    // If we didn't find any WorkEffort-specific assignment for this user, fall back to global roles
    if (!assignmentsFound) {
        try {
            Debug.logInfo("getWorkEffortUserRole: no per-workEffort assignment for user; checking global PartyRoleView", "getWorkEffortUserRole")
            def prEvalCond = [:]
            prEvalCond.partyId = userLogin.partyId
            prEvalCond.roleTypeId = "WEM_EVAL_IN_CHARGE"
            def prEval = delegator.findOne("PartyRoleView", prEvalCond, false)
            if (prEval) {
                context.isValutato = true
                context.isValutatore = false
                if (!context.userAssignedRoleTypeIds.contains("WEM_EVAL_IN_CHARGE")) context.userAssignedRoleTypeIds.add("WEM_EVAL_IN_CHARGE")
                Debug.logInfo("getWorkEffortUserRole: PartyRoleView indicates WEM_EVAL_IN_CHARGE for party " + userLogin.partyId, "getWorkEffortUserRole")
            } else {
                def prMgrCond = [:]
                prMgrCond.partyId = userLogin.partyId
                prMgrCond.roleTypeId = "WEM_EVAL_MANAGER"
                def prMgr = delegator.findOne("PartyRoleView", prMgrCond, false)
                if (prMgr) {
                    context.isValutatore = true
                    context.isValutato = false
                    if (!context.userAssignedRoleTypeIds.contains("WEM_EVAL_MANAGER")) context.userAssignedRoleTypeIds.add("WEM_EVAL_MANAGER")
                    Debug.logInfo("getWorkEffortUserRole: PartyRoleView indicates WEM_EVAL_MANAGER for party " + userLogin.partyId, "getWorkEffortUserRole")
                }
            }
        } catch (Throwable t) {
            Debug.logInfo("getWorkEffortUserRole: controllo PartyRoleView fallito: " + t.toString(), "getWorkEffortUserRole")
            Debug.logError(t, "getWorkEffortUserRole")
        }
    } else {
        Debug.logInfo("getWorkEffortUserRole: using per-workEffort assignment result (no fallback)", "getWorkEffortUserRole")
    }

    // If we determined a role, ask the form renderer to avoid forcing global read-only so field flags apply
    try {
        if (context.isValutato == true || context.isValutatore == true) {
            context.evalPartyIdReadOnly = true
            Debug.logInfo("getWorkEffortUserRole: imposto context.evalPartyIdReadOnly = true perché isValutato/isValutatore è true", "getWorkEffortUserRole")
        }
    } catch (Throwable t) {
        Debug.logError(t, "getWorkEffortUserRole")
    }

    // Big visible debug block
    Debug.logInfo("================================================================================", "getWorkEffortUserRole")
    Debug.logInfo("===================== RISULTATO getWorkEffortUserRole ============================", "getWorkEffortUserRole")
    Debug.logInfo("workEffortId: " + workEffortId, "getWorkEffortUserRole")
    Debug.logInfo("partyId: " + userLogin.partyId, "getWorkEffortUserRole")
    Debug.logInfo("isValutatore: " + context.isValutatore, "getWorkEffortUserRole")
    Debug.logInfo("isValutato: " + context.isValutato, "getWorkEffortUserRole")
    Debug.logInfo("userAssignedRoleTypeIds: " + context.userAssignedRoleTypeIds, "getWorkEffortUserRole")
    Debug.logInfo("userAssignments: " + context.userAssignments, "getWorkEffortUserRole")
    Debug.logInfo("===================== FINE RISULTATO getWorkEffortUserRole =======================", "getWorkEffortUserRole")
    Debug.logInfo("================================================================================", "getWorkEffortUserRole")

} catch (Throwable e) {
    // ensure we log full stack trace so we can diagnose toMap style errors
    Debug.logInfo("getWorkEffortUserRole: unexpected error: " + e.toString(), "getWorkEffortUserRole")
    Debug.logError(e, "getWorkEffortUserRole")
}
