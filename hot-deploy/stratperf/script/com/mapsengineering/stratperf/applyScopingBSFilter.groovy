import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;

// Punto unico di scoping orgUnitId per il contesto CTX_BS (Performance Strategica).
// Imposta parameters.orgUnitId come CSV di partyId UOC visibili all'utente in base al suo ruolo.
// Se l'utente non rientra in nessun ruolo con scoping (admin, ecc.) non imposta nulla → vede tutto.
//
// Per aggiungere un nuovo ruolo: aggiungere qui il branch corrispondente e aggiornare questo commento.
//
// GROUP_ROLLUP a Cardarelli: partyIdFrom=GENITORE, partyIdTo=FIGLIO.
// ORG_RESPONSIBLE:           partyIdFrom=org/dept, partyIdTo=persona.

String userLoginId = userLogin?.getString("userLoginId");
if (!userLoginId) return;

def groups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
groups = EntityUtil.filterByDate(groups);
def groupIds = groups?.collect { it.getString("groupId") } ?: [];

boolean isDirUO     = groupIds.contains("STRATPERF_DIR_UO");
boolean isDirSanAmm = groupIds.any { it in ["STRATPERF_DIR_SAN", "STRATPERF_DIR_AMM"] };
boolean isDirDip    = groupIds.contains("STRATPERF_DIR_DIP");
boolean isDirSan    = groupIds.contains("STRATPERF_DIR_SAN");

def nowTs = UtilDateTime.nowTimestamp();

if (isDirUO && !isDirSanAmm && !isDirDip) {
	// DIR_UO: vede solo le UO di cui è ORG_RESPONSIBLE diretto.
	def orgUnits = new LinkedHashSet();
	def rels = delegator.findByAnd("PartyRelationship", UtilMisc.toMap(
		"partyIdTo", userLogin.getString("partyId"),
		"partyRelationshipTypeId", "ORG_RESPONSIBLE"));
	for (r in rels) {
		def thru = r.getTimestamp("thruDate");
		if (thru == null || thru.after(nowTs)) {
			String ouId = r.getString("partyIdFrom");
			if (UtilValidate.isNotEmpty(ouId)) { orgUnits.add(ouId); }
		}
	}
	Debug.log("### [applyScopingBSFilter] DIR_UO " + userLoginId + " orgUnits=" + orgUnits);
	parameters.orgUnitId = orgUnits.isEmpty() ? "__NONE__" : orgUnits.join(",");

} else if (isDirSanAmm) {
	// DIR_SAN: vede le UO dei dipartimenti classificati DIP_SANITARIO.
	// DIR_AMM: vede le UO dei dipartimenti classificati DIP_AMMINISTRATIVO.
	String targetGroup = isDirSan ? "DIP_SANITARIO" : "DIP_AMMINISTRATIVO";
	def classifs = delegator.findByAnd("PartyClassification", UtilMisc.toMap("partyClassificationGroupId", targetGroup));
	def deptIds = classifs?.findAll { cl ->
		def t = cl.getTimestamp("thruDate"); t == null || t.after(nowTs)
	}?.collect { it.getString("partyId") } ?: [];
	def orgUnits = new LinkedHashSet();
	for (deptId in deptIds) {
		def uoRels = delegator.findByAnd("PartyRelationship", UtilMisc.toMap(
			"partyIdFrom", deptId, "partyRelationshipTypeId", "GROUP_ROLLUP"));
		for (r in uoRels) {
			def thru = r.getTimestamp("thruDate");
			if (thru == null || thru.after(nowTs)) { orgUnits.add(r.getString("partyIdTo")); }
		}
	}
	Debug.log("### [applyScopingBSFilter] DIR_" + (isDirSan ? "SAN" : "AMM") + " " + userLoginId + " orgUnits=" + orgUnits);
	parameters.orgUnitId = orgUnits.isEmpty() ? "__NONE__" : orgUnits.join(",");

} else if (isDirDip) {
	// DIR_DIP: vede le UO figlie dei dipartimenti di cui è ORG_RESPONSIBLE.
	def myDeptRels = delegator.findByAnd("PartyRelationship", UtilMisc.toMap(
		"partyIdTo", userLogin.getString("partyId"),
		"partyRelationshipTypeId", "ORG_RESPONSIBLE"));
	def orgUnits = new LinkedHashSet();
	for (dr in myDeptRels) {
		def dthru = dr.getTimestamp("thruDate");
		if (dthru == null || dthru.after(nowTs)) {
			String deptId = dr.getString("partyIdFrom");
			def uoRels = delegator.findByAnd("PartyRelationship", UtilMisc.toMap(
				"partyIdFrom", deptId, "partyRelationshipTypeId", "GROUP_ROLLUP"));
			for (r in uoRels) {
				def thru = r.getTimestamp("thruDate");
				if (thru == null || thru.after(nowTs)) { orgUnits.add(r.getString("partyIdTo")); }
			}
		}
	}
	Debug.log("### [applyScopingBSFilter] DIR_DIP " + userLoginId + " orgUnits=" + orgUnits);
	parameters.orgUnitId = orgUnits.isEmpty() ? "__NONE__" : orgUnits.join(",");
}

// STRATPERF_REFERENTE: nessuno scoping applicato (ruolo senza accesso al menu CTX_BS al momento).
// Quando il ruolo verrà abilitato, aggiungere:
//   WorkEffortMeasure.partyId = userLogin.partyId AND workEffortTypeId = CTX_BS
//   → orgUnitId dei WorkEffort collegati.

// Admin e altri ruoli senza scoping: parameters.orgUnitId non viene impostato → vede tutte le UOC.
