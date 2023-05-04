package com.mapsengineering.base.standardimport;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.enumeration.OrgRespInterfaceFieldEnum;
import com.mapsengineering.base.standardimport.enumeration.OrganizationInterfaceFieldEnum;
import com.mapsengineering.base.standardimport.helper.OrganizationInterfaceHelper;
import com.mapsengineering.base.standardimport.organization.WeRootImportManager;
import com.mapsengineering.base.standardimport.party.PartyRelationshipCleanConditionsBuilder;
import com.mapsengineering.base.standardimport.party.PartyRelationshipCleaner;
import com.mapsengineering.base.util.DateUtilService;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.MessageUtil;


/**
 * Manage Organization interface
 *
 */
public class OrganizationInterfaceTakeOverService extends AbstractPartyTakeOverService {

    public static final String MODULE = OrganizationInterfaceTakeOverService.class.getName();

    private String partyId;
    private String orgRoleTypeId;
    private String parentOrgId;
    private String parentRoleTypeId;
    private String parentTypeId;
    private String oldPartyName;
    private String oldPartyNameLang;

    private OrganizationInterfaceHelper organizationInterfaceHelper;
    private PartyRelationshipCleaner partyRelationshipCleaner;

    @Override
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        organizationInterfaceHelper = new OrganizationInterfaceHelper(this, this.getManager().getDelegator());
        partyRelationshipCleaner = new PartyRelationshipCleaner(this);

        GenericValue gv = getExternalValue();
        parentTypeId = E.ORGANIZATION_UNIT.name();

        if (UtilValidate.isNotEmpty(gv)) {
            orgRoleTypeId = gv.getString(E.orgRoleTypeId.name());
            if (E.GOAL05.name().equals(orgRoleTypeId)) {
                parentTypeId = E.GOALDIV01.name();
            }
        }

