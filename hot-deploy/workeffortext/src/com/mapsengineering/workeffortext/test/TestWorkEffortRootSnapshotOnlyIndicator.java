package com.mapsengineering.workeffortext.test;

import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * Integration Test 
 * WorkEffort Snapshot only with indicator glAccountCreation = N
 *
 */
public class TestWorkEffortRootSnapshotOnlyIndicator extends TestWorkEffortRootSnapshotWithoutResourceValues {

    protected void setUp() throws Exception {
        super.setUp();
        context.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.N.name());
    }

    protected String getTitle() {
        return "Snapshot PEG12, glAccountCreation = N";
    }

    protected String getWorkEffortRevisionId() {
        return "R10000";
    }
}
