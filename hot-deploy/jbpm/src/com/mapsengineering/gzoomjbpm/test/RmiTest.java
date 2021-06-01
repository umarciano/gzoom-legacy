package com.mapsengineering.gzoomjbpm.test;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.mapsengineering.gzoomjbpm.JbpmManager;
import com.mapsengineering.gzoomjbpm.JbpmStub;
import com.mapsengineering.gzoomjbpm.rmi.Client;
import com.mapsengineering.gzoomjbpm.rmi.JbpmServer;

public class RmiTest extends TestCase {
	
	private static JbpmStub client;
	private static String pid;
	
	public static void main (String[] args) {
		TestRunner.run(suite());
	}
	 
	public RmiTest(String method) {
		super(method);
	}
	 
	public static Test suite() {
		TestSuite suite = new TestSuite();
	    suite.addTest(new RmiTest("testProcessStart"));
	    suite.addTest(new RmiTest("testExecution"));
	   // suite.addTest(new RmiTest("testVariables"));
	    suite.addTest(new RmiTest("testTaskList"));
	    suite.addTest(new RmiTest("testImage"));
	    //suite.addTest(new RmiTest("testDelete"));
	    //suite.addTest(new RmiTest("testImage"));

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
			JbpmManagerCleaner.main(null);
		}catch(Exception e) {
			e.printStackTrace();
		}
		JbpmManager.deploy("test_process.jpdl.xml");

		Map<String, Object> retMap;
		try {
			retMap = client.startProcess("test_process", "processDescription", "USER A", null, null);
		pid = (String)retMap.get(JbpmServer.PID);
		}catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	public static void oneTimeTearDown() {
		System.out.println("oneTimeTearDown()"); 
		JbpmManager.closeProcessEngine();
	}
	
	public void testProcessStart() {
		try {
			//Caso funzionante
			List<String> retVars = new FastList<String>();
			retVars.add("var1");
			Map<String, Object> retMap = client.startProcess("test_process", "processDescription2", "USER A", null, retVars);
			assertNotNull(retMap.get(JbpmServer.PID));
			assertEquals("A", retMap.get(JbpmServer.STATUS));
			assertEquals("1", retMap.get("var1"));
			assertNull(retMap.get(JbpmServer.ERROR));
			
			retMap = client.startProcess("test_process", "processDescription3", "USER 3", null, null);
			assertNotNull(retMap.get(JbpmServer.PID));
			assertEquals("A", retMap.get(JbpmServer.STATUS));
			assertNull(retMap.get(JbpmServer.ERROR));
			String pid3 = (String)retMap.get(JbpmServer.PID);
			client.setStepUser(pid3, "USER Y");
			
			List<String> l = new FastList<String>();
			l.add(JbpmServer.DESCRIPTION);
			l.add(JbpmServer.USER);
			Map<String, Object> variables = client.getVariables(pid, l);
			String description = (String)variables.get(JbpmServer.DESCRIPTION);
			String user = (String)variables.get(JbpmServer.USER);
			assertEquals("processDescription", description);
			assertEquals("USER A", user);
			
			//Casi con errore
			try {
				retMap = client.startProcess(null, "processDescription3", "USER AAA", null, retVars);
			}catch(Exception e) {
				assertTrue(true);
			}
			assertNotNull(retMap.get(JbpmServer.ERROR));
		}catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testExecution() {
		try {
			List<String> retVars = new FastList<String>();
			retVars.add("var1");
			retVars.add("var2");
	
			Map<String, Object> retMap = client.signalExecution(pid, null, "USER B", null, retVars);
			assertNotNull(retMap);
			assertNull(retMap.get(JbpmServer.ERROR));
			assertEquals("B", retMap.get(JbpmServer.STATUS));
			assertEquals("1", retMap.get("var1"));
			
			List<String> varList = new FastList<String>();
			varList.add(JbpmServer.USER);
			varList.add(JbpmServer.DESCRIPTION);
			Map<String, Object> vars = client.getVariables(pid, varList);
			assertEquals("USER B", vars.get(JbpmServer.USER));
			assertEquals("processDescription", vars.get(JbpmServer.DESCRIPTION));
			
			varList = new FastList<String>();
			varList.add(JbpmServer.USER);
			varList.add("var1");
			varList.add("var2");
			Map<String, Object> parameters = new FastMap<String, Object>();
			parameters.put("var2", "2");
			retMap = client.signalExecution(pid, null, "USER C", parameters, varList);
			assertNotNull(retMap);
			assertNull(retMap.get(JbpmServer.ERROR));
			assertEquals("C", retMap.get(JbpmServer.STATUS));
			assertEquals("1", retMap.get("var1"));
			assertEquals("2", retMap.get("var2"));
			
			//vado all'ultima attivita
			//client.signalExecution(pid, null, null, null);
			
		}catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	} 
	
	public void testVariables() {
		try {
		List<String> list = new FastList<String>();
		list.add(JbpmServer.USER);
		client.setStepUser(pid, "User X");
		Map<String, Object> vars = client.getVariables(pid, list);
		assertEquals("User X", vars.get(JbpmServer.USER));
		
		list.add(JbpmServer.DESCRIPTION);
		vars = client.getVariables(pid, list);
		assertEquals("User X", vars.get(JbpmServer.USER));
		assertEquals("processDescription", vars.get(JbpmServer.DESCRIPTION));
		
		client.setStepUser(pid, "USER Y");
		vars = client.getVariables(pid, list);
		assertEquals("USER Y", vars.get(JbpmServer.USER));
		}catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	} 
	
	public void testTaskList() {
		try {
		List<Map<String, Object>> list = client.getTaskList("test_process", "USER Y");
		Iterator<Map<String, Object>> it = list.iterator();
		while(it.hasNext()) {
			Map<String, Object> map = it.next();
			assertNotNull(map.get(JbpmServer.PID));
			
			System.out.println("***************** PID=" + map.get(JbpmServer.PID));
			//assertEquals("processDescription", map.get(JbpmServer.DESCRIPTION));
			assertNotNull(map.get(JbpmServer.STATUS));
			
			System.out.println("----------------- STATUS=" + map.get(JbpmServer.STATUS));
			System.out.println("----------------- activiy=" + map.get(JbpmServer.ACTIVE_ACTIVITY));
		}
		}catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testDelete() {
		Map<String, Object> retMap = null;
		try {
			retMap = client.deleteProcess(pid);
			assertNull(retMap.get(JbpmServer.ERROR));
		}catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
	public void testImage() {
		Map<String, Object> retMap = null;
		try {
			retMap = client.getProcessHistoryImage(pid, null);
			assertNull(retMap.get(JbpmServer.ERROR));
			assertNotNull(retMap.get(JbpmServer.STREAM));
			FileOutputStream file = new FileOutputStream("immagine." + JbpmServer.IMAGE_TYPE);
			file.write((byte[])retMap.get(JbpmServer.STREAM));
			file.close();
		}catch(Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}
	
}
