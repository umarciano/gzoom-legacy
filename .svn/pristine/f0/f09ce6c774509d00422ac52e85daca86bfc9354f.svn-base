package com.mapsengineering.base.standardimport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.glaccount.GlAccountTypeHelper;
import com.mapsengineering.base.standardimport.helper.GlAccountInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.GlAccountInterfaceInputEnumHelper;
import com.mapsengineering.base.standardimport.helper.GlAccountInterfaceMeasureTypeHelper;
import com.mapsengineering.base.standardimport.helper.GlAccountInterfacePeriodHelper;
import com.mapsengineering.base.standardimport.helper.GlAccountInterfaceUomHelper;
import com.mapsengineering.base.standardimport.helper.GlAccountPurposeInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.ValidationHelper;
import com.mapsengineering.base.standardimport.util.TakeOverUtil;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * GlAccount Interface STD
 *
 */
public class GlAccountInterfaceTakeOverService extends TakeOverService {

    public static final String MODULE = GlAccountInterfaceTakeOverService.class.getName();

    public static final String GL_ACCOUNT_PREFIX = "F";
    public static final String PARENT_ROLE_TYPE_ID_DEFAULT = "ORGANIZATION_UNIT";
    public static final String INPUT_ENUM_ID_OBIETTIVO = "ACCINP_OBJ";
    public static final String DETAIL_ENUM_ID_NULL = "ACCDET_NULL";
    public static final String ACCOUNT_TYPE_ID_FIN = "FINANCIAL";
    public static final String DEBIT_CREDIT_DEFAULT_D = "D";
    public static final String DEBIT_CREDIT_DESC = "C";
    public static final String PERIODICAL_ABSOLUTE_ENUM_ID_ABSOLUTE = "PRDABS_ALL";
    public static final String CURRENT_STATUS_ID_ACTIVE = "GLACC_ACTIVE";
    public static final String DEFAULT_UOM_ID_EUR = "EUR";
    public static final String CHILD_FOLDER_FILE = "FILE";
    public static final String PERIOD_TYPE_ID_FISCAL_YEAR = "FISCAL_YEAR";
    public static final String WORK_EFFORT_PURPOSE_TYPE_ID_DEFAULT = "INDICATOR";
    public static final String ENUM_TYPE_ACC_TYPE = "ACC_TYPE";
    public static final String PURPOSE_TYPE_ENUM_ID_PREFIX = "PT_";
    public static final String TREND_ENUM_ID_TREND_CONST = "TREND_CONST";
    public static final String WESCORE_MAXRANGE = "WESCORE_MAXRANGE";
    public static final String WECONVER_PERCENTWRK = "WECONVER_PERCENTWRK";
    public static final String WEALERT_TARGETUP = "WEALERT_TARGETUP";
    public static final String WEWITHPERF_PERF_0 = "WEWITHPERF_PERF_0";
    public static final String DEFAULT_DETECT_ORG_UNIT_ID_FLAG_N = "N";
    public static final Long DEFAULT_PRIO_CALC = 1L;

    private String glAccountId = null;
    private String accountCode = null;
    private String glAccountClassId;
    private String partyId;
    private String roleTypeId;
    private String respCenterId;
    private String respCenterRoleTypeId;
    private String productId;
    private String referencedAccountId;

    private String accountTypeEnumId;
    private String accountTypeId;
    private String glResourceTypeId;

    private String periodTypeId;
    private String defaultUomId;
    private String weMeasureTypeEnumId;
    private String inputEnumId;

    private String dataSourceId;
    private String calcCustomMethodId;
    private Long prioCalc;
    private String detectOrgUnitIdFlag;
    private String uomRangeId;

    private GlAccountInterfaceHelper glAccountInterfaceHelper;
    private GlAccountPurposeInterfaceHelper glAccountPurposeInterfaceHelper;

    @Override
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        ImportManager manager = getManager();
        glAccountInterfaceHelper = new GlAccountInterfaceHelper(this, manager.getDispatcher(), manager.getDelegator());
        glAccountPurposeInterfaceHelper = new GlAccountPurposeInterfaceHelper(this, manager.getDispatcher(), manager.getDelegator());

