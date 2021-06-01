package com.mapsengineering.workeffortext.scorecard;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;

import com.mapsengineering.base.find.WorkEffortFindServices;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;
import com.mapsengineering.workeffortext.scorecard.helper.UomRangeValuesHelper;

/**
 * Calcolo Punteggio
 *
 */
@SuppressWarnings("unchecked")
public class ScoreCard {

    /**
     * Risultati elaborazione
     */
    public static class ElaborationResults {
        private long recordElaborated = 0;
        private long errorMessages = 0;
        private long warnMessages = 0;
        
        /**
         * @return the recordElaborated
         */
        public long getRecordElaborated() {
            return recordElaborated;
        }
        
        /**
         * @param recordElaborated the recordElaborated to set
         */
        public void setRecordElaborated(long recordElaborated) {
            this.recordElaborated = recordElaborated;
        }
        
        /**
         * @return the errorMessages
         */
        public long getErrorMessages() {
            return errorMessages;
        }
        
        /**
         * @param errorMessages the errorMessages to set
         */
        public void setErrorMessages(long errorMessages) {
            this.errorMessages = errorMessages;
        }
        
        /**
         * @return the warnMessages
         */
        public long getWarnMessages() {
            return warnMessages;
        }
        
        /**
         * @param warnMessages the warnMessages to set
         */
        public void setWarnMessages(long warnMessages) {
            this.warnMessages = warnMessages;
        }
    }

    /**
     * Max numero figli
     */
    public static final String MODULE = ScoreCard.class.getName();

    public static final Integer MAX_CHILD = 2000;
    public static final int HUNDRED = 100;
    public static final double HUNDREDD = 100d;
    protected static final String PERF_AMOUNT_CALC = "PERF_AMOUNT_CALC";
    private static final String SCORE = "SCORE";
    protected static final String KPI_VALUE = "KPI_VALUE";
    public static final String FOUND_SCOREKPI_VALUE = "FOUND_SCOREKPI_VALUE";
    private static final String SUM = "SUM";
    protected static final String TRATT = " - ";

    public static final double ZEROD = 0d;

    private Delegator delegator;
    private GenericDispatcher dispatcher;
    private GenericValue userLogin;
    private long recordElaborated = 0;
    private String rootHierarchyAssocTypeId = null;
    private String rootHolder;
    private boolean isRoot;
    private String periodTypeId;
    private String scorePeriodEnumId;
    private JobLogger jLogger;

    private GenericValue customTimePeriod;
    
    /**
     * Constructor
     * 
     * @param delegator
     */
    public ScoreCard(GenericDispatcher dispatcher, GenericValue userLogin) {
        this.dispatcher = dispatcher;
        this.delegator = dispatcher.getDelegator();
        this.userLogin = userLogin;
        this.jLogger = new JobLogger(MODULE);
    }

    /**
     * Cerce e restituisce l'ultimo messaggio inserito in coda
     * 
     * @return
     */
    private MessageCode getLastMessage() {

        if (jLogger.getMessages().size() == 0) {
            return MessageCode.INFO_GENERIC;
        }
        Map<String, Object> lastMessage = jLogger.getMessages().get(jLogger.getMessages().size() - 1);
        String msg = (String)lastMessage.get("logCode");
        if (msg == null) {
            return MessageCode.INFO_GENERIC;
        }
        return MessageCode.valueOf(msg);
    }

    /**
     * Scrive il nuovo valore kpi
     * 
     * @param kpi
     * @param kpiValue
     * @throws GenericServiceException
     */
    private void storeKpi(Map<String, Object> kpi, Map<String, Double> kpiMap, Date transDate, String scoreValueType, String sourceReferenceId) throws GenericServiceException {

        Double kpiValue = kpiMap.get(ScoreCard.KPI_VALUE);
        String workEffortMeasureId = (String)kpi.get(E.workEffortMeasureId.name());

        GenericValue meas = null;
        String measDesc = "";
        String accountCode = "";
        try {
            meas = delegator.findOne(E.WorkEffortMeasure.name(), UtilMisc.toMap(E.workEffortMeasureId.name(), workEffortMeasureId), false);
            GenericValue glAcc = delegator.getRelatedOneCache(E.GlAccount.name(), meas);
            measDesc = glAcc.getString(E.glAccountId.name()) + TRATT + glAcc.getString("accountName");
            accountCode = glAcc.getString(E.accountCode.name());
        } catch (Exception e) {
            Debug.log(e);
        }

        // "Scrivo kpi per misura \"" + measDesc + "\" valore %.2f", kpiValue),
        jLogger.addMessage(ServiceLogger.makeLogInfo("Write kpi for workEffort measure \"" + measDesc + "\" value = " + kpiValue, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));

        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.workEffortMeasureId.name(), workEffortMeasureId);
        serviceMap.put(E.glAccountId.name(), kpi.get(E.glAccountId.name()));
        serviceMap.put("transValue", kpiValue);
        serviceMap.put("transDate", transDate);
        serviceMap.put("acctgTransTypeId", "SCOREKPI");
        serviceMap.put(E.glFiscalTypeId.name(), scoreValueType);
        serviceMap.put(E.perfAmountCalc.name(), kpiMap.get(ScoreCard.PERF_AMOUNT_CALC));
        serviceMap.put("isPosted", "N");
        serviceMap.put("userLogin", this.userLogin);
        if (UtilValidate.isNotEmpty(kpi.get(E.hasScoreAlert.name()))) {
            serviceMap.put(E.hasScoreAlert.name(), kpi.get(E.hasScoreAlert.name()));
        }

