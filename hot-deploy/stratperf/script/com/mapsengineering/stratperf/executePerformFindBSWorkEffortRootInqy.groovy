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
if (userLoginId) {
	def groups = delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
	groups = EntityUtil.filterByDate(groups);
	isDirUO = groups?.any { it.getString("groupId") == "STRATPERF_DIR_UO" };
	isDirSanAmm = groups?.any { it.getString("groupId") in ["STRATPERF_DIR_SAN", "STRATPERF_DIR_AMM"] };
}
// Il direttore sanitario/amministrativo vede TUTTE le schede: nessuna restrizione per UO, anche
// se possiede ANCHE il profilo DIR_UO (che gli viene assegnato in quanto ORG_RESPONSIBLE). Solo
// il direttore "puro" di UO resta ristretto alle proprie UO. Vedi doc 10 / doc 5.
if (isDirUO && !isDirSanAmm) {
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
}

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRootInqy.groovy", context);
return res;
