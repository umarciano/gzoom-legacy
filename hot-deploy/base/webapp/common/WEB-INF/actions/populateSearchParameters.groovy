import org.ofbiz.base.util.*;
import org.ofbiz.base.util.UtilHttp;
import javolution.util.FastMap;

import java.util.regex.Matcher
import java.util.regex.Pattern

import java.util.logging.Logger

Logger logger = Logger.getLogger("")
logger.info ("[populateSearchParameters.groovy] BEGIN")

// Gestione queryString e queryStringMap, vengono gestiti nella sessione in una mappa del tipo WorkEffortAssignmentView_searchParamsMap_N
// nella classe java SessionHistoryWorker
//Questo groovy recupera dai parameters tutti i parametri relativi ad una precedente richiesta di performFind
//quindi quelli nella forma filedName"_fld" o"_op" e li mette nel contesto. Nel caso tra i parameters sia gia'
//presente la queryStringMap si mette quella nel contesto, altrimenti si costruisce partendo dal modello dell'entita'
//Questo permette di poter tornare indietro alla mappa di ricerca salvando i dati di ricerca e inviandoli con altre request
entityName = context.entityName;
logger.info ("[populateSearchParameters.groovy] entityName = " + entityName);
if (UtilValidate.isEmpty(entityName))
    entityName = parameters.entityName;

if (UtilValidate.isNotEmpty(entityName)) {
	
    if (UtilValidate.isNotEmpty(parameters.queryStringMap)) {
        context.putAll(parameters.queryStringMap);
        context.queryString = parameters.queryString;
 		logger.info ("[populateSearchParameters.groovy] queryString = " + context.queryString);
    } else {
        modelEntity = delegator.getModelEntity(entityName);

        if (UtilValidate.isNotEmpty(modelEntity)) {
            fieldNameList = modelEntity.getAllFieldNames();

            parameters.each { key,value ->
                fieldNameList.each { fieldName ->
					def pattern = "^" + fieldName + "(_fld[01])?(_op)?\$"
					if (key ==~ pattern) {
                        context[key] = value;
                        if (!key.endsWith("_op"))
                            context[fieldName] = value;
                    }
                }
            }
        }
    }
}

logger.info ("[populateSearchParameters.groovy] END")