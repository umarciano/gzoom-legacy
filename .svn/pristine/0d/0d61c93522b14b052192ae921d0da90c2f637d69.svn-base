package com.mapsengineering.gzoomjbpm.history;

import org.jbpm.pvm.internal.history.model.HistoryProcessInstanceImpl;
import org.jbpm.pvm.internal.history.model.HistoryTaskImpl;
import org.jbpm.pvm.internal.history.model.HistoryVariableImpl;
import org.jbpm.pvm.internal.id.DbidGenerator;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.type.Variable;

public class FullHistoryVariableImpl extends HistoryVariableImpl {

	private static final long serialVersionUID = 6902492488068466542L;
	
	protected ActivityImpl act;
	protected String activityName;
	
	public FullHistoryVariableImpl() {
		
	}
	
	public FullHistoryVariableImpl(HistoryProcessInstanceImpl historyProcessInstance, HistoryTaskImpl historyTask, Variable variable, ActivityImpl act) {
		super(historyProcessInstance, historyTask, variable);
		this.dbid = DbidGenerator.getDbidGenerator().getNextId();
		this.act = act;
		if(act != null) {
			activityName = act.getName();
		}
	}

	public ActivityImpl getActivity() {
		return this.act;
	}
}
