package com.mapsengineering.base.standardimport.util;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.TakeOverService;

/**
 * Manage PartyRelationship
 * @author dain
 *
 */
public class BasePartyRelationshipUtil {

    private TakeOverService takeOverService;

    /**
     * Constructor
     * @param takeOverService
     */
    public BasePartyRelationshipUtil(TakeOverService takeOverService) {
        this.takeOverService = takeOverService;
    }

    protected void aggiornaRelazione(Map<String, Object> serviceMap, String successMsg, String errorMsg, boolean isRelationFrom, Map<String, Timestamp> tmpThruDate) throws GeneralException {
        if (isRelationFrom) {
            aggiornaRelazioneFrom(serviceMap, successMsg, errorMsg, tmpThruDate);
        } else {
            aggiornaRelazioneTo(serviceMap, successMsg, errorMsg, tmpThruDate);
        }
    }

    /**
     * Creating relationship, use thruDate of previous relationship with 
     * same partyRelationshipTypeId, 
     * same partyIdTo,
     * same fromDate
     * @param tmpThruDate 
     * 
     */
    protected void creazioneRelazione(Map<String, Object> serviceMap, String successMsg, String errorMsg, boolean isRelationFrom, Map<String, Timestamp> tmpThruDate) throws GeneralException {
        if (isRelationFrom) {
            creazioneRelazioneFrom(serviceMap, successMsg, errorMsg, tmpThruDate);
        } else {
            creazioneRelazioneTo(serviceMap, successMsg, errorMsg, tmpThruDate);
        }
    }

    /**
     * Creating relationship, use thruDate of previous relationship with 
     * same partyRelationshipTypeId, 
     * same partyIdTo,
     * same fromDate
     * @param tmpThruDate 
     * 
     */
    private void creazioneRelazioneTo(Map<String, Object> serviceMap, String successMsg, String errorMsg, Map<String, Timestamp> tmpThruDate) throws GeneralException {
        Map<String, Object> map = setBasicMapTo(serviceMap, tmpThruDate);
        String msg = "Creating relationship To " + map.get(E.partyRelationshipTypeId.name()) + " with : " + map + " ...";
        takeOverService.addLogInfo(msg);
        takeOverService.runSyncCrud(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_CREATE, map, successMsg, errorMsg, true);
    }

    /**
     * Creating relationship, use thruDate of previous relationship with 
     * same partyRelationshipTypeId, 
     * same partyIdTo,
     * same fromDate
     * @param tmpThruDate 
     * 
     */
    private void aggiornaRelazioneTo(Map<String, Object> serviceMap, String successMsg, String errorMsg, Map<String, Timestamp> tmpThruDate) throws GeneralException {
        Map<String, Object> map = setBasicMapTo(serviceMap, tmpThruDate);
        String msg = "Updating relationship To " + map.get(E.partyRelationshipTypeId.name()) + " with : " + map + " ...";
        takeOverService.addLogInfo(msg);
        takeOverService.runSyncCrud(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_UPDATE, map, successMsg, errorMsg, true);
    }
    
    private Map<String, Object> setBasicMapTo(Map<String, Object> serviceMap, Map<String, Timestamp> tmpThruDate) {
        HashMap<String, Object> mappaKey = new HashMap<String, Object>();
        mappaKey.put(E.partyRelationshipTypeId.name(), serviceMap.get(E.partyRelationshipTypeId.name()));
        mappaKey.put(E.partyIdTo.name(), serviceMap.get(E.partyIdFrom.name()));
        mappaKey.put(E.fromDate.name(), serviceMap.get(E.fromDate.name()));
        mappaKey.put(E.comments.name(), serviceMap.get(E.comments.name()));
 
        String key = takeOverService.getPkShortValueString(mappaKey);
        if (tmpThruDate.containsKey(key)) {
            serviceMap.put(E.thruDate.name(), (Timestamp)tmpThruDate.get(key));
        }
        
        return serviceMap;
    }

    /**
     * Creating relationship, use thruDate of previous relationship with 
     * same partyRelationshipTypeId, 
     * same partyIdFrom,
     * same roleTypeIdFrom,
     * same fromDate
     * 
     */
    private void creazioneRelazioneFrom(Map<String, Object> serviceMap, String successMsg, String errorMsg, Map<String, Timestamp> tmpThruDate) throws GeneralException {
        Map<String, Object> map = setBasicMapFrom(serviceMap, tmpThruDate);
        String msg = "Creating relationship From " + map.get(E.partyRelationshipTypeId.name()) + " with : " + map + " ...";
        takeOverService.addLogInfo(msg);
        takeOverService.runSyncCrud(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_CREATE, map, successMsg, errorMsg, true);
    }

    /**
     * Creating relationship, use thruDate of previous relationship with 
     * same partyRelationshipTypeId, 
     * same partyIdFrom,
     * same roleTypeIdFrom,
     * same fromDate
     * 
     */
    private void aggiornaRelazioneFrom(Map<String, Object> serviceMap, String successMsg, String errorMsg, Map<String, Timestamp> tmpThruDate) throws GeneralException {
        Map<String, Object> map = setBasicMapFrom(serviceMap, tmpThruDate);
        String msg = "Updating relationship From " + map.get(E.partyRelationshipTypeId.name()) + " with : " + map + " ...";
        takeOverService.addLogInfo(msg);
        takeOverService.runSyncCrud(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_UPDATE, map, successMsg, errorMsg, true);
    }

    private Map<String, Object> setBasicMapFrom(Map<String, Object> serviceMap, Map<String, Timestamp> tmpThruDate) {
        HashMap<String, Object> mappaKey = new HashMap<String, Object>();
        mappaKey.put(E.partyRelationshipTypeId.name(), serviceMap.get(E.partyRelationshipTypeId.name()));
        mappaKey.put(E.partyIdFrom.name(), serviceMap.get(E.partyIdFrom.name()));
        mappaKey.put(E.roleTypeIdFrom.name(), serviceMap.get(E.roleTypeIdFrom.name()));
        mappaKey.put(E.fromDate.name(), serviceMap.get(E.fromDate.name()));
        mappaKey.put(E.comments.name(), serviceMap.get(E.comments.name()));
        
        String key = takeOverService.getPkShortValueString(mappaKey);
        if (tmpThruDate.containsKey(key)) {
            serviceMap.put(E.thruDate.name(), (Timestamp)tmpThruDate.get(key));
        }
        
        return serviceMap;
    }

    /**
     * @return the takeOverService
     */
    public TakeOverService getTakeOverService() {
        return takeOverService;
    }

}
