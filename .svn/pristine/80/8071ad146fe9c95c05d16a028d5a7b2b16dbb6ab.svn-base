package com.mapsengineering.workeffortext.pub.services.impl.mockup;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.mapsengineering.workeffortext.pub.data.PubGlAccountData;
import com.mapsengineering.workeffortext.pub.data.PubGlAccountHistoryData;
import com.mapsengineering.workeffortext.pub.data.PubWorkEffortData;
import com.mapsengineering.workeffortext.pub.services.PublicInterface;
import com.mapsengineering.workeffortext.pub.services.PublicInterfaceException;

public class PublicInterfaceMockup implements PublicInterface {

    private static final String EMOT_GREEN = "/content/control/stream?contentId=EMOT_GREEN";
    private static final String EMOT_YELLOW = "/content/control/stream?contentId=EMOT_YELLOW";
    private static final String EMOT_RED = "/content/control/stream?contentId=EMOT_RED";

    private static final String DESC_WE01 = "Il comune si occupa della gestione dei nidi d'infanzia comunali e delle iscrizioni nei nidi comunali e convenzionati, oltre all'erogazione di contributi per la frequenza ai nidi convenzionati. Gli asili nido comunali sono 7 e mettono a disposizione 353 posti. Altri 244 sono messi a disposizione dai nidi convenzionati";
    private static final String DESC_WE02 = "Quella delle mense biologiche nelle scuole \u00e8 gi\u00e0 una realt\u00e0 molto importante in Italia: sono pi\u00f9 di un centinaio i comuni che le hanno istituite e la ristorazione scolastica biologica, oggi, \u00e8 diffusa in 12 regioni, 34 province e 103 comuni ed i pasti biologici distribuiti giornalmente sono circa 90.000. Nelle esperienze in corso alcune sono simili al Comune di XXX, pioniere in questa innovazione, dove esiste la presenza completa di prodotti biologici;";
    private static final String DESC_WE03 = "Organizzazione delle linee di trasporto scolastico in collaborazione con gli Istituti scolastici e l'agenzia di trasporto pubblico Start Romagna, in relazione alle richieste dell'utenza. Il servizio di trasporto scolastico \u00e8 dedicato agli alunni delle scuole primarie del comune e del circondario. Il servizio comprende l'assistenza al trasporto scolastico di minori non autonomi e per il trasporto speciale rivolto ad alunni disabili.";

    private enum E {
        WE01, WE02, WE03 //
        , GA0101, GA0102, GA0103, GA0104, GA0105 //
        , GA0201, GA0202, GA0203 //
        , GA0301, GA0302, GA0303, GA0304 //
    }

    @Override
    public List<PubWorkEffortData> getWorkEffortDataByType(Timestamp periodDate, String glFiscalTypeId, String workEffortTypeId) throws PublicInterfaceException {
        final List<PubWorkEffortData> list = new ArrayList<PubWorkEffortData>();
        list.add(getWorkEffortData(periodDate, glFiscalTypeId, E.WE01.name()));
        list.add(getWorkEffortData(periodDate, glFiscalTypeId, E.WE02.name()));
        list.add(getWorkEffortData(periodDate, glFiscalTypeId, E.WE03.name()));
        return list;
    }

    @Override
    public PubWorkEffortData getWorkEffortData(Timestamp periodDate, String glFiscalTypeId, String sourceReferenceId) throws PublicInterfaceException {
        final PubWorkEffortData data;
        if (E.WE01.name().equals(sourceReferenceId)) {
            data = new PubWorkEffortData(sourceReferenceId, "Asili nido", DESC_WE01, gauge("090"));
        } else if (E.WE02.name().equals(sourceReferenceId)) {
            data = new PubWorkEffortData(sourceReferenceId, "Servizio di mensa per il sistema scolastico", DESC_WE02, gauge("100"));
        } else if (E.WE03.name().equals(sourceReferenceId)) {
            data = new PubWorkEffortData(sourceReferenceId, "Trasporto scolastico", DESC_WE03, gauge("075"));
        } else {
            throw new PublicInterfaceException("Servizio non trovato: " + sourceReferenceId);
        }
        return data;
    }

