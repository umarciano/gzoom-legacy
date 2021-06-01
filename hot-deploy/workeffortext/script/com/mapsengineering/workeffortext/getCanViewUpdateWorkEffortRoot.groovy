import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.security.*;
import javolution.util.FastList;
import com.mapsengineering.base.util.*;
import com.mapsengineering.base.birt.util.Utils;

result = ServiceUtil.returnSuccess();
result.canViewRoot = "N";
result.canUpdateRoot = "N";

//verifico i permessi dell utente per poi cercare la scheda tramite vista o servizio di ricerca
if (UtilValidate.isEmpty(context.security)) {
    context.security = SecurityFactory.getInstance(delegator);
}
def security = dctx.getSecurity();

def rootViewConditions = [];
rootViewConditions.add(EntityCondition.makeCondition("workEffortId", parameters.workEffortRootId));
rootViewConditions.add(EntityCondition.makeCondition("uvUserLoginId", context.userLogin.userLoginId));
def rootViewList = delegator.findList("WorkEffortRootViewAllStatus", EntityCondition.makeCondition(rootViewConditions), null, null, null, false);
def rootView = EntityUtil.getFirst(rootViewList);

// Attenzione: la WorkEffortRootView potrebbe non estrarre la scheda se: 
// 1) il workEffortId e' un obiettivo, 
// 2) non esiste la workEffortTypeStatus per quella scheda
Debug.log("********************************* getCanViewUpdateWorkEffortRoot.groovy  rootView " + rootView);
if (UtilValidate.isNotEmpty(rootView)) {
    
    //utilizzano due dispacher diversi
    def localDispacher = dispatcher;
    if (dispatcher instanceof org.ofbiz.service.ServiceDispatcher ) {
        localDispacher = dispatcher.getLocalDispatcher("base");
    }
    
    String permission = ContextPermissionPrefixEnum.getPermissionPrefix(rootView.weContextId);
    def userLoginPermissionView = security.hasEntityPermission(permission + "MGR", "_VIEW", userLogin);
    Debug.log("********************************* getCanViewUpdateWorkEffortRoot.groovy userLoginId" + userLogin.userLoginId + " for permission " + permission + " has userLoginPermissionView " + userLoginPermissionView);
    if (userLoginPermissionView) {
        def mapService = Utils.getMapUserPermision(security, rootView.weContextId, context.userLogin, null);
        mapService.remove("workEffortRevisionId");
        mapService.put("workEffortId", parameters.workEffortRootId);
        //Debug.log(" - mapService " + mapService);
        
        //se sono org, role, top o sup cerco la scheda con il servizio di ricerca apposito
        if (mapService.isOrgMgr || mapService.isRole || mapService.isSup || mapService.isTop) {
            def rootSearchRootInqyServiceRes = localDispacher.runSync("executeChildPerformFindWorkEffortRootInqy", mapService);
            if(UtilValidate.isNotEmpty(rootSearchRootInqyServiceRes) && UtilValidate.isNotEmpty(rootSearchRootInqyServiceRes.rowList)) { 
                Debug.log(" - rootSearchRootInqyServiceRes.rowList " + rootSearchRootInqyServiceRes.rowList);
                result.canViewRoot = "Y";
                
                def rootSearchServiceRes = localDispacher.runSync("executeChildPerformFindWorkEffortRoot", mapService);
                Debug.log(" - rootSearchServiceRes.rowList " + rootSearchServiceRes.rowList);
                if (UtilValidate.isNotEmpty(rootSearchServiceRes.rowList) && rootSearchServiceRes.rowList.size() > 0) {
                    result.canUpdateRoot = "Y";
                }
            }
        } else {
            result.canViewRoot = "Y";
            result.canUpdateRoot = "Y";
        }
    }
}

Debug.log("********************************* getCanViewUpdateWorkEffortRoot.groovy result " + result);

return result;