package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapContext;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.report.OfbizReportContext;
import com.mapsengineering.base.util.closeable.Closeables;

public class CardsUpdateComparingVersion extends CardsComparingVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String queryObiettivoAdd = "sql/cardsComparingVersions/obiettivo/obiettivoAddList.sql.ftl";
    private static final String queryObiettivoDelete = "sql/cardsComparingVersions/obiettivo/obiettivoDeleteList.sql.ftl";
    private static final String queryObiettivoUpdate = "sql/cardsComparingVersions/obiettivo/obiettivoUpdateList.sql.ftl";

    private transient final OfbizReportContext ctx = OfbizReportContext.get();
    private transient Iterator<ObiettivoUpdateComparingVersion> obiettivoUpdateList;

    public String getOldWorkEffortId() throws SQLException {
        return getRs().getString(E.OLD_WORK_EFFORT_ID.name());
    }

    public String getOldWorkEffortSnapshotId() throws SQLException {
        return getRs().getString(E.OLD_WORK_EFFORT_SNAPSHOT_ID.name());
    }

    /**
     * Ritorno true per specificare sche la scheda ha almeno un obiettivo modificato
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public Boolean getIsModified() throws SQLException, IOException {
        this.getObiettivoUpdateList();
        try {
            //TODO farlo nel modo intelligente
            /** Devo scorrere tutto l iteratore se c' e almeno un obiettivo modificato allora lo mostro 
             * altrimenti controllo se esso stesso e' modificato
             * **/
            ObiettivoUpdateComparingVersion ele = null;
            while (obiettivoUpdateList.hasNext()) {
                ele = obiettivoUpdateList.next();
                if (ele.getIsModified()) {
                    return Boolean.TRUE;
                } else if (!UtilValidate.areEqual(ele.getEtch(), ele.getOldEtch()) || !UtilValidate.areEqual(ele.getWorkEffortName(), ele.getOldWorkEffortName()) 
                        || !UtilValidate.areEqual(ele.getDescription(), ele.getOldDescription()) || !UtilValidate.areEqual(ele.getPartyName(), ele.getOldPartyName())
                        || !UtilValidate.areEqual(ele.getEstimatedStartDate(), ele.getOldEstimatedStartDate()) 
                        || !UtilValidate.areEqual(ele.getEstimatedCompletionDate(), ele.getOldEstimatedCompletionDate()) ) {
                    return Boolean.TRUE;
                }
            }
            return Boolean.FALSE;
        } finally {
            obiettivoUpdateList = Closeables.close(obiettivoUpdateList);
        }
    }

    @Override
    protected void onNext() {
        super.onNext();
        obiettivoUpdateList = Closeables.close(obiettivoUpdateList);
    }

    private MapContext<String, Object> mapContextLocal() throws SQLException {
        MapContext<String, Object> mapContext = MapContext.createMapContext(ctx);
        mapContext.push();
        mapContext.put("workEffortId", getWorkEffortId());
        mapContext.put("oldWorkEffortId", getOldWorkEffortId());
        return mapContext;
    }

    /**
     * Ritorno iteratore della lista obiettivo aggiunti
     * @return
     * @throws SQLException
     */
    public Iterator<CardsComparingVersion> getObiettivoAddList() throws SQLException {
        return new FtlQuery(ctx.getDelegator(), queryObiettivoAdd, mapContextLocal()).iterate(new CardsComparingVersion());
    }

    /**
     * Ritorno iteratore della lista obiettivo rimossi
     * @return
     * @throws SQLException
     */
    public Iterator<CardsComparingVersion> getObiettivoDeleteList() throws SQLException {
        return new FtlQuery(ctx.getDelegator(), queryObiettivoDelete, mapContextLocal()).iterate(new CardsComparingVersion());
    }

    /**
     * Ritorno iteratore della lista obiettivo modificati
     * @return
     * @throws SQLException
     */
    public Iterator<ObiettivoUpdateComparingVersion> getObiettivoUpdateList() throws SQLException {
        if (Closeables.isClosed(obiettivoUpdateList)) {
            obiettivoUpdateList = new FtlQuery(ctx.getDelegator(), queryObiettivoUpdate, mapContextLocal()).iterate(new ObiettivoUpdateComparingVersion());
        }
        return obiettivoUpdateList;
    }
}
