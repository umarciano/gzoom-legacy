package com.mapsengineering.base.util.entity;

import java.util.List;
import java.util.Set;

import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;

import com.mapsengineering.base.util.OfbizServiceContext;

/**
 * Typed Iterator wrapper for a GenericValueBaseWrapper.<br/>
 * Require an OfbizServiceContext.
 * @author sivi
 *
 * @param <T>
 */
public class GenericValueWrapperIterator<T extends GenericValueBaseWrapper> extends EntityListConvertIterator<T> {

    private static final long serialVersionUID = 1L;

    private final OfbizServiceContext ctx;
    private final GenericValueBaseWrapper gvw;
    private final EntityCondition whereEntityCondition;
    private final EntityCondition havingEntityCondition;
    private final Set<String> fieldsToSelect;
    private final List<String> orderBy;
    private final EntityFindOptions findOptions;

    /**
     * Create a new iterator and call the find on OFBiz delegator
     * @param gvw instance of a GenericValueBaseWrapper to wrap each item returned
     * @param whereEntityCondition
     * @param havingEntityCondition
     * @param fieldsToSelect
     * @param orderBy
     * @param findOptions
     * @throws GenericEntityException
     */
    public GenericValueWrapperIterator(GenericValueBaseWrapper gvw, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
        this.ctx = OfbizServiceContext.get();
        this.gvw = gvw;
        this.whereEntityCondition = whereEntityCondition;
        this.havingEntityCondition = havingEntityCondition;
        this.fieldsToSelect = fieldsToSelect;
        this.orderBy = orderBy;
        this.findOptions = findOptions;
        find();
    }

    protected void find() throws GenericEntityException {
        EntityListIterator it = ctx.getDelegator().find(gvw.getEntityName(), whereEntityCondition, havingEntityCondition, fieldsToSelect, orderBy, findOptions);
        setEntityListIterator(it);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T convert(GenericValue src) throws Exception {
        gvw.setGenericValue(src);
        return (T)gvw;
    }
}
