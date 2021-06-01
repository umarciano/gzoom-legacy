package com.mapsengineering.base.util.entity;

import java.io.Serializable;
import java.util.Iterator;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityListIterator;

/**
 * Typed Iterator wrapper for OFBiz EntityListIterator.<br/>
 * Close the iterator after the last record.
 * @author sivi
 *
 * @param <DST>
 */
public abstract class EntityListConvertIterator<DST> implements Iterator<DST>, Serializable {

    private static final String MODULE = EntityListConvertIterator.class.getName();

    private static final long serialVersionUID = 1L;

    private EntityListIterator it;
    private GenericValue next;

    /**
     * Wrap an OFBiz EntityListIterator
     * @param it
     */
    public EntityListConvertIterator(EntityListIterator it) {
        setEntityListIterator(it);
    }

    protected EntityListConvertIterator() {
    }

    protected EntityListIterator getEntityListIterator() {
        return it;
    }

    protected void setEntityListIterator(EntityListIterator it) {
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        next = it != null ? it.next() : null;
        if (next == null) {
            closeIterator();
            return false;
        }
        return true;
    }

    @Override
    public DST next() {
        try {
            return convert(next);
        } catch (Exception ex) {
            closeIterator();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove not supported");
    }

    protected void closeIterator() {
        if (it != null) {
            try {
                it.close();
            } catch (GenericEntityException e) {
                Debug.logError(e, MODULE);
            }
            it = null;
        }
    }

    protected abstract DST convert(GenericValue src) throws Exception;
}
