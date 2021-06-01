package com.mapsengineering.gzoomjbpm;

import org.jbpm.api.activity.ActivityBehaviour;
import org.jbpm.api.activity.ActivityExecution;

public class StatusActivity implements ActivityBehaviour {

	private static final long serialVersionUID = -5933524316495056001L;
	private String status;
	private String user;
	
	@Override
	public void execute(ActivityExecution execution) {
		execution.setVariable("status", status);
		execution.setVariable("user", user);
	}

}
