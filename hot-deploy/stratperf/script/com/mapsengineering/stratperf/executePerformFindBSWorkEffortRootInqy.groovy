import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

context.permission = "BSCPERF";
parameters.weContextId = "CTX_BS";

GroovyUtil.runScriptAtLocation("com/mapsengineering/stratperf/applyScopingBSFilter.groovy", context);

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRootInqy.groovy", context);
return res;