        // run inside this transaction
        Map<String, Object> serviceResult = dispatcher.runSync("createWeTrans_fixedGlAccount", serviceMap);

        if (ServiceUtil.isError(serviceResult)) {
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Write service kpi returns with error: %s", ServiceUtil.getErrorMessage(serviceResult)), "020", sourceReferenceId, accountCode, null));
            return;
        }

        String acctgTransId = (String)serviceResult.get(E.acctgTransId.name());
        String acctgTransEntrySeqId = (String)serviceResult.get(E.acctgTransEntrySeqId.name());
        try {

            GenericValue acctgTransEntry = delegator.findOne(E.AcctgTransEntry.name(), UtilMisc.toMap(E.acctgTransId.name(), acctgTransId, E.acctgTransEntrySeqId.name(), acctgTransEntrySeqId), false);
            Map<String, Object> fields = acctgTransEntry.getAllFields();
            fields.put(E.perfAmountTarget.name(), kpi.get(E.targetValue.name()));
            fields.put(E.perfAmountActual.name(), kpi.get(E.actualValue.name()));
            fields.put(E.perfAmountMin.name(), kpi.get(E.limitMinValue.name()));
            fields.put(E.perfAmountMax.name(), kpi.get(E.limitMaxValue.name()));
            GenericValue newValue = delegator.makeValue(E.AcctgTransEntry.name());
            newValue.putAll(fields);
            delegator.store(newValue);

        } catch (GenericEntityException e) {
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Write service kpi returns with error: %s", e.getMessage()), "020", sourceReferenceId, accountCode, null));
            return;
        }

