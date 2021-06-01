package com.mapsengineering.base.test;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.ImportManagerUploadFile;
import com.mapsengineering.base.standardimport.common.DeleteEnum;
import com.mapsengineering.base.util.MessageUtil;

/**
 *  Test WorkEffort Standard Import with DeleteEnum = SAME_DATA_SOURCE_WITHOUT_MOV
 *
 */
public class TestWorkEffortStandardImportDeleteSameDataSourceWithoutMovement extends BaseTestWeStandardImportUploadFile {
    public static final String MODULE = TestWorkEffortStandardImportDeleteSameDataSourceWithoutMovement.class.getName();
    
    /**
     * Test WeRootInterface and WeMeasureInterface with SAME_DATA_SOURCE_WITHOUT_MOV
     * - "poi creati dei mov per questa misura"
     * - "poi per delete without movement"
     * @throws Exception 
     */
    public void testWeInterfaceUploadFileDeleteSameDataSourceWithoutMovement() {
        try {
            // Create movement for
            createMovement();

            context.put(E.entityListToImport.name(), E.WeRootInterface.name() + "|" + E.WeMeasureInterface.name());
            context.put(E.deletePrevious.name(), DeleteEnum.SAME_DATA_SOURCE_WITHOUT_MOV.name());
            final String nameFile4 = "WeMeasureInterface_DeleteEnum_2.xls";
            getLoadContext(E.WeMeasureInterface.name(), nameFile4);
            final String nameFile = "WeRootInterface_DeleteEnum_2.xls";
            getLoadContext(E.WeRootInterface.name(), nameFile);
            Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
            Debug.log(" - result " + result);
            assertEquals(ServiceUtil.returnSuccess().get("responseMessage"), result.get("responseMessage"));

            manageResultListWe(result, 0, 1, 0, 1);

            List<EntityCondition> condition = FastList.newInstance();
            condition.add(EntityCondition.makeCondition(E.uomDescr.name(), "poi creati dei mov per questa misura"));
            condition.add(EntityCondition.makeCondition(E.glAccountId.name(), "G30000"));
            GenericValue measureWithSameDataSourceWithMovement = getWorkEffortMeasure(EntityCondition.makeCondition(condition));
            assertNotNull(measureWithSameDataSourceWithMovement);

            // assert delete workEffortMeasure with same datasource but no mov
            condition = FastList.newInstance();
            condition.add(EntityCondition.makeCondition(E.uomDescr.name(), "poi per delete without movement"));
            condition.add(EntityCondition.makeCondition(E.glAccountId.name(), "G30000"));
            GenericValue measureWithSameDataSourceWithoutMovement = getWorkEffortMeasure(EntityCondition.makeCondition(condition));
            assertNull(measureWithSameDataSourceWithoutMovement);

        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }

    private void createMovement() throws GeneralException {
        Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("crudServiceDefaultOrchestration_AcctgTransAndEntries", "IN", context);
        Map<String, Object> parameters = FastMap.newInstance();

        GenericValue ctp = delegator.findOne("CustomTimePeriod", UtilMisc.toMap(E.customTimePeriodId.name(), "201201"), false);

        List<EntityCondition> condition = FastList.newInstance();
        condition.add(EntityCondition.makeCondition(E.uomDescr.name(), "poi creati dei mov per questa misura"));
        condition.add(EntityCondition.makeCondition(E.glAccountId.name(), "G30000"));
        GenericValue workEffortMeasure = getWorkEffortMeasure(EntityCondition.makeCondition(condition));
        
        localContext.put(ServiceLogger.ENTITY_NAME, E.WorkEffortTransactionIndicatorView.name());
        localContext.put("operation", CrudEvents.OP_CREATE);
        parameters.put("operation", CrudEvents.OP_CREATE);
        parameters.put(E.weTransValue.name(), 10d);
        parameters.put(E.weTransAccountId.name(), workEffortMeasure.getString(E.glAccountId.name()));
        parameters.put(E.weTransWeId.name(), workEffortMeasure.getString(E.workEffortId.name()));
        parameters.put(E.weTransTypeValueId.name(), E.ACTUAL.name());
        parameters.put(E.weTransMeasureId.name(), workEffortMeasure.getString(E.workEffortMeasureId.name()));
        parameters.put(E.partyId.name(), E.Company.name());
        parameters.put(E.roleTypeId.name(), E.AMM.name());
        parameters.put(E.weTransCurrencyUomId.name(), E.OTH_SCO.name());
        parameters.put(E.customTimePeriodId.name(), ctp.getString(E.customTimePeriodId.name()));
        parameters.put(E.acctgTransTypeId.name(), E.CTX_BS.name());
        parameters.put(E.defaultOrganizationPartyId.name(), E.Company.name());
        localContext.put("parameters", parameters);

        dispatcher.runSync(E.crudServiceDefaultOrchestration_AcctgTransAndEntries.name(), localContext);
    }
}
