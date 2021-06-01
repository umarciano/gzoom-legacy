package com.mapsengineering.workeffortext.scorecard;

import java.util.Date;
import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.services.ServiceLogger;
import com.mapsengineering.base.util.JobLoggedService;
import com.mapsengineering.base.util.JobLogger;

/**
 * Create condition for read acctgTrans and acctgTransEntry
 *
 */
public class ReadkpiConditionCreator implements JobLoggedService {

    public static final String MODULE = ReadkpiConditionCreator.class.getName();

    private Delegator delegator;
    
    private JobLogger jLogger;
    
    private Date thruDate; 
    private String workEffortMeasureId;
    private Date fromDate;
    private String gPeriodicalAbsEnumId;
    private String sourceReferenceId;
    private String accountCode;
    private String gTargetPeriodEnumId;
    private String organizationId;
    
        
    /**
     * Constructor
     * @param delegator
     * @param thruDate
     * @param workEffortMeasureId
     * @param fromDate
     * @param gPeriodicalAbsEnumId
     * @param sourceReferenceId
     * @param accountCode
     */
    public ReadkpiConditionCreator(Delegator delegator, Date thruDate, String workEffortMeasureId, Date fromDate, String gPeriodicalAbsEnumId, String gTargetPeriodEnumId, String sourceReferenceId, String accountCode, String organizationId) {
		this.delegator = delegator;
		this.jLogger = new JobLogger(MODULE);
		this.thruDate = thruDate;
		this.workEffortMeasureId = workEffortMeasureId;
		this.fromDate = fromDate;
		this.gPeriodicalAbsEnumId = gPeriodicalAbsEnumId;
		this.gTargetPeriodEnumId = gTargetPeriodEnumId;
		this.sourceReferenceId = sourceReferenceId;
		this.accountCode = accountCode;
		this.organizationId = organizationId;
	}



