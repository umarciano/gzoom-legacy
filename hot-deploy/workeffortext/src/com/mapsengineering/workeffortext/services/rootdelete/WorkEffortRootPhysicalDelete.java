package com.mapsengineering.workeffortext.services.rootdelete;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

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
public class WorkEffortRootPhysicalDelete extends GenericServiceLoop {

    public static final String MODULE = WorkEffortRootPhysicalDelete.class.getName();
    public static final String SERVICE_NAME = "WorkEffortRootPhysicalDelete";
    public static final String SERVICE_TYPE = "WRK_ROOT_DELETE";

    /**
     * Delete workeffort root with child
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> workEffortRootPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
        WorkEffortRootPhysicalDelete obj = new WorkEffortRootPhysicalDelete(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public WorkEffortRootPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
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
                String workEffortId = (String)context.get(E.workEffortId.name());

                List<EntityCondition> cond = FastList.newInstance();
                cond.add(EntityCondition.makeCondition(E.workEffortParentId.name(), workEffortId));
                cond.add(EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.NOT_EQUAL, workEffortId));

                List<GenericValue> workEffortList = delegator.findList(E.WorkEffort.name(), EntityCondition.makeCondition(cond), null, null, null, getUseCache());
                List<String> toDeleteList = EntityUtil.getFieldListFromEntityList(workEffortList, E.workEffortId.name(), true);
                toDeleteList.add(workEffortId);
                for (String toDelete : toDeleteList) {
                    executeSingleDelete(toDelete);
                }
            }
        }).execute().rethrow();
    }

    /** Delete workeffort */
    protected void executeSingleDelete(String toDelete) throws GeneralException {
        GenericValue wrkToDelete = findOne(E.WorkEffort.name(), EntityCondition.makeCondition(E.workEffortId.name(), toDelete), "Found more than one workEffort with id " + toDelete , "Workeffort with " + toDelete + " does not exists");

        wrkToDelete.set(E.workEffortParentId.name(), null);
        wrkToDelete.store();

        addLogInfo("Deleting workEffort: " + toDelete + " \"" + wrkToDelete.getString(E.workEffortName.name()) + "\"");
        Map<String, Object> serviceMap = baseCrudInterface("WorkEffortView", E.DELETE.name(), UtilMisc.toMap(E.workEffortId.name(), toDelete));
        serviceMap.put(E.userLogin.name(), userLogin);
        dispatcher.runSync("crudServiceDefaultOrchestration_WorkEffort", serviceMap);
        setRecordElaborated(getRecordElaborated() + 1);
    }
}
