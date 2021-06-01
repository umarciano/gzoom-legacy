package com.mapsengineering.base.delete.userlogin;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.crypto.HashCrypt;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.mapsengineering.base.services.GenericServiceLoop;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.TransactionItem;
import com.mapsengineering.base.util.TransactionRunner;

/**
 * Delete a workEffort root with its child
 * @author dain
 *
 */
public class UserLoginPhysicalDelete extends GenericServiceLoop {

    public static final String MODULE = UserLoginPhysicalDelete.class.getName();
    public static final String SERVICE_NAME = "UserLoginPhysicalDelete";
    public static final String SERVICE_TYPE = "USER_LOGIN_DELETE";

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public UserLoginPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE, MODULE);
    }
    
    /**
     * Delete workeffort root with child
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> userLoginPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
        String sessionId = (String)context.get(ServiceLogger.SESSION_ID);
        if (UtilValidate.isEmpty(sessionId)) {
            String nowAsString = UtilDateTime.nowAsString();
            sessionId = HashCrypt.getDigestHash(nowAsString, "SHA");
            sessionId = sessionId.substring(37);
            context.put(ServiceLogger.SESSION_ID, sessionId);
        }
        
        UserLoginPhysicalDelete obj = new UserLoginPhysicalDelete(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Find and delete workeffort with same workEffortParentId
     * @throws Exception
     */
    protected void execute() throws Exception {
        new TransactionRunner(MODULE, true, ServiceLogger.TRANSACTION_TIMEOUT_DEFAULT, new TransactionItem() {
            @Override
            public void run() throws Exception {
                String userLoginId = (String)context.get(E.userLoginId.name());
                String newUserLoginId = (String)context.get(E.newUserLoginId.name());
                addLogInfo("Execute update from " + userLoginId + " to " + newUserLoginId);
                EntityCondition cond = EntityCondition.makeCondition(E.userLoginId.name(), userLoginId);
                GenericValue userLogin = findOne(E.UserLogin.name(), EntityCondition.makeCondition(E.userLoginId.name(), userLoginId), "", "No userLogin found with " + cond);
         		
                setRecordElaborated(getRecordElaborated() + 1);
                String partyId = userLogin.getString(E.partyId.name());
         		
                getListAndRemove(E.UserLoginValidPartyRole.name(), cond, userLoginId);                
                getListAndRemove(E.UserLoginSecurityGroup.name(), cond, userLoginId);
                
                EntityCondition condCreated = EntityCondition.makeCondition(E.createdByUserLogin.name(), userLoginId);
                EntityCondition condUpdated = EntityCondition.makeCondition(E.lastModifiedByUserLogin.name(), userLoginId);
                EntityCondition condW = EntityCondition.makeCondition(condCreated, EntityOperator.OR, condUpdated);
                
                List<String> nameValueChangeList = UtilMisc.toList(E.createdByUserLogin.name(), E.lastModifiedByUserLogin.name());
                getListAndUpdate(E.WorkEffort.name(), condW, userLoginId, nameValueChangeList, newUserLoginId);
                getListAndUpdate(E.WorkEffortMeasure.name(), condW, userLoginId, nameValueChangeList, newUserLoginId);
                getListAndUpdate(E.WorkEffortPartyAssignment.name(), condW, userLoginId, nameValueChangeList, newUserLoginId);
                getListAndUpdate(E.AcctgTrans.name(), condW, userLoginId, nameValueChangeList, newUserLoginId);
                getListAndUpdate(E.AcctgTransEntry.name(), condW, userLoginId, nameValueChangeList, newUserLoginId);
                
                getListAndUpdate(E.Party.name(), condW, userLoginId, nameValueChangeList, newUserLoginId);
                getListAndUpdate(E.PartyGroup.name(), condW, userLoginId, nameValueChangeList, newUserLoginId);
                getListAndUpdate(E.Person.name(), condW, userLoginId, nameValueChangeList, newUserLoginId);
                
                getListAndUpdate(E.PartyNameHistory.name(), condW, userLoginId, nameValueChangeList, newUserLoginId);
                
                GenericValue userLoginNew = findOne(E.UserLogin.name(), EntityCondition.makeCondition(E.userLoginId.name(), newUserLoginId), "", "");
        		EntityCondition conditionNote = EntityCondition.makeCondition(E.noteParty.name(), userLogin.getString(E.partyId.name()));
        		getListAndUpdate(E.NoteData.name(), conditionNote, userLogin.getString(E.partyId.name()), E.noteParty.name(), userLoginNew.getString(E.partyId.name()));
        		
                List<String> nameValueChangeStatusList = UtilMisc.toList(E.createdByUserLogin.name(), E.lastModifiedByUserLogin.name(), E.setByUserLogin.name());
                getListAndUpdate(E.WorkEffortStatus.name(), condW, userLoginId, nameValueChangeStatusList, newUserLoginId);
                
                userLogin.remove();
         		addLogInfo("remove userLogin userLoginId " + userLoginId);
         		
                addLogInfo("call partyPhysicalDelete partyId " + partyId);
                String serviceName = "partyPhysicalDelete";
                Map<String, Object> serviceContext = getDctx().makeValidContext(serviceName, ModelService.IN_PARAM, context);
                serviceContext.put(E.partyId.name(), partyId);
                serviceContext.put(E.jobLogger.name(), jobLogger);
                dispatcher.runSync(serviceName, serviceContext);
                
                
            }
        }).execute().rethrow();
    }
    
}
