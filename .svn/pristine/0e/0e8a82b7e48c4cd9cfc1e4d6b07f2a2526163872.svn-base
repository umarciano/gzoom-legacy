package com.mapsengineering.base.util;

import java.util.List;

import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

/**
 * Generic Service
 *
 */
public class FindUtilService {

    public static final String COLON_SEP = " : ";
    public static final String MSG_SUCCESSFULLY_CREATED = " successfully created.";
    public static final String MSG_SUCCESSFULLY_UPDATE = " successfully update.";
    public static final String MSG_ERROR_CREATE = "Error in create ";
    public static final String MSG_ERROR_UPDATE = "Error in update ";
    public static final String MSG_SUCCESSFULLY_DELETE = " successfully deleted.";
    public static final String MSG_ERROR_DELETE = "Error in delete ";
    public static final String MSG_SUCCESSFULLY_ADD = " successfully add to ";
    public static final String MSG_ERROR_ADD = "Error in add ";

    public static final String MSG_PROBLEM_CREATE = "Problem with creation ";
    public static final String MSG_PROBLEM_UPDATE = "Problem with updating ";

    public static final String MSG_NOT_NULL = " must be not empty";
    public static final String MSG_BOTH_NOT_NULL = " must be not empty both";


    /**
     * Constructor
     */
    private FindUtilService() {
    }

    /** Ritorna un solo elemento */
    public static GenericValue findOne(Delegator delegator, String entityNameToFind, EntityCondition cond, String foundMore, String noFound, boolean throwIfNone, boolean throwIfMany) throws GeneralException {
        List<GenericValue> parents = delegator.findList(entityNameToFind, cond, null, null, null, false);
        if (UtilValidate.isNotEmpty(parents) && parents.size() > 1 && throwIfMany) {
            throw new GeneralException(foundMore);
        } else if (UtilValidate.isEmpty(parents) && throwIfNone) {
            throw new GeneralException(noFound);
        }

        return EntityUtil.getFirst(parents);
    }
    
    /** Ritorna un solo elemento, rilancia eccezione negli altri casi*/
    public static GenericValue findOne(Delegator delegator, String entityNameToFind, EntityCondition cond, String foundMore, String noFound) throws GeneralException {
        return findOne(delegator, entityNameToFind, cond, foundMore, noFound, true, true);
    }

}
