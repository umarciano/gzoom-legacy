import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def getNoteDataInfo(index, workEffortMainNoteList, noteDataList) {
	def result = [:];
	if (UtilValidate.isNotEmpty(noteDataList) && index < noteDataList.size()) {
		def noteId = workEffortMainNoteList[index].noteId;
		if (UtilValidate.isNotEmpty(noteId)) {
			result["noteId" + (index+1)] = noteId;
			def noteDataValue = EntityUtil.getFirst(EntityUtil.filterByCondition(noteDataList, EntityCondition.makeCondition("noteId", noteId)));
			if (UtilValidate.isNotEmpty(noteDataValue)) {
				result["noteName" + (index+1)] = noteDataValue.noteName;
				result["noteName" + (index+1) + "Lang"] = noteDataValue.noteNameLang;
				result["noteInfo" + (index+1)] = noteDataValue.noteInfo;
				result["noteInfo" + (index+1) + "Lang"] = noteDataValue.noteInfoLang;
			}
		}
	}
	
	result;
}

def workEffortId = context.workEffortId;
if (UtilValidate.isEmpty(workEffortId)) {
	workEffortId = parameters.workEffortId;
}

if (UtilValidate.isNotEmpty(workEffortId)) {
	EntityCondition entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("workEffortId", workEffortId), EntityCondition.makeCondition("isMain", "Y"));
	def workEffortMainNoteList = delegator.findList("WorkEffortNote", entityCondition, null, ["sequenceId"], null, true);
	if (UtilValidate.isNotEmpty(workEffortMainNoteList)) {
		def noteDataList = EntityUtil.getRelated("NoteData", workEffortMainNoteList);
		def result = getNoteDataInfo(0, workEffortMainNoteList, noteDataList);
		if (UtilValidate.isNotEmpty(result)) {
			context.putAll(result);
		}
		result = getNoteDataInfo(1, workEffortMainNoteList, noteDataList);
		if (UtilValidate.isNotEmpty(result)) {
			context.putAll(result);
		}
	}
}