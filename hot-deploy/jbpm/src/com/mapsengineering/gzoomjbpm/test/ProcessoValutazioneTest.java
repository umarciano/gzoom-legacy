package com.mapsengineering.gzoomjbpm.test;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessDefinitionQuery;
import org.jbpm.api.ProcessEngine;

import javolution.util.FastList;
import javolution.util.FastMap;

import com.mapsengineering.gzoomjbpm.JbpmManager;
import com.mapsengineering.gzoomjbpm.JbpmStub;
import com.mapsengineering.gzoomjbpm.rmi.Client;
import com.mapsengineering.gzoomjbpm.rmi.JbpmServer;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class ProcessoValutazioneTest extends TestCase {
	
	private static JbpmStub client;
	
	public static void main (String[] args) {
		TestRunner.run(suite());
	}
	
	public ProcessoValutazioneTest(String method) {
		super(method);
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
	    suite.addTest(new ProcessoValutazioneTest("testExecution"));

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
		Map<String, String> deployMap = JbpmManager.deploy("EPM.jpdl.xml");
		assertNotNull(deployMap.get(JbpmManager.DEPLOYMENT_ID));
		assertNull(deployMap.get(JbpmManager.ERROR));
	}
	
	public static void oneTimeTearDown() {
		System.out.println("oneTimeTearDown()"); 
		JbpmManager.closeProcessEngine();
	}
	
	public void testExecution() {
		try {
			//Avvio processo
			List<String> toReturn = new FastList<String>();
			toReturn.add(JbpmServer.USER);
			Map<String, Object> retMap = client.startProcess("EPM", "processDescription", "USER A", null, toReturn);
			assertNotNull(retMap.get(JbpmServer.PID));
			assertEquals("WEEVALST_PLANINIT", retMap.get(JbpmServer.STATUS));
			assertEquals("USER A", retMap.get(JbpmServer.USER));
			assertNull(retMap.get(JbpmServer.ERROR));
			String pid = (String)retMap.get(JbpmServer.PID);
			
			//Passo alla seconda attivita WEEVALST_PLANPEND
			toReturn.clear();
			toReturn.add(JbpmServer.USER);
			retMap = client.signalExecution(pid, null, "USER B", null, toReturn);
			assertEquals("WEEVALST_PLANPEND", retMap.get(JbpmServer.STATUS));
			assertEquals("USER B", retMap.get(JbpmServer.USER));
			assertNull(retMap.get(JbpmServer.ERROR));
			
			//Passo alla terza attivita WEEVALST_EXECSUB
			retMap = client.signalExecution(pid, null, "USER C", null, null);
			assertEquals("WEEVALST_PLANSUB", retMap.get(JbpmServer.STATUS));
			assertNull(retMap.get(JbpmServer.ERROR));
			//Provo il get delle variabili
			List<String> variableNames = new FastList<String>();
			variableNames.add(JbpmServer.USER);
			retMap = client.getVariables(pid, variableNames);
			assertEquals("USER C", retMap.get(JbpmServer.USER));
			assertNull(retMap.get(JbpmServer.ERROR));
			
			//Passo alla quarta attivita WEEVALST_PLANSHARED
			toReturn.clear();
			toReturn.add(JbpmServer.DESCRIPTION);
			toReturn.add(JbpmServer.USER);
			retMap = client.signalExecution(pid, null, "USER D", null, toReturn);
			assertEquals("WEEVALST_PLANSHARED", retMap.get(JbpmServer.STATUS));
			assertEquals("USER D", retMap.get(JbpmServer.USER));
			assertEquals("processDescription", retMap.get(JbpmServer.DESCRIPTION));
			assertNull(retMap.get(JbpmServer.ERROR));
			//Interrogo Task List
			List<Map<String, Object>> taskList = client.getTaskList("EPM", "USER D");
			assertEquals(1, taskList.size());
			assertEquals(pid, taskList.get(0).get(JbpmServer.PID));
			assertEquals("WEEVALST_PLANSHARED", taskList.get(0).get(JbpmServer.ACTIVE_ACTIVITY));
			assertEquals("WEEVALST_PLANSHARED", taskList.get(0).get(JbpmServer.STATUS));
			
			//Passo alla quinta attivita WEEVALST_PLANFINAL
			retMap = client.signalExecution(pid, null, "USER A", null, null);
			assertEquals("WEEVALST_PLANFINAL", retMap.get(JbpmServer.STATUS));
			assertNull(retMap.get(JbpmServer.ERROR));
			
			//Passo avanti di due step
			//stato WEEVALST_MONITPEND
			toReturn.clear();
			toReturn.add(JbpmServer.USER);
			retMap = client.signalExecution(pid, null, null, null, toReturn);
			assertEquals("WEEVALST_MONITPEND", retMap.get(JbpmServer.STATUS));
			assertNull(retMap.get(JbpmServer.USER));
			assertNull(retMap.get(JbpmServer.ERROR));
			//stato WEEVALST_MONITSUB
			retMap = client.signalExecution(pid, null, "USER B", null, null);
			assertEquals("WEEVALST_MONITSUB", retMap.get(JbpmServer.STATUS));
			assertNull(retMap.get(JbpmServer.ERROR));
			
			//Passo all'attivita 8 WEEVALST_MONITFINAL
			retMap = client.signalExecution(pid, null, "USER C", null, null);
			assertEquals("WEEVALST_MONITFINAL", retMap.get(JbpmServer.STATUS));
			assertNull(retMap.get(JbpmServer.ERROR));
			//Setto lo user
			retMap = client.setStepUser(pid, "USER X");
			assertNull(retMap.get(JbpmServer.ERROR));
			//Interrogo per vedere se e cambiato
			toReturn.clear();
			toReturn.add(JbpmServer.USER);
			retMap = client.getVariables(pid, toReturn);
			assertEquals("USER X", retMap.get(JbpmServer.USER));
			assertNull(retMap.get(JbpmServer.ERROR));
			//Chiedo la taskList
			taskList = client.getTaskList("EPM", "USER X");
			assertEquals(1, taskList.size());
			assertEquals(pid, taskList.get(0).get(JbpmServer.PID));
			assertEquals("WEEVALST_MONITFINAL", taskList.get(0).get(JbpmServer.ACTIVE_ACTIVITY));
			assertEquals("WEEVALST_MONITFINAL", taskList.get(0).get(JbpmServer.STATUS));
			
			//Creo un nuovo processo
			retMap = client.startProcess("EPM", "processDescription2", "USER Z", null, null);
			assertNotNull(retMap.get(JbpmServer.PID));
			assertEquals("WEEVALST_PLANINIT", retMap.get(JbpmServer.STATUS));
			assertNull(retMap.get(JbpmServer.ERROR));
			String pid2 = (String)retMap.get(JbpmServer.PID);
			
			//Interrogo taskList
			taskList = client.getTaskList("EPM", null);
			assertEquals(2, taskList.size());
			taskList = client.getTaskList(null, null);
			assertEquals(2, taskList.size());
			taskList = client.getTaskList("EPM", "USER X");
			assertEquals(1, taskList.size());
			assertEquals(pid, taskList.get(0).get(JbpmServer.PID));
			assertEquals("WEEVALST_MONITFINAL", taskList.get(0).get(JbpmServer.ACTIVE_ACTIVITY));
			assertEquals("WEEVALST_MONITFINAL", taskList.get(0).get(JbpmServer.STATUS));
			taskList = client.getTaskList("EPM", "USER Z");
			assertEquals(1, taskList.size());
			assertEquals(pid2, taskList.get(0).get(JbpmServer.PID));
			assertEquals("WEEVALST_PLANINIT", taskList.get(0).get(JbpmServer.ACTIVE_ACTIVITY));
			assertEquals("WEEVALST_PLANINIT", taskList.get(0).get(JbpmServer.STATUS));
			taskList = client.getTaskList(null, "USER X");
			assertEquals(1, taskList.size());
			assertEquals(pid, taskList.get(0).get(JbpmServer.PID));
			assertEquals("WEEVALST_MONITFINAL", taskList.get(0).get(JbpmServer.ACTIVE_ACTIVITY));
			assertEquals("WEEVALST_MONITFINAL", taskList.get(0).get(JbpmServer.STATUS));
			taskList = client.getTaskList(null, "USER Z");
			assertEquals(1, taskList.size());
			assertEquals(pid2, taskList.get(0).get(JbpmServer.PID));
			assertEquals("WEEVALST_PLANINIT", taskList.get(0).get(JbpmServer.ACTIVE_ACTIVITY));
			assertEquals("WEEVALST_PLANINIT", taskList.get(0).get(JbpmServer.STATUS));
			taskList = client.getTaskList("non_esiste", null);
			assertEquals(0, taskList.size());
			taskList = client.getTaskList("EPM", "USER NON ESISTE");
			assertEquals(0, taskList.size());
			
			//Provo variables to set e vado all'attivita 9 WEEVALST_EXECPEND
			Map<String, Object> variablesToSet = new FastMap<String, Object>();
			variablesToSet.put("var1", "1");
			client.signalExecution(pid, null, null, variablesToSet, null);
			toReturn.clear();
			toReturn.add("var1");
			retMap = client.getVariables(pid, toReturn);
			assertEquals("1", retMap.get("var1"));
			
			//Arrivo all'ultima attivita
			client.signalExecution(pid, null, null, null, null); //sono alla 10
			client.signalExecution(pid, null, null, null, null); //sono alla 11
			client.signalExecution(pid, null, null, null, null); //sono alla 12
			
			//Adesso alla fine
			toReturn.clear();
			toReturn.add("var1");

		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
