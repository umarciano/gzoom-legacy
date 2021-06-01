package com.mapsengineering.workeffortext.test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.MassiveWorkEffortRootCopyService;

/**
 * Test WorkEffortAssoc 
 *
 */
public class TestWorkEffortRootSnapshotAssoc extends BaseTestWorkEffortSnapshot {

    public static final String MODULE = TestWorkEffortRootSnapshotAssoc.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(E.workEffortTypeIdFrom.name(), "14O1IN");

        context.put(E.workEffortId.name(), getWorkEffortRootId());
        context.put(E.workEffortRevisionId.name(), getWorkEffortRevisionId());

        context.put(E.estimatedStartDateFrom.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2014, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateFrom.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));

        context.put(E.workEffortTypeIdTo.name(), "14O1IN");

        context.put(E.estimatedStartDateTo.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2014, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateTo.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2014, 0, 0, 0).getTime()));
    }

    /**
     * Test Snapshot of Indice PEG
     */
    public void testWorkEffortRootSnapshot() {
        Debug.log(getTitle());

        try {
            checkBeforeSnapshot(getWorkEffortId());

            Map<String, Object> result = MassiveWorkEffortRootCopyService.massiveWorkEffortRootCopyService(dispatcher.getDispatchContext(), context);
            Debug.log("Result : " + result);
            assertTrue(ServiceUtil.isSuccess(result));

            checkBeforeSnapshot(getWorkEffortId());

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    protected void checkBeforeSnapshot(String workEffortId) throws GeneralException {
        List<GenericValue> listaScheda = delegator.findList(E.WorkEffort.name(), EntityCondition.makeCondition(E.sourceReferenceId.name(), "PEG14"), null, null, null, false);
        Debug.log("List Root " + listaScheda);
        GenericValue workEffortScheda = EntityUtil.getFirst(listaScheda);
        String workEffortSchedaId = workEffortScheda.getString(E.workEffortId.name());

        List<GenericValue> listaObj = delegator.findList(E.WorkEffort.name(), EntityCondition.makeCondition(E.sourceReferenceId.name(), "PEG14-D025"), null, null, null, false);
        Debug.log("List Obj " + listaObj);
        // deve trovarne 2 perche' uno e' gia' stato storicizzato
        assertEquals(2, listaObj.size());
        
        GenericValue workEffortObj = EntityUtil.getFirst(listaObj);
        String workEffortObjId = workEffortObj.getString(E.workEffortId.name());

        // Ricerca elemento storico
        String snaphotObjId = getWorkEffortSnapshotId(workEffortObjId, getWorkEffortRevisionId());

        GenericValue workEffortAssocRoot = delegator.findOne(E.WorkEffortAssoc.name(), false, E.workEffortAssocTypeId.name(), E.ROOT.name(), E.workEffortIdFrom.name(), workEffortSchedaId, E.workEffortIdTo.name(), workEffortSchedaId, E.fromDate.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2014, 0, 0, 0).getTime()));
        assertNotNull(workEffortAssocRoot);

        GenericValue workEffortAssocRootObj = delegator.findOne(E.WorkEffortAssoc.name(), false, E.workEffortAssocTypeId.name(), E.ROOT.name(), E.workEffortIdFrom.name(), workEffortObjId, E.workEffortIdTo.name(), workEffortObjId, E.fromDate.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2014, 0, 0, 0).getTime()));
        assertNotNull(workEffortAssocRootObj);

        GenericValue workEffortAssocHie = delegator.findOne(E.WorkEffortAssoc.name(), false, E.workEffortAssocTypeId.name(), E.SNAPSHOT.name(), E.workEffortIdFrom.name(), workEffortObjId, E.workEffortIdTo.name(), snaphotObjId, E.fromDate.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2014, 0, 0, 0).getTime()));
        assertNotNull(workEffortAssocHie);

        EntityCondition condition = EntityCondition.makeCondition(E.workEffortIdFrom.name(), "E12032");
        List<GenericValue> allWorkEffortAssoc = delegator.findList(E.WorkEffortAssoc.name(), condition, null, null, null, false);
        Debug.log("For workEffortIdFrom = E12032 there are: " + allWorkEffortAssoc);
        // deve trovarne 2 perche' uno e' quello storicizzato
        assertEquals(2, allWorkEffortAssoc.size());

        EntityCondition conditionTo = EntityCondition.makeCondition(E.workEffortIdTo.name(), "E12033");
        allWorkEffortAssoc = delegator.findList(E.WorkEffortAssoc.name(), conditionTo, null, null, null, false);
        Debug.log("For workEffortIdFrom = E12033 there are:  " + allWorkEffortAssoc);
        // deve trovarne 2 perche' uno e' quello storicizzato
        assertEquals(2, allWorkEffortAssoc.size());

    }

    protected String getWorkEffortRevisionId() {
        return "PEG14.0";
    }

    protected String getWorkEffortRootId() {
        return "12200";
    }

    protected String getWorkEffortId() {
        return "12200";
    }

    protected String getTitle() {
        return "Snapshot Indice PEG12";
    }
}
