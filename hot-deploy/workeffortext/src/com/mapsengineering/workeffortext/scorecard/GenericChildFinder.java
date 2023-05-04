package com.mapsengineering.workeffortext.scorecard;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

/**
 * Search WorkEffortAssocNoScore, l' entity si chiama WorkEffortAssocNoScore ma in realta' recupera tutti i workEffort legati tramite WorkEffortAssoc
 *
 */
public class GenericChildFinder {

    private Delegator delegator;

    /**
     * Constructor
     * @param delegator
     */
    public GenericChildFinder(Delegator delegator) {
        this.delegator = delegator;
    }

    /**
     * Query di estrazione child legati tramite WorkEffortAssoc, con assocWeight <> -1
     * 
     * @return
     * @throws GenericEntityException
     */
    protected List<GenericValue> findChild(String scoreCard, Date thruDate, String scoreValueType, String rootHierarchyAssocTypeId) throws GenericEntityException {
        List<EntityCondition> condList = new FastList<EntityCondition>();
        condList.add(EntityCondition.makeCondition(E.workEffortIdFrom.name(), EntityOperator.EQUALS, scoreCard));
        condList.add(EntityCondition.makeCondition(E.workEffortParentId.name(), EntityOperator.EQUALS_FIELD, E.workEffortParentIdFrom.name()));
        condList.add(EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), EntityOperator.EQUALS, rootHierarchyAssocTypeId));
        // Bug 3852 1
        condList.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
        condList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.getYearStart(new Timestamp(thruDate.getTime()))));
        condList.add(EntityCondition.makeCondition(E.actStEnumId.name(), EntityOperator.NOT_EQUAL, E.ACTSTATUS_REPLACED.name()));
        condList.add(EntityCondition.makeCondition(E.assocWeight.name(), EntityOperator.NOT_EQUAL, -1));
        
        // si chiama NoScore, ma estrae tutti i figli, senza fare considerazioni sugli eventuali SCORE
        List<GenericValue> assocList = delegator.findList("WorkEffortAssocNoScore", EntityCondition.makeCondition(condList), null, null, null, false);

        return assocList;
    }

    /**
     * Delegator
     * @return
     */
    public Delegator getDelegator() {
        return delegator;
    }
}
