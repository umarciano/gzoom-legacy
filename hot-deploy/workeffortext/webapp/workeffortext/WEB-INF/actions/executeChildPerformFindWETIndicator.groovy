import java.util.Comparator;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import com.mapsengineering.base.util.FolderLayuotTypeExtractor;

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
                    def glAccountId1 = map1.glAccountId;
                    def glAccountId2 = map2.glAccountId;
					
					if (UtilValidate.isEmpty(glAccountId1) && UtilValidate.isNotEmpty(glAccountId2)) {
						return 1;
					} else if (UtilValidate.isNotEmpty(glAccountId1) && UtilValidate.isEmpty(glAccountId2)) {
						return -1;
					} else if (UtilValidate.isNotEmpty(glAccountId1) && UtilValidate.isNotEmpty(glAccountId2)) {
	                    c =  glAccountId1.compareTo(glAccountId2);
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

context.insertMode = UtilValidate.isEmpty(context.insertMode) ? UtilValidate.isEmpty(parameters.insertMode) ? "N" : parameters.insertMode : context.insertMode;
context.accountTypeEnumId = "INDICATOR";
context.glAccountConditionField = "workEffortTypeIdInd";

/*Debug.log(" - executeChildPerformFindWETIndicator.groovy context.layoutType " + context.layoutType + " parameters.layoutType " + parameters.layoutType);
Debug.log(" - executeChildPerformFindWETIndicator.groovy context.folderIndex " + context.folderIndex + " parameters.folderIndex " + parameters.folderIndex);
Debug.log(" - executeChildPerformFindWETIndicator.groovy context.folderContentIds " + context.folderContentIds);

Debug.log(" - executeChildPerformFindWETIndicator.groovy" + context.insertMode + " - parameters.contentIdInd " + parameters.contentIdInd);
*/
//questo valore va messo nel layoutType per essere trovato da executeChildPerformFindWEMResource
def layoutType = new FolderLayuotTypeExtractor(context, parameters).getLayoutTypeFromFolderIndex(parameters.contentIdInd);
context.layoutType = layoutType;
Debug.log(" - executeChildPerformFindWETIndicator.groovy context.layoutType " + context.layoutType + " - parameters.contentIdInd = " + parameters.contentIdInd);

if("N".equals(context.insertMode)){
	//Causa Bug 4463
	/*context.condition = EntityCondition.makeCondition([EntityCondition.makeCondition("weTransWeId", parameters.workEffortId),
		                                               EntityCondition.makeCondition("weAcctgTransAccountId", EntityOperator.EQUALS_FIELD, "weTransAccountId")]); */
							
    def workEffort = delegator.findOne("WorkEffort", ["workEffortId": parameters.workEffortId], false);
    def workEffortType = delegator.getRelatedOne("WorkEffortType", workEffort);
    					   
	context.condition = EntityCondition.makeCondition("weTransWeId", parameters.workEffortId);
    if("Y".equals(workEffortType.showScorekpi)){
    	context.showScorekpi = "Y";
    }
    context.isObiettivo = parameters.isObiettivo;
	context.entityNameFind = "WorkEffortTransactionIndicatorView";
	context.glAccountField = "weTransAccountId";
	context.orderByWemList = ["weTransSequenceId", "weTransAccountId", "-weTransDate", "weTransTypeValueDesc"];
}
GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWEMResource.groovy", context);

if (UtilValidate.isNotEmpty(context.listIt)) {
    Collections.sort(context.listIt, new WorkEffortMeasureComparator());
}