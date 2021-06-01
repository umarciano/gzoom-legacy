package com.mapsengineering.base.standardimport.helper;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.standardimport.util.jobloglog.JobLogLogCreator;
import com.mapsengineering.base.standardimport.util.jobloglog.JobLogLogPartyParentRoleCreator;
import com.mapsengineering.base.standardimport.util.jobloglog.JobLogLogPartyRoleCreator;
import com.mapsengineering.base.util.JobLogLog;

/**
 * manage party and roles
 * @author nito
 *
 */
public class PartyRoleHelper {
	
	private TakeOverService takeOverService;
	private static final String resourceName = "StandardImportUiLabels";
	
	public PartyRoleHelper(TakeOverService takeOverService) {
		this.takeOverService = takeOverService;
	}
	
    /**
     * Ricerca e ritorna un unico PartyRole con 
     * @param partyId
     * @param roleTypeId
     * @param parentRoleTypeId
     * @return GenericValue partyRole
     * @throws GeneralException se non esiste o ne trova piu di uno
     */
    public GenericValue checkValidityPartyRole(String partyId, String roleTypeId, String parentRoleTypeId, String sourceId) throws GeneralException {
        GenericValue gv = takeOverService.getExternalValue();
        
        EntityCondition condition = getValidityPartyRoleConditions(partyId, roleTypeId, parentRoleTypeId);
        
        JobLogLogCreator jobLogLogPartyRoleCreator = new JobLogLogPartyRoleCreator(resourceName, partyId, roleTypeId, sourceId);
        jobLogLogPartyRoleCreator.run();
        JobLogLog partyRoleNotUnique = jobLogLogPartyRoleCreator.getNotUniqueJobLogLog(takeOverService.getManager().getLocale());
        JobLogLog noPartyRoleFound = jobLogLogPartyRoleCreator.getNotFoundJobLogLog(takeOverService.getManager().getLocale());
        
        return takeOverService.findOne(E.PartyRole.name(), condition, partyRoleNotUnique, noPartyRoleFound, takeOverService.getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
    }
    
    /**
     * 
     * @param partyId
     * @param roleTypeId
     * @param parentRoleTypeId
     * @return
     */
    private EntityCondition getValidityPartyRoleConditions(String partyId, String roleTypeId, String parentRoleTypeId) {
        List<EntityCondition> conditions = FastList.newInstance();
        if (UtilValidate.isNotEmpty(parentRoleTypeId)) {
        	conditions.add(EntityCondition.makeCondition(E.parentRoleTypeId.name(), parentRoleTypeId));
        }
        
        if (UtilValidate.isNotEmpty(partyId)) {
        	conditions.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        }
        
        if (UtilValidate.isNotEmpty(roleTypeId)) {
        	conditions.add(EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));
        }
        
        return EntityCondition.makeCondition(conditions);    	
    }
	
    /**
     * Ricerca e restituisce partyId dato parentRoleCode e roleTypeId
     * @param parentRoleCode
     * @return partyId
     * @throws GeneralException
     */
    public String checkValidityPartyParentRole(String parentRoleCode, String roleTypeId, String sourceId) throws GeneralException {
        GenericValue gv = takeOverService.getExternalValue();
        
        EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition(E.parentRoleCode.name(), parentRoleCode), EntityCondition.makeCondition(E.roleTypeId.name(), roleTypeId));
        
        JobLogLogCreator jobLogLogPartyParentRoleCreator = new JobLogLogPartyParentRoleCreator(resourceName, parentRoleCode, roleTypeId, sourceId);
        jobLogLogPartyParentRoleCreator.run();
        JobLogLog partyParentRoleNotUnique = jobLogLogPartyParentRoleCreator.getNotUniqueJobLogLog(takeOverService.getManager().getLocale());
        JobLogLog noPartyParentRoleFound = jobLogLogPartyParentRoleCreator.getNotFoundJobLogLog(takeOverService.getManager().getLocale());
        
        GenericValue partyParentRole = takeOverService.findOne(E.PartyParentRole.name(), condition, partyParentRoleNotUnique, noPartyParentRoleFound, takeOverService.getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
        return partyParentRole.getString(E.partyId.name());
    }
    
    /**
     * Ricerca e restituisce partyId dato parentRoleCode e roleTypeId = ORGANIZATION_UNIT
     * @param parentRoleCode
     * @return partyId
     * @throws GeneralException
     */
    public String checkValidityPartyParentRole(String parentRoleCode, String sourceId) throws GeneralException {
        return checkValidityPartyParentRole(parentRoleCode, E.ORGANIZATION_UNIT.name(), sourceId);
    }

}
