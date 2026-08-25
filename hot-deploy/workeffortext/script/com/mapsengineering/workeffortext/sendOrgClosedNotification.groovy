import com.mapsengineering.base.find.PartyEmailFindServices
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.service.ServiceUtil

/**
 * sendOrgClosedNotification
 *
 * Trigger di CHIUSURA della Performance Organizzativa (scheda CTX_BS -> WEORCARD_CLOSED).
 * Quando il risultato organizzativo diventa UFFICIALE, notifica via email i destinatari
 * delle schede individuali (CTX_EP) della stessa UO/anno.
 *
 * DESTINATARI (facilmente modificabili qui): oggi = VALUTATI (WEM_EVAL_IN_CHARGE) delle
 * individuali della UO. Per cambiare in valutatori usare 'WEM_EVAL_MANAGER' (o entrambi).
 *
 * Invocato da EECA su create di WorkEffortStatus con statusId=WEORCARD_CLOSED (vedi eecas.xml).
 *
 * IN:
 *   workEffortId - ID della scheda CTX_BS appena chiusa (dal record WorkEffortStatus)
 */

String MODULE = "sendOrgClosedNotification"
final String RULE_ID = "ORG_CLOSED"
// Ruolo destinatario: cambiare qui per notificare i valutatori invece dei valutati.
final String RECIPIENT_ROLE = "WEM_EVAL_IN_CHARGE"

String bsWorkEffortId = parameters.workEffortId
if (!UtilValidate.isNotEmpty(bsWorkEffortId)) {
    return ServiceUtil.returnSuccess()
}

// La scheda chiusa dev'essere una CTX_BS (lo statusId WEORCARD_CLOSED e' gia' specifico, ma ricontrolliamo).
def bs = delegator.findOne("WorkEffort", [workEffortId: bsWorkEffortId], false)
if (bs == null || !"CTX_BS".equals(bs.getString("workEffortTypeId"))) {
    return ServiceUtil.returnSuccess()
}

// Regola abilitata?
def emailRule = delegator.findOne("GzoomEmailRule", [ruleId: RULE_ID], false)
if (emailRule != null && "N" == emailRule.getString("enabled")) {
    Debug.log("[GZOOM-NOTIF] Regola ${RULE_ID} disabilitata, invio saltato", MODULE)
    return ServiceUtil.returnSuccess()
}

// Dedup: se abbiamo gia' notificato la chiusura di QUESTA scheda, non rifare (evita doppioni su ri-create).
def already = delegator.findList("GzoomEmailLog", EntityCondition.makeCondition([
    EntityCondition.makeCondition("ruleId", EntityOperator.EQUALS, RULE_ID),
    EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, bsWorkEffortId),
    EntityCondition.makeCondition("status", EntityOperator.EQUALS, "SENT")
], EntityOperator.AND), null, null, null, false)
if (UtilValidate.isNotEmpty(already)) {
    Debug.log("[GZOOM-NOTIF] ${RULE_ID} gia' inviata per ${bsWorkEffortId}, skip", MODULE)
    return ServiceUtil.returnSuccess()
}

String orgUnitId = bs.getString("orgUnitId")
if (!UtilValidate.isNotEmpty(orgUnitId)) {
    Debug.logWarning("[GZOOM-NOTIF] CTX_BS ${bsWorkEffortId} senza orgUnitId, skip", MODULE)
    return ServiceUtil.returnSuccess()
}
// Anno di riferimento = anno della data fine della scheda organizzativa.
def bsEnd = bs.getTimestamp("estimatedCompletionDate")
Integer bsYear = bsEnd != null ? (bsEnd.toLocalDateTime().getYear()) : null

// Individuali (CTX_EP) della stessa UO.
def indList = delegator.findList("WorkEffort", EntityCondition.makeCondition([
    EntityCondition.makeCondition("workEffortTypeId", EntityOperator.EQUALS, "CTX_EP"),
    EntityCondition.makeCondition("orgUnitId", EntityOperator.EQUALS, orgUnitId)
], EntityOperator.AND), null, null, null, false)

