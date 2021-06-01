package com.mapsengineering.base.jdbc;

import java.io.Serializable;
import java.sql.ResultSet;

public class ResultSetWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient ResultSet rs;

    protected ResultSetWrapper() {
        this(null);
    }

    public ResultSetWrapper(ResultSet rs) {
        setRs(rs);
    }

    protected ResultSet getRs() {
        return rs;
    }

    protected final void setRs(ResultSet rs) {
        this.rs = rs;
    }

    protected void onNext() {
        // do nothing
    }

    protected void onClose() {
        // do nothing
    }
}
