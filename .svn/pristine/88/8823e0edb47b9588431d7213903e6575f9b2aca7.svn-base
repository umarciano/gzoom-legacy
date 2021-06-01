package com.mapsengineering.base.events;

import static org.ofbiz.base.util.UtilGenerics.checkList;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.event.EventHandlerException;
import org.ofbiz.webapp.event.FileUploadProgressListener;

import com.mapsengineering.base.util.FindWorker;

public final class CrudEvents {

    public static final String MODULE = CrudEvents.class.getName();

    public static final String OP_CREATE = "CREATE";
    public static final String OP_UPDATE = "UPDATE";
    public static final String OP_DELETE = "DELETE";

    private static final String UPLOADED_FILE = "uploadedFile";
    private static final String STRING = "String";
    private static final String ERROR_MESSAGE_LIST = "errorMessageList";
    private static final String MULTI_PART_MAP = "multiPartMap";
    private static final String UPLOADED_ITEMS = "uploadedItems";
    public static final String OPERATION = "operation";
    private static final String AUXILIARY_PARAMETERS = "auxiliaryParameters";
    private static final String MESSAGE_CONTEXT = "messageContext";
    public static final String PARAMETERS = "parameters";
    private static final String CRUD_SERVICE_EPILOG = "crudServiceEpilog";
    private static final String OPEN_NEW_TRANSACTION = "openNewTransaction";
    public static final String ENTITY_NAME = "entityName";
    private static final String SUCCESS = "success";
    private static final String ERROR = "error";
    private static final String DELEGATOR = "delegator";

    private CrudEvents() {
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     */
    public static String elaborateMultiParameters(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator)request.getAttribute(DELEGATOR);

        String all = request.getParameter("all");
        if ("Y".equals(all)) {
            return elaborateForDeleteAll(request, response);
        } else {
            try {
                checkMultiPartMap(request, response);
            } catch (Exception e) {
                return ERROR;
            }

            int rowCount = UtilHttp.getMultiFormRowCount(request);
            if (rowCount > 0) {
                String entityName = "";
                boolean transactionCreated = false;
                for (int i = 0; i < rowCount; i++) {
                    String curSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;

                    if (UtilValidate.isEmpty(entityName)) {
                        entityName = request.getParameter(ENTITY_NAME + curSuffix);
                    }

                    if (UtilValidate.isNotEmpty(entityName)) {

                        if (UtilValidate.isNotEmpty(request.getParameter(CRUD_SERVICE_EPILOG + curSuffix)) && !transactionCreated) {
                        	if (!"N".equals(request.getParameter(OPEN_NEW_TRANSACTION + curSuffix))){
                        		try {
                                    //Per il timeout vedi crudService
                                    TransactionUtil.begin(28800);
                                    transactionCreated = true;
                                } catch (Exception e) {
                                }
                        	}                         	
                        }

                        ModelEntity modelEntity = delegator.getModelEntity(entityName);
                        if (UtilValidate.isNotEmpty(modelEntity)) {
                            Map<String, Object> inputFieldsMap = new FastMap<String, Object>();

                            for (Object key : request.getParameterMap().keySet()) {
                                if (key instanceof String) {
                                    String keyString = UtilGenerics.cast(key);

                                    if (keyString.endsWith(curSuffix)) {
                                        inputFieldsMap.put(keyString.substring(0, keyString.indexOf(curSuffix)), request.getParameter(keyString));
                                    }
                                }
                            }

                            if (UtilValidate.isNotEmpty(inputFieldsMap)) {
                                if (UtilValidate.isNotEmpty(request.getParameter("_AUTOMATIC_PK_" + curSuffix))) {
                                    inputFieldsMap.put("_AUTOMATIC_PK_", request.getParameter("_AUTOMATIC_PK_" + curSuffix));
                                }
                                if (UtilValidate.isNotEmpty(request.getParameter("completeRemove" + curSuffix))) {
                                    inputFieldsMap.put("completeRemove", request.getParameter("completeRemove" + curSuffix));
                                }
                                request.setAttribute(PARAMETERS + curSuffix, inputFieldsMap);
                            }
                        }
                    }
                }
            }

            request.setAttribute(MESSAGE_CONTEXT, request.getParameter(MESSAGE_CONTEXT));
        }

        if (!"Y".equals(request.getParameter("ignoreAuxiliaryParameters"))) {
            Collection<String> c = new FastList<String>();
            c.add("fromDelete");

            request.setAttribute(AUXILIARY_PARAMETERS, StringUtil.mapToStr(UtilHttp.getParameterMap(request, UtilMisc.toSet(c))));
        }

        return SUCCESS;
    }

