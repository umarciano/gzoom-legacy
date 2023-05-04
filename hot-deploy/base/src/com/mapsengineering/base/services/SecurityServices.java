package com.mapsengineering.base.services;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transaction;
import javolution.util.FastMap;
import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.login.LoginServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;
import com.mapsengineering.base.find.PartyEmailFindServices;
import com.mapsengineering.base.util.MessageUtil;

public class SecurityServices {
	public static final String MODULE = SecurityServices.class.getName();
	
	public static final String RESOURCE = "SecurityextUiLabels_custom";
	public static final String RESOURCE_STANDARD = "SecurityextUiLabels";
	
	public static Map<String, Object> resetPassword(DispatchContext ctx, Map<String, Object> context) {
		Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        Security security = ctx.getSecurity();
        
        GenericValue loggedInUserLogin = (GenericValue) context.get("userLogin");
        
        Map<String, Object> result = FastMap.newInstance();
        
        boolean useEncryption = "true".equals(UtilProperties.getPropertyValue("security.properties", "password.encrypt"));
        
        String userLoginId = (String) context.get("userLoginId");
        String errMsg = null;

        if (UtilValidate.isEmpty(userLoginId)) {
            userLoginId = loggedInUserLogin.getString("userLoginId");
        }

        // <b>security check</b>: userLogin userLoginId must equal userLoginId, or must have PARTYMGR_UPDATE permission
        // NOTE: must check permission first so that admin users can set own password without specifying old password
        // TODO: change this security group because we can't use permission groups defined in the applications from the framework.
        if (!security.hasEntityPermission("PARTYMGR", "_UPDATE", loggedInUserLogin)) {
            if (!userLoginId.equals(loggedInUserLogin.getString("userLoginId"))) {
                errMsg = UtilProperties.getMessage(RESOURCE_STANDARD,"loginservices.not_have_permission_update_password_for_user_login", locale);
                return ServiceUtil.returnError(errMsg);
            }
        }
        
        GenericValue userLoginToUpdate = null;

        try {
        	String STANDARD_PASSWORD = UtilProperties.getPropertyValue("security.properties", "password.standard.when.expired");
        	
            userLoginToUpdate = delegator.findOne("UserLogin", false, "userLoginId", userLoginId);
            
            userLoginToUpdate.set("currentPassword", useEncryption ? HashCrypt.getDigestHash(STANDARD_PASSWORD, LoginServices.getHashType()) : STANDARD_PASSWORD, false);
            userLoginToUpdate.set("disabledDateTime", null);
            userLoginToUpdate.set("successiveFailedLogins", 0L);

            try {
                userLoginToUpdate.store();
                
                result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
                result.put("updatedUserLogin", userLoginToUpdate);
            } catch (GenericEntityException e) {
                Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
                errMsg = UtilProperties.getMessage(RESOURCE_STANDARD,"loginservices.could_not_change_password_write_failure", messageMap, locale);
                return ServiceUtil.returnError(errMsg);
            }
        } catch (GenericEntityException e) {
            Map<String, String> messageMap = UtilMisc.toMap("errorMessage", e.getMessage());
            errMsg = UtilProperties.getMessage(RESOURCE_STANDARD,"loginservices.could_not_change_password_read_failure", messageMap, locale);
            return ServiceUtil.returnError(errMsg);
        }
        
        return result;
	}
	
