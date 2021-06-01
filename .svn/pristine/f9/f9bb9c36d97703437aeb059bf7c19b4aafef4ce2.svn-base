package com.mapsengineering.base.find;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

/**
 * Generic Service for EntityCondition
 *
 */
public class BaseFindServices {
    public static final String MODULE = BaseFindServices.class.getName();

    private Delegator delegator;
    private LocalDispatcher dispatcher;

    /**
     * Constructor
     * @param delegator
     */
    public BaseFindServices(Delegator delegator) {
        this.delegator = delegator;
    }
    
    /**
     * Constructor
     * @param delegator
     */
    public BaseFindServices(Delegator delegator, LocalDispatcher dispatcher) {
        this.delegator = delegator;
        this.dispatcher = dispatcher;
    }

    /**
     * 
     * @return
     */
    public Delegator getDelegator() {
        return delegator;
    }
    
    /**
     * Throw exception if empty value
     * @param value
     * @param exception
     * @throws GeneralException
     */
    protected void throwNoFound(GenericValue value, String exception) throws GeneralException {
        if (UtilValidate.isEmpty(value)) {
            throw new GeneralException(exception);
        }
    }

    /**
     * Throw exception if more than one value
     * @param size
     * @param throwErrorForMore
     * @param exception
     * @throws GeneralException
     */
    protected void throwFoundMore(int size, boolean throwErrorForMore, String exception) throws GeneralException {
        if (size > 1 && throwErrorForMore) {
            throw new GeneralException(exception);
        }
    }

    /**
     * @return the dispatcher
     */
    public LocalDispatcher getDispatcher() {
        return dispatcher;
    }
}
