package com.mapsengineering.workeffortext.services.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.QueryExecutorService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.util.E;
import com.mapsengineering.workeffortext.util.WorkEffortTypeStatusParamsEvaluator;

import bsh.EvalError;

/**
 * CheckHasMandatoryTransEmptyService
 */
public class CheckHasQueryConfigErrorService extends GenericService {

    public static final String MODULE = CheckHasQueryConfigErrorService.class.getName();
    private static final String SERVICE_NAME = "checkWorkEffortStatusHasQueryConfigError";
    private static final String SERVICE_TYPE = null;

    private static final String WorkeffortExtErrorLabels = "WorkeffortExtErrorLabels";
    private static final String StandardImportUiLabels = "StandardImportUiLabels";

    
   /**
     * CheckHasQueryConfigErrorService
     */
    public static Map<String, Object> checkHasQueryConfigError(DispatchContext dctx, Map<String, Object> context) {
        CheckHasQueryConfigErrorService obj = new CheckHasQueryConfigErrorService(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    private boolean hasQueryConfigError;
    private boolean hasQueryConfigWarning;

    /**
     * Constructor
     */
    public CheckHasQueryConfigErrorService(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }

    /**
     * Main loop <br/>
     * La query estrae i record per cui esiste un problema che si oppone al cambio stato
     * Ogni record corrisponde ad un problema
     * Per ogni problema viene scritto un messaggio.
     * I diversi messaggi sono concatenati e restituiti al servizio che lo invoca
     * Non sono messaggi di errore, poiche' gli errori sono lasciati a problemi della scrittura della query
     */
    public void mainLoop() {
        hasQueryConfigError = false;
        hasQueryConfigWarning = false;
        String queryCode = null;
        try {
            String statusCheck = getStatusCheck();
            if(UtilValidate.isNotEmpty(statusCheck)) {
                EntityCondition condition = EntityCondition.makeCondition(getQueryConfigConditions(statusCheck));
                List<GenericValue> queryList = findList(E.QueryConfig.name(), condition, false, null);
                String msg = "Found " + queryList.size() + " QueryConfig to execute for " + condition;
                addLogInfo(msg);
                for (GenericValue query : queryList) {
                    queryCode = query.getString(E.queryCode.name());
                    executeQuery(query);
                }
            }
        } catch (Exception e) {
            String msg = "Error in executing queryConfig: " + e.getMessage();
            addLogError(e, msg);
            String returnErrorMessage = UtilProperties.getMessage(WorkeffortExtErrorLabels, "QUERY_CONFIG_ERROR", UtilMisc.toMap("queryCode", queryCode , "errorMessage", e.getMessage()), getLocale());
            setResult(ServiceUtil.returnError(returnErrorMessage));
        } finally {
            getResult().put("hasQueryConfigError", hasQueryConfigError);
            getResult().put("hasQueryConfigWarning", hasQueryConfigWarning);
        }
    }
    
    /**
     * @param statusCheck 
     * @param getStatusId() per recuperare il queryCode
     * @return ritorna le condizioni di ricerca delle query
     * @throws GeneralException 
     * @throws EvalError 
     */
    private List<EntityCondition> getQueryConfigConditions(String statusCheck) throws EvalError, GeneralException {
        List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
        conditionList.add(EntityCondition.makeCondition(E.queryType.name(), E.R.name()));
        conditionList.add(EntityCondition.makeCondition(E.queryActive.name(), E.Y.name()));
        conditionList.add(EntityCondition.makeCondition(E.queryCode.name(), EntityOperator.LIKE, "%" + statusCheck + "%"));
        return conditionList;
    }
    
    private String getStatusCheck() throws EvalError, GeneralException {
        GenericValue workEffort = findOne(E.WorkEffort.name(), EntityCondition.makeCondition(E.workEffortId.name(), getWorkEffortId()), "", "");
        WorkEffortTypeStatusParamsEvaluator paramsStatusEvaluator = new WorkEffortTypeStatusParamsEvaluator(context, delegator);
        paramsStatusEvaluator.evaluateParams(workEffort.getString(E.workEffortTypeId.name()), getStatusId(), true);
        return (String)context.get(E.statusCheck.name());
    }

    /**
     * esegue la query, imposta eventuale jobLogId nelle query di POST, colleziona i messaggi
     * @param query
     * @param isPost
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void executeQuery(GenericValue query) throws GenericServiceException {
        List<String> messageList = new ArrayList<String>();
        List<String> warningMessageList = new ArrayList<String>();
        
        Map<String, Object> serviceContext = getDctx().makeValidContext(E.queryExecutorService.name(), ModelService.IN_PARAM, context);
        serviceContext.put(E.queryId.name(), query.getString(E.queryId.name()));
        serviceContext.put(E.cond0Info.name(), getWorkEffortId());
        Map<String, Object> queryCodeLogParameters = UtilMisc.toMap(E.queryCode.name(), query.getString(E.queryCode.name()));
        JobLogLog execStart = new JobLogLog().initLogCode(StandardImportUiLabels, "QUERY_EXCEC", queryCodeLogParameters, getLocale());
        addLogInfo(execStart.getLogCode(), execStart.getLogMessage(), getWorkEffortId(), StandardImportUiLabels, execStart.getParametersJSON());
        
        Map<String, Object> resultMap = dispatcher.runSync(E.queryExecutorService.name(), serviceContext);
        if (! ServiceUtil.isSuccess(resultMap)) {
            JobLogLog execError = new JobLogLog().initLogCode(StandardImportUiLabels, "QUERY_EXCEC_ERROR", queryCodeLogParameters, getLocale());
            String errorMsg = execError.getLogMessage() + ": " + resultMap.get(ModelService.ERROR_MESSAGE);
            throw new GenericServiceException(errorMsg);
        } else {
            JobLogLog execSuccess = new JobLogLog().initLogCode(StandardImportUiLabels, "QUERY_EXCEC_SUCCESS", queryCodeLogParameters, getLocale());
            addLogInfo(execSuccess.getLogCode(), execSuccess.getLogMessage(), getWorkEffortId(), StandardImportUiLabels, execSuccess.getParametersJSON());
            List<Map<String, Object>> resultList = (List<Map<String, Object>>)resultMap.get(QueryExecutorService.RESULT_LIST);
            if (UtilValidate.isNotEmpty(resultList)) {
                for(Map<String, Object> messageMap : resultList) {
                    String message = (String)messageMap.get(QueryExecutorService.MESSAGE);
                    if(!isPrimaryLang() && UtilValidate.isNotEmpty((String)messageMap.get(QueryExecutorService.MESSAGE_LANG))) {
                        message = (String)messageMap.get(QueryExecutorService.MESSAGE_LANG);
                    }
                    if (UtilValidate.isNotEmpty(message)) {
                        hasQueryConfigError = true;
                        messageList.add(message);
                    }
                    String warningMessage = (String)messageMap.get(QueryExecutorService.WARNING_MESSAGE);
                    if(!isPrimaryLang() && UtilValidate.isNotEmpty((String)messageMap.get(QueryExecutorService.WARNING_MESSAGE_LANG))) {
                        warningMessage = (String)messageMap.get(QueryExecutorService.WARNING_MESSAGE_LANG);
                    }
                    if (UtilValidate.isNotEmpty(warningMessage)) {
                        hasQueryConfigWarning = true;
                        warningMessageList.add(warningMessage);
                    }
                    
                }
                
            }
        }
        if (hasQueryConfigError) {
            String errMsg = "<br>" + StringUtil.join(messageList, "<br>");
            getResult().put("errorMsg", errMsg);
        }
        if (hasQueryConfigWarning) {
            String warningMsg = "<br>" + StringUtil.join(warningMessageList, "<br>");
            getResult().put("warningMsg", warningMsg);
        }
    }

    private String getStatusId() {
        return (String)context.get(E.statusId.name());
    }

    private String getWorkEffortId() {
        return (String)context.get(E.workEffortId.name());
    }
}
