package com.mapsengineering.base.delete.userlogin;

import java.util.Map;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.mapsengineering.base.services.GenericServiceLoop;

/**
 * Delete a workEffort root with its child
 * @author asma
 *
 */
public class UserLoginOnlyPhysicalDelete extends GenericServiceLoop {

    public static final String MODULE = UserLoginOnlyPhysicalDelete.class.getName();
    public static final String SERVICE_NAME = "ULOnlyPhysicalDelete";
    public static final String SERVICE_TYPE = "UL_ONLY_DELETE";

    /**
     * Constructor
     * @param dctx
     * @param context
     */
    public UserLoginOnlyPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, SERVICE_NAME, SERVICE_TYPE, MODULE);
    }
    
    /**
     * Delete workeffort root with child
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> userLoginOnlyPhysicalDelete(DispatchContext dctx, Map<String, Object> context) {
    	UserLoginOnlyPhysicalDelete obj = new UserLoginOnlyPhysicalDelete(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Find and delete workeffort with same workEffortParentId
     * @throws Exception
     */
    protected void execute() throws Exception {    	 
    	String userLoginId = (String)context.get(E.userLoginId.name());
        addLogInfo("add service remove userLoginId " + userLoginId);
         
        GenericValue userLogin = findOne( E.UserLogin.name(), EntityCondition.makeCondition(E.userLoginId.name(), userLoginId), "", "");
 		String partyId = userLogin.getString(E.partyId.name());

 		EntityCondition condUserLogin = EntityCondition.makeCondition(E.userLoginId.name(), userLoginId);
 		getListAndRemove(E.UserLoginValidPartyRole.name(), condUserLogin, userLoginId);                
        getListAndRemove(E.UserLoginSecurityGroup.name(), condUserLogin, userLoginId);
 		userLogin.remove();
 		addLogInfo("remove userLogin userLoginId " + userLoginId);
 		
 		addLogInfo("call partyPhysicalDelete partyId " + partyId);
        String serviceName = "partyPhysicalDelete";
        Map<String, Object> serviceContext = getDctx().makeValidContext(serviceName, ModelService.IN_PARAM, context);
        serviceContext.put(E.partyId.name(), partyId);
        serviceContext.put(E.jobLogger.name(), jobLogger);
        dispatcher.runSync(serviceName, serviceContext);
        
    }
    
}
