package com.mapsengineering.base.standardimport.party;

import com.mapsengineering.base.standardimport.common.E;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;

import com.mapsengineering.base.standardimport.common.TakeOverService;

public class PartyRelationshipDater {
	
	private TakeOverService takeOverService;

	/**
	 * Per valorizzare le date della relazione con i campi in input
	 * @param takeOverService
	 */
	public PartyRelationshipDater(TakeOverService takeOverService) {
		this.takeOverService = takeOverService;
	}
	
	/**
	 * valorizza il thruDate
	 * @param serviceMap
	 * @param tmpThruDate
	 * @param thruDate
	 */
	public void setThruDate(Map<String, Object> serviceMap, Map<String, Timestamp> tmpThruDate, Timestamp thruDate) {
		Timestamp fromDate = (Timestamp) serviceMap.get(E.fromDate.name());
		if (UtilValidate.isNotEmpty(fromDate)) {
			if (thruDate.compareTo(fromDate) < 0) {
				addLogWarningThruDate(serviceMap, thruDate);
				return;
			}
		}
		setTmpThruDate(serviceMap, tmpThruDate, thruDate);
	}
	
	/**
	 * imposta la mappa dei thruDate
	 * @param serviceMap
	 * @param tmpThruDate
	 * @param thruDate
	 */
	private void setTmpThruDate(Map<String, Object> serviceMap, Map<String, Timestamp> tmpThruDate, Timestamp thruDate) {
	      Map<String, Object> mappaKey = new HashMap<String, Object>();
          mappaKey.put(E.partyRelationshipTypeId.name(), serviceMap.get(E.partyRelationshipTypeId.name()));
          mappaKey.put(E.partyIdTo.name(), serviceMap.get(E.partyIdFrom.name()));
          mappaKey.put(E.fromDate.name(), serviceMap.get(E.fromDate.name()));
          mappaKey.put(E.comments.name(), serviceMap.get(E.comments.name()));

          String key = takeOverService.getPkShortValueString(mappaKey);
          tmpThruDate.put(key, thruDate);         
	}
	
	/**
	 * avvertimento in caso di thrudate minore di fromDate
	 * @param serviceMap
	 * @param thruDate
	 */
	private void addLogWarningThruDate(Map<String, Object> serviceMap, Timestamp thruDate) {
		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append("For PartyRelationship with partyIdFrom");
		msgBuilder.append(serviceMap.get(E.partyIdFrom.name()));
		msgBuilder.append(" partyIdTo ");
		msgBuilder.append(serviceMap.get(E.partyIdTo.name()));
		msgBuilder.append(" and partyRelationshipTypeId ");
		msgBuilder.append(serviceMap.get(E.partyRelationshipTypeId.name()));
		msgBuilder.append(" the thruDate ");
		msgBuilder.append(thruDate);
		msgBuilder.append("is less than fromDate");
        takeOverService.addLogWarning(msgBuilder.toString());
	}

}
