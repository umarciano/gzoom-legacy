package com.mapsengineering.workeffortext.test;

/**
 * Test create workEffort root OBO
 */
public class TestCreateWorkEffortRootOBO extends BaseTestCreateWorkEffortRoot {

    public static final String MODULE = TestCreateWorkEffortRootOBO.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context = setServiceMap("18PMA4OBO", "DSSL01", "SSL");
    }

    protected String getTitle() {
        return "testCreateWorkEffortRoot OBO";
    }
}
