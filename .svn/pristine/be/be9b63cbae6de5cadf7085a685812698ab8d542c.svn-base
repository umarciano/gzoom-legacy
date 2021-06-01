package com.mapsengineering.workeffortext.scorecard.helper;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.scorecard.BaseConverter;
import com.mapsengineering.workeffortext.scorecard.E;
import com.mapsengineering.workeffortext.scorecard.MessageCode;

/**
 * UomRangeValues Helper
 *
 */
public class UomRangeValuesHelper extends BaseConverter {

    public static final String MODULE = UomRangeValuesHelper.class.getName();

    private JobLogger jLogger;

    /**
     * Constructor
     * @param delegator
     */
    public UomRangeValuesHelper(Delegator delegator) {
        super(delegator);
        jLogger = new JobLogger(MODULE);
    }

    /** Search UomRangeValues with uomRangeId, sort with orderBy, return the fieldName value: fromValue or ThruValue */
    public Double searchUomRangeAndValues(String uomRangeId, String fieldName, String workEffortMeasureId, String workEffortId, List<String> orderBy) {
        if (UtilValidate.isNotEmpty(uomRangeId)) {
            try {
                return getUomRangeValueFromList(uomRangeId, fieldName, orderBy);
            } catch (GenericEntityException gee) {
                addMessage(gee.getMessage(), workEffortMeasureId, workEffortId, uomRangeId);
            }
        }
        return null;
    }

    private void addMessage(String message, String workEffortMeasureId, String workEffortId, String uomRangeId) {
        try {
            GenericValue glAcc = getDelegator().findOne(E.WorkEffortMeasureAndGlAccountView.name(), UtilMisc.toMap(E.workEffortMeasureId.name(), workEffortMeasureId), false);
            String accountCode = glAcc.getString(E.accountCode.name());
            GenericValue wrk = getDelegator().findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
            String sourceReferenceId = wrk.getString(E.sourceReferenceId.name());

            GenericValue uomRange = getDelegator().findOne("UomRange", UtilMisc.toMap(E.uomRangeId.name(), uomRangeId), false);
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Error while search uom range values for uom range \"%s\": %s", uomRange != null ? uomRange.getString(E.description.name()) : uomRangeId, message), "012", sourceReferenceId, accountCode, null));
        } catch (GenericEntityException gee) {
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Error while search uom range values for uom range id \"%s\": %s", uomRangeId, gee.getMessage()), "012", null, null, null));
        }
    }

    /** Search UomRangeValues with uomRangeId, sort with orderBy, return the fieldName value: fromValue or ThruValue */
    private Double getUomRangeValueFromList(String uomRangeId, String fieldName, List<String> orderBy) throws GenericEntityException {
        Double value = null;

        GenericValue rangeValue = EntityUtil.getFirst(getUomRangeValueList(uomRangeId, orderBy));
        
        if (UtilValidate.isNotEmpty(rangeValue)) {
            value = rangeValue.getDouble(fieldName);
        }
        return value;
    }

    /** Search UomRangeValues with uomRangeId, sort with orderBy, return the fromValue or ThruValue*/
    private List<GenericValue> getUomRangeValueList(String uomRangeId, List<String> orderBy) throws GenericEntityException {
        List<GenericValue> values = FastList.newInstance();
        if (UtilValidate.isNotEmpty(uomRangeId)) {
            values = getDelegator().findList(E.UomRangeValues.name(), EntityCondition.makeCondition(E.uomRangeId.name(), uomRangeId), null, orderBy, null, true);
        }
        return values;
    }

    private double getRangeValueFactor(double refValue, String uomRangeId, String sourceReferenceId, String accountCode) throws GenericEntityException {
        List<GenericValue> values = getUomRangeValueList(uomRangeId, UtilMisc.toList(E.uomRangeValuesId.name()));
        for (GenericValue value : values) {
            double from = value.getDouble(E.fromValue.name());
            double thru = value.getDouble(E.thruValue.name());
            if (refValue >= from && refValue <= thru) {
                jLogger.addMessage(ServiceLogger.makeLogInfo("For this workEffort type the value " + refValue + " is converted with its range values factor " + value.getDouble(E.rangeValuesFactor.name()), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));

                return value.getDouble(E.rangeValuesFactor.name());
            }
        }

        return refValue;
    }

    /** Search UomRangeValues and return the rangeValueFactor */
    private double searchRangeValuesFactor(double refValue, String uomRangeId, String sourceReferenceId, String accountCode) {
        if (UtilValidate.isNotEmpty(uomRangeId)) {
            GenericValue uomRange = null;
            try {
                uomRange = getDelegator().findOne("UomRange", UtilMisc.toMap(E.uomRangeId.name(), uomRangeId), false);
                return getRangeValueFactor(refValue, uomRangeId, sourceReferenceId, accountCode);
            } catch (GenericEntityException gee) {
                jLogger.addMessage(ServiceLogger.makeLogError(String.format("Error while search uom range values for uom range \"%s\": %s", uomRange != null ? uomRange.getString(E.description.name()) : uomRangeId, gee.getMessage()), "012", sourceReferenceId, accountCode, null));
            }
        }

        return refValue;
    }

    /**Return orderBy with fromValue or -thruValue*/
    public List<String> getOrderBy(String fieldName) {
        List<String> orderBy = new FastList<String>();
        orderBy.add(fieldName);
        if (E.thruValue.name().equals(fieldName)) {
            orderBy.add("-" + fieldName);
        }
        return orderBy;
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }

    /** Return workEffortType and check if applyScoreRange = Y and uomRangeScoreId is not null*/
    public Double checkApplyScoreRange(double amount, String workEffortId, String sourceReferenceId, String accountCode) throws GenericEntityException {
        Double uomRangeValue = null; 
        GenericValue workEffortType = getWorkeffortType(workEffortId, sourceReferenceId, accountCode);
        if("Y".equals(workEffortType.getString(E.applyScoreRange.name()))) {
            jLogger.addMessage(ServiceLogger.makeLogInfo("This workEffort type apply score range ", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));
            uomRangeValue = searchRangeValuesFactor(amount, workEffortType.getString(E.uomRangeScoreId.name()), sourceReferenceId, accountCode);
        }
        return uomRangeValue;
    }

    /** Return workEffortType */
    public GenericValue getWorkeffortType(String workEffortId, String sourceReferenceId, String accountCode) {
        GenericValue workEffort = null;
        GenericValue workEffortType = null;
        if (UtilValidate.isNotEmpty(workEffortId)) {
            try {
                workEffort = getDelegator().findOne("WorkEffort", UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
                workEffortType = getDelegator().findOne("WorkEffortType", UtilMisc.toMap(E.workEffortTypeId.name(), workEffort.getString(E.workEffortTypeId.name())), false);
            } catch (GenericEntityException gee) {
                jLogger.addMessage(ServiceLogger.makeLogError(String.format("Error while workEffort type for workEffort \"%s\": %s", workEffortId, gee.getMessage()), "012", sourceReferenceId, accountCode, null));
            }
        }

        return workEffortType;
    }
}
