import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***BS parameters.menuItem" + parameters.menuItem);
context.permission = "CDGPERF"; 
parameters.weContextId = "CTX_CG";

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRootInqy.groovy", context);
return res;
