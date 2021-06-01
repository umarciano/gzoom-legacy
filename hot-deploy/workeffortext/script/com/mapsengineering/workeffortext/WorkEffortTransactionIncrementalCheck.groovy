/* Bug 4903 punto 3 */

import org.ofbiz.base.util.*;
import org.ofbiz.service.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import java.util.Locale;
import java.util.TimeZone;

def parametersCrud = parameters;
def parameters = parameters.parameters;

def returnMap = ServiceUtil.returnSuccess();
 
def glAccount = delegator.findOne("GlAccount", ["glAccountId": parameters.weTransAccountId], false);
if(UtilValidate.isEmpty(parameters.weTransWeId)) {
	parameters.weTransWeId = parameters.workEffortId;
}
def workEffort = delegator.findOne("WorkEffort", ["workEffortId": parameters.weTransWeId], false);


if("TREND_INC".equals(glAccount.trendEnumId) && UtilValidate.isNotEmpty(parameters.weTransMeasureId) && !"DELETE".equals(parametersCrud.operation)) {
	def groovyContext = [:];
	groovyContext.timeZone = timeZone;
    groovyContext.locale = locale;
    groovyContext.dispatcher = dctx.getDispatcher();
	groovyContext.security = dctx.getSecurity();
	groovyContext.delegator = delegator;
	groovyContext.userLogin = userLogin;
	groovyContext.parameters = parameters;
	groovyContext.parameters.workEffortMeasureId = parameters.weTransMeasureId;
	groovyContext.parameters.workEffortId = parameters.weTransWeId;
	groovyContext.parameters.insertMode = "N"; 
	def ctx = [:];
	ctx.workEffortId = parameters.weTransWeId;
	groovyContext.parameters = parameters;
	groovyContext.context = ctx;
	GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindValueIndicator.groovy", groovyContext);
	
	// GN-345
	// la lista contien tutti tanti glAccount, perche la condition per la ricerca e fatta solo su workEffortTypeIdInd = workEffort.workEffortTypeId
	// ulteriori filtri sono fatti nel ciclo seguente.
	def workEffortTransactionIndicatorList = groovyContext.listIt;
	def weTransDate = parameters.weTransDate;
	if(UtilValidate.isEmpty(weTransDate)) {
		def customTimePeriod =  delegator.findOne("CustomTimePeriod", ["customTimePeriodId": parameters.customTimePeriodId], false);
		weTransDate = customTimePeriod.thruDate;
	}
	
	def itemFound = "";
	def weTransDateLast = null;
	workEffortTransactionIndicatorList.each { item ->
		if(item.weTransTypeValueId == parameters.weTransTypeValueId) {
			if(("ACCINP_OBJ" == glAccount.inputEnumId && item.weTransMeasureId == parameters.weTransMeasureId)
			|| ("ACCINP_PRD" == glAccount.inputEnumId && item.transProductId == parameters.weTransProductId)
			|| ("ACCINP_UO" == glAccount.inputEnumId && item.weTransAccountId == glAccount.glAccountId)) {
				if("ACCDET_NULL" != glAccount.detailEnumId) {
					// controllo su entryPartyId e entryRoleTypeId
					if(((UtilValidate.isEmpty(parameters.entryPartyId) && UtilValidate.isEmpty(item.entryPartyId)) || (item.entryPartyId == parameters.entryPartyId))
						&& ((UtilValidate.isEmpty(parameters.entryRoleTypeId) && UtilValidate.isEmpty(item.entryRoleTypeId)) || (item.entryRoleTypeId == parameters.entryRoleTypeId))) {
						if(item.weTransDate != null && ((Timestamp) item.weTransDate).before(weTransDate)) {
							if(((Timestamp) item.weTransDate).after(workEffort.estimatedStartDate) && ((Timestamp) item.weTransDate).before(workEffort.estimatedCompletionDate)) {
								if(weTransDateLast == null || ((Timestamp)item.weTransDate).after((Timestamp)weTransDateLast)) {
									weTransDateLast = item.weTransDate;
									itemFound = item.weTransId;
								}
							}
						}
					}
				}else {
					if(item.weTransDate != null && ((Timestamp) item.weTransDate).before(weTransDate)) {
						if(((Timestamp) item.weTransDate).after(workEffort.estimatedStartDate) && ((Timestamp) item.weTransDate).before(workEffort.estimatedCompletionDate)) {
							if(weTransDateLast == null || ((Timestamp)item.weTransDate).after((Timestamp)weTransDateLast)) {
								weTransDateLast = item.weTransDate;
								itemFound = item.weTransId;
							}
						}
					}
				}
			}
		} 
	}
	
	if(UtilValidate.isNotEmpty(itemFound)) {
		def transEntryToCheckList = delegator.findList("AcctgTransEntry", EntityCondition.makeCondition("acctgTransId", itemFound), null, null, null, true);
		def transEntryToCheck = EntityUtil.getFirst(transEntryToCheckList);
		if(parameters.weTransValue < transEntryToCheck.amount) {
			failMessage = UtilProperties.getResourceBundleMap("WorkeffortExtErrorLabels", locale).IncrementalAmountLowerPreviuos;
			return ServiceUtil.returnFailure(failMessage);
		}
	}
}

return returnMap;