package com.mapsengineering.gzoomjbpm.query;

import java.util.Iterator;
import java.util.Map;

import javolution.util.FastMap;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A.
 * Class rapresenting the ProcessVariableQuery result
 */
public class ProcessVariableQueryResult {
	
	private String processName;
	private String executionId;
	private String processId;
	private String user;
	private Map<String, Object> varMap = new FastMap<String, Object>();
	
	public String getProcessName() {
		return processName;
	}
	
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	
	public String getExecutionId() {
		return executionId;
	}
	
	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}
	
	public String getProcessId() {
		return processId;
	}
	
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public Map<String, Object> getVarMap() {
		return varMap;
	}
	
	public void setVarMap(Map<String, Object> varMap) {
		this.varMap = varMap;
	}
	
	public Object getVariable(String varName) {
		return varMap.get(varName);
	}
	
	public Object putVariable(String name, Object value) {
		return varMap.put(name, value);
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(" executionId: " + getExecutionId() + ";");
		str.append(" processId: " + getProcessId() + ";");
		str.append(" processName: " + getProcessName() + ";");
		Iterator<String> it = getVarMap().keySet().iterator();
		while(it.hasNext()) {
			String name = it.next();
			Object value = getVariable(name);
			str.append(" " + name + ": " + value + ";");
		}
		return str.toString();
	}

}
