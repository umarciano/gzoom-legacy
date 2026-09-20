import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
 * checkBSDirettoreUo - PUNTO UNICO della capability "sono il Direttore della UO di QUESTA scheda".
 *
 * Regola (gerarchia profili 2026): il Direttore di UO NON e' piu' identificato dal solo gruppo
 * STRATPERF_DIR_UO. La gerarchia rimuove i ruoli verso il basso (POST_IMPORT_ASSEGNA_PROFILI.sql:
 * chi e' SAN/AMM perde DIR_UO e DIR_DIP), quindi un direttore di dipartimento che dirige anche una
 * propria UO non ha piu' il gruppo DIR_UO pur essendone il direttore a tutti gli effetti.
 * Il criterio corretto e': essere un direttore (UO | DIP | SAN | AMM) ED essere ORG_RESPONSIBLE
 * della UO della scheda.
 *
 * Distinto dal PERIMETRO di visibilita' (getBSPerimetroOrgUnits): un Dir Dipartimento VEDE tutte le
 * schede del proprio dipartimento, ma e' Direttore di UO solo su quella che dirige personalmente.
 *
 * Output in context:
 *   bsIsDirettore        Boolean  appartiene a un gruppo direttore (UO | DIP | SAN | AMM)
 *   bsIsDirSanAmm        Boolean  DIR_SAN o DIR_AMM (validazione completa: globale, senza ORG_RESPONSIBLE)
 *   bsIsResponsabileWe   Boolean  ORG_RESPONSIBLE della UO della scheda
 *   bsIsDirettoreUoWe    Boolean  bsIsDirettore && bsIsResponsabileWe
 *   bsWeCurrentStatusId  String   stato REALE della scheda, letto dall'entita' WorkEffort
 *   bsIsCtxBs            Boolean  la scheda e' di tipo CTX_BS (dall'entita', non dai parametri)
 *
 * Lo stato va sempre preso da qui e MAI da context.currentStatusId: nel form root
 * (WorkEffortRootViewManagementForm) loadWorkEffortViewCard non gira, quindi context.currentStatusId
 * resta il valore del FILTRO di ricerca - vuoto nel Portale, stale dopo un cambio stato.
 *
 * NB motore groovy: solo findOne/findList + EntityCondition.
 */

context.bsIsDirettore = false;
context.bsIsDirSanAmm = false;
context.bsIsResponsabileWe = false;
context.bsIsDirettoreUoWe = false;
context.bsWeCurrentStatusId = null;
context.bsIsCtxBs = false;

String userLoginId = userLogin?.getString("userLoginId");
if (UtilValidate.isEmpty(userLoginId)) return;

def groups = EntityUtil.filterByDate(delegator.findList("UserLoginSecurityGroup",
        EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
        null, null, null, false));
def gids = groups.collect { it.getString("groupId") };

boolean isDirSanAmm = gids.contains("STRATPERF_DIR_SAN") || gids.contains("STRATPERF_DIR_AMM");
context.bsIsDirSanAmm = isDirSanAmm;
context.bsIsDirettore = isDirSanAmm || gids.contains("STRATPERF_DIR_UO") || gids.contains("STRATPERF_DIR_DIP");

String weId = context.workEffortId ?: parameters.workEffortId;
if (UtilValidate.isEmpty(weId)) { weId = parameters.workEffortIdRoot; }
if (UtilValidate.isEmpty(weId)) return;

def we = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", weId), false);
if (we == null) return;

context.bsWeCurrentStatusId = we.getString("currentStatusId");
context.bsIsCtxBs = "CTX_BS".equals(we.getString("workEffortTypeId"));

String orgUnitId = we.getString("orgUnitId");
String myPartyId = userLogin.getString("partyId");
if (UtilValidate.isEmpty(orgUnitId) || UtilValidate.isEmpty(myPartyId)) return;

def rels = delegator.findList("PartyRelationship", EntityCondition.makeCondition([
        EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, orgUnitId),
        EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, myPartyId),
        EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ORG_RESPONSIBLE")
], EntityOperator.AND), null, null, null, false);
def nowTs = UtilDateTime.nowTimestamp();
for (r in rels) {
    def thru = r.getTimestamp("thruDate");
    if (thru == null || thru.after(nowTs)) { context.bsIsResponsabileWe = true; break; }
}

context.bsIsDirettoreUoWe = context.bsIsDirettore && context.bsIsResponsabileWe;
