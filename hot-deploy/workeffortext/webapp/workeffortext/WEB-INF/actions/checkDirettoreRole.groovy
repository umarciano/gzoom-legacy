import org.ofbiz.base.util.*;

// (1) Flag di ruolo direttore, usati dalla form WorkEffortRootViewManagementForm per mostrare i BOTTONI
//     di validazione al posto del dropdown stato (vedi doc 10):
//   isDirUO      -> gruppo STRATPERF_DIR_UO  (validazione parziale: TO_VALIDATE -> VALPART)
//   isDirSanAmm  -> gruppo STRATPERF_DIR_SAN o STRATPERF_DIR_AMM (validazione completa: VALPART -> VALIDATED)
context.isDirUO = false;
context.isDirSanAmm = false;
if (userLogin?.getString("userLoginId")) {
    def grps = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId")));
    if (grps) {
        for (g in grps) {
            String gid = g.getString("groupId");
            if ("STRATPERF_DIR_UO".equals(gid)) { context.isDirUO = true; }
            if ("STRATPERF_DIR_SAN".equals(gid) || "STRATPERF_DIR_AMM".equals(gid)) { context.isDirSanAmm = true; }
        }
    }
}
context.isDirettore = context.isDirUO || context.isDirSanAmm;

// (2) Date di validazione, lette nativamente dallo storico WorkEffortStatus (opzione A). Visibili a TUTTI
//     (admin incluso): "Validata parzialmente il ..." (VALPART) e "Validata il ..." (VALIDATED).
context.dataValidazioneParzialeStr = null;
context.dataValidazioneCompletaStr = null;
String weId = context.workEffortId ?: parameters.workEffortId;
if (weId) {
    def latestDate = { statusId ->
        def rows = delegator.findByAnd("WorkEffortStatus", UtilMisc.toMap("workEffortId", weId, "statusId", statusId));
        def latest = null;
        if (rows) {
            for (r in rows) {
                def d = r.getTimestamp("statusDatetime");
                if (d != null && (latest == null || d.after(latest))) { latest = d; }
            }
        }
        return latest;
    };
    def dParz = latestDate("WEORCARD_VALPART");
    def dComp = latestDate("WEORCARD_VALIDATED");
    if (dParz != null) { context.dataValidazioneParzialeStr = UtilDateTime.toDateString(dParz, "dd/MM/yyyy HH:mm"); }
    if (dComp != null) { context.dataValidazioneCompletaStr = UtilDateTime.toDateString(dComp, "dd/MM/yyyy HH:mm"); }
}

// (3) Stato REALE della scheda, per il gating dei BOTTONI di validazione. Va letto dall'entita'
//     WorkEffort (NON da context.currentStatusId, che in Definizione resta il valore del FILTRO di
//     ricerca "WEORCARD_TOVALIDATE" e faceva ricomparire il bottone anche dopo la validazione, cioe'
//     su schede non piu' in quello stato). Vedi doc 10 §4bis.
context.weCurrentStatusIdReal = null;
// (4) L'utente e' RESPONSABILE (ORG_RESPONSIBLE) dell'org unit di QUESTA scheda? Serve a mostrare la
//     "Valida parzialmente" SOLO sulle schede che il direttore effettivamente dirige (la sua UOC per il
//     Dir UO; le proprie strutture direzionali per Dir san/amm), mentre la firma "completa" resta di
//     competenza dei Dir san/amm su qualsiasi scheda. NB: va SEMPRE combinato con isDirettore, perche'
//     ORG_RESPONSIBLE include anche i referenti (che NON devono validare parzialmente).
context.isResponsabileWe = false;
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
        String weOrgUnitId = weRec.getString("orgUnitId");
        String myPartyId = userLogin?.getString("partyId");
        if (UtilValidate.isNotEmpty(weOrgUnitId) && UtilValidate.isNotEmpty(myPartyId)) {
            def rels = delegator.findByAnd("PartyRelationship", UtilMisc.toMap(
                "partyIdFrom", weOrgUnitId,
                "partyIdTo", myPartyId,
                "partyRelationshipTypeId", "ORG_RESPONSIBLE"));
            def nowTs = UtilDateTime.nowTimestamp();
            if (rels) {
                for (r in rels) {
                    def thru = r.getTimestamp("thruDate");
                    if (thru == null || thru.after(nowTs)) { context.isResponsabileWe = true; break; }
                }
            }
        }
    }
}
