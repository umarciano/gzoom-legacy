package com.mapsengineering.workeffortext.services.trans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.util.JobLogger;

/**
 * Manage enumeration type for CustomTimePeriod
 *
 */
public class CustomTimePeriod extends GenericService {

    public static final String MODULE = CustomTimePeriod.class.getName();

    private static final String SERVICE_NAME = "getCustomTimePeriodList";
    private static final String SERVICE_TYPE = null;

    private static final String NONE = "NONE";

    private boolean prev2;
    private boolean prev1;
    private boolean current;
    private boolean next1;
    private boolean next2;

    private String periodTypeId;
    private String periodElapsed;
    private String showPeriods;
    private int scrollInt;
    private Timestamp estimatedStartDate;
    private Timestamp estimatedCompletionDate;
    private Timestamp searchDate;
    
    List<GenericValue> resultList = new ArrayList<GenericValue>();
    
    /**
     * Constructor
     */
    public CustomTimePeriod(DispatchContext dctx, Map<String, Object> context) {
        super(dctx, context, new JobLogger(MODULE), SERVICE_NAME, SERVICE_TYPE, MODULE);
        periodTypeId = (String)context.get(WTI.periodTypeId.name());
        periodElapsed = (String)context.get(WTI.periodElapsed.name());
        showPeriods = (String)context.get(WTI.showPeriods.name());
        scrollInt = (Integer)context.get(WTI.scrollInt.name());
        estimatedStartDate = (Timestamp)context.get(WTI.estimatedStartDate.name());
        estimatedCompletionDate = (Timestamp)context.get(WTI.estimatedCompletionDate.name());
        searchDate = (Timestamp)context.get(WTI.searchDate.name());
    }

    /**
     * getCustomTimePeriodList
     */
    public static Map<String, Object> getCustomTimePeriodList(DispatchContext dctx, Map<String, Object> context) {
        CustomTimePeriod obj = new CustomTimePeriod(dctx, context);
        obj.mainLoop();
        return obj.getResult();
    }

    /**
     * Return CustomTimePeriod List
     * @param indicator
     * @return
     * @throws GeneralException 
     */
    public void mainLoop() {
        try {
            GenericValue master = getMasterCustomTimePeriod();
            List<GenericValue> lista = getCustomTimePeriodList();
            if (NONE.equals(showPeriods)) {
                setResultList(lista);
            } else {
                ShowPeriodsEnum showPeriodsEnum = ShowPeriodsEnum.valueOf(showPeriods);
                prev2 = showPeriodsEnum.isPrev2();
                prev1 = showPeriodsEnum.isPrev1();
                current = showPeriodsEnum.current();
                next1 = showPeriodsEnum.isNext1();
                next2 = showPeriodsEnum.isNext2();
                
                
                Integer currentIndex = getCurrentIndex(master, lista);
                if (currentIndex == null) {
                	currentIndex = 0;
                	master = lista.get(0);
                }
                setResultList(addCustomTimePeriodsToResult(currentIndex, lista));
            }
            
            getResult().put(WTI.customTimePeriodList.name(), getResultList());
            getResult().put(WTI.firstCustomTimePeriodId.name(), lista.get(0).getString(WTI.customTimePeriodId.name()));
            getResult().put(WTI.lastCustomTimePeriodId.name(), lista.get(lista.size() - 1).getString(WTI.customTimePeriodId.name()));
            
            getResult().put(WTI.masterCustomTimePeriodId.name(), master.getString(WTI.customTimePeriodId.name()));
            getResult().put(WTI.thruDate.name(), master.getTimestamp(WTI.thruDate.name()));
            
        } catch (Exception e) {
            String msg = "Error searching customTimePeriodList: ";
            addLogError(e, msg);
            setResult(ServiceUtil.returnFailure(e.getMessage()));
        } finally {
            setResult(getResult());
        }
    }

