package com.mapsengineering.workeffortext.test;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootInqy extends BaseTestExecuteChildPerformFindWorkEffortRoot {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootInqy.class.getName();

    @SuppressWarnings("unchecked")
    public void testExecuteChildPerformFindWorkEffortRoot() {
        Debug.log(getTitle());
        
        try {
            //se sono org, role, top o sup cerco la scheda con il servizio di ricerca apposito
            //if (mapService.isOrgMgr || mapService.isRole || mapService.isSup || mapService.isTop) {
            Map<String, Object> result = dispatcher.runSync("executeChildPerformFindWorkEffortRootInqy", context);
            Debug.log("executeChildPerformFindWorkEffortRootInqy result = " + result);
            assertTrue(ServiceUtil.isSuccess(result));
            List<Map<String, Object>> rowList = (List<Map<String, Object>>)result.get("rowList");
            Debug.log("executeChildPerformFindWorkEffortRootInqy rowList.size " + rowList.size());
            Debug.log("executeChildPerformFindWorkEffortRootInqy rowList " + rowList);
            assertNotNull(rowList); // controllo che la lista ci sia, anche se vuota, perche' se c'e' un errore nella query il servizio ritorna solo la response success
            assertEquals(getRowListNumber(), rowList.size()); // controllo numero di schede restituite
        } catch (GenericServiceException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    
    protected int getRowListNumber() {
        return 46;
    }

    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRootInqy ";
    }
}
