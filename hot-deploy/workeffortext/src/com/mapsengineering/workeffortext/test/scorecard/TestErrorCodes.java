package com.mapsengineering.workeffortext.test.scorecard;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.ErrorCodes;

/**
 * Error Code Cover 
 *
 */
public class TestErrorCodes extends BaseTestCase {

    /**
     * ErrorCodes.EIGHT
     */
    public void testErrorCodes() {
        ErrorCodes ec = new ErrorCodes(); 
        assertNotNull(ErrorCodes.EIGHT);
        assertNotNull(ec);
    }
}
