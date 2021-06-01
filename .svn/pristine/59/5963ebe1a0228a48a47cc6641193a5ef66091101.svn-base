import java.util.Comparator;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.FolderLayuotTypeExtractor;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;

class WorkEffortMeasureComparator implements Comparator {
    
		
	public int compare(Object obj1, Object obj2) {
        if (UtilValidate.isEmpty(obj1) && UtilValidate.isNotEmpty(obj2)) {
            return 1;
        } else if (UtilValidate.isNotEmpty(obj1) && UtilValidate.isEmpty(obj2)) {
            return -1;
        } else if (UtilValidate.isNotEmpty(obj1) && UtilValidate.isNotEmpty(obj2)) {
            Map map1 = UtilGenerics.checkMap(obj1);
            Map map2 = UtilGenerics.checkMap(obj2);
        
            def sequenceId1 = map1.sequenceId;
            def sequenceId2 = map2.sequenceId;
            
            if (UtilValidate.isEmpty(sequenceId1) && UtilValidate.isNotEmpty(sequenceId2)) {
                return 1;
            } else if (UtilValidate.isNotEmpty(sequenceId1) && UtilValidate.isEmpty(sequenceId2)) {
                return -1;
            } else if (UtilValidate.isNotEmpty(sequenceId1) && UtilValidate.isNotEmpty(sequenceId2)) {
                def c = sequenceId1.compareTo(sequenceId2);
				if (c == 0) {
                    def weMeasureAccountCode1 = map1.weMeasureAccountCode;
                    def weMeasureAccountCode2 = map2.weMeasureAccountCode;
					
					if (UtilValidate.isEmpty(weMeasureAccountCode1) && UtilValidate.isNotEmpty(weMeasureAccountCode2)) {
						return 1;
					} else if (UtilValidate.isNotEmpty(weMeasureAccountCode1) && UtilValidate.isEmpty(weMeasureAccountCode2)) {
						return -1;
					} else if (UtilValidate.isNotEmpty(weMeasureAccountCode1) && UtilValidate.isNotEmpty(weMeasureAccountCode2)) {
	                    c =  weMeasureAccountCode1.compareTo(weMeasureAccountCode2);
						if (c == 0){
							def uomDescr1 = map1.uomDescr;
							def uomDescr2 = map2.uomDescr;
							if (UtilValidate.isEmpty(uomDescr1) && UtilValidate.isNotEmpty(uomDescr2)) {
				                return 1;
				            } else if (UtilValidate.isNotEmpty(uomDescr1) && UtilValidate.isEmpty(uomDescr2)) {
				                return -1;
				            } else if (UtilValidate.isNotEmpty(uomDescr1) && UtilValidate.isNotEmpty(uomDescr2)) {
								c =  uomDescr1.compareTo(uomDescr2);
								if (c == 0){
									def workEffortMeasureId1 = map1.workEffortMeasureId;
									def workEffortMeasureId2 = map2.workEffortMeasureId;
									c =  workEffortMeasureId1.compareTo(workEffortMeasureId2);
								}
							}
							
						}
					}

                } 
                return c;
            }
        }
        
        return 0;
    }
}

// Debug.log(" - executeChildPerformFindWEMIndicator.groovy ");

context.insertMode = UtilValidate.isEmpty(context.insertMode) ? UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode : context.insertMode;
context.accountTypeEnumId = "INDICATOR";
context.glAccountConditionField = "workEffortTypeIdInd";
// WEFLD_AFIN 
context.contentIdInd = UtilValidate.isNotEmpty(context.contentIdInd) ? context.contentIdInd : 'WEFLD_IND';
parameters.contentIdInd = UtilValidate.isNotEmpty(parameters.contentIdInd) ? parameters.contentIdInd : 'WEFLD_IND';
context.contentIdSecondary = UtilValidate.isNotEmpty(context.contentIdSecondary) ? context.contentIdSecondary : 'WEFLD_AIND';
parameters.contentIdSecondary = UtilValidate.isNotEmpty(parameters.contentIdSecondary) ? parameters.contentIdSecondary : 'WEFLD_AIND';

if("N".equals(context.insertMode)){
	FromAndThruDatesProviderFromParams fromAndThruDatesProvider = new FromAndThruDatesProviderFromParams(context, parameters, delegator, false);
	fromAndThruDatesProvider.run();
	
	def conditionList = [];
	conditionList.add(EntityCondition.makeCondition("workEffortId", parameters.workEffortId));
	if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getFromDate())) {
		conditionList.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromAndThruDatesProvider.getFromDate()));
	}
	if(UtilValidate.isNotEmpty(fromAndThruDatesProvider.getThruDate())) {
		conditionList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromAndThruDatesProvider.getThruDate()));
	}
	
	context.condition = EntityCondition.makeCondition(conditionList);
	context.entityNameFind = "WorkEffortMeasureGlAccountView";
	context.glAccountField = "glAccountId";
	context.orderByList = ["glAccountTypeDescription", "accountCode"];    
    context.orderByWemList = ["sequenceId", "weMeasureAccountCode"];	
}

// in alcuni casi il file e' invocato da un servizio executeWorkEffortMeasureScript, ed il contentId e' espresso come contentIdInd, 
// questo valore va messo nel layoutType per essere trovato da executeChildPerformFindWEMResource
def layoutType = new FolderLayuotTypeExtractor(context, parameters).getLayoutTypeFromFolderIndex(parameters.contentIdInd);
context.layoutType = layoutType;
Debug.log(" - executeChildPerformFindWEMIndicator.groovy layoutType " + layoutType);

def workEffortTypeId = parameters.workEffortTypeId;

if(UtilValidate.isEmpty(workEffortTypeId)) {
	def workEffort = null;
	def workEffortId = parameters.workEffortId;
	if(UtilValidate.isNotEmpty(workEffortId)) {
		workEffort = delegator.findOne("WorkEffort", ["workEffortId" : workEffortId], false);
		if(UtilValidate.isNotEmpty(workEffort)) {
			workEffortTypeId = workEffort.workEffortTypeId;
		}
	}
}
context.workEffortTypeId = workEffortTypeId;

context.showKpiWeight = "Y";
context.showKpiOtherWeight = "N";
context.showDescr = "Y";
context.noConditionFind = "N";
context.showSequence = "Y";

context.onlyWithBudget = UtilValidate.isNotEmpty(context.onlyWithBudget) ? context.onlyWithBudget : "N";

/*Debug.log(" - executeChildPerformFindWEMIndicator.groovy context.layoutType " + context.layoutType + " parameters.layoutType " + parameters.layoutType);
Debug.log(" - executeChildPerformFindWEMIndicator.groovy context.folderIndex " + context.folderIndex + " parameters.folderIndex " + parameters.folderIndex);
Debug.log(" - executeChildPerformFindWEMIndicator.groovy context.folderContentIds " + context.folderContentIds);
*/

GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWEMResource.groovy", context);

if (UtilValidate.isNotEmpty(context.listIt)) {
    Collections.sort(context.listIt, new WorkEffortMeasureComparator());
}
