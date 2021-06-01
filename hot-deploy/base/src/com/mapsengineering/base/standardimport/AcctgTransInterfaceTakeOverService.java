package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.acctg.GlAccountHelper;
import com.mapsengineering.base.standardimport.acctg.ImportAmountHelper;
import com.mapsengineering.base.standardimport.acctg.ImportGlAccountHelper;
import com.mapsengineering.base.standardimport.common.AcctgTransInterfaceConstants;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.helper.AcctgTransInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.CustomTimePeriodHelper;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * AcctgTrans STD
 *
 */
public class AcctgTransInterfaceTakeOverService extends TakeOverService implements AcctgTransInterfaceConstants {

    public static final String MODULE = AcctgTransInterfaceTakeOverService.class.getName();

    private String acctgTransEntrySeqId = null;
    private GenericValue glAccount;
    private String glAccountId;
    private String inputEnumId;
    private String glFiscalTypeId;
    private String acctgTransTypeId;
    private String partyId;
    private String roleTypeId;
    private String entryPartyId;
    private String entryRoleTypeId;
    private String weTransWeId;
    private String weTransProductId;
    private String weTransMeasureId;
    private String weTransCurrencyUomId;
    private String customTimePeriodId;
    private Date fromDateCompetence;
    private Date toDateCompetence;
    private Double weTransValue;
    private boolean alreadyCheckedWorkeffort;

    private GenericValue glAccInput;
    private GenericValue glAccMeasure;
    private GenericValue workEffortMeasure;
    private GenericValue product;
    private GenericValue workEffort;
    private Timestamp weTransactionDate;

    private AcctgTransInterfaceHelper acctgTransInterfaceHelper;
    
    @Override
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        ImportManager manager = getManager();
        GenericValue externalValue = getExternalValue();
        alreadyCheckedWorkeffort = false;

        String defaultOrganizationPartyId = (String) getManager().getContext().get(E.defaultOrganizationPartyId.name());
        acctgTransInterfaceHelper = new AcctgTransInterfaceHelper(manager.getDelegator(), defaultOrganizationPartyId);

        // Recuperare partyIds and roleTypeIds
        checkPartiesAndRoles();

        checkFiscalType();

        // 1.1 Recuperare GlAccount tramite voucherRef, uomDescr o accountCode		
        doImportGlAccount(manager, externalValue);

        checkWorkEffortOrProduct();

        setCustomTimePeriod();

        checkGlAccount();
        acctgTransInterfaceHelper.setGlAccount(glAccount);

        //import del valore - amount o amountcode
        doImportAmount(manager, externalValue);

