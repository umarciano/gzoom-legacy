package com.mapsengineering.base.standardimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.GlAccountOtherFieldEnum;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.WeBaseDateInterfaceTakeOverService;
import com.mapsengineering.base.standardimport.common.WeInterfaceConstants;
import com.mapsengineering.base.standardimport.common.WeMeasureTypeLookup;
import com.mapsengineering.base.standardimport.common.WeMeasureTypeLookup.Entry;
import com.mapsengineering.base.standardimport.helper.WeGlAccountInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeMeasureInterfaceHelper;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Elaborate measure for workEffort
 * @author dain
 *
 */
public class WeMeasureInterfaceTakeOverService extends WeBaseDateInterfaceTakeOverService {

    public static final String MODULE = WeMeasureInterfaceTakeOverService.class.getName();

    private WeGlAccountInterfaceHelper weGlAccountInterfaceHelper;

    private Timestamp fromDate;
    private Timestamp thruDate;

    private String workEffortMeasureId;

    private String wePurposeTypeIdRes;
    private String wePurposeTypeIdInd;

    private String measureTypeIndRes;

    private String glAccountId;

    private String weScoreRangeEnumId;
    private String weScoreConvEnumId;
    private String weOtherGoalEnumId;
    private String weAlertRuleEnumId;
    private String weWithoutPerf;
    private String weMeasureTypeEnumId;
    private String uomDescr;
    private Long sequenceId;

    private String uomRangeId;

    private String periodTypeId;

    @Override
    /** Set localValue con record locale presente sul db or null in caso di nuovo inserimento. 
     * Recupera tutti i campi da externalValue, record sulla tabella di interfaccia
     * @params extLogicKey chiave logica esterna
     *  */
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        super.initLocalValue(extLogicKey);
        weGlAccountInterfaceHelper = new WeGlAccountInterfaceHelper(this, getManager().getDelegator());

        checkValidityGlAccount();
        checkValidityWorkEffortMeasureCode();

        checkValidityWeMeasureTypeDesc();

