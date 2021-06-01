package com.mapsengineering.workeffortext.services.rootcopy;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.util.WorkEffortTypeStatusParamsEvaluator;

/**
 * Service for copy and snaphot
 *
 */
public class WorkEffortRootDuplicateService extends GenericService {
    public static final String MODULE = WorkEffortRootDuplicateService.class.getName();
    public static final String SERVICE_NAME = "workEffortRootDuplicateService";
    public static final String SERVICE_TYPE_ID = "WRK_ROOT_DUPL";

    private String duplicateAdmit;

    /**
     * Create workEffort and other data
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> workEffortRootDuplicateService(DispatchContext dctx, Map<String, Object> context) {
        WorkEffortRootDuplicateService obj = new WorkEffortRootDuplicateService(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public WorkEffortRootDuplicateService(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE_ID, MODULE);
        setUseCache(false);
    }

    /**
     * Main service
     */
    public void mainLoop() {
        Map<String, Object> serviceResult = null;
        String msg = "Starting WorkEffort Root Duplicate";
        addLogInfo(msg, null);

        String workEffortIdRoot = (String)context.get("workEffortIdRoot");

        try {

            GenericValue workEffortOriginal = findOne(E.WorkEffort.name(), EntityCondition.makeCondition(E.workEffortId.name(), workEffortIdRoot), "Found more WorkEffort with id = " + workEffortIdRoot, "No WorkEffort with id = " + workEffortIdRoot);
            WorkEffortTypeStatusParamsEvaluator paramsStatusEvaluator = new WorkEffortTypeStatusParamsEvaluator(context, delegator);
            paramsStatusEvaluator.evaluateParams(workEffortOriginal.getString(E.workEffortTypeId.name()), workEffortOriginal.getString(E.currentStatusId.name()), true);
            duplicateAdmit = (String)context.get(E.duplicateAdmit.name());

            addLogInfo("Elaborating workEffortRootId = " + workEffortIdRoot + " with duplicateAdmit = " + duplicateAdmit);

            Map<String, Object> serviceMap = makeValidServiceMap(workEffortIdRoot, workEffortOriginal);
            if (ServiceTypeEnum.SNAPSHOT.name().equals(duplicateAdmit)) {
                serviceMap.put(WorkEffortRootCopyService.SNAPSHOT, E.Y.name());
                String snapShotDescription = UtilProperties.getPropertyValue("BaseConfig", "workEffortRootDuplicate.snapShotDescription", workEffortOriginal.getString(E.workEffortName.name()));
                serviceMap.put(E.snapShotDescription.name(), snapShotDescription);

                serviceResult = runSync(E.workEffortSnapshotService.name(), serviceMap, "Finished WorkEffort Duplicate", "Finished WorkEffort Duplicate", false);
            } else {
                if (ServiceTypeEnum.COPY.name().equals(duplicateAdmit)) {
                    GenericValue workEffortTypeAssoc = findOne(E.WorkEffortType.name(), EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortOriginal.getString(E.workEffortTypeId.name())), "", "No childTemplateId with id = " + EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortOriginal.getString(E.workEffortTypeId.name())));
                    serviceMap.put(E.workEffortTypeIdTo.name(), workEffortTypeAssoc.getString(E.childTemplateId.name()));
                }

                serviceResult = runSync(E.workEffortRootCopyService.name(), serviceMap, "Finished WorkEffort Duplicate", "Finished WorkEffort Duplicate", false);
            }
        } catch (Exception e) {
            addLogError(e, "Errore: ");
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            manageResult(serviceResult, "Finished workEffortRootId = " + workEffortIdRoot + " with duplicateAdmit = " + duplicateAdmit, "");
        }
    }
    
    private Map<String, Object> makeValidServiceMap(String workEffortIdRoot, GenericValue workEffortOriginal) {
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.userLogin.name(), userLogin);

        serviceMap.put(ServiceLogger.LOCALE, context.get(ServiceLogger.LOCALE));
        serviceMap.put(ServiceLogger.TIME_ZONE, context.get(ServiceLogger.TIME_ZONE));
        serviceMap.put(E.workEffortId.name(), workEffortIdRoot);
        serviceMap.put(WorkEffortRootCopyService.ORGANIZATION_PARTY_ID, workEffortOriginal.getString(E.organizationId.name()));
        serviceMap.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.N.name());
        serviceMap.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.Y.name());
        serviceMap.put(WorkEffortRootCopyService.CHECK_EXISTING, E.N.name());
        serviceMap.put(E.workEffortTypeIdFrom.name(), workEffortOriginal.getString(E.workEffortTypeId.name()));
        serviceMap.put(E.workEffortTypeIdTo.name(), workEffortOriginal.getString(E.workEffortTypeId.name()));
        serviceMap.put(E.duplicateAdmit.name(), duplicateAdmit);
        serviceMap.put(E.estimatedStartDateFrom.name(), workEffortOriginal.getTimestamp(E.estimatedStartDate.name()));
        serviceMap.put(E.estimatedCompletionDateFrom.name(), workEffortOriginal.getTimestamp(E.estimatedCompletionDate.name()));
        serviceMap.put(E.estimatedStartDateTo.name(), workEffortOriginal.getTimestamp(E.estimatedStartDate.name()));
        serviceMap.put(E.estimatedCompletionDateTo.name(), workEffortOriginal.getTimestamp(E.estimatedCompletionDate.name()));
        return serviceMap;
    }
}
