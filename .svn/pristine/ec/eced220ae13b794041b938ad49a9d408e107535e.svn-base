package com.mapsengineering.workeffortext.test.rootcopy;

import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * Test WorkEffort Root Copy (Snapshot) Not Enabled, with workEffortTypeId = NO_COPY, that has enabledSnapshot = N 
 */
public class TestWorkEffortRootSnapshotNotEnabled extends TestWorkEffortRootCopyNotEnabled {

    public static final String MODULE = TestWorkEffortRootSnapshotNotEnabled.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(WorkEffortRootCopyService.SNAPSHOT, E.Y.name());
    }

    protected String getTitle() {
        return "Test WorkEffort Root Snapshot NO_COPY Not enabled";
    }
}
