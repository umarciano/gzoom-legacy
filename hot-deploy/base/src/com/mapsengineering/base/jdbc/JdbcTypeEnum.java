package com.mapsengineering.base.jdbc;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public enum JdbcTypeEnum {

    BIT(Types.BIT), //
    TINYINT(Types.TINYINT), //
    SMALLINT(Types.SMALLINT), //
    INTEGER(Types.INTEGER), //
    BIGINT(Types.BIGINT), //
    FLOAT(Types.FLOAT), //
    REAL(Types.REAL), //
    DOUBLE(Types.DOUBLE), //
    NUMERIC(Types.NUMERIC), //
    DECIMAL(Types.DECIMAL), //
    CHAR(Types.CHAR), //
    VARCHAR(Types.VARCHAR), //
    LONGVARCHAR(Types.LONGVARCHAR), //
    DATE(Types.DATE), //
    TIME(Types.TIME), //
    TIMESTAMP(Types.TIMESTAMP), //
    BINARY(Types.BINARY), //
    VARBINARY(Types.VARBINARY), //
    LONGVARBINARY(Types.LONGVARBINARY), //
    NULL(Types.NULL), //
    OTHER(Types.OTHER), //
    JAVA_OBJECT(Types.JAVA_OBJECT), //
    DISTINCT(Types.DISTINCT), //
    STRUCT(Types.STRUCT), //
    ARRAY(Types.ARRAY), //
    BLOB(Types.BLOB), //
    CLOB(Types.CLOB), //
    REF(Types.REF), //
    DATALINK(Types.DATALINK), //
    BOOLEAN(Types.BOOLEAN), //
    ROWID(Types.ROWID), //
    NCHAR(Types.NCHAR), //
    NVARCHAR(Types.NVARCHAR), //
    LONGNVARCHAR(Types.LONGNVARCHAR), //
    NCLOB(Types.NCLOB), //
    SQLXML(Types.SQLXML);

    private static final Map<Integer, JdbcTypeEnum> mapByCode = initMapByCode();

    private int code;

    private JdbcTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static JdbcTypeEnum getByCode(int code) {
        return mapByCode.get(code);
    }

    private static Map<Integer, JdbcTypeEnum> initMapByCode() {
        Map<Integer, JdbcTypeEnum> map = new HashMap<Integer, JdbcTypeEnum>();
        for (JdbcTypeEnum e : JdbcTypeEnum.values()) {
            JdbcTypeEnum eOld = map.put(e.getCode(), e);
            if (eOld != null) {
                throw new IllegalStateException("Enum " + e.name() + " has equal code to " + eOld);
            }
        }
        return map;
    }
}
