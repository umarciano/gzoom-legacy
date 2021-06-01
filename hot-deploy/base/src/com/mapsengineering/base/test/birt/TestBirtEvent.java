package com.mapsengineering.base.test.birt;


import com.mapsengineering.base.birt.event.EventHandlerFactory;
import com.mapsengineering.base.birt.event.GroovyEventHandler;
import com.mapsengineering.base.birt.event.IBirtParameterEventHandler;
import com.mapsengineering.base.birt.event.JavaEventHandler;
import com.mapsengineering.base.birt.event.ServiceEventHandler;
import com.mapsengineering.base.birt.event.datasource.DataSourceEventHandler;
import com.mapsengineering.base.birt.model.ModelParameter.Event;
import com.mapsengineering.base.test.BaseTestCase;

public class TestBirtEvent extends BaseTestCase {
	
    public void testEventHandlerFactory() {
    	
    	EventHandlerFactory eventHandlerFactory = EventHandlerFactory.getEventHandlerFactory();
    	IBirtParameterEventHandler iBirtParameterEventHandler = null;
    	
    	try{
    		iBirtParameterEventHandler = eventHandlerFactory.getEventHandler("xxxx", context);
    	}catch(Exception e)
    	{
    		
    	} 	
        assertNull(iBirtParameterEventHandler);
    } 
    
    public  void testJavaEventHandler() {
    	JavaEventHandler javaEventHandler = new JavaEventHandler();
    	boolean res = true;
    	try {
			javaEventHandler.invoke(new Event("java", "com.mapsengineering.base.services.HelpServices", "getContextHelp"), context);
		} catch (Exception e) {
			res = false;
		}
    	assertFalse(res);
    }
    
    public void testServiceEventHandler() {
    	ServiceEventHandler serviceEventHandler = new ServiceEventHandler();
    	boolean res = true;
    	
    	context.put(ServiceEventHandler.CONTEXT_DISPACHER, dispatcher);
    	try {
    		serviceEventHandler.invoke(new Event("java", "com.mapsengineering.base.services.HelpServices", "getContextHelp"), context);
		} catch (Exception e) {
			res = false;
		} 
    	assertFalse(res);
    } 
    
    public void testGroovyEventHandler()
    {
    	GroovyEventHandler groovyEventHandler = new GroovyEventHandler();
    	boolean res = true;

    	try{
    	    groovyEventHandler.init(context);
    	    groovyEventHandler.invoke(new Event("groovy", "com/mapsengineering/base/", "checkContentInParameters.groovy"), context); 
    	}catch(Exception e)
    	{
    		res = false;
    	}
    	
    	assertFalse(res);
    }
    
    public void testDataSourceEventHandler()
    {
    	DataSourceEventHandler dataSourceEventHandler = new DataSourceEventHandler();
    	boolean res = true;
    	
    	try {
			dataSourceEventHandler.beforeOpen(null, null);
		} catch (Exception e) { 
			res = false;
		}
    	
    	assertFalse(res);
    }

}
