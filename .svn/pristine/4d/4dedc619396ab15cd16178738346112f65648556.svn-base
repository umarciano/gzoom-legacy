import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

def workEffortId = UtilValidate.isNotEmpty(parameters.workEffortId) ? parameters.workEffortId : parameters.parentWorkEffortId;
// parentWorkEffortId e' il workeffort attuale
if(UtilValidate.isNotEmpty(workEffortId)) {
    context.parentWe = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
}
