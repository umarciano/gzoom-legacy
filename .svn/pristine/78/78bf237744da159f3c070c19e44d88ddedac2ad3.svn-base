import org.ofbiz.base.util.*;
import org.ofbiz.webapp.control.SessionViewHistory;

//
// Cancello lo stack dall'indice passato come parametro in poi
//

breadcrumbIndex = 0;
breadcrumbSize = 0;

try {

	breadcrumbIndex = Integer.parseInt(parameters.get("breadcrumbIndex"));
	breadcrumbSize = Integer.parseInt(parameters.get("breadcrumbSize"));
} catch (Exception e) {

	return "none";
}

// Se l'indice cliccato é l'ultimo non faccio nulla
if (breadcrumbIndex == breadcrumbSize) {

	return "none";
}
 
sessionViewHistory = SessionViewHistory.get(session, "_SAVED_VIEW_");

// 
// Elimino tanti item quanti sono gli indici prima di quello *successivo* a quello cliccato
// Devo fare così perché il controller, con un valore di ritorno "success" esegue un view-last, quindi 
// automaticamente si posiziona sulla richiesta precedente.  Ad esempio, se clicco il terzo breadcrumb in una catena di 5 devo eliminare 
// solo l'ultimo (5), cosi' che nello stack rimangono 4 elementi e il view last si posiziona sul terzo. 
// nota: l'indice é a base 1

for (int i = breadcrumbSize; i > breadcrumbIndex + 1; i--) {

	sessionViewHistory.pop();
}

return "success";