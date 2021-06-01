import org.ofbiz.base.util.*;

screenNameListIndex = "";

workEffortMeasureId = parameters.workEffortMeasureId;
if(UtilValidate.isEmpty(workEffortMeasureId)) {
	workEffortMeasureId = context.workEffortMeasureId;
}
if(UtilValidate.isEmpty(workEffortMeasureId)) {
	workEffortMeasureId = parameters.weTransMeasureId;
	if(UtilValidate.isEmpty(workEffortMeasureId)) {
		workEffortMeasureId = context.weTransMeasureId;
	}
}

if(UtilValidate.isNotEmpty(workEffortMeasureId)) {
	
	screenNameListIndex = "1";
	
	workEffotMeasure = delegator.findOne("WorkEffortMeasure", [ workEffortMeasureId : workEffortMeasureId ], true);
	
	glAccountId = workEffotMeasure.glAccountId;
	
	if(UtilValidate.isNotEmpty(glAccountId)) {
		
	    glAccount = delegator.findOne("GlAccount", [ glAccountId : glAccountId ], true);
		
	    if(glAccount != null && UtilValidate.isNotEmpty(glAccount.defaultUomId)) {
	    	uom = delegator.findOne("Uom", [ uomId : glAccount.defaultUomId ], true);
	    	if (uom!=null && "RATING_SCALE".equalsIgnoreCase(uom.uomTypeId)) {
	    		screenNameListIndex = "2";        		
	    	}
	    }
	}
}

return screenNameListIndex;