import org.ofbiz.base.util.*;
import org.ofbiz.service.*;

import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

result = ServiceUtil.returnSuccess();

def roleTypeIdOut = "";
def roleTypeDescOut = "";

GroovyUtil.runScriptAtLocation("component://base/webapp/base/WEB-INF/actions/setLocaleSecondary.groovy", context);
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getOrgUnitRoleTypeList.groovy", context);

List resultList = [];
if (UtilValidate.isNotEmpty(context.orgUnitRoleTypeList)) {
	for(ele in context.orgUnitRoleTypeList) {
	     Map resultMap = [:];
	     resultMap.put(ele.roleTypeId, ele.get(context.orgUnitRoleTypeDisplayField));
	     resultList.push(resultMap);
	     roleTypeIdOut = ele.roleTypeId;
	     roleTypeDescOut = ele.get(context.orgUnitRoleTypeDisplayField);
	}
}

result.roleTypeList = resultList;
result.roleTypeIdOut = roleTypeIdOut;
result.roleTypeDescOut = roleTypeDescOut;

return result;