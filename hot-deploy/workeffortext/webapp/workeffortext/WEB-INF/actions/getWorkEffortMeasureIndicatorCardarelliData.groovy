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
// higher-better: fromValue della banda 100% (target ">= X"); lower-better: thruValue ("<= X").
if (context.fasceList) {
    def b100 = context.fasceList.find { it.rangeValuesFactor != null && (it.rangeValuesFactor as Double) == 100.0d };
    if (b100 != null) {
        def fromV = (b100.fromValue != null) ? (b100.fromValue as Double) : null;
        def thruV = (b100.thruValue != null) ? (b100.thruValue as Double) : null;
        boolean useFrom = (fromV != null && fromV > -900000d);
        context.targetValue = useFrom ? fromV : thruV;
        // Verso della soglia: true => "&ge; target" (higher-better), false => "&le; target" (lower-better).
        context.targetHigherBetter = useFrom;
    }
}
// Percentuale: i metodi di calcolo che terminano in "*100" (A/B*100, (A-B)/B*100) esprimono una %,
// quindi il Target va mostrato con il simbolo "%". Rapporto (A/B), SUM(A), diretto e SI_NO: nessun "%".
context.targetIsPercent = ((glAccount.calcCustomMethodId ?: "").endsWith("*100"));

// --- descrFascia: stringa "Fascia" pronta per il display, stile Excel (come nel file Obiettivi) ---
// Le bande sono memorizzate "gapless": thru = soglia_successiva - 0.01 (=> X,99) e il ">X" stretto ha
// from = X + 0.01 (=> X,01). Qui ricostruiamo la stringa arrotondando SOLO questi artefatti (.99/.01)
// e preservando i decimali VERI (es. 0,60). Operatore: prima banda "≤ N" (lower-better) o "< N"
// (higher-better); ultima banda "> N" (from con .01 = stretto) o "≥ N"; bande centrali "A - B".
// Difensivo: in caso di errore lascio fasceList invariata e il FTL fa fallback al display precedente.
try {
    if (context.fasceList) {
        def pct = context.targetIsPercent ? "%" : "";
        boolean higher = (context.targetHigherBetter == true);
        def niceNum = { v ->
            if (v == null) return null;
            double d = v as double; double fl2 = Math.floor(d); double fr = d - fl2;
            return (fr > 0.98d || (fr > 0.0d && fr < 0.02d)) ? fl2 : d;   // .99/.01 => intero; decimale vero invariato
        };
        def numStr = { v ->
            if (v == null) return "";
            double d = v as double;
            return (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d).replace(".", ",");
        };
        // Anche il Target usa lo stesso arrotondamento (es. 180,99 -> 180; decimali veri preservati).
        if (context.targetValue != null) { context.targetValue = niceNum(context.targetValue); }
        def lst = context.fasceList; int nB = lst.size(); def enriched = [];
        for (int i = 0; i < nB; i++) {
            def b = lst[i];
            // PREFERITO: display gia' calcolato FEDELE all'Excel dal generatore (uom_range_values.comments).
            String descr = (b.comments != null && b.comments.toString().trim() != "") ? b.comments.toString() : null;
            if (descr == null) {
                // FALLBACK (range senza comments precalcolate): ricostruzione approssimata da from/thru.
                Double frm = (b.fromValue != null) ? (b.fromValue as Double) : null;
                Double thru = (b.thruValue != null) ? (b.thruValue as Double) : null;
                if (frm != null && frm <= -900000d) {
                    descr = (higher ? "< " : "<= ") + numStr(niceNum(thru)) + pct;
                } else if (thru != null && thru >= 900000d) {
                    boolean strict = (frm != null && (frm - Math.floor(frm as double)) > 0.0d && (frm - Math.floor(frm as double)) < 0.02d);
                    descr = (strict ? "> " : ">= ") + numStr(niceNum(frm)) + pct;
                } else {
                    descr = numStr(niceNum(frm)) + pct + " - " + numStr(niceNum(thru)) + pct;
                }
            }
            enriched.add([fromValue: b.fromValue, thruValue: b.thruValue, rangeValuesFactor: b.rangeValuesFactor, descrFascia: descr]);
        }
        context.fasceList = enriched;
    }
} catch (Exception eFascia) {
    org.ofbiz.base.util.Debug.logWarning("descrFascia non calcolata: " + eFascia.getMessage(), "getWorkEffortMeasureIndicatorCardarelliData");
}
