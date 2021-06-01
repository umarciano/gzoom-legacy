package com.mapsengineering.base.birt.util;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;

import com.mapsengineering.base.reminder.E;

public class UtilFilter {
    
    public static final String MODULE = UtilFilter.class.getName();
    
    private String MGR_ADMIN = "MGR_ADMIN";
    private String ORG_ADMIN = "ORG_ADMIN";
    private String ROLE_ADMIN = "ROLE_ADMIN";
    private String SUP_ADMIN = "SUP_ADMIN";
    private String TOP_ADMIN = "TOP_ADMIN";
    
    private String FULLADMIN = "FULLADMIN";

    
    private String filterInnerJoin = " INNER JOIN \n" + 
             "      ( SELECT FIL_UL.PARTY_ID AS UL_PARTY_ID, FIL_ULVR.USER_LOGIN_ID AS ULVR_USER_LOGIN_ID, FIL_ULVR.PARTY_ID AS ULVR_PARTY_ID, FIL_ULVR.ROLE_TYPE_ID AS ULVR_ROLE_TYPE_ID \n" +
             "        FROM USER_LOGIN_VALID_PARTY_ROLE FIL_ULVR \n" +
             "        INNER JOIN USER_LOGIN FIL_UL ON FIL_ULVR.USER_LOGIN_ID = FIL_UL.USER_LOGIN_ID) \n" +
             "              FIL_U ON FIL_W.ORGANIZATION_ID = FIL_U.ULVR_PARTY_ID AND FIL_U.ULVR_ROLE_TYPE_ID = 'INTERNAL_ORGANIZATIO' \n" +
             " INNER JOIN WORK_EFFORT_TYPE_STATUS FIL_STATUS ON FIL_W.WORK_EFFORT_TYPE_ID = FIL_STATUS.WORK_EFFORT_TYPE_ROOT_ID AND FIL_W.CURRENT_STATUS_ID = FIL_STATUS.CURRENT_STATUS_ID \n" +
             " INNER JOIN STATUS_ITEM FIL_ST ON FIL_ST.STATUS_ID = FIL_W.CURRENT_STATUS_ID";
   
              
    private String filterLeftJoin = " LEFT OUTER JOIN PARTY_RELATIONSHIP FIL_PRM ON FIL_W.ORG_UNIT_ROLE_TYPE_ID = FIL_PRM.ROLE_TYPE_ID_FROM AND FIL_W.ORG_UNIT_ID = FIL_PRM.PARTY_ID_FROM \n" +  
                                    "       AND FIL_PRM.PARTY_RELATIONSHIP_TYPE_ID IN ('ORG_RESPONSIBLE', 'ORG_DELEGATE') AND FIL_PRM.FROM_DATE <= FIL_W.ESTIMATED_COMPLETION_DATE \n" +
                                    "       AND (FIL_PRM.THRU_DATE IS NULL OR FIL_PRM.THRU_DATE >= FIL_W.ESTIMATED_COMPLETION_DATE) AND FIL_PRM.PARTY_ID_TO = FIL_U.UL_PARTY_ID \n" +
                                    " LEFT OUTER JOIN PARTY_RELATIONSHIP FIL_PRS ON FIL_W.ORG_UNIT_ROLE_TYPE_ID = FIL_PRS.ROLE_TYPE_ID_TO AND FIL_W.ORG_UNIT_ID = FIL_PRS.PARTY_ID_TO \n" +
                                    "       AND FIL_PRS.PARTY_RELATIONSHIP_TYPE_ID = 'GROUP_ROLLUP' AND FIL_PRS.FROM_DATE <= FIL_W.ESTIMATED_COMPLETION_DATE \n" +
                                    "       AND (FIL_PRS.THRU_DATE IS NULL OR FIL_PRS.THRU_DATE >= FIL_W.ESTIMATED_COMPLETION_DATE) \n" + 
                                    " LEFT OUTER JOIN PARTY_RELATIONSHIP FIL_PRS2 ON FIL_PRS.ROLE_TYPE_ID_FROM = FIL_PRS2.ROLE_TYPE_ID_FROM AND FIL_PRS.PARTY_ID_FROM = FIL_PRS2.PARTY_ID_FROM \n" +
                                    "       AND FIL_PRS2.PARTY_RELATIONSHIP_TYPE_ID IN ('ORG_RESPONSIBLE', 'ORG_DELEGATE') AND FIL_PRS2.FROM_DATE <= FIL_W.ESTIMATED_COMPLETION_DATE \n" +
                                    "       AND (FIL_PRS2.THRU_DATE IS NULL OR FIL_PRS2.THRU_DATE >= FIL_W.ESTIMATED_COMPLETION_DATE) AND FIL_PRS2.PARTY_ID_TO = FIL_U.UL_PARTY_ID \n" +                              
                                    " LEFT OUTER JOIN PARTY_RELATIONSHIP FIL_PRS3 ON FIL_PRS.ROLE_TYPE_ID_FROM = FIL_PRS3.ROLE_TYPE_ID_TO AND FIL_PRS.PARTY_ID_FROM = FIL_PRS3.PARTY_ID_TO \n" +
                                    "       AND FIL_PRS3.PARTY_RELATIONSHIP_TYPE_ID = 'GROUP_ROLLUP' AND FIL_PRS3.FROM_DATE <= FIL_W.ESTIMATED_COMPLETION_DATE \n" +
                                    "       AND (FIL_PRS3.THRU_DATE IS NULL OR FIL_PRS3.THRU_DATE >= FIL_W.ESTIMATED_COMPLETION_DATE) \n" + 
                                    " LEFT OUTER JOIN PARTY_RELATIONSHIP FIL_PRS4 ON FIL_PRS3.ROLE_TYPE_ID_FROM = FIL_PRS4.ROLE_TYPE_ID_FROM AND FIL_PRS3.PARTY_ID_FROM = FIL_PRS4.PARTY_ID_FROM \n" +
                                    "       AND FIL_PRS4.PARTY_RELATIONSHIP_TYPE_ID IN ('ORG_RESPONSIBLE', 'ORG_DELEGATE') AND FIL_PRS4.FROM_DATE <= FIL_W.ESTIMATED_COMPLETION_DATE \n" +
                                    "       AND (FIL_PRS4.THRU_DATE IS NULL OR FIL_PRS4.THRU_DATE >= FIL_W.ESTIMATED_COMPLETION_DATE) AND FIL_PRS4.PARTY_ID_TO = FIL_U.UL_PARTY_ID \n" +                                 
                                    " LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT FIL_WEPA ON FIL_W.WORK_EFFORT_ID = FIL_WEPA.WORK_EFFORT_ID AND FIL_W.ESTIMATED_COMPLETION_DATE = FIL_WEPA.THRU_DATE \n" +
                                    "       AND FIL_WEPA.ROLE_TYPE_ID LIKE 'WEM%' AND FIL_WEPA.PARTY_ID = FIL_U.UL_PARTY_ID \n" + 
                                    " LEFT OUTER JOIN WORK_EFFORT_PARTY_ASSIGNMENT FIL_PA ON FIL_PA.WORK_EFFORT_ID = FIL_W.WORK_EFFORT_ID AND FIL_PA.ROLE_TYPE_ID = 'WEM_EVAL_IN_CHARGE'"; // TODO AND FIL_WEPA.ROLE_TYPE_ID = FIL_STATUS.MANAGEMENT_ROLE_TYPE_ID AND FIL_STATUS.MANAGEMENT_ROLE_TYPE_ID IS NOT NULL \n";

    
    
