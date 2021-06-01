package com.mapsengineering.workeffortext.birt.comparingVersions;

import java.sql.SQLException;
import java.sql.Timestamp;

public class MisuraUpdateComparingVersion extends MisuraComparingVersion {

    private static final long serialVersionUID = 1L;

    public String getOldUomDescr() throws SQLException {
        return getRs().getString(E.OLD_UOM_DESCR.name());
    }

    public String getOldComments() throws SQLException {
        return getRs().getString(E.OLD_COMMENTS.name());
    }

    public Double getOldKpiScoreWeight() throws SQLException {
        return getRs().getDouble(E.OLD_KPI_SCORE_WEIGHT.name());
    }

    public String getOldSequenceId() throws SQLException {
        return getRs().getString(E.OLD_SEQUENCE_ID.name());
    }

    public Timestamp getOldFromDate() throws SQLException {
        return getRs().getTimestamp(E.OLD_FROM_DATE.name());
    }

    public Timestamp getOldThruDate() throws SQLException {
        return getRs().getTimestamp(E.OLD_THRU_DATE.name());
    }
}
