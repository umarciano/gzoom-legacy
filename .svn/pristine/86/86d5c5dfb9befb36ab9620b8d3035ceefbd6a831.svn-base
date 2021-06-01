package com.mapsengineering.base.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.lang.JSON;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

public class CommonUtil {

    private CommonUtil() {
        // empty
    }

    public static final String id(Object obj, boolean typed) {
        String str = "@" + Integer.toString(System.identityHashCode(obj), 16);
        if (typed && obj != null) {
            str = obj.getClass().getName() + str;
        }
        return str;
    }

    public static final String id(Object obj) {
        return id(obj, true);
    }

    @SuppressWarnings("rawtypes")
    public static String[] toStringArray(Object obj) {
        if (obj == null)
            return null;
        if (obj instanceof String[])
            return (String[])obj;
        if (obj instanceof List)
            obj = ((List)obj).toArray();
        String[] result = null;
        if (obj instanceof Object[]) {
            Object[] objArray = (Object[])obj;
            result = new String[objArray.length];
            for (int i = 0; i < objArray.length; ++i) {
                result[i] = objArray[i] != null ? objArray[i].toString() : null;
            }
        } else {
            result = new String[] { obj != null ? obj.toString() : null };
        }
        return result;
    }

    /**
     * Estrae il campo indicato dai generic value della lista e restituisce 
     * una lista parallela di sole stringhe
     * @param gvList
     * @param fieldName
     * @return
     */
    public static List<String> makeFieldList(List<GenericValue> gvList, String fieldName) {
        List<String> res = FastList.newInstance();
        for (GenericValue item : gvList) {
            res.add(item.getString(fieldName));
        }
        return res;
    }

    /**
     * Data una lista retorna un ogetto json
     * @param object
     * @return 
     */
    public static Object jsonResponseFromObject(List<Map<String, Object>> object) {
        if (UtilValidate.isEmpty(object)) {
            return "";
        }
        try {
            return JSON.from(object).toString();
        } catch (IOException e) {
            Debug.logError(e, "Error in jsonResponseFromObject ");
        }
        return "";
    }
}
