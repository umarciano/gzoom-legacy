import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.standardimport.util.TakeOverUtil;

def populateErrorMap (mappaErrorList, jobLogId) {
	jobLogErrorList = delegator.findList("JobLogLog", EntityCondition.makeCondition( 
			EntityCondition.makeCondition("jobLogId", jobLogId),
			EntityCondition.makeCondition("logTypeEnumId", "LOG_ERR"))						
			, null, null, null, false);
	
	jobLogErrorList.each { jobLogError ->
		mappaError = [:];
		mappaError.id = jobLogError.jobLogId + "/ " + jobLogError.jobLogLogId;
		mappaError.message = jobLogError.logMessage;
		if(UtilValidate.isNotEmpty(jobLogError.valuePk1) && jobLogError.valuePk1.indexOf("sourceReferenceRootId") > -1) {
			def valuePk1 = TakeOverUtil.toMap(jobLogError.valuePk1);
			def codice = valuePk1["sourceReferenceRootId"];
			
			mappaError.context = codice;
		}
		
		mappaErrorList.add(mappaError);				
	}
	
	return mappaErrorList;
}

def populateWorkEffortTypeMap (workEffortTypeMap, key, description) {
	if (!workEffortTypeMap.containsKey(key) || UtilValidate.isEmpty(workEffortTypeMap[key])) {
		mappa = [:];
		mappa.recordElaborated = 0;
		mappa.description = description;
		workEffortTypeMap[key] = mappa;
	}
	
	workEffortTypeMap[key].recordElaborated = workEffortTypeMap[key].recordElaborated + 1;
    Debug.log(" - workEffortTypeMap[" + key + "] = " + workEffortTypeMap[key]);
    return workEffortTypeMap;
}

Debug.log(" - parameters.resultListUploadFile " + parameters.resultListUploadFile);
Debug.log(" - parameters.resultList " + parameters.resultList);
Debug.log(" - parameters.resultETLList " + parameters.resultETLList);

jobLogList = [];
mappaErrorList = [];

def blockingErrorsUploadFile = 0;
def blockingErrors = 0;

workEffortTypeMap = [:];

if (UtilValidate.isNotEmpty(parameters.resultListUploadFile)) {
	def resultListUploadFileList = parameters.resultListUploadFile;
	
	resultListUploadFileList.each { resultListUploadFile ->
		blockingErrorsUploadFile += resultListUploadFile.blockingErrors;
		Debug.log(" blockingErrorsUploadFile " + blockingErrorsUploadFile);
		
		def jobLogUploadFileId = resultListUploadFile.jobLogId;
		jobLogList.add(jobLogUploadFileId);
		
		if (blockingErrorsUploadFile > 0) {
			mappaErrorList = populateErrorMap(mappaErrorList, jobLogUploadFileId);
		}
	}
}

if (UtilValidate.isNotEmpty(parameters.resultList)) {
	
	def resultListList = parameters.resultList;
	resultListList.each { resultList ->
		def importedListPk = resultList.importedListPK;
		
		blockingErrors += resultList.blockingErrors;
		Debug.log(" blockingErrors " + blockingErrors);
		
		def jobLogId = resultList.jobLogId;
		jobLogList.add(jobLogId);
		
		if (blockingErrors > 0) {
			mappaErrorList = populateErrorMap(mappaErrorList, jobLogId);
		}
		Debug.log(" - importedListPk" + importedListPk);
	    
		// popolo mappa dei workEffortType PRIMA con gli elementi caricati correttamente
		importedListPk.each { importedPk ->
			if (importedPk.containsKey("sourceReferenceRootId")) {
				if (importedPk instanceof GenericPK && importedPk.getEntityName() != null) {
					if("WeRootInterface".equals(importedPk.getEntityName())) {
						def workEffortList = delegator.findList("WorkEffortAndTypeView", EntityCondition.makeCondition("sourceReferenceId", importedPk.sourceReferenceRootId), null, null, null, false);
						def workEffortType = EntityUtil.getFirst(workEffortList);
						if (UtilValidate.isNotEmpty(workEffortType)) {
							workEffortTypeMap = populateWorkEffortTypeMap(workEffortTypeMap, workEffortType.workEffortTypeId, workEffortType.description);
					    }
					}
				} else {
					def workEffortList = delegator.findList("WorkEffortAndTypeView", EntityCondition.makeCondition("sourceReferenceId", importedPk.sourceReferenceRootId), null, null, null, false);
					def workEffortType = EntityUtil.getFirst(workEffortList);
					if (UtilValidate.isNotEmpty(workEffortType)) {
						workEffortTypeMap = populateWorkEffortTypeMap(workEffortTypeMap, workEffortType.workEffortTypeId, workEffortType.description);
				    }
				}
			}
		
		};
		Debug.log(" - importedListPk" + importedListPk);
	    
	};
		
		
	// popolo mappa dei workEffortType POI con gli elementi contenenti errori
	def workEffortList = delegator.findList("WeRootInterface", null, null, null, null, false);
	Debug.log(" - workEffortList" + workEffortList);
    workEffortList.each { element ->
		workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId": element.workEffortTypeId], false);
		if (UtilValidate.isNotEmpty(workEffortType)) {
			workEffortTypeMap = populateWorkEffortTypeMap(workEffortTypeMap, workEffortType.workEffortTypeId, workEffortType.description);
	    } else {
	    	workEffortTypeMap = populateWorkEffortTypeMap(workEffortTypeMap, element.workEffortTypeId, element.workEffortTypeId);
	    }
	}
}

context.blockingErrorsUploadFile = blockingErrorsUploadFile;
context.blockingErrors = blockingErrors;
context.workEffortTypeMap = workEffortTypeMap;
context.mappaErrorList = mappaErrorList;
context.jobLogList = jobLogList;