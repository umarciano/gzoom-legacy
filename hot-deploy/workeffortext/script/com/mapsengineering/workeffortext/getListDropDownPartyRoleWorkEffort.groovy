import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.workeffortext.util.WorkEffortTypeCntParamsEvaluator;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

result = ServiceUtil.returnSuccess();
Debug.log("### parameters.index = " + parameters.index);
def condList = [];
condList.add(EntityCondition.makeCondition("wefromWetoEnumId", parameters.wefromWetoEnumId));
condList.add(EntityCondition.makeCondition("isUnique", "Y"));
condList.add(EntityCondition.makeCondition("workEffortTypeId", parameters.workEffortTypeId));
condList.add(EntityCondition.makeCondition("workEffortAssocTypeId", parameters.workEffortAssocTypeId));
Debug.log("### condList = " + condList);
def workEffortTypeAssocList = delegator.findList("WorkEffortTypeAssoc", EntityCondition.makeCondition(condList), null, null, null, false);
Debug.log("### workEffortTypeAssocList = " + workEffortTypeAssocList);
if (UtilValidate.isNotEmpty(workEffortTypeAssocList)) {
    workEffortTypeAssoc = EntityUtil.getFirst(workEffortTypeAssocList);
    
    if (UtilValidate.isNotEmpty(workEffortTypeAssoc)) {
        WorkEffortTypeCntParamsEvaluator paramsEvaluator = new WorkEffortTypeCntParamsEvaluator(parameters, parameters, delegator);
        def map = paramsEvaluator.getParams(parameters.workEffortTypeId, workEffortTypeAssoc.contentId, false);
        Debug.log("### map = " + map);
        if (UtilValidate.isNotEmpty(map)) {
            parameters.assocLevelSameUO = map.get("assocLevelSameUO");
            parameters.assocLevelParentUO = map.get("assocLevelParentUO");
            parameters.assocLevelChildUO = map.get("assocLevelChildUO");
            parameters.assocLevelSisterUO = map.get("assocLevelSisterUO");
            parameters.assocLevelTopUO = map.get("assocLevelTopUO");
            parameters.assocLevelSameUOAss = map.get("assocLevelSameUOAss");
            parameters.assocLevelParentUOAss = map.get("assocLevelParentUOAss");
            parameters.assocLevelChildUOAss = map.get("assocLevelChildUOAss");
            parameters.assocLevelSisterUOAss = map.get("assocLevelSisterUOAss");
            parameters.assocLevelTopUOAss = map.get("assocLevelTopUOAss");
        }
    }
}

// Debug.log("### getListDropDownPartyRoleWorkEffort.groovy -> parameters.orgUnitId="+parameters.orgUnitId);
// Debug.log("### getListDropDownPartyRoleWorkEffort.groovy -> parameters.orgUnitId="+parameters.orgUnitRoleTypeId);
// Debug.log("### getListDropDownPartyRoleWorkEffort.groovy -> context.orgUnitId="+context.orgUnitId);
// Debug.log("### getListDropDownPartyRoleWorkEffort.groovy -> context.orgUnitId="+context.orgUnitRoleTypeId);
GroovyUtil.runScriptAtLocation("component://base/webapp/base/WEB-INF/actions/setLocaleSecondary.groovy", context);
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getOrgUnitIdList.groovy", context);

result.index = parameters.index;
result.orgUnitIdList = context.orgUnitIdList;
result.wepaPartyIdList = context.wepaPartyIdList;
return result;