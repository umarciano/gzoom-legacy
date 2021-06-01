package com.mapsengineering.base.birt.event;

import java.util.Map;

import com.mapsengineering.base.birt.model.ModelParameter.Event;

public interface IBirtParameterEventHandler {
	/**
	 * @param context
	 * @throws EventHandlerException
	 */
	void init(Map<String, Object> context) throws EventHandlerException;
	
	/**
	 * @param event
	 * @param context
	 * @throws EventHandlerException
	 */
	void invoke(Event event, Map<String, Object> context) throws EventHandlerException;
}
