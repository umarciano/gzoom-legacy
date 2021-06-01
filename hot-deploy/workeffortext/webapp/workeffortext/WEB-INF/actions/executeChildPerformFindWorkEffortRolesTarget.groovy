import java.util.Comparator;
import java.util.Map;

import org.ofbiz.base.util.*;

/**
 * GN-1349 fare due View da mettere in UNION tramite script groovy
 * WorkEffortRolesTarget1 - WorkEffortRolesTarget2
 * 
 */
public class WorkEffortRoleTargetComparator implements Comparator<Map> {
    @Override
    public int compare(Map obj1, Map obj2) {

        if (UtilValidate.isEmpty(obj1) && UtilValidate.isNotEmpty(obj2)) {
            return 1;
        } else if (UtilValidate.isNotEmpty(obj1) && UtilValidate.isEmpty(obj2)) {
            return -1;
        } else if (UtilValidate.isNotEmpty(obj1) && UtilValidate.isNotEmpty(obj2)) {
            Map map1 = UtilGenerics.checkMap(obj1);
            Map map2 = UtilGenerics.checkMap(obj2);

            def parentSequenceNum1 = map1.parentSequenceNum;
            def parentSequenceNum2 = map2.parentSequenceNum;

            if (UtilValidate.isEmpty(parentSequenceNum1) && UtilValidate.isNotEmpty(parentSequenceNum2)) {
                return 1;
            } else if (UtilValidate.isNotEmpty(parentSequenceNum1) && UtilValidate.isEmpty(parentSequenceNum2)) {
                return -1;
            } else {
                def c = 0;
                if (UtilValidate.isNotEmpty(parentSequenceNum1) && UtilValidate.isNotEmpty(parentSequenceNum2)) {
                    c = parentSequenceNum1.compareTo(parentSequenceNum2);
                }

                if (c == 0) {
                    def parentSourceReferenceId1 = map1.parentSourceReferenceId;
                    def parentSourceReferenceId2 = map2.parentSourceReferenceId;

                    if (UtilValidate.isEmpty(parentSourceReferenceId1) && UtilValidate.isNotEmpty(parentSourceReferenceId2)) {
                        return 1;
                    } else if (UtilValidate.isNotEmpty(parentSourceReferenceId1) && UtilValidate.isEmpty(parentSourceReferenceId2)) {
                        return -1;
                    } else {
                        if (UtilValidate.isNotEmpty(parentSourceReferenceId1) && UtilValidate.isNotEmpty(parentSourceReferenceId2)) {
                            c = parentSourceReferenceId1.compareTo(parentSourceReferenceId2);
                        }
                        if (c == 0) {
                            def parentWorkEffortId1 = map1.parentWorkEffortId;
                            def parentWorkEffortId2 = map2.parentWorkEffortId;
                            c = parentWorkEffortId1.compareTo(parentWorkEffortId2);

                            if (c == 0) {
                                def obbSequenceNum1 = map1.obbSequenceNum;
                                def obbSequenceNum2 = map2.obbSequenceNum;

                                if (UtilValidate.isEmpty(obbSequenceNum1) && UtilValidate.isNotEmpty(obbSequenceNum2)) {
                                    return 1;
                                } else if (UtilValidate.isNotEmpty(obbSequenceNum1) && UtilValidate.isEmpty(obbSequenceNum2)) {
                                    return -1;
                                } else {
                                    
                                    if (UtilValidate.isNotEmpty(obbSequenceNum1) && UtilValidate.isNotEmpty(obbSequenceNum2)) {
                                        c = obbSequenceNum1.compareTo(obbSequenceNum2);
                                    }

                                    if (c == 0) {
                                        def obbSourceReferenceId1 = map1.obbSourceReferenceId;
                                        def obbSourceReferenceId2 = map2.obbSourceReferenceId;

                                        if (UtilValidate.isEmpty(obbSourceReferenceId1) && UtilValidate.isNotEmpty(obbSourceReferenceId2)) {
                                            return 1;
                                        } else if (UtilValidate.isNotEmpty(obbSourceReferenceId1) && UtilValidate.isEmpty(obbSourceReferenceId2)) {
                                            return -1;
                                        } else {
                                            if (UtilValidate.isNotEmpty(obbSourceReferenceId1) && UtilValidate.isNotEmpty(obbSourceReferenceId2)) {
                                                c = obbSourceReferenceId1.compareTo(obbSourceReferenceId2);
                                            }
                                            if (c == 0) {
                                                def obbId1 = map1.obbId;
                                                def obbId2 = map2.obbId;
                                                c = obbId1.compareTo(obbId2);
                                            }
                                        }
                                    }
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

def concatList(list1, list2) {
    if (UtilValidate.isEmpty(list1)) {
        return list2;
    }
    if (UtilValidate.isEmpty(list2)) {
        return list1;
    }
    def list3 = [];
    for(int i=0; i < list1.size(); i++){
        map = list1.getAt(i);
        if (list2.indexOf(map) == -1) {
            list2.add(map);
        }
    }
    return list2;
}

// per prendere il valutatore devo andare su WorkEffortPartyAssignment
// con workEffortId = idScheda
// roleTypeId = 'WEM_EVAL_IN_CHARGE'

def workEffortPartyAssignmentList = delegator.findByAnd("WorkEffortPartyAssignment", [workEffortId: parameters.workEffortId, roleTypeId: "WEM_EVAL_IN_CHARGE"]);

if (UtilValidate.isNotEmpty(workEffortPartyAssignmentList)) {
    def workEffortPartyAssignment = workEffortPartyAssignmentList[0];
    context.inputFields.partyId = workEffortPartyAssignment.partyId;
    context.inputFields.workEffortId = parameters.workEffortId;
    //context.sortField = "obbSequenceNum|obbSourceReferenceId|obbId";

    //Debug.log("**************  workEffortId="+parameters.workEffortId+"      partyId="+workEffortPartyAssignment.partyId);

    //primo query
    context.entityName = "WorkEffortRolesTarget1";
    GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
    def list1 = context.listIt;

    //seconda query
    context.entityName = "WorkEffortRolesTarget2";
    GroovyUtil.runScriptAtLocation("component://base/webapp/common/WEB-INF/actions/executeChildPerformFind.groovy", context);
    def list2 = context.listIt;

    def list3 = concatList(list1, list2);
    Collections.sort(list3, new WorkEffortRoleTargetComparator());

    context.listIt = list3
    //Debug.log("................  context.listIt="+context.listIt);
}
