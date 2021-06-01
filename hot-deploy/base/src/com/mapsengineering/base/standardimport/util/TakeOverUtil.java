package com.mapsengineering.base.standardimport.util;

import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.standardimport.common.EntityNameStdImportEnum;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.util.JSONUtils;

/**
 * Utility for check Valid Entity
 *
 */
public class TakeOverUtil {

    /**
     * Check find One
     * @param primaryKeyName
     * @param primaryKeyValue
     * @param findOneEntityName
     * @param serviceEntityName
     * @param serviceExtKey
     * @param manager
     * @throws GeneralException
     */
	static public void checkValidEntity(String primaryKeyName, String primaryKeyValue, String findOneEntityName, String serviceEntityName, String serviceExtKey, ImportManager manager) throws GeneralException {
		String msg = "";
	
		GenericValue genericValue = manager.getDelegator().findOne(findOneEntityName, UtilMisc.toMap(primaryKeyName, primaryKeyValue), false);
		if (UtilValidate.isEmpty(genericValue)) {
	        msg = "The " + primaryKeyName + " " + primaryKeyValue + ImportManagerConstants.STR_IS_NOT_VALID;
	        throw new ImportException(serviceEntityName, serviceExtKey, msg);
		}
	}
	
	/**
     * Il metodo viene ulitilizzato per ritornare la chiave del genericValue in stringa 
     * @param map
     * @return
     */
	static public String toString(Map<String, ? extends Object> map) {
	    if(map instanceof GenericValue) {
	        GenericValue gv = (GenericValue) map;
            String entityName = gv.getEntityName();
            Map<String, Object> key = null;
            try {
                key = EntityNameStdImportEnum.getLogicalPrimaryKey(entityName, gv);
            } catch (GenericEntityException e) {
                e.printStackTrace();
            }
            return JSONUtils.toJson(key);
	    }
	        
	    return JSONUtils.toJson(map);
    }
    
    /**
     * Il metodo viene ulitilizzato per ritornare la chiave del genericValue in mappa 
     * @param map
     * @return
     */
	static public Map<String, ? extends Object> toMap(String s) {
        return JSONUtils.fromJson(s);
    }
}
