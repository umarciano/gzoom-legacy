package com.mapsengineering.partyext.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.partyext.common.E;

/**
 * Service for update info with WorkEffort and relative table, when party has a thruDate
 *
 */
public class WorkEffortPartyServices extends GenericService {
    public static final String MODULE = WorkEffortPartyServices.class.getName();
    public static final String SERVICE_NAME = "updatePartyEndDate";
    public static final String SERVICE_TYPE_ID = "UPDA_PARTY_END_DATE";

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public WorkEffortPartyServices(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE_ID, MODULE);
    }

    /**
     * Constructor
     * @param dctx
     * @param context
     * @param jobLogger
     */
    public WorkEffortPartyServices(DispatchContext dctx, Map<String, Object> context, JobLogger jobLogger) {
        super(dctx, context, jobLogger, SERVICE_NAME, SERVICE_TYPE_ID, MODULE);
    }

    /**
     * Main service
     * @param dctx
     * @param context
     * @return
     * @throws GeneralException
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> updatePartyEndDate(DispatchContext dctx, Map<String, ? extends Object> context) throws GeneralException {
        WorkEffortPartyServices obj = new WorkEffortPartyServices(dctx, (Map<String, Object>)context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Close all WorkEffortPartyAssignement and individual records
     * @throws GeneralException
     */
    public void mainLoop() throws GeneralException {

        String partyId = (String)context.get(E.partyId.name());
        Timestamp endDate = (Timestamp)context.get(E.endDate.name());
        String returnMessages = (String)context.get(E.returnMessages.name());
        
        /**
         * Nel caso ci sia una modifica alla data di cessazione, 
         * cerco le schede con la vecchia data, e le aggiorno con la nuova data
         * endDate : vecchia data di cessazione
         * newEndDate : nuova data di cessazione
         */
        Timestamp newEndDate = (Timestamp)context.get(E.newEndDate.name());
        if (UtilValidate.isEmpty(newEndDate)) {
        	newEndDate = endDate;
        }
        
        try {
            closeAllPartyRelationship(partyId, endDate, newEndDate);
            closeAllWorkEffortPartyAssignement(partyId, endDate, newEndDate);
            closeAllIndividualRecords(partyId, endDate, newEndDate, null);

        } catch (GeneralException e) {
            String msg = "Error update WorkEffort for Party endDate changed";
            jobLogger.printLogError(e, msg);
            setResult(ServiceUtil.returnError(msg + e.getMessage()));
        } finally {
            /**
             * Seleziono il tipo di return in base a se ho ricevuto in ingresso o no il jobLogger
             */
            if (UtilValidate.isNotEmpty(returnMessages) && returnMessages.equals("Y")) {
                getResult().put(ServiceLogger.MESSAGES, jobLogger.getMessages());
            } else {
                /**
                 * Vado a scrivere i log e ritorno ID
                 */
                String jobLogId = delegator.getNextSeqId("JobLog");
                super.writeLogs(UtilDateTime.nowTimestamp(), jobLogId);
                getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            }
        }
    }
    
    /***
     * Vado a chiudere tutte relazioni Da/a con fromDate <= endDate e thruDate is null
     * 
     * @param partyId
     * @param endDate
     * @param previuosDat
     */
    private void closeAllPartyRelationship(String partyId, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        List<EntityCondition> condition = FastList.newInstance();
        condition.add(EntityCondition.makeCondition(
                        EntityCondition.makeCondition(E.partyIdTo.name(), partyId),
                        EntityOperator.OR,
                        EntityCondition.makeCondition(E.partyIdFrom.name(), partyId)
                    )); 
        condition.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, endDate));
        condition.add(EntityCondition.makeCondition(E.thruDate.name(), null));

        closePartyRelationship(EntityCondition.makeCondition(condition), endDate, previuosDate);
    }
    
    private void closePartyRelationship(EntityCondition condition, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        List<GenericValue> genericValueListStore = FastList.newInstance();

        List<GenericValue> list = delegator.findList(E.PartyRelationship.name(), condition, null, null, null, false);
        jobLogger.printLogInfo("Found " + list.size() + " PartyRelationship with condition= " + condition);

        for (GenericValue element : list) {

            genericValueListStore.add(disabledPartyRelationship(element, endDate, previuosDate));
        }

        storeList(genericValueListStore);

        return;
    }
    
    private GenericValue disabledPartyRelationship(GenericValue partyRelationship, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.partyIdFrom.name(), (String)partyRelationship.get(E.partyIdFrom.name()), E.partyIdTo.name(), (String)partyRelationship.get(E.partyIdTo.name()), E.roleTypeIdFrom.name(), (String)partyRelationship.get(E.roleTypeIdFrom.name()), E.roleTypeIdTo.name(), (String)partyRelationship.get(E.roleTypeIdTo.name()), E.fromDate.name(), (Timestamp)partyRelationship.get(E.fromDate.name()), E.partyRelationshipTypeId.name(), (String)partyRelationship.get(E.partyRelationshipTypeId.name()), E.thruDate.name(), previuosDate);

        jobLogger.printLogInfo("Disabled PartyRelationship with parameters= " + serviceMapParams);
        return delegator.makeValue("PartyRelationship", serviceMapParams);
    }

    /**
     * Create EntityCondition and call closeWorkEffortPartyAssignement for all WorkEffortPartyAssignView with partyId (input) and fromDate <= endDate and thruDate >= endDate  
     * @param ctx
     * @param context
     * @return
     * @throws GeneralException 
     */
    private void closeAllWorkEffortPartyAssignement(String partyId, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        List<EntityCondition> condition = FastList.newInstance();
        condition.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        condition.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, endDate));
        condition.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, endDate));

        closeWorkEffortPartyAssignement(EntityCondition.makeCondition(condition), endDate, previuosDate);
    }
    
    /**
     * Close WorkEffortPartyAssignView with specific condition  
     * @param ctx
     * @param context
     * @return
     * @throws GeneralException 
     */
    private void closeWorkEffortPartyAssignement(EntityCondition condition, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        List<GenericValue> genericValueListStore = FastList.newInstance();

        List<GenericValue> list = delegator.findList(E.WorkEffortPartyAssignView.name(), condition, null, null, null, false);
        jobLogger.printLogInfo("Found " + list.size() + " WorkEffortPartyAssignView with condition= " + condition);

        for (GenericValue element : list) {

            genericValueListStore.add(disabledWorkEffortPartyAssignment(element, endDate, previuosDate));
        }

        storeList(genericValueListStore);

        return;
    }
    
    /**
     * Create EntityCondition and call closeWorkEffortPartyAssignement for WE_ASSIGNMENT WorkEffortPartyAssignView with partyId (input) and fromDate <= endDate and thruDate >= endDate  
     * @param ctx
     * @param context
     * @return
     * @throws GeneralException 
     */
    public void closeAssignementWorkEffortPartyAssignement(String partyId, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        List<EntityCondition> condition = FastList.newInstance();
        condition.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        condition.add(EntityCondition.makeCondition(E.roleTypeId.name(), E.WE_ASSIGNMENT.name()));
        condition.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, endDate));
        condition.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, endDate));
        
        closeWorkEffortPartyAssignement(EntityCondition.makeCondition(condition), endDate, previuosDate);
        
    }

    private GenericValue disabledWorkEffortPartyAssignment(GenericValue workEffortPartyAssignment, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.workEffortId.name(), (String)workEffortPartyAssignment.get(E.workEffortId.name()), E.roleTypeId.name(), (String)workEffortPartyAssignment.get(E.roleTypeId.name()), E.partyId.name(), (String)workEffortPartyAssignment.get(E.partyId.name()), E.fromDate.name(), (Timestamp)workEffortPartyAssignment.get(E.fromDate.name()), E.thruDate.name(), previuosDate);

        jobLogger.printLogInfo("Disabled WorkEffortPartyAssignment with parameters= " + serviceMapParams);
        return delegator.makeValue("WorkEffortPartyAssignment", serviceMapParams);
    }

    /**
     * Close all WorkEffort, WorkEffortMeasure, WorkEffortAssoc, WorkEffortPartyAssignment relative to Individual WorkEffort Root of party
     * @param partyId
     * @param endDate
     * @param previuosDay
     * @param locale
     * @param addCondition
     * @throws GeneralException
     */
    public void closeAllIndividualRecords(String partyId, Timestamp endDate, Timestamp previuosDate, List<Map<String, Object>> addCondition) throws GeneralException {
        String wemEvalManager = null;
        List<EntityCondition> condition = getSchedaValutazioneIndividualeCondition(partyId, "", endDate, true);

        if (UtilValidate.isNotEmpty(addCondition)) {
            for (Map<String, Object> gv : addCondition) {
                /**
                 * nel caso venga passato come condizione aggiuntiva 'valutatore' filto dopo aver fatto la ricaerca
                 */
                if (gv.get(E.name.name()).equals(E.WEM_EVAL_MANAGER.name())) {
                    wemEvalManager = (String)gv.get(E.value.name());
                } else {
                    condition.add(EntityCondition.makeCondition((String)gv.get(E.name.name()), (String)gv.get(E.value.name())));
                }

            }
        }

        List<GenericValue> list = delegator.findList(E.WorkEffortAndWorkEffortPartyAssView.name(), EntityCondition.makeCondition(condition), null, null, null, false);
        for (GenericValue element : list) {
            jobLogger.printLogInfo("closeAllIndividualRecords Found " + element.get(E.workEffortId.name()) + " with condition = " + condition);
            
            boolean executeWorkEffortFromParen = controlValutatore((String)element.get(E.workEffortId.name()), endDate, wemEvalManager);

            if (executeWorkEffortFromParen) {
                getWorkEffortFromParent((String)element.get(E.workEffortId.name()), endDate, previuosDate);
            }
        }

        return;
    }

    private List<EntityCondition> getSchedaValutazioneIndividualeCondition(String partyId, String orgUnitId, Timestamp refDate, boolean isRoot) {
        List<EntityCondition> condition = new ArrayList<EntityCondition>();
        condition.add(EntityCondition.makeCondition(E.parentTypeId.name(), E.CTX_EP.name()));
        if (isRoot) {
            condition.add(EntityCondition.makeCondition(E.isRoot.name(), E.Y.name()));
        }
        if (UtilValidate.isNotEmpty(orgUnitId)) {
        	condition.add(EntityCondition.makeCondition(E.orgUnitId.name(), orgUnitId));
        }
        condition.add(EntityCondition.makeCondition(E.isTemplate.name(), E.N.name()));
        condition.add(EntityCondition.makeCondition(E.workEffortRevisionId.name(), null));
        condition.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, refDate));

        condition.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        condition.add(EntityCondition.makeCondition(E.roleTypeId.name(), E.WEM_EVAL_IN_CHARGE.name()));

        return condition;
    }

    /**
     * Return Individual WorkEffort Root for party with roletypeId = WEM_EVAL_IN_CHARGE 
     * @param partyId
     * @param refDate
     * @param isRoot
     * @return
     * @throws GenericEntityException
     */
    public List<GenericValue> getSchedaValutazioneIndividuale(String partyId, Timestamp refDate, boolean isRoot) throws GenericEntityException {
        List<EntityCondition> condition = getSchedaValutazioneIndividualeCondition(partyId, "", refDate, isRoot);

        List<GenericValue> list = delegator.findList(E.WorkEffortAndWorkEffortPartyAssView.name(), EntityCondition.makeCondition(condition), null, null, null, false);
        jobLogger.printLogInfo("Found " + list.size() + " WorkEffortAndWorkEffortPartyAssView with condition = " + condition);

        return list;
    }
    
    /**
     * ritorna la scheda individuale per partyId e orgUnitId
     * @param partyId
     * @param orgUnitId
     * @param refDate
     * @param isRoot
     * @return
     * @throws GenericEntityException
     */
    public List<GenericValue> getSchedaValutazioneIndividuale(String partyId, String orgUnitId, Timestamp refDate, boolean isRoot) throws GenericEntityException {
        List<EntityCondition> condition = getSchedaValutazioneIndividualeCondition(partyId, orgUnitId, refDate, isRoot);

        List<GenericValue> list = delegator.findList(E.WorkEffortAndWorkEffortPartyAssView.name(), EntityCondition.makeCondition(condition), null, null, null, false);
        jobLogger.printLogInfo("Found " + list.size() + " WorkEffortAndWorkEffortPartyAssView with condition = " + condition);

        return list;
    }


    private boolean controlValutatore(String workEffortId, Timestamp endDate, String wemEvalManager) throws GeneralException {
        boolean executeWorkEffortFromParen = false;

        if (UtilValidate.isEmpty(wemEvalManager)) {
            executeWorkEffortFromParen = true;
        } else {
            /**
             * controllo se sono valutatore della scheda
             */
            List<EntityCondition> condition = FastList.newInstance();
            condition.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));
            condition.add(EntityCondition.makeCondition(E.partyId.name(), wemEvalManager));
            condition.add(EntityCondition.makeCondition(E.roleTypeId.name(), E.WEM_EVAL_MANAGER.name()));
            condition.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, endDate));
            condition.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, endDate));

            List<GenericValue> list = delegator.findList(E.WorkEffortPartyAssignment.name(), EntityCondition.makeCondition(condition), null, null, null, false);

            if (UtilValidate.isNotEmpty(list)) {
                executeWorkEffortFromParen = true;
            }
        }

        return executeWorkEffortFromParen;
    }

    private void getWorkEffortFromParent(String workEffortId, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        List<GenericValue> genericValueListStore = FastList.newInstance();

        if (UtilValidate.isNotEmpty(workEffortId)) {

            List<EntityCondition> condition = FastList.newInstance();
            condition.add(EntityCondition.makeCondition(E.workEffortParentId.name(), workEffortId));
            condition.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, endDate));

            List<GenericValue> list = delegator.findList(E.WorkEffort.name(), EntityCondition.makeCondition(condition), null, null, null, false);
            jobLogger.printLogInfo("Found " + list.size() + " WorkEffort with condition = " + condition);
            for (GenericValue element : list) {

                genericValueListStore.add(closeWorkEffort((String)element.get(E.workEffortId.name()), endDate, previuosDate));
                genericValueListStore.addAll(closeAllWorkEffortMeasure((String)element.get(E.workEffortId.name()), endDate, previuosDate));
                genericValueListStore.addAll(closeAllWorkEffortPartyAssignment((String)element.get(E.workEffortId.name()), endDate, previuosDate));
                genericValueListStore.addAll(closeWorkEffortAssoc(E.WorkEffortAssocToView.name(), (String)element.get(E.workEffortId.name()), endDate, previuosDate));
                genericValueListStore.addAll(closeWorkEffortAssoc(E.WorkEffortAssocFromView.name(), (String)element.get(E.workEffortId.name()), endDate, previuosDate));
            }

        }

        storeList(genericValueListStore);

    }

    private List<GenericValue> closeWorkEffortAssoc(String entityName, String workEffortId, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        List<GenericValue> genericValueListStore = FastList.newInstance();

        List<EntityCondition> condition = getConditionList(workEffortId, endDate);

        List<GenericValue> list = delegator.findList(entityName, EntityCondition.makeCondition(condition), null, null, null, false);
        for (GenericValue element : list) {
            genericValueListStore.add(closeWorkEffortAssoc(element, endDate, previuosDate));
        }

        return genericValueListStore;
    }

    private List<GenericValue> closeAllWorkEffortMeasure(String workEffortId, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        List<GenericValue> genericValueListStore = FastList.newInstance();

        List<EntityCondition> condition = getConditionList(E.weMeasureParentId.name(), E.weMeasureFromDate.name(), E.weMeasureThruDate.name(), E.weMeasureWeStartDate.name(), E.weMeasureWeCompletionDate.name(), workEffortId, endDate);
        List<GenericValue> list = delegator.findList(E.WorkEffortMeasureView.name(), EntityCondition.makeCondition(condition), null, null, null, false);
        jobLogger.printLogInfo("Found " + list.size() + " WorkEffortMeasureView with condition= " + condition);
        for (GenericValue element : list) {
            genericValueListStore.add(closeWorkEffortMeasure((String)element.get(E.weMeasureId.name()), endDate, previuosDate));
        }

        return genericValueListStore;
    }

    private List<GenericValue> closeAllWorkEffortPartyAssignment(String workEffortId, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        List<GenericValue> genericValueListStore = FastList.newInstance();

        List<EntityCondition> condition = getConditionList(workEffortId, endDate);
        jobLogger.printLogInfo("List WorkEffortAndWorkEffortPartyAssView with condition = " + condition);
        List<GenericValue> list = delegator.findList(E.WorkEffortAndWorkEffortPartyAssView.name(), EntityCondition.makeCondition(condition), null, null, null, false);
        for (GenericValue element : list) {
            genericValueListStore.add(disabledWorkEffortPartyAssignment(element, endDate, previuosDate));
        }

        return genericValueListStore;
    }

    private GenericValue closeWorkEffort(String workEffortId, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        Map<String, Object> newWorkEffortMap = UtilMisc.toMap(E.workEffortId.name(), workEffortId, E.estimatedCompletionDate.name(), previuosDate);
        jobLogger.printLogInfo("Disabled WorkEffort with parameters= " + newWorkEffortMap);
        return delegator.makeValue(E.WorkEffort.name(), newWorkEffortMap);
    }

    private GenericValue closeWorkEffortMeasure(String workEffortMeasureId, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        Map<String, Object> newWorkEffortMap = UtilMisc.toMap(E.workEffortMeasureId.name(), workEffortMeasureId, E.thruDate.name(), previuosDate);
        jobLogger.printLogInfo("Disabled WorkEffortMeasure with parameters= " + newWorkEffortMap);
        return delegator.makeValue(E.WorkEffortMeasure.name(), newWorkEffortMap);
    }

    private GenericValue closeWorkEffortAssoc(GenericValue genericValue, Timestamp endDate, Timestamp previuosDate) throws GeneralException {
        Map<String, Object> newWorkEffortMap = UtilMisc.toMap(E.workEffortIdFrom.name(), genericValue.get(E.workEffortIdFrom.name()), E.workEffortIdTo.name(), genericValue.get(E.workEffortIdTo.name()), E.fromDate.name(), genericValue.get(E.fromDate.name()), E.workEffortAssocTypeId.name(), genericValue.get(E.workEffortAssocTypeId.name()), E.thruDate.name(), previuosDate);
        jobLogger.printLogInfo("Disabled WorkEffortAssoc with parameters= " + newWorkEffortMap);
        return delegator.makeValue(E.WorkEffortAssoc.name(), newWorkEffortMap);
    }

    private void storeList(List<GenericValue> genericValueList) throws GenericServiceException, GenericEntityException {
        delegator.storeAll(genericValueList);
    }

    private List<EntityCondition> getConditionList(String workEffortId, Timestamp endDate) {
        return getConditionList(E.workEffortParentId.name(), E.fromDate.name(), E.thruDate.name(), E.estimatedStartDate.name(), E.estimatedCompletionDate.name(), workEffortId, endDate);
    }

    private List<EntityCondition> getConditionList(String workEffortParentId, String fromDate, String thruDate, String estimatedStartDate, String estimatedCompletionDate, String workEffortId, Timestamp endDate) {
        List<EntityCondition> condition = FastList.newInstance();
        condition.add(EntityCondition.makeCondition(workEffortParentId, workEffortId));
        condition.add(EntityCondition.makeCondition(fromDate, EntityOperator.LESS_THAN_EQUAL_TO, endDate));
        condition.add(EntityCondition.makeCondition(thruDate, EntityOperator.GREATER_THAN_EQUAL_TO, endDate));
        condition.add(EntityCondition.makeCondition(estimatedStartDate, EntityOperator.LESS_THAN_EQUAL_TO, endDate));
        condition.add(EntityCondition.makeCondition(estimatedCompletionDate, EntityOperator.GREATER_THAN_EQUAL_TO, endDate));

        return condition;
    }
}