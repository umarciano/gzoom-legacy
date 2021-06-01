package com.mapsengineering.gzoomjbpm.util;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.Properties;


public class UtilProperties {

	/** Returns the specified resource/properties file
	 * @param resource The name of the resource - can be a file, class, or URL
	 * @return The properties file
	 */
	public static Properties getProperties(String resource) {
		if (resource == null || resource.length() <= 0) {
			return null;
		}
		Properties properties = null;
		try {
			URL url = UtilURL.fromResource(resource);

			if (url == null)
				return null;
			properties = getProperties(url);
		} catch (MissingResourceException e) {
		}
		if (properties == null) {
			return null;
		}
		return properties;
	}

	/** Returns the specified resource/properties file
	 * @param url The URL to the resource
	 * @return The properties file
	 */
	public static Properties getProperties(URL url) {
		if (url == null) {
			return null;
		}
		Properties properties = null;
		try {
			properties = new Properties();
			properties.load(url.openStream());
		} catch (Exception e) {
		}
		if (properties == null) {
			return null;
		}
		return properties;
	}
}
