package com.mapsengineering.accountingext.services;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.find.WorkEffortFindServices;
import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.util.JobLogger;

/**
 * Write transaction
 *
 */
class ValuesWriter {

    private LocalDispatcher dispatcher;

    private Map<String, ? extends Object> context;

    private final Map<String, String> valResultFieldMap;

    private JobLogger jLogger;

    /**
     * Constructor
     * @param dispatcher
     * @param context
     * @param jLogger
     */
    ValuesWriter(LocalDispatcher dispatcher, Map<String, ? extends Object> context, JobLogger jLogger) {
        this.dispatcher = dispatcher;
        this.context = context;
        this.valResultFieldMap = FastMap.newInstance();
        this.valResultFieldMap.put("amount", "weTransValue");
        this.valResultFieldMap.put("origAmount", "origTransValue");
        this.jLogger = jLogger;
    }

    @SuppressWarnings("unchecked")
    String storeValues(GenericValue indicator, String resultName, Double valResult, Map<String, Object> extraParametersToStore, EntityCondition condition) throws GeneralException {
        List<EntityCondition> valueCond = FastList.newInstance();
        Map<String, Object> localContext = getParameterMap(indicator, resultName, valResult);
        valueCond.add(EntityCondition.makeCondition("entryWorkEffortSnapshotId", null));
        valueCond.add(EntityCondition.makeCondition("workEffortSnapshotId", null));
        valueCond.add(EntityCondition.makeCondition(condition));
        // use this view because contains detailEnumId and inputEnumId
        String msg = "Search AcctgTransAndEntriesIndicCalcView with condition = " + EntityCondition.makeCondition(valueCond);
        jLogger.printLogInfo(msg);
        List<GenericValue> valueList = dispatcher.getDelegator().findList("AcctgTransAndEntriesIndicCalcView", EntityCondition.makeCondition(valueCond), null, null, null, false);
        String operation = "CREATE";
        String automaticPk = "Y";

        if (UtilValidate.isNotEmpty(valueList)) {
            GenericValue first = EntityUtil.getFirst(valueList);
            operation = "UPDATE";
            localContext.put("weTransId", first.getString(E.acctgTransId.name()));
            localContext.put("weTransEntryId", first.getString("entryAcctgTransEntrySeqId"));
            automaticPk = "";
        }

        if (UtilValidate.isNotEmpty(extraParametersToStore)) {
            localContext.putAll(extraParametersToStore);
        }

        localContext.put(E.weTransTypeValueId.name(), extraParametersToStore.get(E.entryGlFiscalTypeId.name()));

        localContext.put("operation", operation);
        localContext.put("_AUTOMATIC_PK_", automaticPk);
        localContext.put(E.debitCreditDefault.name(), indicator.getString(E.debitCreditDefault.name()));
        
        WorkEffortFindServices workEffortFindServices = new WorkEffortFindServices(dispatcher.getDelegator(), dispatcher);
        localContext.put("defaultOrganizationPartyId", workEffortFindServices.getOrganizationId((GenericValue) context.get(ServiceLogger.USER_LOGIN), false));

        localContext = baseCrudInterface("WorkEffortTransactionView", operation, localContext);

        Map<String, Object> srvResult = callService(resultName, valResult, indicator.getString(E.glAccountId.name()), extraParametersToStore, condition, localContext);
        return (String)(((Map<String, Object>)srvResult.get("id")).get("weTransId"));
    }

    private Map<String, Object> callService(String resultName, Double valResult, String glAccountId, Map<String, Object> extraParametersToStore, EntityCondition condition, Map<String, Object> localContext) throws GeneralException {
        Map<String, Object> srvResult = dispatcher.runSync("crudServiceDefaultOrchestration_AcctgTransAndEntries", localContext);
        if (!ServiceUtil.isSuccess(srvResult)) {
            String msg = "Error while writing row for " + resultName + " = " + valResult + " with indicator id " + glAccountId + ", extraParametersToStore " + extraParametersToStore + " and condition" + condition;
            msg += ", service error: " + ServiceUtil.getErrorMessage(srvResult);
            throw new GeneralException(msg);
        }
        return srvResult;
    }

    /**
     * Set acctgTransTypeId = GLA_ORU,
     * weTransCurrencyUomId = defaultUomId
     * origAmount = amount if empty
     * customTimePeriodId = thruDate with periodTypeId of indicator
     * @param indicator
     * @param resultName
     * @param valResult
     * @return
     * @throws GeneralException
     */
    private Map<String, Object> getParameterMap(GenericValue indicator, String resultName, Double valResult) throws GeneralException {
        Delegator delegator = dispatcher.getDelegator();

        Map<String, Object> localContext = UtilMisc.toMap(E.weTransAccountId.name(), indicator.getString(E.glAccountId.name()), "weTransCurrencyUomId", indicator.get("defaultUomId"), "acctgTransTypeId", "GLA_ORU");

        EntityCondition condition = EntityCondition.makeCondition(EntityCondition.makeCondition("periodTypeId", indicator.getString("periodTypeId")), EntityCondition.makeCondition(E.thruDate.name(), context.get(E.thruDate.name())));

        GenericValue cp = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", condition, null, null, null, true));
        if (UtilValidate.isEmpty(cp)) {
            String msg = "No custom type period found for indicator id = " + indicator.getString(E.glAccountId.name()) + ", " + condition;
            throw new GeneralException(msg);
        }
        localContext.put(E.customTimePeriodId.name(), cp.getString("customTimePeriodId"));
        if (E.amount.name().equals(resultName)) {
            localContext.put(valResultFieldMap.get("origAmount"), valResult);
        }
        localContext.put(valResultFieldMap.get(resultName), valResult);

        localContext.put("customTimePeriodId", cp.getString("customTimePeriodId"));

        return localContext;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> baseCrudInterface(String entityName, String operation, @SuppressWarnings("rawtypes") Map parameters) {
        Map<String, Object> serviceMap = FastMap.newInstance();
        serviceMap.put("entityName", entityName);
        serviceMap.put("operation", operation);
        serviceMap.put(ServiceLogger.USER_LOGIN, context.get(ServiceLogger.USER_LOGIN));
        parameters.put("operation", operation);
        serviceMap.put("parameters", parameters);
        serviceMap.put(ServiceLogger.LOCALE, context.get(ServiceLogger.LOCALE));
        return serviceMap;
    }
}
