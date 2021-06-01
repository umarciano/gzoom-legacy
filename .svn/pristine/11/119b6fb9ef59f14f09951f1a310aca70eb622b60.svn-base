package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManagerUploadFile;

/**
 * Test workEffort standard Import with indicator used in other workeffort
 */
public class TestWeStandardImportUploadFile_MeasureWARN extends BaseTestWeStandardImportUploadFile {

    /**
     * Test check indicator is used in other workeffort
     * @throws Exception
     */
    public void testWeInterfaceUploadFile_MeasureWARN() {
        try {
            final String nameFile = "WeRootInterface_MeasureWARN.xls";
            getLoadContext(E.WeRootInterface.name(), nameFile);
            final String nameFile4 = "WeMeasureInterface_MeasureWARN.xls";
            getLoadContext(E.WeMeasureInterface.name(), nameFile4);
            context.put(E.entityListToImport.name(), E.WeRootInterface.name() + "|" + E.WeMeasureInterface.name());
            Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
            Debug.log(" - result " + result);
            assertEquals(ServiceUtil.returnSuccess().get("responseMessage"), result.get("responseMessage"));

            manageResultListWe(result, 0, 2, 0, 2); // TODO controllare altri risultati
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);

        }
    }
}
