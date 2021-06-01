import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;


def conditionList = [];
conditionList.add(EntityCondition.makeCondition("workEffortId", parameters.workEffortId));
conditionList.add(EntityCondition.makeConditionDate("fromDate", "thruDate"));
 
def workEffortContentList = delegator.findList("WorkEffortAndContentDataResourceExtended", EntityCondition.makeCondition(conditionList), null, null, null, true);

def localContext = [:];
def contentPresent = "";
localContext.delegator = delegator;
localContext.cruContent = "Y";
localContext.listIt = workEffortContentList;
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getWorkEffortAndContentDataResourceExtendedList.groovy", localContext);

context.listIt = localContext.listIt;