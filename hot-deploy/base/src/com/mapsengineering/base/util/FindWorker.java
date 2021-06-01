package com.mapsengineering.base.util;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Find Service
 *
 */
public class FindWorker {
	
	private FindWorker() {}
	
    public static Map<String, Object> performFind(Map<String, Object> parameters, GenericDispatcher dispatcher, TimeZone timeZone, Locale locale) {
        Map<String, Object> result = new FastMap<String, Object>();
        try {
            String serviceName = "performFind";
            ModelService modelService = dispatcher.getDispatchContext().getModelService(serviceName);

            Map<String, Object> toMap = new FastMap<String, Object>();
            toMap.put("inputFields", parameters);

            for (ModelParam modelParam : modelService.getInModelParamList()) {
                if (parameters.containsKey(modelParam.name)) {
                    Object value = parameters.get(modelParam.name);

                    if (UtilValidate.isNotEmpty(modelParam.type)) {
                        try {
                            if (value != null && value instanceof String && modelParam.type.equals(List.class.getName()) && value.toString().indexOf("|") != -1) {
                                value = StringUtil.replaceString(value.toString(), "|", ", ");
                            }
                            value = ObjectType.simpleTypeConvert(value, modelParam.type, null, timeZone, locale, true);
                        } catch (GeneralException e) {
                            value = null;
                        }
                    }

                    toMap.put(modelParam.name, value);
                }
            }
            
            String orderBy = UtilGenerics.cast(UtilValidate.isNotEmpty(parameters.get("sortField")) ? parameters.get("sortField") : parameters.get("orderBy"));
            if (UtilValidate.isNotEmpty(orderBy)) {
                List<String> orderByList = StringUtil.split(orderBy,"|");
                if (UtilValidate.isNotEmpty(orderByList)) {
                	String entityName = UtilGenerics.cast(toMap.get("entityName"));
                	ModelEntity modelEntity = dispatcher.getDelegator().getModelEntity(entityName);
                	
                	boolean putOrderByList = true;
                	for (String fieldName : orderByList) {
                		if (!modelEntity.isField(fieldName)) {
                			putOrderByList = false;
                			break;
                		}
                	}
                	if (putOrderByList) {
                		toMap.put("orderBy", orderBy);
                	}
                }
            }
            
            toMap.put("locale", locale);
            toMap.put("timeZone", timeZone);
            Map<String, Object> resultService = dispatcher.runSync(serviceName, toMap);
            if (ServiceUtil.isError(resultService)) {
                Map<String, Object> errorMap = ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "", locale));
                result.putAll(errorMap);
                //TODO Aggiungere messaggio di errore e anche lo stack-trace
            } else {
                for (String paramName : modelService.getOutParamNames()) {
                    result.put(paramName, resultService.get(paramName));
                }
            }


        } catch (GenericServiceException e) {
            Map<String, Object> errorMap = ServiceUtil.returnError(UtilProperties.getMessage("BaseErrorLabels", "", locale));
            result.putAll(errorMap);
            //TODO Aggiungere messaggio di errore e anche lo stack-trace
        }

        return result;
    }
    
    public static List<Map<String, Object>> paginate(List<Map<String, Object>> listIn, int viewIndex, int viewSize) {
        int fromIndex = viewIndex * viewSize;
        int toIndex = fromIndex + viewSize;
        if (toIndex > listIn.size())
            toIndex = listIn.size();
        return listIn.subList(fromIndex, toIndex);
    }

    public static void paginate(Map<String, Object> context) {
        paginate(context, context);
    }

    public static void paginate(Map<String, Object> context, Map<String, Object> result) {
        paginate(context, result, "list", "listSize");
    }

    @SuppressWarnings("unchecked")
    public static void paginate(Map<String, Object> context, Map<String, Object> result, String listName, String listSizeName) {
        Integer viewSize = (Integer)context.get("viewSize");
        if (viewSize != null) {
            Integer viewIndex = (Integer)context.get("viewIndex");
            if (viewIndex == null) {
                viewIndex = 0;
                context.put("viewIndex", viewIndex);
            }
            List<Map<String, Object>> list = (List<Map<String, Object>>)result.get(listName);
            if (list == null)
                list = (List<Map<String, Object>>)context.get(listName);
            if (list != null) {
                result.put(listSizeName, list.size());
                list = paginate(list, viewIndex, viewSize);
                result.put(listName, list);
            }
        }
    }
    
    /**
     * Utilizzato per filtrare i dati dalle date che vengono inserite in ingresso
     * @param <T>
     * @param datedValues
     * @param fromDate
     * @param thruDate
     * @param fromDateName
     * @param thruDateName
     * @return
     */
    public static <T extends GenericEntity> List<T> filterByDate(List<T> datedValues, Timestamp fromDate, Timestamp thruDate, String fromDateName, String thruDateName) {
        if (datedValues == null) return null;
        if (fromDate == null) return datedValues;
        
        if (fromDateName == null) fromDateName = "fromDate";
        if (thruDateName == null) thruDateName = "thruDate";

        List<T> result = FastList.newInstance();
        Iterator<T> iter = datedValues.iterator();

        
        ModelField fromDateField = null;
        ModelField thruDateField = null;

        if (iter.hasNext()) {
            T datedValue = iter.next();

            fromDateField = datedValue.getModelEntity().getField(fromDateName);
            if (fromDateField == null) throw new IllegalArgumentException("\"" + fromDateName + "\" is not a field of " + datedValue.getEntityName());
            thruDateField = datedValue.getModelEntity().getField(thruDateName);
            if (thruDateField == null) throw new IllegalArgumentException("\"" + thruDateName + "\" is not a field of " + datedValue.getEntityName());

            Timestamp fromDateValues = (java.sql.Timestamp) datedValue.dangerousGetNoCheckButFast(fromDateField);
            Timestamp thruDateValues = (java.sql.Timestamp) datedValue.dangerousGetNoCheckButFast(thruDateField);

            if ((thruDateValues == null || thruDate == null || thruDateValues.after(thruDate) || thruDateValues.equals(thruDate)) && (fromDateValues == null || fromDateValues.before(fromDate) || fromDateValues.equals(fromDate))) {
                result.add(datedValue);
            }// else not active at moment
        }
        while (iter.hasNext()) {
            T datedValue = iter.next();
            Timestamp fromDateValues = (Timestamp) datedValue.dangerousGetNoCheckButFast(fromDateField);
            Timestamp thruDateValues = (Timestamp) datedValue.dangerousGetNoCheckButFast(thruDateField);

            if ((thruDateValues == null || thruDate == null || thruDateValues.after(thruDate) || thruDateValues.equals(thruDate)) && (fromDateValues == null || fromDateValues.before(fromDate) || fromDateValues.equals(fromDate))) {
                result.add(datedValue);
            }// else not active at moment
        }
        

        return result;
    }
}
