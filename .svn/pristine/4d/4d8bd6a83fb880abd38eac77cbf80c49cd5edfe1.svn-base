import java.lang.ConditionalSpecialCasing.Entry;

import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.GenericEntity;



def getIconImage(workEffortTypePeriod) {
	
	if(!"OPEN".equals(workEffortTypePeriod.statusEnumId)){
		return "SEMAPHOR_WHITE";
	} else{
		def period = delegator.findOne("CustomTimePeriod", ["customTimePeriodId": workEffortTypePeriod.customTimePeriodId], false);
		
		def condList = [];			
		condList.add(EntityCondition.makeCondition("workEffortTypePeriodId", workEffortTypePeriod.workEffortTypePeriodId));
		condList.add(EntityCondition.makeCondition("workEffortRevisionId", GenericEntity.NULL_FIELD));
		condList.add(EntityCondition.makeCondition("workEffortSnapshotId", GenericEntity.NULL_FIELD));
		condList.add(EntityCondition.makeCondition("estimatedStartDate", EntityOperator.LESS_THAN_EQUAL_TO, period.thruDate));
		condList.add(EntityCondition.makeCondition("estimatedCompletionDate", EntityOperator.GREATER_THAN_EQUAL_TO, period.fromDate));

		def workEffortList = delegator.findList("WorkEffortStatusView", EntityCondition.makeCondition(condList), null, null, null, true);
		def isNotClosed = false;
		def isNotActived = false;
		if(UtilValidate.isNotEmpty(workEffortList)){
			workEffortList.each {workEffort ->
				if(!"ACTSTATUS_CLOSED".equals(workEffort.actStEnumId)) {
					isNotClosed = true;
				}
				if(!"ACTSTATUS_ACTIVE".equals(workEffort.actStEnumId)) {
					isNotActived = true;
				}
				
				//stato attivo tutte vedo giallo
			}
		}
		if(isNotClosed){
			if(!isNotActived){
				return "SEMAPHOR_YELLOW";
			}else{
				return "SEMAPHOR_RED";
			}			
		} else{
			return "SEMAPHOR_GREEN";
		}
	}
	return "SEMAPHOR_YELLOW";
}

res = "success";
/*
Debug.log(" - parameters.statusEnumId " + parameters.statusEnumId);
Debug.log(" - parameters.statusEnumId " + parameters.statusEnumId);
Debug.log(" - parameters.statusEnumId " + request.getAttribute('statusEnumId_fld0_op'));
Debug.log(" - parameters.statusEnumId " + request.getAttribute('statusEnumId_fld0_value'));
Debug.log(" - parameters.statusEnumId " + request.getAttribute('statusEnumId_fld0_ic'));
Debug.log(" - parameters.statusEnumId " + request.getAttribute('workEffortTypeId_fld0_op'));
Debug.log(" - parameters.statusEnumId " + request.getAttribute('workEffortTypeId_fld0_value'));
Debug.log(" - parameters.statusEnumId " + request.getAttribute('workEffortTypeId_fld0_ic'));
Debug.log(" - parameters.statusEnumId " + parameters.workEffortTypeId_fld0_op);
Debug.log(" - parameters.statusEnumId " + parameters.workEffortTypeId_fld0_value);
Debug.log(" - parameters.statusEnumId " + parameters.workEffortTypeId_fld0_ic);
Debug.log(" - parameters.statusEnumId_fld0_op " + parameters.statusEnumId_fld0_op);
Debug.log(" - parameters.statusEnumId_fld0_value " + parameters.statusEnumId_fld0_value);
Debug.log(" - parameters.statusEnumId_fld0_ic " + parameters.statusEnumId_fld0_ic);
*/




def statusEnumId = null;


if(UtilValidate.isNotEmpty(parameters.statusEnumId)){
    statusEnumId = parameters.statusEnumId;
    parameters.statusEnumId = null;
    
    /**L'array viene convertito in stringa nella paginazione, e bisogna ripristinarlo come array!!*/
    if(statusEnumId instanceof String){ 
		
		statusEnumString = statusEnumId;
		
		if(statusEnumId.contains("{") || statusEnumId.contains("}")){
			statusEnumString = statusEnumId.substring(1, statusEnumId.size()-1);
		}
    	
    	statusEnumList = statusEnumString.split(',');
    	statusEnumId = [];
    	for(int i=0; i<statusEnumList.size(); i++ ){
    		String ele = statusEnumList[i]; 
    		if(i != 0){
    			ele = ele.substring(1, ele.size());
    		}
    		statusEnumId.add(ele);
    	}
    }
    
	
	parameters.statusEnumId_fld0_op = 'in';
	parameters.statusEnumId_fld0_value = statusEnumId;
	parameters.statusEnumId_fld0_ic = 'Y';
	context.statusEnumId_fld0_op = 'in';
	context.statusEnumId_fld0_value = statusEnumId;
	context.statusEnumId_fld0_ic = 'Y';
	
	
}

def res = org.ofbiz.base.util.GroovyUtil.runScriptAtLocation("com/mapsengineering/base/populateManagement.groovy", context);

def listIt = request.getAttribute("listIt");

/*if(UtilValidate.isNotEmpty(status)){
	def condList = [];
	if(status instanceof String){
		condList.add(status);
	}
	else {
		condList.addAll(status);
	}
	Debug.log(" - condList: " + condList);
	def conditionList = EntityCondition.makeCondition("status", EntityJoinOperator.IN, condList);
	
	listIt = EntityUtil.filterByCondition(listIt, conditionList);
	//parameters.status = conditionList;
	
	
	
	//request.setAttribute('status', conditionList);
}*/

def resultListIt =[];

listIt.each {result ->
	if(UtilValidate.isNotEmpty(result)){
		//Debug.log(" - result " + result);
		def mappa = [:];
		try{
			mappa = result.getAllFields();
			
			mappa.iconContentId = getIconImage(result);
			
		}catch(e){
			
		}
		resultListIt.add(mappa);
	}
}


//result.listIt = lista;
request.setAttribute('listIt',resultListIt);

return res;