    private static String elaborateForDeleteAll(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator)request.getAttribute(DELEGATOR);

        try {
            checkMultiPartMap(request, response);
        } catch (Exception e) {
            return ERROR;
        }

        request.setAttribute(MESSAGE_CONTEXT, request.getParameter(MESSAGE_CONTEXT));

        Map<String, Object> parameters = UtilHttp.getParameterMap(request);

        Map<String, Object> searchResult = FindWorker.performFind(parameters, (GenericDispatcher)request.getAttribute("dispatcher"), UtilHttp.getTimeZone(request), UtilHttp.getLocale(request));
        if (ServiceUtil.isError(searchResult)) {
            request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(searchResult));
            return ERROR;
        }

        boolean searchOk = false;
        if (UtilValidate.isNotEmpty(searchResult)) {
            EntityListIterator listIt = (EntityListIterator)searchResult.get("listIt");
            if (UtilValidate.isNotEmpty(listIt)) {
                try {
                    int i = 0;
                    GenericValue value = null;
                    while ((value = listIt.next()) != null) {
                        if (!searchOk) {
                            searchOk = true;
                        }
                        Map<String, Object> keys = value.getPrimaryKey().getAllFields();
                        if (UtilValidate.isNotEmpty(keys)) {
                            Map<String, Object> inputFieldsMap = new FastMap<String, Object>();
                            for (Map.Entry<String, Object>  keyEntry : keys.entrySet()) {
                            	inputFieldsMap.put(keyEntry.getKey(), keyEntry.getValue());
                            }
                            request.setAttribute(PARAMETERS + UtilHttp.MULTI_ROW_DELIMITER + i, inputFieldsMap);
                            request.setAttribute(OPERATION + UtilHttp.MULTI_ROW_DELIMITER + i, parameters.get(OPERATION));

                            i++;
                        }
                    }
                } finally {
                    try {
                        listIt.close();
                    } catch (GenericEntityException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
        if (!searchOk) {
            String entityName = (String)parameters.get(ENTITY_NAME);
            if (UtilValidate.isNotEmpty(entityName)) {
                ModelEntity modelEntity = delegator.getModelEntity(entityName);

                String firstPkFieldName = modelEntity.getFirstPkFieldName();
                String firstPkFieldValue = (String)parameters.get(firstPkFieldName);
                if (UtilValidate.isNotEmpty(firstPkFieldValue)) {

                    Map<String, Object> parametersMap = new FastMap<String, Object>();
                    for (String pkFieldName : modelEntity.getPkFieldNames()) {
                        String pkFieldValue = (String)parameters.get(pkFieldName);

                        Map<String, Object> keyMap = null;
                        try {
                            if (pkFieldValue.indexOf('[') != 0) {
                                pkFieldValue = '[' + pkFieldValue;
                            }
                            if (pkFieldValue.indexOf(']') != pkFieldValue.length() - 1) {
                                pkFieldValue += ']';
                            }
                            List<String> fieldValues = StringUtil.toList(pkFieldValue, "\\|");
                            for (int i = 0; i < fieldValues.size(); i++) {
                                keyMap = UtilGenerics.checkMap(parametersMap.get(PARAMETERS + UtilHttp.MULTI_ROW_DELIMITER + i));
                                if (UtilValidate.isEmpty(keyMap)) {
                                    keyMap = new FastMap<String, Object>();
                                    parametersMap.put(PARAMETERS + UtilHttp.MULTI_ROW_DELIMITER + i, keyMap);
                                }
                                keyMap.put(pkFieldName, fieldValues.get(i));
                            }
                        } catch (IllegalArgumentException e) {
                            keyMap = new FastMap<String, Object>();
                            keyMap.put(pkFieldName, pkFieldValue);

                            parametersMap.put(PARAMETERS + UtilHttp.MULTI_ROW_DELIMITER + "0", keyMap);
                        }
                    }

                    if (UtilValidate.isNotEmpty(parametersMap)) {
                        int i = 0;
                        for (Map.Entry<String, Object>  mapEntry : parametersMap.entrySet()) {
                        	request.setAttribute(mapEntry.getKey(), mapEntry.getValue());
                            request.setAttribute(OPERATION + UtilHttp.MULTI_ROW_DELIMITER + i, parameters.get(OPERATION));
                            i++;
                		}
                    }
                }
            }
        }

        return SUCCESS;
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     */
    public static String elaborateParameters(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator)request.getAttribute(DELEGATOR);

        try {
            checkMultiPartMap(request, response);
        } catch (Exception e) {
            return ERROR;
        }
        Map<String, Object> parameters = UtilHttp.getParameterMap(request);

        request.setAttribute(PARAMETERS, /*StringUtil.mapToStr(parameters)*/parameters);
        request.setAttribute(MESSAGE_CONTEXT, request.getParameter(MESSAGE_CONTEXT));

        
        //Eventuale Transazione aperta in elaborateParameters
        if (UtilValidate.isNotEmpty(request.getParameter(CRUD_SERVICE_EPILOG))) {
            if (!"N".equals(request.getParameter(OPEN_NEW_TRANSACTION))){
                try {
                    //Per il timeout vedi crudService
                    TransactionUtil.begin(28800);
                } catch (Exception e) {
                }
            }
        }

        if (!"Y".equals(request.getParameter("ignoreAuxiliaryParameters"))) {
            Collection<String> c = new FastList<String>();
            c.add("insertMode");
            c.add(OPERATION);
            c.add("fromDelete");
            c.add("justRegisters");
            String entityName = (String)parameters.get(ENTITY_NAME);
            if (UtilValidate.isNotEmpty(entityName)) {
                ModelEntity modelEntity = delegator.getModelEntity(entityName);
                if (UtilValidate.isNotEmpty(modelEntity)) {
                    c.addAll(modelEntity.getPkFieldNames());
                }
            }

            request.setAttribute(AUXILIARY_PARAMETERS, StringUtil.mapToStr(UtilHttp.getParameterMap(request, UtilMisc.toSet(c))));
        }

        if (UtilValidate.isNotEmpty(parameters.get("rootValues"))) {
            return "tree";
        }
        return SUCCESS;
    }

    private static void checkMultiPartMap(HttpServletRequest request, HttpServletResponse response) throws EventHandlerException {
        HttpSession session = request.getSession();

        // get the http upload configuration
        String maxSizeStr = UtilProperties.getPropertyValue("general.properties", "http.upload.max.size", "-1");
        long maxUploadSize = -1;
        try {
            maxUploadSize = Long.parseLong(maxSizeStr);
        } catch (NumberFormatException e) {
            Debug.logError(e, "Unable to obtain the max upload size from general.properties; using default -1", MODULE);
            maxUploadSize = -1;
        }
        // get the http size threshold configuration - files bigger than this will be
        // temporarly stored on disk during upload
        String sizeThresholdStr = UtilProperties.getPropertyValue("general.properties", "http.upload.max.sizethreshold", "10240");
        // 10K
        int sizeThreshold = 10240;
        try {
            sizeThreshold = Integer.parseInt(sizeThresholdStr);
        } catch (NumberFormatException e) {
            Debug.logError(e, "Unable to obtain the threshold size from general.properties; using default 10K", MODULE);
            sizeThreshold = -1;
        }
        // directory used to temporarily store files that are larger than the configured size threshold
        String tmpUploadRepository = UtilProperties.getPropertyValue("general.properties", "http.upload.tmprepository", "runtime/tmp");
        String encoding = request.getCharacterEncoding();
        // check for multipart content types which may have uploaded items
        boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
        Map<String, Object> multiPartMap = FastMap.newInstance();
        if (isMultiPart) {
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(sizeThreshold, new File(tmpUploadRepository)));

            // create the progress listener and add it to the session
            FileUploadProgressListener listener = new FileUploadProgressListener();
            upload.setProgressListener(listener);
            session.setAttribute("uploadProgressListener", listener);

            if (encoding != null) {
                upload.setHeaderEncoding(encoding);
            }
            upload.setSizeMax(maxUploadSize);

            List<FileItem> uploadedItems = null;
            try {
                uploadedItems = UtilGenerics.<FileItem> checkList(upload.parseRequest(request));
            } catch (FileUploadException e) {
                throw new EventHandlerException("Problems reading uploaded data", e);
            }
            if (uploadedItems != null) {
                request.setAttribute(UPLOADED_ITEMS, uploadedItems);
                for (FileItem item : uploadedItems) {
                    String fieldName = item.getFieldName();
                    if (item.isFormField() || item.getName() == null) {
                        if (multiPartMap.containsKey(fieldName)) {
                            Object mapValue = multiPartMap.get(fieldName);
                            if (mapValue instanceof List) {
                                checkList(mapValue, Object.class).add(item.getString());
                            } else if (mapValue instanceof String) {
                                List<String> newList = FastList.newInstance();
                                newList.add((String)mapValue);
                                newList.add(item.getString());
                                multiPartMap.put(fieldName, newList);
                            } else {
                                Debug.logWarning("Form field found [" + fieldName + "] which was not handled!", MODULE);
                            }
                        } else {
                            if (encoding != null) {
                                try {
                                    multiPartMap.put(fieldName, item.getString(encoding));
                                } catch (java.io.UnsupportedEncodingException uee) {
                                    Debug.logError(uee, "Unsupported Encoding, using deafault", MODULE);
                                    multiPartMap.put(fieldName, item.getString());
                                }
                            } else {
                                multiPartMap.put(fieldName, item.getString());
                            }
                        }
                    } else {
                        String fileName = item.getName();
                        if (fileName.indexOf('\\') > -1 || fileName.indexOf('/') > -1) {
                            // get just the file name IE and other browsers also pass in the local path
                            int lastIndex = fileName.lastIndexOf('\\');
                            if (lastIndex == -1) {
                                lastIndex = fileName.lastIndexOf('/');
                            }
                            if (lastIndex > -1) {
                                fileName = fileName.substring(lastIndex + 1);
                            }
                        }
                        multiPartMap.put(fieldName, ByteBuffer.wrap(item.get()));
                        multiPartMap.put("_" + fieldName + "_size", Long.valueOf(item.getSize()));
                        multiPartMap.put("_" + fieldName + "_fileName", fileName);
                        multiPartMap.put("_" + fieldName + "_contentType", item.getContentType());
                    }
                }
            }
        }

        // store the multi-part map as an attribute so we can access the parameters
        request.setAttribute(MULTI_PART_MAP, multiPartMap);
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     */
    public static String elaborateMultiReturnParameters(HttpServletRequest request, HttpServletResponse response) {
        String res = SUCCESS;

        Map<String, Object> parameters = UtilHttp.getCombinedMap(request, UtilMisc.toSet("resultList"));
        String crudServiceEpilogKey = null;
        if (UtilValidate.isNotEmpty(parameters)) {

            //Elaborazione post servizio
            Set<String> keySet = parameters.keySet();
            Iterator<String> keyIt = keySet.iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                if (key.contains(CRUD_SERVICE_EPILOG)) {
                    crudServiceEpilogKey = key;
                    break;
                }
            }
            if (UtilValidate.isNotEmpty(crudServiceEpilogKey)) {
                String crudServiceEpilog = (String)parameters.get(crudServiceEpilogKey);
                GenericDispatcher dispatcher = (GenericDispatcher)request.getAttribute("dispatcher");
                Iterator<String> paramIt = parameters.keySet().iterator();
                Map<String, Object> serviceMap = new HashMap<String, Object>();
                while (paramIt.hasNext()) {
                    String paramKey = paramIt.next();
                    if (paramKey.contains(UtilHttp.MULTI_ROW_DELIMITER)) {
                        String cleanParam = paramKey.substring(0, paramKey.indexOf(UtilHttp.MULTI_ROW_DELIMITER));
                        serviceMap.put(cleanParam, parameters.get(paramKey));
                    }
                }
                DispatchContext dctx = dispatcher.getDispatchContext();
                try {
                    ModelService epilogModel = dctx.getModelService(crudServiceEpilog);
                    serviceMap = epilogModel.makeValid(serviceMap, ModelService.IN_PARAM);
                    Map<String, Object> serviceParam = epilogModel.makeValid(parameters, ModelService.IN_PARAM);
                    serviceMap.putAll(serviceParam);
                    Map<String, Object> epilogRetMap = dispatcher.runSync(crudServiceEpilog, serviceMap);
                    if (UtilValidate.isNotEmpty(epilogRetMap.get(ERROR_MESSAGE_LIST))) {
                        request.setAttribute("_ERROR_MESSAGE_LIST_", epilogRetMap.get(ERROR_MESSAGE_LIST));
                        res = ERROR;
                    }
                } catch (Exception e) {
                }
            }

            List<Map<String, Object>> resultList = UtilGenerics.checkList(parameters.get("resultList"));

            if (UtilValidate.isNotEmpty(resultList)) {
                List<String> idList = new FastList<String>();
                for (Map<String, Object> m : resultList) {
                    if (m.containsKey("id")) {
                        Map<String, Object> idMap = UtilGenerics.checkMap(m.get("id"));
                        for (String key : idMap.keySet()) {
                            if (!ObjectType.instanceOf(idMap.get(key), "java.lang.String")) {
                                try {
                                    idMap.put(key, ObjectType.simpleTypeConvert(idMap.get(key), STRING, null, null));
                                } catch (GeneralException e) {
                                    idMap.put(key, null);
                                }

                            }
                        }

                        idList.add(StringUtil.mapToStr(UtilGenerics.checkMap(idMap), false));
                    }
                }

                if (UtilValidate.isNotEmpty(idList)) {
                    String auxiliaryParameters = (String)parameters.get(AUXILIARY_PARAMETERS);
                    Map<String, String> auxiliaryParametersMap = StringUtil.strToMap(auxiliaryParameters);
                    if (UtilValidate.isEmpty(auxiliaryParameters)) {
                        auxiliaryParametersMap = new FastMap<String, String>();
                    }
                    auxiliaryParametersMap.put("id", StringUtil.join(idList, ","));

                    auxiliaryParameters = StringUtil.mapToStr(auxiliaryParametersMap);
                    request.setAttribute(AUXILIARY_PARAMETERS, auxiliaryParameters/*UtilHttp.encodeBlanks(auxiliaryParameters)*/);
                }
            }
        }

        res = new CrudEvents().manageUpload(request, res);

        //Eventuale Transazione aperta in elaborateMultiParameters
        try {
            if (UtilValidate.isNotEmpty(crudServiceEpilogKey)) {
                if (ERROR.equals(res)) {
                    TransactionUtil.rollback();
                } else {
                    TransactionUtil.commit();
                }
            }
        } catch (Exception e) {
        }

        return res;
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     */
    public static String elaborateReturnParameters(HttpServletRequest request, HttpServletResponse response) {
        String res = SUCCESS;

        Map<String, Object> parameters = UtilHttp.getCombinedMap(request, UtilMisc.toSet("resultList"));
        String id = null;
        //Sandro - aggiunto ritorno di retValues
        Map<String, String> retValues = CrudEvents.getRetValues(parameters);

        //Elaborazione post servizio
        String crudServiceEpilog = (String)parameters.get(CRUD_SERVICE_EPILOG);
        if (UtilValidate.isNotEmpty(crudServiceEpilog)) {
            GenericDispatcher dispatcher = (GenericDispatcher)request.getAttribute("dispatcher");
            DispatchContext dctx = dispatcher.getDispatchContext();
            try {
                ModelService epilogModel = dctx.getModelService(crudServiceEpilog);
                Map<String, Object> serviceMap = epilogModel.makeValid(parameters, ModelService.IN_PARAM);
                Map<String, Object> epilogRetMap = dispatcher.runSync(crudServiceEpilog, serviceMap);
                if (UtilValidate.isNotEmpty(epilogRetMap.get(ERROR_MESSAGE_LIST))) {
                    request.setAttribute("_ERROR_MESSAGE_LIST_", epilogRetMap.get(ERROR_MESSAGE_LIST));
                    res = ERROR;
                }
            } catch (Exception e) {
            }
        }

        if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("id")) {
            Map<String, Object> idMap = UtilGenerics.checkMap(parameters.get("id"));
            if (UtilValidate.isNotEmpty(idMap)) {           	
                for (Map.Entry<String, Object>  idMapEntry : idMap.entrySet()) {
                    if (!ObjectType.instanceOf(idMapEntry.getValue(), STRING)) {
                        try {
                            if (!ObjectType.instanceOf(idMapEntry.getValue(), "java.sql.Timestamp")) {
                                idMap.put(idMapEntry.getKey(), ObjectType.simpleTypeConvert(idMapEntry.getValue(), STRING, UtilDateTime.getDateTimeFormat(UtilHttp.getLocale(request)), null));
                            } else {
                                idMap.put(idMapEntry.getKey(), idMapEntry.getValue().toString());
                            }
                        } catch (GeneralException e) {

                        }
                    }
                }
            }
            request.setAttribute("id", idMap);

            id = StringUtil.mapToStr(idMap, false);
        } else {
            String entityName = (String)parameters.get(ENTITY_NAME);
            Delegator delegator = (Delegator)request.getAttribute(DELEGATOR);

            ModelEntity modelEntity = delegator.getModelEntity(entityName);
            List<String> pkFieldsName = modelEntity.getPkFieldNames();
            Map<String, Object> pkMap = new FastMap<String, Object>();
            for (String pkFieldName : pkFieldsName) {
                try {
                    pkMap.put(pkFieldName, ObjectType.simpleTypeConvert(parameters.get(pkFieldName), STRING, UtilDateTime.getDateTimeFormat(UtilHttp.getLocale(request)), null));
                } catch (GeneralException e) {
                    pkMap.put(pkFieldName, parameters.get(pkFieldName));
                }
            }
            request.setAttribute("id", pkMap);

            if (UtilValidate.isNotEmpty(pkMap)) {
                id = StringUtil.mapToStr(pkMap);
            }
        }
        if (UtilValidate.isNotEmpty(id) && !"Y".equals(request.getParameter("ignoreAuxiliaryParameters"))) {
            String auxiliaryParameters = (String)parameters.get(AUXILIARY_PARAMETERS);
            Map<String, String> auxiliaryParametersMap = StringUtil.strToMap(auxiliaryParameters);
            if (UtilValidate.isEmpty(auxiliaryParameters)) {
                auxiliaryParametersMap = new FastMap<String, String>();
            }

            auxiliaryParametersMap.put("id", id);

            //Sandro - aggiunto retValues
            if (UtilValidate.isNotEmpty(retValues)) {
                auxiliaryParametersMap.putAll(retValues);
            }

            auxiliaryParameters = StringUtil.mapToStr(auxiliaryParametersMap);

            request.setAttribute(AUXILIARY_PARAMETERS, UtilHttp.encodeBlanks(auxiliaryParameters));
        }

        res = new CrudEvents().manageUpload(request, res);

        //Eventuale Transazione aperta in elaborateParameters
        try {
            if (UtilValidate.isNotEmpty(crudServiceEpilog)) {
                if (ERROR.equals(res)) {
                    TransactionUtil.rollback();
                } else {
                    TransactionUtil.commit();
                }
            }
        } catch (Exception e) {
        }

        return res;
    }

    /**
     * Sandro: Controllo presenza e ritorno retValues 
     * @param parameters
     * @return Map with values or null if parameters not contains retValues 
     */
    @SuppressWarnings("unchecked")
    private static Map<String, String> getRetValues(Map<String, Object> parameters) {
        //retValues é definita come lista in crudServices
        Map<String, String> res = null;
        if ((parameters != null) && parameters.containsKey("retValues")) {
            if (parameters.get("retValues") instanceof List) {
                res = FastMap.newInstance();
                List retValues = (List)parameters.get("retValues");
                //Ciclo su tutti i valori ritornati
                for (Object item : retValues) {
                    //Check che il valore sia una map
                    if (item instanceof Map) {
                        Iterator it = ((Map)item).entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry entry = (Map.Entry)it.next();
                            //Check che value sia una stringa
                            if (entry.getValue() instanceof String) {
                                res.put((String)entry.getKey(), (String)entry.getValue());
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    private String manageUpload(HttpServletRequest request, String oldRes) {
        String res = oldRes;
        if (UtilValidate.isNotEmpty(request.getAttribute(UPLOADED_ITEMS))) {
            request.removeAttribute(UPLOADED_ITEMS);
        }
        if (UtilValidate.isNotEmpty(request.getAttribute(MULTI_PART_MAP))) {
            request.removeAttribute(MULTI_PART_MAP);
            res = "upload";
        }
        if (UtilValidate.isNotEmpty(request.getAttribute(PARAMETERS))) {
            Map<String, Object> parametersInAttribute = UtilGenerics.checkMap(request.getAttribute(PARAMETERS));
            if (parametersInAttribute.containsKey(UPLOADED_FILE)) {
                parametersInAttribute.remove(UPLOADED_FILE);
            }
        }
        return res;
    }
}
