package com.mapsengineering.workeffortext.test.rootcopy;

import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.workeffortext.services.E;

/**
 * Test WorkEffort Root Copy Not Enabled, with workEffortTypeId = NO_COPY, that has copy = N 
 */
public class TestWorkEffortRootCopyNotEnabled extends TestWorkEffortRootCopy {

    public static final String MODULE = TestWorkEffortRootCopyNotEnabled.class.getName();

    private static final String WORK_EFFOT_TYPE_ID_DEF = "NO_COPY";

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortTypeIdFrom.name(), WORK_EFFOT_TYPE_ID_DEF);
        context.put(E.workEffortTypeIdTo.name(), WORK_EFFOT_TYPE_ID_DEF);
        context.put(E.workEffortId.name(), "W80012");

        context.put(E.estimatedStartDateFrom.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2019, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateFrom.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2019, 0, 0, 0).getTime()));
        context.put(E.estimatedStartDateTo.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2019, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateTo.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2019, 0, 0, 0).getTime()));
    }

    protected String getTitle() {
        return "Test WorkEffort Root Copy NO_COPY Not enabled";
    }

    protected Long getExpectedRecordElaborated() {
        return 0l;
    }
}
