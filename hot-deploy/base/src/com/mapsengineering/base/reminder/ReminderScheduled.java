package com.mapsengineering.base.reminder;


import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.services.async.AsyncJobManager;
import com.mapsengineering.base.services.async.AsyncJobOfbizService;
import com.mapsengineering.base.util.OfbizServiceContext;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;


public class ReminderScheduled {

    private static final String REMINDER = "reminder";
    private static final String REMINDER_SCADENZA = "REMINDER_SCADENZA";
    private static final String REMINDER_PERIODO = "REMINDER_PERIODO";
    private static final String REMINDER_STATO = "REMINDER_STATO";
    private static final String REMINDER_ASS_OBI = "REMINDER_ASS_OBI";
    private static final String REMINDER_ASS_OBI_1 = "REMINDER_ASS_OBI_1";
    private static final String REMINDER_ASS_OBI_2 = "REMINDER_ASS_OBI_2";
    private static final String REMINDER_VAL_DIP = "REMINDER_VAL_DIP";
    private static final String REMINDER_VAL_DIP_1 = "REMINDER_VAL_DIP_1";
    private static final String REMINDER_VAL_DIP_2 = "REMINDER_VAL_DIP_2";
    private static final String MODULE = ReminderScheduled.class.getName();
  
    /**
     * Scheduled service reminder for scadenza obiettivo
     * @param dctx DispatchContext
     * @param context Map
     * @return result Map
     * @throws IOException 
     */
    public static Map<String, Object> reminderScadObiScheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
        	String path = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.pathGzoom2");
            getWorkEffortTypeAndRunAsyncJob(ctx, context, REMINDER_SCADENZA, path + "/reminder-expiry");
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }
    
    
    /**
     * Scheduled service reminder for periodo
     * @param dctx DispatchContext
     * @param context Map
     * @return result Map
     * @throws IOException 
     */
    public static Map<String, Object> reminderPeriodoScheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
        	String path = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.pathGzoom2");
        	context.put(E.batchReminder.name(), true);
            getWorkEffortTypeAndRunAsyncJob(ctx, context, REMINDER_PERIODO, path + "/reminder-period");
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }
    
    /**
     * Scheduled service reminder for status
     * @param dctx
     * @param context
     * @return
     * @throws IOException
     */
    public static Map<String, Object> reminderStatoScheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
        	String path = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.pathGzoom2");
        	context.put(E.batchReminder.name(), true);
            getWorkEffortTypeAndRunAsyncJob(ctx, context, REMINDER_STATO, path + "/reminder-stato");
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }
    
    /**
     * Scheduled service for ass obi
     * @param dctx
     * @param context
     * @return
     * @throws IOException
     */
    public static Map<String, Object> reminderAssObiScheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
        	String path = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.pathGzoom2");
        	// Per i servizi di CMTorino, la data_soll viene aggiornata nel servizio custom
            // context.put(E.batchReminder.name(), true);
            getWorkEffortTypeAndRunAsyncJob(ctx, context, REMINDER_ASS_OBI, path + "/reminder-assobi");
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }
    
    /**
     * Scheduled service for ass obi
     * @param dctx
     * @param context
     * @return
     * @throws IOException
     */
    public static Map<String, Object> reminderAssObi2Scheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
            String path = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.pathGzoom2");
            // Per i servizi di CMTorino, la data_soll viene aggiornata nel servizio custom
            // context.put(E.batchReminder.name(), true);
            getWorkEffortTypeAndRunAsyncJob(ctx, context, REMINDER_ASS_OBI_2, path + "/reminder-assobi-2");
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }
    
    /**
     * Scheduled service for ass obi
     * @param dctx
     * @param context
     * @return
     * @throws IOException
     */
    public static Map<String, Object> reminderAssObi1Scheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
            String path = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.pathGzoom2");
            // Per i servizi di CMTorino, la data_soll viene aggiornata nel servizio custom
            // context.put(E.batchReminder.name(), true);
            getWorkEffortTypeAndRunAsyncJob(ctx, context, REMINDER_ASS_OBI_1, path + "/reminder-assobi-1");
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }
    
    /**
     * Scheduled service for val dip
     * @param dctx
     * @param context
     * @return
     * @throws IOException
     */
    public static Map<String, Object> reminderValDipScheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
        	String path = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.pathGzoom2");
        	// Per i servizi di CMTorino, la data_soll viene aggiornata nel servizio custom
            // context.put(E.batchReminder.name(), true);
            getWorkEffortTypeAndRunAsyncJob(ctx, context, REMINDER_VAL_DIP, path + "/reminder-valdip");
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }
    
    /**
     * Scheduled service for val dip 1
     * @param dctx
     * @param context
     * @return
     * @throws IOException
     */
    public static Map<String, Object> reminderValDip1Scheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
        	String path = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.pathGzoom2");
        	// Per i servizi di CMTorino, la data_soll viene aggiornata nel servizio custom
            // context.put(E.batchReminder.name(), true);
            getWorkEffortTypeAndRunAsyncJob(ctx, context, REMINDER_VAL_DIP_1, path + "/reminder-valdip-1");
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }
    
    /**
     * Scheduled service for val dip 2
     * @param dctx
     * @param context
     * @return
     * @throws IOException
     */
    public static Map<String, Object> reminderValDip2Scheduled(DispatchContext dctx, Map<String, Object> context) throws IOException {
        Map<String, Object> serviceMap = ServiceUtil.returnSuccess();
        OfbizServiceContext ctx = new OfbizServiceContext(dctx, context);
        try {
        	String path = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.pathGzoom2");
        	// Per i servizi di CMTorino, la data_soll viene aggiornata nel servizio custom
            // context.put(E.batchReminder.name(), true);
            getWorkEffortTypeAndRunAsyncJob(ctx, context, REMINDER_VAL_DIP_2, path + "/reminder-valdip-2");
        } catch (GeneralException e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
        return serviceMap;
    }    
    
    private static void getWorkEffortTypeAndRunAsyncJob(final OfbizServiceContext ctx, Map<String, Object> context, final String name, String url) throws GeneralException {
    	String serviceNewApplication = UtilProperties.getPropertyValue("BaseConfig.properties", "ReminderScheduled.serviceNewApplication");
    	if ("Y".equals(serviceNewApplication)) { 
            Debug.log("***URL="+url);
    		callUrlGzoom2(url);
    		return;
    	}
        
    	
        // TODO set locale it_IT
    	Locale locale = Locale.ITALY;
        ctx.put(ServiceLogger.LOCALE, locale);
        
        final Map<String, Object> serviceParams = ctx.getDctx().makeValidContext(REMINDER, ModelService.IN_PARAM, ctx);
        serviceParams.put(E.queryReminder.name(), ReminderReportContentIdEnum.getQuery(name));
        serviceParams.put(E.reportContentId.name(), name);
        
        try {
            new TransactionRunner(MODULE, new TransactionItem() {
                @Override
                public void run() throws Exception {
                    List<String> listWorkEffortTypeId = getAllWorkEffortTypeReminderActive(ctx.getDelegator(), name);
                    Debug.log("listWorkEffortTypeId " + listWorkEffortTypeId);

                    for(String workEffortTypeId: listWorkEffortTypeId) {
                        Debug.log("workEffortTypeId " + workEffortTypeId);

                        serviceParams.put(E.workEffortTypeId.name(), workEffortTypeId); 
                        runAsyncJob(ctx.getDispatcher(), REMINDER, serviceParams);
                    }
                }
            }).execute().rethrow();
        } catch (Exception e) {
            e.printStackTrace();
        }
       
    }
    
    public static String callUrlGzoom2(String url) {
        String result = "";
        GetMethod get = null;
        HttpClient client = new HttpClient(new SimpleHttpConnectionManager());
        client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
        
        try {            
            get = new GetMethod(url);
            get.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            get.setFollowRedirects(true);
            client.executeMethod(get);
            
        } catch (Exception e) {
            Debug.log("################ ERROR Exception: " + e.getMessage());
        } finally {
            if (get != null) {
                get.releaseConnection();
            }
        }
        
        return result;
    }
    
    private static void runAsyncJob(LocalDispatcher dispatcher, String serviceName, Map<String, Object> serviceParams) throws GeneralException {
        // set AsyncJobOfbizService
        AsyncJobOfbizService job = new AsyncJobOfbizService(dispatcher, serviceName, serviceParams);
        job.getConfig().setKeepAliveTimeout(0L);
        AsyncJobManager.submit(job);
    }

    /**
     * Prendo tutti i workEffortType che hanno la stampa reportContentId 
     * attivata e hanno il flag reminderActive attivo
     * @param delegator
     * @param reportContentId
     * @return
     * @throws GenericEntityException
     */
    private static List<String> getAllWorkEffortTypeReminderActive(Delegator delegator, String reportContentId) throws GenericEntityException {
        EntityCondition filert = EntityCondition.makeCondition(EntityCondition.makeCondition(E.contentId.name(), reportContentId), 
                        EntityCondition.makeCondition(E.reminderActive.name(), E.Y.name()));
        return EntityUtil.getFieldListFromEntityList(delegator.findList(E.WorkEffortTypeContentReminder.name(), filert, null, null, null, false), E.workEffortTypeId.name(), false);
    }

}
