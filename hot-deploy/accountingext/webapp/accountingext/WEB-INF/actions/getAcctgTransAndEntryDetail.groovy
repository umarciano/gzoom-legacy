import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.birt.util.UtilDateTime;

def weTransAccountId = UtilValidate.isNotEmpty(context.weTransAccountId) ? context.weTransAccountId : parameters.weTransAccountId;
def weTransEntryId = UtilValidate.isNotEmpty(context.weTransEntryId) ? context.weTransEntryId : parameters.weTransEntryId;
def weTransId = UtilValidate.isNotEmpty(context.weTransId) ? context.weTransId : parameters.weTransId;
def weTransMeasureId = UtilValidate.isNotEmpty(context.weTransMeasureId) ? context.weTransMeasureId : parameters.weTransMeasureId;

def glAccount = delegator.findOne("GlAccount", ["glAccountId" : weTransAccountId], false);

context.accountTypeEnumId = glAccount.accountTypeEnumId;

//bug 4279 periodTypeId se vuoto da glAccount
def periodTypeId = context.periodTypeId;
if(UtilValidate.isEmpty(periodTypeId)){
	periodTypeId = glAccount.periodTypeId;
	context.periodTypeId = periodTypeId;
}
if(UtilValidate.isNotEmpty(weTransAccountId)) {
	if(UtilValidate.isNotEmpty(weTransId) && UtilValidate.isNotEmpty(weTransEntryId)) {
		def acctgTrans = delegator.findOne("AcctgTrans", ["acctgTransId" : weTransId], false);
		def acctgTransEntry = delegator.findOne("AcctgTransEntry", ["acctgTransId" : weTransId, "acctgTransEntrySeqId" : weTransEntryId], false);
		
		def workEffort = delegator.getRelatedOne("WorkEffort", acctgTrans);
		if(UtilValidate.isNotEmpty(workEffort)) {
			context.weTransWeId = workEffort.workEffortId;
		}
		
		if(UtilValidate.isNotEmpty(acctgTransEntry.productId)) {
			context.weTransProductId = acctgTransEntry.productId;
		}
		
		context.roleTypeId = acctgTrans.roleTypeId;
		context.partyId = acctgTrans.partyId;
		context.weTransTypeValueId = acctgTransEntry.glFiscalTypeId;
		context.fromDateCompetence = acctgTransEntry.fromDateCompetence;
		context.toDateCompetence = acctgTransEntry.toDateCompetence;
		context.weTransComment = acctgTransEntry.description;
		context.weTransValue = acctgTransEntry.amount;
		context.weTransMeasureId = acctgTransEntry.voucherRef;
		
		//GN-239 -> controllo il tipo di output che deve uscire e se è di tipo data lo converto un una data
		def uom = delegator.getRelatedOne("Uom", glAccount);
		if(UtilValidate.isNotEmpty(uom) && uom.uomTypeId == "DATE_MEASURE"){
			context.weTransValue = UtilDateTime.numberConvertToDate(acctgTransEntry.amount, locale);
		}
		
		context.weTransDate = acctgTrans.transactionDate;
		//bug 4279 weTransValue nascosto nella form, viene visualizzato il thruDate del periodo
		def conditionThruDate = EntityCondition.makeCondition([
			EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId),
			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, acctgTrans.transactionDate),
			EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, acctgTrans.transactionDate)], EntityJoinOperator.AND);
		if(UtilValidate.isNotEmpty(periodTypeId)) {
			def customTimePeriodList = delegator.findList("CustomTimePeriod", conditionThruDate, null, null, null, false);
			def customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
			context.customTimePeriodId = customTimePeriod.customTimePeriodId;
		}
		context.isPosted = acctgTrans.isPosted;
		context.weTransComments = acctgTrans.description;
		context.entryPartyId = acctgTransEntry.partyId;
		context.entryRoleTypeId = acctgTransEntry.roleTypeId;
		context.weTransCurrencyUomId = acctgTransEntry.currencyUomId;
		context.weAcctgTransAccountId = acctgTransEntry.glAccountId;
        context.amountLocked = acctgTransEntry.amountLocked;
	}
	
	def uom = delegator.getRelatedOne("Uom", glAccount);
	if(UtilValidate.isNotEmpty(uom)) {
		context.weTransUomDesc = uom.description;
		context.weTransUomType = uom.uomTypeId;
		context.weTransValueDesc = uom.description;
		context.defaultUomId = uom.uomId;
		if(UtilValidate.isEmpty(context.weTransCurrencyUomId)){
			context.weTransCurrencyUomId = uom.uomId;
		}
		context.weTransDecimalScale = uom.decimalScale;
	}
	if(UtilValidate.isNotEmpty(weTransMeasureId)) {
		def workMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : weTransMeasureId], false);
		context.weTransValueDesc = workMeasure.uomDescr;
		context.weTransMeasureId = weTransMeasureId;
	}
}

context.weTransId = weTransId;
context.weTransEntryId = weTransEntryId;
