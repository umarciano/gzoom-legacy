package com.mapsengineering.base.test.birt;

import com.mapsengineering.base.birt.BirtException;
import com.mapsengineering.base.birt.event.EventHandlerException;
import com.mapsengineering.base.test.BaseTestCase;

public class TestBirtException extends BaseTestCase {

	public void testBirtException() {		
    	try {
    		throw new BirtException();
    	} catch(BirtException be) {
    		assertTrue(true);
    	}
    }

	public void testBirtExceptionWithMessage() {
    	try {
    		throw new BirtException("message");
    	} catch(BirtException be) {
    		assertTrue(true);
    	}
	}

	public void testBirtExceptionWithMessageAndT() {
    	try {
    		throw new BirtException("message", new Throwable("throwable"));
    	} catch(BirtException be) {
    		assertTrue(true);
    	}
	}

	public void testEventHandlerException() {
    	try {
    		throw new EventHandlerException();
    	} catch(EventHandlerException ehe) {
    		assertTrue(true);
    	}
	}

	public void testEventHandlerExceptionWithMessage() {
    	try {
    		throw new EventHandlerException("message");
    	} catch(EventHandlerException ehe) {
    		assertTrue(true);
    	}
	}

	public void testEventHandlerExceptionWithT() {
    	try {
    		throw new EventHandlerException(new Throwable("throwable"));
    	} catch(EventHandlerException ehe) {
    		assertTrue(true);
    	}
	}

	public void testEventHandlerExceptionWithMessageAndT() {
    	try {
    		throw new EventHandlerException("message", new Throwable("throwable"));
    	} catch(EventHandlerException ehe) {
    		assertTrue(true);
    	}
	}
}
