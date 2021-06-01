import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;

if(UtilValidate.isNotEmpty(parameters.fromAssoc) && "Y".equals(parameters.fromAssoc)) {
	def isTypeReadonly = (UtilValidate.isNotEmpty(parameters.isTypeReadonly) ? parameters.isTypeReadonly : "Y");
	def workEffortTypeId = parameters.workEffortTypeId;
	if(UtilValidate.isNotEmpty(workEffortTypeId)) {
		def workEffortTypeIdList = StringUtil.split(workEffortTypeId, ",");
		if(UtilValidate.isNotEmpty(workEffortTypeIdList)) {
			if(workEffortTypeIdList.size() > 1 || "![null-field]".equals(workEffortTypeIdList.get(0))) {
				context.workEffortTypeId = "";
				parameters.workEffortTypeId = "";
				isTypeReadonly = (UtilValidate.isNotEmpty(parameters.isTypeReadonly) ? parameters.isTypeReadonly : "N");			
			}
		}
	}
	parameters.isTypeReadonly = isTypeReadonly;

	GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/getWorkEffortTypeListAssoc.groovy", context);
}
