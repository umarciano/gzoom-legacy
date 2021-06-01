package com.mapsengineering.base.standardimport.acctg;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.AcctgTransInterfaceConstants;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.helper.AcctgTransInterfaceHelper;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Helper for import GlAccount
 *
 */
public class ImportGlAccountHelper implements AcctgTransInterfaceConstants {
    private ImportManager manager;
    private GenericValue externalValue;
    private String entityName;
    
    private final AcctgTransInterfaceHelper acctgTransInterfaceHelper;
    private final TakeOverService service;
    
    private GenericValue workEffort;
    private String entryPartyId;
    private String entryRoleTypeId;
    
    /**
     * 
     * @param manager
     * @param externalValue
     * @param entityName
     */
    public ImportGlAccountHelper (ImportManager manager, GenericValue externalValue, String entityName, AcctgTransInterfaceHelper acctgTransInterfaceHelper,
            TakeOverService service) {
        this.manager = manager;
        this.externalValue = externalValue;
        this.entityName = entityName;
        this.acctgTransInterfaceHelper = acctgTransInterfaceHelper;
        this.service = service;
    }
    
        
    /**
     * recupera il GlAccount dal glAccountCode in input
     * @return
     * @throws GeneralException
     */
    public GenericValue findGlAccountInputByCode() throws GeneralException {
        GenericValue glAcc = null;
        String glAccountCode = externalValue.getString(E.glAccountCode.name());
        
        if (!ValidationUtil.isEmptyOrNA(glAccountCode)) {
            EntityCondition conditionGlAccount = EntityCondition.makeCondition(E.accountCode.name(), glAccountCode); 
            Set<String> fieldsToSelect = UtilMisc.toSet(E.accountCode.name(), E.glAccountId.name());
            List<GenericValue> glAccountList = manager.getDelegator().findList(E.GlAccount.name(), conditionGlAccount, fieldsToSelect, null, null, false);

            glAcc = EntityUtil.getFirst(glAccountList);
            
            if(UtilValidate.isEmpty(glAcc)) {
                String msg = "No GlAccount found for glAccountCode " + glAccountCode;
                throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
        }
        return glAcc;
    }
    
    /**
     * 
     * @return
     * @throws GeneralException
     */
    public GenericValue findWorkEffortInputByCode() throws GeneralException {
        GenericValue glWe = null;
        String workEffortCode = externalValue.getString("workEffortCode");
        
        if (!ValidationUtil.isEmptyOrNA(workEffortCode)) {
            glWe = acctgTransInterfaceHelper.findWorkEffortByCode(manager, workEffortCode);
            
            if(UtilValidate.isEmpty(glWe)) {
                String msg = "No WorkEffort found for workEffortCode " + workEffortCode;
                throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
        }
        this.workEffort = glWe;
        return glWe;
    }
    
    
    /**
     * prende la Glaccount della misura e controlla che sia modello su obiettivo
     * @param glAccountId
     * @param workEffortMeasureId
     * @return
     * @throws GeneralException
     */
    public GenericValue findAndCheckGlAccountMeasure(String glAccountId, String workEffortMeasureId) throws GeneralException {
        GenericValue glAccMeasure = null;
        EntityCondition conditionGlAccount = EntityCondition.makeCondition(E.glAccountId.name(), glAccountId); 
        Set<String> fieldsToSelect = UtilMisc.toSet(E.glAccountId.name(), E.accountCode.name(), E.inputEnumId.name(), E.accountTypeEnumId.name());
        List<GenericValue> glAccountList = manager.getDelegator().findList(E.GlAccount.name(), conditionGlAccount, fieldsToSelect, null, null, false);
        
        glAccMeasure = EntityUtil.getFirst(glAccountList);
        
        if(UtilValidate.isEmpty(glAccMeasure)) {
            String msg = "No GlAccount found for WorkEffortMeasure (" + workEffortMeasureId + ")";
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        //il glAccount deve essere modello obiettivo, se non lo e' errore
        if (! INPUT_ENUM_ID_OBIETTIVO.equals(glAccMeasure.getString(E.inputEnumId.name()))) {
            String msg = "inputEnumId " + glAccMeasure.getString(E.inputEnumId.name()) + " of GlAccount for WorkEffortMeasure (" + workEffortMeasureId;
            msg += ") " +STR_IS_NOT_VALID;
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);                    
        }
        
        return glAccMeasure;
    }
    
    
   /**
    * 
    * @param glAccInput
    * @param uomDescr
    * @return
    * @throws GeneralException
    */
    public Map<String, String> getUomDescrConditions(GenericValue glAccInput, String uomDescr) throws GeneralException {
        String msg = "";
        Map<String, String> uomDescrConditions = UtilMisc.toMap(E.uomDescr.name(), uomDescr);
        
        if (UtilValidate.isNotEmpty(workEffort)) {
            uomDescrConditions.put(E.workEffortId.name(), workEffort.getString(E.workEffortId.name()));
        }
        if (UtilValidate.isNotEmpty(glAccInput)) {
            uomDescrConditions.put(E.glAccountId.name(), glAccInput.getString(E.glAccountId.name()));
        }
        if (UtilValidate.isNotEmpty(entryPartyId)) {
            uomDescrConditions.put(E.partyId.name(), entryPartyId);
        }
        if (UtilValidate.isNotEmpty(entryRoleTypeId)) {
            uomDescrConditions.put(E.roleTypeId.name(), entryRoleTypeId);
        }
        
        msg = "Find workEffortMeasure by condition " + uomDescrConditions;
        service.addLogInfo(msg);

        return uomDescrConditions;
    }
    
    /**
     * controllo la corrispondenza tra campi in input e campi della misure, per WorkEffort e GlAccount
     * @param gvInput
     * @param check
     * @param field
     * @param voucherRef
     * @param fieldErrorMsg
     * @throws GeneralException
     */
    public void chekIfGvFieldMatchesWithInput (GenericValue gvInput, String check, String field, String voucherRef, String fieldErrorMsg) throws GeneralException {
        if(UtilValidate.isNotEmpty(gvInput)) {
            String inputVal = "";
            if("g".equals(check)) { //sto controllando GlAccount input
                inputVal = gvInput.getString(E.glAccountId.name());
            }
            if("w".equals(check)) { //sto controllando WorkEffort input
                inputVal = gvInput.getString(E.workEffortId.name());
            }
            chekIfFieldMatchesWithInput(inputVal, field, voucherRef, fieldErrorMsg);
        }
    }
  
    /**
     * controllo la corrispondenza tra campi in input e campi della misure, per Altre Destinazioni
     * @param inputVal
     * @param field
     * @param voucherRef
     * @param fieldErrorMsg
     * @throws GeneralException
     */
    public void chekIfFieldMatchesWithInput (String inputVal, String field, String voucherRef, String fieldErrorMsg) throws GeneralException {
        if(! ValidationUtil.isEmptyOrNA(inputVal)) {
            if(! inputVal.equals(field)) {
                throwImportExceptionInputNotMatched(fieldErrorMsg, inputVal, field, voucherRef);
            }
        }
    }
    
    /**
     * lancio l'eccezione con messaggio di errore quando i campi in input non corrispondono con quelli della misura
     * @param fieldErrorMsg
     * @param field1
     * @param field2
     * @param voucherRef
     * @throws GeneralException
     */
    private void throwImportExceptionInputNotMatched (String fieldErrorMsg, String field1, String field2, String voucherRef) throws GeneralException {
        String msg = fieldErrorMsg + " (" + field2 + ") of WorkEffortMeasure (" + voucherRef;
        msg += ") is different from " +fieldErrorMsg + " (" + field1 + ") in input";
        throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);         
    }
    
    
    /**
     * Aggiunge il log info con il glAccount o il workeffort trovati se non in input
     * @param weTransWeId
     * @param glAccountId
     * @throws GeneralException
     */
    public void logWorkEffortOrGlAccountFoundIfNotInInput(String weTransWeId, String glAccountId) throws GeneralException {
        String voucherRef = externalValue.getString(E.voucherRef.name());
        String uomDescr = externalValue.getString(E.uomDescr.name());
        String glAccountCode = externalValue.getString(E.glAccountCode.name());
        String workEffortCode = externalValue.getString("workEffortCode");
        
        String infoLogEnd = getLogWorkEffortOrGlAccountFoundEnd(voucherRef, uomDescr);
        
        if (ValidationUtil.isEmptyOrNA(glAccountCode)) {
            String msg = "Found glAccountId " + glAccountId + " form inputEnumId " + INPUT_ENUM_ID_OBIETTIVO + " and " + infoLogEnd;
            service.addLogInfo(msg);
        }
        if (ValidationUtil.isEmptyOrNA(workEffortCode)) {
            String msg = "Found workEffortId " + weTransWeId + " form inputEnumId " + INPUT_ENUM_ID_OBIETTIVO + " and " + infoLogEnd;
            service.addLogInfo(msg);
        }
    }
    
    /**
     * 
     * @param voucherRef
     * @param uomDescr
     * @return
     */
    private String getLogWorkEffortOrGlAccountFoundEnd(String voucherRef, String uomDescr) {
        if (!ValidationUtil.isEmptyOrNA(voucherRef)) {
            return "voucherRef = " + voucherRef;
        }
        return "uomDescr = " + uomDescr;
    }


    public String getEntryPartyId() {
        return entryPartyId;
    }

    public void setEntryPartyId(String entryPartyId) {
        this.entryPartyId = entryPartyId;
    }


    public String getEntryRoleTypeId() {
        return entryRoleTypeId;
    }

    public void setEntryRoleTypeId(String entryRoleTypeId) {
        this.entryRoleTypeId = entryRoleTypeId;
    }

}
