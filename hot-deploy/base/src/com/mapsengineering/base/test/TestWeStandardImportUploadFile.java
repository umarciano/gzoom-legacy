package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManagerUploadFile;

/**
 * Test WorkEffort StandardImport from excel
 *
 */
public class TestWeStandardImportUploadFile extends BaseTestWeStandardImportUploadFile {

    /**
     * Test Standard Import for workEffort with File
     * @throws Exception
     */
    public void testWeRootInterfaceUploadFile() throws Exception {
        final String nameFile = "WeRootInterface.xls";
        context.put(E.entityListToImport.name(), E.WeRootInterface.name());
        getLoadContext(E.WeRootInterface.name(), nameFile);

        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));

        manageResultListWe(result, 0, 14, 3, 14); // TODO controllare altri risultati
    }
}
