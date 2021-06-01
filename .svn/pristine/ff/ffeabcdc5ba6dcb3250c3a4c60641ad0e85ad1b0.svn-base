package com.mapsengineering.accountingext.util;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;

import com.mapsengineering.accountingext.services.E;

/**
 * PartyRelationship CustomMethod Calculator Util
 *
 */
public class PartyRelationshipCustomMethodCalculatorUtil extends BaseCustomMethodCalculatorUtil {

    private static final String queryTesteCustomMethodPartyRelationship = "sql/partyRelationship/queryTesteCustomMethod.sql.ftl";
    private static final String queryFteACustomMethodPartyRelationship = "sql/partyRelationship/queryFteACustomMethod.sql.ftl";
    private static final String queryFteMCustomMethodPartyRelationship = "sql/partyRelationship/queryFteMCustomMethod.sql.ftl";

    public static final String ORG_ALLOCATION = "ORG_ALLOCATION";
    public static final String ORG_EMPLOYMENT = "ORG_EMPLOYMENT";
    public static final String APP = "APP";
    public static final String ASS = "ASS";
    public static final String SIPT = "SIPT";
    public static final String NOPT = "NOPT";
    public static final String FTEAA = "FTEAA";
    public static final String FTEMM = "FTEMM";
    public static final String TESTE = "TESTE";

    /**
     * Constructor
     * @param delegator
     * @param context
     */
    public PartyRelationshipCustomMethodCalculatorUtil(Delegator delegator, Map<String, ? extends Object> context) {
        super(delegator, context);
    }

    /**
     * return map with parameters for query
     * @param glAccountId
     * @param refDate
     * @param customMethodName
     * @param context
     * @return
     * @throws GenericEntityException
     * @throws SQLException
     * @throws GenericServiceException
     */
    public MapContext<String, Object> mapContextUpdate(String glAccountId, Timestamp refDate, String customMethodName, Map<String, ? extends Object> context) throws GenericEntityException, SQLException, GenericServiceException {
        String workEffortId = (String)context.get(E.workEffortId.name());
        boolean isFinalPeriod = getIsFinalPeriod(customMethodName);
        boolean isPartTime = getIsPartTime(customMethodName);

        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(PartyRelationshipFieldEnum.isPartTime.name(), isPartTime);
        mapContext.put(PartyRelationshipFieldEnum.isFinalPeriod.name(), isFinalPeriod);
        mapContext.put(PartyRelationshipFieldEnum.glAccountId.name(), glAccountId);
        mapContext.putAll(getPartyAndRoleFromWorkEffort(workEffortId));
        mapContext.put(PartyRelationshipFieldEnum.refDate.name(), refDate);
        mapContext.put(PartyRelationshipFieldEnum.partyRelationTypeId.name(), getPartyRelationTypeId(customMethodName));
        return mapContext;
    }

    private boolean getIsPartTime(String customMethodName) throws GenericServiceException {
        if (customMethodName.indexOf(SIPT) > 0) {
            return true;
        }
        if (customMethodName.indexOf(NOPT) > 0) {
            return false;
        }
        throw new GenericServiceException("the customMethodName (" + customMethodName + ") MUST contains SIPT  or NOPT");
    }

    /**
     * Return one of different query
     * @param customMethodName
     * @return
     */
    public String getQuery(String customMethodName) {
        if (customMethodName.indexOf(FTEAA) > 0) {
            return queryFteACustomMethodPartyRelationship;
        }
        if (customMethodName.indexOf(FTEMM) > 0) {
            return queryFteMCustomMethodPartyRelationship;
        }
        if (customMethodName.indexOf(TESTE) > 0) {
            return queryTesteCustomMethodPartyRelationship;
        }
        return null;
    }

    private Map<? extends String, ? extends Object> getPartyAndRoleFromWorkEffort(String workEffortId) throws GenericEntityException {
        Map<String, Object> mappa = FastMap.newInstance();
        if (UtilValidate.isNotEmpty(workEffortId)) {
            GenericValue workEffort = getDelegator().findOne("WorkEffort", UtilMisc.toMap("workEffortId", workEffortId), false);
            mappa.put("orgUnitId", workEffort.get("orgUnitId"));
            mappa.put("orgUnitRoleTypeId", workEffort.get("orgUnitRoleTypeId"));
        }
        return mappa;
    }

    /**
     * Check wether if customMethodName end with F.<br/>
     *  - F = fine periodo<br/>
     *  - M = media periodo<br/>
     * @throws GenericServiceException 
     */
    private boolean getIsFinalPeriod(String customMethodName) throws GenericServiceException {
        String customMethodNameSub = customMethodName.substring(0, customMethodName.indexOf('('));
        String customMethodNameTrim = customMethodNameSub.trim();
        if (customMethodNameTrim.endsWith("F")) {
            return true;
        }
        if (customMethodNameTrim.endsWith("M")) {
            return false;
        }
        throw new GenericServiceException("the customMethodName (" + customMethodName + ") MUST end with F or M");
    }

    private String getPartyRelationTypeId(String customMethodName) throws GenericServiceException {
        if (customMethodName.indexOf(PartyRelationshipCustomMethodCalculatorUtil.APP) > -1) {
            return PartyRelationshipCustomMethodCalculatorUtil.ORG_EMPLOYMENT;
        }
        if (customMethodName.indexOf(PartyRelationshipCustomMethodCalculatorUtil.ASS) > -1) {
            return PartyRelationshipCustomMethodCalculatorUtil.ORG_ALLOCATION;
        }
        throw new GenericServiceException("the customMethodName (" + customMethodName + ") MUST contains APP or ASS");
    }
}
