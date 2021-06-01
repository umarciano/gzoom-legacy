/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package com.mapsengineering.base.birt.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelParam;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;

import com.mapsengineering.base.birt.model.ModelParameter.Event;

/**
 * ServiceEventHandler - Service Event Handler
 */
public class ServiceEventHandler implements IBirtParameterEventHandler {

	public static final String MODULE = ServiceEventHandler.class.getName();

	public static final String SYNC = "sync";
	public static final String ASYNC = "async";
	
	//TODO spostare?
	public static final String CONTEXT_DISPACHER = "dispatcher";
	public static final String CONTEXT_USERLOGIN = "userLogin";
	public static final String CONTEXT_LOCALE = "locale";
	public static final String CONTEXT_TIMEZONE = "timeZone";
	public static final String SEPARATOR = "\n";
	

	/**
	 * @see org.ofbiz.webapp.event.EventHandler#invoke(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void invoke(Event event, Map<String, Object> context) throws EventHandlerException {
		// make sure we have a valid reference to the Service Engine
		LocalDispatcher dispatcher = UtilGenerics.cast(context.get(ServiceEventHandler.CONTEXT_DISPACHER));
		if (dispatcher == null || dispatcher.getDispatchContext() == null) {
			throw new EventHandlerException("The local service dispatcher is null");
		}
		DispatchContext dctx = dispatcher.getDispatchContext();

		// get the details for the service(s) to call
		String mode = SYNC;
		String serviceName = null;

		if ( !UtilValidate.isEmpty(event.path)) {
			mode = event.path;
		}

		// make sure we have a defined service to call
		serviceName = event.invoke;
		if (serviceName == null) {
			throw new EventHandlerException("Service name (eventMethod) cannot be null");
		}
		if (Debug.verboseOn()) {
		    Debug.logVerbose("[Set mode/service]: " + mode + "/" + serviceName, MODULE);
		}

		// some needed info for when running the service
		Locale locale = UtilGenerics.cast(context.get(ServiceEventHandler.CONTEXT_LOCALE));
		TimeZone timeZone = UtilGenerics.cast(context.get(ServiceEventHandler.CONTEXT_TIMEZONE));
		GenericValue userLogin = UtilGenerics.cast(context.get(ServiceEventHandler.CONTEXT_USERLOGIN));

		// get the service model to generate context
		ModelService model = null;

		try {
			model = dctx.getModelService(serviceName);
		} catch (GenericServiceException e) {
			throw new EventHandlerException("Problems getting the service model", e);
		}

		if (model == null) {
			throw new EventHandlerException("Problems getting the service model");
		}

		if (Debug.verboseOn()) {
		    Debug.logVerbose("[Processing]: SERVICE Event", MODULE);
		    Debug.logVerbose("[Using delegator]: " + dispatcher.getDelegator().getDelegatorName(), MODULE);
		}

		// we have a service and the model; build the context
		Map<String, Object> serviceContext = FastMap.newInstance();
		for (ModelParam modelParam: model.getInModelParamList()) {
			
			buildContext(modelParam.name,modelParam, context, serviceContext );
			
		}

		// get only the parameters for this service - converted to proper type
		// TODO: pass in a list for error messages, like could not convert type or not a proper X, return immediately with messages if there are any
		List<Object> errorMessages = FastList.newInstance();
		serviceContext = model.makeValid(serviceContext, ModelService.IN_PARAM, true, errorMessages, timeZone, locale);
		if (errorMessages.size() > 0) {
			generateEventHandlerException(errorMessages);
		}

		// include the UserLogin value object
		if (userLogin != null) {
			serviceContext.put(ServiceEventHandler.CONTEXT_USERLOGIN, userLogin);
		}

		// include the Locale object
		if (locale != null) {
			serviceContext.put(ServiceEventHandler.CONTEXT_LOCALE, locale);
		}

		// include the TimeZone object
		if (timeZone != null) {
			serviceContext.put(ServiceEventHandler.CONTEXT_TIMEZONE, timeZone);
		}

		// invoke the service
		Map<String, Object> result = dispatch(mode, dispatcher, serviceName, serviceContext);
		
		manageResult(result);
	}
	
	@Override
	public void init(Map<String, Object> context) throws EventHandlerException {
	}
	
	/*
	 * Context Builder by Request
	 * */
	protected void buildContext(String paramName, ModelParam modelParam, Map<String, Object> context, Map<String, Object> serviceContext ){
		String name = paramName;

		// don't include userLogin, locale, timeZone that's taken care of below
		if (ServiceEventHandler.CONTEXT_USERLOGIN.equals(name) || ServiceEventHandler.CONTEXT_LOCALE.equals(name) || ServiceEventHandler.CONTEXT_TIMEZONE.equals(name)) {
		    return;
		}

		Object value = null;
		if (UtilValidate.isNotEmpty(modelParam.stringMapPrefix)) {
			Map<String, Object> paramMap = UtilHttp.makeParamMapWithPrefix(context, null, modelParam.stringMapPrefix, null);
			value = paramMap;
			if (Debug.verboseOn()) {
			    Debug.log("Set [" + modelParam.name + "]: " + paramMap, MODULE);
			}
		} 
		else {
			// next check attributes; do this before parameters so that attribute which can be changed by code can override parameters which can't
			value = context.get(name);

			// no field found
			if (value == null) {
				//still null, give up for this one
				return;
			}

			if (value instanceof String && ((String) value).length() == 0) {
				// interpreting empty fields as null values for each in back end handling...
				value = null;
			}
		}
		// set even if null so that values will get nulled in the db later on
		serviceContext.put(name, value);
		
	}
	