        partyId = super.initLocalValuePartyParentRole((String)extLogicKey.get(E.orgCode.name()), parentTypeId);
    }

    @Override
    public void doImport() throws GeneralException {
    	setImported(true);
        GenericValue gv = getExternalValue();
        String msg = "Elaborating party " + gv.getString(E.description.name()) + " (" + parentTypeId + ")" + " [" + gv.getString(OrganizationInterfaceFieldEnum.orgCode.name()) + "]";
        addLogInfo(msg);
        
        // non si gestiscono organizzazioni con date future
        if (checkRefDate(getExternalValue())) {
            msg = "Organization with future date";
            addLogInfo(msg);
            setImported(false);
            return;
        }

        // non si gestiscono organizzazioni cessate con thruDate valorizzato
        if(!isPartyToBeImported()) {
            msg = "Organization already disabled, do nothing";
            addLogInfo(msg);
            setImported(false);
            return;
        } else if(isPartyDisabled()) {
            msg = "Organization reopen";
            addLogInfo(msg);
        }

        parentOrgId = gv.getString(E.parentOrgCode.name());
        parentRoleTypeId = gv.getString(E.parentRoleTypeId.name());
        if (UtilValidate.isNotEmpty(getLocalValue())) {
        	oldPartyName = getLocalValue().getString(E.partyName.name());
        	oldPartyNameLang = getLocalValue().getString(E.partyNameLang.name());
        } else {
        	oldPartyName = "";
        	oldPartyNameLang = "";
        }
        doImportParty();
        
        msg = "END IMPORT " + partyId;
        addLogInfo(msg);
    }
    
    private void doImportParty() throws GeneralException {
        checkDescriptionLang();
        doImportPartyGroup();
        doImportPartyParentRole();
        checkParent();
        doImportPartyRole();

        // only for organization unit
        if (E.ORGANIZATION_UNIT.name().equals(parentTypeId)) {
            createOrgParentRelation();
            doImportExpiredRelations(partyId);
            
            // va fatto dopo, per ultimo, per via del roleTypeId che deve essere gia' stato popolato correttamente
            populateOrgRespInterface();
        }

        // import workEffort, in different transaction
        checkHasAssociativeRoleType();

        // set statusId disable, if thruDate is not empty
        checkThruDate();
    }

    private void checkThruDate() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();
        
        if (UtilValidate.isNotEmpty(gv.getTimestamp(E.thruDate.name()))) { 
            // Aggiornamento Unita Organizzativa
            String msg = "Trying to disabled party " + gv.getString(E.description.name()) + "[" + partyId + "]";
            addLogInfo(msg);
    
            Map<String, Object> serviceMapUpdate = UtilMisc.toMap(E.partyId.name(), partyId, E.groupName.name(), gv.getString(E.description.name()), 
                    E.description.name(), gv.getString(E.longDescription.name()), 
                    E.statusId.name(), E.PARTY_DISABLED.name(), 
                    E.userLogin.name(), manager.getUserLogin());
            addGroupNameLangAndLongDescription(serviceMapUpdate, gv);
            runSync("updatePartyGroup", serviceMapUpdate, E.Party.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_ERROR_UPDATE + E.Party.name(), true);
        }
    }

    /**
     * Verifico presenza descriptionLang nel caso bilingue, perche' obbligatorio in quel caso
     * @throws GeneralException
     */
    private void checkDescriptionLang() throws GeneralException {
        GenericValue gv = getExternalValue();
        if (isMultiLang() && UtilValidate.isEmpty(gv.getString(E.descriptionLang.name()))) {
            String msg = "descriptionLang must not be empty";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    /**
     * Creazione/Aggiornamento PartyGroup, stato viene sempre abilitato, per permettere eventuali modifiche
     * @throws GeneralException
     */
    protected void doImportPartyGroup() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();
        GenericValue party = getLocalValue();
        String msg = null;
        
        // 1 Creazione anagrafica Unita Organizzativa

        // 1.b Creazione/aggiornamento PARTY_GROUP
        if (UtilValidate.isEmpty(party)) {
            // Creazione Unita Organizzativa
            msg = "Trying to create party ";
            addLogInfo(msg);

            Map<String, Object> serviceMapCreate = UtilMisc.toMap(E.partyId.name(), partyId, E.groupName.name(), gv.getString(E.description.name()), 
                    E.description.name(), gv.getString(E.longDescription.name()), 
                    E.statusId.name(), E.PARTY_ENABLED.name(), 
                    E.userLogin.name(), manager.getUserLogin());
            addGroupNameLangAndLongDescription(serviceMapCreate, gv);

            Map<String, Object> res = manager.getDispatcher().runSync("createPartyGroup", serviceMapCreate);
            msg = ServiceUtil.getErrorMessage(res);
            partyId = (String)res.get("partyId");
            if (UtilValidate.isEmpty(msg)) {
                msg = "Party " + partyId + " successfully created";
                addLogInfo(msg);
                party = manager.getDelegator().findOne(E.PartyGroup.name(), false, E.partyId.name(), partyId);
                setLocalValue(party);
            } else {
                msg = "Party creation error " + msg;
                addLogError(msg);
            }
        } else {
            // Aggiornamento Unita Organizzativa
            msg = "Trying to update party " + gv.getString(E.description.name()) + "[" + partyId + "]";
            addLogInfo(msg);

            Map<String, Object> serviceMapUpdate = UtilMisc.toMap(E.partyId.name(), partyId, E.groupName.name(), gv.getString(E.description.name()), 
                    E.description.name(), gv.getString(E.longDescription.name()), 
                    E.statusId.name(), E.PARTY_ENABLED.name(), 
                    E.changeNameDate.name(), gv.getTimestamp(E.refDate.name()),
                    E.userLogin.name(), manager.getUserLogin());
            addGroupNameLangAndLongDescription(serviceMapUpdate, gv);
            runSync("updatePartyGroup", serviceMapUpdate, E.Party.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_ERROR_UPDATE + E.Party.name(), true);
        }
    }

    /**
     * gestisce groupNameLang nel caso multi lingua
     * @param serviceMap
     * @param gv
     */
    protected void addGroupNameLangAndLongDescription(Map<String, Object> serviceMap, GenericValue gv) {
        if (isMultiLang()) {
            serviceMap.put(E.groupNameLang.name(), gv.getString(E.descriptionLang.name()));
            serviceMap.put(E.descriptionLang.name(), gv.getString(E.longDescriptionLang.name()));
        }
    }

    protected void doImportPartyParentRole() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();
        String msg = "";

        // 2.a Creazione PartyParentRole
        String organizationId = (String) manager.getContext().get(E.defaultOrganizationPartyId.name());
        List<EntityCondition> partyParentRoleCondList = new ArrayList<EntityCondition>();
        partyParentRoleCondList.add(EntityCondition.makeCondition(E.partyId.name(), partyId));
        partyParentRoleCondList.add(EntityCondition.makeCondition(E.organizationId.name(), organizationId));
        List<GenericValue> partyParentRoleList = manager.getDelegator().findList(E.PartyParentRole.name(), EntityCondition.makeCondition(partyParentRoleCondList), null, null, null, false);
        if (UtilValidate.isEmpty(partyParentRoleList)) {
            Map<String, Object> pprCreateMap = UtilMisc.toMap(E.partyId.name(), (Object) partyId, E.roleTypeId.name(), parentTypeId, E.parentRoleCode.name(), gv.getString(E.orgCode.name()), E.organizationId.name(), organizationId);
            msg = "Trying to create PartyParentRole: " + pprCreateMap;
            addLogInfo(msg);
            runSyncCrud(E.crudServiceDefaultOrchestration_PartyParentRole.name(), E.PartyParentRole.name(), CrudEvents.OP_CREATE, pprCreateMap, E.PartyParentRole.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_ERROR_CREATE + E.PartyParentRole.name(), true);
        } else {
            msg = "PartyParentRole already exists for party " + partyId;
            addLogInfo(msg);
        }
    }
    
    /**
     * Unita Organizzativa padre
     */
    protected void checkParent() throws GeneralException {
        GenericValue gv = getExternalValue();
        // Prima di creare il party_role e le relazioni, importo il parent per
        // poter calcolare i ruoli di default
        if (E.ORGANIZATION_UNIT.name().equals(parentTypeId) && UtilValidate.isNotEmpty(parentOrgId)) {
            String msg = "Trying Parent Organization Unit with " + parentOrgId + " with role " + gv.getString(E.parentRoleTypeId.name());
            addLogInfo(msg);

            try {
                parentRoleTypeId = gv.getString(E.parentRoleTypeId.name());

                if (!E.ORGROOT001.name().equals(parentOrgId)) {
                    importHigher(gv);
                } else {
                    parentOrgId = E.ORGANIZATION_ROOT.name();
                    parentRoleTypeId = E.ORGANIZATION_ROOT.name();
                }

                msg = "Found Parent Organization Unit with id " + parentOrgId;
                addLogInfo(msg);

                // Ruoli di default per parent e check
                parentRoleTypeId = organizationInterfaceHelper.checkParentRole(parentOrgId, parentRoleTypeId);
                msg = "Found Parent Organization Unit " + parentOrgId + " with role " + parentRoleTypeId;
                addLogInfo(msg);

                // Ruoli di default e check
                orgRoleTypeId = organizationInterfaceHelper.checkRoleType(partyId, orgRoleTypeId, parentRoleTypeId);

                // cercare se ci sono parent da cancellare 
                deleteParentRelationships(gv, parentOrgId);
            } catch (GeneralException e) {
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), e.getMessage());
            }
        } else {
            // Ruoli di default e check
            organizationInterfaceHelper.checkRoleTypeId(orgRoleTypeId, gv.getString(ImportManagerConstants.RECORD_FIELD_ID));
        }

    }

    protected void importHigher(GenericValue gv) throws GeneralException {
        // innesca acquisizione Parent
        String msg = "Trying to import Parent Organization Unit with code " + gv.getString(E.parentOrgCode.name());
        addLogInfo(msg);
        GenericValue parentOrganizationUnit = null;
        parentOrganizationUnit = doImport(getEntityName(), UtilMisc.toMap(E.orgCode.name(), gv.getString(E.parentOrgCode.name())));

        if (UtilValidate.isEmpty(parentOrganizationUnit)) {
            msg = "Parent Organization Unit " + gv.getString(E.parentOrgCode.name()) + " not found";
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
        parentOrgId = (String)parentOrganizationUnit.get(E.partyId.name());

    }

    protected void doImportPartyRole() throws GeneralException {
        String msg = "";
        ImportManager manager = getManager();

        // 2.c Creazione PartyRole
        List<GenericValue> partyRoleList = manager.getDelegator().findList(E.PartyRole.name(), EntityCondition.makeCondition(EntityCondition.makeCondition(E.partyId.name(), partyId), EntityCondition.makeCondition(E.roleTypeId.name(), orgRoleTypeId)), null, null, null, false);
        if (UtilValidate.isEmpty(partyRoleList)) {
            Map<String, ? extends Object> partyRoleCreateMap = UtilMisc.toMap(E.partyId.name(), partyId, E.roleTypeId.name(), orgRoleTypeId, "parentRoleTypeId", parentTypeId);
            msg = "Trying to create PartyRole: " + partyRoleCreateMap;
            addLogInfo(msg);
            runSyncCrud(E.crudServiceDefaultOrchestration_PartyRole.name(), E.PartyRole.name(), CrudEvents.OP_CREATE, partyRoleCreateMap, E.PartyRole.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_ERROR_CREATE + "PartyRole ", true);
        } else {
            msg = "PartyRole already exists for partyId " + partyId + " and parentRoleTypeId " + parentTypeId;
            addLogInfo(msg);
        }
    }

    /**
     * Create PartyRelationship with parentOrgId, if it is not empty
     * @throws GeneralException
     */
    protected void createOrgParentRelation() throws GeneralException {
        String msg;
        GenericValue gv = getExternalValue();

        // 3 Relazione Unita Organizzativa Padre
        if (UtilValidate.isNotEmpty(parentOrgId)) {
        	Timestamp fromDate = UtilValidate.isNotEmpty(gv.getTimestamp(E.parentFromDate.name())) ? gv.getTimestamp(E.parentFromDate.name()) : gv.getTimestamp(E.refDate.name());
            Timestamp thruDate = new Timestamp(DateUtilService.getPreviousDay(fromDate).getTime());
            
            // cancella relazioni con padri precedenti
            organizationInterfaceHelper.setParentRelationshipThruDate(E.GROUP_ROLLUP.name(), parentOrgId, partyId, orgRoleTypeId, thruDate);

            // crea nuova relazioni con padre
            List<EntityCondition> parentPartyRelCondList = new ArrayList<EntityCondition>();
            parentPartyRelCondList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), E.GROUP_ROLLUP.name()));
            parentPartyRelCondList.add(EntityCondition.makeCondition(E.thruDate.name(), null));
            parentPartyRelCondList.add(EntityCondition.makeCondition(E.partyIdTo.name(), partyId));
            parentPartyRelCondList.add(EntityCondition.makeCondition(E.roleTypeIdTo.name(), orgRoleTypeId));
            parentPartyRelCondList.add(EntityCondition.makeCondition(E.partyIdFrom.name(), parentOrgId));
            parentPartyRelCondList.add(EntityCondition.makeCondition(E.roleTypeIdFrom.name(), parentRoleTypeId));

            List<GenericValue> parentPartyRelList = getManager().getDelegator().findList(E.PartyRelationship.name(), EntityCondition.makeCondition(parentPartyRelCondList), null, null, null, false);

            if (UtilValidate.isEmpty(parentPartyRelList)) {
                Map<String, Object> parMap = UtilMisc.toMap(E.partyIdFrom.name(), parentOrgId, E.partyRelationshipTypeId.name(), E.GROUP_ROLLUP.name(), E.roleTypeIdFrom.name(), parentRoleTypeId, E.partyIdTo.name(), partyId, E.roleTypeIdTo.name(), orgRoleTypeId, E.fromDate.name(), fromDate);
                msg = "Found no relation with parent Organization Unit. Creating one: ";
                addLogInfo(msg);
                runSyncCrudWarning(E.crudServiceDefaultOrchestration_PartyRelationship.name(), E.PartyRelationship.name(), CrudEvents.OP_CREATE, parMap, "Relationship with parent" + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_PROBLEM_CREATE + "Relationship  with parent ");
            } else {
                msg = "Relation with parent Organization Unit already exists";
                addLogInfo(msg);
            }
        }
    }

    /**
     * Crea il record
     * @throws GeneralException
     */
    protected void populateOrgRespInterface() throws GeneralException {
        GenericValue gv = getExternalValue();

        if (UtilValidate.isNotEmpty(gv.getString(E.responsibleCode.name()))) {
            String msg = "Populate OrgRespInterface to import Responsible party with code " + gv.getString(E.responsibleCode.name());
            addLogInfo(msg);

            try {
                // popola tabella di interfaccia, senza scatenare importazione
            	Timestamp refDate = UtilValidate.isNotEmpty(gv.getTimestamp(OrganizationInterfaceFieldEnum.responsibleFromDate.name())) ? gv.getTimestamp(OrganizationInterfaceFieldEnum.responsibleFromDate.name()) : gv.getTimestamp(OrganizationInterfaceFieldEnum.refDate.name());
            	Timestamp thruDate = UtilValidate.isNotEmpty(gv.getTimestamp(OrganizationInterfaceFieldEnum.responsibleThruDate.name())) ? gv.getTimestamp(OrganizationInterfaceFieldEnum.responsibleThruDate.name()) : gv.getTimestamp(OrganizationInterfaceFieldEnum.thruDate.name());
                GenericValue orgResp = getManager().getDelegator().makeValue(ImportManagerConstants.ORG_RESP_INTERFACE);
                orgResp.setString(OrgRespInterfaceFieldEnum.dataSource.name(), gv.getString(OrganizationInterfaceFieldEnum.dataSource.name()));
                orgResp.set(OrgRespInterfaceFieldEnum.refDate.name(), refDate);
                orgResp.setString(OrgRespInterfaceFieldEnum.orgCode.name(), gv.getString(OrganizationInterfaceFieldEnum.orgCode.name()));
                orgResp.setString(OrgRespInterfaceFieldEnum.orgRoleTypeId.name(), orgRoleTypeId);
                orgResp.setString(OrgRespInterfaceFieldEnum.responsibleCode.name(), gv.getString(OrganizationInterfaceFieldEnum.responsibleCode.name()));
                if (UtilValidate.isNotEmpty(gv.getString(OrganizationInterfaceFieldEnum.responsibleComments.name()))) {
                	orgResp.setString(OrgRespInterfaceFieldEnum.comments.name(), gv.getString(OrganizationInterfaceFieldEnum.responsibleComments.name()));
                }
                if (UtilValidate.isNotEmpty(gv.getString(OrganizationInterfaceFieldEnum.responsibleRoleTypeId.name()))) {
                	orgResp.setString(OrgRespInterfaceFieldEnum.roleTypeId.name(), gv.getString(OrganizationInterfaceFieldEnum.responsibleRoleTypeId.name()));
                }
                orgResp.set(OrgRespInterfaceFieldEnum.thruDate.name(), thruDate);
                
                // se il record esiste gia', da eccezione
                orgResp.create();
            } catch (GeneralException e) {
                String errorMessage =  MessageUtil.getExceptionMessage(e);
                msg = "Error in create for Organization Unit " + gv.getString(OrganizationInterfaceFieldEnum.orgCode.name()) + " with responsible " + gv.getString(E.responsibleCode.name()) + " : " + errorMessage;
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
        }
    }

    /**
     * Check orgRoleTypeId and, if has associative, import workEffort
     * @throws GeneralException
     */
    private void checkHasAssociativeRoleType() throws GeneralException {
        GenericValue gv = getExternalValue();
        GenericValue roleType = getManager().getDelegator().findOne(E.RoleType.name(), UtilMisc.toMap(E.roleTypeId.name(), orgRoleTypeId), false);
        if (hasAssociative(roleType) && UtilValidate.isNotEmpty(parentOrgId) && UtilValidate.isNotEmpty(gv.getString(E.parentOrgCode.name()))) {
            new WeRootImportManager(this, partyId, roleType, gv.getString(E.parentOrgCode.name()), oldPartyName, oldPartyNameLang).doImport();
        }
    }

    /**
     * Cancella le relazioni col parent, a parita di refDate
     * @param gv
     * @param parentOrgId
     * @throws GeneralException
     */
    private void deleteParentRelationships(GenericValue gv, String parentOrgId) throws GeneralException {
    	Timestamp fromDate = UtilValidate.isNotEmpty(gv.getTimestamp(E.parentFromDate.name())) ? gv.getTimestamp(E.parentFromDate.name()) : gv.getTimestamp(E.refDate.name());
        PartyRelationshipCleanConditionsBuilder parentConditionsBuilder = new PartyRelationshipCleanConditionsBuilder(E.GROUP_ROLLUP.name(), fromDate, "", parentOrgId, partyId, "", "", "", "", "");
        partyRelationshipCleaner.cleanToRelationships(parentConditionsBuilder, true, true);
    }

}
