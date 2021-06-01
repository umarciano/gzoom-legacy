package com.mapsengineering.base.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.component.ComponentConfig;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.ResourceBundleMapWrapper;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;

import com.mapsengineering.base.util.MessageUtil;

/**
 * Helper for left menu
 *
 */
public final class MenuHelper {

	public static final String MODULE = MenuHelper.class.getName();
    
	private static final String ATTR_NAME = "attrName";
    private static final String ATTR_VALUE = "attrValue";
    private static final String CONTENT_ID = "contentId";
    private static final String LINK = "link";
    private static final String TITLE = "title";
    
    /**
     * Constructor
     */
    private MenuHelper() {}

    /**
     * Controllo se l'utente loggato ha le permission listate
     * @param permissionList Lista di permission separate da ,
     * @param userLogin userLogin in sessione
     * @param security istanza di Security nel context di Ofbiz
     * @return true se l'utente possiede una delle permission listate, false altrimenti
     */
    public static boolean hasPermission(String permissionList, GenericValue userLogin, Security security) {

    	//default
        boolean res = false;
        try {
            //Stringa vuota OK
            if (UtilValidate.isEmpty(permissionList)) {
                return true;
            }
            StringTokenizer st = new StringTokenizer(permissionList, ",");
            //Permissions in OR
            while (st.hasMoreTokens()) {
                String perm = st.nextToken();
                if (UtilValidate.isEmpty(perm)) {
                    continue;
                }
                if (security.hasPermission(perm, userLogin)) {
                    return true;
                }
            }

        } catch (Exception e) {
            Debug.logError("Errore esecuzione metodo statico hasPermission della classe MenuHelper: " + e.getMessage(), MODULE);
        }
        return res;
    }

    /**
     * 
     * @param session
     * @param itemListLink
     * @return
     */
    public static Map<String, String> initItemSelectedMap(HttpSession session, String itemListLink) {
        Map<String, String> itemSelectedMap = null;
        if (session != null) {
            itemSelectedMap = FastMap.newInstance();
            itemSelectedMap.put("itemListLink", itemListLink);
            session.setAttribute("itemSelectedMap", itemSelectedMap);
        }
        return itemSelectedMap;
    }
    
    /**
     * Checks if the logged user has the basic permissions required by the module
     * @param request The HttpServletRequest
     * @param contextRoot The module name
     * @return True if the user has the required permission, false otherwise
     */
    public static boolean hasBasePermission(HttpServletRequest request, String contextRoot) {
    	ServletContext context = (ServletContext) request.getAttribute("servletContext");
    	Security security = (Security)request.getAttribute("security");
    	GenericValue userLogin = (GenericValue)request.getAttribute("userlogin");
    	
    	String serverId = (String) context.getAttribute("_serverId");
    	
    	return MenuHelper.hasBasePermission(security, userLogin, serverId, contextRoot);
    }
    
    /**
     * Checks if the logged user has the basic permissions required by the module
     * @param security The security handler
     * @param userLogin The user login object associated with the logged user 
     * @param serverId The server name
     * @param contextRoot The module name
     * @return True if the user has the required permission, false otherwise
     */
    public static boolean hasBasePermission(Security security, GenericValue userLogin, String serverId, String contextRoot) {
    	ComponentConfig.WebappInfo info = ComponentConfig.getWebAppInfo(serverId, contextRoot);
        
        if (security != null) {
            if (info != null) {
                for (String permission: info.getBasePermission()) {
                    if (!"NONE".equals(permission) && !security.hasEntityPermission(permission, "_VIEW", userLogin)) {
                        return false;
                    }
                }
            } else {
                Debug.logInfo("No webapp configuration found for : " + serverId + " / " + contextRoot, MODULE);
            }
        } else {
            Debug.logWarning("Received a null Security object from HttpServletRequest", MODULE);
        }
        
        return true;
    }
    
