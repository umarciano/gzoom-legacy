import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.GroovyUtil
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate

/**
 * checkBSDetailAccess - autorizzazione APERTURA DETTAGLIO scheda CTX_BS (anti-IDOR).
 *
 * Il dettaglio (view managementContainerOnly -> ManagementContainerOnlyScreen) si apre per workEffortId.
 * Senza questo check un direttore potrebbe aprire la scheda di QUALSIASI UO conoscendone l'id (la LISTA
 * e' scopata da executePerformFindBSWorkEffortRoot*, ma il DETTAGLIO no): Broken Access Control / IDOR.
 *
 * Regola = STESSA visibilita' della lista (invariante: apri il dettaglio esattamente delle schede che
 * vedi). L'invariante e' garantita per costruzione perche' il perimetro viene da getBSPerimetroOrgUnits,
 * lo stesso punto usato da applyScopingBSFilter per la lista.
 *
 * Solo per work effort CTX_BS: per altri tipi/contesti non blocca (bsDetailAuthorized resta true).
 * Imposta context.bsDetailAuthorized (Boolean), letto dallo screen per rendere il dettaglio o il messaggio.
 */

context.bsDetailAuthorized = true

String weId = UtilValidate.isNotEmpty(parameters.workEffortIdRoot) ? parameters.workEffortIdRoot : parameters.workEffortId
if (UtilValidate.isEmpty(weId)) return

def we = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", weId), false)
if (we == null || !"CTX_BS".equals(we.getString("workEffortTypeId"))) return   // check solo su CTX_BS

if (UtilValidate.isEmpty(userLogin?.getString("userLoginId"))) { context.bsDetailAuthorized = false; return }

String orgUnitId = we.getString("orgUnitId")
if (UtilValidate.isEmpty(orgUnitId)) return   // scheda senza UO: fallback permissivo (edge case)

GroovyUtil.runScriptAtLocation("com/mapsengineering/stratperf/getBSPerimetroOrgUnits.groovy", context)

if ("ADMIN".equals(context.bsPerimetroKind)) return   // admin vede tutto

def perimetro = context.bsPerimetroOrgUnits
if (perimetro == null || !perimetro.contains(orgUnitId)) {
    context.bsDetailAuthorized = false
    Debug.logWarning("[BS-DETAIL-AUTH] accesso NEGATO al dettaglio: " + userLogin.getString("userLoginId")
            + " -> scheda " + weId + " (UO " + orgUnitId + "): fuori perimetro "
            + context.bsPerimetroKind, "checkBSDetailAccess")
}
return
