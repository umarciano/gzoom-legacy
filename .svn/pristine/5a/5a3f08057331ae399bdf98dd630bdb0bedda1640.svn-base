package com.mapsengineering.workeffortext.test.report;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;

import com.mapsengineering.base.test.BaseTestCase;
import com.mapsengineering.base.util.closeable.Closeables;
import com.mapsengineering.workeffortext.birt.comparingVersions.AllegatoComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.AllegatoUpdateComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.AssegnazioneComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.AssegnazioneUpdateComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.CardsComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.CardsComparingVersionReport;
import com.mapsengineering.workeffortext.birt.comparingVersions.CardsUpdateComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.MisuraComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.MisuraUpdateComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.ObiettivoUpdateComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.RelazioneComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.RelazioneUpdateComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.RuoloComparingVersion;
import com.mapsengineering.workeffortext.birt.comparingVersions.RuoloUpdateComparingVersion;

/**
 * TestCardsComparingVersion
 *
 */
public class TestCardsComparingVersion extends BaseTestCase {

    public static final String MODULE = TestCardsComparingVersion.class.getName();

    /**
     * testCardsComparingVersion
     */
    public void testCardsComparingVersion() {
        try {

            updateWorkEffort();

            Map<String, Object> parameters = FastMap.newInstance();
            parameters.put("userProfile", "ROLE_ADMIN");
            parameters.put("exposeDetailIndicator", "N");
            parameters.put("excludeHumanResource", "N");
            parameters.put("excludeRequest", "N");
            parameters.put("excludeCollegati", "N");
            parameters.put("excludeReference", "N");
            parameters.put("excludeIndicator", "N");
            parameters.put("monitoringDate", new Timestamp(UtilDateTime.toDate(12, 31, 2012, 0, 0, 0).getTime()));
            parameters.put("selectNote", "DATE");
            parameters.put("workEffortTypeId", "PEG12");
            parameters.put("parentTypeId", "CTX_BS");
            parameters.put("userLoginId", "admin");
            parameters.put("workEffortRevisionIdComp", "R10002");
            Map<String, Object> context = FastMap.newInstance();
            context.put("birtParameters", parameters);
            context.put("userLoginId", "admin");

            CardsComparingVersionReport cards = new CardsComparingVersionReport(context);

            int count = 0;
            Iterator<CardsComparingVersion> iAdd = cards.getAddList();
            try {
                while (iAdd.hasNext()) {
                    CardsComparingVersion add = iAdd.next();
                    Debug.log("- testCardsComparingVersion scheda add=" + add.getWorkEffortId());
                    count++;
                }

            } finally {
                Closeables.close(iAdd);
            }
            Debug.log("- testCardsComparingVersion scheda add count=" + count);
            if (count != 3) {
                assertTrue(false);
            }

            count = 0;
            Iterator<CardsComparingVersion> iDelete = cards.getDeleteList();
            try {

                while (iDelete.hasNext()) {
                    CardsComparingVersion delete = iDelete.next();
                    Debug.log("- testCardsComparingVersion delete=" + delete.getWorkEffortId());
                    count++;
                }

            } finally {
                Closeables.close(iAdd);
            }
            Debug.log("- testCardsComparingVersion scheda delete count=" + count);
            if (count != 0) {
                assertTrue(false);
            }

            Iterator<CardsUpdateComparingVersion> iUpdate = cards.getUpdateList();
            controlUpdateComparingVersion(iUpdate);

            assertTrue(true);

        } catch (Exception e) {
            Debug.logError(e, MODULE);
            assertTrue(false);
        }
    }

