package com.mapsengineering.workeffortext.test.rootcopy;

import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;

/**
 * TestWorkEffortRootSnapshot RCW12824 with type SPR10 and revision SPR10.01
 */
public class TestMassiveWorkEffortRootSnapshotFiliera extends TestMassiveWorkEffortRootSnapshotSPR10 {

    public static final String MODULE = TestMassiveWorkEffortRootSnapshotFiliera.class.getName();

    protected String getTitle() {
        return "Snapshot 18PMA3OBSAFIL with attr";
    }

    protected String getWorkEffortRevisionId() {
        return "18PMA3OBSAFIL";
    }

    protected String getWorkEffortId() {
        return "W80010";
    }

    protected String getWorkEffortTypeIdFrom() {
        return "18PMA3OBSAFIL";
    }

    protected String getWorkEffortTypeIdTo() {
        return "18PMA3OBSAFIL";
    }

    protected Timestamp getEstimatedStartDate() {
        return new Timestamp(UtilDateTime.toDate(1, 1, 2019, 0, 0, 0).getTime());
    }

    protected Timestamp getEstimatedCompletionDate() {
        return new Timestamp(UtilDateTime.toDate(31, 12, 2019, 0, 0, 0).getTime());
    }

    protected Long getExpectedRecordElaborated() {
        return 2l;
    }

    protected Long getExpectedBlockingErrors() {
        return 0l;
    }
}
