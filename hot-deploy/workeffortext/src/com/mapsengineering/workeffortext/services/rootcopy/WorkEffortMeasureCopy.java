package com.mapsengineering.workeffortext.services.rootcopy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.find.WorkEffortMeasureFindServices;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.workeffortext.services.E;

/**
 * Manage WorkEffortMeasure, for copy and snapshot
 *
 */
public class WorkEffortMeasureCopy extends AbstractWorkEffortDataCopy {
    
    public static final String MODULE = WorkEffortMeasureCopy.class.getName(); 

    /**
     * Constructor
     * @param service
     * @param snapshot
     */
    public WorkEffortMeasureCopy(WorkEffortRootCopyService service, boolean snapshot) {
        super(service, snapshot);
    }
    
    /**
     * Search WorkEffortMeasure for selected workEffortId and execute crudService crudServiceDefaultOrchestration_WorkEffortMeasure_copy.<br/>
     * Search WorkEffortMeasureByWorkEffortPurposeAccount if glAccountCreation equals N, because search only indicator (no resource).
     * Only is NOT use enableSnapshot weMeasureTypeEnumId != WEMT_SCORE<br/>
     */
    @Override
    public Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException {
        WorkEffortMeasureFindServices workEffortMeasureFindServices = new WorkEffortMeasureFindServices(getDelegator());
        Map<String, Object> returnValue = new HashMap<String, Object>();
        List<Map<String, String>> newWorkEffortMeasureIdList = new ArrayList<Map<String, String>>();

        String entityToSearch = E.WorkEffortMeasure.name();
        GenericValue newWorkEffort = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), newWorkEffortId), false);
        GenericValue newWorkEffortType = getDelegator().findOne(E.WorkEffortType.name(), UtilMisc.toMap(E.workEffortTypeId.name(), newWorkEffort.getString(E.workEffortTypeId.name())), false);

        List<EntityCondition> conditionList = getConditionWorkEffort(origWorkEffortId);
        
        
        if (E.N.name().equals(data.get(WorkEffortRootCopyService.GL_ACCOUNT_CREATION))) {
            conditionList.add(workEffortMeasureFindServices.getOtherWorkEffortPurposeType(newWorkEffortType.getString(E.workEffortTypeId.name()), newWorkEffortType.getString(E.wePurposeTypeIdInd.name())));
            entityToSearch = E.WorkEffortMeasureByWorkEffortPurposeAccount.name();
        }
        EntityCondition condition = EntityCondition.makeCondition(conditionList);
        List<GenericValue> wemList = getDelegator().findList(entityToSearch, condition, null, null, null, getUseCache());
        addLogInfo("Found " + wemList.size() + " " + entityToSearch + " for condition " + condition, origWorkEffortId);
        
        
        for (GenericValue wm : wemList) {
            String oldWorkEffortMeasureId = wm.getString(E.workEffortMeasureId.name());
            Map<String, Object> serviceReturnValue =  copyWorkEffortMeasure(newWorkEffort, wm, origWorkEffortId);
            
            if (!ServiceUtil.isError(serviceReturnValue)) {
                Map<String, String> oldAndNewWorkEffortMeasureIdMap = new HashMap<String, String>();
                oldAndNewWorkEffortMeasureIdMap.put(oldWorkEffortMeasureId, (String) UtilGenerics.checkMap(serviceReturnValue.get(E.id.name())).get(E.workEffortMeasureId.name()));
                newWorkEffortMeasureIdList.add(oldAndNewWorkEffortMeasureIdMap);
            }
        }

        returnValue.put(E.workEffortMeasureId.name(), newWorkEffortMeasureIdList);

        return returnValue;
    }
    
    /**
     * Return condition for workEffortId
     * <p> if isSnapshot = false, discard WEMT_SCORE (Score Calc)
     * @param origWorkEffortId
     * @return workEffortId = 'origWorkEffortId' AND [ weMeasureTypeEnumId <> WEMT_SCORE]
     */
    private List<EntityCondition> getConditionWorkEffort(String origWorkEffortId){
        List<EntityCondition> cond = FastList.newInstance();
        if (!isUseEnableSnapshot()) {
            cond.add(EntityCondition.makeCondition(E.weMeasureTypeEnumId.name(), EntityOperator.NOT_EQUAL, E.WEMT_SCORE.name()));
        }
        cond.add(EntityCondition.makeCondition(E.workEffortId.name(), origWorkEffortId));
        return cond;
    }

    /**
     * 
     * Create WorkEffortMeasure, if use enabled Snapshot, set from and thrudate of measure from original measure
     * @param newWorkEffort
     * @param wm WorkEffortMeasure
     * @param origWorkEffortId
     * @param oldWorkEffortMeasureId
     * @return
     * @throws GeneralException
     */
    private Map<String, Object> copyWorkEffortMeasure(GenericValue newWorkEffort, GenericValue wm, String origWorkEffortId) throws GeneralException {
        
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.putAll(wm.getAllFields());
        serviceMap.remove(E.workEffortMeasureId.name());
        serviceMap.put(E._AUTOMATIC_PK_.name(), E.Y.name());
        serviceMap.put(E.workEffortId.name(), newWorkEffort.getString(E.workEffortId.name()));
        serviceMap.put(E.fromDate.name(), newWorkEffort.getTimestamp(E.estimatedStartDate.name()));
        serviceMap.put(E.thruDate.name(), newWorkEffort.getTimestamp(E.estimatedCompletionDate.name()));
        serviceMap.put(E.isPosted.name(), getIsPosted(wm.getString(E.isPosted.name())));
        if(isUseEnableSnapshot()) {
            serviceMap.put(E.fromDate.name(), wm.getTimestamp(E.fromDate.name()));
            serviceMap.put(E.thruDate.name(), wm.getTimestamp(E.thruDate.name()));
        }
        
        String successMsg = "Created Measure for workEffort " + serviceMap.get(E.workEffortId.name()) + " and glAccount " + serviceMap.get(E.glAccountId.name());
        String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortMeasure.name() + " for workEffort " + serviceMap.get(E.workEffortId.name());
        return runSyncCrud("crudServiceDefaultOrchestration_WorkEffortMeasure_copy", E.WorkEffortMeasure.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
        
    }

}
