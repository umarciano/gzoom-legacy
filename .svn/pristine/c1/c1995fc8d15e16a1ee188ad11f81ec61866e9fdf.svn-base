package com.mapsengineering.base.standardimport.common;

import org.ofbiz.service.GenericServiceException;

public final class TakeOverFactory {

    public static final String MODULE = TakeOverFactory.class.getName();

    private TakeOverFactory() {
    }

    public static TakeOverService instance(String entityName) throws GenericServiceException {
        Class<?> clazz;
        try {
            clazz = Class.forName("com.mapsengineering.base.standardimport." + entityName + "TakeOverService");
            TakeOverService takeOverService = (TakeOverService)clazz.newInstance();
            takeOverService.setEntityName(entityName);
            return takeOverService;
        } catch (Exception e) {
            throw new GenericServiceException(e);
        }
    }
}
