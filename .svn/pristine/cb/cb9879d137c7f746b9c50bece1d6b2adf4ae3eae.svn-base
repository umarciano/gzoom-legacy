package com.mapsengineering.base.util;

import java.util.Map;

import java.util.Hashtable;

/**
 * Enumeration for permission prefix
 * @author nito
 *
 */
public enum ContextPermissionPrefixEnum {
	
    CTX_BS("CTX_BS", "BSCPERF"),
    CTX_OR("CTX_OR", "ORGPERF"),
    CTX_EP("CTX_EP", "EMPLPERF"),
    CTX_PM("CTX_PM", "PROJECT"),
    CTX_AM("CTX_AM", "MANAGACC"),
    CTX_CO("CTX_CO", "CORPERF"),
    CTX_CG("CTX_CG", "CDGPERF"),
    CTX_PR("CTX_PR", "PROCPERF"),
    CTX_TR("CTX_TR", "TRASPERF"),
    CTX_RE("CTX_RE", "RENDPERF"),
    CTX_GD("CTX_GD", "GDPRPERF"),
    CTX_PA("CTX_PA", "PARTPERF"),
    CTX_DI("CTX_DI", "DIRIGPERF");
	
    private final String code;
	
	private final String permissionPrefix;
	
	/**
	 * Constructor
	 * @param code
	 * @param permissionPrefix
	 */
	ContextPermissionPrefixEnum(String code, String permissionPrefix) {
		this.code = code;
		this.permissionPrefix = permissionPrefix;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the permissionPrefix
	 */
	public String getPermissionPrefix() {
		return permissionPrefix;
	}
	
	private static final Map<String, String> PERM_PREFIX_MAP = new Hashtable<String, String>();
	
	static {
        for (ContextPermissionPrefixEnum ss : values()) {
        	PERM_PREFIX_MAP.put(ss.code, ss.permissionPrefix);
        }
	}
	
	/**
	 * gives the permissionPrefix associated to code
	 * @param code
	 * @return
	 */
	public static String getPermissionPrefix(String code) {
		return PERM_PREFIX_MAP.get(code);
	}

}
