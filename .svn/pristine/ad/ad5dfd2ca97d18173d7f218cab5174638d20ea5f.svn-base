package com.mapsengineering.base.jdbc.test;

import java.io.IOException;
import java.sql.Types;

import org.ofbiz.base.util.Debug;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.test.BaseTestCase;

public class FtlQueryTest extends BaseTestCase {

    public void testSelectUserLoginById() throws IOException {
        try {
            final String testUserName = "test-user-1";
            final String queryOut = "select * from PUBLIC.USER_LOGIN where USER_LOGIN_ID=?";

            FtlQuery query = new FtlQuery(delegator, "test/sql/selectUserLogin.sql.ftl", context);
            context.put("userLoginId", testUserName);
            String sql = query.getQueryString();
            Debug.log("SQL: " + sql);
            assertNotNull("SQL", sql);
            sql = sql.replaceAll("(\r\n)|(\t)|(\r)|(\n)", " ").trim();
            assertEquals("SQL", queryOut, sql);
            Debug.log("SQL params: " + query.getParams());
            assertEquals("SQL params size", 1, query.getParams().size());
            assertEquals("SQL param[0].type", Types.CHAR, query.getParams().get(0).getJdbcType());
            assertEquals("SQL param[0].value", testUserName, query.getParams().get(0).getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
