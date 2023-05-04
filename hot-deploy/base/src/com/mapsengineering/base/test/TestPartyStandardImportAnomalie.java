package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManagerUploadFile;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;

/**
 * Test import with anomalie
 *
 */
public class TestPartyStandardImportAnomalie extends BaseTestStandardImportUploadFile {

    private enum E {
        PersonInterface, OrganizationInterface, entityListToImport, responseMessage
    };

    /**
     * The relationship is delete and re-create
     * @throws Exception
     */
    public void testPersonInterfaceAnomalie() throws Exception {
        final String nameFile = "PersonInterface3.xls";
        getLoadContext(E.PersonInterface.name(), nameFile);
        context.put(E.entityListToImport.name(), ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
        
        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result PersonInterface3 " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        manageAllResultList(result, "resultList", 0, 1);
        
        final String nameFile2 = "PersonInterface4.xls";
        getLoadContext(E.PersonInterface.name(), nameFile2);
        context.put(E.entityListToImport.name(), ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
        
        Map<String, Object> result2 = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result PersonInterface4 " + result2);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result2.get(E.responseMessage.name()));
        manageAllResultList(result2, "resultList", 0, 5);
            
    }
	
}
