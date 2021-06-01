package com.mapsengineering.base.bl.validation.validator;

import java.net.URL;
import java.util.List;

import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorResources;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilURL;
import org.ofbiz.base.util.cache.UtilCache;
/**
 * Manages Apache Validators
 * @author sandro
 *
 */
public class ApacheValidatorFactory {
	
	private ApacheValidatorFactory() {}

	/**
	 * Beans for validators caching
	 * @author sandro
	 *
	 */
	public static class CacheStoreValidator {
		public Validator validator = null;
		public ValidatorResources resources = null;
		public CacheStoreValidator(Validator val, ValidatorResources res) {
			this.validator = val;
			this.resources = res;
		}
	}
	
	/**
	 * Validator resources cache
	 */
	private static UtilCache<String, CacheStoreValidator> vrCache = UtilCache.createUtilCache("gplus.Validator", 0, 0);
	
	/**
	 * Get Validator instance by resourcePath and entity name 
	 * @param validatorResourcePath[] array of validator resources
	 * @param entityName
	 */
	public static synchronized CacheStoreValidator getInstance(List<String> validatorResourcePath, String entityName) {
		
		ValidatorResources vr = null;
		
		//Generates cache key for this pair: validator - entity
		String key = validatorResourcePath.hashCode() + "#" + entityName;
		CacheStoreValidator csv = null;
		
		if (vrCache.containsKey(key)) {
			csv = vrCache.get(key);
		} else {
			URL[] url = new URL[validatorResourcePath.size()];
			for (int i=0; i<validatorResourcePath.size(); i++) {
				url[i] = UtilURL.fromResource(validatorResourcePath.get(i));
			}
			try {
				//Note: url.inputStream is closed by ValidatorResources
				vr = new ValidatorResources(url);
				Validator validator = new Validator(vr);
				csv = new CacheStoreValidator(validator, vr);
				vrCache.put(key, csv);
			} catch (Exception e) {
				Debug.logError("Errore creazione Validator: " + e.getMessage(), ApacheValidatorFactory.class.getName());
				return null;	
			}
		}
		
		csv.validator.clear();
		//After clear
		csv.validator.setFormName(entityName);
		//This may seems necessary for correct Validator instantiation 
		csv.validator.setUseContextClassLoader(true);
		
		return csv;
	}

}
