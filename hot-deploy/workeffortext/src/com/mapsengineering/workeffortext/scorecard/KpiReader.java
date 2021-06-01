package com.mapsengineering.workeffortext.scorecard;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLoggedService;
import com.mapsengineering.base.util.JobLogger;


/**
 * Read kpi Value, complex
 *
 */
public class KpiReader implements JobLoggedService {

    public static final String MODULE = KpiReader.class.getName();

    private Delegator delegator;

    private JobLogger jLogger;

    private String scoreField;
    private GenericValue customTimePeriod;
    private String periodTypeId;
    private String organizationId;

    /**
     * Constructor
     * @param delegator
     */
    public KpiReader(Delegator delegator, GenericValue customTimePeriod, String periodTypeId, String organizationId) {
        this.delegator = delegator;
        this.customTimePeriod = customTimePeriod;
        this.periodTypeId = periodTypeId;
        this.organizationId = organizationId;
        this.jLogger = new JobLogger(MODULE);
    }

    /**
     * Questo metodo gestisce la qry di lettura che e molto 'complicata'. Non
     * potendo gestire tutte le condizioni direttamente in qry perche ci sono
     * dei parametri ho cercato di inserire tutte le outer join indipendenti da
     * parametri nella view, mentre le clausole where globali della query e
     * quelle della outer join le gestisco qui in lettura
     * @param scoreCard
     * @param thruDate
     * @param limitExcellent
     * @param limitMax
     * @param target
     * @param limitMin
     * @param performance
     * @param scoreValueType
     * @param sourceReferenceId
     * @param weightType
     * @param scorePeriodEnumId
     * @return
     * @throws GenericEntityException
     */
    public List<Map<String, Object>> readKpi(String scoreCard, Date thruDate, String limitExcellent, String limitMax, String target, String limitMin, String performance, String scoreValueType, String sourceReferenceId, String weightType, String scorePeriodEnumId) throws GenericEntityException {
        scoreField = E.ALT.name().equals(weightType) ? E.kpiOtherWeight.name() : E.kpiScoreWeight.name();

        //estrazione misure
        WorkEffortMeasureKpiExtractor workEffortMeasureKpiExtractor = new WorkEffortMeasureKpiExtractor(delegator, scoreCard, performance, thruDate, weightType, scorePeriodEnumId, scoreField);
        List<GenericValue> workEffortMeasureKpiList = workEffortMeasureKpiExtractor.getWorkEffortMeasureKpiList();
        
        List<String> workEffortMeasureIdList = EntityUtil.getFieldListFromEntityList(workEffortMeasureKpiList, E.workEffortMeasureId.name(), false);
        jLogger.addMessage(ServiceLogger.makeLogInfo("Found " + workEffortMeasureKpiList.size() + " " + workEffortMeasureKpiExtractor.getWorkEffortMeasureKpiEntityName() + " with id = " + workEffortMeasureIdList, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, null, null));

        // Recupero le date MAX_ACTUAL e MAX_BUDGET, prima left join
        //GN-319 TODO per ora prendo movimenti con workEffortSnapshotId non vuoto     
        EntityCondition whereConditionActual = getWhereConditionList(performance, workEffortMeasureIdList, thruDate);
        EntityCondition whereConditionLimitExcellent = getWhereConditionList(limitExcellent, workEffortMeasureIdList, thruDate);
        EntityCondition whereConditionLimitMax = getWhereConditionList(limitMax, workEffortMeasureIdList, thruDate);
        EntityCondition whereConditionBudget = getWhereConditionList(target, workEffortMeasureIdList, thruDate);
        EntityCondition whereConditionLimitMin = getWhereConditionList(limitMin, workEffortMeasureIdList, thruDate);
        // 4482
        //Bug 16
        EntityCondition whereConditionActualPy = getWhereConditionList("ACTUAL_PY", workEffortMeasureIdList, thruDate);

        EntityListIterator actualListIt = delegator.find(E.WorkEffortTransactionIndicatorView.name(), whereConditionActual, null, null, UtilMisc.toList("-weTransDate"), null);
        EntityListIterator limitExcellentListIt = delegator.find(E.WorkEffortTransactionIndicatorView.name(), whereConditionLimitExcellent, null, null, UtilMisc.toList("-weTransDate"), null);
        EntityListIterator limitMaxListIt = delegator.find(E.WorkEffortTransactionIndicatorView.name(), whereConditionLimitMax, null, null, UtilMisc.toList("-weTransDate"), null);
        EntityListIterator budgetListIt = delegator.find(E.WorkEffortTransactionIndicatorView.name(), whereConditionBudget, null, null, UtilMisc.toList("-weTransDate"), null);
        EntityListIterator limitMinListIt = delegator.find(E.WorkEffortTransactionIndicatorView.name(), whereConditionLimitMin, null, null, UtilMisc.toList("-weTransDate"), null);
        EntityListIterator actualPyListIt = delegator.find(E.WorkEffortTransactionIndicatorView.name(), whereConditionActualPy, null, null, UtilMisc.toList("-weTransDate"), null);

        List<GenericValue> actualList = actualListIt.getCompleteList();
        List<GenericValue> limitExcellentList = limitExcellentListIt.getCompleteList();
        List<GenericValue> limitMaxList = limitMaxListIt.getCompleteList();
        List<GenericValue> budgetList = budgetListIt.getCompleteList();
        List<GenericValue> limitMinList = limitMinListIt.getCompleteList();
        List<GenericValue> actualPyList = actualPyListIt.getCompleteList();

        actualListIt.close();
        limitExcellentListIt.close();
        limitMaxListIt.close();
        budgetListIt.close();
        limitMinListIt.close();
        actualPyListIt.close();

        // Contiene scoreKPI gia esistenti, alla data di lancio oppure al periodo precedente (se il glAccount ha WEWITHTARG_PRV_SCORE)
        List<Map<String, Object>> entityList = FastList.newInstance();

        // Contiene i movimenti estratti dalla query, che poi verranno utilizzati per il calcolo
        List<Map<String, Object>> valuesList = new FastList<Map<String, Object>>();

        Iterator<GenericValue> it = workEffortMeasureKpiList.iterator();
        while (it.hasNext()) {
            GenericValue measKpi = it.next();
            String workEffortMeasureId = measKpi.getString(E.workEffortMeasureId.name());
            String accountCode = measKpi.getString(E.accountCode.name());

            WorkEffortMeasureScoreKpiExtractor workEffortMeasureScoreKpiExtractor = new WorkEffortMeasureScoreKpiExtractor(delegator, scoreCard, scoreValueType, thruDate, weightType, scorePeriodEnumId);
            workEffortMeasureScoreKpiExtractor.setWorkEffortMeasureScoreKpiConditions(workEffortMeasureId, false);
            jLogger.addMessage(ServiceLogger.makeLogDebug("Search " + workEffortMeasureScoreKpiExtractor.getWorkEffortMeasureScoreKpiEntityName() + " with condition " + EntityCondition.makeCondition(workEffortMeasureScoreKpiExtractor.getWorkEffortMeasureScoreKpiConditions()), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));
            List<GenericValue> scoreKpiList = workEffortMeasureScoreKpiExtractor.getWorkEffortMeasureScoreKpiList();

            //NOTA: Bug 29 Se trovo uno SCOREKPI utilizzo quello!!
            if (UtilValidate.isNotEmpty(scoreKpiList)) {
                GenericValue scoreKpi = EntityUtil.getFirst(scoreKpiList);
                Double value = scoreKpi.getDouble("amount");
                jLogger.addMessage(ServiceLogger.makeLogInfo("Found a SCOREKPI with value " + value, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, scoreKpi.getString("mAccountCode"), null));

                Map<String, Object> map = new FastMap<String, Object>();
                map.put(ScoreCard.FOUND_SCOREKPI_VALUE, value);
                map.put(E.kpiScoreWeight.name(), scoreKpi.getDouble(scoreField));
                entityList.add(map);
            } else {
                String workEffortId = measKpi.getString(E.workEffortId.name());
                GenericValue wrk = delegator.findOne(E.WorkEffort.name(), UtilMisc.toMap(E.workEffortId.name(), workEffortId), false);
                String sourceReferenceIdMeasure = wrk.getString(E.sourceReferenceId.name());
            	
            	GenericValue workEffortMeasure = delegator.findOne(E.WorkEffortMeasure.name(), UtilMisc.toMap(E.workEffortMeasureId.name(), workEffortMeasureId), false);
            	if (UtilValidate.isNotEmpty(workEffortMeasure)) {
            		if (ConversionRule.WECONVER_WRKCAP.name().equals(workEffortMeasure.getString(E.weScoreConvEnumId.name())) || ConversionRule.WECONVER_GAPCAP.name().equals(workEffortMeasure.getString(E.weScoreConvEnumId.name()))) {
            			TransactionAPFinder transactionAPFinder = new TransactionAPFinder(delegator, workEffortMeasureId, organizationId, thruDate, sourceReferenceIdMeasure, accountCode);
            		    List<GenericValue> transactionList = transactionAPFinder.getTransactionList();
            		    if (UtilValidate.isNotEmpty(transactionList)) {
            		    	Iterator<GenericValue> transactionListIter = transactionList.iterator();
            		    	while(transactionListIter.hasNext()) {
            		    		GenericValue transactionItem = transactionListIter.next();
                                Map<String, Object> map = measKpi.getAllFields();
                                map.putAll(transactionItem.getAllFields());
                                map.put(E.actualValue.name(), transactionItem.get(E.weTransValue.name()));
                                GenericValue transactionPA = EntityUtil.getFirst(transactionAPFinder.getTransactionAPList());
                                if (UtilValidate.isNotEmpty(transactionPA)) {
                                	map.put(E.actualPyValue.name(), transactionPA.get(E.weTransValue.name()));
                                }
                                entityList.add(map);
            		    	}
            		    }
            		}
            	}

                Date fromDate = new Date(measKpi.getTimestamp(E.fromDate.name()).getTime());
                String gPeriodicalAbsEnumId = measKpi.getString(E.periodicalAbsoluteEnumId.name());
                String gTargetPeriodEnumId = measKpi.getString(E.targetPeriodEnumId.name());
                String gWeWithoutTarget = measKpi.getString(E.weWithoutTarget.name());

                Date maxLimitExcellent = getMaxDate(limitExcellentList, workEffortMeasureId);
                Date maxLimitMax = getMaxDate(limitMaxList, workEffortMeasureId);
                Date dateBudget = getMaxDate(budgetList, workEffortMeasureId);

                // eventuale data di fine periodo padre, usato per il target
                Date dateParentBudget = null;
                if (TargetPeriod.isTargetParent(gTargetPeriodEnumId)) {
                    dateParentBudget = getParentBugdet();
                }
                
                Date maxLimitMin = getMaxDate(limitMinList, workEffortMeasureId);
                Date maxActual = getMaxDate(actualList, workEffortMeasureId);
                //Bug 16
                Date maxActualPy = getMaxDate(actualPyList, workEffortMeasureId);

                // Seconda left join
                ReadkpiConditionCreator rKpiCond = new ReadkpiConditionCreator(delegator, thruDate, workEffortMeasureId, fromDate, gPeriodicalAbsEnumId, gTargetPeriodEnumId, sourceReferenceIdMeasure, accountCode, organizationId);
                EntityCondition leftJoinCod = rKpiCond.createReadKpiCondition(maxLimitExcellent, maxLimitMax, dateBudget, dateParentBudget, maxLimitMin, maxActual, maxActualPy, limitExcellent, limitMax, target, limitMin, performance);
                jLogger.mergeData(rKpiCond.getJobLogger());

                jLogger.addMessage(ServiceLogger.makeLogDebug("Search " + E.WorkEffortTransactionIndicatorView.name() + " with condition " + leftJoinCod, MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));
                List<GenericValue> wemInnerList = delegator.findList(E.WorkEffortTransactionIndicatorView.name(), leftJoinCod, null, UtilMisc.toList("-weTransTypeValueId", "weTransDate"), null, false);

                if (UtilValidate.isNotEmpty(wemInnerList)) {
                    Iterator<GenericValue> innerListIt = wemInnerList.iterator();
                    while (innerListIt.hasNext()) {
                        GenericValue innerItem = innerListIt.next();
                        Map<String, Object> map = measKpi.getAllFields();
                        map.putAll(innerItem.getAllFields());
                        map.put(E.glFiscalTypeId.name(), innerItem.get((E.weTransTypeValueId.name())));
                        map.put(E.amount.name(), innerItem.get("weTransValue"));
                        map.put(E.kpiScoreWeight.name(), measKpi.get(scoreField));
                        valuesList.add(map);
                    }
                } else {
                    Map<String, Object> map = measKpi.getAllFields();
                    map.put(E.kpiScoreWeight.name(), measKpi.get(scoreField));
                    valuesList.add(map);
                }

                // recupera SCOREKPI del periodo precedente dello stesso anno di thruDate, verranno utilizzati se non esiste il target al periodo e TARGET_PARENT_EXEC
                // oppure se non esiste la performance per fare il calcolo 
                if (WithoutTarget.WEWITHTARG_PRV_SCORE.name().equals(gWeWithoutTarget)) {              	
                    List<EntityCondition> condList = new FastList<EntityCondition>();
                    condList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
                    condList.add(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, getFirstDayOfYear(thruDate)));
                    condList.add(EntityCondition.makeCondition(E.periodTypeId.name(), periodTypeId));
                    List<String> orderBy = UtilMisc.toList("-fromDate");
                    List<GenericValue> perioList = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(condList), null, orderBy, null, false);
                    GenericValue lastPeriod = EntityUtil.getFirst(perioList);
                    
                    if (UtilValidate.isNotEmpty(lastPeriod)) {
                        WorkEffortMeasureScoreKpiExtractor workEffortMeasureScoreKpiExtractor2 = new WorkEffortMeasureScoreKpiExtractor(delegator, scoreCard, scoreValueType, lastPeriod.getTimestamp(E.thruDate.name()), weightType, scorePeriodEnumId);
                        workEffortMeasureScoreKpiExtractor2.setWorkEffortMeasureScoreKpiConditions(workEffortMeasureId, true);
                        jLogger.addMessage(ServiceLogger.makeLogDebug("Search " + workEffortMeasureScoreKpiExtractor2.getWorkEffortMeasureScoreKpiEntityName() + " with condition " + EntityCondition.makeCondition(workEffortMeasureScoreKpiExtractor2.getWorkEffortMeasureScoreKpiConditions()), MessageCode.INFO_GENERIC.toString(), sourceReferenceId, accountCode, null));
                        
                        List<GenericValue> scoreKpiList2 = workEffortMeasureScoreKpiExtractor2.getWorkEffortMeasureScoreKpiList();

                        //Se trovo uno SCOREKPI al periodo precedente utilizzo quello!!
                        if (UtilValidate.isNotEmpty(scoreKpiList2)) {
                            GenericValue scoreKpi2 = EntityUtil.getFirst(scoreKpiList2);
                            Double value = scoreKpi2.getDouble("amount");
                            jLogger.addMessage(ServiceLogger.makeLogInfo("Indicator with \"Punteggio precedente\".\\n Found a SCOREKPI with value " + value + " for previous period", MessageCode.INFO_GENERIC.toString(), sourceReferenceId, scoreKpi2.getString("mAccountCode"), null));

                            Map<String, Object> map = new FastMap<String, Object>();
                            map.put(ScoreCard.FOUND_SCOREKPI_VALUE, value);
                            map.put(E.kpiScoreWeight.name(), scoreKpi2.getDouble(scoreField));
                            map.put(E.workEffortMeasureId.name(), scoreKpi2.getString(E.workEffortMeasureId.name()));
                            map.put(E.glAccountId.name(), scoreKpi2.getString(E.mAccountId.name()));
                            entityList.add(map);
                        }                   	
                    }
                }
            }
        }

        // Ordino la lista in base al workEffortMeasureId, glAccountId, ecc...,
        Collections.sort(valuesList, MeasureKpiComparator.getComparator(MeasureKpiComparator.WE_MEASURE_ID, MeasureKpiComparator.GL_ACCOUNT, MeasureKpiComparator.KPI_SCORE, MeasureKpiComparator.WE_SCORE_RANGE, MeasureKpiComparator.WE_SCORE_CONV, MeasureKpiComparator.WE_OTHER_GOAL, MeasureKpiComparator.WE_WITHOUT, MeasureKpiComparator.DESC_WE_TRANS_TYPE));

        // raggruppo le liste in base a workEffortMeasureId, glAccountId,
        // ecc..., bug 4414
        List<List<Map<String, Object>>> groupedList = subDivideByKey(valuesList);

        for (List<Map<String, Object>> subList : groupedList) {
            // Ciclo di group by e calcoli, entityList contiene i scoreKpi trovati, subList contiene i movimenti
            executeHaving(entityList, subList, limitExcellent, limitMax, target, limitMin, performance, scoreCard, thruDate);
        }

        return entityList;
    }
    
    /**
     * ritorna il primo gennaio dell anno di thruDate
     * @param thruDate
     * @return
     */
    private Date getFirstDayOfYear(Date thruDate) {
    	Calendar thruDateCal = new GregorianCalendar();
    	thruDateCal.setTime(thruDate);
    	Calendar firstDayOfYear = new GregorianCalendar();
    	firstDayOfYear.set(Calendar.YEAR, thruDateCal.get(Calendar.YEAR));
    	firstDayOfYear.set(Calendar.MONTH, 0);
    	firstDayOfYear.set(Calendar.DAY_OF_MONTH, 1);
    	firstDayOfYear.set(Calendar.HOUR_OF_DAY, 0);
    	firstDayOfYear.set(Calendar.MINUTE, 0);
    	firstDayOfYear.set(Calendar.SECOND, 0);
    	firstDayOfYear.set(Calendar.MILLISECOND, 0);
    	return firstDayOfYear.getTime();
    	
    }

    private Date getParentBugdet() {
        if (UtilValidate.isNotEmpty(customTimePeriod) && UtilValidate.isNotEmpty(customTimePeriod.getString(E.customTimePeriodId.name()))) {
            if (UtilValidate.isNotEmpty(customTimePeriod.getTimestamp("parentThruDate"))) {
                return new Date(customTimePeriod.getTimestamp("parentThruDate").getTime());
            }
            return new Date(customTimePeriod.getTimestamp("thruDate").getTime());
        }
        return null;
    }

    private void executeHaving(List<Map<String, Object>> entityList, List<Map<String, Object>> subList, String limitExcellent, String limitMax, String target, String limitMin, String performance, String scoreCard, Date thruDate) throws GenericEntityException {
        Map<String, Object> lastItem = null;
        double limitExcellentValue = 0d, limitMaxValue = 0d, targetValue = 0d, limitMinValue = 0d, actualValue = 0d, actualPyValue = 0d, limitExcellentCount = 0d, limitMaxCount = 0d, targetCount = 0d, limitMinCount = 0d, actualCount = 0d, actualPyCount = 0d;
        String key = "";
        boolean havingTypeId = false; // having con target per weScoreConvEnumId = WECONVER_NOCONVERSIO
        boolean havingActualTypeId = false; // having con performance per weWithoutPerf = WEWITHPERF_NO_CALC

        // tiene traccia del fatto che ho almeno un BUDGET/Target alla data di lancio
        boolean havingTargetTypeId = false;
        boolean havingMadatoryTargetEmpty = false;

        for (Map<String, Object> item : subList) {
            // Applico having, prendero' la misura se almeno un movimento soddisfa le clausole sia per weScoreConvEnumId che per weWithoutPerf
            
            // se ho gia un movimento da prendere per weScoreConvEnumId per quella misura dal precedente passo del ciclo for
            boolean alreadyHasHavingTypeId = havingTypeId;
            // se ho un movimento di BUDGET o di SOGLIA (alla data del calcolo), ( e weScoreConvEnumId != NOCONVERSION)
            boolean havingConversionAndType = (item.get(E.amount.name()) != null && hasFiscalType(item, limitExcellent, limitMax, target, limitMin) && !ConversionRule.WECONVER_NOCONVERSIO.name().equalsIgnoreCase((String)item.get(E.weScoreConvEnumId.name())));
            // se weScoreConvEnumId == NOCONVERSION, (BUDGET non serve)
            boolean havingNoConversion = (ConversionRule.WECONVER_NOCONVERSIO.name().equalsIgnoreCase((String)item.get(E.weScoreConvEnumId.name())));
            
            boolean havingTarget = (
                    target.equalsIgnoreCase((String)item.get(E.glFiscalTypeId.name())) 
                    && (
                            (!(TargetPeriod.TARGET_PARENT_EXEC.name().equals((String)item.get(E.targetPeriodEnumId.name()))) && UtilValidate.isNotEmpty(item.get(E.weTransDate.name())))
                            || (TargetPeriod.TARGET_PARENT_EXEC.name().equals((String)item.get(E.targetPeriodEnumId.name())) && thruDate.equals(item.get(E.weTransDate.name()))) 
                       ));
            // jLogger.addMessage(ServiceLogger.makeLogInfo(" misura = " + item.get(E.workEffortMeasureId.name()) + " havingTargetTypeId = " + havingTargetTypeId, MessageCode.INFO_GENERIC.toString(), null, null, null)); 
            havingTargetTypeId = havingTargetTypeId || havingTarget;
            
            // jLogger.addMessage(ServiceLogger.makeLogInfo(" misura = " + item.get(E.workEffortMeasureId.name()) + " havingTargetTypeId = " + havingTargetTypeId + " havingMadatoryTargetEmpty = " + havingMadatoryTargetEmpty, MessageCode.INFO_GENERIC.toString(), null, null, null)); 
            
            havingMadatoryTargetEmpty = TargetPeriod.TARGET_PARENT_EXEC.name().equals((String)item.get(E.targetPeriodEnumId.name())) && !havingTargetTypeId;
            
            // se BUDGET popolato
            if (havingTargetTypeId) {
                // jLogger.addMessage(ServiceLogger.makeLogInfo(" misura = " + item.get(E.workEffortMeasureId.name()) + " rimuovo SCOREKPI ", MessageCode.INFO_GENERIC.toString(), null, null, null)); 
                if (UtilValidate.isNotEmpty(entityList)) {
                    for (Map<String, Object> map : entityList) {
                        if (map.containsKey(ScoreCard.FOUND_SCOREKPI_VALUE)) {
                            String workEffortMeasureIdMap = (String)map.get(E.workEffortMeasureId.name());
                            String workEffortMeasureIdItem = (String)item.get(E.workEffortMeasureId.name());
                            if (workEffortMeasureIdItem.equals(workEffortMeasureIdMap)) {
                                entityList.remove(map);
                            }
                        }
                    }
                }
            }

            // sposto la gestione del movimento ACTUAL piu' avanti,
            // in modo da poter gestire anche le misure che hanno ACTUAL e NO_CALC
            havingTypeId = (alreadyHasHavingTypeId || havingConversionAndType || havingNoConversion);
            
            // se BUDGET obbligatorio e non popolato non fa calcolo
            if (!havingTypeId || havingMadatoryTargetEmpty) {
                continue;
            }
            
            // Controllo groupBy
            StringBuilder sb = new StringBuilder();
            sb.append((String)item.get(E.workEffortMeasureId.name()));
            sb.append((String)item.get(E.glAccountId.name()));
            sb.append(item.get(E.kpiScoreWeight.name()));
            sb.append((String)item.get(E.weScoreRangeEnumId.name()));
            sb.append((String)item.get(E.weScoreConvEnumId.name()));
            sb.append((String)item.get(E.weOtherGoalEnumId.name()));
            sb.append((String)item.get(E.weWithoutPerf.name()));

            // cambio chiave, operazioni in group by
            if (!sb.toString().equals(key)) {

                key = sb.toString();
                // se = null sono al primo giro quindi devo solo azzerare i valori
                if (isLastItemToClone(lastItem, item)) {

                    // Resetto indicatore di valore a budget e actual controllando
                    // che il record corrente (il primo della rottura chiave) non lo sia
                    havingTypeId = hasFiscalType(item, limitExcellent, limitMax, target, limitMin);
                    // A questo punto ho elaborato tutti i movimenti per quella misura, ma prima di considerarla davvero per il calcolo devo verificare che esite ACTUAL oppure che non sia NO_CALC

                    // se ho gia un movimento da prendere per per weWithoutPerf per quella misura dal precedente passo del ciclo for
                    boolean alreadyHasHavingActualTypeId = havingActualTypeId;
                    // se ho un movimento di ACTUAL, ( e weWithoutPerf == WEWITHPERF_NO_CALC)
                    boolean havingPerfNoCalc = (item.get(E.amount.name()) != null && performance.equalsIgnoreCase((String)item.get(E.glFiscalTypeId.name())) && WithoutPerformance.WEWITHPERF_NO_CALC.name().equalsIgnoreCase((String)item.get(E.weWithoutPerf.name())));
                    // se weWithoutPerf != WEWITHPERF_NO_CALC, (ACTUAL non serve, viene forzato)
                    boolean havingOtherPerf = (!WithoutPerformance.WEWITHPERF_NO_CALC.name().equalsIgnoreCase((String)item.get(E.weWithoutPerf.name())));

                    havingActualTypeId = (alreadyHasHavingActualTypeId || havingPerfNoCalc || havingOtherPerf);
                    if (havingActualTypeId) {
                        // Clono tutti i valori
                        ValueClone valueClone = new ValueClone(delegator);
                        valueClone.setLimitExcellent(limitExcellentValue, limitExcellentCount);
                        valueClone.setLimitMax(limitMaxValue, limitMaxCount);
                        valueClone.setTarget(targetValue, targetCount);
                        valueClone.setLimitMin(limitMinValue, limitMinCount);
                        valueClone.setActual(actualValue, actualCount);
                        valueClone.setActualPy(actualPyValue, actualPyCount);
                        valueClone.cloneValues(entityList, lastItem);
                    }
                }
                // Reset values
                limitExcellentValue = 0d;
                limitMaxValue = 0d;
                targetValue = 0d;
                limitMinValue = 0d;
                actualValue = 0d;
                actualPyValue = 0d;
                limitExcellentCount = 0d;
                limitMaxCount = 0d;
                targetCount = 0d;
                limitMinCount = 0d;
                actualCount = 0d;
                actualPyCount = 0d;
            }

            // Calcolo valori su groupby
            String glFiscalTypeId = (String)item.get(E.glFiscalTypeId.name());
            if (glFiscalTypeId != null) {
                double amount = (item.get(E.amount.name()) != null) ? (Double)item.get(E.amount.name()) : 0;
                String gTargetPeriodEnumId = (item.get(E.targetPeriodEnumId.name()) != null) ? (String)item.get(E.targetPeriodEnumId.name()) : null;
                
                if (limitExcellent.equals(glFiscalTypeId)) {
                    limitExcellentValue += amount;
                    limitExcellentCount++;
                }

                
                if (limitMax.equals(glFiscalTypeId)) {
                    limitMaxValue += amount;
                    limitMaxCount++;
                }

                if (target.equals(glFiscalTypeId)) {
                    if (TargetPeriod.isTargetParent(gTargetPeriodEnumId) && "BUDGET".equals(target)) {
                        // per BUDGET potrei avere due valori possibili di target, uno del periodo padre e uno per il lancio
                        // do per sconatato che prima passa il target al periodo di lancio e poi al periodo padre
                        if (getParentBugdet() != null && getParentBugdet().equals(item.get(E.weTransDate.name()))) {
                            targetValue = amount;
                        } else if (thruDate.equals(item.get(E.weTransDate.name()))) {
                            targetValue = amount;
                        }
                    } else {
                        targetValue += amount;
                        targetCount++;
                    }
                }

                if (limitMin.equals(glFiscalTypeId)) {
                    limitMinValue += amount;
                    limitMinCount++;
                }

                if (performance.equals(glFiscalTypeId)) {
                    if (TargetPeriod.isTargetParent(gTargetPeriodEnumId) && "BUDGET".equals(performance)) {
                        // potrei avere due valori possibili di target, uno del periodo di lancio e uno del periodo padre
                        if (thruDate.equals(item.get(E.weTransDate.name()))) {
                            actualValue += amount;
                            actualCount++;
                        }
                    } else {
                        actualValue += amount;
                        actualCount++;
                    }
                }

                if ("ACTUAL_PY".equals(glFiscalTypeId)) {
                    actualPyValue += amount;
                    actualPyCount++;
                }
            }
            // riferimento all'ultimo record da inserire
            lastItem = item;
        }

        // Aggiungo ultimo item alla lista
        if (isLastItemToClone(lastItem, null)) {
            // se ho gia un movimento da prendere per per weWithoutPerf per quella misura dal precedente passo del ciclo for
            boolean alreadyHasHavingActualTypeId = havingActualTypeId;
            // se ho un movimento di ACTUAL, ( e weWithoutPerf == WEWITHPERF_NO_CALC)
            boolean havingPerfNoCalc = (lastItem.get(E.amount.name()) != null && performance.equalsIgnoreCase((String)lastItem.get(E.glFiscalTypeId.name())) && WithoutPerformance.WEWITHPERF_NO_CALC.name().equalsIgnoreCase((String)lastItem.get(E.weWithoutPerf.name())));
            // se weWithoutPerf != WEWITHPERF_NO_CALC, (ACTUAL non serve, viene forzato)
            boolean havingOtherPerf = (!WithoutPerformance.WEWITHPERF_NO_CALC.name().equalsIgnoreCase((String)lastItem.get(E.weWithoutPerf.name())));

            havingActualTypeId = (alreadyHasHavingActualTypeId || havingPerfNoCalc || havingOtherPerf);

            if (havingActualTypeId) {
                // Clono tutti i valori di GenericValue
                // Clono tutti i valori
                ValueClone valueClone = new ValueClone(delegator);
                valueClone.setLimitExcellent(limitExcellentValue, limitExcellentCount);
                valueClone.setLimitMax(limitMaxValue, limitMaxCount);
                valueClone.setTarget(targetValue, targetCount);
                valueClone.setLimitMin(limitMinValue, limitMinCount);
                valueClone.setActual(actualValue, actualCount);
                valueClone.setActualPy(actualPyValue, actualPyCount);
                valueClone.cloneValues(entityList, lastItem);
            }
        }

    }

    /**
     * costruisce le condizioni di ricerca dei movimenti
     * @param weTransTypeValueId
     * @param workEffortMeasureIdList
     * @param thruDate
     * @return
     */
    private EntityCondition getWhereConditionList(String weTransTypeValueId, List<String> workEffortMeasureIdList, Date thruDate) {
        List<EntityCondition> whereConditionList = new FastList<EntityCondition>();
        whereConditionList.add(EntityCondition.makeCondition(E.weTransTypeValueId.name(), weTransTypeValueId));
        whereConditionList.add(EntityCondition.makeCondition(E.weTransMeasureId.name(), EntityOperator.IN, workEffortMeasureIdList));
        whereConditionList.add(EntityCondition.makeCondition(E.weTransDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
        whereConditionList.add(EntityCondition.makeCondition(E.organizationPartyId.name(), organizationId));

        whereConditionList.add(EntityCondition.makeCondition(E.weTransWorkEffortSnapShotId.name(), null));
        return EntityCondition.makeCondition(whereConditionList);
    }

    /**
     * ritorna la data del movimento
     * @param weTransIndList
     * @param workEffortMeasureId
     * @return
     */
    private Date getMaxDate(List<GenericValue> weTransIndList, String workEffortMeasureId) {
        GenericValue weTransIndItem = EntityUtil.getFirst(EntityUtil.filterByAnd(weTransIndList, UtilMisc.toMap(E.weTransMeasureId.name(), workEffortMeasureId)));
        Date maxDate = new Date();
        if (UtilValidate.isNotEmpty(weTransIndItem)) {
            Timestamp maxStamp = weTransIndItem.getTimestamp(E.weTransDate.name());
            maxDate = new Date(maxStamp.getTime());
        }
        return maxDate;
    }

    /**
     * verifica che il glFiscalTypeId sia tra quelli utili per il calcolo
     * @param item
     * @param limitExcellent
     * @param limitMax
     * @param target
     * @param limitMin
     * @return
     */
    private boolean hasFiscalType(Map<String, Object> item, String limitExcellent, String limitMax, String target, String limitMin) {
    	return limitExcellent.equalsIgnoreCase((String)item.get(E.glFiscalTypeId.name()))
    			|| limitMax.equalsIgnoreCase((String)item.get(E.glFiscalTypeId.name()))
    			|| target.equalsIgnoreCase((String)item.get(E.glFiscalTypeId.name()))
    			|| limitMin.equalsIgnoreCase((String)item.get(E.glFiscalTypeId.name()));
    }

    @Override
    /**
     * Return jobLogger
     */
    public JobLogger getJobLogger() {
        return jLogger;
    }

    private List<List<Map<String, Object>>> subDivideByKey(List<Map<String, Object>> valuesList) {
        List<List<Map<String, Object>>> returnList = new ArrayList<List<Map<String, Object>>>();
        String key = "";
        List<Map<String, Object>> subList = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < valuesList.size(); i++) {
            Map<String, Object> mappa = (Map<String, Object>)valuesList.get(i);
            StringBuilder sb = new StringBuilder();
            sb.append((String)mappa.get(E.workEffortMeasureId.name()));
            sb.append((String)mappa.get(E.glAccountId.name()));
            sb.append(mappa.get(E.kpiScoreWeight.name()));
            sb.append((String)mappa.get(E.weScoreRangeEnumId.name()));
            sb.append((String)mappa.get(E.weScoreConvEnumId.name()));
            sb.append((String)mappa.get(E.weOtherGoalEnumId.name()));
            sb.append((String)mappa.get(E.weWithoutPerf.name()));
            // cambio chiave, operazioni in group by
            if (!sb.toString().equals(key)) {
                key = sb.toString();
                if (UtilValidate.isNotEmpty(subList)) {
                    returnList.add(subList);
                }
                subList = new ArrayList<Map<String, Object>>();
                subList.add(mappa);
            } else {
                subList.add(mappa);
            }

        }
        if (UtilValidate.isNotEmpty(subList)) {
            returnList.add(subList);
        }
        return returnList;
    }

    /**
     * se la regola di calcolo e' no conversion lastItem va sempre aggiunto,
     * altrimeni va aggiunto solo se ho anche il movimento
     * @param lastItem
     * @param item
     * @return
     */
    private boolean isLastItemToClone(Map<String, Object> lastItem, Map<String, Object> item) {
        if (lastItem == null) {
            return false;
        }
        String weScoreConvEnumId = (item != null) ? (String)item.get(E.weScoreConvEnumId.name()) : (String)lastItem.get(E.weScoreConvEnumId.name());
        if (ConversionRule.WECONVER_NOCONVERSIO.name().equals(weScoreConvEnumId)) {
            return true;
        }
        return ((String)lastItem.get(E.glFiscalTypeId.name()) != null);
    }
}
