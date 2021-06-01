package com.mapsengineering.gzoomjbpm;

import java.util.Map;

import org.jbpm.api.activity.ActivityExecution;
import org.jbpm.api.activity.ExternalActivityBehaviour;


public class WaitingStatusActivity extends StatusActivity implements ExternalActivityBehaviour {
  
	private static final long serialVersionUID = 1L;
	  
	@Override
	public void execute(ActivityExecution execution) {
		super.execute(execution);
		execution.waitForSignal();
	}

	@Override
	public void signal(ActivityExecution execution,
			String signalName, 
            Map<String, ?> parameters) {
		execution.take(signalName);
	}
}
