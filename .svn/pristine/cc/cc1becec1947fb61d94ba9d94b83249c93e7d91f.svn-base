package com.mapsengineering.base.test;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.reminder.E;
import com.mapsengineering.base.reminder.ReminderReportContentIdEnum;

public class TestReminder extends BaseTestCase {
    
    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.reportContentId.name(), "REMINDER_SCADENZA");
        context.put(E.workEffortTypeId.name(), "17TRA9SOL");
        
        context.put("security", dispatcher.getSecurity());
        context.put("dispatcher", dispatcher);
        context.put("enabledSendMail", "Y");
        context.put("outputFormat", "pdf");
        
        //TODO la creazione della mail non funziona manca qualche parametro di configurazione
    }
    
    /**
     * test ReminderReportContentIdEnum
     */
    public void testReminderReportContentIdEnum() {
        String permissionPrefixScadenza = ReminderReportContentIdEnum.getQuery("REMINDER_SCADENZA");
        
        assertEquals("sql/reminder/queryReminderScadObi.sql.ftl", permissionPrefixScadenza);
    }
    
    
    /**
     * test Reminder
     * 
     */
    public void testReminder() {
        try {
            Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext(E.managementPrintBirtSendEmail.name(), ModelService.IN_PARAM, context);
            serviceParams.put(E.queryReminder.name(), "sql/reminder/queryReminderScadObi.sql.ftl");
            Map<String, Object> result = dispatcher.runSync(E.managementPrintBirtSendEmail.name(), serviceParams);
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
            
            
            Thread.sleep(10000);
            
            createPartyContactMech();
            result = dispatcher.runSync(E.managementPrintBirtSendEmail.name(), serviceParams);
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
            
            Thread.sleep(10000);
            
        } catch (Exception e) {
            assertNull("ERROR: " + e.getMessage(), e);
            return;
        }
        
    }
    
    /**
     * <ContactMech contactMechId="10230" contactMechTypeId="EMAIL_ADDRESS" infoString="gzoom.gzoom@mapsgroup.it"/>
     * <PartyContactMech partyId="admin" contactMechId="10230" fromDate="2017-11-30 00:00:00"/>
     * 
     * @throws GenericServiceException 
     */
    private void  createPartyContactMech() throws GenericServiceException {
        
        Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext(E.createPartyContactMech.name(), "IN", context);
        serviceParams.put("contactMechTypeId", "EMAIL_ADDRESS");
        serviceParams.put("infoString", "gzoom.gzoom@mapsgroup.it");
        serviceParams.put(E.fromDate.name(), new Timestamp(UtilDateTime.toDate(30, 11, 2017, 0, 0, 0).getTime()));

        dispatcher.runSync(E.createPartyContactMech.name(), serviceParams);
    }

    /**
     * test Reminder Scadenza obiettivo schedulato
     */
    public void testReminderScadObiScheduled() {
        try {
            Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext("reminderScadObiScheduled", ModelService.IN_PARAM, context);
            Map<String, Object> result = dispatcher.runSync("reminderScadObiScheduled", serviceParams);
            
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
            
        } catch (Exception e) {
            assertNull("ERROR: " + e.getMessage(), e);
            return;
        }
        
    }
    
    /**
     * test Reminder Scadenza obiettivo schedulato
     */
    public void testReminderPeriodoScheduled() {
        try {
            Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext("reminderPeriodoScheduled", ModelService.IN_PARAM, context);
            Map<String, Object> result = dispatcher.runSync("reminderPeriodoScheduled", serviceParams);
            
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
            
        } catch (Exception e) {
            assertNull("ERROR: " + e.getMessage(), e);
            return;
        }
        
    }
    
    /**
     * test Reminder Print Error Query
     */
    public void testReminderPrintErrorQuery() {
        try {
            Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext(E.managementPrintBirtSendEmail.name(), ModelService.IN_PARAM, context);
            Map<String, Object> result = dispatcher.runSync(E.managementPrintBirtSendEmail.name(), serviceParams);
            
            assertEquals(ServiceUtil.returnSuccess().get(E.responseMessage.name()), result.get(E.responseMessage.name()));
            
        } catch (Exception e) {
            assertNull("ERROR: " + e.getMessage(), e);
            return;
        }
        
    }
    
}
