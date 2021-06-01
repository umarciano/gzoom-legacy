package com.mapsengineering.base.jdbc;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.ofbiz.base.util.Debug;

import com.mapsengineering.base.util.closeable.CloseableIterator;
import com.mapsengineering.base.util.closeable.Closeables;

public abstract class ResultSetGenericIterator<T> implements CloseableIterator<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private transient ResultSet rs;
    private boolean hasNextCalled;
    private boolean hasNext;

    protected ResultSetGenericIterator(ResultSet rs) {
        initResultSetGenericIterator(rs);
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    };

    public ResultSet getRs() {
        return rs;
    }

    @Override
    public boolean hasNext() {
        if (!hasNextCalled) {
            try {
                // Debug.log("*** " + CommonUtil.id(this) + " beforeNext");
                beforeNext();
                // Debug.log("*** " + CommonUtil.id(this) + " rs.next");
                hasNext = rs.next();
                // Debug.log("*** " + CommonUtil.id(this) + " rs.hasNext=" + hasNext);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
            hasNextCalled = true;
            if (!hasNext) {
                try {
                    // Debug.log("*** " + CommonUtil.id(this) + " afterLast");
                    afterLast();
                } catch (IOException e) {
                    Debug.logWarning(e, "Error closing result set after last record", getClass().getName());
                }
            }
        }
        // Debug.log("*** " + CommonUtil.id(this) + " hasNext=" + hasNext);
        return hasNext;
    }

    @Override
    public T next() {
        // Debug.log("*** " + CommonUtil.id(this) + " next");
        hasNextCalled = false;
        return convert();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("NOT IMPLEMENTED");
    }

    private void initResultSetGenericIterator(ResultSet rs) {
        // If resultset is null then act as an empty iterator.
        // Debug.log("*** " + CommonUtil.id(this) + " init rs=" + CommonUtil.id(rs));
        this.rs = rs;
        if (this.rs != null) {
            Closeables.add(this);
        }
        hasNextCalled = this.rs == null;
        hasNext = false;
    }

    protected void init(ResultSet rs) {
        initResultSetGenericIterator(rs);
    }

    /**
     * Converts current resultset position to an object
     * @return the converted object
     */
    protected abstract T convert();

    protected void beforeNext() {
        // empty
    }

    protected void afterLast() throws IOException {
        close();
    }

    @Override
    public void close() throws IOException {
        try {
            // Debug.log("*** " + CommonUtil.id(this) + " close");
            if (rs != null) {
                // Debug.log("*** " + CommonUtil.id(this) + " rs.close");
                rs.close();
                rs = null;
            }
        } catch (SQLException e) {
            throw new IOException("Error closing result set", e);
        } finally {
            Closeables.remove(this);
            // Debug.log("*** " + CommonUtil.id(this) + " closed");
        }
    }
}
