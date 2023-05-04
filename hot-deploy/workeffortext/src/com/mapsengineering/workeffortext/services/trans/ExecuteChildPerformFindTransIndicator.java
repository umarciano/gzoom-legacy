package com.mapsengineering.workeffortext.services.trans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.accountingext.services.InputAndDetailValue;
import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.services.resource.I;

/**
 * ExecuteChildPerformFindTransIndicator
 */
public class ExecuteChildPerformFindTransIndicator extends GenericService {

    public static final String MODULE = ExecuteChildPerformFindTransIndicator.class.getName();
    private static final String SERVICE_NAME = "executePerformFindTransIndicator";
    private static final String SERVICE_TYPE = null;

    protected static final String queryTransIndicator = "sql/transIndicator/queryTransIndicator.sql.ftl";
    
    protected Double kpiScoreWeightTotal = 0d;

    /**
     * Constructor
     */
    public static Map<String, Object> executeChildPerformFindTransIndicator(DispatchContext dctx, Map<String, Object> context) {

        ExecuteChildPerformFindTransIndicator obj = new ExecuteChildPerformFindTransIndicator(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     */
    public ExecuteChildPerformFindTransIndicator(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        userLogin = (GenericValue)context.get(ServiceLogger.USER_LOGIN);
    }

    /**
     * Main loop
     */
    public void mainLoop() {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> rowList = FastList.newInstance();
        long startTime = System.currentTimeMillis();
        int index = 0;
        try {
            JdbcQueryIterator queryTransIndicatorList = new FtlQuery(getDelegator(), queryTransIndicator, mapContextUpdate()).iterate();
            try {
                while (queryTransIndicatorList.hasNext()) {
                    if(index == 0) {
                        long endTime = System.currentTimeMillis();
                        JobLogLog print = new JobLogLog().initLogCode("BaseUiLabels", "EndFtlService", null, getLocale());
                        addLogInfo(print.getLogMessage() + " " + (endTime - startTime) + " milliseconds ", null);
                        index++;
                    }
                    ResultSet ele = queryTransIndicatorList.next();
                    Map<String, Object> genericValue = getGenericValue(ele);
                    Map<String, Object> row = FastMap.newInstance();
                    row.put("gv", genericValue);
                    
                    boolean isReadOnly = getIsReadOnlyIndicatorResponsable(ele);
                    row.put("isReadOnly", isReadOnly);
                    rowList.add(row);
                }

                result.put("rowList", rowList);
                result.put("kpiScoreWeightTotal", kpiScoreWeightTotal);
            } finally {
                queryTransIndicatorList.close();
            }

        } catch (Exception e) {
            String msg = "Error: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnError(e.getMessage()));
        } finally {
            setResult(result);
        }
    }

    protected Map<String, Object> getGenericValue(ResultSet ele) throws SQLException {
        Map<String, Object> genericValue = FastMap.newInstance(); // like WorkEffortTransactionIndicatorView 
        genericValue.put("weTransWeId", getObiettivoId(ele));
        genericValue.put("weTransMeasureId", getMisuraObj(ele));
        genericValue.put("weTransAccountId", getGlAccountId(ele));
        genericValue.put("weTransAccountDesc", ele.getString(WTI.ACCOUNT_NAME.name()));
        genericValue.put("weTransAccountDescLang", ele.getString(WTI.ACCOUNT_NAME_LANG.name()));
        genericValue.put("glDescription", ele.getString(WTI.ACCOUNT_DESCRIPTION.name()));
        genericValue.put("glDescriptionLang", ele.getString(WTI.ACCOUNT_DESCRIPTION_LANG.name()));

        genericValue.put("glResourceTypeId", ele.getString(I.GL_RESOURCE_TYPE_ID.name()));
        genericValue.put("glResourceTypeDesc", ele.getString(I.GL_RESOURCE_TYPE_DESC.name()));
        genericValue.put("glResourceTypeDescLang", ele.getString(I.GL_RESOURCE_TYPE_DESC_LANG.name()));
        
        genericValue.put("weScoreConvEnumId", ele.getString(WTI.WE_SCORE_CONV_ENUM_ID.name()));
        
        genericValue.put("kpiOtherWeight", getKpiOtherWeight(ele));
        genericValue.put("kpiScoreWeight", getKpiScoreWeight(ele));
        kpiScoreWeightTotal += getKpiScoreWeight(ele);

        genericValue.put("weTransCurrencyUomId", ele.getString(WTI.DEFAULT_UOM_ID.name()));
        genericValue.put("weTransDecimalScale", ele.getLong(WTI.DECIMAL_SCALE.name()));
        genericValue.put("weTransUomType", ele.getString(WTI.UOM_TYPE_ID.name()));
        genericValue.put("weTransUomAbb", ele.getString(WTI.ABBREVIATION.name()));
        genericValue.put("weTransUomAbbLang", ele.getString(WTI.ABBREVIATION_LANG.name()));

        genericValue.put("weTransUomDesc", ele.getString(WTI.UOM_DESCR.name()));
        genericValue.put("weTransUomDescLang", ele.getString(WTI.UOM_DESCR_LANG.name()));

        genericValue.put("isPosted", getIsPosted(ele));
        genericValue.put("inputEnumId", getInputEnumId(ele));
        // in realta' la gestione del WEMOMG_ORG non deve essere piu' utilizzata:
        // per sapere se il movimento va memorizzato con l'unita' organizzativa del workeffort, 
        // ci si basa sul valore di detectOrgUnitIdFlag
        genericValue.put("weTransOtherMeasGoal", ele.getString(WTI.WE_OTHER_GOAL_ENUM_ID.name()));
        genericValue.put("detectOrgUnitIdFlag", ele.getString(WTI.DETECT_ORG_UNIT_ID_FLAG.name()));

        genericValue.put("weTransId", ele.getString(WTI.A_ACCTG_TRANS_ID.name()));
        genericValue.put("weTransEntryId", ele.getString(WTI.B_ACCTG_TRANS_ENTRY_SEQ_ID.name()));
        genericValue.put("weTransValue", getTransValue(ele));
        genericValue.put("weTransDate", getTransactionDate(ele));

        // usato nel folder degli Indicatori, quando mostra i movimenti
        genericValue.put("weTransComment", getWeTransComment(ele));
        genericValue.put("weTransCommentLang", getWeTransCommentLang(ele));
        genericValue.put("weTransComments", getWeTransComments(ele));
        genericValue.put("weTransCommentsLang", getWeTransCommentsLang(ele));
        genericValue.put("comments", ele.getString(WTI.COMMENTS.name()));
        genericValue.put("commentsLang", ele.getString(WTI.COMMENTS_LANG.name()));
        genericValue.put("weTransValueCode", getTransValueCode(ele));
        genericValue.put("weTransValueCodeLang", getTransValueCodeLang(ele));
        genericValue.put("customTimePeriodId", getCustomTimePeriodId(ele));

        genericValue.put("weTransTypeValueId", getGlFiscalTypeId(ele));

        genericValue.put("glValModId", ele.getString(WTI.VAL_MOD_ID.name()));
        if (isModelloSuProdotto(ele)) {
            genericValue.put("wmValModId", ele.getString(WTI.WM_VAL_MOD_ID.name()));
            genericValue.put("transProductId", ele.getString(WTI.PRODUCT_ID.name()));
            genericValue.put("voucherRef", ele.getString(WTI.B_VOUCHER_REF.name()));
        }

        genericValue.put("orgUnitId", ele.getString(WTI.ORG_UNIT_ID.name()));
        genericValue.put("orgUnitRoleTypeId", ele.getString(WTI.ORG_UNIT_ROLE_TYPE_ID.name()));
        
        genericValue.put("partyId", ele.getString(WTI.PARTY_ID.name()));
        genericValue.put("roleTypeId", ele.getString(WTI.ROLE_TYPE_ID.name()));
        genericValue.put("entryPartyId", getEntryPartyId(ele));
        genericValue.put("entryRoleTypeId", getEntryRoleTypeId(ele));
        genericValue.put("entryPartyName", getEntryPartyName(ele));
        genericValue.put("gltDescr", ele.getString(WTI.GLT_DESCR.name()));
        
        genericValue.put("dc", ele.getString(WTI.DEBIT_CREDIT_DEFAULT.name()));
        genericValue.put("dcDescr", ele.getString(WTI.DC_DESCR.name()));
        genericValue.put("workEffortTypePeriodId", ele.getString(WTI.WORK_EFFORT_TYPE_PERIOD_ID.name()));
        
        return genericValue;
    }

    protected Double getTransValue(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.B_AMOUNT.name()))) {
            return ele.getDouble(WTI.B_AMOUNT.name());
        }
        return null;
    }

    protected String getTransValueCode(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.UOM_CODE.name()))) {
            return ele.getString(WTI.UOM_CODE.name());
        }
        return null;
    }

    protected String getTransValueCodeLang(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.UOM_CODE_LANG.name()))) {
            return ele.getString(WTI.UOM_CODE_LANG.name());
        }
        return null;
    }

    protected String getCustomTimePeriodId(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.CUSTOM_TIME_PERIOD_ID.name()))) {
            return ele.getString(WTI.CUSTOM_TIME_PERIOD_ID.name());
        }
        return null;
    }

    protected String getWeTransComment(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.B_DESCRIPTION.name()))) {
            return ele.getString(WTI.B_DESCRIPTION.name());
        }
        return null;
    }

    protected String getWeTransCommentLang(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.B_DESCRIPTION_LANG.name()))) {
            return ele.getString(WTI.B_DESCRIPTION_LANG.name());
        }
        return null;
    }

    protected String getWeTransComments(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.A_DESCRIPTION.name()))) {
            return ele.getString(WTI.A_DESCRIPTION.name());
        }
        return null;
    }

    protected String getWeTransCommentsLang(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.A_DESCRIPTION_LANG.name()))) {
            return ele.getString(WTI.A_DESCRIPTION_LANG.name());
        }
        return null;
    }

    protected String getIsPosted(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.IS_POSTED.name()))) {
            return ele.getString(WTI.IS_POSTED.name());
        }
        return "N";
    }

    protected String getGlFiscalTypeId(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getString(WTI.A_GL_FISCAL_TYPE_ID.name()))) {
            return ele.getString(WTI.A_GL_FISCAL_TYPE_ID.name());
        }
        return getGlFiscalTypeId();
    }

    protected boolean getIsReadOnlyIndicatorResponsable(ResultSet ele) throws SQLException {
        if (((isModelloSuObiettivo(ele) || isNonModello(ele)) && "INDICATOR_RESP".equals(ele.getString(WTI.DATA_SOURCE_ID.name()))) || (isModelloSuProdotto(ele) && "INDICATOR_RESP".equals(ele.getString(WTI.WM_DATA_SOURCE_ID.name())))) {

            if (UtilValidate.isNotEmpty(ele.getString(WTI.ORG_UNIT_ROLE_TYPE_ID.name())) && UtilValidate.isNotEmpty(ele.getString(WTI.ORG_UNIT_ID.name())) && (!ele.getString(WTI.ORG_UNIT_ROLE_TYPE_ID.name()).equals(ele.getString(WTI.M_ORG_UNIT_ROLE_TYPE_ID.name())) || !ele.getString(WTI.ORG_UNIT_ID.name()).equals(ele.getString(WTI.M_ORG_UNIT_ID.name())))) {
                return true;
            }

        }
        return false;

    }

    protected boolean isNonModello(ResultSet ele) throws SQLException {
        return InputAndDetailValue.ACCINP_UO.equals(getInputEnumId(ele));
    }

    protected Object getKpiOtherWeight(ResultSet ele) throws SQLException {
        return ele.getDouble(WTI.KPI_OTHER_WEIGHT.name());
    }

    protected Double getKpiScoreWeight(ResultSet ele) throws SQLException {
        return ele.getDouble(WTI.KPI_SCORE_WEIGHT.name());
    }

    protected String getGlAccountId(ResultSet ele) throws SQLException {
        return ele.getString(WTI.GL_ACCOUNT_ID.name());
    }

    protected Timestamp getTransactionDate(ResultSet ele) throws SQLException {
        if (UtilValidate.isNotEmpty(ele.getTimestamp(WTI.A_TRANSACTION_DATE.name()))) {
            return ele.getTimestamp(WTI.A_TRANSACTION_DATE.name());
        }
        return getWeTransDate();
    }

    protected boolean isModelloSuProdotto(ResultSet ele) throws SQLException {
        return InputAndDetailValue.ACCINP_PRD.equals(getInputEnumId(ele));
    }

    protected boolean isModelloSuObiettivo(ResultSet ele) throws SQLException {
        return InputAndDetailValue.ACCINP_OBJ.equals(getInputEnumId(ele));
    }

    protected String getObiettivoId(ResultSet ele) throws SQLException {
        return ele.getString(WTI.WORK_EFFORT_ID.name());
    }

    protected String getMisuraObj(ResultSet ele) throws SQLException {
        return ele.getString(WTI.WORK_EFFORT_MEASURE_ID.name());
    }

    protected String getInputEnumId(ResultSet ele) throws SQLException {
        return ele.getString(WTI.INPUT_ENUM_ID.name());
    }
    
    protected String getEntryPartyId(ResultSet ele) throws SQLException {
        return ele.getString(WTI.ENTRY_PARTY_ID.name());
    }
    
    protected String getEntryRoleTypeId(ResultSet ele) throws SQLException {
        return ele.getString(WTI.ENTRY_ROLE_TYPE_ID.name());
    }

    protected String getEntryPartyName(ResultSet ele) throws SQLException {
        return "";
    }

    protected MapContext<String, Object> mapContextUpdate() throws SQLException, GeneralException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WTI.workEffortId.name(), getWorkEffortId());
        mapContext.put(WTI.contentId.name(), getContentId());
        if (UtilValidate.isNotEmpty(getWorkEffortMeasureId())) {
            mapContext.put(WTI.workEffortMeasureId.name(), getWorkEffortMeasureId());
        }
        if (UtilValidate.isNotEmpty(getGlFiscalTypeId())) {
            mapContext.put(WTI.glFiscalTypeId.name(), getGlFiscalTypeId());
        }
        if (UtilValidate.isNotEmpty(getWeTransDate())) {
            mapContext.put(WTI.weTransDate.name(), getWeTransDate());
        }
        if (UtilValidate.isNotEmpty(getWeTransCurrencyUomId())) {
            mapContext.put(WTI.weTransCurrencyUomId.name(), getWeTransCurrencyUomId());
        }
        if (UtilValidate.isNotEmpty(getOrganizationId())) {
            mapContext.put(WTI.organizationId.name(), getOrganizationId());
        }
        mapContext.put(WTI.mostraMovimenti.name(), true);
        mapContext.put(WTI.onlyWithBudget.name(), false);
        mapContext.put(WTI.accountFilter.name(), getAccountInput());
        mapContext.put(WTI.mostraPossibiliPeriodi.name(), false);
        mapContext.put(WTI.mostraSoggetti.name(), false);
        
        return mapContext;
    }
    
    protected String getContentId() {
        return (String)context.get(WTI.contentId.name());
    }

    protected String getWeTransCurrencyUomId() {
        return (String)context.get(WTI.weTransCurrencyUomId.name());
    }

    protected Timestamp getWeTransDate() {
        return (Timestamp)context.get(WTI.weTransDate.name());
    }

    protected String getGlFiscalTypeId() {
        return (String)context.get(WTI.glFiscalTypeId.name());
    }

    protected String getWorkEffortId() {
        return (String)context.get(WTI.workEffortId.name());
    }

    protected String getWorkEffortMeasureId() {
        return (String)context.get(WTI.workEffortMeasureId.name());
    }
    
    protected String getOrganizationId() {
        return (String)context.get(WTI.organizationId.name());
    }
    
    protected String getAccountInput() {
        return AccountFilterEnum.valueOf((String)context.get(WTI.accountFilter.name())).toString();
    }
    
    protected MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
}
