package com.mapsengineering.gzoomjbpm.query;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.hibernate.Query;
import org.jbpm.api.Configuration;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.jbpm.pvm.internal.query.AbstractQuery;
import org.jbpm.pvm.internal.repository.DeploymentProperty;
import org.jbpm.pvm.internal.type.Variable;

import com.mapsengineering.gzoomjbpm.query.ProcessVariableQuery;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A.
 * 	ProcessVariableQuery implementation
 */
public class ProcessVariableQueryImpl extends AbstractQuery implements ProcessVariableQuery {
	
	private String processName;
	private Map<String, Object> whereVars = new FastMap<String, Object>();

	@Override
	protected void applyParameters(Query query) {
	}

	@Override
	public String hql() {
		StringBuilder hql = new StringBuilder();
		hql.append(" select execution.dbid as execution_id," +
						" execution.id as process_id," + 
						" deplprop.objectName as process_name," + 
						" variable.key as variable_name," +
						" variable.string as string_value ");
		hql.append(" from ");
		hql.append(DeploymentProperty.class.getName());
		hql.append(" as deplprop ");
		hql.append(", " + ExecutionImpl.class.getName());
		hql.append(" as execution ");
		hql.append(", " + Variable.class.getName());
		hql.append(" as variable ");
		if(processName != null) {
			appendWhereClause("deplprop.objectName = '" + processName + "'" , hql);
		}
		appendWhereClause(" deplprop.key = 'pdid' ", hql);
		appendWhereClause(" deplprop.stringValue = execution.processDefinitionId ", hql);
		appendWhereClause(" execution.dbid = variable.execution ", hql);
		
		addOrderByClause(" variable.execution ");
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
		List<String> executionIdList = null;
		List<?> untypedList = untypedList();
		Iterator<?> it = untypedList.iterator();
		String lastExecutionId = "";
		ProcessVariableQueryResult resultItem = null;
		
		if(!whereVars.keySet().isEmpty()) {
			ExecutionIdByVariableQuery ebv = new ExecutionIdByVariableQueryImpl();
			ebv.variables(whereVars);
			executionIdList = ebv.list();
		}
		while(it.hasNext()) {
			Object[] row = (Object[])it.next();
			String executionId = ((Long)row[0]).toString();
			String processId = (String)row[1];
			if(executionIdList == null || executionIdList.contains(processId)) {
				if(!executionId.equals(lastExecutionId)) {
					resultItem = new ProcessVariableQueryResult();
					resultItem.setExecutionId(executionId);
					resultItem.setProcessId((String)row[1]);
					resultItem.setProcessName((String)row[2]);
				}
				
				
				resultItem.putVariable((String)row[3], row[4]);
					
				if(!executionId.equals(lastExecutionId)) {
					lastExecutionId = executionId;
					resultList.add(resultItem);
				}
			}
		}
		return resultList;
	}

	@Override
	public String getQuery() {
		return hql();
	}
}