    private String filterWherIsOrg = " OR (FIL_PRM.PARTY_ID_TO IS NOT NULL AND (FIL_PA.PARTY_ID IS NULL OR FIL_PA.PARTY_ID <> 'loginPartyId'))"; //" OR (FIL_STATUS.MANAG_WE_STATUS_ENUM_ID = 'ORGMANAGER' AND FIL_PRM.PARTY_ID_TO IS NOT NULL) ";    
    private String filterWhereIsSup = " OR (FIL_PRS2.PARTY_ID_TO IS NOT NULL AND (FIL_PA.PARTY_ID IS NULL OR FIL_PA.PARTY_ID <> 'loginPartyId'))"; //" OR (FIL_STATUS.MANAG_WE_STATUS_ENUM_ID = 'SUPMANAGER' AND FIL_PRS2.PARTY_ID_TO IS NOT NULL) ";
    private String filterWhereIsTop = " OR (FIL_PRS4.PARTY_ID_TO IS NOT NULL AND (FIL_PA.PARTY_ID IS NULL OR FIL_PA.PARTY_ID <> 'loginPartyId'))"; //" OR (FIL_STATUS.MANAG_WE_STATUS_ENUM_ID = 'TOPMANAGER' AND FIL_PRS4.PARTY_ID_TO IS NOT NULL) ";
    private String filterWherIsRole = " OR (FIL_WEPA.PARTY_ID IS NOT NULL AND (FIL_WEPA.ROLE_TYPE_ID <> 'WEM_EVAL_IN_CHARGE' OR FIL_STATUS.MANAGEMENT_ROLE_TYPE_ID = 'WEM_EVAL_IN_CHARGE' OR FIL_ST.ACT_ST_ENUM_ID = 'ACTSTATUS_CLOSED'))";  //" OR (FIL_STATUS.MANAG_WE_STATUS_ENUM_ID = 'ROLE' AND FIL_WEPA.ROLE_TYPE_ID = FIL_STATUS.MANAGEMENT_ROLE_TYPE_ID AND FIL_WEPA.PARTY_ID IS NOT NULL) ";   
    private String filterUserLogin  = "AND FIL_U.ULVR_USER_LOGIN_ID = ";
    
