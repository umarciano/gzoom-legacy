import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.location.*;

/**
 *  Dato il contenId del report carico vadfo a caricare il nome dell'ftl 
 */
def ftlLocationName="";

//controllo se sono nel caso del popup
def defaultNameFile = "_param.ftl";
if (parameters.popup == "Y") {
    defaultNameFile = "_popup.ftl";
}

if (UtilValidate.isNotEmpty(parameters.contentId)) {

    def content = delegator.findOne("Content", [contentId: parameters.contentId], true);
    if (UtilValidate.isNotEmpty(content)) {
        def dataresource = delegator.findOne("DataResource", [dataResourceId: content.dataResourceId], true);
        if (UtilValidate.isNotEmpty(dataresource)) {
            /**
             * Vado a prendere il percorso objectInfo per vedere dove si trova il file
             * e il contentName che è il nome del file ftl da cercare
             */
            //component://custom/webapp/custom/birt/report/SchedaBudget.rptdesign

            def index = dataresource.objectInfo.lastIndexOf('/');
            
            
            def subStringObjectInfo = dataresource.objectInfo.substring(0,index);
            def index2 = subStringObjectInfo.lastIndexOf('/');
            
            ftlLocationName = dataresource.objectInfo.substring(0, index2 + 1) + "ftl/" + content.contentName + defaultNameFile;

            /**
             * Controllos e il file esiste altrimenti
             * Se è vuoto e se non esiste metto il fiel di default! (controllare differenze tra i diversi tipi)
             */
            Debug.log("###  ftlLocationName="+ftlLocationName);
            if (UtilValidate.isNotEmpty(ftlLocationName)) {
                def url = FlexibleLocation.resolveLocation(ftlLocationName);
                if(UtilValidate.isEmpty(url)) {
                    ftlLocationName = "";
                }
            }

            if (UtilValidate.isEmpty(ftlLocationName)) {
            
            	// se non ho nessun ftl associato devo controllare se è un'analisi se si ha un altro ftl standard
            	conditionList = [EntityCondition.makeCondition("contentId", "WE_PRINT_ANALYSIS"),
                        EntityCondition.makeCondition("contentIdTo", parameters.contentId),
                        EntityCondition.makeCondition("contentAssocTypeId", "REP_PERM"),
                        EntityCondition.makeCondition("thruDate", null)];    
			    listAssoc = delegator.findList("ContentAssoc", EntityCondition.makeCondition(conditionList), null, null, null, true);
			    if (UtilValidate.isNotEmpty(listAssoc)) {
			    	ftlLocationName = "component://workeffortext/webapp/workeffortext/birt/ftl/workeffortAnalysisAllPrintBirtExtraParameters.ftl";
			    } else {
	                //controllo se sono popup per mettere un file di default diverso per entrambi!
	                // ho dei dati di base diversi per corePerf perciò se sono in quel caso cambio il file di base
	                ftlLocationName = "component://workeffortext/webapp/workeffortext/birt/ftl/workeffortAllPrintBirtExtraParameters.ftl";
	                if ("CTX_CO".equals(parameters.parentTypeId)) {
	                    ftlLocationName = "component://corperf/webapp/corperf/birt/ftl/corPerfAllPrintBirtExtraParameters.ftl";
	                } else if ("CTX_EP".equals(parameters.parentTypeId)) {
	                    ftlLocationName = "component://emplperf/webapp/emplperf/birt/ftl/emplPerfAllPrintBirtExtraParameters.ftl";
	                }
                }
                
                if (parameters.popup == "Y") {
                    // non devo cacare sempre un default ma andare sul db per vedere se ci dati da caricare
                    // abbiamo utilizzato come campo solo la data perciò se ho un valore prendo la data
                    ftlLocationName = null; //"component://workeffortext/webapp/workeffortext/birt/ftl/reportNewPrintBirtOnlyDateParam.ftl";

                    def conditionList = [
                        EntityCondition.makeCondition("contentIdStart", content.contentId),
                        EntityCondition.makeCondition("contentTypeId", "BIRT_LNCH_FLD_SCREEN"),
                        EntityCondition.makeCondition("caContentAssocTypeId", "BIRT_EXTRAFIELD_OF")
                    ];
                    
                    def assocList = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition(conditionList), null, ["caSequenceNum"], null, true);
                    if (UtilValidate.isNotEmpty(assocList)) {
                        /**
                         * Prendo solo il primo della lista perchè nei nostri casi ne abbiamo solo 1
                         */
                        contentAssocViewTo = assocList[0];
                        if (UtilValidate.isNotEmpty(contentAssocViewTo.dataResourceId)) {
                            def dataResource = delegator.findOne("DataResource", ["dataResourceId" : contentAssocViewTo.dataResourceId], true);
                            if (UtilValidate.isNotEmpty(dataResource)) {
                                def objectInfo = dataResource.objectInfo;
                                if (UtilValidate.isNotEmpty(objectInfo)) {
                                    def screenLocationSplitted = StringUtil.split(objectInfo, "#");

                                    context.extraFieldContainerScreenName = screenLocationSplitted[1];
                                    context.extraFieldContainerScreenLocation = screenLocationSplitted[0];

                                    Debug.log("###  extraFieldContainerScreenLocation="+context.extraFieldContainerScreenLocation+"    extraFieldContainerScreenName="+context.extraFieldContainerScreenName);
                                }
                            }
                        }

                    }
                }



            }

        }

    }



}


Debug.log("###  ftlLocationName="+ftlLocationName);
context.ftlLocationName = ftlLocationName;
