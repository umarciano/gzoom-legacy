package com.mapsengineering.gzoomjbpm.test;

import com.mapsengineering.gzoomjbpm.JbpmFactory;

import junit.framework.TestCase;

public class FactoryTestCase extends TestCase {
	
	public void testFactoty() {
		Object stub = null;
		try {
			stub = JbpmFactory.instance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertNotNull(stub);
	}
}
