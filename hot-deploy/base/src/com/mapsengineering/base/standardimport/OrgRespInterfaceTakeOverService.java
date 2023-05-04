package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.enumeration.OrgRespInterfaceFieldEnum;
import com.mapsengineering.base.standardimport.party.PartyRelationshipCleanConditionsBuilder;
import com.mapsengineering.base.standardimport.party.PartyRelationshipCleaner;
import com.mapsengineering.base.util.DateUtilService;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogLog;
import com.mapsengineering.base.util.ValidationUtil;

/**
 * Manage Organization interface
 *
 */
public class OrgRespInterfaceTakeOverService extends AbstractPartyTakeOverService {

    public static final String MODULE = OrgRespInterfaceTakeOverService.class.getName();

    private String partyId;
    private String orgRoleTypeId;
    
    private PartyRelationshipCleaner partyRelationshipCleaner;

    @Override
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        partyRelationshipCleaner = new PartyRelationshipCleaner(this);
        
        // Responsabile solo per ORGANIZATION_UNIT
        partyId = super.initLocalValuePartyParentRole((String)extLogicKey.get(OrgRespInterfaceFieldEnum.orgCode.name()), E.ORGANIZATION_UNIT.name());
    }

    @Override
    public void doImport() throws GeneralException {
    	setImported(true);
        String msg;

        GenericValue gv = getExternalValue();
        if (checkRefDate(gv)) {
        	setImported(false);
            return;
        }
        orgRoleTypeId = gv.getString(OrgRespInterfaceFieldEnum.orgRoleTypeId.name());
        doImportPartyManager();
        
        msg = "END IMPORT " + partyId;
        addLogInfo(msg);
    }

    /**
     * 
     * @throws GeneralException
     */
    protected void doImportPartyManager() throws GeneralException {
        GenericValue gv = getExternalValue();
        
        // import person
        GenericValue responsible = findResponsible(gv);

        if (UtilValidate.isNotEmpty(responsible)) {
            findRoleResponsible(responsible, gv);
        }
        
        // OrgUnit cessata, quindi fa scadere la relazione con responsabile
        if(UtilValidate.isNotEmpty(gv.getTimestamp(OrgRespInterfaceFieldEnum.thruDate.name()))) {
            setRespoRelationshipThruDate(E.ORG_RESPONSIBLE.name(), partyId, orgRoleTypeId, null, gv.getTimestamp(OrgRespInterfaceFieldEnum.thruDate.name()), "", "");
        }
        //if responsible is _NA_ remove all responsible
        if(ValidationUtil._NA_.equals(gv.getString(OrgRespInterfaceFieldEnum.responsibleCode.name()))) {
            Timestamp refDate = new Timestamp(DateUtilService.getPreviousDay(gv.getTimestamp(OrgRespInterfaceFieldEnum.refDate.name())).getTime());
            // far scadere la relaz per il responsabile se questo e' null oppure non e presetne sul db
            setRespoRelationshipThruDate(E.ORG_RESPONSIBLE.name(), partyId, orgRoleTypeId, null, refDate, "", "");
        }
    }

    /**
     * 
     * @param responsible
     * @param gv
     * @throws GeneralException
     */
    private void findRoleResponsible(GenericValue responsible, GenericValue gv) throws GeneralException {
        // Cerco ruolo per responsabile
        ImportManager manager = getManager();
        String responsibleRoleTypeId = "";
        if (UtilValidate.isNotEmpty(gv.getString(OrgRespInterfaceFieldEnum.roleTypeId.name()))) {
        	responsibleRoleTypeId = gv.getString(OrgRespInterfaceFieldEnum.roleTypeId.name());
        } else {
            GenericValue partyRelationshipRoleResponsible = getPartyRelationshipRoleResponsible(responsible, gv);
            responsibleRoleTypeId = partyRelationshipRoleResponsible.getString("roleTypeValidTo");
        }
        
        deleteResponsibleRelationships(gv, responsible.getString(E.partyId.name()), responsibleRoleTypeId);
        
        String msg = "Found role " + responsibleRoleTypeId + " for responsible " + responsible.getString(E.partyId.name());
        addLogInfo(msg);

        // far scadere la relaz per il responsabile prima di crearne di nuove, altrimenti il servizio controlloUnicoFoglio va in Eccezione
        String comments = UtilValidate.isNotEmpty(gv.getString(OrgRespInterfaceFieldEnum.comments.name())) ? gv.getString(OrgRespInterfaceFieldEnum.comments.name()) : "";
        Timestamp thruDate = new Timestamp(DateUtilService.getPreviousDay(gv.getTimestamp(OrgRespInterfaceFieldEnum.refDate.name())).getTime());
        setRespoRelationshipThruDate(E.ORG_RESPONSIBLE.name(), partyId, orgRoleTypeId, responsible.getString(E.partyId.name()), thruDate, comments, responsibleRoleTypeId);

        List<EntityCondition> responsibleCondList = new ArrayList<EntityCondition>();
        responsibleCondList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyId));
        responsibleCondList.add(EntityCondition.makeCondition(E.roleTypeIdFrom.name(), orgRoleTypeId));
        responsibleCondList.add(EntityCondition.makeCondition(E.thruDate.name(), null));
        responsibleCondList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.ORG_RESPONSIBLE.name()));
        responsibleCondList.add(EntityCondition.makeCondition(E.partyIdTo.name(), responsible.getString(E.partyId.name())));
        responsibleCondList.add(EntityCondition.makeCondition(E.roleTypeIdTo.name(), responsibleRoleTypeId));    
        if (UtilValidate.isNotEmpty(comments)) {
        	responsibleCondList.add(EntityCondition.makeCondition(E.comments.name(), comments));
        }

        List<GenericValue> respRelationList = manager.getDelegator().findList(E.PartyRelationship.name(), EntityCondition.makeCondition(responsibleCondList), null, null, null, false);

        if (UtilValidate.isEmpty(respRelationList)) {
            Map<String, Object> pMap = UtilMisc.toMap(E.partyIdFrom.name(), partyId, E.partyRelationshipTypeId.name(), E.ORG_RESPONSIBLE.name(), E.roleTypeIdFrom.name(), orgRoleTypeId, E.partyIdTo.name(), responsible.getString(E.partyId.name()), E.roleTypeIdTo.name(), responsibleRoleTypeId, E.fromDate.name(), gv.getTimestamp(OrgRespInterfaceFieldEnum.refDate.name()), E.comments.name(), comments);
            Map<String, Object> verifyMap = new HashMap<String, Object>();
            verifyMap.putAll(pMap);
            verifyMap.put("userLogin", manager.getUserLogin());
            runSync("verifyPartyRoleExist", verifyMap, "verify Party Role Exist", "Error in PartyRole VERIFY ", false);

            msg = "Creating RELATIONSHIP: " + pMap;
            addLogInfo(msg);
            runSyncCrudWarning(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_CREATE, pMap, "Responsible Relationship" + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_ERROR_CREATE + "Responsible Relationship");
        }

    }
    
    /**
     * ritorna il ruolo del responsabile
     * @param responsible
     * @param gv
     * @return
     * @throws GeneralException
     */
    private GenericValue getPartyRelationshipRoleResponsible(GenericValue responsible, GenericValue gv) throws GeneralException {
    	ImportManager manager = getManager();
    	
    	List<EntityCondition> partyRelationshipRoleResponsibleConditions = new ArrayList<EntityCondition>();
    	partyRelationshipRoleResponsibleConditions.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.ORG_RESPONSIBLE.name()));
    	partyRelationshipRoleResponsibleConditions.add(EntityCondition.makeCondition(E.roleTypeValidFrom.name(), orgRoleTypeId));
    	
    	List<GenericValue> responsibleRoles = manager.getDelegator().findList(E.PartyRole.name(), EntityCondition.makeCondition(E.partyId.name(), responsible.getString(E.partyId.name())), null, null, null, false);
    	if (UtilValidate.isNotEmpty(responsibleRoles)) {
    		List<Object> roleList = EntityUtil.getFieldListFromEntityList(responsibleRoles, E.roleTypeId.name(), true);
    		if (UtilValidate.isNotEmpty(roleList)) {
    			partyRelationshipRoleResponsibleConditions.add(EntityCondition.makeCondition(E.roleTypeValidTo.name(), EntityOperator.IN, roleList));
    		}
    	}
    	
    	List<GenericValue> partyRelationshipRoleResponsibleList = manager.getDelegator().findList(E.PartyRelationshipRole.name(), EntityCondition.makeCondition(partyRelationshipRoleResponsibleConditions), null, null, null, false);
        String msg = "";
    	if (UtilValidate.isEmpty(partyRelationshipRoleResponsibleList)) {
            msg = "Can not find the responsible's role for relationship ORG_RESPONSIBLE and Organization Unit role " + orgRoleTypeId;
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        if (partyRelationshipRoleResponsibleList.size() > 1) {
            msg = "Can not find an unique role for responsible " + responsible.getString(E.partyId.name());
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        
        return EntityUtil.getFirst(partyRelationshipRoleResponsibleList);
    }
    
    /**
     * cancella le relazioni resp a parita di refDate
     * @param gv
     * @param responsibleId
     * @param responsibleRoleTypeId
     * @throws GeneralException
     */
    private void deleteResponsibleRelationships(GenericValue gv, String responsibleId, String responsibleRoleTypeId) throws GeneralException {
        PartyRelationshipCleanConditionsBuilder profilesConditionsBuilder = new PartyRelationshipCleanConditionsBuilder(E.ORG_RESPONSIBLE.name(), gv.getTimestamp(E.refDate.name()), partyId, "", "", responsibleId, orgRoleTypeId, responsibleRoleTypeId, "", "");
        partyRelationshipCleaner.cleanFromRelationships(profilesConditionsBuilder, "", true, true);
    }

    /** set thruDate for different relation, like responsible
     * @param notPartyIdTo = id or null */
    protected void setRespoRelationshipThruDate(String partyRelationshipTypeId, String partyIdFrom, String roleTypeIdFrom, String notPartyIdTo, Timestamp thruDate, String comments, String roleTypeIdTo) throws GeneralException {
        ImportManager manager = getManager();
        List<EntityCondition> condList = new ArrayList<EntityCondition>();
        condList.add(EntityCondition.makeCondition(E.thruDate.name(), null));
        condList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), partyRelationshipTypeId));
        condList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), partyIdFrom));
        if (UtilValidate.isNotEmpty(comments) || UtilValidate.isNotEmpty(roleTypeIdTo)) {
        	List<EntityCondition> orCondList = new ArrayList<EntityCondition>();
        	orCondList.add(EntityCondition.makeCondition(E.partyIdTo.name(), EntityOperator.NOT_EQUAL, notPartyIdTo));
        	if (UtilValidate.isNotEmpty(comments)) {
        		orCondList.add(EntityCondition.makeCondition(E.comments.name(), EntityOperator.NOT_EQUAL, comments));
        	}
        	if (UtilValidate.isNotEmpty(roleTypeIdTo)) {
        		orCondList.add(EntityCondition.makeCondition(E.roleTypeIdTo.name(), EntityOperator.NOT_EQUAL, roleTypeIdTo));
        	}
        	condList.add(EntityCondition.makeCondition(orCondList, EntityJoinOperator.OR));
        } else {
        	condList.add(EntityCondition.makeCondition(E.partyIdTo.name(), EntityOperator.NOT_EQUAL, notPartyIdTo));
        }
        condList.add(EntityCondition.makeCondition(E.roleTypeIdFrom.name(), roleTypeIdFrom));
        List<GenericValue> partyRelList = manager.getDelegator().findList(E.PartyRelationship.name(), EntityCondition.makeCondition(condList), null, null, null, false);
        String msg = "Found " + partyRelList.size() + " responsible for condition " + EntityCondition.makeCondition(condList) + " to disabled";
        addLogInfo(msg);
        for (GenericValue relation : partyRelList) {
            Map<String, Object> parametersMap = UtilMisc.toMap(E.partyIdFrom.name(), relation.getString(E.partyIdFrom.name()), E.roleTypeIdFrom.name(), relation.getString(E.roleTypeIdFrom.name()), E.partyIdTo.name(), relation.getString(E.partyIdTo.name()), E.roleTypeIdTo.name(), relation.getString(E.roleTypeIdTo.name()), E.partyRelationshipTypeId.name(), relation.getString(E.partyRelationshipTypeId.name()), E.fromDate.name(), relation.getTimestamp(E.fromDate.name()), E.thruDate.name(), thruDate);
            runSyncCrudWarning(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_UPDATE, parametersMap, E.PartyRelationship.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_PROBLEM_UPDATE + E.PartyRelationship.name());
        }
    }

    /**
     * Ricera del responsabile
     * @param gv
     * @return
     * @throws GeneralException 
     */
    private GenericValue findResponsible(GenericValue gv) throws GeneralException {
        // 4 Relazione con Responsabile
        String msg = "Trying to import Responsible party with code " + gv.getString(OrgRespInterfaceFieldEnum.responsibleCode.name());
        addLogInfo(msg);

        Map<String, Object> parameters = UtilMisc.toMap(OrgRespInterfaceFieldEnum.orgCode.name(), gv.get(OrgRespInterfaceFieldEnum.orgCode.name()), OrgRespInterfaceFieldEnum.responsibleCode.name(), (Object)gv.getString(OrgRespInterfaceFieldEnum.responsibleCode.name()));
        JobLogLog noRespFound = new JobLogLog().initLogCode("StandardImportUiLabels", "NO_RESP_FOUND", parameters, getManager().getLocale());
        JobLogLog foundMore = new JobLogLog().initLogCode("StandardImportUiLabels", "FOUND_MORE", parameters, getManager().getLocale());
        
        String organizationId = (String) getManager().getContext().get(E.defaultOrganizationPartyId.name());
        List<EntityCondition> partyParentRoleCondList = new ArrayList<EntityCondition>();
        partyParentRoleCondList.add(EntityCondition.makeCondition(E.parentRoleCode.name(), gv.getString(OrgRespInterfaceFieldEnum.responsibleCode.name())));
        partyParentRoleCondList.add(EntityCondition.makeCondition(E.roleTypeId.name(), E.EMPLOYEE.name()));
        partyParentRoleCondList.add(EntityCondition.makeCondition(E.organizationId.name(), organizationId));

        return findOneWarning(E.PartyParentRole.name(), EntityCondition.makeCondition(partyParentRoleCondList), foundMore, noRespFound);
    }
}
