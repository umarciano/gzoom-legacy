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
 * Test for insert and update organization with thruDate
 *
 */
public class TestOrganizationStandardImportThruDate extends BaseOrganizationStandardImportUploadFileTestCase {

    protected static final Timestamp THRU_DATE_NOV_2019 = new Timestamp(UtilDateTime.toDate(11, 30, 2019, 0, 0, 0).getTime());
    protected static final Timestamp THRU_DATE_DIC_2019 = new Timestamp(UtilDateTime.toDate(12, 31, 2019, 0, 0, 0).getTime());
    
    private static final String ORG100 = "ORG100";
    private static final String ORG200 = "ORG200";
    private static final String ORG101 = "ORG101"; 
    private static final String ORG102 = "ORG102"; 
    private static final String RESP14 = "RESP14";
    private static final String RESP16 = "RESP16";

    /**
     * 3 Caricamenti di file excel per testare le modif
     */
    public void testOrganizationInterfaceThruDate() {
        try {
            // Responsabili gia' esistenti
            String resp14PartyId = getPartyId(RESP14);
            String resp16PartyId = getPartyId(RESP16);
            
            Debug.log("Primo caricamento con la creazione di 4 unita organizzative");
            setContextAndRunOrganizationInterfaceUpdate("OrganizationInterface_thruDate.xls", 0, 9); // TODO cosa fare con data futura

            // Organizzazioni appena create
            String org100PartyId = getPartyId(ORG100);
            String org200PartyId = getPartyId(ORG200);
            String org101PartyId = getPartyId(ORG101);
            String org102PartyId = getPartyId(ORG102);
            
            Debug.log("Primo caricamento - org100PartyId " + org100PartyId);
            List<GenericValue> lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), org100PartyId), null, null, null, false);
            Debug.log("Primo caricamento - PartyHistoryView lista " + lista);
            assertEquals(0, lista.size());
            
            lista = delegator.findList("PartyRole", EntityCondition.makeCondition(E.partyId.name(), org100PartyId), null, null, null, false);
            Debug.log("Primo caricamento - PartyRole lista " + lista);
            assertEquals(1, lista.size());
            
            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), org100PartyId), null, null, null, false);
            Debug.log("Primo caricamento - Party lista " + lista);
            assertEquals(E.PARTY_ENABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            Debug.log("Primo caricamento Controllo che ORG100 abbia 1 relazione con ORG101 scaduta e 1 relazione con la Company");
            checkPartyRelationship(org100PartyId, 1, org101PartyId, THRU_DATE_NOV_2019, 1, "Company", null, 1, resp14PartyId, null);

            lista = delegator.findList(TemplateEnum.PartyHistoryView.name(), EntityCondition.makeCondition(TemplateEnum.partyId.name(), org200PartyId), null, null, null, false);
            Debug.log("Primo caricamento - PartyHistoryView lista " + lista);
            assertEquals(0, lista.size());
            
            lista = delegator.findList("PartyRole", EntityCondition.makeCondition(E.partyId.name(), org200PartyId), null, null, null, false);
            Debug.log("Primo caricamento - PartyRole lista " + lista);
            assertEquals(1, lista.size());
            
            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), org200PartyId), null, null, null, false);
            Debug.log("Primo caricamento - Party lista " + lista);
            assertEquals(E.PARTY_ENABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            Debug.log("Primo caricamento Controllo che ORG200 abbia 1 relazione con ORG102 e 1 relazione con la Company");
            checkPartyRelationship(org200PartyId, 1, org102PartyId, null, 1, "Company", null, 1, resp14PartyId, null);
            
            // 100 has thruDate, so disabled party and 1 relationship 
            // 101 already disabled
            // 102 same refDate, from 200 to 100, so 1 delete
            Debug.log("Secondo caricamento");
            setContextAndRunOrganizationInterfaceUpdate("OrganizationInterface_thruDate_sameRefDate.xls", 0, 5);
            
            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), org100PartyId), null, null, null, false);
            Debug.log("Secondo caricamento - Party lista " + lista);
            assertEquals(E.PARTY_DISABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), org101PartyId), null, null, null, false);
            Debug.log("Secondo caricamento - Party lista " + lista);
            assertEquals(E.PARTY_DISABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            Debug.log("Secondo caricamento Controllo che ORG100 abbia 2 relazione con figli e 1 relazione con la Company scaduta e 1 responsabile scaduto");
            checkPartyRelationship(org100PartyId, 2, null, null, 1, "Company", THRU_DATE_DIC_2019, 1, resp16PartyId, THRU_DATE_DIC_2019);
            Debug.log("Secondo caricamento Controllo che ORG101 abbia 1 relazione con ORG100 scaduta");
            checkPartyRelationship(org101PartyId, 0, null, null, 1, org100PartyId, THRU_DATE_NOV_2019, 0, null, null);
            
            Debug.log("Secondo caricamento Controllo che ORG102 abbia 1 relazione con ORG100 scaduta e 1 responsabile scaduto");
            checkPartyRelationship(org102PartyId, 0, null, null, 1, org100PartyId, THRU_DATE_DIC_2019, 1, resp16PartyId, THRU_DATE_DIC_2019);
            
            Debug.log("Secondo caricamento Controllo che ORG200 abbia 1 relazione con la Company");
            checkPartyRelationship(org200PartyId, 0, null, null, 1, "Company", null, 1, resp14PartyId, null);
            
            // 100 already disabled
            // 101 reopen
            // 102 already disabled
            Debug.log("Terzo caricamento");
            setContextAndRunOrganizationInterfaceUpdate("OrganizationInterface_thruDate_reopen.xls", 0, 3);
            
            lista = delegator.findList("Party", EntityCondition.makeCondition(E.partyId.name(), org101PartyId), null, null, null, false);
            Debug.log("Terzo caricamento - Party lista " + lista);
            assertEquals(E.PARTY_ENABLED.name(), lista.get(0).getString(E.statusId.name()));
            
            Debug.log("Terzo caricamento Controllo che ORG101 abbia 1 relazione con ORG100 scaduta");
            checkPartyRelationship(org101PartyId, 0, null, null, 1, org200PartyId, null, 0, null, null);
            
        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }
}
