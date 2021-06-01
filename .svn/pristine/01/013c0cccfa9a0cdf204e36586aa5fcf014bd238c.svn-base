package com.mapsengineering.base.find;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import com.mapsengineering.base.standardimport.common.E;

/**
 * Generic Service for EntityCondition
 *
 */
public class WorkEffortMeasureFindServices extends BaseFindServices{
    public static final String MODULE = WorkEffortMeasureFindServices.class.getName();

    private static final String WEFLD_IND = "WEFLD_IND%";

    /**
     * Constructor
     * @param delegator
     */
    public WorkEffortMeasureFindServices(Delegator delegator) {
        super(delegator);
    }

    /**
     * Costruisce la lista di condizioni per i workEffortPurposeTypeId validi per gli Indicatori, recuperati da workEffortType.wePurposeTypeIdInd oppure da WorkEffortTypeContent.workEffortPurposeTypeId
     * @param workEffortTypeId
     * @param wePurposeTypeIdInd
     * @return
     * @throws GenericEntityException
     */
    public EntityCondition getOtherWorkEffortPurposeType(String workEffortTypeId, String wePurposeTypeIdInd) throws GenericEntityException {
        List<EntityCondition> conditionPTList = FastList.newInstance();

        EntityCondition entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition(UtilMisc.toMap(E.workEffortTypeId.name(), workEffortTypeId)), EntityCondition.makeCondition(E.contentId.name(), EntityJoinOperator.LIKE, WEFLD_IND));

        List<GenericValue> workEffortPurposeTypeIdList = getDelegator().findList(E.WorkEffortTypeContent.name(), entityCondition, null, null, null, false);
        for (GenericValue workEffortTypeContent : workEffortPurposeTypeIdList) {
            String workEffortPurposeTypeId = workEffortTypeContent.getString(E.workEffortPurposeTypeId.name());
            if (UtilValidate.isNotEmpty(workEffortPurposeTypeId) && !workEffortPurposeTypeId.equals(wePurposeTypeIdInd)) {
                conditionPTList.add(EntityCondition.makeCondition(E.workEffortPurposeTypeId.name(), workEffortTypeContent.getString(E.workEffortPurposeTypeId.name())));
            }
        }
        conditionPTList.add(EntityCondition.makeCondition(E.workEffortPurposeTypeId.name(), wePurposeTypeIdInd));

        return EntityCondition.makeCondition(conditionPTList, EntityJoinOperator.OR);
    }

}
