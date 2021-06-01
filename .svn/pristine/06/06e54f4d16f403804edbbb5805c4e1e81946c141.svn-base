package com.mapsengineering.workeffortext.test.rootcopy;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.MassiveWorkEffortRootCopyService;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * TestWorkEffortRootSnapshot RCW12824 with type SPR10 and revision SPR10.01
 */
public class TestMassiveWorkEffortRootSnapshotSPR10 extends TestMassiveWorkEffortRootSnapshot {

    public static final String MODULE = TestMassiveWorkEffortRootSnapshotSPR10.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.Y.name());
        context.put(WorkEffortRootCopyService.CHECK_EXISTING, E.N.name());
        context.put(E.snapShotDescription.name(), "Automatica");

        context.put(E.workEffortId.name(), getWorkEffortId());
        context.put(E.workEffortTypeIdFrom.name(), getWorkEffortTypeIdFrom());
        context.put(E.workEffortTypeIdTo.name(), getWorkEffortTypeIdTo());

        context.put(E.estimatedStartDateFrom.name(), getEstimatedStartDate());
        context.put(E.estimatedCompletionDateFrom.name(), getEstimatedCompletionDate());
        context.put(E.estimatedStartDateTo.name(), getEstimatedStartDate());
        context.put(E.estimatedCompletionDateTo.name(), getEstimatedCompletionDate());
    }

    protected Timestamp getEstimatedStartDate() {
        return new Timestamp(UtilDateTime.toDate(1, 1, 2010, 0, 0, 0).getTime());
    }

    protected Timestamp getEstimatedCompletionDate() {
        return new Timestamp(UtilDateTime.toDate(31, 12, 2010, 0, 0, 0).getTime());
    }

    protected String getWorkEffortTypeIdFrom() {
        return "SPR10";
    }

    protected String getWorkEffortTypeIdTo() {
        return "SPR10";
    }

    /**
     * TestWorkEffortRootSnapshot SPR10
     */
    public void testWorkEffortRootSnapshot() {
        Debug.log(getTitle());

        try {
            Map<String, Object> result = MassiveWorkEffortRootCopyService.massiveWorkEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - result " + result);
            assertTrue(ServiceUtil.isSuccess(result));
            assertEquals(getExpectedRecordElaborated(), result.get(ServiceLogger.RECORD_ELABORATED));
            assertEquals(getExpectedBlockingErrors(), result.get(ServiceLogger.BLOCKING_ERRORS));
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    protected String getWorkEffortId() {
        return "RCW12824";
    }

    protected String getWorkEffortRevisionId() {
        return "SPR10.01";
    }

    protected Long getExpectedRecordElaborated() {
        return 1l;
    }

    protected Long getExpectedBlockingErrors() {
        return 0l;
    }
    
    protected String getTitle() {
        return "Snapshot SPR10 with attr";
    }
}
