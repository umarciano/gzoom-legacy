import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***PM parameters.menuItem" + parameters.menuItem);
context.permission = "PROJECTMGR"; 
parameters.weContextId = "CTX_PM";

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRoot.groovy", context);
return res;
