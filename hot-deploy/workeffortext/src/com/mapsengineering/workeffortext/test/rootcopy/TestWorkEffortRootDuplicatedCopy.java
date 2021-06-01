package com.mapsengineering.workeffortext.test.rootcopy;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootDuplicateService;

/**
 * Test WorkEffortRootCopy Duplicated Copy 
 */
public class TestWorkEffortRootDuplicatedCopy extends BaseTestCase {

    public static final String MODULE = TestWorkEffortRootDuplicatedCopy.class.getName();
    protected GenericValue ctp;
    
    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortIdRoot.name(), getWorkEffortId());
        ctp = delegator.findOne("WorkEffortTypeStatus", UtilMisc.toMap(E.workEffortTypeRootId.name(), "18PMA3OBSA", E.currentStatusId.name(), "WEPERFST_PLANINIT"), false);
        ctp.setString(E.params.name(), "duplicateAdmit=\"COPY\"");
        ctp.store();
    }

    protected Long getExpectedBlockingErrors() {
        return 0l;
    }

    protected Long getExpectedRecordElaborated() {
        return 1l;
    }
    
    /**
     * Test copy
     */
    public void testWorkEffortRootDuplicate() {
        Debug.log(getTitle());
        try {
            Map<String, Object> result = WorkEffortRootDuplicateService.workEffortRootDuplicateService(dispatcher.getDispatchContext(), context);
            Debug.log(" - duplicateAdmit " + result);
            assertTrue(ServiceUtil.isSuccess(result));
            assertEquals(getExpectedBlockingErrors(), result.get(ServiceLogger.BLOCKING_ERRORS));
            assertEquals(getExpectedRecordElaborated(), result.get(ServiceLogger.RECORD_ELABORATED));
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    protected String getTitle() {
        return "Test WorkEffort Root Duplicate with duplicateAdmit = COPY";
    }

    protected String getWorkEffortId() {
        return "W80010";
    }
}
