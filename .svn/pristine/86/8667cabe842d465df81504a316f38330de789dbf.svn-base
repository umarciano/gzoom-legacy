package com.mapsengineering.workeffortext.test;

import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.workeffortext.services.E;

/**
 * Integration Test 
 * WorkEffort with 20 measure and 2 transaction
 */

public class TestWorkEffortRootSnapshotWithSelectedResourceValues extends TestWorkEffortRootSnapshotWithResourceValues {
    
    public static final String MODULE = TestWorkEffortRootSnapshotWithSelectedResourceValues.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.fromDate.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2012, 0, 0, 0).getTime()));
        context.put(E.thruDate.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2012, 0, 0, 0).getTime()));
    }
    
    protected String getTitle() {
        return "Snapshot 12BSC With 2 Resource Values";
    }
    
    protected int getExpectedMeasure() {
        return 20;
    }

    protected int getExpectedResourceTransaction() {
        return 2;
    }

    protected String getWorkEffortRevisionId() {
        return "R10004";
    }
}
