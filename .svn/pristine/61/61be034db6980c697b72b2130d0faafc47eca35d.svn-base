package com.mapsengineering.accountingext.services;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.accountingext.util.GlFiscalTypeOutputUtil;
import com.mapsengineering.accountingext.util.WorkEffortUtil;
import com.mapsengineering.base.find.WorkEffortFindServices;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;

/**
 * Indicator Calc for workEffort, call Indicator Calc
 *
 */
public class IndicatorCalcObiettivoServices {

    public static final String MODULE = IndicatorCalcServices.class.getName();

    private Delegator delegator;

    private LocalDispatcher dispatcher;

    private JobLogger jLogger;

    private String workEffortId;
    
    private String onlyElaborateIndicator;

    private Map<String, Object> res;

    private Map<String, ? extends Object> context;

    /**
     * Esegue calcolo indicatori obiettivo
     * <p>Parametri attesi:
     * <p>thruDate - data del calcolo;
     * <p>glFiscalTypeIdInput - tipo rilevazione input  
     * <p>glFiscalTypeIdOuput - tipo rilevazione output
     * <p>workeffortId - obiettivo  
     * @param dctx
     * @param context 
     * @return risultati del calcolo degli indicatori
     */
    public static Map<String, Object> indicatorCalcObiettivoImpl(DispatchContext dctx, Map<String, ? extends Object> context) {
        IndicatorCalcObiettivoServices srv = new IndicatorCalcObiettivoServices(dctx, context);
        srv.execute();
        return srv.getResult();
    }

    /**
     * Constructor
     */
    public IndicatorCalcObiettivoServices(DispatchContext dctx, Map<String, ? extends Object> context) {
        res = ServiceUtil.returnSuccess();
        dispatcher = dctx.getDispatcher();
        delegator = dctx.getDelegator();
        
        // locale and timeZone is in context
        this.context = context;
        jLogger = new JobLogger(MODULE);

        this.onlyElaborateIndicator = (String) context.get(E.onlyElaborateIndicator.name());
        
        workEffortId = (String)context.get(E.workEffortId.name());
    }

    /**
     * Esegue calcolo indicatori, popola mappa coi risultati dell'elaborazione
     */
    public void execute() {

        String msg = "Start Elaboration Indicator Target Calculation ";
        jLogger.printLogInfo(msg);

        try {

            loadIndicatorList();

            executeScoreCardCalcImpl();

        } catch (Exception e) {
            msg = "Indicator Calc Target Service return the error below: ";
            jLogger.printLogError(e, msg);
            res = ServiceUtil.returnError(e.getMessage());
        }

        msg = "Finished elaboration indicator calculator Target with " + jLogger.getRecordElaborated() + " elaborated values and " + jLogger.getErrorMessages() + " errors";
        jLogger.printLogInfo(msg);

        res.put("warnMessages", jLogger.getWarnMessages());
        res.put(JobLogger.ERROR_MESSAGES, jLogger.getErrorMessages());
        res.put("runResults", jLogger.getMessages());
        res.put("recordElaborated", jLogger.getRecordElaborated());
    }

    /**
     * Ritorna risultati del calcolo degli indicatori
     * @return mappa contenente: 
     * <p>runResults con i messaggi di log
     * <p>recordElaborated
     * <p>warnMessages
     * <p>errorMessages
     */
    public Map<String, Object> getResult() {
        return res;
    }

    private void loadIndicatorList() throws GeneralException {

        List<GenericValue> indicatorList = getIndicatorList();
        Iterator<GenericValue> indicIt = indicatorList.iterator();
        while (indicIt.hasNext()) {
            GenericValue indicator = indicIt.next();
            jLogger.addRecordElaborated(1L);
            runSingleIndicatorTransaction(indicator);
        }
        return;
    }