    private String filterWherIsOrg2 = " OR (FIL_PRM.PARTY_ID_TO IS NOT NULL)"; //" OR (FIL_STATUS.MANAG_WE_STATUS_ENUM_ID = 'ORGMANAGER' AND FIL_PRM.PARTY_ID_TO IS NOT NULL) ";    
    private String filterWhereIsSup2 = " OR (FIL_PRS2.PARTY_ID_TO IS NOT NULL)"; //" OR (FIL_STATUS.MANAG_WE_STATUS_ENUM_ID = 'SUPMANAGER' AND FIL_PRS2.PARTY_ID_TO IS NOT NULL) ";
    private String filterWhereIsTop2 = " OR (FIL_PRS4.PARTY_ID_TO IS NOT NULL)"; //" OR (FIL_STATUS.MANAG_WE_STATUS_ENUM_ID = 'TOPMANAGER' AND FIL_PRS4.PARTY_ID_TO IS NOT NULL) ";
    private String filterWherIsRole2 = " OR (FIL_WEPA.PARTY_ID IS NOT NULL)"; 
    
    private boolean isFullAdmin = false;
    private Security security;
    private String localDispatcherName;
    private GenericValue userLogin;
    
    public UtilFilter(Security security, String userLoginId, String localDispatcherName) {
        try {
            GenericValue userLogin = security.getDelegator().findOne("UserLogin", UtilMisc.toMap("userLoginId", userLoginId), false);
            setFullAdmin(security, userLogin, localDispatcherName);
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
        }
        
    }
    
    public UtilFilter(Security security, GenericValue userLogin, String localDispatcherName) {
        setFullAdmin(security, userLogin, localDispatcherName);
    }
    
