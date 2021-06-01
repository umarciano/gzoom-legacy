package com.mapsengineering.base.standardimport.helper;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.AcctgTransInterfaceConstants;
import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.common.ImportException;
import com.mapsengineering.base.standardimport.common.ImportManagerConstants;
import com.mapsengineering.base.standardimport.common.TakeOverService;
import com.mapsengineering.base.util.JobLogLog;

/**
 * Helper CustomTimePeriod
 *
 */
public class CustomTimePeriodHelper {

    private TakeOverService takeOverService;
    private Timestamp weTransactionDate;

    /**
     * Constructor
     * @param takeOverService
     */
    public CustomTimePeriodHelper(TakeOverService takeOverService) {
        this.takeOverService = takeOverService;
    }

    /**
     * 1.13 Check weTransDate and return customTimePeriodId
     * @param periodTypeId
     * @return
     * @throws GeneralException
     */
    public String getCustomTimePeriodId(String periodTypeId) throws GeneralException {
        String customTimePeriodIdLocal = "";
        GenericValue mov = takeOverService.getExternalValue();
        Date weTransDate = mov.getTimestamp(E.refDate.name());
        EntityCondition conditionThruDate = EntityCondition.makeCondition(EntityCondition.makeCondition(E.periodTypeId.name(), EntityOperator.EQUALS, periodTypeId), EntityCondition.makeCondition(E.thruDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, weTransDate), EntityCondition.makeCondition(E.fromDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, weTransDate));
        List<GenericValue> customTimePeriodList = takeOverService.getManager().getDelegator().findList(E.CustomTimePeriod.name(), conditionThruDate, null, null, null, false);
        GenericValue customTimePeriod = EntityUtil.getFirst(customTimePeriodList);
        if (UtilValidate.isEmpty(customTimePeriod)) {           
            JobLogLog noPeriodFound = getNoPeriodFoundJobLogLog(weTransDate, mov.get(E.glAccountCode.name()));            
            throw new ImportException(takeOverService.getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), noPeriodFound);                        
        }
        weTransactionDate = customTimePeriod.getTimestamp(E.thruDate.name());
        if (!customTimePeriod.getTimestamp(E.thruDate.name()).equals(weTransDate)) {
            customTimePeriodIdLocal = getCustomTimePeriodHelp(mov, weTransDate, customTimePeriod, periodTypeId);
        } else {
            customTimePeriodIdLocal = customTimePeriod.getString(E.customTimePeriodId.name());
        }
        return customTimePeriodIdLocal;
    }

    /**
     * 
     * @param mov
     * @param weTransDate
     * @param customTimePeriod
     * @param periodTypeId
     * @return
     * @throws GeneralException
     */
    private String getCustomTimePeriodHelp(GenericValue mov, Date weTransDate, GenericValue customTimePeriod, String periodTypeId) throws GeneralException {
        String customTimePeriodIdLocal = "";
        if (AcctgTransInterfaceConstants.DATA_SOURCE_ABB.equals(mov.getString(E.dataSource.name())) || AcctgTransInterfaceConstants.DATA_SOURCE_RAW.equals(mov.getString(E.dataSource.name()))) {
        	String msg = "Change date " + weTransDate + " to customTimePeriod.thruDate " + customTimePeriod.getTimestamp(E.thruDate.name());
            takeOverService.addLogInfo(msg);
            customTimePeriodIdLocal = customTimePeriod.getString(E.customTimePeriodId.name());
        } else {
            JobLogLog thruDateError = getThruDateErrorJobLogLog(weTransDate, mov.get(E.glAccountCode.name()));            
            throw new ImportException(takeOverService.getEntityName(), mov.getString(ImportManagerConstants.RECORD_FIELD_ID), thruDateError);
        }
        return customTimePeriodIdLocal;
    }
    
    /**
     * 
     * @param weTransDate
     * @param glAccountCode
     * @return
     */
    private JobLogLog getNoPeriodFoundJobLogLog(Date weTransDate, Object glAccountCode) {
        return getJobLogLog(weTransDate, glAccountCode, "NO_PERIOD_FOUND");
    }
    
    /**
     * 
     * @param weTransDate
     * @param glAccountCode
     * @return
     */
    private JobLogLog getThruDateErrorJobLogLog(Date weTransDate, Object glAccountCode) {
        return getJobLogLog(weTransDate, glAccountCode, "PER_THRU_DATE_ERR");
    }
    
    /**
     * 
     * @param weTransDate
     * @param glAccountCode
     * @param logCode
     * @return
     */
    private JobLogLog getJobLogLog(Date weTransDate, Object glAccountCode, String logCode) {
    	String refDate = UtilDateTime.toDateString(weTransDate, takeOverService.getManager().getLocale());
    	Map<String, Object> parameters = UtilMisc.toMap(E.refDate.name(), (Object) refDate, E.accountCode.name(), glAccountCode);
        return new JobLogLog().initLogCode("StandardImportUiLabels", logCode, parameters, takeOverService.getManager().getLocale());
    }   

    /**
     * Return customTimePeriod.thruDate
     * @return
     */
    public Timestamp getWeTransactionDate() {
        return weTransactionDate;
    }

}
