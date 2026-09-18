import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

context.permission = "BSCPERF";
parameters.weContextId = "CTX_BS";

boolean isValutazione = "WorkEffortRootExecViewSearchFormScreen".equals(parameters.searchFormScreenName);

// Stati visibili — uguali per tutti i ruoli; l'unica differenza tra ruoli è l'orgUnitId (scoping UO).
// Valutazione: ciclo consuntivazione (TOACC_INT..REVIEWED).
// Definizione: stati pre-consuntivazione (INIT..VALIDATED).
String allStati;
if (isValutazione) {
	allStati = "WEORCARD_TOACC_INT,WEORCARD_ACC_INT,WEORCARD_TOACCOUNT,WEORCARD_ACCOUNTED,WEORCARD_REVIEWED";
} else {
	allStati = "WEORCARD_INIT,WEORCARD_TOVALIDATE,WEORCARD_TOCLRFY_DUO,WEORCARD_VALPART,WEORCARD_TOCLRFY_DSA,WEORCARD_VALIDATED";
}
if (UtilValidate.isEmpty(parameters.currentStatusId)) {
	parameters.currentStatusId_op = "contains";
	parameters.currentStatusId_value = allStati;
	parameters.currentStatusContains = allStati;
	parameters.remove("currentStatusId");
}

// Scoping orgUnitId per ruolo.
// GROUP_ROLLUP a Cardarelli: partyIdFrom=GENITORE, partyIdTo=FIGLIO (opposto allo standard OFBiz).
// ORG_RESPONSIBLE: partyIdFrom=org/dept, partyIdTo=persona.
String userLoginId = userLogin?.getString("userLoginId");
boolean isDirUO = false;
boolean isDirSanAmm = false;
boolean isDirDip = false;
boolean isDirSan = false;
if (userLoginId) {
	def groups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
	isDirUO    = groups?.any { it.getString("groupId") == "STRATPERF_DIR_UO" };
	isDirSanAmm = groups?.any { it.getString("groupId") in ["STRATPERF_DIR_SAN", "STRATPERF_DIR_AMM"] };
	isDirDip   = groups?.any { it.getString("groupId") == "STRATPERF_DIR_DIP" };
	isDirSan   = groups?.any { it.getString("groupId") == "STRATPERF_DIR_SAN" };
}

if (isDirUO && !isDirSanAmm && !isDirDip) {
	// Le UO di cui l'utente è ORG_RESPONSIBLE diretto.
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
	parameters.orgUnitId = orgUnits.isEmpty() ? "__NONE__" : orgUnits.join(",");

} else if (isDirSanAmm) {
	// Le UO dei dipartimenti DIP_SANITARIO (DIR_SAN) o DIP_AMMINISTRATIVO (DIR_AMM).
	String targetGroup = isDirSan ? "DIP_SANITARIO" : "DIP_AMMINISTRATIVO";
	def nowTs = UtilDateTime.nowTimestamp();
	def classifs = delegator.findByAnd("PartyClassification", UtilMisc.toMap("partyClassificationGroupId", targetGroup));
	def deptIds = classifs?.findAll { cl ->
		def t = cl.getTimestamp("thruDate"); t == null || t.after(nowTs)
	}?.collect { it.getString("partyId") } ?: []
	def orgUnits = new LinkedHashSet();
	for (deptId in deptIds) {
		def uoRels = delegator.findByAnd("PartyRelationship", UtilMisc.toMap(
			"partyIdFrom", deptId, "partyRelationshipTypeId", "GROUP_ROLLUP"));
		for (r in uoRels) {
			def thru = r.getTimestamp("thruDate");
			if (thru == null || thru.after(nowTs)) { orgUnits.add(r.getString("partyIdTo")); }
		}
	}
	Debug.log("### DIR_" + (isDirSan ? "SAN" : "AMM") + " scoping " + userLoginId + " orgUnits=" + orgUnits);
	parameters.orgUnitId = orgUnits.isEmpty() ? "__NONE__" : orgUnits.join(",");

} else if (isDirDip) {
	// Le UO figlie dei dipartimenti di cui l'utente è ORG_RESPONSIBLE.
	def nowTs = UtilDateTime.nowTimestamp();
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
	Debug.log("### DIR_DIP scoping " + userLoginId + " orgUnits=" + orgUnits);
	parameters.orgUnitId = orgUnits.isEmpty() ? "__NONE__" : orgUnits.join(",");

}
// else: admin → nessun filtro orgUnitId (vede tutte le UO)

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRoot.groovy", context);
return res;