	/**
	 * Create condition for specific periodicalAbsoluteEnumId
	 * @param maxLimitExcellent
	 * @param maxLimitMax
	 * @param dateBudget
	 * @param dateParentBudget
	 * @param maxLimitMin
	 * @param maxActual
	 * @param maxActualPy
	 * @param limitMax
	 * @param target
	 * @param limitMin
	 * @param performance
	 * @return
	 */
    public EntityCondition createReadKpiCondition(Date maxLimitExcellent, Date maxLimitMax, Date dateBudget, Date dateParentBudget, Date maxLimitMin, Date maxActual, Date maxActualPy, String limitExcellent, String limitMax, String target, String limitMin, String performance) {
        List<EntityCondition> conditionList = new FastList<EntityCondition>();

        conditionList.add(EntityCondition.makeCondition(E.weTransMeasureId.name(), workEffortMeasureId));
        conditionList.add(EntityCondition.makeCondition(E.weTransWorkEffortSnapShotId.name(), null));
        conditionList.add(EntityCondition.makeCondition(E.organizationPartyId.name(), organizationId));
        List<String> glFiscalTypeList = null;
        try {
            glFiscalTypeList = EntityUtil.getFieldListFromEntityList(delegator.findList("GlFiscalType", EntityCondition.makeCondition("glFiscalTypeEnumId", EntityOperator.IN, UtilMisc.toList("GLFISCTYPE_ACTUAL", "GLFISCTYPE_TARGET", "GLFISCTYPE_ACTUAL_PY")), null, null, null, true), E.glFiscalTypeId.name(), true);
        } catch (Exception e) {
            jLogger.addMessage(ServiceLogger.makeLogError("It is not possible found GlFiscalType with value Actual or Target.", "027", sourceReferenceId, accountCode, null));
        }
        conditionList.add(EntityCondition.makeCondition(E.weTransTypeValueId.name(), EntityOperator.IN, glFiscalTypeList));
        

        // Situazione del periodo
        EntityExpr c0 = null;
        if ("PRDABS_PERIODICAL".equals(gPeriodicalAbsEnumId) || UtilValidate.isEmpty(gPeriodicalAbsEnumId)) {

            EntityExpr c01 = EntityCondition.makeCondition(E.weTransDate.name(), EntityOperator.GREATER_THAN_EQUAL_TO, fromDate);
            EntityExpr c02 = EntityCondition.makeCondition(E.weTransDate.name(), EntityOperator.LESS_THAN_EQUAL_TO, thruDate);

            c0 = EntityCondition.makeCondition(c01, EntityOperator.AND, c02);
            if (UtilValidate.isEmpty(gPeriodicalAbsEnumId)) {
                EntityExpr c03 = EntityCondition.makeCondition(E.periodicalAbsoluteEnumId.name(), "PRDABS_PERIODICAL");
                c0 = EntityCondition.makeCondition(c0, EntityOperator.AND, c03);
            }
        }

        // Situazione alla data
        EntityExpr c1 = null;
        if ("PRDABS_ABSOLUTE".equals(gPeriodicalAbsEnumId) || UtilValidate.isEmpty(gPeriodicalAbsEnumId)) {       	
        	EntityExpr c11 = makeCond(maxLimitExcellent, limitExcellent);
        	EntityExpr c12 = makeCond(maxLimitMax, limitMax);
        	EntityExpr c13 = makeCond(dateBudget, target);
        	EntityExpr c14 = makeCond(maxLimitMin, limitMin);
        	EntityExpr c15 = makeCond(maxActual, performance);
        	EntityExpr c16 = makeCond(maxActualPy, "ACTUAL_PY");
        	
        	EntityExpr e1 = EntityCondition.makeCondition(EntityCondition.makeCondition(c11, EntityOperator.OR , c12), EntityOperator.OR, c13);
        	c1 = EntityCondition.makeCondition(EntityCondition.makeCondition(c14, EntityOperator.OR , c15), EntityOperator.OR, c16);
        	c1 = EntityCondition.makeCondition(c1, EntityOperator.OR, e1);
            if (UtilValidate.isEmpty(gPeriodicalAbsEnumId)) {
            	EntityCondition c17 = EntityCondition.makeCondition(E.periodicalAbsoluteEnumId.name(), "PRDABS_ABSOLUTE");
                c1 = EntityCondition.makeCondition(c1, EntityOperator.AND, c17);
            }
        }

        // Situazione totale
        EntityExpr c2 = null;
        if ("PRDABS_ALL".equals(gPeriodicalAbsEnumId) || UtilValidate.isEmpty(gPeriodicalAbsEnumId)) {
            if (TargetPeriod.isTargetParent(gTargetPeriodEnumId)) {
                EntityExpr c21 = makeCond(thruDate, target);
                EntityExpr c22 = makeCond(dateParentBudget, target);
                
                List<String> partialGlFiscalTypeList = FastList.newInstance(); 
                partialGlFiscalTypeList.addAll(glFiscalTypeList);
                partialGlFiscalTypeList.remove(target);
                EntityCondition c23 = EntityCondition.makeCondition(
                        EntityCondition.makeCondition(E.weTransTypeValueId.name(), EntityOperator.IN, partialGlFiscalTypeList), 
                        EntityCondition.makeCondition(E.weTransDate.name(), thruDate));
                
                c2 = EntityCondition.makeCondition(EntityCondition.makeCondition(c21, EntityOperator.OR , c22), EntityOperator.OR, c23);
            } else {
                // GN-270
                c2 = EntityCondition.makeCondition(E.weTransDate.name(), thruDate);

                if (UtilValidate.isEmpty(gPeriodicalAbsEnumId)) {
                    EntityExpr c21 = EntityCondition.makeCondition(E.periodicalAbsoluteEnumId.name(), "PRDABS_ALL");
                    c2 = EntityCondition.makeCondition(c2, EntityOperator.AND, c21);
                }
            }
        }

        // Collego c0 c1 e c2 in OR
        EntityExpr orCond = null;
        if (UtilValidate.isNotEmpty(c0) || UtilValidate.isNotEmpty(c1) || UtilValidate.isNotEmpty(c2)) {
            orCond = c0;
            if (UtilValidate.isNotEmpty(c1)) {
                if (UtilValidate.isNotEmpty(orCond)) {
                    orCond = EntityCondition.makeCondition(orCond, EntityOperator.OR, c1);
                } else {
                    orCond = c1;
                }
            }
            if (UtilValidate.isNotEmpty(c2)) {
                if (UtilValidate.isNotEmpty(orCond)) {
                    orCond = EntityCondition.makeCondition(orCond, EntityOperator.OR, c2);
                } else {
                    orCond = c2;
                }
            }
        }
        if (UtilValidate.isNotEmpty(orCond)) {
            conditionList.add(orCond);
        }

        return EntityCondition.makeCondition(conditionList);
    }
    
    /**
     * crea le conditions
     * @param weTransDate
     * @param weTransTypeValueId
     * @return
     */
    private EntityExpr makeCond(Date weTransDate, String weTransTypeValueId) {
    	EntityExpr weTransDateCondition = EntityCondition.makeCondition(E.weTransDate.name(), weTransDate);
    	EntityExpr weTransTypeValueCondition = EntityCondition.makeCondition(E.weTransTypeValueId.name(), weTransTypeValueId);
        
        return EntityCondition.makeCondition(weTransDateCondition, EntityOperator.AND, weTransTypeValueCondition);
    }

    @Override
    public JobLogger getJobLogger() {
        return jLogger;
    }
}
