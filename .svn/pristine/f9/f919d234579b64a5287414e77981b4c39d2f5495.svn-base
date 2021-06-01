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

import java.lang.reflect.Method;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;

import com.mapsengineering.base.birt.model.ModelParameter.Event;

/**
 * JavaEventHandler - Static Method Java Event Handler
 */
public class JavaEventHandler implements IBirtParameterEventHandler {

    public static final String module = JavaEventHandler.class.getName();

    private Map<String, Class<?>> eventClassMap = FastMap.newInstance();

    /* (non-Javadoc)
     * @see com.mapsengineering.base.birt.event.IBirtParameterEventHandler#invoke(com.mapsengineering.base.birt.model.ModelParameter.Event, java.util.Map)
     */
    public void invoke(Event event, Map<String, Object> context) throws EventHandlerException {
        Class<?> eventClass = this.eventClassMap.get(event.path);

        if (eventClass == null) {
            synchronized (this) {
                eventClass = this.eventClassMap.get(event.path);
                if (eventClass == null) {
                    try {
                        ClassLoader loader = Thread.currentThread().getContextClassLoader();
                        eventClass = loader.loadClass(event.path);
                    } catch (ClassNotFoundException e) {
                        Debug.logError(e, "Error loading class with name: " + event.path + ", will not be able to run event...", module);
                    }
                    if (eventClass != null) {
                        eventClassMap.put(event.path, eventClass);
                    }
                }
            }
        }
        if (Debug.verboseOn()) Debug.logVerbose("[Set path/method]: " + event.path + " / " + event.invoke, module);

        Class<?>[] paramTypes = new Class<?>[] {Map.class};

        Debug.logVerbose("*[[Event invocation]]*", module);
        Object[] params = new Object[] {context};

        invoke(event.path, event.invoke, eventClass, paramTypes, params);
    }

    /**
     * @param eventPath
     * @param eventMethod
     * @param eventClass
     * @param paramTypes
     * @param params
     * @throws EventHandlerException
     */
    private void invoke(String eventPath, String eventMethod, Class<?> eventClass, Class<?>[] paramTypes, Object[] params) throws EventHandlerException {
        if (eventClass == null) {
            throw new EventHandlerException("Error invoking event, the class " + eventPath + " was not found");
        }
        if (eventPath == null || eventMethod == null) {
            throw new EventHandlerException("Invalid event method or path; call initialize()");
        }

        Debug.logVerbose("[Processing]: JAVA Event", module);
        try {
            Method m = eventClass.getMethod(eventMethod, paramTypes);
            String eventReturn = (String) m.invoke(null, params);

            if (Debug.verboseOn()) Debug.logVerbose("[Event Return]: " + eventReturn, module);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable t = e.getTargetException();

            if (t != null) {
                Debug.logError(t, "Problems Processing Event", module);
                throw new EventHandlerException("Problems processing event: " + t.toString(), t);
            } else {
                Debug.logError(e, "Problems Processing Event", module);
                throw new EventHandlerException("Problems processing event: " + e.toString(), e);
            }
        } catch (Exception e) {
            Debug.logError(e, "Problems Processing Event", module);
            throw new EventHandlerException("Problems processing event: " + e.toString(), e);
        }
    }
    
    /* (non-Javadoc)
     * @see com.mapsengineering.base.birt.event.IBirtParameterEventHandler#init(java.util.Map)
     */
    @Override
	public void init(Map<String, Object> context) throws EventHandlerException {
	}
}
