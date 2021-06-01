package com.mapsengineering.emplperf.update;

import java.util.List;
import java.util.ListIterator;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.workeffortext.services.E;

public class EmplPerfUpdateHelper {
	private Delegator delegator;
	private String keyId;
	private String logMsg;
	
	/**
	 * costruttore
	 * @param delegator
	 */
	public EmplPerfUpdateHelper(Delegator delegator) {
		this.delegator = delegator;
	}
	
	/**
	 * esegue la pullizia delle tabelle, nel caso WeRoot pulisce anche le tabelle collegate
	 * @param entityListToImport
	 * @throws GenericEntityException
	 */
    public void deleteAllInterfaceData(String entityListToImport) throws GenericEntityException {
    	List<String> entityListToImportList = StringUtil.split(entityListToImport, "|");
    	if (UtilValidate.isNotEmpty(entityListToImportList)) {
    		for (String entityListToImportItem : entityListToImportList) {
    			delegator.removeByAnd(entityListToImportItem);
    			if (ImportManagerConstants.WE_ROOT_INTERFACE.equals(entityListToImportItem)) {    				
    				delegator.removeByAnd(ImportManagerConstants.WE_INTERFACE);
    				delegator.removeByAnd(ImportManagerConstants.WE_ASSOC_INTERFACE);
    				delegator.removeByAnd(ImportManagerConstants.WE_MEASURE_INTERFACE);
    				delegator.removeByAnd(ImportManagerConstants.WE_PARTY_INTERFACE);
    				delegator.removeByAnd(ImportManagerConstants.WE_NOTE_INTERFACE);   				
    			}
    		}
    	}
    }
    
    /**
     * valorizza chiave e messaggio di log
     * @param gvUpdater
     * @param gv
     */
    public void setKeyAndMsg(EmplPerfValueUpdate gvUpdater, GenericValue gv) {
    	keyId = "";
    	logMsg = "";
    	
    	logMsg = "Init elaboration for party = '" + gv.getString(E.evalPartyId.name()) + "'";
    	
    	List<String> key = gvUpdater.getKey();
    	if (UtilValidate.isNotEmpty(key)) {
    		int s = key.size();
    		ListIterator<String> keyIter = key.listIterator();
    		while (keyIter.hasNext()) {
    			String field = keyIter.next();
    			int idx = keyIter.nextIndex();
    			
    			logMsg += " and " + field + " = '" + gv.getString(field) + "'";
    			keyId += gv.getString(field);
    			if (idx < s) {
    				keyId += ",";
    			}
    		}   		
    	}
    	
    	logMsg += gv;   	
    }
    
	/**
	 * @return the keyId
	 */
	public String getKeyId() {
		return keyId;
	}


	/**
	 * @return the logMsg
	 */
	public String getLogMsg() {
		return logMsg;
	}

}
