import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.datamigration.util.DatabaseUtil;


def result = ServiceUtil.returnSuccess();

def delegator = dctx.getDelegator();
def security = dctx.getSecurity();

userLogin = (GenericValue) context.get("userLogin");
if (!security.hasPermission("ENTITY_MAINT", userLogin)) {
    return ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "ManagementErrorModelEntityNotSet", locale));
}

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

def batchStatusTempExpr = delegator.findOne("TemporalExpression", ["tempExprId": "BATCH_STATUS"], false);
if(UtilValidate.isEmpty(batchStatusTempExpr)) {
    //inserisco
    tmExp = delegator.makeValue("TemporalExpression");
    tmExp.put("tempExprId", "BATCH_STATUS");
    tmExp.put("tempExprTypeId", "UNION");
    tmExp.put("description", "Every Day at 1");
    tmExp.create();
    Debug.log("## inserito il tmExp:"+tmExp);
}

def batchStatusTempExprAssoc = delegator.findOne("TemporalExpressionAssoc", ["toTempExprId": "HOUR_01",  "fromTempExprId": "BATCH_STATUS"], false);
if(UtilValidate.isEmpty(batchStatusTempExprAssoc)) {
    //inserisco
    tmExpAss = delegator.makeValue("TemporalExpressionAssoc");
    tmExpAss.put("toTempExprId", "HOUR_01");
    tmExpAss.put("fromTempExprId", "BATCH_STATUS");
    tmExpAss.create();
    Debug.log("## inserito il tmExp:"+tmExpAss);
}

conditionBatchStatus = EntityCondition.makeCondition(EntityCondition.makeCondition("jobName", "Batch status"),
    EntityCondition.makeCondition("statusId",  "SERVICE_PENDING")
);
def batchStatusScheduled = delegator.findList("JobSandbox", conditionBatchStatus, null, null, null, false);
Debug.log("## batchStatusScheduled:"+batchStatusScheduled);
if (UtilValidate.isEmpty(batchStatusScheduled) || batchStatusScheduled.size() <= 0) {
    jobSandBoxBatchStatus = delegator.makeValue("JobSandbox");
    jobSandBoxBatchStatus.put("jobId", "BATCH_STATUS");
    jobSandBoxBatchStatus.put("jobName", "Batch status");
    jobSandBoxBatchStatus.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
    jobSandBoxBatchStatus.put("serviceName", "batchStatusScheduled");
    jobSandBoxBatchStatus.put("poolId", "pool");
    jobSandBoxBatchStatus.put("runAsUser", "admin");
    jobSandBoxBatchStatus.put("tempExprId", "BATCH_STATUS");
    jobSandBoxBatchStatus.put("maxRecurrenceCount", -1L);
    jobSandBoxBatchStatus.create();
    
    Debug.log("## inserito il job:"+jobSandBoxBatchStatus);
}

return result;
