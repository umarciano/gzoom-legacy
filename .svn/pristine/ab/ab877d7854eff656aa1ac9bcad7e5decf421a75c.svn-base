package com.mapsengineering.gzoomjbpm.rmi;

import it.sauronsoftware.base64.Base64;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jbpm.api.JbpmException;

import com.mapsengineering.gzoomjbpm.JbpmStub;
import com.mapsengineering.gzoomjbpm.util.UtilProperties;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A
 * RMI client
 *
 */
public class Client implements JbpmStub {
	
	private JbpmServer stub = null;
	private String host = null;
	private int port = -1;
	
	private static final Logger logger = Logger.getLogger(Client.class);
	
	public Client() throws Exception {
		stub = getStub();
	}
	
	@Override
	public Map<String, Object> startProcess(String processName, String description, String firstUser, Map<String, Object> initParams, List<String> returnVars) throws RemoteException {
		Map<String, Object> map = null;
		if(stub == null) {
			stub = getStub();
		}
		try {
			map = stub.startProcess(processName, description, firstUser, initParams, returnVars);
		} catch(RemoteException re) {
			logger.info("Lost connection with Jbpm Server. Retrying...");
			stub = getStub();
			try {
				map = stub.startProcess(processName, description, firstUser, initParams, returnVars);
			} catch(RemoteException re2) {
				stub = null;
				logger.error("startProcess: ", re2);
				throw re2;
			}
		}
		if(map.get(JbpmServer.ERROR) != null) {
			logger.error("startProcess: " + map.get(JbpmServer.ERROR));
			throw new JbpmException((String)map.get(JbpmServer.ERROR));
		}
		logger.info("Jbpm process created with pid: " + map.get(JbpmServer.PID));
		return map;
	}
	
	@Override
	public Map<String, Object> getVariables(String pid, List<String> variableNames) throws RemoteException {
		Map<String, Object> map = null;
		if(stub == null) {
			stub = getStub();
		}
		try {
			map = stub.getVariables(pid, variableNames);
		} catch(RemoteException re ) {
			logger.info("Lost connection with Jbpm Server. Retrying...");
			stub = getStub();
			try {
				map = stub.getVariables(pid, variableNames);
			} catch(RemoteException re2) {
				stub = null;
				throw re2;
			}
		}
		if(map.get(JbpmServer.ERROR) != null) {
			throw new JbpmException((String)map.get(JbpmServer.ERROR));
		}
		return map;
	}
	
