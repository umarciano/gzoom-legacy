package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.WeBaseInterfaceTakeOverService;
import com.mapsengineering.base.standardimport.common.WeInterfaceConstants;
import com.mapsengineering.base.standardimport.helper.WeNoteInterfaceHelper;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Elaborate Note for WorkEffort
 *
 */
public class WeNoteInterfaceTakeOverService extends WeBaseInterfaceTakeOverService {

    public static final String MODULE = WeNoteInterfaceTakeOverService.class.getName();

    private WeNoteInterfaceHelper weNoteInterfaceHelper;

    private String noteName;
    private String noteId;
    private Timestamp noteDateTime;
    private String internalNote;
    private String isMain;
    private String isHtml;
    private Long sequenceId;

    @Override
    /** Set localValue con record locale presente sul db or null in caso di nuovo inserimento. 
     * Recupera tutti i campi da externalValue, record sulla tabella di interfaccia
     * @params extLogicKey chiave logica esterna
     *  */
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        super.initLocalValue(extLogicKey);

        checkValidityNoteName();
        checkValidityNoteDateTime();

        weNoteInterfaceHelper = new WeNoteInterfaceHelper(this, getManager().getDelegator());

        String msg = " Try WorkEffortNoteAndData for id " + getWorkEffortLevelId() + " and noteName " + noteName + " and noteDateTime " + noteDateTime;
        addLogInfo(msg);

        GenericValue localValue = weNoteInterfaceHelper.getWorkEffortNoteAndData(getWorkEffortLevelId(), noteName, noteDateTime);

        noteId = checkLocaleValue(localValue, WeInterfaceConstants.WORK_EFFORT_NOTE_PREFIX, E.WorkEffortNoteAndData.name(), E.noteId.name(), E.NoteData.name());
        
    }

    @Override
    /**
     * Esegue importazione record esterno
     */
    public void doImport() throws GeneralException {
    	setImported(true);
        String msg;

        doImportWorkeffortTypeAttr();

        updateWeNoteInterface();

        createOrUpdateWorkEffortNoteAndData();

        msg = "END IMPORT WorkEffortNoteAndData " + getLocalValue().getPrimaryKey();
        addLogInfo(msg);
    }

    private void doImportWorkeffortTypeAttr() throws GeneralException {
        GenericValue workefforttypeAttr = weNoteInterfaceHelper.getWorkEffortTypeAttr(getWorkEffortTypeLevelId(), noteName);
        internalNote = workefforttypeAttr.getString(E.internalNote.name());
        isMain = workefforttypeAttr.getString(E.isMain.name());
        isHtml = workefforttypeAttr.getString(E.isHtml.name());
        sequenceId = workefforttypeAttr.getLong(E.sequenceId.name());
    }

    private void updateWeNoteInterface() {
        getExternalValue().set(E.workEffortId.name(), getWorkEffortLevelId());
        getExternalValue().set(E.workEffortRootId.name(), getWorkEffortRootId());
        getExternalValue().set(E.elabResult.name(), ImportManager.RECORD_ELAB_RESULT_OK);
    }

    private void createOrUpdateWorkEffortNoteAndData() throws GeneralException {
        GenericValue we = getLocalValue();
        String msg = "";

        Map<String, Object> serviceMapParams = setServiceMapParameters();
        Map<String, Object> result = null;

        if (UtilValidate.isEmpty(we)) {

            // Creazione WorkEffort
            msg = "Trying to create WorkEffortNoteAndData with id " + getWorkEffortLevelId() + " noteName" + noteName + " noteDateTime " + noteDateTime;
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortNoteAndData.name(), E.WorkEffortNoteAndData.name(), CrudEvents.OP_CREATE, serviceMapParams, E.WorkEffortNoteAndData.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortNoteAndData.name(), true);
        } else {
            msg = "Trying to update WorkEffortNoteAndData with id " + getWorkEffortLevelId() + " noteName" + noteName + " noteDateTime " + noteDateTime;
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortNoteAndData.name(), E.WorkEffortNoteAndData.name(), CrudEvents.OP_UPDATE, serviceMapParams, E.WorkEffortNoteAndData.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_PROBLEM_UPDATE + E.WorkEffortNoteAndData.name(), true);
        }
        Map<String, Object> keys = UtilMisc.toMap(E.workEffortId.name(), getWorkEffortLevelId(), E.noteId.name(), (Object)noteId);
        manageResult(result, E.WorkEffortNoteAndData.name(), keys);
    }

    private Map<String, Object> setServiceMapParameters() {
        GenericValue externalValue = getExternalValue();

        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.workEffortId.name(), (Object)getWorkEffortLevelId());
        serviceMapParams.put(E.noteId.name(), noteId);
        serviceMapParams.put(E.noteName.name(), noteName);
        serviceMapParams.put(E.noteDateTime.name(), noteDateTime);
        serviceMapParams.put(E.internalNote.name(), internalNote);
        serviceMapParams.put(E.isMain.name(), isMain);
        serviceMapParams.put(E.isHtml.name(), isHtml);
        serviceMapParams.put(E.sequenceId.name(), sequenceId);

        String noteInfo = externalValue.getString(E.noteInfo.name());
        serviceMapParams.put(E.noteInfo.name(), noteInfo);
        
        addNoteInfoLang(serviceMapParams, externalValue);
        
        return serviceMapParams;
    }
    
    /**
     * gestisce noteInfoLang nel caso multi lingua
     * @param serviceMapParams
     * @param externalValue
     */
    private void addNoteInfoLang(Map<String, Object> serviceMapParams, GenericValue externalValue) {
    	if (isMultiLang()) {
            String noteInfoLang = externalValue.getString(E.noteInfoLang.name());
            serviceMapParams.put(E.noteInfoLang.name(), noteInfoLang);
    	}
    }

    /**
     * 6.1.1 Check noteName 
     * @throws GeneralException
     */
    private void checkValidityNoteName() throws GeneralException {
        GenericValue externalValue = getExternalValue();
        noteName = externalValue.getString(E.noteName.name());
        if (ValidationUtil.isEmptyOrNA(noteName)) {
            String msg = "The field noteName must be not empty";
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * 6.1.1 Check noteDateTime 
     * @throws GeneralException
     */
    private void checkValidityNoteDateTime() throws GeneralException {
        GenericValue externalValue = getExternalValue();
        noteDateTime = externalValue.getTimestamp(E.noteDateTime.name());
        if (ValidationUtil.isEmptyOrNA(externalValue.getString(E.noteDateTime.name()))) {
            String msg = "The field noteDateTime must be not empty";
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
      * Override ordinamento chiavi per i log
      */
    public void addLogInfo(String msg) {
        String msgNew = getEntityName() + ": " + msg;

        Map<String, Object> map = FastMap.newInstance();
        map.put(E.sourceReferenceRootId.name(), getExternalValue().get(E.sourceReferenceRootId.name()));
        map.put(E.workEffortTypeId.name(), getExternalValue().get(E.workEffortTypeId.name()));
        map.put(E.sourceReferenceId.name(), getExternalValue().get(E.sourceReferenceId.name()));
        map.put(E.noteDateTime.name(), getExternalValue().get(E.noteDateTime.name()));
        map.put(E.noteName.name(), getExternalValue().get(E.noteName.name()));
        map.put(E.workEffortName.name(), getExternalValue().get(E.workEffortName.name()));
        
        getManager().addLogInfo(msgNew, MODULE, map.toString());
    }
}
