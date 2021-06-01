import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;


def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

// check permission
userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

/* 
 * <TemporalExpression tempExprId="HOUR_01" tempExprTypeId="HOUR_RANGE" description="Hour 1" integer1="1" integer2="1"/>
    <TemporalExpression tempExprId="ANONYMOUS_SURVEY" tempExprTypeId="UNION" description="Every Day at 1" />
    <TemporalExpressionAssoc toTempExprId="HOUR_01" fromTempExprId="ANONYMOUS_SURVEY" />
    <JobSandbox jobId="CLEAN_SURVEY" jobName="CLEAN_SURVEY" runTime="2000-01-01 00:00:00.000" serviceName="anonymousSurveyAsync" poolId="pool" runAsUser="system" tempExprId="CLEAN_SURVEY" maxRecurrenceCount="-1"/> -->
    
    
*/
//CREO EVENDO HOUR_01
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

//CREO EVENDO ANONYMOUS_SURVEY
def reminder = delegator.findOne("TemporalExpression", ["tempExprId": "ANONYMOUS_SURVEY"], false);
if(UtilValidate.isEmpty(reminder)) {
    //inserisco
    tmExp = delegator.makeValue("TemporalExpression");
    tmExp.put("tempExprId", "ANONYMOUS_SURVEY");
    tmExp.put("tempExprTypeId", "UNION");
    tmExp.put("description", "Every Day at 1");
    tmExp.create();
    Debug.log("## inserito il tmExp:"+tmExp);
}


//CREO EVENDO ANONYMOUS_SURVEY ASSOC
def reminderAssoc = delegator.findOne("TemporalExpressionAssoc", ["toTempExprId": "HOUR_01",  "fromTempExprId": "ANONYMOUS_SURVEY"], false);
if(UtilValidate.isEmpty(reminderAssoc)) {
    //inserisco
    tmExpAss = delegator.makeValue("TemporalExpressionAssoc");
    tmExpAss.put("toTempExprId", "HOUR_01");
    tmExpAss.put("fromTempExprId", "ANONYMOUS_SURVEY");
    tmExpAss.create();
    Debug.log("## inserito il tmExp:"+tmExpAss);
}

//CREO EVENDO SOLLECITO OBIETTIVO
conditionReminder = EntityCondition.makeCondition(EntityCondition.makeCondition("jobName", "anonymousSurveyAsync"),
    EntityCondition.makeCondition("statusId",  "SERVICE_PENDING")
);
def reminderScheduled = delegator.findList("JobSandbox", conditionReminder, null, null, null, false);
Debug.log("## anonymousSurveyAsync:"+reminderScheduled);
if (UtilValidate.isEmpty(reminderScheduled) || reminderScheduled.size() <= 0) {
    //inserisco
    jobSandBoxReminder = delegator.makeValue("JobSandbox");
    jobSandBoxReminder.put("jobId", "ANONYMOUS_SURVEY");
    jobSandBoxReminder.put("jobName", "anonymousSurveyAsync");
    jobSandBoxReminder.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobSandBoxReminder.put("serviceName", "anonymousSurveyAsync");
    jobSandBoxReminder.put("poolId", "pool");
    jobSandBoxReminder.put("runAsUser", "system");
    jobSandBoxReminder.put("tempExprId", "ANONYMOUS_SURVEY");
    jobSandBoxReminder.put("maxRecurrenceCount", -1L);
    jobSandBoxReminder.create();
    
    Debug.log("## inserito il job:"+jobSandBoxReminder);
}

return result;
