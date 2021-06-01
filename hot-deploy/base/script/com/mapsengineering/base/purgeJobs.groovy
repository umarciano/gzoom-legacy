import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;

def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

// check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

conditionStatusList = [];
conditionStatusList.add(EntityCondition.makeCondition("statusId", GenericValue.NULL_FIELD));
conditionStatusList.add(EntityCondition.makeCondition("statusId", "SERVICE_PENDING"));
conditionStatusList.add(EntityCondition.makeCondition("statusId", "SERVICE_RUNNING"));
conditionStatusMain = EntityCondition.makeCondition(conditionStatusList, EntityOperator.OR);
// Debug.log("## purgeJobs jobSandboxScheduled conditionStatusMain: " + conditionStatusMain);
def scheduled = delegator.findList("JobSandbox", conditionStatusMain, null, null, null, false);
Debug.log("## purgeJobs - jobSandboxScheduled : " + scheduled);

// CLEAN_TMP e CLEAN_CACHE sarebbero caricati dallo stesso file xml, quindi meglio caricarli a mano, uno per volta
//CREO TemporalExpression per CLEAN_TMP
def tCleanTmp = delegator.findOne("TemporalExpression", ["tempExprId": "CLEAN_TMP"], false);
if(UtilValidate.isEmpty(tCleanTmp)) {
    //inserisco
    tmExp = delegator.makeValue("TemporalExpression");
    tmExp.put("tempExprId", "CLEAN_TMP");
    tmExp.put("tempExprTypeId", "FREQUENCY");
    tmExp.put("description", "Daily Midnight");
    tmExp.put("date1", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    tmExp.put("integer1", 5L);
    tmExp.put("integer2", 1L);
    tmExp.create();
    Debug.log("## purgeJobs - inserito il tmExp : " + tmExp);
}
conditionMain = EntityCondition.makeCondition(
	EntityCondition.makeCondition("jobName", "CLEAN_TMP"),
	conditionStatusMain
);
def cleanTmpScheduled = delegator.findList("JobSandbox", conditionMain, null, null, null, false);
Debug.log("## purgeJobs - cleanTmpScheduled:"+cleanTmpScheduled);
if (UtilValidate.isEmpty(cleanTmpScheduled) || cleanTmpScheduled.size() <= 0) {
    //inserisco
    jobCleanTmp = delegator.makeValue("JobSandbox");
    jobCleanTmp.put("jobId", "CLEAN_TMP");
    jobCleanTmp.put("jobName", "CLEAN_TMP");
    jobCleanTmp.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobCleanTmp.put("serviceName", "gzCleanTmp");
    jobCleanTmp.put("poolId", "pool");
    jobCleanTmp.put("runAsUser", "system");
    jobCleanTmp.put("tempExprId", "CLEAN_TMP");
    jobCleanTmp.put("maxRecurrenceCount", -1L);
    jobCleanTmp.create();
    Debug.log("## purgeJobs - inserito il job:"+jobCleanTmp);
}

//CREO HOUR_05
def hour05 = delegator.findOne("TemporalExpression", ["tempExprId": "HOUR_05"], false);
if(UtilValidate.isEmpty(hour05)) {
    //inserisco
    tmExp = delegator.makeValue("TemporalExpression");
    tmExp.put("tempExprId", "HOUR_05");
    tmExp.put("tempExprTypeId", "HOUR_RANGE");
    tmExp.put("description", "Hour 5");
    tmExp.put("integer1", 5L);
    tmExp.put("integer2", 5L);
    tmExp.create();
    Debug.log("## purgeJobs - inserito il tmExp:"+tmExp);
}

//CREO CLEAN_CACHE
def tCleanCache = delegator.findOne("TemporalExpression", ["tempExprId": "CLEAN_CACHE"], false);
if(UtilValidate.isEmpty(tCleanCache)) {
    //inserisco
    tmExp = delegator.makeValue("TemporalExpression");
    tmExp.put("tempExprId", "CLEAN_CACHE");
    tmExp.put("tempExprTypeId", "UNION");
    tmExp.put("description", "Every Day at 5");
    tmExp.create();
    Debug.log("## purgeJobs - inserito il tmExp:"+tmExp);
}

//CREO CLEAN_CACHE ASSOC
def cleanCacheAssoc = delegator.findOne("TemporalExpressionAssoc", ["fromTempExprId": "CLEAN_CACHE",  "toTempExprId": "HOUR_05"], false);
if(UtilValidate.isEmpty(cleanCacheAssoc)) {
    //inserisco
    tmExpAss = delegator.makeValue("TemporalExpressionAssoc");
    tmExpAss.put("fromTempExprId", "CLEAN_CACHE");
    tmExpAss.put("toTempExprId", "HOUR_05");
    tmExpAss.create();
    Debug.log("## purgeJobs - inserito il tmExp:"+tmExpAss);
}

//CREO CLEAN_CACHE
conditionMain = EntityCondition.makeCondition(EntityCondition.makeCondition("jobName", "CLEAN_CACHE"),
	conditionStatusMain
);
def cleanCacheScheduled = delegator.findList("JobSandbox", conditionMain, null, null, null, false);
Debug.log("## purgeJobs - cleanCacheScheduled:"+cleanCacheScheduled);
if (UtilValidate.isEmpty(cleanCacheScheduled) || cleanCacheScheduled.size() <= 0) {
    //inserisco
    jobSandBoxCleanCache = delegator.makeValue("JobSandbox");
    jobSandBoxCleanCache.put("jobId", "CLEAN_CACHE");
    jobSandBoxCleanCache.put("jobName", "CLEAN_CACHE");
    jobSandBoxCleanCache.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobSandBoxCleanCache.put("serviceName", "gzCleanCache");
    jobSandBoxCleanCache.put("poolId", "pool");
    jobSandBoxCleanCache.put("runAsUser", "admin");
    jobSandBoxCleanCache.put("tempExprId", "CLEAN_CACHE");
    jobSandBoxCleanCache.put("maxRecurrenceCount", -1L);
    jobSandBoxCleanCache.create();
    
    Debug.log("## purgeJobs - inserito il job:"+jobSandBoxCleanCache);
}

// Adesso i job del file di proeprties
jobsScheduled = UtilProperties.getPropertyValueList("BaseConfig", "purgeJobs.jobSandbox");
Debug.log("## purgeJobs - jobsScheduled: " + jobsScheduled);

if (UtilValidate.isNotEmpty(jobsScheduled)) {
	jobsScheduled.each{ jobProp ->
		Debug.log("## purgeJobs - jobProp: " + jobProp);
    	key = jobProp.key;
		jobKey = key.substring(21); // ("purgeJobs.jobSandbox.") + 
    	Debug.log("## purgeJobs - jobKey: " + jobKey);
    	if (UtilValidate.isNotEmpty(jobKey)) {
	    	conditionKeyList = [];
			conditionKeyList.add(EntityCondition.makeCondition("jobId", jobKey));
			conditionKeyList.add(EntityCondition.makeCondition("jobName", jobKey));
			conditionKeyList.add(EntityCondition.makeCondition("serviceName", jobKey));
            
			conditionJobKey = EntityCondition.makeCondition(conditionKeyList, EntityOperator.OR);
	    	
	    	conditionMain = EntityCondition.makeCondition(conditionJobKey,conditionStatusMain);
	    	
			jobScheduled = delegator.findList("JobSandbox", conditionMain, null, null, null, false);
			Debug.log("## purgeJobs - jobScheduled:"+jobScheduled);
			if (UtilValidate.isEmpty(jobScheduled) || jobScheduled.size() <= 0) {
			
				serviceInMap = dctx.makeValidContext("gzImportDataFile", ModelService.IN_PARAM, parameters);
				serviceInMap.put("filename", jobProp.value);
				serviceInMap.put("userLogin", context.userLogin);
				serviceInMap.put("locale", context.locale);
				resService = dctx.getDispatcher().runSync("gzImportDataFile", serviceInMap);
				
				Debug.log("## purgeJobs - res resService : " + resService);
			}
		}
	}
}

return result;