    /**
     * Creates a list of enabled ContentAssocViewTo objects checking if the logged user has the permissions
     * required by each menu item
     * @param request The HttpServletRequest
     * @param startContentId The root content id
     * @return The list of enabled ContentAssocViewTo objects
     */
    public static List<Map<String, Object>> createValidSubContentMap(HttpServletRequest request, String startContentId, String originalBreadcrumbs) {
        // long startTime = System.currentTimeMillis();
        Delegator delegator = (Delegator) request.getAttribute("delegator");
    	Map<String, Object> paramMap = UtilHttp.getCombinedMap(request);
    	Locale locale = UtilHttp.getLocale(request);
    	ResourceBundleMapWrapper uiLabelMap = UtilProperties.getResourceBundleMap("MenuUiLabels", locale);

    	List<Map<String, Object>> validSubContentList = new FastList<Map<String, Object>>();
    	
    	List<GenericValue> contentAssocViewToList = null;
		try {
			EntityConditionList<EntityCondition> listCondition = EntityConditionList.makeCondition(EntityCondition.makeCondition("contentIdStart", startContentId), EntityCondition.makeCondition("caContentAssocTypeId", "TREE_CHILD"), EntityCondition.makeConditionDate("fromDate", "thruDate"));
			contentAssocViewToList = delegator.findList("ContentAssocViewTo", listCondition, null, UtilMisc.toList("caSequenceNum"), null, false);
		} catch (GenericEntityException gee) {
			Debug.log(gee);
		}
		if (contentAssocViewToList != null) {
    	    for (GenericValue contentAssocViewTo : contentAssocViewToList) {
    		    Map<String, Object> dataMap = new FastMap<String, Object>();

                List<GenericValue> contentAttributes = null;
    		
    		    String relContentId = (String)contentAssocViewTo.get(CONTENT_ID);
    		    List<GenericValue> links = null;
			    try {
                    contentAttributes = delegator.findByAndCache("ContentAttribute", UtilMisc.toMap(CONTENT_ID, relContentId));
				    links = EntityUtil.filterByAnd(contentAttributes, UtilMisc.toMap(ATTR_NAME, LINK));
			    } catch (GenericEntityException gee) {
				    Debug.logError(gee, MODULE);
			    }
    		    if (UtilValidate.isNotEmpty(links)) {
                    GenericValue userLogin = (GenericValue)paramMap.get("userLogin");
                    Security security = (Security)paramMap.get("security");
                    String serverId = (String) paramMap.get("_serverId");

                    Map<String, Object> subContentMap = getSubContentMap(delegator, locale, relContentId, userLogin, security, serverId, originalBreadcrumbs);
                    if (UtilValidate.isNotEmpty(subContentMap)) {
                        validSubContentList.add(subContentMap);
                    }
    		    }
    		    else {
                    dataMap.put(CONTENT_ID, relContentId);
 
                    List<GenericValue> titles = EntityUtil.filterByAnd(contentAttributes, UtilMisc.toMap(ATTR_NAME, TITLE));

                    String title = "";
                    String titleLabel = "";
                    String defaultTitle = "";
                    if (UtilValidate.isNotEmpty(titles)) {
                        titleLabel = EntityUtil.getFirst(titles).getString(ATTR_VALUE);
                        defaultTitle = (String)uiLabelMap.get(titleLabel);
                        title = MessageUtil.getMessage(titleLabel, defaultTitle, locale);
                        dataMap.put(TITLE, title);
                    }


    			    String contentType = contentAssocViewTo.getString("contentTypeId");
    			    if (contentType != null && "GPLUS_MENU_ROOT".equals(contentType)) {
    				    String localBreadcrumbs = originalBreadcrumbs;
    				    if (localBreadcrumbs.equals("")) {
    					    localBreadcrumbs = "[" + MessageUtil.getMessage(titleLabel, defaultTitle, locale) + "|";
    				    }
    				    else {
    					    localBreadcrumbs = localBreadcrumbs + MessageUtil.getMessage(titleLabel, defaultTitle, locale) + "|"; 
    				    }
    				    List<Map<String, Object>> childsToDisplay = MenuHelper.createValidSubContentMap(request, relContentId, localBreadcrumbs);
    				    if (UtilValidate.isNotEmpty(childsToDisplay)) {
    					    dataMap.put("child", childsToDisplay);
    					    validSubContentList.add(dataMap);
    				    }
    			    }
    		    }
    	    }
        }
		// long endTime = System.currentTimeMillis();
        // if (Debug.timingOn()) { Debug.log("createValidSubContentMap " + (endTime - startTime) + " milliseconds ");}
    	return validSubContentList;
    }

