package com.mapsengineering.emplperf.test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ModelService;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.test.BasePersonStandardImportUploadFileTestCase;
import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.emplperf.update.ParamsEnum;
import com.mapsengineering.workeffortext.services.E;

/**
 * Utility for emplperf service
 *
 */
public class BaseTestInsertFromTemplate extends BasePersonStandardImportUploadFileTestCase {
    public static final String MODULE = BaseTestInsertFromTemplate.class.getName();
    
    protected static final String EMPL_POSITION_TYPE_ID_D = "D";
    protected static final String TEMPLATE_ID_FOR_D = "W13440";
    protected static final String EMPL_POSITION_TYPE_ID_ABCC = "ABCC";
    protected static final String TEMPLATE_ID_FOR_ABCC = "TEMP1";
    
    protected static final String UORG_1 = "14"; // partyId = "RCP10022";
    protected static final String PERSONA = "726"; // partyId = "RCP10193";
    protected static final String ROLE_TYPE_ID_UOSET = "UOSET";
    
    protected static final String SOGGETTO_1 = "0002";
    protected static final String SOGGETTO_2 = "0015";
    
    protected static final String APPROVATORE_1 = "0003";
    protected static final String APPROVATORE_2 = "0004";
    protected static final String APPROVATORE_3 = "0012";
    protected static final String APPROVATORE_4 = "0014";
    protected static final String APPROVATORE_5 = "0017";
    protected static final String VALUTATORE_1 = "0005";
    protected static final String VALUTATORE_2 = "0006";
    protected static final String VALUTATORE_3 = "0011";
    protected static final String VALUTATORE_4 = "0013";
    protected static final String VALUTATORE_5 = "0016";
    
    /** 01/01/2000 */
    protected static final Timestamp DEF_START_DATE_2000 = new Timestamp(UtilDateTime.toDate(1, 1, 2000, 0, 0, 0).getTime());
    /** 31/12/2099 */
    protected static final Timestamp DEF_COMPLETION_DATE_2099 = new Timestamp(UtilDateTime.toDate(12, 31, 2099, 0, 0, 0).getTime());

