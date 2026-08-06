import org.ofbiz.base.util.*;

// (1) Flag di ruolo direttore, usati dalla form WorkEffortRootViewManagementForm per mostrare i BOTTONI
//     di validazione al posto del dropdown stato (vedi doc 10):
//   isDirUO      -> gruppo ORGPERF_DIR_UO  (validazione parziale: TO_VALIDATE -> VALPART)
//   isDirSanAmm  -> gruppo ORGPERF_DIR_SAN o ORGPERF_DIR_AMM (validazione completa: VALPART -> VALIDATED)
context.isDirUO = false;
context.isDirSanAmm = false;
if (userLogin?.getString("userLoginId")) {
    def grps = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId")));
    if (grps) {
        for (g in grps) {
            String gid = g.getString("groupId");
            if ("ORGPERF_DIR_UO".equals(gid)) { context.isDirUO = true; }
            if ("ORGPERF_DIR_SAN".equals(gid) || "ORGPERF_DIR_AMM".equals(gid)) { context.isDirSanAmm = true; }
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
