package com.mapsengineering.gzoomjbpm;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;

import org.jbpm.api.Configuration;
import org.jbpm.api.Execution;
import org.jbpm.api.JbpmException;
import org.jbpm.api.NewDeployment;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.model.Transition;
import org.jbpm.internal.log.Log;
import org.jbpm.pvm.internal.env.EnvironmentFactory;
import org.jbpm.pvm.internal.env.EnvironmentImpl;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ExecutionImpl;

import com.mapsengineering.gzoomjbpm.query.FullHistoryVariableQuery;
import com.mapsengineering.gzoomjbpm.query.FullHistoryVariableQueryImpl;
import com.mapsengineering.gzoomjbpm.query.ProcessVariableQuery;
import com.mapsengineering.gzoomjbpm.query.ProcessVariableQueryImpl;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A.
 * Utility class for interact with JBPM engine
 *
 */
public class JbpmManager {
	
	public static final String PID = "PID";
	public static final String PDID = "PDID";
	public static final String DEPLOYMENT_ID = "DEPLOYMENT_ID";
	public static final String ERROR = "ERROR";
	
	private final Log log = Log.getLog(getClass().getName());
	
	/**
	 * Method for obtain the process engine singleton
	 * @return The process engine singleton
	 */
	public static ProcessEngine buildProcessEngine() {
		return Configuration.getProcessEngine();
	}
	
	/**
	 * Close the process engine
	 */
	public static void closeProcessEngine() {
		buildProcessEngine().close();
	}
	
	/**
	 * Deletes deployment, contained process definitions, related process instances and their history information 
	 * @param deploymentId The deployment id
	 */
	public static void deleteDeploymentCascade(String deploymentId) {
		ProcessEngine pe = buildProcessEngine();
		pe.getRepositoryService().deleteDeploymentCascade(deploymentId);
	}
	
	/**
	 * Deletes a deployment if the process definitions don't have running executions. history information (if any) remains in the database.
	 * @param deploymentId The deployment id
	 */
	public static void deleteDeployment(String deploymentId) {
		ProcessEngine pe = buildProcessEngine();
		pe.getRepositoryService().deleteDeployment(deploymentId);
	}
	
	/**
	 * After adding resources, this will perform the actual deployment.
	 * @param resourceName The relative path of process definition
	 * @return A Map containing the deployment id and error messages
	 */
	public static Map<String, String> deploy(String resourceName) {
		Map<String, String> returnMap = new FastMap<String, String>();
		ProcessEngine pe = buildProcessEngine();
		try {
			NewDeployment deployment =  pe.getRepositoryService().createDeployment().addResourceFromClasspath(resourceName);
			String deploymentId = deployment.deploy();
			returnMap.put(JbpmManager.DEPLOYMENT_ID, deploymentId);
		}catch(JbpmException je) {
			returnMap.put(ERROR, je.getMessage());
		}
		return returnMap;
	}
	
	/**
	 * Starts a new Process instance
	 * @param processName The process name
	 * @param variables The initial value of process variables
	 * @return A Map containing the process id and error messages
	 */
	public Map<String, Object> startProcess(String processName, Map<String, Object> variables) {
		Map<String, Object> returnMap = new FastMap<String, Object>();
		try {
			ProcessInstance processInstance = buildProcessEngine().getExecutionService().startProcessInstanceByKey(processName, variables);
			returnMap.put(JbpmManager.PID, processInstance.getId());
			
			if(!processInstance.isEnded()) {
				buildProcessEngine().getExecutionService().createVariable(processInstance.getId(), JbpmManager.PDID, processInstance.getProcessDefinitionId(), true);
			}
		}catch(JbpmException je) {
			returnMap.put(JbpmManager.ERROR, je.getMessage());
		}
		return returnMap;
	}
	
	/**
	 * Get the process instance
	 * @param pid The process instance id
	 * @return The process instance
	 */
	public ProcessInstance getProcessInstance(String pid) {
		ProcessInstance pi = null;
		try {
			pi = buildProcessEngine().getExecutionService().findProcessInstanceById(pid);
		}catch(JbpmException je) {
		}
		return pi;
	}
	
	/**
	 * Provides an external trigger to an execution
	 * @param pid The process instance id
	 * @return A Map containing error messages
	 */
	public Map<String, Object> signalExecutionById(String pid) {
		return signalExecutionById(pid, null, null, null);
	}
	
	/**
	 * Provides an external trigger to an execution
	 * @param pid The process instance id
	 * @param returnVariables A list of process variables to return after execution
	 * @return A Map containing error messages and the required process variables
	 */
	public Map<String, Object> signalExecutionById(String pid, List<String> returnVariables) {
		return signalExecutionById(pid, null, null, returnVariables);
	}
	
	/**
	 * Provides an external trigger to an execution
	 * @param pid The process instance id
	 * @param signalName The transition to signal
	 * @param variablesToSet Variables to set before process execution
	 * @param returnVariables A list of process variables to return after execution
	 * @return A Map containing error messages and the required process variables
	 */
	public Map<String, Object> signalExecutionById(String pid, String signalName, Map<String, Object> variablesToSet, List<String> returnVariables) {
		Map<String, Object> toReturnMap = new FastMap<String, Object>();
		String error = "";
		Set<String> variableNames = new FastSet<String>();
		try {
			setVariables(pid, variablesToSet);
			ProcessInstance pi = buildProcessEngine().getExecutionService().signalExecutionById(pid, signalName);
			if(!pi.isEnded()) {
				variableNames = buildProcessEngine().getExecutionService().getVariableNames(pid);
			}
		}catch(JbpmException je) {
			error += je.getMessage();
		}
		
		if(returnVariables != null) {
			Iterator<String> it = returnVariables.iterator();
			while(it.hasNext()) {
				String variableName = it.next();
				if(variableNames.contains(variableName)) {
					toReturnMap.put(variableName, buildProcessEngine().getExecutionService().getVariable(pid, variableName));
				}
			}
		}
		
		if(!"".equals(error)) {
			toReturnMap.put(JbpmManager.ERROR, error);
		}
		return toReturnMap;
	}
	
