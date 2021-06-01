package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.standardimport.ImportManager;
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
        
        manageResultList(result, "resultList", "Importazione Risorse Umane Standard", 8, 34);
        manageResultList(result, "resultList", "Importazione Unit\u00E0 organizzativa Standard", 6, 26); // dovrebbero esserci altri 2 errori nell'OrgRespInterface
        try {
            GenericValue serviceOne = delegator.findOne("JobLogServiceType", false,  "serviceTypeId", "STD_UP_ORGANIZATIONI");
            String serviceDesc = serviceOne.getString("description");
            manageResultList(result, "resultList", serviceDesc, 0, 3);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }
}
