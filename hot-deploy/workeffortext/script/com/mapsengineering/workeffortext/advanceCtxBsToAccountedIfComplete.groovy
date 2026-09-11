import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.service.ServiceUtil

/**
 * advanceCtxBsToAccountedIfComplete
 *
 * Auto-avanzamento della scheda Performance Organizzativa (CTX_BS) da "Da consuntivare"
 * (WEORCARD_TOACCOUNT) a "Consuntivata" (WEORCARD_ACCOUNTED) quando TUTTI gli indicatori
 * della scheda hanno un consuntivo FINALE (movimento ACTUAL).
 *
 * MARKER = movimento ACTUAL con entry sull'indicatore (glAccountId = indicatore). E' scelto
 * apposta al posto di SCOREKPI perche' e' robusto anche per gli indicatori SENZA fascia
 * (annuali/SI-NO) che NON producono SCOREKPI: nel ciclo finale ogni indicatore (flag Y e N)
 * viene valorizzato con ACTUAL, quindi la presenza dell'ACTUAL e' il segnale corretto di
 * "consuntivato".
 *
 * DOPPIO CICLO (2026-09): questo hook riguarda SOLO il ciclo FINALE:
 *   - guardia su currentStatusId == WEORCARD_TOACCOUNT (stato finale);
 *   - marker ACTUAL (NON ACTUAL_INT), quindi i salvataggi intermedi non lo attivano.
 * Il ciclo INTERMEDIO (WEORCARD_TOACC_INT -> WEORCARD_ACC_INT, solo indicatori flag=Y,
 * marker ACTUAL_INT/SCOREKPI) e' gestito separatamente da checkCardCompleteAndAdvance:
 * i due non interferiscono (stato e fiscal-type diversi).
 *
 * SICUREZZA: invocato in coda a saveIndicatorConsuntivo (stessa transazione, per "vedere" il
 * movimento appena salvato). NON deve MAI far fallire quel salvataggio:
 *   - tutto il corpo e' in try/catch (ritorna sempre success, non propaga eccezioni);
 *   - il cambio stato gira in TRANSAZIONE NUOVA (requireNewTransaction=true), cosi' un suo errore
 *     non marca rollback-only la transazione del consuntivo.
 *
 * NB Groovy/OFBiz: NON usare delegator.findByAnd(name, map, null, false) ne' UtilMisc.toMap(...):
 *   con l'argomento null Groovy risolve alla overload varargs findByAnd(String, Object...) che
 *   fa UtilMisc.toMap([map,null,false]) -> "You must pass an even sized array". Si usano SEMPRE
 *   findList + EntityCondition (firma unica, nessuna ambiguita').
 *
 * IN: workEffortId (la scheda CTX_BS; passare wem.workEffortId, non parameters.workEffortId che
 *     createWeTrans azzera).
 */

String MODULE = "advanceCtxBsToAccountedIfComplete"
try {
    String weId = parameters.workEffortId
    if (!UtilValidate.isNotEmpty(weId)) return ServiceUtil.returnSuccess()

    def scheda = delegator.findOne("WorkEffort", [workEffortId: weId], false)
    if (scheda == null || !"CTX_BS".equals(scheda.getString("workEffortTypeId"))) return ServiceUtil.returnSuccess()
    // Solo da "Da consuntivare" (ciclo finale): niente re-fire su stati successivi; a CLOSED e' congelato.
    if (!"WEORCARD_TOACCOUNT".equals(scheda.getString("currentStatusId"))) return ServiceUtil.returnSuccess()

    // Indicatori RICHIESTI = glAccountId di TUTTE le misure attive della scheda (le misure CTX_BS sono gli indicatori).
    def measures = EntityUtil.filterByDate(delegator.findList("WorkEffortMeasure",
        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, weId),
        null, null, null, false))
    def required = new HashSet()
    for (m in measures) {
        String gid = m.getString("glAccountId")
        if (UtilValidate.isNotEmpty(gid)) required.add(gid)
    }
    if (required.isEmpty()) return ServiceUtil.returnSuccess()

    // Indicatori CONSUNTIVATI = glAccountId con un movimento ACTUAL (CTX_BS) sulla scheda.
    def trans = delegator.findList("AcctgTrans", EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, weId),
        EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, "ACTUAL"),
        EntityCondition.makeCondition("acctgTransTypeId", EntityOperator.EQUALS, "CTX_BS")
    ], EntityOperator.AND), null, null, null, false)
    def done = new HashSet()
    for (t in trans) {
        def entries = delegator.findList("AcctgTransEntry",
            EntityCondition.makeCondition("acctgTransId", EntityOperator.EQUALS, t.getString("acctgTransId")),
            null, null, null, false)
        for (e in entries) {
            String gid = e.getString("glAccountId")
            if (required.contains(gid)) done.add(gid)
        }
    }

    if (!done.containsAll(required)) {
        Debug.log("[AUTO-ACCOUNTED] scheda ${weId}: consuntivati ${done.size()}/${required.size()}, resto in TOACCOUNT", MODULE)
        return ServiceUtil.returnSuccess()
    }

    // Tutti consuntivati -> avanza a ACCOUNTED. Mappa esplicita (niente toMap). Transazione NUOVA
    // (firma runSync a 4 arg: String, Map, int, boolean): un errore qui non tocca il consuntivo.
    def ctx = new HashMap()
    ctx.put("workEffortId", weId)
    ctx.put("statusId", "WEORCARD_ACCOUNTED")
    // userLogin: accesso SICURO dal binding (binding.hasVariable(String) non e' disponibile in questo
    // runner groovy -> lanciava "No signature of method: Binding.hasVariable()" e bloccava l'avanzamento).
    def ul = binding.variables.get("userLogin")
    if (ul != null) ctx.put("userLogin", ul)
    def res = dctx.dispatcher.runSync("changeWorkEffortRootStatus", ctx, 0, true)
    if (ServiceUtil.isError(res)) {
        Debug.logWarning("[AUTO-ACCOUNTED] errore avanzamento ${weId}: " + ServiceUtil.getErrorMessage(res), MODULE)
    } else {
        Debug.log("[AUTO-ACCOUNTED] scheda ${weId}: tutti i ${required.size()} indicatori consuntivati -> ACCOUNTED", MODULE)
    }
} catch (Exception e) {
    // MAI far fallire il salvataggio del consuntivo per colpa dell'auto-avanzamento.
    Debug.logWarning("[AUTO-ACCOUNTED] eccezione (ignorata, consuntivo salvo): ${e.message}", MODULE)
}
return ServiceUtil.returnSuccess()