String sendFrom = UtilProperties.getPropertyValue("general.properties", "defaultFromEmailAddress", "gzoom@gzoom.it")
PartyEmailFindServices emailFinder = new PartyEmailFindServices(delegator)
def sentTo = new HashSet()
int sent = 0

for (ind in indList) {
    // Filtro anno: solo le individuali dell'anno della scheda organizzativa.
    if (bsYear != null) {
        def indEnd = ind.getTimestamp("estimatedCompletionDate")
        if (indEnd == null || indEnd.toLocalDateTime().getYear() != bsYear) continue
    }

    // Destinatario = party col ruolo configurato (valutato) su questa individuale, assegnazione attiva.
    def assignList = delegator.findList("WorkEffortPartyAssignment", EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, ind.getString("workEffortId")),
        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, RECIPIENT_ROLE)
    ], EntityOperator.AND), null, null, null, false)

    for (assign in assignList) {
        String recipientPartyId = assign.getString("partyId")
        if (!UtilValidate.isNotEmpty(recipientPartyId) || sentTo.contains(recipientPartyId)) continue

        def emailList = emailFinder.getEmailAddress(recipientPartyId)
        if (!emailList || emailList.isEmpty()) {
            Debug.logWarning("[GZOOM-NOTIF] Nessuna email per partyId=${recipientPartyId} (${RULE_ID})", MODULE)
            continue
        }
        String toEmail = emailList[0].infoString
        if (!UtilValidate.isNotEmpty(toEmail)) continue
        sentTo.add(recipientPartyId)

        def person = delegator.findOne("Person", [partyId: recipientPartyId], false)
        String name = person ? "${person.firstName} ${person.lastName}".trim() : recipientPartyId

        String subject = "GZOOM - Valutazione della performance organizzativa disponibile"
        String body = """<html><body style="font-family:Arial,sans-serif;font-size:14px;">
<p>Gentile ${name},</p>
<p>La valutazione della <strong>performance organizzativa</strong> della Sua struttura e' stata
<strong>conclusa</strong> ed e' ora disponibile nella Sua scheda di valutazione individuale.</p>
<p>Accedere al portale GZOOM per visualizzare la valutazione complessiva.</p>
<br/><p>Cordiali saluti,<br/>UOC Pianificazione e Controllo di Gestione</p>
</body></html>"""

        String logStatus = "SENT"
        String logError = null
        try {
            Map sendResult = dctx.dispatcher.runSync("sendMail", UtilMisc.toMap(
                "sendTo", toEmail, "sendFrom", sendFrom, "subject", subject,
                "body", body, "contentType", "text/html"))
            if (ServiceUtil.isError(sendResult)) {
                logStatus = "ERROR"; logError = ServiceUtil.getErrorMessage(sendResult)
                Debug.logWarning("[GZOOM-NOTIF] Errore invio a ${toEmail}: ${logError}", MODULE)
            } else {
                sent++
                Debug.log("[GZOOM-NOTIF] ${RULE_ID} inviata a ${toEmail} (UO ${orgUnitId})", MODULE)
            }
        } catch (Exception e) {
            logStatus = "ERROR"; logError = e.message
            Debug.logWarning("[GZOOM-NOTIF] Eccezione invio a ${toEmail}: ${e.message}", MODULE)
        }

        try {
            def logEntry = delegator.makeValue("GzoomEmailLog")
            logEntry.set("logId", delegator.getNextSeqId("GzoomEmailLog"))
            logEntry.set("ruleId", RULE_ID)
            logEntry.set("workEffortId", bsWorkEffortId)
            logEntry.set("recipientEmail", toEmail)
            logEntry.set("subject", subject)
            logEntry.set("sentAt", UtilDateTime.nowTimestamp())
            logEntry.set("status", logStatus)
            logEntry.set("errorMessage", logError)
            delegator.create(logEntry)
        } catch (Exception e) {
            Debug.logWarning("[GZOOM-NOTIF] Errore log email: ${e.message}", MODULE)
        }
    }
}

Debug.log("[GZOOM-NOTIF] ${RULE_ID} completata per CTX_BS ${bsWorkEffortId}: ${sent} email inviate", MODULE)
return ServiceUtil.returnSuccess()
