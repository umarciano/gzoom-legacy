/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.entity.jdbc;

import java.io.IOException;
import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.conversion.ConversionException;
import org.ofbiz.base.conversion.Converter;
import org.ofbiz.base.conversion.Converters;
import org.ofbiz.base.util.Debug;

/**
 * Classes, data structures and utility methods for handling SQL data types.
 *
 */
public class SqlTypes {
    public static final String module = SqlTypes.class.getName();
    protected static final Map<Integer, Class<?>> dataTypeClassMap = createFieldClassMap();
    protected static final Map<String, SqlObjectHandlerFactory> sqlObjectHandlerMap = createSqlObjectHandlerMap();

    protected static Map<Integer, Class<?>> createFieldClassMap() {
        Map<Integer, Class<?>> result = FastMap.newInstance();
        result.put(Types.ARRAY, java.sql.Array.class);
        result.put(Types.BIGINT, Long.class);
        result.put(Types.BINARY, byte[].class);
        result.put(Types.BIT, Boolean.class);
        result.put(Types.BLOB, java.sql.Blob.class);
        result.put(Types.BOOLEAN, Boolean.class);
        result.put(Types.CHAR, String.class);
        result.put(Types.CLOB, java.sql.Clob.class);
        result.put(Types.DATE, java.sql.Date.class);
        result.put(Types.DECIMAL, java.math.BigDecimal.class);
        result.put(Types.DOUBLE, Double.class);
        result.put(Types.FLOAT, Double.class);
        result.put(Types.INTEGER, Integer.class);
        result.put(Types.LONGVARBINARY, byte[].class);
        result.put(Types.LONGVARCHAR, String.class);
        result.put(Types.NUMERIC, java.math.BigDecimal.class);
        result.put(Types.REAL, Float.class);
        result.put(Types.REF, java.sql.Ref.class);
        result.put(Types.SMALLINT, Short.class);
        result.put(Types.STRUCT, java.sql.Struct.class);
        result.put(Types.TIME, java.sql.Time.class);
        result.put(Types.TIMESTAMP, java.sql.Timestamp.class);
        result.put(Types.VARBINARY, byte[].class);
        result.put(Types.VARCHAR, String.class);
        return result;
    }

    protected static Map<String, SqlObjectHandlerFactory> createSqlObjectHandlerMap() {
        Map<String, SqlObjectHandlerFactory> result = FastMap.newInstance();
        result.put("ARRAY", new ArraySqlObjectHandler(Types.ARRAY));
        result.put("BIGINT", new LongSqlObjectHandler(Types.BIGINT));
        result.put("BINARY", new ByteArraySqlObjectHandler(Types.BINARY));
        result.put("BIT", new BooleanSqlObjectHandler(Types.BIT));
        result.put("BLOB", new BlobSqlObjectHandler(Types.BLOB));
        result.put("BOOLEAN", new BooleanSqlObjectHandler(Types.BOOLEAN));
        result.put("CHAR", new StringSqlObjectHandler(Types.CHAR));
        result.put("CLOB", new ClobSqlObjectHandler(Types.CLOB));
        result.put("DATE", new DateSqlObjectHandler(Types.DATE));
        result.put("DECIMAL", new BigDecimalSqlObjectHandler(Types.DECIMAL));
        result.put("DOUBLE", new DoubleSqlObjectHandler(Types.DOUBLE));
        result.put("FLOAT", new DoubleSqlObjectHandler(Types.FLOAT));
        result.put("INTEGER", new IntegerSqlObjectHandler(Types.INTEGER));
        result.put("LONGVARBINARY", new ByteArraySqlObjectHandler(Types.LONGVARBINARY));
        result.put("LONGVARCHAR", new StringSqlObjectHandler(Types.LONGVARCHAR));
        result.put("NUMERIC", new BigDecimalSqlObjectHandler(Types.NUMERIC));
        result.put("REAL", new FloatSqlObjectHandler(Types.REAL));
        result.put("REF", new RefSqlObjectHandler(Types.REF));
        result.put("SMALLINT", new ShortSqlObjectHandler(Types.SMALLINT));
        // result.put("STRUCT", ?);
        result.put("TIME", new TimeSqlObjectHandler(Types.TIME));
        result.put("TIMESTAMP", new TimestampSqlObjectHandler(Types.TIMESTAMP));
        result.put("VARBINARY", new ByteArraySqlObjectHandler(Types.VARBINARY));
        result.put("VARCHAR", new StringSqlObjectHandler(Types.VARCHAR));
        return result;
    }

