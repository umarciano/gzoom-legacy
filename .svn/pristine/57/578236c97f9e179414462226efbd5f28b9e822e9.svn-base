package com.mapsengineering.base.standardimport.common;

import java.util.Hashtable;
import java.util.Map;

/**
 * Enumeration 
 *
 */
public enum PersonTypeEnum {
	WEM_EVAL_MANAGER("WEM_EVAL_MANAGER", "evaluatorCode", "WEF_EVALUATED_BY", "Valutatore", "MAN"),
    WEM_EVAL_APPROVER("WEM_EVAL_APPROVER", "approverCode", "WEF_APPROVED_BY", "Approvatore", "APP");
	
    private final String roleTypeId;
	
	private final String interfaceFieldName;
	
	private final String partyRelationshipTypeId;
	
	private final String description;

    private final String code;

	/**
	 * Constructor
	 * @param roleTypeId
	 * @param interfaceFieldName
	 * @param partyRelationshipTypeId
	 * @param description
	 * @param code
	 */
	PersonTypeEnum(String roleTypeId, String interfaceFieldName, String partyRelationshipTypeId, String description, String code) {
		this.roleTypeId = roleTypeId;
		this.interfaceFieldName = interfaceFieldName;
		this.partyRelationshipTypeId = partyRelationshipTypeId;
		this.description = description;
		this.code = code;
	}

	/**
	 * Return roleTypeId (es: WEM_EVAL_MANAGER)
	 * @return roleTypeId
	 */
	public String roleTypeId() {
		return roleTypeId;
	}
	
	/**
     * Return description (es: Evaluator)
     * @return description
     */
    public String description() {
        return description;
    }
	
    /**
     * Return interfaceFieldName (es: evaluatorCode)
     * @return interfaceFieldName
     */
    public String interfaceFieldName() {
		return interfaceFieldName;
	}

	private static final Map<String, PersonTypeEnum> STRING_2_TYPE = new Hashtable<String, PersonTypeEnum>();

	static {
        for (PersonTypeEnum ss : values()) {
        	STRING_2_TYPE.put(ss.roleTypeId, ss);
        }
	}
	
	/**
	 * Return Map
	 * @param code
	 * @return
	 */
	public static PersonTypeEnum parse(String roleTypeId) {
		return STRING_2_TYPE.get(roleTypeId);
	}

	/**
	 * Return partyRelationshipTypeId (es: WEF_EVALUATED_BY)
	 * @return partyRelationshipTypeId
	 */
    public String partyRelationshipTypeId() {
        return partyRelationshipTypeId;
    }

    /**
     * Return code (es: MAN)
     * @return the code
     */
    public String getCode() {
        return code;
    }
}
