package com.mapsengineering.workeffortext.scorecard;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Clone Value
 *
 */
public class ValueClone {
    
    private Delegator delegator;
    
    private Double limitExcellentValue;
    private Double limitMaxValue; 
    private Double targetValue; 
    private Double limitMinValue;
    private Double actualValue; 
    private Double actualPyValue; 
    private Double limitExcellentCount;
    private Double limitMaxCount; 
    private Double targetCount; 
    private Double limitMinCount; 
    private Double actualCount; 
    private Double actualPyCount;
    
    /**
     * Constructor
     * @param delegator
     */
    public ValueClone(Delegator delegator) {
        this.delegator = delegator;
    }
    
    /**
     * 
     * @param limitExcellentValue
     * @param limitExcellentCount
     */
    public void setLimitExcellent(double limitExcellentValue, double limitExcellentCount) {
    	this.limitExcellentValue = new Double(limitExcellentValue);
    	this.limitExcellentCount = new Double(limitExcellentCount);
    }
    
    /**
     * 
     * @param limitMaxValue
     * @param limitMaxCount
     */
    public void setLimitMax(double limitMaxValue, double limitMaxCount) {
    	this.limitMaxValue = new Double(limitMaxValue);
    	this.limitMaxCount = new Double(limitMaxCount);
    }
    
    /**
     * 
     * @param targetValue
     * @param targetCount
     */
    public void setTarget(double targetValue, double targetCount) {
    	this.targetValue = new Double(targetValue);
    	this.targetCount = new Double(targetCount);
    }
    
    /**
     * 
     * @param limitMinValue
     * @param limitMinCount
     */
    public void setLimitMin(double limitMinValue, double limitMinCount) {
    	this.limitMinValue = new Double(limitMinValue);
    	this.limitMinCount = new Double(limitMinCount);
    }
    
    /**
     * 
     * @param actualValue
     * @param actualCount
     */
    public void setActual(double actualValue, double actualCount) {
    	this.actualValue = new Double(actualValue);
    	this.actualCount = new Double(actualCount);
    }
    
    /**
     * 
     * @param actualPyValue
     * @param actualPyCount
     */
    public void setActualPy(double actualPyValue, double actualPyCount) {
    	this.actualPyValue = new Double(actualPyValue);
    	this.actualPyCount = new Double(actualPyCount);
    }

    /**
     * Add item to list
     * @param entityList
     * @param lastItem
     * @return
     * @throws GenericEntityException
     */
    public List<Map<String, Object>> cloneValues(List<Map<String, Object>> entityList, Map<String, Object> lastItem) throws GenericEntityException {
        Map<String, Object> newItem = new FastMap<String, Object>(lastItem);

        // Gestione alert
        String alertFlag = "";
        Double amountValue = UtilValidate.isNotEmpty(lastItem.get(E.amount.name())) ? (Double)lastItem.get(E.amount.name()) : 0d;
        String amount = new DecimalFormat("#.#####").format(amountValue);
        List<GenericValue> rangeValueList = delegator.findByAnd(E.UomRangeValues.name(), UtilMisc.toMap(E.uomRangeId.name(), (String)lastItem.get(E.weMeasureUomId.name()), E.uomRangeValuesId.name(), amount));
        GenericValue rangeValue = EntityUtil.getFirst(rangeValueList);
        if (UtilValidate.isNotEmpty(rangeValue)) {
            alertFlag = rangeValue.getString("alert");
        }

        // Aggiungo calcolati
        newItem.put(E.limitExcellentValue.name(), limitExcellentValue);
        newItem.put(E.limitMaxValue.name(), limitMaxValue);        
        newItem.put(E.targetValue.name(), targetValue);
        newItem.put(E.limitMinValue.name(), limitMinValue); 
        newItem.put(E.actualValue.name(), actualValue);
        newItem.put(E.actualPyValue.name(), actualPyValue);
        newItem.put(E.limitExcellentCount.name(), limitExcellentCount);
        newItem.put(E.limitMaxCount.name(), limitMaxCount);
        newItem.put(E.targetCount.name(), targetCount);
        newItem.put(E.limitMinCount.name(), limitMinCount);
        newItem.put(E.actualCount.name(), actualCount);
        newItem.put(E.actualPyCount.name(), actualPyCount);
        if (UtilValidate.isNotEmpty(alertFlag)) {
            newItem.put(E.hasScoreAlert.name(), alertFlag);
        }
        if(!entityList.contains(newItem)) {
            entityList.add(newItem);
        }

        return entityList;
    }
}
