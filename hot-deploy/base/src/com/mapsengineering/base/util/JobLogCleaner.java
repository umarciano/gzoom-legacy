package com.mapsengineering.base.util;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class JobLogCleaner {

    private static final String MODULE = JobLogCleaner.class.getName();
    private static final String PROP_DAYS_BEFORE_DELETE = "JobLogCleaner.daysBeforeDelete";
    private static final int DEFAULT_DAYS_BEFORE_DELETE = 15;

    private static final long DAY_MILLIS = 1000L * 60L * 60L * 24L;

    private final Delegator delegator;
    private Timestamp tsBeforeDelete;

    enum E {
        BaseConfig, JobLog, JobLogLog, JobLogJobExecParams, jobLogId, logDate
    }

    public JobLogCleaner(Delegator delegator) {
        this.delegator = delegator;
        tsBeforeDelete = null;
        loadConfig();
    }

    public static Map<String, Object> cleanSrv(DispatchContext dctx, Map<String, Object> context) {
        JobLogCleaner obj = new JobLogCleaner(dctx.getDelegator());
        try {
            obj.clean();
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
            return ServiceUtil.returnError(e.getMessage());
        }
        return ServiceUtil.returnSuccess();
    }

    public void clean() throws GenericEntityException {
        if (tsBeforeDelete != null) {
            EntityCondition condition = EntityCondition.makeCondition(E.logDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, tsBeforeDelete);
            EntityListIterator it = delegator.find(E.JobLog.name(), condition, null, UtilMisc.toSet(E.jobLogId.name()), UtilMisc.toList(E.jobLogId.name()), null);
            try {
                GenericValue gv;
                while ((gv = it.next()) != null) {
                    String jobLogId = gv.getString(E.jobLogId.name());
                    cleanJobLogRelated(E.JobLogJobExecParams.name(), jobLogId);
                    cleanJobLogRelated(E.JobLogLog.name(), jobLogId);
                    gv.remove();
                }
            } finally {
                it.close();
            }
        }
    }

    public int cleanJobLogRelated(String entityName, String jobLogId) throws GenericEntityException {
        EntityCondition condition = EntityCondition.makeCondition(E.jobLogId.name(), jobLogId);
        return delegator.removeByCondition(entityName, condition);
    }

    private void loadConfig() {
        long milisBeforeDelete = DAY_MILLIS * getPropertyInt(PROP_DAYS_BEFORE_DELETE, DEFAULT_DAYS_BEFORE_DELETE);
        if (milisBeforeDelete > 0L) {
            milisBeforeDelete = System.currentTimeMillis() - milisBeforeDelete;
            tsBeforeDelete = new java.sql.Timestamp(milisBeforeDelete);
        }
    }

    private Integer getPropertyInt(String propName, Integer defValue) {
        try {
            String str = UtilProperties.getPropertyValue(E.BaseConfig.name(), propName);
            return str == null ? defValue : Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }
}
