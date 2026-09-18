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
 *   - Dir Sanitario (STRATPERF_DIR_SAN)                         -> solo UO dei dipartimenti DIP_SANITARIO;
 *   - Dir Amministrativo (STRATPERF_DIR_AMM)                    -> solo UO dei dipartimenti DIP_AMMINISTRATIVO;
 *   - Dir Dipartimento (STRATPERF_DIR_DIP)                      -> solo UO del proprio dipartimento (ORG_RESPONSIBLE);
 *   - Dir UO (STRATPERF_DIR_UO)                                 -> solo la UO di cui e' ORG_RESPONSIBLE;
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
if (isAdmin) return   // admin vede tutto

String orgUnitId = we.getString("orgUnitId")

boolean isSanAmm = gids.contains("STRATPERF_DIR_SAN") || gids.contains("STRATPERF_DIR_AMM")
if (isSanAmm) {
    // Feature 2: verifica che la UO della scheda appartenga al perimetro SAN o AMM.
    // GROUP_ROLLUP a Cardarelli: partyIdFrom=GENITORE(Dept), partyIdTo=FIGLIO(UO).
    // Per trovare il dipartimento di una UO: query partyIdTo=orgUnitId, leggi partyIdFrom.
    String targetGroupId = gids.contains("STRATPERF_DIR_SAN") ? "DIP_SANITARIO" : "DIP_AMMINISTRATIVO";
    if (UtilValidate.isNotEmpty(orgUnitId)) {
        def nowTs = org.ofbiz.base.util.UtilDateTime.nowTimestamp()
        def deptRels = EntityUtil.filterByDate(delegator.findList("PartyRelationship",
                EntityCondition.makeCondition([
                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, orgUnitId),
                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP")
                ], EntityOperator.AND), null, null, null, false))
        for (deptRel in deptRels) {
            String deptId = deptRel.getString("partyIdFrom")
            def classifs = delegator.findList("PartyClassification",
                    EntityCondition.makeCondition([
                        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, deptId),
                        EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, targetGroupId)
                    ], EntityOperator.AND), null, null, null, false)
            def active = classifs?.findAll { cl -> def t = cl.getTimestamp("thruDate"); t == null || t.after(nowTs) }
            if (UtilValidate.isNotEmpty(active)) return  // dipartimento nel perimetro -> autorizzato
        }
        context.bsDetailAuthorized = false
        Debug.logWarning("[BS-DETAIL-AUTH] DIR_" + targetGroupId + " accesso NEGATO: "
                + userLoginId + " -> scheda " + weId + " (UO " + orgUnitId
                + "): dipartimento non nel perimetro " + targetGroupId, "checkBSDetailAccess")
        return
    }
    return  // orgUnitId vuota: fallback permissivo (edge case - non blocchiamo)
}

// Dir Dipartimento: autorizzato se la UO della scheda e' figlia diretta (1 hop GROUP_ROLLUP)
// di un dipartimento di cui l'utente e' ORG_RESPONSIBLE.
// GROUP_ROLLUP a Cardarelli: partyIdFrom=GENITORE(Dept), partyIdTo=FIGLIO(UO).
boolean isDirDip = gids.contains("STRATPERF_DIR_DIP")
if (isDirDip) {
    if (UtilValidate.isNotEmpty(orgUnitId)) {
        def myDeptRels = EntityUtil.filterByDate(delegator.findList("PartyRelationship",
                EntityCondition.makeCondition([
                    EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, userLogin.getString("partyId")),
                    EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ORG_RESPONSIBLE")
                ], EntityOperator.AND), null, null, null, false))
        for (deptRel in myDeptRels) {
            String deptId = deptRel.getString("partyIdFrom")
            def childRels = EntityUtil.filterByDate(delegator.findList("PartyRelationship",
                    EntityCondition.makeCondition([
                        EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, deptId),
                        EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, orgUnitId),
                        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP")
                    ], EntityOperator.AND), null, null, null, false))
            if (UtilValidate.isNotEmpty(childRels)) return  // UO appartiene al dipartimento -> autorizzato
        }
    }
    context.bsDetailAuthorized = false
    Debug.logWarning("[BS-DETAIL-AUTH] DIR_DIP accesso NEGATO: " + userLoginId + " -> scheda " + weId
            + " (UO " + orgUnitId + "): UO non nel dipartimento del direttore.", "checkBSDetailAccess")
    return
}

// Dir UO: autorizzato solo se ORG_RESPONSIBLE della UO della scheda (qualunque ruolo) -> come la lista.
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
