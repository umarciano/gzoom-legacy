package com.mapsengineering.base.standardimport.common;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.util.WeRootInterfaceContext;

/**
 * Base WorkEffort Interface with Dates
 *
 */
public abstract class WeBaseDateInterfaceTakeOverService extends WeBaseInterfaceTakeOverService {

    public static final String MODULE = WeBaseDateInterfaceTakeOverService.class.getName();

    private Timestamp estimatedStartDate;
    private Timestamp estimatedCompletionDate;
    private String operationType;

    @Override
    /**
     * Esegue importazione record esterno
     */
    public abstract void doImport() throws GeneralException;

    protected void manageResult(Map<String, Object> result, String entityName, Map<String, Object> keys) throws GeneralException {
        String msg = "";
        if (!ServiceUtil.isError(result)) {
            msg = "successfull created/updated value with id " + keys;
            addLogInfo(msg);
            setFromDateFromResult(result);
            GenericValue workEffort = getManager().getDelegator().findOne(entityName, keys, false);
            setLocalValue(workEffort);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            addLogInfo(msg);
        }
    }
    
    protected void setWithWeRootInterfaceContext(WeRootInterfaceContext weRootInterfaceContext){
    	 estimatedStartDate = weRootInterfaceContext.getEstimatedStartDate();
         estimatedCompletionDate = weRootInterfaceContext.getEstimatedCompletionDate();
         operationType = weRootInterfaceContext.getOperationType();
    }

    private void setFromDateFromResult(Map<String, Object> result) {
        if (UtilValidate.isNotEmpty(result.get("id"))) {
            Map<String, Object> resultId = UtilMisc.toMap(result.get("id"));
            if (UtilValidate.isNotEmpty(resultId)) {
                setEstimatedStartDate((Timestamp)resultId.get(E.fromDate.name()));
            }
        }
    }

    /**
     * Override ordinamento chiavi per i log
     * @parmas msg, log message
     */
    public abstract void addLogInfo(String msg);

    /**
     * @return the estimatedCompletionDate
     */
    public Timestamp getEstimatedCompletionDate() {
        return estimatedCompletionDate;
    }

    /**
     * @param estimatedCompletionDate the estimatedCompletionDate to set
     */
    private void setEstimatedCompletionDate(Timestamp estimatedCompletionDate) {
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    /**
     * @return the estimatedStartDate
     */
    public Timestamp getEstimatedStartDate() {
        return estimatedStartDate;
    }

    /**
     * @param estimatedStartDate the estimatedStartDate to set
     */
    private void setEstimatedStartDate(Timestamp estimatedStartDate) {
        this.estimatedStartDate = estimatedStartDate;
    }

    /**
     * @return the operationType
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * @param operationType the operationType to set
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
