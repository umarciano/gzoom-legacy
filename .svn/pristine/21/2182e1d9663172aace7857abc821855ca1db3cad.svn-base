package com.mapsengineering.workeffortext.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericServiceException;

import com.mapsengineering.base.birt.util.Utils;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class BaseTestExecuteChildPerformFindWorkEffortRoot extends BaseTestCase  {

    public static final String MODULE = BaseTestExecuteChildPerformFindWorkEffortRoot.class.getName();
    
    protected void setUp() throws Exception {
        super.setUp();
        context = setServiceMap(ContextPermissionPrefixEnum.CTX_OR.getCode());
    }
    
    protected Map<String, Object> setServiceMap(String weContextId) throws GenericServiceException {
        try {
            GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "user17"), true);
            context.put(USER_LOGIN, userLogin); 
            Security security = dispatcher.getSecurity();
            Map<String, Object> mapService = Utils.getMapUserPermision(security, weContextId, userLogin, null);
            mapService.remove("workEffortRevisionId");
            mapService.remove("weContextId");
            mapService.put(GenericService.ORGANIZATION_ID, COMPANY);
            Debug.log("mapService = " + mapService);
            return mapService;
        } catch (GenericEntityException e) {
            e.printStackTrace();
            assertTrue(false);
        }
        return null;
    }
    
    public void testExecuteChildPerformFindWorkEffortRoot() {
        Debug.log(getTitle());
        
        try {
            //se sono org, role, top o sup cerco la scheda con il servizio di ricerca apposito
            //if (mapService.isOrgMgr || mapService.isRole || mapService.isSup || mapService.isTop) {
            Map<String, Object> rootSearchRootInqyServiceRes = dispatcher.runSync("executeChildPerformFindWorkEffortRoot", context);
            Debug.log("rootSearchRootInqyServiceRes = " + rootSearchRootInqyServiceRes);
        } catch (GenericServiceException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    
    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRoot ";
    }
}
