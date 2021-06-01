package com.mapsengineering.workeffortext.services.print;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.services.async.AsyncJob;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.services.E;

public class MassivePrintAndSendMail extends GenericService {

    public static final String MODULE = MassivePrintAndSendMail.class.getName();
    public static final String SERVICE_NAME = "massivePrintAndSendMail";
    public static final String SERVICE_TYPE = "MASS_PRINT_SEND_MAIL";
    
    public static Map<String, Object> massivePrintAndSendMail(DispatchContext dctx, Map<String, Object> context) {
        MassivePrintAndSendMail obj = new MassivePrintAndSendMail(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    private AsyncJob asyncJob;

    public MassivePrintAndSendMail(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(E.userLogin.name());
        asyncJob = (AsyncJob) context.get("asyncJob");
        setSessionId(asyncJob.getJobId());
    }

    public void mainLoop() {
        String msg;
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        setResult(ServiceUtil.returnSuccess());
        
        try {
            List<String> workEffortRootList = (List<String>) context.get(E.workEffortRootIdList.name());
            
            String enabledSendMail = (String) context.get(E.enabledSendMail.name());
            addLogInfo("Start Massive Print for sessionId " + getSessionId() + " with " + enabledSendMail);
            
            for(String workEffortId : workEffortRootList) {
                if (asyncJob.isInterrupted()) {
                    getResult().putAll(ServiceUtil.returnError("User service interrrupted"));
                    break;
                }
                elaborateWorkEffortRoot(workEffortId);
            }
            
            
        } catch (Exception e) {
            msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            String jobLogId = delegator.getNextSeqId("JobLog");
            writeLogs(startTimestamp, jobLogId);
            
            getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
        }
    }
    
    // TODO
    private void manageResult(Map<String, Object> result, String workEffortId) throws GeneralException {
        String msg = "";
        setBlockingErrors(getBlockingErrors() + (Long) result.get(ServiceLogger.BLOCKING_ERRORS));
        setRecordElaborated(getRecordElaborated() + (Long) result.get(ServiceLogger.RECORD_ELABORATED));
        if (ServiceUtil.isSuccess(result)) {
            msg = "Successfull TODO for workEffortId = " + workEffortId + result;
            addLogInfo(msg);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            addLogInfo(msg);
        }
    }


    private void elaborateWorkEffortRoot(String workEffortRootId) throws GeneralException, IOException {
        // elaborateWorkEffortRoot invocare come servizio transazione 15 minuti
        String serviceName = "executePrintAndSendMail";
        Map<String, Object> serviceContext = getDctx().makeValidContext(serviceName, ModelService.IN_PARAM, context);
        serviceContext.put(E.workEffortRootId.name(), workEffortRootId);
        serviceContext.put(ServiceLogger.SESSION_ID, getSessionId());
        manageResult(dispatcher.runSync(serviceName, serviceContext), workEffortRootId);
    }
 }
