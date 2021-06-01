package com.mapsengineering.accountingext.test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.accountingext.services.E;
import com.mapsengineering.accountingext.services.IndicatorCalcServices;
import com.mapsengineering.accountingext.services.InputAndDetailValue;
import com.mapsengineering.accountingext.util.PartyRelationshipCustomMethodCalculatorUtil;
import com.mapsengineering.base.services.GenericService;
import com.mapsengineering.base.test.BaseTestCase;

/**
 * Test Indicator Calc Service
 *
 */
public class BaseTestIndicatorCalcServices extends BaseTestCase {

    public static final String ACTUAL = "ACTUAL";
    public static final String BUDGET = "BUDGET";
    private static final String MODULE = BaseTestIndicatorCalcServices.class.getName();
    protected static final Integer YEAR_2012 = 2012;
    private static final Integer YEAR_2013 = 2013;
    private static final Integer YEAR_2014 = 2014;
    protected static final Integer MOUNTH_DIC = 12;
    protected static final Integer MOUNTH_AUG = 8;
    protected static final Integer DAY_31 = 31;
    private Timestamp refDate2014 = new Timestamp(UtilDateTime.toDate(MOUNTH_DIC, DAY_31, YEAR_2014, 0, 0, 0).getTime());
    private Timestamp refDate2013 = new Timestamp(UtilDateTime.toDate(MOUNTH_DIC, DAY_31, YEAR_2013, 0, 0, 0).getTime());
    private Timestamp refDate2012 = new Timestamp(UtilDateTime.toDate(MOUNTH_DIC, DAY_31, YEAR_2012, 0, 0, 0).getTime());
    
    protected void setUp() throws Exception {
        super.setUp();
        context.put(GenericService.ORGANIZATION_PARTY_ID, COMPANY);
    }

    protected GenericValue getFirstMovement(String workEffortId, String glAccountId, String glFiscalTypeId, Timestamp refDate, int expectedMovement) throws GeneralException {
        List<EntityCondition> condList = FastList.newInstance();
        if (UtilValidate.isNotEmpty(workEffortId)) {
            condList.add(EntityCondition.makeCondition(E.workEffortId.name(), workEffortId));
        }
        if (UtilValidate.isNotEmpty(glAccountId)) {
            condList.add(EntityCondition.makeCondition(E.entryGlAccountId.name(), glAccountId));
        }
        condList.add(EntityCondition.makeCondition(E.transactionDate.name(), refDate));
        condList.add(EntityCondition.makeCondition(E.glFiscalTypeId.name(), glFiscalTypeId));

        List<GenericValue> valueList = delegator.findList(E.AcctgTransAndEntriesView.name(), EntityCondition.makeCondition(condList), null, null, null, false);
        Debug.log(" Found " + valueList.size() + " " + E.AcctgTransAndEntriesView.name() + " with condition " + EntityCondition.makeCondition(condList) + " : " + valueList);
        assertEquals(expectedMovement, valueList.size());
        return EntityUtil.getFirst(valueList);

    }

    protected Map<String, Object> runTest(String glAccountId, Long prioCalc, String glFiscalTypeIdInput, String glFiscalTypeIdOutput, Timestamp refDate, Integer expectedMovement, Double entryAmount) {
        context.put(E.thruDate.name(), refDate);
        context.put(E.glFiscalTypeIdInput.name(), glFiscalTypeIdInput);
        context.put(E.glFiscalTypeIdOutput.name(), glFiscalTypeIdOutput);
        if (UtilValidate.isNotEmpty(glAccountId)) {
            context.put(E.glAccountId.name(), glAccountId);
        }
        if (UtilValidate.isNotEmpty(prioCalc)) {
            context.put(E.prioCalc.name(), prioCalc);
        }
        Map<String, Object> result = IndicatorCalcServices.indicatorCalcImpl(dispatcher.getDispatchContext(), context);
        Debug.log(" - indicatorCalc result = " + result);
        if (UtilValidate.isNotEmpty(expectedMovement)) {
            try {
                GenericValue mov = getFirstMovement(null, glAccountId, glFiscalTypeIdOutput, refDate, expectedMovement);
                if(UtilValidate.isNotEmpty(entryAmount)) {
                    assertEquals(entryAmount, mov.getDouble(E.entryAmount.name()));
                }
            } catch (GeneralException e) {
                Debug.logError(e, MODULE);
            }
        }
        return result;
    }
    
    protected Map<String, Object> runTest(String glAccountId, Long prioCalc, String glFiscalTypeIdInput, String glFiscalTypeIdOutput, Timestamp refDate, Integer expectedMovement) {
        return runTest(glAccountId, prioCalc, glFiscalTypeIdInput, glFiscalTypeIdOutput, refDate, expectedMovement, null);
    }
    
