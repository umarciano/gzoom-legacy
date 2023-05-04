package com.mapsengineering.workeffortext.test;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * il servizio executeChildPerformFindWorkEffortRootInqyPartySummary restituisce dati solo se utente con limitazioni e contesto valorizzato
 */
public class TestExecuteChildPerformFindWorkEffortRootPartySummeryUserLogin17 extends BaseTestExecuteChildPerformFindWorkEffortRoot {

    public static final String MODULE = TestExecuteChildPerformFindWorkEffortRootPartySummeryUserLogin17.class.getName();

    protected String getWeContextId() {
        return ContextPermissionPrefixEnum.CTX_OR.getCode();
    }
    
    @SuppressWarnings("unchecked")
    public void testExecuteChildPerformFindWorkEffortRoot() {
        Debug.log(getTitle());

        try {
            Map<String, Object> result = dispatcher.runSync("executeChildPerformFindWorkEffortRootInqyPartySummary", context);
            Debug.log("executeChildPerformFindWorkEffortRootInqyPartySummary = " + result);
            assertTrue(ServiceUtil.isSuccess(result));
            List<Map<String, Object>> rowList = (List<Map<String, Object>>)result.get("rowList");
            Debug.log("executeChildPerformFindWorkEffortRootInqyPartySummary rowList " + rowList.size());
            Debug.log("executeChildPerformFindWorkEffortRootInqyPartySummary rowList " + rowList);
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
        return 1;
    }

    protected String getTitle() {
        return "Test execute executeChildPerformFindWorkEffortRootInqyPartySummary OR user17";
    }
}
