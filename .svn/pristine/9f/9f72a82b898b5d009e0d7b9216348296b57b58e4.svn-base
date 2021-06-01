package com.mapsengineering.gzoomjbpm.query;

import java.util.Iterator;
import java.util.List;

import javolution.util.FastList;

import org.hibernate.Query;
import org.jbpm.api.Configuration;
import org.jbpm.pvm.internal.query.AbstractQuery;

import com.mapsengineering.gzoomjbpm.history.FullHistoryVariableImpl;

public class FullHistoryVariableQueryImpl extends AbstractQuery implements FullHistoryVariableQuery {
	
	protected String processInstanceId;
	protected String executionId;
	protected String value;
	protected String activityName;
	protected List<String> variableNames = new FastList<String>();

	@Override
	public String hql() {
		StringBuilder hql = new StringBuilder();
		hql.append(" select fullHistory.processInstanceId, fullHistory.executionId");
		hql.append(" ,fullHistory.activityName, fullHistory.variableName, fullHistory.value");
		hql.append(" from ");
		hql.append(FullHistoryVariableImpl.class.getName());
		hql.append(" as fullHistory ");
		
		if (processInstanceId != null) {
			appendWhereClause(" fullHistory.processInstanceId = '"+processInstanceId+"' ", hql);
		}
		
		if (executionId != null) {
			appendWhereClause(" fullHistory.executionId = '"+executionId+"' ", hql);
		}
		
		if (value != null) {
			appendWhereClause(" fullHistory.value = '"+value+"' ", hql);
		}
		
		if (activityName != null) {
			appendWhereClause(" fullHistory.activityName = '"+activityName+"' ", hql);
		}
		
		if(variableNames != null) {
			Iterator<String> selectIt = variableNames.iterator();
			if(!variableNames.isEmpty()) {
				if(!isWhereAdded) {
					hql.append(" where ");
					isWhereAdded = true;
				}
				else {
					hql.append(" and ");
				}
			}
			boolean first = true;
			while(selectIt.hasNext()) {
				if(first) {
					hql.append(" ( ");
					first = false;
				}
				else {
					hql.append(" or ");
				}
				hql.append(" fullHistory.variableName = '" + selectIt.next() + "' ");
			}
			if(!variableNames.isEmpty()) {
				hql.append(" ) ");
			}
		}
		//System.out.println("------QUERY=" + hql.toString());
		
		return hql.toString();
	}

	@Override
	protected void applyParameters(Query query) {
	}

	@Override
	public String getQuery() {
		return hql();
	}
	
	@Override
	public List<?> untypedList() {
		return (List<?>) Configuration.getProcessEngine().execute(this);
	}

	@Override
	public List<FullHistoryVariableQueryResult> list() {
		List<FullHistoryVariableQueryResult> resultList = new FastList<FullHistoryVariableQueryResult>();
		FullHistoryVariableQueryResult resultItem;
		List<?> untypedList = untypedList();
		Iterator<?> it = untypedList.iterator();
		while(it.hasNext()) {
			Object[] row = (Object[])it.next();
			resultItem = new FullHistoryVariableQueryResult();
			resultItem.setProcessId((String)row[0]);
			resultItem.setExecutionId((String)row[1]);
			resultItem.setActivityName((String)row[2]);
			resultItem.setVariableName((String)row[3]);
			resultItem.setValue((String)row[4]);
			
			resultList.add(resultItem);
		}
		return resultList;
	}

	@Override
	public FullHistoryVariableQuery executionId(String executionId) {
		this.executionId = executionId;
		return this;
	}

	@Override
	public FullHistoryVariableQuery activityName(String activityName) {
		this.activityName = activityName;
		return this;
	}

	@Override
	public FullHistoryVariableQuery processInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
		return this;
	}

	@Override
	public FullHistoryVariableQuery value(String value) {
		this.value = value;
		return this;
	}

	@Override
	public FullHistoryVariableQuery variableNames(List<String> variableNames) {
		this.variableNames = variableNames;
		return this;
	}
}
