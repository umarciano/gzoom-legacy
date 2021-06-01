package com.mapsengineering.base.delete.party;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import com.mapsengineering.base.services.GenericServiceLoop;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;

/**
 * Delete a workEffort root with its child
 * @author dain
 *
 */
public class PartyPhysicalDelete extends GenericServiceLoop {

    public static final String MODULE = PartyPhysicalDelete.class.getName();
    public static final String SERVICE_NAME = "PartyPhysicalDelete";
    public static final String SERVICE_TYPE = "PARTY_DELETE";

    /**
     * Delete workeffort root with child
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> partyPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
        PartyPhysicalDelete obj = new PartyPhysicalDelete(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public PartyPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE, MODULE);
    }

    

    /**
     * Find and delete workeffort with same workEffortParentId
     * @throws Exception
     */
    protected void execute() throws Exception {
        String partyId = (String)context.get(E.partyId.name());
        addLogInfo("Execute delete of party with " + partyId);
        EntityCondition condParty = EntityCondition.makeCondition(E.partyId.name(), partyId);

        EntityCondition condTo = EntityCondition.makeCondition(E.partyIdTo.name(), partyId);
        getListAndRemove(E.PartyRelationship.name(), condTo, partyId);

        EntityCondition condFrom = EntityCondition.makeCondition(E.partyIdFrom.name(), partyId);
        getListAndRemove(E.PartyRelationship.name(), condFrom, partyId);
        
        getListAndRemove(E.PartyRole.name(), condParty, partyId);
        getListAndRemove(E.PartyParentRole.name(), condParty, partyId);
        getListAndRemove(E.PartyStatus.name(), condParty, partyId);
        getListAndRemove(E.PartyHistory.name(), condParty, partyId);
        getListAndRemove(E.PartyNameHistory.name(), condParty, partyId);
        getListAndRemove(E.PartyAttribute.name(), condParty, partyId);
        getListAndRemove(E.PartyContactMechPurpose.name(), condParty, partyId);
        getListAndRemove(E.WorkEffortPartyAssignment.name(), condFrom, partyId);
        
        
        getListToListAndRemove(E.PartyNote.name(), condParty, partyId, E.noteId.name(), E.NoteData.name());
        getListToListAndRemove(E.PartyContent.name(), condParty, partyId, E.contentId.name(), E.Content.name());
        
        
        getPartyContactMechAndRemove(E.PartyContactMech.name(), condParty, partyId);
            
        
        GenericValue partytoDelete = findOne(E.Party.name(), condParty, "", partyId);
        if ("PARTY_GROUP".equals(partytoDelete.getString(E.partyTypeId.name()))) {
            GenericValue personToDelete = findOne(E.PartyGroup.name(), condParty, "", partyId);
            personToDelete.remove();
        } else {
            GenericValue partygroupToDelete = findOne(E.Person.name(), condParty, "", partyId);
            partygroupToDelete.remove();
        }
        partytoDelete.remove();
        addLogInfo("Party with " + partyId + " succeful delete");
    }
    
    private void getListToListAndRemove(String entityName, EntityCondition entityCondition, String partyId, String nameFiled, String entityNameIter) throws GeneralException {
    	List<GenericValue> toDeleteList = findList(entityName, entityCondition, false, partyId);
    	addLogInfo("Deleted entityName= " + entityName + " with entityCondition= " + entityCondition + "  and size= " + toDeleteList.size());
    	for (GenericValue toDelete : toDeleteList) {
    		toDelete.remove();
    		
            EntityCondition condition = EntityCondition.makeCondition(nameFiled, toDelete.getString(nameFiled));
            getListAndRemove(entityNameIter, condition, partyId);
            
        }
    }
    
    private void getPartyContactMechAndRemove(String entityName, EntityCondition entityCondition, String partyId) throws GeneralException {
    	List<GenericValue> toDeleteList = findList(entityName, entityCondition, false, partyId);
    	addLogInfo("Deleted entityName= " + entityName + " with entityCondition= " + entityCondition + "  and size= " + toDeleteList.size());
    	for (GenericValue toDelete : toDeleteList) {
    		toDelete.remove();
    		
            EntityCondition condition = EntityCondition.makeCondition(E.contactMechId.name(), toDelete.getString(E.contactMechId.name()));
            getListAndRemove(E.TelecomNumber.name(), condition, partyId);
            getListAndRemove(E.ContactMechAttribute.name(), condition, partyId);
            getListAndRemove(E.PostalAddress.name(), condition, partyId);
            getListAndRemove(E.ContactMech.name(), condition, partyId);    
            
        }
    }
    
}
