import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

context.noExecuteChildPerformFind = "Y"; // si usa invocando un groovy custom e mettendolo prima di invocare executeChildPerformFind
GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
