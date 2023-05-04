package com.mapsengineering.workeffortext.test;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootPartySummeryStratOrgUserLogin17 extends BaseTestExecuteChildPerformFindWorkEffortRoot {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootPartySummeryStratOrgUserLogin17.class.getName();

    @SuppressWarnings("unchecked")
    public void testExecuteChildPerformFindWorkEffortRoot() {
        Debug.log(getTitle());

        try {
            Map<String, Object> result = dispatcher.runSync("executeChildPerformFindWorkEffortRootInqyPartySummaryStratOrg", context);
            Debug.log("executeChildPerformFindWorkEffortRootInqyPartySummaryStratOrg " + result);
            assertTrue(ServiceUtil.isSuccess(result));
            List<Map<String, Object>> rowList = (List<Map<String, Object>>)result.get("rowList");
            Debug.log("executeChildPerformFindWorkEffortRootInqyPartySummaryStratOrg rowList " + rowList.size());
            Debug.log("executeChildPerformFindWorkEffortRootInqyPartySummaryStratOrg rowList " + rowList);
            assertNotNull(rowList); // controllo che la lista ci sia, anche se vuota, perche' se c'e' un errore nella query il servizio ritorna solo la response success
            assertEquals(getRowListNumber(), rowList.size()); // controllo numero di schede restituite
        } catch (GenericServiceException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
    
    protected String getUserLoginId() {
        return "user17";
    }
    
    protected int getRowListNumber() {
        return 0;
    }

    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRootInqyPartySummaryStratOrg ";
    }
}
