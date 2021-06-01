package com.mapsengineering.workeffortext.scorecard;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLoggedService;
import com.mapsengineering.base.util.JobLogger;

/**
 * Clean previous result
 *
 */
public class PreviuosResultCleaner implements JobLoggedService {

    public static final String MODULE = PreviuosResultCleaner.class.getName();

    private Delegator delegator;
    
    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public PreviuosResultCleaner(Delegator delegator) {
        this.delegator = delegator;
        jLogger = new JobLogger(MODULE);
    }
    
    /**
     * Pulizia dei risultati di eventuali calcoli precedenti
     * 
     * @param scoreCard
     * @param thruDate
     */
    public void cleanPreviousResult(String scoreCard, Date thruDate, String scoreValueType) {
        String sourceReferenceId = "";
    	try {
            GenericValue wrk = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), scoreCard), false);
            sourceReferenceId = wrk.getString(E.sourceReferenceId.name());
            String wrkDesc = scoreCard + ScoreCard.TRATT + wrk.getString(E.workEffortName.name());
            jLogger.addMessage(ServiceLogger.makeLogInfo("Clean previous result for workEffort \"" + wrkDesc + "\"", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

            Set<String> deleteAcctgTransSet = new HashSet<String>();
            
            // Cancello righe kpi
            int entryRemoved = 0;
            List<GenericValue> acctgTransEntryList = delegator.findByAnd("WorkEffortMeasureDeleteKpiRow", UtilMisc.toMap(E.workEffortParentId.name(), scoreCard, E.transactionDate.name(), thruDate, E.glFiscalTypeId.name(), scoreValueType));
            for (GenericValue item : acctgTransEntryList) {
                deleteAcctgTransSet.add(item.getString(E.acctgTransId.name()));
                delegator.removeByAnd(E.AcctgTransEntry.name(), UtilMisc.toMap(E.acctgTransEntrySeqId.name(), item.getString(E.acctgTransEntrySeqId.name()), E.acctgTransId.name(), item.getString(E.acctgTransId.name())), true);
                entryRemoved++;
            }

            // Cancello righe obiettivi
            acctgTransEntryList = delegator.findByAnd("WorkEffortMeasureDeleteAchieveRow", UtilMisc.toMap(E.workEffortParentId.name(), scoreCard, E.transactionDate.name(), thruDate, E.glFiscalTypeId.name(), scoreValueType));
            for (GenericValue item : acctgTransEntryList) {
                deleteAcctgTransSet.add(item.getString(E.acctgTransId.name()));
                delegator.removeByAnd(E.AcctgTransEntry.name(), UtilMisc.toMap(E.acctgTransEntrySeqId.name(), item.getString(E.acctgTransEntrySeqId.name()), E.acctgTransId.name(), item.getString(E.acctgTransId.name())), true);
                entryRemoved++;
            }

            // Cancello testate kpi
            List<EntityCondition> deleteAcctgTransConditonList = new FastList<EntityCondition>();
            deleteAcctgTransConditonList.add(EntityCondition.makeCondition("workEffortParentId", scoreCard));
            deleteAcctgTransConditonList.add(EntityCondition.makeCondition("transactionDate", thruDate));
            deleteAcctgTransConditonList.add(EntityCondition.makeCondition("glFiscalTypeId", scoreValueType));
            deleteAcctgTransConditonList.add(EntityCondition.makeCondition("acctgTransId", EntityOperator.IN, deleteAcctgTransSet));
            
            List<GenericValue> acctgTransList = delegator.findList("WorkEffortMeasureDeleteKpiHead", EntityCondition.makeCondition(deleteAcctgTransConditonList), null, null, null, false);
            for (GenericValue item : acctgTransList) {
                delegator.removeByAnd("AcctgTrans", UtilMisc.toMap(E.acctgTransId.name(), item.getString(E.acctgTransId.name())), true);
            }

            // Cancello testate obiettivi
            acctgTransList = delegator.findList("WorkEffortMeasureDeleteAchieveHead", EntityCondition.makeCondition(deleteAcctgTransConditonList), null, null, null, false);
            for (GenericValue item : acctgTransList) {
                delegator.removeByAnd("AcctgTrans", UtilMisc.toMap(E.acctgTransId.name(), item.getString(E.acctgTransId.name())), true);
            }

            jLogger.addMessage(ServiceLogger.makeLogInfo(String.format("Deleted %d result of previous elaborations.", entryRemoved), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

        } catch (Exception e) {
            // Segnalo errore non gestito nel log
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Error while deleting previous result: %s", e.getMessage()), "026", sourceReferenceId, null, null));
        }
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }
}
