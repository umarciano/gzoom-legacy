package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManagerUploadFile;

/**
 * Test WorkEffort StandardImport from excel
 *
 */
public class TestWeStandardImportUploadFile2 extends BaseTestWeStandardImportUploadFile {

    /**
     * Test Standard Import for workEffort with no Root File 
     * @throws Exception
     */
    public void testWeRootInterfaceUploadFileWithError() throws Exception {
        final String nameFile = "WeInterface.xls";
        context.put(E.entityListToImport.name(), E.WeRootInterface.name());
        getLoadContext(E.WeRootInterface.name(), nameFile);
        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result testWeRootInterfaceUploadFileWithError " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));

        manageResultListWe(result, 9, 9, 0, 0); // TODO controllare altri risultati
    }

    /**
     * Test all file for standard import for workeffort
     * @throws Exception
     */
    public void testAllWeInterfaceUploadFile() throws Exception {
        final String nameFile = "WeRootInterface.xls";
        getLoadContext(E.WeRootInterface.name(), nameFile);
        final String nameFile2 = "WeInterface.xls";
        getLoadContext(E.WeInterface.name(), nameFile2);
        final String nameFile3 = "WeAssocInterface.xls";
        getLoadContext(E.WeAssocInterface.name(), nameFile3);
        final String nameFile4 = "WeMeasureInterface.xls";
        getLoadContext(E.WeMeasureInterface.name(), nameFile4);
        final String nameFile5 = "WePartyInterface.xls";
        getLoadContext(E.WePartyInterface.name(), nameFile5);
        final String nameFile6 = "WeNoteInterface.xls";
        getLoadContext(E.WeNoteInterface.name(), nameFile6);
        context.put(E.entityListToImport.name(), E.WeRootInterface.name() + "|" + E.WeInterface.name() + "|" + E.WeAssocInterface.name() + "|" + E.WeMeasureInterface.name() + "|" + E.WePartyInterface.name() + "|" + E.WeNoteInterface.name());
        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));

        manageResultListWe(result, 0, 14, 8, 14); // TODO controllare altri risultati
    }
}
