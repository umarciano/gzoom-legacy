import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";
context.permission = "PROCPERF";

res = GroovyUtil.runScriptAtLocation("com/mapsengineering/base/checkEntityAdmin.groovy", context);
return res;
