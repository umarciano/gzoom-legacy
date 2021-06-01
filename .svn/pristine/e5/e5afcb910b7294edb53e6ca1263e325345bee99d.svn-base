package com.mapsengineering.emplperf.insert;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.services.note.enumeration.NoteDataFieldEnum;
import com.mapsengineering.emplperf.insert.EmplPerfInsertFromTemplateReadFieldSelect;
import com.mapsengineering.emplperf.insert.ParamsEnum;
import com.mapsengineering.emplperf.insert.EmplPerfRootViewFieldEnum;
import com.mapsengineering.workeffortext.services.E;

/**
 * Create workEffort from template for emplperf 
 *
 */
public class EmplPerfInsertFromTemplate extends GenericService {
    public static final String MODULE = EmplPerfInsertFromTemplate.class.getName();
    public static final String SERVICE_NAME = "emplPerfInsertFromTemplate";
    public static final String SERVICE_TYPE_ID = "EMPL_PERF_TMPL";
    public static final String ORG_EMPLOYMENT = "ORG_EMPLOYMENT";
    public static final String ORG_ALLOCATION = "ORG_ALLOCATION";

    
    private String partyRelationshipTypeId;
    
    /**
     * Main service
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> emplPerfInsertFromTemplate(DispatchContext dctx, Map<String, Object> context) {
        EmplPerfInsertFromTemplate obj = new EmplPerfInsertFromTemplate(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public EmplPerfInsertFromTemplate(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE_ID, MODULE);
    }

    /**
     * 1. search evaluated for orgUnitId
     * 2. check if exist the performance with estimatedStartDate and estimatedCompletionDate from context
     * 3. check if there is change for party and create performance with specific estimatedStartDate and specific estimatedCompletionDate
     */
    public void mainLoop() {
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        String msg;
        Timestamp contextEstimatedStartDate = (Timestamp)context.get(ParamsEnum.estimatedStartDate.name());
        Timestamp contextEstimatedCompletionDate = (Timestamp)context.get(ParamsEnum.estimatedCompletionDate.name());
        String contextShowCode = (String)context.get(ParamsEnum.showCode.name());
        String contextOrganizationId = (String)context.get(ParamsEnum.organizationId.name());
        String forcedTemplateId = (String)context.get(ParamsEnum.forcedTemplateId.name());
        
        String evalPartyId = null;
        
        // Determina vista da utilizzare per recupero schede
        String retrieveView = ParamsEnum.EmploymentTemplateView.name();
        partyRelationshipTypeId = UtilProperties.getPropertyValue("BaseConfig.properties", "EmplPerfInsertFromTemplate.partyRelationshipTypeId", ORG_EMPLOYMENT);
        if(ORG_ALLOCATION.equals(partyRelationshipTypeId)){
        	retrieveView = ParamsEnum.AllocationTemplateView.name();
        }
        
        // log
        Map<String, Object> emplPerfInsFromTemplParams = UtilMisc.toMap(E.partyRelationshipTypeId.name(), partyRelationshipTypeId, 
                ParamsEnum.organizationId.name(), contextOrganizationId, 
        		ParamsEnum.estimatedStartDate.name(), contextEstimatedStartDate, 
        		ParamsEnum.estimatedCompletionDate.name(), contextEstimatedCompletionDate);
        createInfoLogWithLabel(emplPerfInsFromTemplParams, "EmplPerfInsertFromTemplate_Start", ParamsEnum.BaseUiLabels.name());
        
        try {
        	
        	// recupero parametri per query di ricerca delle schede performance da creare
            EntityCondition readCondition = ReadConditionCreator.buildReadCondition(context, partyRelationshipTypeId);
            EntityFindOptions entityFindOptions = new EntityFindOptions();
            entityFindOptions.setDistinct(true);
            
            // log
            emplPerfInsFromTemplParams = UtilMisc.toMap(ParamsEnum.entityView.name(), retrieveView, 
            		ParamsEnum.emplPerfInsertFromTemplateParams.name(),  (Object)readCondition.toString());
            createInfoLogWithLabel(emplPerfInsFromTemplParams, "EmplPerfInsertFromTemplate_Params", ParamsEnum.BaseUiLabels.name());
            
            // recupero schede
            List<GenericValue> curList = delegator.findList(retrieveView, readCondition, EmplPerfInsertFromTemplateReadFieldSelect.getFieldsToSelect(), EmplPerfInsertFromTemplateOrberByFields.getOrderByFields(), entityFindOptions, getUseCache());

            // log
            emplPerfInsFromTemplParams = UtilMisc.toMap(ParamsEnum.value.name(), (Object)Integer.valueOf(curList.size()));
            createInfoLogWithLabel(emplPerfInsFromTemplParams, "EmplPerfInsertFromTemplate_Found", ParamsEnum.BaseUiLabels.name());

            if (UtilValidate.isNotEmpty(curList)) {
                Timestamp startDate = null;
	            for (int count = 0; count < curList.size(); count++) {	            	
	                boolean beganTransaction = false;
	                GenericValue gv = curList.get(count);
	                try {
	                    evalPartyId = gv.getString(ParamsEnum.partyId.name());
	                    String organizationId = gv.getString(ParamsEnum.organizationId.name());
	                    String orgUnitId = gv.getString(ParamsEnum.orgUnitId.name());
	                    String templateId = gv.getString(ParamsEnum.templateId.name());
	                    Timestamp completionDate = gv.getTimestamp(ParamsEnum.thruDate.name());
	                    String evaluator = gv.getString(ParamsEnum.evaluator.name());
	                    String approver = gv.getString(ParamsEnum.approver.name());
	                    
	                    if(UtilValidate.isNotEmpty(forcedTemplateId)){
	                    	templateId = forcedTemplateId;
	                    }
		            	
		            	// controlla se l'elemento successivo e' consecutivo, in tal caso memorizza la from_date e continua
		            	int countNext = count+1;
		            	if((countNext < curList.size()) && UtilValidate.isNotEmpty(curList.get(countNext))){
		            		GenericValue gvNext = curList.get(countNext);
		            		Double effort = gv.getDouble(ParamsEnum.effort.name());
		            		
		            		// thruDate + 1 dell'elemento attuale corrisponde alla fromDate dell'elemento successivo
		            		Timestamp nextDay = new Timestamp(getNextDay(completionDate).getTime());
		            		boolean controlloDateConsecutive = nextDay.compareTo(gvNext.getTimestamp(ParamsEnum.fromDate.name())) == 0;
		            		
		            		if(evalPartyId.equalsIgnoreCase(gvNext.getString(ParamsEnum.partyId.name())) &&
		            				templateId.equalsIgnoreCase(gvNext.getString(ParamsEnum.templateId.name())) &&
		            				orgUnitId.equalsIgnoreCase(gvNext.getString(ParamsEnum.orgUnitId.name())) &&
		            				effort.equals(gvNext.getDouble(ParamsEnum.effort.name())) &&
		            				controlloDateConsecutive){
		            			if(startDate == null)
			                    {
		            				startDate = gv.getTimestamp(ParamsEnum.fromDate.name());
			                    }
		            			continue;
		            		}
		            	}
	                    
	                    // date per verifica e creazione schede performance
	                    if(startDate == null)
	                    {
	                    	startDate = gv.getTimestamp(ParamsEnum.fromDate.name());
	                    }
	                    
	                    // log
	                    emplPerfInsFromTemplParams = UtilMisc.toMap(ParamsEnum.partyId.name(), (Object)evalPartyId, ParamsEnum.organizationId.name(), (Object)organizationId,
	                            ParamsEnum.orgUnitId.name(), (Object)orgUnitId,	ParamsEnum.templateId.name(), (Object)templateId);
	                    createInfoLogWithLabel(emplPerfInsFromTemplParams, "EmplPerfInsertFromTemplate_Roots", ParamsEnum.BaseUiLabels.name());
	                    
	                    // contextEstimatedStartDate > fromdate -> contextEstimatedStartDate
	                    // contextEstimatedCompletionDate < thruDate -> contextEstimatedCompletionDate
	                    if(contextEstimatedStartDate.after(startDate)){
	                    	startDate = contextEstimatedStartDate;
	                    }
	                    if(contextEstimatedCompletionDate.before(completionDate)){
	                    	completionDate = contextEstimatedCompletionDate;
	                    }
	                    
	                    beganTransaction = TransactionUtil.begin(ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT);
	                    
	                    // controlla se esiste gia' una scheda
	                    boolean justExists = doCheckDuplicate(gv.getString(ParamsEnum.partyId.name()), organizationId, orgUnitId, templateId, evaluator, approver, startDate, completionDate);
	
	                    // se non esiste, la crea
	                    if (!justExists) {
	                        setRecordElaborated(getRecordElaborated() + 1);
	                        doInsertFromTemplate(gv, contextShowCode, organizationId, startDate, completionDate, templateId);
	                    }
	                    
	                    // per iterazione successiva
	                    startDate = null;
	
	                    TransactionUtil.commit(beganTransaction);
	                } catch (Exception e) {
	                    msg = "Error creating CREATION FROM TEMPLATE for employee '" + gv.getString(ParamsEnum.partyId.name()) + "'";
	                    addLogError(e, msg, evalPartyId);
	                    TransactionUtil.rollback(beganTransaction, msg, e);
	                } finally {
	                    msg = "Finished CREATION FROM TEMPLATE";
	                    addLogInfo(msg, evalPartyId);
	                    evalPartyId = null;
	                }
	            }
            }
            writeSystemNote();

        } catch (Exception e) {
            msg = "Error: ";
            addLogError(e, msg, evalPartyId);
            setResult(ServiceUtil.returnError(e.getMessage()));
            try {
                TransactionUtil.rollback(e);
            } catch (GenericTransactionException gte) {
                msg = "Error during rollaback: ";
                addLogError(gte, msg);
            }
        } finally {
            String jobLogId = delegator.getNextSeqId("JobLog");
            executeWriteLogs(startTimestamp, jobLogId);

            getResult().put(ServiceLogger.BLOCKING_ERRORS, getBlockingErrors());
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
        }
    }

