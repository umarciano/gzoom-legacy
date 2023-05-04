package com.mapsengineering.base.test;

import org.ofbiz.base.util.Debug;

/**
 * Test for update person with thruDate
 *
 */
public class TestPersonStandardImportFromDate extends BasePersonStandardImportUploadFileTestCase {

    private static final String EMPL16 = "EMPL16";
    private static final String EMPL17 = "EMPL17";
    private static final String EMPL18 = "EMPL18";
    private static final String EMPL19 = "EMPL19";
    private static final String ORG1 = "ORG1";
    
    /**
     * Different check on PartyRelationship.fromDate
     */
    public void testPersonInterfaceFromDate() {
        try {
            // Unita' Organizzative gia' esistenti
            String org1PartyId = getPartyId(ORG1);
            
            Debug.log("Primo caricamento con la creazione di 4 dipendenti");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_FromDate.xls", 0, 4);

            String empl16PartyId = getPartyId(EMPL16);
            String empl17PartyId = getPartyId(EMPL17);
            String empl18PartyId = getPartyId(EMPL18);
            String empl19PartyId = getPartyId(EMPL19);
            
            Debug.log("Primo caricamento Controllo 16 ");
            checkPartyRelationship(empl16PartyId, 1 , org1PartyId, FIRST_DATE_GEN_2022, null, 1 , org1PartyId, FIRST_DATE_GEN_2022, null, 0, null, null, 0, null, null);

            Debug.log("Primo caricamento Controllo 17 ");
            checkPartyRelationship(empl17PartyId, 1 , org1PartyId, FIRST_DATE_GEN_2022, null, 1 , org1PartyId, FIRST_DATE_GEN_2022, null, 0, null, null, 0, null, null);

            Debug.log("Secondo caricamento Controllo 18");
            checkPartyRelationship(empl18PartyId, 1, org1PartyId, FIRST_DATE_GEN_2021, null, 1, org1PartyId, FIRST_DATE_GEN_2021, null, 0, null, null, 0, null, null);

            Debug.log("Secondo caricamento Controllo 19");
            checkPartyRelationship(empl19PartyId, 1, org1PartyId, FIRST_DATE_GEN_2020, null, 1, org1PartyId, FIRST_DATE_GEN_2020, null, 0, null, null, 0, null, null);

        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
}
