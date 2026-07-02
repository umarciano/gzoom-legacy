import com.mapsengineering.base.find.PartyEmailFindServices
import org.ofbiz.base.util.Debug
import org.ofbiz.base.util.UtilMisc
import org.ofbiz.base.util.UtilProperties
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil
import org.ofbiz.service.ServiceUtil

/**
 * sendGzoomEventNotification
 *
 * Sends event-driven email notifications for GZOOM performance evaluation events:
 *   SHARE_EVAL      - valutatore condivide scheda → notifica valutato
 *   NOTE_VALUTATORE - valutatore inserisce nota esterna → notifica valutato
 *   NOTE_VALUTATO   - valutato inserisce nota → notifica valutatore
 *
 * Parameters (IN):
 *   workEffortId    - ID della scheda
 *   notificationType - SHARE_EVAL | NOTE_VALUTATORE | NOTE_VALUTATO
 */

String MODULE = "sendGzoomEventNotification"

String workEffortId    = parameters.workEffortId
String notificationType = parameters.notificationType

if (!UtilValidate.isNotEmpty(workEffortId) || !UtilValidate.isNotEmpty(notificationType)) {
    Debug.logWarning("[GZOOM-NOTIF] workEffortId o notificationType mancante", MODULE)
    return ServiceUtil.returnSuccess()
}

// Recupera valutatore (WEM_EVAL_MANAGER) e valutato (WEM_EVAL_IN_CHARGE)
def managerList = delegator.findList("WorkEffortPartyAssignment",
    EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_MANAGER")
    ], EntityOperator.AND), null, null, null, false)

def inChargeList = delegator.findList("WorkEffortPartyAssignment",
    EntityCondition.makeCondition([
        EntityCondition.makeCondition("workEffortId", EntityOperator.EQUALS, workEffortId),
        EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "WEM_EVAL_IN_CHARGE")
    ], EntityOperator.AND), null, null, null, false)

def managerAssign  = EntityUtil.getFirst(managerList)
def inChargeAssign = EntityUtil.getFirst(inChargeList)

if (!managerAssign || !inChargeAssign) {
    Debug.logWarning("[GZOOM-NOTIF] Impossibile trovare valutatore/valutato per workEffortId=${workEffortId}", MODULE)
    return ServiceUtil.returnSuccess()
}

String valutatorePId = managerAssign.partyId
String valutatoPId   = inChargeAssign.partyId

// Recupera dettagli persone e scheda
def personValutato   = delegator.findOne("Person", [partyId: valutatoPId],   false)
def personValutatore = delegator.findOne("Person", [partyId: valutatorePId], false)
def workEffort       = delegator.findOne("WorkEffort", [workEffortId: workEffortId], false)

String valutatoName   = personValutato   ? "${personValutato.firstName} ${personValutato.lastName}".trim()   : valutatoPId
String valutatoreName = personValutatore ? "${personValutatore.firstName} ${personValutatore.lastName}".trim() : valutatorePId
String schedaName     = workEffort?.workEffortName ?: workEffortId

// Determina destinatario e testo in base al tipo di notifica
String recipientPartyId
String subject
String body

