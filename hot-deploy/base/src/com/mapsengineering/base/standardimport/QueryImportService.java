package com.mapsengineering.base.standardimport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mapsengineering.base.util.UtilGenericValue;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.util.JobLogLog;

/**
 * Gestione esecuzione query di PRE e POST importazione standard
 * @author nito
 *
 */
public class QueryImportService {
    private ImportManager importManager;
    private List<String> entityListToImport;
    private String jobLogId;
    private final String resource = "StandardImportUiLabels";
    private List<String> queryPreMessages;
    private List<String> queryPostMessages;
    private List<GenericValue> queryConfigPreList;
    private List<GenericValue> queryConfigPostList;
    
    /**
     * Constructor
     * @param importManager
     * @param entityListToImport
     * @param jobLogId
     * @throws Exception
     */
    public QueryImportService(ImportManager importManager, List<String> entityListToImport, String jobLogId) throws Exception {
        this.importManager = importManager;
        this.entityListToImport = entityListToImport;
        this.jobLogId = jobLogId;
		this.queryConfigPreList = new ArrayList<GenericValue>();
		this.queryConfigPostList = new ArrayList<GenericValue>();
        this.queryPreMessages = new ArrayList<String>();
        this.queryPostMessages = new ArrayList<String>();
        findQueryConfigPre();
        findQueryConfigPost();
    }
    