        jLogger.addMessage(ServiceLogger.makeLogInfo("Finish write service kpi without errors.", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));
    }

    /**
     * Scrive il valore punteggio
     * 
     * @param scoreCard
     * @param scoreValue
     * @throws GenericEntityException
     * @throws GenericServiceException
     * @throws ServiceValidationException
     */
    private double storeScore(GenericValue workEffort, double score, Date thruDate, String scoreValueType, boolean hasScoreAlert, String periodTypeId, String weightType, String organizationId) throws GenericEntityException, ServiceValidationException, GenericServiceException {

        String workEffortId = workEffort.getString(E.workEffortId.name());
        String sourceReferenceId = workEffort.getString(E.sourceReferenceId.name());
        String wrkDesc =  workEffortId + TRATT + workEffort.getString(E.workEffortName.name());;
        

        jLogger.addMessage(ServiceLogger.makeLogInfo("Write kpi for workeffort \"" + wrkDesc + "\", value " + score, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

        // Subito valuto se esiste la misura, se no la creo
        EntityConditionList<EntityExpr> condList = EntityConditionList.makeCondition(EntityJoinOperator.AND, EntityCondition.makeCondition(E.workEffortId.name(), EntityOperator.EQUALS, workEffortId), EntityCondition.makeCondition("weMeasureTypeEnumId", EntityOperator.EQUALS, "WEMT_SCORE"));

        List<GenericValue> li = delegator.findList(E.WorkEffortMeasure.name(), condList, null, null, null, false);
        String workEffortMeasureId = null;
        String accountCode = null;
        
        if (li.size() < 1) {

            jLogger.addMessage(ServiceLogger.makeLogInfo("WorkEffort measure does not exist, create...", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

            String uomDescr = "";
            GenericValue glAccount = delegator.findOne(E.GlAccount.name(), UtilMisc.toMap(E.glAccountId.name(), SCORE), false);
            accountCode = glAccount.getString(E.accountCode.name());
            if (UtilValidate.isNotEmpty(glAccount) && UtilValidate.isNotEmpty(glAccount.get("defaultUomId"))) {
                uomDescr = delegator.findOne("Uom", UtilMisc.toMap("uomId", glAccount.getString("defaultUomId")), false).getString(E.description.name());
            }

            // Nota: non esiste un servizio che crei la measure
            GenericValue workEffortMeasure = delegator.makeValue(E.WorkEffortMeasure.name());
            workEffortMeasureId = delegator.getNextSeqId(E.WorkEffortMeasure.name());
            workEffortMeasure.put(E.workEffortId.name(), workEffortId);
            workEffortMeasure.put(E.workEffortMeasureId.name(), workEffortMeasureId);
            workEffortMeasure.put("weMeasureTypeEnumId", "WEMT_SCORE");
            workEffortMeasure.put(E.weScoreRangeEnumId.name(), "WESCORE_ISVALUE");
            workEffortMeasure.put(E.weScoreConvEnumId.name(), "WECONVER_NOCONVERSIO");
            workEffortMeasure.put(E.weOtherGoalEnumId.name(), "WEMOMG_WEFF");
            workEffortMeasure.put(E.glAccountId.name(), SCORE);
            workEffortMeasure.put("uomDescr", uomDescr);
            workEffortMeasure.put("periodTypeId", periodTypeId);
            workEffortMeasure.put(E.fromDate.name(), workEffort.getTimestamp("estimatedStartDate"));
            workEffortMeasure.put(E.thruDate.name(), workEffort.getTimestamp("estimatedCompletionDate"));

            delegator.create(workEffortMeasure);
        } else {

            workEffortMeasureId = li.get(0).getString(E.workEffortMeasureId.name());
            String wemDesc = "";
            accountCode = "";
            try {
                GenericValue glAcc = delegator.findOne(E.GlAccount.name(), UtilMisc.toMap(E.glAccountId.name(), SCORE), false);
                wemDesc = SCORE + TRATT + glAcc.getString("accountName");
                accountCode = glAcc.getString("accountCode");
            } catch (Exception e) {
                Debug.log(e);
            }
            jLogger.addMessage(ServiceLogger.makeLogInfo("WorkEffort measure updated \"" + wemDesc + "\"", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));
        }

        // Call servizio creazione punteggio
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put(E.workEffortMeasureId.name(), workEffortMeasureId);
        serviceMap.put("transDate", thruDate);
        String scoreAlertFlag = hasScoreAlert ? "Y" : "N";
        serviceMap.put(E.hasScoreAlert.name(), scoreAlertFlag);
        serviceMap.put(E.glFiscalTypeId.name(), scoreValueType);
        serviceMap.put("userLogin", this.userLogin);
        serviceMap.put("isPosted", "N");
        serviceMap.put("defaultOrganizationPartyId", organizationId);

        serviceMap.putAll(getValueMap(score, workEffortId, sourceReferenceId, accountCode, weightType));
        GenericValue wem = null;
        String wemDesc = "";
        String accountCodeMeasure = "";
        try {
            wem = delegator.findOne(E.WorkEffortMeasure.name(), UtilMisc.toMap(E.workEffortMeasureId.name(), workEffortMeasureId), false);
            GenericValue glAcc = delegator.getRelatedOneCache(E.GlAccount.name(), wem);
            wemDesc = workEffortMeasureId + TRATT + glAcc.getString("accountName");
            accountCodeMeasure = glAcc.getString(E.accountCode.name());
        } catch (Exception e) {
            Debug.log(e);
        }

        // run inside this transaction
        Map<String, Object> serviceResult = dispatcher.runSync("createWeTrans", serviceMap);
        if (ServiceUtil.isError(serviceResult)) {
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Write service for workEffort measure \"" + wemDesc + "\" returns error below: %s", ServiceUtil.getErrorMessage(serviceResult)), "021", sourceReferenceId, accountCodeMeasure, null));
        } else {
            jLogger.addMessage(ServiceLogger.makeLogInfo("Finish write service for workEffort measure \"" + wemDesc + "\" without errors", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCodeMeasure, null));
        }
        
        return (Double) serviceMap.get(E.transValue.name());
    }

    /**
     * Tutti i messaggi registrati durante l'elaborazione
     * 
     * @return
     */
    public List<Map<String, Object>> getMessages() {
        return jLogger.getMessages();
    }

    /**
     * Risultati numerici dell'elaborazione
     * 
     * @return
     */
    public ElaborationResults getResults() {

        ElaborationResults er = new ElaborationResults();
        er.setRecordElaborated(this.recordElaborated);
        for (Map<String, Object> item : jLogger.getMessages()) {
            String logType = (String)item.get("logType");
            if (ServiceLogger.LOG_TYPE_ERROR.equals(logType)) {
                er.setErrorMessages(er.getErrorMessages() + 1);
            } else {
                if (ServiceLogger.LOG_TYPE_WARN.equals(logType)) {
                    er.setWarnMessages(er.getWarnMessages() + 1);
                }
            }
        }
        return er;
    }

    /**
     * Estrae hierarchyAssocTypeId della root
     * 
     * @param workEffort
     */
    private void findHierarchyAssocTypeId(GenericValue workEffort) {

        try {

            GenericValue workEffortType = delegator.findOne(E.WorkEffortType.name(), UtilMisc.toMap(E.workEffortTypeId.name(), workEffort.getString(E.workEffortTypeId.name())), false);
            this.rootHierarchyAssocTypeId = workEffortType.getString("hierarchyAssocTypeId");

            if(UtilValidate.isEmpty(rootHierarchyAssocTypeId)){
                jLogger.addMessage(ServiceLogger.makeLogInfo("No Hierarchy Assoc Type for \"" + workEffort.getString(E.workEffortTypeId.name()) + "\"", MessageCode.INFO_GENERIC.toString(), workEffort.getString(E.sourceReferenceId.name()), null, null));
            }else {
                printLogWorkEffortAssocTypeId(workEffort.getString(E.sourceReferenceId.name()));
            }
        } catch (GenericEntityException e) {
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Error find hierarchy assoc type for workEffort: %s", e.getMessage()), "022", workEffort.getString(E.sourceReferenceId.name()), null, null));
        }
    }
    
    /**
     * Stampa messaggio per WorkEffortAssocType
     * 
     * @param workEffort
     */
    private void printLogWorkEffortAssocTypeId(String sourceReferenceId) {
        try {
            GenericValue wassType = delegator.findOne("WorkEffortAssocType", UtilMisc.toMap(E.workEffortAssocTypeId.name(), this.rootHierarchyAssocTypeId), false);
            if(UtilValidate.isEmpty(wassType)){
                jLogger.addMessage(ServiceLogger.makeLogInfo("No WorkEffort assoc found", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
            } else {
                jLogger.addMessage(ServiceLogger.makeLogInfo("WorkEffort assoc type \"" + wassType.getString(E.description.name()) + "\"", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
            }
        } catch (GenericEntityException e) {
            jLogger.addMessage(ServiceLogger.makeLogError(String.format("Error find workEffort assoc type for workEffort: %s", e.getMessage()), "022", sourceReferenceId, null, null));
        }
    }

    /**
     * Calculate and return score
     * @param scoreCard
     * @param thruDate
     * @param limitExcellent
     * @param limitMax
     * @param target
     * @param limitMin
     * @param performance
     * @param scoreValueType
     * @param weightType
     * @return
     */
    public double calculate(String scoreCard, Date thruDate, String limitExcellent, String limitMax, String target, String limitMin, String performance, String scoreValueType, String weightType) {
        Map<String, Object> result = this.calculate(scoreCard, thruDate, limitExcellent, limitMax, target, limitMin, performance, scoreValueType, weightType, false);
        return (Double)result.get(E.score.name());
    }

    /**
     * Algoritmo di calcolo schede obbiettivi
     * 
     * @param scoreCard
     * @param thruDate
     * @param limitExcellent
     * @param limitMax
     * @param target
     * @param limitMin
     * @param performance
     * @param scoreValueType
     * @param weightType
     * @param hasScoreAlert
     * @return
     */
    public Map<String, Object> calculate(String scoreCard, Date thruDate, String limitExcellent, String limitMax, String target, String limitMin, String performance, String scoreValueType, String weightType, boolean hasScoreAlert) {
        double score = 0d;
        boolean alertFlag = hasScoreAlert;
        boolean somethingToWrite = false;
        Map<String, Object> toReturnMap = new FastMap<String, Object>();
        boolean hasAlreadyScore = false;
        Double alreadyScore = 0d;
        String sourceReferenceId = "";
        try {
            GenericValue scheda = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), scoreCard), false);
            String wrkDesc = scoreCard + TRATT + scheda.getString(E.workEffortName.name());
            sourceReferenceId = scheda.getString(E.sourceReferenceId.name());
            jLogger.addMessage(ServiceLogger.makeLogInfo("Start elaboration for workEffort \"" + wrkDesc + "\"", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

            // Carico la scheda corrente
            GenericValue workEffort = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), scoreCard), false);
            
            WorkEffortFindServices workEffortFindServices = new WorkEffortFindServices(delegator, dispatcher);
            String organizationId = workEffortFindServices.getOrganizationId(userLogin, false);

            // Memorizzo la root di calcolo
            if (rootHolder == null) {
                rootHolder = scoreCard;
                isRoot = true;
                GenericValue workEffortType = delegator.findOne(E.WorkEffortType.name(), UtilMisc.toMap(E.workEffortTypeId.name(), workEffort.getString(E.workEffortTypeId.name())), false);
                periodTypeId = UtilValidate.isNotEmpty(workEffortType) ? workEffortType.getString("periodTypeId") : "";
                scorePeriodEnumId = UtilValidate.isNotEmpty(workEffortType) ? workEffortType.getString("scorePeriodEnumId") : "";
                customTimePeriod = getPeriod(scoreCard);
                if (UtilValidate.isEmpty(customTimePeriod) || UtilValidate.isEmpty(customTimePeriod.getString(E.customTimePeriodId.name()))) {
                    jLogger.addMessage(ServiceLogger.makeLogWarn("No period found for " + sourceReferenceId, "028", sourceReferenceId, null, null));
                }
            } else {
                isRoot = false;
            }

            if (workEffort == null) {
                jLogger.addMessage(ServiceLogger.makeLogError(String.format("WorkEffort %s not found in application, score = 0", scoreCard), "023", sourceReferenceId, null, null));
                toReturnMap.put(E.score.name(), 0d);
                return toReturnMap;
            }

            // Al primo ingresso nella funzione (e' ricorsiva) estraggo
            // workEffortAssocType della root
            if (rootHierarchyAssocTypeId == null) {
                findHierarchyAssocTypeId(workEffort);
            }

            // Inc record elaborated
            //jLogger.addMessage(ServiceLogger.makeLogInfo("Calculate score for workEffort  \"" + wrkDesc + "\"", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
            recordElaborated++;

            // Controllo se obbiettivo esterno senza punteggio (solo se non sono
            // nella root) -> errore bloccante
            if (!isRoot && !rootHolder.equalsIgnoreCase(workEffort.getString(E.workEffortParentId.name()))) {
                jLogger.addMessage(ServiceLogger.makeLogError("External workEffort without score \"" + wrkDesc + "\"", "002", sourceReferenceId, null, null));
                toReturnMap.put(E.hasScoreAlert.name(), false);
                return toReturnMap;
            }

            //Bug 29 Vedo se l'obiettivo ha gia un suo SCORE, lo score va cercato per scoreValueType, cioe il valore con cui andrebbe scritto
            List<EntityExpr> scoreCondList = UtilMisc.toList(EntityCondition.makeCondition("glFiscalTypeId", scoreValueType), EntityCondition.makeCondition("workEffortId", scoreCard), EntityCondition.makeCondition("transactionDate", thruDate), EntityCondition.makeCondition("amountLocked", "Y"), EntityCondition.makeCondition("organizationPartyId", organizationId));
            List<GenericValue> wScore = delegator.findList("WorkEffortMeasureScore", EntityCondition.makeCondition(scoreCondList), null, null, null, false);
            if (UtilValidate.isNotEmpty(wScore)) {
                hasAlreadyScore = true;
                GenericValue s = EntityUtil.getFirst(wScore);
                alreadyScore = s.getDouble("amount");
                jLogger.addMessage(ServiceLogger.makeLogInfo("Found SCORE for workeffort \"" + wrkDesc + "\" value " + alreadyScore, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
            }

            // Inizializzo valori
            double kpiScore = 0d;
            double reviewScore = 0d;
            double assocWorkEffortScore = 0d;
            double childScore = 0d;
            double childAlreadyCalculatedScore = 0d;
            double childWithoutCalculatedScore = 0d;

            // Lettura pesi
            Double weightKpi = (workEffort.getDouble("weightKpi") != null) ? workEffort.getDouble("weightKpi") : 0d;
            Double weightReview = (workEffort.getDouble("weightReview") != null) ? workEffort.getDouble("weightReview") : 0d;
            Double weightAssocWorkEffort = (workEffort.getDouble("weightAssocWorkEffort") != null) ? workEffort.getDouble("weightAssocWorkEffort") : 0d;
            Double weightSons = (workEffort.getDouble("weightSons") != null) ? workEffort.getDouble("weightSons") : 0d;
            
            double weightTotalSons = 0d;

            // Cerco eventuali figli che hanno gia un punteggio
            // Vedi bug 3763
            // Bug 3915
            if (weightSons != 0d && !hasAlreadyScore) {
//                double weightSumAlreadyCalculatedSons = 0d;
                ChildWithScoreFinder childWithScoreFinder = new ChildWithScoreFinder(delegator, organizationId);
                List<Map<String, Object>> childWithScoreList = childWithScoreFinder.findChildWithScore(scoreCard, thruDate, scoreValueType, this.rootHierarchyAssocTypeId);

                int childWithScoreNumber = childWithScoreList.size();
                jLogger.addMessage(ServiceLogger.makeLogInfo(String.format("Found %d childs with calculated score.", childWithScoreNumber), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

                for (Map<String, Object> childCalculated : childWithScoreList) {
                    double calcValue = (Double)childCalculated.get("minAmount");
                    double weight = (Double)childCalculated.get(E.assocWeight.name());
                    childAlreadyCalculatedScore += calcValue * weight;
                    weightTotalSons += weight;

                    GenericValue child = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), childCalculated.get(E.workEffortId.name())), false);
                    String childDesc = child.getString(E.sourceReferenceId.name()) + TRATT + child.getString(E.workEffortName.name());
                    jLogger.addMessage(ServiceLogger.makeLogInfo("Returned score from workEffort \"" + childDesc + "\" = " + calcValue * weight + " with weight = " + weight, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
                }
            }

            // Calcolo punteggio figli
            // Estrazione child di questa scheda
            if (!hasAlreadyScore) {
                ChildWithoutScoreFinder childWithoutScoreFinder = new ChildWithoutScoreFinder(delegator, organizationId); 
                List<GenericValue> childList = childWithoutScoreFinder.findChildWithoutScore(scoreCard, thruDate, scoreValueType, this.rootHierarchyAssocTypeId);

                int childsNumber = childList.size();
                jLogger.addMessage(ServiceLogger.makeLogInfo(String.format("Found %d childs to calculate score", childsNumber), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

                for (GenericValue child : childList) {
                	
                	GenericValue schedaChild = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), child.getString(E.workEffortId.name())), false);
                    String wrkDescChild = child.getString(E.workEffortId.name()) + TRATT + schedaChild.getString(E.workEffortName.name());
                    
                    Map<String, Object> calcMap = calculate(child.getString(E.workEffortId.name()), thruDate, limitExcellent, limitMax, target, limitMin, performance, scoreValueType, weightType, false);
                    alertFlag = alertFlag || (Boolean)calcMap.get(E.hasScoreAlert.name());
                    if (weightSons == 0d || UtilValidate.isEmpty(calcMap.get(E.score.name()))) {
                        continue;
                    }
                    if ((Boolean)calcMap.get(E.somethingToWrite.name())) {
                        double calcValue = (Double)calcMap.get(E.score.name());

                        double weight = child.getDouble(E.assocWeight.name());
                        childWithoutCalculatedScore += calcValue * weight;
                        weightTotalSons += weight;
                        jLogger.addMessage(ServiceLogger.makeLogInfo("Calculated score from workEffort \"" + wrkDescChild + "\" = " + calcValue * weight + " with weight = " + weight, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
                    }
                }
            }

            
            childScore = (childAlreadyCalculatedScore + childWithoutCalculatedScore);
            jLogger.addMessage(ServiceLogger.makeLogInfo("Total Sum of child's score = "+ childScore + " = " + childWithoutCalculatedScore + " (already calculated) + " + childAlreadyCalculatedScore, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
                    
            if (!SUM.equals(workEffort.getString("totalEnumIdSons"))) {
                if (weightTotalSons != 0) {
                    childScore = childScore / weightTotalSons;
                }
            }
            
            if (weightTotalSons != 0) {
                somethingToWrite = true;
            }
            
            // Calcolo KPI
            if (weightKpi != 0d && !hasAlreadyScore) {

                // Prima di eseguire la lettura inserisco la data che mi serve
                // da campo confronto nella qry
                Timestamp lastCorrectScoreDate = workEffort.getTimestamp(E.lastCorrectScoreDate.name());

                workEffort.put(E.lastCorrectScoreDate.name(), thruDate);
                // importante pulire la cache per le query successive
                delegator.store(workEffort, true);

                // Lettura KPI
                KpiReader kpiReader = new KpiReader(delegator, customTimePeriod, periodTypeId, organizationId);
                List<Map<String, Object>> kpiList = kpiReader.readKpi(scoreCard, thruDate, limitExcellent, limitMax, target, limitMin, performance, scoreValueType, sourceReferenceId, weightType, scorePeriodEnumId);
                jLogger.mergeData(kpiReader.getJobLogger());
                
                GenericValue wrk = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), scoreCard), false);
                wrkDesc = scoreCard + TRATT + wrk.getString(E.workEffortName.name());
                

                workEffort.put(E.lastCorrectScoreDate.name(), lastCorrectScoreDate);
                // importante pulire la cache
                delegator.store(workEffort, true);

                //
                // Calcolo punteggio totale kpi
                double kpiScoreWeightSum = 0d;
                double weightedKpi = 0d;
                for (Map<String, Object> kpi : kpiList) {
                    //Bug 29 riutilizzo SCOREKPI
                    if (UtilValidate.isNotEmpty(kpi.get(ScoreCard.FOUND_SCOREKPI_VALUE))) {
                        double kpiValue = (Double)kpi.get(ScoreCard.FOUND_SCOREKPI_VALUE);
                        double kpiScoreWeight = (Double)kpi.get(E.kpiScoreWeight.name());
                        
                        weightedKpi = kpiValue * kpiScoreWeight;
                        kpiScoreWeightSum += (Double)kpi.get(E.kpiScoreWeight.name());
                        
                        // se workEffortMeasureId != null invocare storeKpi
                        if(UtilValidate.isNotEmpty(kpi.get(E.workEffortMeasureId.name()))) {
                            Map<String, Double> kpiMap = new FastMap<String, Double>();
                            kpiMap.put(ScoreCard.KPI_VALUE, kpiValue);
                            kpiMap.put(ScoreCard.PERF_AMOUNT_CALC, (Double)kpi.get(ScoreCard.PERF_AMOUNT_CALC)); // TODO
                            storeKpi(kpi, kpiMap, thruDate, scoreValueType, sourceReferenceId);
                        }
                    } else {
                        // Conversione

                        KpiCalculator kpiCalculator = new KpiCalculator(delegator);
                        Map<String, Double> kpiMap = kpiCalculator.calculateKpiValue(kpi);
                        jLogger.mergeData(kpiCalculator.getJobLogger());

                        double kpiValue = kpiMap.get(ScoreCard.KPI_VALUE);

                        double kpiScoreWeight = (Double)kpi.get(E.kpiScoreWeight.name());
                        kpiScoreWeightSum += kpiScoreWeight;

                        weightedKpi = kpiValue * kpiScoreWeight;

                        // Scrivo kpi
                        // Bug 3207
                        storeKpi(kpi, kpiMap, thruDate, scoreValueType, sourceReferenceId);
                    }

                    // Check messaggi, se l'ultimo e un errore bloccante
                    switch (getLastMessage()) {
                    case ERROR_BLOCKING: {
                        toReturnMap.put(E.score.name(), score);
                        return toReturnMap;
                    }
                    case ERROR_NEXTCARD:
                        // Vado alla prossima scheda
                        continue;
                    }

                    kpiScore += weightedKpi;
                    alertFlag = alertFlag || "Y".equals(kpi.get(E.hasScoreAlert.name()));
                }

                if (kpiScoreWeightSum != 0) {
                    somethingToWrite = true;
                    if (!SUM.equals(workEffort.getString("totalEnumIdKpi"))) {
                        kpiScore = kpiScore / kpiScoreWeightSum;
                    }
                }

                kpiScore = weightKpi * kpiScore;
                jLogger.addMessage(ServiceLogger.makeLogInfo("For workEffort \"" + wrkDesc + "\" kpiScore = " + kpiScore + " with weightKpi = " + weightKpi, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
            }

            //
            // Calcolo REVIEW
            if (weightReview != 0d && !hasAlreadyScore) {
                // TODO: Calcolo review, attualmente non gestito (cosi in analisi)
                reviewScore = weightReview * reviewScore;
            }

            // Calcolo childScore
            // nel calcolo dello score childScore deve essere lo scroe dei child moltiplicato per il peso weightSons
            if (weightSons != 0d && !hasAlreadyScore) {
                // se sto calcolando il punteggio finale della Scheda devo rimoltiplicare * il peso...
                childScore = weightSons * childScore;
            }

            //
            // Calcolo Obiettivi collegati
            if (weightAssocWorkEffort != 0d && !hasAlreadyScore) {
                String workEffortAssocTypeId = workEffort.getString(E.workEffortAssocTypeId.name());
                AssociatedAchievesReader associatedAchievesReader = new AssociatedAchievesReader(delegator);
                AssociatedAchievesResult aar = associatedAchievesReader.readAssociatedAchieves(scoreCard, thruDate, performance, scoreValueType, workEffortAssocTypeId, rootHolder);
                jLogger.mergeData(associatedAchievesReader.getJobLogger());

                // Esistono collegati senza punteggio?
                if (aar.getChildWithoutScore().size() > 0) {
                    GenericValue wrk = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), aar.getChildWithoutScore().get(0)), false);
                    wrkDesc = wrk.getString(E.workEffortId.name()) + TRATT + wrk.getString(E.workEffortName.name());
                    jLogger.addMessage(ServiceLogger.makeLogError("WorkEffort connected \"" + wrkDesc + "\" without score", "003", sourceReferenceId, null, null));
                    if (!isRoot) {
                        if (!rootHolder.equalsIgnoreCase(workEffort.getString(E.workEffortParentId.name()))) {
                            toReturnMap.put(E.hasScoreAlert.name(), false);
                            return toReturnMap;
                        }
                    }
                }

                if (SUM.equals(workEffort.getString("totalEnumIdAssoc"))) {
                    assocWorkEffortScore = aar.getWeightedAverage();
                } else {
                    if (aar.getWeightSum() != 0) {
                        assocWorkEffortScore = aar.getWeightedAverage() / aar.getWeightSum();
                    }
                }

                assocWorkEffortScore = assocWorkEffortScore * weightAssocWorkEffort;
                
                if (aar.getAlertCount() > 0) {
                    alertFlag = true;
                }

                if (aar.getWeightSum() != 0) {
                    somethingToWrite = true;
                }
            }

            //
            // Calcolo finale
            //
            if (!hasAlreadyScore) {
            	jLogger.addMessage(ServiceLogger.makeLogInfo("Score calculated is the sum of childScore (" + childScore + ") + kpiScore (" + kpiScore + ") + reviewScore (" + reviewScore + ") + assocWorkEffortScore (" + assocWorkEffortScore + ")", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
                jLogger.addMessage(ServiceLogger.makeLogInfo("divided by 100.0", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
            	
            	score = (childScore + kpiScore + reviewScore + assocWorkEffortScore) / 100.0;
                
                // Scrivo punteggio di questo obbiettivo
                if (somethingToWrite) {
                    score = storeScore(workEffort, score, thruDate, scoreValueType, alertFlag, periodTypeId, weightType, organizationId);
                }

                // Aggiorno data ultimo calcolo se non fatto prima
                // Bug 3083
                if (scoreCard.equals(rootHolder)) {
                    delegator.refresh(workEffort);
                    workEffort.put(E.lastCorrectScoreDate.name(), thruDate);
                    delegator.store(workEffort, false);
                }
            } else {
                score = alreadyScore;
            }
            jLogger.addMessage(ServiceLogger.makeLogInfo("For workEffort \"" + wrkDesc + "\" score = " + score, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));
        	
        } catch (Exception e) {           
            @SuppressWarnings("rawtypes")
            Map error = ServiceLogger.makeLogError(String.format("Error not manage %s", e.getMessage()), "024", sourceReferenceId, null, null);
            jLogger.addMessage(error);
        }
        toReturnMap.put(E.score.name(), score);
        toReturnMap.put(E.hasScoreAlert.name(), alertFlag);
        toReturnMap.put(E.somethingToWrite.name(), somethingToWrite);
        return toReturnMap;
    }

    /**
     * Return rootHierarchyAssocTypeId
     * @return
     */
    public String getRootHierarchyAssocTypeId() {
        return this.rootHierarchyAssocTypeId;
    }
    
    private Map<String, Object> getValueMap(double amount, String workEffortId, String sourceReferenceId, String accountCode, String weightType) throws GenericEntityException {
        Map<String, Object> serviceMap = FastMap.newInstance();
        double amountCalc = amount;
        
        if (! E.ALT.name().equals(weightType)) {
        	UomRangeValuesHelper uomRangeValuesHelper = new UomRangeValuesHelper(delegator);
        	Double uomRangeValue = uomRangeValuesHelper.checkApplyScoreRange(amount, workEffortId, sourceReferenceId, accountCode);
        	if (UtilValidate.isNotEmpty(uomRangeValue)) {
        		amountCalc = uomRangeValue.doubleValue();
        	}        
        	jLogger.mergeData(uomRangeValuesHelper.getJobLogger());
        }
        serviceMap.put(E.perfAmountCalc.name(), amount);
        serviceMap.put(E.transValue.name(), amountCalc);
        
        return serviceMap;
    }
    
    /**
     * Return customTimePeriod for Root
     * @param workEffortRootId
     * @return
     * @throws GenericEntityException
     */
    private GenericValue getPeriod(String workEffortRootId) throws GenericEntityException {
        List<GenericValue> periodList = delegator.findList(E.WorkEffortAndTypePeriodAndThruDate.name(), EntityCondition.makeCondition(E.workEffortId.name(), workEffortRootId), null, null, null, false);
        return EntityUtil.getFirst(periodList);
    }
}
