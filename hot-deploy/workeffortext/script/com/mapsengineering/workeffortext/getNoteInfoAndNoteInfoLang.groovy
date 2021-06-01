import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.text.*;
import org.ofbiz.service.ServiceUtil;
import javolution.util.FastList;


result = ServiceUtil.returnSuccess();

def condList = FastList.newInstance();
condList.add(EntityCondition.makeCondition("workEffortTypeId", parameters.workEffortTypeId));
if (UtilValidate.isNotEmpty(parameters.noteName)) {
	condList.add(EntityCondition.makeCondition("noteName", parameters.noteName));
}
if (UtilValidate.isNotEmpty(parameters.noteNameLang)) {
	condList.add(EntityCondition.makeCondition("noteNameLang", parameters.noteNameLang));
}

def noteList = delegator.findList("WorkEffortTypeAttrAndNoteData", EntityCondition.makeCondition(condList), null, null, null, false);
def noteItem = EntityUtil.getFirst(noteList);
if (UtilValidate.isNotEmpty(noteItem)) {
	result.put("noteInfo", noteItem.noteInfo);
	result.put("noteInfoLang", noteItem.noteInfoLang);
}

return result;