    private void controlUpdateComparingVersion(Iterator<CardsUpdateComparingVersion> i) throws SQLException, IOException {
        try {

            while (i.hasNext()) {
                CardsUpdateComparingVersion ele = i.next();
                Debug.log("- testCardsComparingVersion Obiettivo=" + ele.getWorkEffortId());

                // controllo solo l'obiettivo W30000
                if (ele.getWorkEffortId().equals("W30000")) {
                    int count = 0;
                    Iterator<CardsComparingVersion> iAdd = ele.getObiettivoAddList();
                    try {

                        while (iAdd.hasNext()) {
                            CardsComparingVersion add = iAdd.next();
                            Debug.log("- testCardsComparingVersion obiettivo add=" + add.getWorkEffortId());
                            count++;
                        }

                    } finally {
                        Closeables.close(iAdd);
                    }
                    Debug.log("- testCardsComparingVersion obiettivo add count=" + count);
                     if (count != 1) {
                         assertTrue(false);
                     }

                    count = 0;
                    Iterator<CardsComparingVersion> iDelete = ele.getObiettivoDeleteList();
                    try {

                        while (iDelete.hasNext()) {
                            CardsComparingVersion delete = iDelete.next();
                            Debug.log("- testCardsComparingVersion obiettivo delete=" + delete.getWorkEffortId());
                            count++;
                        }

                    } finally {
                        Closeables.close(iAdd);
                    }
                    Debug.log("- testCardsComparingVersion obiettivo delete count=" + count);
                    if (count != 0) {
                        assertTrue(false);
                    }

                    count = 0;
                    Iterator<ObiettivoUpdateComparingVersion> iUpdate = ele.getObiettivoUpdateList();
                    controlUpdateObiettivo(iUpdate);
                }
            }

        } finally {
            Closeables.close(i);
        }

    }

    private void controlUpdateObiettivo(Iterator<ObiettivoUpdateComparingVersion> i) throws SQLException, IOException {
        int countUpdate = 0;
        try {
            while (i.hasNext()) {
                ObiettivoUpdateComparingVersion update = i.next();
                Debug.log("- testCardsComparingVersion obiettivo update=" + update.getWorkEffortId() + ": IsModified=" + update.getIsModified());

                /**
                 * Controllo se sono stati modificati 
                 * 
                 */
                checkAssegnazioni(update);
                checkRuoli(update);
                checkAllegato(update);
                checkRelazioni(update);
                checkMisure(update);
                countUpdate++;
            }

        } finally {
            Closeables.close(i);
        }
        Debug.log("- testCardsComparingVersion obiettivo update count=" + countUpdate);
        if (countUpdate != 1) {
            assertTrue(false);
        }

    }

