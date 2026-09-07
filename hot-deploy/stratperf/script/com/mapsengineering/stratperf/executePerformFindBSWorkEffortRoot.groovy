import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***BS parameters.menuItem" + parameters.menuItem);
context.permission = "BSCPERF";
parameters.weContextId = "CTX_BS";

// Mappatura stato->menu x ruolo (confermata 2026-07-31, allineata al profilo STRATPERF_DIR_UO;
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
boolean isDirSanAmm = false;
if (userLoginId) {
	def groups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
	isDirUO = groups?.any { it.getString("groupId") == "STRATPERF_DIR_UO" };
	isDirSanAmm = groups?.any { it.getString("groupId") in ["STRATPERF_DIR_SAN", "STRATPERF_DIR_AMM"] };
}
// Il direttore sanitario/amministrativo vede TUTTE le schede (ramo "vedi tutto" sotto), anche se
// possiede ANCHE il profilo DIR_UO (assegnato in quanto ORG_RESPONSIBLE della propria UOC): NON va
// scopato per UO, altrimenti non vedrebbe le schede VALPART di altre UO da validare (bottone
// "Valida"). Solo il direttore "puro" di UO resta ristretto. Stesso trattamento gia' presente in
// Interrogazione (executePerformFindBSWorkEffortRootInqy.groovy). Vedi doc 10 §4ter.
if (isDirUO && !isDirSanAmm) {
	// direttore in DEFINIZIONE: TO_VALIDATE (valida parziale) + ACCOUNTED (presa visione della
	// consuntivazione -> REVIEWED). CSV => il template genera IN(...).
	// In Valutazione: ACCOUNTED + ACC_INT (stesso profilo di ACCOUNTED nel ciclo intermedio).
	// TOACC_INT non incluso: il direttore non vede nemmeno TOACCOUNT in Valutazione.
	String stato = isValutazione ? "WEORCARD_ACCOUNTED,WEORCARD_ACC_INT" : "WEORCARD_TOVALIDATE,WEORCARD_TOCLRFY_DUO,WEORCARD_ACCOUNTED";
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
	// admin: in Valutazione vede solo gli stati del ciclo di consuntivazione (sequenceId 05-10);
	// in Definizione vede tutti gli stati del workflow.
	// ATTENZIONE: il template queryWorkEffortRoot.sql.ftl usa currentStatusContains come
	// match ESATTO (A.CURRENT_STATUS_ID = ?) quando NON contiene virgole e non e' "_EXEC".
	// Passiamo la lista CSV => il template genera "AND A.CURRENT_STATUS_ID IN (...)".
	// Valutazione: stati ciclo consuntivazione (TOACC_INT..REVIEWED); CLOSED solo in Interrogazione.
	// Definizione: stati pre-consuntivazione (INIT..VALIDATED).
	String allStati;
	if (isValutazione) {
		allStati = "WEORCARD_TOACC_INT,WEORCARD_ACC_INT,WEORCARD_TOACCOUNT,WEORCARD_ACCOUNTED,WEORCARD_REVIEWED";
	} else {
		allStati = "WEORCARD_INIT,WEORCARD_TOVALIDATE,WEORCARD_TOCLRFY_DUO,WEORCARD_VALPART,WEORCARD_TOCLRFY_DSA,WEORCARD_VALIDATED";
	}
	parameters.currentStatusId_op = "contains";
	parameters.currentStatusId_value = allStati;
	parameters.currentStatusContains = allStati;
	parameters.remove("currentStatusId");
}

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRoot.groovy", context);
return res;