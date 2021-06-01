package com.mapsengineering.emplperf.test;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.emplperf.update.ParamsEnum;

/**
 * Test Massive creation of employment performance,
 * UORG_1 = "14" with partyId = "RCP10022"; 
 *
 */
public class TestEmplPerfInsertFromTemplate extends BaseTestInsertFromTemplate {
    public static final String MODULE = TestEmplPerfInsertFromTemplate.class.getName();
    
    private static final String SERVICE_NAME_ALL = "executeEmplPerfUpdateFromPreviousAsync";
            
    protected void setUp() throws Exception {
        super.setUp();
        setContextInsertFromTemplate(TestPartyUpdateWorkEffortWithInsertFromTemplate.UORG_1, TestPartyUpdateWorkEffortWithInsertFromTemplate.ROLE_TYPE_ID_UOSET, TestPartyUpdateWorkEffortWithInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, new Timestamp(UtilDateTime.toDate(1, 1, 2013, 0, 0, 0).getTime()), new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()));
    }

    /**
     * Test for employment performance
     */
    public void testEmplPerfInsertFromTemplate() {
        try {
            Map<String, Object> serviceParams = dispatcher.getDispatchContext().makeValidContext("emplPerfInsertFromTemplate", ModelService.IN_PARAM, context);
            Debug.log("testEmplPerfInsertFromTemplate serviceParams " + serviceParams);
            Map<String, Object> result = dispatcher.runSync("emplPerfInsertFromTemplate", serviceParams);
            Debug.log("testEmplPerfInsertFromTemplate result " + result);
            assertTrue(ServiceUtil.isSuccess(result));
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }
    
    
    /**
     * Test for employment performance error flag
     */
    public void testEmplPerfException() {
        Debug.log(" - testEmplPerfException ");
        // 1 record beacuse creates a record
        try {
            setContextAndRunInsertFromTemplate(BaseTestInsertFromTemplate.UORG_1, BaseTestInsertFromTemplate.ROLE_TYPE_ID_UOSET, BaseTestInsertFromTemplate.EMPL_POSITION_TYPE_ID_D, BaseTestInsertFromTemplate.START_DATE_2014, BaseTestInsertFromTemplate.COMPLETION_DATE_2014, 0, 1);
            setContextUpdateFromPrevious("MVD-12", BaseTestInsertFromTemplate.TEMPLATE_ID_FOR_D, "PROGETTI", "VC12C", new Timestamp(UtilDateTime.toDate(12, 31, 2013, 0, 0, 0).getTime()), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
            Map<String, Object> serviceMap = dispatcher.getDispatchContext().makeValidContext(SERVICE_NAME_ALL, "IN", context);
            serviceMap.put(ParamsEnum.updateAcctgTransAndEntry.name(), "N");
            serviceMap.put(ParamsEnum.updateWorkEffortNote.name(), "N");
            serviceMap.put(ParamsEnum.updateWorkEffortAssoc.name(), "N");           
            Debug.log(" - testEmplPerfException serviceMap " + serviceMap);
            Map<String, Object> result = dispatcher.runSync(SERVICE_NAME_ALL, serviceMap);
            Debug.log(" - testEmplPerfException " + SERVICE_NAME_ALL + " = " + result);
            assertFalse(ServiceUtil.isSuccess(result));
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }
}