    /**
     * controllo misure
     * @param updateObiettivo
     * @throws SQLException
     */
    private void checkMisure(ObiettivoUpdateComparingVersion updateObiettivo) throws SQLException {
        int countAdd = 0;
        Iterator<MisuraComparingVersion> iAdd = updateObiettivo.getMisuraAddList();
        try {

            while (iAdd.hasNext()) {
                MisuraComparingVersion add = iAdd.next();
                Debug.log("- testCardsComparingVersion MisuraComparingVersion add=" + add.getAccountName());
                countAdd++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion MisuraComparingVersion add count=" + countAdd);

        int countDelete = 0;
        Iterator<MisuraComparingVersion> iDelete = updateObiettivo.getMisuraDeleteList();
        try {

            while (iDelete.hasNext()) {
                MisuraComparingVersion delete = iDelete.next();
                Debug.log("- testCardsComparingVersion MisuraComparingVersion delete=" + delete.getAccountName());
                countDelete++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion MisuraComparingVersion delete count=" + countDelete);

        int countUpdate = 0;
        Iterator<MisuraUpdateComparingVersion> iUpdate = updateObiettivo.getMisuraUpdateList();
        try {

            while (iUpdate.hasNext()) {
                MisuraUpdateComparingVersion update = iUpdate.next();
                Debug.log("- testCardsComparingVersion MisuraUpdateComparingVersion update=" + update.getAccountName());
                countUpdate++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion MisuraUpdateComparingVersion update count=" + countUpdate);

        if (countAdd != 0 || countDelete != 0 || countUpdate != 0) {
            assertTrue(false);
        }

    }

    /**
     * controllo relazioni
     * @param updateObiettivo
     * @throws SQLException
     */
    private void checkRelazioni(ObiettivoUpdateComparingVersion updateObiettivo) throws SQLException {
        int countAdd = 0;
        Iterator<RelazioneComparingVersion> iAdd = updateObiettivo.getRelazioneAddList();
        try {

            while (iAdd.hasNext()) {
                RelazioneComparingVersion add = iAdd.next();
                Debug.log("- testCardsComparingVersion RelazioneComparingVersion add=" + add.getWorkEffortName());
                countAdd++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion RelazioneComparingVersion add count=" + countAdd);

        int countDelete = 0;
        Iterator<RelazioneComparingVersion> iDelete = updateObiettivo.getRelazioneDeleteList();
        try {

            while (iDelete.hasNext()) {
                RelazioneComparingVersion delete = iDelete.next();
                Debug.log("- testCardsComparingVersion RelazioneComparingVersion delete=" + delete.getWorkEffortName());
                countDelete++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion RelazioneComparingVersion delete count=" + countDelete);

        int countUpdate = 0;
        Iterator<RelazioneUpdateComparingVersion> iUpdate = updateObiettivo.getRelazioneUpdateList();
        try {

            while (iUpdate.hasNext()) {
                RelazioneUpdateComparingVersion update = iUpdate.next();
                Debug.log("- testCardsComparingVersion RelazioneUpdateComparingVersion update=" + update.getWorkEffortName());
                countUpdate++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion RelazioneUpdateComparingVersion update count=" + countUpdate);
        //TODO countAdd != 0 BHO
        if (countDelete != 0 || countUpdate != 0) {
            assertTrue(false);
        }

    }

    /**
     * controllo allegato
     * @param updateObiettivo
     * @throws SQLException
     */
    private void checkAllegato(ObiettivoUpdateComparingVersion updateObiettivo) throws SQLException {
        int countAdd = 0;
        Iterator<AllegatoComparingVersion> iAdd = updateObiettivo.getAllegatoAddList();
        try {

            while (iAdd.hasNext()) {
                AllegatoComparingVersion add = iAdd.next();
                Debug.log("- testCardsComparingVersion AllegatoComparingVersion add=" + add.getDataResourceName());
                countAdd++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion AllegatoComparingVersion add count=" + countAdd);

        int countDelete = 0;
        Iterator<AllegatoComparingVersion> iDelete = updateObiettivo.getAllegatoDeleteList();
        try {

            while (iDelete.hasNext()) {
                AllegatoComparingVersion delete = iDelete.next();
                Debug.log("- testCardsComparingVersion AllegatoComparingVersion delete=" + delete.getDataResourceName());
                countDelete++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion AllegatoComparingVersion delete count=" + countDelete);

        int countUpdate = 0;
        Iterator<AllegatoUpdateComparingVersion> iUpdate = updateObiettivo.getAllegatoUpdateList();
        try {

            while (iUpdate.hasNext()) {
                AllegatoUpdateComparingVersion update = iUpdate.next();
                Debug.log("- testCardsComparingVersion AllegatoUpdateComparingVersion update=" + update.getDataResourceName());
                countUpdate++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion AllegatoUpdateComparingVersion update count=" + countUpdate);

        if (countAdd != 0 || countDelete != 0 || countUpdate != 0) {
            assertTrue(false);
        }

    }

    /**
     * controllo sulle allegato
     * @param updateObiettivo
     * @throws SQLException
     */
    private void checkRuoli(ObiettivoUpdateComparingVersion updateObiettivo) throws SQLException {
        int countAdd = 0;
        Iterator<RuoloComparingVersion> iAdd = updateObiettivo.getRuoloAddList();
        try {

            while (iAdd.hasNext()) {
                RuoloComparingVersion add = iAdd.next();
                Debug.log("- testCardsComparingVersion RuoloComparingVersion add=" + add.getPartyName());
                countAdd++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion RuoloComparingVersion add count=" + countAdd);

        int countDelete = 0;
        Iterator<RuoloComparingVersion> iDelete = updateObiettivo.getRuoloDeleteList();
        try {

            while (iDelete.hasNext()) {
                RuoloComparingVersion delete = iDelete.next();
                Debug.log("- testCardsComparingVersion RuoloComparingVersion delete=" + delete.getPartyName());
                countDelete++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion RuoloComparingVersion delete count=" + countDelete);

        int countUpdate = 0;
        Iterator<RuoloUpdateComparingVersion> iUpdate = updateObiettivo.getRuoloUpdateList();
        try {

            while (iUpdate.hasNext()) {
                RuoloUpdateComparingVersion update = iUpdate.next();
                Debug.log("- testCardsComparingVersion RuoloUpdateComparingVersion update=" + update.getPartyName());
                countUpdate++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion RuoloUpdateComparingVersion update count=" + countUpdate);

        if (countAdd != 1 || countDelete != 0 || countUpdate != 2) {
            assertTrue(false);
        }

    }

    /**
     * controllo sui assegnazioni
     * @param updateObiettivo
     * @throws SQLException
     */
    private void checkAssegnazioni(ObiettivoUpdateComparingVersion updateObiettivo) throws SQLException {
        int countAdd = 0;
        Iterator<AssegnazioneComparingVersion> iAdd = updateObiettivo.getAssegnazioneAddList();
        try {

            while (iAdd.hasNext()) {
                AssegnazioneComparingVersion add = iAdd.next();
                Debug.log("- testCardsComparingVersion AssegnazioneComparingVersion add=" + add.getPartyName());
                countAdd++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion AssegnazioneComparingVersion add count=" + countAdd);

        int countDelete = 0;
        Iterator<AssegnazioneComparingVersion> iDelete = updateObiettivo.getAssegnazioneDeleteList();
        try {

            while (iDelete.hasNext()) {
                AssegnazioneComparingVersion delete = iDelete.next();
                Debug.log("- testCardsComparingVersion AssegnazioneComparingVersion delete=" + delete.getPartyName());
                countDelete++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion AssegnazioneComparingVersion delete count=" + countDelete);

        int countUpdate = 0;
        Iterator<AssegnazioneUpdateComparingVersion> iUpdate = updateObiettivo.getAssegnazioneUpdateList();
        try {

            while (iUpdate.hasNext()) {
                AssegnazioneUpdateComparingVersion update = iUpdate.next();
                Debug.log("- testCardsComparingVersion AssegnazioneComparingVersion update=" + update.getPartyName());
                countUpdate++;
            }

        } finally {
            Closeables.close(iAdd);
        }
        Debug.log("- testCardsComparingVersion AssegnazioneComparingVersion update count=" + countUpdate);

        if (countAdd != 1 || countDelete != 1 || countUpdate != 1) {
            assertTrue(false);
        }

    }

    /**
     * Modifico i dati per differenzirae le scheda storicizzata da quella originale!
     * @throws Exception 
     */
    private void updateWorkEffort() throws Exception {
        // Aggiungo una scheda
        Map<String, Object> contextCreate = FastMap.newInstance();
        contextCreate.putAll(context);
        contextCreate.put("workEffortParentId", "W30000");
        contextCreate.put("workEffortTypeId", "PEG12");
        contextCreate.put("currentStatusId", "WEPERFST_PLANINIT");
        contextCreate.put("workEffortName", "Prova scheda inserimento");
        contextCreate.put("estimatedStartDate", "2010-01-01 00:00:00.0");
        contextCreate.put("estimatedCompletionDate", "2012-12-31 00:00:00.0");
        contextCreate.put("sourceReferenceId", "1.0");
        contextCreate.put("organizationId", "Company");
        contextCreate.put("orgUnitRoleTypeId", "UOSET");
        contextCreate.put("orgUnitId", "RCP10022");
        contextCreate.put("isPosted", "N");
        dispatcher.runSync("createWorkEffort", contextCreate);

        //aggiungo all'obiettivo W30000 una assegnazione
        Map<String, Object> update = FastMap.newInstance();
        update.putAll(context);
        update.put("partyId", "RCP10192");
        update.put("workEffortId", "W30000");
        update.put("roleTypeId", "WE_ASSIGNMENT");
        update.put("fromDate", "2010-01-01 00:00:00");
        update.put("thruDate", "2012-12-31 00:00:00.0");
        update.put("statusId", "PRTYASGN_ASSIGNED");
        dispatcher.runSync("assignPartyToWorkEffort", update);

        //aggiungo all'obiettivo W30000 una ruolo
        update.put("partyId", "RCP10036");
        update.put("roleTypeId", "WEM_ASSESORE");
        dispatcher.runSync("assignPartyToWorkEffort", update);

        //modifico all'obiettivo W30000 una 
        update.put("partyId", "E10001");
        update.put("roleTypeId", "WE_ASSIGNMENT");
        update.put("comments", "prova modifico commento");
        dispatcher.runSync("updatePartyToWorkEffortAssignment", update);

        //modifico all'obiettivo W30000 una 
        update.put("partyId", "DRESP14");
        update.put("roleTypeId", "UOSET");
        update.put("roleTypeWeight", "90");
        dispatcher.runSync("updatePartyToWorkEffortAssignment", update);

        //modifico all'obiettivo W30000 una 
        update.put("partyId", "RCP10022");
        update.put("roleTypeId", "UOSET");
        update.put("thruDate", "2013-12-31 00:00:00");
        dispatcher.runSync("updatePartyToWorkEffortAssignment", update);

        //elimino all'obiettivo W30000 una assegnazione 
        Map<String, Object> delete = FastMap.newInstance();
        delete.putAll(context);
        delete.put("partyId", "DRESP14");
        delete.put("workEffortId", "W30000");
        delete.put("roleTypeId", "WE_ASSIGNMENT");
        delete.put("fromDate", "2010-01-01 00:00:00");
        dispatcher.runSync("unassignPartyFromWorkEffort", delete);

    }

}
