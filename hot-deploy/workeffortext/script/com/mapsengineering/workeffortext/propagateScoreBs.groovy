import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.base.util.UtilValidate

def bs = delegator.findOne("WorkEffort", [workEffortId: parameters.workEffortBsId], false)
if (bs == null || "CTX_BS" != bs.getString("workEffortTypeId")) {
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
        scoreEntries.addAll(delegator.findList("AcctgTransEntry", EntityCondition.makeCondition([
                EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS, transaction.getString("acctgTransId")),
                EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, "SCOREKPI"),
                EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL")
        ], EntityOperator.AND), null, null, null, false))
    }
}

if (UtilValidate.isEmpty(scoreEntries)) {
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

def orgUnitId = bs.getString("orgUnitId")
def bsStart = bs.getTimestamp("estimatedStartDate")
def bsEnd = bs.getTimestamp("estimatedCompletionDate")
def epList = delegator.findList("WorkEffort", EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "CTX_EP"),
        EntityCondition.makeCondition("orgUnitId", EntityOperator.EQUALS, orgUnitId)
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