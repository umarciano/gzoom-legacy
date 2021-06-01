package com.mapsengineering.base.find;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.service.LocalDispatcher;

import com.mapsengineering.base.standardimport.common.E;

/**
 * Utility for find WorkEffort
 *
 */
public class WorkEffortFindServices extends BaseFindServices {
    public static final String MODULE = WorkEffortFindServices.class.getName();

    /**
     * Constructor
     * @param delegator
     */
    public WorkEffortFindServices(Delegator delegator) {
        super(delegator);
    }
    
    /**
     * Constructor
     * @param delegator
     */
    public WorkEffortFindServices(Delegator delegator, LocalDispatcher dispatcher) {
        super(delegator, dispatcher);
    }

    /**
     * Get organizationId for userLoginId
     * @param userLoginId:String
     * @param throwErrorForMoreOrganizationId:boolean
     * @return
     * @throws GeneralException
     */
    public String getOrganizationId(GenericValue userLogin, boolean throwErrorForMoreOrganizationId) throws GeneralException {
        if (getDispatcher() != null) {
            Map<String, Object> result = getDispatcher().runSync("getUserPreferenceGroup", UtilMisc.toMap("userPrefGroupTypeId", "GLOBAL_PREFERENCES", "userLogin", userLogin));
            if(UtilValidate.isNotEmpty(result)){
            	Map<String, Object> userPrefMap = (Map<String, Object>) result.get("userPrefMap");
            	if (UtilValidate.isNotEmpty(userPrefMap)) {
            		return (String) userPrefMap.get("ORGANIZATION_PARTY");
            	}
            }
        }
        return null;
    }
    
    /**
     * Get EntityCondition for find WorkEffort<br>
     * ORG_UNIT_ID = partyId of orgCode<br>
     * AND WORK_EFFORT_TYPE_ID = workEffortTypeId<br>
     * AND   W.ESTIMATED_START_DATE      <= refDate<br>
     * AND   W.ESTIMATED_COMPLETION_DATE >= refDate<br>
     * @param parameters
     * @param genericValue
     * @return
     * @throws GeneralException 
     */
    public EntityCondition getWorkEffortEntityCondition(String orgCode, String workEffortTypeId, Timestamp refDate) throws GeneralException {
        PartyFindServices partyFindServices = new PartyFindServices(getDelegator());
        String orgUnitId = partyFindServices.getPartyId(orgCode, E.ORGANIZATION_UNIT.name());
        
        List<EntityCondition> conditionPTList = FastList.newInstance();

        conditionPTList.add(EntityCondition.makeCondition(E.orgUnitId.name(), orgUnitId));
        conditionPTList.add(EntityCondition.makeCondition(E.workEffortTypeId.name(), workEffortTypeId));
        conditionPTList.add(EntityCondition.makeCondition(E.estimatedStartDate.name(), EntityJoinOperator.LESS_THAN_EQUAL_TO,  refDate));
        conditionPTList.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), EntityJoinOperator.GREATER_THAN_EQUAL_TO,  refDate));
        conditionPTList.add(EntityCondition.makeCondition(E.workEffortSnapshotId.name(), null));
        
        return EntityCondition.makeCondition(conditionPTList);
    }
    
    /**
     * Get EntityCondition for find WorkEffort<br>
     * (W.ETCH = workEffortAssignmentCode<br>
     * OR W.SOURCE_REFERENCE_ID = workEffortAssignmentCode)<br>
     * AND   W.ESTIMATED_START_DATE      <= refDate<br>
     * AND   W.ESTIMATED_COMPLETION_DATE >= refDate<br>
     * AND W.WORK_EFFORT_SNAPSHOT_ID empty
     * @param parameters
     * @param genericValue
     * @return
     * @throws GeneralException 
     */
    public EntityCondition getWorkEffortForCodeEntityCondition(String workEffortAssignmentCode, Timestamp refDate) throws GeneralException {
        List<EntityCondition> listaCondition = FastList.newInstance();
        
        listaCondition.add(EntityCondition.makeCondition(E.estimatedCompletionDate.name(), EntityJoinOperator.GREATER_THAN_EQUAL_TO, refDate));
        listaCondition.add(EntityCondition.makeCondition(E.estimatedStartDate.name(), EntityJoinOperator.LESS_THAN_EQUAL_TO, refDate));
        listaCondition.add(EntityCondition.makeCondition(E.workEffortSnapshotId.name(), null));
        
        List<EntityCondition> listaConditionCode = FastList.newInstance();
        listaConditionCode.add(EntityCondition.makeCondition(E.sourceReferenceId.name(), workEffortAssignmentCode));
        listaConditionCode.add(EntityCondition.makeCondition(E.etch.name(), workEffortAssignmentCode));
        listaCondition.add(EntityCondition.makeCondition(listaConditionCode, EntityJoinOperator.OR));

        return EntityCondition.makeCondition(listaCondition);

    }
}
