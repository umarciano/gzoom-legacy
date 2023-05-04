package com.mapsengineering.base.standardimport.common;

import java.util.Hashtable;
import java.util.Map;

/**
 * Enumeration 
 *
 */
public enum EntityCodeEnum {
	ORGA("ORGA", "OrganizationInterface", "STD_UP_ORGANIZATIONI", "STD_ORGANIZATIONINTE"),
	PERS("PERS", "PersonInterface", "STD_UP_PERSONINTERFA", "STD_PERSONINTERFACE"),
    UNIT("UNIT", "GlAccountInterface", "STD_UP_GLACCOUNTINTE", "STD_GLACCOUNTINTERFA"),
    MOVI("MOVI", "AcctgTransInterface", "STD_UP_ACCTGTRANSINT", "STD_ACCTGTRANSINTERF");
	
    private final String code;
	
	private final String interfaceName;
	
	private final String serviceTypeId;
	
	private final String serviceTypeIdUplodFile;
    
	/**
	 * Constructor
	 * @param code
	 * @param interfaceName
	 */
	EntityCodeEnum(String code, String interfaceName, String serviceTypeId, String serviceTypeIdUplodFile) {
		this.code = code;
		this.interfaceName = interfaceName;
		this.serviceTypeId = serviceTypeId;
		this.serviceTypeIdUplodFile = serviceTypeIdUplodFile;
	}

	private static final Map<String, EntityCodeEnum> STRING_2_TYPE = new Hashtable<String, EntityCodeEnum>();

	static {
        for (EntityCodeEnum ss : values()) {
        	STRING_2_TYPE.put(ss.code, ss);
        }
	}
	
	/**
	 * Return Map
	 * @param code
	 * @return
	 */
	public static EntityCodeEnum parse(String code) {
		return STRING_2_TYPE.get(code);
	}

    /**
     * @return the interfaceName
     */
    public String getInterfaceName() {
        return interfaceName;
    }

    /**
     * @return the serviceTypeId
     */
    public String getServiceTypeId() {
        return serviceTypeId;
    }

    /**
     * @return the serviceTypeIdUplodFile
     */
    public String getServiceTypeIdUplodFile() {
        return serviceTypeIdUplodFile;
    }
}
