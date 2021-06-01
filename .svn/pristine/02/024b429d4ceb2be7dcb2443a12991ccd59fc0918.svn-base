package com.mapsengineering.workeffortext.services.rootdelete;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.mapsengineering.base.services.GenericServiceLoop;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;
import com.mapsengineering.workeffortext.services.E;

/**
 * Delete a workEffort root with its child
 * @author dain
 *
 */
public class WorkEffortsRootPhysicalDelete extends GenericServiceLoop {

    public static final String MODULE = WorkEffortsRootPhysicalDelete.class.getName();
    public static final String SERVICE_NAME = "WorkEffortsRootPhysicalDelete";
    public static final String SERVICE_TYPE = "WRK_ROOTS_DELETE";

    /**
     * Delete workeffort root with child
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> workEffortsRootPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
        WorkEffortsRootPhysicalDelete obj = new WorkEffortsRootPhysicalDelete(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public WorkEffortsRootPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
    	super(dctx, context, SERVICE_NAME, SERVICE_TYPE, MODULE);
    }

    /**
     * Find and delete workeffort with same workEffortParentId
     * @throws Exception
     */
    protected void execute() throws Exception {
        new TransactionRunner(MODULE, true, ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT, new TransactionItem() {
            @Override
            public void run() throws Exception {
                String workEffortTypeId = (String)context.get(E.workEffortTypeId.name());
                String currentStatusId = (String)context.get(E.currentStatusId.name());

                List<EntityCondition> cond = FastList.newInstance();
                if (UtilValidate.isNotEmpty(workEffortTypeId)) {
                    cond.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId));
                }
                if (UtilValidate.isNotEmpty(currentStatusId)) {
                    cond.add(EntityCondition.makeCondition(E.currentStatusId.name(), currentStatusId));
                }
                cond.add(EntityCondition.makeCondition(E.workEffortRevisionId.name(), null));

                cond.add(EntityCondition.makeCondition(E.workEffortParentId.name(), EntityOperator.EQUALS_FIELD, E.workEffortId.name()));

                if (UtilValidate.isEmpty(workEffortTypeId) && UtilValidate.isEmpty(currentStatusId)) {
                    throw new GeneralException("Almost WorkEffort Type Id or Current Status Id MUST be NOT empty");
                }
                List<GenericValue> workEffortList = delegator.findList(E.WorkEffort.name(), EntityCondition.makeCondition(cond), null, null, null, getUseCache());
                List<String> toDeleteList = EntityUtil.getFieldListFromEntityList(workEffortList, E.workEffortId.name(), true);
                addLogInfo("Found " + toDeleteList.size() + " workEffort with " + EntityCondition.makeCondition(cond) + " to delete");
                for (String toDelete : toDeleteList) {

                    String serviceName = "workEffortRootPhysicalDelete";
                    Map<String, Object> serviceContext = getDctx().makeValidContext(serviceName, ModelService.IN_PARAM, context);
                    serviceContext.put(E.workEffortId.name(), toDelete);
                    serviceContext.put(ServiceLogger.SESSION_ID, getSessionId());
                    manageResult(dispatcher.runSync(serviceName, serviceContext), toDelete);
                }
            }
        }).execute().rethrow();
    }
    
}
