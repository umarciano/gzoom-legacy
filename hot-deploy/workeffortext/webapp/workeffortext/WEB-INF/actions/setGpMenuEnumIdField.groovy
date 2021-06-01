import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

if (UtilValidate.isEmpty(parameters.gpMenuEnumId) && UtilValidate.isNotEmpty(context.workEffortType)) {
	context.gpMenuEnumId = context.workEffortType.gpMenuEnumId;
}