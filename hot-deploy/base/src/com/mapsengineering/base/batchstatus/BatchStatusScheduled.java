package com.mapsengineering.base.batchstatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.async.AsyncJobManager;
import com.mapsengineering.base.services.async.AsyncJobOfbizService;
import com.mapsengineering.base.util.OfbizServiceContext;

public class BatchStatusScheduled {
	
	private static final String BATCH_STATUS_CHANGE = "batchStatusChange";
	
	/**
	 * servizio batch status schedulato
	 * @param dctx
	 * @param context
	 * @return
	 * @throws IOException
	 */
    public static Map<String, Object> batchStatusScheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
        	List<GenericValue> workEffortTypeList = getWorkEffortTypeList(ctx);
        	if (UtilValidate.isNotEmpty(workEffortTypeList)) {
        		for (GenericValue workEffortType : workEffortTypeList) {
        	        Map<String, Object> serviceParams = ctx.getDctx().makeValidContext(BATCH_STATUS_CHANGE, ModelService.IN_PARAM, ctx);
        	        serviceParams.put(E.workEffortTypeId.name(), workEffortType.getString(E.workEffortTypeId.name()));
        	        runAsyncJob(ctx.getDispatcher(), BATCH_STATUS_CHANGE, serviceParams);
        		}
        	}
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }
    
    /**
     * esegue il servizio di cambio stato
     * @param dispatcher
     * @param serviceName
     * @param serviceParams
     * @throws GeneralException
     */
    private static void runAsyncJob(LocalDispatcher dispatcher, String serviceName, Map<String, Object> serviceParams) throws GeneralException {
        AsyncJobOfbizService job = new AsyncJobOfbizService(dispatcher, serviceName, serviceParams);
        job.getConfig().setKeepAliveTimeout(0L);
        AsyncJobManager.submit(job);
    }
    
    /**
     * ritorna la lista di tipi obiettivi con batch stato attivo
     * @param ctx
     * @return
     * @throws GenericEntityException
     */
    private static List<GenericValue> getWorkEffortTypeList(OfbizServiceContext ctx) throws GenericEntityException {
    	return ctx.getDelegator().findList(E.WorkEffortType.name(), EntityCondition.makeCondition(E.batchStatusActive.name(), E.Y.name()), null, null, null, false);
    }

}
