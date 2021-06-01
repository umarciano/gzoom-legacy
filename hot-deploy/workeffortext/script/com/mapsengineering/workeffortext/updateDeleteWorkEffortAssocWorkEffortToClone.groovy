import java.util.Map;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;


Debug.log("******************** STARTED SERVICE updateWorkEffortAssocWorkEffortToClone ******************");

def dispatcher = dctx.getDispatcher();

def operation = parameters.operation;
def workEffortIdFrom = parameters.workEffortIdFrom;
def workEffortIdTo = parameters.workEffortIdTo;
def hierarchyAssocTypeIdRoot = parameters.hierarchyAssocTypeIdRoot;
def sequenceNum = parameters.sequenceNum;
def assocWeight = parameters.assocWeight;

def listCopyCondition = [];
listCopyCondition.add(EntityCondition.makeCondition("padre", workEffortIdTo));
listCopyCondition.add(EntityCondition.makeCondition("clonato", workEffortIdFrom));
def listCopyAll = delegator.findList("WorkEffortAssocAndClone", EntityCondition.makeCondition(listCopyCondition), null, null, null, false);
def valueCopy = EntityUtil.getFirst(listCopyAll);

if (UtilValidate.isNotEmpty(valueCopy)) {
    
    def listCondition = [];
    listCondition.add(EntityCondition.makeCondition("workEffortIdFrom", workEffortIdTo));
    listCondition.add(EntityCondition.makeCondition("workEffortAssocTypeId", hierarchyAssocTypeIdRoot));
    listCondition.add(EntityCondition.makeCondition("workEffortIdTo", valueCopy.figlio));
    
    def list = delegator.findList("WorkEffortAssoc", EntityCondition.makeCondition(listCondition), null, null, null, false);
    
    for(item in list ){
        
        if ("DELETE".equals(operation)) {
            def map = [:];
            map.workEffortId = item.workEffortIdTo;
            map.operation = "DELETE";
            
            dispatcher.runSync("crudServiceDefaultOrchestration_WorkEffort", ["parameters": map, "userLogin": context.userLogin, "operation": "DELETE", "entityName": "WorkEffort", "locale" : locale]);
            Debug.log("***  updateWorkEffortAssocWorkEffortToClone delete WorkEffort="+map);
        
        } else {
            //sono nel caso di update scrivendo i nuovi valori
            item.assocWeight = assocWeight;
            item.sequenceNum = sequenceNum;
            
            dispatcher.runSync("crudServiceDefaultOrchestration_WorkEffortAssoc_Override", ["parameters": item, "userLogin": context.userLogin, "operation": "UPDATE", "entityName": "WorkEffortAssoc", "locale" : locale]);
            Debug.log("***  updateWorkEffortAssocWorkEffortToClone update WorkEffortAssoc="+item);
    
        }
    }
    
}