    private List<GenericValue> addCustomTimePeriodsToResult(Integer currentIndex, List<GenericValue> lista) {
        List<GenericValue> customTimePeriodList = new ArrayList<GenericValue>();

        if (prev2) {
            customTimePeriodList = getAtIndex(customTimePeriodList, lista, currentIndex + scrollInt - 2);
        }

        if (prev1) {
            customTimePeriodList = getAtIndex(customTimePeriodList, lista, currentIndex + scrollInt - 1);
        }

        if (current) {
            customTimePeriodList = getAtIndex(customTimePeriodList, lista, currentIndex + scrollInt);
        }
        if (next1) {
            customTimePeriodList = getAtIndex(customTimePeriodList, lista, currentIndex + scrollInt + 1);
        }
        if (next2) {
            customTimePeriodList = getAtIndex(customTimePeriodList, lista, currentIndex + scrollInt + 2);
        }
        return customTimePeriodList;
    }

    private Integer getCurrentIndex(GenericValue master, List<GenericValue> lista) {
        Integer currentIndex = null;
        for (int index = 0; index < lista.size(); index++) {
            GenericValue item = lista.get(index);
            if (master.getString(WTI.customTimePeriodId.name()).equals(item.getString(WTI.customTimePeriodId.name()))) {
                currentIndex = index;
                break;
            }
        }
        return currentIndex;
    }

    private List<GenericValue> getAtIndex(List<GenericValue> result, List<GenericValue> lista, int index) {
        int size = lista.size();
        if (index < 0 || size <= index) {
            Debug.logWarning("No customTimePeriod found for periodTypeId = " + periodTypeId + " and index " + index, MODULE);
        } else {
            result.add(lista.get(index));
        }
        return result;
    }

    /**
     * Return customTimePeriod, if periodElabsed == PROJECT, the customTimePeriod is inside the date of workeffort
     * @return
     * @throws GeneralException
     */
    private List<GenericValue> getCustomTimePeriodList() throws GeneralException {
        List<GenericValue> customTimePeriodList = new ArrayList<GenericValue>();
        EntityCondition condition = EntityCondition.makeCondition(WTI.periodTypeId.name(), periodTypeId);
        List<String> orderBy = UtilMisc.toList(WTI.fromDate.name());

        List<GenericValue> lista = delegator.findList(WTI.CustomTimePeriod.name(), condition, null, orderBy, null, false);
        if (UtilValidate.isEmpty(lista)) {
            Map<String, Object> parameters = UtilMisc.toMap(WTI.periodTypeId.name(), (Object)periodTypeId);
            String logMessage = UtilProperties.getMessage("BaseErrorLabels", "NO_PERIOD_LIST", parameters, getLocale());
            throw new GeneralException(logMessage);
        }
        
        if (WTI.PROJECT.name().equals(periodElapsed)) {
            for (GenericValue customTimePeriod : lista) {
                if ((customTimePeriod.getTimestamp("thruDate").after(estimatedStartDate)
                 || customTimePeriod.getTimestamp("thruDate").equals(estimatedStartDate))
                 && ((customTimePeriod.getTimestamp("fromDate").before(estimatedCompletionDate)
                         || customTimePeriod.getTimestamp("fromDate").equals(estimatedCompletionDate)))) {
                    customTimePeriodList.add(customTimePeriod);
               }
            }
        } else {
            customTimePeriodList.addAll(lista);
        }
        
        return customTimePeriodList;

    }

    private GenericValue getMasterCustomTimePeriod() throws GeneralException {    	
        EntityCondition condition = EntityCondition.makeCondition(
        		EntityCondition.makeCondition(WTI.periodTypeId.name(), periodTypeId),
        		EntityCondition.makeCondition(WTI.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, searchDate),
        		EntityCondition.makeCondition(WTI.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, searchDate)
        		);        
        Map<String, Object> parameters = UtilMisc.toMap(WTI.periodTypeId.name(), periodTypeId, WTI.searchDate.name(), searchDate);
        String logMessage = UtilProperties.getMessage("BaseErrorLabels", "NO_CUS_TIME_PERIOD", parameters, getLocale());
        return findOne(WTI.CustomTimePeriod.name(), condition, null, logMessage);
        
    }

    /**
     * @return the resultList
     */
    public List<GenericValue> getResultList() {
        return resultList;
    }

    /**
     * @param resultList the resultList to set
     */
    public void setResultList(List<GenericValue> resultList) {
        this.resultList = resultList;
    }
}