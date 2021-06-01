import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;

def screenNameListIndex = "";

if (UtilValidate.isNotEmpty(parameters.isManagement) && "Y".equals(parameters.isManagement)) {
	screenNameListIndex = "1";
}

Debug.log("-------------------- screenNameListIndex=" + screenNameListIndex);
return screenNameListIndex;