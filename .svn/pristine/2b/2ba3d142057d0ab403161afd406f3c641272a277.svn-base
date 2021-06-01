package com.mapsengineering.workeffortext.services.rootcopy;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.workeffortext.services.E;

/**
 * Manage WorkEfforStatus, only for snapshot, snapshot = true
 *
 */
public class WorkEffortStatusCopy extends AbstractWorkEffortDataCopy {
    
    public static final String MODULE = WorkEffortStatusCopy.class.getName();
    
    /**
     * Constructor, snapshot = true
     * @param service
     */
    public WorkEffortStatusCopy(WorkEffortRootCopyService service) {
        super(service, true);
    }

    /**
     * Search WorkEffortStatus and execute crudServiceDefaultOrchestration
     */
    @Override
    public Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException {
        List<GenericValue> attrList = getDelegator().findList(E.WorkEffortStatus.name(), EntityCondition.makeCondition(E.workEffortId.name(), origWorkEffortId), null, null, null, getUseCache());
        for (GenericValue attr : attrList) {
            Map<String, Object> serviceMap = FastMap.newInstance();
            serviceMap.putAll(attr.getAllFields());
            serviceMap.put(E.workEffortId.name(), newWorkEffortId);
            String successMsg = "Created Status " + serviceMap.get(E.statusId.name()) + " for WorkEffort " + newWorkEffortId;
            String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortStatus.name() + " Status " + serviceMap.get(E.statusId.name()) + " for WorkEffort " + newWorkEffortId;
            runSyncCrud("crudServiceDefaultOrchestration", E.WorkEffortStatus.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
        }
        return null;
    }
}
