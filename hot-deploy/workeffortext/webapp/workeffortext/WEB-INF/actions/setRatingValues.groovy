// Script per creare lista centralizzata dei valori di rating 1-5
// Utilizzato dai campi weTransValue per evitare valori hardcoded

import org.ofbiz.base.util.Debug;

Debug.logInfo("=== DEBUG setRatingValues: Inizio creazione lista rating values ===", "setRatingValues");

// Crea lista semplice dei valori di rating da 1 a 5 
// Per OFBiz list-options senza attributi, crea lista di stringhe semplice
ratingValues = [];

for (int i = 1; i <= 5; i++) {
    ratingValues.add(i.toString());
    Debug.logInfo("=== DEBUG setRatingValues: Aggiunto valore " + i + " ===", "setRatingValues");
}

Debug.logInfo("=== DEBUG setRatingValues: Lista completa creata con " + ratingValues.size() + " elementi ===", "setRatingValues");

// Imposta la lista nel context per essere utilizzata dai form
context.ratingValues = ratingValues;

Debug.logInfo("=== DEBUG setRatingValues: Lista impostata nel context. Context ratingValues size: " + context.ratingValues?.size() + " ===", "setRatingValues");
