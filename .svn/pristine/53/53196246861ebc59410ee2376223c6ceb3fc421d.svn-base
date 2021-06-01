package com.mapsengineering.workeffortext.services.rootcopy;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.workeffortext.services.E;

/**
 * Manage WorkEffortNote, for copy and snapshot
 *
 */
public class WorkEffortNoteCopy extends AbstractWorkEffortDataCopy {
	
	public static final String MODULE = WorkEffortNoteCopy.class.getName();
		
	/**
	 * Constructor
	 * @param service
	 * @param snapshot
	 */
    public WorkEffortNoteCopy(WorkEffortRootCopyService service, boolean snapshot) {
        super(service, snapshot);
    }	
	
	/**
	 * Search WorkEffortNoteAndData and execute crudService crudServiceDefaultOrchestration for snapshot, or crudServiceDefaultOrchestration_WorkEffortNoteAndData with isPosted = N for copy.<br/>
     * For copy workEffortNoteAndData.isDefault = Y 
	 */
	@Override
    public Map<String, Object> copy(String origWorkEffortId, String newWorkEffortId, Map<String, ? extends Object> data) throws GeneralException {
		
		List<EntityCondition> cond = FastList.newInstance();
        if(!isUseEnableSnapshot()){
            cond.add(EntityCondition.makeCondition(E.isDefault.name(), EntityOperator.EQUALS, E.Y.name()));
        }
        cond.add(EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.EQUALS, origWorkEffortId));
		
        List<GenericValue> attrList = getDelegator().findList(E.WorkEffortNoteAndData.name(), EntityCondition.makeCondition(cond), null, null, null, getUseCache());
        for (GenericValue attr : attrList) {
            final String noteIdOrig = attr.getString(E.noteId.name());
            Map<String, Object> serviceMap = FastMap.newInstance();
            serviceMap.putAll(attr.getAllFields());
            serviceMap.put(E.workEffortId.name(), newWorkEffortId);
            serviceMap.put(E.noteId.name(), null);
            serviceMap.put(E.noteDateTime.name(), getNoteDateTime(attr, data));
            serviceMap.put(E.isPosted.name(), getIsPosted(attr.getString(E.isPosted.name())));
            String successMsg = "Copied note " + noteIdOrig + " to WorkEffort " + newWorkEffortId;
            String errorMsg = FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortNoteAndData.name() + " note " + noteIdOrig + " to WorkEffort " + newWorkEffortId;
            runSyncCrud("crudServiceDefaultOrchestration_WorkEffortNoteAndData", E.WorkEffortNoteAndData.name(), E.CREATE.name(), serviceMap, successMsg, errorMsg, true, origWorkEffortId);
        }
        return null;
    }
	
	/**
	 * se storicizzazione ritorna noteData.noteDateTime, altrimenti workEffort.estimatedStartDate
	 * @param attr
	 * @param data
	 * @return
	 */
	private Object getNoteDateTime(GenericValue attr, Map<String, ? extends Object> data) {
	    if (isUseEnableSnapshot()) {
	        return attr.get(E.noteDateTime.name());
	    }
	    return data.get(E.estimatedStartDate.name());
	}
	
}
