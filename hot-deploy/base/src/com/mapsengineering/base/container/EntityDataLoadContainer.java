package com.mapsengineering.base.container;

import java.util.Arrays;
import java.util.List;

import org.ofbiz.base.container.Container;
import org.ofbiz.base.container.ContainerException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.service.ServiceDispatcher;

public class EntityDataLoadContainer implements Container {

    public static final String MODULE = EntityDataLoadContainer.class.getName();

    private ContainerConfigHelper config;

    @Override
    public void init(String[] args, String configFile) throws ContainerException {
        Debug.logInfo("Init data load container", MODULE);

        // disable job scheduler, JMS listener and startup services
//        ServiceDispatcher.enableJM(false);
//        ServiceDispatcher.enableJMS(false);
//        ServiceDispatcher.enableSvcs(false);

        List<String> argsList = null;
        if(args != null){
        	argsList = Arrays.asList(args);
        }
        config = new ContainerConfigHelper(argsList, configFile, "dataload-container");
    }

    @Override
    public boolean start() throws ContainerException {
        Debug.logInfo("Start data load container", MODULE);
        if ("true".equals(config.getProperty("runInstallCustomSeedInitial", null))) {
            runInstallCustomSeedInitial();
        } else if ("true".equals(config.getProperty("runInstallCustomSeed", null))) {
            runInstallCustomSeed();
        }
        return true;
    }

    @Override
    public void stop() throws ContainerException {
        Debug.logInfo("Stop data load container", MODULE);
    }

    protected void runInstallCustomSeed() throws ContainerException {
        Debug.logInfo("*** runInstallCustomSeed ***", MODULE);
        runInstallReaders("gplus");
    }

    protected void runInstallCustomSeedInitial() throws ContainerException {
        Debug.logInfo("*** runInstallCustomSeedInitial ***", MODULE);
        runInstallReaders("gplus,gplus-initial");
    }

    protected void runInstallReaders(String dataReaders) throws ContainerException {
        String[] args = new String[] { "readers=" + dataReaders };
        runInstallContainer(args);
    }

    protected void runInstallContainer(String[] args) throws ContainerException {
        org.ofbiz.entityext.data.EntityDataLoadContainer container = new org.ofbiz.entityext.data.EntityDataLoadContainer();
        container.init(args, config.getConfigFile());
        container.start();
        container.stop();
    }
}
