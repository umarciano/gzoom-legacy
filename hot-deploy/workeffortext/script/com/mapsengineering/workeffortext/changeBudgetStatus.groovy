import org.ofbiz.base.util.*;
import org.ofbiz.security.*;
import org.ofbiz.service.*;

def workEffortTypeId = context.workEffortTypeId;
def finalBudget = context.finalBudget;
def cdcResponsibleId = context.cdcResponsibleId;
def partyId = context.partyId;
def debitCreditFlag = context.debitCreditFlag;

if (UtilValidate.isEmpty(context.security)) {
	context.security = SecurityFactory.getInstance(delegator);
}
GroovyUtil.runScriptAtLocation("component://workeffortext/webapp/workeffortext/WEB-INF/actions/getBudgetProposalData.groovy", context);

def allowUpdateOtherResp = false;

if (!context.denyStatusChange && UtilValidate.isNotEmpty(context.nextStatusList)) {
	for (nextStatusItem in context.nextStatusList) {
		if (nextStatusItem.statusIdTo.equals(context.groupStatusId)) {
		    context.denyStatusChangeMsg = nextStatusItem.denyStatusChangeMsg;
		    context.denyStatusChange = UtilValidate.isNotEmpty(context.denyStatusChangeMsg);
		    allowUpdateOtherResp = nextStatusItem.allowUpdateOtherResp;
			break;
		}
	}
}

if (context.denyStatusChange) {
	return ServiceUtil.returnError(context.denyStatusChangeMsg);
}

def transactionPanelMap = context.transactionPanelMap;

for (glAccountId in transactionPanelMap.keySet()) {
	def transactionList = transactionPanelMap[glAccountId];
	for (item in transactionList) {
		if (UtilValidate.isNotEmpty(item.weTransId)) {
		    if (allowUpdateOtherResp || !item.isReadOnly) {
				def acctgTrans = delegator.makeValue("AcctgTrans", ["acctgTransId": item.weTransId]);
				acctgTrans.groupStatusId = context.groupStatusId;
				delegator.store(acctgTrans);
		    }
		}
	}
}
