
import javolution.util.FastMap;
import javolution.util.FastList;
import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.*;

// Creo la struttura per la costruzione del menu principale a sinistra
// La struttura è composta da una list principale che corrisponde agli argomenti del menu.
// il terzo elemento è a sua volta una list dei sub menu. Ciascun item di sub-menu è a sua volta
// una map, con primo elemento il titolo del sub menu e secondo elemento il link da richiamare

//
//Costruisco il menu solo al primo ingresso, altrimenti lo mantengo in sessione
//per migliorare le performance
//

//globalNodeTrail = session.getAttribute("globalNodeTrail");

//if (UtilValidate.isEmpty(globalNodeTrail)) {
    //result = dispatcher.runSync("traverseContent", [contentId : "GP_MENU", thruDateStr : UtilDateTime.nowTimestamp().toString(), contentAssocTypeId : "TREE_CHILD"]);
    //if (!ServiceUtil.isError(result)) {
        //globalNodeTrail = result.pickList;
    //}
    //session.setAttribute("globalNodeTrail", globalNodeTrail);
//}

//context.globalNodeTrail = globalNodeTrail;

//
//Get delle ultime impostazioni salvate
//
itemSelectedMap = session.getAttribute("itemSelectedMap");
if (UtilValidate.isNotEmpty(itemSelectedMap)) {
    context.put("_menuContentId", itemSelectedMap._menuContentId);
    context.put("_menuRootIdx", itemSelectedMap._menuRootIdx);
} else {
    //In questo caso sono al primo ingresso e cerco i dati se sono stati salvati
    //nelle userPreferences
    if (UtilValidate.isNotEmpty(userPreferences.contentId) && UtilValidate.isNotEmpty(userPreferences.rootIdx)) {
        context.put("_menuContentId", userPreferences.contentId);
        context.put("_menuRootIdx", userPreferences.rootIdx);
    }
}

return "success";
