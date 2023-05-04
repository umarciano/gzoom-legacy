package com.mapsengineering.base.birt.util;


import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;

import com.mapsengineering.base.birt.model.ModelReader;
import com.mapsengineering.base.birt.model.ModelReport;
import com.mapsengineering.base.reminder.E;
import com.mapsengineering.base.util.ContextPermissionPrefixEnum;

/**
 * 
 * Utility per gestire le responsabilita' degli utenti
 */
public class Utils {

	private static String MGR_ADMIN = "MGR_ADMIN";
	private static String ORG_ROLE_ADMIN = "ORG_ROLE_ADMIN";
	private static String ORG_ADMIN = "ORG_ADMIN";
	private static String ROLE_ADMIN = "ROLE_ADMIN";

	private static String BSCPERF = "BSCPERF";
	
	@Deprecated
	/**
	 * Al suo posto utilizzare getMapUserPermision
	 * @param security
	 * @param userLogin
	 * @param localDispatcherName
	 * @return
	 */
	public static String getUserProfile(Security security, GenericValue userLogin, String localDispatcherName) {
		String userProfile = "";
		String permission = permissionLocalDispatcherName(localDispatcherName);
		
		if (UtilValidate.isNotEmpty(permission)) {
			if (security.hasPermission(permission + MGR_ADMIN, userLogin)) {
				userProfile = MGR_ADMIN;
			} else if (security.hasPermission(permission + ORG_ADMIN, userLogin)) {
				if (security.hasPermission(permission	+ ROLE_ADMIN, userLogin)) {
					userProfile = ORG_ROLE_ADMIN;
				} else {
					userProfile = ORG_ADMIN;
				}
			} else {
				userProfile = ROLE_ADMIN;
			}
		}		
		return userProfile;
	}
	
	/**
	 * Data il localDispatcherName, ritorno il nome del gruppo di sicurezza
	 * @param localDispatcherName
	 * @return
	 */
	public static String permissionLocalDispatcherName(String localDispatcherName) {
	    String permission = localDispatcherName.toUpperCase();
	    if ("STRATPERF".equals(permission)) {
	        permission = BSCPERF;
	    }
	    
	    return permission;
	}
	
	/**
	 * Ritorna se un utente ha un gruppo di sicurezza
	 * @param security
	 * @param userLogin
	 * @param gruopId
	 * @return
	 */
	public static boolean hasSecurityGroup(Security security, GenericValue userLogin, String gruopId) {
	    try {
	        EntityCondition entitycond = EntityCondition.makeCondition(
                                            EntityCondition.makeCondition("userLoginId", userLogin.get("userLoginId")), 
                                            EntityCondition.makeCondition("groupId", gruopId));
            List<GenericValue> list = EntityUtil.filterByDate(security.getDelegator().findList("UserLoginSecurityGroup", entitycond, null, null, null, false));
            if (UtilValidate.isNotEmpty(list)) {
                return true;
            }
            
        } catch (GenericEntityException e) {
            Debug.logError(e, "");
        }
	    return false;
	}
	
	/**
	 * Ritorna true se un utente e' Responsabile Organizzativo
	 * @param security
	 * @param userLogin
	 * @param permission
	 * @return
	 */
	public static boolean isOrgMgr(Security security, GenericValue userLogin, String permission) {
	    return security.hasPermission(permission + "ORG_ADMIN", userLogin);	    
	}

	/**
	 * Ritorna true se un utente ha un Ruolo di Gestione
	 * @param security
	 * @param userLogin
	 * @param permission
	 * @return
	 */
	public static boolean isRole(Security security, GenericValue userLogin, String permission) {
	    return security.hasPermission(permission + "ROLE_ADMIN", userLogin);
	}

	/**
	 * Ritorna true se un utente e' Resp. organizz. superiore
	 * @param security
	 * @param userLogin
	 * @param permission
	 * @return
	 */
	public static boolean isSup(Security security, GenericValue userLogin, String permission) {
	    return security.hasPermission(permission + "SUP_ADMIN", userLogin);
	}

	/**
	 * Ritorna true se un utente e' Verice Gerarchico
	 * @param security
	 * @param userLogin
	 * @param permission
	 * @return
	 */
	public static boolean isTop(Security security, GenericValue userLogin, String permission) {
	    return security.hasPermission(permission + "TOP_ADMIN", userLogin);
	}

	/**
	 * Ritorna una mappa con la sintesi dei permessi di un utente, utile nelle stampe e in molti servizi
	 * @param security
	 * @param weContextId
	 * @param userLogin
	 * @param workEffortRevisionId
	 * @return
	 */
	public static Map<String, Object> getMapUserPermision(Security security, String weContextId, GenericValue userLogin, String workEffortRevisionId) {
	    Map<String, Object> result = Utils.getMapUserPermision(security, weContextId, userLogin, false, workEffortRevisionId);
        return result;
	}
	
