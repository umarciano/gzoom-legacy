import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***BS parameters.menuItem" + parameters.menuItem);
context.permission = "BSCPERF";
parameters.weContextId = "CTX_BS";

// INTERROGAZIONE (consultazione, TUTTI gli stati - nessun filtro di stato).
// Scoping: il direttore UO vede SOLO le proprie UO (in qualsiasi stato); il direttore
// sanitario/amministrativo e l'admin vedono TUTTE le schede. La UO del direttore si ricava
// dalla relazione nativa DIRETTORE_UOC/ORG_RESPONSIBLE e si inietta come lista CSV in orgUnitId
// (il template queryWorkEffortRootInqyPartySummary.sql.ftl genera "A.ORG_UNIT_ID IN (...)").
// Vedi doc 10.
String userLoginId = userLogin?.getString("userLoginId");
boolean isDirUO = false;
boolean isDirSanAmm = false;
boolean isDirDip = false;
boolean isDirSan = false;
if (userLoginId) {
	def groups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
	groups = EntityUtil.filterByDate(groups);
	isDirUO = groups?.any { it.getString("groupId") == "STRATPERF_DIR_UO" };
	isDirSanAmm = groups?.any { it.getString("groupId") in ["STRATPERF_DIR_SAN", "STRATPERF_DIR_AMM"] };
	isDirDip = groups?.any { it.getString("groupId") == "STRATPERF_DIR_DIP" };
	isDirSan = groups?.any { it.getString("groupId") == "STRATPERF_DIR_SAN" };
}
// Gerarchia profili: DIR_SAN/AMM > DIR_DIP > DIR_UO.
// GROUP_ROLLUP a Cardarelli: partyIdFrom=GENITORE, partyIdTo=FIGLIO.
// Tutti i profili direttore usano lo scoping CSV orgUnitId injection nel template.
if (isDirUO && !isDirSanAmm && !isDirDip) {
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
	parameters.orgUnitId = orgUnits.isEmpty() ? "__NONE__" : orgUnits.join(",");
	Debug.log("### DIR_UO Interrogazione scoping " + userLoginId + " orgUnitId=" + parameters.orgUnitId);
} else if (isDirSanAmm) {
	// Feature 2: scoping perimetro SAN/AMM via PartyClassification.
	// GROUP_ROLLUP: partyIdFrom=GENITORE(Dept), partyIdTo=FIGLIO(UO).
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
	Debug.log("### DIR_" + (isDirSan ? "SAN" : "AMM") + " Interrogazione scoping " + userLoginId + " orgUnits=" + orgUnits);
	parameters.orgUnitId = orgUnits.isEmpty() ? "__NONE__" : orgUnits.join(",");
} else if (isDirDip) {
	// Feature 1: DIR_DIP - scoping UO dei propri dipartimenti.
	// GROUP_ROLLUP: partyIdFrom=GENITORE(Dept), partyIdTo=FIGLIO(UO).
	def nowTs2 = UtilDateTime.nowTimestamp();
	def myDeptRels = delegator.findByAnd("PartyRelationship", UtilMisc.toMap(
		"partyIdTo", userLogin.getString("partyId"),
		"partyRelationshipTypeId", "ORG_RESPONSIBLE"));
	def orgUnits = new LinkedHashSet();
	for (dr in myDeptRels) {
		def dthru = dr.getTimestamp("thruDate");
		if (dthru == null || dthru.after(nowTs2)) {
			String deptId = dr.getString("partyIdFrom");
			def uoRels = delegator.findByAnd("PartyRelationship", UtilMisc.toMap(
				"partyIdFrom", deptId, "partyRelationshipTypeId", "GROUP_ROLLUP"));
			for (r in uoRels) {
				def thru = r.getTimestamp("thruDate");
				if (thru == null || thru.after(nowTs2)) { orgUnits.add(r.getString("partyIdTo")); }
			}
		}
	}
	parameters.orgUnitId = orgUnits.isEmpty() ? "__NONE__" : orgUnits.join(",");
	Debug.log("### DIR_DIP Interrogazione scoping " + userLoginId + " orgUnitId=" + parameters.orgUnitId);
}

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRootInqy.groovy", context);
return res;
