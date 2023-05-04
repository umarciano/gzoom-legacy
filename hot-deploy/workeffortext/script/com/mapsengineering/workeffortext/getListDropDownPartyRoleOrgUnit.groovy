import org.ofbiz.base.util.*;
import org.ofbiz.service.*;

import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

result = ServiceUtil.returnSuccess();

GroovyUtil.runScriptAtLocation("component://base/webapp/base/WEB-INF/actions/setLocaleSecondary.groovy", context);
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/evaluateCtxTypeParams.groovy", context);
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/executePerformFindPartyRoleOrgUnit.groovy", context);


Debug.log("### getListDropDownPartyRoleOrgUnit.groovy -> context.orgUnitDisplayField="+context.orgUnitDisplayField);
Debug.log("### getListDropDownPartyRoleOrgUnit.groovy -> context.orgUnitId="+context.orgUnitId);

def orgUnitDesc = "";
List orgUnitIdList = [];
List resultList = [];
for(ele in context.partyRoleList) {
     Map resultMap = [:];
     //Company : "ORGUNIT001 - Regione Lombardia"
     if (UtilValidate.isNotEmpty(context.codeField)) {
    	 resultMap.put(ele.partyId, ele.get(context.codeField) + " - " + ele.get(context.orgUnitDisplayField)); 
     } else {
    	 resultMap.put(ele.partyId, ele.get(context.orgUnitDisplayField));
     }
     if (UtilValidate.isNotEmpty(context.orgUnitId) && context.orgUnitId.equals(ele.partyId)) {
    	 if (UtilValidate.isNotEmpty(context.codeField)) {
    		 orgUnitDesc = ele.get(context.codeField) + " - " + ele.get(context.orgUnitDisplayField);
    	 } else {
    		 orgUnitDesc = ele.get(context.orgUnitDisplayField); 
    	 }
     }
     orgUnitIdList.push(ele.partyId);
     resultList.push(resultMap);
}


result.result = resultList;
result.orgUnitDesc = orgUnitDesc;
result.orgUnitIdList = orgUnitIdList;
return result;