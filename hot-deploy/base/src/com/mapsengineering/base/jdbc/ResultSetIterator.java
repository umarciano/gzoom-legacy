package com.mapsengineering.base.jdbc;

import java.sql.ResultSet;

public abstract class ResultSetIterator extends ResultSetGenericIterator<ResultSet> {

    private static final long serialVersionUID = 1L;

    public ResultSetIterator(ResultSet rs) {
        super(rs);
    }

    @Override
    protected ResultSet convert() {
        return getRs();
    }
}