    /**
     * 
     * @param delegator
     * @param locale
     * @param relContentId
     * @param userLogin
     * @param security
     * @param serverId
     * @param originalBreadcrumbs
     * @return 
     */
    public static Map<String, Object> getSubContentMap(Delegator delegator, Locale locale, String relContentId, GenericValue userLogin, Security security, String serverId, String originalBreadcrumbs) {
        // long startTime = System.currentTimeMillis();
        Map<String, Object> dataMap = new HashMap<String, Object>();

        ResourceBundleMapWrapper uiLabelMap = UtilProperties.getResourceBundleMap("MenuUiLabels", locale);

        List<GenericValue> contentAttributes = null;
        List<GenericValue> links = null;
        List<GenericValue> linkContextRoot = null;
        List<GenericValue> titles = null;
        List<GenericValue> targetTypes = null;
        String titleLabel = "";
        String title = "";
        String defaultTitle = "";

        try {
            contentAttributes = delegator.findByAndCache("ContentAttribute", UtilMisc.toMap(CONTENT_ID, relContentId));
            links = EntityUtil.filterByAnd(contentAttributes, UtilMisc.toMap(ATTR_NAME, LINK));
            titles = EntityUtil.filterByAnd(contentAttributes, UtilMisc.toMap(ATTR_NAME, TITLE));
            linkContextRoot = EntityUtil.filterByAnd(contentAttributes, UtilMisc.toMap(ATTR_NAME, "link-contextRoot"));
            targetTypes = EntityUtil.filterByAnd(contentAttributes, UtilMisc.toMap(ATTR_NAME, "target-type"));
        } catch (GenericEntityException gee) {
            Debug.logError(gee, MODULE);
        }
        if (UtilValidate.isNotEmpty(titles)) {
            titleLabel = EntityUtil.getFirst(titles).getString(ATTR_VALUE);
            defaultTitle = (String)uiLabelMap.get(titleLabel);
            title = MessageUtil.getMessage(titleLabel, defaultTitle, locale);
        }

        String linkValue = EntityUtil.getFirst(links).getString(ATTR_VALUE);
        int secondSlash = linkValue.indexOf('/', 1);
        if (secondSlash > 0) {
            String contextRoot = linkValue.substring(0, secondSlash);
            String contextPermissionRoot = contextRoot;
            
            if(UtilValidate.isNotEmpty(linkContextRoot)){
                contextPermissionRoot = EntityUtil.getFirst(linkContextRoot).getString(ATTR_VALUE);
            } 
            
            boolean modulePermission = MenuHelper.hasBasePermission(security, userLogin, serverId, contextPermissionRoot);
            if (modulePermission) {
                String userLoginId = (String)userLogin.get("userLoginId");
                List<GenericValue> userLoginSecurityGroups = null;
                try {
                    userLoginSecurityGroups = delegator.findByAndCache("UserLoginSecurityGroup", UtilMisc.toMap("userLoginId", userLoginId));
                } catch (GenericEntityException e) {
                    Debug.logError(e, MODULE);
                }
                userLoginSecurityGroups = EntityUtil.filterByDate(userLoginSecurityGroups);
                List<String> groupIds = EntityUtil.getFieldListFromEntityList(userLoginSecurityGroups, "groupId", true);

                List<GenericValue> securityGroupContents = null;
                List<EntityExpr> exprs = UtilMisc.toList(
                        EntityCondition.makeCondition(CONTENT_ID, EntityOperator.EQUALS, relContentId),
                        EntityCondition.makeCondition("groupId", EntityOperator.IN, groupIds));
                EntityConditionList<EntityExpr> conditions = EntityCondition.makeCondition(exprs, EntityOperator.AND);
                try {
                    securityGroupContents = delegator.findList("SecurityGroupContent", conditions, null, null, null, true);
                } catch (GenericEntityException gee) {
                    Debug.logError(gee, MODULE);
                }
                securityGroupContents = EntityUtil.filterByDate(securityGroupContents);

                boolean contentAttributePermission = UtilValidate.isEmpty(securityGroupContents);

                if (contentAttributePermission) {
                    String targetType = EntityUtil.getFirst(targetTypes).getString(ATTR_VALUE);
                    dataMap.put(CONTENT_ID, relContentId);
                    dataMap.put(TITLE, title);

                    String localBreadcrumbs = originalBreadcrumbs;
                    if (UtilValidate.isNotEmpty(title)) {
                        localBreadcrumbs = localBreadcrumbs + title + "]";
                    }

                    if (linkValue.indexOf('?') != -1) {
                        linkValue = linkValue.substring(0, linkValue.indexOf('?'));
                    }
                    dataMap.put(LINK, linkValue);
                    dataMap.put("targetType", targetType);
                    if(UtilValidate.isEmpty(targetType) || !"plain".equals(targetType)){
                        dataMap.put("breadcrumbs", localBreadcrumbs);
                    }
                    
                    List<GenericValue> linkList = EntityUtil.filterByCondition(contentAttributes, EntityCondition.makeCondition(ATTR_NAME, EntityOperator.LIKE, "link%"));
                    linkList = EntityUtil.orderBy(linkList, UtilMisc.toList(ATTR_NAME));
                    if (UtilValidate.isNotEmpty(linkList)) {
                        dataMap.put("parameterList", MenuUtil.createParameterList(linkList, ATTR_VALUE));
                    }

                    dataMap.put("module", contextRoot);
                }
            }
        }
        // long endTime = System.currentTimeMillis();
        // if (Debug.timingOn()) {Debug.log("getSubContentMap " + (endTime - startTime) + " milliseconds ");}
        return dataMap;
    }

