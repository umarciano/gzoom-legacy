package com.mapsengineering.partyext.test;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;

import com.mapsengineering.partyext.common.E;

/**
 * Test for PartyHistoryService
 *
 */
public class TestPartyHistory3 extends TestPartyHistoryUtil {

    public static final String MODULE = TestPartyHistory.class.getName();
    
    /**
     * Test senza errore perche anche se la data inserita e' minore di quella attuale,
     *  il flag evita di invocare il servizio di insert nel PartyHistory 
     */
    public void testUpdatePersonNoError() {
    	Debug.log(" - testUpdatePersonNoError ");
        String partyId = "DRESP14";
        
        Map<String, Object> localContext = getDefaultContext(partyId);
        
        localContext.put(E.emplPositionTypeDate.name(), new Timestamp(UtilDateTime.toDate(8, 31, 2012, 0, 0, 0).getTime()));
        localContext.put(E.description.name(), "nuova descrizione ERRORE");
        localContext.put(E.comments.name(), "nuovo commento ERRORE");
        localContext.put(E.skipStore.name(), true);
        
        try {
            Map<String, Object> result = this.dispatcher.runSync("updatePerson", localContext);            
             
            assertFalse(ServiceUtil.isError(result));

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
        
    }
    
    /**
     * Test updatePerson senza storicizzare, senza passare la data non deve creare il movimento
     */
    public void testUpdatePersonNoHistory() {
    	Debug.log(" - testUpdatePersonNoHistory ");

        String partyId = "RCP10192";
        
        Map<String, Object> localContext = this.context;
        
        localContext.put(E.partyId.name(), partyId);
        localContext.put(E.lastName.name(), "Moretti");
        localContext.put(E.firstName.name(), "Orazio");
        
        localContext.put(E.description.name(), "nuova descrizione2");
        localContext.put(E.comments.name(), "nuovo commento2");
        
        try {
            GenericValue personOld = delegator.findByPrimaryKey(E.Person.name(), UtilMisc.toMap(E.partyId.name(), partyId));
            
            this.dispatcher.runSync("updatePerson", localContext);
            
            Timestamp fromDate = Timestamp.valueOf("2001-01-01 00:00:00");
            if (UtilValidate.isNotEmpty(personOld.get(E.emplPositionTypeDate.name()))) {
                fromDate = (Timestamp)personOld.get(E.emplPositionTypeDate.name());
            }
            
            GenericValue partyHistory = delegator.findByPrimaryKey(E.PartyHistory.name(), UtilMisc.toMap(E.partyId.name(), partyId, E.fromDate.name(), fromDate));
            assertTrue(UtilValidate.isEmpty(partyHistory));
            
        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
        
    }

}
