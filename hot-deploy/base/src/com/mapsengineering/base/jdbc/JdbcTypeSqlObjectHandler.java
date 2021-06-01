package com.mapsengineering.base.jdbc;

import java.util.HashMap;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.jdbc.SqlTypes;
import org.ofbiz.entity.jdbc.SqlTypes.SqlObjectHandler;

public final class JdbcTypeSqlObjectHandler {

    private static final String MODULE = JdbcTypeSqlObjectHandler.class.getName();

    private static final Map<Integer, SqlObjectHandler> jdbcTypeHandlerMap = initJdbcJavaMap();

    private JdbcTypeSqlObjectHandler() {
    }

    public static SqlObjectHandler get(int jdbcType) {
        return jdbcTypeHandlerMap.get(jdbcType);
    }

    private static Map<Integer, SqlObjectHandler> initJdbcJavaMap() {
        Map<Integer, SqlObjectHandler> map = new HashMap<Integer, SqlObjectHandler>();
        for (JdbcTypeEnum e : JdbcTypeEnum.values()) {
            Class<?> clazz = SqlTypes.toJavaClass(e.getCode());
            if (clazz != null) {
                try {
                    SqlObjectHandler handler = SqlTypes.toSqlObjectHandler(e.name(), clazz);
                    if (handler != null) {
                        SqlObjectHandler handlerOld = map.put(e.getCode(), handler);
                        if (handlerOld != null) {
                            Debug.logError("Replaced old handler " + e.name() + " " + clazz.getName(), MODULE);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    Debug.logWarning(ex, "Error initializing JdbcTypeSqlObjectHandler", MODULE);
                }
            }
        }
        return map;
    }
}
