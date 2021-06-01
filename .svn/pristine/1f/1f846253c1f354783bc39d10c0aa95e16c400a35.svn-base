package com.mapsengineering.base.test;

import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;
import junit.framework.TestCase;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;

/**
 * Sets up stuff for test unit
 * @author sandro
 *
 */
public class GplusTestCase extends TestCase {
	
	protected static final String DELEGATOR_NAME = "test";
	protected static final String DISPATCHER_NAME = "test";

	protected Delegator delegator;
	protected LocalDispatcher dispatcher;
	protected Map<String, Object> context;

	protected void setUp() throws Exception {
		super.setUp();
		
		delegator = DelegatorFactory.getDelegator(DELEGATOR_NAME);
		dispatcher = GenericDispatcher.getLocalDispatcher(DISPATCHER_NAME, delegator);
		
		//Simulo il context con User Login
		context = FastMap.newInstance();
		GenericValue userLogin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
		context.put("userLogin", userLogin);
		context.put("locale", Locale.ITALY);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
