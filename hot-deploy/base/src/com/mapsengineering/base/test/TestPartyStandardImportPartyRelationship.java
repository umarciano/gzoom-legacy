package com.mapsengineering.base.test;

import java.util.List;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.mapsengineering.base.standardimport.common.E;

/**
 * Test for update person
 *
 */
public class TestPartyStandardImportPartyRelationship extends BasePersonStandardImportUploadFileTestCase {

    private static final String PERSON1 = "PERSON1";
    private static final String PERSON2 = "PERSON2";
    private static final String PERSON3 = "PERSON3";
    private static final String PERSON4 = "PERSON4";
    private static final String PERSON5 = "PERSON5";
    private static final String PERSON6 = "PERSON6";
    private static final String PERSON7 = "PERSON7";
    private static final String PERSON8 = "PERSON8";
    private static final String PERSON9 = "PERSON9";
    private static final String PERSON10 = "PERSON10";

    /**
     * Insert and then update PartyRelationship
     */
    public void testPersonInterfacePartyRelationship() {
        try {
            setContextAndRunPersonInterfaceUpdate("PersonInterfacePartyRelationshipInsert.xls", 0, 10);

            setContextAndRunPersonInterfaceUpdate("PersonInterfacePartyRelationshipUpdate.xls", 0, 10);

            assertPerson1();
            assertPerson2();
            assertPerson3();
            assertPerson4();
            assertPerson5();
            assertPerson6();
            assertPerson7();
            assertPerson8();
            assertPerson9();
            assertPerson10();
            

        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
    
    private List<GenericValue> assertPerson(String partyCode, int size) throws GenericEntityException {
    	String partyId = getPartyId(partyCode);
        List<GenericValue> listaTo = delegator.findList(E.PartyRelationship.name(), EntityCondition.makeCondition(E.partyIdTo.name(), partyId), null, null, null, false);
        Debug.log(" - listaTo " + listaTo);
        assertEquals(size, listaTo.size());  	
        return listaTo;
    }

    private void assertPerson1() throws GenericEntityException {
        assertPerson(PERSON1, 0);
    }

    private void assertPerson2() throws GenericEntityException {
        List<GenericValue> listaTo = assertPerson(PERSON2, 2);
        for(GenericValue lista : listaTo) {
            Debug.log(" - DPERSON2 " + lista);
            assertEquals("su org1", lista.getString(E.comments.name()));
        }
    }
    
    private void assertPerson3() throws GenericEntityException {
        List<GenericValue> listaTo = assertPerson(PERSON3, 2);
        for(GenericValue lista : listaTo) {
            Debug.log(" - DPERSON3 " + lista);
            assertEquals("cambiato", lista.getString(E.comments.name()));
        }
    }
    
    private void assertPerson4() throws GenericEntityException {
        List<GenericValue> listaTo = assertPerson(PERSON4, 4);
        for (GenericValue lista : listaTo) {
            Debug.log(" - DPERSON4 " + lista);
            if (UtilValidate.isEmpty(lista.getTimestamp(E.thruDate.name()))) {
                assertEquals("cambiato", lista.getString(E.comments.name()));
            } else if (UtilValidate.isNotEmpty(lista.getTimestamp(E.thruDate.name())) && lista.getTimestamp(E.thruDate.name()).equals(THRU_DATE_2013)) {
                assertEquals("poi cambia", lista.getString(E.comments.name()));
            } else {
                assertTrue(false);
            }
        }
    }
    
    private void assertPerson5() throws GenericEntityException {
        List<GenericValue> listaTo = assertPerson(PERSON5, 2);
        for(GenericValue lista : listaTo) {
            Debug.log(" - DPERSON5 " + lista);
            assertEquals("su org2", lista.getString(E.comments.name()));
        }
    }
    
    private void assertPerson6() throws GenericEntityException {
        List<GenericValue> listaTo = assertPerson(PERSON6, 4);
        for (GenericValue lista : listaTo) {
            Debug.log(" - DPERSON6 " + lista);
            if (UtilValidate.isEmpty(lista.getTimestamp(E.thruDate.name()))) {
                assertEquals("su org2", lista.getString(E.comments.name()));
            } else if (UtilValidate.isNotEmpty(lista.getTimestamp(E.thruDate.name())) && lista.getTimestamp(E.thruDate.name()).equals(THRU_DATE_2013)) {
                assertEquals("su org1", lista.getString(E.comments.name()));
            } else {
                assertTrue(false);
            }
        }
    }
    
    private void assertPerson7() throws GenericEntityException {
        assertPerson(PERSON7, 0);
    }
    
    private void assertPerson8() throws GenericEntityException {
        List<GenericValue> listaTo = assertPerson(PERSON8, 2);
        for(GenericValue lista : listaTo) {
            Debug.log(" - DPERSON8 " + lista);
            assertEquals(null, lista.getString(E.comments.name()));
        }
    }
    
    private void assertPerson9() throws GenericEntityException {
        List<GenericValue> listaTo = assertPerson(PERSON9, 2);
        for(GenericValue lista : listaTo) {
            Debug.log(" - DPERSON9 " + lista);
            assertEquals(null, lista.getString(E.comments.name()));
        }
    }
    
    private void assertPerson10() throws GenericEntityException {
        List<GenericValue> listaTo = assertPerson(PERSON10, 4);
        for (GenericValue lista : listaTo) {
            Debug.log(" - DPERSON10 " + lista);
            if (UtilValidate.isEmpty(lista.getTimestamp(E.thruDate.name()))) {
                assertEquals(null, lista.getString(E.comments.name()));
            } else if (UtilValidate.isNotEmpty(lista.getTimestamp(E.thruDate.name())) && lista.getTimestamp(E.thruDate.name()).equals(THRU_DATE_2013)) {
                assertEquals(null, lista.getString(E.comments.name()));
            } else {
                assertTrue(false);
            }
        }
    }

}
