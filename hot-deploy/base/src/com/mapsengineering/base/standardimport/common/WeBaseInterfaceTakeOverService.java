package com.mapsengineering.base.standardimport.common;

import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.standardimport.helper.WeInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeRootInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeTypeInterfaceHelper;
import com.mapsengineering.base.standardimport.util.WeRootInterfaceContext;

/**
 * Base workEffort Interface 
 *
 */
public abstract class WeBaseInterfaceTakeOverService extends WorkEffortCommonTakeOverService {

    public static final String MODULE = WeBaseInterfaceTakeOverService.class.getName();

    private String workEffortLevelId;
    private String sourceReferenceId;
    private String workEffortTypeLevelId;

    private WeInterfaceHelper weInterfaceHelper;
    private WeRootInterfaceHelper weRootInterfaceHelper;
    private WeTypeInterfaceHelper weTypeInterfaceHelper;

	@Override
    /** Set localValue con record locale presente sul db or null in caso di nuovo inserimento. 
     * Recupera tutti i campi da externalValue, record sulla tabella di interfaccia
     * @params extLogicKey chiave logica esterna
     *  */
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
        GenericValue externalValue = getExternalValue();

        // se externalValue != null importa da WEInterface, altrimenti non e possibile continuare l'esecuzione del servizio
        if (externalValue == null) {
            throw new ImportException(getEntityName(), "", "record required to import");
        }
        // extLogicKey {sourceReferenceRootId=BSC12}
        // externalValue 

        // per avere id sourceReferenceRootId
        getContextData();

        if (UtilValidate.isEmpty(getWorkEffortRootId())) {
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), WeRootInterfaceContext.ERROR_ROOT_RECORD);
        }

        String sourceReferenceRootId = externalValue.getString(E.sourceReferenceRootId.name());

        setWeRootInterfaceHelper(new WeRootInterfaceHelper(this, getManager().getDelegator()));
        setWeInterfaceHelper(new WeInterfaceHelper(this, getManager().getDelegator()));
        setWeTypeInterfaceHelper(new WeTypeInterfaceHelper(this));

        getWeRootInterfaceHelper().getWorkEffort(sourceReferenceRootId, getOrganizationId());

        checkValidityWorkEffortType();
        checkValidityWorkEffort();

    }
    
    protected String checkLocaleValue(GenericValue localValue, String tableInterface, String valueEntityName, String valueFieldName, String sequenceEntityName) {
        String valueId = null;
        String msg = null;
        if (UtilValidate.isNotEmpty(localValue)) {
            msg = "Found " + valueEntityName + " with " + localValue.getPrimaryKey();
            addLogInfo(msg);
            valueId = localValue.getString(valueFieldName);
        } else {
            valueId = tableInterface + getManager().getDelegator().getNextSeqId(sequenceEntityName);
            msg = "Try to create " + valueEntityName + " with " + valueId;
            addLogInfo(msg);
        }
        setLocalValue(localValue);
        return valueId;
    }

    @Override
    /**
     * Esegue importazione record esterno
     */
    public abstract void doImport() throws GeneralException;

    protected void manageResult(Map<String, Object> result, String entityName, Map<String, Object> keys) throws GeneralException {
        String msg = "";
        if (!ServiceUtil.isError(result)) {
            msg = "successfull created/updated workeffort with id " + keys;
            addLogInfo(msg);
            GenericValue workEffort = getManager().getDelegator().findOne(entityName, keys, false);
            setLocalValue(workEffort);
        } else {
            msg = ServiceUtil.getErrorMessage(result);
            addLogInfo(msg);
        }
    }

    private void checkValidityWorkEffort() throws GeneralException {
        GenericValue externalValue = getExternalValue();

        sourceReferenceId = externalValue.getString(E.sourceReferenceId.name());
        String workEffortName = externalValue.getString(E.workEffortName.name());
        String msg = " Try workEffort with sourceReferenceId " + sourceReferenceId + " and title " + workEffortName;
        addLogInfo(msg);

        GenericValue we = getWeInterfaceHelper().getValidityWorkEffort(getWorkEffortRootId(), sourceReferenceId, getOrganizationId(), workEffortName, getWorkEffortTypeLevelId());
        setWorkEffortLevelId(we.getString(E.workEffortId.name()));
    }
    
    protected void checkValidityWorkEffortType() throws GeneralException {
        String localWorkEffortTypeId = getExternalValue().getString(E.workEffortTypeId.name());
        GenericValue workEffortType = getWeTypeInterfaceHelper().checkValidityWorkEffortType(localWorkEffortTypeId, null, null);
        setWorkEffortTypeLevelId(workEffortType.getString(E.workEffortTypeId.name()));
    }

    /**
     * Override ordinamento chiavi per i log
     * @parmas msg, log message
     */
    public abstract void addLogInfo(String msg);

    /**
     * @return the workEffortLevelId
     */
    public String getWorkEffortLevelId() {
        return workEffortLevelId;
    }

    /**
     * @param workEffortLevelId the workEffortLevelId to set
     */
    public void setWorkEffortLevelId(String workEffortLevelId) {
        this.workEffortLevelId = workEffortLevelId;
    }

    /**
     * @return the workEffortTypeLevelId
     */
    public String getWorkEffortTypeLevelId() {
        return workEffortTypeLevelId;
    }
    
    /**
     * 
     * @return the sourceReferenceId
     */
    public String getSourceReferenceId() {
        return sourceReferenceId;
    }

    /**
     * @return the weRootInterfaceHelper
     */
    public WeRootInterfaceHelper getWeRootInterfaceHelper() {
        return weRootInterfaceHelper;
    }

    /**
     * @param weRootInterfaceHelper the weRootInterfaceHelper to set
     */
    public void setWeRootInterfaceHelper(WeRootInterfaceHelper weRootInterfaceHelper) {
        this.weRootInterfaceHelper = weRootInterfaceHelper;
    }

    /**
     * @return the weInterfaceHelper
     */
    public WeInterfaceHelper getWeInterfaceHelper() {
        return weInterfaceHelper;
    }

    /**
     * @param weInterfaceHelper the weInterfaceHelper to set
     */
    public void setWeInterfaceHelper(WeInterfaceHelper weInterfaceHelper) {
        this.weInterfaceHelper = weInterfaceHelper;
    }

    /**
     * @return the weTypeInterfaceHelper
     */
    public WeTypeInterfaceHelper getWeTypeInterfaceHelper() {
        return weTypeInterfaceHelper;
    }

    /**
     * @param weTypeInterfaceHelper the weTypeInterfaceHelper to set
     */
    public void setWeTypeInterfaceHelper(WeTypeInterfaceHelper weTypeInterfaceHelper) {
        this.weTypeInterfaceHelper = weTypeInterfaceHelper;
    }

    /**
     * @param workEffortTypeLevelId the workEffortTypeLevelId to set
     */
    public void setWorkEffortTypeLevelId(String workEffortTypeLevelId) {
        this.workEffortTypeLevelId = workEffortTypeLevelId;
    }
}
