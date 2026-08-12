/*
 * Carica i dati anagrafici dell'indicatore per la card "Cardarelli" (Performance Strategica).
 * Formato: Area(Natura), Codice, Descrizione sintetica, Indicatore, Formula,
 *          Valore atteso/soglie, Range/fasce, Fonte dati, Referente, Peso in 60esimi.
 *
 * Context in ingresso: workEffortMeasureSecondary (WorkEffortMeasure) gia' caricato dallo screen.
 * Espone in context: glAccount, glResourceType, referentePartyName, fasceList, soglieList.
 */
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def wem = context.workEffortMeasureSecondary;
if (UtilValidate.isEmpty(wem)) {
    return;
}

def currentUserLoginId = userLogin?.getString("userLoginId");
def userGroups = currentUserLoginId ? delegator.findByAnd("UserLoginSecurityGroup", [userLoginId: currentUserLoginId])*.getString("groupId") : [];
def canEditIndicatorComment = "admin" == currentUserLoginId || userGroups.contains("STRATPERF_DIR_SAN") || userGroups.contains("STRATPERF_DIR_AMM");
def measureWorkEffort = delegator.findOne("WorkEffort", [workEffortId: wem.workEffortId], false);
context.canEditIndicatorComment = canEditIndicatorComment && measureWorkEffort?.workEffortTypeId == "CTX_BS";

// --- GlAccount (anagrafica indicatore) ---
def glAccount = wem.getRelatedOne("GlAccount");
context.glAccount = glAccount;
if (UtilValidate.isEmpty(glAccount)) {
    return;
}

// --- Area (Natura) ---
if (UtilValidate.isNotEmpty(glAccount.glResourceTypeId)) {
    context.glResourceType = delegator.findOne("GlResourceType",
        UtilMisc.toMap("glResourceTypeId", glAccount.glResourceTypeId), false);
}

// --- Referente indicatore (ruolo WEM_IND_IN_CHARGE) ---
def referenteRoles = delegator.findList("GlAccountRoleAndParty",
    EntityCondition.makeCondition([
        EntityCondition.makeCondition("glAccountId", glAccount.glAccountId),
        EntityCondition.makeCondition("roleTypeId", "WEM_IND_IN_CHARGE")]),
    null, null, null, false);
referenteRoles = EntityUtil.filterByDate(referenteRoles);
def referente = EntityUtil.getFirst(referenteRoles);
if (UtilValidate.isNotEmpty(referente)) {
    context.referentePartyName = referente.partyName;
}

// --- Formula "parlante": composta dalle etichette dei parametri della modale di consuntivo ---
// (gl_account_input_calc + gl_fiscal_type PAR_*, caricati da POST_IMPORT_PARAMETRI_INDICATORI.sql).
// Es. "slot saturati / slot disponibili". Se non ci sono parametri: SI_NO -> "Si/No",
// altrimenti fallback al codice calc_custom_method_id.
def inputs = delegator.findList("GlAccountInputCalc",
    EntityCondition.makeCondition("glAccountId", glAccount.glAccountId),
    null, ["inputSequenceNum"], null, false);
def formulaParts = [];
if (UtilValidate.isNotEmpty(inputs)) {
    inputs.each { ic ->
        def ft = delegator.findOne("GlFiscalType", UtilMisc.toMap("glFiscalTypeId", ic.glFiscalTypeId), true);
        if (ft != null && UtilValidate.isNotEmpty(ft.description)) { formulaParts.add(ft.description); }
    }
}
if (formulaParts) {
    context.formulaParlante = formulaParts.join(" / ");
} else if ("SI_NO".equals(glAccount.calcCustomMethodId)) {
    context.formulaParlante = "Si/No";
} else {
    context.formulaParlante = glAccount.calcCustomMethodId;
}

// --- Range / fasce: le 4 bande reali dell'indicatore ---
// La scala (RNG_<codice>, seed POST_IMPORT_FASCE_INDICATORI.sql) sta sulla MISURA
// (work_effort_measure.uom_range_id), non su gl_account.
def uomRangeId = wem.uomRangeId ?: glAccount.uomRangeId;
// Scala REALE solo se per-indicatore (RNG_<codice>, seed POST_IMPORT_FASCE_INDICATORI.sql).
// La scala generica di default PERF_4FASCE (o nessuna scala) NON e' significativa per l'indicatore
// (bande numeriche assurde tipo -1/0/100/200): NON va mostrata come fasce/target. Cosi' per gli
// indicatori senza scala reale (inclusi i SI_NO) la card mostra "n/d" / "nessuna scala" invece
// di numeri a caso. Il punteggio verra' comunque inserito a mano nella modale di consuntivo.
boolean hasRealRange = UtilValidate.isNotEmpty(uomRangeId) && !"PERF_4FASCE".equals(uomRangeId);
if (hasRealRange) {
    context.fasceList = delegator.findList("UomRangeValues",
        EntityCondition.makeCondition("uomRangeId", uomRangeId),
        null, ["fromValue"], null, false);
}

// --- Target (valore singolo) = confine della banda 100% ---
// higher-better: fromValue della banda 100%; lower-better: thruValue.
if (context.fasceList) {
    def b100 = context.fasceList.find { it.rangeValuesFactor != null && (it.rangeValuesFactor as Double) == 100.0d };
    if (b100 != null) {
        def fromV = (b100.fromValue != null) ? (b100.fromValue as Double) : null;
        def thruV = (b100.thruValue != null) ? (b100.thruValue as Double) : null;
        context.targetValue = (fromV != null && fromV > -900000d) ? fromV : thruV;
    }
}