    /** Returns the Java class that corresponds to a JDBC data type. The data
     * types specified in <code>java.sql.Types</code> are mapped
     * to corresponding Java classes.
     * @see <a href="http://java.sun.com/j2se/1.5.0/docs/guide/jdbc/getstart/mapping.html">
     *Mapping SQL and Java Types</a>  
     * 
     * @param jdbcDataType The JDBC data type specified in <code>java.sql.Types</code>
     * @return The corresponding Java class
     */
    public static Class<?> toJavaClass(int jdbcDataType) {
        return dataTypeClassMap.get(jdbcDataType);
    }

    /** Returns the <code>SqlObjectHandler</code> that corresponds to a JDBC
     * data type.
     *  
     * @param jdbcDataType The JDBC data type specified in <code>java.sql.Types</code>
     * @param javaClass The Java class to convert from/to
     * @return A <code>SqlObjectHandler</code> instance
     * @throws ClassNotFoundException 
     */
    public static SqlObjectHandler toSqlObjectHandler(String jdbcDataType, Class<?> javaClass) throws ClassNotFoundException {
        SqlObjectHandlerFactory handlerFactory = sqlObjectHandlerMap.get(jdbcDataType.toUpperCase());
        if (handlerFactory != null) {
            return handlerFactory.getInstance(javaClass);
        }
        return null;
    }

    /** An interface that gets/sets field values in a database table. */
    public interface SqlObjectHandler {
        /** Returns an object from a <code>ResultSet</code>. The returned
         * object is converted to the Java data type specified in the fieldtype
         * file.
         * 
         * @param rs
         * @param columnIndex
         * @return
         * @throws SQLException
         * @throws ConversionException
         */
        public Object getObject(ResultSet rs, int columnIndex) throws SQLException, ConversionException;
        /** Sets a value in a <code>PreparedStatement</code>. The
         * <code>obj</code> argument is converted to the correct data
         * type.
         * 
         * @param ps
         * @param parameterIndex
         * @param obj
         * @throws SQLException
         * @throws ConversionException
         * @throws ClassNotFoundException
         */
        public void setObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException, ConversionException, ClassNotFoundException;
    }

    /** An object that contains values common to all database implementations. */
    protected static abstract class SqlObjectHandlerFactory {
        /** The SQL class of this Field */
        protected final Class<?> sqlClass;
        /** The JDBC SQL data type of this Field */
        protected final int sqlType;

        public SqlObjectHandlerFactory(int sqlType, Class<?> sqlClass) {
            this.sqlClass = sqlClass;
            this.sqlType = sqlType;
        }

        @SuppressWarnings("unchecked")
        public SqlObjectHandler getInstance(Class<?> javaClass) throws ClassNotFoundException {
            Converter<Object, Object> sqlToJavaConverter = (Converter<Object, Object>) Converters.getConverter(this.sqlClass, javaClass);
            return new HandlerDecorator(this, javaClass, sqlToJavaConverter);
        }

        /** Set the SQL object by using one of the <code>PreparedStatement.setXxx</code>
         * methods. Subclasses cast <code>obj</code> to the required data type.
         * 
         * @param ps
         * @param parameterIndex
         * @param obj
         * @throws SQLException
         */
        protected abstract void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException;

        /** Get the SQL object by using one of the <code>ResultSet.getXxx</code>
         * methods.
         * 
         * @param rs
         * @param columnIndex
         * @return
         * @throws SQLException
         */
        protected abstract Object getObject(ResultSet rs, int columnIndex) throws SQLException;
    }

