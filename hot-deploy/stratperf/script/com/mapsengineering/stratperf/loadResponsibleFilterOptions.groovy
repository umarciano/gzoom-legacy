import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

// =====================================================================
// Filtro "Responsabile scheda" per la ricerca CTX_BS (Definizione/Valutazione).
// =====================================================================
// Visibile SOLO ai Direttori Sanitario/Amministrativo: loro vedono TUTTE le schede
// (non sono 'isRole'), quindi in Definizione si trovano davanti l'elenco completo e non
// riescono a distinguere le proprie (quelle su cui devono fare "Valida parzialmente").
// La tendina contiene UN SOLO valore: il nome dell'utente loggato stesso (Mensorio vede
// "MENSORIO ...", Abbate vede "ABBATE ...") -> selezionandolo filtra l'elenco alle sole
// schede di cui e' responsabile. Il parametro 'responsiblePartyId' e' gia' gestito dal
// filtro nativo in executePerformFindWorkEffortRootInqy.groovy (post-filtro su ORG_RESPONSIBLE).
//
// NB: non tocca il backend, e' solo popolamento della tendina + gating di visibilita'.

def delegator = context.delegator;
def userLogin = context.userLogin;

def isDirSanAmm = false;
String userLoginId = userLogin?.getString("userLoginId");
if (UtilValidate.isNotEmpty(userLoginId)) {
    def groups = EntityUtil.filterByDate(
        delegator.findByAnd("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId)));
    isDirSanAmm = groups?.any { it.getString("groupId") in ["STRATPERF_DIR_SAN", "STRATPERF_DIR_AMM"] };
}
context.showResponsibleFilter = isDirSanAmm ? "Y" : "N";

def responsibleOptions = [];
if (isDirSanAmm) {
    // Unico valore: l'utente loggato stesso.
    def myPartyId = userLogin?.getString("partyId");
    if (UtilValidate.isNotEmpty(myPartyId)) {
        def person = delegator.findOne("Person", UtilMisc.toMap("partyId", myPartyId), false);
        def display = myPartyId;
        if (UtilValidate.isNotEmpty(person)) {
            def cognome = person.lastName ?: "";
            def nome = person.firstName ?: "";
            def full = (cognome + " " + nome).trim();
            if (UtilValidate.isNotEmpty(full)) { display = full; }
        }
        responsibleOptions.add([partyId: myPartyId, displayName: display]);
    }
}
context.responsibleOptions = responsibleOptions;
