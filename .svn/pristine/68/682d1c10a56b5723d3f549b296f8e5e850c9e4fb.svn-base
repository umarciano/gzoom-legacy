package com.mapsengineering.base.jdbc;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.Collection;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.entity.GenericNotImplementedException;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.jdbc.SqlJdbcUtil;
import org.ofbiz.entity.jdbc.SqlTypes.SqlObjectHandler;

import com.mapsengineering.base.util.logging.FormattedStringBuilder;

public class JdbcParam {

    private Object value;
    private int jdbcType;

    public JdbcParam() {
        this(null, 0);
    }

    /**
     * Create parameter
     * @param value
     * @param jdbcType if 0 the type will be inferred by value
     */
    public JdbcParam(Object value, int jdbcType) {
        this.value = value;
        this.jdbcType = jdbcType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getJdbcType() {
        if (jdbcType == 0) {
            jdbcType = inferType();
        }
        return jdbcType;
    }

    public void setJdbcType(int jdbcType) {
        this.jdbcType = jdbcType;
    }

    @Override
    public String toString() {
        return new FormattedStringBuilder().braceOpen().space() //
                .nv("jdbcType", getJdbcType()) //
                .comma().space().nv("value", getValue()) //
                .space().braceClose().toString();
    }

    /**
     * @see org.ofbiz.entity.jdbc.SqlJdbcUtil#setValue
     * @param sqlP
     * @throws SQLException
     */
    public void setValueTo(SQLProcessor sqlP) throws SQLException {
        final int type = getJdbcType();
        SqlObjectHandler handler = JdbcTypeSqlObjectHandler.get(type);
        if (handler != null) {
            try {
                sqlP.setObject(handler, value);
                return;
            } catch (Exception e) {
                Debug.logInfo(e, getClass().getName());
            }
        }
        switch (type) {
        case 1:
            sqlP.setValue((String)value);
            break;
        case 2:
            sqlP.setValue((java.sql.Timestamp)value);
            break;
        case 3:
            sqlP.setValue((java.sql.Time)value);
            break;
        case 4:
            sqlP.setValue((java.sql.Date)value);
            break;
        case 5:
            sqlP.setValue((java.lang.Integer)value);
            break;
        case 6:
            sqlP.setValue((java.lang.Long)value);
            break;
        case 7:
            sqlP.setValue((java.lang.Float)value);
            break;
        case 8:
            sqlP.setValue((java.lang.Double)value);
            break;
        case 9:
            sqlP.setValue((java.math.BigDecimal)value);
            break;
        case 10:
            sqlP.setValue((java.lang.Boolean)value);
            break;
        case 11:
            sqlP.setBinaryStream(value);
            break;
        case 12:
            if (value instanceof byte[]) {
                sqlP.setBytes((byte[])value);
            } else if (value instanceof ByteBuffer) {
                sqlP.setBytes(((ByteBuffer)value).array());
            } else {
                sqlP.setValue((java.sql.Blob)value);
            }
            break;
        case 13:
            sqlP.setValue((java.sql.Clob)value);
            break;
        case 14:
            if (value != null) {
                sqlP.setValue(new java.sql.Date(((java.util.Date)value).getTime()));
            } else {
                sqlP.setValue((java.sql.Date)null);
            }
            break;
        case 15:
            sqlP.setValue(UtilGenerics.<Collection<?>> cast(value));
            break;
        default:
            throw new IllegalArgumentException("Cannot set JDBC value for type " + type);
        }
    }

    private int inferType() {
        if (value == null) {
            throw new IllegalArgumentException("Cannot infer JDBC type from null value");
        }
        String javaType = value.getClass().getName();
        try {
            return SqlJdbcUtil.getType(javaType);
        } catch (GenericNotImplementedException e) {
            throw new IllegalArgumentException("Cannot infer JDBC type from type/value: " + javaType + " / " + value, e);
        }
    }
}
