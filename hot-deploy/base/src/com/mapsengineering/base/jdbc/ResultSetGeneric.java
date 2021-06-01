package com.mapsengineering.base.jdbc;

import java.io.IOException;
import java.io.Serializable;

import com.mapsengineering.base.util.closeable.Closeables;

public abstract class ResultSetGeneric implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private transient Integer rs;

    protected ResultSetGeneric(Integer rs) {
        initIntegerGeneric(rs);
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    };

    public Integer getRs() {
        return rs;
    }

    private void initIntegerGeneric(Integer rs) {
        // If resultset is null then act as an empty iterator.
        // Debug.log("*** " + CommonUtil.id(this) + " init rs=" + CommonUtil.id(rs));
        this.rs = rs;
        if (this.rs != null) {
            Closeables.add(this);
        }
    }

    protected void init(Integer rs) {
        initIntegerGeneric(rs);
    }

    protected void beforeNext() {
        // empty
    }

    protected void afterLast() throws IOException {
        close();
    }

    public void close() throws IOException {
        Closeables.remove(this);
    }
}
