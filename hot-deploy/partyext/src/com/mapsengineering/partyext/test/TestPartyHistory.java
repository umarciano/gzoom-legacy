package com.mapsengineering.partyext.test;

import java.sql.Timestamp;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.mapsengineering.partyext.common.E;

/**
 * Test for PartyHistoryService
 *
 */
public class TestPartyHistory extends TestPartyHistoryUtil {

    public static final String MODULE = TestPartyHistory.class.getName();

    /**
     * Test updatePerson parte la storicizzazione
     */
    public void testUpdatePerson() {
    	Debug.log(" - testUpdatePerson ");
        String partyId = "DRESP14";
        
        Map<String, Object> localContext = getDefaultContext(partyId);
        
        try {
            GenericValue personOld = delegator.findByPrimaryKey(E.Person.name(), UtilMisc.toMap(E.partyId.name(), partyId));
            
            
            this.dispatcher.runSync("updatePerson", localContext);
            Timestamp fromDate = Timestamp.valueOf("2001-01-01 00:00:00");
            if(UtilValidate.isNotEmpty(personOld.get(E.emplPositionTypeDate.name()))) {
                fromDate = (Timestamp)personOld.get(E.emplPositionTypeDate.name());
            }
            
            //controllo che ho fatto la storicizzazione corretta,
            //controllando nella tabella PartyHistory che ci sia ildato corretto
            GenericValue partyHistory = delegator.findByPrimaryKey(E.PartyHistory.name(), UtilMisc.toMap(E.partyId.name(), partyId, E.fromDate.name(), fromDate));
            
            assertTrue(UtilValidate.isNotEmpty(partyHistory)
                    && ( (partyHistory.get(E.employmentAmount.name()) == null && personOld.get(E.employmentAmount.name()) == null) || partyHistory.get(E.employmentAmount.name()).equals(personOld.get(E.employmentAmount.name()))) 
                    && ( (partyHistory.get(E.emplPositionTypeId.name()) == null && personOld.get(E.emplPositionTypeId.name()) == null) || partyHistory.get(E.emplPositionTypeId.name()).equals(personOld.get(E.emplPositionTypeId.name()))) 
                    && ( (partyHistory.get(E.description.name()) == null && personOld.get(E.description.name()) == null) || partyHistory.get(E.description.name()).equals(personOld.get(E.description.name())))
                    && ( (partyHistory.get(E.comments.name()) == null && personOld.get(E.comments.name()) == null) || partyHistory.get(E.comments.name()).equals(personOld.get(E.comments.name()))));

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
        
    }
}
