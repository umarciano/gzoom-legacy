package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.WeInterfaceConstants;
import com.mapsengineering.base.standardimport.common.WorkEffortCommonTakeOverService;
import com.mapsengineering.base.standardimport.helper.PartyRoleHelper;
import com.mapsengineering.base.standardimport.helper.WePartyInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.WeTypeInterfaceHelper;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * WorkEffortRoot check Validity
 *
 */
public abstract class AbstractWorkEffortRootTakeOverService extends WorkEffortCommonTakeOverService {

    public static final String MSG_IF_IS_REAL_INSERT_OPERATION = "For new insert the field ";

    private String orgUnitId;
    private String orgUnitRoleTypeId;

    private String workEffortName;

    private Timestamp estimatedStartDate;
    private Timestamp estimatedCompletionDate;   
    private Timestamp actualStartDate;
  	private Timestamp actualCompletionDate;
    private Timestamp scheduledStartDate;
  	private Timestamp scheduledCompletionDate;
    
    private Double weightKpi;
    private Double weightSons;
    private Double weightAssocWorkEffort;
    private String totalEnumIdKpi;
    private String totalEnumIdAssoc;
    private String totalEnumIdSons;
    private String workEffortAssocTypeId;
    private String wePurposeTypeIdWe;
    private String workEffortTypeHierarchyId;

    private String workEffortPurposeTypeId;

    private WePartyInterfaceHelper wePartyInterfaceHelper;
    private PartyRoleHelper partyRoleHelper;
    private WeTypeInterfaceHelper weTypeInterfaceHelper;

    private String operationType;

    AbstractWorkEffortRootTakeOverService() {
        wePartyInterfaceHelper = new WePartyInterfaceHelper(this);
        partyRoleHelper = new PartyRoleHelper(this);
        weTypeInterfaceHelper = new WeTypeInterfaceHelper(this);
    }
    

    /**
     * Esegue importazione campi data da record esterno
     */
    protected void setWorkEffortDates() throws GeneralException {
        setEstimatedStartDate(getExternalValue().getTimestamp(E.estimatedStartDate.name()));
        setEstimatedCompletionDate(getExternalValue().getTimestamp(E.estimatedCompletionDate.name()));
        
        setActualStartDate(getExternalValue().getTimestamp(E.actualStartDate.name()));
        setActualCompletionDate(getExternalValue().getTimestamp(E.actualCompletionDate.name()));
        
        setScheduledStartDate(getExternalValue().getTimestamp(E.scheduledStartDate.name()));
        setScheduledCompletionDate(getExternalValue().getTimestamp(E.scheduledCompletionDate.name()));
    }