    /**
     * Return if exist a performance with estimatedStartDate = max(relationship.fromDate, estimatedStartDate and emplPositionTypeDate) and estimatedCompletionDate = min(relationship.thruDate, estimatedCompletionDate)
     * @param evalPartyId
     * @param orgUnitId
     * @param estimatedStartDate from context
     * @param estimatedCompletionDate from context
     * @return
     * @throws GeneralException
     */
    private boolean doCheckDuplicate(String evalPartyId, String organizationId, String orgUnitId, String templateId, String evaluator, String approver, Timestamp tempEstimatedStartDate, Timestamp tempEstimatedCompletionDate) throws GeneralException {
        return UtilValidate.isNotEmpty(getPerformanceList(evalPartyId, organizationId, orgUnitId, templateId, evaluator, approver, tempEstimatedStartDate, tempEstimatedCompletionDate));
    }

    /**
     * Search EmplPerfRootView with same dates
     * @param evalPartyId
     * @param orgUnitId
     * @param estimatedStartDate
     * @param estimatedCompletionDate
     * @return
     * @throws GeneralException
     */
    private List<GenericValue> getPerformanceList(String evalPartyId, String organizationId, String orgUnitId, String templateId, String evaluator, String approver, Timestamp tempEstimatedStartDate, Timestamp tempEstimatedCompletionDate) throws GeneralException {
        List<EntityCondition> cond = ReadConditionCreator.buildBaseCondition(evalPartyId, organizationId, orgUnitId, templateId, evaluator, approver);
        cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.estimatedStartDate.name(), tempEstimatedStartDate));
        cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.estimatedCompletionDate.name(), tempEstimatedCompletionDate));
        
        String parentTypeId = (String) context.get(EmplPerfRootViewFieldEnum.parentTypeId.name());
        cond.add(EntityCondition.makeCondition(EmplPerfRootViewFieldEnum.parentTypeId.name(), parentTypeId));
        
        EntityFindOptions entityFindOptions = new EntityFindOptions();
        entityFindOptions.setDistinct(true);
        List<GenericValue> emplPerfRootViewList = delegator.findList(EmplPerfRootViewFieldEnum.EmplPerfRootView.name(), EntityCondition.makeCondition(cond), UtilMisc.toSet(EmplPerfRootViewFieldEnum.workEffortId.name()), null, entityFindOptions, getUseCache());
        
        // log       
        if (UtilValidate.isNotEmpty(emplPerfRootViewList)) {
        	Map<String, Object> emplPerfInsFromTemplParams = UtilMisc.toMap(E.params.name(), (Object)cond.toString(), E.value.name(), (Object)emplPerfRootViewList.toString());
            createInfoLogWithLabel(emplPerfInsFromTemplParams, "EmplPerfInsertFromTemplate_PerformanceFound", ParamsEnum.BaseUiLabels.name());
        } else{
        	Map<String, Object> emplPerfInsFromTemplParams = UtilMisc.toMap(E.params.name(), (Object)cond.toString());
            createInfoLogWithLabel(emplPerfInsFromTemplParams, "EmplPerfInsertFromTemplate_NoPerformance", ParamsEnum.BaseUiLabels.name());
        }
        return emplPerfRootViewList;
    }
  
    /**
     * Check dates and create performance
     * @param gv
     * @param tempEstimatedStartDate from context
     * @param tempEstimatedCompletionDate from context
     * @param contextShowCode 
     * @param completionDate 
     * @param startDate 
     * @param organizationId 
     * @param forcedTemplateId 
     * @return 
     * @throws GeneralException
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> doInsertFromTemplate(GenericValue gv, String contextShowCode, String organizationId, Timestamp startDate, Timestamp completionDate, String templateId) throws GeneralException {
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E._AUTOMATIC_PK_.name(), E.Y.name());
        serviceMap.put(E.weContextId.name(), E.CTX_EP.name());
        serviceMap.put(E.showCode.name(), contextShowCode);
        serviceMap.put(E.evalPartyId.name(), gv.get(E.partyId.name()));
        
        serviceMap.put("tempEstimatedStartDate", startDate);
        serviceMap.put("tempEstimatedCompletionDate", completionDate);
        
        serviceMap.put(E.orgUnitId.name(), gv.get(E.orgUnitId.name()));
        serviceMap.put(E.uoRoleTypeId.name(), gv.get(E.orgUnitRoleTypeId.name()));
        serviceMap.put(E.evalManagerPartyId.name(), gv.get(ParamsEnum.evaluator.name()));
        serviceMap.put(E.templateId.name(), templateId);
        serviceMap.put(E.estimatedTotalEffort.name(), gv.get(ParamsEnum.effort.name()));
        serviceMap.put(E.description.name(), getEmplPerfDescription(templateId, gv.getString(E.partyId.name())));
        serviceMap.put(GenericService.ORGANIZATION_ID, organizationId);
        String successMsg = "Created Employment Performance for employee " + gv.getString(E.partyId.name()) + " since " + startDate + " to " + completionDate;
        Map<String, Object> res = runSyncCrud("crudServiceDefaultOrchestration_WorkEffortRootView", E.WorkEffortView.name(), E.CREATE.name(), serviceMap, successMsg, MODULE, true, false, gv.getString(E.partyId.name()));
        return UtilMisc.toMap(E.workEffortId.name(), ((Map<String, Object>)res.get(E.id.name())).get(E.workEffortId.name()));
    }
    
    /**
     * If the tempEstimatedStartDate equals context.get(E.estimatedStartDate.name()) check if there is another performance for evualuted for the same period but with different estimatedcompletionDate
     * @param templateId
     * @param evalPartyId
     * @return
     * @throws GeneralException
     */
    private String getEmplPerfDescription(String templateId, String evalPartyId) throws GenericEntityException {
        String res = null;

        if (UtilValidate.isNotEmpty(templateId) && UtilValidate.isNotEmpty(evalPartyId)) {
            GenericValue party = delegator.findOne(ParamsEnum.Party.name(), UtilMisc.toMap(ParamsEnum.partyId.name(), evalPartyId), false);

            GenericValue template = delegator.findOne(ParamsEnum.WorkEffort.name(), UtilMisc.toMap(ParamsEnum.workEffortId.name(), templateId), false);
            
            String parentRoleCode = "";
            GenericValue partyParentRole = delegator.findOne(ParamsEnum.PartyParentRole.name(), UtilMisc.toMap(ParamsEnum.partyId.name(), evalPartyId, ParamsEnum.roleTypeId.name(), ParamsEnum.EMPLOYEE.name()), false);
            if (UtilValidate.isNotEmpty(partyParentRole)) {
            	parentRoleCode = partyParentRole.getString(ParamsEnum.parentRoleCode.name());
            }
            
            res = party.getString(ParamsEnum.partyName.name());
            if (UtilValidate.isNotEmpty(parentRoleCode)) {
            	res += " (" + parentRoleCode + ")";
            }
            res += " - " + template.getString(ParamsEnum.workEffortName.name());
        }

        return res;
    }

    /**
     * Write Log for all record
     * @param startTimestamp
     * @param jobLogId
     */
    private void executeWriteLogs(Timestamp startTimestamp, String jobLogId) {
        Map<String, Object> serviceParameters = FastMap.newInstance();

        serviceParameters.put("estimatedStartDate", context.get(ParamsEnum.estimatedStartDate.name()));
        serviceParameters.put("estimatedCompletionDate", context.get(ParamsEnum.estimatedCompletionDate.name()));
        serviceParameters.put("emplPositionTypeId", context.get(ParamsEnum.emplPositionTypeId.name()));
        serviceParameters.put("orgUnitId", context.get(ParamsEnum.orgUnitId.name()));
        serviceParameters.put("orgUnitRoleTypeId", context.get(ParamsEnum.orgUnitRoleTypeId.name()));

        super.writeLogs(startTimestamp, jobLogId, serviceParameters);
    }

    /**
     * Write Note at the end of elaboration
     * @throws GenericEntityException
     */
    private void writeSystemNote() throws GenericEntityException {
        //Creazione nota
        GenericValue noteData = delegator.makeValue(NoteDataFieldEnum.NoteData.name());
        noteData.put(NoteDataFieldEnum.noteId.name(), delegator.getNextSeqId(NoteDataFieldEnum.NoteData.name()));
        noteData.put(NoteDataFieldEnum.noteName.name(), NoteDataFieldEnum.SYSTEMNOTE.name());
        noteData.put(NoteDataFieldEnum.noteDateTime.name(), UtilDateTime.nowTimestamp());
        noteData.put(NoteDataFieldEnum.noteInfo.name(), "Employment performance form created for period " + UtilDateTime.toDateString((Date)context.get(ParamsEnum.estimatedStartDate.name()), getLocale()) + " - " + UtilDateTime.toDateString((Date)context.get(ParamsEnum.estimatedCompletionDate.name()), getLocale()));
        noteData.put(NoteDataFieldEnum.noteParty.name(), userLogin.getString(NoteDataFieldEnum.partyId.name()));
        noteData.put(NoteDataFieldEnum.isPublic.name(), NoteDataFieldEnum.N.name());
        delegator.create(noteData);
    }

}