    /** An object that contains a <code>SqlObjectHandlerFactory</code> and
     * implements <code>SqlObjectHandler</code>.
     * <p>Each <code>ModelFieldType</code> instance contains an instance of this
     * class. The instance is passed to the SQL processing code to
     * retrieve or set field values. This class acts as a bridge between
     * the models and the SQL processing logic. It shields the <code>ModelFieldType</code>
     * object from the details of SQL processing, while also shielding
     * the SQL processing logic from the details of <code>ModelFieldType</code>.</p>
     *
     */
    protected static class HandlerDecorator implements SqlObjectHandler {
        protected final SqlObjectHandlerFactory handlerFactory;
        /** The Java class of this Field */
        protected final Class<?> javaClass;
        /** The SQL class to Java class converter for this Field */
        protected final Converter<Object, Object> sqlToJavaConverter;

        public HandlerDecorator(SqlObjectHandlerFactory handlerFactory, Class<?> javaClass, Converter<Object, Object> sqlToJavaConverter) throws ClassNotFoundException {
            this.handlerFactory = handlerFactory;
            this.javaClass = javaClass;
            this.sqlToJavaConverter = sqlToJavaConverter;
        }

        @SuppressWarnings("unchecked")
        public void setObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException, ConversionException, ClassNotFoundException {
            if (obj == null) {
                ps.setNull(parameterIndex, this.handlerFactory.sqlType);
                return;
            }
            Class<?> sourceClass = obj.getClass();
            if (this.handlerFactory.sqlClass.equals(sourceClass)) {
                this.handlerFactory.castSetObject(ps, parameterIndex, obj);
                return;
            }
            Converter<Object, Object> converter = (Converter<Object, Object>) Converters.getConverter(sourceClass, this.handlerFactory.sqlClass);
            this.handlerFactory.castSetObject(ps, parameterIndex, converter.convert(obj));
        }

        public Object getObject(ResultSet rs, int columnIndex) throws SQLException, ConversionException {
            Object sourceObject =this.handlerFactory.getObject(rs, columnIndex);
            if (sourceObject == null) {
                return null;
            }
            if (this.javaClass.equals(sourceObject.getClass())) {
                return sourceObject;
            }
            sourceObject = this.sqlToJavaConverter.convert(sourceObject);
            return sourceObject;
        }
    }

