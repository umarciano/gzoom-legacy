import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";
if ("GlAccount".equals(parameters.entityName) || "GlAccountWithWorkEffortPurposeType".equals(parameters.entityName) || "GlAccountAndGlAccountOrganization".equals(parameters.entityName)) {
    res = "glaccount";
} 

if (res == "success") {
    res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/checkExportSearchResult.groovy", context);
}

return res;