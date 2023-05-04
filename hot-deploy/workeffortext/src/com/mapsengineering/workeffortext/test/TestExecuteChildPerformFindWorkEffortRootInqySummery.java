package com.mapsengineering.workeffortext.test;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

/**
 * Provare tutti gli ftl per prevenire errori
 */
public class TestExecuteChildPerformFindWorkEffortRootInqySummery extends BaseTestExecuteChildPerformFindWorkEffortRoot {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootInqySummery.class.getName();

    /**
     * Test
     */
    @SuppressWarnings("unchecked")
    public void testExecuteChildPerformFindWorkEffortRoot() {
        Debug.log(getTitle());

        try {
            Map<String, Object> result = dispatcher.runSync("executeChildPerformFindWorkEffortRootInqySummary", context);
            Debug.log("executeChildPerformFindWorkEffortRootInqySummary = " + result);
            assertTrue(ServiceUtil.isSuccess(result));
            List<Map<String, Object>> rowList = (List<Map<String, Object>>)result.get("rowList");
            Debug.log("executeChildPerformFindWorkEffortRootInqy rowList " + rowList.size());
            Debug.log("executeChildPerformFindWorkEffortRootInqySummary rowList " + rowList);
            assertNotNull(rowList); // controllo che la lista ci sia, anche se vuota, perche' se c'e' un errore nella query il servizio ritorna solo la response success
            assertEquals(getRowListNumber(), rowList.size()); // controllo numero di schede restituite
        } catch (GenericServiceException e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRootInqySummary ";
    }
    
    protected int getRowListNumber() {
        return 0;
    }
}
