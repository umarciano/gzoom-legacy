package com.mapsengineering.base.test;

import java.sql.Timestamp;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.delete.party.E;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.ImportManager;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.enumeration.PersonInterfaceFieldEnum;


public class TestPhysicalDelete extends BaseTestCase {

	
	public void testUserLoginPhysicalDelete() {
		try {
			createPartyAndUserLogin();
			context.put(E.userLoginId.name(), "TEST");
			context.put(E.newUserLoginId.name(), "user16");
			Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext(E.userLoginPhysicalDelete.name(), ModelService.IN_PARAM, context);
	        Map<String, Object> result = dispatcher.runSync(E.userLoginPhysicalDelete.name(), serviceParams);
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        } catch (Exception e) {
            assertNull("ERROR: " + e.getMessage(), e);
        }
        
    }
	
	
	
	public void testPartyPhysicalDelete() {
		try {
            context.put(E.partyId.name(), "10000");
            Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext(E.partyPhysicalDelete.name(), ModelService.IN_PARAM, context);
            Map<String, Object> result = dispatcher.runSync(E.partyPhysicalDelete.name(), serviceParams);
            assertEquals(ServiceUtil.returnError(E.responseMessage.name()).get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        } catch (Exception e) {
            assertNull("ERROR: " + e.getMessage(), e);
        }
        
    }
	
	public void testWorkEffortsPhysicalDelete() {
		try {
			Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext(E.workEffortsRootPhysicalDelete.name(), ModelService.IN_PARAM, context);
            serviceParams.put(E.workEffortTypeId.name(), "12BSC");
            Map<String, Object> result = dispatcher.runSync(E.workEffortsRootPhysicalDelete.name(), serviceParams);
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
        } catch (Exception e) {
            assertNull("ERROR: " + e.getMessage(), e);
        }
        
    }
	
	
	public void createPartyAndUserLogin() throws GeneralException {
		GenericValue ul = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
        Map<String, Object> context = FastMap.newInstance();
        context.put(ServiceLogger.USER_LOGIN, ul);
        context.put(ServiceLogger.LOCALE, Locale.ITALY);
        context.put(ServiceLogger.TIME_ZONE, TimeZone.getDefault());
        context.put("entityListToImport", ImportManagerConstants.PERSON_INTERFACE + ImportManagerConstants.SEP + ImportManagerConstants.PERS_RESP_INTERFACE);
        context.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, COMPANY);
        LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(delegator.getDelegatorName(), delegator);
        makePersonInterface(delegator);
        ImportManager.doImportSrv(dispatcher.getDispatchContext(), context);
	}
	
	private void makePersonInterface(Delegator delegator) throws GeneralException {
		Timestamp refDate = UtilDateTime.getDayStart(UtilDateTime.nowTimestamp());
		GenericValue gv = delegator.makeValue(ImportManagerConstants.PERSON_INTERFACE);
        gv.put(PersonInterfaceFieldEnum.dataSource.name(), "TEST");
        gv.put(PersonInterfaceFieldEnum.employmentOrgCode.name(), "TEST_GROUP");
        gv.put(PersonInterfaceFieldEnum.refDate.name(), refDate);
        gv.put(PersonInterfaceFieldEnum.personCode.name(), "TEST");
        gv.put(PersonInterfaceFieldEnum.fromDate.name(), refDate);
        gv.put(PersonInterfaceFieldEnum.emplPositionTypeId.name(), "ABCD");
        gv.put(PersonInterfaceFieldEnum.firstName.name(), "first name");
        gv.put(PersonInterfaceFieldEnum.lastName.name(), "last name");
        gv.put(PersonInterfaceFieldEnum.personRoleTypeId.name(), "EMPLOYEE");
        gv.put(PersonInterfaceFieldEnum.userLoginId.name(), "TEST");
        gv.put(PersonInterfaceFieldEnum.groupId.name(), "PARTYADMIN");
        gv.put(PersonInterfaceFieldEnum.contactMail.name(), "pippo@pluto.it");
        gv.put(PersonInterfaceFieldEnum.contactMobile.name(), "123456789");
        gv.create();
    }
}
