package com.mapsengineering.base.standardimport.party;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.TakeOverService;

/**
 * Controllo anomalie
 *
 */
public class PartyRelationshipCleaner {
		
	private TakeOverService takeOverService;
	
	/**
	 * gestisce la cancellazione delle relazioni per il controllo anomalie
	 * @param takeOverService
	 */
	public PartyRelationshipCleaner(TakeOverService takeOverService) {
		this.takeOverService = takeOverService;
	}
	
	/**
	 * limina le relazioni a partire dal partyIdTo, utilizzato epr controllo anomalie
	 * @param conditionsBuilder
	 * @param clean
	 * @param excludeFromDate
	 * @return
	 * @throws GeneralException
	 */
	public Map<String, Timestamp> cleanToRelationships(PartyRelationshipCleanConditionsBuilder conditionsBuilder, boolean clean, boolean excludeFromDate) throws GeneralException {
		List<EntityCondition> condList = conditionsBuilder.buildToRelConditions(clean, excludeFromDate);
		
        List<GenericValue> partyRelList = takeOverService.getManager().getDelegator().findList(E.PartyRelationship.name(), 
        		EntityCondition.makeCondition(condList), null, null, null, false);
        addLogWarning(partyRelList, condList);
        
        Map<String, Timestamp> tmpThruDate = new HashMap<String, Timestamp>();
        for (GenericValue relation : partyRelList) {
            HashMap<String, Object> mappaKey = new HashMap<String, Object>();
            mappaKey.put(E.partyRelationshipTypeId.name(), conditionsBuilder.getPartyRelationshipTypeId());
            mappaKey.put(E.partyIdTo.name(), conditionsBuilder.getPartyIdTo());
            mappaKey.put(E.fromDate.name(), conditionsBuilder.getFromDate());

            String key = takeOverService.getPkShortValueString(mappaKey);
            tmpThruDate.put(key, relation.getTimestamp(E.thruDate.name()));
            relation.remove();
        }
        return tmpThruDate;
	}
	
	/**
	 * elimina le relazioni a partire dal partyIdFrom
	 * @param conditionsBuilder
	 * @param roleTypeIdFrom
	 * @param clean
	 * @param excludeFromDate
	 * @return
	 * @throws GeneralException
	 */
	public Map<String, Timestamp> cleanFromRelationships(PartyRelationshipCleanConditionsBuilder conditionsBuilder, String roleTypeIdFrom, boolean clean, boolean excludeFromDate) throws GeneralException {
		List<EntityCondition> condList = getFromRelCondList(conditionsBuilder, roleTypeIdFrom, clean, excludeFromDate);
				
		List<GenericValue> partyRelList = takeOverService.getManager().getDelegator().findList(E.PartyRelationship.name(), 
				EntityCondition.makeCondition(condList), null, null, null, false);
		addLogWarning(partyRelList, condList);
		
		Map<String, Timestamp> tmpThruDate = new HashMap<String, Timestamp>();
        for (GenericValue relation : partyRelList) {
            HashMap<String, Object> mappaKey = new HashMap<String, Object>();
            mappaKey.put(E.partyRelationshipTypeId.name(), conditionsBuilder.getPartyRelationshipTypeId());
            mappaKey.put(E.partyIdFrom.name(), conditionsBuilder.getPartyIdFrom());
            if(! E.QUALIF.name().equals(conditionsBuilder.getPartyRelationshipTypeId())) {
            	mappaKey.put(E.roleTypeIdFrom.name(), roleTypeIdFrom);
            }
            mappaKey.put(E.fromDate.name(), conditionsBuilder.getFromDate());
            String key = takeOverService.getPkShortValueString(mappaKey);
            tmpThruDate.put(key, relation.getTimestamp(E.thruDate.name()));
            relation.remove();
        }
        return tmpThruDate;
	}
	
	/**
	 * ritorna le condizioni di cancellazione from in base al tipo relazione
	 * @param conditionsBuilder
	 * @param roleTypeIdFrom
	 * @param clean
	 * @param excludeFromDate
	 * @return
	 */
	private List<EntityCondition> getFromRelCondList(PartyRelationshipCleanConditionsBuilder conditionsBuilder, String roleTypeIdFrom, boolean clean, boolean excludeFromDate) {
		if (E.WEF_EVALUATED_BY.name().equals(conditionsBuilder.getPartyRelationshipTypeId()) 
				|| E.WEF_APPROVED_BY.name().equals(conditionsBuilder.getPartyRelationshipTypeId())) {
			return conditionsBuilder.buildEvaluatorsAndApproversFromRelConditions(roleTypeIdFrom, null, clean);
		}
		return conditionsBuilder.buildFromRelConditions(clean, excludeFromDate);
	}
	
	/**
	 * aggiunge il log
	 * @param partyRelList
	 * @param condList
	 */
	private void addLogWarning(List<GenericValue> partyRelList, List<EntityCondition> condList) {
        String msg = "Found " + partyRelList.size() + " for condition " + EntityCondition.makeCondition(condList) + " to delete (previous elaboration)";
         if(partyRelList.size() > 0) {
             takeOverService.addLogWarning(msg);
             return;
         }
         takeOverService.addLogInfo(msg);
	}
}
