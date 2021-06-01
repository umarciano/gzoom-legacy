package com.mapsengineering.gzoomjbpm.test;

import java.io.FileOutputStream;
import java.util.Map;

import com.mapsengineering.gzoomjbpm.JbpmManager;
import com.mapsengineering.gzoomjbpm.JbpmStub;
import com.mapsengineering.gzoomjbpm.rmi.Client;
import com.mapsengineering.gzoomjbpm.rmi.JbpmServer;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class ImageTest extends TestCase {

	private static JbpmStub client;
	private static String pid;
	
	public static void main (String[] args) {
		TestRunner.run(suite());
	}
	
	public ImageTest(String method) {
		super(method);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
	    suite.addTest(new ImageTest("testImage"));
	    suite.addTest(new ImageTest("testImageEnd"));

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
			assertFalse(true);
		}

		Map<String, Object> retMap;
		try {
			JbpmManager.deploy("EPE.jpdl.xml");
			retMap = client.startProcess("EPE", "processDescription", "USER A", null, null);
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
	
	public void testImage() {
		Map<String, Object> retMap;
		try {
			retMap = client.signalExecution(pid, null, null, null, null);
			assertNull(retMap.get(JbpmServer.ERROR));
			
			retMap = client.signalExecution(pid, null, null, null, null);
			assertNull(retMap.get(JbpmServer.ERROR));
			
			retMap = client.getProcessHistoryImage(pid, null);
			assertNotNull(retMap.get(JbpmServer.STREAM));
			FileOutputStream fos = new FileOutputStream("EPE" + "." + JbpmServer.IMAGE_TYPE);
			fos.write((byte[])retMap.get(JbpmServer.STREAM));
			fos.close();
		} catch(Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
	
	public void testImageEnd() {
		Map<String, Object> retMap;
		try {
			client.signalExecution(pid, null, null, null, null); // stato 4
			client.signalExecution(pid, null, null, null, null); // stato 5
			client.signalExecution(pid, null, null, null, null); // stato 6
			client.signalExecution(pid, null, null, null, null); // stato 7
			client.signalExecution(pid, null, null, null, null); // stato 8
			client.signalExecution(pid, null, null, null, null); // stato 9
			
			retMap = client.getProcessHistoryImage(pid, null);
			assertNotNull(retMap.get(JbpmServer.STREAM));
			FileOutputStream fos = new FileOutputStream("EPE2" + "." + JbpmServer.IMAGE_TYPE);
			fos.write((byte[])retMap.get(JbpmServer.STREAM));
			fos.close();
		}catch(Exception e) {
			e.printStackTrace();
			assertFalse(true);
		}
	}
}
