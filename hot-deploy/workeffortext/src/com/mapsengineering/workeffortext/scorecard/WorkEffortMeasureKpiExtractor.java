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
public class WorkEffortMeasureKpiExtractor {
	private Delegator delegator;
	
	private String scoreCard;
	private String performance;
	private Date thruDate;
	private String weightType; 
	private String scorePeriodEnumId;
	private String scoreField;
	
	
	/**
	 * constructor
	 * @param delegator
	 * @param scoreCard
	 * @param performance
	 * @param thruDate
	 * @param weightType
	 * @param scorePeriodEnumId
	 * @param scoreField
	 */
	public WorkEffortMeasureKpiExtractor(Delegator delegator, String scoreCard, String performance, Date thruDate, String weightType, String scorePeriodEnumId, String scoreField) {
		this.delegator = delegator;
		this.scoreCard = scoreCard;
		this.performance = performance;
		this.thruDate = thruDate;
		this.weightType = weightType;
		this.scorePeriodEnumId = scorePeriodEnumId;
		this.scoreField = scoreField;
	}
	
	/**
	 * estrae le misure
	 * @return
	 * @throws GenericEntityException
	 */
	public List<GenericValue> getWorkEffortMeasureKpiList() throws GenericEntityException {
		String entityName = getWorkEffortMeasureKpiEntityName();
        List<EntityExpr> conditionList = getWorkEffortMeasureKpiConditions();
        return delegator.findList(entityName, EntityCondition.makeCondition(conditionList), null, getWorkEffortMeasureKpiOrderBy(), null, false);
	}
	
	/**
	 * definisce entityName di ricerca
	 * @return
	 */
	public String getWorkEffortMeasureKpiEntityName() {
		if (E.SCORE_PERIOD_OPEN.name().equals(scorePeriodEnumId)) {
		    return "WorkEffortMeasureKpiAndOpenPeriod";
		}
		if (E.SCORE_PERIOD_PARENT.name().equals(scorePeriodEnumId)) {
		    return "WorkEffortMeasureKpiAndOpenPeriodWithParent";
		}
		return isWeightTypeDefault() ?  "WorkEffortMeasureKpi" : "WorkEffortMeasureKpiScoreAndOther";
	}

	/**
	 * definisce le condizioni di ricerca in base a tipo peso e periodo punteggio
	 * @return
	 */
	private List<EntityExpr> getWorkEffortMeasureKpiConditions() {
        List<EntityExpr> workEffortMeasureKpiConditions = new FastList<EntityExpr>();
        workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.workEffortId.name(), scoreCard));
        workEffortMeasureKpiConditions.add(EntityCondition.makeCondition("glFiscalTypeId", performance));
        workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
        
        if (!E.SCORE_PERIOD_OPEN.name().equals(scorePeriodEnumId) && !E.SCORE_PERIOD_PARENT.name().equals(scorePeriodEnumId)) {
        	workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, thruDate)); //TODO GN-3282            
        }
        
        workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.kpiScoreWeight.name(), EntityOperator.NOT_EQUAL, null));
        
        if (! isScorePeriodDefault()) {
        	if (isWeightTypeDefault()) {
        		workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.weMeasureTypeEnumId.name(), E.WEMT_PERF.name()));
        		workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.kpiScoreWeight.name(), EntityOperator.NOT_EQUAL, null));
        		workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.kpiScoreWeight.name(), EntityOperator.NOT_EQUAL, 0));
        	} else {
        		workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.kpiOtherWeight.name(), EntityOperator.NOT_EQUAL, null));
        		workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.kpiOtherWeight.name(), EntityOperator.GREATER_THAN, 0));        		
        	}
        } else {
        	if (! isWeightTypeDefault()) {
        		workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.kpiOtherWeight.name(), EntityOperator.NOT_EQUAL, null));
        		workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.kpiOtherWeight.name(), EntityOperator.GREATER_THAN, 0));         		
        	} else {
        	    workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.kpiScoreWeight.name(), EntityOperator.NOT_EQUAL, null));
                workEffortMeasureKpiConditions.add(EntityCondition.makeCondition(E.kpiScoreWeight.name(), EntityOperator.NOT_EQUAL, 0));
        	}
        }
        
        return workEffortMeasureKpiConditions;
	}
	
	/**
	 * ordinamento lista
	 * @return
	 */
	private List<String> getWorkEffortMeasureKpiOrderBy() {
        List<String> workEffortMeasureKpiOrderBy = FastList.newInstance();
        workEffortMeasureKpiOrderBy.add(E.workEffortMeasureId.name());
        workEffortMeasureKpiOrderBy.add(E.glAccountId.name());
        workEffortMeasureKpiOrderBy.add(scoreField);
        workEffortMeasureKpiOrderBy.add(E.weScoreRangeEnumId.name());
        workEffortMeasureKpiOrderBy.add(E.weScoreConvEnumId.name());
        workEffortMeasureKpiOrderBy.add(E.weOtherGoalEnumId.name());
        workEffortMeasureKpiOrderBy.add(E.weWithoutPerf.name());
        
        return workEffortMeasureKpiOrderBy;
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
}
