package com.mapsengineering.base.jdbc;

import java.io.IOException;

public class JdbcQueryWrapperIterator<T extends ResultSetWrapper> extends JdbcQueryGenericIterator<T> {

    private static final long serialVersionUID = 1L;

    private final T record;

    public JdbcQueryWrapperIterator(JdbcQuery query, T record) {
        super(query);
        this.record = record;
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

    @Override
    protected void executeQuery() {
        super.executeQuery();
        if (record != null) {
            record.setRs(getRs());
        }
    }
}
