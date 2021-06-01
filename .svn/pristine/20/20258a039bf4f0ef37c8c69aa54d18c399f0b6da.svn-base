package com.mapsengineering.workeffortext.services.rootcopy;

import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.util.ContextIdEnum;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.workeffortext.services.E;

/**
 * Manage workEffort, for copy and snapshot 
 *
 */
public class WorkEffortCopy extends AbstractWorkEffortDataCopy {
    private String storeRevisionWorkEffortAssoc;
    private String workEffortRevisionIdAssoc;
    private ServiceTypeEnum serviceTypeEnum;

    public static final String MODULE = WorkEffortCopy.class.getName();

    /**
     * Constructor
     * @param duplicateAdmit 
     */
    public WorkEffortCopy(WorkEffortRootCopyService service, ServiceTypeEnum serviceTypeEnum, String storeRevisionWorkEffortAssoc, String workEffortRevisionIdAssoc) {
        super(service, serviceTypeEnum.isUseEnableSnapshot());
        this.storeRevisionWorkEffortAssoc = storeRevisionWorkEffortAssoc;
        this.workEffortRevisionIdAssoc = workEffortRevisionIdAssoc;
        this.serviceTypeEnum = serviceTypeEnum;
    }

    /**
     * Execute crudServiceDefaultOrchestration_WorkEffort, with different parameters for copy and snapshot
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException {
        // workEffortTypeId preso da data contiene il workEffortTypeId preso dal context,
        // e quindi utile solo per la COPY e COPY_ALL
        // per gli obiettivifigli, invece, si va a cercare la relazione COPY
        String workEffortTypeId = (String)data.get(E.workEffortTypeId.name());
        String workEffortParentId = (String)data.get(E.workEffortParentId.name());
        String hierarchyAssocTypeId = (String)data.get(E.parentHierarchyAssocTypeId.name());
        Timestamp estimatedStartDate = (Timestamp)data.get(E.estimatedStartDate.name());
        Timestamp estimatedCompletionDate = (Timestamp)data.get(E.estimatedCompletionDate.name());
        String workEffortRootId = (String)data.get(E.workEffortRootId.name());

        GenericValue oldRoot = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), origWorkEffortId), false);
        GenericValue wrkType = getDelegator().findOne(E.WorkEffortType.name(), UtilMisc.toMap(E.workEffortTypeId.name(), workEffortTypeId), false);

        Map<String, Object> serviceMap = oldRoot.getAllFields();
        serviceMap.remove(E.workEffortId.name());
        serviceMap.put(E._AUTOMATIC_PK_.name(), E.Y.name());
        serviceMap.put(E.isPosted.name(), getIsPosted(oldRoot.getString(E.isPosted.name())));

        // skipAutomaticServiceAssoc, skipAutomaticServiceMeasure e skipAutomaticServiceNote,
        // Per la COPY_ALL invece deve creare Measure e Note automatiche
        serviceMap.put(E.skipAutomaticServiceAssoc.name(), E.Y.name());
        serviceMap.put(E.skipAutomaticServiceMeasure.name(), E.Y.name());
        serviceMap.put(E.skipAutomaticServiceNote.name(), E.Y.name());

        if (UtilValidate.isNotEmpty(workEffortParentId)) {
            Double assocWeight = (Double)data.get(E.assocWeight.name());
            Long sequenceNum = (Long)data.get(E.sequenceNum.name());

            serviceMap.put(E.workEffortParentId.name(), workEffortRootId);
            // Crea relazione ROOT
            serviceMap.put(E.workEffortIdRoot.name(), workEffortRootId);
            serviceMap.put(E.weHierarchyTypeId.name(), hierarchyAssocTypeId);
            // Crea relazione gerarchica
            serviceMap.put(E.workEffortIdFrom.name(), workEffortParentId);

            serviceMap.put(E.assocWeight.name(), assocWeight);
            serviceMap.put(E.sequenceNum.name(), sequenceNum);
        }

        // CLONE, COPY_ALL, COPY
        if (!serviceTypeEnum.isUseWorkEffortRevisionId()) {
            serviceMap.putAll(setCopyParams(serviceTypeEnum, oldRoot.getString(E.workEffortId.name()), oldRoot.getString(E.workEffortTypeId.name()), workEffortTypeId, estimatedStartDate, estimatedCompletionDate, wrkType.getString(E.parentTypeId.name())));
        } else {
            // SNAPSHOT E MASSIVE_SNAPSHOT
            serviceMap.put(E.workEffortSnapshotId.name(), oldRoot.get(E.workEffortId.name()));
            serviceMap.put(E.snapShotDescription.name(), data.get(E.snapShotDescription.name()));
            serviceMap.put(E.snapShotDate.name(), data.get(E.snapShotDate.name()));
            serviceMap.put(E.workEffortRevisionId.name(), data.get(E.workEffortRevisionId.name()));

            if (E.Y.name().equals(storeRevisionWorkEffortAssoc)) {
                serviceMap.put(E.workEffortRevisionIdAssoc.name(), workEffortRevisionIdAssoc);
            }
        }
        // TODO problema con la copia della scheda individuale e problema di Anna per la creazione della relazione " causa "
        Debug.log(" serviceMap " + serviceMap);
        String successMsg = "Created WorkEffort \"" + oldRoot.getString(E.workEffortName.name()) + "\"";
        String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffort.name() + " " + oldRoot.getString(E.workEffortName.name()) + "\"";
        Map<String, Object> res = runSyncCrud("crudServiceDefaultOrchestration_WorkEffort", E.WorkEffort.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
        Map<String, Object> resId = (Map<String, Object>)res.get(E.id.name());
        return UtilMisc.toMap(E.workEffortId.name(), resId.get(E.workEffortId.name()));
    }

    /** Set values for workeffort copy 
     * @param originalWorkEffortTypeId 
     * @param workEffortTypeId 
     * @throws GeneralException */
    private Map<String, Object> setCopyParams(ServiceTypeEnum serviceTypeEnum, String workEffortId, String originalWorkEffortTypeId, String workEffortTypeId, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate, String wrtParenTYpeId) throws GeneralException {
        Map<String, Object> serviceMap = new FastMap<String, Object>();
        serviceMap.put(E.estimatedStartDate.name(), estimatedStartDate);
        serviceMap.put(E.estimatedCompletionDate.name(), estimatedCompletionDate);
        
        // se fromCard = S gestisce codifica automatica
        serviceMap.put(E.fromCard.name(), E.S.name());
        // skipAutomaticServiceAssoc, skipAutomaticServiceMeasure e skipAutomaticServiceNote,
        // Per la COPY_ALL invece deve creare Measure e Note automatiche
        if (ServiceTypeEnum.COPY_ALL.equals(serviceTypeEnum)) {
            serviceMap.put(E.skipAutomaticServiceMeasure.name(), E.N.name());
            serviceMap.put(E.skipAutomaticServiceNote.name(), E.N.name());
            serviceMap.put(E.fromCard.name(), E.N.name());
            
            //serviceMap.put(E.workEffortTypeId.name(), workEffortTypeId);
            GenericValue workEffortTypeAssoc = getService().findOne(E.WorkEffortType.name(), EntityCondition.makeCondition(E.workEffortTypeId.name(), originalWorkEffortTypeId), "", "No childTemplateId with id = " + EntityCondition.makeCondition(E.workEffortTypeId.name(), originalWorkEffortTypeId));
            serviceMap.put(E.workEffortTypeId.name(), workEffortTypeAssoc.getString(E.childTemplateId.name()));
        
            // currentStatusId va passato null perche' in questi casi deve essere recuperato dal periodo 
            serviceMap.put(E.currentStatusId.name(), null);
        } else if (ServiceTypeEnum.COPY.equals(serviceTypeEnum)) {
            serviceMap.put(E.lastCorrectScoreDate.name(), null);
            serviceMap.put(E.workEffortTypePeriodId.name(), null);

            ContextIdEnum contextIdEnum = ContextIdEnum.parse(wrtParenTYpeId);
            serviceMap.put(E.defaultStatusPrefix.name(), contextIdEnum.defaultStatusPrefix());
            serviceMap.put(E.sourceReferenceId.name(), null);
            serviceMap.put(E.etch.name(), null);
        }

        return serviceMap;
    }
}
