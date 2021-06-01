package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants.MessageCode;

public class QueryImportService {
	private ImportManager importManager;
	private List<String> entityListToImport;
	private EntityCondition readConditionDefault;
	private List<String> orderBy;
	private String jobLogId;
	private String entityName;
	private String dataSource;
	private Timestamp startTimestamp;
	private final String resource = "StandardImportUiLabels";
	
	/**
	 * Constructor
	 * @param importManager
	 * @param entityListToImport
	 * @param readConditionDefault
	 * @param orderBy
	 * @param jobLogId
	 * @param startTimestamp
	 * @throws Exception
	 */
	public QueryImportService(ImportManager importManager, List<String> entityListToImport, EntityCondition readConditionDefault, List<String> orderBy, String jobLogId, Timestamp startTimestamp) throws Exception {
		this.importManager = importManager;
		this.entityListToImport = entityListToImport;
		this.readConditionDefault = readConditionDefault;
		this.orderBy = orderBy;
		this.jobLogId = jobLogId;
		this.startTimestamp = startTimestamp;
		findDataSource();
	}
	
	/**
	 * trova il dataSource
	 * @throws Exception
	 */
	private void findDataSource() throws Exception {
		if (UtilValidate.isNotEmpty(entityListToImport)) {
			entityName = entityListToImport.get(0);
			List<GenericValue> list = importManager.getDelegator().findList(entityName, readConditionDefault, null, orderBy, null, false);
			GenericValue item = EntityUtil.getFirst(list);
			if (UtilValidate.isNotEmpty(item)) {
				dataSource = item.getString(E.dataSource.name());
			}
		}
	}
	
	/**
	 * esegue le query pre caricamento
	 * @throws Exception
	 */
	public void executePreQueries() throws Exception {
		if (UtilValidate.isNotEmpty(dataSource)) {
			List<GenericValue> queryList = importManager.getDelegator().findList(E.QueryConfig.name(), EntityCondition.makeCondition(getQueryConfigPreConditions()), null, getQueryConfigOrderBy(), null, false);
		    if (UtilValidate.isNotEmpty(queryList)) {
		    	for (GenericValue query : queryList) {
		    		executeQuery(query, false);
		    	}
		    }			
		}
	}
	
	/**
	 * ritorna le condizioni di ricerca delle query pre caricamento
	 * @return
	 */
	private List<EntityCondition> getQueryConfigPreConditions() {
		List<EntityCondition> queryConfigPreConditions = getQueryConfigBaseConditions();
		queryConfigPreConditions.add(EntityCondition.makeCondition(E.queryCode.name(), EntityOperator.LIKE, dataSource + "%PRE%"));
		return queryConfigPreConditions;
	}
	
	/**
	 * esegue le query post caricamento
	 * @throws Exception
	 */
	public void executePostQueries() throws Exception {
		if (UtilValidate.isNotEmpty(dataSource)) {
			List<GenericValue> queryList = importManager.getDelegator().findList(E.QueryConfig.name(), EntityCondition.makeCondition(getQueryConfigPostConditions()), null, getQueryConfigOrderBy(), null, false);
		    if (UtilValidate.isNotEmpty(queryList)) {
		    	for (GenericValue query : queryList) {
		    		executeQuery(query, true);
		    	}
		    }			
		}
	}
	
	/**
	 * ritorna le condizioni di ricerca delle query post caricamento
	 * @return
	 */
	private List<EntityCondition> getQueryConfigPostConditions() {
		List<EntityCondition> queryConfigPostConditions = getQueryConfigBaseConditions();
		queryConfigPostConditions.add(EntityCondition.makeCondition(E.queryCode.name(), EntityOperator.LIKE, dataSource + "%POST%"));
		return queryConfigPostConditions;
	}
	
