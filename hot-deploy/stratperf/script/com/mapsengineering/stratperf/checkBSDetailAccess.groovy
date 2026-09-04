import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil

/**
 * checkBSDetailAccess — autorizzazione APERTURA DETTAGLIO scheda CTX_BS (anti-IDOR).
 *
 * Il dettaglio (view managementContainerOnly -> ManagementContainerOnlyScreen) si apre per workEffortId.
 * Senza questo check un Direttore UO puo' aprire la scheda di QUALSIASI UO conoscendone l'id (la LISTA e'
 * scopata da executePerformFindBSWorkEffortRoot*, ma il DETTAGLIO no): Broken Access Control / IDOR.
 *
 * Regola = STESSA visibilita' della lista (invariante: apri il dettaglio esattamente delle schede che vedi):
 *   - admin (BSCPERFMGR_ADMIN o gruppo AORNADMIN)               -> tutte;
 *   - Dir Sanitario/Amministrativo (STRATPERF_DIR_SAN/AMM)      -> tutte;
 *   - Dir UO (STRATPERF_DIR_UO): solo le UO di cui e' ORG_RESPONSIBLE (qualunque ruolo);
 *   - chiunque altro                                            -> nessuna.
 *
 * Solo per work effort CTX_BS: per altri tipi/contesti non blocca (bsDetailAuthorized resta true).
 * Imposta context.bsDetailAuthorized (Boolean), letto dallo screen per rendere il dettaglio o il messaggio.
 * NB motore groovy: solo findOne/findList + EntityCondition (no findByAnd con null).
 */

context.bsDetailAuthorized = true

String weId = UtilValidate.isNotEmpty(parameters.workEffortIdRoot) ? parameters.workEffortIdRoot : parameters.workEffortId
if (UtilValidate.isEmpty(weId)) return

def we = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", weId), false)
if (we == null || !"CTX_BS".equals(we.getString("workEffortTypeId"))) return   // check solo su CTX_BS

String userLoginId = userLogin?.getString("userLoginId")
if (UtilValidate.isEmpty(userLoginId)) { context.bsDetailAuthorized = false; return }

def groups = EntityUtil.filterByDate(delegator.findList("UserLoginSecurityGroup",
        EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId), null, null, null, false))
def gids = groups.collect { it.getString("groupId") }
boolean isAdmin = security.hasPermission("BSCPERFMGR_ADMIN", userLogin) || gids.contains("AORNADMIN")
boolean isSanAmm = gids.contains("STRATPERF_DIR_SAN") || gids.contains("STRATPERF_DIR_AMM")
if (isAdmin || isSanAmm) return    // vedono tutto

// Dir UO: autorizzato solo se ORG_RESPONSIBLE della UO della scheda (qualunque ruolo) -> come la lista.
String orgUnitId = we.getString("orgUnitId")
boolean responsabile = false
if (UtilValidate.isNotEmpty(orgUnitId)) {
    def rels = EntityUtil.filterByDate(delegator.findList("PartyRelationship", EntityCondition.makeCondition([
            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, orgUnitId),
            EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ORG_RESPONSIBLE")
    ], EntityOperator.AND), null, null, null, false))
    responsabile = UtilValidate.isNotEmpty(rels)
}

if (!responsabile) {
    context.bsDetailAuthorized = false
    Debug.logWarning("[BS-DETAIL-AUTH] accesso NEGATO al dettaglio: " + userLoginId + " -> scheda " + weId
            + " (UO " + orgUnitId + "): non responsabile.", "checkBSDetailAccess")
}
return
