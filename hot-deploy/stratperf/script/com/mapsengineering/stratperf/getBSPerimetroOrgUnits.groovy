import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

/**
 * getBSPerimetroOrgUnits - PUNTO UNICO del perimetro di VISIBILITA' CTX_BS.
 *
 * Risponde a "quali UO puo' vedere questo utente": consumato sia dallo scoping della LISTA
 * (applyScopingBSFilter) sia dalla guardia anti-IDOR del DETTAGLIO (checkBSDetailAccess),
 * cosi' l'invariante "apri esattamente le schede che vedi" e' garantita per costruzione.
 *
 * NON risponde a "sono il direttore di QUESTA UO" (capability): per quello vedi checkBSDirettoreUo.
 *
 * Perimetro per profilo (2026):
 *   ADMIN   -> nessun limite (lista null)
 *   SANAMM  -> UO dei dipartimenti classificati DIP_SANITARIO / DIP_AMMINISTRATIVO  U  UO proprie
 *   DIP     -> UO figlie (1 hop GROUP_ROLLUP) dei dipartimenti di cui e' ORG_RESPONSIBLE  U  UO proprie
 *   UO      -> UO di cui e' ORG_RESPONSIBLE
 *   NONE    -> UO di cui e' ORG_RESPONSIBLE (nessun gruppo direttore)
 *
 * "UO proprie" = UO di cui l'utente e' direttamente ORG_RESPONSIBLE. E' in unione su TUTTI i rami
 * perche' un direttore puo' essere responsabile di una UO fuori dal perimetro derivato dal gruppo
 * (caso reale in AORN): senza l'unione non vedrebbe la scheda della UO che dirige.
 *
 * GROUP_ROLLUP a Cardarelli: partyIdFrom=GENITORE(Dept), partyIdTo=FIGLIO(UO).
 * ORG_RESPONSIBLE:           partyIdFrom=org/dept,       partyIdTo=persona.
 *
 * Output in context:
 *   bsPerimetroKind      String  ADMIN | SANAMM | DIP | UO | NONE
 *   bsPerimetroOrgUnits  List    orgUnitId visibili; null se ADMIN (nessun limite)
 *
 * NB motore groovy: solo findOne/findList + EntityCondition (gira anche in contesti dove
 * findByAnd con argomenti null non e' ammesso).
 */

context.bsPerimetroKind = "NONE";
context.bsPerimetroOrgUnits = [];

String userLoginId = userLogin?.getString("userLoginId");
if (UtilValidate.isEmpty(userLoginId)) return;

def groups = EntityUtil.filterByDate(delegator.findList("UserLoginSecurityGroup",
        EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, userLoginId),
        null, null, null, false));
def gids = groups.collect { it.getString("groupId") };

def sec = context.get("security");
boolean isAdmin = gids.contains("AORNADMIN") || (sec != null && sec.hasPermission("BSCPERFMGR_ADMIN", userLogin));
if (isAdmin) {
    context.bsPerimetroKind = "ADMIN";
    context.bsPerimetroOrgUnits = null;
    return;
}

def nowTs = UtilDateTime.nowTimestamp();
String myPartyId = userLogin.getString("partyId");

// Org di cui l'utente e' ORG_RESPONSIBLE: contiene sia i dipartimenti (per il ramo DIP)
// sia le UO dirette (unite al perimetro di ogni ramo).
def mieOrg = new LinkedHashSet();
if (UtilValidate.isNotEmpty(myPartyId)) {
    def rels = delegator.findList("PartyRelationship", EntityCondition.makeCondition([
            EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, myPartyId),
            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "ORG_RESPONSIBLE")
    ], EntityOperator.AND), null, null, null, false);
    for (r in rels) {
        def thru = r.getTimestamp("thruDate");
        if (thru == null || thru.after(nowTs)) {
            String ouId = r.getString("partyIdFrom");
            if (UtilValidate.isNotEmpty(ouId)) { mieOrg.add(ouId); }
        }
    }
}

def figlieDi = { String deptId ->
    def out = new LinkedHashSet();
    def uoRels = delegator.findList("PartyRelationship", EntityCondition.makeCondition([
            EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, deptId),
            EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP")
    ], EntityOperator.AND), null, null, null, false);
    for (r in uoRels) {
        def thru = r.getTimestamp("thruDate");
        if (thru == null || thru.after(nowTs)) { out.add(r.getString("partyIdTo")); }
    }
    return out;
};

boolean isDirSan = gids.contains("STRATPERF_DIR_SAN");
boolean isDirAmm = gids.contains("STRATPERF_DIR_AMM");
boolean isDirDip = gids.contains("STRATPERF_DIR_DIP");
boolean isDirUO  = gids.contains("STRATPERF_DIR_UO");

def perimetro = new LinkedHashSet();
String kind = "NONE";

if (isDirSan || isDirAmm) {
    kind = "SANAMM";
    String targetGroup = isDirSan ? "DIP_SANITARIO" : "DIP_AMMINISTRATIVO";
    def classifs = delegator.findList("PartyClassification",
            EntityCondition.makeCondition("partyClassificationGroupId", EntityOperator.EQUALS, targetGroup),
            null, null, null, false);
    for (cl in classifs) {
        def t = cl.getTimestamp("thruDate");
        if (t != null && !t.after(nowTs)) { continue; }
        perimetro.addAll(figlieDi(cl.getString("partyId")));
    }
} else if (isDirDip) {
    kind = "DIP";
    for (deptId in mieOrg) { perimetro.addAll(figlieDi(deptId)); }
} else if (isDirUO) {
    kind = "UO";
}

perimetro.addAll(mieOrg);

context.bsPerimetroKind = kind;
context.bsPerimetroOrgUnits = new ArrayList(perimetro);
Debug.log("### [getBSPerimetroOrgUnits] " + userLoginId + " kind=" + kind + " orgUnits=" + context.bsPerimetroOrgUnits);
