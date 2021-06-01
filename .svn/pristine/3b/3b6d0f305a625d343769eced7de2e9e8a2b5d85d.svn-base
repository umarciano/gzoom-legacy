import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

	context.inputFields.weTypeContentTypeId = "FOLDER";
	
	GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
	
	//Debug.log(" - context.inputFields.workEffortTypeId: " + context.inputFields.workEffortTypeId);
	def listReturn = [];
	if (UtilValidate.isNotEmpty(context.listIt)) {
		for (GenericValue value: context.listIt) {
			def map = [:];
			map.putAll(value);
			//Debug.log(" - value.contentId: " + value.contentId);
	
			def contentTypeList = delegator.findByAnd("WorkEffortTypeContent", [workEffortTypeId : context.inputFields.workEffortTypeId, weTypeContentTypeId : value.contentId]);

			if (UtilValidate.isNotEmpty(contentTypeList)) {
				GenericValue contentType = contentTypeList.get(0);
				/** i params sono validi solo nel layoutContentId */
				map.put("layoutContentId", contentType.contentId);
				map.put("params", contentType.params);
			}
			
			listReturn.add(map);
		}
	}
	
	context.listIt = listReturn;
	
