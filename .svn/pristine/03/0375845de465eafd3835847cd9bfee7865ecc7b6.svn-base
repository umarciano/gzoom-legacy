package com.mapsengineering.base.standardimport.helper;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.find.WorkEffortMeasureFindServices;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.common.WeInterfaceConstants;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Helper for GlAccount
 *
 */
public class WeGlAccountInterfaceHelper {
    private TakeOverService takeOverService;
    private Delegator delegator;
    private WorkEffortMeasureFindServices workEffortMeasureFindServices;

    /**
     * Constructor
     * @param takeOverService
     * @param delegator
     */
    public WeGlAccountInterfaceHelper(TakeOverService takeOverService, Delegator delegator) {
        this.takeOverService = takeOverService;
        this.delegator = delegator;
    }

    /**
     * Ritorna unico glAccount se esiste, altrimenti lancia eccezione
     * @param glAccountCode
     * @param glAccountName
     * @return
     * @throws GeneralException
     */
    public GenericValue getGlAccount(String glAccountCode, String glAccountName) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();

        if (ValidationUtil.isEmptyOrNA(glAccountCode) && ValidationUtil.isEmptyOrNA(glAccountName)) {
            String msg = "The field glAccountCode or glAccountName must be not empty";
            throw new ImportException(getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        List<EntityCondition> conditionList = FastList.newInstance();

        if (!ValidationUtil.isEmptyOrNA(glAccountCode)) {
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.accountCode.name(), glAccountCode));
            conditionList.add(cond);
        } else if (!ValidationUtil.isEmptyOrNA(glAccountName)) {
            EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toMap(E.accountName.name(), glAccountName));
            conditionList.add(cond);
        }

        EntityCondition condition = EntityCondition.makeCondition(conditionList);

        String foundMore = "Found more than one glAccount with condition :" + condition;
        String noFound = "No glAccount with condition :" + condition;

        return getTakeOverService().findOne(E.GlAccount.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }

    /**
     * Controlla unico WorkEffortPurposeAccount se esiste, altrimenti lancia eccezione
     * @param glAccountId
     * @param measureTypeIndRes
     * @param workEffortTypeId
     * @param wePurposeTypeIdInd
     * @param wePurposeTypeIdRes
     * @throws GeneralException
     */
    public void checkWorkEffortPurposeAccount(String glAccountId, String measureTypeIndRes, String workEffortTypeId, String wePurposeTypeIdInd, String wePurposeTypeIdRes) throws GeneralException {
        GenericValue gv = getTakeOverService().getExternalValue();
        workEffortMeasureFindServices = new WorkEffortMeasureFindServices(delegator);
        List<EntityCondition> conditionList = FastList.newInstance();
        conditionList.add(EntityCondition.makeCondition(UtilMisc.toMap(E.glAccountId.name(), glAccountId)));

        if (WeInterfaceConstants.RES.equals(measureTypeIndRes)) {
            conditionList.add(EntityCondition.makeCondition(E.workEffortPurposeTypeId.name(), wePurposeTypeIdRes));
        } else {
            conditionList.add(workEffortMeasureFindServices.getOtherWorkEffortPurposeType(workEffortTypeId, wePurposeTypeIdInd));
        }

        EntityCondition condition = EntityCondition.makeCondition(conditionList); // AND

        String foundMore = "Found more than one WorkEffortPurposeAccount with condition :" + condition;
        String noFound = "No WorkEffortPurposeAccount with condition :" + condition;

        getTakeOverService().findOne(E.WorkEffortPurposeAccount.name(), condition, foundMore, noFound, getTakeOverService().getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), true, false);

    }

    /**
     * Return takeOverService
     * @return
     */
    public TakeOverService getTakeOverService() {
        return takeOverService;
    }
}
