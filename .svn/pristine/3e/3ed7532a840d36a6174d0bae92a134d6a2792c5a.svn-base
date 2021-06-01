package com.mapsengineering.workeffortext.scorecard;

import java.util.Date;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;

/**
 * Extractor
 *
 */
public class WorkEffortMeasureScoreKpiExtractor {
	private Delegator delegator;
	
	private String scoreCard;
	private String scoreValueType;
	private Date thruDate;
	private String weightType;
	private String scorePeriodEnumId;
	
	private List<EntityExpr> workEffortMeasureScoreKpiConditions;
	
	
	/**
	 * estrazione misure con punteggio
	 * @param delegator
	 * @param scoreCard
	 * @param scoreValueType
	 * @param thruDate
	 * @param weightType
	 * @param scorePeriodEnumId
	 */
	public WorkEffortMeasureScoreKpiExtractor(Delegator delegator, String scoreCard, String scoreValueType, Date thruDate, String weightType, String scorePeriodEnumId) {
		this.delegator = delegator;
		this.scoreCard = scoreCard;
		this.scoreValueType = scoreValueType;
		this.thruDate = thruDate;
		this.weightType = weightType;
		this.scorePeriodEnumId = scorePeriodEnumId;
	}
	
	/**
	 * estrae le misure con punteggio
	 * @return
	 * @throws GenericEntityException
	 */
	public List<GenericValue> getWorkEffortMeasureScoreKpiList() throws GenericEntityException {
		String entityName = getWorkEffortMeasureScoreKpiEntityName();
        return delegator.findList(entityName, EntityCondition.makeCondition(workEffortMeasureScoreKpiConditions), null, null, null, false);
	}
	
	/**
	 * definisce entityName di ricerca
	 * @return
	 */
	public String getWorkEffortMeasureScoreKpiEntityName() {
		if (E.SCORE_PERIOD_OPEN.name().equals(scorePeriodEnumId)) {
			return "WorkEffortMeasureScoreKpiAndOpenPeriod";
		}
		if (E.SCORE_PERIOD_PARENT.name().equals(scorePeriodEnumId)) {
			return "WorkEffortMeasureScoreKpiAndOpenPeriodWithParent";
		}
		return isWeightTypeDefault() ?  "WorkEffortMeasureScoreKpi" : "WorkEffortMeasureScoreKpiScoreAndOther";
	}
	
	/**
	 * definisce le condizioni di ricerca in base a tipo peso e misura
	 * @param workEffortMeasureId
	 */
	public void setWorkEffortMeasureScoreKpiConditions(String workEffortMeasureId, boolean noHasBudget) {
		workEffortMeasureScoreKpiConditions = new FastList<EntityExpr>();
        workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition(E.workEffortId.name(), scoreCard));
        
        if(!noHasBudget) {
            workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition("amountLocked", "Y"));       
        }
        workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition("glFiscalTypeId", scoreValueType));       
        workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition("workEffortMeasureId", workEffortMeasureId));       
        workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition(E.transactionDate.name(), thruDate));
          	
        if (! isScorePeriodDefault()) {
        	if (isWeightTypeDefault()) {
        		workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition(E.weMeasureTypeEnumId.name(), E.WEMT_PERF.name()));
        	} else {
        		workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition(E.kpiOtherWeight.name(), EntityOperator.NOT_EQUAL, null));
        		workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition(E.kpiOtherWeight.name(), EntityOperator.GREATER_THAN, 0));        		
        	}
        } else {
        	if (! isWeightTypeDefault()) {
        		workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition(E.kpiOtherWeight.name(), EntityOperator.NOT_EQUAL, null));
        		workEffortMeasureScoreKpiConditions.add(EntityCondition.makeCondition(E.kpiOtherWeight.name(), EntityOperator.GREATER_THAN, 0));         		
        	}
        }   	
	}
	
	/**
	 * definisce periodo punteggio default
	 * @return
	 */
	private boolean isScorePeriodDefault() {
		return UtilValidate.isEmpty(scorePeriodEnumId) || E.SCORE_PERIOD_ALL.name().equals(scorePeriodEnumId);
	}
	
	/**
	 * definisce tipo peso default
	 * @return
	 */
	private boolean isWeightTypeDefault() {
		return UtilValidate.isEmpty(weightType) || E.ORIG.name().equals(weightType);
	}
	
	public List<EntityExpr> getWorkEffortMeasureScoreKpiConditions() {
        return workEffortMeasureScoreKpiConditions;
	}

}
