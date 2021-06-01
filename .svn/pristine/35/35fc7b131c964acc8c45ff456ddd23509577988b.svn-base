package com.mapsengineering.gzoomjbpm.query;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.hibernate.Query;
import org.jbpm.api.Configuration;
import org.jbpm.api.Execution;
import org.jbpm.pvm.internal.query.AbstractQuery;
import org.jbpm.pvm.internal.type.Variable;

/**
 * 
 * @author Aldo Figlioli Maps S.p.A.
 * ExecutionIdByVariableQuery implementation
 *
 */
public class ExecutionIdByVariableQueryImpl extends AbstractQuery implements ExecutionIdByVariableQuery {
	
	private Map<String, Object> variables = new FastMap<String, Object>();

	@Override
	protected void applyParameters(Query query) {
	}

	@Override
	public String hql() {
		StringBuilder hql = new StringBuilder();
		hql.append(" select variable.execution as execution_id");
		hql.append(" from ");
		hql.append(Variable.class.getName());
		hql.append(" as variable ");
		
		Iterator<String> it = variables.keySet().iterator();
		
		while(it.hasNext()) {
			String name = it.next();
			Object value = variables.get(name);
			
			appendWhereClause(" variable.key = " + "'" + name + "' ", hql);
			if(value instanceof String) {
				appendWhereClause(" variable.string = " + "'" + value + "' ", hql);
			}
			if(value instanceof Long) {
				appendWhereClause(" variable.l = " + "'" + value + "' ", hql);
			}
			if(value instanceof Date) {
				appendWhereClause(" variable.date = " + "'" + value + "' ", hql);
			}
			if(value instanceof Double) {
				appendWhereClause(" variable.d = " + "'" + value + "' ", hql);
			}
		}
		
		addOrderByClause(" variable.execution ");
		appendOrderByClause(hql);
		
		//System.out.println("------QUERY=" + hql.toString());
		
		return hql.toString();
	}

	@Override
	public List<?> untypedList() {
		return (List<?>) Configuration.getProcessEngine().execute(this);
	}
	
	@Override
	public List<String> list() {
		List<String> resultList = new FastList<String>();
		
		List<?> untypedList = untypedList();
		Iterator<?> it = untypedList.iterator();
		while(it.hasNext()) {
			Execution ex = (Execution)it.next();
			resultList.add(ex.getId());
		}
		return resultList;
	}

	@Override
	public String getQuery() {
		return hql();
	}

	@Override
	public ExecutionIdByVariableQuery variable(String name, Object value) {
		variables.put(name, value);
		return this;
	}

	@Override
	public ExecutionIdByVariableQuery variables(Map<String, Object> variables) {
		this.variables.putAll(variables);
		return this;
	}
}
