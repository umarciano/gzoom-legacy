package com.mapsengineering.base.services;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.delete.userlogin.E;

/**
 * Generic Service, with execute method and writelog
 *
 */
public abstract class GenericServiceLoop extends GenericService {

	 /**
     * Constructor
     * @param dctx
     * @param context
     */
	public GenericServiceLoop(DispatchContext dctx, Map<String, Object> context, String serviceName, String serviceType, String module) {
		super(dctx, context, serviceName, serviceType, module);
	}

	/**
     * Execute method and writelog
     */
    public void mainLoop() {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();

        try {
            execute();
        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {

            if (UtilValidate.isEmpty(context.get(E.jobLogger.name()))) {
                String jobLogId = delegator.getNextSeqId("JobLog");
                writeLogs(startTimestamp, jobLogId, getServiceParameters());
                getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            }

            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
            getResult().put(ServiceLogger.JOB_LOGGER, getJobLogger());
        }
    }
    
    protected Map<String, Object> getServiceParameters() {
        return null;
    }

    abstract protected void execute() throws Exception;
    
    
    public void manageResult(Map<String, Object> result, String id) throws GeneralException {
        String msg = "";
        setBlockingErrors(getBlockingErrors() + (Long)result.get(ServiceLogger.BLOCKING_ERRORS));
        setRecordElaborated(getRecordElaborated() + (Long)result.get(ServiceLogger.RECORD_ELABORATED));
        if (ServiceUtil.isSuccess(result)) {
            msg = "Successfull for id = " + id + result;
            addLogInfo(msg);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            addLogInfo(msg);
        }
    }
    
    protected void updateList(List<GenericValue> toDeleteList, String param, String newValue) throws GenericEntityException {
    	for (GenericValue toUpdate : toDeleteList) {
    		toUpdate.set(param, newValue);
    		toUpdate.store();
        }
    }
    
    protected void removeList(List<GenericValue> toDeleteList) throws GenericEntityException {
    	for (GenericValue toDelete : toDeleteList) {
            toDelete.remove();
        }
    }
    protected void getListAndRemove(String entityName, EntityCondition entityCondition, String userLoginId) throws GeneralException {
    	List<GenericValue> toDeleteList = findList(entityName, entityCondition, false, userLoginId);
    	removeList(toDeleteList);
    	addLogInfo("Deleted entityName= " + entityName + " with entityCondition= " + entityCondition + "  and size= " + toDeleteList.size());
    }
    
    protected void getListAndUpdate(String entityName, EntityCondition entityCondition, String keyId, String nameValueChange, String valueChange) throws GeneralException {
    	List<GenericValue> toUpdateList = findList(entityName, entityCondition, false, keyId);
    	updateList(toUpdateList, nameValueChange, valueChange);
    	addLogInfo("Update entityName= " + entityName + " with entityCondition= " + entityCondition + "  and size= " + toUpdateList.size());
    }
    
    protected void getListAndUpdate(String entityName, EntityCondition entityCondition, String keyId, List<String> nameValueChangeList, String valueChange) throws GeneralException {
    	List<GenericValue> toUpdateList = findList(entityName, entityCondition, false, keyId);
    	for(String nameValueChange: nameValueChangeList) {
    		updateList(toUpdateList, nameValueChange, valueChange);
    	}
    	addLogInfo("Update entityName= " + entityName + " with entityCondition= " + entityCondition + "  and size= " + toUpdateList.size());
    }  
}
