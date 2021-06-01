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

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilMisc;

/**
 * EventFactory - Event Handler Factory
 */
public class EventHandlerFactory {
	
	private static final Map<String, String> factoryClassMap = UtilMisc.toMap(
			"service", "com.mapsengineering.base.birt.event.ServiceEventHandler",
			"java", "com.mapsengineering.base.birt.event.JavaEventHandler",
			"groovy", "com.mapsengineering.base.birt.event.GroovyEventHandler");

    public static final String MODULE = EventHandlerFactory.class.getName();

    protected Map<String, IBirtParameterEventHandler> handlers = null;

    private static EventHandlerFactory instance = null;
    
    private EventHandlerFactory() {
        handlers = FastMap.newInstance();
    }
    
    public static EventHandlerFactory getEventHandlerFactory() {
    	if (instance == null) {
            synchronized (EventHandlerFactory.class) {
                if (instance == null) {
                    instance = new EventHandlerFactory();
                }
            }      	
    	}
    	
    	return instance;
    }

    public IBirtParameterEventHandler getEventHandler(String type, Map<String, Object> context) throws EventHandlerException {
        // attempt to get a pre-loaded handler
    	IBirtParameterEventHandler handler = handlers.get(type);

        if (handler == null) {
            synchronized (IBirtParameterEventHandler.class) {
                handler = handlers.get(type);
                if (handler == null) {
                    handler = this.loadEventHandler(type, context);
                    handlers.put(type, handler);
                }
            }           
        }
        return handler;
    }

    public void clear() {
        handlers.clear();
    }

    private IBirtParameterEventHandler loadEventHandler(String type, Map<String, Object> context) throws EventHandlerException {
    	IBirtParameterEventHandler handler = null;
        String handlerClass = factoryClassMap.get(type);
        if (handlerClass == null) {
            throw new EventHandlerException("Unknown handler type: " + type);
        }

        try {
            handler = (IBirtParameterEventHandler) ObjectType.getInstance(handlerClass);
            handler.init(context);
        } catch (NoClassDefFoundError e) {
            throw new EventHandlerException("No class def found for handler [" + handlerClass + "]", e);
        } catch (ClassNotFoundException cnf) {
            throw new EventHandlerException("Cannot load handler class [" + handlerClass + "]", cnf);
        } catch (InstantiationException ie) {
            throw new EventHandlerException("Cannot get instance of the handler [" + handlerClass + "]", ie);
        } catch (IllegalAccessException iae) {
            throw new EventHandlerException(iae.getMessage(), iae);
        }
        return handler;
    }
}
