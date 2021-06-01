package com.mapsengineering.workeffortext.test;

/**
 * TestInsertUpdateMovement FINANCIAL
 *
 */
public class TestInsertUpdateFinancialMovement extends BaseTestInsertUpdateMovement {

    public static final String MODULE = TestInsertUpdateFinancialMovement.class.getName();
    private static final double AMOUNT_IN_CREATE = 40d;
    private static final double AMOUNT_IN_UPDATE = 41d;

    protected String getWorkEffortId() {
        return "W10017";
    }

    protected String getWorkEffortMeasureId() {
        return "M00000";
    }

    protected String getCustomTimePeriodId() {
        return "201212";
    }

    protected String getGlAccountTypeId() {
        return "WEF";
    }

    protected String getUomId() {
        return "OTH_100";
    }

    protected String getGlAccountId() {
        return "G30000";
    }

    /**
     * Is different from glAccountId for FINANCIAL 
     * @return
     */
    protected String getEntryGlAccountId() {
        return "G40001";
    }

    protected double getExpectedResult() {
        return AMOUNT_IN_CREATE;
    }

    protected double getExpectedResultAfterUpdate() {
        return AMOUNT_IN_UPDATE;
    }

    @Override
    protected String getTitle() {
        return "TestInsertUpdate FINANCIAL Movement";
    }
}
