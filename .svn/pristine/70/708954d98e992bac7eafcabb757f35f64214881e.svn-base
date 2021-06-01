package com.mapsengineering.base.standardimport;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.enumeration.PersRespInterfaceFieldEnum;
import com.mapsengineering.base.standardimport.enumeration.PersonInterfaceFieldEnum;
import com.mapsengineering.base.standardimport.helper.PartyContactMechHelper;
import com.mapsengineering.base.standardimport.helper.PartyRoleHelper;
import com.mapsengineering.base.standardimport.helper.PersonEmplPositionTypeHelper;
import com.mapsengineering.base.standardimport.helper.PersonInterfaceHelper;
import com.mapsengineering.base.standardimport.helper.TemplateEnum;
import com.mapsengineering.base.standardimport.helper.TemplateHelper;
import com.mapsengineering.base.standardimport.helper.UserLoginHelper;
import com.mapsengineering.base.standardimport.helper.WorkEffortPartyAssignmentHelper;
import com.mapsengineering.base.standardimport.party.PartyRelationshipCleanConditionsBuilder;
import com.mapsengineering.base.standardimport.party.PartyRelationshipCleaner;
import com.mapsengineering.base.standardimport.party.PartyRelationshipDater;
import com.mapsengineering.base.standardimport.party.PartyRelationshipRestorer;
import com.mapsengineering.base.standardimport.util.PartyRelationshipUtil;
import com.mapsengineering.base.standardimport.util.PersonInterfaceContext;
import com.mapsengineering.base.util.FindUtilService;
import com.mapsengineering.base.util.JobLogger;

/**
 * Import Person
 */
public class PersonInterfaceTakeOverService extends AbstractPartyTakeOverService {

    public static final String MODULE = PersonInterfaceTakeOverService.class.getName();

    protected static final BigDecimal K_100 = new BigDecimal(100);
    private static final String AND_SEP = " and ";
    
    /** serve nella scelta del fromDate della relazione tra dipendente e unita organizzativa */
    private boolean newInsert = false;
    private String partyId;
    private String personRoleTypeId;
    private GenericValue orgUnitApp;
    private PersonInterfaceHelper personInterfaceHelper;
    private PartyRoleHelper partyRoleHelper;
    private UserLoginHelper userLoginHelper;
    private PartyContactMechHelper partycontactMechHelper;
    private PartyRelationshipUtil partyRelationshipUtil;
    private PartyRelationshipCleaner partyRelationshipCleaner;
    private PartyRelationshipRestorer partyRelationshipRestorer;
    private PartyRelationshipDater partyRelationshipDater;

    /** Store thruDate of previous elaboration */
    private Map<String, Timestamp> tmpThruDate;

    private Map<String, Object> contextPartyUpdateWorkEffortOld;
    private Map<String, Object> contextPartyUpdateWorkEffortNew;
    private Timestamp emplPositionTypeDate;

    @Override
    public void initLocalValue(Map<String, ? extends Object> extLogicKey) throws GeneralException {
    	setImported(false);
        personInterfaceHelper = new PersonInterfaceHelper(this, this.getManager().getDispatcher(), this.getManager().getDelegator());
        partyRoleHelper = new PartyRoleHelper(this);
        userLoginHelper = new UserLoginHelper(this);
        partycontactMechHelper = new PartyContactMechHelper(this);
        partyRelationshipUtil = new PartyRelationshipUtil(this);
        partyRelationshipCleaner = new PartyRelationshipCleaner(this);
        partyRelationshipRestorer = new PartyRelationshipRestorer(this);
        partyRelationshipDater = new PartyRelationshipDater(this);

        tmpThruDate = new HashMap<String, Timestamp>();
        partyId = super.initLocalValuePartyParentRole((String)extLogicKey.get(E.personCode.name()), E.EMPLOYEE.name());
    }