    /**
     * Prendo tutti gli indicatori dell'obiettivo ordinandoli per obiettivo e sequenza calcolo		 
     */
    private List<GenericValue> getIndicatorList() throws GeneralException {

        /**
         * Carico tutte le schede relativi all'obittivo 
         */
        List<EntityCondition> condWorkEffort = FastList.newInstance();
        condWorkEffort.add(EntityCondition.makeCondition(E.workEffortParentId.name(), workEffortId));
        condWorkEffort.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));

        List<GenericValue> workEffortList = delegator.findList(E.WorkEffort.name(), EntityCondition.makeCondition(condWorkEffort, EntityJoinOperator.OR), null, null, null, false);
        List<String> workEffortIdList = EntityUtil.getFieldListFromEntityList(workEffortList, E.workEffortId.name(), true);
        
        /**
         * Carico tutti gli indicatori
         */
        Timestamp startYearDate = UtilDateTime.getYearStart((Timestamp) context.get(E.thruDate.name()));
        Timestamp endYearDate = UtilDateTime.getYearEnd((Timestamp) context.get(E.thruDate.name()), (TimeZone) context.get(ServiceLogger.TIME_ZONE), (Locale) context.get(ServiceLogger.LOCALE));
        List<GenericValue> indicatorList = FastList.newInstance();
        List<EntityCondition> cond = FastList.newInstance();
        WorkEffortFindServices workEffortFindServices = new WorkEffortFindServices(delegator, dispatcher);
        String organizationId = workEffortFindServices.getOrganizationId((GenericValue) context.get(ServiceLogger.USER_LOGIN), false);
        cond.add(EntityCondition.makeCondition(E.weTransWeId.name(), EntityOperator.IN, workEffortIdList));
        cond.add(EntityCondition.makeCondition(E.accountTypeEnumId.name(), "INDICATOR"));
        cond.add(EntityCondition.makeCondition(E.organizationPartyId.name(), organizationId));
        cond.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.calcCustomMethodId.name(), EntityOperator.NOT_EQUAL, null)));
        cond.add(EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, endYearDate));
        cond.add(EntityCondition.makeCondition(EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, startYearDate), EntityOperator.OR, EntityCondition.makeCondition(E.thruDate.name(), GenericValue.NULL_FIELD)));
     
        List<String> orderBy = FastList.newInstance();
        orderBy.add(E.weTransWeId.name());
        orderBy.add(E.prioCalc.name());

        indicatorList = delegator.findList(E.WorkEffortIndicatorView.name(), EntityCondition.makeCondition(cond), null, orderBy, null, false);

        String msg = "Find  " + indicatorList.size() + " for condition " + EntityCondition.makeCondition(cond);
        jLogger.printLogInfo(msg);
        return indicatorList;
    }

    @SuppressWarnings("unchecked")
    private void runSingleIndicatorTransaction(GenericValue indicator) throws GeneralException {
        if (indicator == null) {
            String msg = "Indicator id is not a valid identifier ";
            jLogger.printLogError(msg);
        } else {

            /**
             * Chiamo il calcolo indicatori
             */
            GlFiscalTypeOutputUtil glFiscalTypeOutputUtil = new GlFiscalTypeOutputUtil(delegator, context);
            String glFiscalTypeOutput = glFiscalTypeOutputUtil.getGlFiscalTypeIdOutput(indicator);
            Long prioCalc = getPrioCalc(indicator.getLong(E.prioCalc.name()));
            String msg = "Execute indicator Calc with workEffortId=" + workEffortId + ", thruDate=" + context.get(E.thruDate.name()) + ", glFiscalTypeIdInput=" + context.get(E.glFiscalTypeIdInput.name()) + ", glFiscalTypeIdOutput=" + glFiscalTypeOutput + ", prioCalc=" + prioCalc + ", glAccountId=" + indicator.get(E.weTransAccountId.name());
            jLogger.printLogInfo(msg);

            Map<String, Object> localContext = FastMap.newInstance();

            localContext.put(ServiceLogger.USER_LOGIN, context.get(ServiceLogger.USER_LOGIN));
            localContext.put(ServiceLogger.LOCALE, context.get(ServiceLogger.LOCALE));
            localContext.put(ServiceLogger.TIME_ZONE, context.get(ServiceLogger.TIME_ZONE));

            localContext.put(E.thruDate.name(), (Timestamp)context.get(E.thruDate.name()));
            localContext.put(E.glFiscalTypeIdInput.name(), (String)context.get(E.glFiscalTypeIdInput.name()));
            localContext.put(E.prioCalc.name(), prioCalc);
            localContext.put(E.glAccountId.name(), indicator.get(E.weTransAccountId.name()));
            localContext.put(E.workEffortId.name(), workEffortId);
            localContext.put(E.glFiscalTypeIdOutput.name(), glFiscalTypeOutput);

            Map<String, Object> srvResult = dispatcher.runSync("indicatorCalcImpl", localContext);
            if (!ServiceUtil.isSuccess(srvResult)) {
                msg = "Errore";
                throw new GeneralException(msg);
            }

            readResultIndicator((List<Map<String, Object>>)srvResult.get("runResults"));
        }
    }
    
    private Long getPrioCalc(Long prioCalc) {
    	return (prioCalc != null ? prioCalc : 1L);
    }

    private void readResultIndicator(List<Map<String, Object>> messages) {
        
        for (Map<String, Object> message : messages) {
            String logType = (String)message.get("logType");
            if (ServiceLogger.LOG_TYPE_ERROR.equals(logType)) {
                jLogger.printLogError((String)message.get("logMessage"), (String)message.get("valueRef1"));
            } else if (ServiceLogger.LOG_TYPE_WARN.equals(logType)) {
                jLogger.printLogWarn((String)message.get("logMessage"), (String)message.get("valueRef1"));
            } else {
                jLogger.printLogInfo((String)message.get("logMessage"), (String)message.get("logCode"), (String)message.get("valueRef1"), (String)message.get("valueRef2"), (String)message.get("valueRef3"));
            }
        }

    }

    private void scoreCardCalcImpl() throws GeneralException {

        if (!E.Y.name().equals(onlyElaborateIndicator)) {
            /**
             * Prendo il glAccountId = "SCORE" e per ogni glFiscalTypeId chiamo il calcolo punteggio!
             */
            List<GenericValue> glAccountAndTypeAndFiscalTypeList = delegator.findList(E.GlAccountAndTypeAndFiscalType.name(), EntityCondition.makeCondition(E.glAccountId.name(), EntityOperator.EQUALS, "SCORE"), null, null, null, false);
            Iterator<GenericValue> iteratorList = glAccountAndTypeAndFiscalTypeList.iterator();

            String msg = "Find " + glAccountAndTypeAndFiscalTypeList.size() + " glFiscalType";
            jLogger.printLogInfo(msg);

            while (iteratorList.hasNext()) {
                GenericValue element = iteratorList.next();
                runSingleScoreCard(element);
            }
        }
        return;
    }

    private void executeScoreCardCalcImpl() throws GeneralException {

        WorkEffortUtil workEffortUtil = new WorkEffortUtil(delegator);
        boolean isRoot = workEffortUtil.isRoot(workEffortId);

        if (isRoot) {
            scoreCardCalcImpl();
        } else {
            String msg = "WorkEffort is not a root, no Score Card to calculate... ";
            jLogger.printLogInfo(msg);
        }
    }

    @SuppressWarnings("unchecked")
    private void runSingleScoreCard(GenericValue element) throws GeneralException {

        if (UtilValidate.isEmpty(element)) {
            String msg = "GlFiscalType id is not a valid identifier ";
            jLogger.printLogError(msg);

        } else {

            /**
             * Chiamo il calcolo punteggio
             */

            String msg = "Execute Score Card whitn workEffortId=" + workEffortId + ", target=BUDGET, thruDate=" + context.get(E.thruDate.name()) + ", glFiscalTypeId=" + element.get(E.glFiscalTypeId.name()) + ", cleanOnlyScoreCard=N";
            jLogger.printLogInfo(msg);

            Map<String, Object> localContext = FastMap.newInstance();

            localContext.put(ServiceLogger.USER_LOGIN, context.get(ServiceLogger.USER_LOGIN));
            localContext.put(ServiceLogger.LOCALE, context.get(ServiceLogger.LOCALE));
            localContext.put(ServiceLogger.TIME_ZONE, context.get(ServiceLogger.TIME_ZONE));

            localContext.put(E.workEffortId.name(), workEffortId);
            localContext.put(E.thruDate.name(), (Timestamp)context.get(E.thruDate.name()));
            localContext.put(E.target.name(), "BUDGET");

            localContext.put(E.performance.name(), element.get(E.glFiscalTypeId.name()));
            localContext.put(E.scoreValueType.name(), element.get(E.glFiscalTypeId.name()));

            localContext.put(E.cleanOnlyScoreCard.name(), "N");

            Map<String, Object> srvResult = dispatcher.runSync("scoreCardCalcImpl", localContext);
            if (!ServiceUtil.isSuccess(srvResult)) {
                msg = "Errore";
                throw new GeneralException(msg);
            }

            readResultIndicator((List<Map<String, Object>>)srvResult.get("runResults"));
        }
    }
}
