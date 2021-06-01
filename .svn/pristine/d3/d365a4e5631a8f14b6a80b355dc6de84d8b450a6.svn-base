package com.mapsengineering.workeffortext.services.rootcopy;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.mapsengineering.workeffortext.services.E;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Generic WorkEffortDataCopy
 *
 */
public abstract class AbstractWorkEffortDataCopy {
    protected static final String DEFAULT_ORGANIZATION_PARTY_ID = "defaultOrganizationPartyId";

    private WorkEffortRootCopyService service;
    private boolean useEnableSnapshot;

    /**
     * 
     * @param service
     */
    public AbstractWorkEffortDataCopy(WorkEffortRootCopyService service) {
        this.service = service;
    }
    
    /**
     * 
     * @param service
     * @param snapshot
     */
    public AbstractWorkEffortDataCopy(WorkEffortRootCopyService service, boolean snapshot) {
        this.service = service;
        this.useEnableSnapshot = snapshot;
    }

    /**
     * copy method
     * @param origWorkEffortId
     * @param newWorkEffortId
     * @param data
     * @return
     * @throws GeneralException
     */
    public abstract Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException;

    /**
     * Execute crud service
     * @param serviceName
     * @param entityName
     * @param operation
     * @param parametersMap
     * @param successMsg
     * @param errorMsg
     * @param throwOnError
     * @param valRef1 for log message
     * @return resultMap
     * @throws GeneralException
     */
    protected Map<String, Object> runSyncCrud(String serviceName, String entityName, String operation, Map<String, ? extends Object> parametersMap, String successMsg, String errorMsg, boolean throwOnError, String valRef1) throws GeneralException {
        return service.runSyncCrud(serviceName, entityName, operation, parametersMap, successMsg, errorMsg, throwOnError, false, valRef1);
    }
    
    /**
     * Execute service
     * @param serviceName
     * @param parametersMap
     * @param successMsg
     * @param errorMsg
     * @param throwOnError
     * @param valRef1 for log message
     * @return resultMap
     * @throws GeneralException
     */
    protected Map<String, Object> runSync(String serviceName, Map<String, ? extends Object> parametersMap, String successMsg, String errorMsg, boolean throwOnError, String valRef1) throws GeneralException {
        return service.runSync(serviceName, parametersMap, successMsg, errorMsg, throwOnError, false, valRef1);
    }

    protected Delegator getDelegator() {
        return service.getDelegator();
    }

    protected boolean getUseCache() {
        return service.getUseCache();
    }

    protected GenericValue getUserLogin() {
        return service.getUserLogin();
    }

    protected Locale getLocale() {
        return service.getLocale();
    }
    
    protected TimeZone getTimeZone() {
        return service.getTimeZone();
    }

    protected LocalDispatcher getDispatcher() {
        return service.getDispatcher();
    }

    public boolean isUseEnableSnapshot() {
        return useEnableSnapshot;
    }

    protected void addLogError(String message, String valRef1) {
        service.addLogError(message, valRef1);
    }

    protected void addLogInfo(String message, String valRef1) {
        service.addLogInfo(message, valRef1);
    }
    
    /**
     * if snapshot = true newWepa.thruDate = originalWepa.thruDate else newWepa.thruDate = newWorkeffort.estimatedCompletionDate
     * @param estimatedCompletionDate
     * @param thruDate
     * @return
     */
    protected Timestamp getThruDate(Timestamp estimatedCompletionDate, Timestamp thruDate) {
        if(isUseEnableSnapshot()) {
            return thruDate;
        }
        return estimatedCompletionDate;
    }

    /**
     * if snapshot = true newWepa.fromDate = originalWepa.fromDate else newWepa.fromDate = newWorkeffort.estimatedStartDate
     * @param estimatedStartDate
     * @param fromDate
     * @return
     */
    protected Timestamp getFromDate(Timestamp estimatedStartDate, Timestamp fromDate) {
        if(isUseEnableSnapshot()) {
            return fromDate;
        }
        return estimatedStartDate;
    }
    
    /**
     * if is snapshot return the isPosted value of original genericValue, else return N
     * @param isPosted
     * @return
     */
    protected String getIsPosted(String isPosted) {
        if(isUseEnableSnapshot()) {
            return isPosted;
        }
        return E.N.name();
    }

    /**
     * @return the service
     */
    public WorkEffortRootCopyService getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(WorkEffortRootCopyService service) {
        this.service = service;
    }
}