	public static Map<String, Object> checkPasswordValidity(DispatchContext ctx, Map<String, Object> context) {
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        
        Map<String, Object> result = FastMap.newInstance();
        
        if ("true".equals(UtilProperties.getPropertyValue("security.properties", "store.login.history"))) {
	        int validityDays = 0;
	        
	        try {
	        	validityDays = Integer.parseInt(UtilProperties.getPropertyValue("security.properties", "password.validity.days", "0"));
	        } catch (NumberFormatException e) {
	        	validityDays = 0;
	        }
	        
	        if (validityDays == 0) {
	        	result = ServiceUtil.returnSuccess();
	        } else {
	        	GenericValue currentUserLogin = GenericValue.class.cast(context.get("userLogin"));
	        	
	        	String username = currentUserLogin != null ? (String) currentUserLogin.get("userLoginId") : null;
	            if (username == null) username = (String) context.get("usernameToCheck");
	            
	            GenericValue lastUserLoginHistory = null;
	            
	            String password = null;
	            
	            boolean encriptedPassword = true;
	            
	            if (UtilValidate.isNotEmpty(username)) {
	                EntityCondition cond = EntityCondition.makeCondition("userLoginId", username);
                    if ("true".equalsIgnoreCase(UtilProperties.getPropertyValue("security.properties", "username.lowercase"))) {
		                username = username.toLowerCase();
		                cond = EntityCondition.makeCondition(EntityFunction.LOWER_FIELD("userLoginId"), EntityOperator.EQUALS, username);
		            }
	            	
	            	try {
	                    // only get userLogin from cache for service calls; for web and other manual logins there is less time sensitivity
	            	    EntityListIterator eli = delegator.find("UserLogin", cond, null, null, UtilMisc.toList("-createdStamp"), null);
	                    GenericValue user;
	                    if ((user = eli.next()) != null) {
	                        currentUserLogin = user;
	                        username = currentUserLogin.getString("userLoginId");
	                    }
	                    eli.close();
	                    password = currentUserLogin != null ? (String) currentUserLogin.get("currentPassword") : null;
	    	            if (password == null) {
	    	            	encriptedPassword = false;
	    	            }
	                } catch (GenericEntityException e) {
	                    Debug.logWarning(e, "", MODULE);
	                }
	            	
	            	if (currentUserLogin != null && (UtilValidate.isEmpty(currentUserLogin.getString("enabled")) || "Y".equals(currentUserLogin.getString("enabled"))) && (UtilValidate.isEmpty(currentUserLogin.getString("requirePasswordChange")) || "N".equals(currentUserLogin.get("requirePasswordChange")))) {
			            try {
							List<GenericValue> userLoginPasswordHistoryList = delegator.findList("UserLoginPasswordHistory", EntityCondition.makeCondition("userLoginId", username), null, UtilMisc.toList("-fromDate"), null, false);
							if (UtilValidate.isNotEmpty(userLoginPasswordHistoryList)) {
								lastUserLoginHistory = EntityUtil.getFirst(userLoginPasswordHistoryList);
								if (!checkUserLoginPasswordValidity(lastUserLoginHistory, validityDays)) {
									//Password was expired. Disable userLogin, put standard password and set requirePasswordChange
									Timestamp expiredDate = lastUserLoginHistory.getTimestamp("thruDate");
									if (expiredDate == null) {
										expiredDate = lastUserLoginHistory.getTimestamp("fromDate");
									}
									
									Map<String, Object> localResult = disableUserLoginAndSetStandardPassword(ctx, context, username, expiredDate);
									if (ServiceUtil.isError(localResult)) {
										return localResult;
									}
									
									
									return ServiceUtil.returnError(UtilProperties.getMessage(RESOURCE,"loginservices.password_expired_changepassword", UtilMisc.toMap("daysExpired", UtilDateTime.getIntervalInDays(lastUserLoginHistory.getTimestamp("fromDate"), UtilDateTime.nowTimestamp())), locale));
								}
							}
						} catch (GenericEntityException e) {
							Debug.logWarning(e, "", MODULE);
							
							return ServiceUtil.returnError(e.getMessage());
						} catch (GenericServiceException e) {
							Debug.logWarning(e, "", MODULE);
							
							return ServiceUtil.returnError(e.getMessage());
						}
	            	}
		            
		            
	            }
	            
	        }
        }
        
        return result;
	}
	
	private static boolean checkUserLoginPasswordValidity(GenericValue userLoginPasswordHistory, int validityDays) {
		boolean res = true;
				
		if (userLoginPasswordHistory != null && userLoginPasswordHistory.get("thruDate") == null) {
			Timestamp fromDate = userLoginPasswordHistory.getTimestamp("fromDate");
			Timestamp now = UtilDateTime.nowTimestamp();
			
		    res = UtilDateTime.getIntervalInDays(fromDate, now) <= validityDays;
		}
		
		return res;
	}
	
