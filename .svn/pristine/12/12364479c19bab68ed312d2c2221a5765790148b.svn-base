import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import com.mapsengineering.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;

def checkPermission(permission) {
    if (security.hasPermission(permission, userLogin))
        return permission;
    return null;
}

res = "success";
if(UtilValidate.isEmpty(parameters.permission)) {
    parameters.permission = "EMPLPERF";
}

if (UtilValidate.isNotEmpty(userLogin)) {
    userLoginPermissionOrg = checkPermission(parameters.permission + "ORG_ADMIN");
    userLoginPermissionRole = checkPermission(parameters.permission + "ROLE_ADMIN");
    userLoginPermissionAdmin = checkPermission(parameters.permission + "MGR_ADMIN");
//    Debug.log("***EP parameters.userLoginPermissionOrg" + userLoginPermissionOrg);
//    Debug.log("***EP parameters.userLoginPermissionRole" + userLoginPermissionRole);
//    Debug.log("***EP parameters.userLoginPermissionAdmin" + userLoginPermissionAdmin);
    
    if(UtilValidate.isNotEmpty(userLoginPermissionOrg) && UtilValidate.isEmpty(userLoginPermissionRole)){
    // solo permessi per vedere i valutati tali che:
        parameters.entityName = "PartyEvalOrgPermissionView";
        parameters.orderBy = "parentRoleCode";
        parameters.partyUserLoginId = userLogin.userLoginId;
    }
    else if(UtilValidate.isEmpty(userLoginPermissionOrg) && UtilValidate.isNotEmpty(userLoginPermissionRole)){
    // solo permessi per vedere i valutati tali che:
        parameters.entityName = "PartyEvalRolePermissionView";
        parameters.orderBy = "parentRoleCode";
        parameters.partyUserLoginId = userLogin.userLoginId;
    }
    else if(UtilValidate.isNotEmpty(userLoginPermissionOrg) && UtilValidate.isNotEmpty(userLoginPermissionRole)){
        parameters.entityName = "PartyEvalOrgPermissionView";
        parameters.orderBy = "parentRoleCode";
        parameters.partyUserLoginId = userLogin.userLoginId;
        // parameters.orgUserLoginId = userLogin.userLoginId OR parameters.roleUserLoginId = userLogin.userLoginId
        // poichè non è possibile inserire questa condizione nella LeftJoin 
        // dopo la perform find la lista viene nuovamnte filtrata
    }
    else if(UtilValidate.isNotEmpty(userLoginPermissionAdmin)) {
        parameters.entityName = "PartyRoleViewLook";
        parameters.orderBy = "parentRoleCode";
    
    }
//    Debug.log("***EP parameters.entityName" + parameters.entityName);
}
context.executePerformFindScriptName = null;
context.executePerformFind = "Y";

res = GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeLookupPerformFind.groovy", context);

if(UtilValidate.isNotEmpty(userLoginPermissionOrg) && UtilValidate.isNotEmpty(userLoginPermissionRole)){
    listaOrg = context.listIt;
    listPartyRoleViewOrg = EntityUtil.getRelated("PartyRoleView", listaOrg);
    //Debug.log("***EP parameters.listPartyRoleViewOrg " + listPartyRoleViewOrg);
    //Debug.log("***EP parameters.listPartyRoleViewOrg " + listPartyRoleViewOrg.size());
    
    parameters.entityName = "PartyEvalRolePermissionView";
    parameters.orderBy = "parentRoleCode";
    parameters.partyUserLoginId = userLogin.userLoginId;
    res = GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeLookupPerformFind.groovy", context);
    listaRole = context.listIt;
    listPartyRoleViewRole = EntityUtil.getRelated("PartyRoleView", listaRole);
    //Debug.log("****** EP parameters.listPartyRoleViewRole " + listPartyRoleViewRole);
    //Debug.log("****** EP parameters.listPartyRoleViewRole " + listPartyRoleViewRole.size());
    
    listPartyRoleViewOrg.each { partyRoleViewOrg ->
        if(!listPartyRoleViewRole.contains(partyRoleViewOrg)) {
            listPartyRoleViewRole.add(partyRoleViewOrg);
        }
    }
    context.listIt = listPartyRoleViewRole;
}

return res;