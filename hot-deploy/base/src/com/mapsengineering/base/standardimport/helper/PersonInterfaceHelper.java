package com.mapsengineering.base.standardimport.helper;


import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.FindUtilService;

/**
 * Manage EmplPositionType, PartyRelationshipRole, deletePartyRole
 *
 */
public class PersonInterfaceHelper {
	private TakeOverService takeOverService;
	private Delegator delegator;
	
	/**
	 * Constructor
	 * @param takeOverService
	 * @param dispatcher
	 * @param delegator
	 */
	public PersonInterfaceHelper(TakeOverService takeOverService, LocalDispatcher dispatcher, Delegator delegator) {
		this.takeOverService = takeOverService;
		this.delegator = delegator;
	}
	
	/**
	 * Check only if exists EmplPositionType
	 * @param emplPositionTypeId
	 * @throws GeneralException
	 */
	public void checkValidEmplPositionTypeId(String emplPositionTypeId) throws GeneralException {
		GenericValue gv = takeOverService.getExternalValue();
		String msg = "";

		if (UtilValidate.isNotEmpty(emplPositionTypeId)) {
			GenericValue emplPositionType = delegator.findOne("EmplPositionType", UtilMisc.toMap(E.emplPositionTypeId.name(), emplPositionTypeId), false);
			if (UtilValidate.isEmpty(emplPositionType)) {
				msg = "The employment position type " + gv.getString(E.emplPositionTypeId.name()) + " is not valid";
				throw new ImportException(takeOverService.getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
			}
		}
	}
	
	/**
     * 
     * @param emplPositionTypeId
     * @throws GeneralException
     */
    public String checkValidPartyRelationshipRole(String partyRelationshipTypeId, String roleTypeValidFrom, String roleTypeValidTo, String description) throws GeneralException {
        String msg = "";
     
        List<GenericValue> partyRelationshipRoleList = getPartyRelationshipRoleList(partyRelationshipTypeId, roleTypeValidFrom, roleTypeValidTo, description, true);      
        String roleTypeId = EntityUtil.getFirst(partyRelationshipRoleList).getString(E.roleTypeValidFrom.name());
        msg = "Found role " + roleTypeId + " for " + description;
        takeOverService.addLogInfo(msg);

        return roleTypeId;
    }
    
	/**
	 * deletePartyRole
	 * @param partyId
	 * @param roleTypeId
	 * @throws GeneralException
	 */
	public void deletePartyRole (String partyId, String roleTypeId) throws GeneralException {
	    String msg = "";
	    
	    GenericValue roleType = takeOverService.getManager().getDelegator().findOne("PartyRole", UtilMisc.toMap(E.partyId.name(), partyId, E.roleTypeId.name(), roleTypeId), false);
	    if (UtilValidate.isNotEmpty(roleType)) {
	        msg = "RoleTypeId " + roleTypeId + " for partyId " +partyId + FindUtilService.MSG_SUCCESSFULLY_DELETE;
	        Map<String, Object> parametersMap = UtilMisc.toMap(E.partyId.name(), (Object)partyId, E.roleTypeId.name(), E.WEM_EVAL_MANAGER.name(), "userLogin", takeOverService.getManager().getUserLogin());
	        takeOverService.runSync("deletePartyRole", parametersMap, msg, FindUtilService.MSG_ERROR_DELETE + E.PartyRole.name(), false);
	    }
	}
	
	/**
	 * 
	 * @param personRoleTypeId
	 * @return
	 * @throws GeneralException
	 */
	public List<String> getPersonInterfaceRoleList (String personRoleTypeId) throws GeneralException {
		List<String> personInterfaceRoleList = FastList.newInstance();
		List<GenericValue> partyRelationshipRoleList = getPartyRelationshipRoleList(E.PERSON_INTERFACE.name(), personRoleTypeId, "", "", false);
		for(GenericValue pr : partyRelationshipRoleList) {
			if(pr != null) {
				personInterfaceRoleList.add(pr.getString(E.roleTypeValidTo.name()));
			}
		}
		
		return personInterfaceRoleList;
	}
	
	/**
	 * gets the party rel role list
	 * @param partyRelationshipTypeId
	 * @param roleTypeValidFrom
	 * @param roleTypeValidTo
	 * @param description
	 * @param onlyOneRel
	 * @return
	 * @throws GeneralException
	 */
	private List<GenericValue> getPartyRelationshipRoleList (String partyRelationshipTypeId, String roleTypeValidFrom, String roleTypeValidTo, String description, boolean onlyOneRel) throws GeneralException {
		String msg = "";
		
        List<EntityCondition> prrCondList = FastList.newInstance();
        prrCondList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        prrCondList.add(EntityCondition.makeCondition(E.roleTypeValidFrom.name(), roleTypeValidFrom));
        if(UtilValidate.isNotEmpty(roleTypeValidTo)) {
        	prrCondList.add(EntityCondition.makeCondition(E.roleTypeValidTo.name(), roleTypeValidTo));
        }
              
        EntityCondition prrCond = EntityCondition.makeCondition(prrCondList);
        msg = "Verifying in PartyRelationshipRole: " + prrCond;
        takeOverService.addLogInfo(msg);
        
        List<GenericValue> partyRelationshipRoleList = delegator.findList(E.PartyRelationshipRole.name(), prrCond, null, null, null, false);
        if(onlyOneRel) {
        	checkOnlyOneRelationship(prrCondList, description, partyRelationshipRoleList);
        }
        
        return partyRelationshipRoleList;
	}
	
	/**
	 * checks that there is one and only one item
	 * @param prrCondList
	 * @param description
	 * @param partyRelationshipRoleList
	 * @throws GeneralException
	 */
	private void checkOnlyOneRelationship(List<EntityCondition> prrCondList, String description, List<GenericValue> partyRelationshipRoleList) throws GeneralException {
		GenericValue gv = takeOverService.getExternalValue();
		String msg = "";
		if (UtilValidate.isEmpty(partyRelationshipRoleList)) {
            msg = "Error: Can't find the role for the "+ description +": " + prrCondList;
            throw new ImportException(takeOverService.getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        } 
		if (partyRelationshipRoleList.size() > 1) {
			msg = "Error: More than one role for the "+ description +": " + prrCondList;
            throw new ImportException(takeOverService.getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
	}
	
	/**
     * do Import AllocationInterface
     * @param personCode
     * @throws GeneralException
     */
    public void importAllocationInterface(String personCode) throws GeneralException {     
        takeOverService.doImportMulti(ImportManagerConstants.ALLOCATION_INTERFACE, UtilMisc.toMap(E.personCode.name(), personCode));
    }
}
