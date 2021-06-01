package com.mapsengineering.workeffortext.scorecard;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
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
public class AssociatedAchievesReader implements JobLoggedService {

    public static final String MODULE = AssociatedAchievesReader.class.getName();

    private Delegator delegator;
    
    private JobLogger jLogger;
    
    /**
     * Constructor
     * @param delegator
     */
    public AssociatedAchievesReader(Delegator delegator) {
        this.delegator = delegator;
        jLogger = new JobLogger(MODULE);
    }
    
    /**
     * Lettura obiettivi collegati
     * 
     * @throws GenericEntityException
     */
    public AssociatedAchievesResult readAssociatedAchieves(String scoreCard, Date refDate, String performance, String scoreValueType, String workEffortAssocTypeId, String rootHolder) throws GenericEntityException {

        GenericValue root = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), scoreCard), false);
        String wrkDesc = scoreCard + ScoreCard.TRATT + root.getString(E.workEffortName.name());
        String sourceReferenceId = root.getString(E.sourceReferenceId.name());

        EntityCondition condList = EntityCondition.makeCondition(EntityJoinOperator.AND, EntityCondition.makeCondition(E.workEffortIdFrom.name(), scoreCard), EntityCondition.makeCondition(E.workEffortAssocTypeId.name(), EntityOperator.EQUALS, workEffortAssocTypeId),
                // Bug 3852 punto 3
                EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, refDate),
                // Bug 4445
                EntityCondition.makeCondition(EntityJoinOperator.OR, EntityCondition.makeCondition(E.movTransactionDate.name(), EntityOperator.EQUALS, refDate), EntityCondition.makeCondition(E.movTransactionDate.name(), EntityOperator.EQUALS, null)),
                EntityCondition.makeCondition("movGlFiscalTypeId", EntityOperator.EQUALS, scoreValueType),
                EntityCondition.makeCondition("parentTo", EntityOperator.NOT_EQUAL, scoreCard),
                EntityCondition.makeCondition(E.actStEnumId.name(), EntityOperator.NOT_EQUAL, E.ACTSTATUS_REPLACED.name()));

        List<GenericValue> valuesList = delegator.findList("WorkEffortAchieveConnected", condList, null, null, null, false);
        jLogger.addMessage(ServiceLogger.makeLogInfo("Found " + valuesList.size() + " related (associated) objectives with calculated performance to \"" + wrkDesc + "\"", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
        // for debug jLogger.addMessage(ServiceLogger.makeLogInfo("Found " + valuesList, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

        // Calcoli di punteggio
        AssociatedAchievesResult res = new AssociatedAchievesResult();
        Set<String> noScoreSet = new HashSet<String>();
        Set<String> hasScoreSet = new HashSet<String>();
        for (GenericValue item : valuesList) {
            if (item.get(E.amount.name()) != null) {
                jLogger.addMessage(ServiceLogger.makeLogInfo("Found " + item.getString("workEffortId") + " with " + item.getDouble(E.amount.name()) + " * " + item.getDouble(E.assocWeight.name()), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
                res.incrementWeightedAverage(item.getDouble(E.amount.name()) * item.getDouble(E.assocWeight.name()));
                res.incrementScoreCount(1d);
                String workEffortId = item.getString(E.workEffortId.name());
                if (UtilValidate.isEmpty(workEffortId) || !new Timestamp(refDate.getTime()).equals(item.getTimestamp(E.movTransactionDate.name())) || !performance.equals(item.getString("movGlFiscalTypeId"))) {
                    noScoreSet.add(item.getString("workEffortIdTo"));
                } else {
                    // questi sono quelli con valore
                    hasScoreSet.add(item.getString("workEffortIdTo"));
                }
            }
            res.incrementWeightSum(item.getDouble(E.assocWeight.name()));
            if ("Y".equalsIgnoreCase(item.getString(E.hasScoreAlert.name()))) {
                res.incrementAlertCount(1d);
            }
        }

        noScoreSet.removeAll(hasScoreSet);
        List<String> childWithoutScore = res.getChildWithoutScore();
        childWithoutScore.addAll(noScoreSet);
        res.setChildWithoutScore(childWithoutScore);

        jLogger.addMessage(ServiceLogger.makeLogInfo(String.format("In reading related objectives have been found %.0f alert", res.getAlertCount()), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
        jLogger.addMessage(ServiceLogger.makeLogInfo(String.format("In reading related objectives have been found " + res.getWeightedAverage() + " with " + res.getWeightSum() + " for " + res.getScoreCount()), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

        return res;
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }
}
