package com.mapsengineering.base.standardimport.party;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.TakeOverService;

public class PartyRelationshipRestorer {
	
	private TakeOverService takeOverService;

	/**
	 * ripristina le assegnazioni/appartenenze
	 * @param takeOverService
	 */
	public PartyRelationshipRestorer(TakeOverService takeOverService) {
		this.takeOverService = takeOverService;
	}
	
	/**
	 * cerca l ass/app piu recente e la ripristina
	 * @param partyIdTo
	 * @param partyRelationshipTypeId
	 * @throws GeneralException
	 */
	public void restorePartyRelationship(String partyIdTo, String partyRelationshipTypeId) throws GeneralException {
		List<EntityCondition> condActiveList = buildConditions(partyIdTo, partyRelationshipTypeId, false);
		List<GenericValue> partyRelActiveList = takeOverService.getManager().getDelegator().findList(E.PartyRelationship.name(), 
				EntityCondition.makeCondition(condActiveList), null, null, null, false);
		if (UtilValidate.isNotEmpty(partyRelActiveList)) {
			addActiveRelFoundLogWarning(partyIdTo, partyRelationshipTypeId);
			return;
		}
		
		List<EntityCondition> condList = buildConditions(partyIdTo, partyRelationshipTypeId, true);
		List<GenericValue> partyRelList = takeOverService.getManager().getDelegator().findList(E.PartyRelationship.name(), 
				EntityCondition.makeCondition(condList), null, getOrderBy(), null, false);
		
		GenericValue partyRel = EntityUtil.getFirst(partyRelList);
		if (UtilValidate.isNotEmpty(partyRel)) {
			addLogInfo(partyIdTo, partyRel.getString(E.partyIdFrom.name()), partyRel.getString(E.roleTypeIdFrom.name()), partyRelationshipTypeId);
			partyRel.set(E.thruDate.name(), null);
			partyRel.store();
		}
	}
	
	/**
	 * costruisce le condizioni di ricerca della relazione
	 * @param partyIdTo
	 * @param partyRelationshipTypeId
	 * @param withThruDate
	 * @return
	 */
	private List<EntityCondition> buildConditions(String partyIdTo, String partyRelationshipTypeId, boolean withThruDate) {
        List<EntityCondition> conditionList = FastList.newInstance();       
        conditionList.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyIdTo));
        conditionList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
		if (withThruDate) {
			conditionList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.NOT_EQUAL, GenericValue.NULL_FIELD));
		} else {
			conditionList.add(EntityCondition.makeCondition(E.thruDate.name(), GenericValue.NULL_FIELD));
		}
        
        return conditionList;
	}
	
	/**
	 * 
	 * @return
	 */
	private List<String> getOrderBy() {
		StringBuilder sbOrderBy = new StringBuilder();
		sbOrderBy.append("-");
		sbOrderBy.append(E.fromDate.name());
		return UtilMisc.toList(sbOrderBy.toString());
	}
	
	/**
	 * aggiunge log di warining in caso di relazione attiva trovata
	 * @param partyIdTo
	 * @param partyRelationshipTypeId
	 */
	private void addActiveRelFoundLogWarning(String partyIdTo, String partyRelationshipTypeId) {
		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append("Found one or more active relationships with partyRelationshipTypeId = ");
		msgBuilder.append(partyRelationshipTypeId);
		msgBuilder.append(" while reactivating party ");
		msgBuilder.append(partyIdTo);
        takeOverService.addLogWarning(msgBuilder.toString());
	}
	
	/**
	 * aggiunge log di info
	 * @param partyIdTo
	 * @param partyIdFrom
	 * @param roleTypeIdFrom
	 * @param partyRelationshipTypeId
	 */
	private void addLogInfo(String partyIdTo, String partyIdFrom, String roleTypeIdFrom, String partyRelationshipTypeId) {
		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append("Restoring relationship with partyRelationshipTypeId = ");
		msgBuilder.append(partyRelationshipTypeId);
		msgBuilder.append(" and partyIdFrom ");
		msgBuilder.append(partyIdFrom);
		msgBuilder.append(" and roleTypeIdFrom ");
		msgBuilder.append(roleTypeIdFrom);
		msgBuilder.append(" while reactivating party ");
		msgBuilder.append(partyIdTo);
        takeOverService.addLogInfo(msgBuilder.toString());
	}
}
