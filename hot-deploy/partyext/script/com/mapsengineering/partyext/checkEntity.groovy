import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

if ("Party".equals(parameters.entityName) || "PartyRoleView".equals(parameters.entityName)) {
    res = "partyrole";
}
if ("QueryConfigView".equals(parameters.entityName) && UtilValidate.isNotEmpty(parameters.queryCtx)) {
	res = "queryConfigView";
}

return res;