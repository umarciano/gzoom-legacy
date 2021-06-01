package com.mapsengineering.base.test;

import com.mapsengineering.base.etl.EtlException;

public class TestEtlException extends BaseTestCase {

    public void testEtlException() {
    	try {
    		throw new EtlException();
    	} catch(EtlException ee) {
    		assertTrue(true);
    	}
    }
    
    public void testEtlExceptionWithMessage() {
    	try {
    		throw new EtlException("message", new Exception());
    	} catch(EtlException ee) {
    		assertTrue(true);
    	}
    }    
}
