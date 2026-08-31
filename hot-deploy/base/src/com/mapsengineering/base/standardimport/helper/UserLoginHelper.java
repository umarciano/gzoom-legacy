package com.mapsengineering.base.standardimport.helper;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.FindUtilService;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * Manage UserLogin, UserLoginSecurityGroup, UserLoginValidPartyRole 
 *
 */
public class UserLoginHelper {

    private TakeOverService takeOverService;
    private static final String MODULE = UserLoginHelper.class.getName();
    private static final String MSG_USER_LOGIN = "User Login ";
    private static final String MSG_TO_SECURITY_GROUP = " to security group ";

    /** Caratteri usati per la generazione della password casuale */
    private static final String PWD_CHARS_UPPER   = "ABCDEFGHJKLMNPQRSTUVWXYZ";    // no I, O
    private static final String PWD_CHARS_LOWER   = "abcdefghjkmnpqrstuvwxyz";     // no i, l, o
    private static final String PWD_CHARS_DIGITS   = "23456789";                   // no 0, 1
    private static final String PWD_CHARS_SPECIAL  = "!@#$%&*";
    private static final int    PWD_RANDOM_LENGTH  = 12;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    /** Constructor */
    public UserLoginHelper(TakeOverService takeOverService) {
        this.takeOverService = takeOverService;
    }

    /**
     * Create userLogin, if not exists, and enabled or disabled it 
     * @param partyId
     * @param GenericValue gv
     * @throws GeneralException
     */
    public void doImportUserLogin(String partyId, GenericValue gv) throws GeneralException {
        ImportManager manager = takeOverService.getManager();
        String msg = "";

        // 4 UserLogin
        if (UtilValidate.isNotEmpty(gv.getString(E.userLoginId.name()))) {
            GenericValue userLogin = manager.getDelegator().findOne(E.UserLogin.name(), UtilMisc.toMap(E.userLoginId.name(), gv.getString(E.userLoginId.name())), false);
            if (UtilValidate.isEmpty(userLogin)) {
                // if (UtilValidate.isEmpty(gv.getTimestamp(E.thruDate.name()))) {
                msg = "Creating user login " + gv.getString(E.userLoginId.name()) + " for party " + partyId;
                takeOverService.addLogInfo(msg);
                String loginPasswordNoteInfo = getLoginPasswordNoteInfo(gv);
                String currentPassword = "";
                // GN-4978 / logica password iniziale configurabile via security.properties
                // property: password.standardimport.mode = SMVP_CF (default) | RANDOM
                String fiscalCode = gv.getString(E.fiscalCode.name());
                String passwordMode = UtilProperties.getPropertyValue("security", "password.standardimport.mode", "SMVP_CF");
                String defaultPassword;
                if ("RANDOM".equalsIgnoreCase(passwordMode)) {
                    // Password casuale di 12 caratteri (sicura, non legata ai dati dell'utente)
                    // Per recuperarla: SELECT current_password FROM user_login WHERE user_login_id = '...'
                    // (il valore è hashato con SHA; per decodificarlo vedere la query nella documentazione)
                    defaultPassword = generateRandomPassword();
                } else if (UtilValidate.isNotEmpty(fiscalCode)) {
                    // Usa SMVP + CF in maiuscolo come password iniziale
                    defaultPassword = "SMVP" + fiscalCode.toUpperCase();
                } else {
                    // Fallback: PWD + userLoginId (+ eventuale suffix)
                    String suffix = UtilProperties.getPropertyValue("security", "password.standardimport.suffix");
                    defaultPassword = "PWD" + gv.getString(E.userLoginId.name()) + (UtilValidate.isNotEmpty(suffix) ? suffix : "");
                }
                currentPassword = UtilValidate.isNotEmpty(loginPasswordNoteInfo) ? loginPasswordNoteInfo : defaultPassword;
                // Non forzare il cambio password al primo accesso
                String requirePasswordChange = "N";
                Map<String, Object> serviceMap = FastMap.newInstance();
                serviceMap.put(E.userLogin.name(), manager.getUserLogin());
                serviceMap.put(E.userLoginId.name(), gv.getString(E.userLoginId.name()));
                serviceMap.put("currentPassword", currentPassword);
                serviceMap.put("currentPasswordVerify", currentPassword);
                serviceMap.put("requirePasswordChange", requirePasswordChange);
                serviceMap.put(E.enabled.name(), "Y");
                serviceMap.put(E.partyId.name(), partyId);
                // TODO ripulire anche disabledDatetime? 
                String successMsg = MSG_USER_LOGIN + gv.getString(E.userLoginId.name()) + FindUtilService.MSG_SUCCESSFULLY_CREATED;
                String errorMsg = "Error in creation of user login " + gv.getString(E.userLoginId.name());
                takeOverService.runSync("createUserLogin", serviceMap, successMsg, errorMsg, true);
                
                // Imposta lastLocale dopo la creazione dell'utente
                userLogin = manager.getDelegator().findOne("UserLogin", 
                    UtilMisc.toMap("userLoginId", gv.getString(E.userLoginId.name())), false);
                if (userLogin != null) {
                    userLogin.set("lastLocale", "it_IT");
                    userLogin.store();
                }
            } else {
                msg = MSG_USER_LOGIN + gv.getString(E.userLoginId.name()) + " already exists";
                takeOverService.addLogInfo(msg);
            }

            if (UtilValidate.isNotEmpty(gv.getTimestamp(E.thruDate.name()))) {
                updateUserLoginSecurity(gv, "N", " successfully disabled");
            }
            doImportUserLoginValidPartyRole(gv);
            doImportUserPreference(gv);
            doImportUserLoginSecurityGroup(partyId, gv);
        }
    }
    
