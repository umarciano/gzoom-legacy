package com.mapsengineering.workeffortext.test.rootcopy;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.MassiveWorkEffortRootCopyService;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * TestMassiveWorkEffortRootSnapshot for R10002 with AcctgTrans
 *
 */
public class TestMassiveWorkEffortRootSnapshotHasAcctgTrans extends BaseTestCase {

    public static final String MODULE = TestMassiveWorkEffortRootSnapshotHasAcctgTrans.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.N.name());
        context.put(WorkEffortRootCopyService.SNAPSHOT, E.Y.name());
        
        context.put(E.workEffortRevisionId.name(), "R10002");
        
    }
    
    /**
     * test with acctgTrans
     */
    public void testMassiveWorkEffortRootSnapshotHasAcctgTrans() {
        try {
            Map<String, Object> result = MassiveWorkEffortRootCopyService.massiveWorkEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - result " + result);
            assertTrue(ServiceUtil.isSuccess(result));
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

}
