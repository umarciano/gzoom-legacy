package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;

import com.mapsengineering.base.jdbc.FtlQuery;
import com.mapsengineering.base.report.OfbizReportContext;

public class CardsComparingVersionReport implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String MODULE = CardsComparingVersionReport.class.getName();

    private static final String queryAdd = "sql/cardsComparingVersions/cards/addList.sql.ftl";
    private static final String queryDelete = "sql/cardsComparingVersions/cards/deleteList.sql.ftl";
    private static final String queryUpdate = "sql/cardsComparingVersions/cards/updateList.sql.ftl";

    private transient final OfbizReportContext ctx;

    public CardsComparingVersionReport(Object context) {
        ctx = new OfbizReportContext(context);
    }

    /**
     * Ritorno iteratore della lista delle schede aggiunte
     * @return
     * @throws SQLException
     */
    public Iterator<CardsComparingVersion> getAddList() throws SQLException {
        return new FtlQuery(ctx.getDelegator(), queryAdd, ctx).iterate(new CardsComparingVersion());
    }

    /**
     * Ritorno iteratore della lista delle schede rimosse
     * @return
     * @throws SQLException
     */
    public Iterator<CardsComparingVersion> getDeleteList() throws SQLException {
        return new FtlQuery(ctx.getDelegator(), queryDelete, ctx).iterate(new CardsComparingVersion());
    }

    /**
     * Ritorno iteratore della lista delle schede modificate
     * @return
     * @throws SQLException
     */
    public Iterator<CardsUpdateComparingVersion> getUpdateList() throws SQLException {
        return new FtlQuery(ctx.getDelegator(), queryUpdate, ctx).iterate(new CardsUpdateComparingVersion());
    }
}
