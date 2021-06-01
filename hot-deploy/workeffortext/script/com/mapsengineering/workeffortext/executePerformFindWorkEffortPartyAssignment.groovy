import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

res = "success";

if(UtilValidate.isNotEmpty(parameters.massiveInsertQueryString)){
	def mappa = StringUtil.strToMap(parameters.massiveInsertQueryString, "|", false);
	parameters.putAll(mappa);
	context.lookup = "Y"; // per esguire il performFind

	GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executePerformFindPartyRoleOrgHumanResView.groovy", context);
	request.setAttribute("workEffortId", context.workEffortId);
    request.setAttribute("roleTypeId", parameters.roleTypeId);
    request.setAttribute("listIt", context.listIt);
}

return res;