package com.mapsengineering.base.jawr.web.resource.bundle.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import net.jawr.web.resource.bundle.IOUtils;
import net.jawr.web.resource.bundle.factory.util.MultipleFileConfigSource;

import org.ofbiz.base.location.FlexibleLocation;

public class ComponentMultipleFileConfigSource extends MultipleFileConfigSource {

    public static final String MODULE = ComponentMultipleFileConfigSource.class.getName();

    protected static final String COMPONENT_PREFIX = "component:";

    /**
     * Reads a config file at the specified path. If the path starts with file:, it will
     * be read from the filesystem. Otherwise it will be loaded from the classpath.
     *
     * @param path
     * @return
     */
    protected Properties readConfigFile(String path) {
        Properties props = new Properties();
        // Load properties file
        InputStream is = null;
        try {
            URL url = FlexibleLocation.resolveLocation(path);

            // load properties into a Properties object
            props.load(url.openStream());
        }
        catch (MalformedURLException e) {
            props = super.readConfigFile(path);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("jawr configuration could not be found at "
                    + path + ". Make sure parameter is properly set "
                    + "in web.xml. ", e);
        }finally{
            IOUtils.close(is);
        }

        return props;
    }
}
