package com.mapsengineering.base.standardimport.common;

import java.util.Map;

import javolution.util.FastMap;

/**
 * Lookup for WeMeasureType
 *
 */
public class WeMeasureTypeLookup {
    
    /**
     * (enumId, enumIndRes)
     *
     */
    public static class Entry{
        
        private final String enumId;
        private final String enumIndRes;
        
        /**
         * Constructor
         * @param enumId
         * @param enumIndRes
         */
        public Entry(String enumId, String enumIndRes){
            this.enumId = enumId;
            this.enumIndRes = enumIndRes;
        }

        /**
         * Return enumIndRes, "RES" or "IND"
         * @return
         */
        public String getEnumIndRes() {
            return enumIndRes;
        }

        /**
         * Return enumId
         * @return
         */
        public String getEnumId() {
            return enumId;
        }
    }
    
    private static final Map<String, Entry> LOOKUP = FastMap.newInstance();

    /**
     * WeMeasureType values
     *
     */
    private enum WeMeasureTypeEnum {
        WEMT_ECONOMIC, 
        WEMT_FINANCIAL, 
        WEMT_HUMAN, 
        WEMT_MEANS, 
        WEMT_ALERT, 
        WEMT_OUTCOME, 
        WEMT_OUTPUT, 
        WEMT_PERF, 
        WEMT_QUALITY
    }
    
    /**
     * Lookup
     */
    static {
        LOOKUP.put("Risorsa Economica", new Entry(WeMeasureTypeEnum.WEMT_ECONOMIC.name(), WeInterfaceConstants.RES));
        LOOKUP.put("Risorsa Finanziaria", new Entry(WeMeasureTypeEnum.WEMT_FINANCIAL.name(), WeInterfaceConstants.RES));
        LOOKUP.put("Risorsa Umana", new Entry(WeMeasureTypeEnum.WEMT_HUMAN.name(), WeInterfaceConstants.RES));
        LOOKUP.put("Risorsa Strumentale", new Entry(WeMeasureTypeEnum.WEMT_MEANS.name(), WeInterfaceConstants.RES));
        LOOKUP.put("Allarme", new Entry(WeMeasureTypeEnum.WEMT_ALERT.name(), WeInterfaceConstants.IND));
        LOOKUP.put("Effetto (OutCome)", new Entry(WeMeasureTypeEnum.WEMT_OUTCOME.name(), WeInterfaceConstants.IND));
        LOOKUP.put("Realizzazione (Output)", new Entry(WeMeasureTypeEnum.WEMT_OUTPUT.name(), WeInterfaceConstants.IND));
        LOOKUP.put("Prestazione (KPI)", new Entry(WeMeasureTypeEnum.WEMT_PERF.name(), WeInterfaceConstants.IND));
        LOOKUP.put("Qualita", new Entry(WeMeasureTypeEnum.WEMT_QUALITY.name(), WeInterfaceConstants.IND));
    }
    
    /**
     * Return value
     * @param description
     * @return
     */
    public static Entry get(String description){
        return LOOKUP.get(description);
    }
}
