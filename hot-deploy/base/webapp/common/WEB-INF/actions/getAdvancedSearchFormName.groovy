import org.ofbiz.base.util.UtilValidate
import org.ofbiz.widget.form.FormFactory

// Determina il nome del form di ricerca avanzata
def advancedSearchFormName = context.advancedSearchFormName
def entityName = context.entityName

// Se non è già definito, prova a costruirlo dal nome dell'entità
if (UtilValidate.isEmpty(advancedSearchFormName) && UtilValidate.isNotEmpty(entityName)) {
    def candidateFormName = entityName + "AdvancedSearchForm"
    def advancedSearchFormLocation = context.advancedSearchFormLocation ?: "component://base/widget/BaseForms.xml"
    
    try {
        // Prova a caricare il form per vedere se esiste
        def form = FormFactory.getFormFromLocation(advancedSearchFormLocation, candidateFormName, delegator.getModelReader(), dispatcher.getDispatchContext())
        
        if (form != null) {
            // Il form esiste, usalo
            advancedSearchFormName = candidateFormName
        } else {
            // Il form non esiste, usa il form base
            advancedSearchFormName = "BaseAdvancedSearchForm"
        }
    } catch (Exception e) {
        // Errore nel caricamento del form, usa il form base
        advancedSearchFormName = "BaseAdvancedSearchForm"
    }
} else if (UtilValidate.isEmpty(advancedSearchFormName)) {
    // Nessun nome specificato, usa il form base
    advancedSearchFormName = "BaseAdvancedSearchForm"
}

// Imposta il nome del form nel contesto
context.advancedSearchFormName = advancedSearchFormName