    /**
     * cerca la nota AUTOMATIC_LOGIN_PASSWORD
     * @param gv
     * @return
     * @throws GeneralException
     */
    private String getLoginPasswordNoteInfo(GenericValue gv) throws GeneralException {
    	ImportManager manager = takeOverService.getManager();
    	List<EntityCondition> condList = new ArrayList<EntityCondition>();
    	String defaultOrganizationPartyId = (String) manager.getContext().get(E.defaultOrganizationPartyId.name());
    	condList.add(EntityCondition.makeCondition(E.targetPartyId.name(), defaultOrganizationPartyId));
    	condList.add(EntityCondition.makeCondition(E.noteName.name(), E.AUTOMATIC_LOGIN_PASSWORD.name()));
    	List<GenericValue> partyNoteList = manager.getDelegator().findList(E.PartyNoteView.name(), EntityCondition.makeCondition(condList), null, null, null, false);
    	GenericValue partyNote = EntityUtil.getFirst(partyNoteList);
    	if (UtilValidate.isNotEmpty(partyNote)) {
    		return partyNote.getString(E.noteInfo.name());
    	}
    	return null;
    }

    /**
     * Genera una password casuale crittograficamente sicura di lunghezza PWD_RANDOM_LENGTH.
     * La password contiene almeno un carattere per ciascuna categoria:
     * maiuscole, minuscole, cifre, carattere speciale.
     */
    private String generateRandomPassword() {
        String allChars = PWD_CHARS_UPPER + PWD_CHARS_LOWER + PWD_CHARS_DIGITS + PWD_CHARS_SPECIAL;
        char[] password = new char[PWD_RANDOM_LENGTH];
        // Garantisce almeno un carattere per categoria
        password[0] = PWD_CHARS_UPPER.charAt(SECURE_RANDOM.nextInt(PWD_CHARS_UPPER.length()));
        password[1] = PWD_CHARS_LOWER.charAt(SECURE_RANDOM.nextInt(PWD_CHARS_LOWER.length()));
        password[2] = PWD_CHARS_DIGITS.charAt(SECURE_RANDOM.nextInt(PWD_CHARS_DIGITS.length()));
        password[3] = PWD_CHARS_SPECIAL.charAt(SECURE_RANDOM.nextInt(PWD_CHARS_SPECIAL.length()));
        // Riempie il resto con caratteri casuali dall'insieme completo
        for (int i = 4; i < PWD_RANDOM_LENGTH; i++) {
            password[i] = allChars.charAt(SECURE_RANDOM.nextInt(allChars.length()));
        }
        // Mescola l'array per evitare posizioni prevedibili
        for (int i = PWD_RANDOM_LENGTH - 1; i > 0; i--) {
            int j = SECURE_RANDOM.nextInt(i + 1);
            char tmp = password[i];
            password[i] = password[j];
            password[j] = tmp;
        }
        return new String(password);
    }

