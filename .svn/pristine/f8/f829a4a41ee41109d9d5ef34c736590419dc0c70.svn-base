package com.mapsengineering.base.standardimport.acctg;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.AcctgTransInterfaceConstants;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.WeInterfaceConstants;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Helper for validity GlAccount
 *
 */
public class GlAccountHelper implements AcctgTransInterfaceConstants {
    private final TakeOverService service;
    private String acctgTransTypeId;
    private final String glAccountId;
    private final String inputEnumId;
    private final GenericValue glAccount;
    private String weTransWeId;
    private String weTransMeasureId;
    private String weTransProductId;
    private final String partyId;
    private final String roleTypeId;
    private final String entryPartyId;
    private final String entryRoleTypeId;
    private boolean alreadyCheckedWorkeffort;

    private GenericValue product;
    private GenericValue workEffort;
    private GenericValue workEffortMeasure;

    /**
     * Constructor
     * @param service
     * @param glAccountId
     * @param partyId
     * @param roleTypeId
     * @param entryPartyId
     * @param entryRoleTypeId
     * @param inputEnumId
     * @param glAccount
     * @param product
     * @param workEffort
     * @param alreadyCheckedWorkeffort
     */
    public GlAccountHelper(TakeOverService service, String glAccountId, String partyId, String roleTypeId, String entryPartyId, String entryRoleTypeId, String inputEnumId, GenericValue glAccount, GenericValue product, GenericValue workEffort, boolean alreadyCheckedWorkeffort) {
        this.service = service;
        this.glAccountId = glAccountId;
        this.glAccount = glAccount;
        this.partyId = partyId;
        this.roleTypeId = roleTypeId;
        this.entryPartyId = entryPartyId;
        this.entryRoleTypeId = entryRoleTypeId;
        this.inputEnumId = inputEnumId;

        this.product = product;
        this.workEffort = workEffort;
        this.alreadyCheckedWorkeffort = alreadyCheckedWorkeffort;
    }

    /**
     * Set accountTypeEnumId and check productId or workEffortId,
     * @throws GeneralException
     */
    public void checkGlAccount() throws GeneralException {
        acctgTransTypeId = ACCTG_TRANS_TYPE_ID_GLA_ORU;

        // 1.11 Controllo glAccount.accountTypeEnumId con glAccountTypeEnumId
        String accountTypeEnumId = getAccountTypeEnumId();

        checkWorkEffortWithInputEnumId(accountTypeEnumId);
        checkProductWithInputEnumId();
    }

    /**
     * se modello su obiettivo verifica obiettivo, altrimenti logga in presenza di workEffortCode diverso
     * @param accountTypeEnumId
     * @throws GeneralException
     */
    protected void checkWorkEffortWithInputEnumId(String accountTypeEnumId) throws GeneralException {
        if (INPUT_ENUM_ID_OBIETTIVO.equals(inputEnumId)) {
            checkWorkEffort(accountTypeEnumId);
            return;
        }
        addLogWarning("workEffortCode", INPUT_ENUM_ID_OBIETTIVO);
    }

    private void addLogWarning(String fieldName, String inputEnumId) {
        GenericValue mov = service.getExternalValue();
        String fieldValue = mov.getString(fieldName);

        if (!ValidationUtil.isEmptyOrNA(fieldValue)) {
            String msg = "Found " + fieldName + " \"" + fieldValue + "\" in input for GlAccount with inputEnumId <> " + inputEnumId;
            service.addLogWarning(msg);
        }

    }

    /**
     * se modello su prodotto verifica prodotto, altrimenti logga in presenza di productCode diverso
     * @throws GeneralException
     */
    protected void checkProductWithInputEnumId() throws GeneralException {
        if (INPUT_ENUM_ID_PRODOTTO.equals(inputEnumId)) {
            checkProduct();
            return;
        }
        addLogWarning("productCode", INPUT_ENUM_ID_PRODOTTO);
    }

