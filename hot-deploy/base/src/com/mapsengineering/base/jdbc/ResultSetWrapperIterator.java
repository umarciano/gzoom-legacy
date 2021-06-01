package com.mapsengineering.base.jdbc;

import java.io.IOException;
import java.sql.ResultSet;

public class ResultSetWrapperIterator<T extends ResultSetWrapper> extends ResultSetGenericIterator<T> {

    private static final long serialVersionUID = 1L;

    private final T record;

    public ResultSetWrapperIterator(ResultSet rs, T record) {
        super(rs);
        this.record = record;
        initResultSetWrapperIterator();
    }

    @Override
    public void close() throws IOException {
        try {
            if (record != null) {
                record.onClose();
            }
        } finally {
            super.close();
        }
    }

    @Override
    protected void beforeNext() {
        super.beforeNext();
        if (record != null) {
            record.onNext();
        }
    }

    @Override
    protected T convert() {
        return record;
    }

    private void initResultSetWrapperIterator() {
        if (record != null) {
            record.setRs(getRs());
        }
    }

    @Override
    protected void init(ResultSet rs) {
        super.init(rs);
        initResultSetWrapperIterator();
    }
}
