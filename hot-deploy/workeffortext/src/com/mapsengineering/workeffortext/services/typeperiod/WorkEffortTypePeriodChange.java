package com.mapsengineering.workeffortext.services.typeperiod;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.services.E;

public class WorkEffortTypePeriodChange extends GenericService {

    public static final String MODULE = WorkEffortTypePeriodChange.class.getName();
    private static final String SERVICE_NAME = "openPeriodWorkEffortTypePeriod";
    private static final String SERVICE_TYPE = "WORK_EFFORT_TYPE_PERIOD";
    
    private static String ACTSTATUS_REPLACED = "ACTSTATUS_REPLACED";
    private static String GLFISCTYPE_TARGET = "GLFISCTYPE_TARGET";
            
    private List<GenericValue> list = FastList.newInstance();
    private GenericValue statusItem = null;
    private GenericValue customTypePeriod = null;
    
    
    public static Map<String, Object> openPeriodWorkEffortTypePeriod(DispatchContext dctx, Map<String, Object> context) {
        WorkEffortTypePeriodChange obj = new WorkEffortTypePeriodChange(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }
    
    
    public WorkEffortTypePeriodChange(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE); 
    }
    

    /**
     * Main loop
     */
    public void mainLoop() {
        setResult(ServiceUtil.returnSuccess());
        Timestamp startTimestamp = UtilDateTime.nowTimestamp();
        
        addWorkeffortExtUiLabelsLog("StartWorkEffortTypePeriodChange", " workEffortTypePeriodId=" + (String)context.get(E.workEffortTypePeriodId.name()) +  " : startTimestamp=" + startTimestamp);
        try {
            //
            customTypePeriod = getCustomTypePeriod((String)context.get(E.workEffortTypePeriodId.name()));
            statusItem = getStatusItem((String)customTypePeriod.get(E.statusTypeId.name()));
            
            
            findWorkEffort();
            getDelegator().storeAll(list);
            
            addWorkeffortExtUiLabelsLog("WorkEffortTypePeriodChangeTotalElement", " " + list.size());
            
        } catch (Exception e) {
            setResult(ServiceUtil.returnError(e.getMessage()));
            JobLogLog typePeriod  = new JobLogLog().initLogCode("BaseUiLabels", "BaseError", null, getLocale());
            addLogError(e, typePeriod.getLogCode(), typePeriod.getLogMessage());
            
            
        } finally {
            String jobLogId = delegator.getNextSeqId("JobLog");
            getResult().put(ServiceLogger.JOB_LOG_ID, jobLogId);
            getResult().put(ServiceLogger.RECORD_ELABORATED, getRecordElaborated());
            
            Timestamp endTimestamp = UtilDateTime.nowTimestamp();
            addWorkeffortExtUiLabelsLog("EndWorkEffortTypePeriodChange", " - " + endTimestamp);
            writeLogs(startTimestamp, jobLogId);
        }
    }
    
    private void addWorkeffortExtUiLabelsLog(String name, String log) {
        JobLogLog typePeriod = new JobLogLog().initLogCode("WorkeffortExtUiLabels", name, null, getLocale());
        addLogInfo(typePeriod.getLogMessage() + log, null);

    }
    
    private GenericValue getCustomTypePeriod(String workEffortTypePeriodId) throws GenericEntityException {
        if (UtilValidate.isNotEmpty(customTypePeriod)) {
            return customTypePeriod;
        }
        customTypePeriod = EntityUtil.getFirst(getDelegator().findList(E.WorkEffortTypePeriodAndCustomTimePeriodView.name(), EntityCondition.makeCondition(E.workEffortTypePeriodId.name(), workEffortTypePeriodId), null, null, null, false));
        if (UtilValidate.isEmpty(customTypePeriod)) {
            JobLogLog statusItemLog = new JobLogLog().initLogCode("WorkeffortExtUiLabels", "WorkEffortTypePeriodChangeErrorWorkEffortTypePeriodId", UtilMisc.toMap(E.workEffortTypePeriodId.name(), (Object)workEffortTypePeriodId), getLocale());
            throw new GenericEntityException(statusItemLog.getLogMessage() + " - " + customTypePeriod);
        }
        return customTypePeriod;
    }
    
