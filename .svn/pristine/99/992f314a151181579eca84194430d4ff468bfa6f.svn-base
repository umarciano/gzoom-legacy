package com.mapsengineering.base.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.security.Security;
import org.ofbiz.security.authz.Authorization;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.control.LoginWorker;
import org.ofbiz.webapp.control.RequestHandler;

public class OfbizHttpContext {

    public static final String module = OfbizHttpContext.class.getName();
    public static final String RESULT_CODE_PARAM = "resultCode";

    public static final int TYPE_MATCH_NONE = -1;
    public static final int TYPE_MATCH_SIMPLE = 0;
    public static final int TYPE_MATCH_COMPLEX = 1;
    public static final int TYPE_MATCH_ARRAY = 2;

    public HttpServletRequest request;
    public HttpServletResponse response;
    public HttpSession session;
    public Delegator delegator;
    public LocalDispatcher dispatcher;
    public DispatchContext dctx;
    public Authorization authz;
    public Security security;

    public OfbizHttpContext() {
        getContext();
    }

    public OfbizHttpContext(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        getContext();
    }

    public void close() {
        releaseContext();
    }

    public boolean isComplexType(Class<?> clazz) {
        if (clazz.isPrimitive())
            return false;
        if (String.class.isAssignableFrom(clazz))
            return false;
        if (Number.class.isAssignableFrom(clazz))
            return false;
        if (Boolean.class.isAssignableFrom(clazz))
            return false;
        if (java.util.Date.class.isAssignableFrom(clazz))
            return false;
        if (java.util.Calendar.class.isAssignableFrom(clazz))
            return false;
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] listOfMap2objectArray(Class clazz, List<Map> list) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (list == null)
            return null;
        T[] result = (T[])Array.newInstance(clazz, list.size());
        for (int i = 0; i < list.size(); ++i) {
            result[i] = (T)map2object(clazz, list.get(i));
        }
        return result;
    }

    public List<Map> objectArray2listOfMap(Object[] array) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if (array == null)
            return null;
        List<Map> list = FastList.newInstance();
        for (int i = 0; i < array.length; ++i) {
            Map map = object2Map(array[i]);
            list.add(map);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public <T> T map2object(Class clazz, Map values) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        if (values == null)
            return null;
        T obj = (T)clazz.newInstance();
        setObjectFields(obj, values);
        return obj;
    }

    public Map object2Map(Object obj) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
        if (obj == null)
            return null;
        Map values = FastMap.newInstance();
        getFieldValues(obj, values);
        getBeanValues(obj, values);
        return values;
    }

    @SuppressWarnings("unchecked")
    public Object mapValue2objectValue(Object propValue, Class fieldType, int typeMatch) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        switch (typeMatch) {
        case TYPE_MATCH_COMPLEX:
            return map2object(fieldType, (Map)propValue);
        case TYPE_MATCH_ARRAY:
            return (Object[])listOfMap2objectArray(fieldType.getComponentType(), (List<Map>)propValue);
        default:
            return propValue;
        }
    }

    public int mapValue2objectValueTypeMatch(Object propValue, Class fieldType) {
        if (propValue == null) {
            if (!fieldType.isPrimitive())
                return TYPE_MATCH_COMPLEX;
            return TYPE_MATCH_NONE;
        }
        if (fieldType.isArray() && propValue instanceof List)
            return TYPE_MATCH_ARRAY;
        if (isComplexType(fieldType) && propValue instanceof Map)
            return TYPE_MATCH_COMPLEX;
        return TYPE_MATCH_SIMPLE;
    }

    public void setObjectFields(Object obj, Map values) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        for (Object entryObj : values.entrySet()) {
            Map.Entry entry = (Map.Entry)entryObj;
            String propName = (String)entry.getKey();
            Object propValue = entry.getValue();
            boolean beanSet = setBeanValue(obj, propName, propValue);
            if (!beanSet)
                setFieldValue(obj, propName, propValue);
        }
    }

    public boolean setBeanValue(Object obj, String propName, Object propValue) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Class clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        if (methods != null) {
            String setterName = "set" + StringUtils.capitalize(propName);
            for (Method method : methods) {
                if (setterName.equals(method.getName())) {
                    Class[] paramTypes = method.getParameterTypes();
                    if (paramTypes != null && paramTypes.length == 1) {
                        Class<?> paramType = paramTypes[0];
                        int typeMatch = mapValue2objectValueTypeMatch(propValue, paramType);
                        if (typeMatch != TYPE_MATCH_NONE) {
                            propValue = mapValue2objectValue(propValue, paramType, typeMatch);
                            method.invoke(obj, new Object[] { propValue });
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean setFieldValue(Object obj, String propName, Object propValue) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Class clazz = obj.getClass();
        Field field = null;
        try {
            field = clazz.getField(propName);
        } catch (SecurityException e) {
        } catch (NoSuchFieldException e) {
        }
        if (field != null) {
            Class fieldType = field.getType();
            int typeMatch = mapValue2objectValueTypeMatch(propValue, fieldType);
            if (typeMatch != TYPE_MATCH_NONE) {
                propValue = mapValue2objectValue(propValue, fieldType, typeMatch);
                field.set(obj, propValue);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void getFieldValues(Object obj, Map values) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class clazz = obj.getClass();
        for (Field field : clazz.getFields()) {
            String fieldName = field.getName();
            Object fieldValue = field.get(obj);
            Class fieldType = field.getType();
            if (fieldType.isArray()) {
                fieldValue = objectArray2listOfMap((Object[])fieldValue);
            } else if (isComplexType(fieldType)) {
                fieldValue = object2Map(fieldValue);
            }
            values.put(fieldName, fieldValue);
        }
    }

    @SuppressWarnings("unchecked")
    public void getBeanValues(Object obj, Map values) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        if (methods != null) {
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("get") && !methodName.equals("getTypeDesc") && !methodName.equals("getClass")) {
                    String propName = methodName.substring(3);
                    if (propName.length() > 0) {
                        Class[] paramTypes = method.getParameterTypes();
                        if (paramTypes == null || paramTypes.length <= 0) {
                            propName = StringUtils.uncapitalize(propName);
                            Object propValue = method.invoke(obj);
                            Class propType = method.getReturnType();
                            if (propType.isArray()) {
                                propValue = objectArray2listOfMap((Object[])propValue);
                            } else if (isComplexType(propType)) {
                                propValue = object2Map(propValue);
                            }
                            values.put(propName, propValue);
                        }
                    }
                }
            }
        }
    }

    public Map<String, Object> runSync(String serviceName, Map serviceContext) throws GenericServiceException {
        return runSync(serviceName, true, serviceContext);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> runSync(String serviceName, boolean exception, Map serviceContext) throws GenericServiceException {
        ModelService model = this.dctx.getModelService(serviceName);
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        serviceContext.put("userLogin", userLogin);
        serviceContext = model.makeValid(serviceContext, ModelService.IN_PARAM);
        Map<String, Object> result = this.dispatcher.runSync(serviceName, serviceContext);
        if (!ServiceUtil.isSuccess(result)) {
            String errMsg = ServiceUtil.getErrorMessage(result);
            if (exception)
                throw new GenericServiceException(errMsg);
            if (UtilValidate.isEmpty(result.get(RESULT_CODE_PARAM)))
                result.put(RESULT_CODE_PARAM, errMsg);
        }
        return result;
    }

    public String returnCode(Map<String, Object> serviceResult) {
        return (String)serviceResult.get(RESULT_CODE_PARAM);
    }

    protected void getContext() {
        this.session = this.request.getSession();
        ServletContext servletContext = (ServletContext)this.request.getAttribute("servletContext");
        RequestHandler requestHandler = RequestHandler.getRequestHandler(servletContext);

        // java.security.Principal userPrincipal =
        // this.request.getUserPrincipal();
        // Debug.log("*** context.getUsername=" + this.context.getUsername());
        // Debug.log("*** context.getPassword=" + this.context.getPassword());
        // Debug.log("*** request.getAuthType=" + this.request.getAuthType());
        // Debug.log("*** request.getRemoteUser=" +
        // this.request.getRemoteUser());
        // Debug.log("*** request.getUserPrincipal=" + (userPrincipal != null ?
        // userPrincipal.getName() : "null"));

        // taken from org.ofbiz.webapp.control.ControlServlet

        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        // Debug.log("Cert Chain: " +
        // request.getAttribute("javax.servlet.request.X509Certificate"),
        // module);

        // set the Entity Engine user info if we have a userLogin
        if (userLogin != null) {
            GenericDelegator.pushUserIdentifier(userLogin.getString("userLoginId"));
        }

        // Setup the CONTROL_PATH for JSP dispatching.
        String contextPath = request.getContextPath();
        if (contextPath == null || "/".equals(contextPath)) {
            contextPath = "";
        }
        request.setAttribute("_CONTROL_PATH_", contextPath + request.getServletPath());
        if (Debug.verboseOn())
            Debug.logVerbose("Control Path: " + request.getAttribute("_CONTROL_PATH_"), module);

        // for convenience, and necessity with event handlers, make security and
        // delegator available in the request:
        // try to get it from the session first so that we can have a
        // delegator/dispatcher/security for a certain user if desired
        String delegatorName = (String)this.session.getAttribute("delegatorName");
        if (UtilValidate.isNotEmpty(delegatorName)) {
            this.delegator = DelegatorFactory.getDelegator(delegatorName);
        }
        if (this.delegator == null) {
            this.delegator = (Delegator)servletContext.getAttribute("delegator");
        }
        if (this.delegator == null) {
            Debug.logError("[WebServiceContext] ERROR: delegator not found in ServletContext", module);
        } else {
            this.request.setAttribute("delegator", this.delegator);
            // always put this in the session too so that session events can use
            // the delegator
            this.session.setAttribute("delegatorName", this.delegator.getDelegatorName());
        }

        this.dispatcher = (LocalDispatcher)this.session.getAttribute("dispatcher");
        if (this.dispatcher == null) {
            this.dispatcher = (LocalDispatcher)servletContext.getAttribute("dispatcher");
        }
        if (this.dispatcher == null) {
            Debug.logError("[WebServiceContext] ERROR: dispatcher not found in ServletContext", module);
        }
        this.request.setAttribute("dispatcher", this.dispatcher);
        
        if (this.dispatcher != null)
            this.dctx = this.dispatcher.getDispatchContext();

        this.authz = (Authorization)this.session.getAttribute("authz");
        if (this.authz == null) {
            this.authz = (Authorization)servletContext.getAttribute("authz");
        }
        if (this.authz == null) {
            Debug.logError("[WebServiceContext] ERROR: authorization not found in ServletContext", module);
        }
        this.request.setAttribute("authz", this.authz); // maybe we should also
                                                        // add the value to
                                                        // 'security'

        this.security = (Security)this.session.getAttribute("security");
        if (this.security == null) {
            this.security = (Security)servletContext.getAttribute("security");
        }
        if (this.security == null) {
            Debug.logError("[WebServiceContext] ERROR: security not found in ServletContext", module);
        }
        this.request.setAttribute("security", this.security);

        this.request.setAttribute("_REQUEST_HANDLER_", requestHandler);

        // Try to login user
        LoginWorker.checkLogin(this.request, this.response);
    }

    protected void releaseContext() {
        // sanity check 2: make sure there are no user or session infos in the
        // delegator, ie clear the thread
        GenericDelegator.clearUserIdentifierStack();
    }
    
    public Delegator getDelegator() {
        return delegator;
    }

    public void setDelegator(Delegator delegator) {
        this.delegator = delegator;
    }

    public LocalDispatcher getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(LocalDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public DispatchContext getDctx() {
        return dctx;
    }

    public void setDctx(DispatchContext dctx) {
        this.dctx = dctx;
    }
}