    /**
     * Trova le query da elaborare in base alle entity da importare e i relativi dataSource
     * @throws Exception
     */
    public void findQueryConfigPre() throws Exception {
         if (UtilValidate.isNotEmpty(entityListToImport)) {
             for (String entityName : entityListToImport) {
                Set<String> fieldsToSelect = UtilMisc.toSet(E.dataSource.name());
                EntityFindOptions distinctOption = new EntityFindOptions();
                distinctOption.setDistinct(true);
                List<GenericValue> list = importManager.getDelegator().findList(entityName, null, fieldsToSelect, null, distinctOption, false);
                for (GenericValue item : list) {
                    String dataSource = item.getString(E.dataSource.name());
                    List<GenericValue> queryList = importManager.getDelegator().findList(E.QueryConfig.name(), EntityCondition.makeCondition(getQueryConfigPreConditions(entityName, dataSource)), null, getQueryConfigOrderBy(), null, false);
                    if (UtilValidate.isNotEmpty(queryList)) {
                        for (GenericValue query : queryList) {
                        	if (! UtilGenericValue.containsGenericValue(queryConfigPreList, query)) {
                        		queryConfigPreList.add(query);
                        	}
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Trova le query da elaborare in base alle entity da importare e i relativi dataSource
     * @throws Exception
     */
    public void findQueryConfigPost() throws Exception {
         if (UtilValidate.isNotEmpty(entityListToImport)) {
             for (String entityName : entityListToImport) {
                Set<String> fieldsToSelect = UtilMisc.toSet(E.dataSource.name());
                EntityFindOptions distinctOption = new EntityFindOptions();
                distinctOption.setDistinct(true);
                List<GenericValue> list = importManager.getDelegator().findList(entityName, null, fieldsToSelect, null, distinctOption, false);
                for (GenericValue item : list) {
                    String dataSource = item.getString(E.dataSource.name());
                    List<GenericValue> queryList = importManager.getDelegator().findList(E.QueryConfig.name(), EntityCondition.makeCondition(getQueryConfigPostConditions(entityName, dataSource)), null, getQueryConfigOrderBy(), null, false);
                    if (UtilValidate.isNotEmpty(queryList)) {
                        for (GenericValue query : queryList) {
                        	if (! UtilGenericValue.containsGenericValue(queryConfigPostList, query)) {
                        		queryConfigPostList.add(query);
                        	}
                        }
                    }
                }
            }
        }
    }
    
    /**
     * esegue le query pre caricamento
     * @throws Exception
     */
    public void executePreQueries() throws Exception {
        for (GenericValue query : queryConfigPreList) {
            executeQuery(query, false);
        }
    }
    
    /**
     * Ritorna le condizioni di ricerca delle query PRE caricamento
     * @param entityName per recuperare il contesto
     * @param dataSource
     * @return ritorna le condizioni di ricerca delle query pre caricamento
     */
    private List<EntityCondition> getQueryConfigPreConditions(String entityName, String dataSource) {
        List<EntityCondition> queryConfigPreConditions = getQueryConfigBaseConditions(entityName);
        queryConfigPreConditions.add(EntityCondition.makeCondition(E.queryCode.name(), EntityOperator.LIKE, dataSource + "%PRE%"));
        return queryConfigPreConditions;
    }
    
    /**
     * esegue le query post caricamento
     * @throws Exception
     */
    public void executePostQueries() throws Exception {
        for (GenericValue query : queryConfigPostList) {
            executeQuery(query, true);
        }
    }
    
    /**
     * Ritorna le condizioni di ricerca delle query POST caricamento
     * @param entityName per recuperare il contesto
     * @param dataSource
     * @return ritorna le condizioni di ricerca delle query post caricamento
     */
    private List<EntityCondition> getQueryConfigPostConditions(String entityName, String dataSource) {
        List<EntityCondition> queryConfigPostConditions = getQueryConfigBaseConditions(entityName);
        queryConfigPostConditions.add(EntityCondition.makeCondition(E.queryCode.name(), EntityOperator.LIKE, dataSource + "%POST%"));
        return queryConfigPostConditions;
    }
    
    /**
     * @param entityName per recuperare il contesto
     * @return ritorna le condizioni comuni di ricerca delle query
     */
    private List<EntityCondition> getQueryConfigBaseConditions(String entityName) {
        List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
        conditionList.add(EntityCondition.makeCondition(E.queryType.name(), E.A.name()));
        conditionList.add(EntityCondition.makeCondition(E.queryActive.name(), E.Y.name()));
        if (E.OrganizationInterface.name().equals(entityName) || E.PersonInterface.name().equals(entityName) || E.AllocationInterface.name().equals(entityName)) {
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
     * @return queryCode
     */
    private List<String> getQueryConfigOrderBy() {
        List<String> queryConfigOrderBy = new ArrayList<String>();
        queryConfigOrderBy.add(E.queryCode.name());
        return queryConfigOrderBy;
    }
    
    /**
     * esegue la query, imposta eventuale jobLogId nelle query di POST, colleziona i messaggi
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
        
        Map<String, Object> queryCodeLogParameters = UtilMisc.toMap(E.queryCode.name(), query.getString(E.queryCode.name()));
        JobLogLog execStart = new JobLogLog().initLogCode(resource, "QUERY_EXCEC", queryCodeLogParameters, importManager.getLocale());
        if (isPost) {
            queryPostMessages.add(execStart.getLogMessage());
        } else {
            queryPreMessages.add(execStart.getLogMessage());
        }
        Map<String, Object> resultMap = importManager.getDispatcher().runSync(E.queryExecutorService.name(), serviceInMap);
        if (! ServiceUtil.isSuccess(resultMap)) {
            JobLogLog execError = new JobLogLog().initLogCode(resource, "QUERY_EXCEC_ERROR", queryCodeLogParameters, importManager.getLocale());
            String errorMsg = execError.getLogMessage() + ": " + resultMap.get(ModelService.ERROR_MESSAGE);
            if (isPost) {
                queryPostMessages.add(errorMsg);
            } else {
                queryPreMessages.add(errorMsg);
            }
        } else {
            JobLogLog execSuccess = new JobLogLog().initLogCode(resource, "QUERY_EXCEC_SUCCESS", queryCodeLogParameters, importManager.getLocale());
            if (isPost) {
                queryPostMessages.add(execSuccess.getLogMessage());
            } else {
                queryPreMessages.add(execSuccess.getLogMessage());
            }
        }
    }
    
    /**
     * ritorna queryPreMessages
     * @return
     */
    public List<String> getQueryPreMessages() {
        return queryPreMessages;
    }
    
    /***
     * ritorna queryPostMessages
     * @return
     */
    public List<String> getQueryPostMessages() {
        return queryPostMessages;
    }
}
