package com.mapsengineering.base.standardimport.organization;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.TakeOverService;

public class WeMeasureDataMapBuilder {
	private TakeOverService service;
	private Map<String, Object> weRootDataMap;
	private String partyId;

	public WeMeasureDataMapBuilder(TakeOverService service, Map<String, Object> weRootDataMap, String partyId) {
		this.service = service;
		this.weRootDataMap = weRootDataMap;
		this.partyId = partyId;
	}
	
	/**
	 * costruisce mappa per import misure
	 * @return
	 * @throws GeneralException
	 */
	public List<Map<String, Object>> buildWeMeasureListDataMap() throws GeneralException {
		List<Map<String, Object>> weMeasureListDataMap = FastList.newInstance();
		List<GenericValue> glAccRoleList = getGlAccRoleList();
		if (UtilValidate.isNotEmpty(glAccRoleList)) {
			for (GenericValue glAccRole : glAccRoleList) {
				fillWeMeasureListDataMap(glAccRole, weMeasureListDataMap);
			}
		}
		
		return weMeasureListDataMap;
	}
	
	/**
	 * ritorna la lista dei GlAccountRole
	 * @return
	 * @throws GeneralException
	 */
	private List<GenericValue> getGlAccRoleList() throws GeneralException {
		List<EntityCondition> conditionList = FastList.newInstance();
		GenericValue externalValue = service.getExternalValue();
		conditionList.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
		conditionList.add(EntityCondition.makeCondition(E.accountTypeEnumId.name(), E.INDICATOR.name()));
		conditionList.add(EntityCondition.makeCondition(E.roleTypeId.name(), externalValue.getString(E.orgRoleTypeId.name())));
		conditionList.add(EntityCondition.makeCondition(E.grFromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, (Timestamp) weRootDataMap.get(E.estimatedCompletionDate.name())));
		
        List<EntityCondition> thruConditionList = FastList.newInstance();
        thruConditionList.add(EntityCondition.makeCondition(E.grThruDate.name(), GenericValue.NULL_FIELD));
        thruConditionList.add(EntityCondition.makeCondition(E.grThruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, (Timestamp) weRootDataMap.get(E.estimatedStartDate.name())));
        conditionList.add(EntityCondition.makeCondition(thruConditionList, EntityOperator.OR));
        
        return service.getManager().getDelegator().findList(E.GlAccountAndRoleView.name(), EntityCondition.makeCondition(conditionList), null, null, null, false);
	}
	
	/**
	 * popola la lista di WeMeasureInterface da importare
	 * @param glAccRole
	 * @param weMeasureListDataMap
	 */
	private void fillWeMeasureListDataMap(GenericValue glAccRole, List<Map<String, Object>> weMeasureListDataMap) {
		if (UtilValidate.isNotEmpty(glAccRole)) {
			Map<String, Object> weMeasureDataMap = FastMap.newInstance();
			weMeasureDataMap.put(E.sourceReferenceRootId.name(), weRootDataMap.get(E.sourceReferenceRootId.name()));
			weMeasureDataMap.put(E.workEffortTypeId.name(), weRootDataMap.get(E.workEffortTypeId.name()));
			weMeasureDataMap.put(E.sourceReferenceId.name(), weRootDataMap.get(E.sourceReferenceRootId.name()));
			weMeasureDataMap.put(E.workEffortMeasureCode.name(), E._NA_.name());
			weMeasureDataMap.put(E.workEffortName.name(), E._NA_.name());
			weMeasureDataMap.put(E.accountCode.name(), glAccRole.getString(E.accountCode.name()));
			weMeasureDataMap.put(E.accountName.name(), E._NA_.name());
			weMeasureDataMap.put(E.fromDate.name(), getFromDate(glAccRole.getTimestamp(E.grFromDate.name())));
			weMeasureDataMap.put(E.thruDate.name(), getThruDate(glAccRole.getTimestamp(E.grThruDate.name())));
			weMeasureDataMap.put(E.uomDescr.name(), E._NA_.name());
			
			weMeasureListDataMap.add(weMeasureDataMap);
		}
	}
	
	/**
	 * max delle fromDate
	 * @param grFromDate
	 * @return
	 */
	private Timestamp getFromDate(Timestamp grFromDate) {
		Timestamp estimatedStartDate = (Timestamp) weRootDataMap.get(E.estimatedStartDate.name());
		if (UtilValidate.isNotEmpty(estimatedStartDate) && UtilValidate.isNotEmpty(grFromDate)) {
			return estimatedStartDate.compareTo(grFromDate) >= 0 ? estimatedStartDate : grFromDate;
		}
		return null;
	}
	
	/**
	 * min delle thruDate
	 * @param grThruDate
	 * @return
	 */
	private Timestamp getThruDate(Timestamp grThruDate) {
		Timestamp estimatedCompletionDate = (Timestamp) weRootDataMap.get(E.estimatedCompletionDate.name());
		if (UtilValidate.isEmpty(grThruDate)) {
			return estimatedCompletionDate;
		}
		
		if (UtilValidate.isNotEmpty(estimatedCompletionDate)) {
			return estimatedCompletionDate.compareTo(grThruDate) <= 0 ? estimatedCompletionDate : grThruDate;
		}
		return null;
	}

}
