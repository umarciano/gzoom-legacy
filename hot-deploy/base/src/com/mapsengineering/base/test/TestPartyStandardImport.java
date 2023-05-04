package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.ImportManagerUploadFile;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;

/**
 * Test standard import for OrganizationInterface and PersonInterface
 *
 */
public class TestPartyStandardImport extends BaseTestCase {
	
    /**
     * OrganizationInterface.xml and PersonInterface.xml
     */
	public void testPersonAndOrganizationInterface() {
        
        context.put(E.entityListToImport.name(), ImportManagerConstants.ORGANIZATION_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERSON_INTERFACE +  ImportManagerConstants.SEP + ImportManagerConstants.ORG_RESP_INTERFACE +  ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
	    context.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, COMPANY);
        
        Map<String, Object> result = ImportManager.doImportSrv(dispatcher.getDispatchContext(), context);
        
        Debug.log(" - result testPersonAndOrganizationInterface " + result);
        assertEquals(ServiceUtil.returnSuccess().get("responseMessage"), result.get("responseMessage"));
        
        manageAllResultList(result, "resultList", 17, 76);
        
        // Lancio qui l'import con le tabelle di interfaccia caricate gia negli xml,
        // poiche i lservizio di ImportManagerUploadFile.doImportSrv andrebbe a cancellare tutto cio' che c'e nella tabelle
        context.put(E.entityListToImport.name(), ImportManagerConstants.ORGANIZATION_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.ALLOCATION_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.ORG_RESP_INTERFACE +  ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
        context.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, COMPANY);
        context.put("OrganizationInterfaceImportFromExt", "OrganizationInterfaceExt");
        context.put("PersonInterfaceImportFromExt", "PersonInterfaceExt");
        context.put("AllocationInterfaceImportFromExt", "AllocationInterfaceExt");

        result = ImportManagerUploadFile.doImportSrv(dispatcher.getDispatchContext(), context);
        
        Debug.log(" - result testPersonAndAllocationAndOrganizationInterface " + result);
        assertEquals(ServiceUtil.returnSuccess().get("responseMessage"), result.get("responseMessage"));
        manageResultList(result, "resultListUploadFile", "Importazione Risorse Umane Standard Upload", 0, 1);
        try {
            GenericValue serviceOne = delegator.findOne("JobLogServiceType", false,  "serviceTypeId", "STD_UP_ORGANIZATIONI");
            String serviceDesc = serviceOne.getString("description");
            manageResultList(result, "resultListUploadFile", serviceDesc, 0, 102);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        manageResultList(result, "resultListUploadFile", "Importazione Assegnazioni Standard Upload", 0, 2);
        manageAllResultList(result, "resultList", 0, 68);
    }
}
