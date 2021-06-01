package com.mapsengineering.base.report;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

import com.mapsengineering.base.birt.util.Utils;
import com.mapsengineering.base.util.OfbizServiceContext;

/**
 * Context for reports, may wrap context from engines like BIRT and others.
 * @author sivi
 *
 */
public class OfbizReportContext extends OfbizServiceContext {

    /**
     * Common context attribute names
     * @author sivi
     *
     */
    public enum E {
        dispatcher, birtParameters, userProfile, parameters
    }

    private final Object originalContext;
    private Map<String, Object> parameters;

    /**
     * Create a report context to wrap a generic context
     * @param originalContext generic context made by BIRT or other engines.
     */
    public OfbizReportContext(Object originalContext) {
        this.originalContext = originalContext;
        initOfbizReportContext();
    }

    /**
     * Get current thread local context
     * @return
     */
    public static OfbizReportContext get() {
        return (OfbizReportContext)OfbizServiceContext.get();
    }

    /**
     * Get the original generic context
     * @return
     */
    public Object getOriginalContext() {
        return originalContext;
    }

    /**
     * Get report parameters
     * @return
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    protected void init(DispatchContext dctx, Map<String, Object> context, Map<String, Object> result) {
        super.init(dctx, context, result);
        initOfbizReportContext();
    }

    private void initOfbizReportContext() {
        LocalDispatcher dispatcher = null;
        Map<String, Object> mapContext = UtilGenerics.toMap(originalContext);
        if (mapContext != null) {
            dispatcher = (LocalDispatcher)mapContext.get(E.dispatcher.name());
        }
        super.init(dispatcher != null ? dispatcher.getDispatchContext() : null, mapContext, null);
        if (mapContext != null) {
            initOfbizReportContextParameters(mapContext);
        }
    }

    private void initOfbizReportContextParameters(Map<String, Object> mapContext) {
        Object parametersObj = mapContext.get(E.birtParameters.name());
        if (parametersObj != null) {
            parameters = UtilGenerics.toMap(parametersObj);
        }
        if (parameters == null) {
            parameters = FastMap.newInstance();
        }
        put(E.parameters.name(), parameters);
        initExtraParameters();
    }

    private void initExtraParameters() {
        String userProfile = Utils.getUserProfile(getSecurity(), getUserLogin(), getDispatcher().getName());
        parameters.put(E.userProfile.name(), userProfile);
    }
}
