package com.mapsengineering.gzoomjbpm;

import java.util.Properties;

import com.mapsengineering.gzoomjbpm.util.UtilProperties;

public class JbpmFactory {
	public static final String FILENAME = "jbpm_factory.properties";
	public static final String INSTANCEPROPERTY = "instance";
	public static final String ENABLED = "enabled";
	
	private static JbpmStub jbpmStub;

	protected JbpmFactory(Properties properties) throws Exception {
		if(properties == null) {
			properties = UtilProperties.getProperties(FILENAME);
		}
		
		if(properties != null) {
		   String enabled = properties.getProperty(ENABLED, "N");
		   if(!enabled.equals("N")) {
			   if(jbpmStub == null) {
				   String instanceClass = properties.getProperty(INSTANCEPROPERTY);
				    if(instanceClass != null) {
						Class clazz = Class.forName(instanceClass);
						jbpmStub = (JbpmStub)clazz.newInstance();
					}
			   }
		   }
		   else {
			   jbpmStub = null;
		   }
		}
	}
	
	public static JbpmStub instance() throws Exception{
		return instance(null);
	}
	
	public static JbpmStub instance(Properties properties) throws Exception{
		new JbpmFactory(properties);
		return jbpmStub;
	}
}