	private static Map<String, Object> disableUserLoginAndSetStandardPassword(DispatchContext ctx, Map<String, Object> context, String userLoginId, Date expiredDate) throws GenericEntityException, GenericServiceException {
		
		// this section is being done in its own transaction rather than in the
        //current/existing transaction because we may return error and we don't
        //want that to stop this from getting stored
        Transaction parentTx = null;
        boolean beganTransaction = false;

        try {
            try {
                parentTx = TransactionUtil.suspend();
            } catch (GenericTransactionException e) {
                Debug.logError(e, "Could not suspend transaction: " + e.getMessage(), MODULE);
            }

            try {
                beganTransaction = TransactionUtil.begin();

                Delegator delegator = ctx.getDelegator();
        		
            	GenericValue userLogin = delegator.findOne("UserLogin", false, "userLoginId", userLoginId);
            	if (userLogin != null && "Y".equals(userLogin.getString("enabled"))) {
            		userLogin.set("requirePasswordChange", "Y");
//            		userLogin.set("disabledDateTime", UtilDateTime.nowTimestamp());
            		userLogin.store();
            		
            		
            		EntityFindOptions efo = new EntityFindOptions();
                    efo.setResultSetType(EntityFindOptions.TYPE_SCROLL_INSENSITIVE);
                    EntityListIterator eli = delegator.find("UserLoginPasswordHistory", EntityCondition.makeConditionMap("userLoginId", userLoginId), null, null, UtilMisc.toList("-fromDate"), efo);
                    Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
                    GenericValue pwdHist;
                    if ((pwdHist = eli.next()) != null) {
                        // updating password so set end date on previous password in history
                        pwdHist.set("thruDate", nowTimestamp);
                        pwdHist.store();
                        // check if we have hit the limit on number of password changes to be saved. If we did then delete the oldest password from history.
                        eli.last();
                    }
                    eli.close();
            	}
            } catch (GenericEntityException e) {
                String geeErrMsg = "Error resetting password";
                geeErrMsg += ": " + e.toString();
                try {
                    TransactionUtil.rollback(beganTransaction, geeErrMsg, e);
                } catch (GenericTransactionException e2) {
                    Debug.logError(e2, "Could not rollback nested transaction: " + e2.getMessage(), MODULE);
                }

                return ServiceUtil.returnError(geeErrMsg);
            } finally {
                try {
                    TransactionUtil.commit(beganTransaction);
                } catch (GenericTransactionException e) {
                    Debug.logError(e, "Could not commit nested transaction: " + e.getMessage(), MODULE);
                }
            }
        } finally {
            // resume/restore parent transaction
            if (parentTx != null) {
                try {
                    TransactionUtil.resume(parentTx);
                    Debug.logVerbose("Resumed the parent transaction.", MODULE);
                } catch (GenericTransactionException e) {
                    Debug.logError(e, "Could not resume parent nested transaction: " + e.getMessage(), MODULE);
                }
            }
        }
        
        return ServiceUtil.returnSuccess();
		
	}
	