	/**
     * Ritorna una mappa con la sintesi dei permessi di un utente, utile nelle stampe e in molti servizi
     * @param security
     * @param weContextId
     * @param userLogin
     * @param excludeViewAdminView, se deve vedere tutte le UO o solo quelle di sua competenza
     * @param workEffortRevisionId
     * @return
     */
    public static Map<String, Object> getMapUserPermision(Security security, String weContextId, GenericValue userLogin, Boolean excludeViewAdminView, String workEffortRevisionId) {
        Map<String, Object> result = new HashMap<String, Object>();
        String permission = ContextPermissionPrefixEnum.getPermissionPrefix(weContextId);
                
        boolean isOrgMgr = false;
        boolean isRole = false;
        boolean isSup = false;
        boolean isTop = false;
        
        boolean userLoginPermissionViewAdmin = security.hasPermission(permission + "VIEW_ADMIN", userLogin);
        
        if (!userLoginPermissionViewAdmin || excludeViewAdminView) {   
            isOrgMgr = isOrgMgr(security, userLogin, permission);
            isRole = isRole(security, userLogin, permission);
            isSup = isSup(security, userLogin, permission);
            isTop = isTop(security, userLogin, permission);
        }
        
        result.put("isOrgMgr", isOrgMgr);
        result.put("isRole", isRole);
        result.put("isSup", isSup);
        result.put("isTop", isTop);
        result.put("userLogin", userLogin);
        result.put("weContextId", weContextId);
        result.put("workEffortRevisionId", workEffortRevisionId);
        
        return result;
        
    }
	
	/**
	 * Ritorna una mappa con la sintesi dei permessi di un utente e il tipo obiettivo, utile nelle stampe e in molti servizi
	 * @param security
	 * @param weContextId
	 * @param userLogin
	 * @param workEffortTypeId
	 * @param workEffortRevisionId
	 * @return
	 */
	public static Map<String, Object> getMapUserPermision(Security security, String weContextId, GenericValue userLogin, String workEffortTypeId, String workEffortRevisionId) {
	    Map<String, Object> result = Utils.getMapUserPermision(security, weContextId, userLogin, workEffortRevisionId);
        result.put("workEffortTypeId", workEffortTypeId);
        return result;
    }
	
	/**
	 * Ritorna una mappa con la sintesi dei permessi di un utente per quanto riguarda la sola responsabilita', senza revisionId, utile nelle stampe e in molti servizi
	 * @param security
	 * @param weContextId
	 * @param userLogin
	 * @return
	 */
	public static Map<String, Object> getMapUserPermisionOrgUnit(Security security, String weContextId, GenericValue userLogin) {
        Map<String, Object> result = Utils.getMapUserPermisionOrgUnit(security, weContextId, userLogin, false);
        return result;
    }
	
	/**
     * Ritorna una mappa con la sintesi dei permessi di un utente per quanto riguarda la sola responsabilita', senza revisionId, utile nelle stampe e in molti servizi
     * @param security
     * @param weContextId
     * @param userLogin
     * @param excludeViewAdminView, se deve vedere tutte le UO o solo quelle di sua competenza
     * @return
     */
    public static Map<String, Object> getMapUserPermisionOrgUnit(Security security, String weContextId, GenericValue userLogin, Boolean excludeViewAdminView ) {
        Map<String, Object> result = Utils.getMapUserPermision(security, weContextId, userLogin, excludeViewAdminView, null);
        result.remove("isRole");
        result.remove("workEffortRevisionId");
        return result;
    }
	
	/**
	 * Controlla e riotrna la mappa da passare al report birt
	 * @param context
	 * @param ele
	 * @param reportId
	 * @param locale
	 * @param localDispatcherName
	 * @return
	 * @throws GeneralException
	 */
	public static Map<String, Object> checkReportParameters(Map<String, Object> context, Map<String, Object> ele, String reportId, Locale locale, String localDispatcherName) throws GeneralException {
        ModelReport report = ModelReader.getModelReport(reportId);
        if (UtilValidate.isNotEmpty(report)) {
            Map<String, Object> birtParameters = new HashMap<String, Object>();
            birtParameters.putAll(context);  //devo caricare anceh i parametri che sono nel context (quelli che possono essere lanciati come filtri)
            birtParameters.putAll(ele); //dalle select della query elimino icampi che non servono al report
            
            birtParameters = report.convertAllValue(birtParameters, locale); //adesso carico nel report solo quelli nececssari
            if (UtilValidate.isNotEmpty(birtParameters)) {
                //aggiungo il localDispatcherName
                birtParameters.put(E.localDispatcherName.name(), localDispatcherName);
                return birtParameters;
            }
        } 
        return null;        
    }
	
	/**
	 * Ritorna la stringa con &amp;
	 * @param theMap
	 * @return
	 */
	public static <K, V> String toStringMap(Map<? extends K, ? extends V> theMap) {
        StringBuilder theBuf = new StringBuilder();
        for (Map.Entry<? extends K, ? extends V> entry: theMap.entrySet()) {
            theBuf.append(entry.getKey());
            theBuf.append("=");
            theBuf.append(entry.getValue());
            theBuf.append("&amp;");
        }
        return theBuf.toString();
    }
	
}
