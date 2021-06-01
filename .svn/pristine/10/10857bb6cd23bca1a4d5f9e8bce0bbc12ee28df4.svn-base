package com.mapsengineering.base.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.ofbiz.base.location.FlexibleLocation;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * Services to manage logs for java.util.logging, log4j, etc.
 * @author sivi
 *
 */
public class LoggingServices {

    private static final String MODULE = LoggingServices.class.getName();

    /**
     * Service to (re)read the current JUL configuration
     * @param ctx
     * @param context
     * @return
     */
    public static Map<String, Object> julReadConfigurationSrv(DispatchContext ctx, Map<String, Object> context) {
        try {
            julReadConfiguration();
            return ServiceUtil.returnSuccess();
        } catch (IOException e) {
            Debug.logError(e, MODULE);
            return ServiceUtil.returnError(e.toString());
        }
    }

    /**
     * Read the current JUL configuration from a file in the classpath named "base-jul.properties"
     * @throws IOException
     */
    public static void julReadConfiguration() throws IOException {
        URL url = FlexibleLocation.resolveLocation("base-jul.properties");
        InputStream is = url.openStream();
        try {
            java.util.logging.LogManager.getLogManager().readConfiguration(is);
        } finally {
            is.close();
        }
    }
}
