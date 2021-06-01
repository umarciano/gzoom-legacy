package com.mapsengineering.base.standardimport;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.helper.ImportManagerUploadFileHelper;

/**
 * ImportManager ETL
 *
 */
public class ImportManagerFromETL {
	
	private ImportManagerUploadFileHelper importManagerUploadFileHelper;
	private Map<String, Object> context;
	private LocalDispatcher dispatcher;	
	private Map<String, Object> result = ServiceUtil.returnSuccess();
	private List<Map<String, Object>> resultList = FastList.newInstance();
	
	/**
	 * Constructor
	 * @param dctx
	 * @param context
	 */
	public ImportManagerFromETL(DispatchContext dctx, Map<String, Object> context) {
		this.context = context;
		this.dispatcher = dctx.getDispatcher();
		importManagerUploadFileHelper = new ImportManagerUploadFileHelper(dctx, context);
	}
	
	/**
	 * 	
	 * @param entityListToImport
	 * @param checkFromETL
	 * @return
	 * @throws GenericEntityException
	 * @throws GenericServiceException
	 */
	public  Map<String, Object> inmportAllETL(List<String> entityListToImport, String checkFromETL) throws GenericEntityException, GenericServiceException {
		List<GenericValue> listEntityETL = importManagerUploadFileHelper.checkFromAllETL(checkFromETL);
		if(UtilValidate.isNotEmpty(listEntityETL)){
			for(GenericValue value: listEntityETL){
				importETL(entityListToImport, (String)value.get("enumCode"));
			}
		}
		
		result.put("resultEtl", resultList);		
		return result;
	}

	/**
	 * 
	 * @param entityListToImport
	 * @param entityName
	 * @throws GenericEntityException
	 * @throws GenericServiceException
	 */
	protected void importETL(List<String> entityListToImport, String entityName) throws GenericEntityException, GenericServiceException {
		
		if(entityListToImport.contains(entityName)){
			Map<String, Object> resultEtl = runLoadETL(entityName);
			resultList.add(resultEtl);
		}
		
	}
	
	/**
	 * Caricamento tramite ETL
	 * @param entityName
	 * @return
	 * @throws GenericServiceException
	 */
	protected Map<String, Object> runLoadETL(String entityName) throws GenericServiceException {		
		
		/** Alla fine chiamo importazione standart */
		Map<String, Object> serviceMap = FastMap.newInstance();        
        serviceMap.put("userLogin", context.get("userLogin"));
        serviceMap.put("entityName", entityName);
        
        String nameService = entityName + "LoadETL";
        
		return dispatcher.runSync(nameService, serviceMap);
		
	}
}
