import org.ofbiz.base.util.*
import org.ofbiz.entity.condition.*
import org.ofbiz.entity.util.*
import org.ofbiz.entity.transaction.TransactionUtil

/**
 * Salvataggio AJAX dei voti competenze della Performance Individuale (CTX_EP), digitati inline nella
 * colonna "Valore" della griglia (pulsante "Salva voti", WorkEffortEmplScoreSaveButton.ftl).
 * Per ogni competenza scrive/sostituisce il movimento ACTUAL_PY sul conto della competenza
 * (stessa forma del pannello di valutazione, letta dal totale e dalla stampa BIRT).
 * Idempotente per voucherRef = workEffortMeasureId + glFiscalTypeId = ACTUAL_PY.
 *
 * IN:  parameters.workEffortId, parameters.wemIds (CSV), parameters.scores (CSV parallelo).
 * OUT: JSON {"success":bool,"saved":N} | {"success":false,"error":".."}
 * NB: niente JsonSlurper (non disponibile in questo Groovy) -> due liste CSV parallele.
 */

def writeJson = { json ->
    response.setContentType("application/json")
    response.setCharacterEncoding("UTF-8")
    def w = response.getWriter(); w.write(json); w.flush()
}
def esc = { s -> (s ?: "errore").toString().replace('\\', '\\\\').replace('"', '\\"') }

def workEffortId = parameters.workEffortId
def wemIdsStr = parameters.wemIds
def scoresStr = parameters.scores

try {
    boolean hasPerm = security.hasPermission("WORKEFFORTMGR_CREATE", userLogin) ||
                      security.hasPermission("WORKEFFORTMGR_UPDATE", userLogin) ||
                      security.hasPermission("WORKEFFORTORG_ADMIN", userLogin) ||
                      security.hasPermission("WORKEFFORTROLE_ADMIN", userLogin)
    if (!hasPerm) { writeJson('{"success":false,"error":"Permessi insufficienti"}'); return "success" }
    if (UtilValidate.isEmpty(workEffortId) || UtilValidate.isEmpty(wemIdsStr)) {
        writeJson('{"success":false,"error":"Parametri mancanti"}'); return "success"
    }

    def scheda = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false)
    if (scheda == null || !"CTX_EP".equals(scheda.getString("workEffortTypeId"))) {
        writeJson('{"success":false,"error":"Scheda non valida"}'); return "success"
    }

    // Vincolo valutato/valutatore: SOLO il valutatore (WEM_EVAL_MANAGER) di QUESTA scheda puo' salvare,
    // stesso vincolo del pannello originale (checkIfUserIsEvaluatorOnCard). Difesa server-side oltre alla
    // cella editabile solo per il valutatore (getIndicatorScoreKpi). Blocca admin, valutato e altri utenti.
    def myPartyId = userLogin?.getString("partyId")
    def evalMgr = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, myPartyId),
        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_MANAGER")
    ], EntityOperator.AND), null, null, null, false)
    if (UtilValidate.isEmpty(evalMgr)) {
        writeJson('{"success":false,"error":"Non sei il valutatore di questa scheda"}'); return "success"
    }

    String[] wemIds = wemIdsStr.split(",", -1)
    String[] scores = (scoresStr ?: "").split(",", -1)

    def transDate = scheda.getTimestamp("estimatedCompletionDate") ?: UtilDateTime.nowTimestamp()
    def orgUnitId = scheda.getString("orgUnitId")
    def roleTypeId = scheda.getString("orgUnitRoleTypeId")

    int saved = 0
    boolean beganTx = TransactionUtil.begin()
    try {
        for (int i = 0; i < wemIds.length; i++) {
            def wemId = wemIds[i]?.trim()
            def scoreStr = (i < scores.length) ? scores[i]?.trim() : null
            if (UtilValidate.isEmpty(wemId)) continue

            def wem = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId": wemId], false)
            if (wem == null) continue
            def compAccount = wem.getString("glAccountId")

            java.math.BigDecimal score = null
            if (scoreStr != null && scoreStr != "") {
                try { score = new java.math.BigDecimal(scoreStr) } catch (ex) { continue }
                if (score.doubleValue() < 0 || score.doubleValue() > 5) continue
            }

            // Cella VUOTA = "non toccare": salta la competenza senza cancellare il voto esistente.
            // Evita l'azzeramento involontario quando la griglia invia celle non compilate (i voti
            // sparirebbero dalla stampa). Per azzerare davvero un voto si digita esplicitamente 0.
            if (score == null) continue

            // Idempotenza: elimina la ACTUAL_PY precedente di questa competenza (voucherRef = wemId).
            def old = delegator.findList("AcctgTrans", EntityCondition.makeCondition([
                EntityCondition.makeCondition("voucherRef", EntityOperator.EQUALS, wemId),
                EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL_PY")
            ], EntityOperator.AND), null, null, null, false)
            for (t in old) {
                delegator.removeByAnd("AcctgTransEntry", ["acctgTransId": t.getString("acctgTransId")])
                delegator.removeValue(t)
            }

            if (score != null) {
                def transId = delegator.getNextSeqId("AcctgTrans")
                delegator.create(delegator.makeValue("AcctgTrans", [
                    acctgTransId: transId, acctgTransTypeId: "CTX_EP", glFiscalTypeId: "ACTUAL_PY",
                    transactionDate: transDate, isPosted: "N", partyId: orgUnitId, roleTypeId: roleTypeId,
                    workEffortId: workEffortId, voucherRef: wemId]))
                // NB: voucherRef va impostato ANCHE sull'entry: la stampa BIRT
                // (SchedaObiettiviOrganizzativi.rptdesign) abbina la competenza via
                // ACCTG_TRANS_ENTRY.VOUCHER_REF = WORK_EFFORT_MEASURE_ID. Senza, i Punti escono vuoti.
                delegator.create(delegator.makeValue("AcctgTransEntry", [
                    acctgTransId: transId, acctgTransEntrySeqId: "00001", glAccountId: compAccount,
                    glAccountTypeId: "WECAL", organizationPartyId: "Company", amount: score, origAmount: score,
                    currencyUomId: "OTH_SCO", debitCreditFlag: "D", isSummary: "N", voucherRef: wemId]))
            }
            saved++
        }
        TransactionUtil.commit(beganTx)
    } catch (Exception txe) {
        TransactionUtil.rollback(beganTx, "saveEmplScoresAjax: rollback", txe)
        throw txe
    }

    writeJson('{"success":true,"saved":' + saved + '}')
    return "success"
} catch (Exception e) {
    Debug.logError(e, "saveEmplScoresAjax: " + e.getMessage(), "saveEmplScoresAjax")
    writeJson('{"success":false,"error":"' + esc(e.message) + '"}')
    return "success"
}
