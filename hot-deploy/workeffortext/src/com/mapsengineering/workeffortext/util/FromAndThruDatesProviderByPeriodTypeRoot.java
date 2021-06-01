package com.mapsengineering.workeffortext.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;


/**
 * estrae i campi fromDate e thruDate in base alla periodicita della scheda se attivo il filtro su periodo padre
 * @author nito
 *
 */
public class FromAndThruDatesProviderByPeriodTypeRoot {
	private Map<String, Object> context;
	private Delegator delegator;
	private GenericValue workEffortType;
	private Timestamp paramSearchDate;
	
	private Timestamp fromDate;
	private Timestamp thruDate;
	
	
	/**
	 * costruttore
	 * @param context
	 * @param delegator
	 * @param workEffortType
	 * @param paramSearchDate
	 */
	public FromAndThruDatesProviderByPeriodTypeRoot(Map<String, Object> context, Delegator delegator, GenericValue workEffortType, Timestamp paramSearchDate) {
		this.context = context;
		this.delegator = delegator;
		this.workEffortType = workEffortType;
		this.paramSearchDate = paramSearchDate;
	}
	
	/**
	 * 
	 * @throws GeneralException
	 */
	public void run() throws GeneralException {
		List<EntityCondition> condList = buildConditions();
		List<GenericValue> periodList = delegator.findList(E.CustomTimePeriodAndParentView.name(), EntityCondition.makeCondition(condList), null, null, null, false);
		setDates(EntityUtil.getFirst(periodList));
	}
	
	/**
	 * imposta le date
	 * se trovo il periodo e questo ha periodo padre imposta le date da periodo padre
	 * se trovo il periodo senza padre imposta le date da periodo
	 * altrimenti imposta le date da anno solare 
	 * @param period
	 */
	private void setDates(GenericValue period) {
		if (UtilValidate.isEmpty(period)) {
			fromDate = UtilDateTime.getYearEnd(paramSearchDate, (TimeZone) context.get(E.timeZone.name()), (Locale) context.get(E.locale.name()));
			thruDate = UtilDateTime.getYearStart(paramSearchDate, (TimeZone) context.get(E.timeZone.name()), (Locale) context.get(E.locale.name()));
			return;
		}
		if (UtilValidate.isNotEmpty(period.getString(E.parentPeriodId.name()))) {
			fromDate = period.getTimestamp(E.parentThruDate.name());
			thruDate = period.getTimestamp(E.parentFromDate.name());
			return;
		}
		fromDate = period.getTimestamp(E.thruDate.name());
		thruDate = period.getTimestamp(E.fromDate.name());
	}
	
	/**
	 * costruisce condizioni di ricerca in base a data imputata e periodicita scheda
	 * @return
	 */
	private List<EntityCondition> buildConditions() {
		List<EntityCondition> condList = FastList.newInstance();
		condList.add(EntityCondition.makeCondition(E.periodTypeId.name(), workEffortType.getString(E.periodTypeId.name())));
		condList.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, paramSearchDate));
		condList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, paramSearchDate));
		return condList;
	}


	public Timestamp getFromDate() {
		return fromDate;
	}

	public Timestamp getThruDate() {
		return thruDate;
	}
	
}
