package com.mapsengineering.workeffortext.test;


import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericEntityException;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.util.FromAndThruDatesProviderFromParams;

public class TestFromAndThruDatesProviderFromParams extends BaseTestCase {
	
    public enum E {
    	workEffortTypeId, searchDate, workEffortIdRoot
    }
    
    /**
     * test FromAndThruDatesProviderFromParams
     * @throws GeneralException 
     * @throws GenericEntityException
     */
    public void testFromAndThruDatesProviderFromParams() throws GeneralException {
    	makeTestWithoutParentPeriod();
    	makeTestWithParentPeriod();
    }
    
    /**
     * test con workEffortType senza parentPeriod
     * @throws GeneralException
     */
    private void makeTestWithoutParentPeriod() throws GeneralException {
    	context.put(E.workEffortTypeId.name(), "PPE10");
    	Map<String, Object> parameters = FastMap.newInstance();
    	parameters.put(E.searchDate.name(), "2016-01-01 00:00:00.0");
    	checkDatesParams(runFromAndThruDatesProviderFromParams(parameters, false), false);
    	checkDatesParams(runFromAndThruDatesProviderFromParams(parameters, true), false);
    }
    
    /**
     * test con workEffortType con parentPeriod
     * @throws GeneralException
     */
    private void makeTestWithParentPeriod() throws GeneralException {
    	context.put(E.workEffortTypeId.name(), "TDPFSO01");
    	context.put(E.workEffortIdRoot.name(), "W60000");
    	Map<String, Object> parameters = FastMap.newInstance();
    	parameters.put(E.searchDate.name(), "2012-03-01 00:00:00.0");
    	checkDatesParams(runFromAndThruDatesProviderFromParams(parameters, false), true);
    }
       
    /**
     * esegue il FromAndThruDatesProviderFromParams
     * @param parameters
     * @param withDefaultDate
     * @return
     * @throws GeneralException
     */
    private FromAndThruDatesProviderFromParams runFromAndThruDatesProviderFromParams(Map<String, Object> parameters, boolean withDefaultDate) throws GeneralException {
    	FromAndThruDatesProviderFromParams fromAndThruDatesProviderFromParams = new FromAndThruDatesProviderFromParams(context, parameters, delegator, withDefaultDate);
    	fromAndThruDatesProviderFromParams.run();
    	return fromAndThruDatesProviderFromParams;
    }
    
    /**
     * verifica i parametri
     * @param fromAndThruDatesProviderFromParams
     * @param parentPeriodExpected
     */
    private void checkDatesParams(FromAndThruDatesProviderFromParams fromAndThruDatesProviderFromParams, boolean parentPeriodExpected) {
    	if(parentPeriodExpected) {
    		assertTrue(fromAndThruDatesProviderFromParams.isEnableParentPeriodFilter());    		
    		assertEquals(UtilDateTime.toTimestamp(12, 31, 2012, 0, 0, 0), fromAndThruDatesProviderFromParams.getFromDate());
    		assertEquals(UtilDateTime.toTimestamp(1, 1, 2012, 0, 0, 0), fromAndThruDatesProviderFromParams.getThruDate());
    		return;
    	}
		assertFalse(fromAndThruDatesProviderFromParams.isEnableParentPeriodFilter());
    }

}