    @Override
    public List<PubGlAccountData> getGlAccountData(Timestamp periodDate, String glFiscalTypeId, String sourceReferenceId) throws PublicInterfaceException {
        final List<PubGlAccountData> list = new ArrayList<PubGlAccountData>();
        if (E.WE01.name().equals(sourceReferenceId)) {
            list.add(new PubGlAccountData(E.GA0101.name(), "Perc", "Bambini accolti rispetto alle domande", null, new BigDecimal("97"), new BigDecimal("92"), EMOT_GREEN));
            list.add(new PubGlAccountData(E.GA0102.name(), "Num", "Numero settimane di apertura", null, new BigDecimal("39"), new BigDecimal("40"), EMOT_GREEN));
            list.add(new PubGlAccountData(E.GA0103.name(), "Perc", "Grado di copertura spese da parte degli utenti", null, new BigDecimal("32"), new BigDecimal("34"), EMOT_GREEN));
            list.add(new PubGlAccountData(E.GA0104.name(), "Num", "Rapporto insegnanti/Bambini", null, new BigDecimal("0.12"), new BigDecimal("0.15"), EMOT_YELLOW));
            list.add(new PubGlAccountData(E.GA0105.name(), "Num", "Posti complessivi disponibili", null, new BigDecimal("618"), new BigDecimal("640"), EMOT_GREEN));
        } else if (E.WE02.name().equals(sourceReferenceId)) {
            list.add(new PubGlAccountData(E.GA0201.name(), "EUR", "Costo unitario pasto", null, new BigDecimal("5.4"), new BigDecimal("5.4"), EMOT_GREEN));
            list.add(new PubGlAccountData(E.GA0202.name(), "Num", "Numero pasti", null, new BigDecimal("507342"), new BigDecimal("510000"), EMOT_GREEN));
            list.add(new PubGlAccountData(E.GA0203.name(), "Num", "Totale utenti", null, new BigDecimal("5457"), new BigDecimal("5300"), EMOT_GREEN));
        } else if (E.WE03.name().equals(sourceReferenceId)) {
            list.add(new PubGlAccountData(E.GA0301.name(), "Num", "Totale utenti", null, new BigDecimal("912"), new BigDecimal("950"), EMOT_GREEN));
            list.add(new PubGlAccountData(E.GA0302.name(), "Num", "Bambini con trasporto gratuito", null, new BigDecimal("12"), new BigDecimal("20"), EMOT_RED));
            list.add(new PubGlAccountData(E.GA0303.name(), "Perc", "% rette con riduzione ISEE", null, new BigDecimal("13"), new BigDecimal("6"), EMOT_GREEN));
            list.add(new PubGlAccountData(E.GA0304.name(), "EUR", "Costo medio complessivo per utente", null, new BigDecimal("899"), new BigDecimal("887"), EMOT_GREEN));
        } else {
            throw new PublicInterfaceException("Servizio non trovato: " + sourceReferenceId);
        }
        return list;
    }

    @Override
    public PubGlAccountHistoryData getGlAccountHistoryData(Timestamp periodDate, String glFiscalTypeId, String sourceReferenceId, String id) throws PublicInterfaceException {
        final PubGlAccountHistoryData data;
        if (E.WE01.name().equals(sourceReferenceId)) {
            if (is(id, E.GA0101) || is(id, E.GA0102) || is(id, E.GA0103) || is(id, E.GA0104) || is(id, E.GA0105)) {
                data = new PubGlAccountHistoryData(graph(id));
            } else {
                throw new PublicInterfaceException("Indicatore non trovato: " + id);
            }
        } else if (E.WE02.name().equals(sourceReferenceId)) {
            if (is(id, E.GA0201) || is(id, E.GA0202) || is(id, E.GA0203)) {
                data = new PubGlAccountHistoryData(graph(id));
            } else {
                throw new PublicInterfaceException("Indicatore non trovato: " + id);
            }
        } else if (E.WE03.name().equals(sourceReferenceId)) {
            if (is(id, E.GA0301) || is(id, E.GA0302) || is(id, E.GA0303) || is(id, E.GA0304)) {
                data = new PubGlAccountHistoryData(graph(id));
            } else {
                throw new PublicInterfaceException("Indicatore non trovato: " + id);
            }
        } else {
            throw new PublicInterfaceException("Servizio non trovato: " + sourceReferenceId);
        }
        return data;
    }

    private boolean is(String glAccountId, E glAccountEnum) {
        return glAccountEnum.name().equals(glAccountId);
    }

    private String gauge(String suffix) {
        return "/workeffortpub/images/test/gauge-" + suffix + ".png";
    }

    private String graph(String suffix) {
        return "/workeffortpub/images/test/graph-" + suffix + ".png";
    }
}
