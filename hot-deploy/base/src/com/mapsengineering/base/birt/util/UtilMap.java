package com.mapsengineering.base.birt.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javolution.util.FastMap;


public class UtilMap {
    

//    private static Map<? extends Object, ? extends Object> subMap(Map<? extends Object, ? extends Object> map, List<? extends Object> keyList){
//        if (map == null) return null;
//        if (keyList == null) return null;
//        
//        Map<? extends Object, ? extends Object> subMap = FastMap.newInstance();
//        
//        for (Map.Entry<? extends Object, ? extends Object> entry: map.entrySet()) {
//            Object key = entry.getKey();
//            if (keyList.contains(key)) {
//                subMap.putAll((Map<?, ?>)entry);
//            }
//        }
//        
//        return subMap;
//    }
    /**
     * Data una mappa e una lista che contine le chiavi delle mappa
     * ritorno una mappa che contiene solamente le chiavi della lista passata in ingresso
     * @param map
     * @param keyList
     * @return
     */
    public static Map<String, Object> subMap(Map<String, Object> map, List<String> keyList) {
        if (map == null) return null;
        if (keyList == null) return null;
        
        Map<String, Object> subMap = FastMap.newInstance();
        
        for (Entry<String, Object> entry: map.entrySet()) {
            String key = entry.getKey();
            if (keyList.contains(key)) {
                subMap.put(key, entry.getValue());
            }
        }
        
        return subMap;
    }
}
