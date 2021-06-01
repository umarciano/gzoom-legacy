package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.standardimport.ImportManager;

public class TestAccountingStandardImport extends BaseTestCase {
	
	public void testAcctgTransInterface() {
	    context.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, COMPANY);
		context.put("entityListToImport", "GlAccountInterface|AcctgTransInterface");
		Map<String, Object> result = ImportManager.doImportSrv(dispatcher.getDispatchContext(), context);
	        
	    Debug.log(" - result " + result);
	    assertEquals(ServiceUtil.returnSuccess().get("responseMessage"), result.get("responseMessage"));
	    manageResultList(result, "resultList", "Importazione Anagrafica Unit\u00E0 Contabili/Extracontabili", 15, 27);
	    manageResultList(result, "resultList", "Importazione Movimenti Unit\u00E0 Contabili/Extracontabili", 10, 23);
    }
}
