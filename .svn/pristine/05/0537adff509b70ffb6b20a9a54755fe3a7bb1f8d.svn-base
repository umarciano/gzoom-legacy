package com.mapsengineering.base.services.async;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.util.OfbizServiceContext;

import javolution.util.FastList;

public class AsyncVisitorCleaner {

    private static final String MODULE = AsyncVisitorCleaner.class.getName();
    private static final String PROP_DAYS_BEFORE_DELETE = "AsyncVisitorCleaner.daysBeforeDelete";
    private static final int DEFAULT_DAYS_BEFORE_DELETE = 1;
    private static final long DAY_MILLIS = 1000L * 60L * 60L * 24L;

    private final OfbizServiceContext ctx;
    private Timestamp tsBeforeDelete;

    enum E {
        BaseConfig,Visit,Visitor,thruDate,createdTxStamp,visitorId
    }

    public AsyncVisitorCleaner(OfbizServiceContext ctx) {
        this.ctx = ctx;
        tsBeforeDelete = new java.sql.Timestamp(getMillisBeforeDelete());
    }

    public static Map<String, Object> cleanSrv(DispatchContext dctx, Map<String, Object> context) throws IOException {
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        AsyncVisitorCleaner obj = new AsyncVisitorCleaner(ctx);
        try {
            obj.clean();
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
    	
    	//Get all visit records with thruDate less or equals a certain param date and thruDate not null 
        EntityCondition condition = EntityCondition.makeCondition( //
                EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, tsBeforeDelete),
                EntityCondition.makeCondition(E.thruDate.name(), GenericEntity.NULL_FIELD)
                );
        
        //Delete all records
        List<GenericValue> visitList = ctx.getDelegator().findList(E.Visit.name(), condition, null, null, null, false);
        ctx.getDelegator().removeAll(visitList);  
        
        List<String> visitorIdRemoved = FastList.newInstance();
        //Create a String List with deleted visitorId taken once time 
        for (GenericValue visitRecord : visitList) {
        	if(!visitorIdRemoved.contains(visitRecord.getString(E.visitorId.name())))
        		visitorIdRemoved.add(visitRecord.getString(E.visitorId.name()));
        }
        
        //remove from list the exists visitorid on visit table
        condition = EntityCondition.makeCondition(E.visitorId.name(),EntityOperator.IN,visitorIdRemoved);
        visitList = ctx.getDelegator().findList(E.Visit.name(), condition, null, null, null, false);
        
        for (GenericValue visitRecord : visitList) {
        	if(visitorIdRemoved.contains(visitRecord.getString(E.visitorId.name())))
        		visitorIdRemoved.remove(visitRecord.getString(E.visitorId.name()));
        }
        
        //Delete all visitor with createdtxstamp less or equals a param date and visitorIdRemoved
        condition = EntityCondition.makeCondition( //
                EntityCondition.makeCondition(E.createdTxStamp.name(), EntityOperator.LESS_THAN_EQUAL_TO, tsBeforeDelete),
                EntityCondition.makeCondition(E.visitorId.name(),EntityOperator.IN,visitorIdRemoved)
                );
        
        List<GenericValue> visitorList = ctx.getDelegator().findList(E.Visit.name(), condition, null, null, null, false);
        visitorList =  ctx.getDelegator().findList(E.Visitor.name(), condition, null, null, null, false);
        ctx.getDelegator().removeAll(visitorList);
    }

}
