import org.ofbiz.base.util.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.entity.condition.*;

def formIndicator = [:];
if (UtilValidate.isNotEmpty(parameters.workEffortMeasureId)) {

	def workEffortMeasure = delegator.findOne("WorkEffortMeasure", ["workEffortMeasureId" : parameters.workEffortMeasureId], true);
	if (UtilValidate.isNotEmpty(workEffortMeasure)) {
		formIndicator.comments = workEffortMeasure.comments;
		formIndicator.uomDescr = workEffortMeasure.uomDescr;
		formIndicator.weScoreConvEnumId = workEffortMeasure.weScoreConvEnumId;
		formIndicator.kpiScoreWeight = workEffortMeasure.kpiScoreWeight;
		// sandro: bug 3937
		formIndicator.weMeasureTypeEnumId = workEffortMeasure.weMeasureTypeEnumId;
		
		def glAccount = workEffortMeasure.getRelatedOneCache("GlAccount");
		if (UtilValidate.isNotEmpty(glAccount)) {
			formIndicator.accountName = glAccount.accountName;
			formIndicator.description = glAccount.description;
			formIndicator.debitCreditDefault = glAccount.debitCreditDefault;
			
			def glAccountType = glAccount.getRelatedOneCache("GlAccountType");
			if (UtilValidate.isNotEmpty(glAccountType)) {
				formIndicator.accountTypeDescription = glAccountType.description;
			}
		}
		def periodType = workEffortMeasure.getRelatedOneCache("PeriodType");
		if (UtilValidate.isNotEmpty(periodType)) {
			formIndicator.periodTypeDescription = periodType.description;
		}
		if (UtilValidate.isNotEmpty(workEffortMeasure.uomRangeId)) {
			def uomRangeList = delegator.findList("UomRangeValues", EntityCondition.makeCondition("uomRangeId", workEffortMeasure.uomRangeId), null, ["fromValue", "thruValue"], null, true);
			if (UtilValidate.isNotEmpty(uomRangeList)) {
				formIndicator.uomRangeList = uomRangeList;
			}
		}
	}
}
context.formIndicator = formIndicator;