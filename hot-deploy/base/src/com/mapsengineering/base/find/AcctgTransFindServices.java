package com.mapsengineering.base.find;

import java.sql.Timestamp;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.standardimport.common.AcctgTransInterfaceConstants;
import com.mapsengineering.base.standardimport.common.E;

/**
 * Generic Service for EntityCondition
 *
 */
public class AcctgTransFindServices extends BaseFindServices {
    public static final String MODULE = AcctgTransFindServices.class.getName();
    
    private String defaultOrganizationPartyId;

    /**
     * Constructor
     * @param delegator
     */
    public AcctgTransFindServices(Delegator delegator, String defaultOrganizationPartyId) {
        super(delegator);
        this.defaultOrganizationPartyId = defaultOrganizationPartyId;
    }

    /**
     * 
     * @param partyId
     * @param entryPartyId Deprecated
     * @param transactionDate
     * @param glFiscalTypeId
     * @param workEffortMeasureId
     * @param workEffortId
     * @param glAccountId
     * @param productId Deprecated
     * @return
     * @throws GeneralException
     */
    public Map<String, String> getConditionIsUpdate(String partyId, String entryPartyId, Timestamp transactionDate, String glFiscalTypeId, String workEffortMeasureId, String workEffortId, String glAccountId, String productId) throws GeneralException {
        // Query di select per update vs create
        Map<String, String> conditionIsUpdate = UtilMisc.<String, String>toMap(E.transactionDate.name(), transactionDate, E.glFiscalTypeId.name(), glFiscalTypeId, E.workEffortSnapshotId.name(), null, E.entryWorkEffortSnapshotId.name(), null);
        GenericValue glAccount = getDelegator().findOne(E.GlAccount.name(), UtilMisc.toMap(E.glAccountId.name(), glAccountId), false);
        String inputEnumId = glAccount.getString(E.inputEnumId.name());
        conditionIsUpdate.putAll(getConditionIsUpdateGlAccount(glAccountId, glAccount.getString(E.accountTypeEnumId.name())));

        GenericValue workEffortMeasure = getDelegator().findOne(E.WorkEffortMeasure.name(), UtilMisc.toMap(E.workEffortMeasureId.name(), workEffortMeasureId), false);
        GenericValue workEffort = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);

        if (AcctgTransInterfaceConstants.INPUT_ENUM_ID_PRODOTTO.equals(inputEnumId)) { //modello su prodotto                 
            conditionIsUpdate.putAll(getConditionIsUpdateProduct(partyId, workEffortMeasure.getString(E.orgUnitId.name()), entryPartyId, productId));
        } else if (AcctgTransInterfaceConstants.INPUT_ENUM_ID_OBIETTIVO.equals(inputEnumId)) { //modello su obiettivo                
            conditionIsUpdate.putAll(getConditionIsUpdateWorkEffort(workEffort, workEffortMeasure));
        } else { //no modello
            conditionIsUpdate.putAll(getConditionIsUpdateGeneral(partyId, entryPartyId, glAccount.getString(E.respCenterId.name())));
        }

        return conditionIsUpdate;
    }

    /**
     * condizioni di update per GlAccount: per i finanziari metto glAccountFinId
     * @param glAccountId
     * @throws GenericEntityException 
     */
    private Map<String, String> getConditionIsUpdateGlAccount(String glAccountId, String accountTypeEnumId) throws GenericEntityException {
        if (AcctgTransInterfaceConstants.GL_ACCOUNT_TYPE_ENUM_ID_FIN.equals(accountTypeEnumId)) {
            return UtilMisc.toMap(E.entryGlAccountFinId.name(), glAccountId);
        }
        return UtilMisc.toMap(E.entryGlAccountId.name(), glAccountId);
    }

    /**
     * condizioni di update per modello su prodotto
     * @param partyIdLocal
     * @param partyIdDefault
     * @param entryPartyIdLocal
     */
    private Map<String, String> getConditionIsUpdateProduct(String partyIdLocal, String orgUnitId, String entryPartyIdLocal, String productId) {
        Map<String, String> conditionIsUpdate = FastMap.newInstance();

        //partyId
        if (UtilValidate.isNotEmpty(partyIdLocal)) {
            conditionIsUpdate.put(E.partyId.name(), partyIdLocal);
        } else if (UtilValidate.isNotEmpty(orgUnitId)) {
            conditionIsUpdate.put(E.partyId.name(), orgUnitId);
        } else {
            conditionIsUpdate.put(E.partyId.name(), defaultOrganizationPartyId);
        }
        //entryPartyId
        if (UtilValidate.isNotEmpty(entryPartyIdLocal)) {
            conditionIsUpdate.put(E.entryPartyId.name(), entryPartyIdLocal);
        }
        //entryProductId
        if (UtilValidate.isNotEmpty(productId)) {
            conditionIsUpdate.put(E.entryProductId.name(), productId);
        }
        return conditionIsUpdate;
    }

    /**
     * condizioni di update per modello su obiettivo, cioe' workEffortMeasure e workEffortId
     * @param externalValue
     */
    private Map<String, String> getConditionIsUpdateWorkEffort(GenericValue workEffort, GenericValue workEffortMeasure) {
        Map<String, String> conditionIsUpdate = FastMap.newInstance();

        //workEffortId
        if (UtilValidate.isNotEmpty(workEffort)) {
            conditionIsUpdate.put(E.workEffortId.name(), workEffort.getString(E.workEffortId.name()));
        }
        
        //voucherRef
        conditionIsUpdate.putAll(getConditionIsUpdateVoucherRef(workEffortMeasure));

        return conditionIsUpdate;
    }

    /**
     * condizioni sul voucherRef per modello su obiettivo
     * @param externalValue
     */
    private Map<String, String> getConditionIsUpdateVoucherRef(GenericValue workEffortMeasure) {
        Map<String, String> conditionIsUpdate = FastMap.newInstance();
        
        if (UtilValidate.isNotEmpty(workEffortMeasure)) {
            conditionIsUpdate.put(E.entryVoucherRef.name(), workEffortMeasure.getString(E.workEffortMeasureId.name()));
        }

        return conditionIsUpdate;
    }

    /**
     * condizioni di update se no modello
     * @param partyIdLocal
     * @param partyIdDefault
     * @param entryPartyIdLocal
     */
    private Map<String, String> getConditionIsUpdateGeneral(String partyIdLocal, String entryPartyIdLocal, String respCenterId) {
        Map<String, String> conditionIsUpdate = FastMap.newInstance();

        //partyId
        if (UtilValidate.isNotEmpty(partyIdLocal)) {
            conditionIsUpdate.put(E.partyId.name(), partyIdLocal);
        } else {
            if (UtilValidate.isNotEmpty(respCenterId)) {
                conditionIsUpdate.put(E.partyId.name(), respCenterId);
            } else {
                conditionIsUpdate.put(E.partyId.name(), defaultOrganizationPartyId);
            }
        }

        //entryPartyId
        if (UtilValidate.isNotEmpty(entryPartyIdLocal)) {
            conditionIsUpdate.put(E.entryPartyId.name(), entryPartyIdLocal);
        }

        return conditionIsUpdate;
    }

}
