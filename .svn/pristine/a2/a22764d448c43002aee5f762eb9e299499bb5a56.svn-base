import org.ofbiz.base.util.*;
import com.mapsengineering.base.birt.util.*;

def isReadOnlyIndicatorResponsable = false;

def glAccount = context.glAccount;
def workEffortMeasure = context.workEffortMeasure;
def we = context.we;

if (UtilValidate.isEmpty(glAccount)) {
    def glAccountId = UtilValidate.isEmpty(context.glAccountId) ? parameters.glAccountId : context.glAccountId;
    glAccount = delegator.findOne("GlAccount", ["glAccountId": glAccountId], false);
}

if (UtilValidate.isEmpty(workEffortMeasure)) {
    def workEffortMeasureId = UtilValidate.isEmpty(context.workEffortMeasureId) ? parameters.workEffortMeasureId : context.workEffortMeasureId;
    workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId": workEffortMeasureId], false);
}

/*
 * Se non sono un utente amministratore
 * 
 * Controllo se il sistema alimentante è  = 'INDICATOR_RESP' 
 * se si devo controllare che 
 * respCenterRoleTypeId = orgUnitRoleTypeId and respCenterId = orgUnitId
 * se si posso modificare sia l'indicatore che i valori
 * altrimenti deve essere readOnly
 * 
 */

def securityPermission = Utils.permissionLocalDispatcherName(dispatcher.name);
def isPermission = security.hasPermission(securityPermission+"MGR_ADMIN", context.userLogin);
context.isPermission = isPermission;

if (!isPermission && 
    (((glAccount.inputEnumId == "ACCINP_OBJ" || glAccount.inputEnumId == "ACCINP_UO") && glAccount.dataSourceId == "INDICATOR_RESP" ) ||
        glAccount.inputEnumId == "ACCINP_PRD" && workEffortMeasure.dataSourceId == "INDICATOR_RESP" )) {
    
    if (UtilValidate.isEmpty(we)) {
        def workEffortId = UtilValidate.isEmpty(context.workEffortId) ? parameters.workEffortId : context.workEffortId;
        we = delegator.findOne("WorkEffort", ["workEffortId": workEffortId], false);
    }
    /**
     * Caso1:  inputEnumId == "ACCINP_OBJ" || inputEnumId == "ACCINP_UO"
     * caso2:  inputEnumId == "ACCINP_PRD"
     */
    
    if ( (UtilValidate.isNotEmpty(glAccount.respCenterRoleTypeId) && UtilValidate.isNotEmpty(glAccount.respCenterId) &&
        (glAccount.respCenterRoleTypeId != we.orgUnitRoleTypeId || glAccount.respCenterId != we.orgUnitId)
        && (glAccount.inputEnumId == "ACCINP_OBJ" || glAccount.inputEnumId == "ACCINP_UO")) ||
        
        (UtilValidate.isNotEmpty(workEffortMeasure.orgUnitRoleTypeId) && UtilValidate.isNotEmpty(workEffortMeasure.orgUnitId) &&
        (workEffortMeasure.orgUnitRoleTypeId != we.orgUnitRoleTypeId || workEffortMeasure.orgUnitId != we.orgUnitId)
        && glAccount.inputEnumId == "ACCINP_PRD" )
    ) {
        isReadOnlyIndicatorResponsable = true;
    }
}

context.isReadOnlyIndicatorResponsable = isReadOnlyIndicatorResponsable;
//Debug.log(".............. isReadOnlyIndicatorResponsable="+isReadOnlyIndicatorResponsable);