    private void updateCustomMethod(EntityCondition condition, String oldString, String newString) {
        try {
            List<GenericValue> valueList = delegator.findList(E.GlAccount.name(), condition, null, null, null, false);
            for(GenericValue glAccount : valueList) {
                String calcCustomMethodId = glAccount.getString(E.calcCustomMethodId.name());
                calcCustomMethodId = calcCustomMethodId.replace(oldString, newString);
                glAccount.set(E.calcCustomMethodId.name(), calcCustomMethodId);
                glAccount.store();
            }
        
        } catch (GenericEntityException e) {
            Debug.logError(e, MODULE);
        }
        
    }
    
    private void updateCustomMethodPeuo(String oldString, String newString) {
        EntityCondition cond = EntityCondition.makeCondition(E.calcCustomMethodId.name(), EntityOperator.LIKE, "PEUO_%");
        updateCustomMethod(cond, oldString, newString);
    }
    
    protected void updateCustomMethodAggregAnno() {
        EntityCondition cond = EntityCondition.makeCondition(E.calcCustomMethodId.name(),  InputAndDetailValue.AGGREG);
        updateCustomMethod(cond, InputAndDetailValue.AGGREG, InputAndDetailValue.AGGREG_ANNO);
    }
    
    protected void updateCustomMethodAggregMax() {
        EntityCondition cond = EntityCondition.makeCondition(E.calcCustomMethodId.name(),  InputAndDetailValue.AGGREG_ANNO);
        updateCustomMethod(cond, InputAndDetailValue.AGGREG_ANNO, InputAndDetailValue.AGGREG_MAX);
    }
    
    protected void updateCustomMethodAggregMaxAnno() {
        EntityCondition cond = EntityCondition.makeCondition(E.calcCustomMethodId.name(),  InputAndDetailValue.AGGREG_MAX);
        updateCustomMethod(cond, InputAndDetailValue.AGGREG_MAX, InputAndDetailValue.AGGREG_MAX_ANNO);
    }
    
    protected void updateCustomMethodAggreg() {
        EntityCondition cond = EntityCondition.makeCondition(E.calcCustomMethodId.name(), InputAndDetailValue.AGGREG_MAX_ANNO);
        updateCustomMethod(cond, InputAndDetailValue.AGGREG_MAX_ANNO, InputAndDetailValue.AGGREG);
    }
      
    protected void updateCustomMethodFromAggregToAnnoSum() {
        EntityCondition cond = EntityCondition.makeCondition(E.calcCustomMethodId.name(),  InputAndDetailValue.AGGREG);
        updateCustomMethod(cond, InputAndDetailValue.AGGREG, InputAndDetailValue.ANNO_SUM);
    }
    
    protected void updateCustomMethodFromAnnoSumToAggreg() {
        EntityCondition cond = EntityCondition.makeCondition(E.calcCustomMethodId.name(),  InputAndDetailValue.ANNO_SUM);
        updateCustomMethod(cond, InputAndDetailValue.ANNO_SUM, InputAndDetailValue.AGGREG);
    }   
       
    protected void updateCustomMethodFromAggregToTsweOreOre() {
        EntityCondition cond = EntityCondition.makeCondition(E.calcCustomMethodId.name(),  InputAndDetailValue.AGGREG);
        updateCustomMethod(cond, InputAndDetailValue.AGGREG, InputAndDetailValue.TSWE_ORE_ORE);
    }
    
    protected void updateCustomMethodTsweOreOreToAggreg() {
        EntityCondition cond = EntityCondition.makeCondition(E.calcCustomMethodId.name(),  InputAndDetailValue.TSWE_ORE_ORE);
        updateCustomMethod(cond, InputAndDetailValue.TSWE_ORE_ORE, InputAndDetailValue.AGGREG);
    }   
    
    protected void updateCustomMethodFteAA() {
        updateCustomMethodPeuo(PartyRelationshipCustomMethodCalculatorUtil.FTEMM, PartyRelationshipCustomMethodCalculatorUtil.FTEAA);
        updateCustomMethodPeuo(PartyRelationshipCustomMethodCalculatorUtil.TESTE, PartyRelationshipCustomMethodCalculatorUtil.FTEAA);
    }
    
    protected void updateCustomMethodFteMM() {
        updateCustomMethodPeuo(PartyRelationshipCustomMethodCalculatorUtil.TESTE, PartyRelationshipCustomMethodCalculatorUtil.FTEMM);
        updateCustomMethodPeuo(PartyRelationshipCustomMethodCalculatorUtil.FTEAA, PartyRelationshipCustomMethodCalculatorUtil.FTEMM);
    }
    
    protected void updateCustomMethodTeste() {
        updateCustomMethodPeuo(PartyRelationshipCustomMethodCalculatorUtil.FTEMM, PartyRelationshipCustomMethodCalculatorUtil.TESTE);
        updateCustomMethodPeuo(PartyRelationshipCustomMethodCalculatorUtil.FTEAA, PartyRelationshipCustomMethodCalculatorUtil.TESTE);
    }

    /**
     * @return the refDate2013
     */
    public Timestamp getRefDate2013() {
        return refDate2013;
    }


    /**
     * @return the refDate2012
     */
    public Timestamp getRefDate2012() {
        return refDate2012;
    }


    /**
     * @return the refDate2014
     */
    public Timestamp getRefDate2014() {
        return refDate2014;
    }

}
