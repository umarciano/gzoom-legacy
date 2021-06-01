package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.find.WorkEffortFindServices;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.OfbizServiceContext;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;

/**
 * CustomBaseEtl with OfbizServiceContext , run executeInTrx and write Log
 * @author dain
 *
 */
public abstract class CustomBaseEtl extends GenericService {

    protected static final String MODULE = CustomBaseEtl.class.getName();

    private final OfbizServiceContext ctx;

    /**
     * Base Constructor
     * @param ctx
     */
    public CustomBaseEtl(OfbizServiceContext ctx) {
        super(ctx.getDctx(), ctx, "customBaseEtl", "CUSTOM_BASE_ETL", MODULE);
        this.ctx = ctx;
    }

    /**
     * Constructor
     * @param ctx
     * @param serviceName
     * @param serviceType
     */
    public CustomBaseEtl(OfbizServiceContext ctx, String serviceName, String serviceType) {
        super(ctx.getDctx(), ctx, serviceName, serviceType, MODULE);
        this.ctx = ctx;
    }

    protected OfbizServiceContext getCtx() {
        return ctx;
    }

    /**
     * Execute service and then write Log
     */
    public void execute() {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        try {
            initialize();
        } catch (Exception e) {
            handleException(e);
        }
        try {
            executeInTrx();
        } catch (Exception e) {
            handleException(e);
        } finally {
            String jobLogId = delegator.getNextSeqId("JobLog");
            executeWriteLogs(startTimestamp, jobLogId);
            Map<String, Object> resultETL = FastMap.newInstance();
            List<Map<String, Object>> resultETLList = FastList.newInstance();
            resultETL.put(ServiceLogger.ENTITY_NAME, getDescriptionEntityName());
            resultETL.put(ServiceLogger.JOB_LOG_ID, jobLogId);
            resultETL.put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            resultETL.put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
            resultETLList.add(resultETL);
            ctx.getResult().put("resultETLList", resultETLList);
        }
    }

    protected void initialize() throws Exception {
    }

    protected abstract void executeWriteLogs(Timestamp startTimestamp, String jobLogId);

    protected void executeInTrx() throws Exception {
        GenericValue jobLogServiceType = findOne("JobLogServiceType", EntityCondition.makeCondition(ServiceLogger.SERVICE_TYPE_ID, getServiceType()), "Found more " + getServiceType(), "No service name found " + getServiceType());
        String description = jobLogServiceType.getString(E.description.name());
        setDescriptionEntityName(description);

        JobLogLog infoInterface = new JobLogLog().initLogCode("StandardImportUiLabels.xml", "DELETE_INTERFACE", null, getLocale());
        addLogInfo(infoInterface.getLogCode(), infoInterface.getLogMessage(), null, null, null);
        new TransactionRunner(MODULE, ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT, new TransactionItem() {
            @Override
            public void run() throws Exception {
                deleteAllInterfaceData();
            }
        }).execute().rethrow();

        JobLogLog importEtl = new JobLogLog().initLogCode("StandardImportUiLabels.xml", "IMPORT_ETL", null, getLocale());
        addLogInfo(importEtl.getLogCode(), importEtl.getLogMessage(), null, null, null);
        doImportFromExt();

        JobLogLog importInterface = new JobLogLog().initLogCode("StandardImportUiLabels.xml", "IMPORT_INTERFACE", null, getLocale());
        addLogInfo(importInterface.getLogCode(), importInterface.getLogMessage(), null, null, null);
        doImportFromInterface();

        postImportFromInterface();
    }

    /**
     * Manage the transaction inside
     * @throws Exception
     */
    protected abstract void postImportFromInterface() throws Exception;

    /**
     * Manage the transaction inside
     * @throws Exception
     */
    protected abstract void doImportFromExt() throws Exception;

    protected void doImportFromInterface() throws Exception {
        String entityListToImport = getEntityListToImport();
        ctx.put(E.entityListToImport.name(), entityListToImport);   
        WorkEffortFindServices workEffortFindServices = new WorkEffortFindServices(getDelegator(), getDispatcher());
        ctx.put(E.defaultOrganizationPartyId.name(), workEffortFindServices.getOrganizationId(getUserLogin(), false));        
        ctx.getResult().putAll(ImportManager.doImportSrv(ctx.getDctx(), ctx));
    }

    protected abstract String getEntityListToImport();

    protected abstract void deleteAllInterfaceData() throws GenericEntityException;

    protected void deleteInterfaceData(String entityName) throws GenericEntityException {
        ctx.getDelegator().removeByAnd(entityName);
    }

    protected void handleException(Exception e) {
        Debug.logError(e, MODULE);
        ctx.getResult().putAll(ServiceUtil.returnError(e.getMessage()));
    }
}
