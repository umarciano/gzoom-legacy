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
 * Manage WorkEffortAttribute, for copy and snapshot
 *
 */
public class WorkEffortAttributeCopy extends AbstractWorkEffortDataCopy {
    
    public static final String MODULE = WorkEffortAttributeCopy.class.getName();
    
    /**
     * Constructor
     * @param service
     */
    public WorkEffortAttributeCopy(WorkEffortRootCopyService service, boolean snapshot) {
        super(service, snapshot);
    }

    /**
     * Create WorkEffortAttribute<br/>
     * for copy: with isPosted = N and business service (crudServiceDefaultOrchestration_WorkEffortAttribute)<br/>
     * for snapshot: without business service (crudServiceDefaultOrchestration for WorkEffortAttribute)
     */
    @Override
    public Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException {
        List<GenericValue> attrList = getDelegator().findList(E.WorkEffortAttribute.name(), EntityCondition.makeCondition(E.workEffortId.name(), origWorkEffortId), null, null, null, getUseCache());
        for (GenericValue attr : attrList) {
            Map<String, Object> serviceMap = FastMap.newInstance();
            serviceMap.putAll(attr.getAllFields());
            serviceMap.put(E.workEffortId.name(), newWorkEffortId);
            serviceMap.put(E.isPosted.name(), getIsPosted(attr.getString(E.isPosted.name())));
            String successMsg = "Created Attribute " + serviceMap.get(E.attrName.name()) + " for WorkEffort " + newWorkEffortId;
            String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortAttribute.name() + " for " + serviceMap.get(E.attrName.name()) + " for WorkEffort " + newWorkEffortId;
            if(isUseEnableSnapshot()) {
                runSyncCrud("crudServiceDefaultOrchestration", E.WorkEffortAttribute.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
            } else {
                runSyncCrud("crudServiceDefaultOrchestration_WorkEffortAttribute", E.WorkEffortAttribute.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
            }
        }
        return null;
    }

}