	/**
	 * ritorna le condizioni comuni di ricerca delle query
	 * @return
	 */
	private List<EntityCondition> getQueryConfigBaseConditions() {
		List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
		conditionList.add(EntityCondition.makeCondition(E.queryType.name(), E.A.name()));
		conditionList.add(EntityCondition.makeCondition(E.queryActive.name(), E.Y.name()));
		if (E.OrganizationInterface.name().equals(entityName) || E.PersonInterface.name().equals(entityName)) {
			conditionList.add(EntityCondition.makeCondition(E.queryCtx.name(), E.CTX_PY.name()));
		} else if (E.GlAccountInterface.name().equals(entityName) || E.AcctgTransInterface.name().equals(entityName)) {
			conditionList.add(EntityCondition.makeCondition(E.queryCtx.name(), E.CTX_AC.name()));
		} else {
			conditionList.add(EntityCondition.makeCondition(E.queryCtx.name(), EntityOperator.NOT_EQUAL, E.CTX_PY.name()));
			conditionList.add(EntityCondition.makeCondition(E.queryCtx.name(), EntityOperator.NOT_EQUAL, E.CTX_AC.name()));
		}
		return conditionList;
	}
	
	/**
	 * ritorna  ordinamento query
	 * @return
	 */
	private List<String> getQueryConfigOrderBy() {
		List<String> queryConfigOrderBy = new ArrayList<String>();
		queryConfigOrderBy.add(E.queryCode.name());
		return queryConfigOrderBy;
	}
	
	/**
	 * esegue la query
	 * @param query
	 * @param isPost
	 * @throws Exception
	 */
	private void executeQuery(GenericValue query, boolean isPost) throws Exception {
		Map<String, Object> serviceInMap = importManager.getDispatcher().getDispatchContext().makeValidContext(E.queryExecutorService.name(), ModelService.IN_PARAM, importManager.getContext());
		serviceInMap.put(E.queryId.name(), query.getString(E.queryId.name()));
		if (isPost) {
			serviceInMap.put(E.cond0Info.name(), jobLogId);
		}
		
		List<Map<String, Object>> messages = new ArrayList<Map<String, Object>>();
		
		String msg = UtilProperties.getMessage(resource, "QUERY_EXCECUTION", importManager.getLocale());
		msg = msg.replace(E.queryCode.name(), query.getString(E.queryCode.name()));
		messages.add(ServiceLogger.makeLogInfo(msg, MessageCode.INFO_GENERIC.toString()));
		Map<String, Object> resultMap = importManager.getDispatcher().runSync(E.queryExecutorService.name(), serviceInMap);
		if (! ServiceUtil.isSuccess(resultMap)) {
			String errorMsg = UtilProperties.getMessage(resource, "QUERY_EXCECUTION_ERROR", importManager.getLocale());
			errorMsg = errorMsg.replace(E.queryCode.name(), query.getString(E.queryCode.name()));
			errorMsg += ": " + resultMap.get(ModelService.ERROR_MESSAGE);
			messages.add(ServiceLogger.makeLogInfo(errorMsg, MessageCode.INFO_GENERIC.toString()));
		} else {
			String msgExec = UtilProperties.getMessage(resource, "QUERY_EXCECUTION_SUCCESS", importManager.getLocale());
			msgExec = msgExec.replace(E.queryCode.name(), query.getString(E.queryCode.name()));
			messages.add(ServiceLogger.makeLogInfo(msgExec, MessageCode.INFO_GENERIC.toString()));
		}
		writeLogs(messages);
	}
	
	/**
	 * scrive i log
	 * @param messages
	 * @throws Exception
	 */
	private void writeLogs(List<Map<String, Object>> messages) throws Exception {
		Map<String, Object> logParameters = new HashMap<String, Object>();
		logParameters.put(ServiceLogger.JOB_LOG_ID, jobLogId);
		logParameters.put(ServiceLogger.SERVICE_NAME, E.queryExecutorService.name());
		logParameters.put(ServiceLogger.USER_LOGIN, importManager.getUserLogin());
		logParameters.put(ServiceLogger.LOG_DATE, startTimestamp);
		logParameters.put(ServiceLogger.MESSAGES, messages);
		ServiceLogger.writeLogs(importManager.getDispatcher().getDispatchContext(), logParameters);
	}
}