        checkValidityWorkEffortPurposeAccount();
        getkWorkEffortMeasure();
    }

    @Override
    /**
     * Esegue importazione record esterno
     */
    public void doImport() throws GeneralException {
    	setImported(true);
        String msg;

        checkValidityDate();

        createOrUpdateWorkEffortMeasure();

        updateWeMeasureInterface();

        msg = "END IMPORT " + workEffortMeasureId;
        addLogInfo(msg);
    }

    /**
     * Recupera weMeasureTypeEnumId e measureTypeIndRes, utili per cercare i workEffortPurposeTypeId validi
     * @throws GeneralException
     */
    private void checkValidityWeMeasureTypeDesc() throws GeneralException {
        GenericValue gv = getExternalValue();

        String weMeasureTypeDescStr = gv.getString(E.weMeasureTypeDesc.name());
        if (ValidationUtil.isEmptyOrNA(weMeasureTypeDescStr)) {
            weMeasureTypeDescStr = "Prestazione (KPI)";
        }
        Entry entryMap = WeMeasureTypeLookup.get(weMeasureTypeDescStr);
        if (UtilValidate.isEmpty(entryMap)){
            String msg = "The field weMeasureTypeDesc is not valid";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        weMeasureTypeEnumId = entryMap.getEnumId();
        measureTypeIndRes = entryMap.getEnumIndRes();
    }

    private void checkValidityDate() throws GeneralException {
        GenericValue gv = getExternalValue();

        fromDate = gv.getTimestamp(E.fromDate.name());
        thruDate = gv.getTimestamp(E.thruDate.name());

        if (UtilValidate.isEmpty(fromDate) && UtilValidate.isEmpty(thruDate)) {
            return;
        } else if (UtilValidate.isEmpty(fromDate) || UtilValidate.isEmpty(thruDate)) {
            String msg = E.fromDate.name() + " and " + E.thruDate.name() + FindUtilService.MSG_BOTH_NOT_NULL;
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        if (fromDate.after(thruDate)) {
            String msg = "The field fromDate must be greater or equals than thruDate";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

    }

    private void checkValidityGlAccount() throws ImportException, GeneralException {
        GenericValue externalValue = getExternalValue();

        GenericValue glAccount = weGlAccountInterfaceHelper.getGlAccount(externalValue.getString(E.accountCode.name()), externalValue.getString(E.accountName.name()));
        glAccountId = glAccount.getString(E.glAccountId.name());
        
        setOtherFieldFromGlAccount(glAccount);
    }

    private void setOtherFieldFromGlAccount(GenericValue glAccount) throws ImportException, GenericEntityException {
        GenericValue externalValue = getExternalValue();

        weOtherGoalEnumId = getWeOtherGoalEnumId(glAccount);
        
        EntityCondition entityConditionDesc = EntityCondition.makeCondition(E.description.name(), externalValue.getString(E.weScoreRangeEnumId.name()));
        EntityCondition entityConditionType = EntityCondition.makeCondition(E.enumTypeId.name(), GlAccountOtherFieldEnum.weScoreRangeEnumId.enumTypeId());
        weScoreRangeEnumId = getIdFromDescription(E.Enumeration.name(), EntityCondition.makeCondition(entityConditionDesc, entityConditionType), externalValue.getString(E.weScoreRangeEnumId.name()), E.enumId.name(), externalValue.getString(E.weScoreRangeEnumId.name()) + " for field weScoreRangeEnumId is not valid", glAccount.getString(E.weScoreRangeEnumId.name()));
        
        entityConditionDesc = EntityCondition.makeCondition(E.description.name(), externalValue.getString(E.weScoreConvEnumId.name()));
        entityConditionType = EntityCondition.makeCondition(E.enumTypeId.name(), GlAccountOtherFieldEnum.weScoreConvEnumId.enumTypeId());
        weScoreConvEnumId = getIdFromDescription(E.Enumeration.name(), EntityCondition.makeCondition(entityConditionDesc, entityConditionType), externalValue.getString(E.weScoreConvEnumId.name()), E.enumId.name(), externalValue.getString(E.weScoreConvEnumId.name()) + " for field weScoreConvEnumId is not valid", glAccount.getString(E.weScoreConvEnumId.name()));
        
        entityConditionDesc = EntityCondition.makeCondition(E.description.name(), externalValue.getString(E.weAlertRuleEnumId.name()));
        entityConditionType = EntityCondition.makeCondition(E.enumTypeId.name(), GlAccountOtherFieldEnum.weAlertRuleEnumId.enumTypeId());
        weAlertRuleEnumId = getIdFromDescription(E.Enumeration.name(), EntityCondition.makeCondition(entityConditionDesc, entityConditionType), externalValue.getString(E.weAlertRuleEnumId.name()), E.enumId.name(), externalValue.getString(E.weAlertRuleEnumId.name()) + " for field weAlertRuleEnumId is not valid", glAccount.getString(E.weAlertRuleEnumId.name()));
        
        entityConditionDesc = EntityCondition.makeCondition(E.description.name(), externalValue.getString(E.weWithoutPerf.name()));
        entityConditionType = EntityCondition.makeCondition(E.enumTypeId.name(), GlAccountOtherFieldEnum.weWithoutPerf.enumTypeId());
        weWithoutPerf = getIdFromDescription(E.Enumeration.name(), EntityCondition.makeCondition(entityConditionDesc, entityConditionType), externalValue.getString(E.weWithoutPerf.name()), E.enumId.name(), externalValue.getString(E.weWithoutPerf.name()) + " for field weWithoutPerf is not valid", glAccount.getString(E.weWithoutPerf.name()));
        
        periodTypeId = getIdFromDescription(E.PeriodType.name(), EntityCondition.makeCondition(E.description.name(), externalValue.getString(E.periodTypeDesc.name())), externalValue.getString(E.periodTypeDesc.name()), E.periodTypeId.name(), externalValue.getString(E.periodTypeDesc.name()) + " for field periodTypeDesc is not valid", glAccount.getString(E.periodTypeId.name()));
        uomRangeId = getIdFromDescription(E.UomRange.name(), EntityCondition.makeCondition(E.description.name(), externalValue.getString(E.uomRangeDesc.name())), externalValue.getString(E.uomRangeDesc.name()), E.uomRangeId.name(), externalValue.getString(E.uomRangeDesc.name()) + " for field uomRangeDesc is not valid", glAccount.getString(E.uomRangeId.name()));
    }

    private String getIdFromDescription(String entityNameToSearch, EntityCondition entityCondition, String fieldValue, String fieldNameId, String errorMessage, String defaultId) throws ImportException, GenericEntityException {
        GenericValue externalValue = getExternalValue();
        if(UtilValidate.isEmpty(fieldValue)) {
            return defaultId;
        }
        
        List<GenericValue> uomRangeList = getManager().getDelegator().findList(entityNameToSearch, entityCondition, null, null, null, false);
        GenericValue uomRange = EntityUtil.getFirst(uomRangeList);
        if (UtilValidate.isEmpty(uomRange)){
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), errorMessage);
        }
        return uomRange.getString(fieldNameId);
    }

    private void checkValidityWorkEffortPurposeAccount() throws GeneralException {
        weGlAccountInterfaceHelper.checkWorkEffortPurposeAccount(glAccountId, measureTypeIndRes, getWorkEffortTypeLevelId(), wePurposeTypeIdInd, wePurposeTypeIdRes);
    }

    private void checkValidityWorkEffortMeasureCode() throws GeneralException {
        GenericValue externalValue = getExternalValue();

        WeMeasureInterfaceHelper weMeasureInterfaceHelper = new WeMeasureInterfaceHelper(this, getManager().getDelegator());
        String workEffortMeasureCode = externalValue.getString(E.workEffortMeasureCode.name());

        uomDescr = externalValue.getString(E.uomDescr.name());
        GenericValue workEffortMeasure = weMeasureInterfaceHelper.getWorkEffortMeasure(workEffortMeasureCode, getWorkEffortRootId(), getWorkEffortLevelId(), glAccountId, uomDescr, getOperationType());
        setLocalValue(workEffortMeasure);

    }

    private void getkWorkEffortMeasure() throws GeneralException {
        String msg = "";
        if (UtilValidate.isNotEmpty(getLocalValue())) {
            workEffortMeasureId = getLocalValue().getString(E.workEffortMeasureId.name());
            msg = "Found workEffortMeasure with id " + workEffortMeasureId;
            addLogInfo(msg);
        } else {
            GenericValue externalValue = getExternalValue();
            String workEffortMeasureCode = externalValue.getString(E.workEffortMeasureCode.name());

            if (!ValidationUtil.isEmptyOrNA(workEffortMeasureCode)) {
                workEffortMeasureId = workEffortMeasureCode;
            } else {
                workEffortMeasureId = WeInterfaceConstants.WORK_EFFORT_MEASURE_PREFIX + getManager().getDelegator().getNextSeqId(E.WorkEffortMeasure.name());
            }
            msg = "Try to create WorkEffortMeasure with id " + workEffortMeasureId;
            addLogInfo(msg);
        }
    }

    private void updateWeMeasureInterface() {
        getExternalValue().set(E.workEffortTypeIdOut.name(), getWorkEffortTypeLevelId());
        getExternalValue().set(E.workEffortId.name(), getWorkEffortLevelId());
        getExternalValue().set(E.workEffortMeasureId.name(), workEffortMeasureId);
        getExternalValue().set(E.workEffortRootId.name(), getWorkEffortRootId());
        getExternalValue().set(E.glAccountId.name(), glAccountId);
        getExternalValue().set(E.weMeasureTypeEnumId.name(), weMeasureTypeEnumId);
        getExternalValue().set(E.elabResult.name(), ImportManager.RECORD_ELAB_RESULT_OK);
    }

    private void createOrUpdateWorkEffortMeasure() throws GeneralException {
        GenericValue we = getLocalValue();

        String msg = "";

        Map<String, Object> result = null;
        Map<String, Object> serviceMapParams = setServiceMapParameters();

        if (UtilValidate.isEmpty(we)) {
            serviceMapParams.put(E.weScoreRangeEnumId.name(), weScoreRangeEnumId);
            serviceMapParams.put(E.weScoreConvEnumId.name(), weScoreConvEnumId);
            serviceMapParams.put(E.weAlertRuleEnumId.name(), weAlertRuleEnumId);
            serviceMapParams.put(E.weOtherGoalEnumId.name(), weOtherGoalEnumId);
            serviceMapParams.put(E.weWithoutPerf.name(), weWithoutPerf);
            serviceMapParams.put(E.periodTypeId.name(), periodTypeId);

            if (UtilValidate.isNotEmpty(uomRangeId)) {
                serviceMapParams.put(E.uomRangeId.name(), uomRangeId);
            }

            if (UtilValidate.isNotEmpty(fromDate)) {
                serviceMapParams.put(E.fromDate.name(), fromDate);
            } else {
                serviceMapParams.put(E.fromDate.name(), getEstimatedStartDate());
            }
            if (UtilValidate.isNotEmpty(thruDate)) {
                serviceMapParams.put(E.thruDate.name(), thruDate);
            } else {
                serviceMapParams.put(E.thruDate.name(), getEstimatedCompletionDate());
            }
            if (UtilValidate.isEmpty(sequenceId)) {
                serviceMapParams.put(E.sequenceId.name(), 1L);
            }

            // Creazione WorkEffortRoot
            msg = "Trying to create workEffortMeasure with " + serviceMapParams;
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortMeasure.name(), E.WorkEffortMeasure.name(), CrudEvents.OP_CREATE, serviceMapParams, E.WorkEffortMeasure.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortMeasure.name(), true, false);

        } else {
            if (UtilValidate.isNotEmpty(fromDate)) {
                serviceMapParams.put(E.fromDate.name(), fromDate);
            }
            if (UtilValidate.isNotEmpty(thruDate)) {
                serviceMapParams.put(E.thruDate.name(), thruDate);
            }

            msg = "Trying to update WorkEffortMeasure with " + serviceMapParams;
            addLogInfo(msg);
            result = runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortMeasure.name(), E.WorkEffortMeasure.name(), CrudEvents.OP_UPDATE, serviceMapParams, E.WorkEffortMeasure.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_PROBLEM_UPDATE + E.WorkEffortMeasure.name(), true, false);
        }
        Map<String, Object> key = UtilMisc.toMap(E.workEffortMeasureId.name(), (Object) workEffortMeasureId);
        manageResult(result, E.WorkEffortMeasure.name(), key);

    }

    private String getWeOtherGoalEnumId(GenericValue glAccount) {
        weOtherGoalEnumId = WeInterfaceConstants.WEMOMG_NONE;
        if (E.Y.name().equals(glAccount.getString(E.detectOrgUnitIdFlag.name()))) {
            // in realta' la gestione del WEMOMG_ORG non deve essere piu' utilizzata:
            // per sapere se il movimento va memorizzato con l'unita' organizzativa del workeffort, 
            // ci si basa sul valore di detectOrgUnitIdFlag
            weOtherGoalEnumId = WeInterfaceConstants.WEMOMG_ORG;
        }
        return weOtherGoalEnumId;
    }

    private String getDataSource() {
        GenericValue externalValue = getExternalValue();
        String dataSource = externalValue.getString(E.dataSource.name());
        if (!ValidationUtil.isEmptyOrNA(dataSource)) {
            return externalValue.getString(E.dataSource.name());
        }
        return null;
    }

    private Map<String, Object> setServiceMapParameters() {
        Map<String, Object> serviceMapParams = FastMap.newInstance();
        GenericValue externalValue = getExternalValue();

        serviceMapParams.put(E.workEffortMeasureId.name(), workEffortMeasureId);
        serviceMapParams.put(E.weMeasureTypeEnumId.name(), weMeasureTypeEnumId);
        serviceMapParams.put(E.dataSourceId.name(), getDataSource());

        if (!ValidationUtil.isEmptyOrNA(uomDescr)) {
            serviceMapParams.put(E.uomDescr.name(), uomDescr);
        }

        if (UtilValidate.isNotEmpty(externalValue.getBigDecimal(E.kpiScoreWeight.name()))) {
            BigDecimal kpiScoreWeight = externalValue.getBigDecimal(E.kpiScoreWeight.name());
            serviceMapParams.put(E.kpiScoreWeight.name(), kpiScoreWeight.doubleValue());
        }
        String comments = externalValue.getString(E.comments.name());
        if (UtilValidate.isNotEmpty(comments)) {
            serviceMapParams.put(E.comments.name(), comments);
        }
        if (UtilValidate.isNotEmpty(externalValue.getLong(E.sequenceId.name()))) {
            sequenceId = externalValue.getLong(E.sequenceId.name());
            serviceMapParams.put(E.sequenceId.name(), sequenceId);
        }

        if (UtilValidate.isNotEmpty(getWorkEffortLevelId())) {
            serviceMapParams.put(E.workEffortId.name(), getWorkEffortLevelId());
        }
        if (UtilValidate.isNotEmpty(glAccountId)) {
            serviceMapParams.put(E.glAccountId.name(), glAccountId);
        }
        
        addUomDescrLang(serviceMapParams);

        return serviceMapParams;
    }
    
    /**
     * gestisce uomDescrLang nel caso multi lingua
     * @param serviceMapParams
     */
    private void addUomDescrLang(Map<String, Object> serviceMapParams) {
    	if (isMultiLang()) {
            GenericValue externalValue = getExternalValue();
            String uomDescrLang = externalValue.getString(E.uomDescrLang.name());
            if (! ValidationUtil.isEmptyOrNA(uomDescrLang)) {
                serviceMapParams.put(E.uomDescrLang.name(), uomDescrLang);
            }
    	}
    }

    protected void checkValidityWorkEffortType() throws GeneralException {
        String localWorkEffortTypeId = getExternalValue().getString(E.workEffortTypeId.name());
        GenericValue workEffortType = getWeTypeInterfaceHelper().checkValidityWorkEffortType(localWorkEffortTypeId, null, null);
        setWorkEffortTypeLevelId(workEffortType.getString(E.workEffortTypeId.name()));
        wePurposeTypeIdRes = workEffortType.getString(E.wePurposeTypeIdRes.name());
        wePurposeTypeIdInd = workEffortType.getString(E.wePurposeTypeIdInd.name());
    }

    /**
     * Override ordinamento chiavi per i log
     * @parmas msg, log message
     */
    public void addLogInfo(String msg) {
        String msgNew = getEntityName() + ": " + msg;
        Map<String, Object> map = getMapKey();

        getManager().addLogInfo(msgNew, MODULE, map.toString());
    }

    private Map<String, Object> getMapKey() {
        Map<String, Object> map = FastMap.newInstance();
        map.put(E.sourceReferenceRootId.name(), getExternalValue().get(E.sourceReferenceRootId.name()));
        map.put(E.workEffortMeasureCode.name(), getExternalValue().get(E.workEffortMeasureCode.name()));
        map.put(E.workEffortTypeId.name(), getExternalValue().get(E.workEffortTypeId.name()));
        map.put(E.sourceReferenceId.name(), getExternalValue().get(E.sourceReferenceId.name()));
        map.put(E.accountCode.name(), getExternalValue().get(E.accountCode.name()));
        map.put(E.uomDescr.name(), getExternalValue().get(E.uomDescr.name()));
        map.put(E.workEffortName.name(), getExternalValue().get(E.workEffortName.name()));
        map.put(E.accountName.name(), getExternalValue().get(E.accountName.name()));

        return map;
    }

    /**
     * Override ordinamento chiavi per i log
     * @parmas msg, log message
     */
    @Override
    public void addLogWarning(String msg) {
        String msgNew = getEntityName() + ": " + msg;
        Map<String, Object> map = getMapKey();

        getManager().addLogWarning(msgNew, MODULE, map.toString());
    }
}
