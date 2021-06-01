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

def groupName = (String) context.get("groupName");
if(UtilValidate.isEmpty(groupName)) {
	groupName = DatabaseUtil.DEFAULT_GROUP_NAME;
}

cleanCacheList = delegator.findList("JobSandbox", EntityCondition.makeCondition("jobId", "CLEAN_CACHE"), null, null, null, false);

if(UtilValidate.isEmpty(cleanCacheList)) {
	Debug.log("Schedulazione servizio CLEAN_CACHE...");
	
	GenericValue hours5 = delegator.findOne("TemporalExpression", ["tempExprId": "HOUR_05"], false);
	if(UtilValidate.isEmpty(hours5)) {
    	GenericValue te5 = delegator.makeValue("TemporalExpression");
        te5.put("tempExprId", "HOUR_05");
        te5.put("tempExprTypeId", "HOUR_RANGE");
        te5.put("description", "Hour 5");
        te5.put("integer1", 5L);
        te5.put("integer2", 5L);
    	
        te5.create();
	}
	
	GenericValue un = delegator.findOne("TemporalExpression", ["tempExprId": "CLEAN_CACHE"], false);
	if(UtilValidate.isEmpty(un)) {
	    GenericValue teu = delegator.makeValue("TemporalExpression");
        teu.put("tempExprId", "CLEAN_CACHE");
        teu.put("tempExprTypeId", "UNION");
        teu.put("description", "Every Day at 5");
    	
        teu.create();
	}
	
	cleanCacheList = delegator.findList("TemporalExpressionAssoc", EntityCondition.makeCondition("fromTempExprId", "CLEAN_CACHE"), null, null, null, false);
	if(UtilValidate.isNotEmpty(cleanCacheList)) {
	    delegator.removeAll(cleanCacheList);
	}
	GenericValue tea = delegator.makeValue("TemporalExpressionAssoc");
    tea.put("fromTempExprId", "CLEAN_CACHE");
    tea.put("toTempExprId", "HOUR_05");
	
    tea.create();
    
    
    GenericValue gv = delegator.makeValue("JobSandbox");
	gv.put("jobId", "CLEAN_CACHE");
	gv.put("jobName", "CLEAN_CACHE");
	gv.put("tempExprId", "CLEAN_CACHE");
	gv.put("serviceName", "gzCleanCache");
	gv.put("runAsUser", "system");
	gv.put("runTime", new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime()));
	gv.put("poolId", "pool");
	gv.put("maxRecurrenceCount", -1L);
	
	gv.create();
} else {
	Debug.log("Servizio CLEAN_CACHE gia\' schedulato, controllare orario");
}



return result;

