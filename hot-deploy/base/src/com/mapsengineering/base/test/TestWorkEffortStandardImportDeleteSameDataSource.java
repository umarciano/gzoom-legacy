package com.mapsengineering.base.test;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManagerUploadFile;
import com.mapsengineering.base.standardimport.common.DeleteEnum;

/**
 *  Test WorkEffort Standard Import with DeleteEnum = SAME_DATA_SOURCE
 * 
 */
public class TestWorkEffortStandardImportDeleteSameDataSource extends BaseTestWeStandardImportUploadFile {

    /**
     * Test WeRootInterface and WeMeasureInterface with SAME_DATA_SOURCE
     * Exists 2 WorkEffortMeasure with same dataSource, with uomDescr:
     * - "poi cambia dataSource per test testWorkEffortRootInterfaceDeleteSameDataSource"
     * - "poi usata per test testWorkEffortRootInterfaceDeleteSameDataSource"
     * @throws Exception 
     */
    public void testWorkEffortRootInterfaceDeleteSameDataSource() {
        try {
            // update workEffortMeasure with first uomDescr and datasource = TEST -> VOTO
            List<EntityCondition> condition = FastList.newInstance();
            condition.add(EntityCondition.makeCondition(E.uomDescr.name(), "poi cambia dataSource per test testWorkEffortRootInterfaceDeleteSameDataSource"));
            condition.add(EntityCondition.makeCondition(E.glAccountId.name(), "G10000"));
            updateWorkEffortMeasure(EntityCondition.makeCondition(condition));
            
            final String nameFile = "WeRootInterface_DeleteEnum.xls";

            getLoadContext(E.WeRootInterface.name(), nameFile);

            final String nameFile4 = "WeMeasureInterface_DeleteEnum.xls";
            getLoadContext(E.WeMeasureInterface.name(), nameFile4);
            context.put(E.entityListToImport.name(), E.WeRootInterface.name() + "|" + E.WeMeasureInterface.name());
            context.put(E.deletePrevious.name(), DeleteEnum.SAME_DATA_SOURCE.name());
            Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
            Debug.log("testWorkEffortRootInterfaceDeleteSameDataSource result " + result);
            assertEquals(ServiceUtil.returnSuccess().get("responseMessage"), result.get("responseMessage"));

            manageResultListWe(result, 0, 1, 0, 1);

            // assert delete workEffortMeasure same workeffort, with different datasource
            GenericValue measureWithSameDataSource = getWorkEffortMeasure(EntityCondition.makeCondition(condition));
            assertNull(measureWithSameDataSource);
            
            // assert not delete workEffortMeasure same workeffort, with different datasource
            condition = FastList.newInstance();
            condition.add(EntityCondition.makeCondition(E.uomDescr.name(), "poi usata per test testWorkEffortRootInterfaceDeleteSameDataSource"));
            condition.add(EntityCondition.makeCondition(E.glAccountId.name(), "G10001"));
            GenericValue measureWithOtherDataSource = getWorkEffortMeasure(EntityCondition.makeCondition(condition));
            assertNotNull(measureWithOtherDataSource);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
