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

// Misura + tipo scheda (CTX_BS = strategica -> SCOREKPI; CTX_EP = individuale -> ACTUAL_PY competenza).
def wem = null;
boolean isEmplPerf = false;
try {
    wem = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId": workEffortMeasureId], false);
    if (UtilValidate.isNotEmpty(wem)) {
        def scheda = delegator.findOne("WorkEffort", ["workEffortId": wem.workEffortId], false);
        if (UtilValidate.isNotEmpty(scheda) && "CTX_EP".equals(scheda.workEffortTypeId)) { isEmplPerf = true; }
    }
} catch (Exception e) {
    Debug.logError(e, "getIndicatorScoreKpi.groovy: misura/scheda - " + e.getMessage(), "getIndicatorScoreKpi");
}

if (isEmplPerf) {
    // ---- Performance INDIVIDUALE (CTX_EP): il voto e' il rating della competenza (1..5), memorizzato
    //      come movimento ACTUAL_PY sul conto della competenza (wem.glAccountId). E' lo stesso valore
    //      che legge il totale (WorkEffortIndicatorScoreTotEmpl.ftl) e la stampa BIRT. Max per competenza = 5. ----
    final int MAX_PER_COMPETENZA = 5;
    context.indicatorScoreMax = new java.math.BigDecimal(MAX_PER_COMPETENZA);

    // Colonna "Valore" EDITABILE inline SOLO per il VALUTATORE di questa scheda (WEM_EVAL_MANAGER),
    // stesso vincolo valutato/valutatore del pannello originale (checkIfUserIsEvaluatorOnCard). Per tutti
    // gli altri (admin, valutato, altri utenti) resta in sola lettura. NB: in Interrogazione (rootInqyTree=Y)
    // la form e' comunque forzata read-only. Il salvataggio (saveEmplScoresAjax) ri-verifica lato server.
    context.scoreEditable = "N";
    try {
        def myPartyId = userLogin?.getString("partyId");
        if (UtilValidate.isNotEmpty(myPartyId)) {
            def evalMgr = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition([
                EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, wem.workEffortId),
                EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, myPartyId),
                EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_MANAGER")
            ], EntityOperator.AND), null, null, null, false);
            if (UtilValidate.isNotEmpty(evalMgr)) { context.scoreEditable = "Y"; }
        }
    } catch (Exception e) {
        Debug.logError(e, "getIndicatorScoreKpi.groovy: eval-mgr CTX_EP - " + e.getMessage(), "getIndicatorScoreKpi");
    }
    try {
        def cond = EntityCondition.makeCondition([
            EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, wem.workEffortId),
            EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL_PY")
        ], EntityOperator.AND);
        def transList = delegator.findList("AcctgTrans", cond, null, null, null, false);
        for (t in transList) {
            def eCond = EntityCondition.makeCondition([
                EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS, t.acctgTransId),
                EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, wem.glAccountId)
            ], EntityOperator.AND);
            def entries = delegator.findList("AcctgTransEntry", eCond, null, null, null, false);
            if (UtilValidate.isNotEmpty(entries)) {
                def amt = EntityUtil.getFirst(entries).getBigDecimal("amount");
                if (UtilValidate.isNotEmpty(amt)) { context.indicatorScore = amt; }
                break;
            }
        }
    } catch (Exception e) {
        Debug.logError(e, "getIndicatorScoreKpi.groovy: score CTX_EP - " + e.getMessage(), "getIndicatorScoreKpi");
    }
    return;
}

// ---- Performance STRATEGICA (CTX_BS): punteggio manuale SCOREKPI (comportamento originale). ----
// Peso massimo = kpi_score_weight della misura
try {
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
