/*
 * Script per recuperare la descrizione dell'account GL
 * Usato nel campo glAccountDescription di WorkEffortMeasureForms.xml
 */

import org.ofbiz.base.util.UtilValidate

// Inizializza la descrizione vuota
String glAccountDescr = ""

// Se esiste glAccount nel context
if (UtilValidate.isNotEmpty(glAccount)) {
    // Verifica se usare la descrizione multilingua
    if ("Y".equals(context.get("localeSecondarySet")) && 
        UtilValidate.isNotEmpty(glAccount.get("descriptionLang"))) {
        glAccountDescr = glAccount.get("descriptionLang")
    } else if (UtilValidate.isNotEmpty(glAccount.get("description"))) {
        glAccountDescr = glAccount.get("description")
    }
}

// Metti la descrizione nel context
context.glAccountDescr = glAccountDescr
