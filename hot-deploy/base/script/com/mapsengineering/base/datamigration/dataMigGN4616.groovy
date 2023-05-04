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

//CREO EVENTO DISABLE_USER_LOGIN
def disableUserLoginTemp = delegator.findOne("TemporalExpression", ["tempExprId": "DISABLE_USER_LOGIN"], false);
if(UtilValidate.isEmpty(disableUserLoginTemp)) {
    //inserisco
    tmExp = delegator.makeValue("TemporalExpression");
    tmExp.put("tempExprId", "DISABLE_USER_LOGIN");
    tmExp.put("tempExprTypeId", "UNION");
    tmExp.put("description", "Every Day at 1");
    tmExp.create();
    Debug.log("## inserito il tmExp:"+tmExp);
}

//CREO EVENDO DISABLE_USER_LOGIN ASSOC
def disableUserLoginTempAssoc = delegator.findOne("TemporalExpressionAssoc", ["toTempExprId": "HOUR_01",  "fromTempExprId": "DISABLE_USER_LOGIN"], false);
if(UtilValidate.isEmpty(disableUserLoginTempAssoc)) {
    //inserisco
    tmExpAss = delegator.makeValue("TemporalExpressionAssoc");
    tmExpAss.put("toTempExprId", "HOUR_01");
    tmExpAss.put("fromTempExprId", "DISABLE_USER_LOGIN");
    tmExpAss.create();
    Debug.log("## inserito il tmExp:"+tmExpAss);
}

//CREO EVENTO DISABLE_USER_LOGIN
conditionDisableUserLogin = EntityCondition.makeCondition(EntityCondition.makeCondition("jobName", "Disabilitazione utenti"),
    EntityCondition.makeCondition("statusId",  "SERVICE_PENDING")
);
def disableUserLoginScheduled = delegator.findList("JobSandbox", conditionDisableUserLogin, null, null, null, false);
Debug.log("## disableUserLoginScheduled:"+disableUserLoginScheduled);
if (UtilValidate.isEmpty(disableUserLoginScheduled) || disableUserLoginScheduled.size() <= 0) {
    //inserisco
    jobSandBoxRDisableUserLogin = delegator.makeValue("JobSandbox");
    jobSandBoxRDisableUserLogin.put("jobId", "DISABLE_USER_LOGIN");
    jobSandBoxRDisableUserLogin.put("jobName", "Disabilitazione utenti");
    jobSandBoxRDisableUserLogin.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobSandBoxRDisableUserLogin.put("serviceName", "disableUserLogin");
    jobSandBoxRDisableUserLogin.put("poolId", "pool");
    jobSandBoxRDisableUserLogin.put("runAsUser", "admin");
    jobSandBoxRDisableUserLogin.put("tempExprId", "DISABLE_USER_LOGIN");
    jobSandBoxRDisableUserLogin.put("maxRecurrenceCount", -1L);
    jobSandBoxRDisableUserLogin.create();
    
    Debug.log("## inserito il job:"+jobSandBoxRDisableUserLogin);
}

return result;