	/**
	 * Get the set of all activities that are active. Returns an empty set in case there are no activities active
	 * @param pid The process instance id
	 * @return The set of all activities that are active
	 */
	public Set<String> findActiveActivityNames(String pid) {
		Set<String> activeActivityNames = null;
		ProcessInstance pInstance = getProcessInstance(pid);
		if(pInstance != null) {
			activeActivityNames = pInstance.findActiveActivityNames();
		}
		return activeActivityNames;
	}
	
	/**
	 * Retrieves a map of process variables
	 * @param pid The process instance id
	 * @param variableNames A List of required variables
	 * @return The variable list
	 */
	public Map<String, Object> getVariables(String pid, List<String> variableNames) {
		Map<String, Object> returnMap = new FastMap<String, Object>();
		Set<String> nameSet =null;
		if(variableNames != null) {
			nameSet = new FastSet<String>();
			nameSet.addAll(variableNames);
		}
		try {
			returnMap.putAll(buildProcessEngine().getExecutionService().getVariables(pid, nameSet));
		}catch(JbpmException je) {
			returnMap.put(JbpmManager.ERROR, je.getMessage());
		}
		
		return returnMap;
	}
	
	/**
	 * Retrieves a map of process variables from history
	 * @param pid The process id
	 * @param variableNames A List of required variables
	 * @return The variable list
	 */
	public Map<String, Object> getHistoryVariables(String pid, List<String> variableNames) {
		Map<String, Object> returnMap = new FastMap<String, Object>();
		Set<String> nameSet =null;
		if(variableNames != null) {
			nameSet = new FastSet<String>();
			nameSet.addAll(variableNames);
		}
		try {
			returnMap = (Map<String, Object>) buildProcessEngine().getHistoryService().getVariables(pid, nameSet);
		}catch(JbpmException je) {
			je.printStackTrace();
			returnMap.put(JbpmManager.ERROR, je.getMessage());
		}
		return returnMap;
	}
	
	/**
	 * Creates or overwrites variable values in the referenced execution
	 * @param pid The proces instance id
	 * @param variables The variables to write
	 * @return A Map containing error messages
	 */
	public Map<String, Object> setVariables(String pid, Map<String, Object> variables) {
		Map<String, Object> returnMap = new FastMap<String, Object>();
		try {
			buildProcessEngine().getExecutionService().setVariables(pid, variables);
		}catch(JbpmException je) {
			returnMap.put(JbpmManager.ERROR, je.getMessage());
		}
		
		return returnMap;
	}
	
	/**
	 * Creates a ProcessVariableQuery
	 * @return The ProcessVariableQuery object
	 */
	public ProcessVariableQuery createProcessVariableQuery() {
		return new ProcessVariableQueryImpl();
	}
	
	/**
	 * Creates a FullHistoryVariableQuery
	 * @return The FullHistoryVariableQuery object
	 */
	public FullHistoryVariableQuery createFullHistoryVariableQuery() {
		return new FullHistoryVariableQueryImpl();
	}
	
	/**
	 * Delete a process instance
	 * @param pid The process instance id
	 * @return A Map containing error messages
	 */
	public Map<String, Object> deleteProcessInstance(String pid) {
		Map<String, Object> returnMap = new FastMap<String, Object>();
		try {
			buildProcessEngine().getExecutionService().deleteProcessInstance(pid);
		} catch(JbpmException je) {
			returnMap.put(JbpmManager.ERROR, je.getMessage());
		}
		return returnMap;
	}
	
	/**
	 * Is the process ended?
	 * @param pid The process instance id
	 * @return Is the process ended?
	 */
	public boolean isEnded(String pid) {
		ProcessInstance pi = getProcessInstance(pid);
		return pi == null ? true : pi.isEnded();
	}
	
	/**
	 * Returns the outgoing transitions and the destination activities of the current active activity
	 * @param pid The process id
	 * @return A Map containing destination activity with relative incoming transition
	 */
	public Map<String, String> getOutgoingDestinations(String pid) {
		Map<String, String> destinationMap = new FastMap<String, String>();
		EnvironmentFactory environmentFactory = (EnvironmentFactory)buildProcessEngine();
		Execution execution = buildProcessEngine().getExecutionService().findExecutionById(pid);
		if(execution instanceof ExecutionImpl) {
			ExecutionImpl executionImpl = (ExecutionImpl)execution;
			EnvironmentImpl environment = environmentFactory.openEnvironment();
			ActivityImpl activity = null;
			try {
				activity  = executionImpl.getActivity();
			} finally {
				environment.close();
			}
			List<Transition> outgoingTransitions = (List<Transition>) (activity != null ? activity.getOutgoingTransitions() : null);
			if(outgoingTransitions != null) {
				for(Transition transition : outgoingTransitions) {
					destinationMap.put(transition.getDestination().getName(), transition.getName());
				}
			}
		}
		return destinationMap;
	}
}
