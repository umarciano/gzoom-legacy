package com.mapsengineering.partyext.test;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.partyext.common.E;

/**
 * Test for PartyHistoryService
 *
 */
public class TestPartyHistory2 extends TestPartyHistoryUtil {

    public static final String MODULE = TestPartyHistory.class.getName();

    /**
     * Test con errore xche la data inserita e minore di quella attuale
     */
    public void testUpdatePersonError() {
    	Debug.log(" - testUpdatePersonError ");
        String partyId = "DRESP14";
        
        Map<String, Object> localContext = getDefaultContext(partyId);
        
        localContext.put(E.emplPositionTypeDate.name(), new Timestamp(UtilDateTime.toDate(8, 31, 2012, 0, 0, 0).getTime()));
        localContext.put(E.description.name(), "nuova descrizione ERRORE");
        localContext.put(E.comments.name(), "nuovo commento ERRORE");
        
        try {
            Map<String, Object> result = this.dispatcher.runSync("updatePerson", localContext);            
             
            assertTrue(ServiceUtil.isError(result));

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
        
    }
}
