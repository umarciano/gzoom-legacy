//Bug 4131

import org.ofbiz.base.util.*;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.condition.*;

def dynamicView = new DynamicViewEntity();
dynamicView.addMemberEntity("WTT", "WorkEffortTypeType");
dynamicView.addAliasAll("WTT", null);

dynamicView.addMemberEntity("WTF", "WorkEffortType");
dynamicView.addAlias("WTF", "descriptionFrom", "description", null, false, false, null);
dynamicView.addViewLink("WTT", "WTF", false, ModelKeyMap.makeKeyMapList("workEffortTypeIdFrom", "workEffortTypeId"));

dynamicView.addMemberEntity("WTTO", "WorkEffortType");
dynamicView.addAlias("WTTO", "descriptionTo", "description", null, false, false, null);
dynamicView.addViewLink("WTT", "WTTO", false, ModelKeyMap.makeKeyMapList("workEffortTypeIdTo", "workEffortTypeId"));

def whereCondition = EntityCondition.makeCondition("workEffortTypeIdRoot", parameters.workEffortTypeId); 
def eli = delegator.findListIteratorByCondition(dynamicView, whereCondition, null, null, ["sequenceNum", "descriptionFrom", "descriptionTo"], null);


context.listIt = eli.getCompleteList();

/**
 * Caso di inserimento nuovo elemento
 * */
if (!"N".equals(insertMode)) {
    org.ofbiz.entity.GenericValue genericValue = delegator.makeValue(context.entityName);
    java.util.Map valueMap = javolution.util.FastMap.newInstance();
    valueMap.putAll(genericValue);
    valueMap.put('entityName', context.entityName);
    valueMap.put('parentEntityName', context.parentEntityName);
    valueMap.put("operation", "CREATE");
    if(UtilValidate.isNotEmpty(context.managementChildExtraParams)) {
        extraParams = StringUtil.strToMap(context.managementChildExtraParams);
        valueMap.putAll(extraParams);
    }
    context.listIt.add(valueMap);

}

eli.close();
