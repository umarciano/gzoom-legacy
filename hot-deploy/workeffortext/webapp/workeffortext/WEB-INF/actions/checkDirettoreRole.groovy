import org.ofbiz.base.util.*;

// (1) Flag di ruolo direttore, usati dalla form WorkEffortRootViewManagementForm per mostrare i BOTTONI
//     di validazione al posto del dropdown stato (vedi doc 10).
//     Punto unico: checkBSDirettoreUo calcola sia l'appartenenza ai gruppi direttore sia la
//     responsabilita' ORG_RESPONSIBLE sulla UO di QUESTA scheda (isDirettoreUoWe), che e' il vero
//     criterio di "Direttore di UO" dopo l'introduzione della gerarchia dei profili.
//     Il gate della validazione parziale e' isDirettore + isResponsabileWe (vedi punto 4), che
//     insieme equivalgono a bsIsDirettoreUoWe.
GroovyUtil.runScriptAtLocation("com/mapsengineering/stratperf/checkBSDirettoreUo.groovy", context);
context.isDirettore = context.bsIsDirettore;
context.isDirSanAmm = context.bsIsDirSanAmm;

// Schermata corrente: Definizione (azioni consentite) vs Interrogazione (sola lettura -> niente
// bottoni di workflow; le LABEL data restano visibili in entrambe). Discriminante standard dei
// moduli perf: parameters.rootInqyTree='Y' in Interrogazione (vedi cdgperf).
context.isDefinizioneScreen = !"Y".equals(parameters.rootInqyTree);

// (2) Date di validazione, lette nativamente dallo storico WorkEffortStatus (opzione A). Visibili a TUTTI
//     (admin incluso): "Validata parzialmente il ..." (VALPART) e "Validata il ..." (VALIDATED).
context.dataValidazioneParzialeStr = null;
context.dataValidazioneCompletaStr = null;
context.dataVisioneStr = null;
// Ruolo del firmatario (VALIDATED / REVIEWED): dopo la separazione dei direttori di dipartimento
// la firma deve riportare SOLO il ruolo di chi ha firmato. Fonte = gruppo di sicurezza del
// created_by_user_login del record di stato; fallback generico se il firmatario non e' chiaramente
// Amm/San (es. admin per conto, ne'/entrambi i gruppi).
context.firmaValidazioneRuolo = "Direttore Sanitario/Amministrativo";
context.firmaVisioneRuolo = "Direttore Sanitario/Amministrativo";
String weId = context.workEffortId ?: parameters.workEffortId;
if (weId) {
    def latestRecord = { statusId ->
        def rows = delegator.findByAnd("WorkEffortStatus", UtilMisc.toMap("workEffortId", weId, "statusId", statusId));
        def latest = null;
        if (rows) {
            for (r in rows) {
                def d = r.getTimestamp("statusDatetime");
                if (d != null && (latest == null || d.after(latest.getTimestamp("statusDatetime")))) { latest = r; }
            }
        }
        return latest;
    };
    // Etichetta ruolo dal login del firmatario: STRATPERF_DIR_AMM -> "Direttore Amministrativo",
    // STRATPERF_DIR_SAN -> "Direttore Sanitario"; entrambi/nessuno/null -> generico.
    def ruoloFirmatario = { login ->
        if (!login) { return "Direttore Sanitario/Amministrativo"; }
        def grps = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", login));
        boolean amm = false; boolean san = false;
        def nowTs = UtilDateTime.nowTimestamp();
        if (grps) {
            for (g in grps) {
                def thru = g.getTimestamp("thruDate");
                if (thru != null && thru.before(nowTs)) { continue; }
                if ("STRATPERF_DIR_AMM".equals(g.getString("groupId"))) { amm = true; }
                if ("STRATPERF_DIR_SAN".equals(g.getString("groupId"))) { san = true; }
            }
        }
        if (amm && !san) { return "Direttore Amministrativo"; }
        if (san && !amm) { return "Direttore Sanitario"; }
        return "Direttore Sanitario/Amministrativo";
    };
    def rParz = latestRecord("WEORCARD_VALPART");
    def rComp = latestRecord("WEORCARD_VALIDATED");
    def rVis = latestRecord("WEORCARD_REVIEWED");
    def dParz = rParz?.getTimestamp("statusDatetime");
    def dComp = rComp?.getTimestamp("statusDatetime");
    def dVis = rVis?.getTimestamp("statusDatetime");
    if (dParz != null) { context.dataValidazioneParzialeStr = UtilDateTime.toDateString(dParz, "dd/MM/yyyy HH:mm"); }
    if (dComp != null) {
        context.dataValidazioneCompletaStr = UtilDateTime.toDateString(dComp, "dd/MM/yyyy HH:mm");
        context.firmaValidazioneRuolo = ruoloFirmatario(rComp.getString("createdByUserLogin"));
    }
    if (dVis != null) {
        context.dataVisioneStr = UtilDateTime.toDateString(dVis, "dd/MM/yyyy HH:mm");
        context.firmaVisioneRuolo = ruoloFirmatario(rVis.getString("createdByUserLogin"));
    }
}

// (3) Stato REALE della scheda, per il gating dei BOTTONI di validazione. Va letto dall'entita'
//     WorkEffort (NON da context.currentStatusId, che in Definizione resta il valore del FILTRO di
//     ricerca "WEORCARD_TOVALIDATE" e faceva ricomparire il bottone anche dopo la validazione, cioe'
//     su schede non piu' in quello stato). Vedi doc 10 §4bis.
context.weCurrentStatusIdReal = null;
// (4) L'utente e' RESPONSABILE (ORG_RESPONSIBLE) dell'org unit di QUESTA scheda? Serve a mostrare la
//     "Valida" SOLO sulle schede che il direttore effettivamente dirige. Calcolato da
//     checkBSDirettoreUo (punto unico). NB: va SEMPRE combinato con isDirettore, perche'
//     ORG_RESPONSIBLE include anche i referenti (che NON devono validare parzialmente).
context.isResponsabileWe = context.bsIsResponsabileWe;
// (5) Scheda "pregresso" = le schede 2025 (periodo di esercizio chiuso): anno di estimatedCompletionDate
//     <= 2025. Regola INCHIODATA al 2025 (scelta cliente): queste schede NON devono ricevere le AZIONI
//     nuove del workflow (bottoni di validazione). NB: il "punteggio manuale" NON e' gato qui perche' e'
//     una funzione richiesta dal cliente gia' dal 2025; la Consuntivazione non e' interessata (nel 2025
//     non c'erano referenti/indicatori). Le schede 2026+ restano attive.
context.isSchedaPregresso2025 = false;
if (weId) {
    def weRec = delegator.findOne("WorkEffort", UtilMisc.toMap("workEffortId", weId), false);
    if (weRec != null) {
        context.weCurrentStatusIdReal = weRec.getString("currentStatusId");
        def compDate = weRec.getTimestamp("estimatedCompletionDate");
        if (compDate != null) {
            Calendar cWe = Calendar.getInstance(); cWe.setTimeInMillis(compDate.getTime());
            context.isSchedaPregresso2025 = (cWe.get(Calendar.YEAR) <= 2025);
        }
    }
}
