package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.report.OfbizReportContext;
import com.mapsengineering.base.util.closeable.Closeables;
import com.mapsengineering.base.util.closeable.FilteredCloseableIterator;

public class ObiettivoUpdateComparingVersion extends CardsComparingVersion {

    private static final long serialVersionUID = 1L;

    //query
    private static final String queryAssegnazioneAddDelete = "sql/cardsComparingVersions/assegnazioni/assegnazioneAddDeleteList.sql.ftl";
    private static final String queryAssegnazioneUpdate = "sql/cardsComparingVersions/assegnazioni/assegnazioneUpdateList.sql.ftl";

    private static final String queryRuoloAddDelete = "sql/cardsComparingVersions/ruoli/ruoloAddDeleteList.sql.ftl";
    private static final String queryRuoloUpdate = "sql/cardsComparingVersions/ruoli/ruoloUpdateList.sql.ftl";

    private static final String queryRelazioneAdd = "sql/cardsComparingVersions/relazioni/relazioneAddList.sql.ftl";
    private static final String queryRelazioneDelete = "sql/cardsComparingVersions/relazioni/relazioneDeleteList.sql.ftl";
    private static final String queryRelazioneUpdate = "sql/cardsComparingVersions/relazioni/relazioneUpdateList.sql.ftl";

    private static final String queryNoteAddDelete = "sql/cardsComparingVersions/note/noteAddDeleteList.sql.ftl";
    private static final String queryNoteUpdate = "sql/cardsComparingVersions/note/noteUpdateList.sql.ftl";

    private static final String queryAllegatoAddDelete = "sql/cardsComparingVersions/allegato/allegatoAddDeleteList.sql.ftl";
    private static final String queryAllegatoUpdate = "sql/cardsComparingVersions/allegato/allegatoUpdateList.sql.ftl";

    private static final String queryMisuraAddDelete = "sql/cardsComparingVersions/misura/misuraAddDeleteList.sql.ftl";
    private static final String queryMisuraUpdate = "sql/cardsComparingVersions/misura/misuraUpdateList.sql.ftl";

    private static final String queryValoreAdd = "sql/cardsComparingVersions/valore/valoreAddList.sql.ftl";
    private static final String queryValoreDelete = "sql/cardsComparingVersions/valore/valoreDeleteList.sql.ftl";
    private static final String queryValoreUpdate = "sql/cardsComparingVersions/valore/valoreUpdateList.sql.ftl";

    private transient final OfbizReportContext ctx = OfbizReportContext.get();

    // Cache
    private transient Iterator<AssegnazioneComparingVersion> assegnazioneAddList;
    private transient Iterator<AssegnazioneComparingVersion> assegnazioneDeleteList;
    private transient Iterator<AssegnazioneUpdateComparingVersion> assegnazioneUpdateList;
    private transient Iterator<RuoloComparingVersion> ruoloAddList;
    private transient Iterator<RuoloComparingVersion> ruoloDeleteList;
    private transient Iterator<RuoloUpdateComparingVersion> ruoloUpdateList;
    private transient Iterator<RelazioneComparingVersion> relazioneAddList;
    private transient Iterator<RelazioneComparingVersion> relazioneDeleteList;
    private transient Iterator<RelazioneUpdateComparingVersion> relazioneUpdateList;
    private transient Iterator<NoteComparingVersion> noteAddList;
    private transient Iterator<NoteComparingVersion> noteDeleteList;
    private transient Iterator<NoteUpdateComparingVersion> noteUpdateList;
    private transient Iterator<AllegatoComparingVersion> allegatoAddList;
    private transient Iterator<AllegatoComparingVersion> allegatoDeleteList;
    private transient Iterator<AllegatoUpdateComparingVersion> allegatoUpdateList;
    private transient Iterator<MisuraComparingVersion> misuraAddList;
    private transient Iterator<MisuraComparingVersion> misuraDeleteList;
    private transient Iterator<MisuraUpdateComparingVersion> misuraUpdateList;
    private transient Iterator<ValoreComparingVersion> valoreAddList;
    private transient Iterator<ValoreComparingVersion> valoreDeleteList;
    private transient Iterator<ValoreUpdateComparingVersion> valoreUpdateList;

    public String getOldEtch() throws SQLException {
        return getRs().getString(E.OLD_ETCH.name());
    }

