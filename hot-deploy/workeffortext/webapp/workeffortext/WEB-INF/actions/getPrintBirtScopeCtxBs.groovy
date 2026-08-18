import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

// -----------------------------------------------------------------------------
// Scoping della tendina "Scheda" (workEffortId) del form di STAMPA per CTX_BS
// (Performance Strategica).
//
// Requisito (deciso con l'utente): il direttore vede SOLO le proprie schede, cioe'
// quelle delle UO di cui e' responsabile (party_relationship ORG_RESPONSIBLE /
// DIRETTORE_UOC, stesso meccanismo di executePerformFindBSWorkEffortRoot.groovy);
// l'admin (gruppo AORNADMIN) le vede TUTTE.
//
// Perche' serve: il selettore generico role-based (ramo useFilter=Y ->
// executeChildPerformFindWorkEffortRootInqy) torna VUOTO per CTX_BS (la visibilita'
// nativa RoleView non matcha l'assegnazione del direttore), e il ramo "admin/valutato"
// aggiunge vincoli orgUnitId/currentStatusId legati a campi vuoti che azzerano la query.
// Qui pre-calcoliamo l'elenco corretto e lo passiamo al FTL come dati locali
// dell'autocomplete. Vedi doc 12 (stampe) / doc 10 (scoping direttore).
//
// Lo screen che lo invoca (WorkEffortLoadReportWorkEffortPrintBirtList) e' condiviso da
// tutti i moduli: questo script e' un NO-OP per tipi diversi da CTX_BS (ctxBsPrint="N").
//
// NB: si usano findList + EntityCondition (NON findByAnd/UtilMisc.toMap) per evitare
// l'ambiguita' di overload varargs di findByAnd in Groovy.
// -----------------------------------------------------------------------------

context.ctxBsPrint = "N";

String weTypeId = parameters.workEffortTypeId ?: parameters.parentTypeId;
if (!"CTX_BS".equals(weTypeId)) {
    return;
}
// Solo stampa "corrente": lo storico (snapshot='Y') ha un flusso proprio -> lascio invariato.
if ("Y".equals(parameters.snapshot)) {
    return;
}

def uLogin = context.userLogin ?: parameters.userLogin;
if (uLogin == null) {
    return;
}

// Admin? (gruppo AORNADMIN, assegnazione attiva)
def groups = EntityUtil.filterByDate(delegator.findList("UserLoginSecurityGroup",
        EntityCondition.makeCondition("userLoginId", uLogin.getString("userLoginId")),
        null, null, null, false));
boolean isAdmin = groups?.any { "AORNADMIN".equals(it.getString("groupId")) };
// Dir sanitario/amministrativo: vedono TUTTE le schede come l'admin (anche se hanno ANCHE il profilo
// DIR_UO), perche' non sono DIRETTORE_UOC di alcuna UOC -> la restrizione per UO li azzererebbe.
// Stesso trattamento di executePerformFindBSWorkEffortRoot(Inqy).groovy. Vedi doc 10 §4ter.
boolean isDirSanAmm = groups?.any { "STRATPERF_DIR_SAN".equals(it.getString("groupId")) || "STRATPERF_DIR_AMM".equals(it.getString("groupId")) };

// Condizioni base: schede root CTX_BS non storiche.
def conds = [];
conds.add(EntityCondition.makeCondition("workEffortTypeId", "CTX_BS"));
conds.add(EntityCondition.makeCondition("workEffortSnapshotId", null));

if (!isAdmin && !isDirSanAmm) {
    // UO dirette dall'utente: ORG_RESPONSIBLE / DIRETTORE_UOC, relazioni attive.
    def relConds = [];
    relConds.add(EntityCondition.makeCondition("partyIdTo", uLogin.getString("partyId")));
    relConds.add(EntityCondition.makeCondition("roleTypeIdTo", "DIRETTORE_UOC"));
    relConds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "ORG_RESPONSIBLE"));
    def rels = delegator.findList("PartyRelationship", EntityCondition.makeCondition(relConds),
            null, null, null, false);

    def orgUnits = new LinkedHashSet();
    def nowTs = UtilDateTime.nowTimestamp();
    for (r in rels) {
        def thru = r.getTimestamp("thruDate");
        if (thru == null || thru.after(nowTs)) {
            String ouId = r.getString("partyIdFrom");
            if (UtilValidate.isNotEmpty(ouId)) { orgUnits.add(ouId); }
        }
    }
    if (orgUnits.isEmpty()) {
        // nessuna UO diretta -> nessuna scheda selezionabile
        context.ctxBsPrint = "Y";
        context.ctxBsSchede = [];
        Debug.log("### getPrintBirtScopeCtxBs " + uLogin.getString("userLoginId") + " isAdmin=false NESSUNA UO -> 0 schede");
        return;
    }
    conds.add(EntityCondition.makeCondition("orgUnitId", EntityOperator.IN, new ArrayList(orgUnits)));
}

Set fieldsToSelect = new HashSet();
fieldsToSelect.add("workEffortId");
fieldsToSelect.add("workEffortName");
fieldsToSelect.add("sourceReferenceId");
List orderBy = new ArrayList();
orderBy.add("sourceReferenceId");
orderBy.add("workEffortName");

def rows = delegator.findList("WorkEffortView", EntityCondition.makeCondition(conds),
        fieldsToSelect, orderBy, null, false);

// Dedup per workEffortId (la view puo' produrre piu' righe per scheda).
def seen = new HashSet();
def schede = [];
for (row in rows) {
    String weId = row.getString("workEffortId");
    if (seen.add(weId)) {
        schede.add([workEffortId     : weId,
                    workEffortName   : row.getString("workEffortName"),
                    sourceReferenceId: row.getString("sourceReferenceId")]);
    }
}

context.ctxBsPrint = "Y";
context.ctxBsSchede = schede;
Debug.log("### getPrintBirtScopeCtxBs " + uLogin.getString("userLoginId") + " isAdmin=" + isAdmin + " schede=" + schede.size());
