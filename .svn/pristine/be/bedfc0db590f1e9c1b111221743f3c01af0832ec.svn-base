package com.mapsengineering.gzoomjbpm.rmi;

import it.sauronsoftware.base64.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.log4j.Logger;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.history.HistoryActivityInstance;
import org.jbpm.api.history.HistoryProcessInstance;
import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.gzoomjbpm.JbpmManager;
import com.mapsengineering.gzoomjbpm.graph.JpdlModel;
import com.mapsengineering.gzoomjbpm.graph.JpdlModelDrawer;
import com.mapsengineering.gzoomjbpm.query.FullHistoryVariableQueryResult;
import com.mapsengineering.gzoomjbpm.query.ProcessVariableQuery;
import com.mapsengineering.gzoomjbpm.query.ProcessVariableQueryResult;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A.
 * RMI Server Implementation
 *
 */
public class Server extends UnicastRemoteObject implements JbpmServer {

	private static final long serialVersionUID = 3146368275920859710L;
	private static final Logger logger = Logger.getLogger(Server.class);
	
	private JbpmManager jManager;
	
	protected Server() throws RemoteException {
		super();
		jManager = new JbpmManager();
	}

	@Override
	public Map<String, Object> changeUser(String pid, String user) {
		Map<String, Object> variables = new FastMap<String, Object>();
		variables.put(JbpmServer.USER, user);
		return jManager.setVariables(pid, variables);
	}

	@Override
	public Map<String, Object> getVariables(String pid,	List<String> variableNames) {
		Map<String, Object> retMap = null;
		if(!isEnded(pid)) {
			retMap = jManager.getVariables(pid, variableNames);
		}
		else {
			retMap = jManager.getHistoryVariables(pid, variableNames);
		}
		return retMap;
	}

	@Override
	public List<Map<String, Object>> taskList(String processName, String user) {
		List<Map<String, Object>> list = new FastList<Map<String, Object>>();
		ProcessVariableQuery pvq = jManager.createProcessVariableQuery();
		if(processName != null) {
			pvq.processName(processName);
		}
		if(user != null) {
			pvq.user(user);
		}
		List<ProcessVariableQueryResult> results = pvq.list();
		Iterator<ProcessVariableQueryResult> it = results.iterator();
		while(it.hasNext()) {
			ProcessVariableQueryResult result = it.next();
			Map<String, Object> map = new FastMap<String, Object>();
			map.put(JbpmServer.PID, result.getProcessId());
			Set<String> activityNames = jManager.findActiveActivityNames(result.getProcessId());
			Iterator<String> acivityIt = activityNames.iterator();
			StringBuffer description = new StringBuffer();
			while(acivityIt.hasNext()) {
				String activity = acivityIt.next();
				if(UtilValidate.isNotEmpty(description)) {
					description.append(" - ");
				}
				description.append(activity);
			}
			map.put(JbpmServer.ACTIVE_ACTIVITY, description.toString());
			map.put(JbpmServer.STATUS, result.getVariable(JbpmServer.STATUS));
			list.add(map);
		}
		return list;
	}

	@Override
	public Map<String, Object> startProcess(String processName,
			String description, String firstUser, Map<String, Object> initParams, List<String> returnVars) {
		Map<String, Object> returnMap;
		returnMap = jManager.startProcess(processName, initParams);
		String pid = (String)returnMap.get(JbpmServer.PID);
		if(pid != null) {
			if(!isEnded(pid)) {
				Map<String, Object> params = new FastMap<String, Object>();
				if(firstUser != null) { 
					params.put(JbpmServer.USER, firstUser);
				}
				params.put(JbpmServer.DESCRIPTION, description);
				returnMap.putAll(jManager.setVariables(pid, params));
			}
			
			if(returnVars == null) {
				returnVars = new FastList<String>();
			}
			returnVars.add(JbpmServer.STATUS);
			
			returnMap.putAll(getVariables(pid, returnVars));
		}
		else {
			if(returnMap.get(JbpmServer.ERROR) != null) {
				logger.error("startProcess: " + returnMap.get(JbpmServer.ERROR));
			}
		}
		return returnMap;
	}