        checkIsInsertOrUpdate();
    }

    private void checkWorkEffortOrProduct() throws GeneralException {
        if (UtilValidate.isEmpty(workEffort)) {
            acctgTransInterfaceHelper.findWorkEffortByCode(getManager(), getExternalValue(), inputEnumId);
            workEffort = acctgTransInterfaceHelper.getWorkEffort();
        }
        acctgTransInterfaceHelper.findProductByCode(getManager(), getExternalValue(), inputEnumId);
        product = acctgTransInterfaceHelper.getProduct();
    }

    private void checkIsInsertOrUpdate() throws GeneralException {
        Map<String, String> conditionIsUpdate = acctgTransInterfaceHelper.getConditionIsUpdate(getExternalValue(), weTransactionDate, GL_FISCAL_TYPE_ID_ACTUAL, glAccountId);
        List<GenericValue> acctgTransAndEntriesViews = getManager().getDelegator().findByAnd("AcctgTransAndEntriesView", conditionIsUpdate);
        setLocalValue(EntityUtil.getFirst(acctgTransAndEntriesViews));

        String msg = "Search acctgTransAndEntriesView for condition = " + conditionIsUpdate;
        addLogInfo(msg);

        acctgTransEntrySeqId = ACCTG_TRANS_PREFIX + getManager().getDelegator().getNextSeqId("AcctgTransEntry");

        msg = "InitLocalValue new acctgTransEntrySeqId = " + acctgTransEntrySeqId;
        addLogInfo(msg);
    }

    private void checkGlAccount() throws GeneralException {
        GlAccountHelper glAccountHelper = new GlAccountHelper(this, glAccountId, partyId, roleTypeId, entryPartyId, entryRoleTypeId, inputEnumId, glAccount, product, workEffort, alreadyCheckedWorkeffort);
        if (alreadyCheckedWorkeffort) {
            glAccountHelper.setWeTransWeId(weTransWeId);
            glAccountHelper.setWeTransMeasureId(weTransMeasureId);
            glAccountHelper.setWorkEffortMeasure(workEffortMeasure);

            acctgTransInterfaceHelper.setWorkEffortMeasure(workEffortMeasure);
        }
        glAccountHelper.checkGlAccount();
        acctgTransTypeId = glAccountHelper.getAcctgTransTypeId();
        weTransProductId = glAccountHelper.getWeTransProductId();
        if (!alreadyCheckedWorkeffort) {
            weTransWeId = glAccountHelper.getWeTransWeId();
            weTransMeasureId = glAccountHelper.getWeTransMeasureId();
            acctgTransInterfaceHelper.setWorkEffortMeasure(glAccountHelper.getWorkEffortMeasure());
        }
    }

    private void checkPartiesAndRoles() throws GeneralException {
        // Controllo 1.3
        partyId = acctgTransInterfaceHelper.getPartyId(getExternalValue().getString(E.uorgCode.name()), PARENT_ROLE_TYPE_ID_DEFAULT);
        // Controllo 1.4
        roleTypeId = acctgTransInterfaceHelper.doImportRole(getExternalValue().getString(E.uorgRoleTypeId.name()), partyId, PARENT_ROLE_TYPE_ID_DEFAULT);
        // Controllo 1.8
        entryPartyId = acctgTransInterfaceHelper.getEntryPartyId(getExternalValue().getString(E.partyCode.name()));
        // Controllo 1.9
        entryRoleTypeId = acctgTransInterfaceHelper.doImportRole(getExternalValue().getString(E.roleTypeId.name()), entryPartyId, null);
    }

    private void setCustomTimePeriod() throws GeneralException {
        CustomTimePeriodHelper customTimePeriodHelper = new CustomTimePeriodHelper(this);
        customTimePeriodId = customTimePeriodHelper.getCustomTimePeriodId(glAccount.getString(E.periodTypeId.name()));
        weTransactionDate = customTimePeriodHelper.getWeTransactionDate();
    }

    /**
     * 
     * @param manager
     * @param externalValue
     * @throws GeneralException
     */
    private void doImportGlAccount(ImportManager manager, GenericValue externalValue) throws GeneralException {
        String voucherRef = externalValue.getString(E.voucherRef.name());
        String uomDescr = externalValue.getString(E.uomDescr.name());
        String glAccountCode = externalValue.getString(E.glAccountCode.name());
        String msg = "";

        if (ValidationUtil.isEmptyOrNA(voucherRef) && ValidationUtil.isEmptyOrNA(uomDescr)) {
            msg = "Search glAccount with glAccountCode = " + glAccountCode;
            addLogInfo(msg);
            doImportGlAccountByCode(glAccountCode);
            return;
        }

        ImportGlAccountHelper importGlAccountHelper = new ImportGlAccountHelper(manager, externalValue, getEntityName(), acctgTransInterfaceHelper, this);

        //recupero WorkEffort e GlAccount da input
        workEffort = importGlAccountHelper.findWorkEffortInputByCode();
        acctgTransInterfaceHelper.setWorkEffort(workEffort);
        glAccInput = importGlAccountHelper.findGlAccountInputByCode();

        if (!ValidationUtil.isEmptyOrNA(voucherRef)) {
            msg = "Search glAccount with voucherRef = " + voucherRef;
            addLogInfo(msg);
            doImportGlAccountByVoucherRef(manager, importGlAccountHelper, externalValue, voucherRef);
        } else {
            msg = "Search glAccount with uomDescr = " + uomDescr;
            addLogInfo(msg);
            doImportGlAccountByUomDescr(manager, importGlAccountHelper, externalValue, uomDescr);
        }

        //verifico accountTypeEnumId: se finanziario scarto la misura, altrimenti valorizzo i campi con la misura               
        if (GL_ACCOUNT_TYPE_ENUM_ID_FIN.equals(glAccMeasure.getString(E.accountTypeEnumId.name()))) {
            workEffortMeasure = null;
            return;
        }

        //valorizzazione campi in input con i campi della misura
        weTransWeId = workEffortMeasure.getString(E.workEffortId.name());
        glAccountId = workEffortMeasure.getString(E.glAccountId.name());
        entryPartyId = ValidationUtil.emptyIfNull(workEffortMeasure.getString(E.orgUnitId.name()));
        entryRoleTypeId = ValidationUtil.emptyIfNull(workEffortMeasure.getString(E.orgUnitRoleTypeId.name()));
        weTransMeasureId = workEffortMeasure.getString(E.workEffortMeasureId.name());
        findWorkEffortByMeasure(manager);

        importGlAccountHelper.logWorkEffortOrGlAccountFoundIfNotInInput(weTransWeId, glAccountId);
        alreadyCheckedWorkeffort = true;

        msg = "InitLocalValue glAccountId = " + glAccountId;
        addLogInfo(msg);
    }

    @Override
    public void doImport() throws GeneralException {
    	if (isToIgnore()) {
    		return;
    	}
    	setImported(true);
        String msg;

        checkDetail();
        checkCustomTimePeriodoAndDate();
        createUpdateAcctgTransEntry();

        msg = "END IMPORT " + acctgTransEntrySeqId;
        addLogInfo(msg);
    }
    
    /**
     * controlla se e da importare
     * @return
     */
    private boolean isToIgnore() {
    	GenericValue mov = getExternalValue();
    	if (isMultiLang()) {
    		return ValidationUtil.isEmptyOrNA(mov.getString(E.amount.name())) && ValidationUtil.isEmptyOrNA(mov.getString(E.amountCode.name()))
    				&& UtilValidate.isEmpty(mov.getString("note"))  && UtilValidate.isEmpty(mov.getString("comments"))
    				&& UtilValidate.isEmpty(mov.getString("noteLang")) && UtilValidate.isEmpty(mov.getString("commentsLang"));
    	}
		return ValidationUtil.isEmptyOrNA(mov.getString(E.amount.name())) && ValidationUtil.isEmptyOrNA(mov.getString(E.amountCode.name()))
				&& UtilValidate.isEmpty(mov.getString("note"))  && UtilValidate.isEmpty(mov.getString("comments"));
    }

    /**
     * import GlAccount solo da glAccountCode
     * @param glAccountCode
     * @throws GeneralException
     */
    private void doImportGlAccountByCode(String glAccountCode) throws GeneralException {
        glAccount = doImport(ImportManagerConstants.GL_ACCOUNT_INTERFACE, UtilMisc.toMap(E.accountCode.name(), glAccountCode));
        glAccountId = glAccount.getString(E.glAccountId.name());
        inputEnumId = glAccount.getString(E.inputEnumId.name());
    }

    /**
     * import Glaccount da voucherRef + controlli
     * @param manager
     * @param importGlAccountHelper
     * @param externalValue
     * @param voucherRef
     * @throws GeneralException
     */
    private void doImportGlAccountByVoucherRef(ImportManager manager, ImportGlAccountHelper importGlAccountHelper, GenericValue externalValue, String voucherRef) throws GeneralException {

        workEffortMeasure = manager.getDelegator().findOne(E.WorkEffortMeasure.name(), false, UtilMisc.toMap(E.workEffortMeasureId.name(), voucherRef));
        checkMeasureByVoucherRef(externalValue, voucherRef);

        //verifica corrispondenza glAccountId
        importGlAccountHelper.chekIfGvFieldMatchesWithInput(glAccInput, "g", workEffortMeasure.getString(E.glAccountId.name()), voucherRef, "glAccountId");

        glAccMeasure = importGlAccountHelper.findAndCheckGlAccountMeasure(workEffortMeasure.getString(E.glAccountId.name()), workEffortMeasure.getString(E.workEffortMeasureId.name()));
        //verifico accountTypeEnumId: se finanziario faccio import glaccount, scarto la misura e non proseguo con i controlli
        if (GL_ACCOUNT_TYPE_ENUM_ID_FIN.equals(glAccMeasure.getString(E.accountTypeEnumId.name()))) {
            doImportGlAccountByCode(glAccMeasure.getString(E.accountCode.name()));
            workEffortMeasure = null;
            return;
        }

        //verifica corrispondenza workEffortId
        importGlAccountHelper.chekIfGvFieldMatchesWithInput(workEffort, "w", workEffortMeasure.getString(E.workEffortId.name()), voucherRef, "workEffortId");
        //verifica corrispondenza entryPartyId
        importGlAccountHelper.chekIfFieldMatchesWithInput(entryPartyId, workEffortMeasure.getString(E.orgUnitId.name()), voucherRef, "entryPartyId");
        //verifica corrispondenza entryRoleTypeId
        importGlAccountHelper.chekIfFieldMatchesWithInput(entryRoleTypeId, workEffortMeasure.getString(E.orgUnitRoleTypeId.name()), voucherRef, "entryRoleTypeId");

        doImportGlAccountByCode(glAccMeasure.getString(E.accountCode.name()));
    }

    /**
     * import Glaccount da uomDescr + controlli
     * @param manager
     * @param importGlAccountHelper
     * @param externalValue
     * @param uomDescr
     * @throws GeneralException
     */
    private void doImportGlAccountByUomDescr(ImportManager manager, ImportGlAccountHelper importGlAccountHelper, GenericValue externalValue, String uomDescr) throws GeneralException {

        Map<String, String> uomDescrConditions = importGlAccountHelper.getUomDescrConditions(glAccInput, uomDescr);
        List<GenericValue> workEffortMeasureList = manager.getDelegator().findByAnd("WorkEffortMeasure", uomDescrConditions);
        workEffortMeasure = EntityUtil.getFirst(workEffortMeasureList);
        checkMeasureByUomDescr(externalValue, workEffortMeasureList, uomDescrConditions);

        glAccMeasure = importGlAccountHelper.findAndCheckGlAccountMeasure(workEffortMeasure.getString(E.glAccountId.name()), workEffortMeasure.getString(E.workEffortMeasureId.name()));
        doImportGlAccountByCode(glAccMeasure.getString(E.accountCode.name()));
    }

    /**
     * 
     * @param externalValue
     * @param voucherRef
     * @throws GeneralException
     */
    private void checkMeasureByVoucherRef(GenericValue externalValue, String voucherRef) throws GeneralException {
        if (UtilValidate.isEmpty(workEffortMeasure)) {          
        	Map<String, Object> parameters = UtilMisc.toMap(E.voucherRef.name(), (Object) voucherRef, E.accountCode.name(), externalValue.get(E.glAccountCode.name()));
        	JobLogLog noMeasureFound = new JobLogLog().initLogCode("StandardImportUiLabels", "NO_MEAS_FOUND", parameters, getManager().getLocale());
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), noMeasureFound);                     
        }
    }

    /**
     * 
     * @param externalValue
     * @param workEffortMeasureList
     * @param uomDescrConditions
     * @throws GeneralException
     */
    private void checkMeasureByUomDescr(GenericValue externalValue, List<GenericValue> workEffortMeasureList, Map<String, String> uomDescrConditions) throws GeneralException {
        if (UtilValidate.isEmpty(workEffortMeasureList) || UtilValidate.isEmpty(workEffortMeasure)) {
            String msg = "No WorkEffortMeasure found for condition " + uomDescrConditions;
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        if (workEffortMeasureList.size() > 1) {
            String msg = "More than one WorkEffortMeasure found for condition " + uomDescrConditions;
            throw new ImportException(getEntityName(), externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * 
     * @param manager
     * @throws GeneralException
     */
    private void findWorkEffortByMeasure(ImportManager manager) throws GeneralException {
        if (UtilValidate.isEmpty(workEffort)) {
            workEffort = manager.getDelegator().findOne(E.WorkEffort.name(), false, UtilMisc.toMap(E.workEffortId.name(), weTransWeId));
            acctgTransInterfaceHelper.setWorkEffort(workEffort);
        }
    }

    protected void checkFiscalType() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue mov = getExternalValue();
        String msg = "";

        glFiscalTypeId = mov.getString(E.glFiscalTypeId.name());
        if (ValidationUtil.isEmptyOrNA(glFiscalTypeId)) {
            glFiscalTypeId = GL_FISCAL_TYPE_ID_ACTUAL;
        }
        // 1.5 Recuperare glFiscalTypeId tramite glFiscalTypeId se != null
        GenericValue glFiscalType = manager.getDelegator().findOne("GlFiscalType", false, E.glFiscalTypeId.name(), glFiscalTypeId);
        if (UtilValidate.isNotEmpty(glFiscalTypeId) && UtilValidate.isEmpty(glFiscalType)) {
            msg = "The glFiscalType id " + glFiscalTypeId + STR_IS_NOT_VALID;
            throw new ImportException(getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * 
     * @throws GeneralException
     */
    protected void checkDetail() throws GeneralException {
        GenericValue mov = getExternalValue();
        String msg = "";

        // 1.7 Controllo Dettaglio
        String detailEnumId = glAccount.getString("detailEnumId");
        if ((DETAIL_ENUM_ID_NOSUM.equals(detailEnumId) || DETAIL_ENUM_ID_SUM.equals(detailEnumId)) && (UtilValidate.isEmpty(entryPartyId) || UtilValidate.isEmpty(entryRoleTypeId))) {
            msg = "For detailEnumId " + detailEnumId + " the entryPartyId " + entryPartyId + " and entryRoleTypeId " + entryRoleTypeId + " are not valid";
            throw new ImportException(getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        msg = "Find detailEnumId  " + detailEnumId;
        addLogInfo(msg);
    }

    /**
     * 
     * @throws GeneralException
     */
    protected void checkCustomTimePeriodoAndDate() throws GeneralException {
        GenericValue mov = getExternalValue();
        String msg = null;

        weTransCurrencyUomId = glAccount.getString("defaultUomId");
        msg = "Find weTransCurrencyUomId  " + weTransCurrencyUomId;
        addLogInfo(msg);

        fromDateCompetence = mov.getTimestamp(E.fromDateCompetence.name());
        toDateCompetence = mov.getTimestamp(E.toDateCompetence.name());

        if (UtilValidate.isNotEmpty(fromDateCompetence) && UtilValidate.isNotEmpty(toDateCompetence) && fromDateCompetence.after(toDateCompetence)) {
            msg = "The fromDateCompetence = " + fromDateCompetence + " is after toDateCompetence " + toDateCompetence;
            throw new ImportException(getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * calcola il weTransValue come da analisi
     * @param manager
     * @param externalValue
     * @throws GeneralException
     */
    private void doImportAmount(ImportManager manager, GenericValue externalValue) throws GeneralException {
        ImportAmountHelper importAmountHelper = new ImportAmountHelper(this, manager, getEntityName(), externalValue, glAccount);
        importAmountHelper.doImportAmount();
        weTransValue = importAmountHelper.getWeTransValue();
    }

    /**
     * 
     * @throws GeneralException
     */
    protected void createUpdateAcctgTransEntry() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue mov = getExternalValue();
        GenericValue acctgTransEntry = getLocalValue();
        String msg = "";

        Map<String, Object> serviceMap = null;

        if (UtilValidate.isEmpty(acctgTransEntry)) {
            // Creazione Movimento
            serviceMap = createAcctgTransEntry(mov);
        } else {
            // Aggiornamento Movimento
            serviceMap = updateAcctgTransEntry(acctgTransEntry, mov);
        }

        Map<String, Object> res = manager.getDispatcher().runSync("crudServiceDefaultOrchestration_AcctgTransAndEntries", serviceMap);
        msg = ServiceUtil.getErrorMessage(res);
        if (UtilValidate.isEmpty(msg)) {
            msg = "AcctgTransAndEntries successfully " + (UtilValidate.isEmpty(acctgTransEntry) ? "created" : "updated");
            addLogInfo(msg);

            acctgTransEntry = manager.getDelegator().findOne("AcctgTransEntry", false, UtilMisc.toMap("acctgTransEntrySeqId", acctgTransEntrySeqId, E.acctgTransId.name(), acctgTransEntrySeqId));
            setLocalValue(acctgTransEntry);
        } else {
            msg = "Error in AcctgTransAndEntries " + (UtilValidate.isEmpty(acctgTransEntry) ? "created" : "updated") + msg;
            throw new ImportException(getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        msg = "END IMPORT " + acctgTransEntrySeqId;

    }

    /**
     * Creazione di un nuovo movimento
     * @param mov movimento da creare
     * @return
     * @throws GeneralException
     */
    private Map<String, Object> createAcctgTransEntry(GenericValue mov) throws GeneralException {

        String msg = "Trying to create mov " + acctgTransEntrySeqId;
        addLogInfo(msg);

        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.weTransAccountId.name(), glAccountId, "weTransValue", weTransValue, "weTransComment", mov.getString("note"), "weTransComments", mov.getString("comments"), E.weTransAccountId.name(), glAccountId, "weTransWeId", weTransWeId, "weTransTypeValueId", glFiscalTypeId, E.fromDateCompetence.name(), fromDateCompetence, E.toDateCompetence.name(), toDateCompetence, "entryRoleTypeId", entryRoleTypeId, "entryPartyId", entryPartyId, E.roleTypeId.name(), roleTypeId, E.partyId.name(), partyId, "weTransCurrencyUomId", weTransCurrencyUomId, E.customTimePeriodId.name(), customTimePeriodId, "acctgTransTypeId", acctgTransTypeId, "weTransId", acctgTransEntrySeqId, "weTransEntryId", acctgTransEntrySeqId);
        addMultiLangFields(serviceMapParams, mov);
        
        if (!ValidationUtil.isEmptyOrNA(weTransProductId)) {
            serviceMapParams.put("weTransProductId", weTransProductId);
        }
        if (UtilValidate.isNotEmpty(weTransMeasureId)) {
            serviceMapParams.put("weTransMeasureId", weTransMeasureId);
        }

        serviceMapParams.put("origTransValue", weTransValue);
        String defaultOrganizationPartyId = (String) getManager().getContext().get(E.defaultOrganizationPartyId.name());
        serviceMapParams.put("defaultOrganizationPartyId", defaultOrganizationPartyId);

        return baseCrudInterface("WorkEffortTransactionIndicatorView", CrudEvents.OP_CREATE, serviceMapParams);
    }

    /**
     * Aggiornamento di un movimento
     * @param acctgTransEntry 
     * @param mov
     * @return
     * @throws GeneralException
     */
    private Map<String, Object> updateAcctgTransEntry(GenericValue acctgTransEntry, GenericValue mov) throws GeneralException {

        String weTransEntryId = acctgTransEntry.getString("entryAcctgTransEntrySeqId");
        String weTransId = acctgTransEntry.getString(E.acctgTransId.name());

        String msg = "Trying to update mov weTransId " + weTransId + " - weTransEntryId " + weTransEntryId;
        addLogInfo(msg);

        Map<String, Object> serviceMapParams = UtilMisc.toMap(E.weTransAccountId.name(), glAccountId, "weTransValue", weTransValue, "weTransComment", mov.getString("note"), "weTransComments", mov.getString("comments"), E.weTransAccountId.name(), glAccountId, "weTransWeId", weTransWeId, "weTransTypeValueId", glFiscalTypeId, E.fromDateCompetence.name(), mov.getTimestamp(E.fromDateCompetence.name()), E.toDateCompetence.name(), mov.getTimestamp(E.toDateCompetence.name()), "entryRoleTypeId", entryRoleTypeId, "entryPartyId", entryPartyId, E.roleTypeId.name(), roleTypeId, E.partyId.name(), partyId, "weTransCurrencyUomId", weTransCurrencyUomId, E.customTimePeriodId.name(), customTimePeriodId, "acctgTransTypeId", acctgTransTypeId, "weTransEntryId", weTransEntryId, "weTransId", weTransId, E.acctgTransId.name(), acctgTransEntrySeqId, "acctgTransEntrySeqId", acctgTransEntrySeqId);
        addMultiLangFields(serviceMapParams, mov);
        
        if (!ValidationUtil.isEmptyOrNA(weTransProductId)) {
            serviceMapParams.put("weTransProductId", weTransProductId);
        }
        if (UtilValidate.isNotEmpty(weTransMeasureId)) {
            serviceMapParams.put("weTransMeasureId", weTransMeasureId);
        }

        serviceMapParams.put("origTransValue", weTransValue);
        String defaultOrganizationPartyId = (String) getManager().getContext().get(E.defaultOrganizationPartyId.name());
        serviceMapParams.put("defaultOrganizationPartyId", defaultOrganizationPartyId);

        return baseCrudInterface("WorkEffortTransactionIndicatorView", "UPDATE", serviceMapParams);
    }
    
    /**
     * gestisce i campi nel caso multi lingua
     * @param serviceMapParams
     * @param mov
     */
    private void addMultiLangFields(Map<String, Object> serviceMapParams, GenericValue mov) {
    	if (isMultiLang()) {
    		serviceMapParams.put("weTransCommentLang", mov.getString("noteLang"));
    		serviceMapParams.put("weTransCommentsLang", mov.getString("commentsLang"));
    	}
    }

}
