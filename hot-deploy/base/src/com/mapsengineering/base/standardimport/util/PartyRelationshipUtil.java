package com.mapsengineering.base.standardimport.util;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.party.PartyRelationshipCleanConditionsBuilder;
import com.mapsengineering.base.util.FindUtilService;

/**
 * Manage PartyRelationship
 * @author dain
 *
 */
public class PartyRelationshipUtil extends BasePartyRelationshipUtil{

    /**
     * Constructor
     * @param takeOverService
     */
    public PartyRelationshipUtil(TakeOverService takeOverService) {
        super(takeOverService);
    }

    /**
     * set thruDate for relation with:
     * same partyRelationshipTypeId, 
     * same partyIdTo,
     * thruDate empty
     * @param conditionsBuilder
     * @param thruDate
     * @param isFrom
     * @throws GeneralException
     */
    public void controlloSoggettoUnicoPadre(PartyRelationshipCleanConditionsBuilder conditionsBuilder, Timestamp thruDate, boolean isFrom) throws GeneralException {
    	List<EntityCondition> condList = isFrom ? conditionsBuilder.buildFromRelConditions(false, true) : conditionsBuilder.buildToRelConditions(false, true);
    	List<GenericValue> partyRelList = getPartyRelList(condList);
    	
        for (GenericValue relation : partyRelList) {
            Map<String, Object> parametersMap = UtilMisc.toMap(E.partyIdFrom.name(), relation.getString(E.partyIdFrom.name()), E.roleTypeIdFrom.name(), relation.getString(E.roleTypeIdFrom.name()), E.partyIdTo.name(), relation.getString(E.partyIdTo.name()), E.roleTypeIdTo.name(), relation.getString(E.roleTypeIdTo.name()), E.partyRelationshipTypeId.name(), relation.getString(E.partyRelationshipTypeId.name()), E.fromDate.name(), relation.getTimestamp(E.fromDate.name()), E.thruDate.name(), thruDate);
            getTakeOverService().runSyncCrudWarning(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_UPDATE, parametersMap, E.PartyRelationship.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_PROBLEM_UPDATE + E.PartyRelationship.name());
        }

    }
    
    /**
     * ritorna lista relazioni
     * @param condList
     * @return
     * @throws GeneralException
     */
    private List<GenericValue> getPartyRelList(List<EntityCondition> condList) throws GeneralException {
    	ImportManager manager = getTakeOverService().getManager();
        List<GenericValue> partyRelList = manager.getDelegator().findList(E.PartyRelationship.name(), EntityCondition.makeCondition(condList), null, null, null, false);
        String msg = "Found " + partyRelList.size() + " for condition " + EntityCondition.makeCondition(condList) + " to delete";
        getTakeOverService().addLogInfo(msg);

        return partyRelList;
    }

    /**
     * 
     * @param partyRelationshipCleanConditionsFiller
     * @return
     * @throws GeneralException
     */
    public List<String> returnListPartyAssegnazione(PartyRelationshipCleanConditionsBuilder partyRelationshipCleanConditionsFiller) throws GeneralException {
        List<GenericValue> partyRelList = getPartyRelList(partyRelationshipCleanConditionsFiller.buildToRelConditions(false, true));
        List<String> partyAssegnazione = FastList.newInstance();
        for (GenericValue relation : partyRelList) {
            /**
             * Aggiungo alla lista tutti assegnazioni che devono essere disabilitati
             */
            partyAssegnazione.add(relation.getString(E.partyIdFrom.name()));
        }

        return partyAssegnazione;
    }

