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

import com.mapsengineering.base.birt.util.UtilDateTime;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.AcctgTransInterfaceConstants;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * ImportAmount Helper
 *
 */
public class ImportAmountHelper implements AcctgTransInterfaceConstants {
    private final TakeOverService service;
    private ImportManager manager;
    private String entityName;
    private GenericValue externalValue;
    private GenericValue glAccount;

    private String uomId;
    private String uomTypeId;
    private Double weTransValue;

    /**
     * 
     * @param service
     * @param manager
     * @param entityName
     * @param externalValue
     * @param glAccount
     */
    public ImportAmountHelper(TakeOverService service, ImportManager manager, String entityName, GenericValue externalValue, GenericValue glAccount) {
        this.service = service;
        this.manager = manager;
        this.externalValue = externalValue;
        this.glAccount = glAccount;
        this.entityName = entityName;
    }

    /**
     * Check amount or amountCode with uomType
     * @throws GeneralException
     */
    public void doImportAmount() throws GeneralException {
        findUomType();
        checkOnlyAmountOrAmountCode();

        Double amount = externalValue.getDouble(E.amount.name());
        String amountCode = externalValue.getString(E.amountCode.name());

        if (UtilValidate.isNotEmpty(amount) || ! ValidationUtil.isEmptyOrNA(amountCode)) {
            if (UOM_TYPE_DATE_MEASURE.equals(uomTypeId)) {
                checkAndGetAmountForDateMeasureUom(amount, amountCode);
                return;
            }
            if (UOM_TYPE_RATING_SCALE.equals(uomTypeId)) {
                checkAndGetAmountForRatingScaleUom(amount, amountCode);
                return;
            }
            if (!ValidationUtil.isEmptyOrNA(amountCode)) {
                String msg = "amount not found for uomTypeId " + uomTypeId;
                throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
            weTransValue = amount;       	
        }
    }

    /**
     * prende la misura del glAccount    
     * @throws GeneralException
     */
    private void findUomType() throws GeneralException {
        GenericValue uom = manager.getDelegator().getRelatedOne(E.Uom.name(), glAccount);
        checkUom(uom);

        uomId = uom.getString(E.uomId.name());
        uomTypeId = uom.getString(E.uomTypeId.name());
    }

    /**
     * calcola il valore dell'amount o quello corrispondente alla data dell'amountCode per tipo DATE_MEASURE. In caso di errori eccezione
     * @param amount
     * @param amountCode
     * @throws GeneralException
     */
    private void checkAndGetAmountForDateMeasureUom(Double amount, String amountCode) throws GeneralException {
        if (!ValidationUtil.isEmptyOrNA(amountCode)) {
            int number = UtilDateTime.dateConvertNumber(amountCode, manager.getTimeZone(), manager.getLocale());

            if (number <= 0) {
                String msg = "amountCode " + amountCode + " for uomTypeId " + uomTypeId + " " + STR_IS_NOT_VALID;
                throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
            weTransValue = new Double(Integer.valueOf(number).doubleValue());
        } else {
            weTransValue = amount;
        }
        addLogInfoAmountCode(amountCode);
    }

    /**
     * calcola il valore dell'amount o quello corrispondente alla description dell'amountCode per tipo RATING_SCALE. In caso di errori eccezione
     * @param amount
     * @param amountCode
     * @throws GeneralException
     */
    private void checkAndGetAmountForRatingScaleUom(Double amount, String amountCode) throws GeneralException {
        GenericValue uomRatingScale = getRatingScaleUom(amount, amountCode);
        weTransValue = uomRatingScale.getDouble(E.uomRatingValue.name());

        addLogInfoAmountCode(amountCode);
    }

    /**
     * estrae la UomRatingScale per il tipo RATING_SCALE
     * @param amount
     * @param amountCode
     * @return
     * @throws GeneralException
     */
    private GenericValue getRatingScaleUom(Double amount, String amountCode) throws GeneralException {
        if (!ValidationUtil.isEmptyOrNA(amountCode)) {
            EntityCondition conditions = EntityCondition.makeCondition(UtilMisc.toMap(E.uomId.name(), uomId, E.description.name(), amountCode));
            Set<String> fieldsToSelect = UtilMisc.toSet(E.uomId.name(), E.uomRatingValue.name(), E.description.name());

            List<GenericValue> uomRatingScaleList = manager.getDelegator().findList("UomRatingScale", conditions, fieldsToSelect, null, null, false);

            return checkUomRatingScaleAmountCode(uomRatingScaleList, amountCode);
        }
        Map<String, ? extends Object> conditionsMap = UtilMisc.toMap(E.uomId.name(), uomId, E.uomRatingValue.name(), amount);
        GenericValue uomRatingScale = manager.getDelegator().findOne("UomRatingScale", conditionsMap, false);

        checkUomRatingScaleAmount(uomRatingScale, amount);
        return uomRatingScale;
    }

    /**
     * 
     * @param uom
     * @throws GeneralException
     */
    private void checkUom(GenericValue uom) throws GeneralException {
        if (UtilValidate.isEmpty(uom)) {
            String msg = "No defaultUomId found for glAccountCode " + glAccount.getString(E.accountCode.name());
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * controlla che sia valorizzato uno e uno solo dei campi amount e amountCode
     * @throws GeneralException
     */
    private void checkOnlyAmountOrAmountCode() throws GeneralException {
        String amount = externalValue.getString(E.amount.name());
        String amountCode = externalValue.getString(E.amountCode.name());

        if (!ValidationUtil.isEmptyOrNA(amount) && !ValidationUtil.isEmptyOrNA(amountCode)) {
            String msg = "Only amount or amountCode for glAccountCode " + glAccount.getString(E.accountCode.name());
            msg += " must be specified, but not both";
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * controlla che esista uno e un solo UomRatingScale per amountCode in input
     * @param uomRatingScaleList
     * @param amountCode
     * @return
     * @throws GeneralException
     */
    private GenericValue checkUomRatingScaleAmountCode(List<GenericValue> uomRatingScaleList, String amountCode) throws GeneralException {
        GenericValue uomRatingScale = EntityUtil.getFirst(uomRatingScaleList);
        if (UtilValidate.isEmpty(uomRatingScaleList) || UtilValidate.isEmpty(uomRatingScale)) {
            String msg = "No UomRatingScale found for amountCode  " + amountCode + ", uomId " + uomId + " and uomTypeId " + UOM_TYPE_RATING_SCALE;
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        if (uomRatingScaleList.size() > 1) {
            String msg = "More than one UomRatingScale found for amountCode  " + amountCode + ", uomId " + uomId + " and uomTypeId " + UOM_TYPE_RATING_SCALE;
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }

        return uomRatingScale;
    }

    /**
     * controlla che esista uno e un solo UomRatingScale per amount in input
     * @param uomRatingScale
     * @param amount
     * @throws GeneralException
     */
    private void checkUomRatingScaleAmount(GenericValue uomRatingScale, Double amount) throws GeneralException {
        if (UtilValidate.isEmpty(uomRatingScale)) {
            String msg = "No UomRatingScale found for amount  " + amount + ", uomId " + uomId + " and uomTypeId " + UOM_TYPE_RATING_SCALE;
            throw new ImportException(entityName, externalValue.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * 
     * @param amountCode
     */
    private void addLogInfoAmountCode(String amountCode) {
        if (!ValidationUtil.isEmptyOrNA(amountCode)) {
            String msg = "Setting amount " + weTransValue + " for amountCode " + amountCode + " and uomTypeId " + uomTypeId;
            service.addLogInfo(msg);
        }
    }

    public Double getWeTransValue() {
        return weTransValue;
    }

}
