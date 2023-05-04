package com.mapsengineering.accountingext.test;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.accountingext.services.E;
import com.mapsengineering.accountingext.services.IndicatorCalcObiettivoServices;

/**
 * Test indicator Calc for workEffort
 *
 */
public class TestIndicatorCalcObiettivoServices extends BaseTestIndicatorCalcServices {

    public static final String WORKEFFORT_ID_ROOT = "W50000";
    public static final String WORKEFFORT_ID_CHILD = "W10017";
    public static final String PEG12 = "PEG12";
    public static final String WEFLD_ELIN = "WEFLD_ELIN";
    private static final String MODULE = TestIndicatorCalcObiettivoServices.class.getName();

    /**
     * Test for workEffortRoot
     */
    public void testIndicator1() {
    	addParamRootExecute();
        Map<String, Object> result = runTestObj(WORKEFFORT_ID_ROOT, ACTUAL, ACTUAL, getRefDate2013(), 13);
        assertEquals(ServiceUtil.returnSuccess().get(ModelService.RESPONSE_MESSAGE), result.get(ModelService.RESPONSE_MESSAGE));

    }

    /**
     * Test for workEffortRoot
     */
    public void testIndicator2() {
        Map<String, Object> result = runTestObj(WORKEFFORT_ID_ROOT, BUDGET, BUDGET, getRefDate2013(), 12);
        assertEquals(ServiceUtil.returnSuccess().get(ModelService.RESPONSE_MESSAGE), result.get(ModelService.RESPONSE_MESSAGE));
    }

    /**
     * Test for workEffortChild, WorkEffort is not a root, calculate Score Card for parent...
     */
    public void testIndicatorChild() {
        Map<String, Object> result = runTestObj(WORKEFFORT_ID_CHILD, BUDGET, BUDGET, getRefDate2012(), 6);
        assertEquals(ServiceUtil.returnSuccess().get(ModelService.RESPONSE_MESSAGE), result.get(ModelService.RESPONSE_MESSAGE));
    }
    
    /**
     * imposta il param rootExecute
     */
    private void addParamRootExecute() {
    	try {
			GenericValue workEffortTypeContent = delegator.findOne(E.WorkEffortTypeContent.name(), UtilMisc.toMap(E.workEffortTypeId.name(), PEG12, E.contentId.name(), WEFLD_ELIN), false);
			if (UtilValidate.isNotEmpty(workEffortTypeContent)) {
				workEffortTypeContent.set(E.params.name(), "rootExecute=\"Y\";");
				delegator.store(workEffortTypeContent);
			}
		} catch (GeneralException e) {
			Debug.logError(e, MODULE);
		}
    }

    protected Map<String, Object> runTestObj(String workEffortId, String glFiscalTypeIdInput, String glFiscalTypeIdOutput, Timestamp refDate, int expectedMovement) {
        context.put(E.thruDate.name(), refDate);
        context.put(E.glFiscalTypeIdInput.name(), glFiscalTypeIdInput);
        context.put(E.glFiscalTypeIdOutput.name(), glFiscalTypeIdOutput);
        context.put(E.workEffortId.name(), workEffortId);
        Map<String, Object> result = IndicatorCalcObiettivoServices.indicatorCalcObiettivoImpl(dispatcher.getDispatchContext(), context);
        Debug.log(" - indicatorCalcObiettivo result = " + result);
        try {
            getFirstMovement(workEffortId, null, glFiscalTypeIdOutput, refDate, expectedMovement);
        } catch (GeneralException e) {
            Debug.logError(e, MODULE);
        }
        return result;
    }
}
