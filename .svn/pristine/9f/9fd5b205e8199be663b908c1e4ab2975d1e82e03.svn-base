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
public class TestExecuteChildPerformFindTransIndicator extends BaseTestCase  {

    public static final String MODULE = TestExecuteChildPerformFindTransIndicator.class.getName();
    private static final String queryAcctgTransView = "sql/transIndicator/queryAcctgTransView.sql.ftl"; // TODO cambiare
    
    /**
     * 
     */
    public void testExecuteChildPerformFindTransIndicator() {
        Debug.log(getTitle());
        JdbcQueryIterator queryAcctgTransList = null;
        try {
            queryAcctgTransList = new FtlQuery(dispatcher.getDelegator(), queryAcctgTransView, mapContextUpdate()).iterate();
            while (queryAcctgTransList.hasNext()) {
                ResultSet ele = queryAcctgTransList.next();
                Map<String, Object> genericValue = FastMap.newInstance(); // like WorkEffortTransactionIndicatorView 
                String contentId =  ele.getString("CONTENT_ID");
                Debug.log("contentId = " + contentId);
                genericValue.put("weTransWeId", getObiettivoId(ele));
                genericValue.put("weTransMeasureId", getMisuraObj(ele));
                genericValue.put("weTransId", ele.getString(WTI.A_ACCTG_TRANS_ID.name()));
                genericValue.put("weTransEntryId", ele.getString(WTI.B_ACCTG_TRANS_ENTRY_SEQ_ID.name()));
                genericValue.put("weTransValue", getTransValue(ele));
                genericValue.put("weTransComment", getWeTransComment(ele));
                genericValue.put("weTransComments", getWeTransComments(ele));
                genericValue.put("partyId", ele.getString(WTI.PARTY_ID.name()));
                genericValue.put("roleTypeId", ele.getString(WTI.ROLE_TYPE_ID.name()));
                genericValue.put("entryPartyId", ele.getString(WTI.ENTRY_PARTY_ID.name()));
                genericValue.put("entryRoleTypeId", ele.getString(WTI.ENTRY_ROLE_TYPE_ID.name()));
                Map<String, Object> row = FastMap.newInstance();
                row.put("gv", genericValue);

                Debug.log("row = " + row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
        finally {
            try {
                if (queryAcctgTransList != null)
                    queryAcctgTransList.close();
            } catch (IOException e) {
                e.printStackTrace();
                assertTrue(false);
            }
        }
    }
    
    private String getWeTransComment(ResultSet ele) throws SQLException {
        if(UtilValidate.isNotEmpty(ele.getString(WTI.B_DESCRIPTION.name()))) {
            return ele.getString(WTI.B_DESCRIPTION.name());
        }
        return null;
    }
    
    private String getWeTransComments(ResultSet ele) throws SQLException {
        if(UtilValidate.isNotEmpty(ele.getString(WTI.A_DESCRIPTION.name()))) {
            return ele.getString(WTI.A_DESCRIPTION.name());
        }
        return null;
    }

    private String getObiettivoId(ResultSet ele) throws SQLException {
        return ele.getString(WTI.WORK_EFFORT_ID.name());
    }

    private String getMisuraObj(ResultSet ele) throws SQLException {
        return ele.getString(WTI.WORK_EFFORT_MEASURE_ID.name());
    }

    private Double getTransValue(ResultSet ele) throws SQLException {
        if(UtilValidate.isNotEmpty(ele.getString(WTI.B_AMOUNT.name()))) {
            return ele.getDouble(WTI.B_AMOUNT.name());
        }
        return null;
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(context);
        mapContext.push();
        return mapContext;
    }
    
    protected MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(WTI.workEffortId.name(), getWorkEffortId());
        mapContext.put(E.statusId.name(), "WEPERFST_PLANSUB");
        return mapContext;
    }

    private String getWorkEffortId() {
        return "W50000"; // TODO quale conviene "W30000"; // W10017
    }
    
    protected String getTitle() {
        return "Test TODO execute queryAcctgTrans ";
    }
}
