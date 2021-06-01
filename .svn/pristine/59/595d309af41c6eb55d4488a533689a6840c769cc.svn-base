package com.mapsengineering.base.standardimport.importothers;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;

public class ImportOthersManager {
	private TakeOverService takeOverService;
	
	private String entityListToImport;
	private Map<String, List<Map<String, Object>>> dataMap;
	
	private static final String MODULE = ImportOthersManager.class.getName();

	/**
	 * questa classe gestisce l importazione di interfacce diverse da quella che
	 * si sta gestendo
	 * @param takeOverService
	 */
	public ImportOthersManager(TakeOverService takeOverService) {
		this.takeOverService = takeOverService;
	}
	
	public void execute() {
        try {
            executeInTrx();
        } catch (Exception e) {
            handleException(e);
        }
	}
	
	/**
	 * 
	 * @throws Exception
	 */
    private void executeInTrx() throws Exception {
    	String msgStart = "Start executing implicit import";
    	takeOverService.addLogInfo(msgStart);
    	
        new TransactionRunner(MODULE, new TransactionItem() {
            @Override
            public void run() throws Exception {
                deleteAllData();
            }
        }).execute().rethrow();

        new TransactionRunner(MODULE, new TransactionItem() {
            @Override
            public void run() throws Exception {
            	writeAllData();
            }
        }).execute().rethrow();

        new TransactionRunner(MODULE, new TransactionItem() {
            @Override
            public void run() throws Exception {
                doImport();
            }
        }).execute().rethrow();
        
    	String msgEnd = "End executing implicit import";
    	takeOverService.addLogInfo(msgEnd);
    }
    
    /**
     * primo step, pulisco le tabelle di interfaccia
     * se sto importando la WeRoot, pulisco anche le altre tabelle WE
     * @throws GenericEntityException
     */
    private void deleteAllData() throws GenericEntityException {
    	List<String> entityListToImportList = StringUtil.split(entityListToImport, "|");
    	if (UtilValidate.isNotEmpty(entityListToImportList)) {
    		for (String entityListToImportItem : entityListToImportList) {
    			deleteData(entityListToImportItem);
    			if (ImportManagerConstants.WE_ROOT_INTERFACE.equals(entityListToImportItem)) {    				
    				deleteData(ImportManagerConstants.WE_INTERFACE);
    				deleteData(ImportManagerConstants.WE_ASSOC_INTERFACE);
    				deleteData(ImportManagerConstants.WE_MEASURE_INTERFACE);
    				deleteData(ImportManagerConstants.WE_PARTY_INTERFACE);
    				deleteData(ImportManagerConstants.WE_NOTE_INTERFACE);   				
    			}
    		}
    	}
    }
    
    /**
     * pulizia della singola tabella
     * @param entityName
     * @throws GenericEntityException
     */
    private void deleteData(String entityName) throws GenericEntityException {
    	takeOverService.getManager().getDelegator().removeByAnd(entityName);
    }
    
    /**
     * secondo step, scrivo i dati nelle tabelle da importare
     * @throws GenericEntityException
     */
    private void writeAllData() throws GenericEntityException {
    	if (UtilValidate.isNotEmpty(dataMap) && UtilValidate.isNotEmpty(dataMap.keySet())) {
    		for (String entityName : dataMap.keySet()) {
    			writeData(entityName, dataMap.get(entityName));
    		}
    	}
    }
    
    /**
     * scrittura dati nella singola tabella
     * @param entityName
     * @param dataList
     * @throws GenericEntityException
     */
    private void writeData(String entityName, List<Map<String, Object>> dataList) throws GenericEntityException {
    	if (UtilValidate.isNotEmpty(dataList)) {
    		String dataSource = getDataSource();
    		for (Map<String, Object> data : dataList) {   		
    			GenericValue gv = takeOverService.getManager().getDelegator().makeValue(entityName);
    			gv.put(E.dataSource.name(), dataSource);
    			gv.putAll(data);
    			takeOverService.getManager().getDelegator().create(gv);
    		}
    	}
    }
    
    /**
     * terzo step, eseguo import
     * @throws GeneralException
     */
    private void doImport() throws GeneralException {
    	String successMsg = "Implicit import successfully executed";
    	String errorMsg = "Error while executing implicit import";
    	
    	Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.userLogin.name(), takeOverService.getManager().getUserLogin());
        serviceMap.put("locale", takeOverService.getManager().getLocale());
        serviceMap.put(E.entityListToImport.name(), entityListToImport);
        serviceMap.put(E.defaultOrganizationPartyId.name(), takeOverService.getManager().getContext().get(E.defaultOrganizationPartyId.name()));
    	takeOverService.runSync(E.standardImport.name(), serviceMap, successMsg, errorMsg, true);
    }
    
    private void handleException(Exception e) {
    	String msg = "Error while executing implicit import : " + e.getMessage();
    	takeOverService.addLogError(msg);
    }
    
    /**
     * gestione del dataSource
     * @return
     */
    private String getDataSource() {
    	GenericValue externalValue = takeOverService.getExternalValue();
    	return UtilValidate.isNotEmpty(externalValue) && UtilValidate.isNotEmpty(externalValue.getString(E.dataSource.name()))
    			? externalValue.getString(E.dataSource.name()) : E.IMP_EXCEL.name();
    }

    /**
     * tabelle da importare separate da |
     * @param entityListToImport
     */
	public void setEntityListToImport(String entityListToImport) {
		this.entityListToImport = entityListToImport;
	}

	/**
	 * mappa contenente le entity da importare, e la lista di dati per ogni entity
	 * @param dataMap
	 */
	public void setDataMap(Map<String, List<Map<String, Object>>> dataMap) {
		this.dataMap = dataMap;
	}	
		
}
