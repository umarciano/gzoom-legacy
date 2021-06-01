package com.mapsengineering.base.jdbc.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.ResultSetWrapper;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.base.util.OfbizServiceContext;

public class FtlQueryIteratorTest extends BaseTestCase {

    public static final class TestRecord extends ResultSetWrapper {

        private static final long serialVersionUID = 1L;

        public String getUserLoginId() throws SQLException {
            return getRs().getString("USER_LOGIN_ID");
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        List<GenericValue> gvList = new ArrayList<GenericValue>();
        gvList.add(delegator.makeValue("UserLogin", "userLoginId", "test-user-1"));
        gvList.add(delegator.makeValue("UserLogin", "userLoginId", "test-user-2"));
        gvList.add(delegator.makeValue("UserLogin", "userLoginId", "test-user-3"));
        delegator.storeAll(gvList);
    }

    public void testSelectUserLoginById() throws IOException {
        OfbizServiceContext ctx = new OfbizServiceContext(dispatcher.getDispatchContext(), context);
        try {
            ctx.put("userLoginId", "test-user-1");
            TestRecord record = new TestRecord();
            Iterator<TestRecord> it = new FtlQuery(delegator, "test/sql/selectUserLogin.sql.ftl", context).iterate(record);
            while (it.hasNext()) {
                record = it.next();
                Debug.log("userLoginId=" + record.getUserLoginId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ctx.close();
        }
    }
}
