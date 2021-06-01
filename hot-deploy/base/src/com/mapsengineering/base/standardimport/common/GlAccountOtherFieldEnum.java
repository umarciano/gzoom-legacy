package com.mapsengineering.base.standardimport.common;

import java.util.Hashtable;
import java.util.Map;

/**
 * Enumeration 
 *
 */
public enum GlAccountOtherFieldEnum {
    weScoreRangeEnumId("weScoreRangeEnumId", "WE_SCORE_RULE"),
    weScoreConvEnumId("weScoreConvEnumId", "WE_SCORE_CONVERSION"),
    weAlertRuleEnumId("weAlertRuleEnumId", "WE_ALERT_RULE"),
    weWithoutPerf("weWithoutPerf", "WE_WITHOUT_PERF");
    
    private final String enumTypeId;
	
	private final String fieldName;
	
	/**
	 * Constructor
	 * @param roleTypeId
	 * @param interfaceFieldName
	 * @param partyRelationshipTypeId
	 * @param description
	 */
	GlAccountOtherFieldEnum(String fieldName, String enumTypeId) {
		this.fieldName = fieldName;
		this.enumTypeId = enumTypeId;
	}

	private static final Map<String, GlAccountOtherFieldEnum> STRING_2_TYPE = new Hashtable<String, GlAccountOtherFieldEnum>();

	static {
        for (GlAccountOtherFieldEnum ss : values()) {
        	STRING_2_TYPE.put(ss.fieldName, ss);
        }
	}
	
	/**
	 * Return Map
	 * @param code
	 * @return
	 */
	public static GlAccountOtherFieldEnum parse(String fieldName) {
		return STRING_2_TYPE.get(fieldName);
	}

    /**
     * @return the enumTypeId
     */
    public String enumTypeId() {
        return enumTypeId;
    }
}
