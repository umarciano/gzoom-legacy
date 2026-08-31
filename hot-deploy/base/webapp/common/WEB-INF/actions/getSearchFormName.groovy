import org.ofbiz.base.util.UtilValidate
import org.ofbiz.widget.form.FormFactory

// Determina il nome del form di ricerca
def searchFormName = context.searchFormName
def entityName = context.entityName

// Se non è già definito, prova a costruirlo dal nome dell'entità
if (UtilValidate.isEmpty(searchFormName) && UtilValidate.isNotEmpty(entityName)) {
    def candidateFormName = entityName + "SearchForm"
    def searchFormLocation = context.searchFormLocation ?: "component://base/widget/BaseForms.xml"
    
    try {
        // Prova a caricare il form per vedere se esiste
        def form = FormFactory.getFormFromLocation(searchFormLocation, candidateFormName, delegator.getModelReader(), dispatcher.getDispatchContext())
        
        if (form != null) {
            // Il form esiste, usalo
            searchFormName = candidateFormName
        } else {
            // Il form non esiste, usa il form base
            searchFormName = "BaseSearchForm"
        }
    } catch (Exception e) {
        // Errore nel caricamento del form, usa il form base
        searchFormName = "BaseSearchForm"
    }
} else if (UtilValidate.isEmpty(searchFormName)) {
    // Nessun nome specificato, usa il form base
    searchFormName = "BaseSearchForm"
}

// Imposta il nome del form nel contesto
context.searchFormName = searchFormName
