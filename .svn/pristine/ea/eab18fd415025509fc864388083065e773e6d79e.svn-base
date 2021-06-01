package com.mapsengineering.workeffortext.scorecard;

import org.ofbiz.entity.Delegator;

import com.mapsengineering.base.util.JobLoggedService;

/**
 * Base Converter, only delegator and JobLogger
 *
 */
public abstract class BaseConverter implements JobLoggedService {

    public static final String MODULE = BaseConverter.class.getName();
    
    private Delegator delegator;
    
    /**
     * Constructor
     * @param delegator
     */
    public BaseConverter(Delegator delegator) {
        this.delegator = delegator;
    }
    
    /**
     * Return delegator
     * @return
     */
    public Delegator getDelegator() {
        return delegator;
    }
}