    @Override
    public void doImport() throws GeneralException {
    	setImported(true);
    	// sempre per primo
    	PersonInterfaceContext.push(getManager());
        try {
	        GenericValue gv = getExternalValue();
	        String msg = "Elaborating party "+ gv.getString(PersonInterfaceFieldEnum.firstName.name()) + " " + gv.getString(PersonInterfaceFieldEnum.lastName.name()) + "[" + gv.getString(PersonInterfaceFieldEnum.personCode.name()) + "]";
	        addLogInfo(msg);
	
	        if (checkRefDate(getExternalValue())) {
	            msg = REF_DATE_AFTER_NOW + getEntityName() + " with " + getManager().toString(getExternalValue().getPrimaryKey());
	            throw new ImportException(getEntityName(), getExternalValue().getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
	        }
	
	    	// non si gestiscono persone cessate con thruDate valorizzato
	        if(!isPartyToBeImported()) {
	            msg = "Trying to disable party with partyId " + partyId + " already disabled, import will not be done";
	            addLogWarning(msg);
	            setImported(false);
	            return;
	        } else if(isPartyDisabled()) {
	            msg = "Party reopen";
	            addLogInfo(msg);
	        }
	        
	        newInsert = false;
	        contextPartyUpdateWorkEffortOld = new HashMap<String, Object>();
	        contextPartyUpdateWorkEffortNew = new HashMap<String, Object>();
	
	        doImportParty();
	        
	        msg = "END IMPORT " + partyId;
	        addLogInfo(msg);
	        
	        setContextData(gv);
	        
	        doImportAllocation();
        } finally {
        	PersonInterfaceContext.pop(getManager());
        }
    }

    private void doImportParty() throws GeneralException {
        doImportPerson();
        doImportContact();
        doImportPartyParentRole();
        doImportPartyRoles();
        doImportOrgUnitRel();
        doImportOtherParties();
        doImportAssign();
        doImportUserLogin();
        doImportWorkEffort();

        doImportExpiredRelations(partyId);
        
        manageThruDate();
        
        // Chiamo il servizio postPartyUpdateWorkEffort,
        // che e' gestito solo per Regione Campania e che aggiorna le schede di valutazione individuale solo in caso di cambio
        // di unita' organizzativa di appartenenza (ORG_EMPLOYMENT)
        callServicepostPartyUpdateWorkEffort();
    }

    /**
     * Chiusura rapporto di un soggetto, con chiusura schede individuali, ecc...
     */
    private void manageThruDate() throws GeneralException {
    	ImportManager manager = getManager();
		GenericValue gv = getExternalValue();
		if (UtilValidate.isNotEmpty(gv.getTimestamp(E.thruDate.name()))) {
        	
        	Map<String, Object> parametersMap = UtilMisc.toMap(ServiceLogger.LOCALE, getManager().getLocale(), E.partyId.name(), (Object) partyId, E.firstName.name(), gv.getString(E.firstName.name()), 
            		E.lastName.name(), gv.getString(E.lastName.name()), E.statusId.name(), E.PARTY_DISABLED.name(), 
            		E.endDate.name(), gv.getTimestamp(E.thruDate.name()),
            		E.userLogin.name(), manager.getUserLogin());

        	runSync("updatePerson", parametersMap, "Party" + FindUtilService.MSG_SUCCESSFULLY_UPDATE + " The status is " + E.PARTY_DISABLED.name(), FindUtilService.MSG_ERROR_UPDATE + E.Party.name(), false);
        	
        	String msg = "Start updatePartyEndDate " + partyId + " with end date=" + gv.getTimestamp(E.thruDate.name());
            addLogInfo(msg);

            Map<String, Object> parametersCloseMap = UtilMisc.toMap(E.partyId.name(), partyId, E.endDate.name(), gv.getTimestamp(E.thruDate.name()), E.returnMessages.name(), "Y");
            Map<String, Object> resultPartyUpdate = runSync("updatePartyEndDate", parametersCloseMap, E.Party.name() + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_ERROR_UPDATE + E.Party.name(), false);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> listMessages = (List<Map<String, Object>>)resultPartyUpdate.get("messages");
            for (Map<String, Object> ele : listMessages) {
                addLogInfo((String)ele.get("logMessage"));
            }

            msg = "End updatePartyEndDate " + partyId;
            addLogInfo(msg);

        }
    }
    
    /**
     * Search WorkEffort with sourceReferenceId or etch = workEffortAssignmentCode, and optional workEffortDate
     * and assign party with roleTypeId WE_ASSIGNMENT
     * @throws GeneralException 
     */
    private void doImportWorkEffort() throws GeneralException {
        GenericValue gv = getExternalValue();
        WorkEffortPartyAssignmentHelper workEffortHelper = new WorkEffortPartyAssignmentHelper(this, this.getManager().getDelegator());
        Timestamp refDate = gv.getTimestamp(E.refDate.name());
        Timestamp thruDate = gv.getTimestamp(E.thruDate.name());
        // workeffortDate instead refDate
        if(UtilValidate.isNotEmpty(gv.getTimestamp(E.workEffortDate.name()))) {
            refDate = gv.getTimestamp(E.workEffortDate.name());
        }
        workEffortHelper.doImportWorkEffort(partyId, gv.getString(E.workEffortAssignmentCode.name()), refDate, thruDate);
    }

    private void doImportUserLogin() throws GeneralException {
        userLoginHelper.doImportUserLogin(partyId, getExternalValue());
        userLoginHelper.doImportExpiredUserLogin(partyId, getExternalValue());
    }

    private void doImportOtherParties() throws GeneralException {
        doImportProfile();
        doImportEvaluatorApprover();
    }

    private void doImportContact() throws GeneralException {
        partycontactMechHelper.doImportEmail(partyId, getExternalValue());
        partycontactMechHelper.doImportTel(partyId, getExternalValue());
    }

    @SuppressWarnings("unchecked")
    private void callServicepostPartyUpdateWorkEffort() throws GeneralException {
        GenericValue gv = getExternalValue();

        if (UtilValidate.isNotEmpty(contextPartyUpdateWorkEffortNew) || UtilValidate.isNotEmpty(contextPartyUpdateWorkEffortOld)) {
            ImportManager manager = getManager();

            // Se il party e stato disabilitato, eseguo il servizio ma con refDate = thruDate
            Timestamp refDate = gv.getTimestamp(E.refDate.name());
            if (UtilValidate.isNotEmpty(gv.getTimestamp(E.thruDate.name()))) {
                refDate = gv.getTimestamp(E.thruDate.name());
            }
            String msg = "Start postPartyUpdateWorkEffort " + partyId + " at " + refDate + " (" + gv.getTimestamp(E.evaluatorFromDate.name()) + "), with contextPartyUpdateWorkEffortOld = " + contextPartyUpdateWorkEffortOld + " and contextPartyUpdateWorkEffortNew = " + contextPartyUpdateWorkEffortNew;
            addLogInfo(msg);

            Map<String, Object> parametersMap = UtilMisc.toMap(E.partyId.name(), partyId, E.refDate.name(), refDate, E.evaluatorFromDate.name(), gv.getTimestamp(E.evaluatorFromDate.name()), "parametersOld", contextPartyUpdateWorkEffortOld, "parametersNew", contextPartyUpdateWorkEffortNew, E.userLogin.name(), manager.getUserLogin(), E.returnMessages.name(), "Y");
            parametersMap.put(E.checkEndYearElab.name(), manager.getCheckEndYearElab());
            Map<String, Object> resultPartyUpdate = runSync("postPartyUpdateWorkEffort", parametersMap, "Run postPartyUpdateWorkEffort for " + partyId, "Error in postPartyUpdateWorkEffort for " + partyId, false);
            if (UtilValidate.isNotEmpty(resultPartyUpdate.get("messages"))) {
                List<Map<String, Object>> listMessages = (List<Map<String, Object>>)resultPartyUpdate.get("messages");
                for (Map<String, Object> ele : listMessages) {
                    if (ServiceLogger.LOG_TYPE_ERROR.equals(ele.get("logType"))) {
                        addLogError((String)ele.get(ServiceLogger.LOG_MESSAGE));
                    } else if (ServiceLogger.LOG_TYPE_WARN.equals(ele.get("logType"))) {
                        addLogWarning((String)ele.get(ServiceLogger.LOG_MESSAGE));
                    } else {
                        addLogInfo((String)ele.get(ServiceLogger.LOG_MESSAGE));
                    }
                }
            }

            msg = "End postPartyUpdateWorkEffort " + partyId;
            addLogInfo(msg);

        }
    }

    protected void doImportPerson() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();
        GenericValue party = getLocalValue();
        String msg = "";

        // 1 Creazione anagrafica persona
        String emplPositionTypeId = UtilValidate.isNotEmpty(gv.getString(PersonInterfaceFieldEnum.emplPositionTypeId.name())) ? gv.getString(PersonInterfaceFieldEnum.emplPositionTypeId.name()) : "";
        String fiscalCode = UtilValidate.isNotEmpty(gv.getString(PersonInterfaceFieldEnum.fiscalCode.name())) ? gv.getString(PersonInterfaceFieldEnum.fiscalCode.name()) : "";
        personInterfaceHelper.checkValidEmplPositionTypeId(emplPositionTypeId);

        if (UtilValidate.isEmpty(party)) {
            BigDecimal employmentAmount = UtilValidate.isNotEmpty(gv.getBigDecimal(E.employmentAmount.name())) ? gv.getBigDecimal(PersonInterfaceFieldEnum.employmentAmount.name()) : K_100;

            // Creazione Risorsa Umana
            msg = "Trying to create party";
            addLogInfo(msg);
            
            emplPositionTypeDate = gv.getTimestamp(E.emplPositionTypeDate.name());
            
            Map<String, Object> parametersMap = UtilMisc.toMap(E.partyId.name(), partyId, 
            		E.firstName.name(), gv.getString(PersonInterfaceFieldEnum.firstName.name()), 
            		E.lastName.name(), gv.getString(PersonInterfaceFieldEnum.lastName.name()),
            		E.statusId.name(), E.PARTY_ENABLED.name(), 
            		E.employmentAmount.name(), employmentAmount, 
            		E.comments.name(), gv.getString(PersonInterfaceFieldEnum.comments.name()), E.description.name(), gv.getString(PersonInterfaceFieldEnum.description.name()), 
            		E.emplPositionTypeId.name(), emplPositionTypeId, PersonInterfaceFieldEnum.fiscalCode.name(), fiscalCode, 
            		E.userLogin.name(), manager.getUserLogin());
            if (UtilValidate.isNotEmpty(emplPositionTypeDate)) {
            	parametersMap.put(E.emplPositionTypeDate.name(), emplPositionTypeDate);
            }

            Map<String, Object> res = manager.getDispatcher().runSync("createPerson", parametersMap);
            msg = ServiceUtil.getErrorMessage(res);
            partyId = (String) res.get("partyId");
            if (UtilValidate.isEmpty(msg)) {          	
                msg = "Party " + partyId + " successfully created";
                addLogInfo(msg);
                party = manager.getDelegator().findOne("Person", false, E.partyId.name(), partyId);
                setLocalValue(party);
                newInsert = true;
            } else {
                msg = "Party creation error " + msg;
                addLogError(msg);
            }
        } else {
            // Aggiornamento Risorsa Umana
            msg = "Trying to update party " + partyId;
            addLogInfo(msg);

            /* emplPositionTypeId may be different from input value, so this method return the correct value */
            emplPositionTypeId = getEmplPositionTypeIdAndSetEmplPositionTypeDate();

            // Enabled All party
            Map<String, Object> parametersMap = UtilMisc.toMap(ServiceLogger.LOCALE, getManager().getLocale(), E.partyId.name(), partyId, E.firstName.name(), gv.getString(E.firstName.name()), 
            		E.lastName.name(), gv.getString(E.lastName.name()), 
            		E.statusId.name(), E.PARTY_ENABLED.name(), 
            		E.endDate.name(), null,
            		E.fiscalCode.name(), fiscalCode, E.userLogin.name(), manager.getUserLogin());

            parametersMap.putAll(setOtherFieldNotNullable(emplPositionTypeId, gv));

            msg = "emplPositionTypeDate " + emplPositionTypeDate;
            addLogInfo(msg);
            if (UtilValidate.isNotEmpty(emplPositionTypeDate)) {
                parametersMap.put(E.emplPositionTypeDate.name(), emplPositionTypeDate);
                Timestamp previousDate = new Timestamp(getPreviousDay(emplPositionTypeDate).getTime());
                // NO use jobLogger
                JobLogger jobLogger = new JobLogger(MODULE);
                TemplateHelper templateHelper = new TemplateHelper(jobLogger, manager.getDelegator());
                GenericValue partyHistory = templateHelper.getPartyHistory(partyId, previousDate);
                msg = "partyHistory " + partyHistory;
                addLogInfo(msg);
                if (UtilValidate.isNotEmpty(partyHistory)) {
                    if (previousDate.before(partyHistory.getTimestamp(TemplateEnum.thruDate.name())) || previousDate.equals(partyHistory.getTimestamp(TemplateEnum.thruDate.name()))) { // equals?
                        msg = "There is a history for person at " + partyHistory.getTimestamp(TemplateEnum.thruDate.name()) + ", so skip partyHistory update";
                        addLogInfo(msg);

                        parametersMap.put(E.skipStore.name(), true);
                    }
                }
            }

            msg = "parametersMap " + parametersMap;
            addLogInfo(msg);
            runSync("updatePerson", parametersMap, "Party" + FindUtilService.MSG_SUCCESSFULLY_UPDATE, FindUtilService.MSG_ERROR_UPDATE + E.Party.name(), false);
        }
    }

