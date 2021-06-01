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

//CREO EVENDO INVIO MAIL
conditionMailt = EntityCondition.makeCondition(EntityCondition.makeCondition("jobName", "Send Email"),
    EntityCondition.makeCondition("statusId",  "SERVICE_PENDING")
);
def emailScheduled = delegator.findList("JobSandbox", conditionMailt, null, null, null, false);
Debug.log("## emailScheduled:"+emailScheduled);
if (UtilValidate.isEmpty(emailScheduled) || emailScheduled.size() <= 0) {
    //inserisco
    jobSandBoxMail = delegator.makeValue("JobSandbox");
    jobSandBoxMail.put("jobId", "SENDEMAIL");
    jobSandBoxMail.put("jobName", "Send Email");
    jobSandBoxMail.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobSandBoxMail.put("serviceName", "sendEmailDated");
    jobSandBoxMail.put("poolId", "pool");
    jobSandBoxMail.put("runAsUser", "system");
    jobSandBoxMail.put("tempExprId", "SENDEMAIL");
    jobSandBoxMail.put("maxRecurrenceCount", -1L);
    jobSandBoxMail.create();
    Debug.log("## inserito il job:"+jobSandBoxMail);
}


//CREO EVENTO HOUR_01
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

//CREO EVENTO REMINDER
def reminder = delegator.findOne("TemporalExpression", ["tempExprId": "REMINDER"], false);
if(UtilValidate.isEmpty(reminder)) {
    //inserisco
    tmExp = delegator.makeValue("TemporalExpression");
    tmExp.put("tempExprId", "REMINDER");
    tmExp.put("tempExprTypeId", "UNION");
    tmExp.put("description", "Every Day at 1");
    tmExp.create();
    Debug.log("## inserito il tmExp:"+tmExp);
}

//CREO EVENDO REMINDER ASSOC
def reminderAssoc = delegator.findOne("TemporalExpressionAssoc", ["toTempExprId": "HOUR_01",  "fromTempExprId": "REMINDER"], false);
if(UtilValidate.isEmpty(reminderAssoc)) {
    //inserisco
    tmExpAss = delegator.makeValue("TemporalExpressionAssoc");
    tmExpAss.put("toTempExprId", "HOUR_01");
    tmExpAss.put("fromTempExprId", "REMINDER");
    tmExpAss.create();
    Debug.log("## inserito il tmExp:"+tmExpAss);
}

//CREO EVENTO SOLLECITO SCHEDA VALUTAZIONE
conditionReminder = EntityCondition.makeCondition(EntityCondition.makeCondition("jobName", "Sollecito Stampa Scheda Valutazione Individuale"),
    EntityCondition.makeCondition("statusId",  "SERVICE_PENDING")
);
def reminderScheduled = delegator.findList("JobSandbox", conditionReminder, null, null, null, false);
Debug.log("## reminderScheduled:"+reminderScheduled);
if (UtilValidate.isEmpty(reminderScheduled) || reminderScheduled.size() <= 0) {
    //inserisco
    jobSandBoxReminder = delegator.makeValue("JobSandbox");
    jobSandBoxReminder.put("jobId", "REMINDER_VAL_DIP");
    jobSandBoxReminder.put("jobName", "Sollecito Stampa Scheda Valutazione Individuale");
    jobSandBoxReminder.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobSandBoxReminder.put("serviceName", "reminderValDipScheduled");
    jobSandBoxReminder.put("poolId", "pool");
    jobSandBoxReminder.put("runAsUser", "admin");
    jobSandBoxReminder.put("tempExprId", "REMINDER");
    jobSandBoxReminder.put("maxRecurrenceCount", -1L);
    jobSandBoxReminder.create();
    
    Debug.log("## inserito il job:"+jobSandBoxReminder);
}

//CREO EVENTO SOLLECITO SCHEDA VALUTAZIONE 1
conditionReminder1 = EntityCondition.makeCondition(EntityCondition.makeCondition("jobName", "Sollecito Stampa Scheda Valutazione Individuale Duplicato 1"),
    EntityCondition.makeCondition("statusId",  "SERVICE_PENDING")
);
def reminderScheduled1 = delegator.findList("JobSandbox", conditionReminder1, null, null, null, false);
Debug.log("## reminderScheduled1:"+reminderScheduled1);
if (UtilValidate.isEmpty(reminderScheduled1) || reminderScheduled1.size() <= 0) {
    //inserisco
    jobSandBoxReminder = delegator.makeValue("JobSandbox");
    jobSandBoxReminder.put("jobId", "REMINDER_VAL_DIP_1");
    jobSandBoxReminder.put("jobName", "Sollecito Stampa Scheda Valutazione Individuale Duplicato 1");
    jobSandBoxReminder.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobSandBoxReminder.put("serviceName", "reminderValDip1Scheduled");
    jobSandBoxReminder.put("poolId", "pool");
    jobSandBoxReminder.put("runAsUser", "admin");
    jobSandBoxReminder.put("tempExprId", "REMINDER");
    jobSandBoxReminder.put("maxRecurrenceCount", -1L);
    jobSandBoxReminder.create();
    
    Debug.log("## inserito il job:"+jobSandBoxReminder);
}

//CREO EVENTO SOLLECITO SCHEDA VALUTAZIONE 2
conditionReminder2 = EntityCondition.makeCondition(EntityCondition.makeCondition("jobName", "Sollecito Stampa Scheda Valutazione Individuale Duplicato 2"),
    EntityCondition.makeCondition("statusId",  "SERVICE_PENDING")
);
def reminderScheduled2 = delegator.findList("JobSandbox", conditionReminder2, null, null, null, false);
Debug.log("## reminderScheduled2:"+reminderScheduled2);
if (UtilValidate.isEmpty(reminderScheduled2) || reminderScheduled2.size() <= 0) {
    //inserisco
    jobSandBoxReminder = delegator.makeValue("JobSandbox");
    jobSandBoxReminder.put("jobId", "REMINDER_VAL_DIP_2");
    jobSandBoxReminder.put("jobName", "Sollecito Stampa Scheda Valutazione Individuale Duplicato 2");
    jobSandBoxReminder.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobSandBoxReminder.put("serviceName", "reminderValDip2Scheduled");
    jobSandBoxReminder.put("poolId", "pool");
    jobSandBoxReminder.put("runAsUser", "admin");
    jobSandBoxReminder.put("tempExprId", "REMINDER");
    jobSandBoxReminder.put("maxRecurrenceCount", -1L);
    jobSandBoxReminder.create();
    
    Debug.log("## inserito il job:"+jobSandBoxReminder);
}

return result;
