package com.mapsengineering.gzoomjbpm.query;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.hibernate.Query;
import org.jbpm.api.Configuration;
import org.jbpm.pvm.internal.history.model.HistoryProcessInstanceImpl;
import org.jbpm.pvm.internal.history.model.HistoryVariableImpl;
import org.jbpm.pvm.internal.query.AbstractQuery;
import org.jbpm.pvm.internal.repository.DeploymentProperty;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A.
 * 	ProcessVariableQuery implementation for History
 */
public class ProcessHistoryVariableQueryImpl extends AbstractQuery implements ProcessVariableQuery {
	
	private String processName;
	private Map<String, Object> whereVars = new FastMap<String, Object>();

	@Override
	protected void applyParameters(Query query) {
	}

	@Override
	public String hql() {
		StringBuilder hql = new StringBuilder();
		hql.append(" select historyVar.processInstanceId as process_instance_id," +
						" historyVar.executionId as execution_id," + 
						" deplprop.objectName as process_name," + 
						" historyVar.variableName as variable_name," +
						" historyVar.value as variable_value ");
		hql.append(" from ");
		hql.append(DeploymentProperty.class.getName());
		hql.append(" as deplprop ");
		hql.append(", " + HistoryProcessInstanceImpl.class.getName());
		hql.append(" as historyProcess ");
		hql.append(", " + HistoryVariableImpl.class.getName());
		hql.append(" as historyVar ");
		if(processName != null) {
			appendWhereClause("deplprop.objectName = '" + processName + "'" , hql);
		}
		appendWhereClause(" deplprop.key = 'pdid' ", hql);
		appendWhereClause(" deplprop.stringValue = historyProcess.processDefinitionId ", hql);
		appendWhereClause(" historyProcess.processInstanceId = historyVar.processInstanceId ", hql);
		
		addOrderByClause(" historyVar.processInstanceId ");
		
		Iterator<String> it = whereVars.keySet().iterator();
		while(it.hasNext()) {
			String name = it.next();
			Object value = whereVars.get(name);
			appendWhereClause(" historyVar.variableName = " + "'" + name + "' ", hql);
			appendWhereClause(" historyVar.value = " + "'" + value + "' ", hql);
		}
		
		appendOrderByClause(hql);
		
		//System.out.println("------QUERY=" + hql.toString());
		
		return hql.toString();
	}

	@Override
	public ProcessVariableQuery processName(String processName) {
		this.processName = processName;
		return this;
	}
	
	@Override
	public ProcessVariableQuery user(String user) {
		whereVars.put(ProcessVariableQuery.USER, user);
		return this;
	}
	

	@Override
	public ProcessVariableQuery variableInWhereClause(String name, Object value) {
		whereVars.put(name, value);
		return this;
	}

	@Override
	public ProcessVariableQuery variablesInWhereClause(Map<String, Object> variables) {
		whereVars.putAll(variables);
		return this;
	}

	@Override
	public List<?> untypedList() {
		return (List<?>) Configuration.getProcessEngine().execute(this);
	  }
	
	@Override
	public List<ProcessVariableQueryResult> list() {
		List<ProcessVariableQueryResult> resultList = new FastList<ProcessVariableQueryResult>();
		//List<String> executionIdList = null;
		List<?> untypedList = untypedList();
		Iterator<?> it = untypedList.iterator();
		String lastProcessId = "";
		ProcessVariableQueryResult resultItem = null;
		
	/*	if(!whereVars.keySet().isEmpty()) {
			ExecutionIdByVariableQuery ebv = new ExecutionIdByVariableQueryImpl();
			ebv.variables(whereVars);
			executionIdList = ebv.list();
		} */
		while(it.hasNext()) {
			Object[] row = (Object[])it.next();
		//	String executionId = ((Long)row[0]).toString();
			String processId = (String)row[0];
		//	if(executionIdList == null || executionIdList.contains(processId)) {
				if(!processId.equals(lastProcessId)) {
					resultItem = new ProcessVariableQueryResult();
					//resultItem.setExecutionId(executionId);
					resultItem.setProcessId(processId);
					resultItem.setProcessName((String)row[1]);
				}
				
				
				resultItem.putVariable((String)row[2], row[3]);
					
				if(!processId.equals(lastProcessId)) {
					lastProcessId = processId;
					resultList.add(resultItem);
				}
		//	}
		}
		return resultList;
	}

	@Override
	public String getQuery() {
		return hql();
	}
}
