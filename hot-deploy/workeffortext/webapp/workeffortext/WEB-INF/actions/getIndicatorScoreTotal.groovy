import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
 * Totale punteggi della scheda (riga "Totale" sotto il grid indicatori, Performance Strategica).
 *   context.scoreTotal    -> somma dei punteggi SCOREKPI (ACTUAL) delle misure della scheda
 *   context.scoreTotalMax -> somma dei pesi massimi (kpi_score_weight) delle misure indicatore
 */

def workEffortId = context.workEffortId;
if (UtilValidate.isEmpty(workEffortId)) { workEffortId = parameters?.workEffortId; }
if (UtilValidate.isEmpty(workEffortId) && UtilValidate.isNotEmpty(context.listIt)) { workEffortId = context.listIt[0]?.workEffortId; }

context.scoreTotal = 0;
context.scoreTotalMax = 0;
if (UtilValidate.isEmpty(workEffortId)) { return; }

try {
    // Somma dei punteggi SCOREKPI (view: entry gl_account=SCOREKPI, voucher_ref=misura) della scheda
    def scoreList = delegator.findList("WorkEffortMeasureScoreKpi",
        EntityCondition.makeCondition([
            EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
            EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL")
        ], EntityOperator.AND), null, null, null, false);
    def tot = 0.0d;
    scoreList.each { s -> if (s.getBigDecimal("amount") != null) { tot += s.getBigDecimal("amount").doubleValue(); } };
    context.scoreTotal = tot;

    // Massimo = somma dei pesi degli indicatori (misure WEMT_PERF) della scheda
    def measList = delegator.findByAnd("WorkEffortMeasure", UtilMisc.toMap("workEffortId", workEffortId));
    measList = EntityUtil.filterByDate(measList);
    def maxTot = 0.0d;
    measList.each { m -> if (m.getBigDecimal("kpiScoreWeight") != null) { maxTot += m.getBigDecimal("kpiScoreWeight").doubleValue(); } };
    context.scoreTotalMax = maxTot;
    Debug.logInfo("### getIndicatorScoreTotal: weId=" + workEffortId + " total=" + tot + " max=" + maxTot + " (n SCOREKPI=" + scoreList.size() + ")", "getIndicatorScoreTotal");
} catch (Exception e) {
    Debug.logError(e, "getIndicatorScoreTotal.groovy: " + e.getMessage(), "getIndicatorScoreTotal");
}
