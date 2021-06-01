package com.mapsengineering.base.container;

import java.util.Arrays;
import java.util.Map;

import javolution.util.FastMap;

import org.codehaus.groovy.control.CompilationFailedException;
import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GroovyUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.base.services.LoggingServices;

public class InitializerContainer implements Container {

    public static final String MODULE = InitializerContainer.class.getName();

    private ContainerConfigHelper config;

    @Override
    public void init(String[] args, String configFile) throws ContainerException {
        Debug.log("*** init", MODULE);
        config = new ContainerConfigHelper(args != null ? Arrays.asList(args) : null, configFile, "initializer-container");
    }

    @Override
    public boolean start() throws ContainerException {
        try {
            Debug.log("*** start", MODULE);
            LoggingServices.julReadConfiguration();
            String serviceName = config.getProperty("service", null);
            if (UtilValidate.isNotEmpty(serviceName)) {
                invokeService(serviceName);
            }
            return true;
        } catch (Exception e) {
            Debug.log(e, MODULE);
            return false;
        }
    }

    @Override
    public void stop() throws ContainerException {
        Debug.log("*** stop", MODULE);
    }

    private Delegator makeDelegator() {
        return DelegatorFactory.getDelegator(config.getProperty("delegator-name", "default"));
    }

    private LocalDispatcher makeLocalDispatcher(Delegator delegator) {
        return GenericDispatcher.getLocalDispatcher(config.getProperty("disptacher-name", "initializer-disptacher"), delegator);
    }

    private void invokeService(String serviceName) throws ContainerException {
        try {
            Delegator delegator = makeDelegator();
            LocalDispatcher dispatcher = makeLocalDispatcher(delegator);
            Map<String, Object> serviceContext = FastMap.newInstance();
            serviceName = parseServiceInvocation(serviceName, serviceContext);
            ModelService serviceModel = dispatcher.getDispatchContext().getModelService(serviceName);
            if (serviceModel.auth) {
                GenericValue userLogin = delegator.findOne("UserLogin", false, "userLoginId", "system");
                serviceContext.put("userLogin", userLogin);
            }
            serviceContext = dispatcher.getDispatchContext().makeValidContext(serviceModel, "IN", serviceContext);
            Debug.logInfo("*** service " + serviceName + " " + serviceContext, MODULE);
            Map<String, Object> serviceResult = dispatcher.runSync(serviceName, serviceContext);
            String serviceMsg = ServiceUtil.getErrorMessage(serviceResult);
            if (UtilValidate.isNotEmpty(serviceMsg)) {
                Debug.logError("*** serviceResult=" + serviceResult, MODULE);
            } else {
                Debug.logInfo("*** serviceResult=" + serviceResult, MODULE);
            }
        } catch (GenericEntityException e) {
            throw new ContainerException(e);
        } catch (GenericServiceException e) {
            throw new ContainerException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private String parseServiceInvocation(String serviceName, Map<String, Object> context) {
        int paramsIndex = serviceName.indexOf('[');
        if (paramsIndex > 0) {
            String paramsString = serviceName.substring(paramsIndex);
            serviceName = serviceName.substring(0, paramsIndex);
            try {
                Map<String, Object> paramsMap = (Map<String, Object>)GroovyUtil.eval(paramsString, context);
                if (paramsMap != null) {
                    context.putAll(paramsMap);
                }
            } catch (CompilationFailedException e) {
            }
        }
        return serviceName;
    }
}
