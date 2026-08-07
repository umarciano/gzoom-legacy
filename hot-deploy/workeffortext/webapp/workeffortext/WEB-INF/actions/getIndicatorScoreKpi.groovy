import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
 * Row-action del grid "Indicatori di valutazione" (Performance Strategica).
 * Espone il PUNTEGGIO manuale corrente dell'indicatore (transazione SCOREKPI della misura)
 * e il peso massimo, per pre-compilare e limitare la cella editabile "Valore".
 *   context.indicatorScore    -> punteggio corrente (weTransValue della SCOREKPI ACTUAL), o null
 *   context.indicatorScoreMax -> peso massimo (kpi_score_weight della misura), o null
 */

def workEffortMeasureId = context.workEffortMeasureId;
context.indicatorScore = null;
context.indicatorScoreMax = null;

// Modifica del punteggio consentita SOLO all'admin (gruppo AORNADMIN) per ora.
// TODO: estendere ai gestori (BSCPERFMGR) e vincolare agli stati di valutazione — vedi doc analisi (OP).
context.scoreEditable = "N";
if (UtilValidate.isNotEmpty(userLogin)) {
    def adminGrps = EntityUtil.filterByDate(delegator.findByAnd("UserLoginSecurityGroup",
        ["userLoginId": userLogin.userLoginId, "groupId": "AORNADMIN"]));
    if (UtilValidate.isNotEmpty(adminGrps)) { context.scoreEditable = "Y"; }
}

if (UtilValidate.isEmpty(workEffortMeasureId)) {
    return;
}

// Peso massimo = kpi_score_weight della misura
try {
    def wem = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId": workEffortMeasureId], false);
    if (UtilValidate.isNotEmpty(wem) && UtilValidate.isNotEmpty(wem.kpiScoreWeight)) {
        context.indicatorScoreMax = wem.kpiScoreWeight;
    }
} catch (Exception e) {
    Debug.logError(e, "getIndicatorScoreKpi.groovy: peso - " + e.getMessage(), "getIndicatorScoreKpi");
}

// Punteggio corrente: ultima SCOREKPI (ACTUAL) della misura.
// La view WorkEffortMeasureScoreKpi lega la misura via AcctgTrans.voucherRef = workEffortMeasureId
// ed entry gl_account = 'SCOREKPI'; espone amount = punteggio.
try {
    def cond = EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortMeasureId", EntityOperator.EQUALS, workEffortMeasureId),
        EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL")
    ], EntityOperator.AND);
    def list = delegator.findList("WorkEffortMeasureScoreKpi", cond, null, ["transactionDate DESC"], null, false);
    if (UtilValidate.isNotEmpty(list)) {
        def score = EntityUtil.getFirst(list).getBigDecimal("amount");
        if (UtilValidate.isNotEmpty(score)) {
            context.indicatorScore = score;
        }
    }
} catch (Exception e) {
    Debug.logError(e, "getIndicatorScoreKpi.groovy: score - " + e.getMessage(), "getIndicatorScoreKpi");
}