	/*
	 * generate exception by error list
	 * */
	
	protected void generateEventHandlerException(List<Object> errorMessages) throws EventHandlerException{
		
		// uh-oh, had some problems...
		StringBuffer buff = new StringBuffer();
		for (Object errorMessage : errorMessages) {
			if (buff.length() > 0) {
				buff.append(ServiceEventHandler.SEPARATOR);
			} 
			buff.append(errorMessage);
		}
		throw new EventHandlerException(buff.toString());
		
	}

	
	/*
	 * Dispatch Sync or Async service 
	 * */
	protected Map<String, Object> dispatch( String mode, LocalDispatcher dispatcher, String serviceName, Map<String, Object> serviceContext ) throws EventHandlerException{
		
		Map<String, Object> result = null;
		try {
			if (ASYNC.equalsIgnoreCase(mode)) {
				dispatcher.runAsync(serviceName, serviceContext);
			} else {
				result = dispatcher.runSync(serviceName, serviceContext);
			}
			
		} catch (ServiceAuthException e) {
			// not logging since the service engine already did
			throw new EventHandlerException(e.getNonNestedMessage(), e);
		} catch (ServiceValidationException e) {
			
			if (e.getMessageList() != null) {
				List<Object> objectList = new ArrayList<Object>(e.getMessageList());
				generateEventHandlerException(objectList);
			} else {
				StringBuffer buff = new StringBuffer();
				buff.append(e.getNonNestedMessage());
				throw new EventHandlerException(buff.toString(), e);
			}
			
		} catch (GenericServiceException e) {
			Debug.logError(e, "Service invocation error", MODULE);
			throw new EventHandlerException("Service invocation error", e.getNested());
		}
		
		return result;
	}
	
	/*
	 * Manage success or error result
	 * */
	protected void manageResult(Map<String, Object> result) throws EventHandlerException{
		
		if (result != null && !ServiceUtil.isSuccess(result)) {

			List<String> errorMessageList = UtilGenerics.cast(result.get(ModelService.ERROR_MESSAGE_LIST));
			
			if (errorMessageList != null) {
				List<Object> objectList = new ArrayList<Object>(errorMessageList);
				generateEventHandlerException(objectList);
			} else {
				String errorMessage = UtilGenerics.cast(result.get(ModelService.ERROR_MESSAGE));
				if (errorMessage != null){
					StringBuffer buff = new StringBuffer();
					buff.append(errorMessage);
					throw new EventHandlerException(buff.toString());
				}
			}
		}

		if (Debug.verboseOn()) {
		    Debug.logVerbose("[Event Return]", MODULE);
		}
		
	}
}
