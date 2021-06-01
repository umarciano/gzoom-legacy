import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

	
GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);

def contentTypeList = delegator.findByAnd("ContentType", [parentTypeId : "AN_LAYOUT"]);

def listKey = [];
if (UtilValidate.isNotEmpty(contentTypeList)) {
	for (GenericValue value: contentTypeList) {
		listKey.add(value.contentTypeId);
	}
}
def listReturn = [];
if (UtilValidate.isNotEmpty(context.listIt)) {
	for (GenericValue value: context.listIt) {
		def map = [:];
		map.putAll(value);
		if (UtilValidate.isNotEmpty(value.weTypeContentTypeId)) {
			if (listKey.contains(value.weTypeContentTypeId)) {
				listReturn.add(map);
			}
		}else{
			listReturn.add(map);
		}
	}
}

context.listIt = listReturn;