        importGlAccountInterfa(extLogicKey);
    }

    private void importGlAccountInterfa(Map<String, ? extends Object> extLogicKey) throws GeneralException {
        ImportManager manager = getManager();
        GenericValue externalValue = getExternalValue();
        GenericValue element = null;
        
        if (UtilValidate.isNotEmpty(externalValue)) {
        	this.accountCode = externalValue.getString(E.accountCode.name());
        }
        
        if(!ValidationUtil.isEmptyOrNA(this.accountCode)){
	        List<GenericValue> elements = manager.getDelegator().findList(E.GlAccount.name(), EntityCondition.makeCondition(E.accountCode.name(), this.accountCode), null, null, null, false);
	        element = EntityUtil.getFirst(elements);
        }
        if (UtilValidate.isEmpty(element) && !ValidationUtil.isEmptyOrNA((String)extLogicKey.get(E.accountCode.name()))) {
        	List<GenericValue> elementsExt = manager.getDelegator().findList(E.GlAccount.name(), EntityCondition.makeCondition(E.accountCode.name(), 
        			extLogicKey.get(E.accountCode.name())), null, null, null, false);
            element = EntityUtil.getFirst(elementsExt);
        }
	    
        if (UtilValidate.isNotEmpty(element)) {
            this.glAccountId = element.getString(E.glAccountId.name());
        } else if (UtilValidate.isNotEmpty(externalValue) && !ValidationUtil.isEmptyOrNA(this.accountCode)) {
            // se externalValue == null importa da acctgtransInterface altrimenti da glAccountInterface
            this.glAccountId = GL_ACCOUNT_PREFIX + this.accountCode;  
        } else{
        	// se non e' stato caricato l'accountCode, viene generato un nuovo id ed assegnato sia all'accountCode che al glAccountId
        	String glAccId = manager.getDelegator().getNextSeqId("GlAccount");
        	this.accountCode = glAccId;
        	this.glAccountId = GL_ACCOUNT_PREFIX + glAccId;
        }
        setLocalValue(manager.getDelegator().findOne(E.GlAccount.name(), UtilMisc.toMap(E.glAccountId.name(), glAccountId), false));
    }

    @Override
    public void doImport() throws GeneralException {
    	setImported(true);
        String msg;

        checkAccountNameLang();
        doImportClass();
        doImportUomRange();
        doImportReferencedAccountId();

        GlAccountTypeHelper glAccountTypeHelper = new GlAccountTypeHelper(getManager(), getExternalValue(), getEntityName());
        accountTypeEnumId = glAccountTypeHelper.getAccountTypeEnum();
        accountTypeId = glAccountTypeHelper.getGlAccountType();
        glResourceTypeId = glAccountTypeHelper.getGlResourceType(accountTypeId);

        productId = glAccountPurposeInterfaceHelper.doImportProduct(getExternalValue(), getEntityName());
        partyId = glAccountPurposeInterfaceHelper.doImportCdcParty(getExternalValue());
        roleTypeId = glAccountPurposeInterfaceHelper.doImportCdcRole(getExternalValue(), partyId, PARENT_ROLE_TYPE_ID_DEFAULT);
        respCenterId = glAccountPurposeInterfaceHelper.doImportCdrParty(getExternalValue());
        respCenterRoleTypeId = glAccountPurposeInterfaceHelper.doImportCdrRole(getExternalValue(), respCenterId, PARENT_ROLE_TYPE_ID_DEFAULT);
        doImportDataSource();
        doImportPeriodType();
        doImportUom();
        doImportMeasureType();
        
        GlAccountInterfaceInputEnumHelper glAccountInterfaceInputEnumHelper = new GlAccountInterfaceInputEnumHelper(getManager(), getExternalValue(), getEntityName());
        glAccountInterfaceInputEnumHelper.importInputEnum();
        inputEnumId = glAccountInterfaceInputEnumHelper.getInputEnumId();
        
        detectOrgUnitIdFlag = checkValidDetectOrgUnitIdFlag();        
        doImportCalcCustomMethod();
        doImportPrioCalc();
        doImportOthers();
        glAccountPurposeInterfaceHelper.doImportGlAccountOrganization(glAccountId);

        msg = "END IMPORT " + glAccountId;
        addLogInfo(msg);
    }
    
    /**
     * verifico presenza accountNameLang nel caso bilingue
     * @throws GeneralException
     */
    private void checkAccountNameLang() throws GeneralException {
    	GenericValue acc = getExternalValue();
    	if (isMultiLang() && UtilValidate.isEmpty(acc.getString(E.accountNameLang.name()))) {
            String msg = "accountNameLang must not be empty";
            throw new ImportException(getEntityName(), acc.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
    	}
    }

    private void doImportDataSource() {
        GenericValue acc = getExternalValue();
        String dataSource = acc.getString(E.dataSource.name());
        if (!ValidationUtil.isEmptyOrNA(dataSource)) {
            dataSourceId = acc.getString(E.dataSource.name());
        }

    }

    private void doImportPeriodType() throws GeneralException {
        GlAccountInterfacePeriodHelper glAccountInterfacePeriodHelper = new GlAccountInterfacePeriodHelper(getManager(), getExternalValue(), getEntityName());
        glAccountInterfacePeriodHelper.importPeriodType();
        this.periodTypeId = glAccountInterfacePeriodHelper.getPeriodTypeId();
    }

    private void doImportUom() throws GeneralException {
        GlAccountInterfaceUomHelper glAccountInterfaceUomHelper = new GlAccountInterfaceUomHelper(getManager(), getExternalValue(), getEntityName());
        glAccountInterfaceUomHelper.importUom();
        this.defaultUomId = glAccountInterfaceUomHelper.getUomId(DEFAULT_UOM_ID_EUR);
    }

    private void doImportMeasureType() throws GeneralException {
        GlAccountInterfaceMeasureTypeHelper glAccountInterfaceMeasureTypeHelper = new GlAccountInterfaceMeasureTypeHelper(getManager(), getExternalValue(), getEntityName());
        glAccountInterfaceMeasureTypeHelper.importMeasureType();
        this.weMeasureTypeEnumId = glAccountInterfaceMeasureTypeHelper.getWeMeasureTypeEnumId();
    }

    /**
     * valorizza glAccountClassId
     * @throws GeneralException
     */
    protected void doImportClass() throws GeneralException {
    	ImportManager manager = getManager();
    	GenericValue gv = getExternalValue();
    	if (UtilValidate.isNotEmpty(gv.getString(E.glAccountClassCode.name()))) {
    		List<GenericValue> glAccountClassList = manager.getDelegator().findList(E.GlAccountClass.name(), EntityCondition.makeCondition(E.accountClassCode.name(), gv.getString(E.glAccountClassCode.name())), null, null, null, false);		
    		GenericValue glAccountClass = EntityUtil.getFirst(glAccountClassList);
    		if (UtilValidate.isEmpty(glAccountClass)) {
    	        String msg = "The glAccountClassCode " + gv.getString(E.glAccountClassCode.name()) + ImportManagerConstants.STR_IS_NOT_VALID;
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);	
    		}
    		this.glAccountClassId = glAccountClass.getString(E.glAccountClassId.name());
    	}
    }
    
    /**
     * valorizza uomRangeId
     * @throws GeneralException
     */
    private void doImportUomRange() throws GeneralException {
    	GenericValue gv = getExternalValue();
    	if (UtilValidate.isNotEmpty(gv.getString(E.uomRangeId.name()))) {
    		TakeOverUtil.checkValidEntity(E.uomRangeId.name(), gv.getString(E.uomRangeId.name()), E.UomRange.name(), getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), getManager());
    		this.uomRangeId = gv.getString(E.uomRangeId.name());
    	}
    }
    
    /**
     * valorizza referenceAccountId
     * @throws GeneralException
     */
    private void doImportReferencedAccountId() throws GeneralException {
    	ImportManager manager = getManager();
    	GenericValue gv = getExternalValue();
    	if (UtilValidate.isNotEmpty(gv.getString(E.referencedAccountCode.name()))) {
    		List<GenericValue> glAccountList = manager.getDelegator().findList(E.GlAccount.name(), EntityCondition.makeCondition(E.accountCode.name(), gv.getString(E.referencedAccountCode.name())), null, null, null, false);
    		GenericValue glAccount = EntityUtil.getFirst(glAccountList);
    		if (UtilValidate.isEmpty(glAccount)) {
    	        String msg = "The referencedAccountCode " + gv.getString(E.referencedAccountCode.name()) + ImportManagerConstants.STR_IS_NOT_VALID;
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
    		}
    		this.referencedAccountId = glAccount.getString(E.glAccountId.name());
    	}
    }
    
    /**
     * 
     * @throws GeneralException
     */
    protected void doImportCalcCustomMethod() throws GeneralException {
        GenericValue acc = getExternalValue();
        String calcCustomMethod = acc.getString(E.calcCustomMethodId.name());
        if (! ValidationUtil.isEmptyOrNA(calcCustomMethod)) {
        	TakeOverUtil.checkValidEntity(E.customMethodId.name(), calcCustomMethod, E.CustomMethod.name(), getEntityName(), acc.getString(ImportManagerConstants.RECORD_FIELD_ID), getManager());
            this.calcCustomMethodId = calcCustomMethod;
        }
    }
    
    /**
     * 
     * @throws GeneralException
     */
    protected void doImportPrioCalc() throws GeneralException {
        GenericValue acc = getExternalValue();
        Long prioCalcField = acc.getLong(E.prioCalc.name());
        if(UtilValidate.isEmpty(prioCalcField)) {
        	prioCalcField = DEFAULT_PRIO_CALC;
        }
        this.prioCalc = prioCalcField;
    }
    
    /**
     * 
     * @return
     * @throws GeneralException
     */
    private String checkValidDetectOrgUnitIdFlag() throws GeneralException {
        GenericValue acc = getExternalValue();
        String flag = acc.getString(E.detectOrgUnitIdFlag.name());
        if (ValidationUtil.isEmptyOrNA(flag)) {
        	return DEFAULT_DETECT_ORG_UNIT_ID_FLAG_N;
        }
        ValidationHelper validationHelper = new ValidationHelper(acc, getEntityName());
        validationHelper.checkValidIndicatorTypeField(E.detectOrgUnitIdFlag.name(), flag);
        return flag;
    }

    /**
     * 
     * @throws GeneralException
     */
    protected void doImportOthers() throws GeneralException {
        GenericValue acc = getExternalValue();
        String id = acc.getString(ImportManagerConstants.RECORD_FIELD_ID);
        String msg = "";

        msg = "Find glAccount  " + glAccountId;
        addLogInfo(msg);

        Map<String, String> serviceMapParams = UtilMisc.toMap(E.glAccountId.name(), glAccountId, E.accountCode.name(), this.accountCode, E.glAccountTypeId.name(), accountTypeId, "accountName", acc.getString("accountName"), "description", acc.getString("description"), "productId", productId, "respCenterId", respCenterId, "respCenterRoleTypeId", respCenterRoleTypeId, "referencedAccountId", referencedAccountId, "glAccountClassId", glAccountClassId, E.accountTypeEnumId.name(), accountTypeEnumId, E.dataSourceId.name(), dataSourceId, 
        		E.source.name(), acc.getString(E.source.name()), E.glResourceTypeId.name(), glResourceTypeId, E.inputEnumId.name(), inputEnumId, E.calcCustomMethodId.name(), calcCustomMethodId, E.prioCalc.name(), prioCalc, E.detectOrgUnitIdFlag.name(), detectOrgUnitIdFlag, "uomRangeId", uomRangeId);
        addMultiLangFields(serviceMapParams, acc);

        setGlAccount(id, serviceMapParams, acc);

        String workEffortPurposeTypeId = checkValidWorkEffortPurposeTypeId();
        glAccountInterfaceHelper.setWorkEffortPurposeAccount(id, glAccountId, workEffortPurposeTypeId, getEntityName());

        if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(roleTypeId)) {
            Map<String, Object> serviceMapGlAccountRoleParams = UtilMisc.toMap(E.glAccountId.name(), glAccountId, E.partyId.name(), partyId, E.roleTypeId.name(), roleTypeId, E.fromDate.name(), acc.getTimestamp("refDate"));
            glAccountInterfaceHelper.setGlAccountRole(id, serviceMapGlAccountRoleParams, getEntityName());
        }
    }
    
    /**
     * gestisce i campi nel caso multi lingua
     * @param serviceMapParams
     * @param acc
     */
    protected void addMultiLangFields(Map<String, String> serviceMapParams, GenericValue acc) {
    	if (isMultiLang()) {
    		serviceMapParams.put(E.accountNameLang.name(), acc.getString(E.accountNameLang.name()));
    		serviceMapParams.put(E.descriptionLang.name(), acc.getString(E.descriptionLang.name()));
    		serviceMapParams.put(E.sourceLang.name(), acc.getString(E.sourceLang.name()));
    	}
    }

    /**
     * 
     * @return
     * @throws GeneralException
     */
    private String checkValidWorkEffortPurposeTypeId() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue acc = getExternalValue();
        String msg = "";

        String workEffortPurposeTypeId = acc.getString("purposeTypeId");
        if (UtilValidate.isEmpty(workEffortPurposeTypeId)) {
            workEffortPurposeTypeId = WORK_EFFORT_PURPOSE_TYPE_ID_DEFAULT;
        }

        TakeOverUtil.checkValidEntity(E.workEffortPurposeTypeId.name(), workEffortPurposeTypeId, E.WorkEffortPurposeType.name(), getEntityName(), acc.getString(ImportManagerConstants.RECORD_FIELD_ID), manager);

        List<GenericValue> workEffortPurposeTypes = manager.getDelegator().findByAnd(E.WorkEffortPurposeType.name(), UtilMisc.toMap(E.workEffortPurposeTypeId.name(), workEffortPurposeTypeId, E.purposeTypeEnumId.name(), PURPOSE_TYPE_ENUM_ID_PREFIX + accountTypeEnumId));
        GenericValue workEffortPurposeType = EntityUtil.getFirst(workEffortPurposeTypes);
        if (UtilValidate.isEmpty(workEffortPurposeType)) {
            msg = "The workEffortPurposeTypeId " + workEffortPurposeType + ImportManagerConstants.STR_IS_NOT_VALID;
            throw new ImportException(getEntityName(), acc.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        return workEffortPurposeTypeId;
    }

    /**
     * 
     * @param id
     * @param mappaGlAccountParams
     * @param genericValue
     * @throws GeneralException
     */
    private void setGlAccount(String id, Map<String, String> mappaGlAccountParams, GenericValue genericValue) throws GeneralException {
        String msg = "";
        // GlAccount
        GenericValue glAccount = getLocalValue();

        Map<String, Object> serviceMapGlAccountParams = new HashMap<String, Object>();
        serviceMapGlAccountParams.putAll(mappaGlAccountParams);

        if (UtilValidate.isEmpty(glAccount)) {
        	createGlAccount(id, genericValue, serviceMapGlAccountParams);
        } else {
            // Update glAccount
            msg = "Trying to update acc glAccountId " + glAccountId;
            addLogInfo(msg);

            glAccountInterfaceHelper.checkAccountTypeEnumId(glAccount, accountTypeEnumId, getEntityName(), id);
            createUpdateGlAccount(id, CrudEvents.OP_UPDATE, serviceMapGlAccountParams, "GlAccount successfully updated", "Error in glAccount update ");
        }
    }

    /**
     * 
     * @param id
     * @param genericValue
     * @param serviceMapGlAccountParams
     * @throws GeneralException
     */
    private void createGlAccount(String id, GenericValue genericValue, Map<String, Object> serviceMapGlAccountParams) throws GeneralException {
        // acctgTransEntry
        // Create Mov
        String msg = "";
        msg = "Trying to create glAccount " + glAccountId;
        addLogInfo(msg);

        if (UtilValidate.isNotEmpty(genericValue.getTimestamp(E.fromDate.name()))) {
            // fromDate only in insert
            Date fromDate = genericValue.getTimestamp(E.fromDate.name());
            serviceMapGlAccountParams.put(E.fromDate.name(), fromDate);
        }
        if (UtilValidate.isNotEmpty(genericValue.getTimestamp(E.thruDate.name()))) {
            // thruDate only in insert
            Date thruDate = genericValue.getTimestamp(E.thruDate.name());
            serviceMapGlAccountParams.put(E.thruDate.name(), thruDate);
        }
        if (UtilValidate.isNotEmpty(genericValue.getTimestamp(E.fromDate.name())) && UtilValidate.isNotEmpty(genericValue.getTimestamp(E.thruDate.name())) && genericValue.getTimestamp(E.fromDate.name()).after(genericValue.getTimestamp(E.thruDate.name()))) {
            msg = "The fromDate " + genericValue.getTimestamp(E.fromDate.name()) + " is after thruDate " + genericValue.getTimestamp(E.thruDate.name());
            throw new ImportException(getEntityName(), genericValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        serviceMapGlAccountParams.put("currentStatusId", CURRENT_STATUS_ID_ACTIVE);
        serviceMapGlAccountParams.put("detailEnumId", DETAIL_ENUM_ID_NULL);
        serviceMapGlAccountParams.put("childFolderFile", CHILD_FOLDER_FILE);
        serviceMapGlAccountParams.put("debitCreditDefault", getDebitCreditDefault(id));
        serviceMapGlAccountParams.put("trendEnumId", TREND_ENUM_ID_TREND_CONST);
        serviceMapGlAccountParams.put("weScoreRangeEnumId", WESCORE_MAXRANGE);
        serviceMapGlAccountParams.put("weScoreConvEnumId", WECONVER_PERCENTWRK);
        serviceMapGlAccountParams.put("weAlertRuleEnumId", WEALERT_TARGETUP);
        serviceMapGlAccountParams.put("weWithoutPerf", WEWITHPERF_PERF_0);

        createUpdateGlAccount(id, CrudEvents.OP_CREATE, serviceMapGlAccountParams, "GlAccount successfully created", "Error in glAccount creation ");
    }

    /**
     * 
     * @param id
     * @param operation
     * @param serviceMapGlAccountParams
     * @param successMsg
     * @param errMsg
     * @throws GeneralException
     */
    private void createUpdateGlAccount(String id, String operation, Map<String, Object> serviceMapGlAccountParams, String successMsg, String errMsg) throws GeneralException {
        ImportManager manager = getManager();
        String msg;

        serviceMapGlAccountParams.put("defaultUomId", defaultUomId);
        serviceMapGlAccountParams.put("periodTypeId", periodTypeId);
        serviceMapGlAccountParams.put("weMeasureTypeEnumId", weMeasureTypeEnumId);

        Map<String, Object> serviceMap = baseCrudInterface(E.GlAccount.name(), operation, serviceMapGlAccountParams);
        Map<String, Object> res = manager.getDispatcher().runSync("crudServiceDefaultOrchestration_GlAccount", serviceMap);
        msg = ServiceUtil.getErrorMessage(res);
        if (UtilValidate.isEmpty(msg)) {
            msg = successMsg;
            addLogInfo(msg);

            GenericValue glAccount = manager.getDelegator().findOne(E.GlAccount.name(), false, UtilMisc.toMap(E.glAccountId.name(), glAccountId));
            setLocalValue(glAccount);
        } else {
            msg = errMsg + msg;
            throw new ImportException(getEntityName(), id, msg);
        }
    }

    /**
     * 
     * @param id
     * @return
     * @throws GeneralException
     */
    private String getDebitCreditDefault(String id) throws GeneralException {
        GenericValue acc = getExternalValue();
        String debitCreditDefault = acc.getString(E.debitCreditDefault.name());
        if (UtilValidate.isNotEmpty(debitCreditDefault)) {
            if (!DEBIT_CREDIT_DEFAULT_D.equals(debitCreditDefault) && !DEBIT_CREDIT_DESC.equals(debitCreditDefault)) {
                String msg = "debitCreditDefault " + debitCreditDefault + " for accountCode " + this.accountCode + " " + ImportManagerConstants.STR_IS_NOT_VALID;
                throw new ImportException(getEntityName(), id, msg);
            }
            return debitCreditDefault;
        }
        return DEBIT_CREDIT_DEFAULT_D;
    }

}
