package com.mapsengineering.base.authentication;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.util.List;

/**
 * SAML User Matcher - Database matching for SAML authentication
 */
public class SamlUserMatcher {
    
    private static final String MODULE = SamlUserMatcher.class.getName();
    
    public static GenericValue matchByMatricola(Delegator delegator, String matricola) {
        if (delegator == null || matricola == null || matricola.trim().isEmpty()) {
            Debug.logError("Invalid parameters for matchByMatricola", MODULE);
            return null;
        }
        
        try {
            Debug.logInfo("=== SamlUserMatcher.matchByMatricola() - START ===", MODULE);
            Debug.logInfo("Input matricola: '" + matricola + "'", MODULE);
            
            EntityCondition condition = EntityCondition.makeCondition("parentRoleCode", EntityOperator.EQUALS, matricola);
            List<GenericValue> partyParentRoles = delegator.findList("PartyParentRole", condition, null, null, null, false);
            
            if (UtilValidate.isEmpty(partyParentRoles)) {
                Debug.logWarning("No PartyParentRole found with parentRoleCode: '" + matricola + "'", MODULE);
                return null;
            }
            
            GenericValue partyParentRole = partyParentRoles.get(0);
            String partyId = partyParentRole.getString("partyId");
            Debug.logInfo("PartyId from PartyParentRole: '" + partyId + "'", MODULE);
            
            // UserLogin primary key is userLoginId, not partyId - use findList instead
            EntityCondition conditionUserLogin = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
            List<GenericValue> userLogins = delegator.findList("UserLogin", conditionUserLogin, null, null, null, false);
            
            if (UtilValidate.isEmpty(userLogins)) {
                Debug.logWarning("No UserLogin found for partyId: '" + partyId + "'", MODULE);
                return null;
            }
            
            GenericValue userLogin = userLogins.get(0);
            
            String userLoginId = userLogin.getString("userLoginId");
            Debug.logInfo("UserLogin found: '" + userLoginId + "'", MODULE);
            Debug.logInfo("=== SamlUserMatcher.matchByMatricola() - END - SUCCESS ===", MODULE);
            
            return userLogin;
            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Database error in matchByMatricola for matricola: '" + matricola + "'", MODULE);
            return null;
        }
    }
    
    public static boolean validateFiscalCode(Delegator delegator, GenericValue userLogin, String fiscalCode) {
        if (delegator == null || userLogin == null) {
            Debug.logError("Invalid parameters for validateFiscalCode", MODULE);
            return false;
        }
        
        // Se il fiscalCode non è fornito dall'IdP, saltiamo la validazione e ci fidiamo della matricola
        if (fiscalCode == null || fiscalCode.trim().isEmpty()) {
            Debug.logWarning("FiscalCode non fornito dall'IdP - validazione saltata, autenticazione basata solo su matricola", MODULE);
            return true;
        }
        
        try {
            Debug.logInfo("=== SamlUserMatcher.validateFiscalCode() - START ===", MODULE);
            
            String partyId = userLogin.getString("partyId");
            Debug.logInfo("PartyId: '" + partyId + "'", MODULE);
            Debug.logInfo("Expected fiscalCode: '" + fiscalCode + "'", MODULE);
            
            GenericValue party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));
            
            if (party == null) {
                Debug.logError("Party not found for partyId: '" + partyId + "'", MODULE);
                return false;
            }
            
            String dbFiscalCode = party.getString("fiscalCode");
            Debug.logInfo("DB fiscalCode: '" + dbFiscalCode + "'", MODULE);
            
            if (UtilValidate.isEmpty(dbFiscalCode)) {
                Debug.logWarning("FiscalCode is NULL/EMPTY in database for partyId: '" + partyId + "' - Tolerating for legacy users", MODULE);
                Debug.logInfo("=== SamlUserMatcher.validateFiscalCode() - END - TOLERATED ===", MODULE);
                return true;
            }
            
            boolean matches = fiscalCode.equalsIgnoreCase(dbFiscalCode);
            
            if (matches) {
                Debug.logInfo("FiscalCode MATCH - Input: '" + fiscalCode + "' == DB: '" + dbFiscalCode + "'", MODULE);
                Debug.logInfo("=== SamlUserMatcher.validateFiscalCode() - END - SUCCESS ===", MODULE);
            } else {
                Debug.logError("FiscalCode MISMATCH - Input: '" + fiscalCode + "' != DB: '" + dbFiscalCode + "'", MODULE);
                Debug.logInfo("=== SamlUserMatcher.validateFiscalCode() - END - FAILURE ===", MODULE);
            }
            
            return matches;
            
        } catch (GenericEntityException e) {
            Debug.logError(e, "Database error in validateFiscalCode for partyId: '" + userLogin.getString("partyId") + "'", MODULE);
            return false;
        }
    }
    
    public static GenericValue matchUser(Delegator delegator, String matricola, String fiscalCode) {
        Debug.logInfo("=== SamlUserMatcher.matchUser() - START ===", MODULE);
        Debug.logInfo("Input - matricola: '" + matricola + "', fiscalCode: '" + fiscalCode + "'", MODULE);
        
        GenericValue userLogin = matchByMatricola(delegator, matricola);
        
        if (userLogin == null) {
            Debug.logError("matchByMatricola FAILED - No user found with matricola: '" + matricola + "'", MODULE);
            Debug.logInfo("=== SamlUserMatcher.matchUser() - END - FAILURE ===", MODULE);
            return null;
        }
        
        boolean fiscalCodeValid = validateFiscalCode(delegator, userLogin, fiscalCode);
        
        if (!fiscalCodeValid) {
            Debug.logError("validateFiscalCode FAILED - FiscalCode mismatch for user: '" + userLogin.getString("userLoginId") + "'", MODULE);
            Debug.logInfo("=== SamlUserMatcher.matchUser() - END - FAILURE ===", MODULE);
            return null;
        }
        
        Debug.logInfo("SAML USER MATCH SUCCESS - UserLoginId: '" + userLogin.getString("userLoginId") + "'", MODULE);
        Debug.logInfo("=== SamlUserMatcher.matchUser() - END - SUCCESS ===", MODULE);
        
        return userLogin;
    }
}
