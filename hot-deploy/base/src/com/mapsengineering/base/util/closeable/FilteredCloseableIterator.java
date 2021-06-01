package com.mapsengineering.base.util.closeable;

import java.io.IOException;
import java.io.Serializable;

public abstract class FilteredCloseableIterator<T> implements CloseableIterator<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private final CloseableIterator<T> it;
    private boolean hasNextCalled;
    private boolean hasNext;
    private T nextRecord;

    public FilteredCloseableIterator(CloseableIterator<T> it) {
        // Debug.log("*** " + CommonUtil.id(this) + " init it=" + CommonUtil.id(it));
        this.it = it;
    }

    @Override
    public boolean hasNext() {
        if (!hasNextCalled) {
            // Debug.log("*** " + CommonUtil.id(this) + " hasNext loop");
            T record = null;
            while (it.hasNext()) {
                // Debug.log("*** " + CommonUtil.id(this) + " it.next");
                record = it.next();
                if (accept(record)) {
                    // Debug.log("*** " + CommonUtil.id(this) + " accepted");
                    break;
                } else {
                    // Debug.log("*** " + CommonUtil.id(this) + " skipped");
                    record = null;
                }
            }
            hasNextCalled = true;
            hasNext = record != null;
            nextRecord = record;
        }
        // Debug.log("*** " + CommonUtil.id(this) + " hasNext=" + hasNext);
        return hasNext;
    }

    @Override
    public T next() {
        // Debug.log("*** " + CommonUtil.id(this) + " next nextRecord=" + CommonUtil.id(nextRecord));
        hasNextCalled = false;
        return nextRecord;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    @Override
    public void close() throws IOException {
        try {
            // Debug.log("*** " + CommonUtil.id(this) + " close");
            hasNextCalled = false;
        } finally {
            it.close();
            // Debug.log("*** " + CommonUtil.id(this) + " closed");
        }
    }

    /**
     * Check if current record is acceptable.
     * @param record
     * @return true if record accepted
     */
    protected abstract boolean accept(T record);
}
