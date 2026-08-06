import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***BS parameters.menuItem" + parameters.menuItem);
context.permission = "BSCPERF";
parameters.weContextId = "CTX_BS";

// Mappatura stato->menu x ruolo (confermata 2026-07-31, allineata al profilo ORGPERF_DIR_UO;
// vedi memory project_perf_strategica_workflow):
//   Definizione (WorkEffortRootViewSearchFormScreen):
//       admin        -> INIT         (verifica e fa INIT->TO_VALIDATE)
//       direttore UO -> TO_VALIDATE  (VALIDA qui: "Valida parzialmente")
//   Valutazione (WorkEffortRootExecViewSearchFormScreen): fase presa-visione risultati, NON usata ora
//       admin/direttore -> ACCOUNTED
// Il filtro usa op "contains" (LIKE '%..%') => UN solo stato per volta.
// TODO: (a) scoping per UO del direttore via WorkEffortPartyAssignment (ora vede tutte le TO_VALIDATE);
//       (b) direttore in sola lettura + solo pulsante "Valida parzialmente".
boolean isValutazione = "WorkEffortRootExecViewSearchFormScreen".equals(parameters.searchFormScreenName);
String userLoginId = userLogin?.getString("userLoginId");
boolean isDirUO = false;
if (userLoginId) {
	def groups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
	isDirUO = groups?.any { it.getString("groupId") == "ORGPERF_DIR_UO" };
}
if (isDirUO) {
	// direttore: valida in DEFINIZIONE (TO_VALIDATE); Valutazione = presa visione (fase futura).
	String stato = isValutazione ? "WEORCARD_ACCOUNTED" : "WEORCARD_TOVALIDATE";
	parameters.currentStatusId_op = "contains";
	parameters.currentStatusId_value = stato;
	parameters.currentStatusContains = stato;
	parameters.remove("currentStatusId");

	// SCOPING per-UO: il direttore vede solo le schede delle UO che dirige. La/e UO si ricava/no
	// dalla relazione NATIVA persona->unita' DIRETTORE_UOC/ORG_RESPONSIBLE (import anagrafico),
	// che copre tutti i direttori ed e' multi-UOC. Iniettiamo parameters.orgUnitId come lista CSV
	// => il template queryWorkEffortRoot.sql.ftl genera "AND A.ORG_UNIT_ID IN (...)".
	// NB: non usiamo permessi *_ADMIN (che sbloccherebbero l'editing di form => romperebbero la
	// sola lettura del direttore); lo scoping resta interamente qui.
	def orgUnits = new LinkedHashSet();
	def rels = delegator.findByAnd("PartyRelationship", UtilMisc.toMap(
		"partyIdTo", userLogin.getString("partyId"),
		"roleTypeIdTo", "DIRETTORE_UOC",
		"partyRelationshipTypeId", "ORG_RESPONSIBLE"));
	def nowTs = UtilDateTime.nowTimestamp();
	for (r in rels) {
		def thru = r.getTimestamp("thruDate");
		if (thru == null || thru.after(nowTs)) {
			String ouId = r.getString("partyIdFrom");
			if (UtilValidate.isNotEmpty(ouId)) { orgUnits.add(ouId); }
		}
	}
	Debug.log("### DIR_UO scoping " + userLoginId + " orgUnits=" + orgUnits);
	if (UtilValidate.isNotEmpty(orgUnits)) {
		parameters.orgUnitId = orgUnits.join(",");
	} else {
		// direttore senza relazione DIRETTORE_UOC: nessuna UO propria => non deve vedere schede altrui.
		Debug.logWarning("DIR_UO " + userLoginId + " senza relazione DIRETTORE_UOC/ORG_RESPONSIBLE: "
			+ "nessuna UO, risultato vuoto.", "executePerformFindBSWorkEffortRoot");
		parameters.orgUnitId = "__NONE__";
	}
} else if (UtilValidate.isEmpty(parameters.currentStatusId)) {
	// admin: vede TUTTE le schede del workflow (qualsiasi stato WEORCARD_*).
	// ATTENZIONE: il template queryWorkEffortRoot.sql.ftl usa currentStatusContains come
	// match ESATTO (A.CURRENT_STATUS_ID = ?) quando NON contiene virgole e non e' "_EXEC".
	// Quindi "WEORCARD" (parziale) NON matcha nessuno stato reale (bug: Definizione vuota).
	// Passiamo invece la lista CSV di TUTTI gli stati del workflow => il template genera
	// "AND A.CURRENT_STATUS_ID IN (...)" e admin vede tutte le schede in qualsiasi stato.
	String allStati = "WEORCARD_INIT,WEORCARD_TOVALIDATE,WEORCARD_VALPART,WEORCARD_VALIDATED,WEORCARD_TOACCOUNT,WEORCARD_ACCOUNTED,WEORCARD_REVIEWED,WEORCARD_CLOSED";
	parameters.currentStatusId_op = "contains";
	parameters.currentStatusId_value = allStati;
	parameters.currentStatusContains = allStati;
	parameters.remove("currentStatusId");
}

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRoot.groovy", context);
return res;