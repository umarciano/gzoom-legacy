package com.mapsengineering.workeffortext.scorecard;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLogger;

public class TransactionAPFinder {
	public static final String MODULE = TransactionAPFinder.class.getName();
	
	private Delegator delegator;
	private String workEffortMeasureId;
	private String organizationId;
	private JobLogger jLogger;
	private Date thruDate;
	private String sourceReferenceId;
	private String accountCode;
	
	/**
	 * constructor
	 * @param delegator
	 * @param workEffortMeasureId
	 * @param organizationId
	 * @param thruDate
	 * @param sourceReferenceId
	 * @param accountCode
	 */
	public TransactionAPFinder(Delegator delegator, String workEffortMeasureId, String organizationId, Date thruDate, String sourceReferenceId, String accountCode) {
		this.delegator = delegator;
		this.jLogger = new JobLogger(MODULE);
		this.workEffortMeasureId = workEffortMeasureId;
		this.organizationId = organizationId;
		this.thruDate = thruDate;
		this.sourceReferenceId = sourceReferenceId;
		this.accountCode = accountCode;
	}
	
	/**
	 * ritorna la lista di movimenti anno prec
	 * @return
	 * @throws GenericEntityException
	 */
	public List<GenericValue> getTransactionAPList() throws GenericEntityException {
        List<EntityCondition> conditionList = makeConditions();
        conditionList.add(EntityCondition.makeCondition(E.weTransDate.name(), getAPDate()));
        makeLog(conditionList, "088");
        return delegator.findList(E.WorkEffortTransactionIndicatorView.name(), EntityCondition.makeCondition(conditionList), null, null, null, false);
	}
	
	/**
	 * ritorna la lista movimenti anno att
	 * @return
	 * @throws GenericEntityException
	 */
	public List<GenericValue> getTransactionList() throws GenericEntityException {
        List<EntityCondition> conditionList = makeConditions();
        conditionList.add(EntityCondition.makeCondition(E.weTransDate.name(), new Timestamp(thruDate.getTime())));
        makeLog(conditionList, "089");
        return delegator.findList(E.WorkEffortTransactionIndicatorView.name(), EntityCondition.makeCondition(conditionList), null, null, null, false);
	}
	
	/**
	 * costruisce condizioni di ricerca
	 * @return
	 */
	private List<EntityCondition> makeConditions() {
        List<EntityCondition> conditionList = new FastList<EntityCondition>();

        conditionList.add(EntityCondition.makeCondition(E.weTransMeasureId.name(), workEffortMeasureId));
        conditionList.add(EntityCondition.makeCondition(E.weTransWorkEffortSnapShotId.name(), null));
        conditionList.add(EntityCondition.makeCondition(E.organizationPartyId.name(), organizationId));
        
        List<String> glFiscalTypeList = null;
        try {
            glFiscalTypeList = EntityUtil.getFieldListFromEntityList(delegator.findList("GlFiscalType", EntityCondition.makeCondition("glFiscalTypeEnumId", "GLFISCTYPE_ACTUAL"), null, null, null, true), E.glFiscalTypeId.name(), true);
        } catch (Exception e) {
        }
        conditionList.add(EntityCondition.makeCondition(E.weTransTypeValueId.name(), EntityOperator.IN, glFiscalTypeList));
        
        return conditionList;
	}
	
	/**
	 * costruisce log
	 * @param conditionList
	 * @param code
	 */
	private void makeLog(List<EntityCondition> conditionList, String code) {
		jLogger.addMessage(ServiceLogger.makeLogInfo("Searching WorkEffortTransactionIndicatorView with condition " + conditionList, code, sourceReferenceId, accountCode, null));
	}
	
	/**
	 * ritorna anno precedente
	 * @return
	 */
	private Timestamp getAPDate() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(thruDate);
		
		Calendar calAP = new GregorianCalendar();
		calAP.set(Calendar.YEAR, cal.get(Calendar.YEAR) -1);
		calAP.set(Calendar.MONTH, 11);
		calAP.set(Calendar.DAY_OF_MONTH, 31);
		calAP.set(Calendar.HOUR_OF_DAY, 0);
		calAP.set(Calendar.MINUTE, 0);
		calAP.set(Calendar.SECOND, 0);
		calAP.set(Calendar.MILLISECOND, 0);
		return new Timestamp(calAP.getTime().getTime());
	}
}
