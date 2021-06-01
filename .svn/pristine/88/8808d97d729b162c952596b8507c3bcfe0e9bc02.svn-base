package com.mapsengineering.workeffortext.test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GroovyUtil;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;
import com.mapsengineering.workeffortext.services.rootcopy.WorkEffortRootCopyService;

/**
 * Utility for WorkEffort Snapshot
 *
 */
public class BaseTestWorkEffortSnapshot extends BaseTestCase {

    public static final String MODULE = BaseTestWorkEffortSnapshot.class.getName();

    protected void setUp() throws Exception {
        super.setUp();
        context.put(WorkEffortRootCopyService.DELETE_OLD_ROOTS, E.N.name());
        context.put(WorkEffortRootCopyService.SNAPSHOT, E.Y.name());
        context.put(WorkEffortRootCopyService.GL_ACCOUNT_CREATION, E.Y.name());
        context.put(WorkEffortRootCopyService.CHECK_EXISTING, E.N.name());
        context.put(WorkEffortRootCopyService.ORGANIZATION_PARTY_ID, COMPANY);
        
        context.put(E.snapShotDescription.name(), "Automatica");
        
        context.put(E.workEffortTypeIdFrom.name(), "12BSC");

        context.put(E.workEffortId.name(), "W10000");
        context.put(E.workEffortRevisionId.name(), "R10001");

        context.put(E.estimatedStartDateFrom.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2012, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateFrom.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2012, 0, 0, 0).getTime()));

        context.put(E.workEffortTypeIdTo.name(), "12BSC");

        context.put(E.estimatedStartDateTo.name(), new Timestamp(UtilDateTime.toDate(1, 1, 2012, 0, 0, 0).getTime()));
        context.put(E.estimatedCompletionDateTo.name(), new Timestamp(UtilDateTime.toDate(12, 31, 2012, 0, 0, 0).getTime()));
    }
    
    /**
     * Search and return resourceTransactionList
     * @param workEffortId
     * @return
     * @throws GeneralException
     */
    @SuppressWarnings("unchecked")
    protected List<GenericValue> getResourceValues(String workEffortId) throws GeneralException {
        //Eseguo metodo
        Map<String, Object> groovyContext = new java.util.HashMap<String, Object>();
        Map<String, Object> parameters = new java.util.HashMap<String, Object>();
        parameters.put("workEffortId", workEffortId);

        groovyContext.put("parameters", parameters);
        groovyContext.put("locale", context.get(LOCALE));
        groovyContext.put("timeZone", context.get(TIME_ZONE));
        groovyContext.put("dispatcher", dispatcher);
        groovyContext.put("delegator", delegator);
        groovyContext.put("workEffortId", workEffortId);
        groovyContext.put(GenericService.DEFAULT_ORGANIZATION_PARTY_ID, COMPANY);
        
        //Attendo in output un parametro "listIt" - list di generic value
        GroovyUtil.runScriptAtLocation("component:/workeffortext/webapp/workeffortext/WEB-INF/actions/executeChildPerformFindWETResource.groovy", groovyContext);
        List<GenericValue> resourceTransactionList = (List<GenericValue>)groovyContext.get("listIt");

        Debug.log(" - resourceTransactionList " + resourceTransactionList);
        return resourceTransactionList;
    }

    /**
     * Search and return WorkEffortMeasure
     * @param workEffortId
     * @return
     * @throws GenericEntityException
     */
    protected List<GenericValue> getMeasure(String workEffortId) throws GenericEntityException {
        // Ricerca Misure
        List<EntityCondition> conditionMeasure = FastList.newInstance();
        conditionMeasure.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));

        List<GenericValue> measureList = delegator.findList("WorkEffortMeasure", EntityCondition.makeCondition(conditionMeasure), null, null, null, false);
        Debug.log(" - Found " + measureList.size() + " WorkEffortMeasure with condition " + EntityCondition.makeCondition(conditionMeasure));
        return measureList;
    }

    /**
     * Search and return workEffort, with workEffortSnapshotId = originalWorkEffortId and workEffortRevisionId = workEffortRevisionId
     * @return workEffortId of Snapshot
     * @throws GenericEntityException 
     */
    protected String getWorkEffortSnapshotId(String originalWorkEffortId, String workEffortRevisionId) throws GenericEntityException {
        List<GenericValue> workeffortList = delegator.findList("WorkEffort", EntityCondition.makeCondition(EntityCondition.makeCondition(E.workEffortSnapshotId.name(), originalWorkEffortId), EntityCondition.makeCondition(E.workEffortRevisionId.name(), workEffortRevisionId)), null, null, null, false);
        GenericValue workEffort = EntityUtil.getFirst(workeffortList);
        if (UtilValidate.isNotEmpty(workEffort)) {
            return workEffort.getString(E.workEffortId.name());
        }
        return null;
    }

    protected void checkBeforeSnapshot(String workEffortId, int expectedMeasure, int expectedTransaction) throws GeneralException {
        List<GenericValue> allGlAccount = delegator.findList("GlAccount", null, null, null, null, false);
        Debug.log(" - checkBeforeSnapshot Found " + allGlAccount.size() + " GlAccount");
        
        List<GenericValue> allMeasure = delegator.findList("WorkEffortMeasure", null, null, null, null, false);
        Debug.log(" - checkBeforeSnapshot Found " + allMeasure.size() + " WorkEffortMeasure");
        
        List<GenericValue> allAcctgTrans = delegator.findList("AcctgTrans", null, null, null, null, false);
        Debug.log(" - checkBeforeSnapshot Found " + allAcctgTrans.size() + " AcctgTrans");
        
        // Ricerca Misure per originale
        List<GenericValue> originalMeasure = getMeasure(workEffortId);
        assertEquals(expectedMeasure, originalMeasure.size());
        // Ricerca valori Risorse per originale
        List<GenericValue> originalResourceValues = getResourceValues(workEffortId);
        assertEquals(expectedTransaction, originalResourceValues.size());
    }

    protected void checkAfterSnapshot(String workEffortId, String workEffortRevisionId, int expectedMeasure, int expectedTransaction) throws GeneralException {
        List<GenericValue> allGlAccount = delegator.findList("GlAccount", null, null, null, null, false);
        Debug.log(" - checkAfterSnapshot Found " + allGlAccount.size() + " GlAccount");
        
        List<GenericValue> allMeasure = delegator.findList("WorkEffortMeasure", null, null, null, null, false);
        Debug.log(" - checkAfterSnapshot Found " + allMeasure.size() + " WorkEffortMeasure");
        
        List<GenericValue> allAcctgTrans = delegator.findList("AcctgTrans", null, null, null, null, false);
        Debug.log(" - checkAfterSnapshot Found " + allAcctgTrans.size() + " AcctgTrans");
        
        // Ricerca elemento storico
        String snaphotId = getWorkEffortSnapshotId(workEffortId, workEffortRevisionId);
        // Ricerca Misure per storico
        List<GenericValue> snapshotMeasure = getMeasure(snaphotId);
        assertEquals(expectedMeasure, snapshotMeasure.size());
        // Ricerca valori Risorse per storico
        List<GenericValue> snapshotResourceValues = getResourceValues(snaphotId);
        assertEquals(expectedTransaction, snapshotResourceValues.size());

        // Ricerca Misure per originale dopo storico
        List<GenericValue> originalMeasureAfterSnapshot = getMeasure(workEffortId);
        assertEquals(expectedMeasure, originalMeasureAfterSnapshot.size());
        // Ricerca valori Risorse per originale, in modo che non siano aumentati
        List<GenericValue> originalResourceValuesAfterSnapshot = getResourceValues(workEffortId);
        assertEquals(expectedTransaction, originalResourceValuesAfterSnapshot.size());
    }
}
