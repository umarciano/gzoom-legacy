package com.mapsengineering.partyext.test;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.partyext.common.E;

/**
 * Test for PartyHistoryService
 *
 */
public class TestPartyHistoryUtil extends BaseTestCase {

    public static final String MODULE = TestPartyHistory.class.getName();
    
    public Map<String, Object> getDefaultContext(String partyId) {
        Map<String, Object> localContext = this.context;
        
        localContext.put(E.partyId.name(), partyId);
        localContext.put(E.lastName.name(), "lastName16");
        localContext.put(E.firstName.name(), "name16");
        localContext.put(E.emplPositionTypeDate.name(), new Timestamp(UtilDateTime.toDate(8, 31, 2013, 0, 0, 0).getTime()));
        localContext.put(E.description.name(), "nuova descrizione");
        localContext.put(E.comments.name(), "nuovo commento");
        localContext.put(E.emplPositionTypeId.name(), "D");
        localContext.put(E.employmentAmount.name(), "100");
        
        return localContext;
    }
}
