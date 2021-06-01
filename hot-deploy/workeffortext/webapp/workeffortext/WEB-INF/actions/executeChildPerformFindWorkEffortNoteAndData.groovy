import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

Debug.log(" - execute ");
def nowStamp = UtilDateTime.nowTimestamp();

parameters.noteContentId = UtilValidate.isEmpty(parameters.noteContentId) ? "WEFLD_NOTE" : parameters.noteContentId;
context.inputFields.noteContentId = UtilValidate.isEmpty(context.inputFields.noteContentId) ? "WEFLD_NOTE" : context.inputFields.noteContentId;

context.inputFields.isMain_op = "notEqual";
context.inputFields.isMain = "Y";

context.sortField = "-noteDateTime|sequenceId";

Debug.log(" Search " + context.entityName + " with condition " + context.inputFields);

GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/evaluateParamsWorkEffortNoteAndData.groovy", context);
GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
def nowStamp2 = UtilDateTime.nowTimestamp();

Debug.log(" Found " + context.listIt);
Debug.log(" - execute " + (nowStamp2.getTime() - nowStamp.getTime() ));
