package com.mapsengineering.workeffortext.util;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class FromAndThruDatesProviderFromParams {
	private Map<String, Object> context;
	private Map<String, Object> parameters;
	private Delegator delegator;
	private boolean withDefaultDate;
	private boolean loadTreeRoot;
	
	private boolean enableParentPeriodFilter;
	private GenericValue workEffortType;
	private GenericValue workEffortTypePeriod;
	private Timestamp fromDate;
	private Timestamp thruDate;
	private Timestamp defaultSearchDate;
	
	
	/**
	 * estrae i campi fromDate e thruDate in base a searchDate e TypePeriod per le ricerche nei folder obiettivi/scheda
	 * da chiamare fuori dal caricamento albero scheda
	 * @param context
	 * @param parameters
	 * @param delegator
	 * @param withDefaultDate
	 */
	public FromAndThruDatesProviderFromParams(Map<String, Object> context, Map<String, Object> parameters, Delegator delegator, boolean withDefaultDate) {		
		this(context, parameters, delegator, withDefaultDate, false);
	}
	
	/**
	 * estrae i campi fromDate e thruDate in base a searchDate e TypePeriod per le ricerche nei folder obiettivi/scheda
	 * @param context
	 * @param parameters
	 * @param delegator
	 * @param withDefaultDate
	 * @param loadTreeRoot
	 */
	public FromAndThruDatesProviderFromParams(Map<String, Object> context, Map<String, Object> parameters, Delegator delegator, boolean withDefaultDate,
			boolean loadTreeRoot) {
		this.context = context;
		this.parameters = parameters;
		this.delegator = delegator;
		this.withDefaultDate = withDefaultDate;
		this.loadTreeRoot = loadTreeRoot;
		this.enableParentPeriodFilter = false;
	}
	
	/**
	 * 
	 * @throws GeneralException
	 */
	public void run() throws GeneralException {
		checkWorkEffortType();
		if(enableParentPeriodFilter || withDefaultDate) {
			checkWorkEffortTypePeriod();
		}		
		if(withDefaultDate) {
			setDefaultSearchDate();
		} else {
			setDates();
		}
	}
	
	/**
	 * estrae e verifica i campi di WorkEffortType
	 * @throws GenericEntityException
	 */
	private void checkWorkEffortType() throws GenericEntityException {
		String workEffortTypeId = getWorkEffortTypeId();
		if(UtilValidate.isNotEmpty(workEffortTypeId)) {
			workEffortType = delegator.findOne(E.WorkEffortType.name(), UtilMisc.toMap(E.workEffortTypeId.name(), workEffortTypeId), false);
			checkEnableParentPeriodFilterFromType();
		}
	}
	
	/**
	 * estrae il campo workEffortTypeId
	 * @return
	 * @throws GenericEntityException
	 */
	private String getWorkEffortTypeId() throws GenericEntityException {
		if(loadTreeRoot) {
			String workEffortTypeIdRoot = getFieldFromContext(E.workEffortTypeIdRoot.name());
			return UtilValidate.isNotEmpty(workEffortTypeIdRoot) ? workEffortTypeIdRoot : getWorkEffortTypeIdFromRoot();
		}
		return getWorkEffortTypeIdFromRoot();
	}
	
	/**
	 * estrae i dati della scheda
	 * @return
	 * @throws GenericEntityException
	 */
	private GenericValue getWorkEffortRoot() throws GenericEntityException {
		String workEffortIdRoot = getFieldFromContext(E.workEffortIdRoot.name());
		if(UtilValidate.isNotEmpty(workEffortIdRoot)) {
			return delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortIdRoot), false);			
		}
		return null;
	}
	
	/**
	 * estrae il workEffortTypeId prendendolo dai dati della scheda
	 * @return
	 * @throws GenericEntityException
	 */
	private String getWorkEffortTypeIdFromRoot() throws GenericEntityException {
		GenericValue workEffortRoot = getWorkEffortRoot();
		return UtilValidate.isNotEmpty(workEffortRoot) ? workEffortRoot.getString(E.workEffortTypeId.name()) : getFieldFromContext(E.workEffortTypeId.name());
	}
	
	/**
	 * estrae e verifica i campi di WorkEffortTypePeriod
	 * @throws GenericEntityException
	 */
	private void checkWorkEffortTypePeriod() throws GenericEntityException {
		String workEffortIdRoot = getFieldFromContext(E.workEffortIdRoot.name());
		if(UtilValidate.isNotEmpty(workEffortIdRoot)) {
			workEffortTypePeriod = delegator.findOne(E.WorkEffortAndTypePeriodAndThruDate.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortIdRoot), false);			
		}
		checkEnableParentPeriodFilterFromTypePeriod();
	}
	
	/**
	 * verifica i campi di WorkEffortType
	 * @param workEffortType
	 */
	private void checkEnableParentPeriodFilterFromType() {
		if(UtilValidate.isNotEmpty(workEffortType)) {
			enableParentPeriodFilter = E.Y.name().equals(workEffortType.getString(E.enableMultiYearFlag.name()))
					&& E.Y.name().equals(workEffortType.getString(E.parentPeriodFilter.name()));
		}
	}
	
	/**
	 * verifica parentPeriodId di WorkEffortTypePeriod
	 */
	private void checkEnableParentPeriodFilterFromTypePeriod() {
		enableParentPeriodFilter = enableParentPeriodFilter 
				&& UtilValidate.isNotEmpty(workEffortTypePeriod) 
				&& UtilValidate.isNotEmpty(workEffortTypePeriod.getString(E.parentPeriodId.name()));
	}
	
	/**
	 * valorizza i campi data
	 * @throws GeneralException
	 */
	private void setDates() throws GeneralException {
		Timestamp paramSearchDate = getParamSearchDate();
		if (UtilValidate.isNotEmpty(paramSearchDate)) {
			if (enableParentPeriodFilter) {
				setDatesByPeriodTypeRoot(paramSearchDate);
				return;
			}
			fromDate = UtilDateTime.getYearEnd(paramSearchDate, (TimeZone) context.get(E.timeZone.name()), (Locale) context.get(E.locale.name()));
			thruDate = UtilDateTime.getYearStart(paramSearchDate, (TimeZone) context.get(E.timeZone.name()), (Locale) context.get(E.locale.name()));				
		}
	}
	
	/**
	 * imposta le date in base a data imputata e periodicita scheda
	 * @param paramSearchDate
	 * @throws GeneralException
	 */
	private void setDatesByPeriodTypeRoot(Timestamp paramSearchDate) throws GeneralException {
		FromAndThruDatesProviderByPeriodTypeRoot fromAndThruDatesProviderByPeriodTypeRoot = new FromAndThruDatesProviderByPeriodTypeRoot(context, delegator, workEffortType, paramSearchDate);
		fromAndThruDatesProviderByPeriodTypeRoot.run();
		fromDate = fromAndThruDatesProviderByPeriodTypeRoot.getFromDate();
		thruDate = fromAndThruDatesProviderByPeriodTypeRoot.getThruDate();
	}
	
	/**
	 * valorizza defaultSearchDate
	 * @throws GeneralException 
	 */
	private void setDefaultSearchDate() throws GeneralException {
		Timestamp paramSearchDate = getParamSearchDate();
		if (isParamsDateRelevant(paramSearchDate)) {
			defaultSearchDate = paramSearchDate;
			return;
		}
		if (UtilValidate.isNotEmpty(workEffortTypePeriod)) {
			defaultSearchDate = E.GLFISCTYPE_ACTUAL.name().equals(workEffortTypePeriod.getString(E.glFiscalTypeEnumId.name()))
					? workEffortTypePeriod.getTimestamp(E.thruDate.name())
					: workEffortTypePeriod.getTimestamp(E.fromDate.name());
		}
	}
	
	/**
	 * per decidere se prendere la data dai params o no
	 * @param paramSearchDate
	 * @return
	 */
	private boolean isParamsDateRelevant(Timestamp paramSearchDate) {
		String fromTreeViewSearch = (String) parameters.get(E.fromTreeViewSearch.name());
		return UtilValidate.isNotEmpty(paramSearchDate) || E.Y.name().equals(fromTreeViewSearch);
	}
	
	/**
	 * recupera il field dal context o dai parameters
	 * @param fieldName
	 * @return
	 */
	private String getFieldFromContext(String fieldName) {
		return UtilValidate.isNotEmpty(context.get(fieldName)) ? (String) context.get(fieldName) : (String) parameters.get(fieldName);
	}
	
	/**
	 * recupera il searchDate dai parametri
	 * @return
	 * @throws GeneralException 
	 */
	private Timestamp getParamSearchDate() throws GeneralException {
		Object paramSearchDateObj = parameters.get(E.searchDate.name());
		if(UtilValidate.isNotEmpty(paramSearchDateObj)) {
			return (Timestamp) ObjectType.simpleTypeConvert(paramSearchDateObj, "Timestamp", null, (Locale) context.get(E.locale.name()));
		}
		return null;
	}

	public boolean isEnableParentPeriodFilter() {
		return enableParentPeriodFilter;
	}

	public Timestamp getFromDate() {
		return fromDate;
	}

	public Timestamp getThruDate() {
		return thruDate;
	}
	
	public Timestamp getDefaultSearchDate() {
		return defaultSearchDate;
	}
		
}
