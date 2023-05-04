import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
    context.inputFields.weTypeContentTypeId_fld0_op = 'in';
    context.inputFields.weTypeContentTypeId_fld0_value = "REPORT,JREPORT";
	GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
