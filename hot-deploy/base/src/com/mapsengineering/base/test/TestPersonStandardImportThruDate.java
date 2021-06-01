package com.mapsengineering.base.test;

import java.sql.Timestamp;
import java.util.List;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import com.mapsengineering.base.standardimport.common.E;
import com.mapsengineering.base.standardimport.helper.TemplateEnum;

/**
 * Test for update person with thruDate
 *
 */
public class TestPersonStandardImportThruDate extends BasePersonStandardImportUploadFileTestCase {

    protected static final Timestamp THRU_DATE_NOV_2019 = new Timestamp(UtilDateTime.toDate(11, 13, 2019, 0, 0, 0).getTime());
    protected static final Timestamp THRU_DATE_DIC_2019 = new Timestamp(UtilDateTime.toDate(12, 31, 2019, 0, 0, 0).getTime());
    
    private static final String EMPL13 = "EMPL13";
    private static final String EMPL14 = "EMPL14";
    private static final String EMPL15 = "EMPL15";
    private static final String ORG1 = "ORG1";
    private static final String ORG2 = "ORG2";
    // private static final String ORG3 = "ORG3"; 
    
    /**
     * Different check TODO UserLogin
     */
    public void testPersonInterfaceThruDate() {
        try {
            // Unita' Organizzative gia' esistenti
            String org1PartyId = getPartyId(ORG1);
            String org2PartyId = getPartyId(ORG2);
            // String org3PartyId = getPartyId(ORG3);
            
            Debug.log("Primo caricamento con la creazione di 3 dipendenti");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_ThruDate.xls", 0, 3);

            String empl13PartyId = getPartyId(EMPL13);
            String empl14PartyId = getPartyId(EMPL14);
            String empl15PartyId = getPartyId(EMPL15);
            
            List<GenericValue> lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - PartyHistoryView lista " + lista);

            lista = delegator.findList("PartyRole", EntityCondition.makeCondition(E.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - PartyRole lista " + lista);

            lista = delegator.findList("UserLogin", EntityCondition.makeCondition(E.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - UserLogin lista " + lista);

            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - Party lista " + lista);
            assertEquals(E.PARTY_ENABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            Debug.log("Primo caricamento Controllo 13 con 2 relazioni con ORG1 ");
            checkPartyRelationship(empl13PartyId, 1 , org1PartyId, null, 1 , org1PartyId, null, 0, null, null, 0, null, null);

            lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - PartyHistoryView lista " + lista);
            
            lista = delegator.findList("PartyRole", EntityCondition.makeCondition(E.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - PartyRole lista " + lista);

            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - Party lista " + lista);
            assertEquals(E.PARTY_DISABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            lista = delegator.findList("UserLogin", EntityCondition.makeCondition(E.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - UserLogin lista " + lista);
            
            Debug.log("Primo caricamento Controllo 14 con 2 relazione con ORG1 scadute e 1 relazione 13 scaduta");
            checkPartyRelationship(empl14PartyId, 1, org1PartyId, THRU_DATE_NOV_2019, 1, org1PartyId, THRU_DATE_NOV_2019, 1, empl13PartyId, THRU_DATE_NOV_2019, 0, null, null);

            // 13 same refDate, from ORG1 to ORG2, so 1 delete, no thruDate 
            // 14 already disabled
            // 15 has thruDate, so disabled party and 3 relationship
            Debug.log("Secondo caricamento");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_ThruDate_sameRefDate.xls", 0, 3);

            lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - PartyHistoryView lista " + lista);

            lista = delegator.findList("PartyRole", EntityCondition.makeCondition(E.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - PartyRole lista " + lista);

            lista = delegator.findList("UserLogin", EntityCondition.makeCondition(E.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - UserLogin lista " + lista);

            Debug.log("Secondo caricamento Controllo 13 con 2 relazioni con ORG2");
            checkPartyRelationship(empl13PartyId, 1, org2PartyId, null, 1, org2PartyId, null, 0, null, null, 0, null, null);

            lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - PartyHistoryView lista " + lista);
            
            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - Party lista " + lista);
            assertEquals(E.PARTY_DISABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            lista = delegator.findList("UserLogin", EntityCondition.makeCondition(E.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - lista " + lista);

            Debug.log("Secondo caricamento Controllo 14 con 2 relazione con ORG1 scadute e 1 relazione 13 scaduta");
            checkPartyRelationship(empl14PartyId, 1, org1PartyId, THRU_DATE_NOV_2019, 1, org1PartyId, THRU_DATE_NOV_2019, 1, empl13PartyId, THRU_DATE_NOV_2019, 0, null, null);

            Debug.log("Secondo caricamento Controllo 15 con 2 relazione con ORG2 scadute e 1 relazione 13 scaduta");
            checkPartyRelationship(empl15PartyId, 1, org2PartyId, THRU_DATE_NOV_2019, 1, org2PartyId, THRU_DATE_NOV_2019, 1, empl13PartyId, THRU_DATE_NOV_2019, 0, null, null);

            // 13 cambia ORG2 a ORG3
            // 14 reopen
            // 15 reopen without org
            // 16 ha date nel futuro
            Debug.log("Terzo caricamento");
            setContextAndRunPersonInterfaceUpdate("PersonInterface_ThruDate_change.xls", 1, 4);

            lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - lista " + lista);

            lista = delegator.findList("PartyRole", EntityCondition.makeCondition(E.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - lista " + lista);

            Debug.log("Terzo caricamento Controllo 13 con 2 relazione");
            checkPartyRelationship(empl13PartyId, 2, null, null, 2, null, null, 0, null, null, 0, null, null);

            lista = delegator.findList("UserLogin", EntityCondition.makeCondition(E.partyId.name(), empl13PartyId), null, null, null, false);
            Debug.log(" - lista " + lista);

            lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - lista " + lista);
            
            lista = delegator.findList("PartyRole", EntityCondition.makeCondition(E.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - lista " + lista);

            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - Party lista " + lista);
            assertEquals(E.PARTY_ENABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            Debug.log("Terzo caricamento Controllo 14 con 2 relazione con ORG1");
            checkPartyRelationship(empl14PartyId, 2, null, null, 2, null, null, 1, empl13PartyId, THRU_DATE_NOV_2019, 0, null, null);

            lista = delegator.findList("UserLogin", EntityCondition.makeCondition(E.partyId.name(), empl14PartyId), null, null, null, false);
            Debug.log(" - lista " + lista);
            
            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), empl15PartyId), null, null, null, false);
            Debug.log(" - Party lista " + lista);
            assertEquals(E.PARTY_ENABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            Debug.log("Terzo caricamento Controllo 15 con 1 relazione con ORG2 e 1 con ");
            checkPartyRelationship(empl15PartyId, 1, org2PartyId, null, 1, org2PartyId, null, 1, empl13PartyId, THRU_DATE_NOV_2019, 0, null, null);

        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
}
