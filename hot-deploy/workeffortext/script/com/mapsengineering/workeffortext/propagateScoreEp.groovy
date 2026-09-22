import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.base.util.UtilValidate

def ep = delegator.findOne("WorkEffort", [workEffortId: parameters.workEffortEpId], false)
if (ep == null || "CTX_EP" != ep.getString("workEffortTypeId")) {
    return
}

def measureList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(
        "workEffortId", EntityOperator.EQUALS, ep.getString("workEffortId")
), null, null, null, false)

def scoreEntries = []
measureList.each { measure ->
    def transactions = delegator.findList("AcctgTrans", EntityCondition.makeCondition([
            EntityCondition.makeCondition("voucherRef", EntityOperator.EQUALS, measure.getString("workEffortMeasureId")),
            EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL_PY")
    ], EntityOperator.AND), null, null, null, false)
    transactions.each { transaction ->
        scoreEntries.addAll(delegator.findList("AcctgTransEntry", EntityCondition.makeCondition([
                EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS, transaction.getString("acctgTransId")),
                EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, measure.getString("glAccountId"))
        ], EntityOperator.AND), null, null, null, false))
    }
}

if (UtilValidate.isEmpty(scoreEntries)) {
    return
}

def scoreEp = scoreEntries.inject(BigDecimal.ZERO) { total, entry ->
    total + (entry.getBigDecimal("amount") ?: BigDecimal.ZERO)
}.setScale(2, BigDecimal.ROUND_HALF_UP)

// Denominatore 30 = 6 competenze × 5 max, coerente con il report BIRT (SchedaObiettiviOrganizzativi)
def base = ["SCHEDA 4", "SCHEDA 5"].contains(ep.getString("etch")) ? new BigDecimal("60") : new BigDecimal("40")
def adjustedScoreEp = scoreEp.divide(new BigDecimal("30"), 8, BigDecimal.ROUND_HALF_UP)
        .multiply(base).setScale(2, BigDecimal.ROUND_HALF_UP)

ep.set("scoreEp", scoreEp)
ep.set("adjustedScoreEp", adjustedScoreEp)

def adjustedBs = ep.getBigDecimal("adjustedScoreBs")
ep.set("overallEpBsScore",
        (adjustedScoreEp ?: BigDecimal.ZERO).add(adjustedBs ?: BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP))
delegator.store(ep)