    /**
     * Check if there is a unique relationship and create it
     * @param partyRelationshipTypeId
     * @param partyIdFrom
     * @param roleTypeIdFrom
     * @param partyIdTo
     * @param roleTypeIdTo
     * @param serviceMap
     * @param successMsg
     * @param errorMsg
     * @param isRelationFrom
     * @param tmpThruDate
     * @throws GeneralException
     */
    public void controlloUnicaRelazione(String partyRelationshipTypeId, String partyIdFrom, String roleTypeIdFrom, String partyIdTo, String roleTypeIdTo, Map<String, Object> serviceMap, String successMsg, String errorMsg, boolean isRelationFrom, Map<String, Timestamp> tmpThruDate) throws GeneralException {
        ImportManager manager = getTakeOverService().getManager();
        List<EntityCondition> empRelationshipCondList = FastList.newInstance();
        empRelationshipCondList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        empRelationshipCondList.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyIdTo));
        empRelationshipCondList.add(EntityCondition.makeCondition(E.roleTypeIdTo.name(), roleTypeIdTo));
        empRelationshipCondList.add(EntityCondition.makeCondition(E.thruDate.name(), null));

        empRelationshipCondList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyIdFrom));
        empRelationshipCondList.add(EntityCondition.makeCondition(E.roleTypeIdFrom.name(), roleTypeIdFrom));

        List<GenericValue> empRelationshipList = manager.getDelegator().findList(E.PartyRelationship.name(), EntityCondition.makeCondition(empRelationshipCondList), null, null, null, false);

        if (UtilValidate.isEmpty(empRelationshipList)) {
            creazioneRelazione(serviceMap, successMsg, errorMsg, isRelationFrom, tmpThruDate);
        } else {
            String msg = "There is already in place a relationship between party " + partyIdTo + " and Organization Unit " + partyIdFrom;
            getTakeOverService().addLogInfo(msg);

            GenericValue empRelationship = EntityUtil.getFirst(empRelationshipList);
            if (isRelToUpdate(serviceMap, empRelationship)) {
            	aggiornaRelazione(serviceMap, successMsg, errorMsg, isRelationFrom, tmpThruDate);
            }
        }
    }

    /**
     * Search relationships with thruDate null or with = gv.thruDate, because the service updateParyEndDate close all PartyRelationship
     * @param evaluatorsAndApproversConditionsBuilder
     * @param roleTypeIdFrom
     * @param thruDate
     * @return
     * @throws GeneralException
     */
    public List<GenericValue> returnListEvalApprPartyRelationship(PartyRelationshipCleanConditionsBuilder evaluatorsAndApproversConditionsBuilder, String roleTypeIdFrom, Timestamp thruDate) throws GeneralException {
        ImportManager manager = getTakeOverService().getManager();

        List<EntityCondition> evalCond = evaluatorsAndApproversConditionsBuilder.buildEvaluatorsAndApproversFromRelConditions(roleTypeIdFrom, thruDate, false);
        String msg = "PartyRelationship " + EntityCondition.makeCondition(evalCond);
        getTakeOverService().addLogInfo(msg);
        return manager.getDelegator().findList(E.PartyRelationship.name(), EntityCondition.makeCondition(evalCond), null, null, null, false);
    }

    /**
     * Aggiungo nella mappa per tutti gli elmenti che sono stati disabilitati e il nuovo elemento inserito e inserisco nuovo
     * @param conditionsBuilder
     * @param roleTypeIdFrom
     * @throws GeneralException
     */
    public void controlloSoggettoUnicoEvaluatorApprover(PartyRelationshipCleanConditionsBuilder conditionsBuilder, String roleTypeIdFrom, Timestamp thruDate) throws GeneralException {
        List<GenericValue> evalList = returnListEvalApprPartyRelationship(conditionsBuilder, roleTypeIdFrom, thruDate);
        for (GenericValue evalParty : evalList) {
            String successMsg = "PartyRelationship with " + conditionsBuilder.getPartyRelationshipTypeId() + " id " + evalParty.getString(E.partyIdTo.name()) + " successfully deleted";
            String errorMsg = "Error in PartyRelationship with " + conditionsBuilder.getPartyRelationshipTypeId() + "delete ";
            Timestamp previuosDay = new Timestamp(getTakeOverService().getPreviousDay(conditionsBuilder.getFromDate()).getTime());
            Map<String, Object> serviceMap = UtilMisc.toMap(E.partyIdFrom.name(), evalParty.getString(E.partyIdFrom.name()), E.roleTypeIdFrom.name(), evalParty.getString(E.roleTypeIdFrom.name()), E.partyIdTo.name(), evalParty.getString(E.partyIdTo.name()), E.roleTypeIdTo.name(), evalParty.getString(E.roleTypeIdTo.name()), E.partyRelationshipTypeId.name(), evalParty.getString(E.partyRelationshipTypeId.name()), E.fromDate.name(), evalParty.getTimestamp(E.fromDate.name()), E.thruDate.name(), previuosDay);
            getTakeOverService().runSyncCrudWarning(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_UPDATE, serviceMap, successMsg, errorMsg);
        }
    }
    
    /**
     * aggiorno relazione se ho cambiato commento o descrizione relazione
     * @param serviceMap
     * @param empRelationship
     * @return
     */
    private boolean isRelToUpdate(Map<String, Object> serviceMap, GenericValue empRelationship) {
    	return ( UtilValidate.isNotEmpty(serviceMap.get(E.comments.name())) && ! serviceMap.get(E.comments.name()).equals(empRelationship.get(E.comments.name())) )
    			|| ( UtilValidate.isNotEmpty(serviceMap.get(E.relationshipName.name())) && ! serviceMap.get(E.relationshipName.name()).equals(empRelationship.get(E.relationshipName.name())) );
    }
}
