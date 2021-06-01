package com.mapsengineering.gzoomjbpm.test;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import com.mapsengineering.gzoomjbpm.JbpmManager;
import com.mapsengineering.gzoomjbpm.JbpmStub;
import com.mapsengineering.gzoomjbpm.rmi.Client;
import com.mapsengineering.gzoomjbpm.rmi.JbpmServer;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class DummyTest extends TestCase {
	
	private static JbpmStub client;
	private static String pid;
	
	public static void main (String[] args) {
		TestRunner.run(suite());
	}
	
	public DummyTest(String method) {
		super(method);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
	    //suite.addTest(new DummyTest("testOne"));
		suite.addTest(new DummyTest("testTwo"));

	    TestSetup wrapper = new TestSetup(suite) {
	    	protected void setUp() {
	    		oneTimeSetUp();
		    }

		    protected void tearDown() {
		    	oneTimeTearDown();
		    }
		};

	    return wrapper;
	}
	
	public static void oneTimeSetUp() {
		System.out.println("oneTimeSetUp()");    
		    
		try {
			client = new Client();
		}catch(Exception e) {
			e.printStackTrace();
		}
		JbpmManager.deploy("dummy.jpdl.xml");

		Map<String, Object> retMap;
		try {
			retMap = client.startProcess("dummy", null, null, null, null);
			pid = (String)retMap.get(JbpmServer.PID);
		}catch(Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}

	public static void oneTimeTearDown() {
		System.out.println("oneTimeTearDown()"); 
		JbpmManager.closeProcessEngine();
	}
	
	public void testOne() {
		try {
			assertFalse(client.isEnded(pid));
			
			Map<String, Object> retMap = client.signalExecution(pid, null, null, null, null);
			assertNull(retMap.get(JbpmServer.ERROR));
			//assertTrue((Boolean)retMap.get(JbpmServer.ENDED));
		}catch(Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void testTwo() {
		try {
			List<String> variableNames = new FastList<String>();
			Map<String, Object> map;
			assertTrue(client.isEnded(pid));
			
			variableNames.add("status");
			
			map = client.getVariables(pid, variableNames);
			System.out.println(map);
		}catch(Exception e) {
			e.printStackTrace();
			assertTrue(true);
		}
	}
}