    public String getOldWorkEffortName() throws SQLException {
        return getRs().getString(E.OLD_WORK_EFFORT_NAME.name());
    }

    public String getOldDescription() throws SQLException {
        return getRs().getString(E.OLD_DESCRIPTION.name());
    }

    public String getOldPartyName() throws SQLException {
        return getRs().getString(E.OLD_PARTY_NAME.name());
    }

    public Timestamp getOldEstimatedStartDate() throws SQLException {
        return getRs().getTimestamp(E.OLD_ESTIMATED_START_DATE.name());
    }

    public Timestamp getOldEstimatedCompletionDate() throws SQLException {
        return getRs().getTimestamp(E.OLD_ESTIMATED_COMPLETION_DATE.name());
    }

    public String getOldWorkEffortId() throws SQLException {
        return getRs().getString(E.OLD_WORK_EFFORT_ID.name());
    }

    public String getOldWorkEffortTypeId() throws SQLException {
        return getRs().getString(E.OLD_WORK_EFFORT_TYPE_ID.name());
    }

    public String getOldTitle() throws SQLException {
        String oldTitle = "";
        if (UtilValidate.isNotEmpty(getOldEtch()))
            oldTitle = getOldEtch();

        if (UtilValidate.isNotEmpty(oldTitle))
            oldTitle += " - ";

        if (UtilValidate.isNotEmpty(getOldWorkEffortName()))
            oldTitle += getOldWorkEffortName();

        return oldTitle;
    }

