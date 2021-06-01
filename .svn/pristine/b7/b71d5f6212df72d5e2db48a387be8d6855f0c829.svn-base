package com.mapsengineering.base.util.importexport;

import java.io.OutputStream;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class EntityWriter implements ResourceWriter {

    public static final String DELEGATOR_NAME = "default";

    private GenericDelegator delegator = null;

    private Map<String, Object> context;

    public EntityWriter() {
        this.context = FastMap.newInstance();
    }
    
    public EntityWriter(Map<String, Object> context) {
        this.context = context;
    }
    
    @Override
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public void open(String entityName) throws GenericEntityException {
        delegator = (GenericDelegator)DelegatorFactory.getDelegator(DELEGATOR_NAME);
        delegator.removeAll(entityName);
    }

    @Override
    public void write(GenericValue gv) throws GenericEntityException {
        delegator.create(gv);
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public OutputStream getStream() {
        return null;
    }
}
