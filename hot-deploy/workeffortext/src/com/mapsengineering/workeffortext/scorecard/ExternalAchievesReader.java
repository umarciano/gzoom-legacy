package com.mapsengineering.workeffortext.scorecard;

import java.util.Date;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLoggedService;
import com.mapsengineering.base.util.JobLogger;


/**
 * Find workEffort with workEffortAssocTypeId but different parentTo
 *
 */
@Deprecated
public class ExternalAchievesReader implements JobLoggedService {

    public static final String MODULE = ExternalAchievesReader.class.getName();

    private Delegator delegator;
    
    private JobLogger jLogger;
    
    /**
     * Costructor
     * @param delegator
     */
    public ExternalAchievesReader(Delegator delegator) {
        this.delegator = delegator;
        this.jLogger = new JobLogger(MODULE);
    }
    
    /**
     * Lettura obiettiovi esterni
     * 
     * @throws GenericEntityException
     */
    public ExternalAchievesResult readExternalAchieves(String scoreCard, Date refDate, String performance, String workEffortAssocTypeId, String rootHolder) throws GenericEntityException {

        GenericValue root = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), scoreCard), false);
        String wrkDesc = scoreCard + ScoreCard.TRATT + root.getString(E.workEffortName.name());
        String sourceReferenceId = root.getString(E.sourceReferenceId.name());
        
        // Uso la stessa query degli obbiettivi collegati, cambia solo assocTypeId
        EntityCondition condList = EntityCondition.makeCondition(EntityJoinOperator.AND, EntityCondition.makeCondition(E.workEffortIdFrom.name(), scoreCard), EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), EntityOperator.EQUALS, workEffortAssocTypeId),
                // Bug 3852 3
                EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, refDate),
                EntityCondition.makeCondition(E.assocWeight.name(), EntityOperator.NOT_EQUAL, -1),
                
                EntityCondition.makeCondition(E.movTransactionDate.name(), EntityOperator.EQUALS, refDate), EntityCondition.makeCondition("movGlFiscalTypeId", EntityOperator.EQUALS, performance),
                EntityCondition.makeCondition("parentTo", EntityOperator.NOT_EQUAL, rootHolder), EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.NOT_EQUAL, null));

        // Uso la stessa query degli obbiettivi collegati, cambia solo assocTypeId e il parentTo
        List<GenericValue> valuesList = delegator.findList("WorkEffortAchieveConnected", condList, null, null, null, false);
        jLogger.addMessage(ServiceLogger.makeLogInfo("Found " + valuesList.size() + " external workEffort for workEffort \"" + wrkDesc + "\"", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

        // Uso la stessa query dei collegati, pero essendo internamente una left
        // outer join,
        // nel ciclo scarto quello che non ha workeffortId
        ExternalAchievesResult res = new ExternalAchievesResult();
        for (GenericValue item : valuesList) {
            if (item.get(E.amount.name()) != null) {
                jLogger.addMessage(ServiceLogger.makeLogInfo("Found " + item.getString("workEffortId") + " with " + item.getDouble(E.amount.name()) + " * " + item.getDouble(E.assocWeight.name()), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
                
                res.incrementWeightedAverage(item.getDouble(E.amount.name()) * item.getDouble(E.assocWeight.name()));
                res.incrementScoreCount(1d);
            }
            res.incrementWeightSum(item.getDouble(E.assocWeight.name()));
            if ("Y".equalsIgnoreCase(item.getString(E.hasScoreAlert.name()))) {
                res.incrementAlertCount(1d);
            }
        }

        jLogger.addMessage(ServiceLogger.makeLogInfo(String.format("Reading external workEfforts finished with %.0f alerts", res.getAlertCount()), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
        jLogger.addMessage(ServiceLogger.makeLogInfo(String.format("Reading external workEfforts finished with " + res.getWeightedAverage() + " with " + res.getWeightSum() + " for " + res.getScoreCount()), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

        return res;
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }
}
