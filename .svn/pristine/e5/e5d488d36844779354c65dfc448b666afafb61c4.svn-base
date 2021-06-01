package com.mapsengineering.gzoomjbpm.test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.jbpm.internal.log.Log;

import com.mapsengineering.gzoomjbpm.JbpmManager;
import com.mapsengineering.gzoomjbpm.query.ProcessVariableQuery;
import com.mapsengineering.gzoomjbpm.query.ProcessVariableQueryResult;

import junit.framework.TestCase;

public class JbpmManagerTest extends TestCase {
	
	protected final Log log = Log.getLog(getClass().getName());
	
	private JbpmManager jManager;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		jManager = new JbpmManager();
	}
	
	public void testProcessEngine() {
		assertNotNull(JbpmManager.buildProcessEngine());
	}
	
	public void testDeploy() {
		Map<String, String> deployMap = JbpmManager.deploy("test_process.jpdl.xml");
		assertNotNull(deployMap.get(JbpmManager.DEPLOYMENT_ID));
		assertNull(deployMap.get(JbpmManager.ERROR));
		
		deployMap = JbpmManager.deploy("XXXX");
		assertNull(deployMap.get(JbpmManager.DEPLOYMENT_ID));
		assertNotNull(deployMap.get(JbpmManager.ERROR));
	}
	
	public void testProcessExecution() {
		Map<String, Object> initParams = new FastMap<String, Object>();
		initParams.put("user", "Tizio 0");
		Map<String, Object> map = jManager.startProcess("test_process", initParams);
		String pid = (String)map.get(JbpmManager.PID);
		assertNotNull(pid);
		assertNull(map.get(JbpmManager.ERROR));
		
		map = jManager.startProcess("XXXXX", null);
		assertNull(map.get(JbpmManager.PID));
		assertNotNull(map.get(JbpmManager.ERROR));
		
		assertNotNull(jManager.getProcessInstance(pid));
		
		assertNotNull(jManager.findActiveActivityNames(pid));
		
		List<String> l1 = new FastList<String>();
		l1.add("status");
		l1.add("user");
		assertEquals("A", jManager.getVariables(pid, l1).get("status"));
		assertEquals("Tizio 0", jManager.getVariables(pid, l1).get("user"));
		
		l1 = new FastList<String>();
		l1.add("zzzz");
		Map<String, Object> map2 = jManager.getVariables("XXX", l1);
		assertNotNull(map2.get(JbpmManager.ERROR));
		assertNull(map2.get("zzzz"));
		
		Map<String, Object> parameters = new FastMap<String, Object>();
		parameters.put("user", "Tizio Z");
		//parameters.put("status", "Z");
		List<String> returnVariables = new FastList<String>();
		returnVariables.add("status");
		returnVariables.add("user");
		Map<String, Object> returnParams = jManager.signalExecutionById(pid, null, parameters, returnVariables);
		//assertEquals("Z", returnParams.get("status"));
		assertEquals("Tizio Z", returnParams.get("user"));
		assertNull(returnParams.get(JbpmManager.ERROR));
		
		returnParams = jManager.signalExecutionById("XXXXX", null, null, null);
		assertNotNull(returnParams.get(JbpmManager.ERROR));
		
		Map<String, Object> m3 = new FastMap<String, Object>();
		m3.put("user", "Tizio X");
		Map<String, Object> strMap = jManager.setVariables(pid, m3);
		assertNull(strMap.get(JbpmManager.ERROR));
		
		Map<String, Object> varToSet = new FastMap<String, Object>();
		strMap = jManager.setVariables(pid, varToSet);
		assertNull(strMap.get(JbpmManager.ERROR));
		
		varToSet.put("variabile1", "valore1");
		varToSet.put("variabile2", "valore2");
		strMap = jManager.setVariables(pid, varToSet);
		List<String> l2 = new FastList<String>();
		l2.add("variabile1");
		l2.add("variabile2");
		assertNull(strMap.get(JbpmManager.ERROR));
		assertEquals("valore1", jManager.getVariables(pid, l2).get("variabile1"));
		assertEquals("valore2", jManager.getVariables(pid, l2).get("variabile2"));
		
		Map<String, Object> varMap = jManager.getVariables(pid, returnVariables);
		assertEquals("Tizio X", varMap.get("user"));
		assertEquals("B", varMap.get("status"));
		
		varMap = jManager.getVariables("XXXXXXXX", returnVariables);
		assertNotNull(varMap.get(JbpmManager.ERROR));
		
		varMap = jManager.getVariables(pid, null);
		assertNotNull(varMap.get(JbpmManager.ERROR));
	}
	
	public void testQueryOnVariables() {
		ProcessVariableQuery pvq = jManager.createProcessVariableQuery().processName("test_process");
		List<ProcessVariableQueryResult> results = pvq.list();
		Iterator<ProcessVariableQueryResult>  it = results.iterator();
		while(it.hasNext()) {
			ProcessVariableQueryResult res = it.next();
			assertEquals("test_process", res.getProcessName());
		}
		
		pvq = jManager.createProcessVariableQuery().processName("test_process").user("Tizio X");
		results = pvq.list();
		it = results.iterator();
		while(it.hasNext()) {
			ProcessVariableQueryResult res = it.next();
			assertEquals("test_process", res.getProcessName());
			assertEquals("Tizio X", res.getVariable("user"));
		}
		
		pvq = jManager.createProcessVariableQuery().processName("XYX").user("Tizio X");
		results = pvq.list();
		assertEquals(0, results.size());
		
		log.info("---------- QUERY=" + pvq.getQuery());
	}
	
	@Override
	public void tearDown() throws Exception{
		JbpmManager.closeProcessEngine();
		super.tearDown();
	}
}