	@Override
	public Map<String, Object> stepOver(String pid, String signalName, String user, Map<String, Object> variablesToSet, List<String> returnVars) {
		
		//Find active activity before step execution
		Set<String> activeActivitySet = jManager.findActiveActivityNames(pid);
		String beforeActiveActivity = null;
		if(activeActivitySet != null) {
			Iterator<String> it = activeActivitySet.iterator();
			beforeActiveActivity = it.next();
		}
		
		Map<String, Object> executionMap = jManager.signalExecutionById(pid, signalName, variablesToSet, null);
		
		//Find active activity after step execution
		activeActivitySet = jManager.findActiveActivityNames(pid);
		String afterActiveActivity = null;
		if(activeActivitySet != null) {
			Iterator<String> it = activeActivitySet.iterator();
			afterActiveActivity = it.next();
		}
		
		//Compare before and after active activity
		
		if(user != null && beforeActiveActivity != null && afterActiveActivity != null && !afterActiveActivity.equals(beforeActiveActivity)) {
			changeUser(pid, user);
		}
		
		jManager.getProcessInstance(pid);
		if(returnVars == null) {
			returnVars = new FastList<String>();
		}
		returnVars.add(JbpmServer.STATUS);
		executionMap.putAll(getVariables(pid, returnVars));
		
		return executionMap;
	}
	
	public static void main(String[] args) {
		try {
			JbpmServer server = new Server();
			
			Properties properties = new Properties();
			InputStream is = null;
			try {
				
				is = ClassLoader.getSystemResourceAsStream("jbpm_server.properties");
			    properties.load(is);
			} catch (IOException ioe) {
				logger.error(ioe.getMessage());
			} finally {
				try {
					if(is != null) {
						is.close();
					}
				} catch (IOException ioe) {
					logger.error(ioe.getMessage());
				}
			}
			
			int port = Integer.valueOf(properties.getProperty("port"));
			Registry registry = LocateRegistry.getRegistry(port);
			registry.rebind("jbpm_server", server);
			System.out.println("Jbpm RMI Server Started!");
			logger.info("Jbpm RMI Server Started!");
		}catch(RemoteException re) {
			logger.error(re.getMessage());
		}
	}

	@Override
	public boolean isEnded(String pid) {
		return jManager.isEnded(pid);
	}

	@Override
	public Map<String, Object> deleteProcessInstance(String pid) {
		return jManager.deleteProcessInstance(pid);
	}
	
	@Override
	public Map<String, Object> getProcessHistoryImage(String pid, List<String> selectVars) {
		Map<String, Object> retMap = new FastMap<String, Object>();
		try {
			ProcessEngine pe = JbpmManager.buildProcessEngine();
			RepositoryService rs = pe.getRepositoryService(); 
			ProcessInstance pi = jManager.getProcessInstance(pid);
			String pdId = null;
			if(pi != null) {
				pdId = pi.getProcessDefinitionId();
			}
			else {
				List<String> variableNames = new FastList<String>();
				variableNames.add(JbpmServer.PDID);
				Map<String, Object> vars = jManager.getHistoryVariables(pid, variableNames);
				pdId =  (String)vars.get(JbpmServer.PDID);
			}
			if(pdId != null) {
				ProcessDefinition pd = rs.createProcessDefinitionQuery().processDefinitionId(pdId).uniqueResult();
				InputStream in = rs.getResourceAsStream(pd.getDeploymentId(), pd.getName() + ".jpdl.xml");
				if (in == null) {
					logger.error("Failed to retrieve resource: " + pd.getName());
					throw new RuntimeException("Failed to retrieve resource: " + pd.getName());
				}
				List<HistoryActivityInstance> hai = pe.getHistoryService().createHistoryActivityInstanceQuery().processInstanceId(pid).list();
				HistoryProcessInstance hpi = pe.getHistoryService().createHistoryProcessInstanceQuery().processInstanceId(pid).uniqueResult();

				List<FullHistoryVariableQueryResult> fullHistory = jManager.createFullHistoryVariableQuery().processInstanceId(pid).variableNames(selectVars).list();
				Set<String> activeActivityNames = jManager.findActiveActivityNames(pid);
				JpdlModel jpdlModel = new JpdlModel(in, hai, hpi, fullHistory, activeActivityNames);
				in.close();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ImageIO.write(new JpdlModelDrawer().draw(jpdlModel), JbpmServer.IMAGE_TYPE, out);
				out.close();
				retMap.put(JbpmServer.STREAM, Base64.encode(out.toByteArray()));
			}
			else {
				String errorMessage = "Could not find Process Definition for process id: " + pid;
				logger.error(errorMessage);
				retMap.put(JbpmServer.ERROR, errorMessage);
			}
		}catch(Exception e) {
			logger.error("Error in getProcessHistoryImage: " + e.getMessage());
			retMap.put(JbpmServer.ERROR, e.getMessage());
		}
		return retMap;
	}

	@Override
	public Map<String, String> getOutgoingDestinations(String pid) throws Exception {
		Map<String, String> retMap = new FastMap<String, String>();
		try {
			retMap = jManager.getOutgoingDestinations(pid);
		}
		catch(Exception e) {
			logger.error("Error in getOutgoingDestinations: " + e.getMessage());
			retMap.put(JbpmServer.ERROR, e.getMessage());
		}
		return retMap;
	}
}
