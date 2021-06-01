package com.mapsengineering.base.standardimport.helper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.find.AcctgTransFindServices;
import com.mapsengineering.base.find.PartyFindServices;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.AcctgTransInterfaceConstants;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Helper for AcctgTrans
 *
 */
public class AcctgTransInterfaceHelper implements AcctgTransInterfaceConstants {

    private Delegator delegator;
    private GenericValue product;
    private GenericValue workEffort;
    private GenericValue workEffortMeasure;
    private GenericValue glAccount;
    private String defaultOrganizationPartyId;

    /**
     * Constructor
     * @param delegetor
     */
    public AcctgTransInterfaceHelper(Delegator delegetor, String defaultOrganizationPartyId) {
        this.delegator = delegetor;
        this.defaultOrganizationPartyId = defaultOrganizationPartyId;
    }

    /**
     * 
     * @param partyCode
     * @param manager
     * @return
     * @throws GeneralException
     */
    public String getEntryPartyId(String partyCode) throws GeneralException {
        String entryPartyIdLocal = "";
        // Altra Destinazione
        if (!ValidationUtil.isEmptyOrNA(partyCode)) {
            EntityCondition partyParentRoleCondition = 
                    EntityCondition.makeCondition( //
                            EntityCondition.makeCondition(E.roleTypeId.name(), EntityOperator.LIKE, "GOAL%"), //
                            EntityOperator.OR, //
                            EntityCondition.makeCondition(E.roleTypeId.name(), EntityOperator.EQUALS, E.ORGANIZATION_UNIT.name()) //
                            
                    );
            
            PartyFindServices partyFindServices = new PartyFindServices(this.delegator);
            entryPartyIdLocal = partyFindServices.getPartyId(partyCode, partyParentRoleCondition);
        }
        return entryPartyIdLocal;
    }

    /**
     * Search PartyParentRole with parentRoleCode = uorgCode and roleTypeId = parentRoleTypeId, if uorgCode is not null or _NA_,
     * return partyId
     * 
     * @param uorgCode
     * @param partyIdLocal
     * @return
     * @throws GeneralException
     */
    public String getPartyId(String uorgCode, String parentRoleTypeId) throws GeneralException {
        String partyIdLocal = "";
        if (!ValidationUtil.isEmptyOrNA(uorgCode)) {
            
            PartyFindServices partyFindServices = new PartyFindServices(this.delegator);
            partyIdLocal = partyFindServices.getPartyId(uorgCode, parentRoleTypeId);
        }
        return partyIdLocal;
    }