	@Override
	public List<Map<String, Object>> getProcessHistory(String pid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getTaskList(String processName, String user) throws Exception{
		List<Map<String, Object>> list = null;
		if(stub == null) {
			stub = getStub();
		}
		try {
			list = stub.taskList(processName, user);
		} catch(RemoteException re) {
			logger.info("Lost connection with Jbpm Server. Retrying...");
			stub = getStub();
			try {
				list = stub.taskList(processName, user);
			} catch(RemoteException re2) {
				stub = null;
				throw re2;
			}
		}
		return list;
	}

	@Override
	public Map<String, Object> setStepUser(String pid, String user) throws Exception {
		Map<String, Object> map = null;
		if(stub == null) {
			stub = getStub();
		}
		try {
			map = stub.changeUser(pid, user);
		} catch(RemoteException re) {
			logger.info("Lost connection with Jbpm Server. Retrying...");
			stub = getStub();
			try {
				map = stub.changeUser(pid, user);
			} catch(RemoteException re2) {
				stub = null;
				throw re2;
			}
		}
		if(map.get(JbpmServer.ERROR) != null) {
			throw new JbpmException((String)map.get(JbpmServer.ERROR));
		}
		return map;
	}

	@Override
	public Map<String, Object> signalExecution(String pid, String signalName, String user,	Map<String, Object> variablesToSet, List<String> toReturnVariables) throws Exception {
		Map<String, Object> map = null;
		if(stub == null) {
			stub = getStub();
		}
		try {
			map = stub.stepOver(pid, signalName, user, variablesToSet, toReturnVariables);
		} catch(RemoteException re) {
			logger.info("Lost connection with Jbpm Server. Retrying...");
			stub = getStub();
			try {
				map = stub.stepOver(pid, signalName, user, variablesToSet, toReturnVariables);
			} catch(RemoteException re2) {
				stub = null;
				throw re2;
			}
		}
		if(map.get(JbpmServer.ERROR) != null) {
			throw new JbpmException((String)map.get(JbpmServer.ERROR));
		}
		return map;
	}

	@Override
	public boolean isEnded(String pid) throws Exception {
		boolean isEnded = true;
		if(stub == null) {
			stub = getStub();
		}
		try {
			isEnded = stub.isEnded(pid);
		} catch(RemoteException re) {
			logger.info("Lost connection with Jbpm Server. Retrying...");
			stub = getStub();
			try {
				isEnded = stub.isEnded(pid);
			} catch(RemoteException re2) {
				stub = null;
				throw re2;
			}
		}
		return isEnded;
	}
	
	@Override
	public Map<String, Object> deleteProcess(String pid) throws Exception {
		Map<String, Object> map = null;
		if(stub == null) {
			stub = getStub();
		}
		try {
			map = stub.deleteProcessInstance(pid);
		} catch(RemoteException re) {
			logger.info("Lost connection with Jbpm Server. Retrying...");
			stub = getStub();
			try {
				map = stub.deleteProcessInstance(pid);
			} catch(RemoteException re2) {
				stub = null;
				throw re2;
			}
		}
		return map;
	}
	
	@Override
	public Map<String, Object> getProcessHistoryImage(String pid, List<String> selectVars) throws Exception {
		Map<String, Object> map = null;
		if(stub == null) {
			stub = getStub();
		}
		try {
			map = stub.getProcessHistoryImage(pid, selectVars);
			byte[] encoded = (byte[])map.get(JbpmServer.STREAM);
			map.put(JbpmServer.STREAM, Base64.decode(encoded));
		} catch(RemoteException re) {
			logger.info("Lost connection with Jbpm Server. Retrying...");
			stub = getStub();
			try {
				map = stub.getProcessHistoryImage(pid, selectVars);
				byte[] encoded = (byte[])map.get(JbpmServer.STREAM);
				map.put(JbpmServer.STREAM, Base64.decode(encoded));
			} catch(RemoteException re2) {
				stub = null;
				throw re2;
			}
		}
		return map;
	}
	
	private JbpmServer getStub() throws RemoteException {
		JbpmServer server = null;
		if(host == null || port == -1) {
			Properties properties = UtilProperties.getProperties("jbpm_server.properties");
			this.host = properties.getProperty("host");
			this.port = Integer.valueOf(properties.getProperty("port"));
		}
		
		logger.info("Trying to connect RMI Client to Server on host: " + host + " port: " + port);
		try {
			Registry registry = LocateRegistry.getRegistry(host, port);
			server = (JbpmServer) registry.lookup("jbpm_server");
		}catch(Exception e) {
			logger.error("Jbpm RMI Client unable to connect to host " + host + " on port " + port);
			throw new RemoteException("Jbpm RMI Client unable to connect to host " + host + " on port " + port + " " + e.getMessage());
		}
		return server;
	}

	@Override
	public Map<String, String> getOutgoingDestinations(String pid) throws Exception {
		Map<String, String> map = null;
		if(stub == null) {
			stub = getStub();
		}
		try {
			map = stub.getOutgoingDestinations(pid);
		}
		catch(RemoteException re) {
			logger.info("Lost connection with Jbpm Server. Retrying...");
			stub = getStub();
			try {
				map = stub.getOutgoingDestinations(pid);
			}
			catch(RemoteException re2) {
				stub = null;
				throw re2;
			}
		}
		return map;
	}
}
