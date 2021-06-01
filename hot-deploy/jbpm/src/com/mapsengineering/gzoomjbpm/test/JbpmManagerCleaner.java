package com.mapsengineering.gzoomjbpm.test;

import java.util.Iterator;
import java.util.List;

import org.jbpm.api.Deployment;
import org.jbpm.api.DeploymentQuery;
import org.jbpm.api.ProcessEngine;

import com.mapsengineering.gzoomjbpm.JbpmManager;

public class JbpmManagerCleaner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ProcessEngine pe = JbpmManager.buildProcessEngine();
		
		DeploymentQuery dq = pe.getRepositoryService().createDeploymentQuery();
		List<Deployment> dql = dq.list();
		
		Iterator<Deployment> it = dql.iterator();
		while(it.hasNext()) {
			Deployment d = it.next();
			System.out.println("Deleting deployment: " + d.getId());
			JbpmManager.deleteDeploymentCascade(d.getId());
		}
		
		System.out.println("Process deployment deleted: " + dql.size());
		
		JbpmManager.closeProcessEngine();
	}
}
