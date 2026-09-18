import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
 * Totale punteggi della scheda (riga "Totale" sotto il grid indicatori, Performance Strategica).
 *   context.scoreTotal      -> somma SCOREKPI/ACTUAL (ciclo finale)
 *   context.scoreTotalMax   -> somma kpi_score_weight di tutte le misure
 *   context.scoreTotalInt   -> somma SCOREKPI/ACTUAL_INT (ciclo semestrale, solo misure flag=Y)
 *   context.scoreTotalMaxInt-> somma kpi_score_weight delle sole misure con consuntivabileParzialmente=Y
 *   context.hasIntermediate -> true se almeno una misura ha consuntivabileParzialmente=Y
 */

def workEffortId = context.workEffortId;
if (UtilValidate.isEmpty(workEffortId)) { workEffortId = parameters?.workEffortId; }
if (UtilValidate.isEmpty(workEffortId) && UtilValidate.isNotEmpty(context.listIt)) { workEffortId = context.listIt[0]?.workEffortId; }

context.scoreTotal = 0;
context.scoreTotalMax = 0;
context.scoreTotalInt = 0;
context.scoreTotalMaxInt = 0;
context.hasIntermediate = false;
if (UtilValidate.isEmpty(workEffortId)) { return; }

try {
    // Somma SCOREKPI/ACTUAL (ciclo finale)
    def scoreList = delegator.findList("WorkEffortMeasureScoreKpi",
        EntityCondition.makeCondition([
            EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
            EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL")
        ], EntityOperator.AND), null, null, null, false);
    def tot = 0.0d;
    scoreList.each { s -> if (s.getBigDecimal("amount") != null) { tot += s.getBigDecimal("amount").doubleValue(); } };
    context.scoreTotal = tot;

    // Pesi: massimo globale + massimo semestrale (solo flag=Y)
    def measList = delegator.findByAnd("WorkEffortMeasure", UtilMisc.toMap("workEffortId", workEffortId));
    measList = EntityUtil.filterByDate(measList);
    def maxTot = 0.0d;
    def maxTotInt = 0.0d;
    boolean hasInt = false;
    measList.each { m ->
        def weight = m.getBigDecimal("kpiScoreWeight");
        if (weight != null) {
            maxTot += weight.doubleValue();
            String gaId = m.getString("glAccountId");
            if (UtilValidate.isNotEmpty(gaId)) {
                def ga = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", gaId), true);
                if (ga != null && "Y".equals(ga.getString("consuntivabileParzialmente"))) {
                    maxTotInt += weight.doubleValue();
                    hasInt = true;
                }
            }
        }
    };
    context.scoreTotalMax = maxTot;
    context.scoreTotalMaxInt = maxTotInt;
    context.hasIntermediate = hasInt;

    // Somma SCOREKPI/ACTUAL_INT (ciclo semestrale, solo se esistono misure flag=Y)
    def totInt = 0.0d;
    if (hasInt) {
        def scoreListInt = delegator.findList("WorkEffortMeasureScoreKpi",
            EntityCondition.makeCondition([
                EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
                EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL_INT")
            ], EntityOperator.AND), null, null, null, false);
        scoreListInt.each { s -> if (s.getBigDecimal("amount") != null) { totInt += s.getBigDecimal("amount").doubleValue(); } };
    }
    context.scoreTotalInt = totInt;

    Debug.logInfo("### getIndicatorScoreTotal: weId=" + workEffortId + " total=" + tot + " max=" + maxTot
        + " totalInt=" + totInt + " maxInt=" + maxTotInt + " hasInt=" + hasInt
        + " (n SCOREKPI=" + scoreList.size() + ")", "getIndicatorScoreTotal");
} catch (Exception e) {
    Debug.logError(e, "getIndicatorScoreTotal.groovy: " + e.getMessage(), "getIndicatorScoreTotal");
}
