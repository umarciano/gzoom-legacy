import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.base.util.Debug

def MODULE = "propagateScoreBs"

def bs = delegator.findOne("WorkEffort", [workEffortId: parameters.workEffortId], false)
if (bs == null || "CTX_BS" != bs.getString("workEffortTypeId")) {
    Debug.logInfo("### propagateScoreBs: weId=${parameters.workEffortId} non e' una CTX_BS -> skip", MODULE)
    return
}

def orgUnitId = bs.getString("orgUnitId")
Debug.logInfo("### propagateScoreBs: START bsId=${bs.getString('workEffortId')} orgUnitId=${orgUnitId}", MODULE)

// UO SPLIT: se la UO che chiude e' agganciata a una madre strategica (party_id_from in una
// relazione STRATPERF_MOTHER) la sua scheda CTX_BS e' un placeholder senza obiettivi reali.
// NON propaghiamo il suo (falso) punteggio: le individuali delle split ricevono lo score alla
// chiusura della scheda MADRE (che sotto propaga anche a tutte le proprie split).
def motherRel = EntityUtil.filterByDate(delegator.findList("PartyRelationship", EntityCondition.makeCondition([
        EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, orgUnitId),
        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"),
        EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"),
        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "STRATPERF_MOTHER")
], EntityOperator.AND), null, null, null, false))
if (UtilValidate.isNotEmpty(motherRel)) {
    Debug.logInfo("### propagateScoreBs: orgUnit ${orgUnitId} e' una UO SPLIT (madre=${motherRel[0].getString('partyIdTo')}) -> skip, la propagazione avviene alla chiusura della madre", MODULE)
    return
}

def measureList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, bs.getString("workEffortId")),
        EntityCondition.makeCondition("glAccountId", EntityOperator.NOT_EQUAL, "SCOREKPI")
], EntityOperator.AND), null, null, null, false)

def scoreEntries = []
measureList.each { measure ->
    def transactions = delegator.findList("AcctgTrans", EntityCondition.makeCondition([
            EntityCondition.makeCondition("voucherRef", EntityOperator.EQUALS, measure.getString("workEffortMeasureId")),
            EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL"),
            EntityCondition.makeCondition("acctgTransTypeId", EntityOperator.EQUALS, "CTX_BS")
    ], EntityOperator.AND), null, null, null, false)
    transactions.each { transaction ->
        // NB: il distinguo ACTUAL/ACTUAL_INT (finale vs semestrale) sta sull'HEADER AcctgTrans
        // (filtrato sopra), NON sull'entry: sull'AcctgTransEntry gl_fiscal_type_id e' NULL. Non va
        // quindi filtrato per glFiscalTypeId='ACTUAL' sull'entry, altrimenti non si trova mai nulla.
        scoreEntries.addAll(delegator.findList("AcctgTransEntry", EntityCondition.makeCondition([
                EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS, transaction.getString("acctgTransId")),
                EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, "SCOREKPI")
        ], EntityOperator.AND), null, null, null, false))
    }
}

if (UtilValidate.isEmpty(scoreEntries)) {
    Debug.logInfo("### propagateScoreBs: bsId=${bs.getString('workEffortId')} nessun movimento SCOREKPI/ACTUAL -> skip (nessun punteggio da propagare)", MODULE)
    return
}

def scoreBs = scoreEntries.inject(BigDecimal.ZERO) { total, entry ->
    total + (entry.getBigDecimal("amount") ?: BigDecimal.ZERO)
}.setScale(2, BigDecimal.ROUND_HALF_UP)

if (scoreBs > new BigDecimal("60.00")) {
    throw new IllegalArgumentException("Score BS superiore a 60 per la scheda ${bs.getString('workEffortId')}: ${scoreBs}")
}

bs.set("scoreBs", scoreBs)
bs.set("adjustedScoreBs", null)
delegator.store(bs)

def bsStart = bs.getTimestamp("estimatedStartDate")
def bsEnd = bs.getTimestamp("estimatedCompletionDate")

// Perimetro individuali: la UO madre + tutte le sue UO split (STRATPERF_MOTHER, party_id_to = madre).
// Le persone delle split restano assegnate alla loro UO split ma ereditano lo score della madre.
def orgUnitIds = [orgUnitId]
EntityUtil.filterByDate(delegator.findList("PartyRelationship", EntityCondition.makeCondition([
        EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, orgUnitId),
        EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "ORGANIZATION_UNIT"),
        EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "ORGANIZATION_UNIT"),
        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "STRATPERF_MOTHER")
], EntityOperator.AND), null, null, null, false)).each { rel ->
    def splitId = rel.getString("partyIdFrom")
    if (!orgUnitIds.contains(splitId)) { orgUnitIds.add(splitId) }
}

def epList = delegator.findList("WorkEffort", EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "CTX_EP"),
        EntityCondition.makeCondition("orgUnitId", EntityOperator.IN, orgUnitIds)
], EntityOperator.AND), null, null, null, false)

def matchingEp = epList.findAll { ep ->
    def epStart = ep.getTimestamp("estimatedStartDate")
    def epEnd = ep.getTimestamp("estimatedCompletionDate")
    epStart != null && epEnd != null && bsStart != null && bsEnd != null &&
            !epStart.before(bsStart) && !epEnd.after(bsEnd)
}

if (UtilValidate.isEmpty(matchingEp) && bsStart != null) {
    def bsYear = bsStart.toInstant().atZone(java.time.ZoneId.systemDefault()).year
    matchingEp = epList.findAll { ep ->
        def epStart = ep.getTimestamp("estimatedStartDate")
        epStart != null && epStart.toInstant().atZone(java.time.ZoneId.systemDefault()).year == bsYear
    }
}

matchingEp.each { ep ->
    ep.set("scoreBs", scoreBs)
    if (["SCHEDA 4", "SCHEDA 5"].contains(ep.getString("etch"))) {
        ep.set("adjustedScoreBs", scoreBs.divide(new BigDecimal("60"), 8, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("40")).setScale(2, BigDecimal.ROUND_HALF_UP))
    } else {
        ep.set("adjustedScoreBs", scoreBs)
    }
    def adjustedEp = ep.getBigDecimal("adjustedScoreEp")
    def adjustedBs = ep.getBigDecimal("adjustedScoreBs")
    ep.set("overallEpBsScore", adjustedEp == null && adjustedBs == null ? null :
            (adjustedEp ?: BigDecimal.ZERO).add(adjustedBs ?: BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP))
    delegator.store(ep)
}

Debug.logInfo("### propagateScoreBs: DONE bsId=${bs.getString('workEffortId')} scoreBs=${scoreBs} perimetro=${orgUnitIds} individuali_aggiornate=${matchingEp.size()}", MODULE)