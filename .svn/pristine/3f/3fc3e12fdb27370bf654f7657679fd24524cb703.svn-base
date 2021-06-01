package com.mapsengineering.workeffortext.test.rootcopy;

import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * Test WorkEffortRootCopy With DELETE_OLD_ROOTS = N
 */
public class TestWorkEffortRootCopyWithoutDelete extends TestWorkEffortRootCopy {

    public static final String MODULE = TestWorkEffortRootCopyWithoutDelete.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.N.name());
    }

    protected String getTitle() {
        return "Test WorkEffort Root Copy Without Delete Old Root";
    }

}
