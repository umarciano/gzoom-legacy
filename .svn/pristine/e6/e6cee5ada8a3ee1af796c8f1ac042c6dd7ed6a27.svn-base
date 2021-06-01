package com.mapsengineering.base.standardimport.helper;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;

/**
 * Helper for WorkEffortNote and NoteData
 *
 */
public class WeNoteInterfaceHelper {

    private TakeOverService takeOverService;
    private Delegator delegator;

    /**
     * Constructor
     * @param takeOverService
     * @param delegator
     */
    public WeNoteInterfaceHelper(TakeOverService takeOverService, Delegator delegator) {
        this.takeOverService = takeOverService;
        this.delegator = delegator;
    }
    
    /**
     * Ritorna un WorkEffortNoteAndData se esiste o null, indipendentemente dalle date
     * @param workEffortId
     * @param roleTypeId
     * @param partyId
     * @return GenericValue WorkEffortNoteAndData
     * @throws GeneralException
     */
    public GenericValue getWorkEffortNoteAndData(String workEffortId, String noteName, Timestamp noteDateTime) throws GeneralException {
        GenericValue workEffortNoteAndData = null;
        GenericValue gv = getTakeOverService().getExternalValue();
        String msg = "";
        
        List<GenericValue> parents = getWorkEffortNoteAndDataList(workEffortId, noteName, noteDateTime);
                
        if (UtilValidate.isNotEmpty(parents) && parents.size() > 1) {
            msg = "Found more than one WorkEffortNoteAndData with  workEffortId = " + workEffortId + " noteName = " + noteName + " noteDateTime = " + noteDateTime;
            throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }else {
            workEffortNoteAndData = EntityUtil.getFirst(parents);
        }
        return workEffortNoteAndData;
    }
    
    /**
     * Ritorna lista WorkEffortNoteAndData indipendentemente dalle date
     * @param workEffortId
     * @param roleTypeId
     * @param partyId
     * @return List WorkEffortNoteAndData
     * @throws GeneralException
     */
    private List<GenericValue> getWorkEffortNoteAndDataList(String workEffortId, String noteName, Timestamp noteDateTime) throws GenericEntityException {
        List<GenericValue> parents = null;
        if(delegator != null) {
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortId.name(), workEffortId, E.noteName.name(), noteName, E.noteDateTime.name(), noteDateTime));
            parents = delegator.findList(E.WorkEffortNoteAndData.name(), cond, null, null, null, false);
        }
        return parents;
    }

    /**
     * Controlla esistenza unico roleType che corrisponde ai paramtetri passati, ritorna il generic value oppure eccezione 
     * @param roleTypeId
     * @param roleTypeDesc
     * @return GenericValue statusItem
     * @throws GeneralException
     */
    public GenericValue getWorkEffortTypeAttr(String workEffortTypeId, String noteName) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();

        List<EntityCondition> listaCondition = FastList.newInstance();

        listaCondition.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId));
        listaCondition.add(EntityCondition.makeCondition(E.attrName.name(), noteName));

        EntityCondition condition = EntityCondition.makeCondition(listaCondition);

        String foundMore = "Found more than one WorkEffortTypeAttr with condition :" + condition;
        String noFound = "No WorkEffortTypeAttr with condition :" + condition;

        return getTakeOverService().findOne(E.WorkEffortTypeAttr.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }

    /**
     * @return the takeOverService
     */
    private TakeOverService getTakeOverService() {
        return takeOverService;
    }

    /**
     * do Import WorkEffortNoteAndData
     * @param sourceReferenceRootId
     * @param workEffortRootId
     * @throws GeneralException
     */
    public void importWorkEffortNoteAndData(String sourceReferenceRootId, String workEffortRootId) throws GeneralException {
        takeOverService.doImportMulti(ImportManagerConstants.WE_NOTE_INTERFACE, UtilMisc.toMap(E.sourceReferenceRootId.name(), sourceReferenceRootId));
    }

}
