import org.ofbiz.base.util.*;
import org.ofbiz.service.*;

import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

result = ServiceUtil.returnSuccess();

GroovyUtil.runScriptAtLocation("component://base/webapp/base/WEB-INF/actions/setLocaleSecondary.groovy", context);
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executePerformFindPartyRoleOrgUnit.groovy", context);


Debug.log("### getListDropDownPartyRoleOrgUnit.groovy -> context.orgUnitDisplayField="+context.orgUnitDisplayField);
Debug.log("### getListDropDownPartyRoleOrgUnit.groovy -> context.orgUnitId="+context.orgUnitId);

def orgUnitDesc = "";
List orgUnitIdList = [];
List resultList = [];
for(ele in context.partyRoleList) {
     Map resultMap = [:];
     //Company : "ORGUNIT001 - Regione Lombardia"
     resultMap.put(ele.partyId, ele.parentRoleCode + " - " + ele.get(context.orgUnitDisplayField));
     if (UtilValidate.isNotEmpty(context.orgUnitId) && context.orgUnitId.equals(ele.partyId)) {
         orgUnitDesc = ele.parentRoleCode + " - " + ele.get(context.orgUnitDisplayField);
     }
     orgUnitIdList.push(ele.partyId);
     resultList.push(resultMap);
}


result.result = resultList;
result.orgUnitDesc = orgUnitDesc;
result.orgUnitIdList = orgUnitIdList;
return result;