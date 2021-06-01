package com.mapsengineering.workeffortext.test.rootcopy;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * Test WorkEffortRootCopy
 */
public class TestWorkEffortRootCopy extends BaseTestCase {

    public static final String MODULE = TestWorkEffortRootCopy.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortTypeIdFrom.name(), "SPR10");

        context.put(E.workEffortId.name(), "RCW12824");

        context.put(E.estimatedStartDateFrom.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2010, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateFrom.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2010, 0, 0, 0).getTime()));

        context.put(E.workEffortTypeIdTo.name(), "SPR10");

        context.put(E.estimatedStartDateTo.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2017, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateTo.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2017, 0, 0, 0).getTime()));

        context.put(WorkEffortRootCopyService.ORGANIZATION_PARTY_ID, "Company");
        context.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.Y.name());
        context.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.Y.name());
        context.put(WorkEffortRootCopyService.CHECK_EXISTING, E.Y.name());
    }

    /**
     * Test copy SPR10
     */
    public void testWorkEffortRootCopy() {
        Debug.log(getTitle());
        try {
            Map<String, Object> result = WorkEffortRootCopyService.workEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - result " + result);
            assertTrue(ServiceUtil.isSuccess(result));
            assertEquals(getExpectedBlockingErrors(), result.get(ServiceLogger.BLOCKING_ERRORS));
            assertEquals(getExpectedRecordElaborated(), result.get(ServiceLogger.RECORD_ELABORATED));
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    protected Long getExpectedBlockingErrors() {
        return 0l;
    }

    protected Long getExpectedRecordElaborated() {
        return 1l;
    }

    protected String getTitle() {
        return "Test WorkEffort Root Copy for RCW12824 with type id SPR10";
    }
}