    private GenericValue getStatusItem(String statusTypeId) throws GenericEntityException {
        if (UtilValidate.isNotEmpty(statusItem)) {
            return statusItem;
        }
        statusItem = EntityUtil.getFirst(getDelegator().findList(E.StatusItem.name(), EntityCondition.makeCondition(E.statusTypeId.name(), statusTypeId), null, UtilMisc.toList(E.sequenceId.name()), null, false));
        if (UtilValidate.isEmpty(statusItem)) {
            JobLogLog statusItemLog = new JobLogLog().initLogCode("WorkeffortExtUiLabels", "WorkEffortTypePeriodChangeErrorStatusTypeId", UtilMisc.toMap(E.statusTypeId.name(), (Object)statusTypeId), getLocale());
            throw new GenericEntityException(statusItemLog.getLogMessage() + " - " + statusTypeId);
        }
        return statusItem;
    }
    
   /**
    *  Cerco tutte le schede
    * @throws GenericEntityException
    */
    private void findWorkEffort() throws GenericEntityException {
        
        Timestamp noteDateTime = (Timestamp)customTypePeriod.get(E.thruDate.name());
        if (GLFISCTYPE_TARGET.equals(customTypePeriod.getString(E.glFiscalTypeEnumId.name()))) {
            noteDateTime = (Timestamp)customTypePeriod.get(E.fromDate.name());
        }
        
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), customTypePeriod.get(E.workEffortTypeId.name())));
        conditionList.add(EntityCondition.makeCondition(E.organizationId.name(), context.get(E.organizationId.name())));
        conditionList.add(EntityCondition.makeCondition(E.estimatedStartDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, customTypePeriod.get(E.thruDate.name())));
        conditionList.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, customTypePeriod.get(E.fromDate.name())));
        conditionList.add(EntityCondition.makeCondition(E.actStEnumId.name(),  EntityOperator.NOT_EQUAL, ACTSTATUS_REPLACED));
        conditionList.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition(E.workEffortTypePeriodId.name(),  EntityOperator.NOT_EQUAL, (String)context.get(E.workEffortTypePeriodId.name())), 
                    EntityOperator.OR,
                    EntityCondition.makeCondition(E.workEffortTypePeriodId.name(),  EntityOperator.EQUALS, null)));

        
        addWorkeffortExtUiLabelsLog("SearchParameters", " : " + conditionList);
        List<GenericValue> listWorkEffort = getDelegator().findList(E.WorkEffortStatusView.name(), EntityCondition.makeCondition(conditionList), null, null, null, false);
        addWorkeffortExtUiLabelsLog("WorkEffortListSize", " : " + listWorkEffort.size());
        for(GenericValue gv: listWorkEffort) {           
            //per ogni scheda vado a prendere tutti gli obiettivi
            updateWorkEffortChild((String)gv.get(E.workEffortId.name()), noteDateTime);
        }
    }
    
    /**
     * Per la lista degli obiettivi figli della scheda (compresa la scheda):
     *  - creo WorkEffortStatus e modifico lo stato e
     *      - sono il padre modifico anche il workEffortTypePeriodId
     *      
     *   - e creo una nota, ma devo controllare se non e' stata gia' creata prima
     * @param workEffortId
     * @param noteDateTime
     * @throws GenericEntityException
     */
    private void updateWorkEffortChild(String workEffortId, Timestamp noteDateTime) throws GenericEntityException {
        List<GenericValue> listWorkEffort = getDelegator().findList(E.WorkEffort.name(), EntityCondition.makeCondition(E.workEffortParentId.name(), workEffortId), null, null, null, false);
        
        JobLogLog typePeriod = new JobLogLog().initLogCode("WorkeffortExtUiLabels", "WorkEffortListSize", null, getLocale());
        addLogInfo("workEffortId=" + workEffortId + " - " + typePeriod.getLogMessage() + listWorkEffort.size(), null);
        
        for(GenericValue gv: listWorkEffort) {
            //imposto stato - create WorkEffortStatus
            GenericValue ws = getDelegator().makeValue(E.WorkEffortStatus.name());
            ws.set(E.workEffortId.name(), gv.get(E.workEffortId.name()));
            ws.set(E.statusId.name(), statusItem.get(E.statusId.name()));
            ws.set(E.statusDatetime.name(), UtilDateTime.nowTimestamp());
            ws.create();
            
            // imposta stato su figli schede da trattare
            gv.set(E.currentStatusId.name(), statusItem.get(E.statusId.name()));
            if (gv.get(E.workEffortId.name()).equals(gv.get(E.workEffortParentId.name()))) {
                //sono il padre e imposto anche il  workEffortTypePeriodId
                gv.set(E.workEffortTypePeriodId.name(), (String)context.get(E.workEffortTypePeriodId.name()));
            }
            list.add(gv);
            
            findWorkEffortAttr(gv.getString(E.workEffortId.name()), gv.getString(E.workEffortTypeId.name()), noteDateTime);
            
            setRecordElaborated(getRecordElaborated() +1);
        }
    }
    
    /**
     * Per ogni obiettivo/scheda prendo la lista degli attr di tipo nota
     * @param workEffortTypeId
     * @param noteDateTime
     * @throws GenericEntityException
     */
    private void findWorkEffortAttr(String workEffortId, String workEffortTypeId, Timestamp noteDateTime) throws GenericEntityException {
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId));
        conditionList.add(EntityCondition.makeCondition(E.isNote.name(), "Y"));
        conditionList.add(EntityCondition.makeCondition(E.isAutomatic.name(), customTypePeriod.getString(E.glFiscalTypeEnumId.name())));       
        conditionList.add(EntityCondition.makeCondition(
        		EntityCondition.makeCondition(E.periodNum.name(), customTypePeriod.getLong(E.periodNum.name())), 
                EntityOperator.OR,
                EntityCondition.makeCondition(E.periodNum.name(), EntityOperator.EQUALS, null)));
        
        List<GenericValue> list = getDelegator().findList(E.WorkEffortTypeAttrAndNoteData.name(), EntityCondition.makeCondition(conditionList), null, null, null, false);
        for (GenericValue gv: list) {
            addLogInfo("Controll WorkeffortNote: workEffortId=" + workEffortId + " noteDateTime=" + noteDateTime + " noteName" +  gv.get(E.noteName.name()), null);
            createWorkEffortNote(workEffortId, noteDateTime, gv);
        }
    }
   
    /**
     * Per ogni attibuto controllo se non ci sono note esistenti, se nn ci sono allora la creo
     * @param gv
     * @param noteDateTime
     * @throws GenericEntityException
     */
    private void createWorkEffortNote(String workEffortId, Timestamp noteDateTime, GenericValue gv) throws GenericEntityException {
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));
        conditionList.add(EntityCondition.makeCondition(E.noteDateTime.name(), noteDateTime));
        conditionList.add(EntityCondition.makeCondition(E.noteName.name(), gv.get(E.noteName.name())));
        List<GenericValue> listNote = getDelegator().findList(E.WorkEffortNoteAndData.name(), EntityCondition.makeCondition(conditionList), null, null, null, false);
        if (UtilValidate.isEmpty(listNote)) {
            // create NoteData
            GenericValue noteData = getDelegator().makeValue(E.NoteData.name());
            String noteId = delegator.getNextSeqId(E.NoteData.name());
            noteData.set(E.noteId.name(), noteId);
            noteData.set(E.noteName.name(), gv.get(E.noteName.name()));
            noteData.set(E.noteNameLang.name(), gv.get(E.noteNameLang.name()));
            noteData.set(E.noteDateTime.name(), noteDateTime);
            noteData.create();
            
            // create WorkEffortNote                
            GenericValue workEffortNote = getDelegator().makeValue(E.WorkEffortNote.name());
            workEffortNote.set(E.workEffortId.name(), workEffortId);
            workEffortNote.set(E.noteId.name(), noteId);
            workEffortNote.set(E.internalNote.name(), gv.get(E.internalNote.name()));
            workEffortNote.set(E.isMain.name(), gv.get(E.isMain.name()));
            workEffortNote.set(E.isHtml.name(), gv.get(E.isHtml.name()));
            workEffortNote.set(E.sequenceId.name(), gv.get(E.sequenceId.name()));
            workEffortNote.create();
            
            addLogInfo("Create WorkeffortNote: workEffortId=" + workEffortId + " noteDateTime=" + noteDateTime + " noteName" +  gv.get(E.noteName.name()), null);
            
        }
    
    }
}
