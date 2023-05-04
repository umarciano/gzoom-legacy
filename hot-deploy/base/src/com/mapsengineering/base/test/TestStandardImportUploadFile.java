package com.mapsengineering.base.test;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.ImportManagerUploadFile;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;

/**
 * Test for standard import with file
 *
 */
public class TestStandardImportUploadFile extends BaseTestStandardImportUploadFile {

    private enum E {
        entityListToImport, responseMessage
    };

    /**
     * Import PersonInterface.xls
     * @throws Exception
     */
    public void testPersonInterfaceErrorDate() throws Exception {
        final String nameFile = "PersonInterface.xls";
        getLoadContext(ImportManagerConstants.PERSON_INTERFACE, nameFile);
        context.put(E.entityListToImport.name(), ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result testPersonInterfaceErrorDate " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        manageResultList(result, "resultListUploadFile", "Importazione Risorse Umane Standard Upload", 0, 2);
        manageAllResultList(result, "resultList", 2, 2); // eccezione sulla date provoca 2 errori
    }

    /**
     * Import PersonInterface2.xls
     * @throws Exception
     */
    public void testPersonInterfaceWithError() throws Exception {
        final String nameFile = "PersonInterface2.xls";
        getLoadContext(ImportManagerConstants.PERSON_INTERFACE, nameFile);
        context.put(E.entityListToImport.name(), ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result testPersonInterfaceWithError " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        manageResultList(result, "resultListUploadFile", "Importazione Risorse Umane Standard Upload", 1, 1);
    }

    /**
     * Import PersonInterface.xls with checkFromETL
     * @throws Exception
     */
    public void testPersonInterfaceCheckFromETL() throws Exception {
        Map<String, String> messageMap = UtilMisc.toMap("errorMsg", "Cannot locate service by name (PersonInterfaceLoadETL)");
        String responseMessageExpected = UtilProperties.getMessage("StandardImportUiLabels", "ERROR", messageMap, Locale.ITALIAN);
        final String nameFile = "PersonInterface.xls";
        getLoadContext(ImportManagerConstants.PERSON_INTERFACE, nameFile);
        context.put("checkFromETL", "fromETL");
        context.put(E.entityListToImport.name(), ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result testPersonInterfaceCheckFromETL " + result);
        assertEquals(responseMessageExpected, ServiceUtil.getErrorMessage(result));
    }

    /**
     * Import OrganizationInterface.xls
     * @throws Exception
     */
    public void testOrganizationInterface() throws Exception {
        final String nameFile = "OrganizationInterface.xls";
        getLoadContext(ImportManagerConstants.ORGANIZATION_INTERFACE, nameFile);
        context.put(E.entityListToImport.name(), ImportManagerConstants.ORGANIZATION_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.ORG_RESP_INTERFACE);
        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result testOrganizationInterface " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        try {
            GenericValue serviceOne = delegator.findOne("JobLogServiceType", false,  "serviceTypeId", "STD_UP_ORGANIZATIONI");
            String serviceDesc = serviceOne.getString("description");
            manageResultList(result, "resultListUploadFile", serviceDesc, 0, 2);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        manageAllResultList(result, "resultList", 0, 4);
    }

    /**
     * Import invalid file PerformanceInterfaceDoc.doc
     * @throws Exception
     */
    public void testFormatNotCorrect() throws Exception {
        final String nameFile = "PerformanceInterfaceDoc.doc";
        getLoadContext(ImportManagerConstants.PERSON_INTERFACE, nameFile);
        context.put(E.entityListToImport.name(), ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
        context.put("_" + ImportManagerConstants.PERSON_INTERFACE + "UploadedFile_contentType", "application/vnd.ms-word");
        context.put(ImportManagerConstants.PERSON_INTERFACE, ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result testFormatNotCorrect " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
    }

    /**
     * Import PersonInterface5.xls
     * @throws Exception
     */
    public void testPersonInterfaceWithStandardImportFieldConfig() throws Exception {
        final String nameFile = "PersonInterface5.xls";
        getLoadContext(ImportManagerConstants.PERSON_INTERFACE, nameFile);
        context.put(E.entityListToImport.name(), ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);

        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result PersonInterface5 " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        manageResultList(result, "resultListUploadFile", "Importazione Risorse Umane Standard Upload", 0, 1);
        manageAllResultList(result, "resultList", 0, 1);
    }

    /**
     * Import PersonInterface6.xls
     * @throws Exception
     */
    public void testPersonInterfaceWithStandardImportFieldConfig2() throws Exception {
        final String nameFile = "PersonInterface6.xls";
        getLoadContext(ImportManagerConstants.PERSON_INTERFACE, nameFile);
        context.put(E.entityListToImport.name(), ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);

        Map<String, Object> result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        Debug.log(" - result PersonInterface6 " + result);
        assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        manageResultList(result, "resultListUploadFile", "Importazione Risorse Umane Standard Upload", 0, 1);
        manageAllResultList(result, "resultList", 0, 1);
    }
}
