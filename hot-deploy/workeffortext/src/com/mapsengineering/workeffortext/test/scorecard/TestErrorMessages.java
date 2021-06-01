package com.mapsengineering.workeffortext.test.scorecard;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.scorecard.ErrorMessages;

/**
 * Test Error Message 
 *
 */
public class TestErrorMessages extends BaseTestCase {

    /**
     * TARGET_NOT_EXISTS e TARGET_ZERO
     */
    public void testErrorMessages() {
        ErrorMessages em = new ErrorMessages();
        assertNotNull(ErrorMessages.TARGET_NOT_EXISTS);
        assertNotNull(ErrorMessages.TARGET_ZERO);
        assertNotNull(em);
    }
}
