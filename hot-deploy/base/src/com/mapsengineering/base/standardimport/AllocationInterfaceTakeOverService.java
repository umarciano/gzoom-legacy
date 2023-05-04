package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.enumeration.AllocationInterfaceFieldEnum;
import com.mapsengineering.base.standardimport.util.PersonInterfaceContext;
import com.mapsengineering.base.util.JobLogLog;

public class AllocationInterfaceTakeOverService extends AbstractPartyTakeOverService {
	
	public static final String MODULE = AllocationInterfaceTakeOverService.class.getName();
	
	private PersonInterfaceContext personInterfaceContext;

	public AllocationInterfaceTakeOverService() {
	}

	@Override
	public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
		setImported(false);
		GenericValue gv = getExternalValue();
		
		// controlla che sia caricata una person_interface
		personInterfaceContext = getPersonInterfaceContext();

		if (UtilValidate.isEmpty(personInterfaceContext) || UtilValidate.isEmpty(personInterfaceContext.getPersonCode()) || 
				!personInterfaceContext.getPersonCode().equals(gv.getString(AllocationInterfaceFieldEnum.personCode.name()))) {
			String msg = "Valid person record (PersonInterface record) required to import";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
	}

	@Override
	public void doImport() throws GeneralException {
		setImported(true);
		GenericValue gv = getExternalValue();
        String msg = "Elaborating allocation for party "+ gv.getString(AllocationInterfaceFieldEnum.personCode.name()) + " with orgUnit " + gv.getString(AllocationInterfaceFieldEnum.allocationOrgCode.name()) 
        + " from date " + gv.getString(AllocationInterfaceFieldEnum.allocationFromDate.name()) + " thru date "+ gv.getString(AllocationInterfaceFieldEnum.allocationThruDate.name());
        addLogInfo(msg);
        
        // inserimento relationship
        GenericValue partyRelGv = getManager().getDelegator().makeValue(E.PartyRelationship.name());
        
        // fromDate: MAX(interface.allocationFromDate, 01.01.AAAA(interface.refDate))
        Date allocationFromDate = gv.getTimestamp(AllocationInterfaceFieldEnum.allocationFromDate.name());
        Date allocationThruDate = gv.getTimestamp(AllocationInterfaceFieldEnum.allocationThruDate.name());
        Timestamp refDate = gv.getTimestamp(AllocationInterfaceFieldEnum.refDate.name());
        
        if(allocationFromDate.after(allocationThruDate)){
        	msg = "allocationFromDate is after allocationThruDate";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        
        Date beginYearRefDate = UtilDateTime.getYearStart(refDate);
        partyRelGv.set(E.fromDate.name(), (beginYearRefDate.after(allocationFromDate) ? beginYearRefDate : allocationFromDate));
        
        // thruDate: MIN(interface.allocationThruDate, 31.12.AAAA(interface.refDate))
        Date endYearRefDate = UtilDateTime.getYearEnd(refDate);
        partyRelGv.set(E.thruDate.name(), endYearRefDate.after(allocationThruDate) ? allocationThruDate : endYearRefDate);
        
        // roleTypeIdFrom
        if (UtilValidate.isNotEmpty(gv.getString(AllocationInterfaceFieldEnum.allocationRoleTypeId.name()))) {
        	partyRelGv.setString(E.roleTypeIdFrom.name(), gv.getString(AllocationInterfaceFieldEnum.allocationRoleTypeId.name()));
        } else { // TODO mettere junitest
        	GenericValue rtValidFromMax = findOneValue("NO_ROLETYPEIDFROM", "MORE_ROLETYPEIDFROM", E.RoleTypeValidFromOrgAllocationView.name(), 
        			EntityCondition.makeCondition(E.parentRoleCode.name(), gv.getString(AllocationInterfaceFieldEnum.allocationOrgCode.name())), gv);
        	
            if(UtilValidate.isNotEmpty(rtValidFromMax.getString(E.maxRoleTypeValidFrom.name()))) {
            	partyRelGv.setString(E.roleTypeIdFrom.name(), rtValidFromMax.getString(E.maxRoleTypeValidFrom.name()));
            } 
        }
        
        // partyIdFrom
        String organizationId = (String) getManager().getContext().get(E.defaultOrganizationPartyId.name());
        List<EntityCondition> condList = new ArrayList<EntityCondition>();
        condList.add(EntityCondition.makeCondition(E.parentRoleCode.name(), gv.getString(AllocationInterfaceFieldEnum.allocationOrgCode.name())));
        condList.add(EntityCondition.makeCondition(E.roleTypeId.name(), "ORGANIZATION_UNIT"));
        condList.add(EntityCondition.makeCondition(E.organizationId.name(), organizationId));
        
        GenericValue partyIdFromOrg = findOneValue("NO_PARTYIDFROM", "MORE_PARTYIDFROM", E.PartyParentRole.name(), 
        		EntityCondition.makeCondition(condList), gv);

        if(UtilValidate.isNotEmpty(partyIdFromOrg.getString(E.partyId.name()))) {
        	partyRelGv.setString(E.partyIdFrom.name(), partyIdFromOrg.getString(E.partyId.name()));
        } 
        
        // roleTypeIdTo
        if (UtilValidate.isNotEmpty(gv.getString(AllocationInterfaceFieldEnum.personRoleTypeId.name()))) {
        	partyRelGv.setString(E.roleTypeIdTo.name(), gv.getString(AllocationInterfaceFieldEnum.personRoleTypeId.name()));
        } else {
        	partyRelGv.setString(E.roleTypeIdTo.name(), "EMPLOYEE");
        }
        
        // partyIdTo
        condList = new ArrayList<EntityCondition>();
        condList.add(EntityCondition.makeCondition(E.parentRoleCode.name(), gv.getString(AllocationInterfaceFieldEnum.personCode.name())));
        condList.add(EntityCondition.makeCondition(E.roleTypeId.name(), "EMPLOYEE"));
        condList.add(EntityCondition.makeCondition(E.organizationId.name(), organizationId));
        
        GenericValue partyIdToOrg = findOneValue("NO_PARTYIDTO", "MORE_PARTYIDTO", E.PartyParentRole.name(), 
        		EntityCondition.makeCondition(condList), gv);
        
        if(UtilValidate.isNotEmpty(partyIdToOrg.getString(E.partyId.name()))) {
        	partyRelGv.setString(E.partyIdTo.name(), partyIdToOrg.getString(E.partyId.name()));
        } 
        
        // partyRelationshipTypeId
        partyRelGv.setString(E.partyRelationshipTypeId.name(), "ORG_ALLOCATION");
        
        // relationshipValue: Se interface.allocationValue <0 allora 0
        // Altrimenti se interface.allocationValue > 100 allora 100
        // Altrimenti interface.allocationValue
        Double allocationValue = Double.valueOf(gv.getLong(AllocationInterfaceFieldEnum.allocationValue.name()));
        Double zeroValue = Double.valueOf(0);
        Double centoValue = Double.valueOf(100);
        partyRelGv.set(E.relationshipValue.name(), allocationValue.compareTo(zeroValue) < 0 ? zeroValue : (allocationValue.compareTo(centoValue) > 0 ? centoValue : allocationValue));
        
        
        // valueUomId
        partyRelGv.setString(E.valueUomId.name(), "OTH_100");
        
        msg = "Create PartyRelatinship for "+ gv.getString(AllocationInterfaceFieldEnum.personCode.name()) + " with orgUnit " + gv.getString(AllocationInterfaceFieldEnum.allocationOrgCode.name()) 
        + " from date " + partyRelGv.getTimestamp(E.fromDate.name()) + " and thru date " + partyRelGv.getTimestamp(E.thruDate.name()) ;
        addLogInfo(msg);
        // se il record esiste gia, da eccezione
        partyRelGv.create();
        
        setLocalValue(partyRelGv);
        
        msg = "End import allocation for party "+ gv.getString(AllocationInterfaceFieldEnum.personCode.name()) + " with orgUnit " + gv.getString(AllocationInterfaceFieldEnum.allocationOrgCode.name()) 
        + " from date " + gv.getString(AllocationInterfaceFieldEnum.allocationFromDate.name());
        addLogInfo(msg);
	}
	
	private GenericValue findOneValue(String noValidLabel, String foundMoreLabel, String entityName, EntityCondition ecList, GenericValue allocationValue) throws GeneralException {
		JobLogLog noValid = new JobLogLog().initLogCode("StandardImportUiLabels", noValidLabel, null, getManager().getLocale());
        JobLogLog foundMore = new JobLogLog().initLogCode("StandardImportUiLabels", foundMoreLabel, null, getManager().getLocale());
    	
        GenericValue gValue = findOne(entityName, ecList, foundMore, 
        		noValid, E.PartyRelationship.name(), allocationValue.getString(AllocationInterfaceFieldEnum.personCode.name()));
        
        return gValue;
	}
	
	private PersonInterfaceContext getPersonInterfaceContext() {
		return (this.personInterfaceContext != null) ? this.personInterfaceContext : PersonInterfaceContext.get(getManager());
	}

}
