import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***OR parameters.menuItem" + parameters.menuItem);
context.permission = "ORGPERF"; 
parameters.weContextId = "CTX_OR";

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRootInqy.groovy", context);
return res;
