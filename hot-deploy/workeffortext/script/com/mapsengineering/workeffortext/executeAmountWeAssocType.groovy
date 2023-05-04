import java.util.Map;

import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.ModelService;
import com.mapsengineering.base.find.AcctgTransFindServices;

Debug.log("******************** STARTED SERVICE executeAmountWeAssocType ******************");

// essendo un crud i parametri sono in mappe particolari
def parametersCrud = parameters;
def parameters = parameters.parameters;

// valore di ritorno utilizzato nei servizi per usciore dal ciclo for
res = "success"; 
// valore di ritorno utilizzato nel servizio
def returnMap = ServiceUtil.returnSuccess();

/*
Debug.log("executeAmountWeAssocType.groovy ****************** context.entityName " + context.entityName);
Debug.log("executeAmountWeAssocType.groovy ****************** context.operation " + context.operation);
Debug.log("executeAmountWeAssocType.groovy ****************** parametersCrud " + parametersCrud);
Debug.log("executeAmountWeAssocType.groovy ****************** parameters" + parameters);
// valore utilizzato nella ricerca delle fase legate ai processi
Debug.log("executeAmountWeAssocType.groovy ****************** context.workEffortAssocTypeId " + context.workEffortAssocTypeId);
Debug.log("executeAmountWeAssocType.groovy ****************** parameters.defaultOrganizationPartyId " + parameters.defaultOrganizationPartyId);
Debug.log("executeAmountWeAssocType.groovy ****************** parameters.customTimePeriodId " + parameters.customTimePeriodId);
Debug.log("executeAmountWeAssocType.groovy ****************** parameters.weTransValue " + parameters.weTransValue);
*/

serviceName = "crudServiceDefaultOrchestration_AcctgTransAndEntries";

// Debug.log("executeAmountWeAssocType.groovy customTimePeriodId " + parameters.customTimePeriodId);
def customTimePeriod = delegator.findOne("CustomTimePeriod", ["customTimePeriodId": parameters.customTimePeriodId], false);

def weTransDate = null;
// Debug.log("executeAmountWeAssocType.groovy parameters.weTransDate " + parameters.weTransDate);
if(UtilValidate.isEmpty(parameters.weTransDate)) {
    weTransDate = customTimePeriod.thruDate;
} else {
    weTransDate = ObjectType.simpleTypeConvert(parameters.weTransDate, "Timestamp", null, locale);
}
weTransTypeValueId = parameters.weTransTypeValueId;
Debug.log("executeAmountWeAssocType.groovy weTransDate " + weTransDate);
Debug.log("executeAmountWeAssocType.groovy weTransTypeValueId " + weTransTypeValueId);

Debug.log("executeAmountWeAssocType.groovy parameters.weTransAccountId " + parameters.weTransAccountId);
Debug.log("executeAmountWeAssocType.groovy parameters.weTransWeId " + parameters.weTransWeId);
Debug.log("executeAmountWeAssocType.groovy parameters.weTransCurrencyUomId " + parameters.weTransCurrencyUomId);
Debug.log("executeAmountWeAssocType.groovy parameters.weTransValue " + parameters.weTransValue);
Debug.log("executeAmountWeAssocType.groovy parameters.weTransWeId " + parameters.weTransWeId);
Debug.log("executeAmountWeAssocType.groovy parameters.workEffortMeasureId " + parameters.workEffortMeasureId);
Debug.log("executeAmountWeAssocType.groovy parameters.searchDate " + parameters.searchDate);
Debug.log("executeAmountWeAssocType.groovy parameters.customTimePeriodId " + parameters.customTimePeriodId);

