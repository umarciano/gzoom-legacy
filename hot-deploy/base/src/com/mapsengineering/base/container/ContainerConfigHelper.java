package com.mapsengineering.base.container;

import java.util.List;
import java.util.Properties;

import org.ofbiz.base.container.ContainerConfig;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.UtilValidate;

public class ContainerConfigHelper {

    private List<String> args;
    private String configFile;
    private ContainerConfig.Container config;

    public ContainerConfigHelper(List<String> args, String configFile, String containerName) throws ContainerException {
        this.args = args;
        this.configFile = configFile;
        this.config = ContainerConfig.getContainer(containerName, configFile);
    }

    public List<String> getArgs() {
        return args;
    }

    public String getConfigFile() {
        return configFile;
    }

    public ContainerConfig.Container getConfig() {
        return config;
    }

    public String getProperty(String name, String defaultValue) {
        return getArgProperty(name, false, getSystemProperty(name, false, getConfigProperty(name, false, defaultValue)));
    }

    public String getArgProperty(String name, boolean allowEmpty, String defaultValue) {
        boolean found = false;
        String value = null;
        if (args != null) {
            for (String argument : args) {
                // arguments can prefix w/ a '-'. Just strip them off
                if (argument.startsWith("-")) {
                    int subIdx = 1;
                    if (argument.startsWith("--")) {
                        subIdx = 2;
                    }
                    argument = argument.substring(subIdx);
                }
                // parse the arguments
                String argumentName;
                String argumentVal;
                if (argument.indexOf("=") != -1) {
                    argumentName = argument.substring(0, argument.indexOf("="));
                    argumentVal = argument.substring(argument.indexOf("=") + 1);
                } else {
                    argumentName = argument;
                    argumentVal = null;
                }
                if (name.equalsIgnoreCase(argumentName)) {
                    found = true;
                    value = argumentVal;
                }
            }
        }
        if (found && (allowEmpty || UtilValidate.isNotEmpty(value))) {
            return value;
        }
        return defaultValue;
    }

    public String getSystemProperty(String name, boolean allowEmpty, String defaultValue) {
        Properties props = System.getProperties();
        if (props.containsKey(name)) {
            String value = props.getProperty(name);
            if (allowEmpty || UtilValidate.isNotEmpty(value)) {
                return value;
            }
        }
        return defaultValue;
    }

    public String getConfigProperty(String name, boolean allowEmpty, String defaultValue) {
        ContainerConfig.Container.Property prop = config.getProperty(name);
        if (prop != null) {
            if (allowEmpty || UtilValidate.isNotEmpty(prop.value)) {
                return prop.value;
            }
        }
        return defaultValue;
    }
}