    /**
     * Set field comments, description and emplPositionTypeId only if its is not null
     * @param gv
     * @return
     */
    private Map<? extends String, ? extends Object> setOtherFieldNotNullable(String emplPositionTypeId, GenericValue gv) {
        Map<String, Object> mappa = new HashMap<String, Object>();
        if (UtilValidate.isNotEmpty(gv.getString(E.comments.name()))) {
            mappa.put(E.comments.name(), gv.getString(E.comments.name()));
        }
        if (UtilValidate.isNotEmpty(gv.getString(E.description.name()))) {
            mappa.put(E.description.name(), gv.getString(E.description.name()));
        }
        if (UtilValidate.isNotEmpty(emplPositionTypeId)) {
            mappa.put(E.emplPositionTypeId.name(), emplPositionTypeId);
        }
        if (UtilValidate.isNotEmpty(gv.getBigDecimal(E.employmentAmount.name()))) {
            mappa.put(E.employmentAmount.name(), gv.getBigDecimal(E.employmentAmount.name()));
        }
        return mappa;
    }

    /**
     * Return the correct value for emplPositionTypeId, because it may be different from input value, <br> and set emplPositionTypeDate if there is a change for emplPositionTypeId, description, comments, employmentAmount
     * @return
     * @throws GeneralException
     */
    protected String getEmplPositionTypeIdAndSetEmplPositionTypeDate() throws GeneralException {
        GenericValue gv = getExternalValue();

        String emplPositionTypeId = UtilValidate.isNotEmpty(gv.getString(E.emplPositionTypeId.name())) ? gv.getString(E.emplPositionTypeId.name()) : "";

        String description = UtilValidate.isNotEmpty(gv.getString(E.description.name())) ? gv.getString(E.description.name()) : "";
        String comments = UtilValidate.isNotEmpty(gv.getString(E.comments.name())) ? gv.getString(E.comments.name()) : "";
        BigDecimal employmentAmount = UtilValidate.isNotEmpty(gv.getBigDecimal(E.employmentAmount.name())) ? gv.getBigDecimal(E.employmentAmount.name()) : null;

        PersonEmplPositionTypeHelper personEmplPositionTypeHelper = new PersonEmplPositionTypeHelper(this);

        Map<String, Object> result = personEmplPositionTypeHelper.getEmplPositionTypeIdAndSetEmplPositionTypeDate(emplPositionTypeId, description, comments, employmentAmount, partyId, gv.getTimestamp(E.refDate.name()));

        return setValueForEmplPositionType(result);
    }

