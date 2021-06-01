package com.mapsengineering.base.find;

import java.sql.Timestamp;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import com.mapsengineering.base.standardimport.common.E;

/**
 * Utility for find WorkEffortPartyAssignment
 *
 */
public class WorkEffortPartyAssignmentFindServices extends BaseFindServices {
    public static final String MODULE = WorkEffortPartyAssignmentFindServices.class.getName();

    /**
     * Constructor
     * @param delegator
     */
    public WorkEffortPartyAssignmentFindServices(Delegator delegator) {
        super(delegator);
    }

    /**
     * Get EntityCondition for find valid WorkEffortPartyAssignment<br>
     * W.PARTY_ID = partyId of partyId<br>
     * AND W.WORK_EFFORT_ID = workEffortId<br>
     * AND   W.ROLE_TYPE_ID = roleTypeId<br>
     * AND   W.FROM_DATE <= refDate<br>
     * AND   W.THRU_DATE > refDate<br>
     * @param parameters
     * @param genericValue
     * @return
     * @throws GeneralException 
     */
    public EntityCondition getValidWepaEntityCondition(String workEffortId, String partyId, String roleTypeId, Timestamp refDate) throws GeneralException {
        List<EntityCondition> condList = FastList.newInstance();
        
        condList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityJoinOperator.GREATER_THAN, refDate));
        condList.add(EntityCondition.makeCondition(E.fromDate.name(), EntityJoinOperator.LESS_THAN_EQUAL_TO, refDate));
        condList.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));
        condList.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        condList.add(EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));

        return EntityCondition.makeCondition(condList);
    }
    
    /**
     * Get EntityCondition for find valid WorkEffortPartyAssignment<br>
     * W.PARTY_ID = partyId of partyId<br>
     * AND   W.ROLE_TYPE_ID = roleTypeId<br>
     * AND   W.FROM_DATE < refDate<br>
     * AND   W.THRU_DATE >= refDate<br>
     * @param parameters
     * @param genericValue
     * @return
     * @throws GeneralException 
     */
    public EntityCondition getWepaListEntityCondition(String partyId, String roleTypeId, Timestamp refDate) throws GeneralException {
        List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        condList.add(EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));
        condList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityJoinOperator.GREATER_THAN_EQUAL_TO, refDate));
        condList.add(EntityCondition.makeCondition(E.fromDate.name(), EntityJoinOperator.LESS_THAN, refDate));
        condList.add(EntityCondition.makeCondition(E.workEffortSnapshotId.name(), null));
        
        return EntityCondition.makeCondition(condList);
    }

    /**
     * Get EntityCondition for find valid WorkEffortPartyAssignment<br>
     * W.PARTY_ID = partyId of partyId<br>
     * AND   W.ROLE_TYPE_ID = roleTypeId<br>
     * AND   W.FROM_DATE > refDate<br>
     * @param parameters
     * @param genericValue
     * @return
     * @throws GeneralException 
     */
     public EntityCondition getWepaOtherListEntityCondition(String partyId, String roleTypeId, Timestamp refDate) {
        List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        condList.add(EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));
        condList.add(EntityCondition.makeCondition(E.fromDate.name(), EntityJoinOperator.GREATER_THAN, refDate));
        condList.add(EntityCondition.makeCondition(E.workEffortSnapshotId.name(), null));
        
        return EntityCondition.makeCondition(condList);
    }
}
