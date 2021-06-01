package com.mapsengineering.base.test;

import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.GenericServiceException;

import com.mapsengineering.base.bl.HelpWorker;


public class TestHelpServices extends GplusTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetContextHelp() {

		//First i create entity.field
		setContextWithoutFormName("date", false);
		
		Map<String, Object> res = testAssertNotNull();
		assertTrue(UtilValidate.isNotEmpty(res.get("helpText")));
		
		//then search
		setContextWithoutFormName("date", true);
        
		res = testAssertNotNull();
        assertTrue(((String)res.get("helpText")).equalsIgnoreCase(HelpWorker.GPLUS_HELP_PLACEHOLDER));
		
		//then create precise
        setContextWithoutFormName("date", true);
        
        res = testAssertNotNull();
        assertTrue(UtilValidate.isNotEmpty(res.get("helpText")));

		//then search precise
        setContextWithoutFormName("date", true);
        
        res = testAssertNotNull();
        assertTrue(((String)res.get("helpText")).equalsIgnoreCase(HelpWorker.GPLUS_HELP_PLACEHOLDER));
		
		//then search less
		context.put("entityName", "TestingGplus");
		context.put("fieldName", "date");
		
		res = testAssertNotNull();
        assertTrue(((String)res.get("helpText")).equalsIgnoreCase(HelpWorker.GPLUS_HELP_PLACEHOLDER));
		
	}
	
	private Map<String, Object> testAssertNotNull() {
	    Map<String, Object> res = null;
        try {
            res = dispatcher.runSync("getContextHelp", context);
        } catch (GenericServiceException e) {
            Debug.logError("Errore lancio getContextHelp: " + e.getMessage(), "TestHelpService");
        }
        assertNotNull(res);
        return res;
    }

    protected void setContextWithoutFormName(String fieldName, boolean accurate){
	    context.remove("formName");
        context.put("entityName", "TestingGplus");
        context.put("fieldName", fieldName);
        context.put("accurate", accurate);
	}
    
    protected void setContextWithFormName(String fieldName, boolean accurate){
        context.put("formName", "testForm");
        context.put("entityName", "TestingGplus");
        context.put("fieldName", fieldName);
        context.put("accurate", accurate);
    }
	
}
