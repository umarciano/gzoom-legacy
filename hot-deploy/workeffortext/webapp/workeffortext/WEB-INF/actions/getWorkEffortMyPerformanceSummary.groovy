import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import javolution.util.FastList;
import com.mapsengineering.base.util.*;

def listIt = [];
if (UtilValidate.isNotEmpty(context.listIt)) {
	context.listIt.each { item ->
		def fieldsMap = item.getAllFields();		
		def rootSearchRootInqyServiceMap = [:];
        rootSearchRootInqyServiceMap.put("workEffortRootId", item.workEffortId);
		rootSearchRootInqyServiceMap.put("userLogin", context.userLogin);
		def rootSearchRootInqyServiceRes = dispatcher.runSync("getCanViewUpdateWorkEffortRoot", rootSearchRootInqyServiceMap);
        fieldsMap.canUpdateRoot = rootSearchRootInqyServiceRes.canUpdateRoot;
		fieldsMap.canViewRoot = rootSearchRootInqyServiceRes.canViewRoot;
		
        
	    def nextValidStatusId = "";
	    def nextValidStatusDescr = "";
	    def nextValidStatus = getNextValidStatus(item.currentStatusId, item.sequenceId);
	    if (UtilValidate.isNotEmpty(nextValidStatus)) {
	    	nextValidStatusId = nextValidStatus.statusIdTo;
	    	nextValidStatusDescr = nextValidStatus.description;
	    }    
	    fieldsMap.nextValidStatusId = nextValidStatusId;
	    fieldsMap.nextValidStatusDescr = nextValidStatusDescr;
		
		listIt.add(fieldsMap);		
	}
	context.listIt = listIt;
}

def getNextValidStatus(currentStatusId, sequenceId) {	
    def nextValidStatusConditions = [];
    def nextValidStatusFieldsToSelect = UtilMisc.toSet("statusId", "statusIdTo", "sequenceId", "description");
    
    nextValidStatusConditions.add(EntityCondition.makeCondition("statusId", currentStatusId));
    //nextValidStatusConditions.add(EntityCondition.makeCondition("sequenceId", sequenceId));
    def nextValidStatusList = delegator.findList("StatusItemAndValidChangeStatusTo", EntityCondition.makeCondition(nextValidStatusConditions), nextValidStatusFieldsToSelect, null, 
    		                                      null, false);
    
    return EntityUtil.getFirst(nextValidStatusList);
}
