import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;


def result = ServiceUtil.returnSuccess();
def dispatcher = dctx.getDispatcher();
def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

// check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

/* 
    <TemporalExpression tempExprId="HOUR_01" tempExprTypeId="HOUR_RANGE" description="Hour 1" integer1="1" integer2="1"/>
    <TemporalExpression tempExprId="EXT_FILE_INTERFACE" tempExprTypeId="UNION" description="Every Day at 1" />
    <TemporalExpressionAssoc toTempExprId="HOUR_01" fromTempExprId="EXT_FILE_INTERFACE" />
    <JobSandbox jobId="EXT_FILE_INTERFACE" jobName="EXT_FILE_INTERFACE" runTime="2000-01-01 00:00:00.000" serviceName="standardImportExternalFileAsync" poolId="pool" runAsUser="system" tempExprId="EXT_FILE_INTERFACE" maxRecurrenceCount="-1"/> -->
*/

//CREO HOUR_01
def hour01 = delegator.findOne("TemporalExpression", ["tempExprId": "HOUR_01"], false);
if(UtilValidate.isEmpty(hour01)) {
    //inserisco
    tmExp = delegator.makeValue("TemporalExpression");
    tmExp.put("tempExprId", "HOUR_01");
    tmExp.put("tempExprTypeId", "HOUR_RANGE");
    tmExp.put("description", "Hour 1");
    tmExp.put("integer1", 1L);
    tmExp.put("integer2", 1L);
    tmExp.create();
    Debug.log("## inserito il tmExp:"+tmExp);
}

//CREO EXT_FILE_INTERFACE
def reminder = delegator.findOne("TemporalExpression", ["tempExprId": "EXT_FILE_INTERFACE"], false);
if(UtilValidate.isEmpty(reminder)) {
    //inserisco
    tmExp = delegator.makeValue("TemporalExpression");
    tmExp.put("tempExprId", "EXT_FILE_INTERFACE");
    tmExp.put("tempExprTypeId", "UNION");
    tmExp.put("description", "Every Day at 1");
    tmExp.create();
    Debug.log("## inserito il tmExp:"+tmExp);
}


//CREO ASSOC
def reminderAssoc = delegator.findOne("TemporalExpressionAssoc", ["toTempExprId": "HOUR_01",  "fromTempExprId": "EXT_FILE_INTERFACE"], false);
if(UtilValidate.isEmpty(reminderAssoc)) {
    //inserisco
    tmExpAss = delegator.makeValue("TemporalExpressionAssoc");
    tmExpAss.put("toTempExprId", "HOUR_01");
    tmExpAss.put("fromTempExprId", "EXT_FILE_INTERFACE");
    tmExpAss.create();
    Debug.log("## inserito il tmExp:"+tmExpAss);
}

//GESTIONE EVENTO EXT_FILE_INTERFACE
def job = delegator.findOne("JobSandbox", ["jobId": "EXT_FILE_INTERFACE"], false);
Debug.log("## job " + job);
if (UtilValidate.isNotEmpty(job)) {
    def serviceInMap = dispatcher.getDispatchContext().makeValidContext("resetScheduledJob", ModelService.IN_PARAM, parameters);
    serviceInMap.put("userLogin", context.userLogin);
    serviceInMap.put("locale", context.locale);
    serviceInMap.put("jobId", "EXT_FILE_INTERFACE");
    Debug.log("## reset job: EXT_FILE_INTERFACE " + serviceInMap);
    
    def res = dispatcher.runSync("resetScheduledJob", serviceInMap);
    Debug.log("reset job: EXT_FILE_INTERFACE  -> res="+res);
    
} else {
    //inserisco
    jobSandBoxReminder = delegator.makeValue("JobSandbox");
    jobSandBoxReminder.put("jobId", "EXT_FILE_INTERFACE");
    jobSandBoxReminder.put("jobName", "standardImportExternalFileAsync");
    jobSandBoxReminder.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobSandBoxReminder.put("serviceName", "standardImportExternalFileAsync");
    jobSandBoxReminder.put("poolId", "pool");
    jobSandBoxReminder.put("runAsUser", "system");
    jobSandBoxReminder.put("tempExprId", "EXT_FILE_INTERFACE");
    jobSandBoxReminder.put("maxRecurrenceCount", -1L);
    jobSandBoxReminder.create();
    Debug.log("## inserito il job:"+jobSandBoxReminder);
}

return result;
