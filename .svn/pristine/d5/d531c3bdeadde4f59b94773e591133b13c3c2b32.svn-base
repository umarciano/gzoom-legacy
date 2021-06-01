package com.mapsengineering.emplperf.test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.base.util.MessageUtil;
import com.mapsengineering.workeffortext.services.E;

/**
 * Test Massive creation of employment performance  
 *
 */
public class TestMassivePrintAndSendMail extends BaseTestInsertFromTemplate {
    public static final String MODULE = TestMassivePrintAndSendMail.class.getName();
    private static final Timestamp MONITORING_DATE = new Timestamp(UtilDateTime.toDate(6, 30, 2014, 0, 0, 0).getTime());
    
    protected void setUp() throws Exception {
        super.setUp();
        Map<String, Object> parameters = FastMap.newInstance();
        context.put("reportContentId", "WE_PRINT_SCHEDA_IND");
        context.put("entityNamePrefix", "WorkEffortRoot");
        context.put("entityName", "WorkEffortRootView");
        context.put("security", dispatcher.getSecurity());
        context.put("parameters", parameters);
        context.put("dispatcher", dispatcher);
        context.put("subject", "subject");
        context.put("content", "content");
        context.put("enabledSendMail", "Y");
        context.put("monitoringDate", MONITORING_DATE);
        context.put("outputFormat", "pdf");
    }

    /**
     * Test for employment performance
     */
    public void testMassivePrintAndSendMail() {
        try {
            List<GenericValue> workEffortRootList = delegator.findList("WorkEffort", EntityCondition.makeCondition(E.workEffortId.name(), "W50000"), null, null, null, false);
            workEffortRootList.add(EntityUtil.getFirst(getWorkEffortEmplPerfForEvaluated(PERSONA, MONITORING_DATE, null)));
            context.put("workEffortRootList", workEffortRootList);
            Map<String, Object> localContext = dispatcher.getDispatchContext().makeValidContext("asyncJobMassiveWorkEffortPrintAndSendMail", "IN", context);
            Map<String, Object> result = dispatcher.runSync("asyncJobMassiveWorkEffortPrintAndSendMail", localContext);
            Debug.log(" - result " + result);    
        } catch (Exception e) {
            Debug.logError(e, MessageUtil.getExceptionMessage(e), MODULE);
            assertTrue(false);
        }
    }
}