    protected void checkValidityWorkEffortName() throws GeneralException {
        GenericValue gv = getExternalValue();
        if (ValidationUtil.isEmptyOrNA(workEffortName)) {
            String msg = MSG_IF_IS_REAL_INSERT_OPERATION + E.workEffortName.name() + FindUtilService.MSG_NOT_NULL;
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }
    
    protected void checkValidityWorkEffortNameLang() throws GeneralException {
    	if (isMultiLang()) {
    		GenericValue gv = getExternalValue();
    		if (ValidationUtil.isEmptyOrNA(gv.getString(E.workEffortNameLang.name()))) {
    			String msg = MSG_IF_IS_REAL_INSERT_OPERATION + E.workEffortNameLang.name() + FindUtilService.MSG_NOT_NULL;
    			throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
    		}
    	}
    }

    protected void checkValidityOrgUnitId(String sourceId) throws GeneralException {
        GenericValue externalValue = getExternalValue();
        String orgCode = externalValue.getString(E.orgCode.name());
        if (UtilValidate.isNotEmpty(orgCode)) {
            orgUnitId = partyRoleHelper.checkValidityPartyParentRole(orgCode, sourceId);
        }
    }

    protected void checkValidityOrgUnitRoleTypeId() throws GeneralException {
        String roleTypeId = getExternalValue().getString(E.orgTypeCode.name());
        String roleTypeDesc = getExternalValue().getString(E.orgTypeDesc.name());

        if (UtilValidate.isNotEmpty(roleTypeId) || UtilValidate.isNotEmpty(roleTypeDesc)) {
            GenericValue roleType = wePartyInterfaceHelper.checkValidityRoleType(roleTypeId, roleTypeDesc);
            orgUnitRoleTypeId = roleType.getString(E.roleTypeId.name());
        }
    }

    /** 2.1.3 Controllo esistenza WORK_EFFORT_TYPE */
    protected String checkValidityWorkEffortType(String parentTypeId, String isRoot) throws GeneralException {
        String workEffortTypeId = "";
        
        String localWorkEffortTypeId = getExternalValue().getString(E.workEffortTypeId.name());
        GenericValue workEffortType = weTypeInterfaceHelper.checkValidityWorkEffortType(localWorkEffortTypeId, parentTypeId, isRoot);

        workEffortTypeId = workEffortType.getString(E.workEffortTypeId.name());

        setWeightKpi(workEffortType.getDouble(E.weightKpi.name()));
        setWeightSons(workEffortType.getDouble(E.weightSons.name()));
        setWeightAssocWorkEffort(workEffortType.getDouble(E.weightAssocWorkEffort.name()));
        setTotalEnumIdKpi(workEffortType.getString(E.totalEnumIdKpi.name()));
        setTotalEnumIdSons(workEffortType.getString(E.totalEnumIdSons.name()));
        setTotalEnumIdAssoc(workEffortType.getString(E.totalEnumIdAssoc.name()));

        setWorkEffortAssocTypeId(workEffortType.getString(E.workEffortAssocTypeId.name()));
        setWePurposeTypeIdWe(workEffortType.getString(E.wePurposeTypeIdWe.name()));
        setWorkEffortTypeHierarchyId(workEffortType.getString(E.workEffortTypeHierarchyId.name()));

        return workEffortTypeId;
    }

    protected void checkValidityWorkEffortPurposeType() throws GeneralException {
        String localWorkEffortPurposeTypeId = getExternalValue().getString(E.workEffortPurposeTypeId.name());
        String localWorkEffortPurposeTypeDesc = getExternalValue().getString(E.workEffortPurposeTypeDesc.name());
        if (UtilValidate.isNotEmpty(localWorkEffortPurposeTypeId) || UtilValidate.isNotEmpty(localWorkEffortPurposeTypeDesc)) {
            GenericValue workEffortPurposeType = weTypeInterfaceHelper.checkValidityWorkEffortPurposeType(localWorkEffortPurposeTypeId, localWorkEffortPurposeTypeDesc, getWePurposeTypeIdWe(), WeInterfaceConstants.PURPOSE_TYPE_ENUM_ID_PT_WORKEFFORT);
            setWorkEffortPurposeTypeId(workEffortPurposeType.getString(E.workEffortPurposeTypeId.name()));
        }
    }

    protected void checkValidityOrgUnitName() throws GeneralException {
        GenericValue externalValue = getExternalValue();
        String orgDesc = externalValue.getString(E.orgDesc.name());
        if (UtilValidate.isNotEmpty(orgDesc)) {
            orgUnitId = wePartyInterfaceHelper.checkValidityPartyGroup(orgUnitId, orgDesc);
        }
    }
    
    protected  Map<String, Object> setServiceMapParameterOrgUnit() {
        Map<String, Object> serviceMapParams = FastMap.newInstance();
        
        // update solo se
        if (UtilValidate.isNotEmpty(getOrgUnitRoleTypeId())) {
            serviceMapParams.put(E.orgUnitRoleTypeId.name(), getOrgUnitRoleTypeId());
        }
        // update solo se
        if (UtilValidate.isNotEmpty(getOrgUnitId())) {
            serviceMapParams.put(E.orgUnitId.name(), getOrgUnitId());
        }

        return serviceMapParams;
    }

    /**
     * preparo i parametri di base per insert/update del workEffort
     * @return
     */
    protected Map<String, Object> setServiceMapParametersWorkEffort() {
        Map<String, Object> serviceMapParams = FastMap.newInstance();
        GenericValue gv = getExternalValue();
        
        if (!ValidationUtil.isEmptyOrNA(getWorkEffortName())) {
            serviceMapParams.put(E.workEffortName.name(), getWorkEffortName());
        }

        if (UtilValidate.isNotEmpty(getEstimatedStartDate())) {
            serviceMapParams.put(E.estimatedStartDate.name(), getEstimatedStartDate());
        }
        
        if (UtilValidate.isNotEmpty(getEstimatedCompletionDate())) {
            serviceMapParams.put(E.estimatedCompletionDate.name(), getEstimatedCompletionDate());
        }
        
        if (UtilValidate.isNotEmpty(getActualStartDate())) {
            serviceMapParams.put(E.actualStartDate.name(), getActualStartDate());
        }
        
        if (UtilValidate.isNotEmpty(getActualCompletionDate())) {
            serviceMapParams.put(E.actualCompletionDate.name(), getActualCompletionDate());
        }
        
        if (UtilValidate.isNotEmpty(getScheduledStartDate())) {
            serviceMapParams.put(E.scheduledStartDate.name(), getScheduledStartDate());
        }
        
        if (UtilValidate.isNotEmpty(getScheduledCompletionDate())) {
            serviceMapParams.put(E.scheduledCompletionDate.name(), getScheduledCompletionDate());
        }
        
        serviceMapParams.put(E.etch.name(), gv.getString(E.etch.name()));

        serviceMapParams.put(E.description.name(), gv.getString(E.description.name()));

        // insert or update solo se
        if (UtilValidate.isNotEmpty(getWorkEffortPurposeTypeId())) {
            serviceMapParams.put(E.workEffortPurposeTypeId.name(), getWorkEffortPurposeTypeId());
        }

        String specialTerms = gv.getString(E.specialTerms.name());
        if (UtilValidate.isNotEmpty(specialTerms)) {
            serviceMapParams.put(E.specialTerms.name(), specialTerms);
        }
        
        addMultiLangFields(serviceMapParams, gv);

        return serviceMapParams;
    }
    
    protected void addMultiLangFields(Map<String, Object> serviceMapParams, GenericValue gv) {
    	if (isMultiLang()) {
    		String workEffortNameLang = gv.getString(E.workEffortNameLang.name());
            if (! ValidationUtil.isEmptyOrNA(workEffortNameLang)) {
                serviceMapParams.put(E.workEffortNameLang.name(), workEffortNameLang);
            }
    		
    		serviceMapParams.put(E.descriptionLang.name(), gv.getString(E.descriptionLang.name()));
    	}
    }
    
    protected boolean isRealOperationInsert() {
        return getLocalValue() == null;
    }

    public String getOrgUnitId() {
        return orgUnitId;
    }

    public void setOrgUnitId(String orgUnitId) {
        this.orgUnitId = orgUnitId;
    }

    public String getOrgUnitRoleTypeId() {
        return orgUnitRoleTypeId;
    }

    public void setOrgUnitRoleTypeId(String orgUnitRoleTypeId) {
        this.orgUnitRoleTypeId = orgUnitRoleTypeId;
    }

    public String getWorkEffortName() {
        return workEffortName;
    }

    public void setWorkEffortName(String workEffortName) {
        this.workEffortName = workEffortName;
    }

    public Timestamp getEstimatedStartDate() {
        return estimatedStartDate;
    }

    public void setEstimatedStartDate(Timestamp estimatedStartDate) {
        this.estimatedStartDate = estimatedStartDate;
    }

    public Timestamp getEstimatedCompletionDate() {
        return estimatedCompletionDate;
    }

    public void setEstimatedCompletionDate(Timestamp estimatedCompletionDate) {
        this.estimatedCompletionDate = estimatedCompletionDate;
    }

    public Timestamp getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Timestamp actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public Timestamp getActualCompletionDate() {
		return actualCompletionDate;
	}

	public void setActualCompletionDate(Timestamp actualCompletionDate) {
		this.actualCompletionDate = actualCompletionDate;
	}

	public Timestamp getScheduledStartDate() {
		return scheduledStartDate;
	}

	public void setScheduledStartDate(Timestamp scheduledStartDate) {
		this.scheduledStartDate = scheduledStartDate;
	}

	public Timestamp getScheduledCompletionDate() {
		return scheduledCompletionDate;
	}

	public void setScheduledCompletionDate(Timestamp scheduledCompletionDate) {
		this.scheduledCompletionDate = scheduledCompletionDate;
	}

	public Double getWeightKpi() {
        return weightKpi;
    }

    public void setWeightKpi(Double weightKpi) {
        this.weightKpi = weightKpi;
    }

    public Double getWeightSons() {
        return weightSons;
    }

    public void setWeightSons(Double weightSons) {
        this.weightSons = weightSons;
    }

    public Double getWeightAssocWorkEffort() {
        return weightAssocWorkEffort;
    }

    public void setWeightAssocWorkEffort(Double weightAssocWorkEffort) {
        this.weightAssocWorkEffort = weightAssocWorkEffort;
    }

    public String getTotalEnumIdKpi() {
        return totalEnumIdKpi;
    }

    public void setTotalEnumIdKpi(String totalEnumIdKpi) {
        this.totalEnumIdKpi = totalEnumIdKpi;
    }

    public String getTotalEnumIdAssoc() {
        return totalEnumIdAssoc;
    }

    public void setTotalEnumIdAssoc(String totalEnumIdAssoc) {
        this.totalEnumIdAssoc = totalEnumIdAssoc;
    }

    public String getTotalEnumIdSons() {
        return totalEnumIdSons;
    }

    public void setTotalEnumIdSons(String totalEnumIdSons) {
        this.totalEnumIdSons = totalEnumIdSons;
    }

    public String getWorkEffortAssocTypeId() {
        return workEffortAssocTypeId;
    }

    public void setWorkEffortAssocTypeId(String workEffortAssocTypeId) {
        this.workEffortAssocTypeId = workEffortAssocTypeId;
    }

    public String getWePurposeTypeIdWe() {
        return wePurposeTypeIdWe;
    }

    public void setWePurposeTypeIdWe(String wePurposeTypeIdWe) {
        this.wePurposeTypeIdWe = wePurposeTypeIdWe;
    }

    public String getWorkEffortTypeHierarchyId() {
        return workEffortTypeHierarchyId;
    }

    public void setWorkEffortTypeHierarchyId(String workEffortTypeHierarchyId) {
        this.workEffortTypeHierarchyId = workEffortTypeHierarchyId;
    }

    public String getWorkEffortPurposeTypeId() {
        return workEffortPurposeTypeId;
    }

    public void setWorkEffortPurposeTypeId(String workEffortPurposeTypeId) {
        this.workEffortPurposeTypeId = workEffortPurposeTypeId;
    }

    protected WePartyInterfaceHelper getWePartyInterfaceHelper() {
        return wePartyInterfaceHelper;
    }
    
    protected PartyRoleHelper getPartyRoleHelper() {
    	return partyRoleHelper;
    }

    /**
     * @return the operationType
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * @param operationType the operationType to set
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
