import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

//Debug.log("***MA parameters.menuItem" + parameters.menuItem);
context.permission = "MANAGACC"; 
parameters.weContextId = "CTX_MA";

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/workeffortext/executePerformFindWorkEffortRoot.groovy", context);
return res;
