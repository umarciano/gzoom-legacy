package com.mapsengineering.base.test;

import java.math.BigDecimal;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.helper.TemplateEnum;
import com.mapsengineering.base.standardimport.common.E;

/**
 * Test for update person
 *
 */
public class TestPersonStandardImportUpdate extends BasePersonStandardImportUploadFileTestCase {

    private static final String EMPL4 = "EMPL4";
    
    /**
     * comments and description is null in update
     */
    private static final String EMPL5 = "EMPL5";
    /**
     * comments is null and description became description2 
     */
    private static final String EMPL6 = "EMPL6";
    /**
     * comments became comments2 and description is null
     */
    private static final String EMPL7 = "EMPL7";

    /**
     * employmentAmount became 100
     */
    private static final String EMPL8 = "EMPL8";
    /**
     * employmentAmount became 50
     */
    private static final String EMPL9 = "EMPL9";
    /**
     * employmentAmount is null, so it is 50
     */
    private static final String EMPL10 = "EMPL10";
    /**
     * employmentAmount became 100
     */
    private static final String EMPL11 = "EMPL11";

    /**
     * Different check: update and null value for emplPositionTypeId, comments, description, employmentAmount
     */
    public void testPersonInterfaceUpdate() {
        try {
            setContextAndRunPersonInterfaceUpdate("PersonInterfaceInsert.xls|AllocationInterfaceInsert.xls", 0, 12);

            setContextAndRunPersonInterfaceUpdate("PersonInterfaceUpdate.xls", 0, 12);

            String empl4PartyId = getPartyId(EMPL4);
            List<GenericValue> lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), empl4PartyId), null, null, null, false);
            Debug.log(" - lista " + lista);
            assertEquals(1, lista.size());
            
            assertEmpl5();
            assertEmpl6();
            assertEmpl7();
            
            assertEmpl8();
            assertEmpl9();
            assertEmpl10();
            assertEmpl11();
            
            setContextAndRunPersonInterfaceUpdate("PersonInterfaceUpdate2.xls", 1, 2);
            lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), empl4PartyId), null, null, null, false);
            Debug.log(" - lista dopo filePersonInterfaceUpdate2 " + lista);
            assertEquals(1, lista.size());

        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }

    private void assertEmpl5() throws GenericEntityException {
        assertEmpl(EMPL5, "comments", "description", 0);
    }

    private void assertEmpl6() throws GenericEntityException {
        assertEmpl(EMPL6, "comments", "description2", 1);
    }
    
    private void assertEmpl7() throws GenericEntityException {
        assertEmpl(EMPL7, "comments2", "description", 1);
    }
    
    private void assertEmpl8() throws GenericEntityException {
        assertEmplAmount(EMPL8, new BigDecimal(100), 1);
    }
    
    private void assertEmpl9() throws GenericEntityException {
        assertEmplAmount(EMPL9, new BigDecimal(50), 1);
    }
    
    private void assertEmpl10() throws GenericEntityException {
        assertEmplAmount(EMPL10, new BigDecimal(50), 0);
    }
    
    private void assertEmpl11() throws GenericEntityException {
        assertEmplAmount(EMPL11, new BigDecimal(100), 0);
    }

    private void assertEmpl(String partyCode, String commentsValue, String descriptionValue, int size) throws GenericEntityException {
    	String partyId = getPartyId(partyCode);
        GenericValue person = delegator.findOne(E.Person.name(), false, E.partyId.name(), partyId);
        assertEquals(commentsValue, person.getString(E.comments.name()));
        GenericValue party = delegator.findOne(E.Party.name(), false, E.partyId.name(), partyId);
        assertEquals(descriptionValue, party.getString(E.description.name()));
        List<GenericValue> lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), partyId), null, null, null, false);
        Debug.log(" - lista " + lista);
        assertEquals(size, lista.size());
    }
    
    private void assertEmplAmount(String partyCode, BigDecimal employmentAmount, int size) throws GenericEntityException {
    	String partyId = getPartyId(partyCode);
        GenericValue person = delegator.findOne(E.Person.name(), false, E.partyId.name(), partyId);
        Debug.log(" - person " + person);
        assertEquals(0, (employmentAmount).compareTo(person.getBigDecimal(E.employmentAmount.name())));
        List<GenericValue> lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), partyId), null, null, null, false);
        Debug.log(" - lista " + lista);
        assertEquals(size, lista.size());
    }
    
}
