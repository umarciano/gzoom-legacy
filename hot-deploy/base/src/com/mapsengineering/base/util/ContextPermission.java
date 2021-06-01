package com.mapsengineering.base.util;

import java.util.HashMap;
import java.util.Map;

public class ContextPermission {
	private Map<String, String> contextPermissionMap;
	
	/**
	 * constructor
	 */
	public ContextPermission() {
		contextPermissionMap = new HashMap<String, String>();
		contextPermissionMap.put("CTX_BS", "BSCPERFMGR");
		contextPermissionMap.put("CTX_OR", "ORGPERFMGR");
		contextPermissionMap.put("CTX_EP", "EMPLPERFMGR");
		contextPermissionMap.put("CTX_CO", "CORPERFMGR");
		contextPermissionMap.put("CTX_PR", "PROCPERFMGR");
		contextPermissionMap.put("CTX_GD", "GDPRPERFMGR");
		contextPermissionMap.put("CTX_CG", "CDGPERFMGR");
		contextPermissionMap.put("CTX_TR", "TRASPERFMGR");
		contextPermissionMap.put("CTX_RE", "RENDPERFMGR");
		contextPermissionMap.put("CTX_PA", "PARTPERFMGR");
		contextPermissionMap.put("CTX_DI", "DIRIGPERFMGR");
	}
	
	/**
	 * ritorna il permission
	 * @param contextValue
	 * @return
	 */
	public String getPermission(String contextValue) {
		return contextPermissionMap.get(contextValue);
	}

}
