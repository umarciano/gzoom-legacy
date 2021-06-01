package com.mapsengineering.base.standardimport.workeffort;

import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.util.FindUtilService;

public class WorkEffortRootDeleter {
	private TakeOverService service;
	private String workEffortIdRoot;
	
	/**
	 * gestisce la cancellazione delle schede
	 * @param service
	 * @param workEffortIdRoot
	 */
	public WorkEffortRootDeleter(TakeOverService service, String workEffortIdRoot) {
		this.service = service;
		this.workEffortIdRoot = workEffortIdRoot;
	}
	
	/**
	 * esegue la cancellazione
	 * @throws GeneralException
	 */
	public void execute() throws GeneralException {
		Map<String, Object> serviceMapParams = buildServiceParamMap();
		service.runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortRoot.name(), E.WorkEffortView.name(), CrudEvents.OP_DELETE, serviceMapParams, E.WorkEffort.name() + FindUtilService.MSG_SUCCESSFULLY_DELETE, FindUtilService.MSG_ERROR_DELETE + E.WorkEffort.name(), true, true);
	}
	
	/**
	 * costruisce la mappa dei parametri
	 * @return
	 */
	private Map<String, Object> buildServiceParamMap() {
		return UtilMisc.toMap(E.workEffortIdRoot.name(), (Object) workEffortIdRoot, E.workEffortId.name(), (Object) workEffortIdRoot);
	}

}
