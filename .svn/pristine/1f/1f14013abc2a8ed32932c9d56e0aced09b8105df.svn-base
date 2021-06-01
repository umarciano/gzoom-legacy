package com.mapsengineering.base.test;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Utility for manage result for WorkEffort standardImport
 */
public class BaseTestWeStandardImportUploadFile extends BaseTestStandardImportUploadFile {

    /** Enumeration */
    protected enum E {
        WeRootInterface, WeInterface, WeAssocInterface, WeMeasureInterface, WePartyInterface, WeNoteInterface,
        //
        entityListToImport, resultListUploadFile, resultList, deletePrevious, WorkEffortMeasure, workEffortMeasureId, 
        //
        WorkEffortTransactionIndicatorView, weTransValue, weTransAccountId, weTransWeId, weTransTypeValueId, partyId, roleTypeId, 
        //
        weTransCurrencyUomId, acctgTransTypeId, customTimePeriodId, ACTUAL, Company, AMM, CTX_BS, OTH_SCO, crudServiceDefaultOrchestration_AcctgTransAndEntries, 
        //
        weTransMeasureId, glAccountId, workEffortId, dataSourceId, filterConditions, filterMapList, responseMessage, uomDescr,
        //
        sourceReferenceId, workEffortIdTo, workEffortIdFrom, workEffortName, workEffortAssocTypeId, defaultOrganizationPartyId
    };

    /**
     * Check blockingErrors and recordElaborated for Importazione Schede UploadFile and Importazione Schede
     * @param result
     * @param blockingErrorsUploadFile
     * @param recordElaboratedUploadFile
     * @param blockingErrors
     * @param recordElaborated
     */
    protected void manageResultListWe(Map<String, Object> result, long blockingErrorsUploadFile, long recordElaboratedUploadFile, long blockingErrors, long recordElaborated) {
        manageResultList(result, E.resultListUploadFile.name(), "Importazione Schede UploadFile", blockingErrorsUploadFile, recordElaboratedUploadFile); // TODO controllare altri risultati
        manageResultList(result, E.resultList.name(), "Importazione Schede", blockingErrors, recordElaborated); // TODO controllare altri risultati
    }
    
    protected void updateWorkEffortMeasure(EntityCondition condition) throws GenericEntityException {
        GenericValue workEffortMeasure = getWorkEffortMeasure(condition);
        workEffortMeasure.setString(E.dataSourceId.name(), "VOTO");
        workEffortMeasure.store();
    }
    
    protected GenericValue getWorkEffortMeasure(EntityCondition condition) throws GenericEntityException {
        List<GenericValue> measureList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(condition), null, null, null, false);
        GenericValue workEffortMeasure = EntityUtil.getFirst(measureList);
        Debug.log(" workEffortMeasure with condition  "+ condition + " = " + workEffortMeasure);
        return workEffortMeasure;
    }

}
