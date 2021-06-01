package com.mapsengineering.base.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.util.closeable.Closeables;

/**
 * Java Map wrapper of an OFBiz context for services.
 * @author sivi
 *
 */
public class OfbizServiceContext implements Closeable, Map<String, Object> {

    /**
     * Common OFBiz names
     * @author sivi
     *
     */
    public enum E {
        userLogin, locale, timeZone
    }

    private DispatchContext dctx;
    private Map<String, Object> context;
    private Map<String, Object> result;

    /**
     * Default constructor
     */
    public OfbizServiceContext() {
        this(null, null, null);
    }

    /**
     * Initialize context with default result map.
     * @param dctx DispatchContext from service definition
     * @param context Map from service definition
     */
    public OfbizServiceContext(DispatchContext dctx, Map<String, Object> context) {
        this(dctx, context, null);
    }

    /**
     * Initialize context.
     * @param dctx DispatchContext from service definition
     * @param context Map from service definition
     * @param result Map optional, defaults to success.
     */
    public OfbizServiceContext(DispatchContext dctx, Map<String, Object> context, Map<String, Object> result) {
        Closeables.add(this);
        OfbizServiceContextStack.add(this);
        initOfbizServiceContext(dctx, context, result);
    }

    protected void init(DispatchContext dctx, Map<String, Object> context, Map<String, Object> result) {
        initOfbizServiceContext(dctx, context, result);
    }

    private void initOfbizServiceContext(DispatchContext dctx, Map<String, Object> context, Map<String, Object> result) {
        this.dctx = dctx != null ? dctx : getDefaultDispatchContext();
        this.context = context != null ? context : new HashMap<String, Object>();
        this.result = result != null ? result : ServiceUtil.returnSuccess();
    }

    private DispatchContext getDefaultDispatchContext() {
        Delegator delegator = DelegatorFactory.getDelegator(null);
        LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher(delegator.getDelegatorName(), delegator);
        return dispatcher.getDispatchContext();
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    };

    /**
     * Get current thread local context
     * @return
     */
    public static OfbizServiceContext get() {
        return OfbizServiceContextStack.get();
    }

    /**
     * 
     * @return DispatchContext
     */
    public DispatchContext getDctx() {
        return dctx;
    }

    /**
     * 
     * @return result Map
     */
    public Map<String, Object> getResult() {
        return result;
    }

    /**
     * Gests valid result map for a service output.
     * @param serviceName
     * @return result Map for the service
     */
    public Map<String, Object> getResult(String serviceName) {
        try {
            return dctx.makeValidContext(serviceName, ModelService.OUT_PARAM, result);
        } catch (GenericServiceException e) {
            return result;
        }
    }

    /**
     * 
     * @return Delegator
     */
    public Delegator getDelegator() {
        return dctx.getDelegator();
    }

    /**
     * 
     * @return LocalDispatcher
     */
    public LocalDispatcher getDispatcher() {
        return dctx.getDispatcher();
    }

    /**
     * 
     * @return Security
     */
    public Security getSecurity() {
        return dctx.getSecurity();
    }

    /**
     * 
     * @return userLogin GenericValue
     */
    public GenericValue getUserLogin() {
        return (GenericValue)get(E.userLogin.name());
    }

    /**
     * 
     * @return Locale
     */
    public Locale getLocale() {
        return (Locale)get(E.locale.name());
    }

    /**
     * 
     * @return TimeZone
     */
    public TimeZone getTimeZone() {
        return (TimeZone)get(E.timeZone.name());
    }

    @Override
    public void close() throws IOException {
        try {
            OfbizServiceContextStack.remove(this);
        } finally {
            Closeables.remove(this, true);
        }
    }

    @Override
    public int size() {
        return context.size();
    }

    @Override
    public boolean isEmpty() {
        return context.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return context.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return context.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return context.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return context.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return context.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        context.putAll(m);
    }

    @Override
    public void clear() {
        context.clear();
    }

    @Override
    public Set<String> keySet() {
        return context.keySet();
    }

    @Override
    public Collection<Object> values() {
        return context.values();
    }

    @Override
    public Set<java.util.Map.Entry<String, Object>> entrySet() {
        return context.entrySet();
    }
}
