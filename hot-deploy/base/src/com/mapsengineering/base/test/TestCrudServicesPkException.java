package com.mapsengineering.base.test;

import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.bl.crud.AbstractCrudHandler;
import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.CrudServices;
import com.mapsengineering.base.util.MessageUtil;

public class TestCrudServicesPkException extends GplusTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    @SuppressWarnings("unchecked")
    public void testCrudServicePkValidationPrimaryKey() {
        Map<String, Object> params = getParamsMap(null);
        Map<String, Object> ctx = getContext(CrudEvents.OP_CREATE, params);

        Map<String, Object> res = null;

        try {
            res = CrudServices.crudServicePkValidation(dispatcher.getDispatchContext(), ctx);
        } catch (Exception e) {
            e.getMessage();
        }
        assertFalse(ServiceUtil.isSuccess(res));
        assertEquals(MessageUtil.getErrorMessage("PrimaryKeyNeeded", Locale.ITALIAN, UtilMisc.toList("noteId")), ServiceUtil.getErrorMessage(res));
    }

    @SuppressWarnings("unchecked")
    public void testCrudServicePkValidationDuplicatePrimaryKey() {
        Map<String, Object> params = getParamsMap(null);
        Map<String, Object> ctx = getContext(CrudEvents.OP_CREATE, params);
        params.put("noteId", "E1212112825");
        Map<String, Object> res = null;

        try {
            res = CrudServices.crudServicePkValidation(dispatcher.getDispatchContext(), ctx);
        } catch (Exception e) {
            e.getMessage();
        }
        assertFalse(ServiceUtil.isSuccess(res));
        assertEquals(MessageUtil.getErrorMessage("DuplicatePrimaryKey", Locale.ITALIAN), ServiceUtil.getErrorMessage(res));
    
        // TODO con il throw_error
        Map<String, Object> params2 = getParamsMap(null);
        Map<String, Object> ctx2 = getContext(CrudEvents.OP_CREATE, params2);
        params2.put("noteId", "E1212112825");
        params2.put(AbstractCrudHandler.THROW_ERROR, false);
        
        try {
            res = CrudServices.crudServicePkValidation(dispatcher.getDispatchContext(), ctx2);
        } catch (Exception e) {
            e.getMessage();
        }
        Debug.log(" res " + res);
        assertTrue(ServiceUtil.isSuccess(res));
        String failMessage = (String)res.get(ModelService.FAIL_MESSAGE); // TODO sostituire failMessage in giro
        assertEquals(MessageUtil.getErrorMessage("DuplicatePrimaryKey", Locale.ITALIAN), failMessage);
    }

    private Map<String, Object> getContext(String operation, Map<String, Object> params) {
        Map<String, Object> ctx = FastMap.newInstance();
        ctx.put("entityName", "WorkEffortNote");
        ctx.put("operation", operation);
        ctx.put("parameters", params);
        ctx.put("locale", Locale.ITALIAN);
        ctx.put("context", this.context);

        return ctx;
    }

    protected Map<String, Object> getParamsMap(String testingId) {
        Map<String, Object> params = FastMap.newInstance();
        params.put("workEffortId", "W10000");
        return params;
    }
}
