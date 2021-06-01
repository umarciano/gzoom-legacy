package com.mapsengineering.base.reminder;

import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.services.async.AsyncJobManager;
import com.mapsengineering.base.services.async.AsyncJobOfbizService;
import com.mapsengineering.base.util.JobLogger;

public class ReminderPrint extends GenericService {
    
    public static final String MODULE = ReminderPrint.class.getName();
    public static final String SERVICE_NAME = "ReminderPrint";
    public static final String SERVICE_TYPE = "REMINDER_PRINT";
    private static final String REMINDER = "reminder";
    
    public static Map<String, Object> reminderPrint(DispatchContext dctx, Map<String, Object> context) {
        ReminderPrint obj = new ReminderPrint(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }
    
    
    public ReminderPrint(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
    }
    
    /**
     * 
     */
    public void mainLoop() {
        try {
            
            String query = ReminderReportContentIdEnum.getQuery((String)context.get(E.reportContentId.name()));
            
            Map<String, Object> serviceParams = getDctx().makeValidContext(REMINDER, ModelService.IN_PARAM, context);
            serviceParams.put(E.queryReminder.name(), query);
            
            // set AsyncJobOfbizService
            AsyncJobOfbizService job = new AsyncJobOfbizService(getDctx().getDispatcher(), REMINDER, serviceParams);
            job.getConfig().setKeepAliveTimeout(0L);
            AsyncJobManager.submit(job);
            
            getResult().put(ServiceLogger.SESSION_ID, job.getJobId());
            
        } catch (GeneralException e) {
            setResult(ServiceUtil.returnError(e.getMessage()));
        } 
        
    }
    
   


}


