package com.mapsengineering.workeffortext.services.rootcopy;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.workeffortext.services.E;

/**
 * Manage WorkEffortAssoc, only for copy, snapshot = false
 *
 */
public class WorkEffortAssocCopy extends AbstractWorkEffortDataCopy {

    private String storeRevisionWorkEffortAssoc;
    private String workEffortRevisionId;

    public static final String MODULE = WorkEffortAssocCopy.class.getName();

    /**
     * Constructor, snapshot = false
     * The workEffortAssocType selected is not in (ROOT, TEMPLATE, COPY, SNAPSHOT, List <hierarchyAssocTypeId>)
     * @param service
     * @param snapshot
     */
    public WorkEffortAssocCopy(WorkEffortRootCopyService service, String storeRevisionWorkEffortAssoc, String workEffortRevisionId) {
        super(service);
        this.storeRevisionWorkEffortAssoc = storeRevisionWorkEffortAssoc;
        this.workEffortRevisionId = workEffortRevisionId;
    }

    /**
     * Search WorkEffortAssocType and execute crudService crudServiceDefaultOrchestration_WorkEffortAssoc.<br/>
     * The workEffortAssocType selected is not in (ROOT, TEMPLATE, COPY, SNAPSHOT, List <hierarchyAssocTypeId>).<br/>
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException {
        List<String> listHierarchyAssocTypeId = (List<String>)data.get(E.hierarchyAssocTypeId.name());
        GenericValue newWorkEffort = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), newWorkEffortId), getUseCache());

        List<EntityCondition> cond = FastList.newInstance();
        cond.add(EntityCondition.makeCondition(EntityOperator.OR, EntityCondition.makeCondition(E.workEffortIdFrom.name(), origWorkEffortId), EntityCondition.makeCondition(E.workEffortIdTo.name(), origWorkEffortId)));
        cond.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), EntityOperator.NOT_EQUAL, E.ROOT.name()));
        cond.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), EntityOperator.NOT_EQUAL, E.TEMPL.name()));
        cond.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), EntityOperator.NOT_EQUAL, E.COPY.name()));
        cond.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), EntityOperator.NOT_EQUAL, E.SNAPSHOT.name()));
        for (String hierarchyAssocTypeId : listHierarchyAssocTypeId) {
            cond.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), EntityOperator.NOT_EQUAL, hierarchyAssocTypeId));
        }

        List<GenericValue> assocList = getDelegator().findList(E.WorkEffortAssocAndTypeView.name(), EntityCondition.makeCondition(cond), null, null, null, getUseCache());
        addLogInfo("Found " + assocList.size() + " WorkEffortAssocAndTypeView with condition " + EntityCondition.makeCondition(cond), origWorkEffortId);
        for (GenericValue assoc : assocList) {
            copyWorkEffortAssoc(newWorkEffort, assoc, origWorkEffortId, newWorkEffortId);
        }
        return null;
    }

    /**
     * Create WorkEffortAssoc with isPosted = N, only for copy, with business service (crudServiceDefaultOrchestration_WorkEffortAssoc)
     * @param newWorkEffort
     * @param assoc
     * @param origWorkEffortId
     * @param newWorkEffortId
     * @throws GeneralException
     */
    private void copyWorkEffortAssoc(GenericValue newWorkEffort, GenericValue assoc, String origWorkEffortId, String newWorkEffortId) throws GeneralException {
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.putAll(assoc.getAllFields());
        serviceMap.remove(E.parentTypeId.name());
        if (origWorkEffortId.equals(assoc.getString(E.workEffortIdFrom.name()))) {
            serviceMap.put(E.workEffortIdFrom.name(), newWorkEffortId);
        }
        if (origWorkEffortId.equals(assoc.getString(E.workEffortIdTo.name()))) {
            serviceMap.put(E.workEffortIdTo.name(), newWorkEffortId);
        }
        if (E.Y.name().equals(storeRevisionWorkEffortAssoc)) {
            serviceMap.put(E.workEffortRevisionId.name(), workEffortRevisionId);
        }
        serviceMap.put(E.fromDate.name(), getFromDate(newWorkEffort.getTimestamp(E.estimatedStartDate.name()), assoc.getTimestamp(E.fromDate.name())));
        serviceMap.put(E.thruDate.name(), getThruDate(newWorkEffort.getTimestamp(E.estimatedCompletionDate.name()), assoc.getTimestamp(E.thruDate.name())));
        serviceMap.put(E.isPosted.name(), getIsPosted(assoc.getString(E.isPosted.name())));

        String successMsg = "Created relation of type " + serviceMap.get(E.workEffortAssocTypeId.name()) + " from " + serviceMap.get(E.workEffortIdFrom.name()) + " to " + serviceMap.get(E.workEffortIdTo.name());
        String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortAssoc.name() + " of type " + serviceMap.get(E.workEffortAssocTypeId.name()) + " from " + serviceMap.get(E.workEffortIdFrom.name()) + " to " + serviceMap.get(E.workEffortIdTo.name());
        runSyncCrud("crudServiceDefaultOrchestration_WorkEffortAssoc", E.WorkEffortAssoc.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
    }
}
