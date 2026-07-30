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

// --- Range / fasce: le 4 bande reali dell'indicatore ---
// La scala (RNG_<codice>, seed POST_IMPORT_FASCE_INDICATORI.sql) sta sulla MISURA
// (work_effort_measure.uom_range_id), non su gl_account.
def uomRangeId = wem.uomRangeId ?: glAccount.uomRangeId;
if (UtilValidate.isNotEmpty(uomRangeId)) {
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