	/**
     * If user is logged in, return http session id.
     *
     * @param ctx
     * @param context
     * @return sessionId
     */
    public static Map<String, Object> gzSimpleLogin(DispatchContext ctx, Map<String, ? extends Object> context) {
        try {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            HttpServletRequest req = (HttpServletRequest) context.get("request");
            req.setAttribute("userLogin", userLogin);
            req.getSession().setAttribute("userLogin", userLogin);
            
            Delegator delegator = ctx.getDelegator();
            GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);
            
            Map<String, Object> result = ServiceUtil.returnSuccess();
            String email = getEmailAddress(delegator, userLogin.getString("partyId"));
            if (UtilValidate.isNotEmpty(email)){
                result.put("email", email);
            }
            result.put("firstName", person.getString("firstName"));
            result.put("lastName", person.getString("lastName"));
            String externalLoginKey = LoginWorker.getExternalLoginKey(req);
            result.put(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR, externalLoginKey);
            
            LoginWorker.doBasicLogin(userLogin, req);
            return result;
        } catch (Exception e) {
            return ServiceUtil.returnError("Error call gzSimpleLogin " + MessageUtil.getExceptionMessage(e));
        }
    }

    private static String getEmailAddress(Delegator delegator, String partyId) throws GeneralException {
        PartyEmailFindServices partyEmailFindServices = new PartyEmailFindServices(delegator);
        List<GenericValue> emailAddressesList = partyEmailFindServices.getEmailAddress(partyId);
        GenericValue contact = EntityUtil.getFirst(emailAddressesList);
        
        if (UtilValidate.isNotEmpty(contact)) {
            return contact.getString("infoString");
        }
        return null;
    }
    
    
 
    
    
    
	/**
     * If user is logged in, return http session id.
     *
     * @param ctx
     * @param context
     * @return sessionId
     */
    public static Map<String, Object> gzSimpleLoginWithOnlyUserLoginId(DispatchContext ctx, Map<String, ? extends Object> context) {
        try {
            Delegator delegator = ctx.getDelegator();
            GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", context.get("login.username")), false);
            HttpServletRequest req = (HttpServletRequest) context.get("request");
            req.setAttribute("userLogin", userLogin);
            req.getSession().setAttribute("userLogin", userLogin);
            GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);
            Map<String, Object> result = ServiceUtil.returnSuccess();
            String email = getEmailAddress(delegator, userLogin.getString("partyId"));
            if (UtilValidate.isNotEmpty(email)){
                result.put("email", email);
            }
            result.put("firstName", person.getString("firstName"));
            result.put("lastName", person.getString("lastName"));
            String externalLoginKey = LoginWorker.getExternalLoginKey(req);
            result.put(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR, externalLoginKey);
            LoginWorker.doBasicLogin(userLogin,req);
            return result;
        } catch (Exception e) {
            return ServiceUtil.returnError("Error call gzSimpleLoginWithOnlyUserLoginId " + MessageUtil.getExceptionMessage(e));
        }
    }

    
    
    
    public static Map<String, Object> gzLogout(DispatchContext ctx, Map<String, ? extends Object> context) {
        try {
            Delegator delegator = ctx.getDelegator();
            GenericValue userLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", context.get("login.username")), false);
            HttpServletRequest req = (HttpServletRequest) context.get("request");
            req.setAttribute("userLogin", userLogin);
            req.getSession().setAttribute("userLogin", userLogin);
            HttpServletResponse res = (HttpServletResponse) context.get("response");
            GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", userLogin.getString("partyId")), false);
            Map<String, Object> result = ServiceUtil.returnSuccess();
            String email = getEmailAddress(delegator, userLogin.getString("partyId"));
            if (UtilValidate.isNotEmpty(email)){
                result.put("email", email);
            }
            result.put("firstName", person.getString("firstName"));
            result.put("lastName", person.getString("lastName"));
            String externalLoginKey = LoginWorker.getExternalLoginKey(req);
            result.put(LoginWorker.EXTERNAL_LOGIN_KEY_ATTR, externalLoginKey);
            LoginWorker.doBasicLogout(userLogin, req, res);
            return result;
        } catch (Exception e) {
            return ServiceUtil.returnError("Error call gzLogout " + MessageUtil.getExceptionMessage(e));
        }
    }
    
    
    
    
    
    
    
 
    
    
    
    
    /**
     * If user is logged in, return http session id.
     *
     * @param ctx
     * @param context
     * @return sessionId
     */
    public static Map<String, Object> gzChangePassword(DispatchContext ctx, Map<String, ? extends Object> context) {
        try {
        	Debug.log("** gzChangePassword begin");
            Map<String, Object> resultUpdate = LoginServices.updatePassword(ctx, context);
            if (!ServiceUtil.isSuccess(resultUpdate)) {
            	return resultUpdate;
            }
            return ServiceUtil.returnSuccess();
        } catch (Exception e) {
            return ServiceUtil.returnError("Error call gzChangePassword " + MessageUtil.getExceptionMessage(e));
        }
    }
}
