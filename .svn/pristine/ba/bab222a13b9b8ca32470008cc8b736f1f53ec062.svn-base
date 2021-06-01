package com.mapsengineering.workeffortext.test.rootcopy;

import java.sql.Timestamp;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * Test AbstractWorkEffortRootCopy
 */
public class TestAbstractWorkEffortRootCopy extends BaseTestCase {

    public static final String MODULE = TestAbstractWorkEffortRootCopy.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortTypeIdFrom.name(), "SPR10");
        context.put(E.workEffortTypeIdTo.name(), "SPR10");

        context.put(E.workEffortId.name(), "RCW12824");

        context.put(E.estimatedStartDateFrom.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2010, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateFrom.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2010, 0, 0, 0).getTime()));
        context.put(E.estimatedStartDateTo.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2017, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateTo.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2017, 0, 0, 0).getTime()));

        context.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.N.name());
        context.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.Y.name());
        context.put(WorkEffortRootCopyService.CHECK_EXISTING, E.Y.name());
        context.put(WorkEffortRootCopyService.ORGANIZATION_PARTY_ID, "Company");
    }

    /**
     * Test constructor
     */
    public void testAbstractWorkEffortRootCopyAin() {
        try {
            WorkEffortRootCopyService service = new WorkEffortRootCopyService(dispatcher.getDispatchContext(), context);

            assertNotNull(service.getUserLogin());
            assertNotNull(service.getLocale());
            assertNotNull(service.getDispatcher());
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * Test method addLogError
     */
    public void testAbstractWorkEffortRootCopyLogError() {
        try {
            WorkEffortRootCopyService service = new WorkEffortRootCopyService(dispatcher.getDispatchContext(), context);
            service.addLogError("Test addLogError");
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
    
    /**
     * Test method addLogInfo
     */
    public void testAbstractWorkEffortRootCopyLogInfo() {
        try {
            WorkEffortRootCopyService service = new WorkEffortRootCopyService(dispatcher.getDispatchContext(), context);
            service.addLogInfo("Test addLogInfo", null);
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
}
