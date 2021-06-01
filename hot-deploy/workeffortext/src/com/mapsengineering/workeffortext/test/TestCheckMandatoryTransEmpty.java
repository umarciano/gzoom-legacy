package com.mapsengineering.workeffortext.test;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.accountingext.services.InputAndDetailValue;
import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.jdbc.JdbcQueryIterator;
import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.workeffortext.services.trans.E;
import com.mapsengineering.workeffortext.services.trans.WTI;

/**
 * Provare tutti gli ftl per prevenire errori, intanto conviene usarlo per recuperare qualcosa su cu ifare isPosted = Y
 */
public class TestCheckMandatoryTransEmpty extends TestExecuteChildPerformFindTransIndicator  {

    public static final String MODULE = TestCheckMandatoryTransEmpty.class.getName();
    private static final String queryCheckMandatoryTransEmpty = "sql/checkMandatoryTransEmpty/queryCheckMandatoryTransEmpty.sql.ftl";
    
    public void testCheckMandatoryTransEmpty() {
        JdbcQueryIterator queryCheckMandatoryTransEmptyList = null;
        try {
        
            queryCheckMandatoryTransEmptyList = new FtlQuery(dispatcher.getDelegator(), queryCheckMandatoryTransEmpty, mapContextUpdate()).iterate();
            while (queryCheckMandatoryTransEmptyList.hasNext()) {
                ResultSet ele = queryCheckMandatoryTransEmptyList.next();
                Debug.log("ele = " + ele);
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        finally {
            try {
                if (queryCheckMandatoryTransEmptyList != null)
                    queryCheckMandatoryTransEmptyList.close();
            } catch (IOException e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }
    }
    
    protected String getTitle() {
        return "Test execute queryCheckMandatoryTransEmpty ";
    }
}