    /** 01/01/2014 */
    protected static final Timestamp START_DATE_2014 = new Timestamp(UtilDateTime.toDate(1, 1, 2014, 0, 0, 0).getTime());
    /** 31/12/2014 */
    protected static final Timestamp COMPLETION_DATE_2014 = new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime());

    /** 01/01/2015 */
    protected static final Timestamp START_DATE_2015 = new Timestamp(UtilDateTime.toDate(1, 1, 2015, 0, 0, 0).getTime());
    /** 31/12/2015 */
    protected static final Timestamp COMPLETION_DATE_2015 = new Timestamp(UtilDateTime.toDate(12, 31, 2015, 0, 0, 0).getTime());

    public static final Object TEMPLATE_ID_FOR_ABC = "TEMP2";
    public static final String EMPL_POSITION_TYPE_ID_ABC = "ABC";
    
    /**
     * Set context for creation of workeffort from template
     * @param orgUnitCode
     * @param orgUnitRoleTypeId
     * @param emplPositionTypeId
     * @param estimatedStartDate
     * @param estimatedCompletionDate
     * @throws GenericEntityException 
     */
    protected void setContextInsertFromTemplate(String orgUnitCode, String orgUnitRoleTypeId, String emplPositionTypeId, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate) throws GenericEntityException {
        context.put(E.parentTypeId.name(), "CTX_EP");
        
        context.put(E.orgUnitId.name(), getPartyId(orgUnitCode));
        context.put(E.orgUnitRoleTypeId.name(), orgUnitRoleTypeId);

        context.put(E.estimatedStartDate.name(), estimatedStartDate);
        context.put(E.estimatedCompletionDate.name(), estimatedCompletionDate);

        context.put(E.emplPositionTypeId.name(), emplPositionTypeId);
    }

    /**
     * Set context for update of workeffortNote and acctgTrans and a acctgTransEntry and workEffortAssoc from template
     * @param templateTypeId
     * @param templateId
     * @param updateWorkEffortPurposeType
     * @param updateWorkEffortAssocType
     * @param readDate
     * @param writeDate
     */
    protected void setContextUpdateFromPrevious(String templateTypeId, String templateId, String updateWorkEffortPurposeType, String updateWorkEffortAssocType, Timestamp readDate, Timestamp writeDate) {
        context.put(ParamsEnum.templateTypeId.name(), templateTypeId);
        context.put(ParamsEnum.templateId.name(), templateId);

        context.put(ParamsEnum.updateWorkEffortAssoc.name(), "Y");
        context.put(ParamsEnum.updateAcctgTransAndEntry.name(), "Y");
        context.put(ParamsEnum.updateWorkEffortNote.name(), "Y");
        context.put(ParamsEnum.updateWorkEffortPurposeType.name(), updateWorkEffortPurposeType);
        context.put(ParamsEnum.updateWorkEffortAssocType.name(), updateWorkEffortAssocType);
        context.put(ParamsEnum.readDate.name(), readDate);
        context.put(ParamsEnum.writeDate.name(), writeDate);
    }

    /**
     * Create WorkEffort from template with service
     * @param orgUnitCode
     * @param orgUnitRoleTypeId
     * @param emplPositionTypeId
     * @param estimatedStartDate
     * @param estimatedCompletionDate
     * @throws GenericEntityException 
     */
    protected Map<String, Object> setContextAndRunInsertFromTemplate(String orgUnitCode, String orgUnitRoleTypeId, String emplPositionTypeId, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate) throws GenericEntityException {
        setContextInsertFromTemplate(orgUnitCode, orgUnitRoleTypeId, emplPositionTypeId, estimatedStartDate, estimatedCompletionDate);
        try {
            Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext("emplPerfInsertFromTemplate", ModelService.IN_PARAM, context);
            Debug.log(" - emplPerfInsertFromTemplate serviceParams " + serviceParams);
            Map<String, Object> result = dispatcher.runSync("emplPerfInsertFromTemplate", serviceParams);
            Debug.log(" - emplPerfInsertFromTemplate result " + result);
            return result;
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
        }
        return null;
    }
    
    /**
     * Create WorkEffort from template with service
     * @param orgUnitId
     * @param orgUnitRoleTypeId
     * @param emplPositionTypeId
     * @param estimatedStartDate
     * @param estimatedCompletionDate
     * @throws GenericEntityException 
     */
    protected void setContextAndRunInsertFromTemplate(String orgUnitCode, String orgUnitRoleTypeId, String emplPositionTypeId, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate, long blockingError) throws GenericEntityException {
        Map<String, Object> result = setContextAndRunInsertFromTemplate(orgUnitCode, orgUnitRoleTypeId, emplPositionTypeId, estimatedStartDate, estimatedCompletionDate);
        assertEquals(blockingError, result.get(ServiceLogger.BLOCKING_ERRORS));
    }
    
    /**
     * Create WorkEffort from template with service
     * @param orgUnitId
     * @param orgUnitRoleTypeId
     * @param emplPositionTypeId
     * @param estimatedStartDate
     * @param estimatedCompletionDate
     * @throws GenericEntityException 
     */
    protected void setContextAndRunInsertFromTemplate(String orgUnitCode, String orgUnitRoleTypeId, String emplPositionTypeId, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate, long blockingError, long recordElaborated) throws GenericEntityException {
        Map<String, Object> result = setContextAndRunInsertFromTemplate(orgUnitCode, orgUnitRoleTypeId, emplPositionTypeId, estimatedStartDate, estimatedCompletionDate);
        assertEquals(blockingError, result.get(ServiceLogger.BLOCKING_ERRORS));
        assertEquals(recordElaborated, result.get(ServiceLogger.RECORD_ELABORATED));
    }

    /**
     * Return List of workEffort for Employment Performance
     * @param partyCode
     * @param refDate
     * @param extraCondition
     * @return
     */
    protected List<GenericValue> getWorkEffortEmplPerfForEvaluated(String partyCode, Timestamp refDate, EntityCondition extraCondition) {
        List<GenericValue> list = null;
        try {
            List<EntityCondition> condition = FastList.newInstance();
            condition.add(EntityCondition.makeCondition(E.parentTypeId.name(), E.CTX_EP.name()));

            condition.add(EntityCondition.makeCondition(E.isTemplate.name(), E.N.name()));
            condition.add(EntityCondition.makeCondition(E.workEffortRevisionId.name(), null));
            condition.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, refDate));

            if (UtilValidate.isNotEmpty(partyCode)) {
                String partyId = getPartyId(partyCode);
                condition.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
            }
            condition.add(EntityCondition.makeCondition(E.roleTypeId.name(), E.WEM_EVAL_IN_CHARGE.name()));

            if (UtilValidate.isNotEmpty(extraCondition)) {
                condition.add(extraCondition);
            }

            list = delegator.findList(E.WorkEffortAndWorkEffortPartyAssView.name(), EntityCondition.makeCondition(condition), null, null, null, false);
            Debug.log("Found " + list.size() + " WorkEffortAndWorkEffortPartyAssView with condition = " + condition);

            for (GenericValue workEffort : list) {
                Debug.log("Found id = " + workEffort.getString(E.workEffortId.name()) + " from " + workEffort.getTimestamp(E.fromDate.name()) + " - " + workEffort.getTimestamp(E.thruDate.name()));
            }
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
        }
        return list;
    }

    /**
     * 
     * @param partyId
     * @param refDate
     * @param contextPartyUpdateWorkEffortOld
     * @param contextPartyUpdateWorkEffortNew
     */
    protected void setContextPartyUpdateWorkEffort(String partyId, Timestamp refDate, Map<String, Object> contextPartyUpdateWorkEffortOld, Map<String, Object> contextPartyUpdateWorkEffortNew) {
        context.put(E.partyId.name(), partyId);
        context.put("refDate", refDate);
        context.put("parametersOld", contextPartyUpdateWorkEffortOld);
        context.put("parametersNew", contextPartyUpdateWorkEffortNew);
    }

    /**
     * Set context, run service and log workEffort from template
     * @param partyCode
     * @param orgUnitCode
     * @param orgUnitRoleTypeId
     * @param emplPositionTypeId
     * @param templateId
     * @param estimatedStartDate
     * @param estimatedCompletionDate
     * @param endDate
     * @throws GenericEntityException 
     */
    protected void createWorkEffortFromTemplate(String partyCode, String orgUnitCode, String orgUnitRoleTypeId, String emplPositionTypeId, String templateId, Timestamp estimatedStartDate, Timestamp estimatedCompletionDate, Timestamp endDate) throws GenericEntityException {
        setContextAndRunInsertFromTemplate(orgUnitCode, orgUnitRoleTypeId, emplPositionTypeId, estimatedStartDate, estimatedCompletionDate);
        EntityCondition extraConditionAfter = EntityCondition.makeCondition(E.templateId.name(), templateId);
        getWorkEffortEmplPerfForEvaluated(partyCode, endDate, extraConditionAfter);
    }

    /**
     * Return List of WorkEffort with evaluator or approver
     * @param partyCode
     * @param roleTypeId
     * @param evaluatorApproverId
     * @return
     * @throws GenericEntityException
     */
    protected List<GenericValue> checkEvaluatoApprover(String partyCode, String roleTypeId, String evaluatorApproverCode) throws GenericEntityException {
        return checkEvaluatoApprover(partyCode, roleTypeId, evaluatorApproverCode, null, null);
    }
    
    /**
     * Return List of WorkEffort with evaluator or approver
     * @param partyCode
     * @param roleTypeId
     * @param evaluatorApproverId
     * @return
     * @throws GenericEntityException
     */
    protected List<GenericValue> checkEvaluatoApprover(String partyCode, String roleTypeId, String evaluatorApproverCode, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
        List<EntityCondition> condition = FastList.newInstance();
        condition.add(EntityCondition.makeCondition(E.partyId.name(), getPartyId(partyCode)));
        
        if(UtilValidate.isNotEmpty(evaluatorApproverCode)) {
            condition.add(EntityCondition.makeCondition(E.rolePartyId.name(), getPartyId(evaluatorApproverCode)));
        }
        condition.add(EntityCondition.makeCondition(E.roleRoleTypeId.name(), roleTypeId));
        condition.add(EntityCondition.makeCondition(E.workEffortRevisionId.name(), null));
        
        if(UtilValidate.isNotEmpty(fromDate)) {
            condition.add(EntityCondition.makeCondition(E.fromDate.name(), fromDate));
        }
        if(UtilValidate.isNotEmpty(thruDate)) {
            condition.add(EntityCondition.makeCondition(E.thruDate.name(), thruDate));
        }
        List<GenericValue> list = delegator.findList(E.WorkEffortPartyAssignRole.name(), EntityCondition.makeCondition(condition), null, null, null, false);
        Debug.log("Found " + list.size() + " WorkEffortPartyAssignRole with condition= " + condition);
        return list;
    }

    protected List<GenericValue> checkEvaluatoApprover(int size, String partyCode, String roleTypeId, String evaluatorApproverCode, Timestamp fromDate, Timestamp thruDate) throws GenericEntityException {
        List<GenericValue> lista = checkEvaluatoApprover(partyCode, roleTypeId, evaluatorApproverCode, fromDate, thruDate);
        assertEquals(size, lista.size());
        return lista;
    }

    /**
     * Add condition
     * @param conditionParam
     * @return
     */
    protected List<Map<String, Object>> addCondition(Map<String, ? extends Object> conditionParam) {
        List<Map<String, Object>> listcondition = FastList.newInstance();
        String orgUniId = (String)conditionParam.get(E.orgUnitId.name());
        String templateId = (String)conditionParam.get(E.templateId.name());

        if (UtilValidate.isNotEmpty(orgUniId)) {
            Map<String, Object> map = FastMap.newInstance();
            map.put(E.name.name(), E.orgUnitId.name());
            map.put(E.value.name(), orgUniId);
            listcondition.add(map);
        }

        if (UtilValidate.isNotEmpty(templateId)) {
            Map<String, Object> map = FastMap.newInstance();
            map.put(E.name.name(), E.templateId.name());
            map.put(E.value.name(), templateId);
            listcondition.add(map);
        }

        return listcondition;
    }

    /**
     * Create movement
     * @throws GeneralException
     */
    protected void createMovement(String weTransWeId, String customTimePeriodId) throws GeneralException {
        Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("crudServiceDefaultOrchestration_AcctgTransAndEntries", "IN", context);
        Map<String, Object> parameters = FastMap.newInstance();

        GenericValue ctp = delegator.findOne("CustomTimePeriod", UtilMisc.toMap(E.customTimePeriodId.name(), customTimePeriodId), false);
        
        localContext.put(ServiceLogger.ENTITY_NAME, E.WorkEffortTransactionIndicatorView.name());
        localContext.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
        parameters.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
        parameters.put(E.weTransValue.name(), 100d);
        parameters.put(E.weTransAccountId.name(), "G20004");
        parameters.put(E.weTransWeId.name(), weTransWeId);
        parameters.put(E.weTransTypeValueId.name(), E.ACTUAL.name());
        parameters.put(E.partyId.name(), E.Company.name());
        parameters.put(E.roleTypeId.name(), "AMM");
        parameters.put(E.weTransCurrencyUomId.name(), "OTH_SCO");
        parameters.put(E.customTimePeriodId.name(), ctp.getString(E.customTimePeriodId.name()));
        parameters.put(E.acctgTransTypeId.name(), E.CTX_EP.name());
        parameters.put(E.defaultOrganizationPartyId.name(), E.Company.name());
        localContext.put(CrudEvents.PARAMETERS, parameters);
        
        dispatcher.runSync(E.crudServiceDefaultOrchestration_AcctgTransAndEntries.name(), localContext);
    }

    /**
     * Create note
     * @throws GeneralException
     */
    protected void createNote(String origWorkEffortId, String newWorkEffortId, Timestamp noteDateTime, String noteInfo) throws GeneralException {
        List<GenericValue> attrList = delegator.findList(E.WorkEffortNoteAndData.name(), EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.EQUALS, origWorkEffortId), null, null, null, false);
        for (GenericValue attr : attrList) {
            Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("crudServiceDefaultOrchestration_WorkEffortNoteAndData", "IN", context);
            Map<String, Object> parameters = FastMap.newInstance();

            Debug.log(" - attr " + attr);
            localContext.put(ServiceLogger.ENTITY_NAME, E.WorkEffortNoteAndData.name());
            localContext.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
            parameters.put(CrudEvents.OPERATION, CrudEvents.OP_CREATE);
            parameters.putAll(attr.getAllFields());
            parameters.put(E.workEffortId.name(), newWorkEffortId);
            parameters.put(E.noteId.name(), null);
            parameters.put(E.noteInfo.name(), noteInfo);
            parameters.put(E.noteDateTime.name(), noteDateTime);
            parameters.put(E.isPosted.name(), "N");
            localContext.put(CrudEvents.PARAMETERS, parameters);

            dispatcher.runSync("crudServiceDefaultOrchestration_WorkEffortNoteAndData", localContext);
        }
    }
    
    /**
     * Search WorkEffortAndWorkEffortPartyAssView with estimatedCompletionDate >= completionDate
     * @param size
     * @param partyCode
     * @param completionDate
     * @param extraConditionAfter
     * @return
     * @throws GenericEntityException
     */
    protected List<GenericValue> checkWorkEffortListSize(int size, String partyCode, Timestamp completionDate, EntityCondition extraConditionAfter) throws GenericEntityException {
        List<GenericValue> listaUtente = getWorkEffortEmplPerfForEvaluated(partyCode, completionDate, extraConditionAfter);
        assertEquals(size, listaUtente.size());
        return listaUtente;
    }
    
    /**
     * 
     * @param fromDate
     * @param size
     * @param partyCode
     * @param completionDate2014
     * @param extraConditionAfter
     * @throws GenericEntityException
     */
    protected void checkFromDateAndWorkEffortListSize(Timestamp fromDate, int size, String partyCode, Timestamp completionDate2014, EntityCondition extraConditionAfter) throws GenericEntityException {
        List<GenericValue> listaUtente = checkWorkEffortListSize(size, partyCode, completionDate2014, extraConditionAfter);
        assertEquals(fromDate.getTime(), EntityUtil.getFirst(listaUtente).getTimestamp(E.fromDate.name()).getTime());
    }
    
}