    protected String getAccountTypeEnumId() throws GeneralException {
        GenericValue mov = service.getExternalValue();
        String msg = "";
        String accountTypeEnumId = glAccount.getString("accountTypeEnumId");
        if (!accountTypeEnumId.equals(mov.get("glAccountTypeEnumId"))) {
            msg = "accountTypeEnumId " + accountTypeEnumId + " not equals glAccountTypeEnumId = " + mov.get("glAccountTypeEnumId");
            throw new ImportException(service.getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        return accountTypeEnumId;
    }

    /**
     * Validation workEffort id, workeffortType id and workeffortMeasure id, 
     * <BR/> set acctgTransTypeId = workEffortType.parentTypeId and weTransMeasureId = workEffortMeasure id
     * @param inputEnumId
     * @param accountTypeEnumId
     * @throws GeneralException
     */
    protected void checkWorkEffort(String accountTypeEnumId) throws GeneralException {
        ImportManager manager = service.getManager();
        GenericValue mov = service.getExternalValue();
        String workEffortCode = mov.getString("workEffortCode");
        String msg = "";

        // 1.12 GN-743 Se il movimento e' di tipo Finanziario il campo workEffort
        // in input non e' obbligatorio
        // ma se presente deve esistere
        if (!alreadyCheckedWorkeffort) {
            if (UtilValidate.isNotEmpty(workEffort)) {
                weTransWeId = workEffort.getString(E.workEffortId.name());
            }
            if (UtilValidate.isEmpty(workEffort) && !ValidationUtil.isEmptyOrNA(workEffortCode)) {
                Map<String, Object> parameters = UtilMisc.toMap(E.workEffortCode.name(), (Object) workEffortCode);
                JobLogLog noWorkEffortFound = new JobLogLog().initLogCode("StandardImportUiLabels", "NO_WE_FOUND_OBJ", parameters, service.getManager().getLocale());
                throw new ImportException(service.getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), noWorkEffortFound);
            }
        }
        // lo scambio tra glAccountId e referencedAccountId e' fatto dal
        // servizio
        if (UtilValidate.isEmpty(workEffort) && !GL_ACCOUNT_TYPE_ENUM_ID_FIN.equals(accountTypeEnumId)) {
            msg = "For inputEnumId " + INPUT_ENUM_ID_OBIETTIVO + " accountTypeEnumId = " + accountTypeEnumId + " the workEffort code " + workEffortCode + STR_IS_NOT_VALID;
            throw new ImportException(service.getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        if (UtilValidate.isNotEmpty(workEffort)) {
            GenericValue workEffortType = manager.getDelegator().findOne("WorkEffortType", false, "workEffortTypeId", workEffort.getString("workEffortTypeId"));
            acctgTransTypeId = workEffortType.getString("parentTypeId");

            if (!alreadyCheckedWorkeffort) {
                findWorkEffortMeasureObiettivo(mov);
            }

            addLogsWarningObiettivo();
        }
    }

    /** Validation product id and workeffortMeasure id, set acctgTransTypeId = GLA_PRD */
    protected void checkProduct() throws GeneralException {
        ImportManager manager = service.getManager();
        GenericValue mov = service.getExternalValue();
        String msg = "";
        // 1.6 b
        String productCode = mov.getString("productCode");

        if (UtilValidate.isEmpty(product)) {
            msg = "For inputEnumId = " + INPUT_ENUM_ID_PRODOTTO + " the productCode " + productCode + STR_IS_NOT_VALID;
            throw new ImportException(service.getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        weTransProductId = product.getString(E.productId.name());
        findWorkEffortMeasureProduct(manager, mov);

        acctgTransTypeId = ACCTG_TRANS_TYPE_ID_GLA_PRD;
    }

    /**
     * recupera la Misura per il Modello Obiettivo ed effettua i vari controlli
     * @param mov
     * @throws GeneralException
     */
    private void findWorkEffortMeasureObiettivo(GenericValue mov) throws GeneralException {
        Map<String, String> obiettivoCondition = getObiettivoCondition();
        addLogInfoConditionWorkeffortMeasure(obiettivoCondition);
        
        List<GenericValue> workEffortMeasureList = service.getManager().getDelegator().findList(E.WorkEffortMeasure.name(), EntityCondition.makeCondition(obiettivoCondition), null, null, null, false);
        if (UtilValidate.isNotEmpty(workEffortMeasureList) && workEffortMeasureList.size() > 1) {
        	Map<String, Object> parameters = UtilMisc.toMap(E.accountCode.name(), mov.get(E.glAccountCode.name()), E.workEffortCode.name(), mov.get(E.workEffortCode.name()));
            JobLogLog measureNotUnique = new JobLogLog().initLogCode("StandardImportUiLabels", "OBJ_NOT_UNIQUE_MEAS", parameters, service.getManager().getLocale());
            throw new ImportException(service.getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), measureNotUnique);
        }
        if (UtilValidate.isEmpty(workEffortMeasureList)) {
        	if (! ValidationUtil.isEmptyOrNA(mov.getString(E.voucherRef.name()))) {
        		workEffortMeasure = service.getManager().getDelegator().findOne(E.WorkEffortMeasure.name(), false, UtilMisc.toMap(E.workEffortMeasureId.name(), mov.getString(E.voucherRef.name())));
        	    if (UtilValidate.isNotEmpty(workEffortMeasure)) {
        	    	weTransMeasureId = workEffortMeasure.getString(E.workEffortMeasureId.name());	
        	    }
        	    return;
        	}
        	createWorkEffortMeasure();
        	return;
        }
        
        workEffortMeasure = EntityUtil.getFirst(workEffortMeasureList);
        weTransMeasureId = workEffortMeasure.getString(E.workEffortMeasureId.name());
    }
    
    /**
     * creazione misura in caso di misura non presente
     * @throws GeneralException
     */
    private void createWorkEffortMeasure() throws GeneralException {
    	Map<String, Object> workEffortMeasureServiceMapParams = FastMap.newInstance();
    	String workEffortMeasureId = WeInterfaceConstants.WORK_EFFORT_MEASURE_PREFIX + service.getManager().getDelegator().getNextSeqId(E.WorkEffortMeasure.name());
    	workEffortMeasureServiceMapParams.put(E.workEffortMeasureId.name(), workEffortMeasureId);
    	workEffortMeasureServiceMapParams.put(E.workEffortId.name(), workEffort.getString(E.workEffortId.name()));
    	workEffortMeasureServiceMapParams.put(E.glAccountId.name(), glAccountId);
    	workEffortMeasureServiceMapParams.put(E.weMeasureTypeEnumId.name(), "WEMT_PERF");
    	workEffortMeasureServiceMapParams.put(E.kpiScoreWeight.name(), 100L);
    	workEffortMeasureServiceMapParams.put(E.kpiOtherWeight.name(), 100L);
    	workEffortMeasureServiceMapParams.put(E.sequenceId.name(), 1L);
    	workEffortMeasureServiceMapParams.put(E.fromDate.name(), workEffort.get(E.estimatedStartDate.name()));
    	workEffortMeasureServiceMapParams.put(E.thruDate.name(), workEffort.get(E.estimatedCompletionDate.name()));
    	workEffortMeasureServiceMapParams.put(E.partyId.name(), entryPartyId);
    	workEffortMeasureServiceMapParams.put(E.roleTypeId.name(), entryRoleTypeId);
    	workEffortMeasureServiceMapParams.put(E.isPosted.name(), "N");
    	
    	GenericValue glAccount = service.getManager().getDelegator().findOne(E.GlAccount.name(), false, UtilMisc.toMap(E.glAccountId.name(), glAccountId));
    	if (UtilValidate.isNotEmpty(glAccount)) {
            String weOtherGoalEnumId = WeInterfaceConstants.WEMOMG_NONE;
            if (E.Y.name().equals(glAccount.getString(E.detectOrgUnitIdFlag.name()))) {
                // in realta' la gestione del WEMOMG_ORG non deve essere piu' utilizzata:
                // per sapere se il movimento va memorizzato con l'unita' organizzativa del workeffort, 
                // ci si basa sul valore di detectOrgUnitIdFlag
                weOtherGoalEnumId = WeInterfaceConstants.WEMOMG_ORG;
            }
            workEffortMeasureServiceMapParams.put(E.weOtherGoalEnumId.name(), weOtherGoalEnumId);
            workEffortMeasureServiceMapParams.put(E.weScoreRangeEnumId.name(), glAccount.getString(E.weScoreRangeEnumId.name()));
            workEffortMeasureServiceMapParams.put(E.weScoreConvEnumId.name(), glAccount.getString(E.weScoreConvEnumId.name()));
            workEffortMeasureServiceMapParams.put(E.weAlertRuleEnumId.name(), glAccount.getString(E.weAlertRuleEnumId.name()));
            workEffortMeasureServiceMapParams.put(E.weWithoutPerf.name(), glAccount.getString(E.weWithoutPerf.name()));
            workEffortMeasureServiceMapParams.put(E.periodTypeId.name(), glAccount.getString(E.periodTypeId.name()));
            workEffortMeasureServiceMapParams.put(E.uomRangeId.name(), glAccount.getString(E.uomRangeId.name()));
            workEffortMeasureServiceMapParams.put(E.detailEnumId.name(), glAccount.getString(E.detailEnumId.name()));
    	}
    	
    	service.runSyncCrud(E.crudServiceDefaultOrchestration_WorkEffortMeasure.name(), E.WorkEffortMeasure.name(), CrudEvents.OP_CREATE, workEffortMeasureServiceMapParams, E.WorkEffortMeasure.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_PROBLEM_CREATE + E.WorkEffortMeasure.name(), true, false);
		workEffortMeasure = service.getManager().getDelegator().findOne(E.WorkEffortMeasure.name(), false, UtilMisc.toMap(E.workEffortMeasureId.name(), workEffortMeasureId));
	    if (UtilValidate.isNotEmpty(workEffortMeasure)) {
	    	weTransMeasureId = workEffortMeasure.getString(E.workEffortMeasureId.name());	
	    }
    }

    /**
     * recupera la Misura per il Modello Prodotto ed effettua i vari controlli
     * @param manager
     * @param mov
     * @throws GeneralException
     */
    private void findWorkEffortMeasureProduct(ImportManager manager, GenericValue mov) throws GeneralException {
        Map<String, String> prodottoCondition = UtilMisc.toMap(E.workEffortId.name(), null, E.glAccountId.name(), glAccountId, E.productId.name(), weTransProductId);
        addLogInfoConditionWorkeffortMeasure(prodottoCondition);

        List<GenericValue> workEffortMeasureList = manager.getDelegator().findByAnd("WorkEffortMeasure", prodottoCondition);
        workEffortMeasure = EntityUtil.getFirst(workEffortMeasureList);
        if (UtilValidate.isEmpty(workEffortMeasure)) {
            String msg = "For inputEnumId = " + INPUT_ENUM_ID_PRODOTTO + " the product id " + weTransProductId + " for workEffortId null and glAccountId " + glAccountId + STR_IS_NOT_VALID;
            throw new ImportException(service.getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        weTransMeasureId = workEffortMeasure.getString("workEffortMeasureId");
    }

    /**
     * 
     * @return obiettivo
     */
    protected Map<String, String> getObiettivoCondition() throws GeneralException {
        Map<String, String> obiettivoCondition = UtilMisc.toMap(E.workEffortId.name(), weTransWeId, E.glAccountId.name(), glAccountId);

        if (UtilValidate.isNotEmpty(entryPartyId)) {
            obiettivoCondition.put(E.partyId.name(), entryPartyId);
        }
        if (UtilValidate.isNotEmpty(entryRoleTypeId)) {
            obiettivoCondition.put(E.roleTypeId.name(), entryRoleTypeId);
        }

        return obiettivoCondition;
    }

    private void addLogsWarningObiettivo() {
        addLogWarningParty();
        addLogWarningRoleType();
        addLogWarningEntryParty();
        addLogWarningEntryRoleType();
    }

    private void addLogWarningEntryRoleType() {
        String msg;
        if (!entryRoleTypeId.equals(ValidationUtil.emptyIfNull(workEffortMeasure.getString(E.orgUnitRoleTypeId.name())))) {
            msg = "The roleTypeId " + entryRoleTypeId + " is not the same in WorkEffortMeasure(" + weTransMeasureId + "), ";
            msg += workEffortMeasure.getString(E.orgUnitRoleTypeId.name());
            service.addLogWarning(msg);
        }
    }

    private void addLogWarningEntryParty() {
        GenericValue mov = service.getExternalValue();
        String msg;
        if (!entryPartyId.equals(ValidationUtil.emptyIfNull(workEffortMeasure.getString(E.orgUnitId.name())))) {
            msg = "The partyId for partyCode " + mov.getString("partyCode") + " is not the same in WorkEffortMeasure(" + weTransMeasureId + "), ";
            msg += workEffortMeasure.getString(E.orgUnitId.name());
            service.addLogWarning(msg);
        }
    }

    private void addLogWarningRoleType() {
        String msg;
        if (!roleTypeId.equals(ValidationUtil.emptyIfNull(workEffort.getString(E.orgUnitRoleTypeId.name())))) {
            msg = "The roleTypeId " + roleTypeId + " is not the same in WorkEffort(" + weTransWeId + "), ";
            msg += workEffort.getString(E.orgUnitRoleTypeId.name());
            service.addLogWarning(msg);
        }
    }

    private void addLogWarningParty() {
        GenericValue mov = service.getExternalValue();
        String msg;
        if (!partyId.equals(ValidationUtil.emptyIfNull(workEffort.getString(E.orgUnitId.name())))) {
            msg = "The partyId for uorgCode " + mov.getString("uorgCode") + " is not the same in WorkEffort(" + weTransWeId + "), ";
            msg += workEffort.getString(E.orgUnitId.name());
            service.addLogWarning(msg);
        }
    }

    /**
     * 
     * @param condition
     */
    private void addLogInfoConditionWorkeffortMeasure(Map<String, String> condition) {
        String msg = "Find workEffortMeasures by condition " + condition;
        service.addLogInfo(msg);
    }

    public String getAcctgTransTypeId() {
        return acctgTransTypeId;
    }

    public String getWeTransWeId() {
        return weTransWeId;
    }

    public void setWeTransWeId(String weTransWeId) {
        this.weTransWeId = weTransWeId;
    }

    public String getWeTransMeasureId() {
        return weTransMeasureId;
    }

    public void setWeTransMeasureId(String weTransMeasureId) {
        this.weTransMeasureId = weTransMeasureId;
    }

    public String getWeTransProductId() {
        return weTransProductId;
    }

    public GenericValue getWorkEffortMeasure() {
        return workEffortMeasure;
    }

    public void setWorkEffortMeasure(GenericValue workEffortMeasure) {
        this.workEffortMeasure = workEffortMeasure;
    }
}
