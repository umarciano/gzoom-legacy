import org.ofbiz.base.util.*;

def commentsReadOnly = false;
def commentsRequired = false;

if (! "Y".equals(parameters.detail) && UtilValidate.isNotEmpty(parameters.weTypeSubId)) {
	def inputMap = [:];
	inputMap.workEffortId = parameters.workEffortId;
	inputMap.partyId = parameters.partyId;
	inputMap.roleTypeId = parameters.roleTypeId;
	inputMap.fromDate = parameters.fromDate;
	
	def currentWepa = delegator.findOne("WorkEffortPartyAssignment", inputMap, false);
	if (UtilValidate.isNotEmpty(currentWepa) && ("END_SOST_NN".equals(currentWepa.endReplacementEnumId) || "END_SOST_NAGR".equals(currentWepa.endReplacementEnumId))) {
		commentsReadOnly = false;
		commentsRequired = true;
	} else {
		commentsReadOnly = true;
		commentsRequired = false;
	}
}

context.commentsReadOnly = commentsReadOnly;
context.commentsRequired = commentsRequired;