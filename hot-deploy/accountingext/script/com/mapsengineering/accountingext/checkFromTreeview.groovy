
import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

res = "success";

if ("Y".equals(parameters.fromGlAccountTree) && ("GlAccountClassView".equals(parameters.entityName) || "GlAccountView".equals(parameters.entityName))) {
	modelEntity = delegator.getModelEntity(parameters.entityName);
	modelEntity.getPkFieldNames().each { pkFieldName ->
		parameters[pkFieldName] = parameters.childId;
	}
}
res = org.ofbiz.base.util.GroovyUtil.runScriptAtLocation("com/mapsengineering/base/populateManagement.groovy", context);

return res;