    protected void updateUserLoginSecurity(GenericValue gv, String enabled, String partialSuccessMsg) throws GeneralException {
        ImportManager manager = takeOverService.getManager();

        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.userLogin.name(), manager.getUserLogin());
        serviceMap.put(E.userLoginId.name(), gv.getString(E.userLoginId.name()));
        serviceMap.put(E.enabled.name(), enabled);

        String successMsg = MSG_USER_LOGIN + gv.getString(E.userLoginId.name()) + partialSuccessMsg;
        String errorMsg = "Error in disable of user login " + gv.getString(E.userLoginId.name());
        takeOverService.runSync("updateUserLoginSecurity", serviceMap, successMsg, errorMsg, true);
    }

    /**
     * Create UserLoginValidPartyRole with Company
     * @param GenericValue gv
     * @throws GeneralException
     */
    public void doImportUserLoginValidPartyRole(GenericValue gv) throws GeneralException {
        ImportManager manager = takeOverService.getManager();
        String msg = "";

        // 4.b UserLoginValidPartyRole
        String defaultOrganizationPartyId = (String) manager.getContext().get(E.defaultOrganizationPartyId.name());
        GenericValue userLogin = manager.getDelegator().findOne(E.UserLogin.name(), UtilMisc.toMap(E.userLoginId.name(), gv.getString(E.userLoginId.name())), false);
        if (UtilValidate.isNotEmpty(userLogin)) {
            GenericValue userPartyRole = manager.getDelegator().findOne("UserLoginValidPartyRole", UtilMisc.toMap(E.userLoginId.name(), gv.getString(E.userLoginId.name()), E.partyId.name(), defaultOrganizationPartyId, E.roleTypeId.name(), E.INTERNAL_ORGANIZATIO.name()), false);
            if (UtilValidate.isEmpty(userPartyRole)) {
                msg = "Creating USER_LOGIN_VALID_PARTY_ROLE for userLogin " + gv.getString(E.userLoginId.name());
                takeOverService.addLogInfo(msg);
                GenericValue userLoginValidPartyRole = manager.getDelegator().makeValue("UserLoginValidPartyRole");
                userLoginValidPartyRole.set(E.userLoginId.name(), gv.getString(E.userLoginId.name()));
                userLoginValidPartyRole.set(E.partyId.name(), defaultOrganizationPartyId);
                userLoginValidPartyRole.set(E.roleTypeId.name(), E.INTERNAL_ORGANIZATIO.name());
                manager.getDelegator().create(userLoginValidPartyRole);

            } else {
                msg = "UserLoginValidPartyRole for userLoginId " + gv.getString(E.userLoginId.name()) + " already exists";
                takeOverService.addLogInfo(msg);
            }
        } else {
            msg = "UserLogin for userLoginId " + gv.getString(E.userLoginId.name()) + " not exist, UserLoginValidPartyRole not created";
            takeOverService.addLogInfo(msg);
        }
    }
    
    /**
     * crea UserPreference
     * @param gv
     * @throws GeneralException
     */
    public void doImportUserPreference(GenericValue gv) throws GeneralException {
    	ImportManager manager = takeOverService.getManager();
    	String defaultOrganizationPartyId = (String) manager.getContext().get(E.defaultOrganizationPartyId.name());
    	Map<String, Object> userPrefMap = FastMap.newInstance();
    	userPrefMap.put(E.userLogin.name(), manager.getUserLogin());
    	userPrefMap.put(E.userLoginId.name(), gv.getString(E.userLoginId.name()));
    	userPrefMap.put(E.partyId.name(), defaultOrganizationPartyId);
        String successMsg = "UserPreference successfully created";
        String errorMsg = "Error in creating UserPreference";
        takeOverService.runSync("setUserPrefValueFromUserLoginValidPartyRole", userPrefMap, successMsg, errorMsg, true);
        
        // CUSTOMIZATION: Imposta automaticamente VISUAL_THEME = GPLUS_BLUE_LIGHT per ogni utente importato
        GenericValue visualThemePref = manager.getDelegator().makeValue("UserPreference");
        visualThemePref.set(E.userLoginId.name(), gv.getString(E.userLoginId.name()));
        visualThemePref.set("userPrefTypeId", "VISUAL_THEME");
        visualThemePref.set("userPrefGroupTypeId", "GLOBAL_PREFERENCES");
        visualThemePref.set("userPrefValue", "GPLUS_BLUE_LIGHT");
        manager.getDelegator().createOrStore(visualThemePref);
        takeOverService.addLogInfo("VISUAL_THEME preference set to GPLUS_BLUE_LIGHT for user " + gv.getString(E.userLoginId.name()));
    }

    protected void doImportUserLoginSecurityGroup(String partyId, GenericValue gv) throws GeneralException {
        ImportManager manager = takeOverService.getManager();
        String msg = "";

        // 4.c UserLoginSecurityGroup
        if (UtilValidate.isNotEmpty(gv.getString(E.groupId.name()))) {
            GenericValue securityGroup = manager.getDelegator().findOne("SecurityGroup", UtilMisc.toMap(E.groupId.name(), gv.getString(E.groupId.name())), false);
            if (UtilValidate.isEmpty(securityGroup)) {
                msg = "Error: SecurityGroup " + gv.getString(E.groupId.name()) + " does not exists";
                throw new ImportException(takeOverService.getEntityName(), gv.getString(ImportManagerConstants.RECORD_FIELD_ID), msg);
            }

            List<EntityCondition> condList = FastList.newInstance();
            if (UtilValidate.isNotEmpty(gv.getTimestamp(E.thruDate.name()))) {
                condList = getUserLoginSecurityGorupCondList(gv, false);
                // disable userLoginSecutityGroup
                disableUserLoginSecurityGroup(partyId, EntityCondition.makeCondition(condList), gv.getTimestamp(E.thruDate.name()));
            } else {
            	condList = getUserLoginSecurityGorupCondList(gv, true);
                manageUserLoginSecurityGroup(gv.getString(E.userLoginId.name()), gv.getString(E.groupId.name()), gv.getTimestamp(E.refDate.name()), condList);
            }
        }
    }
    
    /**
     * costruisce condizioni di ricerca UserLoginSecurityGroup
     * @param groupId
     * @return
     */
    private List<EntityCondition> getUserLoginSecurityGorupCondList(GenericValue gv, boolean withGroupId) {
        List<EntityCondition> condList = FastList.newInstance();
        condList.add(EntityUtil.getFilterByDateExpr());
        condList.add(EntityCondition.makeCondition(E.userLoginId.name(), gv.getString(E.userLoginId.name())));
        if (withGroupId) {
        	condList.add(EntityCondition.makeCondition(E.groupId.name(), gv.getString(E.groupId.name())));
        }
        return condList;
    }

    private void manageUserLoginSecurityGroup(String userLoginId, String groupId, Timestamp refDate, List<EntityCondition> condList) throws GeneralException {
        ImportManager manager = takeOverService.getManager();
        String msg = "";
        // enable userLoginSecutityGroup
        List<GenericValue> usgList = manager.getDelegator().findList("UserLoginSecurityGroup", EntityCondition.makeCondition(condList), null, null, null, false);

        GenericValue usg = EntityUtil.getFirst(usgList);
        if (UtilValidate.isNotEmpty(usg)) {
            msg = "User login already belongs to security group " + groupId;
            takeOverService.addLogInfo(msg);
        } else {
            msg = "Adding userLogin " + userLoginId + MSG_TO_SECURITY_GROUP + groupId;
            takeOverService.addLogInfo(msg);
            Map<String, Object> serviceMap = FastMap.newInstance();
            serviceMap.put(E.userLogin.name(), manager.getUserLogin());
            serviceMap.put(E.userLoginId.name(), userLoginId);
            serviceMap.put(E.groupId.name(), groupId);
            serviceMap.put(E.fromDate.name(), refDate);

            String successMsg = MSG_USER_LOGIN + userLoginId + " successfully added to Group " + groupId;
            String errorMsg = "Error in adding user login " + userLoginId + MSG_TO_SECURITY_GROUP + groupId;

            takeOverService.runSync("addUserLoginToSecurityGroupExt", serviceMap, successMsg, errorMsg, true);
        }
    }

    /**
     * Disable userLogin
     * @param partyId
     * @param GenericValue gv
     * @throws GeneralException
     */
    public void doImportExpiredUserLogin(String partyId, GenericValue gv) throws GeneralException {
        ImportManager manager = takeOverService.getManager();
        String msg = "";

        // Disabilito UserLogin
        if (UtilValidate.isNotEmpty(gv.getTimestamp(E.thruDate.name()))) {
            List<GenericValue> userLoginList = manager.getDelegator().findByAnd(E.UserLogin.name(), UtilMisc.toMap(E.partyId.name(), partyId, E.enabled.name(), "Y"));
            msg = "Found " + userLoginList.size() + " UserLogin of party " + partyId + " to disable";
            takeOverService.addLogInfo(msg);
            for (GenericValue userLogin : userLoginList) {
                Map<String, Object> serviceMap = FastMap.newInstance();
                serviceMap.put(E.userLogin.name(), manager.getUserLogin());
                serviceMap.put(E.userLoginId.name(), userLogin.getString(E.userLoginId.name()));
                serviceMap.put(E.enabled.name(), "N");
                String successMsg = MSG_USER_LOGIN + userLogin.getString(E.userLoginId.name()) + " successfully disabled";
                String errorMsg = "Error in disable of user login " + userLogin.getString(E.userLoginId.name());
                takeOverService.runSync("updateUserLoginSecurity", serviceMap, successMsg, errorMsg, true);

                List<EntityCondition> condList = FastList.newInstance();
                condList.add(EntityCondition.makeCondition(E.thruDate.name(), null));
                condList.add(EntityCondition.makeCondition(E.userLoginId.name(), userLogin.getString(E.userLoginId.name())));
                disableUserLoginSecurityGroup(partyId, EntityCondition.makeCondition(condList), gv.getTimestamp(E.thruDate.name()));
            }
        }
    }

    protected void disableUserLoginSecurityGroup(String partyId, EntityCondition condition, Timestamp thruDate) throws GeneralException {
        ImportManager manager = takeOverService.getManager();

        List<GenericValue> userLoginSecurityGroupList = manager.getDelegator().findList("UserLoginSecurityGroup", condition, null, null, null, false);
        String msg = "Found " + userLoginSecurityGroupList.size() + " userLoginSecurityGroupList of party " + partyId + " to delete";
        takeOverService.addLogInfo(msg);
        for (GenericValue userLoginSecurityGroup : userLoginSecurityGroupList) {

            Map<String, Object> serviceMapULSG = FastMap.newInstance();
            serviceMapULSG.put(E.userLogin.name(), manager.getUserLogin());
            serviceMapULSG.put(E.userLoginId.name(), userLoginSecurityGroup.getString(E.userLoginId.name()));
            serviceMapULSG.put(E.groupId.name(), userLoginSecurityGroup.getString(E.groupId.name()));
            serviceMapULSG.put(E.fromDate.name(), userLoginSecurityGroup.getTimestamp(E.fromDate.name()));
            serviceMapULSG.put(E.thruDate.name(), thruDate);

            String successMsg = MSG_USER_LOGIN + userLoginSecurityGroup.getString(E.userLoginId.name()) + " for Group " + userLoginSecurityGroup.getString(E.groupId.name()) + " successfully update with thruDate " + thruDate;
            String errorMsg = "Error in update user login " + userLoginSecurityGroup.getString(E.userLoginId.name()) + MSG_TO_SECURITY_GROUP + userLoginSecurityGroup.getString(E.groupId.name());
            takeOverService.runSync("updateUserLoginToSecurityGroup", serviceMapULSG, successMsg, errorMsg, true);
        }
    }
}
