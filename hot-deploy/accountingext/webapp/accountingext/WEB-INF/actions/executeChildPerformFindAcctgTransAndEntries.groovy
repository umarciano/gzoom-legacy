import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.birt.util.UtilDateTime;
import java.util.*;


class TransEntryComparator implements Comparator {
	public int compare(Object obj1, Object obj2) {
		if (UtilValidate.isEmpty(obj1) && UtilValidate.isNotEmpty(obj2)) {
			return 1;
		} else if (UtilValidate.isNotEmpty(obj1) && UtilValidate.isEmpty(obj2)) {
			return -1;
		} else if (UtilValidate.isNotEmpty(obj1) && UtilValidate.isNotEmpty(obj2)) {
			Map map1 = UtilGenerics.checkMap(obj1);
			Map map2 = UtilGenerics.checkMap(obj2);
		
			def weTransDate1 = map1.weTransDate;
			def weTransDate2 = map2.weTransDate;
			
			if (UtilValidate.isEmpty(weTransDate1) && UtilValidate.isNotEmpty(weTransDate2)) {
				return 1;
			} else if (UtilValidate.isNotEmpty(weTransDate1) && UtilValidate.isEmpty(weTransDate2)) {
				return -1;
			} else if (UtilValidate.isNotEmpty(weTransDate1) && UtilValidate.isNotEmpty(weTransDate2)) {
				return weTransDate1.compareTo(weTransDate2);
			}
		}
		
		return 0;
	}
}

def listIt = [];
def decimalScale;
def glAccountId = UtilValidate.isNotEmpty(context.glAccountId) ? context.glAccountId : parameters.glAccountId;

def insertMode = UtilValidate.isEmpty(context.insertMode) ? UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode : context.insertMode;
if(!"N".equals(insertMode)) {
	GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
}
else {
    // Per i movimenti di unita finanziarie, il glAccountId principale si trova nel campo "glAccountFinId"
	def condition = UtilValidate.isEmpty(context.condition) ? EntityCondition.makeCondition(EntityCondition.makeCondition([EntityCondition.makeCondition("glAccountId", glAccountId), 
										EntityCondition.makeCondition("glAccountFinId", glAccountId)], EntityJoinOperator.OR),
                                        EntityCondition.makeCondition("workEffortRevisionId", null), 
                                        EntityCondition.makeCondition("workEffortSnapshotId", null)) : context.condition;
	
	def transEntryList = delegator.findList("AcctgTransEntry", condition, null, null, null, false);
	def glAccount = delegator.findOne("GlAccount", ["glAccountId" : glAccountId], false);
	def defaultUomDescr = context.defaultUomDescr;	
	//bug 4279 periodTypeId se vuoto da glAccount
	def periodTypeId = context.periodTypeId;
	if(UtilValidate.isEmpty(periodTypeId)){
		periodTypeId = glAccount.periodTypeId;
		context.periodTypeId = periodTypeId;
	}
	
	def uom = delegator.findOne("Uom", ["uomId" : glAccount.defaultUomId], false);
	if(UtilValidate.isEmpty(context.defaultUomDescr)) {
		defaultUomDescr = uom.description;
		decimalScale = uom.decimalScale;
	}
	
	
	transEntryList.each {transEntry ->
		def dataMap = [:];
		def customTimePeriod;
		def trans = delegator.getRelatedOne("AcctgTrans", transEntry);
		dataMap.weTransId = transEntry.acctgTransId;
		dataMap.weTransEntryId = transEntry.acctgTransEntrySeqId;
		dataMap.weTransWeId = trans.workEffortId;
		
		dataMap.weTransMeasureId = transEntry.voucherRef;
		
		dataMap.partyId = trans.partyId;
		dataMap.roleTypeId = trans.roleTypeId;
		
		dataMap.weTransTypeValueId = transEntry.glFiscalTypeId;
		dataMap.weTransDate = trans.transactionDate;
		//bug 4279 weTransValue nascosto nella form, viene visualizzato il thruDate del periodo
		def conditionThruDate = EntityCondition.makeCondition([
			EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, periodTypeId),
			EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, trans.transactionDate),
			EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, trans.transactionDate)], EntityJoinOperator.AND);
		if(UtilValidate.isNotEmpty(periodTypeId)) {
			dataMap.periodTypeId = periodTypeId;
			def customTimePeriodList = delegator.findList("CustomTimePeriod", conditionThruDate, null, null, null, false);
			customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
			if(UtilValidate.isNotEmpty(customTimePeriod)){
				dataMap.customTimePeriodId = customTimePeriod.customTimePeriodId;
			}
			
		}
		
		//GN-239 -> controllo il tipo di output che deve uscire e se è di tipo data lo converto un una data
		dataMap.weTransValue = transEntry.amount;
		if(UtilValidate.isNotEmpty(uom) && uom.uomTypeId == "DATE_MEASURE"){
			dataMap.weTransValue = UtilDateTime.numberConvertToDate(transEntry.amount, locale);
		}
		
		dataMap.defaultUomDescr =  defaultUomDescr;
		dataMap.isPosted = trans.isPosted;
		dataMap.weTransProductId = transEntry.productId;
		dataMap.weTransCurrencyUomId = transEntry.currencyUomId;
		dataMap.weTransDecimalScale = decimalScale;
		dataMap.weAcctgTransAccountId = transEntry.glAccountId;
		//Bug 4968 punto 1
		if(UtilValidate.isNotEmpty(customTimePeriod) && customTimePeriod.thruDate.equals(dataMap.weTransDate)) {
			listIt.add(dataMap);
		}
	};
	
	Collections.sort(listIt, new TransEntryComparator());
	context.listIt = listIt;
}