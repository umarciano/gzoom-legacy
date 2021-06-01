package com.mapsengineering.base.test;

import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.events.CrudEvents;
import com.mapsengineering.base.services.CrudServices;

public class TestCrudServicesFkException extends GplusTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    @SuppressWarnings("unchecked")
    public void testCrudServiceFkValidationNotComplete() {
        Map<String, Object> params = getParamsMap();
        Map<String, Object> ctx = getContext(params);

        Map<String, Object> res = null;

        try {
            res = CrudServices.crudServiceFkValidation(dispatcher.getDispatchContext(), ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertFalse(ServiceUtil.isSuccess(res));
    }

    @SuppressWarnings("unchecked")
    public void testCrudServiceFkValidationDontExists() {
        Map<String, Object> params = getParamsMap();
        Map<String, Object> ctx = getContext(params);
        params.put("orgUnitRoleTypeId", "AMM");
        Map<String, Object> res = null;

        try {
            res = CrudServices.crudServiceFkValidation(dispatcher.getDispatchContext(), ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertFalse(ServiceUtil.isSuccess(res));
    }

    private Map<String, Object> getContext(Map<String, Object> params) {
        Map<String, Object> ctx = FastMap.newInstance();
        ctx.put("entityName", "WorkEffort");
        ctx.put("operation", CrudEvents.OP_UPDATE);
        ctx.put("parameters", params);
        ctx.put("locale", Locale.ITALIAN);
        ctx.put("context", this.context);
        return ctx;
    }

    protected Map<String, Object> getParamsMap() {
        Map<String, Object> params = FastMap.newInstance();
        params.put("workEffortId", "W10000");
        params.put("orgUnitId", "RCP10193");
        return params;
    }
}
