package com.mapsengineering.workeffortext.test.rootcopy;

import java.sql.Timestamp;

import org.ofbiz.base.util.UtilDateTime;

/**
 * TestWorkEffortRootSnapshot 12200 with type 14O1IN and revision PEG14.0"
 */
public class TestMassiveWorkEffortRootSnapshotIndicePEG extends TestMassiveWorkEffortRootSnapshotSPR10 {

    public static final String MODULE = TestMassiveWorkEffortRootSnapshotIndicePEG.class.getName();

    protected String getWorkEffortId() {
        return "12200";
    }

    protected Timestamp getEstimatedStartDate() {
        return new Timestamp(UtilDateTime.toDate(1, 1, 2014, 0, 0, 0).getTime());
    }

    protected Timestamp getEstimatedCompletionDate() {
        return new Timestamp(UtilDateTime.toDate(31, 12, 2014, 0, 0, 0).getTime());
    }

    protected String getTitle() {
        return "Snapshot PEG14.0";
    }

    protected String getWorkEffortRevisionId() {
        return "PEG14.0";
    }

    protected String getWorkEffortTypeIdFrom() {
        return "14O1IN";
    }

    protected String getWorkEffortTypeIdTo() {
        return "14O1IN";
    }

    protected Long getExpectedRecordElaborated() {
        return 5l;
    }

    protected Long getExpectedBlockingErrors() {
        return 0l;
    }
}
