package com.mapsengineering.gzoomjbpm.history;

import org.jbpm.pvm.internal.env.EnvironmentImpl;
import org.jbpm.pvm.internal.history.HistoryEvent;
import org.jbpm.pvm.internal.history.HistorySession;
import org.jbpm.pvm.internal.history.events.VariableCreate;
import org.jbpm.pvm.internal.history.events.VariableUpdate;
import org.jbpm.pvm.internal.history.model.HistoryProcessInstanceImpl;
import org.jbpm.pvm.internal.history.model.HistoryTaskImpl;
import org.jbpm.pvm.internal.model.ActivityImpl;
import org.jbpm.pvm.internal.model.ExecutionImpl;
import org.jbpm.pvm.internal.session.DbSession;
import org.jbpm.pvm.internal.task.TaskImpl;
import org.jbpm.pvm.internal.type.Variable;

public class FullVariableHistorySessionImpl implements HistorySession {

	@Override
	public void process(HistoryEvent historyEvent) {
		if(historyEvent instanceof VariableCreate || historyEvent instanceof VariableUpdate) {
			DbSession dbSession = EnvironmentImpl.getFromCurrent(DbSession.class);
			Variable variable= null;
			ActivityImpl act = null;
			if(historyEvent instanceof VariableCreate) {
				VariableCreate variableCreate = (VariableCreate) historyEvent;
				variable = variableCreate.getVariable();
			}
			if(historyEvent instanceof VariableUpdate) {
				VariableUpdate variableUpdate = (VariableUpdate) historyEvent;
				variable = variableUpdate.getVariable();
			}
			
			HistoryProcessInstanceImpl historyProcessInstance = null;
			ExecutionImpl processInstance = variable.getProcessInstance();
			if (processInstance!=null) {
		      long processInstanceDbid = processInstance.getDbid();
		      historyProcessInstance = dbSession.get(HistoryProcessInstanceImpl.class, processInstanceDbid);
		      act = processInstance.getActivity();
		    }
			
			HistoryTaskImpl historyTask = null;
		    TaskImpl task = variable.getTask();
		    if (task!=null) {
		      long taskDbid = task.getDbid();
		      historyTask = dbSession.get(HistoryTaskImpl.class, taskDbid);
		    }
		    
		    FullHistoryVariableImpl fullHistoryVariable = new FullHistoryVariableImpl(historyProcessInstance, historyTask, variable, act);
		    dbSession.save(fullHistoryVariable);
		}
	}
}
