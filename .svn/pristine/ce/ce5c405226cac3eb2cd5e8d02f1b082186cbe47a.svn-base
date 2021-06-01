package com.mapsengineering.workeffortext.pub.services;

import java.sql.Timestamp;
import java.util.List;

import com.mapsengineering.workeffortext.pub.data.PubGlAccountData;
import com.mapsengineering.workeffortext.pub.data.PubGlAccountHistoryData;
import com.mapsengineering.workeffortext.pub.data.PubWorkEffortData;

/**
 * Interfaccia per ottenere i dati pubblici del sistema.
 * I dati pubblici possono essere esposti a sistemi esterni.
 * @author sivi
 *
 */
public interface PublicInterface {

    /**
     * Ottiene i dati relativi ad obiettivi del tipo indicato.
     * @param periodDate Periodo di riferimento (data fine)
     * @param glFiscalTypeId Tipo di rilevazione (default consuntivo)
     * @param workEffortTypeId Tipo obiettivo
     * @return Lista di dati pubblici
     * @throws PublicInterfaceException
     */
    public List<PubWorkEffortData> getWorkEffortDataByType(Timestamp periodDate, String glFiscalTypeId, String workEffortTypeId) throws PublicInterfaceException;

    /**
     * Ottiene i dati relativi ad un obiettivo indicato tramite codice (sourceReferenceId).
     * @param periodDate Periodo di riferimento (data fine)
     * @param glFiscalTypeId Tipo di rilevazione (default consuntivo)
     * @param sourceReferenceId Codice obiettivo
     * @return Dati pubblici
     * @throws PublicInterfaceException
     */
    public PubWorkEffortData getWorkEffortData(Timestamp periodDate, String glFiscalTypeId, String sourceReferenceId) throws PublicInterfaceException;

    /**
     * Ottiene i dati relativi agli indicatori di un obiettivo.
     * @param periodDate Periodo di riferimento (data fine)
     * @param glFiscalTypeId Tipo di rilevazione (default consuntivo)
     * @param sourceReferenceId Codice obiettivo
     * @return Dati pubblici
     * @throws PublicInterfaceException
     */
    public List<PubGlAccountData> getGlAccountData(Timestamp periodDate, String glFiscalTypeId, String sourceReferenceId) throws PublicInterfaceException;

    /**
     * Ottiene i dati storici relativi ad un indicatore di un obiettivo.
     * @param periodDate Periodo di riferimento (data fine)
     * @param glFiscalTypeId Tipo di rilevazione (default consuntivo)
     * @param sourceReferenceId Codice obiettivo
     * @param glAccountId Codice dell'indicatore
     * @return Dati pubblici
     * @throws PublicInterfaceException
     */
    public PubGlAccountHistoryData getGlAccountHistoryData(Timestamp periodDate, String glFiscalTypeId, String sourceReferenceId, String glAccountId) throws PublicInterfaceException;
}
