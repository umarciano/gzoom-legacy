package com.mapsengineering.workeffortext.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.birt.util.Utils;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.test.BaseTestCase;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class BaseTestExecuteChildPerformFindWorkEffortRoot extends BaseTestCase  {

    public static final String MODULE = BaseTestExecuteChildPerformFindWorkEffortRoot.class.getName();
    
    protected void setUp() throws Exception {
        super.setUp();
        context = setServiceMap();
    }
    
    protected Map<String, Object> setServiceMap() throws GenericServiceException {
        try {
            String weContextId = getWeContextId();
            String userLoginId = getUserLoginId();
            Security security = dispatcher.getSecurity();
            GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), true);
            context.put(USER_LOGIN, userLogin);
            
            Map<String, Object> mapService = new HashMap<String, Object>();
            mapService.putAll(Utils.getMapUserPermision(security, weContextId, userLogin, null));
            mapService.put("userLogin", userLogin);
            mapService.put(GenericService.ORGANIZATION_ID, COMPANY);
            mapService.remove("workEffortRevisionId");
            Debug.log("mapService = " + mapService);
            return mapService;
        } catch (GenericEntityException e) {
            e.printStackTrace();
            assertTrue(false);
        }
        return null;
    }
    
    protected String getUserLoginId() {
        return "admin";
    }

    protected String getWeContextId() {
        return "";
    }

    @SuppressWarnings("unchecked")
    public void testExecuteChildPerformFindWorkEffortRoot() {
        Debug.log(getTitle());
        
        try {
            //se sono org, role, top o sup cerco la scheda con il servizio di ricerca apposito
            //if (mapService.isOrgMgr || mapService.isRole || mapService.isSup || mapService.isTop) {
            Map<String, Object> result = dispatcher.runSync("executeChildPerformFindWorkEffortRoot", context);
            Debug.log("executeChildPerformFindWorkEffortRoot result = " + result);
            assertTrue(ServiceUtil.isSuccess(result));
            List<Map<String, Object>> rowList = (List<Map<String, Object>>)result.get("rowList");
            Debug.log("executeChildPerformFindWorkEffortRoot rowList.size " + rowList.size());
            Debug.log("executeChildPerformFindWorkEffortRoot rowList " + rowList);
            assertNotNull(rowList); // controllo che la lista ci sia, anche se vuota, perche' se c'e' un errore nella query il servizio ritorna solo la response success
            assertEquals(getRowListNumber(), rowList.size()); // controllo numero di schede restituite
        } catch (GenericServiceException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    
    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRoot ";
    }
    
    protected int getRowListNumber() {
        return 12;
    }
}