    private void setFullAdmin(Security security, GenericValue userLogin, String localDispatcherName) {
        this.security = security;
        this.localDispatcherName = localDispatcherName;
        this.userLogin = userLogin;
        
        String permission = Utils.permissionLocalDispatcherName(localDispatcherName);
        if (security.hasPermission(permission + MGR_ADMIN, userLogin) || Utils.hasSecurityGroup(security, userLogin, FULLADMIN)) {
            isFullAdmin = true;
        }
    }

    /**
     * Ritorna la query utilizzata per i filtri in inner join solo se non sono full admin
     * @return
     */
    public String getWorkEffortFilterInner() {       
        if (isFullAdmin) {
            return "";
        }
        return filterInnerJoin;
    }
    
    
    /**
     * Ritorna la query utilizzata per i filtri in left outer join solo se non sono full admin
     * @return
     */
    public String getWorkEffortFilterLeftJoin() {        
        if (isFullAdmin) {
            return "";
        }
        return filterLeftJoin;
    }
    
    /**
     * Ritorna la query utilizzata per i filtri in where solo se non sono full admin e in base al tipo di permesso
     * @return
     */
    public String getWorkEffortFilterWhere() {
        if (isFullAdmin) {
            return "";
        }
        
        String where = filterUserLogin + "'" + userLogin.getString(E.userLoginId.name()) +"'";
        where += " AND (1 = 0 " ;
        String permission = Utils.permissionLocalDispatcherName(localDispatcherName);
        if (security.hasPermission(permission + ORG_ADMIN, userLogin)) {
        	if (UtilValidate.isNotEmpty(userLogin)) {
        		filterWherIsOrg = filterWherIsOrg.replace("loginPartyId", userLogin.getString(E.partyId.name()));
        	}
            where += filterWherIsOrg;
        }
        
        if (security.hasPermission(permission + ROLE_ADMIN, userLogin)) {
            where += filterWherIsRole;
        }
        
        if (security.hasPermission(permission + SUP_ADMIN, userLogin)) {
        	if (UtilValidate.isNotEmpty(userLogin)) {
        		filterWhereIsSup = filterWhereIsSup.replace("loginPartyId", userLogin.getString(E.partyId.name()));
        	}
            where += filterWhereIsSup;
        }
        
        if (security.hasPermission(permission + TOP_ADMIN, userLogin)) {
        	if (UtilValidate.isNotEmpty(userLogin)) {
        		filterWhereIsTop = filterWhereIsTop.replace("loginPartyId", userLogin.getString(E.partyId.name()));
        	}
            where += filterWhereIsTop;
        }
        
        where +=  ") ";
        
        return  where;
    }
    
    /**
     * ritorna la query in where senza le verifiche su userLoginPartyId
     * @return
     */
    public String getWorkEffortFilterWhereWithoutLoginPartyId() {
        if (isFullAdmin) {
            return "";
        }
        
        String where = filterUserLogin + "'" + userLogin.getString(E.userLoginId.name()) +"'";
        where += " AND (1 = 0 " ;
        String permission = Utils.permissionLocalDispatcherName(localDispatcherName);
        if (security.hasPermission(permission + ORG_ADMIN, userLogin)) {
            where += filterWherIsOrg2;
        }
        
        if (security.hasPermission(permission + ROLE_ADMIN, userLogin)) {
            where += filterWherIsRole2;
        }
        
        if (security.hasPermission(permission + SUP_ADMIN, userLogin)) {
            where += filterWhereIsSup2;
        }
        
        if (security.hasPermission(permission + TOP_ADMIN, userLogin)) {
            where += filterWhereIsTop2;
        }
        
        where +=  ") ";
        
        return  where;
    }
    
    
    public static boolean hasUserProfile(Security security, GenericValue userLogin, String localDispatcherName, String permissionAdmin) {
        String permission = Utils.permissionLocalDispatcherName(localDispatcherName);
        return security.hasPermission(permission + permissionAdmin, userLogin);
    }
    
    

}
