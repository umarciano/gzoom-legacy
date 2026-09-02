import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ServiceUtil

/**
 * deriveReferenteGroupOnIndInCharge
 *
 * Derivazione AUTOMATICA della membership STRATPERF_REFERENTE quando si assegna un referente a un
 * indicatore, cioe' quando viene creato un GlAccountRole con roleTypeId='WEM_IND_IN_CHARGE'.
 * Invocato da un EECA su GlAccountRole/create (condizione sul ruolo).
 *
 * MODELLO PERSONA (2026-09-02): il referente e' una SINGOLA PERSONA (party del ruolo = persona),
 * impostata da import. Quindi qui il party del GlAccountRole E' gia' la persona: si trova
 * direttamente il suo UserLogin e lo si aggiunge a STRATPERF_REFERENTE (niente piu' salto
 * UOC -> ORG_RESPONSIBLE). Il gruppo concede CONSUNT_CTX_BS_VIEW che abilita il menu
 * "Consuntivazione indicatori".
 *
 * Additivo e idempotente. Best-effort: tutto in try/catch -> NON deve MAI far fallire l'assegnazione.
 * NB: i permessi sono cache-ati al login -> il referente vede il menu al PROSSIMO accesso.
 * NB Groovy/OFBiz: solo findList+EntityCondition e set() espliciti (niente toMap / findByAnd con null).
 *
 * IN: partyId (= la persona referente), roleTypeId (dai campi del GlAccountRole appena creato).
 */

String MODULE = "deriveReferenteGroupOnIndInCharge"
try {
    String roleTypeId = parameters.roleTypeId
    String personId = parameters.partyId
    if (!"WEM_IND_IN_CHARGE".equals(roleTypeId) || !UtilValidate.isNotEmpty(personId)) return ServiceUtil.returnSuccess()

    def now = UtilDateTime.nowTimestamp()

    // Il party del ruolo E' la persona referente: trova il/i suo/i UserLogin.
    def logins = delegator.findList("UserLogin",
        EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, personId), null, null, null, false)
    for (ul in logins) {
        String ulId = ul.getString("userLoginId")
        def existing = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition([
            EntityCondition.makeCondition("userLoginId", EntityOperator.EQUALS, ulId),
            EntityCondition.makeCondition("groupId", EntityOperator.EQUALS, "STRATPERF_REFERENTE"),
            EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null)
        ], EntityOperator.AND), null, null, null, false)
        if (UtilValidate.isNotEmpty(existing)) continue

        def gv = delegator.makeValue("UserLoginSecurityGroup")
        gv.set("userLoginId", ulId)
        gv.set("groupId", "STRATPERF_REFERENTE")
        gv.set("fromDate", now)
        delegator.create(gv)
        Debug.log("[REFERENTE-AUTO] " + ulId + " -> STRATPERF_REFERENTE (persona referente " + personId + ")", MODULE)
    }
} catch (Exception e) {
    // MAI far fallire l'assegnazione del referente per colpa della derivazione della membership.
    Debug.logWarning("[REFERENTE-AUTO] eccezione (ignorata): " + e.message, MODULE)
}
return ServiceUtil.returnSuccess()