    /**
     * 
     * @param manager
     * @param mov
     * @param partyId
     * @param PARENT_ROLE_TYPE_ID_DEFAULT
     * @return
     * @throws GeneralException
     */
    public String doImportRole(String uorgRoleTypeId, String partyId, String parentRoleTypeIdDefault) throws GeneralException {
        // 1.4 Recuperare roleTypeId tramite uorgRoleTypeId		
        String roleTypeId = "";
        if (UtilValidate.isEmpty(uorgRoleTypeId)) {
            EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap(E.partyId.name(), partyId));

            if (UtilValidate.isNotEmpty(parentRoleTypeIdDefault)) {
                condition = EntityCondition.makeCondition(condition, EntityCondition.makeCondition(UtilMisc.toMap("parentRoleTypeId", parentRoleTypeIdDefault)));
            }

            List<GenericValue> roleTypes = this.delegator.findList(E.PartyRole.name(), condition, null, null, null, false);
            GenericValue roleType = EntityUtil.getFirst(roleTypes);
            if (UtilValidate.isNotEmpty(roleType)) {
                roleTypeId = roleType.getString(E.roleTypeId.name());
            }
        } else {
            GenericValue partyRole = this.delegator.findOne(E.PartyRole.name(), UtilMisc.toMap(E.partyId.name(), partyId, E.roleTypeId.name(), uorgRoleTypeId), true);
            if (UtilValidate.isNotEmpty(partyRole)) {
                roleTypeId = uorgRoleTypeId;
            }
        }
        return roleTypeId;
    }

    /**
     * 
     * @param manager
     * @param externalValue
     * @param inputEnumId
     * @throws GeneralException
     */
    public void findWorkEffortByCode(ImportManager manager, GenericValue externalValue, String inputEnumId) throws GeneralException {
        this.workEffort = null;
        if (INPUT_ENUM_ID_OBIETTIVO.equals(inputEnumId)) {
            this.workEffort = findWorkEffortByCode(manager, externalValue.getString("workEffortCode"));
        }
    }

    /**
     * 
     * @param manager
     * @param workEffortCode
     * @return
     * @throws GeneralException
     */
    public GenericValue findWorkEffortByCode(ImportManager manager, String workEffortCode) throws GeneralException {
        GenericValue gv = null;

        if (!ValidationUtil.isEmptyOrNA(workEffortCode)) {
            EntityCondition conditionWorkEffort = EntityCondition.makeCondition(UtilMisc.toMap(E.sourceReferenceId.name(), workEffortCode, E.workEffortSnapshotId.name(), null));
            List<GenericValue> workEffortList = manager.getDelegator().findList("WorkEffort", conditionWorkEffort, null, null, null, false);

            gv = EntityUtil.getFirst(workEffortList);
        }
        return gv;
    }

    /**
     * 
     * @param manager
     * @param externalValue
     * @param inputEnumId
     * @throws GeneralException
     */
    public void findProductByCode(ImportManager manager, GenericValue externalValue, String inputEnumId) throws GeneralException {
        this.product = null;
        if (INPUT_ENUM_ID_PRODOTTO.equals(inputEnumId)) {
            String productCode = externalValue.getString("productCode");

            if (!ValidationUtil.isEmptyOrNA(productCode)) {
                EntityCondition conditionProduct = EntityCondition.makeCondition(E.productMainCode.name(), productCode);
                List<GenericValue> productList = manager.getDelegator().findList("Product", conditionProduct, null, null, null, false);

                this.product = EntityUtil.getFirst(productList);
            }
        }
    }

    /**
     * 
     * @param externalValue
     * @param transactionDate
     * @param glFiscalTypeIdActual
     * @param glAccountId
     * @return
     * @throws GeneralException
     */
    public Map<String, String> getConditionIsUpdate(GenericValue externalValue, Timestamp transactionDate, String glFiscalTypeIdActual, String glAccountId) throws GeneralException {
        String uorgCode = externalValue.getString(E.uorgCode.name());
        String partyIdLocal = getPartyId(uorgCode, PARENT_ROLE_TYPE_ID_DEFAULT);

        // e' il partyId dell' acctgTransEntry
        String partyCode = externalValue.getString(E.partyCode.name());
        String entryPartyIdLocal = getEntryPartyId(partyCode);

        String glFiscalTypeIdLocal = externalValue.getString(E.glFiscalTypeId.name());
        if (ValidationUtil.isEmptyOrNA(glFiscalTypeIdLocal)) {
            glFiscalTypeIdLocal = glFiscalTypeIdActual;
        }
        AcctgTransFindServices acctgTransFindServices = new AcctgTransFindServices(delegator, defaultOrganizationPartyId);
        
        String productId = null;
        //entryProductId
        if (UtilValidate.isNotEmpty(product)) {
            productId = product.getString(E.productId.name());
        }
        
        String workEffortId = null;
        if (UtilValidate.isNotEmpty(workEffort)) {
            workEffortId =  workEffort.getString(E.workEffortId.name());
        }
        
        String workEffortMeasureId = null;
        String voucherRef = externalValue.getString(E.voucherRef.name());
        if (!ValidationUtil.isEmptyOrNA(voucherRef)) {
            workEffortMeasureId = voucherRef;
        }
        if (UtilValidate.isNotEmpty(workEffortMeasure)) {
            workEffortMeasureId = workEffortMeasure.getString(E.workEffortMeasureId.name());
        }
        
        return acctgTransFindServices.getConditionIsUpdate(partyIdLocal, entryPartyIdLocal, transactionDate, glFiscalTypeIdLocal, workEffortMeasureId, workEffortId, glAccountId, productId);
    }

    public GenericValue getProduct() {
        return product;
    }

    public GenericValue getWorkEffort() {
        return workEffort;
    }

    public void setWorkEffort(GenericValue workEffort) {
        this.workEffort = workEffort;
    }

    public GenericValue getWorkEffortMeasure() {
        return workEffortMeasure;
    }

    public void setWorkEffortMeasure(GenericValue workEffortMeasure) {
        this.workEffortMeasure = workEffortMeasure;
    }

    public GenericValue getGlAccount() {
        return glAccount;
    }

    public void setGlAccount(GenericValue glAccount) {
        this.glAccount = glAccount;
    }

}
