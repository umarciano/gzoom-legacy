import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def baseExcludedScript = "component://base/webapp/common/WEB-INF/actions/getExcludedContent.groovy";
def completeTabList = delegator.findList("ContentAssocViewTo", EntityCondition.makeCondition([EntityCondition.makeCondition("contentIdStart", context.rootFolder),
																							 EntityCondition.makeCondition("caContentAssocTypeId", "FOLDER_OF")]), null, null, null, false);

completeTabList.each { currentTab ->
	if(UtilValidate.isNotEmpty(context.uiLabelMap[currentTab.contentId]) && !currentTab.contentId.equals(context.uiLabelMap[currentTab.contentId])) {
		context[currentTab.contentId + "_title"] = context.uiLabelMap[currentTab.contentId]; 
	}
	else {
		context[currentTab.contentId + "_title"] = currentTab.description;
	}
}
