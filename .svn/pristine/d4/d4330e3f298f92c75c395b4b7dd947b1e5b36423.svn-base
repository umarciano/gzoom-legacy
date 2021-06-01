import org.ofbiz.base.util.*;
import com.mapsengineering.base.birt.util.*;


def securityPermission = Utils.permissionLocalDispatcherName(dispatcher.name);
def isAdmin = security.hasPermission(securityPermission+"MGR_ADMIN", context.userLogin);


context.isSeqNumReadOnly = false;
context.isAssocWeightReadOnly = false;
context.isUomDescrReadOnly = false;

if (! isAdmin) {
	def workEffortType = null;
	
	def workEffortTypeId = (UtilValidate.isNotEmpty(context.workEffortTypeId) ? context.workEffortTypeId : parameters.workEffortTypeId);
	if(UtilValidate.isNotEmpty(workEffortTypeId)) {
		workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : workEffortTypeId], false);
	}
	
	if(UtilValidate.isEmpty(workEffortType)) {
		def workEffortId = (UtilValidate.isNotEmpty(context.workEffortId) ? context.workEffortId : parameters.workEffortId);		
		if(UtilValidate.isNotEmpty(workEffortId)) {
			def workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
			if(UtilValidate.isNotEmpty(workEffort) && UtilValidate.isNotEmpty(workEffort.workEffortTypeId)) {
				workEffortType = delegator.findOne("WorkEffortType", ["workEffortTypeId" : workEffort.workEffortTypeId], false);
			}			
		}
	}
	
	if (UtilValidate.isNotEmpty(workEffortType)) {
		if (workEffortType.hasPersonFilter == 'Y' && ((workEffortType.evalEnumId == 'EVAL_IMP') || (workEffortType.evalEnumId == 'EVAL_IMP_UO'))) {
			context.isSeqNumReadOnly = true;
			context.isAssocWeightReadOnly = true;
			context.isUomDescrReadOnly = true;
		}
	}
}
