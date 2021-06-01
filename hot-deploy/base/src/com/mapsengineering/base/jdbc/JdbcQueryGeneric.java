package com.mapsengineering.base.jdbc;

import java.io.IOException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.mapsengineering.base.util.logging.FormattedStringBuilder;

public abstract class JdbcQueryGeneric extends ResultSetGeneric {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = JdbcQueryGenericIterator.class.getName();

    private transient final JdbcQuery query;
    private transient SQLProcessor sqlP;

    protected JdbcQueryGeneric(JdbcQuery query) {
        super(null);
        this.query = query;
        sqlP = null;
    }

    public JdbcQuery getQuery() {
        return query;
    }

    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if (query != null) {
                try {
                    sqlP = query.closeSqlProcessor(sqlP);
                } catch (GenericDataSourceException e) {
                    throw new IOException("Error closing SQL Processor", e);
                }
            }
        }
    }

    @Override
    public String toString() {
        return new FormattedStringBuilder().braceOpen().nl().indent() //
                .nv("id", super.toString()).comma().space().append(query).nl() //
                .unindent().braceClose().toString();
    }

    public void executeQuery() {
        Integer rs;
        try {
            if (Debug.verboseOn()) {
                Debug.logVerbose("Executing: " + this, MODULE);
            }
            sqlP = query != null ? query.prepareStatement(sqlP) : null;
            rs = sqlP != null ? sqlP.executeUpdate() : null;
            if (rs != null) {
                sqlP.commit();
            }
        } catch (Exception e) {
            try {
                close();
            } catch (IOException e2) {
            }
            String msg = "Error executing query: " + e.getMessage();
            throw new IllegalStateException(msg, e);
        }
        init(rs);
    }
}