    /**
     * 
     * @param delegator
     * @param locale
     * @param menuItemContentId
     * @param originalData
     * @return
     */
    public static Map<String, Object> getModule(Delegator delegator, Locale locale, String menuItemContentId, Map<String, Object> originalData) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> dataMap = originalData;
        if (dataMap == null) {
            dataMap = new HashMap<String, Object>();
        }

        if (UtilValidate.isNotEmpty(menuItemContentId)) {
            String originalBreadcrumbs = UtilGenerics.cast(dataMap.get("breadcrumbs"));
            if (UtilValidate.isEmpty(originalBreadcrumbs)) {
                originalBreadcrumbs = "]";
                dataMap.put("breadcrumbs", originalBreadcrumbs);
            }

            ResourceBundleMapWrapper uiLabelMap = UtilProperties.getResourceBundleMap("MenuUiLabels", locale);

            List<GenericValue> contentAssocViewFromList = null;
            try {
                EntityConditionList<EntityCondition> listCondition = EntityConditionList.makeCondition(EntityCondition.makeCondition("contentIdStart", menuItemContentId), EntityCondition.makeCondition("contentTypeId", "GPLUS_MENU_ROOT"), EntityCondition.makeCondition("caContentAssocTypeId", "TREE_CHILD"), EntityCondition.makeConditionDate("fromDate", "thruDate"));
                contentAssocViewFromList = delegator.findList("ContentAssocViewFrom", listCondition, null, UtilMisc.toList("caSequenceNum"), null, false);

                List<GenericValue> contentAttributes = delegator.findByAndCache("ContentAttribute", UtilMisc.toMap(CONTENT_ID, menuItemContentId));
                List<GenericValue> titles = EntityUtil.filterByAnd(contentAttributes, UtilMisc.toMap(ATTR_NAME, TITLE));

                if (UtilValidate.isNotEmpty(titles)) {
                    String titleLabel = EntityUtil.getFirst(titles).getString(ATTR_VALUE);
                    String defaultTitle = (String)uiLabelMap.get(titleLabel);
                    String title = MessageUtil.getMessage(titleLabel, defaultTitle, locale);

                    if (UtilValidate.isNotEmpty(originalBreadcrumbs)) {
                        originalBreadcrumbs = (UtilValidate.isNotEmpty(contentAssocViewFromList) ? "|" : "") + title + originalBreadcrumbs;
                        dataMap.put("breadcrumbs", originalBreadcrumbs);
                    }
                }
            } catch (GenericEntityException gee) {
                Debug.log(gee);
            }

            if (UtilValidate.isEmpty(contentAssocViewFromList)) {
                if (UtilValidate.isNotEmpty(originalBreadcrumbs)) {
                    originalBreadcrumbs = "[" + originalBreadcrumbs;

                    dataMap.put("breadcrumbs", originalBreadcrumbs);
                    dataMap.put("moduleContentId", menuItemContentId);
                }
            } else {
                for (GenericValue contentAssocViewFrom : contentAssocViewFromList) {
                    dataMap = getModule(delegator, locale, contentAssocViewFrom.getString("contentId"), dataMap);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        Debug.log("getModule " + (endTime - startTime) + " milliseconds ");
        return dataMap;
    }
    
    /**
     * seleziono il modulo del menu da far viualizzare dopo il login o dopo il tasto home
     * @param validSubContentList
     * @return
     */
    public static String selectModuleValidSubContent(List<Map<String, Object>> validSubContentList) {
    	    	
    	if(UtilValidate.isEmpty(validSubContentList)){
    		return "";    		
    	} 
    	
    	return (String) validSubContentList.get(0).get("contentId");
    }
    	   
    
}
