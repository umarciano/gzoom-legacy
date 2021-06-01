package com.mapsengineering.emplperf.update.note;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants.MessageCode;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.emplperf.update.EmplPerfServiceEnum;
import com.mapsengineering.emplperf.update.EmplPerfValueUpdate;

/**
 * Update workEffort note from previous for emplperf 
 *
 */
public class EmplPerfValueUpdateNote extends EmplPerfValueUpdate {

    /**
     * Update noteInfo, only if is it not null
     */
    protected JobLogger doAction(JobLogger jobLogger, Delegator delegator, GenericValue gv, Map<String, Object> context) throws GeneralException {
        return doUpdate(jobLogger, delegator, gv.getString(EmplPerfServiceEnum.noteId.name()), gv.getString(EmplPerfServiceEnum.oldNoteInfo.name()));
    }

    /**
     * Update noteInfo, only if is it not null
     */
    private JobLogger doUpdate(JobLogger jobLogger, Delegator delegator, String newNoteId, String oldNoteInfo) throws GeneralException {
        GenericValue valueToUpdate = delegator.findOne(EmplPerfServiceEnum.NoteData.name(), false, EmplPerfServiceEnum.noteId.name(), newNoteId);
        if (UtilValidate.isNotEmpty(valueToUpdate) && UtilValidate.isNotEmpty(oldNoteInfo) && !oldNoteInfo.equals(valueToUpdate.getString(EmplPerfServiceEnum.noteInfo.name()))) {
            jobLogger.addMessage(ServiceLogger.makeLogInfo("Update comment for note with id = " + newNoteId, MessageCode.INFO_GENERIC.toString(), null, null, null));
            valueToUpdate.setString(EmplPerfServiceEnum.noteInfo.name(), oldNoteInfo);
            valueToUpdate.store();
        }
        return jobLogger;
    }

    @Override
    protected List<String> getKey() {
        return UtilMisc.toList(EmplPerfServiceEnum.noteId.name());
    }

    @Override
    protected Set<String> getFieldsToSelect() {
        return null;
    }

    @Override
    protected boolean executeStandardImport() {
        return false;
    }

	@Override
	protected String getEntityListToImport() {
		return null;
	}

	@Override
	protected List<String> getOrderBy() {
		return null;
	}
}
