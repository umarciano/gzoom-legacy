package com.mapsengineering.workeffortext.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * Integration Test 
 * In Snapshot 'Valori Risorse' dei capitoli di spesa e 'Valori Indicatori'
 *
 */

public class TestWorkEffortRootSnapshotWithResourceValues extends TestWorkEffortRootSnapshotWithoutResourceValues {
    
    public static final String MODULE = TestWorkEffortRootSnapshotWithResourceValues.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortTypeIdFrom.name(), "12BSC");
        context.put(E.workEffortTypeIdTo.name(), "12BSC");
    }
    
    /**
     * WorkEffort with 1 measure and 1 transaction, without resource
     */
    public void testWorkEffortRootSnapshot() {
        Debug.log(getTitle());
        Debug.log("" + context.get(GenericService.DEFAULT_ORGANIZATION_PARTY_ID));
        try {
            checkBeforeSnapshot(getWorkEffortId(), getExpectedMeasure(), getExpectedResourceTransaction());

            Map<String, Object> result = WorkEffortRootCopyService.workEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - result " + result);

            checkAfterSnapshot(getWorkEffortId(), getWorkEffortRevisionId(), getExpectedMeasure(), getExpectedResourceTransaction());

            Map<String, Object> resultSecondSnapshot = WorkEffortRootCopyService.workEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log(" - resultSecondSnapshot " + resultSecondSnapshot);

            checkAfterSnapshot(getWorkEffortId(), getWorkEffortRevisionId(), getExpectedMeasure(), getExpectedResourceTransaction());

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
    
    protected String getTitle() {
        return "Snapshot 12BSC With 1 Resource Values";
    }
    
    protected int getExpectedMeasure() {
        return 20;
    }

    protected int getExpectedResourceTransaction() {
        return 2; // TODO aggiungere filtr odata nella get di test
    }

    protected String getWorkEffortRevisionId() {
        return "R10001";
    }

    protected String getWorkEffortId() {
        return "W10017";
    }
    
    protected String getWorkEffortRootId() {
        return "W10000";
    }
}
