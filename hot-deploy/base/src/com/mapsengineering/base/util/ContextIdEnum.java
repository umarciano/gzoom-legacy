package com.mapsengineering.base.util;

import java.util.Hashtable;
import java.util.Map;

/**
 * Enumeration with workeffort context 
 *
 */
public enum ContextIdEnum {
	// STR, ORG, IND, COR, PM, MAN 
    CTX_BS("CTX_BS", "WEPERFST", "STR", "stratperf"),
    CTX_OR("CTX_OR", "WEORGST", "ORG", "orgperf"),
    CTX_EP("CTX_EP", "WEEVALST", "IND", "emplperf"),
    CTX_PM("CTX_PM", "WEPRJST", "PM", "projectmgrext"),
    CTX_AM("CTX_AM", "WEMANAGST", "MAN", "managacc"),
    CTX_CO("CTX_CO", "WECORRST", "COR", "corperf"),
    CTX_CG("CTX_CG", "WECDGST", "CDG", "cdgperf"),
    CTX_PR("CTX_PR", "WEPROCST", "PROC", "procperf"),
    CTX_TR("CTX_TR", "WETRASST", "TRAS", "trasperf"),
    CTX_RE("CTX_RE", "WERENDST", "REND", "rendperf"),
    CTX_GD("CTX_GD", "WEGDPRST", "GDPR", "gdprperf"),
    CTX_PA("CTX_PA", "WEPARTST", "PART", "partperf"),
    CTX_DI("CTX_DI", "WEEVDIST", "DIR", "dirigperf");
    
    private final String code;
	
	private final String defaultStatusPrefix;
	
	private final String weContext;
	
	private final String webcontext;

	/**
	 * Constructor
	 * @param code
	 * @param defaultStatusPrefix
	 * @param weContext
	 * @param webcontext
	 */
	ContextIdEnum(String code, String defaultStatusPrefix, String weContext, String webcontext) {
		this.code = code;
		this.defaultStatusPrefix = defaultStatusPrefix;
		this.weContext = weContext;
		this.webcontext = webcontext;
	}

	/**
	 * Return code (es: CTX_BS)
	 * @return code
	 */
	public String code() {
		return code;
	}
	
	/**
     * Return webcontext (es: stratperf)
     * @return webcontext
     */
    public String webcontext() {
        return webcontext;
    }
	
    /**
     * Return defaultStatusPrefix (es: WEPERFST)
     * @return defaultStatusPrefix
     */
    public String defaultStatusPrefix() {
		return defaultStatusPrefix;
	}

	private static final Map<String, ContextIdEnum> STRING_2_TYPE = new Hashtable<String, ContextIdEnum>();
	private static final Map<String, ContextIdEnum> WE_CONTEXT_2_TYPE = new Hashtable<String, ContextIdEnum>();

	static {
        for (ContextIdEnum ss : values()) {
        	STRING_2_TYPE.put(ss.code, ss);
        	WE_CONTEXT_2_TYPE.put(ss.weContext, ss);
        }
	}
	
	/**
	 * Return Map
	 * @param code
	 * @return
	 */
	public static ContextIdEnum parse(String code) {
		return STRING_2_TYPE.get(code);
	}
	
	/**
	 * Return weContext Map
	 * @param weContext
	 * @return
	 */
	public static ContextIdEnum parseWeContext(String weContext) {
		return WE_CONTEXT_2_TYPE.get(weContext);
	}

	/**
	 * Return weContext (es: STR)
	 * @return weContext
	 */
    public String weContext() {
        return weContext;
    }
}
