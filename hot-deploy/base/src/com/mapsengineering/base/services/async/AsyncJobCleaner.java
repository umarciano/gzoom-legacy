package com.mapsengineering.base.services.async;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.util.OfbizServiceContext;

public class AsyncJobCleaner {

    private static final String MODULE = AsyncJobCleaner.class.getName();
    private static final String PROP_DAYS_BEFORE_DELETE = "AsyncJobCleaner.daysBeforeDelete";
    private static final int DEFAULT_DAYS_BEFORE_DELETE = 1;
    private static final long DAY_MILLIS = 1000L * 60L * 60L * 24L;

    private final OfbizServiceContext ctx;
    private Timestamp tsBeforeDelete;

    enum E {
        BaseConfig, Content, DataResource, CommEventContentAssoc, ReportActivity, CommunicationEvent, contentTypeId, contentId, dataResourceId, 
        createdDate, objectInfo, communicationEventId, completedStamp, activityId, delContentDataResource
    }

    public AsyncJobCleaner(OfbizServiceContext ctx) {
        this.ctx = ctx;
        tsBeforeDelete = new java.sql.Timestamp(getMillisBeforeDelete());
    }

    public static Map<String, Object> cleanSrv(DispatchContext dctx, Map<String, Object> context) throws IOException {
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        AsyncJobCleaner obj = new AsyncJobCleaner(ctx);
        try {
            obj.clean();
            obj.cleanActivity();
            return ServiceUtil.returnSuccess();
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            ctx.close();
        }
    }

    public static long getMillisBeforeDelete() {
        long milisBeforeDelete = AsyncJobUtil.getInteger(UtilProperties.getPropertyValue(E.BaseConfig.name(), PROP_DAYS_BEFORE_DELETE), DEFAULT_DAYS_BEFORE_DELETE);
        if (milisBeforeDelete <= 0L) {
            milisBeforeDelete = DEFAULT_DAYS_BEFORE_DELETE;
        }
        return System.currentTimeMillis() - (milisBeforeDelete * DAY_MILLIS);
    }

    public void clean() throws GeneralException {
        EntityCondition condition = EntityCondition.makeCondition( //
                EntityCondition.makeCondition(E.contentTypeId.name(), AsyncJobUtil.CONTENT_TYPE_TMP_ENCLOSE), //
                EntityCondition.makeCondition(E.createdDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, tsBeforeDelete) //
                );
        List<String> orderBy = UtilMisc.toList(E.createdDate.name(), E.contentId.name());
        EntityListIterator it = ctx.getDelegator().find(E.Content.name(), condition, null, null, orderBy, null);
        try {
            GenericValue gv;
            while ((gv = it.next()) != null) {
                removeContentAndRelated(gv);
            }
        } finally {
            it.close();
        }
    }
    
    public void cleanActivity() throws GeneralException {
        EntityCondition condition = EntityCondition.makeCondition( EntityCondition.makeCondition(E.completedStamp.name(), EntityOperator.LESS_THAN_EQUAL_TO, tsBeforeDelete) );
        List<GenericValue> list = ctx.getDelegator().findList(E.ReportActivity.name(), condition, null, null, null, false);
        ctx.getDelegator().removeAll(list);  
    }    
    
    private boolean removeContentAndRelated(GenericValue content) throws GeneralException {
        String serviceName;
        Map<String, Object> serviceContext;
        Map<String, Object> serviceResult;
        String contentId = (String)content.get(E.contentId.name());
        
        // removeCommEventContentAssoc
        List<GenericValue> commEventContentAssList = getCommEventContentAssocList((String)content.get(E.contentId.name()));
        serviceName = "deleteCommunicationEventWorkEffort";
        serviceContext = ctx.getDctx().makeValidContext(serviceName, "IN", ctx);
        serviceContext.put(E.delContentDataResource.name(), "N");
        
        for (GenericValue gv: commEventContentAssList) {
            serviceContext.put(E.communicationEventId.name(), gv.get(E.communicationEventId.name()));
            serviceResult = ctx.getDispatcher().runSync(serviceName, serviceContext);
            if (!ServiceUtil.isSuccess(serviceResult)) {
                return false;
            }
        }
        
        
        GenericValue dataResource = content.getRelatedOne(E.DataResource.name());

        serviceName = "removeContent";
        serviceContext = ctx.getDctx().makeValidContext(serviceName, "IN", ctx);
        serviceContext.put(E.contentId.name(), content.get(E.contentId.name()));
        serviceResult = ctx.getDispatcher().runSync(serviceName, serviceContext);
        if (!ServiceUtil.isSuccess(serviceResult)) {
            return false;
        }

        serviceName = "removeDataResource";
        serviceContext = ctx.getDctx().makeValidContext(serviceName, "IN", ctx);
        serviceContext.put(E.dataResourceId.name(), dataResource.get(E.dataResourceId.name()));
        serviceResult = ctx.getDispatcher().runSync(serviceName, serviceContext);
        if (!ServiceUtil.isSuccess(serviceResult)) {
            return false;
        }

        removeFile(dataResource.getString(E.objectInfo.name()));
        removeLinkedAsyncJob(contentId);

        return true;
    }

    private void removeFile(String filePath) {
    	if (filePath == null) 
    		return;
    	File file = new File(filePath);
        if (file.exists()) {
            if (!file.delete()) {
                Debug.logWarning("The file could not be deleted: " + filePath, MODULE);
            }
        } else {
            Debug.logWarning("The file does not exists: " + filePath, MODULE);
        }
    }

    private void removeLinkedAsyncJob(String contentId) throws GeneralException {
        while (true) {
            AsyncJob job = AsyncJobManager.findByResultKey(E.contentId.name(), contentId);
            if (job == null) {
                break;
            }
            job.remove();
        }
    }
    
    private List<GenericValue> getCommEventContentAssocList(String contentId) throws GeneralException {       
        EntityCondition condition = EntityCondition.makeCondition(E.contentId.name(), contentId);
        return ctx.getDelegator().findList(E.CommEventContentAssoc.name(), condition, null, null, null, false);
    }    
}