// Recupero Fattori di calcolo
def glAccountInputCalcList = delegator.findList("GlAccountInputCalc", EntityCondition.makeCondition("glAccountIdRef", parameters.weTransAccountId), null, null, null, false);
glAccountInputCalcList.each { glAccountInputCalc ->
    def itemWeTransAccountId = glAccountInputCalc.glAccountId;
    Debug.log("executeAmountWeAssocType.groovy Fattore di calcolo = " + itemWeTransAccountId);
    def conditionList = [];
    conditionList.add(EntityCondition.makeCondition("workEffortAssocTypeId", context.workEffortAssocTypeId)); // valore inserito nel parametro amountWeAssocType
    
    conditionList.add(EntityCondition.makeCondition("workEffortIdFrom", parameters.weTransWeId));
    conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, customTimePeriod.thruDate));
    conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, customTimePeriod.fromDate));
    conditionList.add(EntityCondition.makeCondition("wrToSnapShotId", null));
    conditionList.add(EntityCondition.makeCondition("wrFromSnapShotId", null));
    conditionList.add(EntityCondition.makeCondition("wrFromActivation", EntityOperator.NOT_EQUAL, "ACTSTATUS_REPLACED"));
    def workEffortAssocList = delegator.findList("WorkEffortAssocExtView", EntityCondition.makeCondition(conditionList), null, null, null, false);
    workEffortAssocList.each { workEffortAssoc ->
        def itemWeTransWeId = workEffortAssoc.workEffortIdTo;
        Debug.log("executeAmountWeAssocType.groovy Fase = " + itemWeTransWeId);
        //Per uscire dal ciclo for in caso di errore
        if ("error".equals(res)) {
            Debug.log("executeAmountWeAssocType.groovy errore durante esecuzione servizio, nessuna ulteriore operazione per questa fase = " + itemWeTransWeId);
            return;
        }
        
        def itemPartyId = null;
        def itemEntryPartyId = null;
        def itemWeTransMeasureId = null;
        def itemTransProductId = null;
        
        // essendo modello su obiettivo posso recuperare workEffortMeasureId, avendo itemWeTransAccountId e itemWeTransWeId
        def wmList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(EntityCondition.makeCondition("glAccountId", itemWeTransAccountId),EntityCondition.makeCondition("workEffortId", itemWeTransWeId)), null, null, null, false);
        GenericValue wm = EntityUtil.getFirst(wmList);
        if(UtilValidate.isEmpty(wm)) {
            Debug.log("executeAmountWeAssocType.groovy la misura non esiste, non fare niente... ");
            return;
        }
        
        // recupero eventuale movimento esistente
        itemWeTransMeasureId = wm.workEffortMeasureId;
        AcctgTransFindServices acctgTransFindServices = new AcctgTransFindServices(delegator, parameters.defaultOrganizationPartyId);
        Debug.log("executeAmountWeAssocType.groovy itemPartyId " + itemPartyId);
        Debug.log("executeAmountWeAssocType.groovy itemEntryPartyId " + itemEntryPartyId); // deprecato
        Debug.log("executeAmountWeAssocType.groovy itemWeTransMeasureId " + itemWeTransMeasureId);
        Debug.log("executeAmountWeAssocType.groovy itemWeTransWeId " + itemWeTransWeId);
        Debug.log("executeAmountWeAssocType.groovy itemWeTransAccountId " + itemWeTransAccountId);
        Debug.log("executeAmountWeAssocType.groovy itemTransProductId " + itemTransProductId); // deprecato
        // esistono 8 valori per la condizione, essendo modello su obiettivo ne usiamo 5
        def conditionIsUpdate = acctgTransFindServices.getConditionIsUpdate(itemPartyId, itemEntryPartyId, weTransDate, weTransTypeValueId, 
                itemWeTransMeasureId, itemWeTransWeId, itemWeTransAccountId, itemTransProductId);

        List<GenericValue> acctgTransAndEntriesViews = delegator.findList("AcctgTransAndEntriesView", EntityCondition.makeCondition(conditionIsUpdate) , null, null, null, false);
        GenericValue acctgTransAndEntriesView = EntityUtil.getFirst(acctgTransAndEntriesViews);
        Debug.log("executeAmountWeAssocType.groovy For conditionIsUpdate " + conditionIsUpdate + " found  acctgTransAndEntriesView = " + acctgTransAndEntriesView);

        def itemLocalParameters = [:];
        itemLocalParameters.weTransValue = parameters.weTransValue;
        itemLocalParameters.defaultOrganizationPartyId = parameters.defaultOrganizationPartyId;
        itemLocalParameters.weTransDate = weTransDate;
        itemLocalParameters.weTransWeId = itemWeTransWeId;
        itemLocalParameters.weTransMeasureId = itemWeTransMeasureId;
        
        itemLocalParameters.weTransAccountId = itemWeTransAccountId;
        GenericValue glAccount = delegator.findOne("GlAccount", false, "glAccountId", itemWeTransAccountId);
        itemLocalParameters.weTransCurrencyUomId = glAccount.defaultUomId;
        
        itemLocalParameters.weTransTypeValueId = parameters.weTransTypeValueId;
        
        // confrontare operation del processo con quello della fase...
        
        // se movimento non esiste 
        // if context.operation != DELETE -> operation = CREATE
        // else do nothing
        
        // se movimento esiste 
        // if context.operation = DELETE -> operation = DELETE
        // else -> operation = UPDATE
        operation = "";
        if(UtilValidate.isEmpty(acctgTransAndEntriesView)) {
            if (!"DELETE".equals(context.operation)) {
                Debug.log("executeAmountWeAssocType.groovy e' da creare ");
                // Debug.log("executeAmountWeAssocType.groovy itemLocalParameters " + itemLocalParameters);
                operation = "CREATE";
                itemLocalParameters.operation = "CREATE";
                itemLocalParameters._AUTOMATIC_PK_ = "Y";
                itemLocalParameters.isPosted = "Y";
                    
                serviceMap = [:];
                serviceMap.put("entityName", "WorkEffortTransactionIndicatorView");
                serviceMap.put("operation", operation);
                serviceMap.put("userLogin", context.userLogin);
                serviceMap.put("parameters", itemLocalParameters);
                serviceMap.put("locale", locale);
                serviceMap.put("timeZone", timeZone);
    
                Debug.log("executeAmountWeAssocType.groovy CREATE serviceInMap " + serviceMap);
                returnMap = dctx.getDispatcher().runSync(serviceName, serviceMap);
                Debug.log("executeAmountWeAssocType.groovy CREATE returnMap " + returnMap);
                if (ServiceUtil.isError(returnMap)) {
                    res = "error";
                    Debug.log("executeAmountWeAssocType.groovy CREATE error");
                    return;
                }
            } else {
                Debug.log("executeAmountWeAssocType.groovy gia' non esiste... ");
            }
        } else {
            Debug.log("executeAmountWeAssocType.groovy e' da cancellare o aggiornare " + itemLocalParameters.weTransValue);
            // Debug.log("executeAmountWeAssocType.groovy itemLocalParameters " + itemLocalParameters);
            itemLocalParameters.weTransId = acctgTransAndEntriesView.acctgTransId;
            itemLocalParameters.weTransEntryId = acctgTransAndEntriesView.entryAcctgTransEntrySeqId;
            
            if ("DELETE".equals(context.operation) || UtilValidate.isEmpty(itemLocalParameters.weTransValue)) {
                operation = "DELETE";
                Debug.log("executeAmountWeAssocType.groovy ****************** esiste movimento ma operation " + context.operation + " e value " + itemLocalParameters.weTransValue + " quindi operation " + operation);
            } else {
                operation = "UPDATE";
                Debug.log("executeAmountWeAssocType.groovy ****************** esiste movimento and value " + itemLocalParameters.weTransValue+ " quindi operation " + operation);
                // il nuovo movimento deve avere isPosted = "Y"
                itemLocalParameters.isPosted = "Y";
            }
            itemLocalParameters.operation = operation;
            serviceMap = [:];
            serviceMap.put("entityName", "WorkEffortTransactionIndicatorView");
            serviceMap.put("operation", operation);
            serviceMap.put("userLogin", context.userLogin);
            serviceMap.put("parameters", itemLocalParameters);
            serviceMap.put("locale", locale);
            serviceMap.put("timeZone", timeZone);
            // sovrascrivere isPosted
            def acctgTransToStore = delegator.findOne("AcctgTrans", ["acctgTransId": itemLocalParameters.weTransId], false);
            acctgTransToStore.set("isPosted", "N");
            acctgTransToStore.store();
            
            returnMap = dctx.getDispatcher().runSync(serviceName, serviceMap);
            Debug.log("executeAmountWeAssocType.groovy returnMap " + returnMap);
            if (ServiceUtil.isError(returnMap)) {
                res = "error";
                Debug.log("executeAmountWeAssocType.groovy error");
                return;
            }
        }
    }
}
Debug.log("******************** FINISH SERVICE executeAmountWeAssocType ******************");
Debug.log("returnMap " + returnMap);
return returnMap;