    protected static class ArraySqlObjectHandler extends SqlObjectHandlerFactory {
        public ArraySqlObjectHandler(int sqlType) {
            super(sqlType, java.sql.Array.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setArray(parameterIndex, (java.sql.Array) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getArray(columnIndex);
        }
    }

    protected static class BigDecimalSqlObjectHandler extends SqlObjectHandlerFactory {
        public BigDecimalSqlObjectHandler(int sqlType) {
            super(sqlType, java.math.BigDecimal.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setBigDecimal(parameterIndex, (java.math.BigDecimal) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getBigDecimal(columnIndex);
        }
    }

    protected static class BlobSqlObjectHandler extends SqlObjectHandlerFactory {
        public BlobSqlObjectHandler(int sqlType) {
            super(sqlType, java.sql.Blob.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setBlob(parameterIndex, (java.sql.Blob) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getBlob(columnIndex);
        }
    }

    protected static class BooleanSqlObjectHandler extends SqlObjectHandlerFactory {
        public BooleanSqlObjectHandler(int sqlType) {
            super(sqlType, Boolean.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setBoolean(parameterIndex, (Boolean) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getBoolean(columnIndex);
        }
    }

    protected static class ByteArraySqlObjectHandler extends SqlObjectHandlerFactory {
        public ByteArraySqlObjectHandler(int sqlType) {
            super(sqlType, byte[].class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setBytes(parameterIndex, (byte[]) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getBytes(columnIndex);
        }
    }

    protected static class ClobSqlObjectHandler extends SqlObjectHandlerFactory {
        public ClobSqlObjectHandler(int sqlType) {
            super(sqlType, java.sql.Clob.class);
        }
        @SuppressWarnings("unchecked")
        @Override
        public SqlObjectHandler getInstance(Class<?> javaClass) throws ClassNotFoundException {
            Converter<Object, Object> sqlToJavaConverter = (Converter<Object, Object>) Converters.getConverter((Class<?>) String.class, javaClass);
            return new HandlerDecorator(this, javaClass, sqlToJavaConverter);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            try {
                if (obj instanceof java.sql.Clob) {
                    try {
                        java.sql.Clob clob = (java.sql.Clob) obj;
                        String str = clob.getSubString(1L, (int)clob.length());
                        ps.setString(parameterIndex, str);
                        return;
                    } catch (Exception e) {}
                }
                ps.setClob(parameterIndex, (java.sql.Clob) obj);
                return;
            } catch (ClassCastException e) {}
            try {
                ps.setString(parameterIndex, (String) obj);
            } catch (ClassCastException e) {
                throw new SQLException("Object type must be java.sql.Clob or java.lang.String");
            }
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            java.sql.Clob clob = rs.getClob(columnIndex);
            if (clob == null) {
                return null;
            }
            int bufferLength = (int) clob.length();
            char[] charBuffer = new char[bufferLength];
            Reader valueReader = clob.getCharacterStream();
            try {
                valueReader.read(charBuffer, 0, bufferLength);
            } catch (IOException e) {
                throw new SQLException(e);
            } finally {
                try {
                    valueReader.close();
                } catch (IOException e) {
                    Debug.logError(e, module);
                }
            }
            return new String(charBuffer);
        }
    }

    protected static class DateSqlObjectHandler extends SqlObjectHandlerFactory {
        public DateSqlObjectHandler(int sqlType) {
            super(sqlType, java.sql.Date.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setDate(parameterIndex, (java.sql.Date) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getDate(columnIndex);
        }
    }

    protected static class DoubleSqlObjectHandler extends SqlObjectHandlerFactory {
        public DoubleSqlObjectHandler(int sqlType) {
            super(sqlType, Double.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setDouble(parameterIndex, (Double) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            Object obj = rs.getObject(columnIndex);
            if (obj == null) return null;
            return rs.getDouble(columnIndex);
        }
    }

    protected static class FloatSqlObjectHandler extends SqlObjectHandlerFactory {
        public FloatSqlObjectHandler(int sqlType) {
            super(sqlType, Float.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setFloat(parameterIndex, (Float) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getFloat(columnIndex);
        }
    }

    protected static class IntegerSqlObjectHandler extends SqlObjectHandlerFactory {
        public IntegerSqlObjectHandler(int sqlType) {
            super(sqlType, Integer.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setInt(parameterIndex, (Integer) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getInt(columnIndex);
        }
    }

    protected static class RefSqlObjectHandler extends SqlObjectHandlerFactory {
        public RefSqlObjectHandler(int sqlType) {
            super(sqlType, java.sql.Ref.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setRef(parameterIndex, (java.sql.Ref) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getRef(columnIndex);
        }
    }

    protected static class StringSqlObjectHandler extends SqlObjectHandlerFactory {
        public StringSqlObjectHandler(int sqlType) {
            super(sqlType, String.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setString(parameterIndex, (String) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getString(columnIndex);
        }
    }

    protected static class LongSqlObjectHandler extends SqlObjectHandlerFactory {
        public LongSqlObjectHandler(int sqlType) {
            super(sqlType, Long.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setLong(parameterIndex, (Long) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getLong(columnIndex);
        }
    }

    protected static class ShortSqlObjectHandler extends SqlObjectHandlerFactory {
        public ShortSqlObjectHandler(int sqlType) {
            super(sqlType, Short.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setShort(parameterIndex, (Short) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getShort(columnIndex);
        }
    }

    protected static class TimeSqlObjectHandler extends SqlObjectHandlerFactory {
        public TimeSqlObjectHandler(int sqlType) {
            super(sqlType, java.sql.Time.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setTime(parameterIndex, (java.sql.Time) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getTime(columnIndex);
        }
    }

    protected static class TimestampSqlObjectHandler extends SqlObjectHandlerFactory {
        public TimestampSqlObjectHandler(int sqlType) {
            super(sqlType, java.sql.Timestamp.class);
        }
        @Override
        protected void castSetObject(PreparedStatement ps, int parameterIndex, Object obj) throws SQLException {
            ps.setTimestamp(parameterIndex, (java.sql.Timestamp) obj);
        }
        @Override
        protected Object getObject(ResultSet rs, int columnIndex) throws SQLException {
            return rs.getTimestamp(columnIndex);
        }
    }

}
