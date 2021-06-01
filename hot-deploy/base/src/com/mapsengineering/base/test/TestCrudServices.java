package com.mapsengineering.base.test;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.ModelService;

import com.mapsengineering.base.bl.crud.OperationCrudHandler;
import com.mapsengineering.base.events.CrudEvents;

public class TestCrudServices extends GplusTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	@SuppressWarnings("unchecked")
	public void testDeafultOrchestration() {
		//------
		//CREATE (2 records, first for update last for delete)
		//-------
		String name = "CrudServices";
		Map<String, Object> params = getParamsMap("", name);
		Map<String, Object> ctx = getContext(CrudEvents.OP_CREATE, params);
		
    	Map<String, Object> res = null;

		try {
			params.put(OperationCrudHandler.AUTOMATIC_PK, "Y");
			res = dispatcher.runSync("crudServiceDefaultOrchestration", ctx);
		} catch (Exception e) {
			assertNull("Si é verificata un'eccezione durante CREATE: " + e.getMessage(), e);
			return;
		}
		assertNotNull(res);
		assertTrue("Errore esecuzione servizio crudServiceDefaultOrchestration in CREATE: " + res.get(ModelService.ERROR_MESSAGE), UtilValidate.isEmpty(res.get(ModelService.ERROR_MESSAGE)));
		
		//------
		//UPDATE
		//-------
		ctx.clear();
		params.clear();
		
		//Get values has been red before
		Map<String, String> foundValues = (Map<String, String>)res.get(OperationCrudHandler.ID);
		String workEffortId = (String) foundValues.get("workEffortId");
		
		name = "CrudServices " + new Date().toString();
		params = getParamsMap(workEffortId, name);
		
		ctx = getContext("UPDATE", params);
		
    	

		try {
			res = dispatcher.runSync("crudServiceDefaultOrchestration", ctx);
		} catch (Exception e) {
			assertNull("Si é verificata un'eccezione durante UPDATE: " + e.getMessage(), e);
			return;
		}
		//Test presenza errori
		assertNotNull(res);
		assertTrue("Errore esecuzione servizio crudServiceDefaultOrchestration in UPDATE: " + res.get(ModelService.ERROR_MESSAGE), UtilValidate.isEmpty(res.get(ModelService.ERROR_MESSAGE)));

		//------
		//DELETE
		//-------
		ctx.clear();
		params.clear();
		
		params.put("workEffortId", workEffortId);
		res = null;
		ctx = getContext("DELETE", params);

		try {
			res = dispatcher.runSync("crudServiceDefaultOrchestration", ctx);
		} catch (Exception e) {
			assertNull("Si é verificata un'eccezione durante DELETE: " + e.getMessage(), e);
			return;
		}
		//Test presenza errori
		assertNotNull(res);
		assertTrue("Errore esecuzione servizio crudServiceDefaultOrchestration in DELETE: " + res.get(ModelService.ERROR_MESSAGE), UtilValidate.isEmpty(res.get(ModelService.ERROR_MESSAGE)));
		
	}
	
	private Map<String, Object> getContext(String operation, Map<String, Object> params) {
		Map<String, Object> ctx =  FastMap.newInstance();
		ctx.put("entityName", "WorkEffort");
    	ctx.put("operation", operation);
    	ctx.put("parameters", params);
    	ctx.put("locale", Locale.ITALIAN);
    	ctx.put("context", this.context);
    	ctx.put("userLogin", this.context.get("userLogin"));
        
		return ctx;
	}

	protected Map<String, Object> getParamsMap(String workEffortId, String name) {
		Map<String, Object> params = FastMap.newInstance();
		
		params.put("workEffortId", workEffortId);
		params.put("workEffortName", name);
		
		params.put("description", "Test create");
		params.put("comments", "Test create");
		params.put("datetime", "12/12/2009 23:23:12");
		params.put("date", "12/12/2009");
		params.put("time", "23:23:12");
		params.put("numeric", "123456789");
		params.put("datefrom", "12/12/2007 12:12:43");
		params.put("datethru", "12/12/2008 12:12:43");
		
		return params;
	}
}