if ("SHARE_EVAL" == notificationType) {
    recipientPartyId = valutatoPId
    subject = "GZOOM - Valutazione condivisa"
    body = """<html><body style="font-family:Arial,sans-serif;font-size:14px;">
<p>Gentile ${valutatoName},</p>
<p>La scheda di valutazione <strong>${schedaName}</strong> è stata condivisa con Lei dal valutatore <strong>${valutatoreName}</strong>.</p>
<p>È ora possibile visualizzare la valutazione accedendo al portale GZOOM.</p>
<br/><p>Cordiali saluti,<br/>UOC Pianificazione e Controllo di Gestione</p>
</body></html>"""

} else if ("NEW_EVAL" == notificationType) {
    // Trigger 2: nuova valutazione strategica inserita dall'admin
    recipientPartyId = valutatoPId
    subject = "GZOOM - Nuova scheda valutazione strategica"
    body = """<html><body style="font-family:Arial,sans-serif;font-size:14px;">
<p>Gentile ${valutatoName},</p>
<p>È stata creata una nuova scheda di valutazione strategica: <strong>${schedaName}</strong>.</p>
<p>Accedere al portale GZOOM per visualizzarla.</p>
<br/><p>Cordiali saluti,<br/>UOC Pianificazione e Controllo di Gestione</p>
</body></html>"""

} else if ("NOTE_VALUTATORE" == notificationType) {
    recipientPartyId = valutatoPId
    subject = "GZOOM - Nuova nota del valutatore"
    body = """<html><body style="font-family:Arial,sans-serif;font-size:14px;">
<p>Gentile ${valutatoName},</p>
<p>Il valutatore <strong>${valutatoreName}</strong> ha inserito una nota sulla scheda <strong>${schedaName}</strong>.</p>
<p>Accedere al portale GZOOM per visualizzare la nota.</p>
<br/><p>Cordiali saluti,<br/>UOC Pianificazione e Controllo di Gestione</p>
</body></html>"""

} else if ("NOTE_VALUTATO" == notificationType) {
    recipientPartyId = valutatorePId
    subject = "GZOOM - Nuova nota del valutato"
    body = """<html><body style="font-family:Arial,sans-serif;font-size:14px;">
<p>Gentile ${valutatoreName},</p>
<p>Il valutato <strong>${valutatoName}</strong> ha inserito una nota sulla scheda <strong>${schedaName}</strong>.</p>
<p>Accedere al portale GZOOM per visualizzare la nota.</p>
<br/><p>Cordiali saluti,<br/>UOC Pianificazione e Controllo di Gestione</p>
</body></html>"""

} else {
    Debug.logWarning("[GZOOM-NOTIF] Tipo notifica sconosciuto: ${notificationType}", MODULE)
    return ServiceUtil.returnSuccess()
}

// Recupera email destinatario
PartyEmailFindServices emailFinder = new PartyEmailFindServices(delegator)
def emailList = emailFinder.getEmailAddress(recipientPartyId)

if (!emailList || emailList.isEmpty()) {
    Debug.logWarning("[GZOOM-NOTIF] Nessuna email trovata per partyId=${recipientPartyId} (${notificationType})", MODULE)
    return ServiceUtil.returnSuccess()
}

String toEmail = emailList[0].infoString
if (!UtilValidate.isNotEmpty(toEmail)) {
    Debug.logWarning("[GZOOM-NOTIF] Email vuota per partyId=${recipientPartyId}", MODULE)
    return ServiceUtil.returnSuccess()
}

Debug.log("[GZOOM-NOTIF] Invio ${notificationType} a ${toEmail} per scheda ${workEffortId}", MODULE)

// Invia email tramite OFBiz sendMail (configurato per MailHog in general.properties)
String sendFrom = UtilProperties.getPropertyValue("general.properties", "defaultFromEmailAddress", "gzoom@gzoom.it")
Map sendMailCtx = UtilMisc.toMap(
    "sendTo",      toEmail,
    "sendFrom",    sendFrom,
    "subject",     subject,
    "body",        body,
    "contentType", "text/html"
)

try {
    // In Groovy service scripts 'dispatcher' is ServiceDispatcher; use dctx.dispatcher for LocalDispatcher
    Map sendResult = dctx.dispatcher.runSync("sendMail", sendMailCtx)
    if (ServiceUtil.isError(sendResult)) {
        Debug.logWarning("[GZOOM-NOTIF] Errore invio email: ${ServiceUtil.getErrorMessage(sendResult)}", MODULE)
    } else {
        Debug.log("[GZOOM-NOTIF] Email inviata con successo a ${toEmail}", MODULE)
    }
} catch (Exception e) {
    Debug.logWarning("[GZOOM-NOTIF] Eccezione durante invio email: ${e.message}", MODULE)
}

return ServiceUtil.returnSuccess()