    private String setValueForEmplPositionType(Map<String, Object> result) {
    	GenericValue gv = getExternalValue();
        String emplPositionTypeId = (String)result.get(E.emplPositionTypeId.name());
        emplPositionTypeDate = UtilValidate.isNotEmpty(gv.getTimestamp(E.emplPositionTypeDate.name())) ? gv.getTimestamp(E.emplPositionTypeDate.name()) : (Timestamp)result.get(E.emplPositionTypeDate.name());

        String msg = "There result for emplPositionTypeDate = " + emplPositionTypeDate;
        addLogInfo(msg);

        if (UtilValidate.isNotEmpty(result.get(E.templateId.name()))) {
            contextPartyUpdateWorkEffortOld.put(E.emplPositionTypeId.name(), result.get(E.templateId.name()));
        }

        return emplPositionTypeId;
    }

    protected void doImportPartyParentRole() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();
        String msg = "";

        // 2
        GenericValue ppr = manager.getDelegator().findOne(E.PartyParentRole.name(), UtilMisc.toMap(E.partyId.name(), partyId, E.roleTypeId.name(), E.EMPLOYEE.name()), false);
        if (UtilValidate.isEmpty(ppr)) {
            msg = "Creating PartyParentRole partyId " + partyId + " roleTypeId EMPLOYEE";
            addLogInfo(msg);
            Map<String, ? extends Object> parametersMap = UtilMisc.toMap(E.partyId.name(), partyId, E.roleTypeId.name(), E.EMPLOYEE.name(), "parentRoleCode", gv.getString(E.personCode.name()));
            runSyncCrud(E.crudServiceDefaultOrchestration_PartyParentRole.name(), E.PartyParentRole.name(), CrudEvents.OP_CREATE, parametersMap, E.PartyParentRole.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_ERROR_CREATE + E.PartyParentRole.name(), false);
        }
    }

    protected void doImportPartyRoles() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();
        String msg = "";

        // 2 RUOLI
        setPersonRoleTypeId(UtilValidate.isNotEmpty(gv.getString(E.personRoleTypeId.name())) ? gv.getString(E.personRoleTypeId.name()) : E.EMPLOYEE.name());

        List<String> roleList = UtilMisc.toList(getPersonRoleTypeId());

        if ("Y".equals(gv.getString(E.isEvalManager.name()))) {
            roleList.add(E.WEM_EVAL_MANAGER.name());
        }
        if ("N".equals(gv.getString(E.isEvalManager.name()))) {
            personInterfaceHelper.deletePartyRole(partyId, E.WEM_EVAL_MANAGER.name());
        }
        roleList.addAll(personInterfaceHelper.getPersonInterfaceRoleList(getPersonRoleTypeId()));

        for (String roleTypeId : roleList) {
            GenericValue roleType = manager.getDelegator().findOne("RoleType", UtilMisc.toMap(E.roleTypeId.name(), roleTypeId), false);
            if (UtilValidate.isEmpty(roleType) || !E.EMPLOYEE.name().equals(roleType.getString("parentTypeId"))) {
                msg = "Error: the role " + roleTypeId + " must be a child of EMPLOYEE";
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
            GenericValue partyRole = manager.getDelegator().findOne(E.PartyRole.name(), UtilMisc.toMap(E.partyId.name(), partyId, E.roleTypeId.name(), roleTypeId), false);
            if (UtilValidate.isEmpty(partyRole)) {
                msg = "Creating role " + roleTypeId + " for party " + partyId;
                addLogInfo(msg);
                Map<String, ? extends Object> parametersMap = UtilMisc.toMap(E.partyId.name(), partyId, E.roleTypeId.name(), roleTypeId, "parentRoleTypeId", E.EMPLOYEE.name());
                runSyncCrud(E.crudServiceDefaultOrchestration_PartyRole.name(), E.PartyRole.name(), CrudEvents.OP_CREATE, parametersMap, E.PartyParentRole.name() + FindUtilService.MSG_SUCCESSFULLY_CREATED, FindUtilService.MSG_ERROR_CREATE + E.PartyParentRole.name(), false);
            }
        }
    }

    protected void doImportOrgUnitRel() throws GeneralException {
        GenericValue gv = getExternalValue();
        String msg = "";
        
        // 3b Relazione Unita Organizzativa Appartenenza
        orgUnitApp = null;
        if (UtilValidate.isNotEmpty(gv.getString(E.employmentOrgCode.name()))) {
            msg = "Trying to import Employment Organization Unit with code " + gv.getString(E.employmentOrgCode.name());
            addLogInfo(msg);
            orgUnitApp = doImport(ImportManagerConstants.ORGANIZATION_INTERFACE, UtilMisc.toMap("orgCode", gv.getString(E.employmentOrgCode.name())));

            String employmentRoleTypeId = doImportRoleOrgUnitRel();
            Timestamp fromDate = getOrgEmplFromDate(gv);
            Timestamp previuosDay = new Timestamp(getPreviousDay(fromDate).getTime());
            
        	PartyRelationshipCleanConditionsBuilder employmentsConditionsBuilder = new PartyRelationshipCleanConditionsBuilder(E.ORG_EMPLOYMENT.name(), fromDate, "", orgUnitApp.getString(E.partyId.name()), 
        			partyId, "", employmentRoleTypeId, personRoleTypeId, gv.getString(E.employmentOrgComments.name()), gv.getString(E.employmentOrgDescription.name()));
        	
            // controllo anomalie
            tmpThruDate = partyRelationshipCleaner.cleanToRelationships(employmentsConditionsBuilder, true, true);             	
            
            setContextPartyUpdateWorkEffortEmploymentAllocation(employmentsConditionsBuilder, E.ORG_EMPLOYMENT.name(), orgUnitApp.getString(E.partyId.name()), gv.getString(E.employmentOrgComments.name()));
                
            // cancella relazioni con organizzazioni precedenti
            partyRelationshipUtil.controlloSoggettoUnicoPadre(employmentsConditionsBuilder, previuosDay, false);

            // crea nuova relazioni con organizzazione
            Map<String, Object> empOrgRel = new HashMap<String, Object>();
            empOrgRel.putAll(UtilMisc.toMap(E.partyIdFrom.name(), orgUnitApp.getString(E.partyId.name()), E.roleTypeIdFrom.name(), employmentRoleTypeId, E.partyRelationshipTypeId.name(), E.ORG_EMPLOYMENT.name(), E.partyIdTo.name(), partyId, E.roleTypeIdTo.name(), personRoleTypeId, E.comments.name(), gv.getString(E.employmentOrgComments.name())));
            empOrgRel.put(E.fromDate.name(), fromDate);
            empOrgRel.put(E.relationshipName.name(), gv.getString(E.employmentOrgDescription.name()));
            if (UtilValidate.isNotEmpty(gv.getTimestamp(E.employmentOrgThruDate.name()))) {
                HashMap<String, Object> mappaKey = new HashMap<String, Object>();
                mappaKey.put(E.partyRelationshipTypeId.name(), E.ORG_EMPLOYMENT.name());
                mappaKey.put(E.partyIdTo.name(), orgUnitApp.getString(E.partyId.name()));
                mappaKey.put(E.fromDate.name(), fromDate);
                mappaKey.put(E.comments.name(), gv.getString(E.employmentOrgComments.name()));
                String key = getPkShortValueString(mappaKey);
                tmpThruDate.put(key, gv.getTimestamp(E.employmentOrgThruDate.name()));
            }

            String successMsg = "PartyRelationship ORG_EMPLOYMENT between " + orgUnitApp.getString(E.partyId.name()) + AND_SEP + partyId + FindUtilService.MSG_SUCCESSFULLY_CREATED;
            String errorMsg = "Error in creation PartyRelationship ORG_EMPLOYMENT between " + orgUnitApp.getString(E.partyId.name()) + AND_SEP + partyId + FindUtilService.COLON_SEP + msg;

            partyRelationshipUtil.controlloUnicaRelazione(E.ORG_EMPLOYMENT.name(), orgUnitApp.getString(E.partyId.name()), employmentRoleTypeId, partyId, personRoleTypeId, empOrgRel, successMsg, errorMsg, false, tmpThruDate);
        } else if(isPartyDisabled() && UtilValidate.isEmpty(gv.getTimestamp(E.thruDate.name()))) {
            partyRelationshipRestorer.restorePartyRelationship(partyId, E.ORG_EMPLOYMENT.name());
        }
    }

    protected String doImportRoleOrgUnitRel() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();
        String msg = "";
        String employmentRoleTypeId = gv.getString("employmentRoleTypeId");

        if (UtilValidate.isEmpty(employmentRoleTypeId)) {
            // Lo calcolo da PartyRelationshipRole
            List<EntityCondition> condList = new ArrayList<EntityCondition>();
            condList.add(EntityCondition.makeCondition(E.partyId.name(), orgUnitApp.getString(E.partyId.name())));
            condList.add(EntityCondition.makeCondition(E.parentRoleTypeId.name(), E.ORGANIZATION_UNIT.name()));

            List<GenericValue> orgUnitAppRoleTypeList = manager.getDelegator().findList(E.PartyRole.name(), EntityCondition.makeCondition(condList), null, null, null, false);
            if (UtilValidate.isEmpty(orgUnitAppRoleTypeList) || orgUnitAppRoleTypeList.size() > 1) {
                msg = "Error: The Organization Unit id " + orgUnitApp.getString(E.partyId.name()) + " hasn't any role or it has more than one role";
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            } else {
                employmentRoleTypeId = personInterfaceHelper.checkValidPartyRelationshipRole(E.ORG_EMPLOYMENT.name(), EntityUtil.getFirst(orgUnitAppRoleTypeList).getString(E.roleTypeId.name()), personRoleTypeId, "Employment Organization Unit");
            }
        } else {
            // Controllo che sia valido PartyRole
            checkValidityPartyRole(orgUnitApp.getString(E.partyId.name()), employmentRoleTypeId, E.ORGANIZATION_UNIT.name());
            // Controllo che sia valido PartyRelationshipRole
            GenericValue partyRelationshipRole = manager.getDelegator().findOne(E.PartyRelationshipRole.name(), UtilMisc.toMap(E.partyRelationshipTypeId.name(), E.ORG_EMPLOYMENT.name(), E.roleTypeValidFrom.name(), employmentRoleTypeId, E.roleTypeValidTo.name(), personRoleTypeId), false);
            if (UtilValidate.isEmpty(partyRelationshipRole)) {
                msg = "Error: the given role " + employmentRoleTypeId + " isn't valid for Employment Organization Unit";
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
        }
        return employmentRoleTypeId;
    }

    private void checkValidityPartyRole(String partyId, String roleTypeId, String parentRoleTypeId) throws GeneralException {
        GenericValue gv = getExternalValue();
        try {
        	partyRoleHelper.checkValidityPartyRole(partyId, roleTypeId, parentRoleTypeId, null);
        } catch (GeneralException e) {
            String msg = "Error: The Organization Unit hasn't the given role " + roleTypeId + " and parentRole " + parentRoleTypeId + ". " + e.getMessage();
            throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
        }
    }

    protected void doImportAssign() throws GeneralException {
        GenericValue gv = getExternalValue();
        String msg = "";

        // 3.a Assegnazione, se valorizzata Assegnazione
        GenericValue orgUnitAss = null;
        if (UtilValidate.isNotEmpty(gv.getString(E.allocationOrgCode.name()))) {
            msg = "Trying to import Allocation Organization Unit code " + gv.getString(E.allocationOrgCode.name());
            addLogInfo(msg);
            orgUnitAss = doImport(ImportManagerConstants.ORGANIZATION_INTERFACE, UtilMisc.toMap("orgCode", gv.getString(E.allocationOrgCode.name())));
            msg = "Allocation Organization Unit with code " + gv.getString(E.allocationOrgCode.name()) + " imported with id " + orgUnitAss.getString(E.partyId.name());
            addLogInfo(msg);
        } else if(isPartyDisabled() && UtilValidate.isEmpty(gv.getTimestamp(E.thruDate.name()))) {
            partyRelationshipRestorer.restorePartyRelationship(partyId, E.ORG_ALLOCATION.name());
        }
        if (UtilValidate.isNotEmpty(orgUnitAss)) {
            doImportRoleAssign(orgUnitAss);
        }
    }

    protected void doImportRoleAssign(GenericValue orgUnitAss) throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();
        String msg = "";

        String allocationRoleTypeId = gv.getString("allocationRoleTypeId");
        if (UtilValidate.isEmpty(allocationRoleTypeId)) {
            List<EntityCondition> condList = new ArrayList<EntityCondition>();
            condList.add(EntityCondition.makeCondition(E.partyId.name(), orgUnitAss.getString(E.partyId.name())));
            condList.add(EntityCondition.makeCondition(E.parentRoleTypeId.name(), E.ORGANIZATION_UNIT.name()));
            // Lo calcolo da PartyRole
            List<GenericValue> orgUnitAssRoleTypeList = manager.getDelegator().findList(E.PartyRole.name(), EntityCondition.makeCondition(condList), null, null, null, false);
            if (UtilValidate.isEmpty(orgUnitAssRoleTypeList) || orgUnitAssRoleTypeList.size() > 1) {
                msg = "Error: The Organization Unit id " + orgUnitAss.getString(E.partyId.name()) + " hasn't any role or it has more than one role";
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            } else {
                // Lo verifico poi su PartyRelationshipRole
                allocationRoleTypeId = personInterfaceHelper.checkValidPartyRelationshipRole(E.ORG_ALLOCATION.name(), EntityUtil.getFirst(orgUnitAssRoleTypeList).getString(E.roleTypeId.name()), personRoleTypeId, "Allocation Organization Unit");
            }
        } else {
            // Controllo che sia valido PartyRole
            checkValidityPartyRole(orgUnitAss.getString(E.partyId.name()), allocationRoleTypeId, E.ORGANIZATION_UNIT.name());
            // Controllo che sia valido PartyRelationshipRole
            GenericValue partyRelationshipRole = manager.getDelegator().findOne(E.PartyRelationshipRole.name(), UtilMisc.toMap(E.partyRelationshipTypeId.name(), E.ORG_ALLOCATION.name(), E.roleTypeValidFrom.name(), allocationRoleTypeId, E.roleTypeValidTo.name(), personRoleTypeId), false);
            if (UtilValidate.isEmpty(partyRelationshipRole)) {
                msg = "Error: the given role " + allocationRoleTypeId + " isn't valid for Allocation Organization Unit";
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
        }

        Timestamp fromDate = (UtilValidate.isNotEmpty(gv.getTimestamp(E.fromDate.name())) && newInsert) ? gv.getTimestamp(E.fromDate.name()) : gv.getTimestamp(E.refDate.name());
        Timestamp previuosDay = new Timestamp(getPreviousDay(gv.getTimestamp(E.refDate.name())).getTime());
        
    	PartyRelationshipCleanConditionsBuilder allocationsConditionsBuilder = new PartyRelationshipCleanConditionsBuilder(E.ORG_ALLOCATION.name(), fromDate, "", orgUnitAss.getString(E.partyId.name()), 
    			partyId, "", allocationRoleTypeId, personRoleTypeId, gv.getString(E.allocationOrgComments.name()), gv.getString(E.allocationOrgDescription.name()));
        
        tmpThruDate = partyRelationshipCleaner.cleanToRelationships(allocationsConditionsBuilder, true, true);             
        
        setContextPartyUpdateWorkEffortEmploymentAllocation(allocationsConditionsBuilder, E.ORG_ALLOCATION.name(), orgUnitAss.getString(E.partyId.name()), gv.getString(E.allocationOrgComments.name()));
        
        partyRelationshipUtil.controlloSoggettoUnicoPadre(allocationsConditionsBuilder, previuosDay, false);

        Map<String, Object> assRelMap = UtilMisc.toMap(E.partyIdFrom.name(), orgUnitAss.getString(E.partyId.name()), E.roleTypeIdFrom.name(), allocationRoleTypeId, E.partyRelationshipTypeId.name(), E.ORG_ALLOCATION.name(), E.comments.name(), gv.getString(E.allocationOrgComments.name()), E.partyIdTo.name(), partyId, E.roleTypeIdTo.name(), personRoleTypeId, "relationshipValue", "100", "valueUomId", "OTH_100");
        assRelMap.put(E.fromDate.name(), fromDate);
        assRelMap.put(E.relationshipName.name(), gv.getString(E.allocationOrgDescription.name()));
        
        //ATTENZIONE valorizzazione trhuDate in input
        Timestamp allocationOrgThruDate = gv.getTimestamp(E.allocationOrgThruDate.name());
        if (UtilValidate.isNotEmpty(allocationOrgThruDate)) {
        	partyRelationshipDater.setThruDate(assRelMap, tmpThruDate, allocationOrgThruDate);
        }

        String successMsg = "PartyRelationship ORG_ALLOCATION between " + orgUnitAss.getString(E.partyId.name()) + AND_SEP + partyId + FindUtilService.MSG_SUCCESSFULLY_CREATED;
        String errorMsg = "Error in creation PartyRelationship ORG_ALLOCATION between " + orgUnitAss.getString(E.partyId.name()) + AND_SEP + partyId;
        partyRelationshipUtil.controlloUnicaRelazione(E.ORG_ALLOCATION.name(), orgUnitAss.getString(E.partyId.name()), allocationRoleTypeId, partyId, personRoleTypeId, assRelMap, successMsg, errorMsg, false, tmpThruDate);
    }

    protected void doImportProfile() throws GeneralException {
        ImportManager manager = getManager();
        GenericValue gv = getExternalValue();
        String msg = "";

        // 3.c Profilo professionale
        if (UtilValidate.isNotEmpty(gv.getString(E.qualifCode.name()))) {
            List<GenericValue> pprList = manager.getDelegator().findList(E.PartyParentRole.name(), EntityCondition.makeCondition(EntityCondition.makeCondition(E.roleTypeId.name(), "GOALDIV01"), EntityCondition.makeCondition("parentRoleCode", gv.getString(E.qualifCode.name()))), null, null, null, false);
            GenericValue qualifPpr = EntityUtil.getFirst(pprList);
            if (UtilValidate.isNotEmpty(qualifPpr)) {
                String qualifId = qualifPpr.getString(E.partyId.name());
                msg = "Qualification code " + gv.getString(E.qualifCode.name()) + " corresponds to partyId " + qualifId;
                addLogInfo(msg);
                Timestamp fromDate = getQualifFromDate(gv);
                String roleTypeIdFrom = UtilValidate.isNotEmpty(gv.getString(E.personRoleTypeId.name())) ? gv.getString(E.personRoleTypeId.name()) : E.EMPLOYEE.name();
                               
            	PartyRelationshipCleanConditionsBuilder profilesConditionsBuilder = new PartyRelationshipCleanConditionsBuilder(E.QUALIF.name(), fromDate, partyId, "", 
            			"", qualifId, roleTypeIdFrom, E.GOAL05.name(), "", "");                      
                tmpThruDate = partyRelationshipCleaner.cleanFromRelationships(profilesConditionsBuilder, "", true, true);
                Timestamp previuosDay = new Timestamp(getPreviousDay(gv.getTimestamp(E.refDate.name())).getTime());
                partyRelationshipUtil.controlloSoggettoUnicoPadre(profilesConditionsBuilder, previuosDay, true);

                Map<String, Object> serviceMap = UtilMisc.toMap(E.partyIdFrom.name(), partyId, E.roleTypeIdFrom.name(), roleTypeIdFrom, E.partyRelationshipTypeId.name(), E.QUALIF.name(), E.partyIdTo.name(), qualifId, E.roleTypeIdTo.name(), E.GOAL05.name(), E.fromDate.name(), fromDate);
                String successMsg = "PartyRelationship QUALIF between " + partyId + AND_SEP + qualifId + FindUtilService.MSG_SUCCESSFULLY_CREATED;
                String errorMsg = "Error in create PartyRelationship QUALIF between " + partyId + AND_SEP + qualifId;
                partyRelationshipUtil.controlloUnicaRelazione(E.QUALIF.name(), partyId, roleTypeIdFrom, qualifId, E.GOAL05.name(), serviceMap, successMsg, errorMsg, true, tmpThruDate);
            } else {
                msg = "Error: " + gv.getString(E.qualifCode.name()) + " isn't a professional qualification";
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
        }
    }

    /**
     * importa il valutatore/approvatore
     * @param type
     * @throws GeneralException
     */
    protected void doImportEvaluatorApprover() throws GeneralException {
        GenericValue gv = getExternalValue();
        String msg = "";

        // 3.d Valutatore/ Approvatore
        if (UtilValidate.isNotEmpty(gv.getString(PersonInterfaceFieldEnum.approverCode.name())) || UtilValidate.isNotEmpty(gv.getString(PersonInterfaceFieldEnum.evaluatorCode.name())) ) {
            msg = "Populate PersRespInterface for party with code " + gv.getString(PersonInterfaceFieldEnum.personCode.name());
            addLogInfo(msg);
           
            try {
                // popola tabella di interfaccia. senza scatenare importazione
                GenericValue persResp = getManager().getDelegator().makeValue(ImportManagerConstants.PERS_RESP_INTERFACE);
                persResp.setString(PersRespInterfaceFieldEnum.dataSource.name(), gv.getString(PersonInterfaceFieldEnum.dataSource.name()));
                persResp.setString(PersRespInterfaceFieldEnum.refDate.name(), gv.getString(PersonInterfaceFieldEnum.refDate.name()));
                persResp.setString(PersRespInterfaceFieldEnum.personCode.name(), gv.getString(PersonInterfaceFieldEnum.personCode.name()));
                persResp.setString(PersRespInterfaceFieldEnum.approverCode.name(), gv.getString(PersonInterfaceFieldEnum.approverCode.name()));
                persResp.setString(PersRespInterfaceFieldEnum.evaluatorCode.name(), gv.getString(PersonInterfaceFieldEnum.evaluatorCode.name()));
                persResp.set(PersRespInterfaceFieldEnum.fromDate.name(), getEvaluatorFromDate(gv));
                persResp.set(PersRespInterfaceFieldEnum.thruDate.name(), gv.getTimestamp(PersonInterfaceFieldEnum.thruDate.name()));
                
                // se il record esiste gia, da eccezione
                persResp.create();

            } catch (GeneralException e) {
                msg = "Record already exists for " + gv.getString(PersonInterfaceFieldEnum.personCode.name()) + " with evaluatorCode " + gv.getString(E.evaluatorCode.name()) + " and with approverCode " + gv.getString(E.approverCode.name());
                throw new ImportException(getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }
        }
    }
    
    protected void doImportAllocation() throws GeneralException { 
    	
    	// query su ALLOCATION_INTERFACE, se almeno un elemento per personCode cancella tutte le relazioni e invoca l'import delle allocazioni
    	List<GenericValue> allocatIntList = getManager().getDelegator().findList(E.AllocationInterface.name(), 
        		EntityCondition.makeCondition(E.personCode.name(), getExternalValue().getString(E.personCode.name())), null, null, null, false);
        
        if(UtilValidate.isNotEmpty(allocatIntList)) {
        	Date beginYearRefDate = UtilDateTime.getYearStart(UtilDateTime.toTimestamp(getExternalValue().getString(E.refDate.name())));            
            Date endYearRefDate = UtilDateTime.getYearEnd(UtilDateTime.toTimestamp(getExternalValue().getString(E.refDate.name())));
            
            List<EntityCondition> condList = new ArrayList<EntityCondition>();
            condList.add(EntityCondition.makeCondition(E.partyRelationshipTypeId.name(), "ORG_ALLOCATION"));
            condList.add(EntityCondition.makeCondition(E.partyIdTo.name(), getExternalValue().getString(E.personCode.name())));
            condList.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, endYearRefDate));
            condList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, beginYearRefDate));
        	
        	getManager().getDelegator().removeByCondition(E.PartyRelationship.name(), EntityCondition.makeCondition(condList));
        	
        	personInterfaceHelper.importAllocationInterface(getExternalValue().getString(E.personCode.name()));
        }
    }
    
    private void setContextData(GenericValue gv) throws GeneralException {
        PersonInterfaceContext personInterfaceContext = PersonInterfaceContext.get(getManager());
        personInterfaceContext.setPersonCode(gv.getString(E.personCode.name()));
    }

    /**
     * Aggiungo nella mappa per tutti gli elmenti che sono stati disabilitati e il nuovo elemento inserito
     * @param conditionsBuilder
     * @throws GeneralException
     */
    private void setContextPartyUpdateWorkEffortEmploymentAllocation(PartyRelationshipCleanConditionsBuilder conditionsBuilder, String partyRelationshipTypeId, String notPartyIdTo, String comments) throws GeneralException {
        // Anche se thruDate valorizzato, valorizzo la mappa per invocare il servizio custom
        List<String> partyAssegnazione = partyRelationshipUtil.returnListPartyAssegnazione(conditionsBuilder);
        
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("Prepare data for updateWorkEffort with ");
        msgBuilder.append(partyRelationshipTypeId);
        msgBuilder.append(" for ");
        msgBuilder.append(notPartyIdTo);        
        msgBuilder.append(AND_SEP);
        msgBuilder.append(partyAssegnazione);
        msgBuilder.append(", with comments ");
        msgBuilder.append(comments);        
        addLogInfo(msgBuilder.toString());

        contextPartyUpdateWorkEffortNew.put(partyRelationshipTypeId, notPartyIdTo);
        contextPartyUpdateWorkEffortOld.put(partyRelationshipTypeId, partyAssegnazione);
    }

    private Timestamp getEvaluatorFromDate(GenericValue gv) {
        if (UtilValidate.isNotEmpty(gv.getTimestamp(E.evaluatorFromDate.name()))) {
            return gv.getTimestamp(E.evaluatorFromDate.name());
        }
        return gv.getTimestamp(E.refDate.name());
    }

    private Timestamp getQualifFromDate(GenericValue gv) {
        if (UtilValidate.isNotEmpty(gv.getTimestamp(E.qualifFromDate.name()))) {
            return gv.getTimestamp(E.qualifFromDate.name());
        }
        return gv.getTimestamp(E.refDate.name());
    }
    
    private Timestamp getOrgEmplFromDate(GenericValue gv) {
    	if (UtilValidate.isNotEmpty(gv.getTimestamp(E.employmentOrgFromDate.name()))) {
    		return gv.getTimestamp(E.employmentOrgFromDate.name());
    	}
    	return UtilValidate.isNotEmpty(gv.getTimestamp(E.fromDate.name())) && newInsert ? gv.getTimestamp(E.fromDate.name()) : gv.getTimestamp(E.refDate.name());
    }
    
    public boolean isNewInsert() {
        return newInsert;
    }

    public void setNewInsert(boolean newInsert) {
        this.newInsert = newInsert;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getPersonRoleTypeId() {
        return personRoleTypeId;
    }

    public void setPersonRoleTypeId(String personRoleTypeId) {
        this.personRoleTypeId = personRoleTypeId;
    }

    public GenericValue getOrgUnitApp() {
        return orgUnitApp;
    }

    public void setOrgUnitApp(GenericValue orgUnitApp) {
        this.orgUnitApp = orgUnitApp;
    }
}
