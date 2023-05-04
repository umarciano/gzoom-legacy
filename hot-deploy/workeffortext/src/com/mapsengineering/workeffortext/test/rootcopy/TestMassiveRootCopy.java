package com.mapsengineering.workeffortext.test.rootcopy;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceUtil;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.E;

public class TestMassiveRootCopy extends BaseTestCase {
	public static final String MODULE = TestMassiveRootCopy.class.getName();
	
	/**
	 * costructor
	 */
	protected void setUp() throws Exception {
		super.setUp(false);
		context.put(E.workEffortRootId.name(), "WD2003");
		context.put(E.partyIdList.name(), "D0002;#D0015");
	}
	
	/**
	 * massive rootcopy
	 */
    public void testMassiveRootCopy() {
        Debug.log(getTitle());
        try {
        	Map<String, Object> result = dispatcher.runSync("massiveRootCopy", context);
            Debug.log(" - result " + result);
            assertTrue(ServiceUtil.isSuccess(result));
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }
	
    /**
     * title
     * @return
     */
    protected String getTitle() {
        return "Test Massive Root Copy for WD2003";
    }
}
