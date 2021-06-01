package com.mapsengineering.base.jdbc;

import java.sql.ResultSet;

public class JdbcQueryIterator extends JdbcQueryGenericIterator<ResultSet> {

    private static final long serialVersionUID = 1L;

    protected JdbcQueryIterator(JdbcQuery query) {
        super(query);
    }

    @Override
    protected ResultSet convert() {
        return getRs();
    }
}
