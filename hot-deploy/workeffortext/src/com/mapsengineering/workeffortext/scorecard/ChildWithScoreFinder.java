package com.mapsengineering.workeffortext.scorecard;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Search Child With Score (WorkEffortMeasureChildWithScore)
 *
 */
public class ChildWithScoreFinder extends GenericChildFinder{
	private String organizationId;
    
    /**
     * Constructor
     * @param delegator
     */
    public ChildWithScoreFinder(Delegator delegator, String organizationId) {
        super(delegator);
        this.organizationId = organizationId;
    }

    /**
     * Query di estrazione child
     * 
     * @return
     * @throws GenericEntityException
     */
    public List<Map<String, Object>> findChildWithScore(String scoreCard, Date thruDate, String scoreValueType, String rootHierarchyAssocTypeId) throws GenericEntityException {
        List<Map<String, Object>> returnList = new FastList<Map<String, Object>>();

        List<GenericValue> assocList = super.findChild(scoreCard, thruDate, scoreValueType, rootHierarchyAssocTypeId);
        
        List<EntityCondition> condList = new FastList<EntityCondition>();
        condList.add(EntityCondition.makeCondition(E.transactionDate.name(), EntityOperator.EQUALS, thruDate));
        condList.add(EntityCondition.makeCondition(E.glFiscalTypeId.name(), EntityOperator.EQUALS, scoreValueType));
        condList.add(EntityCondition.makeCondition(E.organizationPartyId.name(), EntityOperator.EQUALS, organizationId));
        condList.add(EntityCondition.makeCondition(E.actStEnumId.name(), EntityOperator.NOT_EQUAL, E.ACTSTATUS_REPLACED.name()));

        List<GenericValue> weaScore = getDelegator().findList("WorkEffortMeasureChildWithScore", EntityCondition.makeCondition(condList), null, null, null, false);
        if (UtilValidate.isNotEmpty(weaScore)) {
            List<String> weIdList = EntityUtil.getFieldListFromEntityList(weaScore, E.workEffortId.name(), false);
            Iterator<GenericValue> assocIt = assocList.iterator();
            while (assocIt.hasNext()) {
                GenericValue assoc = assocIt.next();
                int index = weIdList.indexOf(assoc.getString(E.workEffortId.name()));
                if (index != -1) {
                    GenericValue gv = weaScore.get(index);
                    if (UtilValidate.isNotEmpty(gv) && weIdList.get(index).equals(assoc.getString(E.workEffortId.name()))) {
                        Map<String, Object> retMap = gv.getAllFields();
                        retMap.put(E.assocWeight.name(), assoc.getDouble(E.assocWeight.name()));
                        returnList.add(retMap);
                    }
                }
            }
        }
        return returnList;
    }
}
