package com.mapsengineering.workeffortext.services.trans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;

/**
 * CheckHasMandatoryTransEmptyService
 */
public class ExecuteChildPerformFindTransGroupIndicator extends ExecuteChildPerformFindTransIndicator {

    public static final String MODULE = ExecuteChildPerformFindTransGroupIndicator.class.getName();
    public static final String KEY_MEASURE_ID = "weTransMeasureId";
    public static final String KEY_ENTRY_PARTY_ID = "entryPartyId";
    public static final String KEY_FISCAL_ID = "weTransTypeValueId";
    public static final String LIST = "measureList";
    public static final String KPI_SCORE_WEIGHT_TOTAL = "kpiScoreWeightTotal";
    public static final String ROW_LIST = "rowList";

    protected static final String SERVICE_NAME = "executePerformFindTransGroupIndicator";

    /**
     * CheckHasMandatoryTransEmptyService
     */
    public static Map<String, Object> executeChildPerformFindTransGroupIndicator(DispatchContext dctx, Map<String, Object> context) {

        ExecuteChildPerformFindTransGroupIndicator obj = new ExecuteChildPerformFindTransGroupIndicator(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public ExecuteChildPerformFindTransGroupIndicator(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context);
    }

    /**
     * Main loop
     */
    public void mainLoop() {
        List<Map<String, Object>> measureList = FastList.newInstance();
        long startTime = System.currentTimeMillis();
        int index = 0;
        try {
            JdbcQueryIterator queryTransIndicatorList = new FtlQuery(getDelegator(), queryTransIndicator, mapContextUpdate()).iterate();
            try {
                String weTransMeasureId = null;
                String entryPartyId = null;
                String glFiscalTypeId = null;
                while (queryTransIndicatorList.hasNext()) {
                    if(index == 0) {
                        long endTime = System.currentTimeMillis();
                        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                        addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                        index++;
                    }
                    ResultSet ele = queryTransIndicatorList.next();
                    
                    weTransMeasureId = getMisuraObj(ele);
                    entryPartyId = getEntryPartyId(ele);
                    glFiscalTypeId = getGlFiscalTypeId(ele);

                    // questo e' l'elemento
                    Map<String, Object> row = getGenericValueExt(ele);
                    boolean isReadOnly = getIsReadOnlyIndicatorResponsable(ele);
                    row.put("isReadOnly", isReadOnly);
                    boolean hasComments = hasComments(ele);
                    row.put("hasComments", hasComments);

                    // !mostraMovimenti "ONE".equals(getShowDetail()) && UtilValidate.isEmpty(getWorkEffortMeasureId())
                    if (!getMostraMovimenti()) {
                        // add to glFiscalTypeList
                       List<Map<String, Object>> lista = UtilMisc.toList(row);
                       
                       Map<String, Object> glFiscalTypeMap = UtilMisc.toMap(KEY_FISCAL_ID, glFiscalTypeId, ROW_LIST, lista);
                       List<Map<String, Object>> glFiscalTypeList = UtilMisc.toList(glFiscalTypeMap);

                       Map<String, Object> entryPartyMap = UtilMisc.toMap(KEY_ENTRY_PARTY_ID, entryPartyId, ROW_LIST, glFiscalTypeList);
                       List<Map<String, Object>> entryPartyList = UtilMisc.toList(entryPartyMap);
                       
                       // add to measureList
                       Map<String, Object> measureMap = UtilMisc.toMap(KEY_MEASURE_ID, weTransMeasureId, ROW_LIST, entryPartyList);
                       if (UtilValidate.isEmpty(measureList)) {
                           measureList = UtilMisc.toList(measureMap);
                       } else {
                           measureList.add(measureMap);
                       }
                   } else {
                       // row va nella lista transList
                       // transList e' la lista assegnata ad un entryPartyId
                        // entryPartyId va nella lista di pippo
                        // pippo e' la lista assegnata ad un weTransTypeValueId
                        // weTransTypeValueId va nella lista di measureList
                        // measureList e' la lista assegnata ad un weTransMeasureId
                        boolean foundMeasureList = false;
                        for (Map<String, Object> measureMap : measureList) {
                            // a parita di misura
                            if (weTransMeasureId.equals(measureMap.get(KEY_MEASURE_ID))) {
                                foundMeasureList = true;
                                
                                
                                // otteng la lista per quella misura
                                List<Map<String, Object>> entryPartyList = (List<Map<String, Object>>)measureMap.get(ROW_LIST);
                                boolean foundEntryPartyList = false;
                                // per ogn ioggett odella lista
                                for (Map<String, Object> entryPartyMap : entryPartyList) {
                                    // se il soggetto e lo stesso lo aggiungo,
                                    // valuto il tipo valore
                                    if (entryPartyId.equals(entryPartyMap.get(KEY_ENTRY_PARTY_ID))) {
                                        foundEntryPartyList = true;
                                        
                                        // otteng la lista per quella misura
                                        List<Map<String, Object>> glFiscalTypeList = (List<Map<String, Object>>)entryPartyMap.get(ROW_LIST);
                                        boolean foundGlFiscalTypeList = false;
                                        // per ogn ioggett odella lista
                                        for (Map<String, Object> glFiscalTypeMap : glFiscalTypeList) {
                                            // se il tipo valore e lo stesso lo aggiungo
                                            if (glFiscalTypeId.equals(glFiscalTypeMap.get(KEY_FISCAL_ID))) {
                                                foundGlFiscalTypeList = true;
                                                UtilMisc.addToListInMap(row, glFiscalTypeMap, ROW_LIST);
                                            }
                                        }
                                        if (!foundGlFiscalTypeList) {
                                            // add to glFiscalTypeList
                                            List<Map<String, Object>> lista = UtilMisc.toList(row);
                                            Map<String, Object> glFiscalTypeMap = UtilMisc.toMap(KEY_FISCAL_ID, glFiscalTypeId, ROW_LIST, lista);
                                            glFiscalTypeList.add(glFiscalTypeMap);
                                        }
                                        
                                        // UtilMisc.addToListInMap(row, entryPartyMap, ROW_LIST);
                                    }
                                }
                                if (!foundEntryPartyList) {
                                    // add to glFiscalTypeList
                                    List<Map<String, Object>> lista = UtilMisc.toList(row);
                                    
                                    Map<String, Object> glFiscalTypeMap = UtilMisc.toMap(KEY_FISCAL_ID, glFiscalTypeId, ROW_LIST, lista);
                                    List<Map<String, Object>> glFiscalTypeList = UtilMisc.toList(glFiscalTypeMap);

                                    Map<String, Object> entryPartyMap = UtilMisc.toMap(KEY_ENTRY_PARTY_ID, entryPartyId, ROW_LIST, glFiscalTypeList);
                                    // List<Map<String, Object>> entryPartyList = UtilMisc.toList(entryPartyMap);
                                    entryPartyList.add(entryPartyMap);
                                }
                             }
                        }
                        if (!foundMeasureList) {
                            // add to glFiscalTypeList
                            List<Map<String, Object>> lista = UtilMisc.toList(row);
                            
                            Map<String, Object> glFiscalTypeMap = UtilMisc.toMap(KEY_FISCAL_ID, glFiscalTypeId, ROW_LIST, lista);
                            List<Map<String, Object>> glFiscalTypeList = UtilMisc.toList(glFiscalTypeMap);

                            Map<String, Object> entryPartyMap = UtilMisc.toMap(KEY_ENTRY_PARTY_ID, entryPartyId, ROW_LIST, glFiscalTypeList);
                            List<Map<String, Object>> entryPartyList = UtilMisc.toList(entryPartyMap);
                            
                            // add to measureList
                            Map<String, Object> measureMap = UtilMisc.toMap(KEY_MEASURE_ID, weTransMeasureId, ROW_LIST, entryPartyList);
                            if (UtilValidate.isEmpty(measureList)) {
                                measureList = UtilMisc.toList(measureMap);
                            } else {
                                measureList.add(measureMap);
                            }

                        }
                    }
                    
                }

                getResult().put(LIST, measureList);
                getResult().put(KPI_SCORE_WEIGHT_TOTAL, kpiScoreWeightTotal);
            } finally {
                queryTransIndicatorList.close();
            }

        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            setResult(getResult());
        }
    }

    private boolean hasComments(ResultSet ele) throws SQLException {
        return (UtilValidate.isNotEmpty(getWeTransComment(ele)) && UtilValidate.isNotEmpty(getWeTransComment(ele).trim())) || (UtilValidate.isNotEmpty(getWeTransComments(ele)) && UtilValidate.isNotEmpty(getWeTransComments(ele).trim())) || (UtilValidate.isNotEmpty(getWeTransCommentLang(ele)) && UtilValidate.isNotEmpty(getWeTransCommentLang(ele).trim())) || (UtilValidate.isNotEmpty(getWeTransCommentsLang(ele)) && UtilValidate.isNotEmpty(getWeTransCommentsLang(ele).trim())) ? true : false;
    }

    private Map<String, Object> getGenericValueExt(ResultSet ele) throws SQLException {
        Map<String, Object> genericValue = getGenericValue(ele);
        genericValue.put("sequenceId", ele.getLong(WTI.SEQUENCE_ID.name()));
        genericValue.put("weTransTypeValueDesc", getGlFiscalTypeDesc(ele));
        genericValue.put("weTransTypeValueDescLang", getGlFiscalTypeDescLang(ele));
        genericValue.put("glFiscalTypeEnumId", getFiscalTypeEnumId(ele));

        genericValue.put("fromDate", ele.getTimestamp(WTI.FROM_DATE.name()));
        genericValue.put("thruDate", ele.getTimestamp(WTI.THRU_DATE.name()));
        
        genericValue.put("customTimePeriodFromDate", ele.getTimestamp(WTI.P_FROM_DATE.name()));
        genericValue.put("customTimePeriodThruDate", ele.getTimestamp(WTI.P_THRU_DATE.name()));
        
        if (getOnlyWithBudget()) {
            if ("ACTUAL".equals(genericValue.get(WTI.weTransTypeValueId.name())) && !"WECONVER_NOCONVERSIO".equals(ele.getString(WTI.WE_SCORE_CONV_ENUM_ID.name()))) {
                if (UtilValidate.isNotEmpty(ele.getString(WTI.BUDGET_AMOUNT.name()))) {
                    genericValue.put(WTI.hasMandatoryBudgetEmpty.name(), true);
                }
            }
        }
        
        return genericValue;
    }

    protected String getFiscalTypeEnumId(ResultSet ele) throws SQLException {
        if(getMostraMovimenti()) {
            return ele.getString(WTI.GT_GL_FISCAL_TYPE_ENUM_ID.name());
        }
        return "";
    }

    protected String getGlFiscalTypeId(ResultSet ele) throws SQLException {
        if(getMostraMovimenti()) {
            if (UtilValidate.isNotEmpty(ele.getString(WTI.A_GL_FISCAL_TYPE_ID.name()))) {
                return ele.getString(WTI.A_GL_FISCAL_TYPE_ID.name());
            } else if (UtilValidate.isNotEmpty(getGlFiscalTypeId())) {
                return getGlFiscalTypeId();
            }
            return ele.getString(WTI.GT_GL_FISCAL_TYPE_ID.name());
        }
        return "";
    }
    
    protected String getGlFiscalTypeDesc(ResultSet ele) throws SQLException {
        if(getMostraMovimenti()) {
            if (UtilValidate.isNotEmpty(ele.getString(WTI.C_DESCRIPTION.name()))) {
                return ele.getString(WTI.C_DESCRIPTION.name());
            } else if (UtilValidate.isNotEmpty(getGlFiscalTypeId())) {
                return null;
            }
            return ele.getString(WTI.GT_DESCRIPTION.name());
        }
        return "";
    }
    
    protected String getGlFiscalTypeDescLang(ResultSet ele) throws SQLException {
        if(getMostraMovimenti()) {
            if (UtilValidate.isNotEmpty(ele.getString(WTI.C_DESCRIPTION_LANG.name()))) {
                return ele.getString(WTI.C_DESCRIPTION_LANG.name());
            } else if (UtilValidate.isNotEmpty(getGlFiscalTypeId())) {
                return null;
            }
            return ele.getString(WTI.GT_DESCRIPTION_LANG.name());
        }
        return "";
    }

    protected Timestamp getTransactionDate(ResultSet ele) throws SQLException {
        if (getMostraMovimenti() && UtilValidate.isNotEmpty(ele.getString(WTI.A_TRANSACTION_DATE.name()))) {
            return ele.getTimestamp(WTI.A_TRANSACTION_DATE.name());
        }
        return null;
    }
    
    protected String getEntryPartyId(ResultSet ele) throws SQLException {
        if (getMostraSoggetti() && UtilValidate.isNotEmpty(ele.getString(WTI.ENTRY_PARTY_ID.name()))) {
            return ele.getString(WTI.ENTRY_PARTY_ID.name());
        } else if (getMostraSoggetti() && UtilValidate.isNotEmpty(ele.getString(WTI.GR_PARTY_ID.name()))) {
            return ele.getString(WTI.GR_PARTY_ID.name());
        }
        return "";
    }
    
    protected String getEntryRoleTypeId(ResultSet ele) throws SQLException {
        if (getMostraSoggetti() && UtilValidate.isNotEmpty(ele.getString(WTI.ENTRY_ROLE_TYPE_ID.name()))) {
            return ele.getString(WTI.ENTRY_ROLE_TYPE_ID.name());
        } else if (getMostraSoggetti() && UtilValidate.isNotEmpty(ele.getString(WTI.GR_ROLE_TYPE_ID.name()))) {
            return ele.getString(WTI.GR_ROLE_TYPE_ID.name());
        }
        return "";
    }

    protected String getEntryPartyName(ResultSet ele) throws SQLException {
        if(getMostraSoggetti() && UtilValidate.isNotEmpty(ele.getString(WTI.PARTY_NAME.name()))) {
            return ele.getString(WTI.PARTY_NAME.name());
        }
        return "";
    }
    
    protected String getIsPosted(ResultSet ele) throws SQLException {
        if(getMostraMovimenti() && UtilValidate.isNotEmpty(ele.getString(WTI.IS_POSTED.name()))) {
            return ele.getString(WTI.IS_POSTED.name());
        }
        return "N";
    }

    protected MapContext<String, Object> mapContextUpdate() throws SQLException, GeneralException {
        MapContext<String, Object> mapContext = this.mapContext();
        
        if (UtilValidate.isNotEmpty(getWorkEffortMeasureId())) {
            mapContext.put(WTI.workEffortMeasureId.name(), getWorkEffortMeasureId());
        }
        if (UtilValidate.isNotEmpty(getCustomTimePeriodIdList())) {
            mapContext.put(WTI.customTimePeriodIdList.name(), getCustomTimePeriodIdList());
        }
        if (UtilValidate.isNotEmpty(getGlFiscalTypeId())) {
            mapContext.put(WTI.glFiscalTypeId.name(), getGlFiscalTypeId());
        }
        if (UtilValidate.isNotEmpty(getWeTransCurrencyUomId())) {
            mapContext.put(WTI.weTransCurrencyUomId.name(), getWeTransCurrencyUomId());
        }
        
    	FromAndThruDatesProviderFromParams fromAndThruDatesProvider = new FromAndThruDatesProviderFromParams(context, context, delegator, false);
    	fromAndThruDatesProvider.run();
        if (UtilValidate.isNotEmpty(fromAndThruDatesProvider.getThruDate())) {
        	mapContext.put(WTI.fromDate.name(), fromAndThruDatesProvider.getThruDate());
        } else if (UtilValidate.isNotEmpty(getFirstCustomTimePeriodFromDate())) {
            mapContext.put(WTI.fromDate.name(), getFirstCustomTimePeriodFromDate());
        }
        if (UtilValidate.isNotEmpty(fromAndThruDatesProvider.getFromDate())) {
        	mapContext.put(WTI.thruDate.name(), fromAndThruDatesProvider.getFromDate());
        } else if (UtilValidate.isNotEmpty(getLastCustomTimePeriodThruDate())) {
            mapContext.put(WTI.thruDate.name(), getLastCustomTimePeriodThruDate());
        }
        
        mapContext.put(WTI.workEffortId.name(), getWorkEffortId());
        mapContext.put(WTI.contentId.name(), getContentId());
        mapContext.put(WTI.showDetail.name(), getShowDetail());

        mapContext.put(WTI.onlyWithBudget.name(), getOnlyWithBudget());
        mapContext.put(WTI.accountFilter.name(), getAccountInput());
        mapContext.put(WTI.mostraMovimenti.name(), getMostraMovimenti());
        mapContext.put(WTI.mostraSoggetti.name(), getMostraSoggetti());
        mapContext.put(WTI.mostraPossibiliPeriodi.name(), true);

        return mapContext;
    }
    
    private Timestamp getLastCustomTimePeriodThruDate() {
        return (Timestamp)context.get(WTI.lastCustomTimePeriodThruDate.name());
    }

    private Timestamp getFirstCustomTimePeriodFromDate() {
        return (Timestamp)context.get(WTI.firstCustomTimePeriodFromDate.name());
    }

    private boolean getMostraSoggetti() {
        return "ALL" == getShowDetail() || ("ONE" == getShowDetail() && UtilValidate.isNotEmpty(getWorkEffortMeasureId()));
    }

    private boolean getMostraMovimenti() {
        return "ONE" != getShowDetail() || UtilValidate.isNotEmpty(getWorkEffortMeasureId());
    }
    
    private boolean getOnlyWithBudget() {
        return "Y".equals((String)context.get(WTI.onlyWithBudget.name())) && UtilValidate.isEmpty(getGlFiscalTypeId());
    }

    protected String getShowDetail() {
        return (String)context.get(WTI.showDetail.name());
    }

    @SuppressWarnings("unchecked")
    private List<String> getCustomTimePeriodIdList() {
        return (List<String>)context.get(WTI.customTimePeriodIdList.name());
    }
}