    /**
     * Ritorno true per specificare se l obiettivo ha almeno una elemento modificato
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public Boolean getIsModified() throws SQLException, IOException {

        if (this.getAssegnazioneAddList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getAssegnazioneDeleteList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getAssegnazioneUpdateList().hasNext()) {
            return Boolean.TRUE;
        }

        if (this.getRuoloAddList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getRuoloDeleteList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getRuoloUpdateList().hasNext()) {
            return Boolean.TRUE;
        }

        if (this.getRelazioneAddList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getRelazioneDeleteList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getRelazioneUpdateList().hasNext()) {
            return Boolean.TRUE;
        }

        if (this.getNoteAddList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getNoteDeleteList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getNoteUpdateList().hasNext()) {
            return Boolean.TRUE;
        }

        if (this.getAllegatoAddList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getAllegatoDeleteList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getAllegatoUpdateList().hasNext()) {
            return Boolean.TRUE;
        }

        if (this.getMisuraAddList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getMisuraDeleteList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getMisuraUpdateList().hasNext()) {
            return Boolean.TRUE;
        }

        if (this.getValoreAddList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getValoreDeleteList().hasNext()) {
            return Boolean.TRUE;
        }
        if (this.getValoreUpdateList().hasNext()) {
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Override
    protected void onNext() {
        super.onNext();

        assegnazioneAddList = Closeables.close(assegnazioneAddList);
        assegnazioneDeleteList = Closeables.close(assegnazioneDeleteList);
        assegnazioneUpdateList = Closeables.close(assegnazioneUpdateList);

        ruoloAddList = Closeables.close(ruoloAddList);
        ruoloDeleteList = Closeables.close(ruoloDeleteList);
        ruoloUpdateList = Closeables.close(ruoloUpdateList);

        relazioneAddList = Closeables.close(relazioneAddList);
        relazioneDeleteList = Closeables.close(relazioneDeleteList);
        relazioneUpdateList = Closeables.close(relazioneUpdateList);

        noteAddList = Closeables.close(noteAddList);
        noteDeleteList = Closeables.close(noteDeleteList);
        noteUpdateList = Closeables.close(noteUpdateList);

        allegatoAddList = Closeables.close(allegatoAddList);
        allegatoDeleteList = Closeables.close(allegatoDeleteList);
        allegatoUpdateList = Closeables.close(allegatoUpdateList);

        misuraAddList = Closeables.close(misuraAddList);
        misuraDeleteList = Closeables.close(misuraDeleteList);
        misuraUpdateList = Closeables.close(misuraUpdateList);

        valoreAddList = Closeables.close(valoreAddList);
        valoreDeleteList = Closeables.close(valoreDeleteList);
        valoreUpdateList = Closeables.close(valoreUpdateList);
    }

    private MapContext<String, Object> mapContext() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(ctx);
        mapContext.push();
        return mapContext;
    }

    private MapContext<String, Object> mapContextAdd() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(E.param1.name(), getWorkEffortId());
        mapContext.put(E.param2.name(), getOldWorkEffortId());
        return mapContext;
    }

    private MapContext<String, Object> mapContextDelete() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(E.param1.name(), getOldWorkEffortId());
        mapContext.put(E.param2.name(), getWorkEffortId());
        return mapContext;
    }

    private MapContext<String, Object> mapContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(E.workEffortId.name(), getWorkEffortId());
        mapContext.put(E.oldWorkEffortId.name(), getOldWorkEffortId());
        return mapContext;
    }

    private String getExcludeQuery(String query, String paramName, String paramExclude) {
        String newQuery = query;
        if (((String)ctx.getParameters().get(paramName)).equals(paramExclude)) {
            newQuery = null;
        }
        return newQuery;
    }

    /*
     * Carico le ASSEGNAZIONI	 
     */
    /**
     * Ritorno iteratore della lista assegnazioni aggiunte
     * @return
     * @throws SQLException
     */
    public Iterator<AssegnazioneComparingVersion> getAssegnazioneAddList() throws SQLException {
        if (Closeables.isClosed(assegnazioneAddList)) {
            assegnazioneAddList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryAssegnazioneAddDelete, E.excludeHumanResource.name(), E.Y.name()), mapContextAdd()).iterate(new AssegnazioneComparingVersion());
        }
        return assegnazioneAddList;
    }

    /**
     * Ritorno iteratore della lista assegnazioni rimosse
     * @return
     * @throws SQLException
     */
    public Iterator<AssegnazioneComparingVersion> getAssegnazioneDeleteList() throws SQLException {
        if (Closeables.isClosed(assegnazioneDeleteList)) {
            assegnazioneDeleteList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryAssegnazioneAddDelete, E.excludeHumanResource.name(), E.Y.name()), mapContextDelete()).iterate(new AssegnazioneComparingVersion());
        }
        return assegnazioneDeleteList;
    }

    /**
     * Ritorno iteratore della lista assegnazioni modificate
     * @return
     * @throws SQLException
     */
    public Iterator<AssegnazioneUpdateComparingVersion> getAssegnazioneUpdateList() throws SQLException {
        if (Closeables.isClosed(assegnazioneUpdateList)) {
            assegnazioneUpdateList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryAssegnazioneUpdate, E.excludeHumanResource.name(), E.Y.name()), mapContextUpdate()).iterate(new AssegnazioneUpdateComparingVersion());
        }
        return assegnazioneUpdateList;
    }

    /*
     * Carico i RUOLI	 
     */
    /**
     * Ritorno iteratore della lista ruoli aggiunti
     * @return
     * @throws SQLException
     */
    public Iterator<RuoloComparingVersion> getRuoloAddList() throws SQLException {
        if (Closeables.isClosed(ruoloAddList)) {
            ruoloAddList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryRuoloAddDelete, E.excludeReference.name(), E.Y.name()), mapContextAdd()).iterate(new RuoloComparingVersion());
        }
        return ruoloAddList;
    }

    /**
     * Ritorno iteratore della lista ruoli rimossi
     * @return
     * @throws SQLException
     */
    public Iterator<RuoloComparingVersion> getRuoloDeleteList() throws SQLException {
        if (Closeables.isClosed(ruoloDeleteList)) {
            ruoloDeleteList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryRuoloAddDelete, E.excludeReference.name(), E.Y.name()), mapContextDelete()).iterate(new RuoloComparingVersion());
        }
        return ruoloDeleteList;
    }

    /**
     * Ritorno iteratore della lista ruoli modificati
     * @return
     * @throws SQLException
     */
    public Iterator<RuoloUpdateComparingVersion> getRuoloUpdateList() throws SQLException {
        if (Closeables.isClosed(ruoloUpdateList)) {
            ruoloUpdateList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryRuoloUpdate, E.excludeReference.name(), E.Y.name()), mapContextUpdate()).iterate(new RuoloUpdateComparingVersion());
        }
        return ruoloUpdateList;
    }

    /*
     * Carico i RELAZIONI    
     */

    private MapContext<String, Object> mapRelazioneContextAdd() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(E.param1.name(), getWorkEffortId());
        mapContext.put(E.param2.name(), getWorkEffortTypeId());
        mapContext.put(E.param3.name(), getOldWorkEffortId());
        return mapContext;
    }

    private MapContext<String, Object> mapRelazioneContextDelete() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(E.param1.name(), getOldWorkEffortId());
        mapContext.put(E.param2.name(), getOldWorkEffortTypeId());
        mapContext.put(E.param3.name(), getWorkEffortId());
        return mapContext;
    }

    private MapContext<String, Object> mapRelazioneContextUpdate() throws SQLException {
        MapContext<String, Object> mapContext = this.mapContext();
        mapContext.put(E.workEffortId.name(), getWorkEffortId());
        mapContext.put(E.workEffortTypeId.name(), getWorkEffortTypeId());
        mapContext.put(E.oldWorkEffortId.name(), getOldWorkEffortId());
        return mapContext;
    }

    /**
     * Ritorno iteratore della lista relazioni aggiunte
     * @return
     * @throws SQLException
     */
    public Iterator<RelazioneComparingVersion> getRelazioneAddList() throws SQLException {
        if (Closeables.isClosed(relazioneAddList)) {
            relazioneAddList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryRelazioneAdd, E.excludeCollegati.name(), E.Y.name()), mapRelazioneContextAdd()).iterate(new RelazioneComparingVersion());
        }
        return relazioneAddList;
    }

    /**
     * Ritorno iteratore della lista relazioni rimosse
     * @return
     * @throws SQLException
     */
    public Iterator<RelazioneComparingVersion> getRelazioneDeleteList() throws SQLException {
        if (Closeables.isClosed(relazioneDeleteList)) {
            relazioneDeleteList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryRelazioneDelete, E.excludeCollegati.name(), E.Y.name()), mapRelazioneContextDelete()).iterate(new RelazioneComparingVersion());
        }
        return relazioneDeleteList;
    }

    /**
     * Ritorno iteratore della lista relazioni modificare
     * @return
     * @throws SQLException
     */
    public Iterator<RelazioneUpdateComparingVersion> getRelazioneUpdateList() throws SQLException {
        if (Closeables.isClosed(relazioneUpdateList)) {
            relazioneUpdateList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryRelazioneUpdate, E.excludeCollegati.name(), E.Y.name()), mapRelazioneContextUpdate()).iterate(new RelazioneUpdateComparingVersion());
        }
        return relazioneUpdateList;
    }

    /*
     * Carico i NOTE    
     */
    /**
     * Ritorno iteratore della lista note aggiunte
     * @return
     * @throws SQLException
     */
    public Iterator<NoteComparingVersion> getNoteAddList() throws SQLException {
        if (Closeables.isClosed(noteAddList)) {
            noteAddList = new FtlQuery(ctx.getDelegator(), queryNoteAddDelete, mapContextAdd()).iterate(new NoteComparingVersion());
        }
        return noteAddList;
    }

    /**
     * Ritorno iteratore della lista note rimosse
     * @return
     * @throws SQLException
     */
    public Iterator<NoteComparingVersion> getNoteDeleteList() throws SQLException {
        if (Closeables.isClosed(noteDeleteList)) {
            noteDeleteList = new FtlQuery(ctx.getDelegator(), queryNoteAddDelete, mapContextDelete()).iterate(new NoteComparingVersion());
        }
        return noteDeleteList;
    }

    /**
     * Ritorno iteratore della lista note modificate
     * @return
     * @throws SQLException
     */
    public Iterator<NoteUpdateComparingVersion> getNoteUpdateList() throws SQLException {
        if (Closeables.isClosed(noteUpdateList)) {
            noteUpdateList = new FilteredCloseableIterator<NoteUpdateComparingVersion>(new FtlQuery(ctx.getDelegator(), queryNoteUpdate, mapContextUpdate()).iterate(new NoteUpdateComparingVersion())) {
                private static final long serialVersionUID = 1L;

                protected boolean accept(NoteUpdateComparingVersion record) {
                    try {
                        if (!UtilValidate.areEqual(record.getInternalNote(), record.getOldInternalNote()) || !UtilValidate.areEqual(record.getNoteInfo(), record.getOldNoteInfo())) {
                            return true;
                        }
                    } catch (SQLException e) {
                    }
                    return false;
                };
            };
        }
        return noteUpdateList;
    }

    /*
     * Carico i ALLEGATO    
     */
    /**
     * Ritorno iteratore della lista allegati aggiunti
     * @return
     * @throws SQLException
     */
    public Iterator<AllegatoComparingVersion> getAllegatoAddList() throws SQLException {
        if (Closeables.isClosed(allegatoAddList)) {
            allegatoAddList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryAllegatoAddDelete, E.excludeRequest.name(), E.Y.name()), mapContextAdd()).iterate(new AllegatoComparingVersion());
        }
        return allegatoAddList;
    }

    /**
     * Ritorno iteratore della lista allegi rimossi
     * @return
     * @throws SQLException
     */
    public Iterator<AllegatoComparingVersion> getAllegatoDeleteList() throws SQLException {
        if (Closeables.isClosed(allegatoDeleteList)) {
            allegatoDeleteList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryAllegatoAddDelete, E.excludeRequest.name(), E.Y.name()), mapContextDelete()).iterate(new AllegatoComparingVersion());
        }
        return allegatoDeleteList;
    }

    /**
     * Ritorno iteratore della lista allegati modificati
     * @return
     * @throws SQLException
     */
    public Iterator<AllegatoUpdateComparingVersion> getAllegatoUpdateList() throws SQLException {
        if (Closeables.isClosed(allegatoUpdateList)) {
            allegatoUpdateList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryAllegatoUpdate, E.excludeRequest.name(), E.Y.name()), mapContextUpdate()).iterate(new AllegatoUpdateComparingVersion());
        }
        return allegatoUpdateList;
    }

    /*
     * Carico i MISURA    
     */
    /**
     * Ritorno iteratore della lista misure aggiunte
     * @return
     * @throws SQLException
     */
    public Iterator<MisuraComparingVersion> getMisuraAddList() throws SQLException {
        if (Closeables.isClosed(misuraAddList)) {
            misuraAddList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryMisuraAddDelete, E.excludeIndicator.name(), E.Y.name()), mapContextAdd()).iterate(new MisuraComparingVersion());
        }
        return misuraAddList;
    }

    /**
     * Ritorno iteratore della lista misure rimosse
     * @return
     * @throws SQLException
     */
    public Iterator<MisuraComparingVersion> getMisuraDeleteList() throws SQLException {
        if (Closeables.isClosed(misuraDeleteList)) {
            misuraDeleteList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryMisuraAddDelete, E.excludeIndicator.name(), E.Y.name()), mapContextDelete()).iterate(new MisuraComparingVersion());
        }
        return misuraDeleteList;
    }

    /**
     * Ritorno iteratore della lista misure modificate
     * @return
     * @throws SQLException
     */
    public Iterator<MisuraUpdateComparingVersion> getMisuraUpdateList() throws SQLException {
        if (Closeables.isClosed(misuraUpdateList)) {
            misuraUpdateList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryMisuraUpdate, E.excludeIndicator.name(), E.Y.name()), mapContextUpdate()).iterate(new MisuraUpdateComparingVersion());
        }
        return misuraUpdateList;
    }

    /*
     * Carico i VALORE    
     */
    /**
     * Ritorno iteratore della lista valori aggiunti
     * @return 
     * @throws SQLException
     */
    public Iterator<ValoreComparingVersion> getValoreAddList() throws SQLException {
        if (Closeables.isClosed(valoreAddList)) {
            valoreAddList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryValoreAdd, E.exposeDetailIndicator.name(), E.N.name()), mapContextAdd()).iterate(new ValoreComparingVersion());
        }
        return valoreAddList;
    }

    /**
     * Ritorno iteratore della lista valori rimossi
     * @return
     * @throws SQLException
     */
    public Iterator<ValoreComparingVersion> getValoreDeleteList() throws SQLException {
        if (Closeables.isClosed(valoreDeleteList)) {
            valoreDeleteList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryValoreDelete, E.exposeDetailIndicator.name(), E.N.name()), mapContextDelete()).iterate(new ValoreComparingVersion());
        }
        return valoreDeleteList;
    }

    /**
     * Ritorno iteratore della lista valori modificati
     * @return
     * @throws SQLException
     */
    public Iterator<ValoreUpdateComparingVersion> getValoreUpdateList() throws SQLException {
        if (Closeables.isClosed(valoreUpdateList)) {
            valoreUpdateList = new FtlQuery(ctx.getDelegator(), getExcludeQuery(queryValoreUpdate, E.exposeDetailIndicator.name(), E.N.name()), mapContextUpdate()).iterate(new ValoreUpdateComparingVersion());
        }
        return valoreUpdateList;
    }
}
