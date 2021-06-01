package com.mapsengineering.base.bl.crud;

import java.util.Iterator;
import java.util.Map;

import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.util.MessageUtil;

public class UploadAttachmentHandler extends AbstractCrudHandler {

	private static final String CONTENT_FIELD_NAME_FIELD = "contentFieldName";
	private static final String RELATION_NAME_FIELD = "relationName";
	
    protected boolean doExecution() {
    	//Eseguo il caricamento del documento allegato
    	DispatchContext dctx = UtilGenerics.cast(context.get("dispatchContext"));
    	String uploadServiceName = UtilGenerics.cast(context.get("uploadServiceName"));
    	Map<String, Object> uploadServiceParameters = UtilGenerics.checkMap(context.get("uploadServiceInParametersMap"));
    	
    	Map<String, Object> uploadReturnMap = null;
    	try {
    		uploadReturnMap = uploadAttachment(dctx, uploadServiceName, uploadServiceParameters);
    	} catch (Exception e) {
    		returnMap.putAll(MessageUtil.buildErrorMap("UploadAttachmentError", e, locale, UtilMisc.toList(entityName)));
    		return false;
    	}
    	
    	if (UtilValidate.isNotEmpty(uploadReturnMap) && !ServiceUtil.isError(uploadReturnMap)) {   		
    			for (Map.Entry<String, Object>  uploadReturnMapEntry : uploadReturnMap.entrySet()) {
    			parameters.put(uploadReturnMapEntry.getKey(), uploadReturnMapEntry.getValue());
    		}
    	}
    	
    	if (UtilValidate.isNotEmpty(this.parameters.get(CONTENT_FIELD_NAME_FIELD))) {
    		this.parameters.put(CONTENT_FIELD_NAME_FIELD, parameters.get("contentId"));
    	} else {
    		ModelEntity model = delegator.getModelEntity(this.entityName);
    		ModelRelation relation = model.getRelation("" + (UtilValidate.isNotEmpty(this.parameters.get(RELATION_NAME_FIELD)) ? this.parameters.get(RELATION_NAME_FIELD) : "") + "Content");
    		if (UtilValidate.isNotEmpty(relation)) {
    			Iterator<ModelKeyMap> it = relation.getKeyMapsIterator();
    			while (it.hasNext()) {
    				ModelKeyMap keyMap = it.next();
    				
    				this.parameters.put(keyMap.getFieldName(), this.parameters.get(keyMap.getRelFieldName()));
    			}
    		}
    	}

        return true;
    }
    
    private Map<String, Object> uploadAttachment(DispatchContext dctx, String serviceName, Map<String, Object> parameters) throws GenericServiceException {
    	Map<String, Object> res = null;
    	
		Map<String, Object> returnMap = dctx.getDispatcher().runSync(serviceName, parameters);
		if (ServiceUtil.isError(returnMap)) {
			throw new GenericServiceException(ServiceUtil.getErrorMessage(returnMap));
		} else {
			res = returnMap;
		}
    	
    	return res;
    }

}
